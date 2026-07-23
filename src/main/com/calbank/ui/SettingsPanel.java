package com.calbank.ui;

import com.calbank.services.UserService;
import com.calbank.ui.theme.ThemeManager;
import com.calbank.utils.InputValidator;

import javax.swing.*;
import java.awt.*;

public final class SettingsPanel extends JPanel implements MainContentPanel.Refreshable {

    private final UserService userService = new UserService();
    private final Runnable onThemeChanged;

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

        int row = 0;

        gbc.gridy = row++; gbc.insets = new Insets(0, 0, 20, 0);
        JLabel title = new JLabel("Settings");
        title.setFont(ThemeManager.getTitleFont());
        title.setForeground(ThemeManager.getTextColor());
        content.add(title, gbc);

        gbc.gridy = row++; gbc.insets = new Insets(0, 0, 20, 0);
        content.add(buildAppearanceCard(), gbc);

        gbc.gridy = row++; gbc.insets = new Insets(0, 0, 20, 0);
        content.add(buildSecurityCard(), gbc);

        gbc.gridy = row++; gbc.insets = new Insets(0, 0, 0, 0);
        content.add(buildAboutCard(), gbc);

        gbc.gridy = row; gbc.weighty = 1.0; gbc.fill = GridBagConstraints.BOTH;
        content.add(Box.createVerticalGlue(), gbc);

        add(content, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    public SettingsPanel(Runnable onThemeChanged) {
        this.onThemeChanged = onThemeChanged;
        setLayout(new BorderLayout());
        setBackground(ThemeManager.getBackgroundColor());

        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(ThemeManager.getBackgroundColor());
        content.setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        int row = 0;

        gbc.gridy = row++; gbc.insets = new Insets(0, 0, 20, 0);
        JLabel title = new JLabel("Settings");
        title.setFont(ThemeManager.getTitleFont());
        title.setForeground(ThemeManager.getTextColor());
        content.add(title, gbc);

        // ── Appearance card ──
        gbc.gridy = row++; gbc.insets = new Insets(0, 0, 20, 0);
        content.add(buildAppearanceCard(), gbc);

        // ── Security card ──
        gbc.gridy = row++; gbc.insets = new Insets(0, 0, 20, 0);
        content.add(buildSecurityCard(), gbc);

        // ── About card ──
        gbc.gridy = row++; gbc.insets = new Insets(0, 0, 0, 0);
        content.add(buildAboutCard(), gbc);

        gbc.gridy = row; gbc.weighty = 1.0; gbc.fill = GridBagConstraints.BOTH;
        content.add(Box.createVerticalGlue(), gbc);

        add(content, BorderLayout.CENTER);
    }

    private JPanel buildAppearanceCard() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(ThemeManager.getCardColor());
        card.setBorder(ThemeManager.createCardBorder());

        GridBagConstraints cg = new GridBagConstraints();
        cg.gridx = 0; cg.anchor = GridBagConstraints.WEST;
        cg.fill = GridBagConstraints.HORIZONTAL; cg.weightx = 1.0;

        cg.gridy = 0; cg.insets = new Insets(0, 0, 12, 0);
        JLabel cardTitle = new JLabel("Appearance");
        cardTitle.setFont(ThemeManager.getSectionFont());
        cardTitle.setForeground(ThemeManager.getTextColor());
        card.add(cardTitle, cg);

        cg.gridy = 1; cg.insets = new Insets(0, 0, 8, 0);
        JSeparator sep = new JSeparator();
        sep.setForeground(ThemeManager.getBorderColor());
        card.add(sep, cg);

        cg.gridy = 2; cg.insets = new Insets(0, 0, 0, 0);
        JPanel themeRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        themeRow.setOpaque(false);

        JLabel themeLabel = new JLabel("Theme:");
        themeLabel.setFont(ThemeManager.getLabelFont().deriveFont(Font.BOLD));
        themeLabel.setForeground(ThemeManager.getTextColor());
        themeRow.add(themeLabel);

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

    private JPanel buildSecurityCard() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(ThemeManager.getCardColor());
        card.setBorder(ThemeManager.createCardBorder());

        GridBagConstraints cg = new GridBagConstraints();
        cg.gridx = 0; cg.anchor = GridBagConstraints.WEST;
        cg.fill = GridBagConstraints.HORIZONTAL; cg.weightx = 1.0;

        cg.gridy = 0; cg.insets = new Insets(0, 0, 12, 0);
        JLabel cardTitle = new JLabel("Security Settings");
        cardTitle.setFont(ThemeManager.getSectionFont());
        cardTitle.setForeground(ThemeManager.getTextColor());
        card.add(cardTitle, cg);

        cg.gridy = 1; cg.insets = new Insets(0, 0, 8, 0);
        JSeparator sep = new JSeparator();
        sep.setForeground(ThemeManager.getBorderColor());
        card.add(sep, cg);

        int row = 2;

        cg.gridy = row++; cg.insets = new Insets(0, 0, 6, 0);
        JLabel oldLabel = new JLabel("Current Password:");
        oldLabel.setFont(ThemeManager.getLabelFont().deriveFont(Font.BOLD));
        oldLabel.setForeground(ThemeManager.getTextColor());
        card.add(oldLabel, cg);

        cg.gridy = row++; cg.insets = new Insets(0, 0, 16, 0);
        JPasswordField oldPwd = new JPasswordField(20);
        ThemeManager.styleInput(oldPwd);
        card.add(oldPwd, cg);

        cg.gridy = row++; cg.insets = new Insets(0, 0, 6, 0);
        JLabel newLabel = new JLabel("New Password:");
        newLabel.setFont(ThemeManager.getLabelFont().deriveFont(Font.BOLD));
        newLabel.setForeground(ThemeManager.getTextColor());
        card.add(newLabel, cg);

        cg.gridy = row++; cg.insets = new Insets(0, 0, 16, 0);
        JPasswordField newPwd = new JPasswordField(20);
        ThemeManager.styleInput(newPwd);
        card.add(newPwd, cg);

        cg.gridy = row++; cg.insets = new Insets(0, 0, 6, 0);
        JLabel confirmLabel = new JLabel("Confirm Password:");
        confirmLabel.setFont(ThemeManager.getLabelFont().deriveFont(Font.BOLD));
        confirmLabel.setForeground(ThemeManager.getTextColor());
        card.add(confirmLabel, cg);

        cg.gridy = row++; cg.insets = new Insets(0, 0, 16, 0);
        JPasswordField confirmPwd = new JPasswordField(20);
        ThemeManager.styleInput(confirmPwd);
        card.add(confirmPwd, cg);

        cg.gridy = row++; cg.insets = new Insets(0, 0, 10, 0);
        JLabel errorLabel = new JLabel(" ");
        errorLabel.setForeground(ThemeManager.getErrorColor());
        errorLabel.setFont(ThemeManager.getSmallFont());
        card.add(errorLabel, cg);

        cg.gridy = row; cg.insets = new Insets(0, 0, 0, 0);
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
            if (!InputValidator.isValidPassword(n)) { errorLabel.setText("Min 6 characters"); return; }
            try {
                if (userService.changePassword(CurrentUser.getInstance().getUser().getId(), o, n)) {
                    errorLabel.setText(" ");
                    ToastNotification.showSuccess(SettingsPanel.this, "Password changed!");
                    oldPwd.setText(""); newPwd.setText(""); confirmPwd.setText("");
                } else {
                    errorLabel.setText("Current password incorrect");
                }
            } catch (Exception ex) { errorLabel.setText(ex.getMessage()); }
        });
        JButton clearPwd = new JButton("Clear");
        ThemeManager.styleGhostButton(clearPwd);
        clearPwd.addActionListener(e -> {
            oldPwd.setText(""); newPwd.setText(""); confirmPwd.setText(""); errorLabel.setText(" ");
        });
        secBtns.add(changePwd);
        secBtns.add(clearPwd);
        card.add(secBtns, cg);

        return card;
    }

    private JPanel buildAboutCard() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(ThemeManager.getCardColor());
        card.setBorder(ThemeManager.createCardBorder());

        GridBagConstraints cg = new GridBagConstraints();
        cg.gridx = 0; cg.anchor = GridBagConstraints.WEST;
        cg.fill = GridBagConstraints.HORIZONTAL; cg.weightx = 1.0;

        cg.gridy = 0; cg.insets = new Insets(0, 0, 12, 0);
        JLabel cardTitle = new JLabel("About CalBank");
        cardTitle.setFont(ThemeManager.getSectionFont());
        cardTitle.setForeground(ThemeManager.getTextColor());
        card.add(cardTitle, cg);

        cg.gridy = 1; cg.insets = new Insets(0, 0, 8, 0);
        JSeparator sep = new JSeparator();
        sep.setForeground(ThemeManager.getBorderColor());
        card.add(sep, cg);

        cg.gridy = 2; cg.insets = new Insets(0, 0, 0, 0);
        JLabel aboutText = new JLabel(
            "<html><b>CalBank</b> v2.0<br>"
            + "Smart Banking for Everyone<br>"
            + "Built with Java Swing + MySQL</html>");
        aboutText.setFont(ThemeManager.getLabelFont());
        aboutText.setForeground(ThemeManager.getTextColor());
        card.add(aboutText, cg);

        return card;
    }
}
