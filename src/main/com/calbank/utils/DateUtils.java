package com.calbank.utils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

public final class DateUtils {

    private static final DateTimeFormatter DISPLAY = DateTimeFormatter.ofPattern("MMM dd, yyyy");
    private static final DateTimeFormatter FULL = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter SHORT = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private static final DateTimeFormatter TIME_ONLY = DateTimeFormatter.ofPattern("hh:mm a");
    private static final DateTimeFormatter MONTH_YEAR = DateTimeFormatter.ofPattern("MMMM yyyy");

    private DateUtils() {}

    public static String formatDisplay(LocalDateTime dt) {
        return dt != null ? dt.format(DISPLAY) : "";
    }

    public static String formatFull(LocalDateTime dt) {
        return dt != null ? dt.format(FULL) : "";
    }

    public static String formatShort(LocalDateTime dt) {
        return dt != null ? dt.format(SHORT) : "";
    }

    public static String formatTime(LocalDateTime dt) {
        return dt != null ? dt.format(TIME_ONLY) : "";
    }

    public static String formatMonthYear(LocalDateTime dt) {
        return dt != null ? dt.format(MONTH_YEAR) : "";
    }

    public static String formatDisplay(Date date) {
        return date != null ? formatFull(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()) : "";
    }

    public static LocalDateTime startOfDay(LocalDateTime dt) {
        return dt != null ? dt.withHour(0).withMinute(0).withSecond(0).withNano(0) : null;
    }

    public static LocalDateTime endOfDay(LocalDateTime dt) {
        return dt != null ? dt.withHour(23).withMinute(59).withSecond(59).withNano(999999999) : null;
    }

    public static LocalDateTime startOfMonth(LocalDateTime dt) {
        return dt != null ? dt.with(TemporalAdjusters.firstDayOfMonth()).withHour(0).withMinute(0).withSecond(0) : null;
    }

    public static LocalDateTime endOfMonth(LocalDateTime dt) {
        return dt != null ? dt.with(TemporalAdjusters.lastDayOfMonth()).withHour(23).withMinute(59).withSecond(59) : null;
    }

    public static LocalDateTime monthsAgo(int months) {
        return LocalDateTime.now().minusMonths(months);
    }

    public static LocalDateTime now() {
        return LocalDateTime.now();
    }
}
