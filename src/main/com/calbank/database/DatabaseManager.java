package com.calbank.database;

import java.sql.*;

public final class DatabaseManager {

    private static DatabaseManager instance;
    private Connection connection;

    private static final String DB_HOST = "localhost";
    private static final int DB_PORT = 3306;
    private static final String DB_NAME = "calbank";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "mynameis123MASTER";

    private static final String BASE_URL = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT;
    private static final String DB_URL = BASE_URL + "/" + DB_NAME + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    private DatabaseManager() {
        initializeDatabase();
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    private void initializeDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            createDatabaseIfNotExists();
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            connection.setAutoCommit(true);
            System.out.println("Connected to MySQL database.");
        } catch (Exception ex) {
            throw new RuntimeException("Failed to connect to MySQL: " + ex.getMessage(), ex);
        }
        createTables();
        seedDefaultCategories();
        seedAdminUser();
    }

    private void createDatabaseIfNotExists() throws SQLException {
        String baseUrl = BASE_URL + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        try (Connection conn = DriverManager.getConnection(baseUrl, DB_USER, DB_PASS);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME + " CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
        }
    }

    private void createTables() {
        String[] tables = {
            "CREATE TABLE IF NOT EXISTS users ("
            + "id INT AUTO_INCREMENT PRIMARY KEY,"
            + "username VARCHAR(50) UNIQUE NOT NULL,"
            + "password_hash VARCHAR(255) NOT NULL,"
            + "email VARCHAR(100) UNIQUE NOT NULL,"
            + "full_name VARCHAR(100) NOT NULL,"
            + "phone VARCHAR(30) DEFAULT '',"
            + "address VARCHAR(255) DEFAULT '',"
            + "role VARCHAR(20) DEFAULT 'USER',"
            + "active TINYINT(1) DEFAULT 1,"
            + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
            + ") ENGINE=InnoDB",

            "CREATE TABLE IF NOT EXISTS accounts ("
            + "account_id VARCHAR(30) PRIMARY KEY,"
            + "user_id INT NOT NULL,"
            + "account_type VARCHAR(30) NOT NULL,"
            + "balance DOUBLE DEFAULT 0,"
            + "currency VARCHAR(10) DEFAULT 'USD',"
            + "status VARCHAR(20) DEFAULT 'ACTIVE',"
            + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
            + "FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE"
            + ") ENGINE=InnoDB",

            "CREATE TABLE IF NOT EXISTS categories ("
            + "category_id INT AUTO_INCREMENT PRIMARY KEY,"
            + "user_id INT DEFAULT 0,"
            + "name VARCHAR(50) NOT NULL,"
            + "icon VARCHAR(10) DEFAULT '',"
            + "color VARCHAR(10) DEFAULT '#4CAF50',"
            + "is_default TINYINT(1) DEFAULT 0,"
            + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
            + "UNIQUE KEY uk_user_cat (user_id, name)"
            + ") ENGINE=InnoDB",

            "CREATE TABLE IF NOT EXISTS transactions ("
            + "transaction_id INT AUTO_INCREMENT PRIMARY KEY,"
            + "account_id VARCHAR(30) NOT NULL,"
            + "transaction_type VARCHAR(20) NOT NULL,"
            + "amount DOUBLE NOT NULL,"
            + "balance_after DOUBLE NOT NULL,"
            + "description VARCHAR(255) DEFAULT '',"
            + "recipient_account VARCHAR(30) DEFAULT '',"
            + "category_id INT DEFAULT NULL,"
            + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
            + "receipt_number VARCHAR(40),"
            + "UNIQUE KEY uk_receipt (receipt_number),"
            + "FOREIGN KEY(account_id) REFERENCES accounts(account_id) ON DELETE CASCADE,"
            + "FOREIGN KEY(category_id) REFERENCES categories(category_id) ON DELETE SET NULL"
            + ") ENGINE=InnoDB",

            "CREATE TABLE IF NOT EXISTS loans ("
            + "loan_id INT AUTO_INCREMENT PRIMARY KEY,"
            + "account_id VARCHAR(30) NOT NULL,"
            + "principal_amount DOUBLE NOT NULL,"
            + "interest_rate DOUBLE NOT NULL,"
            + "loan_term_months INT NOT NULL,"
            + "monthly_payment DOUBLE,"
            + "status VARCHAR(20) DEFAULT 'PENDING',"
            + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
            + "FOREIGN KEY(account_id) REFERENCES accounts(account_id) ON DELETE CASCADE"
            + ") ENGINE=InnoDB",

            "CREATE TABLE IF NOT EXISTS savings ("
            + "savings_id INT AUTO_INCREMENT PRIMARY KEY,"
            + "account_id VARCHAR(30) NOT NULL,"
            + "initial_amount DOUBLE NOT NULL,"
            + "monthly_contribution DOUBLE DEFAULT 0,"
            + "interest_rate DOUBLE DEFAULT 0,"
            + "months INT NOT NULL,"
            + "final_amount DOUBLE,"
            + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
            + "FOREIGN KEY(account_id) REFERENCES accounts(account_id) ON DELETE CASCADE"
            + ") ENGINE=InnoDB",

            "CREATE TABLE IF NOT EXISTS user_preferences ("
            + "pref_id INT AUTO_INCREMENT PRIMARY KEY,"
            + "user_id INT UNIQUE NOT NULL,"
            + "theme VARCHAR(10) DEFAULT 'LIGHT',"
            + "notifications_enabled TINYINT(1) DEFAULT 1,"
            + "FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE"
            + ") ENGINE=InnoDB"
        };

        try (Statement stmt = connection.createStatement()) {
            for (String sql : tables) {
                stmt.execute(sql);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create database tables", e);
        }
    }

    private void seedDefaultCategories() {
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM categories WHERE is_default = 1")) {
            if (rs.next() && rs.getInt(1) == 0) {
                String[] defaultNames = {
                    "Food & Dining", "Transportation", "Bills & Utilities",
                    "Shopping", "Entertainment", "Healthcare",
                    "Education", "Salary", "Investment", "Other"
                };
                String[] defaultIcons = {
                    "\uD83C\uDF54", "\uD83D\uDE97", "\uD83D\uDCA1",
                    "\uD83D\uDECD\uFE0F", "\uD83C\uDFAC", "\uD83C\uDFE5",
                    "\uD83D\uDCDA", "\uD83D\uDCB0", "\uD83D\uDCC8", "\u2753"
                };
                String[] defaultColors = {
                    "#FF5722", "#2196F3", "#FFC107",
                    "#E91E63", "#9C27B0", "#00BCD4",
                    "#607D8B", "#4CAF50", "#FF9800", "#795548"
                };

                String sql = "INSERT INTO categories (user_id, name, icon, color, is_default) "
                           + "VALUES (0, ?, ?, ?, 1)";
                try (PreparedStatement ps = connection.prepareStatement(sql)) {
                    for (int i = 0; i < defaultNames.length; i++) {
                        ps.setString(1, defaultNames[i]);
                        ps.setString(2, defaultIcons[i]);
                        ps.setString(3, defaultColors[i]);
                        ps.executeUpdate();
                    }
                }
            }
        } catch (SQLException ignored) {}
    }

    private void seedAdminUser() {
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users WHERE role = 'ADMIN'")) {
            if (rs.next() && rs.getInt(1) == 0) {
                String hash = com.calbank.utils.PasswordUtils.hashPassword("admin123");
                String sql = "INSERT INTO users (username, password_hash, email, full_name, phone, address, role) "
                           + "VALUES (?, ?, 'admin@calbank.com', 'System Administrator', '+1-555-0100', '100 Admin Blvd', 'ADMIN')";
                try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, "admin");
                    ps.setString(2, hash);
                    ps.executeUpdate();
                    ResultSet keys = ps.getGeneratedKeys();
                    if (keys.next()) {
                        int adminId = keys.getInt(1);
                        String acctId = com.calbank.utils.TransactionUtils.generateAccountNumber();
                        PreparedStatement acctPs = connection.prepareStatement(
                            "INSERT INTO accounts (account_id, user_id, account_type, balance) VALUES (?, ?, 'ADMIN', 0.0)");
                        acctPs.setString(1, acctId);
                        acctPs.setInt(2, adminId);
                        acctPs.executeUpdate();
                    }
                }
            }
        } catch (SQLException ignored) {}
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to obtain database connection", e);
        }
        return connection;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Warning: Error closing database connection: " + e.getMessage());
        }
    }
}
