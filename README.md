# CalBank

**Smart Banking for Everyone** — A full-featured desktop banking application built with Java Swing and MySQL.

![Java](https://img.shields.io/badge/Java-11%2B-orange)
![MySQL](https://img.shields.io/badge/MySQL-8.x-blue)
![Swing](https://img.shields.io/badge/UI-Java%20Swing-green)
![License](https://img.shields.io/badge/License-MIT-yellow)

---

## Overview

CalBank is a desktop banking application that provides a complete banking experience through a professional graphical user interface. It supports multiple user accounts, real-time transaction processing, an admin dashboard with super-admin privileges, and a modern dark/light theme system.

## Features

### Authentication & Accounts
- User registration with full field validation
- Secure login with SHA-256 password hashing and salt
- Session management with automatic logout
- Multiple account types per user (Checking, Savings, Money Market)
- Auto-generated account numbers (`CAL-XXXXXXXX-XXXX`)

### Transactions
- **Deposit** funds with category tagging
- **Withdraw** funds with real-time balance validation
- **Transfer** between any two accounts with atomic transactions
- Full transaction history with search and filtering
- Auto-generated receipt numbers (`RCPT-YYYYMMDDHHmmss-XXXXXX`)

### Dashboard
- Real-time balance overview across all accounts
- Recent transactions list
- Quick-action shortcuts to deposit, withdraw, and transfer

### Reports
- Transaction summaries by date range and type
- CSV export for all data

### Financial Calculators
- Loan calculator with monthly payment computation
- Savings calculator with compound interest projection
- Tip calculator

### Categories
- Custom transaction categories with icons and colors
- Pre-seeded default categories (Food, Transport, Bills, etc.)

### Admin Dashboard
- System-wide user management (view, edit, activate/deactivate)
- All transactions view with filtering
- System-wide settings (appearance, security, database, about)
- Super admin account (cannot be deactivated or deleted)
- Real-time system statistics

### User Profile
- View and edit personal information (name, email, phone, address)
- Avatar display

### Settings
- Dark / Light theme toggle with live switching
- Password change with validation

### UI/UX
- Font Awesome 6.5.1 icon integration (solid + regular)
- Professional split-screen login and registration panels
- Sidebar navigation with icon + text
- Animated splash screen with progress bar
- Toast notifications for success/error feedback
- Responsive dark mode with full color system
- Exit confirmation dialog

## Screenshots

> Add screenshots here after building and running the application.

## Tech Stack

| Component | Technology |
|-----------|-----------|
| Language | Java 11+ (source/target 11) |
| UI Framework | Java Swing |
| Database | MySQL 8.x |
| Icons | Font Awesome 6.5.1 |
| Build | NetBeans / Ant |
| Password Hashing | SHA-256 with salt |
| Logging | SLF4J 2.0.9 |

## Project Structure

```
CalBank/
├── src/main/com/calbank/
│   ├── main/
│   │   └── CalBankApp.java              # Application entry point
│   ├── database/
│   │   └── DatabaseManager.java          # MySQL connection & schema management
│   ├── models/
│   │   ├── User.java
│   │   ├── Account.java
│   │   ├── Transaction.java
│   │   ├── Category.java
│   │   ├── Loan.java
│   │   ├── Savings.java
│   │   └── UserPreference.java
│   ├── services/
│   │   ├── UserService.java
│   │   ├── AccountService.java
│   │   ├── TransactionService.java
│   │   ├── CategoryService.java
│   │   └── ReportService.java
│   ├── ui/
│   │   ├── theme/
│   │   │   └── ThemeManager.java         # Full dark/light theme system
│   │   ├── admin/
│   │   │   ├── AdminDashboardPanel.java
│   │   │   ├── AdminUserManagementPanel.java
│   │   │   ├── AdminTransactionPanel.java
│   │   │   └── AdminSettingsPanel.java
│   │   ├── LoginPanel.java
│   │   ├── RegisterPanel.java
│   │   ├── SplashScreen.java
│   │   ├── SidebarPanel.java
│   │   ├── HeaderPanel.java
│   │   ├── MainContentPanel.java
│   │   ├── DashboardPanel.java
│   │   ├── AccountPanel.java
│   │   ├── DepositPanel.java
│   │   ├── WithdrawPanel.java
│   │   ├── TransferPanel.java
│   │   ├── MiniStatementPanel.java
│   │   ├── CategoryPanel.java
│   │   ├── ReportsPanel.java
│   │   ├── CalculatorsPanel.java
│   │   ├── ProfilePanel.java
│   │   ├── SettingsPanel.java
│   │   ├── ToastNotification.java
│   │   └── CurrentUser.java
│   └── utils/
│       ├── IconUtils.java                # Font Awesome icon loading
│       ├── PasswordUtils.java            # SHA-256 hashing
│       ├── TransactionUtils.java         # Account/receipt number generation
│       ├── InputValidator.java           # Form field validation
│       ├── DateUtils.java                # Date formatting
│       └── ExportUtils.java              # CSV export
├── fonts/
│   ├── fa-solid-900.ttf                  # Font Awesome Solid icons
│   └── fa-regular-400.ttf               # Font Awesome Regular icons
├── lib/
│   ├── mysql-connector-j-8.3.0.jar       # MySQL JDBC driver
│   ├── slf4j-api-2.0.9.jar
│   └── slf4j-nop-2.0.9.jar
├── nbproject/                            # NetBeans project config
├── build.xml                             # Ant build file
├── run.sh                                # Shell launcher script
└── README.md
```

## Prerequisites

- **Java 11 or higher** ([Download](https://www.java.com))
- **MySQL 8.x** running on `localhost:3306`
- **NetBeans** (optional, for IDE development) or any Java IDE

## Setup

### 1. Clone the repository

```bash
git clone https://github.com/YOUR_USERNAME/CalBank.git
cd CalBank
```

### 2. Configure MySQL

The application expects a MySQL instance on localhost:3306. The database `calbank` and all tables are created automatically on first launch.

Default credentials (edit `DatabaseManager.java` if yours differ):

```java
private static final String DB_USER = "root";
private static final String DB_PASS = "your_password";
```

### 3. Compile and run

**Using the launcher script (macOS/Linux):**

```bash
chmod +x run.sh
./run.sh
```

**Using javac directly:**

```bash
javac -d out -cp "lib/*" -sourcepath src/main \
    src/main/com/calbank/main/CalBankApp.java

java -cp "out:lib/*" com.calbank.main.CalBankApp
```

**Using NetBeans:**

Open the project folder in NetBeans. The `nbproject/` configuration is pre-configured with Java 11 source/target and the correct classpath. Click **Run** or press `F6`.

### 4. Using Ant

```bash
ant run
```

## Default Accounts

On first launch, an admin account is automatically created:

| Role | Username | Password |
|------|----------|----------|
| Admin | `admin` | `admin123` |

Register a new user account through the login screen to access the user dashboard.

## Database Schema

The application manages 7 tables:

| Table | Description |
|-------|-------------|
| `users` | User accounts with roles (USER/ADMIN) |
| `accounts` | Bank accounts linked to users |
| `transactions` | All deposit, withdrawal, and transfer records |
| `categories` | Transaction categories with icons and colors |
| `loans` | Loan records |
| `savings` | Savings plan records |
| `user_preferences` | Theme and notification settings |

All tables are created automatically. Default categories are seeded on first run.

## Security

- Passwords are hashed using **SHA-256 with salt** before storage
- Users can only access their own accounts and transactions
- Admin accounts are protected from deactivation/deletion
- Input validation on all forms (username, email, password, amounts)
- SQL injection prevention via PreparedStatement throughout

## Building a Release JAR

```bash
mkdir -p dist
javac -d build/classes -cp "lib/*" -sourcepath src/main \
    src/main/com/calbank/main/CalBankApp.java

# Copy libraries
cp -r lib/* dist/

# Create manifest
echo "Main-Class: com.calbank.main.CalBankApp" > dist/MANIFEST.MF
echo "Class-Path: mysql-connector-j-8.3.0.jar slf4j-api-2.0.9.jar slf4j-nop-2.0.9.jar" >> dist/MANIFEST.MF

# Package
cd build/classes && jar cfm ../../dist/CalBank.jar ../../dist/MANIFEST.MF . && cd ../..

# Include fonts in the JAR
cd fonts && jar uf ../dist/CalBank.jar fa-solid-900.ttf fa-regular-400.ttf && cd ..
```

Or use NetBeans: **Clean and Build Project** (`Shift+F11`) produces `dist/CalBank.jar`.

## Platform Notes

- Tested on **macOS** with Java 17
- Source/target compatibility: **Java 11**
- The splash screen and animations use Swing Timers — works on all platforms
- Font Awesome TTF fonts are bundled — no system font dependencies

## License

This project is provided as an educational application for learning purposes.

---

**Built with Java Swing + MySQL + Font Awesome**
