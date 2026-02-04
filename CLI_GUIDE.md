# Banking System - CLI Mode Guide

## Overview

The Banking System can now run in **CLI (Command Line Interface) mode** in addition to the REST API. This allows you to interact with the banking system through an interactive terminal interface.

## Running CLI Mode

### On Windows

**Option 1: Using the launcher script**
```bash
double-click run-cli.bat
```

**Option 2: Manual command**
```bash
mvn clean package -DskipTests
java -jar target/banking-system-*.jar --spring.profiles.active=cli
```

### On Linux/Mac

**Option 1: Using the launcher script**
```bash
chmod +x run-cli.sh
./run-cli.sh
```

**Option 2: Manual command**
```bash
mvn clean package -DskipTests
java -jar target/banking-system-*.jar --spring.profiles.active=cli
```

## Features

The CLI interface provides the following functionality:

### Authentication
- **Register**: Create a new user account
- **Login**: Authenticate with email and password
- **Logout**: Exit the current session

### Account Management
- **Create Account**: Create new SAVINGS or CHECKING accounts
- **View Accounts**: List all your accounts with current balances
- **Deposit**: Add money to your account
- **Withdraw**: Remove money from your account

### Transactions
- **Transfer Money**: Send money between your accounts
- **View Transaction History**: See all transactions for an account

## Usage Examples

### 1. Register a New User
```
Choose option: 1
Email: john@example.com
Full Name: John Doe
Password: SecurePassword123
✅ Registration successful!
```

### 2. Login
```
Choose option: 2
Email: john@example.com
Password: SecurePassword123
✅ Login successful!
Welcome, John Doe!
```

### 3. Create an Account
```
Choose option: 1
Account Type (SAVINGS/CHECKING): SAVINGS
Account Name: My Savings Account
Initial Balance: 5000
✅ Account created successfully!
Account ID: 1
Balance: 5000.00
```

### 4. Deposit Money
```
Choose option: 3
Account ID: 1
Amount: 1000
✅ Deposit successful!
```

### 5. Withdraw Money
```
Choose option: 4
Account ID: 1
Amount: 500
✅ Withdrawal successful!
```

### 6. Transfer Money
```
Choose option: 5
From Account ID: 1
To Account ID: 2
Amount: 2500
✅ Transfer successful!
```

### 7. View Transaction History
```
Choose option: 6
Account ID: 1
┌───────────────────────────────────────────────────────────┐
│ Transactions:
├─────────────────────────────────────────────────────────┤
│ ID: 1
│ Type: DEPOSIT
│ Amount: 1000.00
│ Date: 2026-02-04 10:30:45
...
```

## System Requirements

- **Java**: JDK 17 or higher
- **Maven**: 3.6.0 or higher
- **MySQL**: 8.0 or higher (must be running)
- **Database**: Must have `banking_system` database created

## Database Setup

Before running CLI mode, ensure your MySQL database is configured:

```sql
CREATE DATABASE banking_system;
CREATE USER 'root'@'localhost' IDENTIFIED BY 'Shivapreetham@123';
GRANT ALL PRIVILEGES ON banking_system.* TO 'root'@'localhost';
FLUSH PRIVILEGES;
```

Or use the credentials in `application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/banking_system
spring.datasource.username=root
spring.datasource.password=Shivapreetham@123
```

## Running Both CLI and REST API

You can run the application in **REST API mode** (with CLI disabled):

```bash
# Start REST API (HTTP server on port 8080)
mvn spring-boot:run

# Or
java -jar target/banking-system-*.jar
```

The REST API will be available at `http://localhost:8080/api`

## Running with Docker

### CLI in Docker
```bash
# Build image
docker build -t banking-cli .

# Run CLI mode
docker run -it --rm --name banking-cli \
  -e SPRING_PROFILES_ACTIVE=cli \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/banking_system \
  banking-cli
```

### REST API in Docker
```bash
docker-compose up -d
# API available at http://localhost:8080/api
```

## Profile Configuration

The application uses Spring profiles:

### Default Profile (REST API)
```bash
java -jar app.jar
# Starts REST API server on port 8080
```

### CLI Profile
```bash
java -jar app.jar --spring.profiles.active=cli
# Starts interactive CLI interface
# No REST API server running
```

## Troubleshooting

### Issue: "MySQL connection refused"
**Solution**: Ensure MySQL is running
```bash
# Windows
net start MySQL80

# Linux
sudo service mysql start

# Mac
brew services start mysql
```

### Issue: "Build failed"
**Solution**: Clear Maven cache and rebuild
```bash
mvn clean install -DskipTests
```

### Issue: "Maven not found"
**Solution**: Install Maven
- Download from: https://maven.apache.org/download.cgi
- Add to system PATH
- Verify: `mvn --version`

### Issue: "Database doesn't exist"
**Solution**: Create the database
```sql
CREATE DATABASE banking_system;
CREATE USER 'root'@'localhost' IDENTIFIED BY 'Shivapreetham@123';
GRANT ALL PRIVILEGES ON banking_system.* TO 'root'@'localhost';
```

## Security Notes

1. **CLI Mode**: Passwords are not echoed to the terminal for security
2. **JWT Tokens**: Changed in docker-compose, update for production
3. **Database Credentials**: Update in `application.properties` for production
4. **Profile Password**: The default password is exposed in properties - change it!

## Performance Tips

1. **Initial Load**: First run may take 30-60 seconds (schema initialization)
2. **Subsequent Runs**: Should start in 10-15 seconds
3. **Large Datasets**: CLI is suitable for typical banking operations (100s of accounts)

## API Endpoints (REST Mode)

When running in REST API mode, access:

```
POST   /api/auth/register        - Register new user
POST   /api/auth/login           - Login user
GET    /api/auth/validate/{token} - Validate token

POST   /api/accounts             - Create account
GET    /api/accounts             - Get user accounts
POST   /api/accounts/{id}/deposit - Deposit money
POST   /api/accounts/{id}/withdraw - Withdraw money

POST   /api/transactions/transfer - Transfer money
GET    /api/transactions/account/{accountId} - Get transaction history
```

See [API_DOCUMENTATION.md](API_DOCUMENTATION.md) for full details.

## Running Tests

```bash
# Run all tests
mvn test

# Run specific test
mvn test -Dtest=AccountServiceTest

# Skip tests during build
mvn clean package -DskipTests
```

## Switching Between Modes

### Switch from CLI to REST API
The JAR file works in both modes:
```bash
# CLI mode
java -jar app.jar --spring.profiles.active=cli

# REST API mode (default)
java -jar app.jar
```

### Using Different Databases
```bash
# CLI with custom database
java -jar app.jar --spring.profiles.active=cli \
  -Dspring.datasource.url=jdbc:mysql://localhost:3306/banking_prod \
  -Dspring.datasource.username=prod_user \
  -Dspring.datasource.password=prod_password
```

## Next Steps

1. **Build the JAR**: `mvn clean package -DskipTests`
2. **Run CLI**: `java -jar target/banking-system-*.jar --spring.profiles.active=cli`
3. **Register a user**: Follow the interactive prompts
4. **Test all features**: Create accounts, deposit, withdraw, transfer
5. **Check logs**: Monitor `target/spring.log` for debugging

## Architecture

```
┌─────────────────────────────────────┐
│      BankingCLI (Spring Component)  │
│  - Interactive Menu Interface       │
│  - User Input Handling              │
└──────────────┬──────────────────────┘
               │
               ├──────────────────────────────────┐
               │                                  │
        ┌──────▼──────┐              ┌────────────▼──────┐
        │ UserService │              │ AccountService    │
        │ - Register  │              │ - Create Account  │
        │ - Login     │              │ - Deposit         │
        └──────┬──────┘              │ - Withdraw        │
               │                     └────────────┬──────┘
               │                                  │
        ┌──────▼──────────────────────────────────▼────────┐
        │          TransactionService                       │
        │  - Transfer Money                                 │
        │  - Get Transaction History                        │
        └──────┬───────────────────────────────────────────┘
               │
        ┌──────▼──────────────────────────────────────────┐
        │    JPA Repositories (Spring Data)                │
        │  - UserRepository                                │
        │  - AccountRepository                             │
        │  - TransactionRepository                         │
        └──────┬───────────────────────────────────────────┘
               │
        ┌──────▼──────────────────────────────────────────┐
        │         MySQL Database                           │
        │  - Users, Accounts, Transactions Tables          │
        └────────────────────────────────────────────────┘
```

## License

Banking System © 2026. All rights reserved.
