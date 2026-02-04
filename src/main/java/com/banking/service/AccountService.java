package com.banking.service;

import com.banking.model.Account;
import com.banking.model.User;
import com.banking.repository.AccountRepository;
import com.banking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Service layer for Account operations
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    /**
     * Create a new account
     */
    public Account createAccount(Integer userId, String accountType, BigDecimal initialBalance) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String accountNumber = generateAccountNumber();

        Account account = new Account(
                user,
                accountNumber,
                accountType,
                initialBalance != null ? initialBalance : BigDecimal.ZERO
        );

        return accountRepository.save(account);
    }

    /**
     * Create account from Account entity (overloaded)
     */
    public Account createAccount(Account account) {
        account.setAccountNumber(generateAccountNumber());
        if (account.getBalance() == null) {
            account.setBalance(BigDecimal.ZERO);
        }
        if (account.getStatus() == null) {
            account.setStatus("ACTIVE");
        }
        return accountRepository.save(account);
    }

    /**
     * Get account by account number
     */
    @Transactional(readOnly = true)
    public Optional<Account> getAccount(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber);
    }

    /**
     * Get account by account number (alias)
     */
    @Transactional(readOnly = true)
    public Optional<Account> getAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber);
    }

    /**
     * Get all active accounts for a user
     */
    @Transactional(readOnly = true)
    public List<Account> getUserAccounts(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return accountRepository.findByUserAndStatus(user, "ACTIVE");
    }

    /**
     * Update account balance
     */
    public Account updateAccountBalance(String accountNumber, BigDecimal newBalance) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        account.setBalance(newBalance);
        return accountRepository.save(account);
    }

    /**
     * Close account (soft delete)
     */
    public void closeAccount(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        account.setStatus("CLOSED");
        accountRepository.save(account);
    }

    /**
     * Close account by ID
     */
    public void closeAccount(Integer accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        account.setStatus("CLOSED");
        accountRepository.save(account);
    }

    /**
     * Check if account exists and is active
     */
    @Transactional(readOnly = true)
    public boolean accountExists(String accountNumber) {
        Optional<Account> account = accountRepository.findByAccountNumber(accountNumber);
        return account.isPresent() && "ACTIVE".equals(account.get().getStatus());
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
