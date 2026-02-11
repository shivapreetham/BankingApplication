# Banking System - Docker Usage Guide

This banking system supports **dual-mode operation**: REST API and CLI modes. Both can be run using Docker.

## Prerequisites

- Docker installed
- Docker Compose installed

## Running the Application

### Option 1: REST API Mode (Default)

Start the REST API server with MySQL database:

```bash
docker-compose up -d
```

This will start:
- MySQL database on port 3306
- Banking REST API on port 8080

Access the API at: `http://localhost:8080/api`

**Available Endpoints:**
- POST `/auth/register` - Register new user
- POST `/auth/login` - Login and get JWT token
- GET `/auth/validate/{token}` - Validate token
- GET `/accounts` - List user accounts
- POST `/accounts` - Create new account
- POST `/transactions/deposit` - Deposit funds
- POST `/transactions/withdraw` - Withdraw funds
- POST `/transactions/transfer` - Transfer between accounts
- GET `/transactions/history/{accountNumber}` - View transaction history

To stop:
```bash
docker-compose down
```

### Option 2: CLI Mode (Interactive)

Start the interactive CLI application:

```bash
docker-compose --profile cli run --rm banking-cli
```

This will:
- Start MySQL database (if not running)
- Launch the interactive CLI interface
- Connect to the same database as the REST API

The CLI provides a menu-driven interface for:
- User registration and login
- Account creation and management
- Deposits, withdrawals, and transfers
- Transaction history viewing
- Profile management

**Note:** The `--rm` flag automatically removes the container after you exit.

To exit the CLI, choose the logout/exit option from the menu.

### Running Both Modes Simultaneously

You can run both the REST API and CLI at the same time:

```bash
# Terminal 1: Start REST API
docker-compose up -d

# Terminal 2: Start CLI
docker-compose --profile cli run --rm banking-cli
```

Both will share the same MySQL database, so changes made in CLI will be visible via the API and vice versa.

## Database Management

### View Database Logs
```bash
docker-compose logs mysql
```

### Connect to MySQL Directly
```bash
docker exec -it banking-mysql mysql -u banking_user -p
# Password: banking_password
```

### Reset Database
```bash
docker-compose down -v
docker-compose up -d
```

## Troubleshooting

### CLI Not Starting
If the CLI doesn't start, ensure MySQL is healthy:
```bash
docker-compose ps
```

Wait for MySQL health check to pass, then try again.

### Port Conflicts
If port 8080 or 3306 is already in use:
- Edit `docker-compose.yml` ports section
- Change `"8080:8080"` to `"8081:8080"` (or any free port)
- Change `"3306:3306"` to `"3307:3306"` (or any free port)

### Viewing Logs
```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f banking-app
docker-compose logs -f banking-cli
docker-compose logs -f mysql
```

## Architecture

```
┌─────────────────┐
│   REST API      │  Port 8080
│  (banking-app)  │
└────────┬────────┘
         │
         ├──────────┐
         │          │
┌────────▼────────┐ │
│   MySQL DB      │ │
│ (banking-mysql) │ │
└────────▲────────┘ │
         │          │
┌────────┴────────┐ │
│   CLI Mode      │─┘
│ (banking-cli)   │
└─────────────────┘
```

Both modes connect to the same MySQL database for data persistence.

## Production Notes

Before deploying to production:
1. Change `APP_JWT_SECRET` to a secure random value (min 32 chars)
2. Change MySQL passwords in `docker-compose.yml`
3. Enable HTTPS/SSL
4. Restrict CORS origins
5. Set up database backups
6. Configure monitoring and logging
