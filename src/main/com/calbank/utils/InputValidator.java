package com.calbank.utils;

import java.util.regex.Pattern;

public final class InputValidator {

    private static final Pattern EMAIL    = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE    = Pattern.compile("^[0-9]{7,15}$");
    private static final Pattern USERNAME = Pattern.compile("^[a-zA-Z0-9_]{3,20}$");

    private InputValidator() {}

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL.matcher(email).matches();
    }

    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.isBlank()) return true;
        return PHONE.matcher(phone.replaceAll("[^0-9]", "")).matches();
    }

    public static boolean isValidUsername(String username) {
        return username != null && USERNAME.matcher(username).matches();
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

    public static boolean isValidAmount(String amount) {
        try {
            double value = Double.parseDouble(amount);
            return value > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidFullName(String name) {
        return name != null && name.trim().length() >= 2;
    }

    public static String sanitize(String input) {
        return input != null ? input.trim() : "";
    }
}
