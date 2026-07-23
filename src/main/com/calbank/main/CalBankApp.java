package com.calbank.main;

import com.calbank.database.DatabaseManager;
import com.calbank.services.AccountService;
import com.calbank.services.TransactionService;
import com.calbank.ui.*;
import com.calbank.ui.admin.AdminDashboardPanel;
import com.calbank.ui.admin.AdminSettingsPanel;
import com.calbank.ui.admin.AdminTransactionPanel;
import com.calbank.ui.admin.AdminUserManagementPanel;
import com.calbank.ui.theme.ThemeManager;

import javax.swing.*;
import java.awt.*;

public final class CalBankApp extends JFrame {

    private SidebarPanel sidebar;
    private HeaderPanel header;
    private MainContentPanel mainContent;

    private CalBankApp() {
        setTitle("CalBank - Smart Banking for Everyone");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setSize(1200, 750);
        setMinimumSize(new Dimension(960, 600));
        setLocationRelativeTo(null);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent we) {
                if (JOptionPane.showConfirmDialog(CalBankApp.this,
                        "Are you sure you want to exit CalBank?",
                        "Exit Confirmation",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                    DatabaseManager.getInstance().closeConnection();
                    System.exit(0);
                }
            }
        });
    }

    private void showLogin() {
        NavigationHelper.clear();
        LoginPanel login = new LoginPanel(
            () -> showDashboard(false),
            () -> showDashboard(true),
            this::showRegister
        );
        setContentPane(login);
        revalidate();
        repaint();
        SwingUtilities.invokeLater(() -> {
            login.requestFocusInWindow();
        });
    }

    private void showRegister() {
        RegisterPanel register = new RegisterPanel(this::showLogin, this::showLogin);
        setContentPane(register);
        revalidate();
        repaint();
    }

    private void showDashboard(boolean admin) {
        JPanel dashboardWrapper = new JPanel(new BorderLayout(0, 0));
        dashboardWrapper.setBackground(ThemeManager.getBackgroundColor());

        sidebar = new SidebarPanel(this::showLogin, admin);
        dashboardWrapper.add(sidebar, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new BorderLayout(0, 0));
        rightPanel.setBackground(ThemeManager.getBackgroundColor());

        header = new HeaderPanel();
        rightPanel.add(header, BorderLayout.NORTH);

        mainContent = new MainContentPanel();

        if (admin) {
            mainContent.addPanel(new AdminDashboardPanel(), "Admin Dashboard");
            mainContent.addPanel(new AdminUserManagementPanel(), "Admin Users");
            mainContent.addPanel(new AdminTransactionPanel(), "Admin Transactions");
            mainContent.addPanel(new AdminSettingsPanel(this::refreshAllThemes), "Admin Settings");
            mainContent.addPanel(new ProfilePanel(() -> { if (header != null) header.refreshTheme(); }), "My Profile");
        } else {
            mainContent.addPanel(new DashboardPanel(), "Dashboard");
            mainContent.addPanel(new AccountPanel(), "My Accounts");
            mainContent.addPanel(new DepositPanel(new AccountService(), new TransactionService()), "Deposit");
            mainContent.addPanel(new WithdrawPanel(new AccountService(), new TransactionService()), "Withdraw");
            mainContent.addPanel(new TransferPanel(new AccountService(), new TransactionService()), "Transfer");
            mainContent.addPanel(new MiniStatementPanel(), "Transactions");
            mainContent.addPanel(new CategoryPanel(), "Categories");
            mainContent.addPanel(new ReportsPanel(), "Reports");
            mainContent.addPanel(new CalculatorsPanel(), "Calculators");
            mainContent.addPanel(new ProfilePanel(() -> { if (header != null) header.refreshTheme(); }), "Profile");
            mainContent.addPanel(new SettingsPanel(this::refreshAllThemes), "Settings");
        }

        rightPanel.add(mainContent, BorderLayout.CENTER);
        dashboardWrapper.add(rightPanel, BorderLayout.CENTER);

        setupSidebarNavigation(admin);

        NavigationHelper.setPanelNavigator(panelName -> {
            sidebar.setActive(NavigationHelper.sidebarLabelForPanel(panelName));
            mainContent.showPanel(panelName);
        });

        setContentPane(dashboardWrapper);
        revalidate();
        repaint();

        if (admin) {
            sidebar.setActive("Dashboard");
            mainContent.showPanel("Admin Dashboard");
        } else {
            sidebar.setActive("Dashboard");
            mainContent.showPanel("Dashboard");
        }

        SwingUtilities.invokeLater(() -> {
            toFront();
            requestFocus();
        });
    }

    private void setupSidebarNavigation(boolean isAdmin) {
        String[] userNavNames = {"Dashboard", "My Accounts", "Deposit", "Withdraw",
            "Transfer", "Transactions", "Categories", "Reports",
            "Calculators", "Profile", "Settings"};
        String[] userPanelNames = {"Dashboard", "My Accounts", "Deposit", "Withdraw",
            "Transfer", "Transactions", "Categories", "Reports",
            "Calculators", "Profile", "Settings"};

        String[] adminNavNames = {"Dashboard", "Manage Users", "All Transactions",
            "System Settings", "My Profile"};
        String[] adminPanelNames = {"Admin Dashboard", "Admin Users", "Admin Transactions",
            "Admin Settings", "My Profile"};

        String[] navNames = isAdmin ? adminNavNames : userNavNames;
        String[] panelNames = isAdmin ? adminPanelNames : userPanelNames;

        for (SidebarPanel.SidebarButton btn : sidebar.getButtons()) {
            String btnText = btn.getLabelText();
            for (int i = 0; i < navNames.length; i++) {
                if (btnText.equals(navNames[i])) {
                    final String panelName = panelNames[i];
                    final String navName = navNames[i];
                    btn.addActionListener(e -> {
                        sidebar.setActive(navName);
                        mainContent.showPanel(panelName);
                    });
                    break;
                }
            }
        }
    }

    private void refreshAllThemes() {
        ThemeManager.applyGlobalTheme();
        if (sidebar != null) sidebar.refreshTheme();
        if (header != null) header.refreshTheme();
        if (mainContent != null) mainContent.refreshTheme();
        getContentPane().setBackground(ThemeManager.getBackgroundColor());
        revalidate();
        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            ThemeManager.applyGlobalTheme();

            final CalBankApp app = new CalBankApp();

            final JDialog splashDialog = new JDialog((Frame) null, "CalBank", false);
            splashDialog.setUndecorated(true);
            splashDialog.setSize(480, 340);
            splashDialog.setLocationRelativeTo(null);
            splashDialog.setAlwaysOnTop(true);
            splashDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

            com.calbank.ui.SplashScreen splashPanel = new com.calbank.ui.SplashScreen();
            splashDialog.setContentPane(splashPanel);

            Timer animTimer = new Timer(25, null);
            final float[] progress = {0f};
            animTimer.addActionListener(e -> {
                progress[0] += 0.02f;
                if (progress[0] > 1f) progress[0] = 1f;
                splashPanel.updateProgress(progress[0]);
            });
            animTimer.start();
            splashDialog.setVisible(true);

            SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() {
                    try {
                        DatabaseManager.getInstance();
                        return true;
                    } catch (Exception e) {
                        return false;
                    }
                }

                @Override
                protected void done() {
                    animTimer.stop();
                    try {
                        Boolean ok = get();
                        if (!ok) {
                            splashDialog.dispose();
                            app.showLogin();
                            JOptionPane.showMessageDialog(app,
                                "Database connection failed.\nPlease ensure MySQL is running.\nYou can still use the app.",
                                "Database Warning", JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                    } catch (Exception e) {
                        splashDialog.dispose();
                        app.showLogin();
                        JOptionPane.showMessageDialog(app,
                            "Startup error: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    splashPanel.updateProgress(1f);
                    Timer finishTimer = new Timer(600, ev -> {
                        splashDialog.dispose();
                        app.setVisible(true);
                        app.showLogin();
                    });
                    finishTimer.setRepeats(false);
                    finishTimer.start();
                }
            };
            worker.execute();
        });
    }
}
