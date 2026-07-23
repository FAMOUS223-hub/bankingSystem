package com.calbank.services;

import com.calbank.database.DatabaseManager;
import com.calbank.models.Account;
import com.calbank.utils.TransactionUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public final class AccountService {

    private final DatabaseManager dbManager = DatabaseManager.getInstance();

    public Account createAccount(int userId, String accountType) {
        String accountId = TransactionUtils.generateAccountNumber();
        String sql = "INSERT INTO accounts (account_id, user_id, account_type, balance) "
                   + "VALUES (?, ?, ?, 0.0)";

        try (PreparedStatement ps = dbManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, accountId);
            ps.setInt(2, userId);
            ps.setString(3, accountType);
            ps.executeUpdate();
            return getAccountById(accountId);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create account: " + e.getMessage(), e);
        }
    }

    public Account getAccountById(String accountId) {
        String sql = "SELECT * FROM accounts WHERE account_id = ?";
        try (PreparedStatement ps = dbManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, accountId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapAccount(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch account: " + e.getMessage(), e);
        }
        return null;
    }

    public List<Account> getAccountsByUserId(int userId) {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts WHERE user_id = ? ORDER BY created_at DESC";
        try (PreparedStatement ps = dbManager.getConnection().prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                accounts.add(mapAccount(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch accounts: " + e.getMessage(), e);
        }
        return accounts;
    }

    public boolean updateBalance(String accountId, double newBalance) {
        String sql = "UPDATE accounts SET balance = ? WHERE account_id = ?";
        try (PreparedStatement ps = dbManager.getConnection().prepareStatement(sql)) {
            ps.setDouble(1, newBalance);
            ps.setString(2, accountId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update balance: " + e.getMessage(), e);
        }
    }

    public double getTotalBalance(int userId) {
        return getAccountsByUserId(userId).stream()
                .mapToDouble(Account::getBalance)
                .sum();
    }

    public int getTotalAccountCount() {
        String sql = "SELECT COUNT(*) FROM accounts";
        try (PreparedStatement ps = dbManager.getConnection().prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            return 0;
        }
    }

    public double getAllAccountsTotalBalance() {
        String sql = "SELECT COALESCE(SUM(balance), 0) FROM accounts";
        try (PreparedStatement ps = dbManager.getConnection().prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getDouble(1) : 0;
        } catch (SQLException e) {
            return 0;
        }
    }

    public List<Account> getAllAccounts() {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT a.*, u.username, u.full_name FROM accounts a "
                   + "JOIN users u ON a.user_id = u.id ORDER BY a.created_at DESC";
        try (PreparedStatement ps = dbManager.getConnection().prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                accounts.add(mapAccount(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch all accounts: " + e.getMessage(), e);
        }
        return accounts;
    }

    public boolean updateAccountStatus(String accountId, String status) {
        String sql = "UPDATE accounts SET status = ? WHERE account_id = ?";
        try (PreparedStatement ps = dbManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, accountId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update account status: " + e.getMessage(), e);
        }
    }

    public boolean resetAllAccountBalancesAndTransactions() {
        Connection conn = dbManager.getConnection();
        try {
            boolean autoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try {
                try (Statement stmt = conn.createStatement()) {
                    stmt.executeUpdate("UPDATE accounts SET balance = 0.0");
                    stmt.executeUpdate("DELETE FROM transactions");
                    stmt.executeUpdate("DELETE FROM loans");
                    stmt.executeUpdate("DELETE FROM savings");
                }
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(autoCommit);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to reset balances and transactions: " + e.getMessage(), e);
        }
    }

    private Account mapAccount(ResultSet rs) throws SQLException {
        Account a = new Account();
        a.setAccountId(rs.getString("account_id"));
        a.setUserId(rs.getInt("user_id"));
        a.setAccountType(rs.getString("account_type"));
        a.setBalance(rs.getDouble("balance"));
        a.setCurrency(rs.getString("currency"));
        try { a.setStatus(rs.getString("status")); } catch (SQLException ignored) {}
        return a;
    }
}
