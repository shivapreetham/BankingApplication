package com.banking.controller;

import com.banking.model.Account;
import com.banking.model.User;
import com.banking.service.AccountService;
import com.banking.service.UserService;
import com.banking.dto.AccountRequest;
import com.banking.dto.AccountResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Account Management REST Controller Endpoints: GET /accounts, POST /accounts,
 * GET /accounts/{accountNumber}, DELETE /accounts/{accountNumber}
 */
@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final UserService userService;

    /**
     * Get all accounts for authenticated user GET /accounts
     */
    @GetMapping
    public ResponseEntity<List<AccountResponse>> getUserAccounts(Authentication authentication) {
        String username = authentication.getName();
        Optional<User> user = userService.findByUsername(username);

        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<Account> accounts = accountService.getUserAccounts(user.get().getUserId());
        List<AccountResponse> responses = accounts.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    /**
     * Create new account for authenticated user POST /accounts
     */
    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(
            @Valid @RequestBody AccountRequest request,
            Authentication authentication) {

        String username = authentication.getName();
        Optional<User> user = userService.findByUsername(username);

        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Account account = new Account();
        account.setUser(user.get());
        account.setAccountType(request.getAccountType());
        account.setBalance(request.getInitialBalance());
        account.setStatus("ACTIVE");

        Account createdAccount = accountService.createAccount(account);

        return ResponseEntity.status(HttpStatus.CREATED).body(convertToResponse(createdAccount));
    }

    /**
     * Get account details by account number GET /accounts/{accountNumber}
     */
    @GetMapping("/{accountNumber}")
    public ResponseEntity<AccountResponse> getAccount(
            @PathVariable String accountNumber,
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

        return ResponseEntity.ok(convertToResponse(account.get()));
    }

    /**
     * Close account DELETE /accounts/{accountNumber}
     */
    @DeleteMapping("/{accountNumber}")
    public ResponseEntity<Void> closeAccount(
            @PathVariable String accountNumber,
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

        accountService.closeAccount(account.get().getAccountId());
        return ResponseEntity.noContent().build();
    }

    /**
     * Convert Account to AccountResponse DTO
     */
    private AccountResponse convertToResponse(Account account) {
        return AccountResponse.builder()
                .accountId(account.getAccountId())
                .accountNumber(account.getAccountNumber())
                .accountType(account.getAccountType())
                .balance(account.getBalance())
                .status(account.getStatus())
                .createdDate(account.getCreatedDate())
                .lastModifiedDate(account.getLastModifiedDate())
                .build();
    }
}
