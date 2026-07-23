package com.calbank.models;

import java.time.LocalDateTime;

public final class Transaction {

    private int transactionId;
    private String accountId;
    private String transactionType;
    private double amount;
    private double balanceAfter;
    private String description;
    private String recipientAccount;
    private Integer categoryId;
    private LocalDateTime createdAt;
    private String receiptNumber;

    public Transaction() {}

    public Transaction(String accountId, String transactionType, double amount,
                       double balanceAfter, String description) {
        this.accountId = accountId;
        this.transactionType = transactionType;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.description = description;
    }

    public int getTransactionId()                             { return transactionId; }
    public void setTransactionId(int id)                      { this.transactionId = id; }
    public String getAccountId()                              { return accountId; }
    public void setAccountId(String id)                       { this.accountId = id; }
    public String getTransactionType()                        { return transactionType; }
    public void setTransactionType(String type)               { this.transactionType = type; }
    public double getAmount()                                 { return amount; }
    public void setAmount(double amount)                      { this.amount = amount; }
    public double getBalanceAfter()                           { return balanceAfter; }
    public void setBalanceAfter(double balance)               { this.balanceAfter = balance; }
    public String getDescription()                            { return description; }
    public void setDescription(String desc)                   { this.description = desc; }
    public String getRecipientAccount()                       { return recipientAccount; }
    public void setRecipientAccount(String acc)               { this.recipientAccount = acc; }
    public Integer getCategoryId()                            { return categoryId; }
    public void setCategoryId(Integer id)                     { this.categoryId = id; }
    public LocalDateTime getCreatedAt()                       { return createdAt; }
    public void setCreatedAt(LocalDateTime dt)                { this.createdAt = dt; }
    public String getReceiptNumber()                          { return receiptNumber; }
    public void setReceiptNumber(String num)                  { this.receiptNumber = num; }

    public boolean isDeposit()    { return "DEPOSIT".equals(transactionType); }
    public boolean isWithdrawal() { return "WITHDRAW".equals(transactionType); }
    public boolean isTransfer()   { return "TRANSFER".equals(transactionType); }

    @Override
    public String toString() {
        return String.format("[%s] %s $%.2f (Balance: $%.2f)",
                transactionType, receiptNumber, amount, balanceAfter);
    }
}
