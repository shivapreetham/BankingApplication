package com.banking.cli;

import com.banking.model.Account;
import com.banking.model.Transaction;
import com.banking.model.User;
import com.banking.service.AccountService;
import com.banking.service.TransactionService;
import com.banking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Interactive CLI Interface for Banking System Run with: java -jar app.jar
 * --spring.profiles.active=cli
 */
@Component
@Profile("cli")
@RequiredArgsConstructor
public class BankingCLI implements CommandLineRunner {

    private final UserService userService;
    private final AccountService accountService;
    private final TransactionService transactionService;
    private final Scanner scanner = new Scanner(System.in);

    private Integer currentUserId = null;
    private String currentUsername = null;

    @Override
    public void run(String... args) {
        System.out.println("\n========================================");
        System.out.println("   Banking System - CLI Interface       ");
        System.out.println("========================================\n");

        while (true) {
            if (currentUserId == null) {
                showAuthMenu();
            } else {
                showMainMenu();
            }
        }
    }

    private void showAuthMenu() {
        System.out.println("\n+-----------------------+");
        System.out.println("| Authentication Menu   |");
        System.out.println("| 1. Register           |");
        System.out.println("| 2. Login              |");
        System.out.println("| 3. Exit               |");
        System.out.println("+-----------------------+");
        System.out.print("Choose option: ");

        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1" ->
                register();
            case "2" ->
                login();
            case "3" ->
                System.exit(0);
            default ->
                System.out.println("❌ Invalid option!");
        }
    }

    private void showMainMenu() {
        System.out.println("\n+-------------------------------+");
        System.out.println("| Main Menu                     |");
        System.out.println("| 1. Create Account             |");
        System.out.println("| 2. View Accounts              |");
        System.out.println("| 3. Deposit Money              |");
        System.out.println("| 4. Withdraw Money             |");
        System.out.println("| 5. Transfer Money             |");
        System.out.println("| 6. View Transaction History   |");
        System.out.println("| 7. Logout                     |");
        System.out.println("+-------------------------------+");
        System.out.print("Choose option: ");

        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1" ->
                createAccount();
            case "2" ->
                viewAccounts();
            case "3" ->
                deposit();
            case "4" ->
                withdraw();
            case "5" ->
                transfer();
            case "6" ->
                viewTransactionHistory();
            case "7" ->
                logout();
            default ->
                System.out.println("❌ Invalid option!");
        }
    }

    private void register() {
        System.out.println("\n▶ Register New User");
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Phone: ");
        String phone = scanner.nextLine().trim();
        System.out.print("Address: ");
        String address = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        try {
            User user = new User(username, password, email, phone, address);
            userService.registerUser(user);
            System.out.println("✅ Registration successful!");
        } catch (Exception e) {
            System.out.println("❌ Registration failed: " + e.getMessage());
        }
    }

    private void login() {
        System.out.println("\n▶ Login");
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        try {
            Optional<User> userOptional = userService.loginUser(username, password);
            if (userOptional.isEmpty()) {
                System.out.println("❌ Invalid username or password!");
                return;
            }

            User user = userOptional.get();
            currentUserId = user.getUserId();
            currentUsername = user.getUsername();
            System.out.println("✅ Login successful!");
            System.out.println("Welcome, " + user.getUsername() + "!");
        } catch (Exception e) {
            System.out.println("❌ Login failed: " + e.getMessage());
        }
    }

    private void logout() {
        currentUserId = null;
        currentUsername = null;
        System.out.println("✅ Logged out successfully!");
    }

    private void createAccount() {
        System.out.println("\n▶ Create Account");
        System.out.print("Account Type (SAVINGS/CHECKING): ");
        String accountType = scanner.nextLine().trim().toUpperCase();
        System.out.print("Initial Balance: ");
        String balanceStr = scanner.nextLine().trim();

        try {
            BigDecimal initialBalance = new BigDecimal(balanceStr);
            Account account = accountService.createAccount(currentUserId, accountType, initialBalance);
            System.out.println("✅ Account created successfully!");
            System.out.println("Account ID: " + account.getAccountId());
            System.out.println("Account Number: " + account.getAccountNumber());
            System.out.println("Balance: " + account.getBalance());
        } catch (Exception e) {
            System.out.println("❌ Account creation failed: " + e.getMessage());
        }
    }

    private void viewAccounts() {
        System.out.println("\n▶ Your Accounts");
        try {
            List<Account> accounts = accountService.getUserAccounts(currentUserId);
            if (accounts.isEmpty()) {
                System.out.println("No accounts found.");
                return;
            }

            System.out.println("+------------------------------------------------+");
            for (Account account : accounts) {
                System.out.println("| ID: " + account.getAccountId());
                System.out.println("| Number: " + account.getAccountNumber());
                System.out.println("| Type: " + account.getAccountType());
                System.out.println("| Balance: " + account.getBalance());
                System.out.println("+------------------------------------------------+");
            }
        } catch (Exception e) {
            System.out.println("❌ Failed to fetch accounts: " + e.getMessage());
        }
    }

    private void deposit() {
        System.out.println("\n▶ Deposit Money");
        System.out.print("Account Number: ");
        String accountNumber = scanner.nextLine().trim();
        System.out.print("Amount: ");
        String amountStr = scanner.nextLine().trim();

        try {
            BigDecimal amount = new BigDecimal(amountStr);
            transactionService.deposit(accountNumber, amount);
            System.out.println("✅ Deposit successful!");
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid amount format!");
        } catch (Exception e) {
            System.out.println("❌ Deposit failed: " + e.getMessage());
        }
    }

    private void withdraw() {
        System.out.println("\n▶ Withdraw Money");
        System.out.print("Account Number: ");
        String accountNumber = scanner.nextLine().trim();
        System.out.print("Amount: ");
        String amountStr = scanner.nextLine().trim();

        try {
            BigDecimal amount = new BigDecimal(amountStr);
            transactionService.withdraw(accountNumber, amount);
            System.out.println("✅ Withdrawal successful!");
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid amount format!");
        } catch (Exception e) {
            System.out.println("❌ Withdrawal failed: " + e.getMessage());
        }
    }

    private void transfer() {
        System.out.println("\n▶ Transfer Money");
        System.out.print("From Account Number: ");
        String fromAccountNumber = scanner.nextLine().trim();
        System.out.print("To Account Number: ");
        String toAccountNumber = scanner.nextLine().trim();
        System.out.print("Amount: ");
        String amountStr = scanner.nextLine().trim();

        try {
            BigDecimal amount = new BigDecimal(amountStr);
            transactionService.transfer(fromAccountNumber, toAccountNumber, amount);
            System.out.println("✅ Transfer successful!");
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid amount format!");
        } catch (Exception e) {
            System.out.println("❌ Transfer failed: " + e.getMessage());
        }
    }

    private void viewTransactionHistory() {
        System.out.println("\n▶ Transaction History");
        System.out.print("Account Number: ");
        String accountNumber = scanner.nextLine().trim();

        try {
            List<Transaction> transactions = transactionService.getTransactionHistory(accountNumber);
            if (transactions.isEmpty()) {
                System.out.println("No transactions found.");
                return;
            }

            System.out.println("+-----------------------------------------------------------+");
            System.out.println("| Transactions:");
            for (Transaction transaction : transactions) {
                System.out.println("+---------------------------------------------------------+");
                System.out.println("| ID: " + transaction.getTransactionId());
                System.out.println("| Type: " + transaction.getTransactionType());
                System.out.println("| Amount: " + transaction.getAmount());
                System.out.println("| Date: " + transaction.getTimestamp());
            }
            System.out.println("+-----------------------------------------------------------+");
        } catch (Exception e) {
            System.out.println("❌ Failed to fetch transactions: " + e.getMessage());
        }
    }
}
