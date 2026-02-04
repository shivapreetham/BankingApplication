package com.banking.controller;

import com.banking.model.Account;
import com.banking.model.Transaction;
import com.banking.model.User;
import com.banking.service.AccountService;
import com.banking.service.TransactionService;
import com.banking.service.UserService;
import com.banking.dto.TransactionRequest;
import com.banking.dto.TransactionResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Transaction Management REST Controller Endpoints: POST /transactions/deposit,
 * POST /transactions/withdraw, POST /transactions/transfer GET
 * /transactions/history/{accountNumber}
 */
@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final AccountService accountService;
    private final UserService userService;

    /**
     * Deposit money to account POST /transactions/deposit
     */
    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponse> deposit(
            @Valid @RequestBody TransactionRequest request,
            Authentication authentication) {

        Optional<Account> account = accountService.getAccountByNumber(request.getAccountNumber());

        if (account.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Verify ownership
        String username = authentication.getName();
        Optional<User> user = userService.findByUsername(username);

        if (user.isEmpty() || !account.get().getUser().getUserId().equals(user.get().getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            Transaction transaction = transactionService.deposit(account.get(), request.getAmount());
            return ResponseEntity.status(HttpStatus.CREATED).body(convertToResponse(transaction));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Withdraw money from account POST /transactions/withdraw
     */
    @PostMapping("/withdraw")
    public ResponseEntity<TransactionResponse> withdraw(
            @Valid @RequestBody TransactionRequest request,
            Authentication authentication) {

        Optional<Account> account = accountService.getAccountByNumber(request.getAccountNumber());

        if (account.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Verify ownership
        String username = authentication.getName();
        Optional<User> user = userService.findByUsername(username);

        if (user.isEmpty() || !account.get().getUser().getUserId().equals(user.get().getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            Transaction transaction = transactionService.withdraw(account.get(), request.getAmount());
            return ResponseEntity.status(HttpStatus.CREATED).body(convertToResponse(transaction));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Transfer money between accounts POST /transactions/transfer
     */
    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponse> transfer(
            @Valid @RequestBody TransactionRequest request,
            Authentication authentication) {

        Optional<Account> fromAccount = accountService.getAccountByNumber(request.getAccountNumber());
        Optional<Account> toAccount = accountService.getAccountByNumber(request.getToAccountNumber());

        if (fromAccount.isEmpty() || toAccount.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Verify ownership of source account
        String username = authentication.getName();
        Optional<User> user = userService.findByUsername(username);

        if (user.isEmpty() || !fromAccount.get().getUser().getUserId().equals(user.get().getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            Transaction transaction = transactionService.transfer(
                    fromAccount.get(),
                    toAccount.get(),
                    request.getAmount()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(convertToResponse(transaction));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get transaction history for account with pagination GET
     * /transactions/history/{accountNumber}?page=0&size=10
     */
    @GetMapping("/history/{accountNumber}")
    public ResponseEntity<?> getTransactionHistory(
            @PathVariable String accountNumber,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {

        Optional<Account> account = accountService.getAccountByNumber(accountNumber);

        if (account.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Verify ownership
        String username = authentication.getName();
        Optional<User> user = userService.findByUsername(username);

        if (user.isEmpty() || !account.get().getUser().getUserId().equals(user.get().getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Transaction> transactionPage = transactionService.getTransactionHistory(account.get(), pageable);

        return ResponseEntity.ok(transactionPage.map(this::convertToResponse));
    }

    /**
     * Convert Transaction to TransactionResponse DTO
     */
    private TransactionResponse convertToResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .transactionId(transaction.getTransactionId())
                .accountNumber(transaction.getAccount().getAccountNumber())
                .transactionType(transaction.getTransactionType())
                .amount(transaction.getAmount())
                .balanceAfter(transaction.getBalanceAfter())
                .description(transaction.getDescription())
                .timestamp(transaction.getTimestamp())
                .build();
    }
}
