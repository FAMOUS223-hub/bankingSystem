package com.calbank.ui;

import com.calbank.models.Account;
import com.calbank.models.Transaction;
import com.calbank.services.AccountService;
import com.calbank.services.ReportService;
import com.calbank.services.TransactionService;
import com.calbank.ui.theme.ThemeManager;
import com.calbank.utils.IconUtils;
import com.calbank.utils.TransactionUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

public final class DashboardPanel extends JPanel implements MainContentPanel.Refreshable {

    private final AccountService accountService = new AccountService();
    private final TransactionService transactionService = new TransactionService();

    public DashboardPanel() {
        setLayout(new BorderLayout());
        setBackground(ThemeManager.getBackgroundColor());
        refresh();
    }

    @Override
    public void refresh() {
        removeAll();
        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(ThemeManager.getBackgroundColor());
        content.setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.weightx = 1.0;

        com.calbank.models.User user = CurrentUser.getInstance().getUser();
        if (user == null) { add(content, BorderLayout.CENTER); return; }

        List<Account> accounts = accountService.getAccountsByUserId(user.getId());
        double totalBalance = accountService.getTotalBalance(user.getId());

        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 28, 0);
        content.add(createSummaryCardsRow(totalBalance, accounts.size(), accounts), gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 28, 0);
        content.add(createSectionHeader("Quick Actions"), gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 28, 0);
        content.add(createQuickActionsRow(), gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 10, 0);
        content.add(createSectionHeader("Account Overview"), gbc);

        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 28, 0);
        if (accounts.isEmpty()) {
            content.add(createEmptyCard("No accounts yet. Create your first account from My Accounts."), gbc);
        } else {
            for (Account acc : accounts) {
                gbc.gridy++;
                gbc.insets = new Insets(0, 0, 6, 0);
                content.add(createAccountCard(acc), gbc);
            }
        }

        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 10, 0);
        content.add(createSectionHeader("Recent Transactions"), gbc);

        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 28, 0);
        if (!accounts.isEmpty()) {
            List<Transaction> recent = transactionService.getRecentTransactions(accounts.get(0).getAccountId(), 8);
            if (recent.isEmpty()) {
                content.add(createEmptyCard("No transactions yet. Make your first deposit!"), gbc);
            } else {
                content.add(createTransactionsTable(recent), gbc);
            }
        }

        gbc.gridy++;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        content.add(Box.createVerticalGlue(), gbc);

        add(content, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private JPanel createSummaryCardsRow(double totalBalance, int accountCount, List<Account> accounts) {
        JPanel row = new JPanel(new GridLayout(1, 4, 16, 0));
        row.setOpaque(false);

        double monthlyIncome = 0, monthlySpending = 0;
        for (Account a : accounts) {
            try {
                ReportService ms = new ReportService();
                ReportService.MonthlySummary summary = ms.getMonthlySummary(a.getAccountId(),
                    java.time.Year.now().getValue(), java.time.MonthDay.now().getMonthValue());
                monthlyIncome += summary.totalDeposits;
                monthlySpending += summary.totalWithdrawals + summary.totalTransfers;
            } catch (Exception ignored) {}
        }

        row.add(createSummaryCard(IconUtils.get("money"), "Total Balance",
            TransactionUtils.formatCurrency(totalBalance), ThemeManager.getPrimaryColor()));
        row.add(createSummaryCard(IconUtils.get("accounts"), "Accounts",
            String.valueOf(accountCount), ThemeManager.getAccentColor()));
        row.add(createSummaryCard(IconUtils.get("chart"), "Monthly Income",
            TransactionUtils.formatCurrency(monthlyIncome), ThemeManager.getInfoColor()));
        row.add(createSummaryCard(IconUtils.get("bills"), "Monthly Spending",
            TransactionUtils.formatCurrency(monthlySpending), ThemeManager.getErrorColor()));

        return row;
    }

    private JPanel createSummaryCard(String icon, String label, String value, Color accent) {
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
        JLabel valLabel = new JLabel(value);
        valLabel.setFont(ThemeManager.getCardValueFont());
        valLabel.setForeground(ThemeManager.getTextColor());
        card.add(valLabel, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 12, 16);
        JLabel nameLabel = new JLabel(label);
        nameLabel.setFont(ThemeManager.getCardLabelFont());
        nameLabel.setForeground(ThemeManager.getTextColorMuted());
        card.add(nameLabel, gbc);

        return card;
    }

    private JPanel createQuickActionsRow() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        row.setOpaque(false);

        JButton depositBtn = new JButton(IconUtils.get("deposit") + " Deposit");
        ThemeManager.styleButton(depositBtn, ThemeManager.getAccentColor());
        depositBtn.addActionListener(e -> navigateTo("Deposit"));

        JButton withdrawBtn = new JButton(IconUtils.get("withdraw") + " Withdraw");
        ThemeManager.styleButton(withdrawBtn, ThemeManager.getErrorColor());
        withdrawBtn.addActionListener(e -> navigateTo("Withdraw"));

        JButton transferBtn = new JButton(IconUtils.get("transfer") + " Transfer");
        ThemeManager.styleButton(transferBtn, ThemeManager.getInfoColor());
        transferBtn.addActionListener(e -> navigateTo("Transfer"));

        row.add(depositBtn);
        row.add(withdrawBtn);
        row.add(transferBtn);
        return row;
    }

    private void navigateTo(String panelName) {
        java.awt.Container parent = SwingUtilities.getAncestorOfClass(MainContentPanel.class, this);
        if (parent instanceof MainContentPanel) {
            ((MainContentPanel) parent).showPanel(panelName);
        }
    }

    private JPanel createAccountCard(Account acc) {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(ThemeManager.getCardColor());
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.getBorderColor(), 1),
            BorderFactory.createEmptyBorder(12, 20, 12, 20)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.VERTICAL;

        gbc.gridx = 0; gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 0, 16);
        JLabel iconLabel = new JLabel(IconUtils.get("accounts"));
        iconLabel.setFont(IconUtils.getIconFontLarge());
        iconLabel.setForeground(ThemeManager.getPrimaryColor());
        card.add(iconLabel, gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 2, 0);
        JLabel idLabel = new JLabel(acc.getAccountId());
        idLabel.setFont(ThemeManager.getLabelFont().deriveFont(Font.BOLD));
        idLabel.setForeground(ThemeManager.getTextColor());
        card.add(idLabel, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        JLabel typeLabel = new JLabel(acc.getAccountType() + " Account");
        typeLabel.setFont(ThemeManager.getSmallFont());
        typeLabel.setForeground(ThemeManager.getTextColorMuted());
        card.add(typeLabel, gbc);

        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(0, 16, 0, 0);
        JLabel balanceLabel = new JLabel(TransactionUtils.formatCurrency(acc.getBalance()));
        balanceLabel.setFont(ThemeManager.getHeadingFont());
        balanceLabel.setForeground(acc.getBalance() >= 0 ? ThemeManager.getPositiveColor() : ThemeManager.getNegativeColor());
        card.add(balanceLabel, gbc);

        return card;
    }

    private JScrollPane createTransactionsTable(List<Transaction> transactions) {
        DefaultTableModel model = new DefaultTableModel(
            new String[]{"Type", "Amount", "Balance", "Description", "Date"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        for (Transaction t : transactions) {
            String amountStr = (t.isDeposit() ? "+" : "-") + TransactionUtils.formatCurrency(t.getAmount());
            model.addRow(new Object[]{
                t.getTransactionType(),
                amountStr,
                TransactionUtils.formatCurrency(t.getBalanceAfter()),
                t.getDescription(),
                com.calbank.utils.DateUtils.formatShort(t.getCreatedAt())
            });
        }

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

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(ThemeManager.getBorderColor(), 1));
        sp.setPreferredSize(new Dimension(0, Math.min(transactions.size() * 32 + 40, 300)));
        return sp;
    }

    private JLabel createSectionHeader(String text) {
        JLabel label = new JLabel(text);
        label.setFont(ThemeManager.getSectionFont());
        label.setForeground(ThemeManager.getTextColor());
        return label;
    }

    private JPanel createEmptyCard(String message) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(ThemeManager.getCardColor());
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.getBorderColor(), 1),
            BorderFactory.createEmptyBorder(24, 20, 24, 20)));
        JLabel label = new JLabel(message);
        label.setFont(ThemeManager.getLabelFont());
        label.setForeground(ThemeManager.getTextColorMuted());
        panel.add(label);
        return panel;
    }
}
