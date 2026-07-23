package com.calbank.ui;

import com.calbank.models.Loan;
import com.calbank.models.Savings;
import com.calbank.ui.theme.ThemeManager;
import com.calbank.utils.TransactionUtils;

import javax.swing.*;
import java.awt.*;

public final class CalculatorsPanel extends JPanel {

    public CalculatorsPanel() {
        setLayout(new BorderLayout());
        setBackground(ThemeManager.getBackgroundColor());

        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(ThemeManager.getBackgroundColor());
        content.setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        gbc.gridy = 0; gbc.insets = new Insets(0, 0, 16, 0);
        content.add(new JLabel("Financial Calculators") {{
            setFont(ThemeManager.getTitleFont());
            setForeground(ThemeManager.getTextColor());
        }}, gbc);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(ThemeManager.getInputFont());
        tabs.addTab("Loan Calculator", createLoanCalculator());
        tabs.addTab("Savings Calculator", createSavingsCalculator());
        tabs.addTab("Mortgage Calculator", createMortgageCalculator());

        gbc.gridy = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 1.0;
        content.add(tabs, gbc);

        add(content, BorderLayout.CENTER);
    }

    private JPanel createLoanCalculator() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(ThemeManager.getBackgroundColor());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;

        JTextField principalField = addField(panel, gbc, 0, "Principal Amount ($)");
        JTextField rateField = addField(panel, gbc, 2, "Annual Interest Rate (%)");
        JTextField termField = addField(panel, gbc, 4, "Loan Term (Months)");

        JTextArea results = makeResultsArea();
        gbc.gridy = 6; gbc.insets = new Insets(8, 0, 8, 0);
        panel.add(new JScrollPane(results) {{ setBorder(null); setPreferredSize(new Dimension(0, 160)); }}, gbc);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        btns.setOpaque(false);
        JButton calc = new JButton("Calculate");
        ThemeManager.stylePrimaryButton(calc);
        calc.addActionListener(e -> {
            try {
                double p = Double.parseDouble(principalField.getText().trim());
                double r = Double.parseDouble(rateField.getText().trim());
                int m = Integer.parseInt(termField.getText().trim());
                if (p <= 0 || r < 0 || m <= 0) { results.setText("Invalid input."); return; }
                Loan loan = new Loan("", p, r, m);
                results.setText(String.format(
                    "LOAN CALCULATION RESULTS\n========================\n\n"
                    + "  Principal:     %s\n  Rate:          %.2f%%\n  Term:          %d months\n\n"
                    + "  Monthly:       %s\n  Total:         %s\n  Interest:      %s",
                    TransactionUtils.formatCurrency(p), r, m,
                    TransactionUtils.formatCurrency(loan.getMonthlyPayment()),
                    TransactionUtils.formatCurrency(loan.getTotalAmount()),
                    TransactionUtils.formatCurrency(loan.getTotalInterest())));
            } catch (NumberFormatException ex) { results.setText("Enter valid numbers."); }
        });
        JButton clear = new JButton("Clear");
        ThemeManager.styleGhostButton(clear);
        clear.addActionListener(e -> { principalField.setText(""); rateField.setText(""); termField.setText(""); results.setText(""); });
        btns.add(calc); btns.add(clear);
        gbc.gridy = 7;
        panel.add(btns, gbc);
        return panel;
    }

    private JPanel createSavingsCalculator() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(ThemeManager.getBackgroundColor());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;

        JTextField initialField = addField(panel, gbc, 0, "Initial Amount ($)");
        JTextField monthlyField = addField(panel, gbc, 2, "Monthly Contribution ($)");
        JTextField rateField = addField(panel, gbc, 4, "Annual Interest Rate (%)");
        JTextField timeField = addField(panel, gbc, 6, "Time Period (Months)");

        JTextArea results = makeResultsArea();
        gbc.gridy = 8; gbc.insets = new Insets(8, 0, 8, 0);
        panel.add(new JScrollPane(results) {{ setBorder(null); setPreferredSize(new Dimension(0, 160)); }}, gbc);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        btns.setOpaque(false);
        JButton calc = new JButton("Calculate");
        ThemeManager.styleSuccessButton(calc);
        calc.addActionListener(e -> {
            try {
                double init = Double.parseDouble(initialField.getText().trim());
                double monthly = Double.parseDouble(monthlyField.getText().trim());
                double rate = Double.parseDouble(rateField.getText().trim());
                int months = Integer.parseInt(timeField.getText().trim());
                if (init < 0 || monthly < 0 || rate < 0 || months <= 0) { results.setText("Invalid input."); return; }
                Savings s = new Savings("", init, monthly, rate, months);
                results.setText(String.format(
                    "SAVINGS CALCULATION RESULTS\n============================\n\n"
                    + "  Initial:       %s\n  Monthly:       %s\n  Rate:          %.2f%%\n  Period:        %d months\n\n"
                    + "  Projected:     %s\n  Contributed:   %s\n  Interest:      %s",
                    TransactionUtils.formatCurrency(init), TransactionUtils.formatCurrency(monthly), rate, months,
                    TransactionUtils.formatCurrency(s.getFinalAmount()),
                    TransactionUtils.formatCurrency(s.getTotalContributed()),
                    TransactionUtils.formatCurrency(s.getTotalInterest())));
            } catch (NumberFormatException ex) { results.setText("Enter valid numbers."); }
        });
        JButton clear = new JButton("Clear");
        ThemeManager.styleGhostButton(clear);
        clear.addActionListener(e -> { initialField.setText(""); monthlyField.setText(""); rateField.setText(""); timeField.setText(""); results.setText(""); });
        btns.add(calc); btns.add(clear);
        gbc.gridy = 9;
        panel.add(btns, gbc);
        return panel;
    }

    private JPanel createMortgageCalculator() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(ThemeManager.getBackgroundColor());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;

        JTextField priceField = addField(panel, gbc, 0, "Home Price ($)");
        JTextField downField = addField(panel, gbc, 2, "Down Payment ($)");
        JTextField rateField = addField(panel, gbc, 4, "Annual Interest Rate (%)");
        JTextField termField = addField(panel, gbc, 6, "Loan Term (Years)");

        JTextArea results = makeResultsArea();
        gbc.gridy = 8; gbc.insets = new Insets(8, 0, 8, 0);
        panel.add(new JScrollPane(results) {{ setBorder(null); setPreferredSize(new Dimension(0, 180)); }}, gbc);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        btns.setOpaque(false);
        JButton calc = new JButton("Calculate");
        ThemeManager.stylePrimaryButton(calc);
        calc.addActionListener(e -> {
            try {
                double price = Double.parseDouble(priceField.getText().trim());
                double down = Double.parseDouble(downField.getText().trim());
                double rate = Double.parseDouble(rateField.getText().trim());
                int years = Integer.parseInt(termField.getText().trim());
                if (price <= 0 || down < 0 || rate < 0 || years <= 0) { results.setText("Invalid input."); return; }
                double principal = price - down;
                int months = years * 12;
                double mr = rate / 100.0 / 12.0;
                double mp = mr == 0 ? principal / months :
                    (principal * mr * Math.pow(1 + mr, months)) / (Math.pow(1 + mr, months) - 1);
                double total = mp * months;
                results.setText(String.format(
                    "MORTGAGE CALCULATION RESULTS\n============================\n\n"
                    + "  Home Price:    %s\n  Down Payment:  %s\n  Loan Amount:   %s\n"
                    + "  Rate:          %.2f%%\n  Term:          %d years\n\n"
                    + "  Monthly:       %s\n  Total Paid:    %s\n  Interest:      %s\n  Ratio:         %.1f%%",
                    TransactionUtils.formatCurrency(price), TransactionUtils.formatCurrency(down),
                    TransactionUtils.formatCurrency(principal), rate, years,
                    TransactionUtils.formatCurrency(mp), TransactionUtils.formatCurrency(total),
                    TransactionUtils.formatCurrency(total - principal),
                    principal > 0 ? ((total - principal) / principal * 100) : 0));
            } catch (NumberFormatException ex) { results.setText("Enter valid numbers."); }
        });
        JButton clear = new JButton("Clear");
        ThemeManager.styleGhostButton(clear);
        clear.addActionListener(e -> { priceField.setText(""); downField.setText(""); rateField.setText(""); termField.setText(""); results.setText(""); });
        btns.add(calc); btns.add(clear);
        gbc.gridy = 9;
        panel.add(btns, gbc);
        return panel;
    }

    private JTextField addField(JPanel panel, GridBagConstraints gbc, int row, String label) {
        gbc.gridy = row; gbc.insets = new Insets(0, 0, 4, 0);
        panel.add(new JLabel(label) {{
            setFont(ThemeManager.getLabelFont().deriveFont(Font.BOLD));
            setForeground(ThemeManager.getTextColor());
        }}, gbc);
        gbc.gridy = row + 1; gbc.insets = new Insets(0, 0, 10, 0);
        JTextField field = new JTextField(20);
        ThemeManager.styleInput(field);
        panel.add(field, gbc);
        return field;
    }

    private JTextArea makeResultsArea() {
        JTextArea area = new JTextArea(10, 40);
        area.setEditable(false);
        area.setFont(ThemeManager.getMonoFont());
        area.setBackground(ThemeManager.getInputBackground());
        area.setForeground(ThemeManager.getTextColor());
        area.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.getBorderColor(), 1),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        return area;
    }
}
