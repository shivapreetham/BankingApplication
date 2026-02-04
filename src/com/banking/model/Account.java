package com.banking.model;

import java.util.Date;

/**
 * Represents a Bank Account
 */
public class Account {

    private int accountId;
    private int userId;
    private String accountNumber;
    private String accountType;      // SAVINGS, CHECKING, etc.
    private double balance;
    private String currency;         // USD, EUR, etc.
    private String status;           // ACTIVE, CLOSED, FROZEN
    private Date createdDate;

    // Default Constructor
    public Account() {
        this.accountType = "SAVINGS";
        this.currency = "USD";
        this.status = "ACTIVE";
        this.balance = 0.0;
        this.createdDate = new Date();
    }

    // Constructor with parameters
    public Account(int userId, String accountNumber, String accountType, double balance) {
        this.userId = userId;
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.balance = balance;
        this.currency = "USD";
        this.status = "ACTIVE";
        this.createdDate = new Date();
    }

    // Full Constructor
    public Account(int accountId, int userId, String accountNumber, String accountType,
            double balance, String currency, String status, Date createdDate) {
        this.accountId = accountId;
        this.userId = userId;
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.balance = balance;
        this.currency = currency;
        this.status = status;
        this.createdDate = createdDate;
    }

    // Getters and Setters
    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public String toString() {
        return "Account{"
                + "accountId=" + accountId
                + ", accountNumber='" + accountNumber + '\''
                + ", accountType='" + accountType + '\''
                + ", balance=" + balance
                + ", currency='" + currency + '\''
                + ", status='" + status + '\''
                + ", createdDate=" + createdDate
                + '}';
    }
}
