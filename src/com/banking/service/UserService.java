package com.banking.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.banking.database.DatabaseConnection;
import com.banking.model.User;

/**
 * Service layer for User authentication and management
 */
public class UserService {

    /**
     * Register a new user
     */
    public boolean registerUser(User user) {
        // Check if username already exists
        if (usernameExists(user.getUsername())) {
            System.out.println("Username already exists. Please choose a different username.");
            return false;
        }

        // Check if email already exists
        if (emailExists(user.getEmail())) {
            System.out.println("Email already registered. Please use a different email.");
            return false;
        }

        String sql = "INSERT INTO users (username, password, email, phone, address, created_date) VALUES (?, ?, ?, ?, ?, NOW())";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, hashPassword(user.getPassword()));
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPhone());
            stmt.setString(5, user.getAddress());

            int result = stmt.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            System.err.println("Error registering user: " + e.getMessage());
            return false;
        }
    }

    /**
     * Authenticate user login
     */
    public User loginUser(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");

                // Verify password
                if (verifyPassword(password, storedPassword)) {
                    User user = new User();
                    user.setUserId(rs.getInt("user_id"));
                    user.setUsername(rs.getString("username"));
                    user.setEmail(rs.getString("email"));
                    user.setPhone(rs.getString("phone"));
                    user.setAddress(rs.getString("address"));
                    user.setCreatedDate(new Date(rs.getTimestamp("created_date").getTime()));

                    return user;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error logging in user: " + e.getMessage());
        }

        return null;
    }

    /**
     * Get user by user ID
     */
    public User getUserById(int userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setEmail(rs.getString("email"));
                user.setPhone(rs.getString("phone"));
                user.setAddress(rs.getString("address"));
                user.setCreatedDate(new Date(rs.getTimestamp("created_date").getTime()));

                return user;
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving user: " + e.getMessage());
        }

        return null;
    }

    /**
     * Check if username already exists
     */
    public boolean usernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.err.println("Error checking username: " + e.getMessage());
        }

        return false;
    }

    /**
     * Check if email already exists
     */
    public boolean emailExists(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.err.println("Error checking email: " + e.getMessage());
        }

        return false;
    }

    /**
     * Update user profile
     */
    public boolean updateUserProfile(User user) {
        String sql = "UPDATE users SET email = ?, phone = ?, address = ? WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getPhone());
            stmt.setString(3, user.getAddress());
            stmt.setInt(4, user.getUserId());

            int result = stmt.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            System.err.println("Error updating profile: " + e.getMessage());
            return false;
        }
    }

    /**
     * Change user password
     */
    public boolean changePassword(int userId, String oldPassword, String newPassword) {
        User user = getUserById(userId);

        if (user == null) {
            return false;
        }

        // Verify old password
        if (!verifyPassword(oldPassword, user.getPassword())) {
            System.out.println("Old password is incorrect.");
            return false;
        }

        String sql = "UPDATE users SET password = ? WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, hashPassword(newPassword));
            stmt.setInt(2, userId);

            int result = stmt.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            System.err.println("Error changing password: " + e.getMessage());
            return false;
        }
    }

    // Password Hashing and Verification (Simple implementation)
    // In production, use BCrypt or Argon2
    private String hashPassword(String password) {
        // Simple hash for demo purposes
        // In production: use BCrypt or Argon2
        return Integer.toString(password.hashCode());
    }

    private boolean verifyPassword(String inputPassword, String storedHash) {
        return hashPassword(inputPassword).equals(storedHash);
    }
}
