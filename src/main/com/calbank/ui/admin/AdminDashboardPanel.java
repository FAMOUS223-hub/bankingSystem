package com.calbank.ui.admin;

import com.calbank.models.Account;
import com.calbank.models.User;
import com.calbank.services.AccountService;
import com.calbank.services.TransactionService;
import com.calbank.services.UserService;
import com.calbank.ui.CurrentUser;
import com.calbank.ui.MainContentPanel;
import com.calbank.ui.theme.ThemeManager;
import com.calbank.utils.DateUtils;
import com.calbank.utils.IconUtils;
import com.calbank.utils.TransactionUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

public final class AdminDashboardPanel extends JPanel implements com.calbank.ui.MainContentPanel.Refreshable {

    private final UserService userService = new UserService();
    private final AccountService accountService = new AccountService();
    private final TransactionService transactionService = new TransactionService();

    private JLabel totalUsersVal, activeUsersVal, totalAccountsVal, totalBalanceVal;
    private JLabel totalDepositsVal, totalWithdrawalsVal, totalTransfersVal, txCountVal;
    private DefaultTableModel recentTxModel;
    private DefaultTableModel recentUsersModel;

    public AdminDashboardPanel() {
        setLayout(new BorderLayout());
        setBackground(ThemeManager.getBackgroundColor());

        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(ThemeManager.getBackgroundColor());
        content.setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.weightx = 1.0;

        User user = CurrentUser.getInstance().getUser();

        // Title row
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 4, 0);
        JLabel title = new JLabel("Admin Dashboard");
        title.setFont(ThemeManager.getTitleFont());
        title.setForeground(ThemeManager.getTextColor());
        content.add(title, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 28, 0);
        JLabel subtitle = new JLabel("System overview and management console");
        subtitle.setFont(ThemeManager.getSmallFont());
        subtitle.setForeground(ThemeManager.getTextColorMuted());
        content.add(subtitle, gbc);

        // System stats row 1 - Users & Accounts
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 16, 0);
        content.add(createSectionHeader("System Overview"), gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 28, 0);
        content.add(createStatsRow1(), gbc);

        // System stats row 2 - Financial
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 16, 0);
        content.add(createSectionHeader("Financial Summary"), gbc);

        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 28, 0);
        content.add(createStatsRow2(), gbc);

        // Quick Actions
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 12, 0);
        content.add(createSectionHeader("Quick Actions"), gbc);

        gbc.gridy = 7;
        gbc.insets = new Insets(0, 0, 28, 0);
        content.add(createQuickActions(), gbc);

        // Recent Users
        gbc.gridy = 8;
        gbc.insets = new Insets(0, 0, 12, 0);
        content.add(createSectionHeader("Recent Users"), gbc);

        gbc.gridy = 9;
        gbc.insets = new Insets(0, 0, 28, 0);
        content.add(createRecentUsersTable(), gbc);

        // Recent Transactions
        gbc.gridy = 10;
        gbc.insets = new Insets(0, 0, 12, 0);
        content.add(createSectionHeader("Recent System Transactions"), gbc);

        gbc.gridy = 11;
        gbc.insets = new Insets(0, 0, 28, 0);
        content.add(createRecentTransactionsTable(), gbc);

        // Spacer
        gbc.gridy = 12;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        content.add(Box.createVerticalGlue(), gbc);

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        refresh();
    }

    @Override
    public void refresh() {
        refreshData();
    }

    private void refreshData() {
        int totalUsers = userService.getTotalUserCount();
        int activeUsers = userService.getActiveUserCount();
        int totalAccounts = accountService.getTotalAccountCount();
        double totalBalance = accountService.getAllAccountsTotalBalance();
        double totalDeposits = transactionService.getTotalDeposits();
        double totalWithdrawals = transactionService.getTotalWithdrawals();
        double totalTransfers = transactionService.getTotalTransfers();
        int txCount = transactionService.getTotalTransactionCount();

        totalUsersVal.setText(String.valueOf(totalUsers));
        activeUsersVal.setText(String.valueOf(activeUsers));
        totalAccountsVal.setText(String.valueOf(totalAccounts));
        totalBalanceVal.setText(TransactionUtils.formatCurrency(totalBalance));
        totalDepositsVal.setText(TransactionUtils.formatCurrency(totalDeposits));
        totalWithdrawalsVal.setText(TransactionUtils.formatCurrency(totalWithdrawals));
        totalTransfersVal.setText(TransactionUtils.formatCurrency(totalTransfers));
        txCountVal.setText(String.valueOf(txCount));

        // Refresh recent users table
        recentUsersModel.setRowCount(0);
        List<User> users = userService.getAllUsers();
        for (int i = 0; i < Math.min(users.size(), 8); i++) {
            User u = users.get(i);
            recentUsersModel.addRow(new Object[]{
                u.getId(), u.getUsername(), u.getFullName(),
                u.getEmail(), u.getRole(), u.isActive() ? "Active" : "Inactive"
            });
        }

        // Refresh recent transactions table
        recentTxModel.setRowCount(0);
        List<com.calbank.models.Transaction> recentTx = transactionService.getAllTransactions(10);
        for (com.calbank.models.Transaction t : recentTx) {
            String amountStr = (t.isDeposit() ? "+" : "-") + TransactionUtils.formatCurrency(t.getAmount());
            recentTxModel.addRow(new Object[]{
                t.getAccountId(),
                t.getTransactionType(),
                amountStr,
                TransactionUtils.formatCurrency(t.getBalanceAfter()),
                t.getDescription(),
                t.getReceiptNumber(),
                DateUtils.formatShort(t.getCreatedAt())
            });
        }
    }

    private JPanel createStatsRow1() {
        JPanel row = new JPanel(new GridLayout(1, 4, 16, 0));
        row.setOpaque(false);

        totalUsersVal = new JLabel("0");
        activeUsersVal = new JLabel("0");
        totalAccountsVal = new JLabel("0");
        txCountVal = new JLabel("0");

        row.add(createStatCard(IconUtils.get("users"), "Total Users", totalUsersVal, ThemeManager.getPrimaryColor()));
        row.add(createStatCard(IconUtils.get("check"), "Active Users", activeUsersVal, ThemeManager.getAccentColor()));
        row.add(createStatCard(IconUtils.get("accounts"), "Total Accounts", totalAccountsVal, ThemeManager.getInfoColor()));
        row.add(createStatCard(IconUtils.get("chart"), "Transactions", txCountVal, new Color(156, 39, 176)));

        return row;
    }

    private JPanel createStatsRow2() {
        JPanel row = new JPanel(new GridLayout(1, 4, 16, 0));
        row.setOpaque(false);

        totalBalanceVal = new JLabel("$0.00");
        totalDepositsVal = new JLabel("$0.00");
        totalWithdrawalsVal = new JLabel("$0.00");
        totalTransfersVal = new JLabel("$0.00");

        row.add(createStatCard(IconUtils.get("money"), "Total Balance", totalBalanceVal, ThemeManager.getPrimaryColor()));
        row.add(createStatCard(IconUtils.get("deposit"), "Total Deposits", totalDepositsVal, ThemeManager.getAccentColor()));
        row.add(createStatCard(IconUtils.get("withdraw"), "Total Withdrawals", totalWithdrawalsVal, ThemeManager.getErrorColor()));
        row.add(createStatCard(IconUtils.get("transfer"), "Total Transfers", totalTransfersVal, new Color(255, 152, 0)));

        return row;
    }

    private JPanel createStatCard(String icon, String label, JLabel valueLabel, Color accent) {
        JPanel card = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeManager.getCardColor());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(ThemeManager.getBorderColor());
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.setColor(accent);
                g2.fillRoundRect(0, 0, 5, getHeight(), 4, 4);
            }
        };
        card.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridheight = 2;
        gbc.insets = new Insets(14, 16, 14, 10);
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(IconUtils.getIconFontLarge());
        iconLabel.setForeground(accent);
        card.add(iconLabel, gbc);

        gbc.gridx = 1; gbc.gridheight = 1;
        gbc.insets = new Insets(12, 0, 0, 16);
        valueLabel.setFont(ThemeManager.getCardValueFont());
        valueLabel.setForeground(ThemeManager.getTextColor());
        card.add(valueLabel, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 12, 16);
        JLabel nameLabel = new JLabel(label);
        nameLabel.setFont(ThemeManager.getCardLabelFont());
        nameLabel.setForeground(ThemeManager.getTextColorMuted());
        card.add(nameLabel, gbc);

        return card;
    }

    private JPanel createQuickActions() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        panel.setOpaque(false);

        JButton usersBtn = new JButton(IconUtils.get("users") + " Manage Users");
        ThemeManager.stylePrimaryButton(usersBtn);
        usersBtn.addActionListener(e -> {
            java.awt.Container parent = SwingUtilities.getAncestorOfClass(MainContentPanel.class, this);
            if (parent instanceof MainContentPanel) {
                ((MainContentPanel) parent).showPanel("Admin Users");
            }
        });

        JButton txBtn = new JButton(IconUtils.get("transactions") + " All Transactions");
        ThemeManager.styleButton(txBtn, ThemeManager.getInfoColor());
        txBtn.addActionListener(e -> {
            java.awt.Container parent = SwingUtilities.getAncestorOfClass(MainContentPanel.class, this);
            if (parent instanceof MainContentPanel) {
                ((MainContentPanel) parent).showPanel("Admin Transactions");
            }
        });

        JButton settingsBtn = new JButton(IconUtils.get("settings") + " System Settings");
        ThemeManager.styleButton(settingsBtn, ThemeManager.getWarningColor());
        settingsBtn.addActionListener(e -> {
            java.awt.Container parent = SwingUtilities.getAncestorOfClass(MainContentPanel.class, this);
            if (parent instanceof MainContentPanel) {
                ((MainContentPanel) parent).showPanel("Admin Settings");
            }
        });

        JButton refreshBtn = new JButton(IconUtils.get("refresh") + " Refresh");
        ThemeManager.styleButton(refreshBtn, ThemeManager.getAccentColor());
        refreshBtn.addActionListener(e -> refreshData());

        panel.add(usersBtn);
        panel.add(txBtn);
        panel.add(settingsBtn);
        panel.add(refreshBtn);

        return panel;
    }

    private JScrollPane createRecentUsersTable() {
        String[] columns = {"ID", "Username", "Full Name", "Email", "Role", "Status"};
        recentUsersModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = createStyledTable(recentUsersModel);
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(ThemeManager.getBorderColor(), 1));
        sp.setPreferredSize(new Dimension(0, 260));
        return sp;
    }

    private JScrollPane createRecentTransactionsTable() {
        String[] columns = {"Account", "Type", "Amount", "Balance", "Description", "Receipt #", "Date"};
        recentTxModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = createStyledTable(recentTxModel);
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(ThemeManager.getBorderColor(), 1));
        sp.setPreferredSize(new Dimension(0, 260));
        return sp;
    }

    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(ThemeManager.getTableFont());
        table.setRowHeight(32);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setBackground(ThemeManager.getCardColor());
        table.setForeground(ThemeManager.getTextColor());
        table.setSelectionBackground(ThemeManager.getTableSelectionColor());
        table.getTableHeader().setFont(ThemeManager.getTableHeaderFont());
        table.getTableHeader().setBackground(ThemeManager.getTableHeaderColor());
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setPreferredSize(new Dimension(0, 36));
        table.getTableHeader().setReorderingAllowed(false);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                if (!sel) {
                    c.setBackground(row % 2 == 0 ? ThemeManager.getTableRowEven() : ThemeManager.getTableRowOdd());
                }
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return c;
            }
        });

        return table;
    }

    private JLabel createSectionHeader(String text) {
        JLabel label = new JLabel(text);
        label.setFont(ThemeManager.getSectionFont());
        label.setForeground(ThemeManager.getTextColor());
        return label;
    }
}
