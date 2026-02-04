package com.banking.service;

import com.banking.model.Transaction;
import com.banking.model.Account;
import com.banking.database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

/**
 * Service layer for Transaction operations
 */
public class TransactionService {

    private AccountService accountService = new AccountService();

    /**
     * Record a deposit transaction
     */
    public boolean deposit(String accountNumber, double amount) {
        if (amount <= 0) {
            System.out.println("Invalid deposit amount. Amount must be greater than zero.");
            return false;
        }

        Account account = accountService.getAccount(accountNumber);
        if (account == null) {
            System.out.println("Account not found.");
            return false;
        }

        double newBalance = account.getBalance() + amount;

        // Update account balance
        if (!accountService.updateAccountBalance(accountNumber, newBalance)) {
            System.out.println("Failed to update account balance.");
            return false;
        }

        // Record transaction
        Transaction transaction = new Transaction(account.getAccountId(), "DEPOSIT", amount, "Deposit");
        transaction.setBalanceAfter(newBalance);

        return recordTransaction(transaction);
    }

    /**
     * Record a withdrawal transaction
     */
    public boolean withdraw(String accountNumber, double amount) {
        if (amount <= 0) {
            System.out.println("Invalid withdrawal amount. Amount must be greater than zero.");
            return false;
        }

        Account account = accountService.getAccount(accountNumber);
        if (account == null) {
            System.out.println("Account not found.");
            return false;
        }

        if (account.getBalance() < amount) {
            System.out.println("Insufficient balance. Your balance: $" + account.getBalance());
            return false;
        }

        double newBalance = account.getBalance() - amount;

        // Update account balance
        if (!accountService.updateAccountBalance(accountNumber, newBalance)) {
            System.out.println("Failed to update account balance.");
            return false;
        }

        // Record transaction
        Transaction transaction = new Transaction(account.getAccountId(), "WITHDRAWAL", amount, "Withdrawal");
        transaction.setBalanceAfter(newBalance);

        return recordTransaction(transaction);
    }

    /**
     * Record a transfer transaction between two accounts
     */
    public boolean transfer(String fromAccountNumber, String toAccountNumber, double amount) {
        if (amount <= 0) {
            System.out.println("Invalid transfer amount. Amount must be greater than zero.");
            return false;
        }

        if (fromAccountNumber.equals(toAccountNumber)) {
            System.out.println("Cannot transfer to the same account.");
            return false;
        }

        Account fromAccount = accountService.getAccount(fromAccountNumber);
        Account toAccount = accountService.getAccount(toAccountNumber);

        if (fromAccount == null || toAccount == null) {
            System.out.println("One or both accounts not found.");
            return false;
        }

        if (fromAccount.getBalance() < amount) {
            System.out.println("Insufficient balance. Your balance: $" + fromAccount.getBalance());
            return false;
        }

        // Update both accounts
        double newFromBalance = fromAccount.getBalance() - amount;
        double newToBalance = toAccount.getBalance() + amount;

        if (!accountService.updateAccountBalance(fromAccountNumber, newFromBalance)) {
            System.out.println("Failed to process transfer.");
            return false;
        }

        if (!accountService.updateAccountBalance(toAccountNumber, newToBalance)) {
            // Revert the first account update
            accountService.updateAccountBalance(fromAccountNumber, fromAccount.getBalance());
            System.out.println("Failed to process transfer.");
            return false;
        }

        // Record transactions
        Transaction fromTransaction = new Transaction(fromAccount.getAccountId(), "TRANSFER", amount,
                "Transfer to " + toAccountNumber);
        fromTransaction.setBalanceAfter(newFromBalance);

        Transaction toTransaction = new Transaction(toAccount.getAccountId(), "TRANSFER", amount,
                "Transfer from " + fromAccountNumber);
        toTransaction.setBalanceAfter(newToBalance);

        return recordTransaction(fromTransaction) && recordTransaction(toTransaction);
    }

    /**
     * Get transaction history for an account
     */
    public List<Transaction> getTransactionHistory(String accountNumber) {
        List<Transaction> transactions = new ArrayList<>();

        Account account = accountService.getAccount(accountNumber);
        if (account == null) {
            System.out.println("Account not found.");
            return transactions;
        }

        String sql = "SELECT * FROM transactions WHERE account_id = ? ORDER BY timestamp DESC LIMIT 50";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, account.getAccountId());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Transaction transaction = new Transaction();
                transaction.setTransactionId(rs.getInt("transaction_id"));
                transaction.setAccountId(rs.getInt("account_id"));
                transaction.setTransactionType(rs.getString("transaction_type"));
                transaction.setAmount(rs.getDouble("amount"));
                transaction.setBalanceAfter(rs.getDouble("balance_after"));
                transaction.setDescription(rs.getString("description"));
                transaction.setTimestamp(new Date(rs.getTimestamp("timestamp").getTime()));
                transaction.setStatus(rs.getString("status"));

                transactions.add(transaction);
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving transactions: " + e.getMessage());
        }

        return transactions;
    }

    /**
     * Get transaction by transaction ID
     */
    public Transaction getTransaction(int transactionId) {
        String sql = "SELECT * FROM transactions WHERE transaction_id = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, transactionId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Transaction transaction = new Transaction();
                transaction.setTransactionId(rs.getInt("transaction_id"));
                transaction.setAccountId(rs.getInt("account_id"));
                transaction.setTransactionType(rs.getString("transaction_type"));
                transaction.setAmount(rs.getDouble("amount"));
                transaction.setBalanceAfter(rs.getDouble("balance_after"));
                transaction.setDescription(rs.getString("description"));
                transaction.setTimestamp(new Date(rs.getTimestamp("timestamp").getTime()));
                transaction.setStatus(rs.getString("status"));

                return transaction;
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving transaction: " + e.getMessage());
        }

        return null;
    }

    /**
     * Record transaction in database
     */
    private boolean recordTransaction(Transaction transaction) {
        String sql = "INSERT INTO transactions (account_id, transaction_type, amount, balance_after, description, status, timestamp) "
                + "VALUES (?, ?, ?, ?, ?, ?, NOW())";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, transaction.getAccountId());
            stmt.setString(2, transaction.getTransactionType());
            stmt.setDouble(3, transaction.getAmount());
            stmt.setDouble(4, transaction.getBalanceAfter());
            stmt.setString(5, transaction.getDescription());
            stmt.setString(6, "SUCCESS");

            int result = stmt.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            System.err.println("Error recording transaction: " + e.getMessage());
            return false;
        }
    }
}
