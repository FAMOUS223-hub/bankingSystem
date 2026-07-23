package com.calbank.ui;

import com.calbank.ui.theme.ThemeManager;

import javax.swing.*;
import java.awt.*;

public final class ToastNotification {

    private ToastNotification() {}

    public static void show(JComponent parent, String message, ToastType type) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(parent);
            if (frame == null) return;

            Color bgColor;
            String icon;
            switch (type) {
                case SUCCESS:
                    bgColor = ThemeManager.getSuccessColor();
                    icon = "\u2705 ";
                    break;
                case ERROR:
                    bgColor = ThemeManager.getErrorColor();
                    icon = "\u274C ";
                    break;
                case WARNING:
                    bgColor = new Color(255, 152, 0);
                    icon = "\u26A0\uFE0F ";
                    break;
                case INFO:
                default:
                    bgColor = ThemeManager.getInfoColor();
                    icon = "\u2139\uFE0F ";
                    break;
            }

            JWindow toast = new JWindow();
            toast.setAlwaysOnTop(true);

            JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
            panel.setBackground(bgColor);

            JLabel msgLabel = new JLabel(icon + message);
            msgLabel.setForeground(Color.WHITE);
            msgLabel.setFont(ThemeManager.getButtonFont());
            panel.add(msgLabel);

            toast.getContentPane().add(panel);
            toast.pack();

            int toastWidth = toast.getWidth();
            int frameX = frame.getX();
            int frameWidth = frame.getWidth();
            toast.setLocation(frameX + (frameWidth - toastWidth) / 2, frame.getY() + 20);

            toast.setVisible(true);

            Timer fadeTimer = new Timer(2500, e -> {
                toast.dispose();
            });
            fadeTimer.setRepeats(false);
            fadeTimer.start();
        });
    }

    public static void showSuccess(JComponent parent, String message) {
        show(parent, message, ToastType.SUCCESS);
    }

    public static void showError(JComponent parent, String message) {
        show(parent, message, ToastType.ERROR);
    }

    public static void showWarning(JComponent parent, String message) {
        show(parent, message, ToastType.WARNING);
    }

    public static void showInfo(JComponent parent, String message) {
        show(parent, message, ToastType.INFO);
    }

    public enum ToastType {
        SUCCESS, ERROR, WARNING, INFO
    }
}
