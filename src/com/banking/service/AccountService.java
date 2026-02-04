package com.banking.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import com.banking.database.DatabaseConnection;
import com.banking.model.Account;

/**
 * Service layer for Account operations
 */
public class AccountService {

    /**
     * Create a new account in database
     */
    public boolean createAccount(Account account) {
        String accountNumber = generateAccountNumber();
        account.setAccountNumber(accountNumber);

        String sql = "INSERT INTO accounts (user_id, account_number, account_type, balance, currency, status, created_date) "
                + "VALUES (?, ?, ?, ?, ?, ?, NOW())";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, account.getUserId());
            stmt.setString(2, accountNumber);
            stmt.setString(3, account.getAccountType());
            stmt.setDouble(4, account.getBalance());
            stmt.setString(5, account.getCurrency());
            stmt.setString(6, "ACTIVE");

            int result = stmt.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            System.err.println("Error creating account: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get account by account number
     */
    public Account getAccount(String accountNumber) {
        String sql = "SELECT * FROM accounts WHERE account_number = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, accountNumber);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Account account = new Account();
                account.setAccountId(rs.getInt("account_id"));
                account.setUserId(rs.getInt("user_id"));
                account.setAccountNumber(rs.getString("account_number"));
                account.setAccountType(rs.getString("account_type"));
                account.setBalance(rs.getDouble("balance"));
                account.setCurrency(rs.getString("currency"));
                account.setStatus(rs.getString("status"));
                account.setCreatedDate(new Date(rs.getTimestamp("created_date").getTime()));

                return account;
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving account: " + e.getMessage());
        }

        return null;
    }

    /**
     * Get all accounts for a user
     */
    public List<Account> getAllUserAccounts(int userId) {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts WHERE user_id = ? AND status = 'ACTIVE'";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Account account = new Account();
                account.setAccountId(rs.getInt("account_id"));
                account.setUserId(rs.getInt("user_id"));
                account.setAccountNumber(rs.getString("account_number"));
                account.setAccountType(rs.getString("account_type"));
                account.setBalance(rs.getDouble("balance"));
                account.setCurrency(rs.getString("currency"));
                account.setStatus(rs.getString("status"));
                account.setCreatedDate(new Date(rs.getTimestamp("created_date").getTime()));

                accounts.add(account);
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving accounts: " + e.getMessage());
        }

        return accounts;
    }

    /**
     * Update account balance
     */
    public boolean updateAccountBalance(String accountNumber, double newBalance) {
        String sql = "UPDATE accounts SET balance = ? WHERE account_number = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, newBalance);
            stmt.setString(2, accountNumber);

            int result = stmt.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            System.err.println("Error updating balance: " + e.getMessage());
            return false;
        }
    }

    /**
     * Delete account (soft delete - mark as CLOSED)
     */
    public boolean deleteAccount(String accountNumber) {
        String sql = "UPDATE accounts SET status = 'CLOSED' WHERE account_number = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, accountNumber);

            int result = stmt.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting account: " + e.getMessage());
            return false;
        }
    }

    /**
     * Check if account exists
     */
    public boolean accountExists(String accountNumber) {
        String sql = "SELECT COUNT(*) FROM accounts WHERE account_number = ? AND status = 'ACTIVE'";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, accountNumber);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.err.println("Error checking account: " + e.getMessage());
        }

        return false;
    }

    /**
     * Generate unique account number
     */
    private String generateAccountNumber() {
        Random random = new Random();
        long accountNum = 1000000000L + random.nextLong(9000000000L);
        return "ACC" + accountNum;
    }
}
