package com.banking.cli;

import com.banking.dto.AccountRequest;
import com.banking.dto.AccountResponse;
import com.banking.dto.LoginRequest;
import com.banking.dto.RegisterRequest;
import com.banking.dto.TransactionRequest;
import com.banking.service.AccountService;
import com.banking.service.TransactionService;
import com.banking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

/**
 * Interactive CLI Interface for Banking System
 * Run with: java -jar app.jar --spring.profiles.active=cli
 */
@Component
@Profile("cli")
@RequiredArgsConstructor
public class BankingCLI implements CommandLineRunner {

    private final UserService userService;
    private final AccountService accountService;
    private final TransactionService transactionService;
    private final Scanner scanner = new Scanner(System.in);
    
    private String currentUserEmail = null;
    private String currentUserToken = null;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║   Banking System - CLI Interface       ║");
        System.out.println("╚════════════════════════════════════════╝\n");

        boolean running = true;
        while (running) {
            if (currentUserEmail == null) {
                showAuthMenu();
            } else {
                showMainMenu();
            }
        }
        scanner.close();
    }

    private void showAuthMenu() {
        System.out.println("\n┌─ Authentication Menu ─┐");
        System.out.println("│ 1. Register           │");
        System.out.println("│ 2. Login              │");
        System.out.println("│ 3. Exit               │");
        System.out.println("└───────────────────────┘");
        System.out.print("Choose option: ");

        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1" -> register();
            case "2" -> login();
            case "3" -> System.exit(0);
            default -> System.out.println("❌ Invalid option!");
        }
    }

    private void showMainMenu() {
        System.out.println("\n┌─ Main Menu ────────────────────┐");
        System.out.println("│ 1. Create Account              │");
        System.out.println("│ 2. View Accounts               │");
        System.out.println("│ 3. Deposit Money               │");
        System.out.println("│ 4. Withdraw Money              │");
        System.out.println("│ 5. Transfer Money              │");
        System.out.println("│ 6. View Transaction History    │");
        System.out.println("│ 7. Logout                      │");
        System.out.println("└────────────────────────────────┘");
        System.out.print("Choose option: ");

        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1" -> createAccount();
            case "2" -> viewAccounts();
            case "3" -> deposit();
            case "4" -> withdraw();
            case "5" -> transfer();
            case "6" -> viewTransactionHistory();
            case "7" -> logout();
            default -> System.out.println("❌ Invalid option!");
        }
    }

    private void register() {
        System.out.println("\n▶ Register New User");
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Full Name: ");
        String fullName = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        try {
            RegisterRequest request = new RegisterRequest();
            request.setEmail(email);
            request.setFullName(fullName);
            request.setPassword(password);
            
            userService.registerUser(request);
            System.out.println("✅ Registration successful!");
        } catch (Exception e) {
            System.out.println("❌ Registration failed: " + e.getMessage());
        }
    }

    private void login() {
        System.out.println("\n▶ Login");
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        try {
            LoginRequest request = new LoginRequest();
            request.setEmail(email);
            request.setPassword(password);
            
            var response = userService.loginUser(request);
            currentUserEmail = email;
            currentUserToken = response.getToken();
            System.out.println("✅ Login successful!");
            System.out.println("Welcome, " + response.getFullName() + "!");
        } catch (Exception e) {
            System.out.println("❌ Login failed: " + e.getMessage());
        }
    }

    private void logout() {
        currentUserEmail = null;
        currentUserToken = null;
        System.out.println("✅ Logged out successfully!");
    }

    private void createAccount() {
        System.out.println("\n▶ Create Account");
        System.out.print("Account Type (SAVINGS/CHECKING): ");
        String accountType = scanner.nextLine().trim().toUpperCase();
        System.out.print("Account Name: ");
        String accountName = scanner.nextLine().trim();
        System.out.print("Initial Balance: ");
        String balanceStr = scanner.nextLine().trim();

        try {
            AccountRequest request = new AccountRequest();
            request.setAccountName(accountName);
            request.setAccountType(accountType);
            request.setBalance(new BigDecimal(balanceStr));
            
            AccountResponse response = accountService.createAccount(currentUserEmail, request);
            System.out.println("✅ Account created successfully!");
            System.out.println("Account ID: " + response.getId());
            System.out.println("Balance: " + response.getBalance());
        } catch (Exception e) {
            System.out.println("❌ Account creation failed: " + e.getMessage());
        }
    }

    private void viewAccounts() {
        System.out.println("\n▶ Your Accounts");
        try {
            List<AccountResponse> accounts = accountService.getUserAccounts(currentUserEmail);
            if (accounts.isEmpty()) {
                System.out.println("No accounts found.");
                return;
            }
            
            System.out.println("┌────────────────────────────────────────────────┐");
            for (AccountResponse account : accounts) {
                System.out.println("│ ID: " + account.getId());
                System.out.println("│ Name: " + account.getAccountName());
                System.out.println("│ Type: " + account.getAccountType());
                System.out.println("│ Balance: " + account.getBalance());
                System.out.println("├────────────────────────────────────────────────┤");
            }
            System.out.println("└────────────────────────────────────────────────┘");
        } catch (Exception e) {
            System.out.println("❌ Failed to fetch accounts: " + e.getMessage());
        }
    }

    private void deposit() {
        System.out.println("\n▶ Deposit Money");
        System.out.print("Account ID: ");
        String accountIdStr = scanner.nextLine().trim();
        System.out.print("Amount: ");
        String amountStr = scanner.nextLine().trim();

        try {
            Long accountId = Long.parseLong(accountIdStr);
            BigDecimal amount = new BigDecimal(amountStr);
            
            accountService.deposit(accountId, amount, currentUserEmail);
            System.out.println("✅ Deposit successful!");
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid input format!");
        } catch (Exception e) {
            System.out.println("❌ Deposit failed: " + e.getMessage());
        }
    }

    private void withdraw() {
        System.out.println("\n▶ Withdraw Money");
        System.out.print("Account ID: ");
        String accountIdStr = scanner.nextLine().trim();
        System.out.print("Amount: ");
        String amountStr = scanner.nextLine().trim();

        try {
            Long accountId = Long.parseLong(accountIdStr);
            BigDecimal amount = new BigDecimal(amountStr);
            
            accountService.withdraw(accountId, amount, currentUserEmail);
            System.out.println("✅ Withdrawal successful!");
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid input format!");
        } catch (Exception e) {
            System.out.println("❌ Withdrawal failed: " + e.getMessage());
        }
    }

    private void transfer() {
        System.out.println("\n▶ Transfer Money");
        System.out.print("From Account ID: ");
        String fromIdStr = scanner.nextLine().trim();
        System.out.print("To Account ID: ");
        String toIdStr = scanner.nextLine().trim();
        System.out.print("Amount: ");
        String amountStr = scanner.nextLine().trim();

        try {
            Long fromId = Long.parseLong(fromIdStr);
            Long toId = Long.parseLong(toIdStr);
            BigDecimal amount = new BigDecimal(amountStr);
            
            TransactionRequest request = new TransactionRequest();
            request.setFromAccountId(fromId);
            request.setToAccountId(toId);
            request.setAmount(amount);
            request.setDescription("Transfer via CLI");
            
            transactionService.transferMoney(request, currentUserEmail);
            System.out.println("✅ Transfer successful!");
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid input format!");
        } catch (Exception e) {
            System.out.println("❌ Transfer failed: " + e.getMessage());
        }
    }

    private void viewTransactionHistory() {
        System.out.println("\n▶ Transaction History");
        System.out.print("Account ID: ");
        String accountIdStr = scanner.nextLine().trim();

        try {
            Long accountId = Long.parseLong(accountIdStr);
            var transactions = transactionService.getAccountTransactions(accountId, 0, 10);
            
            if (transactions.isEmpty()) {
                System.out.println("No transactions found.");
                return;
            }
            
            System.out.println("┌───────────────────────────────────────────────────────────┐");
            System.out.println("│ Transactions:");
            for (var transaction : transactions) {
                System.out.println("├─────────────────────────────────────────────────────────┤");
                System.out.println("│ ID: " + transaction.getId());
                System.out.println("│ Type: " + transaction.getType());
                System.out.println("│ Amount: " + transaction.getAmount());
                System.out.println("│ Date: " + transaction.getTransactionDate());
            }
            System.out.println("└───────────────────────────────────────────────────────────┘");
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid account ID!");
        } catch (Exception e) {
            System.out.println("❌ Failed to fetch transactions: " + e.getMessage());
        }
    }
}
