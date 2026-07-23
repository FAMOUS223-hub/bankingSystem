package com.calbank.ui.admin;

import com.calbank.services.AccountService;
import com.calbank.services.TransactionService;
import com.calbank.services.UserService;
import com.calbank.ui.CurrentUser;
import com.calbank.ui.ToastNotification;
import com.calbank.ui.theme.ThemeManager;
import com.calbank.utils.IconUtils;
import com.calbank.utils.TransactionUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public final class AdminSettingsPanel extends JPanel {

    private final UserService userService = new UserService();
    private final AccountService accountService = new AccountService();
    private final TransactionService transactionService = new TransactionService();
    private final Runnable onThemeChanged;

    public AdminSettingsPanel(Runnable onThemeChanged) {
        this.onThemeChanged = onThemeChanged;
        setLayout(new BorderLayout());
        setBackground(ThemeManager.getBackgroundColor());

        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(ThemeManager.getBackgroundColor());
        content.setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;

        gbc.gridy = 0; gbc.insets = new Insets(0, 0, 4, 0);
        JLabel title = new JLabel("System Settings");
        title.setFont(ThemeManager.getTitleFont());
        title.setForeground(ThemeManager.getTextColor());
        content.add(title, gbc);

        gbc.gridy = 1; gbc.insets = new Insets(0, 0, 24, 0);
        JLabel subtitle = new JLabel("Configure system-wide settings and manage application state");
        subtitle.setFont(ThemeManager.getSmallFont());
        subtitle.setForeground(ThemeManager.getTextColorMuted());
        content.add(subtitle, gbc);

        gbc.gridy = 2; gbc.insets = new Insets(0, 0, 20, 0);
        content.add(createInfoCard(), gbc);

        gbc.gridy = 3; gbc.insets = new Insets(0, 0, 20, 0);
        content.add(createAppearanceCard(), gbc);

        gbc.gridy = 4; gbc.insets = new Insets(0, 0, 20, 0);
        content.add(createSecurityCard(), gbc);

        gbc.gridy = 5; gbc.insets = new Insets(0, 0, 20, 0);
        content.add(createDatabaseCard(), gbc);

        gbc.gridy = 6; gbc.insets = new Insets(0, 0, 20, 0);
        content.add(createAboutCard(), gbc);

        gbc.gridy = 7; gbc.weighty = 1.0; gbc.fill = GridBagConstraints.BOTH;
        content.add(Box.createVerticalGlue(), gbc);

        add(content, BorderLayout.CENTER);
    }

    private JPanel createInfoCard() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(ThemeManager.getCardColor());
        card.setBorder(ThemeManager.createCardBorder());

        GridBagConstraints cg = new GridBagConstraints();
        cg.gridx = 0; cg.fill = GridBagConstraints.HORIZONTAL; cg.weightx = 1.0;

        cg.gridy = 0; cg.insets = new Insets(0, 0, 4, 0);
        card.add(makeSectionLabel("System Information"), cg);

        cg.gridy = 1; cg.insets = new Insets(0, 0, 12, 0);
        card.add(makeSeparator(), cg);

        addInfoRow(card, cg, 2, "Total Users:", String.valueOf(userService.getTotalUserCount()));
        addInfoRow(card, cg, 3, "Active Users:", String.valueOf(userService.getActiveUserCount()));
        addInfoRow(card, cg, 4, "Total Accounts:", String.valueOf(accountService.getTotalAccountCount()));
        addInfoRow(card, cg, 5, "Total Balance:", TransactionUtils.formatCurrency(accountService.getAllAccountsTotalBalance()));
        addInfoRow(card, cg, 6, "Total Transactions:", String.valueOf(transactionService.getTotalTransactionCount()));
        addInfoRow(card, cg, 7, "Total Deposits:", TransactionUtils.formatCurrency(transactionService.getTotalDeposits()));
        addInfoRow(card, cg, 8, "Total Withdrawals:", TransactionUtils.formatCurrency(transactionService.getTotalWithdrawals()));

        return card;
    }

    private JPanel createAppearanceCard() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(ThemeManager.getCardColor());
        card.setBorder(ThemeManager.createCardBorder());

        GridBagConstraints cg = new GridBagConstraints();
        cg.gridx = 0; cg.fill = GridBagConstraints.HORIZONTAL; cg.weightx = 1.0;

        cg.gridy = 0; cg.insets = new Insets(0, 0, 4, 0);
        card.add(makeSectionLabel("Appearance"), cg);

        cg.gridy = 1; cg.insets = new Insets(0, 0, 12, 0);
        card.add(makeSeparator(), cg);

        cg.gridy = 2; cg.insets = new Insets(0, 0, 8, 0);
        JPanel themeRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
        themeRow.setOpaque(false);
        themeRow.add(new JLabel("Theme:") {{
            setFont(ThemeManager.getLabelFont().deriveFont(Font.BOLD));
            setForeground(ThemeManager.getTextColor());
        }});
        JToggleButton themeToggle = new JToggleButton(
            ThemeManager.isLight() ? "Light Mode" : "Dark Mode", !ThemeManager.isLight());
        themeToggle.setFont(ThemeManager.getInputFont());
        themeToggle.setForeground(ThemeManager.getTextColor());
        themeToggle.setBackground(ThemeManager.getInputBackground());
        themeToggle.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.getBorderColor(), 1),
            BorderFactory.createEmptyBorder(6, 16, 6, 16)));
        themeToggle.addActionListener(e -> {
            ThemeManager.setTheme(themeToggle.isSelected() ? ThemeManager.Theme.DARK : ThemeManager.Theme.LIGHT);
            themeToggle.setText(themeToggle.isSelected() ? "Dark Mode" : "Light Mode");
            ThemeManager.applyGlobalTheme();
            onThemeChanged.run();
        });
        themeRow.add(themeToggle);
        card.add(themeRow, cg);

        return card;
    }

    private JPanel createSecurityCard() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(ThemeManager.getCardColor());
        card.setBorder(ThemeManager.createCardBorder());

        GridBagConstraints cg = new GridBagConstraints();
        cg.gridx = 0; cg.fill = GridBagConstraints.HORIZONTAL; cg.weightx = 1.0;

        cg.gridy = 0; cg.insets = new Insets(0, 0, 4, 0);
        card.add(makeSectionLabel("Admin Security"), cg);

        cg.gridy = 1; cg.insets = new Insets(0, 0, 12, 0);
        card.add(makeSeparator(), cg);

        JPasswordField oldPwd = addPasswordField(card, cg, 2, "Current Password:");
        JPasswordField newPwd = addPasswordField(card, cg, 4, "New Password:");
        JPasswordField confirmPwd = addPasswordField(card, cg, 6, "Confirm Password:");

        cg.gridy = 8; cg.insets = new Insets(0, 0, 8, 0);
        JLabel errorLabel = new JLabel(" ");
        errorLabel.setForeground(ThemeManager.getErrorColor());
        errorLabel.setFont(ThemeManager.getSmallFont());
        card.add(errorLabel, cg);

        cg.gridy = 9; cg.insets = new Insets(0, 0, 0, 0);
        JPanel secBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        secBtns.setOpaque(false);
        JButton changePwd = new JButton("Change Password");
        ThemeManager.stylePrimaryButton(changePwd);
        changePwd.addActionListener(e -> {
            String o = new String(oldPwd.getPassword());
            String n = new String(newPwd.getPassword());
            String c = new String(confirmPwd.getPassword());
            if (o.isEmpty() || n.isEmpty()) { errorLabel.setText("Fill all fields"); return; }
            if (!n.equals(c)) { errorLabel.setText("Passwords don't match"); return; }
            if (n.length() < 6) { errorLabel.setText("Min 6 characters"); return; }
            try {
                if (userService.changePassword(CurrentUser.getInstance().getUser().getId(), o, n)) {
                    errorLabel.setText(" ");
                    ToastNotification.showSuccess(AdminSettingsPanel.this, "Password changed successfully!");
                    oldPwd.setText(""); newPwd.setText(""); confirmPwd.setText("");
                } else {
                    errorLabel.setText("Current password incorrect");
                }
            } catch (Exception ex) { errorLabel.setText(ex.getMessage()); }
        });

        JButton clearPwd = new JButton("Clear");
        ThemeManager.styleGhostButton(clearPwd);
        clearPwd.addActionListener(e -> { oldPwd.setText(""); newPwd.setText(""); confirmPwd.setText(""); errorLabel.setText(" "); });
        secBtns.add(changePwd);
        secBtns.add(clearPwd);
        card.add(secBtns, cg);

        return card;
    }

    private JPanel createDatabaseCard() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(ThemeManager.getCardColor());
        card.setBorder(ThemeManager.createCardBorder());

        GridBagConstraints cg = new GridBagConstraints();
        cg.gridx = 0; cg.fill = GridBagConstraints.HORIZONTAL; cg.weightx = 1.0;

        cg.gridy = 0; cg.insets = new Insets(0, 0, 4, 0);
        card.add(makeSectionLabel("Database Management"), cg);

        cg.gridy = 1; cg.insets = new Insets(0, 0, 12, 0);
        card.add(makeSeparator(), cg);

        cg.gridy = 2; cg.insets = new Insets(0, 0, 0, 0);
        JPanel dbBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        dbBtns.setOpaque(false);

        JButton backupBtn = new JButton(IconUtils.get("download") + " Export Data");
        ThemeManager.styleButton(backupBtn, ThemeManager.getAccentColor());
        backupBtn.addActionListener(e -> {
            try {
                JFileChooser chooser = new JFileChooser();
                chooser.setSelectedFile(new File("calbank_export_" + System.currentTimeMillis() + ".csv"));
                chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV Files", "csv"));
                if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                    java.util.List<com.calbank.models.Transaction> allTx = transactionService.getAllTransactions(10000);
                    StringBuilder sb = new StringBuilder();
                    sb.append("ID,Account,Type,Amount,Balance After,Description,Receipt,Date\n");
                    for (com.calbank.models.Transaction t : allTx) {
                        sb.append(t.getTransactionId()).append(",")
                          .append(t.getAccountId()).append(",")
                          .append(t.getTransactionType()).append(",")
                          .append(t.getAmount()).append(",")
                          .append(t.getBalanceAfter()).append(",")
                          .append("\"").append(t.getDescription() != null ? t.getDescription().replace("\"", "\"\"") : "").append("\",")
                          .append(t.getReceiptNumber()).append(",")
                          .append(t.getCreatedAt() != null ? t.getCreatedAt().toString() : "").append("\n");
                    }
                    java.nio.file.Files.writeString(chooser.getSelectedFile().toPath(), sb.toString());
                    ToastNotification.showSuccess(this, "Exported " + allTx.size() + " transactions!");
                }
            } catch (Exception ex) {
                ToastNotification.showError(this, "Export failed: " + ex.getMessage());
            }
        });

        dbBtns.add(backupBtn);
        card.add(dbBtns, cg);

        return card;
    }

    private JPanel createAboutCard() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(ThemeManager.getCardColor());
        card.setBorder(ThemeManager.createCardBorder());

        GridBagConstraints cg = new GridBagConstraints();
        cg.gridx = 0; cg.fill = GridBagConstraints.HORIZONTAL; cg.weightx = 1.0;

        cg.gridy = 0; cg.insets = new Insets(0, 0, 4, 0);
        card.add(makeSectionLabel("About CalBank"), cg);

        cg.gridy = 1; cg.insets = new Insets(0, 0, 12, 0);
        card.add(makeSeparator(), cg);

        cg.gridy = 2;
        JLabel aboutText = new JLabel("<html><b>CalBank</b> v2.0 - Admin Panel<br>Smart Banking for Everyone<br>Built with Java Swing + MySQL<br>Admin Account: admin / admin123</html>");
        aboutText.setFont(ThemeManager.getLabelFont());
        aboutText.setForeground(ThemeManager.getTextColor());
        card.add(aboutText, cg);

        return card;
    }

    private JLabel makeSectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(ThemeManager.getSectionFont());
        label.setForeground(ThemeManager.getTextColor());
        return label;
    }

    private JSeparator makeSeparator() {
        JSeparator sep = new JSeparator();
        sep.setForeground(ThemeManager.getBorderColor());
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        return sep;
    }

    private void addInfoRow(JPanel card, GridBagConstraints cg, int row, String label, String value) {
        cg.gridy = row; cg.insets = new Insets(0, 0, 6, 0);
        JPanel rowPanel = new JPanel(new BorderLayout(8, 0));
        rowPanel.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(ThemeManager.getLabelFont().deriveFont(Font.BOLD));
        lbl.setForeground(ThemeManager.getTextColorMuted());
        rowPanel.add(lbl, BorderLayout.WEST);
        JLabel val = new JLabel(value);
        val.setFont(ThemeManager.getLabelFont().deriveFont(Font.BOLD));
        val.setForeground(ThemeManager.getTextColor());
        rowPanel.add(val, BorderLayout.EAST);
        card.add(rowPanel, cg);
    }

    private JPasswordField addPasswordField(JPanel card, GridBagConstraints cg, int row, String label) {
        cg.gridy = row; cg.insets = new Insets(0, 0, 4, 0);
        card.add(new JLabel(label) {{
            setFont(ThemeManager.getLabelFont().deriveFont(Font.BOLD));
            setForeground(ThemeManager.getTextColor());
        }}, cg);
        cg.gridy = row + 1; cg.insets = new Insets(0, 0, 10, 0);
        JPasswordField field = new JPasswordField(20);
        ThemeManager.styleInput(field);
        card.add(field, cg);
        return field;
    }
}
