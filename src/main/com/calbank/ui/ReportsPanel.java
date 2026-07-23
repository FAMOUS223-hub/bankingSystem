package com.calbank.ui;

import com.calbank.models.Account;
import com.calbank.services.AccountService;
import com.calbank.services.ReportService;
import com.calbank.services.ReportService.CategorySpending;
import com.calbank.services.ReportService.MonthlySummary;
import com.calbank.ui.theme.ThemeManager;
import com.calbank.utils.TransactionUtils;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public final class ReportsPanel extends JPanel implements MainContentPanel.Refreshable {

    private final AccountService accountService = new AccountService();
    private final ReportService reportService = new ReportService();
    private JPanel reportArea;

    @Override
    public void refresh() {
        LocalDate now = LocalDate.now();
        generateReport(now.getMonthValue(), now.getYear());
    }

    public ReportsPanel() {
        setLayout(new BorderLayout());
        setBackground(ThemeManager.getBackgroundColor());

        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(ThemeManager.getBackgroundColor());
        content.setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;

        gbc.gridy = 0; gbc.insets = new Insets(0, 0, 24, 0);
        JLabel title = new JLabel("Financial Reports");
        title.setFont(ThemeManager.getTitleFont());
        title.setForeground(ThemeManager.getTextColor());
        content.add(title, gbc);

        // Month selector
        JPanel selector = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        selector.setOpaque(false);
        LocalDate now = LocalDate.now();
        selector.add(new JLabel("Period:") {{ setFont(ThemeManager.getLabelFont().deriveFont(Font.BOLD)); setForeground(ThemeManager.getTextColor()); }});
        JSpinner monthSpin = new JSpinner(new SpinnerNumberModel(now.getMonthValue(), 1, 12, 1));
        monthSpin.setFont(ThemeManager.getInputFont());
        selector.add(monthSpin);
        selector.add(new JLabel("/"));
        JSpinner yearSpin = new JSpinner(new SpinnerNumberModel(now.getYear(), 2020, 2050, 1));
        yearSpin.setFont(ThemeManager.getInputFont());
        selector.add(yearSpin);
        JButton genBtn = new JButton("Generate Report");
        ThemeManager.stylePrimaryButton(genBtn);
        selector.add(genBtn);

        gbc.gridy = 1; gbc.insets = new Insets(0, 0, 24, 0);
        content.add(selector, gbc);

        // Report area
        reportArea = new JPanel();
        reportArea.setLayout(new BoxLayout(reportArea, BoxLayout.Y_AXIS));
        reportArea.setOpaque(false);
        gbc.gridy = 2; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 1.0;
        content.add(new JScrollPane(reportArea) {{ setBorder(null); }}, gbc);

        genBtn.addActionListener(e -> generateReport((int) monthSpin.getValue(), (int) yearSpin.getValue()));
        add(content, BorderLayout.CENTER);

        SwingUtilities.invokeLater(() -> generateReport(now.getMonthValue(), now.getYear()));
    }

    private void generateReport(int month, int year) {
        reportArea.removeAll();
        List<Account> accounts = accountService.getAccountsByUserId(
            CurrentUser.getInstance().getUser().getId());

        if (accounts.isEmpty()) {
            reportArea.add(makeInfoLabel("No accounts found."));
            reportArea.revalidate();
            return;
        }

        double totalDep = 0, totalWd = 0, totalTr = 0;
        for (Account a : accounts) {
            MonthlySummary ms = reportService.getMonthlySummary(a.getAccountId(), year, month);
            totalDep += ms.totalDeposits;
            totalWd += ms.totalWithdrawals;
            totalTr += ms.totalTransfers;
        }

        // Summary row
        JPanel summaryRow = new JPanel(new GridLayout(1, 4, 16, 0));
        summaryRow.setOpaque(false);
        summaryRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        summaryRow.add(makeStatCard("Deposits", TransactionUtils.formatCurrency(totalDep), ThemeManager.getAccentColor()));
        summaryRow.add(makeStatCard("Withdrawals", TransactionUtils.formatCurrency(totalWd), ThemeManager.getErrorColor()));
        summaryRow.add(makeStatCard("Transfers", TransactionUtils.formatCurrency(totalTr), ThemeManager.getInfoColor()));
        summaryRow.add(makeStatCard("Net Change",
            TransactionUtils.formatCurrency(totalDep - totalWd),
            totalDep >= totalWd ? ThemeManager.getAccentColor() : ThemeManager.getErrorColor()));
        reportArea.add(summaryRow);
        reportArea.add(Box.createVerticalStrut(20));

        // Category spending
        reportArea.add(new JLabel("Spending by Category") {{
            setFont(ThemeManager.getSectionFont());
            setForeground(ThemeManager.getTextColor());
        }});
        reportArea.add(Box.createVerticalStrut(10));

        List<CategorySpending> spending = reportService.getSpendingByCategory(
            accounts.get(0).getAccountId(), year, month);
        double totalSpending = totalWd + totalTr;

        if (spending.isEmpty()) {
            reportArea.add(makeInfoLabel("No spending data for this period."));
        } else {
            for (CategorySpending cs : spending) {
                JPanel bar = new JPanel(new BorderLayout(8, 0));
                bar.setOpaque(false);
                bar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
                bar.setPreferredSize(new Dimension(0, 28));

                JLabel nameLabel = new JLabel(cs.categoryIcon + " " + cs.categoryName);
                nameLabel.setFont(ThemeManager.getLabelFont());
                nameLabel.setForeground(ThemeManager.getTextColor());
                nameLabel.setPreferredSize(new Dimension(160, 28));
                bar.add(nameLabel, BorderLayout.WEST);

                JLabel amountLabel = new JLabel(TransactionUtils.formatCurrency(cs.totalAmount));
                amountLabel.setFont(ThemeManager.getLabelFont().deriveFont(Font.BOLD));
                amountLabel.setForeground(ThemeManager.getTextColor());
                amountLabel.setPreferredSize(new Dimension(100, 28));
                amountLabel.setHorizontalAlignment(SwingConstants.RIGHT);
                bar.add(amountLabel, BorderLayout.EAST);

                double pct = totalSpending > 0 ? cs.totalAmount / totalSpending : 0;
                JPanel barFill = new JPanel() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        Graphics2D g2 = (Graphics2D) g;
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(ThemeManager.getDividerColor());
                        g2.fillRoundRect(0, 4, getWidth(), 16, 8, 8);
                        g2.setColor(ThemeManager.getChartColor(cs.categoryId));
                        int w = (int) (getWidth() * pct);
                        if (w > 0) g2.fillRoundRect(0, 4, w, 16, 8, 8);
                    }
                };
                barFill.setOpaque(false);
                bar.add(barFill, BorderLayout.CENTER);

                reportArea.add(bar);
                reportArea.add(Box.createVerticalStrut(4));
            }
        }

        reportArea.revalidate();
        reportArea.repaint();
    }

    private JPanel makeStatCard(String label, String value, Color accent) {
        JPanel card = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeManager.getCardColor());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(accent);
                g2.fillRoundRect(0, 0, 4, getHeight(), 3, 3);
            }
        };
        card.setOpaque(false);
        GridBagConstraints cg = new GridBagConstraints();
        cg.anchor = GridBagConstraints.WEST; cg.fill = GridBagConstraints.BOTH; cg.weightx = 1.0;
        cg.gridx = 0; cg.gridy = 0; cg.insets = new Insets(8, 14, 0, 10);
        JLabel val = new JLabel(value);
        val.setFont(ThemeManager.getSectionFont());
        val.setForeground(ThemeManager.getTextColor());
        card.add(val, cg);
        cg.gridy = 1; cg.insets = new Insets(0, 14, 8, 10);
        JLabel lbl = new JLabel(label);
        lbl.setFont(ThemeManager.getCardLabelFont());
        lbl.setForeground(ThemeManager.getTextColorMuted());
        card.add(lbl, cg);
        return card;
    }

    private JLabel makeInfoLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(ThemeManager.getLabelFont());
        l.setForeground(ThemeManager.getTextColorMuted());
        return l;
    }
}
