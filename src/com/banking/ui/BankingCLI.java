package com.banking.ui;

import com.banking.service.UserService;
import com.banking.service.AccountService;
import com.banking.service.TransactionService;
import com.banking.model.User;
import com.banking.model.Account;
import com.banking.model.Transaction;
import java.util.Scanner;
import java.util.List;

/**
 * Command Line Interface for Banking System
 */
public class BankingCLI {

    private Scanner scanner;
    private UserService userService;
    private AccountService accountService;
    private TransactionService transactionService;
    private User currentUser;
    private Account currentAccount;
    
    /**
     * Constructor Initializes all services and input scanner
     */
    public BankingCLI() {
        this.scanner = new Scanner(System.in);
        this.userService = new UserService();
        this.accountService = new AccountService();
        this.transactionService = new TransactionService();
        this.currentUser = null;
        this.currentAccount = null;
    }   

    /**
     * Start the banking application
     */
    public void start() {
        System.out.println("========================================");
        System.out.println("   Welcome to Banking System");
        System.out.println("========================================");

        boolean running = true;
        while (running) {
            if (currentUser == null) {
                showAuthenticationMenu();
            } else {
                showMainMenu();
            }
        }
        scanner.close();
    }

    /**
     * Display authentication menu (Login/Register)
     */
    private void showAuthenticationMenu() {
        System.out.println("\n--- Authentication Menu ---");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("3. Exit");
        System.out.print("Choose an option: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                login();
                break;
            case 2:
                register();
                break;
            case 3:
                exit();
                break;
            default:
                System.out.println("Invalid option!");
        }
    }

    /**
     * Display main menu after login
     */
    private void showMainMenu() {
        System.out.println("\n--- Main Menu ---");
        System.out.println("1. View Accounts");
        System.out.println("2. Deposit Money");
        System.out.println("3. Withdraw Money");
        System.out.println("4. Transfer Money");
        System.out.println("5. Transaction History");
        System.out.println("6. Create New Account");
        System.out.println("7. View Profile");
        System.out.println("8. Logout");
        System.out.print("Choose an option: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                viewAccounts();
                break;
            case 2:
                depositMoney();
                break;
            case 3:
                withdrawMoney();
                break;
            case 4:
                transferMoney();
                break;
            case 5:
                viewTransactionHistory();
                break;
            case 6:
                createNewAccount();
                break;
            case 7:
                viewProfile();
                break;
            case 8:
                logout();
                break;
            default:
                System.out.println("Invalid option!");
        }
    }

    /**
     * Handle user login
     */
    private void login() {
        System.out.println("\n--- Login ---");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        currentUser = userService.loginUser(username, password);
        if (currentUser != null) {
            System.out.println("✓ Login successful! Welcome, " + currentUser.getUsername());
        } else {
            System.out.println("✗ Invalid username or password. Please try again.");
        }
    }

    /**
     * Handle user registration
     */
    private void register() {
        System.out.println("\n--- Register ---");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Phone: ");
        String phone = scanner.nextLine();
        System.out.print("Address: ");
        String address = scanner.nextLine();
        
        User newUser = new User(username, password, email, phone, address);
        
        if (userService.registerUser(newUser)) {
            System.out.println("✓ Registration successful! You can now login.");
        } else {
            System.out.println("✗ Registration failed. Please try again.");
        }
    }

    /**
     * Logout user
     */
    private void logout() {
        System.out.println("Logging out...");
        currentUser = null;
        currentAccount = null;
    }

    /**
     * Exit application
     */
    private void exit() {
        System.out.println("Thank you for using Banking System. Goodbye!");
        System.exit(0);
    }

    /**
     * View all accounts of current user
     */
    private void viewAccounts() {
        System.out.println("\n--- Your Accounts ---");
        List<Account> accounts = accountService.getAllUserAccounts(currentUser.getUserId());

        if (accounts.isEmpty()) {
            System.out.println("You have no accounts. Create one to get started.");
            return;
        }

        for (int i = 0; i < accounts.size(); i++) {
            Account acc = accounts.get(i);
            System.out.println((i + 1) + ". " + acc.getAccountNumber() + " (" + acc.getAccountType() + 
                             ") - Balance: $" + String.format("%.2f", acc.getBalance()));
        }

        System.out.print("\nSelect account number to view details (0 to go back): ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice > 0 && choice <= accounts.size()) {
            currentAccount = accounts.get(choice - 1);
            displayAccountDetails(currentAccount);
        }
    }

    /**
     * Display account details
     */
    private void displayAccountDetails(Account account) {
        System.out.println("\n--- Account Details ---");
        System.out.println("Account Number: " + account.getAccountNumber());
        System.out.println("Account Type: " + account.getAccountType());
        System.out.println("Balance: $" + String.format("%.2f", account.getBalance()));
        System.out.println("Currency: " + account.getCurrency());
        System.out.println("Status: " + account.getStatus());
        System.out.println("Created Date: " + account.getCreatedDate());
    }

    /**
     * Create new account
     */
    private void createNewAccount() {
        System.out.println("\n--- Create New Account ---");
        System.out.println("1. SAVINGS");
        System.out.println("2. CHECKING");
        System.out.println("3. MONEY MARKET");
        System.out.print("Select account type: ");
        
        int typeChoice = scanner.nextInt();
        scanner.nextLine();
        
        String accountType;
        switch (typeChoice) {
            case 1:
                accountType = "SAVINGS";
                break;
            case 2:
                accountType = "CHECKING";
                break;
            case 3:
                accountType = "MONEY MARKET";
                break;
            default:
                System.out.println("Invalid account type.");
                return;
        }

        System.out.print("Initial deposit (enter 0 for no deposit): $");
        double initialDeposit = scanner.nextDouble();
        scanner.nextLine();

        Account newAccount = new Account(currentUser.getUserId(), "", accountType, initialDeposit);
        
        if (accountService.createAccount(newAccount)) {
            System.out.println("✓ Account created successfully!");
            System.out.println("Account Number: " + newAccount.getAccountNumber());
        } else {
            System.out.println("✗ Failed to create account.");
        }
    }

    /**
     * View user profile
     */
    private void viewProfile() {
        System.out.println("\n--- Your Profile ---");
        System.out.println("Username: " + currentUser.getUsername());
        System.out.println("Email: " + currentUser.getEmail());
        System.out.println("Phone: " + currentUser.getPhone());
        System.out.println("Address: " + currentUser.getAddress());
        System.out.println("Member Since: " + currentUser.getCreatedDate());

        System.out.println("\n1. Edit Profile");
        System.out.println("2. Change Password");
        System.out.println("3. Go Back");
        System.out.print("Choose an option: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                editProfile();
                break;
            case 2:
                changePassword();
                break;
            case 3:
                break;
            default:
                System.out.println("Invalid option!");
        }
    }

    /**
     * Edit user profile
     */
    private void editProfile() {
        System.out.println("\n--- Edit Profile ---");
        System.out.print("New Email: ");
        String email = scanner.nextLine();
        System.out.print("New Phone: ");
        String phone = scanner.nextLine();
        System.out.print("New Address: ");
        String address = scanner.nextLine();

        currentUser.setEmail(email);
        currentUser.setPhone(phone);
        currentUser.setAddress(address);

        if (userService.updateUserProfile(currentUser)) {
            System.out.println("✓ Profile updated successfully!");
        } else {
            System.out.println("✗ Failed to update profile.");
        }
    }

    /**
     * Change password
     */
    private void changePassword() {
        System.out.println("\n--- Change Password ---");
        System.out.print("Old Password: ");
        String oldPassword = scanner.nextLine();
        System.out.print("New Password: ");
        String newPassword = scanner.nextLine();
        System.out.print("Confirm Password: ");
        String confirmPassword = scanner.nextLine();

        if (!newPassword.equals(confirmPassword)) {
            System.out.println("✗ Passwords do not match.");
            return;
        }

        if (userService.changePassword(currentUser.getUserId(), oldPassword, newPassword)) {
            System.out.println("✓ Password changed successfully!");
        } else {
            System.out.println("✗ Failed to change password.");
        }
    }

    /**
     * Handle deposit operation
     */
    private void depositMoney() {
        System.out.println("\n--- Deposit Money ---");
        if (currentAccount == null) {
            selectAccount();
            if (currentAccount == null) return;
        }

        System.out.print("Enter amount to deposit: $");
        double amount = scanner.nextDouble();
        scanner.nextLine();

        if (transactionService.deposit(currentAccount.getAccountNumber(), amount)) {
            System.out.println("✓ Deposit successful!");
            Account updated = accountService.getAccount(currentAccount.getAccountNumber());
            if (updated != null) {
                currentAccount = updated;
                System.out.println("New Balance: $" + String.format("%.2f", currentAccount.getBalance()));
            }
        } else {
            System.out.println("✗ Deposit failed.");
        }
    }

    /**
     * Handle withdrawal operation
     */
    private void withdrawMoney() {
        System.out.println("\n--- Withdraw Money ---");
        if (currentAccount == null) {
            selectAccount();
            if (currentAccount == null) return;
        }

        System.out.print("Enter amount to withdraw: $");
        double amount = scanner.nextDouble();
        scanner.nextLine();

        if (transactionService.withdraw(currentAccount.getAccountNumber(), amount)) {
            System.out.println("✓ Withdrawal successful!");
            Account updated = accountService.getAccount(currentAccount.getAccountNumber());
            if (updated != null) {
                currentAccount = updated;
                System.out.println("New Balance: $" + String.format("%.2f", currentAccount.getBalance()));
            }
        } else {
            System.out.println("✗ Withdrawal failed.");
        }
    }

    /**
     * Handle transfer operation
     */
    private void transferMoney() {
        System.out.println("\n--- Transfer Money ---");
        if (currentAccount == null) {
            selectAccount();
            if (currentAccount == null) return;
        }

        System.out.print("Enter recipient account number: ");
        String toAccount = scanner.nextLine();
        
        System.out.print("Enter amount to transfer: $");
        double amount = scanner.nextDouble();
        scanner.nextLine();

        if (transactionService.transfer(currentAccount.getAccountNumber(), toAccount, amount)) {
            System.out.println("✓ Transfer successful!");
            Account updated = accountService.getAccount(currentAccount.getAccountNumber());
            if (updated != null) {
                currentAccount = updated;
                System.out.println("New Balance: $" + String.format("%.2f", currentAccount.getBalance()));
            }
        } else {
            System.out.println("✗ Transfer failed.");
        }
    }

    /**
     * View transaction history
     */
    private void viewTransactionHistory() {
        System.out.println("\n--- Transaction History ---");
        if (currentAccount == null) {
            selectAccount();
            if (currentAccount == null) return;
        }

        List<Transaction> transactions = transactionService.getTransactionHistory(currentAccount.getAccountNumber());

        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }

        System.out.println("\nRecent Transactions:");
        System.out.println("---------------------------------------");
        for (Transaction t : transactions) {
            System.out.println(t.getTransactionType() + " | Amount: $" + String.format("%.2f", t.getAmount()) + 
                             " | Balance: $" + String.format("%.2f", t.getBalanceAfter()) + 
                             " | " + t.getTimestamp());
            System.out.println("  Description: " + t.getDescription());
        }
        System.out.println("---------------------------------------");
    }

    /**
     * Select an account from list
     */
    private void selectAccount() {
        System.out.println("\nSelect an account:");
        List<Account> accounts = accountService.getAllUserAccounts(currentUser.getUserId());

        if (accounts.isEmpty()) {
            System.out.println("You have no accounts. Create one first.");
            return;
        }

        for (int i = 0; i < accounts.size(); i++) {
            Account acc = accounts.get(i);
            System.out.println((i + 1) + ". " + acc.getAccountNumber() + " (" + acc.getAccountType() + ")");
        }

        System.out.print("Select account number: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice > 0 && choice <= accounts.size()) {
            currentAccount = accounts.get(choice - 1);
            System.out.println("Selected: " + currentAccount.getAccountNumber());
        }
    }
}
