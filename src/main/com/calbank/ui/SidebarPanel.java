package com.calbank.ui;

import com.calbank.ui.theme.ThemeManager;
import com.calbank.utils.IconUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public final class SidebarPanel extends JPanel {

    private final List<SidebarButton> buttons = new ArrayList<>();
    private SidebarButton activeButton;
    private final Runnable onLogout;
    private final boolean isAdmin;

    public SidebarPanel(Runnable onLogout) {
        this(onLogout, false);
    }

    public SidebarPanel(Runnable onLogout, boolean isAdmin) {
        this.onLogout = onLogout;
        this.isAdmin = isAdmin;
        setLayout(new GridBagLayout());
        setBackground(ThemeManager.getSidebarColor());
        setPreferredSize(new Dimension(220, 0));
        setMinimumSize(new Dimension(220, 0));
        setMaximumSize(new Dimension(220, Integer.MAX_VALUE));

        JPanel inner = new JPanel();
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setBackground(new Color(0, 0, 0, 0));
        inner.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // Logo
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 12));
        logoPanel.setOpaque(false);
        logoPanel.setMaximumSize(new Dimension(220, 60));
        logoPanel.setPreferredSize(new Dimension(220, 60));

        JLabel logoIcon = IconUtils.createFALabel(isAdmin ? "shield" : "dollar", 16);
        logoIcon.setForeground(Color.WHITE);

        JLabel logoText = new JLabel(isAdmin ? "CalBank Admin" : "CalBank");
        logoText.setFont(ThemeManager.getHeadingFont());
        logoText.setForeground(Color.WHITE);

        logoPanel.add(logoIcon);
        logoPanel.add(logoText);
        inner.add(logoPanel);
        inner.add(createSeparator());

        // Admin badge
        if (isAdmin) {
            JPanel badgePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 4));
            badgePanel.setOpaque(false);
            badgePanel.setMaximumSize(new Dimension(220, 32));
            badgePanel.setPreferredSize(new Dimension(220, 32));
            JLabel badge = new JLabel("Super Admin");
            badge.setFont(ThemeManager.getSmallFont().deriveFont(Font.BOLD));
            badge.setForeground(new Color(255, 193, 7));
            badgePanel.add(badge);
            inner.add(badgePanel);
            inner.add(createSeparator());
        }

        // Nav items
        String[][] navItems;
        if (isAdmin) {
            navItems = new String[][] {
                {"home", "Dashboard"},
                {"users", "Manage Users"},
                {"transactions", "All Transactions"},
                {"settings", "System Settings"},
                {"profile", "My Profile"}
            };
        } else {
            navItems = new String[][] {
                {"home", "Dashboard"},
                {"accounts", "My Accounts"},
                {"deposit", "Deposit"},
                {"withdraw", "Withdraw"},
                {"transfer", "Transfer"},
                {"transactions", "Transactions"},
                {"categories", "Categories"},
                {"reports", "Reports"},
                {"calculators", "Calculators"},
                {"profile", "Profile"},
                {"settings", "Settings"}
            };
        }

        for (String[] item : navItems) {
            SidebarButton btn = new SidebarButton(item[0], item[1]);
            btn.setAlignmentX(Component.LEFT_ALIGNMENT);
            inner.add(btn);
            buttons.add(btn);
        }

        inner.add(Box.createVerticalGlue());
        inner.add(createSeparator());

        SidebarButton logoutBtn = new SidebarButton("logout", "Logout");
        logoutBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoutBtn.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to logout?",
                    "Logout Confirmation",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                CurrentUser.getInstance().logout();
                onLogout.run();
            }
        });
        buttons.add(logoutBtn);
        inner.add(logoutBtn);
        inner.add(Box.createVerticalStrut(8));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        add(inner, gbc);
    }

    private Component createSeparator() {
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(255, 255, 255, 30));
        sep.setMaximumSize(new Dimension(220, 6));
        sep.setPreferredSize(new Dimension(220, 6));
        return sep;
    }

    public void setActive(String text) {
        for (SidebarButton btn : buttons) {
            if (btn.labelText.equals(text)) {
                btn.setActive(true);
                activeButton = btn;
            } else {
                btn.setActive(false);
            }
        }
    }

    public java.util.List<SidebarButton> getButtons() {
        return buttons;
    }

    public void refreshTheme() {
        setBackground(ThemeManager.getSidebarColor());
        for (SidebarButton btn : buttons) btn.updateTheme();
        revalidate();
        repaint();
    }

    public static class SidebarButton extends JButton {

        private final String iconKey;
        private final String labelText;
        private boolean isActive = false;
        private boolean isHovering = false;

        SidebarButton(String iconKey, String labelText) {
            this.iconKey = iconKey;
            this.labelText = labelText;

            setHorizontalAlignment(LEFT);
            setFont(ThemeManager.getLabelFont().deriveFont(Font.PLAIN, 13));
            setForeground(ThemeManager.getSidebarText());
            setBackground(new Color(0, 0, 0, 0));
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(0, 24, 0, 10));
            setMaximumSize(new Dimension(220, 44));
            setPreferredSize(new Dimension(220, 44));
            setMinimumSize(new Dimension(220, 44));
            setHorizontalTextPosition(SwingConstants.RIGHT);

            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                    isHovering = true;
                    repaint();
                }
                @Override public void mouseExited(java.awt.event.MouseEvent e) {
                    isHovering = false;
                    repaint();
                }
            });
        }

        public String getLabelText() { return labelText; }

        void setActive(boolean active) {
            this.isActive = active;
            if (active) {
                setForeground(ThemeManager.getSidebarTextActive());
                setFont(ThemeManager.getLabelFont().deriveFont(Font.BOLD, 13));
            } else {
                setForeground(ThemeManager.getSidebarText());
                setFont(ThemeManager.getLabelFont().deriveFont(Font.PLAIN, 13));
            }
            repaint();
        }

        void updateTheme() {
            setActive(isActive);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (isActive) {
                g2.setColor(ThemeManager.getSidebarActive());
                g2.fillRoundRect(8, 3, getWidth() - 16, getHeight() - 6, 10, 10);
            } else if (isHovering) {
                g2.setColor(ThemeManager.getSidebarHover());
                g2.fillRoundRect(8, 3, getWidth() - 16, getHeight() - 6, 10, 10);
            }

            g2.dispose();

            // Draw text with icon prefix
            Graphics2D gText = (Graphics2D) g;
            gText.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            gText.setFont(getFont());
            gText.setColor(getForeground());

            String icon = IconUtils.get(iconKey);
            Font faFont = IconUtils.getIconFontSmall();
            gText.setFont(faFont);
            gText.setColor(getForeground());
            int textY = (getHeight() + gText.getFontMetrics().getAscent() - gText.getFontMetrics().getDescent()) / 2;
            gText.drawString(icon, 16, textY);
            int iconWidth = gText.getFontMetrics().stringWidth(icon);
            gText.setFont(getFont());
            gText.drawString("   " + labelText, 16 + iconWidth, textY);
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(220, 44);
        }

        @Override
        public String getText() {
            return labelText;
        }
    }
}
