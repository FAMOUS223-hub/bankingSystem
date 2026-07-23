package com.calbank.ui;

import com.calbank.ui.theme.ThemeManager;

import javax.swing.*;
import java.awt.*;

public final class MainContentPanel extends JPanel {

    private final CardLayout cardLayout;
    private final JPanel contentPanel;

    public MainContentPanel() {
        setLayout(new BorderLayout());
        setBackground(ThemeManager.getBackgroundColor());

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(ThemeManager.getBackgroundColor());

        add(contentPanel, BorderLayout.CENTER);
    }

    public void addPanel(JPanel panel, String name) {
        contentPanel.add(new JScrollPane(panel,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), name);
    }

    public void showPanel(String name) {
        cardLayout.show(contentPanel, name);
        for (Component comp : contentPanel.getComponents()) {
            if (comp instanceof JScrollPane) {
                Component view = ((JScrollPane) comp).getViewport().getView();
                if (view instanceof Refreshable) {
                    ((Refreshable) view).refresh();
                }
            }
        }
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    public CardLayout getCardLayout() {
        return cardLayout;
    }

    public JPanel getContentPanel() {
        return contentPanel;
    }

    public void refreshTheme() {
        setBackground(ThemeManager.getBackgroundColor());
        contentPanel.setBackground(ThemeManager.getBackgroundColor());
        for (Component comp : contentPanel.getComponents()) {
            if (comp instanceof JScrollPane) {
                Component view = ((JScrollPane) comp).getViewport().getView();
                if (view instanceof Refreshable) {
                    ((Refreshable) view).refresh();
                }
            }
        }
        revalidate();
        repaint();
    }

    public interface Refreshable {
        void refresh();
    }
}
