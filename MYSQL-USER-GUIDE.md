# MySQL User Management Guide

## How to Create a New User in MySQL

### Method 1: Using MySQL Command Line

Connect to MySQL as root:
```bash
mysql -u root -p
```

Then execute these commands:
```sql
-- Create a new user
CREATE USER 'username'@'localhost' IDENTIFIED BY 'password';

-- Or create a user that can connect from anywhere
CREATE USER 'username'@'%' IDENTIFIED BY 'password';

-- Grant privileges to a specific database
GRANT ALL PRIVILEGES ON database_name.* TO 'username'@'%';

-- Grant specific privileges
GRANT SELECT, INSERT, UPDATE, DELETE ON database_name.* TO 'username'@'%';

-- Apply the changes
FLUSH PRIVILEGES;

-- Verify user creation
SELECT user, host FROM mysql.user WHERE user = 'username';
```

### Method 2: Using Docker Environment Variables (Recommended for Docker)

In `docker-compose.yml`:
```yaml
mysql:
  image: mysql:8.0
  environment:
    MYSQL_ROOT_PASSWORD: root_password    # Root user password
    MYSQL_DATABASE: database_name         # Auto-create this database
    MYSQL_USER: username                  # Auto-create this user
    MYSQL_PASSWORD: user_password         # Password for the user
```

This automatically:
- Creates the database
- Creates the user
- Grants ALL privileges on that database to the user

### Method 3: Using Initialization Scripts

Place SQL scripts in `/docker-entrypoint-initdb.d/` directory:
```yaml
mysql:
  volumes:
    - ./init-script.sql:/docker-entrypoint-initdb.d/01-init.sql
```

The script will run automatically on first container start.

## Current Banking System Setup

### Database Configuration

**MySQL Container:**
- Host: `localhost` (from host machine) or `mysql` (from Docker network)
- Port: `3307` (mapped from container's 3306 to avoid conflict with local MySQL)
- Database: `banking_db`

**Users Created:**
1. **Root User**
   - Username: `root`
   - Password: `pfLabRoot@2026`
   - Access: Full administrative access

2. **Application User (pfLab)**
   - Username: `pfLab`
   - Password: `pfLab@2026`
   - Access: Full access to `banking_db` only
   - Used by: Both REST API and CLI applications

### Connection Strings

From Docker containers (internal network):
```
jdbc:mysql://mysql:3306/banking_db
Username: pfLab
Password: pfLab@2026
```

From host machine:
```
jdbc:mysql://localhost:3307/banking_db
Username: pfLab
Password: pfLab@2026
```

## Common MySQL User Operations

### Change User Password
```sql
ALTER USER 'username'@'%' IDENTIFIED BY 'new_password';
FLUSH PRIVILEGES;
```

### Grant Additional Privileges
```sql
-- Grant all privileges on all databases
GRANT ALL PRIVILEGES ON *.* TO 'username'@'%';

-- Grant specific privileges
GRANT CREATE, DROP, ALTER ON database_name.* TO 'username'@'%';

FLUSH PRIVILEGES;
```

### Revoke Privileges
```sql
REVOKE ALL PRIVILEGES ON database_name.* FROM 'username'@'%';
FLUSH PRIVILEGES;
```

### Delete a User
```sql
DROP USER 'username'@'%';
```

### View User Privileges
```sql
SHOW GRANTS FOR 'username'@'%';
```

### List All Users
```sql
SELECT user, host FROM mysql.user;
```

## How to Change Username in This Project

If you want to use a different username (e.g., `myuser` instead of `pfLab`):

### Step 1: Update docker-compose.yml
```yaml
mysql:
  environment:
    MYSQL_USER: myuser              # Change here
    MYSQL_PASSWORD: mypassword      # Change here
```

### Step 2: Update All Connection Strings
```yaml
banking-app:
  environment:
    SPRING_DATASOURCE_USERNAME: myuser       # Change here
    SPRING_DATASOURCE_PASSWORD: mypassword   # Change here

banking-cli:
  environment:
    SPRING_DATASOURCE_USERNAME: myuser       # Change here
    SPRING_DATASOURCE_PASSWORD: mypassword   # Change here
```

### Step 3: Update Health Check
```yaml
mysql:
  healthcheck:
    test: ["CMD", "mysqladmin", "ping", "-h", "localhost", "-umyuser", "-pmypassword"]
```

### Step 4: Recreate Containers
```bash
docker-compose down -v
docker-compose up -d
```

The `-v` flag removes the old volume with the old user.

## Security Best Practices

1. **Never use default passwords in production**
   - Change `pfLabRoot@2026` and `pfLab@2026` to strong passwords

2. **Use strong passwords**
   - Minimum 12 characters
   - Mix of uppercase, lowercase, numbers, and symbols

3. **Principle of least privilege**
   - Application users should only have access to their specific database
   - Avoid using root user for applications

4. **Use environment variables**
   - Store passwords in `.env` file (not in docker-compose.yml)
   - Add `.env` to `.gitignore`

5. **Restrict host access**
   - Use `'username'@'localhost'` instead of `'username'@'%'` when possible
   - `%` allows connections from any host

## Troubleshooting

### "Access denied for user"
```bash
# Check if user exists
docker exec -it banking-mysql mysql -u root -p
SELECT user, host FROM mysql.user;

# Reset user password
ALTER USER 'pfLab'@'%' IDENTIFIED BY 'pfLab@2026';
FLUSH PRIVILEGES;
```

### "Unknown database"
```bash
# List all databases
SHOW DATABASES;

# Create database if missing
CREATE DATABASE banking_db;
```

### Connect to Docker MySQL from Host
```bash
mysql -h localhost -P 3307 -u pfLab -p
# Password: pfLab@2026
```

### Connect to Docker MySQL from Container
```bash
docker exec -it banking-mysql mysql -u pfLab -p
# Password: pfLab@2026
```
