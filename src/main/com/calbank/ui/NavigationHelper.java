package com.calbank.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Bridges panel quick-action buttons to sidebar + main content navigation.
 */
public final class NavigationHelper {

    private static Consumer<String> panelNavigator;
    private static final Map<String, String> PANEL_TO_SIDEBAR = new HashMap<>();

    static {
        // User panel mappings
        PANEL_TO_SIDEBAR.put("Dashboard", "Dashboard");
        PANEL_TO_SIDEBAR.put("My Accounts", "My Accounts");
        PANEL_TO_SIDEBAR.put("Deposit", "Deposit");
        PANEL_TO_SIDEBAR.put("Withdraw", "Withdraw");
        PANEL_TO_SIDEBAR.put("Transfer", "Transfer");
        PANEL_TO_SIDEBAR.put("Transactions", "Transactions");
        PANEL_TO_SIDEBAR.put("Categories", "Categories");
        PANEL_TO_SIDEBAR.put("Reports", "Reports");
        PANEL_TO_SIDEBAR.put("Calculators", "Calculators");
        PANEL_TO_SIDEBAR.put("Profile", "Profile");
        PANEL_TO_SIDEBAR.put("Settings", "Settings");

        // Admin panel mappings
        PANEL_TO_SIDEBAR.put("Admin Dashboard", "Dashboard");
        PANEL_TO_SIDEBAR.put("Admin Users", "Manage Users");
        PANEL_TO_SIDEBAR.put("Admin Transactions", "All Transactions");
        PANEL_TO_SIDEBAR.put("Admin Settings", "System Settings");
        PANEL_TO_SIDEBAR.put("My Profile", "My Profile");
    }

    private NavigationHelper() {}

    public static void setPanelNavigator(Consumer<String> navigator) {
        panelNavigator = navigator;
    }

    public static void clear() {
        panelNavigator = null;
    }

    public static void navigateTo(String panelName) {
        if (panelNavigator != null) {
            panelNavigator.accept(panelName);
            return;
        }
        java.awt.Container parent = null;
        for (java.awt.Window window : java.awt.Window.getWindows()) {
            if (window instanceof java.awt.Frame && window.isShowing()) {
                parent = window;
                break;
            }
        }
        if (parent == null) {
            return;
        }
        MainContentPanel main = findMainContent(parent);
        if (main != null) {
            main.showPanel(panelName);
        }
    }

    public static void navigateAdmin(String panelName) {
        navigateTo(panelName);
    }

    public static String sidebarLabelForPanel(String panelName) {
        return PANEL_TO_SIDEBAR.getOrDefault(panelName, panelName);
    }

    private static MainContentPanel findMainContent(java.awt.Container root) {
        for (java.awt.Component comp : root.getComponents()) {
            if (comp instanceof MainContentPanel) {
                return (MainContentPanel) comp;
            }
            if (comp instanceof java.awt.Container) {
                MainContentPanel found = findMainContent((java.awt.Container) comp);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }
}
