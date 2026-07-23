package com.calbank.ui;

import com.calbank.models.Account;
import com.calbank.models.Category;
import com.calbank.models.Transaction;
import com.calbank.services.AccountService;
import com.calbank.services.CategoryService;
import com.calbank.services.TransactionService;
import com.calbank.ui.theme.ThemeManager;
import com.calbank.utils.DateUtils;
import com.calbank.utils.ExportUtils;
import com.calbank.utils.TransactionUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;

public final class MiniStatementPanel extends JPanel implements MainContentPanel.Refreshable {

    private final AccountService accountService = new AccountService();
    private final TransactionService transactionService = new TransactionService();
    private final CategoryService categoryService = new CategoryService();
    private JComboBox<String> accountSelector, typeFilter, categoryFilter;
    private JTextField searchField;
    private DefaultTableModel tableModel;
    private List<Transaction> currentTransactions;
    private JLabel countLabel;

    public MiniStatementPanel() {
        setLayout(new BorderLayout());
        setBackground(ThemeManager.getBackgroundColor());

        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(ThemeManager.getBackgroundColor());
        content.setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;

        // Title
        gbc.gridy = 0; gbc.insets = new Insets(0, 0, 24, 0);
        content.add(new JLabel("Transaction History") {{
            setFont(ThemeManager.getTitleFont());
            setForeground(ThemeManager.getTextColor());
        }}, gbc);

        // Filters card
        gbc.gridy = 1; gbc.insets = new Insets(0, 0, 24, 0);
        JPanel filtersCard = new JPanel(new GridBagLayout());
        filtersCard.setBackground(ThemeManager.getCardColor());
        filtersCard.setBorder(ThemeManager.createCardBorder());

        GridBagConstraints fg = new GridBagConstraints();
        fg.gridx = 0; fg.fill = GridBagConstraints.HORIZONTAL; fg.weightx = 1.0;

        List<Account> accounts = accountService.getAccountsByUserId(
            CurrentUser.getInstance().getUser().getId());

        // Row 1: Account + Type
        fg.gridy = 0; fg.insets = new Insets(0, 0, 8, 0);
        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        row1.setOpaque(false);
        row1.add(makeFilterLabel("Account:"));
        accountSelector = new JComboBox<>();
        accountSelector.addItem("All Accounts");
        for (Account a : accounts) accountSelector.addItem(a.getAccountId() + " (" + a.getAccountType() + ")");
        accountSelector.setFont(ThemeManager.getInputFont());
        row1.add(accountSelector);
        row1.add(makeFilterLabel("Type:"));
        typeFilter = new JComboBox<>(new String[]{"All", "DEPOSIT", "WITHDRAW", "TRANSFER"});
        typeFilter.setFont(ThemeManager.getInputFont());
        row1.add(typeFilter);
        filtersCard.add(row1, fg);

        // Row 2: Category + Search
        fg.gridy = 1; fg.insets = new Insets(0, 0, 8, 0);
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        row2.setOpaque(false);
        row2.add(makeFilterLabel("Category:"));
        categoryFilter = new JComboBox<>();
        categoryFilter.addItem("All Categories");
        List<Category> categories = categoryService.getCategoriesByUserId(
            CurrentUser.getInstance().getUser().getId());
        for (Category c : categories) categoryFilter.addItem(c.getCategoryId() + " - " + c.toString());
        categoryFilter.setFont(ThemeManager.getInputFont());
        row2.add(categoryFilter);
        row2.add(makeFilterLabel("Search:"));
        searchField = new JTextField(14);
        ThemeManager.styleInput(searchField);
        row2.add(searchField);
        filtersCard.add(row2, fg);

        // Row 3: Buttons
        fg.gridy = 2;
        JPanel row3 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        row3.setOpaque(false);
        JButton searchBtn = new JButton("Apply Filters");
        ThemeManager.stylePrimaryButton(searchBtn);
        searchBtn.addActionListener(e -> applyFilters());
        JButton resetBtn = new JButton("Reset");
        ThemeManager.styleGhostButton(resetBtn);
        resetBtn.addActionListener(e -> resetFilters());
        JButton exportBtn = new JButton("Export CSV");
        ThemeManager.styleSuccessButton(exportBtn);
        exportBtn.addActionListener(e -> {
            if (currentTransactions != null) ExportUtils.exportTransactionsToCSV(currentTransactions, this);
        });
        row3.add(searchBtn);
        row3.add(resetBtn);
        row3.add(exportBtn);
        filtersCard.add(row3, fg);

        content.add(filtersCard, gbc);

        // Table
        tableModel = new DefaultTableModel(
            new String[]{"Receipt #", "Date", "Type", "Amount", "Balance", "Description", "Category"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(tableModel);
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
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                if (!sel) c.setBackground(row % 2 == 0 ? ThemeManager.getTableRowEven() : ThemeManager.getTableRowOdd());
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return c;
            }
        });

        gbc.gridy = 2; gbc.insets = new Insets(0, 0, 6, 0);
        content.add(new JScrollPane(table) {{
            setBorder(BorderFactory.createLineBorder(ThemeManager.getBorderColor(), 1));
            setPreferredSize(new Dimension(0, 360));
        }}, gbc);

        // Count
        countLabel = new JLabel("Showing 0 transaction(s)");
        countLabel.setFont(ThemeManager.getSmallFont());
        countLabel.setForeground(ThemeManager.getTextColorMuted());
        gbc.gridy = 3; gbc.insets = new Insets(0, 0, 0, 0);
        content.add(countLabel, gbc);

        add(content, BorderLayout.CENTER);
        SwingUtilities.invokeLater(this::loadAllTransactions);
    }

    @Override
    public void refresh() {
        loadAllTransactions();
    }

    private void loadAllTransactions() {
        currentTransactions = transactionService.getFilteredTransactions(
            getSelectedAccountId(), null, null, null, null, null);
        populateTable(currentTransactions);
    }

    private void applyFilters() {
        String type = (String) typeFilter.getSelectedItem();
        if ("All".equals(type)) type = null;
        String catStr = (String) categoryFilter.getSelectedItem();
        Integer catId = null;
        if (catStr != null && !catStr.startsWith("All")) {
            try { catId = Integer.parseInt(catStr.split(" - ")[0]); } catch (Exception ignored) {}
        }
        String search = searchField.getText().trim();
        if (search.isEmpty()) search = null;
        currentTransactions = transactionService.getFilteredTransactions(
            getSelectedAccountId(), type, search, catId, null, null);
        populateTable(currentTransactions);
    }

    private void resetFilters() {
        accountSelector.setSelectedIndex(0);
        typeFilter.setSelectedIndex(0);
        categoryFilter.setSelectedIndex(0);
        searchField.setText("");
        loadAllTransactions();
    }

    private void populateTable(List<Transaction> transactions) {
        tableModel.setRowCount(0);
        List<Category> categories = categoryService.getCategoriesByUserId(
            CurrentUser.getInstance().getUser().getId());
        for (Transaction t : transactions) {
            String catName = "Uncategorized";
            if (t.getCategoryId() != null) {
                for (Category c : categories) {
                    if (c.getCategoryId() == t.getCategoryId()) { catName = c.toString(); break; }
                }
            }
            tableModel.addRow(new Object[]{
                t.getReceiptNumber(), DateUtils.formatShort(t.getCreatedAt()),
                t.getTransactionType(),
                (t.isDeposit() ? "+" : "-") + TransactionUtils.formatCurrency(t.getAmount()),
                TransactionUtils.formatCurrency(t.getBalanceAfter()),
                t.getDescription(), catName
            });
        }
        countLabel.setText("Showing " + transactions.size() + " transaction(s)");
    }

    private String getSelectedAccountId() {
        String selected = (String) accountSelector.getSelectedItem();
        if (selected == null || "All Accounts".equals(selected)) {
            List<Account> accounts = accountService.getAccountsByUserId(
                CurrentUser.getInstance().getUser().getId());
            return accounts.isEmpty() ? "" : accounts.get(0).getAccountId();
        }
        return selected.split(" ")[0];
    }

    private JLabel makeFilterLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(ThemeManager.getLabelFont());
        l.setForeground(ThemeManager.getTextColorMuted());
        return l;
    }
}
