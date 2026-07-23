package com.calbank.models;

import java.time.LocalDateTime;

public final class Account {

    private String accountId;
    private int userId;
    private String accountType;
    private double balance;
    private String currency;
    private String status;
    private LocalDateTime createdAt;

    public Account() {}

    public Account(String accountId, int userId, String accountType, double balance) {
        this.accountId = accountId;
        this.userId = userId;
        this.accountType = accountType;
        this.balance = balance;
        this.currency = "USD";
        this.status = "ACTIVE";
    }

    public String getAccountId()                { return accountId; }
    public void setAccountId(String accountId)  { this.accountId = accountId; }
    public int getUserId()                      { return userId; }
    public void setUserId(int userId)           { this.userId = userId; }
    public String getAccountType()              { return accountType; }
    public void setAccountType(String type)     { this.accountType = type; }
    public double getBalance()                  { return balance; }
    public void setBalance(double balance)      { this.balance = balance; }
    public String getCurrency()                 { return currency; }
    public void setCurrency(String currency)    { this.currency = currency; }
    public String getStatus()                   { return status; }
    public void setStatus(String status)        { this.status = status; }
    public LocalDateTime getCreatedAt()         { return createdAt; }
    public void setCreatedAt(LocalDateTime dt)  { this.createdAt = dt; }

    public boolean canWithdraw(double amount) {
        return amount > 0 && amount <= balance && "ACTIVE".equals(status);
    }

    public boolean isActive() {
        return "ACTIVE".equals(status);
    }

    @Override
    public String toString() {
        return String.format("%s [%s] $%.2f", accountId, accountType, balance);
    }
}
