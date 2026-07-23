package com.calbank.ui;

import com.calbank.models.User;
import com.calbank.services.UserService;
import com.calbank.ui.theme.ThemeManager;
import com.calbank.utils.IconUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public final class LoginPanel extends JPanel {

    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JLabel errorLabel;
    private final JPanel usernameInputPanel;
    private final JPanel passwordInputPanel;
    private final JLabel usernameStatus;
    private final JLabel passwordStatus;
    private final Runnable onLoginSuccess;
    private final Runnable onAdminLogin;
    private final Runnable onRegister;
    private final UserService userService = new UserService();
    private JButton loginBtn;

    public LoginPanel(Runnable onLoginSuccess, Runnable onRegister) {
        this(onLoginSuccess, null, onRegister);
    }

    public LoginPanel(Runnable onLoginSuccess, Runnable onAdminLogin, Runnable onRegister) {
        this.onLoginSuccess = onLoginSuccess;
        this.onAdminLogin = onAdminLogin;
        this.onRegister = onRegister;

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

        // ─── Right form panel ───
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(ThemeManager.getBackgroundColor());

        // Build form content using GridBagLayout
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(ThemeManager.getCardColor());
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.getBorderColor(), 1),
            new EmptyBorder(44, 44, 40, 44)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Welcome back
        gbc.gridy = 0; gbc.insets = new Insets(0, 0, 6, 0);
        JLabel welcomeLabel = new JLabel("Welcome back");
        welcomeLabel.setFont(new Font(ThemeManager.getFontFamily(), Font.BOLD, 26));
        welcomeLabel.setForeground(ThemeManager.getTextColor());
        formPanel.add(welcomeLabel, gbc);

        // Subtitle
        gbc.gridy = 1; gbc.insets = new Insets(0, 0, 32, 0);
        JLabel subtitleLabel = new JLabel("Sign in to continue to CalBank");
        subtitleLabel.setFont(new Font(ThemeManager.getFontFamily(), Font.PLAIN, 14));
        subtitleLabel.setForeground(ThemeManager.getTextColorMuted());
        formPanel.add(subtitleLabel, gbc);

        // Username label
        gbc.gridy = 2; gbc.insets = new Insets(0, 0, 8, 0);
        JLabel usernameLbl = new JLabel("Username");
        usernameLbl.setFont(new Font(ThemeManager.getFontFamily(), Font.BOLD, 12));
        usernameLbl.setForeground(ThemeManager.getTextColor());
        formPanel.add(usernameLbl, gbc);

        // Username input
        usernameInputPanel = new JPanel(new BorderLayout());
        usernameInputPanel.setPreferredSize(new Dimension(340, 42));
        usernameInputPanel.setMinimumSize(new Dimension(340, 42));
        usernameInputPanel.setMaximumSize(new Dimension(340, 42));
        usernameInputPanel.setBackground(ThemeManager.getInputBackground());
        usernameInputPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.getInputBorder(), 1),
            BorderFactory.createEmptyBorder(0, 12, 0, 12)));

        usernameField = new JTextField();
        usernameField.setFont(new Font(ThemeManager.getFontFamily(), Font.PLAIN, 14));
        usernameField.setForeground(ThemeManager.getTextColor());
        usernameField.setCaretColor(ThemeManager.getTextColor());
        usernameField.setBorder(null);
        usernameField.setOpaque(false);
        usernameInputPanel.add(usernameField, BorderLayout.CENTER);

        usernameStatus = new JLabel("\u25CB");
        usernameStatus.setFont(new Font(Font.DIALOG, Font.PLAIN, 14));
        usernameStatus.setForeground(ThemeManager.getInputBorder());
        usernameStatus.setHorizontalAlignment(SwingConstants.CENTER);
        usernameStatus.setPreferredSize(new Dimension(28, 0));
        usernameInputPanel.add(usernameStatus, BorderLayout.EAST);

        gbc.gridy = 3; gbc.insets = new Insets(0, 0, 18, 0);
        formPanel.add(usernameInputPanel, gbc);

        // Password label
        gbc.gridy = 4; gbc.insets = new Insets(0, 0, 8, 0);
        JLabel passwordLbl = new JLabel("Password");
        passwordLbl.setFont(new Font(ThemeManager.getFontFamily(), Font.BOLD, 12));
        passwordLbl.setForeground(ThemeManager.getTextColor());
        formPanel.add(passwordLbl, gbc);

        // Password input
        passwordInputPanel = new JPanel(new BorderLayout());
        passwordInputPanel.setPreferredSize(new Dimension(340, 42));
        passwordInputPanel.setMinimumSize(new Dimension(340, 42));
        passwordInputPanel.setMaximumSize(new Dimension(340, 42));
        passwordInputPanel.setBackground(ThemeManager.getInputBackground());
        passwordInputPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.getInputBorder(), 1),
            BorderFactory.createEmptyBorder(0, 12, 0, 12)));

        passwordField = new JPasswordField();
        passwordField.setFont(new Font(ThemeManager.getFontFamily(), Font.PLAIN, 14));
        passwordField.setForeground(ThemeManager.getTextColor());
        passwordField.setCaretColor(ThemeManager.getTextColor());
        passwordField.setBorder(null);
        passwordField.setOpaque(false);
        passwordInputPanel.add(passwordField, BorderLayout.CENTER);

        passwordStatus = new JLabel("\u25CB");
        passwordStatus.setFont(new Font(Font.DIALOG, Font.PLAIN, 14));
        passwordStatus.setForeground(ThemeManager.getInputBorder());
        passwordStatus.setHorizontalAlignment(SwingConstants.CENTER);
        passwordStatus.setPreferredSize(new Dimension(28, 0));
        passwordInputPanel.add(passwordStatus, BorderLayout.EAST);

        gbc.gridy = 5; gbc.insets = new Insets(0, 0, 6, 0);
        formPanel.add(passwordInputPanel, gbc);

        // Error
        errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font(ThemeManager.getFontFamily(), Font.PLAIN, 12));
        errorLabel.setForeground(ThemeManager.getErrorColor());
        gbc.gridy = 6; gbc.insets = new Insets(0, 0, 16, 0);
        formPanel.add(errorLabel, gbc);

        // Login button
        loginBtn = new JButton("Sign In");
        loginBtn.setFont(new Font(ThemeManager.getFontFamily(), Font.BOLD, 14));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setBackground(ThemeManager.getPrimaryColor());
        loginBtn.setFocusPainted(false);
        loginBtn.setBorderPainted(false);
        loginBtn.setOpaque(true);
        loginBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginBtn.setPreferredSize(new Dimension(340, 44));
        loginBtn.setMinimumSize(new Dimension(340, 44));
        loginBtn.setMaximumSize(new Dimension(340, 44));
        loginBtn.addActionListener(e -> handleLogin());

        gbc.gridy = 7; gbc.insets = new Insets(0, 0, 20, 0);
        formPanel.add(loginBtn, gbc);

        // Divider
        JPanel dividerPanel = new JPanel(new GridBagLayout());
        dividerPanel.setOpaque(false);
        dividerPanel.setPreferredSize(new Dimension(340, 20));
        GridBagConstraints divGbc = new GridBagConstraints();
        divGbc.gridy = 0;
        JSeparator leftSep = new JSeparator();
        leftSep.setPreferredSize(new Dimension(130, 1));
        leftSep.setForeground(ThemeManager.getBorderColor());
        JLabel orLabel = new JLabel("or");
        orLabel.setFont(new Font(ThemeManager.getFontFamily(), Font.PLAIN, 11));
        orLabel.setForeground(ThemeManager.getTextColorMuted());
        orLabel.setBorder(new EmptyBorder(0, 8, 0, 8));
        JSeparator rightSep = new JSeparator();
        rightSep.setPreferredSize(new Dimension(130, 1));
        rightSep.setForeground(ThemeManager.getBorderColor());
        divGbc.gridx = 0; divGbc.insets = new Insets(0, 0, 0, 0);
        dividerPanel.add(leftSep, divGbc);
        divGbc.gridx = 1;
        dividerPanel.add(orLabel, divGbc);
        divGbc.gridx = 2;
        dividerPanel.add(rightSep, divGbc);

        gbc.gridy = 8; gbc.insets = new Insets(0, 0, 16, 0);
        formPanel.add(dividerPanel, gbc);

        // Register link
        JButton regBtn = new JButton("Create an account");
        regBtn.setFont(new Font(ThemeManager.getFontFamily(), Font.PLAIN, 13));
        regBtn.setForeground(ThemeManager.getPrimaryColor());
        regBtn.setBackground(null);
        regBtn.setFocusPainted(false);
        regBtn.setBorderPainted(false);
        regBtn.setOpaque(false);
        regBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        regBtn.setPreferredSize(new Dimension(340, 36));
        regBtn.addActionListener(e -> onRegister.run());

        gbc.gridy = 9; gbc.insets = new Insets(0, 0, 0, 0);
        formPanel.add(regBtn, gbc);

        // Center form card inside right panel
        rightPanel.add(formPanel);

        // Assemble: left=420px, right=fills rest
        GridBagConstraints mainGbc = new GridBagConstraints();
        mainGbc.gridx = 0; mainGbc.gridy = 0;
        mainGbc.weighty = 1.0;
        mainGbc.fill = GridBagConstraints.BOTH;
        add(leftPanel, mainGbc);

        mainGbc.gridx = 1;
        mainGbc.weightx = 1.0;
        add(rightPanel, mainGbc);

        // Focus effects
        setupFocusEffect(usernameInputPanel, usernameStatus, usernameField);
        setupFocusEffect(passwordInputPanel, passwordStatus, passwordField);

        // Enter key
        KeyAdapter enterKey = new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) handleLogin();
            }
        };
        usernameField.addKeyListener(enterKey);
        passwordField.addKeyListener(enterKey);

        SwingUtilities.invokeLater(() -> usernameField.requestFocusInWindow());
    }

    private void setupFocusEffect(JPanel inputPanel, JLabel statusIcon, JTextField field) {
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                inputPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ThemeManager.getInputFocusBorder(), 2),
                    BorderFactory.createEmptyBorder(0, 11, 0, 11)));
                statusIcon.setForeground(ThemeManager.getInputFocusBorder());
                statusIcon.setText("\u25CF");
            }
            @Override
            public void focusLost(FocusEvent e) {
                inputPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ThemeManager.getInputBorder(), 1),
                    BorderFactory.createEmptyBorder(0, 12, 0, 12)));
                statusIcon.setForeground(ThemeManager.getInputBorder());
                statusIcon.setText("\u25CB");
            }
        });
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter username and password");
            errorLabel.setForeground(ThemeManager.getErrorColor());
            return;
        }

        loginBtn.setEnabled(false);
        errorLabel.setForeground(ThemeManager.getTextColorMuted());
        errorLabel.setText("Signing in...");

        SwingWorker<User, Void> worker = new SwingWorker<User, Void>() {
            @Override
            protected User doInBackground() {
                return userService.loginUser(username, password);
            }

            @Override
            protected void done() {
                try {
                    User user = get();
                    if (user != null) {
                        CurrentUser.getInstance().setUser(user);
                        errorLabel.setText(" ");
                        if (user.isAdmin() && onAdminLogin != null) {
                            onAdminLogin.run();
                        } else {
                            onLoginSuccess.run();
                        }
                    } else {
                        errorLabel.setForeground(ThemeManager.getErrorColor());
                        errorLabel.setText("Invalid username or password");
                    }
                } catch (Exception ex) {
                    errorLabel.setForeground(ThemeManager.getErrorColor());
                    errorLabel.setText("Login error: " + ex.getMessage());
                }
                loginBtn.setEnabled(true);
            }
        };
        worker.execute();
    }

    public void resetFields() {
        usernameField.setText("");
        passwordField.setText("");
        errorLabel.setText(" ");
    }
}
