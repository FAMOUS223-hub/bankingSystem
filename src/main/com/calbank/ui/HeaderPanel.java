package com.calbank.ui;

import com.calbank.ui.theme.ThemeManager;
import com.calbank.utils.IconUtils;

import javax.swing.*;
import java.awt.*;

public final class HeaderPanel extends JPanel {

    private final JLabel welcomeLabel;
    private final JLabel dateLabel;

    public HeaderPanel() {
        setLayout(new BorderLayout(16, 0));
        setPreferredSize(new Dimension(0, 56));
        setBackground(ThemeManager.getCardColor());
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, ThemeManager.getBorderColor()),
            BorderFactory.createEmptyBorder(0, 24, 0, 24)));

        com.calbank.models.User user = CurrentUser.getInstance().getUser();

        JPanel leftPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        leftPanel.setOpaque(false);

        welcomeLabel = new JLabel("Welcome back, " + (user != null ? user.getDisplayName() : "User"));
        welcomeLabel.setFont(ThemeManager.getHeadingFont());
        welcomeLabel.setForeground(ThemeManager.getTextColor());

        dateLabel = new JLabel(java.time.LocalDate.now().format(
            java.time.format.DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")));
        dateLabel.setFont(ThemeManager.getSmallFont());
        dateLabel.setForeground(ThemeManager.getTextColorMuted());

        leftPanel.add(welcomeLabel);
        leftPanel.add(dateLabel);
        add(leftPanel, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightPanel.setOpaque(false);

        JLabel userIcon = new JLabel(IconUtils.get("bell"));
        userIcon.setFont(IconUtils.getIconFont());
        userIcon.setForeground(ThemeManager.getTextColorMuted());
        userIcon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        rightPanel.add(userIcon);
        add(rightPanel, BorderLayout.EAST);
    }

    public void refreshTheme() {
        setBackground(ThemeManager.getCardColor());
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, ThemeManager.getBorderColor()),
            BorderFactory.createEmptyBorder(0, 24, 0, 24)));
        welcomeLabel.setFont(ThemeManager.getHeadingFont());
        welcomeLabel.setForeground(ThemeManager.getTextColor());
        dateLabel.setForeground(ThemeManager.getTextColorMuted());
        revalidate();
        repaint();
    }
}
