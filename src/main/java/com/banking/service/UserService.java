package com.banking.service;

import com.banking.model.User;
import com.banking.repository.UserRepository;
import com.banking.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service layer for User authentication and management Uses Spring Data JPA
 * instead of manual JDBC
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Register a new user
     */
    public User registerUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        // Encode password using BCrypt
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    /**
     * Authenticate user login
     */
    public Optional<User> loginUser(String username, String password) {
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // Verify password using BCrypt
            if (passwordEncoder.matches(password, user.getPassword())) {
                return userOptional;
            }
        }

        return Optional.empty();
    }

    /**
     * Get user by user ID
     */
    @Transactional(readOnly = true)
    public Optional<User> getUserById(Integer userId) {
        return userRepository.findById(userId);
    }

    /**
     * Get user by username
     */
    @Transactional(readOnly = true)
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Find user by username (alias for getUserByUsername)
     */
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Update user profile
     */
    public User updateUserProfile(Integer userId, String email, String phone, String address) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setEmail(email);
        user.setPhone(phone);
        user.setAddress(address);

        return userRepository.save(user);
    }

    /**
     * Change user password
     */
    public void changePassword(Integer userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Verify old password
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Old password is incorrect");
        }

        // Encode and set new password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    /**
     * Convert User entity to UserResponse DTO
     */
    public UserResponse convertToResponse(User user) {
        return new UserResponse(
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                user.getPhone(),
                user.getAddress(),
                user.getCreatedDate()
        );
    }
}
