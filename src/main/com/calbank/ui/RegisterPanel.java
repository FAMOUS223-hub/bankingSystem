package com.calbank.ui;

import com.calbank.models.User;
import com.calbank.services.UserService;
import com.calbank.ui.theme.ThemeManager;
import com.calbank.utils.IconUtils;
import com.calbank.utils.InputValidator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public final class RegisterPanel extends JPanel {

    private final JTextField usernameField, emailField, fullNameField, phoneField;
    private final JPasswordField passwordField, confirmPasswordField;
    private final JTextArea addressArea;
    private final JLabel errorLabel;
    private final JPanel usernameInputPanel, emailInputPanel, fullNameInputPanel;
    private final JPanel phoneInputPanel, passwordInputPanel, confirmPasswordInputPanel;
    private final Runnable onRegisterSuccess;
    private final Runnable onCancel;
    private final UserService userService = new UserService();

    public RegisterPanel(Runnable onRegisterSuccess, Runnable onCancel) {
        this.onRegisterSuccess = onRegisterSuccess;
        this.onCancel = onCancel;

        setLayout(new GridBagLayout());
        setBackground(ThemeManager.getBackgroundColor());

        // ─── Left branding panel ───
        JPanel leftPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();
                GradientPaint grad = new GradientPaint(0, 0, new Color(15, 50, 150), w * 0.3f, h, new Color(8, 30, 100));
                g2.setPaint(grad);
                g2.fillRect(0, 0, w, h);
                g2.setColor(new Color(255, 255, 255, 8));
                g2.fillOval(-60, h - 200, 300, 300);
                g2.fillOval(w - 140, -80, 250, 250);
                g2.setColor(new Color(255, 255, 255, 5));
                g2.fillOval(w / 2 - 100, h / 2 - 100, 300, 300);
            }
        };
        leftPanel.setLayout(new GridBagLayout());

        JPanel brandingInner = new JPanel();
        brandingInner.setLayout(new BoxLayout(brandingInner, BoxLayout.Y_AXIS));
        brandingInner.setOpaque(false);
        brandingInner.setBorder(new EmptyBorder(0, 50, 0, 50));

        JLabel logoIcon = IconUtils.createFALabel("dollar", 36);
        logoIcon.setForeground(Color.WHITE);
        logoIcon.setAlignmentX(Component.LEFT_ALIGNMENT);
        brandingInner.add(logoIcon);
        brandingInner.add(Box.createVerticalStrut(16));

        JLabel appName = new JLabel("CalBank");
        appName.setFont(new Font(ThemeManager.getFontFamily(), Font.BOLD, 40));
        appName.setForeground(Color.WHITE);
        appName.setAlignmentX(Component.LEFT_ALIGNMENT);
        brandingInner.add(appName);
        brandingInner.add(Box.createVerticalStrut(8));

        JLabel tagline = new JLabel("Smart Banking for Everyone");
        tagline.setFont(new Font(ThemeManager.getFontFamily(), Font.PLAIN, 16));
        tagline.setForeground(new Color(180, 200, 240));
        tagline.setAlignmentX(Component.LEFT_ALIGNMENT);
        brandingInner.add(tagline);
        brandingInner.add(Box.createVerticalStrut(40));

        String[][] features = {
            {"lock", "Bank-grade security for your data"},
            {"transfer", "Instant transfers & deposits"},
            {"chart", "Real-time financial insights"},
        };
        for (String[] feat : features) {
            JPanel featRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            featRow.setOpaque(false);
            featRow.setMaximumSize(new Dimension(320, 32));
            featRow.setAlignmentX(Component.LEFT_ALIGNMENT);
            JLabel icon = IconUtils.createFALabel(feat[0], 14);
            icon.setForeground(new Color(180, 200, 240));
            JLabel text = new JLabel(feat[1]);
            text.setFont(new Font(ThemeManager.getFontFamily(), Font.PLAIN, 13));
            text.setForeground(new Color(200, 215, 245));
            featRow.add(icon);
            featRow.add(text);
            brandingInner.add(featRow);
            brandingInner.add(Box.createVerticalStrut(6));
        }

        leftPanel.add(brandingInner);

        // ─── Right form panel (scrollable) ───
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(ThemeManager.getBackgroundColor());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(ThemeManager.getCardColor());
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.getBorderColor(), 1),
            new EmptyBorder(32, 40, 28, 40)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // Title
        gbc.gridy = row++; gbc.insets = new Insets(0, 0, 6, 0);
        JLabel titleLabel = new JLabel("Create Account");
        titleLabel.setFont(new Font(ThemeManager.getFontFamily(), Font.BOLD, 26));
        titleLabel.setForeground(ThemeManager.getTextColor());
        formPanel.add(titleLabel, gbc);

        // Subtitle
        gbc.gridy = row++; gbc.insets = new Insets(0, 0, 24, 0);
        JLabel subtitleLabel = new JLabel("Join CalBank and start banking smarter");
        subtitleLabel.setFont(new Font(ThemeManager.getFontFamily(), Font.PLAIN, 14));
        subtitleLabel.setForeground(ThemeManager.getTextColorMuted());
        formPanel.add(subtitleLabel, gbc);

        // Username
        usernameInputPanel = createInputPanel();
        usernameField = createTextField(usernameInputPanel);
        row = addLabeledField(formPanel, gbc, row, "Username *", usernameInputPanel);

        // Email
        emailInputPanel = createInputPanel();
        emailField = createTextField(emailInputPanel);
        row = addLabeledField(formPanel, gbc, row, "Email Address *", emailInputPanel);

        // Full Name
        fullNameInputPanel = createInputPanel();
        fullNameField = createTextField(fullNameInputPanel);
        row = addLabeledField(formPanel, gbc, row, "Full Name *", fullNameInputPanel);

        // Phone
        phoneInputPanel = createInputPanel();
        phoneField = createTextField(phoneInputPanel);
        row = addLabeledField(formPanel, gbc, row, "Phone Number", phoneInputPanel);

        // Password
        passwordInputPanel = createInputPanel();
        passwordField = createPasswordField(passwordInputPanel);
        row = addLabeledField(formPanel, gbc, row, "Password *", passwordInputPanel);

        // Confirm Password
        confirmPasswordInputPanel = createInputPanel();
        confirmPasswordField = createPasswordField(confirmPasswordInputPanel);
        row = addLabeledField(formPanel, gbc, row, "Confirm Password *", confirmPasswordInputPanel);

        // Address
        gbc.gridy = row++; gbc.insets = new Insets(0, 0, 8, 0);
        JLabel addressLabel = new JLabel("Address");
        addressLabel.setFont(new Font(ThemeManager.getFontFamily(), Font.BOLD, 12));
        addressLabel.setForeground(ThemeManager.getTextColor());
        formPanel.add(addressLabel, gbc);

        addressArea = new JTextArea(3, 20);
        addressArea.setFont(new Font(ThemeManager.getFontFamily(), Font.PLAIN, 14));
        addressArea.setForeground(ThemeManager.getTextColor());
        addressArea.setBackground(ThemeManager.getInputBackground());
        addressArea.setCaretColor(ThemeManager.getTextColor());
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);
        addressArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.getInputBorder(), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        JScrollPane scrollPane = new JScrollPane(addressArea);
        scrollPane.setPreferredSize(new Dimension(340, 70));
        scrollPane.setMinimumSize(new Dimension(340, 70));
        scrollPane.setMaximumSize(new Dimension(340, 70));

        gbc.gridy = row++; gbc.insets = new Insets(0, 0, 10, 0);
        formPanel.add(scrollPane, gbc);

        // Error
        errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font(ThemeManager.getFontFamily(), Font.PLAIN, 12));
        errorLabel.setForeground(ThemeManager.getErrorColor());
        gbc.gridy = row++; gbc.insets = new Insets(0, 0, 12, 0);
        formPanel.add(errorLabel, gbc);

        // Register button
        JButton regBtn = new JButton("Create Account");
        regBtn.setFont(new Font(ThemeManager.getFontFamily(), Font.BOLD, 14));
        regBtn.setForeground(Color.WHITE);
        regBtn.setBackground(ThemeManager.getAccentColor());
        regBtn.setFocusPainted(false);
        regBtn.setBorderPainted(false);
        regBtn.setOpaque(true);
        regBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        regBtn.setPreferredSize(new Dimension(340, 44));
        regBtn.setMinimumSize(new Dimension(340, 44));
        regBtn.setMaximumSize(new Dimension(340, 44));
        regBtn.addActionListener(e -> handleRegister());

        gbc.gridy = row++; gbc.insets = new Insets(0, 0, 16, 0);
        formPanel.add(regBtn, gbc);

        // Divider
        JPanel dividerPanel = new JPanel(new GridBagLayout());
        dividerPanel.setOpaque(false);
        dividerPanel.setPreferredSize(new Dimension(340, 20));
        GridBagConstraints divGbc = new GridBagConstraints();
        divGbc.gridy = 0;
        JSeparator leftSep = new JSeparator();
        leftSep.setPreferredSize(new Dimension(100, 1));
        leftSep.setForeground(ThemeManager.getBorderColor());
        JLabel orLabel = new JLabel("or");
        orLabel.setFont(new Font(ThemeManager.getFontFamily(), Font.PLAIN, 11));
        orLabel.setForeground(ThemeManager.getTextColorMuted());
        orLabel.setBorder(new EmptyBorder(0, 8, 0, 8));
        JSeparator rightSep = new JSeparator();
        rightSep.setPreferredSize(new Dimension(100, 1));
        rightSep.setForeground(ThemeManager.getBorderColor());
        divGbc.gridx = 0; dividerPanel.add(leftSep, divGbc);
        divGbc.gridx = 1; dividerPanel.add(orLabel, divGbc);
        divGbc.gridx = 2; dividerPanel.add(rightSep, divGbc);

        gbc.gridy = row++; gbc.insets = new Insets(0, 0, 14, 0);
        formPanel.add(dividerPanel, gbc);

        // Sign in link
        JButton signInBtn = new JButton("Already have an account? Sign In");
        signInBtn.setFont(new Font(ThemeManager.getFontFamily(), Font.PLAIN, 13));
        signInBtn.setForeground(ThemeManager.getPrimaryColor());
        signInBtn.setBackground(null);
        signInBtn.setFocusPainted(false);
        signInBtn.setBorderPainted(false);
        signInBtn.setOpaque(false);
        signInBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        signInBtn.setPreferredSize(new Dimension(340, 36));
        signInBtn.addActionListener(e -> onCancel.run());

        gbc.gridy = row++; gbc.insets = new Insets(0, 0, 0, 0);
        formPanel.add(signInBtn, gbc);

        // Scrollable form wrapper
        JScrollPane formScroll = new JScrollPane(formPanel);
        formScroll.setBorder(null);
        formScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        formScroll.getVerticalScrollBar().setUnitIncrement(16);
        formScroll.setPreferredSize(new Dimension(420, 600));

        rightPanel.add(formScroll);

        // Assemble
        GridBagConstraints mainGbc = new GridBagConstraints();
        mainGbc.gridx = 0; mainGbc.gridy = 0;
        mainGbc.weighty = 1.0;
        mainGbc.fill = GridBagConstraints.BOTH;
        add(leftPanel, mainGbc);

        mainGbc.gridx = 1;
        mainGbc.weightx = 1.0;
        add(rightPanel, mainGbc);

        // Focus effects
        setupFocusEffect(usernameInputPanel, usernameField);
        setupFocusEffect(emailInputPanel, emailField);
        setupFocusEffect(fullNameInputPanel, fullNameField);
        setupFocusEffect(phoneInputPanel, phoneField);
        setupFocusEffect(passwordInputPanel, passwordField);
        setupFocusEffect(confirmPasswordInputPanel, confirmPasswordField);

        SwingUtilities.invokeLater(() -> usernameField.requestFocusInWindow());
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(340, 42));
        panel.setMinimumSize(new Dimension(340, 42));
        panel.setMaximumSize(new Dimension(340, 42));
        panel.setBackground(ThemeManager.getInputBackground());
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.getInputBorder(), 1),
            BorderFactory.createEmptyBorder(0, 12, 0, 12)));
        return panel;
    }

    private JTextField createTextField(JPanel inputPanel) {
        JTextField field = new JTextField();
        field.setFont(new Font(ThemeManager.getFontFamily(), Font.PLAIN, 14));
        field.setForeground(ThemeManager.getTextColor());
        field.setCaretColor(ThemeManager.getTextColor());
        field.setBorder(null);
        field.setOpaque(false);
        inputPanel.add(field, BorderLayout.CENTER);
        return field;
    }

    private JPasswordField createPasswordField(JPanel inputPanel) {
        JPasswordField field = new JPasswordField();
        field.setFont(new Font(ThemeManager.getFontFamily(), Font.PLAIN, 14));
        field.setForeground(ThemeManager.getTextColor());
        field.setCaretColor(ThemeManager.getTextColor());
        field.setBorder(null);
        field.setOpaque(false);
        inputPanel.add(field, BorderLayout.CENTER);
        return field;
    }

    private int addLabeledField(JPanel formPanel, GridBagConstraints gbc, int row, String labelText, JPanel inputPanel) {
        gbc.gridy = row; gbc.insets = new Insets(0, 0, 8, 0);
        JLabel label = new JLabel(labelText);
        label.setFont(new Font(ThemeManager.getFontFamily(), Font.BOLD, 12));
        label.setForeground(ThemeManager.getTextColor());
        formPanel.add(label, gbc);

        gbc.gridy = row + 1; gbc.insets = new Insets(0, 0, 14, 0);
        formPanel.add(inputPanel, gbc);

        return row + 2;
    }

    private void setupFocusEffect(JPanel inputPanel, JTextField field) {
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                inputPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ThemeManager.getInputFocusBorder(), 2),
                    BorderFactory.createEmptyBorder(0, 11, 0, 11)));
            }
            @Override
            public void focusLost(FocusEvent e) {
                inputPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ThemeManager.getInputBorder(), 1),
                    BorderFactory.createEmptyBorder(0, 12, 0, 12)));
            }
        });
    }

    private void handleRegister() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String fullName = fullNameField.getText().trim();
        String phone = phoneField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirm = new String(confirmPasswordField.getPassword());
        String address = addressArea.getText().trim();

        if (username.isEmpty() || email.isEmpty() || fullName.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please fill in all required fields");
            errorLabel.setForeground(ThemeManager.getErrorColor());
            return;
        }
        if (!InputValidator.isValidUsername(username)) {
            errorLabel.setText("Username: 3-20 chars, alphanumeric and underscore");
            errorLabel.setForeground(ThemeManager.getErrorColor());
            return;
        }
        if (!InputValidator.isValidEmail(email)) {
            errorLabel.setText("Please enter a valid email address");
            errorLabel.setForeground(ThemeManager.getErrorColor());
            return;
        }
        if (!InputValidator.isValidPassword(password)) {
            errorLabel.setText("Password must be at least 6 characters");
            errorLabel.setForeground(ThemeManager.getErrorColor());
            return;
        }
        if (!password.equals(confirm)) {
            errorLabel.setText("Passwords do not match");
            errorLabel.setForeground(ThemeManager.getErrorColor());
            return;
        }
        if (!phone.isEmpty() && !InputValidator.isValidPhone(phone)) {
            errorLabel.setText("Please enter a valid phone number");
            errorLabel.setForeground(ThemeManager.getErrorColor());
            return;
        }

        try {
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setFullName(fullName);
            user.setPhone(phone);
            user.setAddress(address);

            if (userService.registerUser(user, password)) {
                errorLabel.setText(" ");
                JOptionPane.showMessageDialog(this,
                    "Registration successful! Please sign in.",
                    "Welcome to CalBank", JOptionPane.INFORMATION_MESSAGE);
                onRegisterSuccess.run();
            }
        } catch (Exception ex) {
            errorLabel.setText(ex.getMessage());
            errorLabel.setForeground(ThemeManager.getErrorColor());
        }
    }

    public void resetFields() {
        usernameField.setText("");
        emailField.setText("");
        fullNameField.setText("");
        phoneField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
        addressArea.setText("");
        errorLabel.setText(" ");
    }
}
