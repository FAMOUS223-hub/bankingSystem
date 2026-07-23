package com.calbank.ui;

import com.calbank.models.Category;
import com.calbank.services.CategoryService;
import com.calbank.ui.theme.ThemeManager;
import com.calbank.utils.IconUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

public final class CategoryPanel extends JPanel {

    private final CategoryService categoryService = new CategoryService();
    private DefaultTableModel tableModel;
    private JTable table;

    public CategoryPanel() {
        setLayout(new BorderLayout());
        setBackground(ThemeManager.getBackgroundColor());

        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(ThemeManager.getBackgroundColor());
        content.setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;

        // Title row
        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setOpaque(false);
        JLabel title = new JLabel("Spending Categories");
        title.setFont(ThemeManager.getTitleFont());
        title.setForeground(ThemeManager.getTextColor());
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.setOpaque(false);
        JButton addBtn = new JButton("Add Category");
        ThemeManager.styleSuccessButton(addBtn);
        addBtn.addActionListener(e -> showAddDialog());
        JButton editBtn = new JButton("Edit");
        ThemeManager.stylePrimaryButton(editBtn);
        editBtn.addActionListener(e -> showEditDialog());
        JButton deleteBtn = new JButton("Delete");
        ThemeManager.styleDangerButton(deleteBtn);
        deleteBtn.addActionListener(e -> deleteSelected());
        btnPanel.add(addBtn);
        btnPanel.add(editBtn);
        btnPanel.add(deleteBtn);
        titleRow.add(title, BorderLayout.WEST);
        titleRow.add(btnPanel, BorderLayout.EAST);

        gbc.gridy = 0; gbc.insets = new Insets(0, 0, 16, 0);
        content.add(titleRow, gbc);

        // Table
        tableModel = new DefaultTableModel(
            new String[]{"Icon", "Name", "Color", "Type"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setFont(ThemeManager.getTableFont());
        table.setRowHeight(36);
        table.setShowGrid(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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

        gbc.gridy = 1;
        content.add(new JScrollPane(table) {{
            setBorder(BorderFactory.createLineBorder(ThemeManager.getBorderColor(), 1));
            setPreferredSize(new Dimension(0, 440));
        }}, gbc);

        add(content, BorderLayout.CENTER);
        refreshTable();
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        List<Category> categories = categoryService.getCategoriesByUserId(
            CurrentUser.getInstance().getUser().getId());
        for (Category c : categories)
            tableModel.addRow(new Object[]{c.getIcon(), c.getName(), c.getColor(), c.isDefault() ? "Default" : "Custom"});
    }

    private void showAddDialog() {
        String name = JOptionPane.showInputDialog(this, "Category Name:", "Add Category", JOptionPane.PLAIN_MESSAGE);
        if (name == null || name.trim().isEmpty()) return;
        String[] iconKeys = {"food", "transport", "bills", "shopping",
            "entertainment", "health", "education", "money", "chart", "info"};
        String[] iconLabels = {"Food", "Transport", "Bills", "Shopping",
            "Entertainment", "Health", "Education", "Money", "Chart", "Info"};
        String icon = (String) JOptionPane.showInputDialog(this, "Choose Icon:", "Category Icon",
            JOptionPane.PLAIN_MESSAGE, null, iconLabels, iconLabels[0]);
        if (icon == null) icon = iconLabels[0];
        String iconKey = "info";
        for (int i = 0; i < iconLabels.length; i++) {
            if (iconLabels[i].equals(icon)) { iconKey = iconKeys[i]; break; }
        }
        categoryService.createCategory(CurrentUser.getInstance().getUser().getId(), name.trim(), IconUtils.get(iconKey), "#4CAF50");
        refreshTable();
        ToastNotification.showSuccess(this, "Category created!");
    }

    private void showEditDialog() {
        int row = table.getSelectedRow();
        if (row < 0) { ToastNotification.showWarning(this, "Select a category to edit"); return; }
        List<Category> categories = categoryService.getCategoriesByUserId(
            CurrentUser.getInstance().getUser().getId());
        if (row >= categories.size()) return;
        Category cat = categories.get(row);
        String name = JOptionPane.showInputDialog(this, "Category Name:", cat.getName());
        if (name == null || name.trim().isEmpty()) return;
        categoryService.updateCategory(cat.getCategoryId(), name.trim(), cat.getIcon(), cat.getColor());
        refreshTable();
        ToastNotification.showSuccess(this, "Category updated!");
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { ToastNotification.showWarning(this, "Select a category to delete"); return; }
        List<Category> categories = categoryService.getCategoriesByUserId(
            CurrentUser.getInstance().getUser().getId());
        if (row >= categories.size()) return;
        Category cat = categories.get(row);
        if (cat.isDefault()) { ToastNotification.showWarning(this, "Cannot delete default categories"); return; }
        if (JOptionPane.showConfirmDialog(this, "Delete \"" + cat.getName() + "\"?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            categoryService.deleteCategory(cat.getCategoryId());
            refreshTable();
            ToastNotification.showSuccess(this, "Category deleted!");
        }
    }
}
