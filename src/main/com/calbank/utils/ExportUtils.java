package com.calbank.utils;

import com.calbank.models.Transaction;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Component;
import java.io.*;
import java.util.List;

public final class ExportUtils {

    private ExportUtils() {}

    public static boolean exportTransactionsToCSV(List<Transaction> transactions, Component parent) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Export Transactions");
        chooser.setSelectedFile(new File("transactions_export.csv"));
        chooser.setFileFilter(new FileNameExtensionFilter("CSV Files", "csv"));

        if (chooser.showSaveDialog(parent) != JFileChooser.APPROVE_OPTION) {
            return false;
        }

        File file = chooser.getSelectedFile();
        if (!file.getName().endsWith(".csv")) {
            file = new File(file.getAbsolutePath() + ".csv");
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println("Date,Type,Amount,Balance After,Description,Receipt Number");

            for (Transaction t : transactions) {
                writer.println(String.format("\"%s\",\"%s\",%.2f,%.2f,\"%s\",\"%s\"",
                    DateUtils.formatFull(t.getCreatedAt()),
                    t.getTransactionType(),
                    t.getAmount(),
                    t.getBalanceAfter(),
                    safeQuote(t.getDescription()),
                    t.getReceiptNumber() != null ? t.getReceiptNumber() : ""));
            }

            JOptionPane.showMessageDialog(parent,
                "Transactions exported successfully!\n" + file.getAbsolutePath(),
                "Export Complete", JOptionPane.INFORMATION_MESSAGE);
            return true;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(parent,
                "Failed to export: " + e.getMessage(),
                "Export Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private static String safeQuote(String s) {
        if (s == null) return "";
        return s.replace("\"", "\"\"");
    }
}
