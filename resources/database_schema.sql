-- Banking System Database Schema
-- Create database
CREATE DATABASE IF NOT EXISTS banking_system;
USE banking_system;

-- Users table
CREATE TABLE IF NOT EXISTS users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20),
    address VARCHAR(255),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Accounts table
CREATE TABLE IF NOT EXISTS accounts (
    account_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    account_number VARCHAR(20) UNIQUE NOT NULL,
    account_type VARCHAR(50) NOT NULL, -- SAVINGS, CHECKING, etc.
    balance DECIMAL(15, 2) DEFAULT 0,
    currency VARCHAR(3) DEFAULT 'USD',
    status VARCHAR(20) DEFAULT 'ACTIVE', -- ACTIVE, CLOSED, FROZEN
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Transactions table
CREATE TABLE IF NOT EXISTS transactions (
    transaction_id INT PRIMARY KEY AUTO_INCREMENT,
    account_id INT NOT NULL,
    transaction_type VARCHAR(50) NOT NULL, -- DEPOSIT, WITHDRAWAL, TRANSFER
    amount DECIMAL(15, 2) NOT NULL,
    balance_after DECIMAL(15, 2),
    description VARCHAR(255),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) DEFAULT 'SUCCESS', -- SUCCESS, PENDING, FAILED
    FOREIGN KEY (account_id) REFERENCES accounts(account_id) ON DELETE CASCADE,
    INDEX idx_account_id (account_id),
    INDEX idx_timestamp (timestamp)
);

-- Transfer table (to track transfers between accounts)
CREATE TABLE IF NOT EXISTS transfers (
    transfer_id INT PRIMARY KEY AUTO_INCREMENT,
    from_account_id INT NOT NULL,
    to_account_id INT NOT NULL,
    amount DECIMAL(15, 2) NOT NULL,
    description VARCHAR(255),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) DEFAULT 'SUCCESS',
    FOREIGN KEY (from_account_id) REFERENCES accounts(account_id) ON DELETE CASCADE,
    FOREIGN KEY (to_account_id) REFERENCES accounts(account_id) ON DELETE CASCADE
);

-- Create indexes for better performance
CREATE INDEX idx_user_id ON accounts(user_id);
CREATE INDEX idx_account_number ON accounts(account_number);
CREATE INDEX idx_username ON users(username);

-- Sample Data (Optional)
-- You can uncomment these to add sample data for testing

-- INSERT INTO users (username, password, email, phone, address) 
-- VALUES ('john_doe', 'password123', 'john@example.com', '1234567890', '123 Main St');

-- INSERT INTO accounts (user_id, account_number, account_type, balance)
-- VALUES (1, 'ACC000001', 'SAVINGS', 5000);
