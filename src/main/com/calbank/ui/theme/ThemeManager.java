package com.calbank.ui.theme;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public final class ThemeManager {

    public enum Theme { LIGHT, DARK }

    private static Theme currentTheme = Theme.LIGHT;
    private static final List<Runnable> listeners = new ArrayList<>();

    private ThemeManager() {}

    public static void setTheme(Theme theme) {
        currentTheme = theme;
        for (Runnable r : listeners) {
            try { r.run(); } catch (Exception ignored) {}
        }
    }

    public static Theme getCurrentTheme() { return currentTheme; }

    public static boolean isLight() { return currentTheme == Theme.LIGHT; }

    public static void addThemeChangeListener(Runnable listener) {
        listeners.add(listener);
    }

    public static Color getPrimaryColor() {
        return isLight() ? new Color(79, 70, 229) : new Color(99, 102, 241);
    }

    public static Color getPrimaryDark() {
        return isLight() ? new Color(67, 56, 202) : new Color(79, 70, 229);
    }

    public static Color getSecondaryColor() {
        return isLight() ? new Color(14, 165, 233) : new Color(56, 189, 248);
    }

    public static Color getBackgroundColor() {
        return isLight() ? new Color(248, 250, 252) : new Color(15, 23, 42);
    }

    public static Color getSurfaceColor() {
        return isLight() ? Color.WHITE : new Color(30, 41, 59);
    }

    public static Color getSidebarColor() {
        return isLight() ? Color.BLACK : new Color(11, 15, 25);
    }

    public static Color getSidebarHover() {
        return isLight() ? new Color(50, 50, 50) : new Color(24, 32, 47);
    }

    public static Color getSidebarActive() {
        return isLight() ? new Color(79, 70, 229) : new Color(99, 102, 241);
    }

    public static Color getSidebarText() {
        return Color.WHITE;
    }

    public static Color getSidebarTextActive() {
        return Color.WHITE;
    }

    public static Color getForegroundColor() {
        return isLight() ? new Color(15, 23, 42) : new Color(248, 250, 252);
    }

    public static Color getTextColor() {
        return isLight() ? new Color(15, 23, 42) : new Color(248, 250, 252);
    }

    public static Color getTextColorMuted() {
        return isLight() ? new Color(100, 116, 139) : new Color(203, 213, 225);
    }

    public static Color getAccentColor() {
        return isLight() ? new Color(16, 185, 129) : new Color(52, 211, 153);
    }

    public static Color getAccentDark() {
        return isLight() ? new Color(5, 150, 105) : new Color(16, 185, 129);
    }

    public static Color getErrorColor() {
        return isLight() ? new Color(239, 68, 68) : new Color(248, 113, 113);
    }

    public static Color getWarningColor() {
        return isLight() ? new Color(245, 158, 11) : new Color(251, 191, 36);
    }

    public static Color getSuccessColor() {
        return isLight() ? new Color(16, 185, 129) : new Color(52, 211, 153);
    }

    public static Color getInfoColor() {
        return isLight() ? new Color(59, 130, 246) : new Color(96, 165, 250);
    }

    public static Color getCardColor() {
        return getSurfaceColor();
    }

    public static Color getBorderColor() {
        return isLight() ? new Color(226, 232, 240) : new Color(51, 65, 85);
    }

    public static Color getDividerColor() {
        return isLight() ? new Color(241, 245, 249) : new Color(51, 65, 85);
    }

    public static Color getTableHeaderColor() {
        return isLight() ? new Color(15, 23, 42) : new Color(11, 15, 25);
    }

    public static Color getTableRowEven() {
        return isLight() ? Color.WHITE : new Color(30, 41, 59);
    }

    public static Color getTableRowOdd() {
        return isLight() ? new Color(248, 250, 252) : new Color(23, 32, 47);
    }

    public static Color getTableSelectionColor() {
        return isLight() ? new Color(224, 231, 255) : new Color(49, 46, 129);
    }

    public static Color getInputBackground() {
        return isLight() ? new Color(248, 250, 252) : new Color(15, 23, 42);
    }

    public static Color getInputBorder() {
        return isLight() ? new Color(203, 213, 225) : new Color(71, 85, 105);
    }

    public static Color getInputFocusBorder() {
        return getPrimaryColor();
    }

    public static Color getPositiveColor() {
        return new Color(16, 185, 129);
    }

    public static Color getNegativeColor() {
        return new Color(239, 68, 68);
    }

    public static Color getChartColor(int index) {
        Color[] palette = {
            new Color(79, 70, 229), new Color(16, 185, 129), new Color(245, 158, 11),
            new Color(239, 68, 68), new Color(139, 92, 246), new Color(14, 165, 233),
            new Color(236, 72, 153), new Color(20, 184, 166), new Color(249, 115, 22)
        };
        return palette[index % palette.length];
    }

    public static void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(getButtonFont());
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setOpaque(true);
        btn.setBorderPainted(false);
    }

    public static void styleGhostButton(JButton btn) {
        btn.setBackground(getBorderColor());
        btn.setForeground(getTextColor());
        btn.setFocusPainted(false);
        btn.setFont(getButtonFont());
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setOpaque(true);
        btn.setBorderPainted(false);
    }

    public static void stylePrimaryButton(JButton btn) {
        styleButton(btn, getPrimaryColor());
    }

    public static void styleSuccessButton(JButton btn) {
        styleButton(btn, getAccentColor());
    }

    public static void styleDangerButton(JButton btn) {
        styleButton(btn, getErrorColor());
    }

    public static void styleInput(JComponent comp) {
        comp.setBackground(getInputBackground());
        comp.setForeground(getTextColor());
        comp.setFont(getInputFont());
        if (comp instanceof JTextField || comp instanceof JPasswordField) {
            ((JTextField) comp).setCaretColor(getTextColor());
        }
        if (comp instanceof JComboBox) {
            styleComboBox((JComboBox<?>) comp);
        }
        comp.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(getInputBorder(), 1),
            BorderFactory.createEmptyBorder(8, 14, 8, 14)));
        if (isLight()) {
            comp.putClientProperty("JTextField.placeholderForeground", getTextColorMuted());
        }
    }

    public static void styleComboBox(JComboBox<?> combo) {
        combo.setBackground(getInputBackground());
        combo.setForeground(getTextColor());
        combo.setFont(getInputFont());
        combo.setRenderer(new javax.swing.DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean sel, boolean foc) {
                java.awt.Component c = super.getListCellRendererComponent(list, value, index, sel, foc);
                if (sel) {
                    setBackground(getPrimaryColor());
                    setForeground(Color.BLACK);
                } else {
                    setBackground(getCardColor());
                    setForeground(getTextColor());
                }
                setFont(getInputFont());
                setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
                return c;
            }
        });
    }

    public static void styleLabel(JLabel label) {
        label.setForeground(getTextColor());
        label.setFont(getLabelFont());
    }

    public static void styleCard(JPanel card) {
        card.setBackground(getCardColor());
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(getBorderColor(), 1),
            BorderFactory.createEmptyBorder(20, 24, 20, 24)));
    }

    public static void applyGlobalTheme() {
        UIManager.put("Panel.background", getBackgroundColor());
        UIManager.put("Label.foreground", getTextColor());
        UIManager.put("TextField.background", getInputBackground());
        UIManager.put("TextField.foreground", getTextColor());
        UIManager.put("PasswordField.background", getInputBackground());
        UIManager.put("PasswordField.foreground", getTextColor());
        UIManager.put("ComboBox.background", getInputBackground());
        UIManager.put("ComboBox.foreground", getTextColor());
        UIManager.put("ComboBox.selectionBackground", getPrimaryColor());
        UIManager.put("ComboBox.selectionForeground", Color.BLACK);
        UIManager.put("ComboBoxPopup.background", getCardColor());
        UIManager.put("ComboBoxList.background", getCardColor());
        UIManager.put("ComboBoxList.foreground", getTextColor());
        UIManager.put("List.background", getCardColor());
        UIManager.put("List.foreground", getTextColor());
        UIManager.put("List.selectionBackground", getPrimaryColor());
        UIManager.put("List.selectionForeground", Color.BLACK);
        UIManager.put("Table.background", getCardColor());
        UIManager.put("Table.foreground", getTextColor());
        UIManager.put("TableHeader.background", getTableHeaderColor());
        UIManager.put("TableHeader.foreground", Color.WHITE);
        UIManager.put("ScrollPane.background", getBackgroundColor());
        UIManager.put("TabbedPane.background", getBackgroundColor());
        UIManager.put("TabbedPane.foreground", getTextColor());
        UIManager.put("OptionPane.background", getSurfaceColor());
        UIManager.put("OptionPane.messageForeground", getTextColor());
        UIManager.put("OptionPane.messageFont", getLabelFont());
        UIManager.put("Button.background", getInputBackground());
        UIManager.put("Button.foreground", Color.BLACK);
        UIManager.put("Button.font", getButtonFont());
        UIManager.put("OptionPane.buttonForeground", Color.BLACK);
    }

    public static Font getTitleFont() {
        return new Font(getFontFamily(), Font.BOLD, 26);
    }

    public static Font getSubtitleFont() {
        return new Font(getFontFamily(), Font.PLAIN, 15);
    }

    public static Font getHeadingFont() {
        return new Font(getFontFamily(), Font.BOLD, 18);
    }

    public static Font getSectionFont() {
        return new Font(getFontFamily(), Font.BOLD, 14);
    }

    public static Font getLabelFont() {
        return new Font(getFontFamily(), Font.PLAIN, 13);
    }

    public static Font getInputFont() {
        return new Font(getFontFamily(), Font.PLAIN, 14);
    }

    public static Font getButtonFont() {
        return new Font(getFontFamily(), Font.BOLD, 13);
    }

    public static Font getTableFont() {
        return new Font(getFontFamily(), Font.PLAIN, 13);
    }

    public static Font getTableHeaderFont() {
        return new Font(getFontFamily(), Font.BOLD, 12);
    }

    public static Font getCardValueFont() {
        return new Font(getFontFamily(), Font.BOLD, 26);
    }

    public static Font getCardLabelFont() {
        return new Font(getFontFamily(), Font.PLAIN, 12);
    }

    public static Font getSmallFont() {
        return new Font(getFontFamily(), Font.PLAIN, 11);
    }

    public static Font getMonoFont() {
        return new Font(Font.MONOSPACED, Font.PLAIN, 13);
    }

    public static String getFontFamily() {
        String[] preferred = {"SF Pro Display", "SF Pro Text", "Inter", "Segoe UI", "Helvetica Neue", "Arial", "sans-serif"};
        String[] available = GraphicsEnvironment.getLocalGraphicsEnvironment()
            .getAvailableFontFamilyNames();
        for (String p : preferred) {
            for (String a : available) {
                if (a.equalsIgnoreCase(p)) return a;
            }
        }
        return Font.SANS_SERIF;
    }

    public static Border createCardBorder() {
        return BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(getBorderColor(), 1),
            BorderFactory.createEmptyBorder(20, 24, 20, 24));
    }

    public static Border createEmptyBorder(int top, int left, int bottom, int right) {
        return BorderFactory.createEmptyBorder(top, left, bottom, right);
    }
}
