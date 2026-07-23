package com.calbank.services;

import com.calbank.database.DatabaseManager;
import com.calbank.models.Account;
import com.calbank.models.Transaction;
import com.calbank.utils.TransactionUtils;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public final class TransactionService {

    private final DatabaseManager dbManager = DatabaseManager.getInstance();
    private final AccountService accountService = new AccountService();

    public Transaction deposit(String accountId, double amount, String description, Integer categoryId) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive");

        Account account = accountService.getAccountById(accountId);
        if (account == null) throw new IllegalArgumentException("Account not found");

        double newBalance = account.getBalance() + amount;
        String receipt = TransactionUtils.generateReceiptNumber();
        String sql = "INSERT INTO transactions "
                   + "(account_id, transaction_type, amount, balance_after, description, category_id, receipt_number) "
                   + "VALUES (?, 'DEPOSIT', ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = dbManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, accountId);
            ps.setDouble(2, amount);
            ps.setDouble(3, newBalance);
            ps.setString(4, description != null && !description.isBlank() ? description : "Deposit");
            if (categoryId != null) ps.setInt(5, categoryId); else ps.setNull(5, Types.INTEGER);
            ps.setString(6, receipt);
            ps.executeUpdate();
            accountService.updateBalance(accountId, newBalance);
            return getLastTransaction(accountId);
        } catch (SQLException e) {
            throw new RuntimeException("Deposit failed: " + e.getMessage(), e);
        }
    }

    public Transaction withdraw(String accountId, double amount, String description, Integer categoryId) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive");

        Account account = accountService.getAccountById(accountId);
        if (account == null) throw new IllegalArgumentException("Account not found");
        if (!account.canWithdraw(amount)) {
            throw new IllegalArgumentException("Insufficient funds. Available: "
                + TransactionUtils.formatCurrency(account.getBalance()));
        }

        double newBalance = account.getBalance() - amount;
        String receipt = TransactionUtils.generateReceiptNumber();
        String sql = "INSERT INTO transactions "
                   + "(account_id, transaction_type, amount, balance_after, description, category_id, receipt_number) "
                   + "VALUES (?, 'WITHDRAW', ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = dbManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, accountId);
            ps.setDouble(2, amount);
            ps.setDouble(3, newBalance);
            ps.setString(4, description != null && !description.isBlank() ? description : "Withdrawal");
            if (categoryId != null) ps.setInt(5, categoryId); else ps.setNull(5, Types.INTEGER);
            ps.setString(6, receipt);
            ps.executeUpdate();
            accountService.updateBalance(accountId, newBalance);
            return getLastTransaction(accountId);
        } catch (SQLException e) {
            throw new RuntimeException("Withdrawal failed: " + e.getMessage(), e);
        }
    }

    public Transaction transfer(String fromAccountId, String toAccountId,
                                double amount, String description) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive");
        if (fromAccountId.equals(toAccountId)) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }

        Account from = accountService.getAccountById(fromAccountId);
        Account to   = accountService.getAccountById(toAccountId);
        if (from == null || to == null) {
            throw new IllegalArgumentException("Both accounts must be valid");
        }
        if (!from.canWithdraw(amount)) {
            throw new IllegalArgumentException("Insufficient funds. Available: "
                + TransactionUtils.formatCurrency(from.getBalance()));
        }

        String receipt = TransactionUtils.generateReceiptNumber();
        String sql = "INSERT INTO transactions "
                   + "(account_id, transaction_type, amount, balance_after, description, "
                   + "recipient_account, receipt_number) "
                   + "VALUES (?, 'TRANSFER', ?, ?, ?, ?, ?)";

        try {
            Connection conn = dbManager.getConnection();
            conn.setAutoCommit(false);
            try {
                double newFromBalance = from.getBalance() - amount;
                double newToBalance   = to.getBalance() + amount;

                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, fromAccountId);
                    ps.setDouble(2, amount);
                    ps.setDouble(3, newFromBalance);
                    ps.setString(4, description != null && !description.isBlank()
                            ? description : "Transfer to " + toAccountId);
                    ps.setString(5, toAccountId);
                    ps.setString(6, receipt);
                    ps.executeUpdate();
                }

                accountService.updateBalance(fromAccountId, newFromBalance);
                accountService.updateBalance(toAccountId, newToBalance);

                conn.commit();
                return getLastTransaction(fromAccountId);
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Transfer failed: " + e.getMessage(), e);
        }
    }

    public List<Transaction> getTransactionsByAccountId(String accountId) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE account_id = ? ORDER BY created_at DESC";
        try (PreparedStatement ps = dbManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, accountId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                transactions.add(mapTransaction(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch transactions: " + e.getMessage(), e);
        }
        return transactions;
    }

    public List<Transaction> getRecentTransactions(String accountId, int limit) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE account_id = ? ORDER BY created_at DESC LIMIT ?";
        try (PreparedStatement ps = dbManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, accountId);
            ps.setInt(2, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                transactions.add(mapTransaction(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch recent transactions: " + e.getMessage(), e);
        }
        return transactions;
    }

    public List<Transaction> getFilteredTransactions(String accountId, String type,
            String searchTerm, Integer categoryId, LocalDateTime fromDate, LocalDateTime toDate) {
        List<Transaction> results = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM transactions WHERE account_id = ?");
        List<Object> params = new ArrayList<>();
        params.add(accountId);

        if (type != null && !type.equals("ALL")) {
            sql.append(" AND transaction_type = ?");
            params.add(type);
        }
        if (searchTerm != null && !searchTerm.isBlank()) {
            sql.append(" AND (description LIKE ? OR receipt_number LIKE ?)");
            params.add("%" + searchTerm + "%");
            params.add("%" + searchTerm + "%");
        }
        if (categoryId != null) {
            sql.append(" AND category_id = ?");
            params.add(categoryId);
        }
        if (fromDate != null) {
            sql.append(" AND created_at >= ?");
            params.add(fromDate.toString());
        }
        if (toDate != null) {
            sql.append(" AND created_at <= ?");
            params.add(toDate.toString());
        }
        sql.append(" ORDER BY created_at DESC");

        try (PreparedStatement ps = dbManager.getConnection().prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object p = params.get(i);
                if (p instanceof String) ps.setString(i + 1, (String) p);
                else if (p instanceof Integer) ps.setInt(i + 1, (Integer) p);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                results.add(mapTransaction(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Search failed: " + e.getMessage(), e);
        }
        return results;
    }

    public List<Transaction> searchTransactions(String accountId, String searchTerm) {
        return getFilteredTransactions(accountId, null, searchTerm, null, null, null);
    }

    public List<Transaction> getAllTransactions(int limit) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions ORDER BY created_at DESC LIMIT ?";
        try (PreparedStatement ps = dbManager.getConnection().prepareStatement(sql)) {
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                transactions.add(mapTransaction(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch all transactions: " + e.getMessage(), e);
        }
        return transactions;
    }

    public int getTotalTransactionCount() {
        String sql = "SELECT COUNT(*) FROM transactions";
        try (PreparedStatement ps = dbManager.getConnection().prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            return 0;
        }
    }

    public double getTotalDeposits() {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE transaction_type = 'DEPOSIT'";
        try (PreparedStatement ps = dbManager.getConnection().prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getDouble(1) : 0;
        } catch (SQLException e) {
            return 0;
        }
    }

    public double getTotalWithdrawals() {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE transaction_type = 'WITHDRAW'";
        try (PreparedStatement ps = dbManager.getConnection().prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getDouble(1) : 0;
        } catch (SQLException e) {
            return 0;
        }
    }

    public double getTotalTransfers() {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE transaction_type = 'TRANSFER'";
        try (PreparedStatement ps = dbManager.getConnection().prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getDouble(1) : 0;
        } catch (SQLException e) {
            return 0;
        }
    }

    public List<Transaction> getRecentSystemTransactions(int limit) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT t.*, a.user_id FROM transactions t "
                   + "JOIN accounts a ON t.account_id = a.account_id "
                   + "ORDER BY t.created_at DESC LIMIT ?";
        try (PreparedStatement ps = dbManager.getConnection().prepareStatement(sql)) {
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                transactions.add(mapTransaction(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch recent system transactions: " + e.getMessage(), e);
        }
        return transactions;
    }

    private Transaction getLastTransaction(String accountId) {
        String sql = "SELECT * FROM transactions WHERE account_id = ? "
                   + "ORDER BY transaction_id DESC LIMIT 1";
        try (PreparedStatement ps = dbManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, accountId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapTransaction(rs);
        } catch (SQLException e) {
            System.err.println("Warning: Could not retrieve last transaction: " + e.getMessage());
        }
        return null;
    }

    private Transaction mapTransaction(ResultSet rs) throws SQLException {
        Transaction t = new Transaction();
        t.setTransactionId(rs.getInt("transaction_id"));
        t.setAccountId(rs.getString("account_id"));
        t.setTransactionType(rs.getString("transaction_type"));
        t.setAmount(rs.getDouble("amount"));
        t.setBalanceAfter(rs.getDouble("balance_after"));
        t.setDescription(rs.getString("description"));
        t.setRecipientAccount(rs.getString("recipient_account"));
        t.setReceiptNumber(rs.getString("receipt_number"));
        try {
            java.sql.Timestamp ts = rs.getTimestamp("created_at");
            if (ts != null) t.setCreatedAt(ts.toLocalDateTime());
        } catch (SQLException ignored) {}
        try {
            int catId = rs.getInt("category_id");
            if (!rs.wasNull()) t.setCategoryId(catId);
        } catch (SQLException ignored) {}
        return t;
    }
}
