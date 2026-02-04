# Banking System REST API Documentation

## Overview
This is a Spring Boot REST API for a Banking System with JWT authentication, account management, and transaction processing.

## Base URL
```
http://localhost:8080
```

## Features
- User Authentication (Register/Login with JWT)
- Account Management (Create, View, Close accounts)
- Transaction Processing (Deposit, Withdraw, Transfer)
- Transaction History with Pagination
- Secure endpoints with JWT token validation

---

## Authentication

### 1. Register User
**Endpoint:** `POST /auth/register`

**Request Body:**
```json
{
  "username": "john_doe",
  "password": "password123",
  "email": "john@example.com",
  "phone": "+1-234-567-8900",
  "address": "123 Main St, City, State"
}
```

**Response (201 Created):**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "userId": 1,
  "username": "john_doe",
  "email": "john@example.com"
}
```

**Error Response (400 Bad Request):**
```json
{
  "timestamp": "2024-01-15 10:30:45",
  "status": 400,
  "message": "Validation failed",
  "errors": {
    "username": "Username already exists",
    "email": "must be a valid email"
  }
}
```

---

### 2. Login User
**Endpoint:** `POST /auth/login`

**Request Body:**
```json
{
  "username": "john_doe",
  "password": "password123"
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "userId": 1,
  "username": "john_doe",
  "email": "john@example.com"
}
```

**Error Response (401 Unauthorized):**
```json
{
  "timestamp": "2024-01-15 10:31:20",
  "status": 401,
  "message": "Invalid credentials"
}
```

---

### 3. Validate Token
**Endpoint:** `GET /auth/validate/{token}`

**Response (200 OK):**
```json
true
```

---

## Account Management

### 1. Create Account
**Endpoint:** `POST /accounts`

**Headers:**
```
Authorization: Bearer <token>
```

**Request Body:**
```json
{
  "accountType": "SAVINGS",
  "initialBalance": 5000.00
}
```

**Response (201 Created):**
```json
{
  "accountId": 1,
  "accountNumber": "ACC1234567890",
  "accountType": "SAVINGS",
  "balance": 5000.00,
  "status": "ACTIVE",
  "createdDate": "2024-01-15 10:35:00",
  "lastModifiedDate": "2024-01-15 10:35:00"
}
```

---

### 2. Get All Accounts
**Endpoint:** `GET /accounts`

**Headers:**
```
Authorization: Bearer <token>
```

**Response (200 OK):**
```json
[
  {
    "accountId": 1,
    "accountNumber": "ACC1234567890",
    "accountType": "SAVINGS",
    "balance": 5000.00,
    "status": "ACTIVE",
    "createdDate": "2024-01-15 10:35:00",
    "lastModifiedDate": "2024-01-15 10:35:00"
  },
  {
    "accountId": 2,
    "accountNumber": "ACC9876543210",
    "accountType": "CHECKING",
    "balance": 2500.00,
    "status": "ACTIVE",
    "createdDate": "2024-01-16 11:20:00",
    "lastModifiedDate": "2024-01-16 11:20:00"
  }
]
```

---

### 3. Get Account Details
**Endpoint:** `GET /accounts/{accountNumber}`

**Headers:**
```
Authorization: Bearer <token>
```

**Response (200 OK):**
```json
{
  "accountId": 1,
  "accountNumber": "ACC1234567890",
  "accountType": "SAVINGS",
  "balance": 5000.00,
  "status": "ACTIVE",
  "createdDate": "2024-01-15 10:35:00",
  "lastModifiedDate": "2024-01-15 10:35:00"
}
```

---

### 4. Close Account
**Endpoint:** `DELETE /accounts/{accountNumber}`

**Headers:**
```
Authorization: Bearer <token>
```

**Response (204 No Content)**

---

## Transaction Management

### 1. Deposit Money
**Endpoint:** `POST /transactions/deposit`

**Headers:**
```
Authorization: Bearer <token>
```

**Request Body:**
```json
{
  "accountNumber": "ACC1234567890",
  "amount": 1000.00
}
```

**Response (201 Created):**
```json
{
  "transactionId": 1,
  "accountNumber": "ACC1234567890",
  "transactionType": "DEPOSIT",
  "amount": 1000.00,
  "balanceAfter": 6000.00,
  "description": "Deposit",
  "timestamp": "2024-01-15 10:40:30"
}
```

---

### 2. Withdraw Money
**Endpoint:** `POST /transactions/withdraw`

**Headers:**
```
Authorization: Bearer <token>
```

**Request Body:**
```json
{
  "accountNumber": "ACC1234567890",
  "amount": 500.00
}
```

**Response (201 Created):**
```json
{
  "transactionId": 2,
  "accountNumber": "ACC1234567890",
  "transactionType": "WITHDRAWAL",
  "amount": 500.00,
  "balanceAfter": 5500.00,
  "description": "Withdrawal",
  "timestamp": "2024-01-15 10:42:00"
}
```

**Error Response (400 Bad Request):**
```json
{
  "timestamp": "2024-01-15 10:42:30",
  "status": 400,
  "message": "Insufficient balance. Current balance: 5500.00"
}
```

---

### 3. Transfer Money
**Endpoint:** `POST /transactions/transfer`

**Headers:**
```
Authorization: Bearer <token>
```

**Request Body:**
```json
{
  "accountNumber": "ACC1234567890",
  "toAccountNumber": "ACC9876543210",
  "amount": 750.00
}
```

**Response (201 Created):**
```json
{
  "transactionId": 3,
  "accountNumber": "ACC1234567890",
  "transactionType": "TRANSFER",
  "amount": 750.00,
  "balanceAfter": 4750.00,
  "description": "Transfer to ACC9876543210",
  "timestamp": "2024-01-15 10:45:15"
}
```

---

### 4. Get Transaction History
**Endpoint:** `GET /transactions/history/{accountNumber}`

**Headers:**
```
Authorization: Bearer <token>
```

**Query Parameters:**
- `page` (default: 0) - Page number for pagination
- `size` (default: 10) - Number of records per page

**Example:**
```
GET /transactions/history/ACC1234567890?page=0&size=10
```

**Response (200 OK):**
```json
{
  "content": [
    {
      "transactionId": 3,
      "accountNumber": "ACC1234567890",
      "transactionType": "TRANSFER",
      "amount": 750.00,
      "balanceAfter": 4750.00,
      "description": "Transfer to ACC9876543210",
      "timestamp": "2024-01-15 10:45:15"
    },
    {
      "transactionId": 2,
      "accountNumber": "ACC1234567890",
      "transactionType": "WITHDRAWAL",
      "amount": 500.00,
      "balanceAfter": 5500.00,
      "description": "Withdrawal",
      "timestamp": "2024-01-15 10:42:00"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10,
    "sort": {
      "empty": false,
      "unsorted": false,
      "sorted": true
    },
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "last": true,
  "totalElements": 2,
  "totalPages": 1,
  "first": true,
  "size": 10,
  "number": 0,
  "sort": {
    "empty": false,
    "unsorted": false,
    "sorted": true
  },
  "numberOfElements": 2,
  "empty": false
}
```

---

## Error Handling

### Global Exception Handler
The API returns standardized error responses in JSON format.

**Error Response Format:**
```json
{
  "timestamp": "2024-01-15 10:50:00",
  "status": 400,
  "message": "Error description",
  "path": "/api/endpoint",
  "errors": {
    "fieldName": "Error message"
  }
}
```

**Common HTTP Status Codes:**
- `200 OK` - Request successful
- `201 Created` - Resource created successfully
- `204 No Content` - Successful deletion/update with no content
- `400 Bad Request` - Validation error or invalid input
- `401 Unauthorized` - Missing or invalid JWT token
- `403 Forbidden` - User doesn't own the resource
- `404 Not Found` - Resource not found
- `500 Internal Server Error` - Server error

---

## Security

### JWT Token Authentication
- Tokens are issued on successful login/registration
- Include the token in the `Authorization` header with `Bearer ` prefix
- Tokens expire after 24 hours
- All protected endpoints require valid JWT token

**Example Header:**
```
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwibmFtZSI6ImpvaG4iLCJpYXQiOjE2MDI2NzUwMDB9...
```

---

## Building and Running

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+

### Build
```bash
mvn clean package
```

### Run
```bash
java -jar target/banking-system-2.0.0.jar
```

### Configuration
Update `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/banking_db
spring.datasource.username=root
spring.datasource.password=password
app.jwt.secret=your-secret-key-here
app.jwt.expiration=86400000
```

---

## Testing with cURL

### Register
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "password": "password123",
    "email": "john@example.com",
    "phone": "+1-234-567-8900",
    "address": "123 Main St"
  }'
```

### Login
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "password": "password123"
  }'
```

### Create Account
```bash
curl -X POST http://localhost:8080/accounts \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "accountType": "SAVINGS",
    "initialBalance": 5000.00
  }'
```

### Deposit
```bash
curl -X POST http://localhost:8080/transactions/deposit \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "accountNumber": "ACC1234567890",
    "amount": 1000.00
  }'
```

---

## Support
For issues or questions, please contact the development team.
