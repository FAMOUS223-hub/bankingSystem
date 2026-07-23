package com.calbank.models;

public final class Savings {

    private int savingsId;
    private String accountId;
    private double initialAmount;
    private double monthlyContribution;
    private double interestRate;
    private int months;
    private double finalAmount;

    public Savings() {}

    public Savings(String accountId, double initialAmount, double monthlyContribution,
                   double interestRate, int months) {
        this.accountId          = accountId;
        this.initialAmount      = initialAmount;
        this.monthlyContribution = monthlyContribution;
        this.interestRate       = interestRate;
        this.months             = months;
        calculateFinalAmount();
    }

    public void calculateFinalAmount() {
        double monthlyRate = interestRate / 100.0 / 12.0;
        double fvInitial = initialAmount * Math.pow(1 + monthlyRate, months);

        double fvMonthly;
        if (monthlyRate == 0) {
            fvMonthly = monthlyContribution * months;
        } else {
            fvMonthly = monthlyContribution
                    * ((Math.pow(1 + monthlyRate, months) - 1) / monthlyRate);
        }
        finalAmount = fvInitial + fvMonthly;
    }

    public int getSavingsId()                              { return savingsId; }
    public void setSavingsId(int id)                       { this.savingsId = id; }
    public String getAccountId()                           { return accountId; }
    public void setAccountId(String id)                    { this.accountId = id; }
    public double getInitialAmount()                       { return initialAmount; }
    public void setInitialAmount(double a)                 { this.initialAmount = a; }
    public double getMonthlyContribution()                 { return monthlyContribution; }
    public void setMonthlyContribution(double m)           { this.monthlyContribution = m; }
    public double getInterestRate()                        { return interestRate; }
    public void setInterestRate(double r)                  { this.interestRate = r; }
    public int getMonths()                                 { return months; }
    public void setMonths(int m)                           { this.months = m; }
    public double getFinalAmount()                         { return finalAmount; }
    public void setFinalAmount(double a)                   { this.finalAmount = a; }

    public double getTotalContributed() {
        return initialAmount + monthlyContribution * months;
    }

    public double getTotalInterest() {
        return finalAmount - getTotalContributed();
    }

    @Override
    public String toString() {
        return String.format("Savings[$%.2f initial + $%.2f/mo @ %.2f%% for %dm = $%.2f]",
                initialAmount, monthlyContribution, interestRate, months, finalAmount);
    }
}
