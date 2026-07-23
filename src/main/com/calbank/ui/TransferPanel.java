package com.calbank.ui;

import com.calbank.models.Account;
import com.calbank.models.Transaction;
import com.calbank.services.AccountService;
import com.calbank.services.TransactionService;
import com.calbank.ui.theme.ThemeManager;
import com.calbank.utils.IconUtils;
import com.calbank.utils.InputValidator;
import com.calbank.utils.TransactionUtils;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public final class TransferPanel extends JPanel implements MainContentPanel.Refreshable {

    private final AccountService accountService;
    private final TransactionService transactionService;
    private JComboBox<String> fromAccountSelector;
    private JComboBox<String> toAccountSelector;
    private JTextField amountField;
    private JTextField descriptionField;
    private JLabel errorLabel;
    private JLabel balanceLabel;

    public TransferPanel(AccountService accountService, TransactionService transactionService) {
        this.accountService = accountService;
        this.transactionService = transactionService;
        setLayout(new BorderLayout());
        setBackground(ThemeManager.getBackgroundColor());
        refresh();
    }

    @Override
    public void refresh() {
        removeAll();

        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(ThemeManager.getBackgroundColor());
        content.setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;

        gbc.gridy = 0; gbc.insets = new Insets(0, 0, 28, 0);
        JLabel title = new JLabel("Transfer Funds");
        title.setFont(ThemeManager.getTitleFont());
        title.setForeground(ThemeManager.getTextColor());
        content.add(title, gbc);

        gbc.gridy = 1; gbc.insets = new Insets(0, 0, 28, 0);
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(ThemeManager.getCardColor());
        card.setBorder(ThemeManager.createCardBorder());

        GridBagConstraints cg = new GridBagConstraints();
        cg.gridx = 0; cg.fill = GridBagConstraints.HORIZONTAL; cg.weightx = 1.0;

        List<Account> accounts = accountService.getAccountsByUserId(
            CurrentUser.getInstance().getUser().getId());

        cg.gridy = 0; cg.insets = new Insets(0, 0, 6, 0);
        card.add(makeLabel("From Account"), cg);
        cg.gridy++; cg.insets = new Insets(0, 0, 8, 0);
        fromAccountSelector = new JComboBox<>();
        if (accounts.isEmpty()) {
            fromAccountSelector.addItem("No accounts found");
        } else {
            for (Account a : accounts)
                fromAccountSelector.addItem(a.getAccountId() + " (" + a.getAccountType() + ")");
        }
        ThemeManager.styleComboBox(fromAccountSelector);
        fromAccountSelector.addActionListener(e -> updateBalance());
        card.add(fromAccountSelector, cg);

        cg.gridy++; cg.insets = new Insets(0, 0, 12, 0);
        balanceLabel = new JLabel("Available Balance: $0.00");
        balanceLabel.setFont(ThemeManager.getSectionFont());
        balanceLabel.setForeground(ThemeManager.getAccentColor());
        card.add(balanceLabel, cg);

        // Swap button
        cg.gridy++; cg.insets = new Insets(0, 0, 12, 0);
        JButton swapBtn = new JButton(IconUtils.get("transfer") + " Swap Accounts");
        ThemeManager.styleButton(swapBtn, ThemeManager.getSecondaryColor());
        swapBtn.addActionListener(e -> {
            int fi = fromAccountSelector.getSelectedIndex();
            int ti = toAccountSelector.getSelectedIndex();
            if (fi >= 0 && ti >= 0) {
                fromAccountSelector.setSelectedIndex(ti);
                toAccountSelector.setSelectedIndex(fi);
            }
        });
        card.add(swapBtn, cg);

        cg.gridy++; cg.insets = new Insets(0, 0, 6, 0);
        card.add(makeLabel("To Account"), cg);
        cg.gridy++; cg.insets = new Insets(0, 0, 12, 0);
        toAccountSelector = new JComboBox<>();
        if (accounts.isEmpty()) {
            toAccountSelector.addItem("No accounts found");
        } else {
            for (Account a : accounts)
                toAccountSelector.addItem(a.getAccountId() + " (" + a.getAccountType() + ")");
            if (accounts.size() > 1) toAccountSelector.setSelectedIndex(1);
        }
        ThemeManager.styleComboBox(toAccountSelector);
        card.add(toAccountSelector, cg);

        cg.gridy++; cg.insets = new Insets(0, 0, 6, 0);
        card.add(makeLabel("Amount ($)"), cg);
        cg.gridy++; cg.insets = new Insets(0, 0, 12, 0);
        amountField = new JTextField(20);
        ThemeManager.styleInput(amountField);
        card.add(amountField, cg);

        cg.gridy++; cg.insets = new Insets(0, 0, 6, 0);
        card.add(makeLabel("Description"), cg);
        cg.gridy++; cg.insets = new Insets(0, 0, 12, 0);
        descriptionField = new JTextField(20);
        ThemeManager.styleInput(descriptionField);
        descriptionField.setText("Transfer");
        card.add(descriptionField, cg);

        cg.gridy++; cg.insets = new Insets(0, 0, 8, 0);
        errorLabel = new JLabel(" ");
        errorLabel.setForeground(ThemeManager.getErrorColor());
        errorLabel.setFont(ThemeManager.getSmallFont());
        card.add(errorLabel, cg);

        cg.gridy++; cg.insets = new Insets(0, 0, 0, 0);
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        btnRow.setOpaque(false);
        JButton transferBtn = new JButton("Transfer Funds");
        ThemeManager.stylePrimaryButton(transferBtn);
        transferBtn.addActionListener(e -> handleTransfer());
        JButton clearBtn = new JButton("Clear");
        ThemeManager.styleGhostButton(clearBtn);
        clearBtn.addActionListener(e -> clearForm());
        btnRow.add(transferBtn);
        btnRow.add(clearBtn);
        card.add(btnRow, cg);

        content.add(card, gbc);

        gbc.gridy = 2; gbc.weighty = 1.0; gbc.fill = GridBagConstraints.BOTH;
        content.add(Box.createVerticalGlue(), gbc);

        add(content, BorderLayout.CENTER);
        SwingUtilities.invokeLater(this::updateBalance);
        revalidate();
        repaint();
    }

    private void updateBalance() {
        String selected = (String) fromAccountSelector.getSelectedItem();
        if (selected == null || "No accounts found".equals(selected)) return;
        String accountId = selected.split(" ")[0];
        Account a = accountService.getAccountById(accountId);
        if (a != null)
            balanceLabel.setText("Available Balance: " + TransactionUtils.formatCurrency(a.getBalance()));
    }

    private void handleTransfer() {
        String fromStr = (String) fromAccountSelector.getSelectedItem();
        String toStr = (String) toAccountSelector.getSelectedItem();
        if (fromStr == null || toStr == null || "No accounts found".equals(fromStr) || "No accounts found".equals(toStr)) {
            errorLabel.setText("Create valid accounts first");
            return;
        }
        String fromId = fromStr.split(" ")[0];
        String toId = toStr.split(" ")[0];
        String amount = amountField.getText().trim();
        String desc = descriptionField.getText().trim();

        if (fromId.equals(toId)) { errorLabel.setText("Source and destination must differ"); return; }
        if (amount.isEmpty()) { errorLabel.setText("Please enter an amount"); return; }
        if (!InputValidator.isValidAmount(amount)) { errorLabel.setText("Enter a valid amount > 0"); return; }

        try {
            Transaction tx = transactionService.transfer(fromId, toId, Double.parseDouble(amount), desc);
            if (tx != null) {
                errorLabel.setText(" ");
                ToastNotification.showSuccess(this,
                    "Transferred " + TransactionUtils.formatCurrency(Double.parseDouble(amount))
                    + " | New Balance: " + TransactionUtils.formatCurrency(tx.getBalanceAfter()));
                clearForm();
                updateBalance();
            }
        } catch (Exception ex) { errorLabel.setText(ex.getMessage()); }
    }

    private void clearForm() {
        amountField.setText("");
        descriptionField.setText("Transfer");
        errorLabel.setText(" ");
    }

    private JLabel makeLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(ThemeManager.getLabelFont().deriveFont(Font.BOLD));
        l.setForeground(ThemeManager.getTextColor());
        return l;
    }
}
