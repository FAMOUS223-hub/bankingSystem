package com.calbank.ui;

import com.calbank.models.User;
import com.calbank.services.UserService;
import com.calbank.ui.theme.ThemeManager;
import com.calbank.utils.IconUtils;
import com.calbank.utils.InputValidator;

import javax.swing.*;
import java.awt.*;

public final class ProfilePanel extends JPanel implements MainContentPanel.Refreshable {

    private final UserService userService = new UserService();
    private JTextField fullNameField, emailField, phoneField;
    private JTextArea addressArea;
    private JLabel errorLabel;
    private final Runnable onProfileUpdated;

    @Override
    public void refresh() {
        removeAll();
        setLayout(new BorderLayout());
        setBackground(ThemeManager.getBackgroundColor());

        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(ThemeManager.getBackgroundColor());
        content.setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;

        gbc.gridy = 0; gbc.insets = new Insets(0, 0, 20, 0);
        content.add(new JLabel("My Profile") {{
            setFont(ThemeManager.getTitleFont());
            setForeground(ThemeManager.getTextColor());
        }}, gbc);

        User user = CurrentUser.getInstance().getUser();

        gbc.gridy = 1; gbc.insets = new Insets(0, 0, 20, 0);
        content.add(createProfileCard(user), gbc);

        gbc.gridy = 2; gbc.weighty = 1.0; gbc.fill = GridBagConstraints.BOTH;
        content.add(Box.createVerticalGlue(), gbc);

        add(content, BorderLayout.CENTER);

        User u = CurrentUser.getInstance().getUser();
        if (u != null) {
            if (fullNameField != null) fullNameField.setText(u.getFullName());
            if (emailField != null) emailField.setText(u.getEmail());
            if (phoneField != null) phoneField.setText(u.getPhone() != null ? u.getPhone() : "");
            if (addressArea != null) addressArea.setText(u.getAddress() != null ? u.getAddress() : "");
        }
        revalidate();
        repaint();
    }

    public ProfilePanel(Runnable onProfileUpdated) {
        this.onProfileUpdated = onProfileUpdated;
        refresh();
    }

    private JPanel createProfileCard(User user) {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(ThemeManager.getCardColor());
        card.setBorder(ThemeManager.createCardBorder());

        GridBagConstraints cg = new GridBagConstraints();
        cg.gridx = 0; cg.fill = GridBagConstraints.HORIZONTAL; cg.weightx = 1.0;

        cg.gridy = 0; cg.insets = new Insets(0, 0, 16, 0);
        card.add(createAvatarRow(user), cg);

        cg.gridy = 1; cg.insets = new Insets(0, 0, 4, 0);
        card.add(makeLabel("Full Name *"), cg);
        cg.gridy = 2; cg.insets = new Insets(0, 0, 14, 0);
        fullNameField = new JTextField(20);
        ThemeManager.styleInput(fullNameField);
        fullNameField.setText(user.getFullName());
        card.add(fullNameField, cg);

        cg.gridy = 3; cg.insets = new Insets(0, 0, 4, 0);
        card.add(makeLabel("Email Address *"), cg);
        cg.gridy = 4; cg.insets = new Insets(0, 0, 14, 0);
        emailField = new JTextField(20);
        ThemeManager.styleInput(emailField);
        emailField.setText(user.getEmail());
        card.add(emailField, cg);

        cg.gridy = 5; cg.insets = new Insets(0, 0, 4, 0);
        card.add(makeLabel("Phone Number"), cg);
        cg.gridy = 6; cg.insets = new Insets(0, 0, 14, 0);
        phoneField = new JTextField(20);
        ThemeManager.styleInput(phoneField);
        phoneField.setText(user.getPhone() != null ? user.getPhone() : "");
        card.add(phoneField, cg);

        cg.gridy = 7; cg.insets = new Insets(0, 0, 4, 0);
        card.add(makeLabel("Address"), cg);
        cg.gridy = 8; cg.insets = new Insets(0, 0, 14, 0);
        addressArea = new JTextArea(3, 20);
        ThemeManager.styleInput(addressArea);
        addressArea.setText(user.getAddress() != null ? user.getAddress() : "");
        JScrollPane addrScroll = new JScrollPane(addressArea);
        addrScroll.setBorder(null);
        card.add(addrScroll, cg);

        cg.gridy = 9; cg.insets = new Insets(0, 0, 12, 0);
        card.add(new JLabel("Member since: " + userService.getUserCreationDate(user.getId())) {{
            setFont(ThemeManager.getSmallFont());
            setForeground(ThemeManager.getTextColorMuted());
        }}, cg);

        cg.gridy = 10; cg.insets = new Insets(0, 0, 4, 0);
        errorLabel = new JLabel(" ");
        errorLabel.setForeground(ThemeManager.getErrorColor());
        errorLabel.setFont(ThemeManager.getSmallFont());
        card.add(errorLabel, cg);

        cg.gridy = 11; cg.insets = new Insets(8, 0, 0, 0);
        JButton saveBtn = new JButton("Save Changes");
        ThemeManager.stylePrimaryButton(saveBtn);
        saveBtn.setPreferredSize(new Dimension(200, 40));
        saveBtn.addActionListener(e -> handleSave());
        card.add(saveBtn, cg);

        return card;
    }

    private JPanel createAvatarRow(User user) {
        JPanel row = new JPanel(new GridBagLayout());
        row.setOpaque(false);

        GridBagConstraints rg = new GridBagConstraints();
        rg.gridy = 0;

        rg.gridx = 0; rg.insets = new Insets(0, 0, 0, 16);
        JPanel avatarCircle = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeManager.getPrimaryColor());
                g2.fillOval(0, 0, getWidth(), getHeight());
            }
        };
        avatarCircle.setOpaque(false);
        avatarCircle.setPreferredSize(new Dimension(56, 56));
        JLabel avatarIcon = new JLabel(IconUtils.get("profile"));
        avatarIcon.setFont(IconUtils.getIconFontLarge());
        avatarIcon.setForeground(Color.WHITE);
        avatarCircle.add(avatarIcon);
        row.add(avatarCircle, rg);

        rg.gridx = 1; rg.anchor = GridBagConstraints.WEST; rg.insets = new Insets(0, 0, 0, 0);
        JPanel nameInfo = new JPanel();
        nameInfo.setLayout(new BoxLayout(nameInfo, BoxLayout.Y_AXIS));
        nameInfo.setOpaque(false);
        JLabel nameLabel = new JLabel(user.getFullName());
        nameLabel.setFont(ThemeManager.getHeadingFont());
        nameLabel.setForeground(ThemeManager.getTextColor());
        JLabel usernameLabel = new JLabel("@" + user.getUsername());
        usernameLabel.setFont(ThemeManager.getSmallFont());
        usernameLabel.setForeground(ThemeManager.getTextColorMuted());
        nameInfo.add(nameLabel);
        nameInfo.add(Box.createVerticalStrut(4));
        nameInfo.add(usernameLabel);
        row.add(nameInfo, rg);

        return row;
    }

    private void handleSave() {
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String address = addressArea.getText().trim();

        if (fullName.isEmpty() || email.isEmpty()) { errorLabel.setText("Full name and email required"); return; }
        if (!InputValidator.isValidFullName(fullName)) { errorLabel.setText("Enter a valid full name"); return; }
        if (!InputValidator.isValidEmail(email)) { errorLabel.setText("Enter a valid email"); return; }
        if (!phone.isEmpty() && !InputValidator.isValidPhone(phone)) { errorLabel.setText("Enter a valid phone"); return; }

        try {
            User user = CurrentUser.getInstance().getUser();
            if (userService.emailExistsForOther(email, user.getId())) {
                errorLabel.setText("Email already used by another account"); return;
            }
            user.setFullName(fullName);
            user.setEmail(email);
            user.setPhone(phone);
            user.setAddress(address);
            if (userService.updateUser(user)) {
                errorLabel.setText(" ");
                ToastNotification.showSuccess(this, "Profile updated!");
                onProfileUpdated.run();
            } else {
                errorLabel.setText("Failed to update");
            }
        } catch (Exception ex) { errorLabel.setText(ex.getMessage()); }
    }

    private JLabel makeLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(ThemeManager.getLabelFont().deriveFont(Font.BOLD));
        l.setForeground(ThemeManager.getTextColor());
        return l;
    }
}
