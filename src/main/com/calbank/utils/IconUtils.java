package com.calbank.utils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

public final class IconUtils {

    private static Font faSolid;
    private static Font faRegular;
    private static boolean fontLoaded = false;

    private static final Map<String, String> ICONS = new LinkedHashMap<>();

    static {
        loadFont();

        ICONS.put("home", "\uf015");
        ICONS.put("accounts", "\uf19c");
        ICONS.put("deposit", "\uf3d1");
        ICONS.put("withdraw", "\uf540");
        ICONS.put("transfer", "\uf362");
        ICONS.put("transactions", "\uf0ce");
        ICONS.put("reports", "\uf201");
        ICONS.put("calculators", "\uf1ec");
        ICONS.put("settings", "\uf013");
        ICONS.put("profile", "\uf007");
        ICONS.put("categories", "\uf02c");
        ICONS.put("logout", "\uf2f5");
        ICONS.put("bell", "\uf0f3");
        ICONS.put("search", "\uf002");
        ICONS.put("export", "\uf56e");
        ICONS.put("add", "\uf067");
        ICONS.put("delete", "\uf2ed");
        ICONS.put("edit", "\uf303");
        ICONS.put("check", "\uf00c");
        ICONS.put("warning", "\uf071");
        ICONS.put("error", "\uf06a");
        ICONS.put("success", "\uf058");
        ICONS.put("info", "\uf05a");
        ICONS.put("money", "\uf3d1");
        ICONS.put("chart", "\uf201");
        ICONS.put("calendar", "\uf133");
        ICONS.put("star", "\uf005");
        ICONS.put("diamond", "\uf3a5");
        ICONS.put("food", "\uf80c");
        ICONS.put("transport", "\uf1b9");
        ICONS.put("bills", "\uf0eb");
        ICONS.put("shopping", "\uf07a");
        ICONS.put("entertainment", "\uf008");
        ICONS.put("health", "\uf0f1");
        ICONS.put("education", "\uf19d");
        ICONS.put("salary", "\uf3d1");
        ICONS.put("investment", "\uf201");
        ICONS.put("admin", "\uf3ed");
        ICONS.put("users", "\uf0c0");
        ICONS.put("shield", "\uf3ed");
        ICONS.put("database", "\uf1c0");
        ICONS.put("server", "\uf233");
        ICONS.put("lock", "\uf023");
        ICONS.put("unlock", "\uf13e");
        ICONS.put("key", "\uf084");
        ICONS.put("clock", "\uf017");
        ICONS.put("activity", "\uf468");
        ICONS.put("globe", "\uf0ac");
        ICONS.put("refresh", "\uf2f1");
        ICONS.put("download", "\uf019");
        ICONS.put("upload", "\uf093");
        ICONS.put("filter", "\uf0b0");
        ICONS.put("sort", "\uf0dc");
        ICONS.put("arrowRight", "\uf30b");
        ICONS.put("arrowLeft", "\uf30a");
        ICONS.put("chevronRight", "\uf054");
        ICONS.put("chevronLeft", "\uf053");
        ICONS.put("dollar", "\uf155");
        ICONS.put("pieChart", "\uf200");
        ICONS.put("balanceScale", "\uf24e");
        ICONS.put("creditCard", "\uf3d1");
        ICONS.put("piggyBank", "\uf4d5");
        ICONS.put("handHoldingUsd", "\uf3d1");
    }

    private IconUtils() {}

    private static void loadFont() {
        if (fontLoaded) return;
        try {
            faSolid = loadFontResource("fonts/fa-solid-900.ttf", "/fonts/fa-solid-900.ttf");
            faRegular = loadFontResource("fonts/fa-regular-400.ttf", "/fonts/fa-regular-400.ttf");
            fontLoaded = true;
        } catch (Exception e) {
            fontLoaded = true;
        }
    }

    private static Font loadFontResource(String filePath, String classpathPath) throws Exception {
        java.io.InputStream stream = IconUtils.class.getResourceAsStream(classpathPath);
        if (stream != null) {
            try (java.io.InputStream in = stream) {
                Font font = Font.createFont(Font.TRUETYPE_FONT, in);
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
                return font;
            }
        }

        File file = new File(filePath);
        if (!file.exists()) {
            file = new File(System.getProperty("user.dir"), filePath);
        }
        if (file.exists()) {
            Font font = Font.createFont(Font.TRUETYPE_FONT, file);
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
            return font;
        }
        return null;
    }

    public static String get(String key) {
        return ICONS.getOrDefault(key, "\uf111");
    }

    public static Font getIconFont() {
        loadFont();
        return faSolid != null ? faSolid.deriveFont(Font.PLAIN, 16) : new Font(Font.DIALOG, Font.PLAIN, 16);
    }

    public static Font getIconFontLarge() {
        loadFont();
        return faSolid != null ? faSolid.deriveFont(Font.PLAIN, 22) : new Font(Font.DIALOG, Font.PLAIN, 22);
    }

    public static Font getIconFontSmall() {
        loadFont();
        return faSolid != null ? faSolid.deriveFont(Font.PLAIN, 13) : new Font(Font.DIALOG, Font.PLAIN, 13);
    }

    public static Font getIconFontTitle() {
        loadFont();
        return faSolid != null ? faSolid.deriveFont(Font.PLAIN, 28) : new Font(Font.DIALOG, Font.PLAIN, 28);
    }

    public static JLabel createIconLabel(String iconKey, int size) {
        JLabel label = new JLabel(get(iconKey));
        loadFont();
        if (faSolid != null) {
            label.setFont(faSolid.deriveFont(Font.PLAIN, size));
        } else {
            label.setFont(new Font(Font.DIALOG, Font.PLAIN, size));
        }
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }

    public static JLabel createFALabel(String iconKey, int size) {
        loadFont();
        JLabel label = new JLabel(get(iconKey));
        if (faSolid != null) {
            label.setFont(faSolid.deriveFont(Font.PLAIN, size));
        } else {
            label.setFont(new Font(Font.DIALOG, Font.PLAIN, size));
        }
        return label;
    }
}
