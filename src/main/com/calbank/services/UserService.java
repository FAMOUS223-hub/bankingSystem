package com.calbank.services;

import com.calbank.database.DatabaseManager;
import com.calbank.models.User;
import com.calbank.utils.PasswordUtils;
import com.calbank.utils.TransactionUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public final class UserService {

    private final DatabaseManager dbManager = DatabaseManager.getInstance();

    public boolean registerUser(User user, String password) {
        return registerUser(user, password, User.ROLE_USER);
    }

    public boolean registerUser(User user, String password, String role) {
        if (usernameExists(user.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (emailExists(user.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        String sql = "INSERT INTO users (username, password_hash, email, full_name, phone, address, role) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = dbManager.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, PasswordUtils.hashPassword(password));
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getFullName());
            ps.setString(5, user.getPhone() != null ? user.getPhone() : "");
            ps.setString(6, user.getAddress() != null ? user.getAddress() : "");
            ps.setString(7, role);
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                int userId = keys.getInt(1);
                String acctId = TransactionUtils.generateAccountNumber();
                PreparedStatement acctPs = dbManager.getConnection().prepareStatement(
                    "INSERT INTO accounts (account_id, user_id, account_type, balance) VALUES (?, ?, 'Checking', 0.0)");
                acctPs.setString(1, acctId);
                acctPs.setInt(2, userId);
                acctPs.executeUpdate();
            }
            return true;
        } catch (SQLException e) {
            throw new RuntimeException("Registration failed: " + e.getMessage(), e);
        }
    }

    public User loginUser(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND active = 1";
        try (PreparedStatement ps = dbManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String hash = rs.getString("password_hash");
                if (PasswordUtils.verifyPassword(password, hash)) {
                    return mapUser(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Login failed: " + e.getMessage(), e);
        }
        return null;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY created_at DESC";
        try (PreparedStatement ps = dbManager.getConnection().prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                users.add(mapUser(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch users: " + e.getMessage(), e);
        }
        return users;
    }

    public List<User> searchUsers(String searchTerm) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE username LIKE ? OR full_name LIKE ? OR email LIKE ? ORDER BY created_at DESC";
        try (PreparedStatement ps = dbManager.getConnection().prepareStatement(sql)) {
            String term = "%" + searchTerm + "%";
            ps.setString(1, term);
            ps.setString(2, term);
            ps.setString(3, term);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                users.add(mapUser(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Search failed: " + e.getMessage(), e);
        }
        return users;
    }

    public boolean deleteUser(int userId) {
        Connection conn = dbManager.getConnection();
        try {
            boolean autoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try {
                String checkSql = "SELECT role FROM users WHERE id = ?";
                try (PreparedStatement ps = conn.prepareStatement(checkSql)) {
                    ps.setInt(1, userId);
                    ResultSet rs = ps.executeQuery();
                    if (!rs.next() || "ADMIN".equalsIgnoreCase(rs.getString("role"))) {
                        conn.rollback();
                        return false;
                    }
                }

                String delTx = "DELETE FROM transactions WHERE account_id IN (SELECT account_id FROM accounts WHERE user_id = ?)";
                try (PreparedStatement ps = conn.prepareStatement(delTx)) {
                    ps.setInt(1, userId);
                    ps.executeUpdate();
                }

                String delLoans = "DELETE FROM loans WHERE account_id IN (SELECT account_id FROM accounts WHERE user_id = ?)";
                try (PreparedStatement ps = conn.prepareStatement(delLoans)) {
                    ps.setInt(1, userId);
                    ps.executeUpdate();
                }

                String delSavings = "DELETE FROM savings WHERE account_id IN (SELECT account_id FROM accounts WHERE user_id = ?)";
                try (PreparedStatement ps = conn.prepareStatement(delSavings)) {
                    ps.setInt(1, userId);
                    ps.executeUpdate();
                }

                String delAccts = "DELETE FROM accounts WHERE user_id = ?";
                try (PreparedStatement ps = conn.prepareStatement(delAccts)) {
                    ps.setInt(1, userId);
                    ps.executeUpdate();
                }

                String delCats = "DELETE FROM categories WHERE user_id = ?";
                try (PreparedStatement ps = conn.prepareStatement(delCats)) {
                    ps.setInt(1, userId);
                    ps.executeUpdate();
                }

                String delPrefs = "DELETE FROM user_preferences WHERE user_id = ?";
                try (PreparedStatement ps = conn.prepareStatement(delPrefs)) {
                    ps.setInt(1, userId);
                    ps.executeUpdate();
                }

                String delUser = "DELETE FROM users WHERE id = ?";
                int rows;
                try (PreparedStatement ps = conn.prepareStatement(delUser)) {
                    ps.setInt(1, userId);
                    rows = ps.executeUpdate();
                }

                conn.commit();
                return rows > 0;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(autoCommit);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Delete user failed: " + e.getMessage(), e);
        }
    }

    public boolean toggleUserActive(int userId) {
        String sql = "UPDATE users SET active = CASE WHEN active = 1 THEN 0 ELSE 1 END WHERE id = ? AND role != 'ADMIN'";
        try (PreparedStatement ps = dbManager.getConnection().prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Toggle failed: " + e.getMessage(), e);
        }
    }

    public int getTotalUserCount() {
        String sql = "SELECT COUNT(*) FROM users";
        try (PreparedStatement ps = dbManager.getConnection().prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            return 0;
        }
    }

    public int getActiveUserCount() {
        String sql = "SELECT COUNT(*) FROM users WHERE active = 1";
        try (PreparedStatement ps = dbManager.getConnection().prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            return 0;
        }
    }

    public boolean usernameExists(String username) {
        return countByField("username", username) > 0;
    }

    public boolean emailExists(String email) {
        return countByField("email", email) > 0;
    }

    public boolean emailExistsForOther(String email, int userId) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ? AND id != ?";
        try (PreparedStatement ps = dbManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setInt(2, userId);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public User getUserById(int userId) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (PreparedStatement ps = dbManager.getConnection().prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapUser(rs);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch user: " + e.getMessage(), e);
        }
        return null;
    }

    public boolean updateUser(User user) {
        String sql = "UPDATE users SET email = ?, full_name = ?, phone = ?, address = ? WHERE id = ?";
        try (PreparedStatement ps = dbManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getFullName());
            ps.setString(3, user.getPhone() != null ? user.getPhone() : "");
            ps.setString(4, user.getAddress() != null ? user.getAddress() : "");
            ps.setInt(5, user.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Update failed: " + e.getMessage(), e);
        }
    }

    public boolean changePassword(int userId, String oldPassword, String newPassword) {
        String selectSql = "SELECT password_hash FROM users WHERE id = ?";
        try (PreparedStatement ps = dbManager.getConnection().prepareStatement(selectSql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String hash = rs.getString("password_hash");
                if (PasswordUtils.verifyPassword(oldPassword, hash)) {
                    String updateSql = "UPDATE users SET password_hash = ? WHERE id = ?";
                    try (PreparedStatement up = dbManager.getConnection().prepareStatement(updateSql)) {
                        up.setString(1, PasswordUtils.hashPassword(newPassword));
                        up.setInt(2, userId);
                        return up.executeUpdate() > 0;
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Password change failed: " + e.getMessage(), e);
        }
        return false;
    }

    public String getUserCreationDate(int userId) {
        String sql = "SELECT created_at FROM users WHERE id = ?";
        try (PreparedStatement ps = dbManager.getConnection().prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("created_at");
        } catch (SQLException ignored) {}
        return "N/A";
    }

    private int countByField(String field, String value) {
        String sql = "SELECT COUNT(*) FROM users WHERE " + field + " = ?";
        try (PreparedStatement ps = dbManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, value);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            return 0;
        }
    }

    private User mapUser(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setUsername(rs.getString("username"));
        u.setEmail(rs.getString("email"));
        u.setFullName(rs.getString("full_name"));
        u.setPhone(rs.getString("phone"));
        u.setAddress(rs.getString("address"));
        try {
            u.setRole(rs.getString("role"));
        } catch (SQLException ignored) {
            u.setRole(User.ROLE_USER);
        }
        try {
            u.setActive(rs.getInt("active") == 1);
        } catch (SQLException ignored) {
            u.setActive(true);
        }
        return u;
    }
}
