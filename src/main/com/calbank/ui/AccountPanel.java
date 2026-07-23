package com.calbank.ui;

import com.calbank.models.Account;
import com.calbank.services.AccountService;
import com.calbank.ui.theme.ThemeManager;
import com.calbank.utils.IconUtils;
import com.calbank.utils.TransactionUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

public final class AccountPanel extends JPanel implements MainContentPanel.Refreshable {

    private final AccountService accountService = new AccountService();

    public AccountPanel() {
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
        gbc.gridx = 0; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;

        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setOpaque(false);
        JLabel title = new JLabel("My Accounts");
        title.setFont(ThemeManager.getTitleFont());
        title.setForeground(ThemeManager.getTextColor());
        JButton newAccountBtn = new JButton("Create New Account");
        ThemeManager.styleSuccessButton(newAccountBtn);
        newAccountBtn.addActionListener(e -> showNewAccountDialog());
        titleRow.add(title, BorderLayout.WEST);
        titleRow.add(newAccountBtn, BorderLayout.EAST);

        gbc.gridy = 0; gbc.insets = new Insets(0, 0, 24, 0);
        content.add(titleRow, gbc);

        List<Account> accounts = accountService.getAccountsByUserId(
            CurrentUser.getInstance().getUser().getId());
        double totalBalance = accountService.getTotalBalance(
            CurrentUser.getInstance().getUser().getId());

        gbc.gridy = 1; gbc.insets = new Insets(0, 0, 24, 0);
        JPanel summaryRow = new JPanel(new GridLayout(1, 3, 16, 0));
        summaryRow.setOpaque(false);
        summaryRow.add(makeStatCard("Total Balance", TransactionUtils.formatCurrency(totalBalance), ThemeManager.getPrimaryColor()));
        summaryRow.add(makeStatCard("Accounts", String.valueOf(accounts.size()), ThemeManager.getAccentColor()));
        summaryRow.add(makeStatCard("Types", getUniqueTypes(accounts), ThemeManager.getInfoColor()));
        content.add(summaryRow, gbc);

        DefaultTableModel model = new DefaultTableModel(
            new String[]{"Account ID", "Type", "Balance", "Currency"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        for (Account a : accounts)
            model.addRow(new Object[]{a.getAccountId(), a.getAccountType(),
                TransactionUtils.formatCurrency(a.getBalance()), a.getCurrency()});

        JTable table = new JTable(model);
        table.setFont(ThemeManager.getTableFont());
        table.setRowHeight(36);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setBackground(ThemeManager.getCardColor());
        table.setForeground(ThemeManager.getTextColor());
        table.setSelectionBackground(ThemeManager.getTableSelectionColor());
        table.getTableHeader().setFont(ThemeManager.getTableHeaderFont());
        table.getTableHeader().setBackground(ThemeManager.getTableHeaderColor());
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setPreferredSize(new Dimension(0, 38));
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                if (!sel) c.setBackground(row % 2 == 0 ? ThemeManager.getTableRowEven() : ThemeManager.getTableRowOdd());
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return c;
            }
        });

        gbc.gridy = 2; gbc.insets = new Insets(0, 0, 0, 0);
        content.add(new JScrollPane(table) {{
            setBorder(BorderFactory.createLineBorder(ThemeManager.getBorderColor(), 1));
            setPreferredSize(new Dimension(0, Math.min(accounts.size() * 36 + 42, 400)));
        }}, gbc);

        add(content, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void showNewAccountDialog() {
        String type = (String) JOptionPane.showInputDialog(this,
            "Select Account Type:", "New Account", JOptionPane.QUESTION_MESSAGE, null,
            new String[]{"Checking", "Savings", "Money Market"}, "Checking");
        if (type != null) {
            Account acc = accountService.createAccount(CurrentUser.getInstance().getUser().getId(), type);
            if (acc != null) {
                ToastNotification.showSuccess(this, "Account created! ID: " + acc.getAccountId());
                refresh();
            }
        }
    }

    private JPanel makeStatCard(String label, String value, Color accent) {
        JPanel card = new JPanel(new GridBagLayout());
        card.setOpaque(false);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(3, 0, 0, 0, accent),
            BorderFactory.createEmptyBorder(10, 14, 8, 14)));
        GridBagConstraints cg = new GridBagConstraints();
        cg.anchor = GridBagConstraints.WEST; cg.fill = GridBagConstraints.HORIZONTAL; cg.weightx = 1.0;
        cg.gridx = 0; cg.gridy = 0;
        JLabel val = new JLabel(value);
        val.setFont(ThemeManager.getSectionFont());
        val.setForeground(ThemeManager.getTextColor());
        card.add(val, cg);
        cg.gridy = 1;
        JLabel lbl = new JLabel(label);
        lbl.setFont(ThemeManager.getCardLabelFont());
        lbl.setForeground(ThemeManager.getTextColorMuted());
        card.add(lbl, cg);
        return card;
    }

    private String getUniqueTypes(List<Account> accounts) {
        java.util.Set<String> types = new java.util.LinkedHashSet<>();
        for (Account a : accounts) types.add(a.getAccountType());
        return String.join(", ", types);
    }
}
