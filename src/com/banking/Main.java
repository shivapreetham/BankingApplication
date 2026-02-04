package com.banking;

import com.banking.database.DatabaseConnection;
import com.banking.ui.BankingCLI;

/**
 * Main entry point for the Banking System application
 */
public class Main {

    public static void main(String[] args) {
        // Test database connection
        try {
            DatabaseConnection.testConnection();
        } catch (Exception e) {
            System.err.println("Failed to connect to database!");
            e.printStackTrace();
            return;
        }

        // Start the CLI application
        BankingCLI cli = new BankingCLI();
        cli.start();

        // Close database connection on exit
        DatabaseConnection.closeConnection();
    }
}
