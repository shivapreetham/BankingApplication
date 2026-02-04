package com.banking.model;

import java.util.Date;

/**
 * Represents a Bank Transaction
 */
public class Transaction {

    private int transactionId;
    private int accountId;
    private String transactionType;  // DEPOSIT, WITHDRAWAL, TRANSFER
    private double amount;
    private double balanceAfter;
    private String description;
    private Date timestamp;
    private String status;           // SUCCESS, PENDING, FAILED

    // Default Constructor
    public Transaction() {
        this.timestamp = new Date();
        this.status = "PENDING";
    }

    // Constructor with parameters
    public Transaction(int accountId, String transactionType, double amount, String description) {
        this.accountId = accountId;
        this.transactionType = transactionType;
        this.amount = amount;
        this.description = description;
        this.timestamp = new Date();
        this.status = "SUCCESS";
    }

    // Full Constructor
    public Transaction(int transactionId, int accountId, String transactionType, double amount,
            double balanceAfter, String description, Date timestamp, String status) {
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.transactionType = transactionType;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.description = description;
        this.timestamp = timestamp;
        this.status = status;
    }

    // Getters and Setters
    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getBalanceAfter() {
        return balanceAfter;
    }

    public void setBalanceAfter(double balanceAfter) {
        this.balanceAfter = balanceAfter;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Transaction{"
                + "transactionId=" + transactionId
                + ", accountId=" + accountId
                + ", transactionType='" + transactionType + '\''
                + ", amount=" + amount
                + ", balanceAfter=" + balanceAfter
                + ", description='" + description + '\''
                + ", timestamp=" + timestamp
                + ", status='" + status + '\''
                + '}';
    }
}
