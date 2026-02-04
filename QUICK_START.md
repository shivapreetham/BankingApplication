# 🚀 Banking System - Quick Start Guide

## Prerequisites
- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+
- Postman or cURL for API testing

## 1️⃣ Database Setup

### Create Database
```sql
CREATE DATABASE banking_db;
USE banking_db;
```

### Tables will be auto-created by Hibernate when application starts

---

## 2️⃣ Application Setup

### Clone/Navigate to Project
```bash
cd BankingSystem
```

### Configure Database Connection
Edit `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/banking_db
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD
```

### Build Project
```bash
mvn clean package
```

### Run Application
```bash
java -jar target/banking-system-2.0.0.jar
```

**Success Message:**
```
2024-01-15 10:00:00.000  INFO 12345 --- [main] c.banking.BankingApplication : Started BankingApplication in 3.456 seconds
```

Server will be running at: `http://localhost:8080`

---

## 3️⃣ Test the API

### Option A: Using cURL

#### Step 1: Register a User
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john",
    "password": "pass123",
    "email": "john@example.com",
    "phone": "+1-234-567-8900",
    "address": "123 Main St, City"
  }'
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "userId": 1,
  "username": "john",
  "email": "john@example.com"
}
```

#### Step 2: Save the Token
```bash
export TOKEN="eyJhbGciOiJIUzUxMiJ9..."
```

#### Step 3: Create Account
```bash
curl -X POST http://localhost:8080/accounts \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "accountType": "SAVINGS",
    "initialBalance": 5000
  }'
```

**Response:**
```json
{
  "accountId": 1,
  "accountNumber": "ACC1234567890",
  "accountType": "SAVINGS",
  "balance": 5000.00,
  "status": "ACTIVE",
  "createdDate": "2024-01-15 10:05:00",
  "lastModifiedDate": "2024-01-15 10:05:00"
}
```

#### Step 4: Deposit Money
```bash
curl -X POST http://localhost:8080/transactions/deposit \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "accountNumber": "ACC1234567890",
    "amount": 1000
  }'
```

#### Step 5: Get Transaction History
```bash
curl http://localhost:8080/transactions/history/ACC1234567890 \
  -H "Authorization: Bearer $TOKEN"
```

---

### Option B: Using Postman

1. **Import Environment Variables**
   - Create Postman variable: `base_url` = `http://localhost:8080`
   - Create Postman variable: `token` = (will be set after login)

2. **Register**
   - Method: POST
   - URL: `{{base_url}}/auth/register`
   - Body (JSON):
     ```json
     {
       "username": "john",
       "password": "pass123",
       "email": "john@example.com",
       "phone": "+1-234-567-8900",
       "address": "123 Main St"
     }
     ```
   - Copy token from response: `pm.environment.set("token", pm.response.json().token);`

3. **Create Account**
   - Method: POST
   - URL: `{{base_url}}/accounts`
   - Headers: `Authorization: Bearer {{token}}`
   - Body (JSON):
     ```json
     {
       "accountType": "SAVINGS",
       "initialBalance": 5000
     }
     ```

4. **Make Deposit**
   - Method: POST
   - URL: `{{base_url}}/transactions/deposit`
   - Headers: `Authorization: Bearer {{token}}`
   - Body (JSON):
     ```json
     {
       "accountNumber": "ACC1234567890",
       "amount": 1000
     }
     ```

---

## 4️⃣ API Endpoints Reference

### Authentication
```
POST   /auth/register              - Register new user
POST   /auth/login                 - Login user
GET    /auth/validate/{token}      - Validate token
```

### Accounts
```
GET    /accounts                   - List all accounts
POST   /accounts                   - Create account
GET    /accounts/{accountNumber}   - Get account details
DELETE /accounts/{accountNumber}   - Close account
```

### Transactions
```
POST   /transactions/deposit       - Deposit money
POST   /transactions/withdraw      - Withdraw money
POST   /transactions/transfer      - Transfer between accounts
GET    /transactions/history/{acc} - Get transaction history
```

---

## 5️⃣ Troubleshooting

### Issue: Cannot connect to database
**Solution:**
- Check MySQL is running
- Verify credentials in application.properties
- Ensure database exists: `CREATE DATABASE banking_db;`

### Issue: Port 8080 already in use
**Solution:**
- Change port in application.properties: `server.port=8081`
- Or kill process: `lsof -ti:8080 | xargs kill -9` (macOS/Linux)

### Issue: Invalid JWT token
**Solution:**
- Use token from register/login response
- Ensure token is copied exactly without extra spaces
- Tokens expire after 24 hours

### Issue: 403 Forbidden on account operations
**Solution:**
- Verify you own the account
- Check account number is correct
- Ensure valid JWT token is being used

### Issue: Insufficient balance error
**Solution:**
- Check account balance: GET `/accounts/{accountNumber}`
- Deposit more funds before withdrawing

---

## 6️⃣ Sample Workflow

### Complete Example: Deposit → Transfer → Check Balance

```bash
# 1. Register
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"user1","password":"pass123","email":"user1@test.com","phone":"111","address":"St1"}'

# Copy token from response
export TOKEN="eyJ..."

# 2. Create account for user1
curl -X POST http://localhost:8080/accounts \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"accountType":"SAVINGS","initialBalance":5000}'

# Copy accountNumber: ACC1234567890

# 3. Deposit 1000
curl -X POST http://localhost:8080/transactions/deposit \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"accountNumber":"ACC1234567890","amount":1000}'

# 4. Register second user and get their account
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"user2","password":"pass123","email":"user2@test.com","phone":"222","address":"St2"}'

# Copy token for user2
export TOKEN2="eyJ..."

# Create account for user2
curl -X POST http://localhost:8080/accounts \
  -H "Authorization: Bearer $TOKEN2" \
  -H "Content-Type: application/json" \
  -d '{"accountType":"CHECKING","initialBalance":0}'

# Copy accountNumber: ACC9876543210

# 5. Transfer 500 from user1 to user2
curl -X POST http://localhost:8080/transactions/transfer \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"accountNumber":"ACC1234567890","toAccountNumber":"ACC9876543210","amount":500}'

# 6. Check user2's balance
curl http://localhost:8080/accounts/ACC9876543210 \
  -H "Authorization: Bearer $TOKEN2"

# Expected balance: 500.00

# 7. Get transaction history
curl "http://localhost:8080/transactions/history/ACC1234567890?page=0&size=10" \
  -H "Authorization: Bearer $TOKEN"
```

---

## 7️⃣ Monitoring

### Check Application Logs
```bash
tail -f nohup.out  # if running in background
```

### Monitor Database
```sql
-- Check tables created
SHOW TABLES;

-- View users
SELECT * FROM users;

-- View accounts
SELECT * FROM accounts;

-- View transactions
SELECT * FROM transactions;
```

### Check Active Endpoints
```bash
curl http://localhost:8080/auth/validate/invalid
# Should return {"timestamp":"...","status":400,...}
```

---

## 8️⃣ Common Error Responses

### 400 Bad Request
```json
{
  "timestamp": "2024-01-15 10:30:45",
  "status": 400,
  "message": "Validation failed",
  "errors": {
    "email": "must be a valid email address"
  }
}
```

### 401 Unauthorized
```json
{
  "status": 401,
  "message": "Invalid credentials"
}
```

### 403 Forbidden
```json
{
  "status": 403,
  "message": "You don't have permission to access this resource"
}
```

### 500 Internal Server Error
```json
{
  "status": 500,
  "message": "An unexpected error occurred"
}
```

---

## 📊 Database Verification

After running sample workflow, verify data:

```sql
-- Check users
mysql> SELECT user_id, username, email FROM users;
+--------+----------+------------------+
| user_id| username | email            |
+--------+----------+------------------+
| 1      | user1    | user1@test.com   |
| 2      | user2    | user2@test.com   |
+--------+----------+------------------+

-- Check accounts
mysql> SELECT account_id, account_number, account_type, balance FROM accounts;
+------------+----------------+--------------+----------+
| account_id | account_number | account_type | balance  |
+------------+----------------+--------------+----------+
| 1          | ACC1234567890  | SAVINGS      | 5500.00  |
| 2          | ACC9876543210  | CHECKING     | 500.00   |
+------------+----------------+--------------+----------+

-- Check transactions
mysql> SELECT * FROM transactions ORDER BY timestamp DESC LIMIT 5;
```

---

## 🔐 Security Notes

1. **Change JWT Secret**: Update in `application.properties`
   ```properties
   app.jwt.secret=your-very-secret-key-min-32-chars
   ```

2. **Password Policy**: Enforce strong passwords in RegisterRequest validation

3. **HTTPS**: Use HTTPS in production
   ```properties
   server.ssl.key-store=classpath:keystore.p12
   server.ssl.key-store-password=password
   ```

4. **CORS**: Restrict origins in SecurityConfig for production
   ```java
   configuration.setAllowedOrigins(Arrays.asList("https://yourdomain.com"));
   ```

---

## 📚 Documentation Files

- **API_DOCUMENTATION.md** - Complete endpoint reference
- **SPRING_BOOT_MIGRATION.md** - Architecture and implementation details
- **PROJECT_STRUCTURE.md** - File organization

---

## ✅ Checklist Before Going Live

- [ ] Database configured correctly
- [ ] JWT secret changed to secure value
- [ ] CORS origins restricted
- [ ] HTTPS enabled
- [ ] Logging configured for production
- [ ] Database backups configured
- [ ] Load balancer configured (if needed)
- [ ] Monitoring/alerting set up
- [ ] Rate limiting enabled
- [ ] API documentation accessible

---

## 🎯 You're All Set!

The Banking System is ready to use. Start with the sample workflow above and explore all endpoints using the API documentation.

**Happy Banking! 🏦💰**
