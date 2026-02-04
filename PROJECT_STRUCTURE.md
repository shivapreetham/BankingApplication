# Banking System - Project Structure

## Overview
A basic Java CLI banking application with MySQL database integration using JDBC.

## Project Architecture

```
BankingSystem/
├── src/
│   └── com/banking/
│       ├── Main.java                          (Entry point)
│       ├── model/
│       │   ├── User.java                      (User entity)
│       │   ├── Account.java                   (Account entity)
│       │   └── Transaction.java               (Transaction entity)
│       ├── service/
│       │   ├── UserService.java               (User business logic)
│       │   ├── AccountService.java            (Account business logic)
│       │   └── TransactionService.java        (Transaction business logic)
│       ├── database/
│       │   └── DatabaseConnection.java        (JDBC connection handler)
│       └── ui/
│           └── BankingCLI.java                (Command-line interface)
├── resources/
│   └── database_schema.sql                    (MySQL schema)
└── PROJECT_STRUCTURE.md                       (This file)
```

## Package Description

### `com.banking.model`
**Entity/Model Classes** - Represent database objects
- **User.java**: Represents a customer
  - Properties: userId, username, password, email, phone, address, createdDate
  
- **Account.java**: Represents a bank account
  - Properties: accountId, userId, accountNumber, accountType, balance, status, createdDate
  
- **Transaction.java**: Represents a transaction
  - Properties: transactionId, accountId, transactionType, amount, balanceAfter, timestamp, description

### `com.banking.service`
**Business Logic Layer** - Contains core application logic
- **UserService.java**: User management operations
  - registerUser()
  - loginUser()
  - getUserById()
  - usernameExists()
  - updateUserProfile()
  - changePassword()
  
- **AccountService.java**: Account management operations
  - createAccount()
  - getAccount()
  - getAllUserAccounts()
  - updateAccountBalance()
  - deleteAccount()
  - accountExists()
  
- **TransactionService.java**: Transaction operations
  - deposit()
  - withdraw()
  - transfer()
  - getTransactionHistory()
  - getTransaction()

### `com.banking.database`
**Database Layer** - Handles database connectivity
- **DatabaseConnection.java**: 
  - Manages JDBC MySQL connection
  - Connection pooling (basic singleton pattern)
  - Configure DB_URL, DB_USER, DB_PASSWORD here

### `com.banking.ui`
**Presentation Layer** - User interface
- **BankingCLI.java**: Command-line interface
  - Authentication menu (Login/Register)
  - Main menu (Account and transaction operations)
  - User input handling

### `Main.java`
**Entry Point** - Launches the application

## Database Schema

### Users Table
```
user_id (PK)
username (UNIQUE)
password
email (UNIQUE)
phone
address
created_date
updated_date
```

### Accounts Table
```
account_id (PK)
user_id (FK)
account_number (UNIQUE)
account_type
balance
currency
status
created_date
updated_date
```

### Transactions Table
```
transaction_id (PK)
account_id (FK)
transaction_type
amount
balance_after
description
timestamp
status
```

### Transfers Table
```
transfer_id (PK)
from_account_id (FK)
to_account_id (FK)
amount
description
timestamp
status
```

## Features to Implement

### User Management
- [x] Class structure created
- [ ] User Registration
- [ ] User Login/Authentication
- [ ] Profile Management
- [ ] Password Management

### Account Management
- [x] Class structure created
- [ ] Create Account
- [ ] View Account Details
- [ ] View All User Accounts
- [ ] Account Balance Update
- [ ] Account Deletion

### Transactions
- [x] Class structure created
- [ ] Deposit Money
- [ ] Withdraw Money
- [ ] Transfer Money (between accounts)
- [ ] Transaction History
- [ ] Transaction Status Tracking

### Security Considerations
- [ ] Password Hashing (BCrypt/Argon2)
- [ ] Input Validation
- [ ] SQL Injection Prevention (Use PreparedStatement)
- [ ] Session Management

## Setup Instructions

### Prerequisites
1. Java 8 or higher
2. MySQL Server
3. MySQL JDBC Driver (mysql-connector-java-*.jar)

### Database Setup
1. Open MySQL command line or MySQL Workbench
2. Run the SQL commands from `resources/database_schema.sql`
3. Verify tables are created

### JDBC Driver Setup
1. Download mysql-connector-java-*.jar
2. Add to your IDE's classpath/project build path
3. Update `DatabaseConnection.java` with your MySQL credentials

### Configuration
1. Edit [DatabaseConnection.java](BankingSystem/src/com/banking/database/DatabaseConnection.java)
   - Set DB_URL to your MySQL database URL
   - Set DB_USER to your MySQL username
   - Set DB_PASSWORD to your MySQL password

### Compilation
```bash
javac -d bin src/com/banking/**/*.java
```

### Execution
```bash
java -cp bin com.banking.Main
```

## Basic Operations Flow

```
Start Application
    ↓
Test Database Connection
    ↓
Show Authentication Menu (Login/Register)
    ↓
[After Login] Show Main Menu
    ├── View Accounts
    ├── Deposit Money → Update Account Balance
    ├── Withdraw Money → Update Account Balance
    ├── Transfer Money → Update Both Account Balances
    ├── Transaction History
    ├── Create New Account
    ├── View Profile
    └── Logout
```

## Implementation Guide for You

### Step 1: Model Classes
- Add properties to User, Account, Transaction classes
- Add constructors (default and parameterized)
- Add Getters and Setters
- Add toString() methods

### Step 2: Database Service Layer
- Implement CRUD operations in each Service class
- Use PreparedStatement for SQL queries
- Handle ResultSet mapping to objects
- Implement error handling

### Step 3: Business Logic
- Validate input data
- Check for sufficient balance before withdrawal/transfer
- Update multiple accounts in transaction for transfer
- Log transactions properly

### Step 4: CLI Implementation
- Call service methods from menu options
- Display results to user
- Handle exceptions gracefully
- Validate user inputs

### Step 5: Testing
- Test each functionality individually
- Test edge cases (negative amounts, invalid accounts, etc.)
- Test database connectivity
- Test concurrent operations

## Security Notes
- Always use PreparedStatement to prevent SQL injection
- Hash passwords before storing in database
- Validate and sanitize all user inputs
- Use try-catch blocks for exception handling
- Close database resources properly

## Future Enhancements
- Account interest calculation
- Mini-statements
- Loan management
- Fixed deposits
- Investment tracking
- Multi-currency support
- Transaction notifications
- Account freezing/blocking
- ATM card management
