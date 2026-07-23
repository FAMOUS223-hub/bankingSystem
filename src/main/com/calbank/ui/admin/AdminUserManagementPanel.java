package com.calbank.ui.admin;

import com.calbank.models.User;
import com.calbank.services.UserService;
import com.calbank.ui.theme.ThemeManager;
import com.calbank.ui.ToastNotification;
import com.calbank.utils.IconUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

public final class AdminUserManagementPanel extends JPanel implements com.calbank.ui.MainContentPanel.Refreshable {

    private final UserService userService = new UserService();
    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField searchField;
    private JLabel countLabel;

    public AdminUserManagementPanel() {
        setLayout(new BorderLayout());
        setBackground(ThemeManager.getBackgroundColor());

        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(ThemeManager.getBackgroundColor());
        content.setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 4, 0);
        JLabel title = new JLabel("User Management");
        title.setFont(ThemeManager.getTitleFont());
        title.setForeground(ThemeManager.getTextColor());
        content.add(title, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 20, 0);
        JLabel subtitle = new JLabel("Manage all registered users, roles, and account status");
        subtitle.setFont(ThemeManager.getSmallFont());
        subtitle.setForeground(ThemeManager.getTextColorMuted());
        content.add(subtitle, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 16, 0);
        content.add(createToolbar(), gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 8, 0);
        countLabel = new JLabel(" ");
        countLabel.setFont(ThemeManager.getSmallFont());
        countLabel.setForeground(ThemeManager.getTextColorMuted());
        content.add(countLabel, gbc);

        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 20, 0);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        content.add(createTablePanel(), gbc);

        gbc.gridy = 5;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.BOTH;
        content.add(Box.createVerticalGlue(), gbc);

        add(content, BorderLayout.CENTER);
        refreshTable();
    }

    private JPanel createToolbar() {
        JPanel toolbar = new JPanel(new BorderLayout(12, 0));
        toolbar.setOpaque(false);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        searchPanel.setOpaque(false);

        JLabel searchIcon = new JLabel(IconUtils.get("search"));
        searchIcon.setFont(IconUtils.getIconFont());
        searchIcon.setForeground(ThemeManager.getTextColorMuted());
        searchPanel.add(searchIcon);

        searchField = new JTextField(20);
        ThemeManager.styleInput(searchField);
        searchField.putClientProperty("JTextField.placeholderText", "Search users...");
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                refreshTable();
            }
        });
        searchPanel.add(searchField);

        toolbar.add(searchPanel, BorderLayout.WEST);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.setOpaque(false);

        JButton addBtn = new JButton(IconUtils.get("add") + " Add User");
        ThemeManager.styleSuccessButton(addBtn);
        addBtn.addActionListener(e -> showAddUserDialog());

        JButton refreshBtn = new JButton(IconUtils.get("refresh") + " Refresh");
        ThemeManager.styleButton(refreshBtn, ThemeManager.getPrimaryColor());
        refreshBtn.addActionListener(e -> refreshTable());

        btnPanel.add(addBtn);
        btnPanel.add(refreshBtn);
        toolbar.add(btnPanel, BorderLayout.EAST);

        return toolbar;
    }

    private JScrollPane createTablePanel() {
        String[] columns = {"ID", "Username", "Full Name", "Email", "Phone", "Role", "Status", "Actions"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        table.setFont(ThemeManager.getTableFont());
        table.setRowHeight(40);
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

        table.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                String status = val != null ? val.toString() : "";
                label.setHorizontalAlignment(CENTER);
                if ("Active".equals(status)) {
                    label.setForeground(ThemeManager.getSuccessColor());
                    label.setText("Active");
                } else {
                    label.setForeground(ThemeManager.getErrorColor());
                    label.setText("Inactive");
                }
                if (!sel) {
                    label.setBackground(row % 2 == 0 ? ThemeManager.getTableRowEven() : ThemeManager.getTableRowOdd());
                }
                return label;
            }
        });

        table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                String role = val != null ? val.toString() : "";
                label.setHorizontalAlignment(CENTER);
                if ("ADMIN".equals(role)) {
                    label.setForeground(new Color(156, 39, 176));
                    label.setText("Admin");
                } else {
                    label.setForeground(ThemeManager.getInfoColor());
                    label.setText("User");
                }
                if (!sel) {
                    label.setBackground(row % 2 == 0 ? ThemeManager.getTableRowEven() : ThemeManager.getTableRowOdd());
                }
                return label;
            }
        });

        table.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            {
                setHorizontalAlignment(CENTER);
            }
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                label.setText(IconUtils.get("settings") + " Actions");
                label.setFont(ThemeManager.getSmallFont());
                label.setForeground(ThemeManager.getPrimaryColor());
                if (!sel) {
                    label.setBackground(row % 2 == 0 ? ThemeManager.getTableRowEven() : ThemeManager.getTableRowOdd());
                }
                return label;
            }
        });

        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setPreferredWidth(140);
        table.getColumnModel().getColumn(3).setPreferredWidth(180);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);
        table.getColumnModel().getColumn(5).setPreferredWidth(70);
        table.getColumnModel().getColumn(6).setPreferredWidth(80);
        table.getColumnModel().getColumn(7).setPreferredWidth(100);

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                showActionsPopup(e);
            }
            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                showActionsPopup(e);
            }
        });

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(ThemeManager.getBorderColor(), 1));
        return sp;
    }

    private void showActionsPopup(java.awt.event.MouseEvent e) {
        int row = table.rowAtPoint(e.getPoint());
        int col = table.columnAtPoint(e.getPoint());
        if (row < 0 || col < 7) return;

        table.setRowSelectionInterval(row, row);
        User user = getUserAtRow(row);
        if (user == null) return;

        JPopupMenu popup = new JPopupMenu();
        popup.setBackground(ThemeManager.getCardColor());
        popup.setBorder(BorderFactory.createLineBorder(ThemeManager.getBorderColor(), 1));

        JMenuItem editItem = new JMenuItem(IconUtils.get("edit") + "  Edit User");
        editItem.setFont(ThemeManager.getLabelFont());
        editItem.setForeground(ThemeManager.getTextColor());
        editItem.setBackground(ThemeManager.getCardColor());
        editItem.addActionListener(ev -> showEditUserDialog(user));
        popup.add(editItem);

        JMenuItem toggleItem = new JMenuItem();
        toggleItem.setFont(ThemeManager.getLabelFont());
        toggleItem.setForeground(ThemeManager.getTextColor());
        toggleItem.setBackground(ThemeManager.getCardColor());
        if (user.isActive()) {
            toggleItem.setText(IconUtils.get("warning") + "  Disable User");
        } else {
            toggleItem.setText(IconUtils.get("check") + "  Enable User");
        }
        if (user.isAdmin()) {
            toggleItem.setEnabled(false);
        }
        toggleItem.addActionListener(ev -> toggleUserStatus(user));
        popup.add(toggleItem);

        popup.addSeparator();

        JMenuItem deleteItem = new JMenuItem(IconUtils.get("delete") + "  Delete User");
        deleteItem.setFont(ThemeManager.getLabelFont());
        deleteItem.setForeground(ThemeManager.getErrorColor());
        deleteItem.setBackground(ThemeManager.getCardColor());
        if (user.isAdmin()) {
            deleteItem.setEnabled(false);
        }
        deleteItem.addActionListener(ev -> deleteUser(user));
        popup.add(deleteItem);

        popup.show(table, e.getX(), e.getY());
    }

    private User getUserAtRow(int row) {
        if (row < 0 || row >= tableModel.getRowCount()) return null;
        Object val = tableModel.getValueAt(row, 7);
        return (val instanceof User) ? (User) val : null;
    }

    @Override
    public void refresh() {
        refreshTable();
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        String search = searchField != null ? searchField.getText().trim() : "";
        List<User> users;
        if (search.isEmpty()) {
            users = userService.getAllUsers();
        } else {
            users = userService.searchUsers(search);
        }

        for (User u : users) {
            tableModel.addRow(new Object[]{
                u.getId(), u.getUsername(), u.getFullName(), u.getEmail(),
                u.getPhone() != null ? u.getPhone() : "",
                u.getRole(),
                u.isActive() ? "Active" : "Inactive",
                u
            });
        }

        countLabel.setText("Showing " + users.size() + " user(s)");
    }

    private void showAddUserDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New User", true);
        dialog.setSize(420, 480);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(ThemeManager.getCardColor());
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.getBorderColor(), 1),
            BorderFactory.createEmptyBorder(24, 32, 24, 32)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 12, 0);

        gbc.gridy = 0;
        JLabel titleLabel = new JLabel("Create New User");
        titleLabel.setFont(ThemeManager.getHeadingFont());
        titleLabel.setForeground(ThemeManager.getTextColor());
        panel.add(titleLabel, gbc);

        gbc.gridy = 1;
        panel.add(createSeparator(), gbc);

        JTextField nameField = addFormRow(panel, gbc, 2, "Full Name:");
        JTextField usernameField = addFormRow(panel, gbc, 4, "Username:");
        JTextField emailField = addFormRow(panel, gbc, 6, "Email:");
        JTextField phoneField = addFormRow(panel, gbc, 8, "Phone:");
        JPasswordField pwdField = addPasswordFieldRow(panel, gbc, 10, "Password:");

        gbc.gridy = 12; gbc.insets = new Insets(4, 0, 0, 0);
        JLabel errorLabel = new JLabel(" ");
        errorLabel.setForeground(ThemeManager.getErrorColor());
        errorLabel.setFont(ThemeManager.getSmallFont());
        panel.add(errorLabel, gbc);

        gbc.gridy = 13; gbc.insets = new Insets(16, 0, 0, 0);
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnRow.setOpaque(false);

        JButton cancelBtn = new JButton("Cancel");
        ThemeManager.styleGhostButton(cancelBtn);
        cancelBtn.addActionListener(e -> dialog.dispose());

        JButton createBtn = new JButton("Create User");
        ThemeManager.styleSuccessButton(createBtn);
        createBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String username = usernameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String password = new String(pwdField.getPassword());

            if (name.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                errorLabel.setText("Fill all required fields");
                return;
            }
            if (password.length() < 6) {
                errorLabel.setText("Password must be at least 6 characters");
                return;
            }

            try {
                User user = new User();
                user.setFullName(name);
                user.setUsername(username);
                user.setEmail(email);
                user.setPhone(phone);
                userService.registerUser(user, password);
                ToastNotification.showSuccess(AdminUserManagementPanel.this, "User '" + username + "' created successfully!");
                dialog.dispose();
                refreshTable();
            } catch (Exception ex) {
                errorLabel.setText(ex.getMessage());
            }
        });

        btnRow.add(cancelBtn);
        btnRow.add(createBtn);
        panel.add(btnRow, gbc);

        dialog.setContentPane(new JScrollPane(panel));
        dialog.setVisible(true);
    }

    private void showEditUserDialog(User user) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit User", true);
        dialog.setSize(420, 400);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(ThemeManager.getCardColor());
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.getBorderColor(), 1),
            BorderFactory.createEmptyBorder(24, 32, 24, 32)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 12, 0);

        gbc.gridy = 0;
        JLabel titleLabel = new JLabel("Edit User: " + user.getUsername());
        titleLabel.setFont(ThemeManager.getHeadingFont());
        titleLabel.setForeground(ThemeManager.getTextColor());
        panel.add(titleLabel, gbc);

        gbc.gridy = 1;
        panel.add(createSeparator(), gbc);

        JTextField nameField = addFormRow(panel, gbc, 2, "Full Name:");
        nameField.setText(user.getFullName());
        JTextField emailField = addFormRow(panel, gbc, 4, "Email:");
        emailField.setText(user.getEmail());
        JTextField phoneField = addFormRow(panel, gbc, 6, "Phone:");
        phoneField.setText(user.getPhone() != null ? user.getPhone() : "");

        gbc.gridy = 8; gbc.insets = new Insets(4, 0, 0, 0);
        JLabel errorLabel = new JLabel(" ");
        errorLabel.setForeground(ThemeManager.getErrorColor());
        errorLabel.setFont(ThemeManager.getSmallFont());
        panel.add(errorLabel, gbc);

        gbc.gridy = 9; gbc.insets = new Insets(16, 0, 0, 0);
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnRow.setOpaque(false);

        JButton cancelBtn = new JButton("Cancel");
        ThemeManager.styleGhostButton(cancelBtn);
        cancelBtn.addActionListener(e -> dialog.dispose());

        JButton saveBtn = new JButton("Save Changes");
        ThemeManager.stylePrimaryButton(saveBtn);
        saveBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();

            if (name.isEmpty() || email.isEmpty()) {
                errorLabel.setText("Name and email are required");
                return;
            }

            user.setFullName(name);
            user.setEmail(email);
            user.setPhone(phone);

            try {
                userService.updateUser(user);
                ToastNotification.showSuccess(AdminUserManagementPanel.this, "User updated successfully!");
                dialog.dispose();
                refreshTable();
            } catch (Exception ex) {
                errorLabel.setText(ex.getMessage());
            }
        });

        btnRow.add(cancelBtn);
        btnRow.add(saveBtn);
        panel.add(btnRow, gbc);

        dialog.setContentPane(new JScrollPane(panel));
        dialog.setVisible(true);
    }

    private JTextField addFormRow(JPanel panel, GridBagConstraints gbc, int row, String label) {
        gbc.gridy = row; gbc.insets = new Insets(0, 0, 4, 0);
        JLabel lbl = new JLabel(label);
        lbl.setFont(ThemeManager.getLabelFont().deriveFont(Font.BOLD));
        lbl.setForeground(ThemeManager.getTextColor());
        panel.add(lbl, gbc);

        gbc.gridy = row + 1; gbc.insets = new Insets(0, 0, 10, 0);
        JTextField field = new JTextField(22);
        ThemeManager.styleInput(field);
        panel.add(field, gbc);
        return field;
    }

    private JPasswordField addPasswordFieldRow(JPanel panel, GridBagConstraints gbc, int row, String label) {
        gbc.gridy = row; gbc.insets = new Insets(0, 0, 4, 0);
        JLabel lbl = new JLabel(label);
        lbl.setFont(ThemeManager.getLabelFont().deriveFont(Font.BOLD));
        lbl.setForeground(ThemeManager.getTextColor());
        panel.add(lbl, gbc);

        gbc.gridy = row + 1; gbc.insets = new Insets(0, 0, 10, 0);
        JPasswordField field = new JPasswordField(22);
        ThemeManager.styleInput(field);
        panel.add(field, gbc);
        return field;
    }

    private JSeparator createSeparator() {
        JSeparator sep = new JSeparator();
        sep.setForeground(ThemeManager.getBorderColor());
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        return sep;
    }

    private void toggleUserStatus(User user) {
        if (user.isAdmin()) {
            ToastNotification.showWarning(this, "Cannot deactivate admin accounts");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to " + (user.isActive() ? "deactivate" : "activate") + " user '" + user.getUsername() + "'?",
            "Confirm Status Change",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            userService.toggleUserActive(user.getId());
            refreshTable();
            ToastNotification.showSuccess(this, "User status updated");
        }
    }

    private void deleteUser(User user) {
        if (user.isAdmin()) {
            ToastNotification.showWarning(this, "Cannot delete admin accounts");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to permanently delete user '" + user.getUsername() + "'?\nThis action cannot be undone.",
            "Confirm Deletion",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            userService.deleteUser(user.getId());
            refreshTable();
            ToastNotification.showSuccess(this, "User deleted successfully");
        }
    }
}
