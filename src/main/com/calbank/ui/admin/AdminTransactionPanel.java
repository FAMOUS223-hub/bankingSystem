package com.calbank.ui.admin;

import com.calbank.models.Transaction;
import com.calbank.services.TransactionService;
import com.calbank.ui.theme.ThemeManager;
import com.calbank.utils.DateUtils;
import com.calbank.utils.IconUtils;
import com.calbank.utils.TransactionUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;
import com.calbank.ui.MainContentPanel;

public final class AdminTransactionPanel extends JPanel implements MainContentPanel.Refreshable {

    private final TransactionService transactionService = new TransactionService();
    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField searchField;
    private JComboBox<String> typeFilter;
    private JLabel countLabel;

    public AdminTransactionPanel() {
        refresh();
    }

    @Override
    public void refresh() {
        removeAll();
        setLayout(new BorderLayout());
        setBackground(ThemeManager.getBackgroundColor());

        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(ThemeManager.getBackgroundColor());
        content.setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Title
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 4, 0);
        JLabel title = new JLabel("All Transactions");
        title.setFont(ThemeManager.getTitleFont());
        title.setForeground(ThemeManager.getTextColor());
        content.add(title, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 20, 0);
        JLabel subtitle = new JLabel("View and search all transactions across the entire system");
        subtitle.setFont(ThemeManager.getSmallFont());
        subtitle.setForeground(ThemeManager.getTextColorMuted());
        content.add(subtitle, gbc);

        // Summary cards
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 20, 0);
        content.add(createSummaryCards(), gbc);

        // Toolbar
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 16, 0);
        content.add(createToolbar(), gbc);

        // Count label
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 8, 0);
        countLabel = new JLabel(" ");
        countLabel.setFont(ThemeManager.getSmallFont());
        countLabel.setForeground(ThemeManager.getTextColorMuted());
        content.add(countLabel, gbc);

        // Table
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 20, 0);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        content.add(createTablePanel(), gbc);

        // Spacer
        gbc.gridy = 6;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.BOTH;
        content.add(Box.createVerticalGlue(), gbc);

        add(content, BorderLayout.CENTER);
        refreshTable();
        revalidate();
        repaint();
    }

    private JPanel createSummaryCards() {
        JPanel row = new JPanel(new GridLayout(1, 4, 16, 0));
        row.setOpaque(false);

        double deposits = transactionService.getTotalDeposits();
        double withdrawals = transactionService.getTotalWithdrawals();
        double transfers = transactionService.getTotalTransfers();
        int totalTx = transactionService.getTotalTransactionCount();

        row.add(createMiniCard(IconUtils.get("deposit"), "Total Deposits",
            TransactionUtils.formatCurrency(deposits), ThemeManager.getAccentColor()));
        row.add(createMiniCard(IconUtils.get("withdraw"), "Total Withdrawals",
            TransactionUtils.formatCurrency(withdrawals), ThemeManager.getErrorColor()));
        row.add(createMiniCard(IconUtils.get("transfer"), "Total Transfers",
            TransactionUtils.formatCurrency(transfers), new Color(255, 152, 0)));
        row.add(createMiniCard(IconUtils.get("transactions"), "Total Count",
            String.valueOf(totalTx), ThemeManager.getPrimaryColor()));

        return row;
    }

    private JPanel createMiniCard(String icon, String label, String value, Color accent) {
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
        gbc.insets = new Insets(10, 14, 10, 8);
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(IconUtils.getIconFont());
        iconLabel.setForeground(accent);
        card.add(iconLabel, gbc);

        gbc.gridx = 1; gbc.gridheight = 1;
        gbc.insets = new Insets(8, 0, 0, 12);
        JLabel valLabel = new JLabel(value);
        valLabel.setFont(ThemeManager.getHeadingFont());
        valLabel.setForeground(ThemeManager.getTextColor());
        card.add(valLabel, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 8, 12);
        JLabel nameLabel = new JLabel(label);
        nameLabel.setFont(ThemeManager.getCardLabelFont());
        nameLabel.setForeground(ThemeManager.getTextColorMuted());
        card.add(nameLabel, gbc);

        return card;
    }

    private JPanel createToolbar() {
        JPanel toolbar = new JPanel(new BorderLayout(12, 0));
        toolbar.setOpaque(false);

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        leftPanel.setOpaque(false);

        JLabel searchIcon = new JLabel(IconUtils.get("search"));
        searchIcon.setFont(IconUtils.getIconFont());
        searchIcon.setForeground(ThemeManager.getTextColorMuted());
        leftPanel.add(searchIcon);

        searchField = new JTextField(16);
        ThemeManager.styleInput(searchField);
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyReleased(java.awt.event.KeyEvent e) { refreshTable(); }
        });
        leftPanel.add(searchField);

        JLabel typeLabel = new JLabel("Type:");
        typeLabel.setFont(ThemeManager.getLabelFont().deriveFont(Font.BOLD));
        typeLabel.setForeground(ThemeManager.getTextColor());
        leftPanel.add(typeLabel);

        typeFilter = new JComboBox<>(new String[]{"ALL", "DEPOSIT", "WITHDRAW", "TRANSFER"});
        ThemeManager.styleComboBox(typeFilter);
        typeFilter.addActionListener(e -> refreshTable());
        leftPanel.add(typeFilter);

        toolbar.add(leftPanel, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightPanel.setOpaque(false);

        JButton refreshBtn = new JButton(IconUtils.get("refresh") + " Refresh");
        ThemeManager.stylePrimaryButton(refreshBtn);
        refreshBtn.addActionListener(e -> refreshTable());
        rightPanel.add(refreshBtn);

        toolbar.add(rightPanel, BorderLayout.EAST);
        return toolbar;
    }

    private JScrollPane createTablePanel() {
        String[] columns = {"ID", "Account", "Type", "Amount", "Balance After", "Description", "Receipt #", "Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        table.setFont(ThemeManager.getTableFont());
        table.setRowHeight(34);
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

                // Color-code type column
                if (col == 2 && val != null) {
                    String type = val.toString();
                    if ("DEPOSIT".equals(type)) setForeground(ThemeManager.getSuccessColor());
                    else if ("WITHDRAW".equals(type)) setForeground(ThemeManager.getErrorColor());
                    else if ("TRANSFER".equals(type)) setForeground(ThemeManager.getInfoColor());
                    else setForeground(ThemeManager.getTextColor());
                } else {
                    setForeground(ThemeManager.getTextColor());
                }
                return c;
            }
        });

        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(120);
        table.getColumnModel().getColumn(2).setPreferredWidth(80);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);
        table.getColumnModel().getColumn(5).setPreferredWidth(160);
        table.getColumnModel().getColumn(6).setPreferredWidth(160);
        table.getColumnModel().getColumn(7).setPreferredWidth(100);

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(ThemeManager.getBorderColor(), 1));
        return sp;
    }

    private void refreshTable() {
        tableModel.setRowCount(0);

        String search = searchField != null ? searchField.getText().trim() : "";
        String type = typeFilter != null ? (String) typeFilter.getSelectedItem() : "ALL";

        List<Transaction> transactions;
        if (!search.isEmpty()) {
            transactions = transactionService.getAllTransactions(500);
            String lowerSearch = search.toLowerCase();
            transactions.removeIf(t ->
                !t.getDescription().toLowerCase().contains(lowerSearch) &&
                !t.getAccountId().toLowerCase().contains(lowerSearch) &&
                !t.getReceiptNumber().toLowerCase().contains(lowerSearch));
        } else {
            transactions = transactionService.getAllTransactions(500);
        }

        if (type != null && !"ALL".equals(type)) {
            transactions.removeIf(t -> !type.equals(t.getTransactionType()));
        }

        for (Transaction t : transactions) {
            String amountStr = (t.isDeposit() ? "+" : "-") + TransactionUtils.formatCurrency(t.getAmount());
            tableModel.addRow(new Object[]{
                t.getTransactionId(),
                t.getAccountId(),
                t.getTransactionType(),
                amountStr,
                TransactionUtils.formatCurrency(t.getBalanceAfter()),
                t.getDescription(),
                t.getReceiptNumber(),
                DateUtils.formatShort(t.getCreatedAt())
            });
        }

        countLabel.setText("Showing " + transactions.size() + " transaction(s)");
    }
}
