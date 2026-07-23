package com.calbank.services;

import com.calbank.database.DatabaseManager;
import com.calbank.models.Transaction;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public final class ReportService {

    private final DatabaseManager dbManager = DatabaseManager.getInstance();

    public static final class MonthlySummary {
        public final double totalDeposits;
        public final double totalWithdrawals;
        public final double totalTransfers;
        public final int transactionCount;

        public MonthlySummary(double totalDeposits, double totalWithdrawals,
                              double totalTransfers, int transactionCount) {
            this.totalDeposits = totalDeposits;
            this.totalWithdrawals = totalWithdrawals;
            this.totalTransfers = totalTransfers;
            this.transactionCount = transactionCount;
        }

        public double getNetChange() {
            return totalDeposits - totalWithdrawals;
        }
    }

    public static final class CategorySpending {
        public final int categoryId;
        public final String categoryName;
        public final String categoryIcon;
        public final String categoryColor;
        public final double totalAmount;
        public final int transactionCount;

        public CategorySpending(int categoryId, String categoryName, String categoryIcon,
                                String categoryColor, double totalAmount, int transactionCount) {
            this.categoryId = categoryId;
            this.categoryName = categoryName;
            this.categoryIcon = categoryIcon;
            this.categoryColor = categoryColor;
            this.totalAmount = totalAmount;
            this.transactionCount = transactionCount;
        }
    }

    public MonthlySummary getMonthlySummary(String accountId, int year, int month) {
        String sql = "SELECT transaction_type, SUM(amount), COUNT(*) FROM transactions "
                   + "WHERE account_id = ? AND DATE_FORMAT(created_at, '%Y') = ? "
                   + "AND DATE_FORMAT(created_at, '%m') = ? GROUP BY transaction_type";
        double deposits = 0, withdrawals = 0, transfers = 0;
        int count = 0;

        try (PreparedStatement ps = dbManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, accountId);
            ps.setString(2, String.format("%04d", year));
            ps.setString(3, String.format("%02d", month));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String type = rs.getString(1);
                double amount = rs.getDouble(2);
                int cnt = rs.getInt(3);
                count += cnt;
                switch (type) {
                    case "DEPOSIT": deposits += amount; break;
                    case "WITHDRAW": withdrawals += amount; break;
                    case "TRANSFER": transfers += amount; break;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get monthly summary: " + e.getMessage(), e);
        }
        return new MonthlySummary(deposits, withdrawals, transfers, count);
    }

    public List<CategorySpending> getSpendingByCategory(String accountId, int year, int month) {
        List<CategorySpending> results = new ArrayList<>();
        String sql = "SELECT t.category_id, COALESCE(c.name, 'Uncategorized') AS cat_name, "
                   + "COALESCE(c.icon, '') AS cat_icon, COALESCE(c.color, '#999999') AS cat_color, "
                   + "SUM(t.amount) AS total, COUNT(*) AS cnt "
                   + "FROM transactions t LEFT JOIN categories c ON t.category_id = c.category_id "
                   + "WHERE t.account_id = ? AND t.transaction_type IN ('WITHDRAW', 'TRANSFER') "
                   + "AND DATE_FORMAT(t.created_at, '%Y') = ? AND DATE_FORMAT(t.created_at, '%m') = ? "
                   + "GROUP BY t.category_id ORDER BY total DESC";

        try (PreparedStatement ps = dbManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, accountId);
            ps.setString(2, String.format("%04d", year));
            ps.setString(3, String.format("%02d", month));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                results.add(new CategorySpending(
                    rs.getInt("category_id"),
                    rs.getString("cat_name"),
                    rs.getString("cat_icon"),
                    rs.getString("cat_color"),
                    rs.getDouble("total"),
                    rs.getInt("cnt")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get spending by category: " + e.getMessage(), e);
        }
        return results;
    }

    public Map<String, Double> getMonthlyTrend(String accountId, int monthsBack) {
        Map<String, Double> trend = new LinkedHashMap<>();
        LocalDateTime now = LocalDateTime.now();
        for (int i = monthsBack - 1; i >= 0; i--) {
            LocalDateTime ref = now.minusMonths(i);
            String key = String.format("%04d-%02d", ref.getYear(), ref.getMonthValue());
            MonthlySummary ms = getMonthlySummary(accountId, ref.getYear(), ref.getMonthValue());
            trend.put(key, ms.getNetChange());
        }
        return trend;
    }
}
