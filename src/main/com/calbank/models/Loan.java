package com.calbank.models;

public final class Loan {

    private int loanId;
    private String accountId;
    private double principalAmount;
    private double interestRate;
    private int loanTermMonths;
    private double monthlyPayment;
    private String status;

    public Loan() {}

    public Loan(String accountId, double principalAmount, double interestRate, int loanTermMonths) {
        this.accountId       = accountId;
        this.principalAmount = principalAmount;
        this.interestRate    = interestRate;
        this.loanTermMonths  = loanTermMonths;
        this.status          = "PENDING";
        calculateMonthlyPayment();
    }

    public void calculateMonthlyPayment() {
        double monthlyRate = interestRate / 100.0 / 12.0;
        if (monthlyRate == 0) {
            monthlyPayment = principalAmount / loanTermMonths;
        } else {
            monthlyPayment = (principalAmount * monthlyRate
                    * Math.pow(1 + monthlyRate, loanTermMonths))
                    / (Math.pow(1 + monthlyRate, loanTermMonths) - 1);
        }
    }

    public int getLoanId()                            { return loanId; }
    public void setLoanId(int id)                     { this.loanId = id; }
    public String getAccountId()                      { return accountId; }
    public void setAccountId(String id)               { this.accountId = id; }
    public double getPrincipalAmount()                { return principalAmount; }
    public void setPrincipalAmount(double a)          { this.principalAmount = a; }
    public double getInterestRate()                   { return interestRate; }
    public void setInterestRate(double r)             { this.interestRate = r; }
    public int getLoanTermMonths()                    { return loanTermMonths; }
    public void setLoanTermMonths(int m)              { this.loanTermMonths = m; }
    public double getMonthlyPayment()                 { return monthlyPayment; }
    public void setMonthlyPayment(double p)           { this.monthlyPayment = p; }
    public String getStatus()                         { return status; }
    public void setStatus(String s)                   { this.status = s; }

    public double getTotalAmount()    { return monthlyPayment * loanTermMonths; }
    public double getTotalInterest()  { return getTotalAmount() - principalAmount; }

    @Override
    public String toString() {
        return String.format("Loan[$%.2f @ %.2f%% for %dm - %s]",
                principalAmount, interestRate, loanTermMonths, status);
    }
}
