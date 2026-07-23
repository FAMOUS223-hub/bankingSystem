package com.calbank.ui.theme;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public final class ThemeManager {

    public enum Theme { LIGHT, DARK }

    private static Theme currentTheme = Theme.LIGHT;
    private static final java.util.List<Runnable> listeners = new java.util.ArrayList<>();

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
        return isLight() ? new Color(30, 80, 200) : new Color(80, 130, 230);
    }

    public static Color getPrimaryDark() {
        return isLight() ? new Color(20, 60, 160) : new Color(60, 110, 200);
    }

    public static Color getSecondaryColor() {
        return isLight() ? new Color(52, 152, 219) : new Color(52, 73, 94);
    }

    public static Color getBackgroundColor() {
        return isLight() ? new Color(244, 246, 251) : new Color(22, 25, 32);
    }

    public static Color getSurfaceColor() {
        return isLight() ? Color.WHITE : new Color(30, 33, 42);
    }

    public static Color getSidebarColor() {
        return isLight() ? new Color(25, 55, 140) : new Color(18, 20, 28);
    }

    public static Color getSidebarHover() {
        return isLight() ? new Color(40, 75, 170) : new Color(38, 42, 58);
    }

    public static Color getSidebarActive() {
        return isLight() ? new Color(50, 90, 200) : new Color(50, 58, 80);
    }

    public static Color getSidebarText() {
        return isLight() ? new Color(200, 210, 230) : new Color(180, 190, 210);
    }

    public static Color getSidebarTextActive() {
        return isLight() ? Color.WHITE : new Color(255, 255, 255);
    }

    public static Color getForegroundColor() {
        return isLight() ? new Color(44, 62, 80) : new Color(220, 225, 235);
    }

    public static Color getTextColor() {
        return isLight() ? new Color(50, 55, 70) : new Color(210, 215, 225);
    }

    public static Color getTextColorMuted() {
        return isLight() ? new Color(130, 145, 170) : new Color(150, 160, 180);
    }

    public static Color getAccentColor() {
        return isLight() ? new Color(34, 180, 100) : new Color(46, 204, 113);
    }

    public static Color getAccentDark() {
        return isLight() ? new Color(25, 150, 80) : new Color(39, 174, 96);
    }

    public static Color getErrorColor() {
        return isLight() ? new Color(220, 53, 69) : new Color(235, 80, 90);
    }

    public static Color getWarningColor() {
        return isLight() ? new Color(255, 193, 7) : new Color(255, 205, 50);
    }

    public static Color getSuccessColor() {
        return isLight() ? new Color(40, 167, 69) : new Color(60, 190, 90);
    }

    public static Color getInfoColor() {
        return isLight() ? new Color(0, 123, 255) : new Color(100, 170, 255);
    }

    public static Color getCardColor() {
        return getSurfaceColor();
    }

    public static Color getBorderColor() {
        return isLight() ? new Color(220, 225, 235) : new Color(55, 60, 72);
    }

    public static Color getDividerColor() {
        return isLight() ? new Color(230, 235, 242) : new Color(48, 52, 64);
    }

    public static Color getTableHeaderColor() {
        return isLight() ? new Color(30, 80, 200) : new Color(35, 38, 50);
    }

    public static Color getTableRowEven() {
        return isLight() ? Color.WHITE : new Color(30, 33, 42);
    }

    public static Color getTableRowOdd() {
        return isLight() ? new Color(248, 250, 254) : new Color(25, 28, 36);
    }

    public static Color getTableSelectionColor() {
        return isLight() ? new Color(200, 220, 255) : new Color(45, 55, 80);
    }

    public static Color getInputBackground() {
        return isLight() ? new Color(250, 252, 255) : new Color(35, 38, 48);
    }

    public static Color getInputBorder() {
        return isLight() ? new Color(200, 210, 225) : new Color(65, 70, 82);
    }

    public static Color getInputFocusBorder() {
        return getPrimaryColor();
    }

    public static Color getPositiveColor() {
        return new Color(34, 180, 100);
    }

    public static Color getNegativeColor() {
        return new Color(220, 53, 69);
    }

    public static Color getChartColor(int index) {
        Color[] palette = {
            new Color(30, 80, 200), new Color(34, 180, 100), new Color(255, 152, 0),
            new Color(220, 53, 69), new Color(156, 39, 176), new Color(0, 188, 212),
            new Color(255, 87, 34), new Color(103, 58, 183), new Color(0, 150, 136),
            new Color(244, 67, 54)
        };
        return palette[index % palette.length];
    }

    public static void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(getButtonFont());
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 22, 10, 22));
        btn.setOpaque(true);
        btn.setBorderPainted(false);
    }

    public static void styleGhostButton(JButton btn) {
        btn.setBackground(getBorderColor());
        btn.setForeground(getTextColor());
        btn.setFocusPainted(false);
        btn.setFont(getButtonFont());
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 22, 10, 22));
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
        comp.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(getInputBorder(), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        if (isLight()) {
            comp.putClientProperty("JTextField.placeholderForeground", getTextColorMuted());
        }
    }

    public static void styleLabel(JLabel label) {
        label.setForeground(getTextColor());
        label.setFont(getLabelFont());
    }

    public static void styleCard(JPanel card) {
        card.setBackground(getCardColor());
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(getBorderColor(), 1),
            BorderFactory.createEmptyBorder(16, 16, 16, 16)));
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
        UIManager.put("Table.background", getCardColor());
        UIManager.put("Table.foreground", getTextColor());
        UIManager.put("TableHeader.background", getTableHeaderColor());
        UIManager.put("TableHeader.foreground", Color.WHITE);
        UIManager.put("ScrollPane.background", getBackgroundColor());
        UIManager.put("TabbedPane.background", getBackgroundColor());
        UIManager.put("TabbedPane.foreground", getTextColor());
    }

    public static Font getTitleFont() {
        return new Font(getFontFamily(), Font.BOLD, 26);
    }

    public static Font getSubtitleFont() {
        return new Font(getFontFamily(), Font.PLAIN, 16);
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
        return new Font(getFontFamily(), Font.BOLD, 13);
    }

    public static Font getCardValueFont() {
        return new Font(getFontFamily(), Font.BOLD, 28);
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
        String[] preferred = {"SF Pro Text", "SF Pro Display", "Helvetica Neue",
                              "Segoe UI", "Arial", "sans-serif"};
        String[] available = GraphicsEnvironment.getLocalGraphicsEnvironment()
            .getAvailableFontFamilyNames();
        for (String p : preferred) {
            for (String a : available) {
                if (a.equals(p)) return p;
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
