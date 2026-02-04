package com.banking.controller;

import com.banking.model.User;
import com.banking.service.UserService;
import com.banking.dto.RegisterRequest;
import com.banking.dto.LoginRequest;
import com.banking.dto.AuthResponse;
import com.banking.security.JwtTokenProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Authentication REST Controller Endpoints: POST /auth/register, POST
 * /auth/login
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Register a new user POST /auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        try {
            User user = new User(
                    request.getUsername(),
                    request.getPassword(),
                    request.getEmail(),
                    request.getPhone(),
                    request.getAddress()
            );

            User registeredUser = userService.registerUser(user);

            String token = jwtTokenProvider.generateToken(registeredUser.getUserId(), registeredUser.getUsername());

            AuthResponse response = new AuthResponse(
                    token,
                    registeredUser.getUserId(),
                    registeredUser.getUsername(),
                    registeredUser.getEmail()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Login user POST /auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        Optional<User> user = userService.loginUser(request.getUsername(), request.getPassword());

        if (user.isPresent()) {
            User authenticatedUser = user.get();
            String token = jwtTokenProvider.generateToken(authenticatedUser.getUserId(), authenticatedUser.getUsername());

            AuthResponse response = new AuthResponse(
                    token,
                    authenticatedUser.getUserId(),
                    authenticatedUser.getUsername(),
                    authenticatedUser.getEmail()
            );

            return ResponseEntity.ok(response);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    /**
     * Validate token GET /auth/validate
     */
    @GetMapping("/validate/{token}")
    public ResponseEntity<Boolean> validateToken(@PathVariable String token) {
        boolean isValid = jwtTokenProvider.validateToken(token);
        return ResponseEntity.ok(isValid);
    }
}
