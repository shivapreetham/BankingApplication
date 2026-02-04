package com.banking.service;

import com.banking.model.Account;
import com.banking.model.Transaction;
import com.banking.repository.AccountRepository;
import com.banking.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service layer for Transaction operations
 */
@Service
@RequiredArgsConstructor
@Transactional
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    /**
     * Record a deposit transaction
     */
    public Transaction deposit(String accountNumber, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Deposit amount must be greater than zero");
        }

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        // Update balance
        BigDecimal newBalance = account.getBalance().add(amount);
        account.setBalance(newBalance);
        accountRepository.save(account);

        // Record transaction
        Transaction transaction = new Transaction(account, "DEPOSIT", amount, "Deposit");
        transaction.setBalanceAfter(newBalance);
        transaction.setStatus("SUCCESS");

        return transactionRepository.save(transaction);
    }

    /**
     * Record a deposit transaction (overloaded version accepting Account
     * object)
     */
    public Transaction deposit(Account account, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be greater than zero");
        }

        // Update balance
        BigDecimal newBalance = account.getBalance().add(amount);
        account.setBalance(newBalance);
        accountRepository.save(account);

        // Record transaction
        Transaction transaction = new Transaction(account, "DEPOSIT", amount, "Deposit");
        transaction.setBalanceAfter(newBalance);

        return transactionRepository.save(transaction);
    }

    /**
     * Record a withdrawal transaction
     */
    public Transaction withdraw(String accountNumber, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Withdrawal amount must be greater than zero");
        }

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (account.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance. Current balance: " + account.getBalance());
        }

        // Update balance
        BigDecimal newBalance = account.getBalance().subtract(amount);
        account.setBalance(newBalance);
        accountRepository.save(account);

        // Record transaction
        Transaction transaction = new Transaction(account, "WITHDRAWAL", amount, "Withdrawal");
        transaction.setBalanceAfter(newBalance);
        transaction.setStatus("SUCCESS");

        return transactionRepository.save(transaction);
    }

    /**
     * Record a withdrawal transaction (overloaded version accepting Account
     * object)
     */
    public Transaction withdraw(Account account, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be greater than zero");
        }

        if (account.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance. Current balance: " + account.getBalance());
        }

        // Update balance
        BigDecimal newBalance = account.getBalance().subtract(amount);
        account.setBalance(newBalance);
        accountRepository.save(account);

        // Record transaction
        Transaction transaction = new Transaction(account, "WITHDRAWAL", amount, "Withdrawal");
        transaction.setBalanceAfter(newBalance);

        return transactionRepository.save(transaction);
    }

    /**
     * Record a transfer transaction between accounts
     */
    public void transfer(String fromAccountNumber, String toAccountNumber, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Transfer amount must be greater than zero");
        }

        if (fromAccountNumber.equals(toAccountNumber)) {
            throw new RuntimeException("Cannot transfer to the same account");
        }

        Account fromAccount = accountRepository.findByAccountNumber(fromAccountNumber)
                .orElseThrow(() -> new RuntimeException("From account not found"));

        Account toAccount = accountRepository.findByAccountNumber(toAccountNumber)
                .orElseThrow(() -> new RuntimeException("To account not found"));

        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance. Current balance: " + fromAccount.getBalance());
        }

        // Update both accounts
        BigDecimal newFromBalance = fromAccount.getBalance().subtract(amount);
        BigDecimal newToBalance = toAccount.getBalance().add(amount);

        fromAccount.setBalance(newFromBalance);
        toAccount.setBalance(newToBalance);

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        // Record transactions
        Transaction fromTransaction = new Transaction(
                fromAccount,
                "TRANSFER",
                amount,
                "Transfer to " + toAccountNumber
        );
        fromTransaction.setBalanceAfter(newFromBalance);
        fromTransaction.setStatus("SUCCESS");

        Transaction toTransaction = new Transaction(
                toAccount,
                "TRANSFER",
                amount,
                "Transfer from " + fromAccountNumber
        );
        toTransaction.setBalanceAfter(newToBalance);
        toTransaction.setStatus("SUCCESS");

        transactionRepository.save(fromTransaction);
        transactionRepository.save(toTransaction);
    }

    /**
     * Record a transfer transaction between accounts (overloaded version
     * accepting Account objects)
     */
    public Transaction transfer(Account fromAccount, Account toAccount, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be greater than zero");
        }

        if (fromAccount.getAccountId().equals(toAccount.getAccountId())) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }

        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance. Current balance: " + fromAccount.getBalance());
        }

        // Update both accounts
        BigDecimal newFromBalance = fromAccount.getBalance().subtract(amount);
        BigDecimal newToBalance = toAccount.getBalance().add(amount);

        fromAccount.setBalance(newFromBalance);
        toAccount.setBalance(newToBalance);

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        // Record transaction for source account
        Transaction transaction = new Transaction(
                fromAccount,
                "TRANSFER",
                amount,
                "Transfer to " + toAccount.getAccountNumber()
        );
        transaction.setBalanceAfter(newFromBalance);

        return transactionRepository.save(transaction);
    }

    /**
     * Get transaction history for an account (last 50 transactions)
     */
    @Transactional(readOnly = true)
    public List<Transaction> getTransactionHistory(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        Pageable pageable = PageRequest.of(0, 50);
        return transactionRepository.findByAccountOrderByTimestampDesc(account, pageable);
    }

    /**
     * Get transaction history for an account with pagination
     */
    @Transactional(readOnly = true)
    public Page<Transaction> getTransactionHistory(Account account, Pageable pageable) {
        return transactionRepository.findByAccountOrderByTimestampDesc(account, pageable);
    }

    /**
     * Get all transactions for an account
     */
    @Transactional(readOnly = true)
    public List<Transaction> getAllTransactions(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        return transactionRepository.findByAccount(account);
    }
}
