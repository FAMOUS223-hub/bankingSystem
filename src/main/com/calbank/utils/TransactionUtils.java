package com.calbank.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public final class TransactionUtils {

    private static final String ACCOUNT_PREFIX = "CAL";

    private TransactionUtils() {}

    public static String generateAccountNumber() {
        long timestamp = System.currentTimeMillis() % 100000;
        int random = (int) (Math.random() * 10000);
        return String.format("%s-%05d-%04d", ACCOUNT_PREFIX, timestamp, random);
    }

    public static String generateReceiptNumber() {
        String date = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String random = String.format("%06d", (int) (Math.random() * 1000000));
        return "RCPT-" + date + "-" + random;
    }

    public static String generateTransactionId() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public static String formatCurrency(double amount) {
        return String.format("$%,.2f", amount);
    }

    public static String formatDate(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }
}
