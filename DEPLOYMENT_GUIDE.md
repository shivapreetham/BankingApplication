# 🚀 Banking System - Deployment Guide

## Pre-Deployment Checklist

- [ ] Java 17+ installed
- [ ] Maven 3.6+ installed
- [ ] MySQL 8.0+ running
- [ ] application.properties configured
- [ ] All dependencies resolved
- [ ] Application tested locally
- [ ] Database schema created
- [ ] JWT secret configured
- [ ] HTTPS certificates obtained
- [ ] Backups configured

---

## 1. Local Development Deployment

### 1.1 Setup MySQL Database

```bash
# Start MySQL service
sudo service mysql start    # Linux
brew services start mysql   # macOS
# On Windows: Search "MySQL" in services

# Login to MySQL
mysql -u root -p

# Create database
CREATE DATABASE banking_db;
CREATE USER 'banking_user'@'localhost' IDENTIFIED BY 'secure_password';
GRANT ALL PRIVILEGES ON banking_db.* TO 'banking_user'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

### 1.2 Configure Application

Edit `src/main/resources/application.properties`:

```properties
# MySQL Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/banking_db
spring.datasource.username=banking_user
spring.datasource.password=secure_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.jpa.properties.hibernate.format_sql=false
spring.jpa.properties.hibernate.generate_statistics=false

# JWT Configuration
app.jwt.secret=my-super-secret-key-change-this-in-production-at-least-32-chars
app.jwt.expiration=86400000

# Server Configuration
server.port=8080
server.servlet.context-path=/api

# Logging
logging.level.root=INFO
logging.level.com.banking=DEBUG
logging.file.name=logs/application.log
```

### 1.3 Build Application

```bash
cd BankingSystem

# Clean build
mvn clean package -DskipTests

# Output: target/banking-system-2.0.0.jar
```

### 1.4 Run Application

```bash
# Option 1: Using JAR file
java -jar target/banking-system-2.0.0.jar

# Option 2: Using Maven
mvn spring-boot:run

# Option 3: With custom properties
java -jar target/banking-system-2.0.0.jar \
  --spring.datasource.password=your_password \
  --server.port=8080
```

### 1.5 Verify Running

```bash
# Check if running
curl http://localhost:8080/auth/validate/test

# Expected response:
# {"timestamp":"...","status":400,"message":"..."}

# Check logs
tail -f logs/application.log
```

---

## 2. Docker Deployment

### 2.1 Create Dockerfile

Create `Dockerfile` in project root:

```dockerfile
FROM maven:3.8.1-openjdk-17 AS builder
WORKDIR /build
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:17-alpine
WORKDIR /app
COPY --from=builder /build/target/banking-system-2.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 2.2 Create docker-compose.yml

```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: root_password
      MYSQL_DATABASE: banking_db
      MYSQL_USER: banking_user
      MYSQL_PASSWORD: user_password
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      timeout: 20s
      retries: 10

  banking-app:
    build: .
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/banking_db
      SPRING_DATASOURCE_USERNAME: banking_user
      SPRING_DATASOURCE_PASSWORD: user_password
      APP_JWT_SECRET: your-secret-key-here-min-32-chars
    ports:
      - "8080:8080"
    depends_on:
      mysql:
        condition: service_healthy
    restart: unless-stopped

volumes:
  mysql_data:
```

### 2.3 Build and Run

```bash
# Build Docker image
docker build -t banking-system:2.0.0 .

# Run with docker-compose
docker-compose up -d

# View logs
docker-compose logs -f banking-app

# Stop services
docker-compose down
```

---

## 3. Production Deployment (Ubuntu Server)

### 3.1 Server Preparation

```bash
# Update system
sudo apt update && sudo apt upgrade -y

# Install Java 17
sudo apt install -y openjdk-17-jdk

# Install MySQL
sudo apt install -y mysql-server

# Verify installations
java -version
mysql --version
```

### 3.2 Create Application User

```bash
# Create non-root user for application
sudo useradd -m -s /bin/bash banking
sudo usermod -aG sudo banking

# Switch to user
sudo su - banking
```

### 3.3 Setup Application Directory

```bash
# Create app directory
mkdir -p ~/banking-app
cd ~/banking-app

# Copy JAR file (from local machine)
# scp target/banking-system-2.0.0.jar banking@server:/home/banking/banking-app/

# Create logs directory
mkdir -p logs
chmod 755 logs
```

### 3.4 Configure Environment

```bash
# Create .env file
cat > ~/banking-app/.env << EOF
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/banking_db
SPRING_DATASOURCE_USERNAME=banking_user
SPRING_DATASOURCE_PASSWORD=SecurePassword123!
APP_JWT_SECRET=your-production-secret-key-minimum-32-characters-long
SERVER_PORT=8080
JAVA_OPTS=-Xmx512m -Xms256m
EOF

chmod 600 ~/banking-app/.env
```

### 3.5 Setup MySQL

```bash
# Login to MySQL
mysql -u root -p

# Create database
CREATE DATABASE banking_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# Create user
CREATE USER 'banking_user'@'localhost' IDENTIFIED BY 'SecurePassword123!';

# Grant privileges
GRANT ALL PRIVILEGES ON banking_db.* TO 'banking_user'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

### 3.6 Create Systemd Service

```bash
# Create service file
sudo tee /etc/systemd/system/banking.service > /dev/null << EOF
[Unit]
Description=Banking System REST API
After=network.target
Wants=network-online.target

[Service]
Type=simple
User=banking
WorkingDirectory=/home/banking/banking-app
EnvironmentFile=/home/banking/banking-app/.env
ExecStart=/usr/bin/java \${JAVA_OPTS} -jar banking-system-2.0.0.jar
Restart=on-failure
RestartSec=10
StandardOutput=journal
StandardError=journal
SyslogIdentifier=banking-app

[Install]
WantedBy=multi-user.target
EOF

# Reload systemd
sudo systemctl daemon-reload

# Enable and start service
sudo systemctl enable banking
sudo systemctl start banking

# Check status
sudo systemctl status banking

# View logs
sudo journalctl -u banking -f
```

### 3.7 Configure Nginx Reverse Proxy

```bash
# Install Nginx
sudo apt install -y nginx

# Create Nginx config
sudo tee /etc/nginx/sites-available/banking > /dev/null << 'EOF'
upstream banking_backend {
    server localhost:8080;
}

server {
    listen 80;
    server_name banking.example.com;
    
    # Redirect HTTP to HTTPS
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name banking.example.com;
    
    # SSL Certificates (use Let's Encrypt)
    ssl_certificate /etc/letsencrypt/live/banking.example.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/banking.example.com/privkey.pem;
    
    # Security headers
    add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header X-Frame-Options "DENY" always;
    add_header X-XSS-Protection "1; mode=block" always;
    
    # Proxy settings
    location / {
        proxy_pass http://banking_backend;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_read_timeout 90;
    }
}
EOF

# Enable site
sudo ln -s /etc/nginx/sites-available/banking /etc/nginx/sites-enabled/banking
sudo rm -f /etc/nginx/sites-enabled/default

# Test and restart Nginx
sudo nginx -t
sudo systemctl restart nginx
```

### 3.8 Setup SSL with Let's Encrypt

```bash
# Install Certbot
sudo apt install -y certbot python3-certbot-nginx

# Get certificate
sudo certbot certonly --nginx -d banking.example.com

# Auto-renewal is configured by default
sudo systemctl enable certbot.timer
```

### 3.9 Setup Database Backups

```bash
# Create backup script
cat > ~/banking-app/backup.sh << 'EOF'
#!/bin/bash
BACKUP_DIR="/home/banking/backups"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
BACKUP_FILE="$BACKUP_DIR/banking_db_$TIMESTAMP.sql"

mkdir -p $BACKUP_DIR

mysqldump -u banking_user -p$MYSQL_PASSWORD banking_db > $BACKUP_FILE
gzip $BACKUP_FILE

# Keep only last 7 days of backups
find $BACKUP_DIR -name "banking_db_*.sql.gz" -mtime +7 -delete

echo "Backup completed: $BACKUP_FILE.gz"
EOF

chmod +x ~/banking-app/backup.sh

# Schedule daily backup
(crontab -l 2>/dev/null; echo "0 2 * * * /home/banking/banking-app/backup.sh") | crontab -
```

---

## 4. AWS Deployment (Elastic Beanstalk)

### 4.1 Install EB CLI

```bash
pip install awsebcli --upgrade --user
```

### 4.2 Initialize EB Project

```bash
# In project root
eb init -p java-17 banking-system

# Create environment
eb create production-env
```

### 4.3 Configure Environment

```bash
# Create .ebextensions/app.config
mkdir -p .ebextensions

cat > .ebextensions/app.config << 'EOF'
option_settings:
  aws:elasticbeanstalk:application:environment:
    SPRING_DATASOURCE_URL: jdbc:mysql://your-rds-endpoint:3306/banking_db
    SPRING_DATASOURCE_USERNAME: banking_user
    APP_JWT_SECRET: your-secret-key
  aws:autoscaling:launchconfiguration:
    InstanceType: t3.small
    RootVolumeSize: 20
EOF
```

### 4.4 Deploy to AWS

```bash
# Deploy application
eb deploy

# Monitor deployment
eb logs

# Access application
eb open
```

---

## 5. Performance Tuning

### 5.1 JVM Optimization

```bash
# Set in startup command
JAVA_OPTS="-Xmx1024m -Xms512m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"
java $JAVA_OPTS -jar banking-system-2.0.0.jar
```

### 5.2 Database Connection Pool

Edit `application.properties`:

```properties
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.idle-timeout=600000
spring.datasource.hikari.max-lifetime=1800000
```

### 5.3 Hibernate Optimization

```properties
spring.jpa.properties.hibernate.jdbc.batch_size=20
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.order_updates=true
spring.jpa.properties.hibernate.jdbc.fetch_size=50
```

---

## 6. Monitoring & Logging

### 6.1 Application Logging

Edit `application.properties`:

```properties
logging.file.name=logs/banking-system.log
logging.file.max-size=10MB
logging.file.max-history=30
logging.level.root=INFO
logging.level.com.banking=DEBUG
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} - %logger{36} - %msg%n
logging.pattern.file=%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n
```

### 6.2 Health Check Endpoint

Add to `application.properties`:

```properties
management.endpoints.web.exposure.include=health,metrics
management.endpoint.health.show-details=always
```

Check health:

```bash
curl http://localhost:8080/actuator/health
```

### 6.3 Monitoring with Prometheus (Optional)

```properties
management.endpoints.web.exposure.include=health,metrics,prometheus
management.metrics.export.prometheus.enabled=true
```

---

## 7. Security Hardening

### 7.1 Update application.properties

```properties
# Production secrets should use environment variables
app.jwt.secret=${JWT_SECRET:change-me-in-production}

# HTTPS only
server.http2.enabled=true
server.ssl.enabled=true
server.ssl.key-store=${SSL_KEYSTORE_PATH}
server.ssl.key-store-password=${SSL_KEYSTORE_PASSWORD}

# Session security
server.servlet.session.cookie.http-only=true
server.servlet.session.cookie.secure=true
server.servlet.session.cookie.same-site=strict
```

### 7.2 WAF Configuration (AWS WAF)

- Enable rate limiting
- Block known malicious IPs
- Protect against SQL injection
- Enable CORS restrictions

### 7.3 Database Security

```bash
# Encrypt connections
# Add to MySQL connection string: &useSSL=true&serverTimezone=UTC

# Restrict database user
mysql> GRANT SELECT, INSERT, UPDATE, DELETE ON banking_db.* TO 'banking_user'@'localhost';
```

---

## 8. Rollback Procedure

### 8.1 Keep Previous Version

```bash
# Backup current JAR
mv banking-system-2.0.0.jar banking-system-2.0.0.jar.backup

# Restore previous version
cp banking-system-1.9.9.jar banking-system-2.0.0.jar

# Restart service
sudo systemctl restart banking
```

### 8.2 Database Rollback

```bash
# Restore from backup
mysql -u banking_user -p banking_db < backup_file.sql
```

---

## 9. Testing After Deployment

### 9.1 Health Check

```bash
curl -i http://localhost:8080/api/auth/validate/test
# Expected: 400 Bad Request
```

### 9.2 API Testing

```bash
# Register
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"pass123","email":"test@example.com","phone":"123","address":"St"}'

# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"pass123"}'
```

### 9.3 Load Testing

```bash
# Using Apache Bench
ab -n 1000 -c 10 http://localhost:8080/auth/validate/test

# Using wrk
wrk -t12 -c400 -d30s http://localhost:8080/auth/validate/test
```

---

## 10. Troubleshooting Deployment

### Issue: Port Already in Use

```bash
# Find process using port 8080
lsof -i :8080

# Kill process
kill -9 <PID>
```

### Issue: Database Connection Failed

```bash
# Check MySQL status
sudo systemctl status mysql

# Restart MySQL
sudo systemctl restart mysql

# Verify credentials
mysql -u banking_user -p -h localhost
```

### Issue: Out of Memory

```bash
# Increase heap size
export JAVA_OPTS="-Xmx2048m -Xms1024m"
java $JAVA_OPTS -jar banking-system-2.0.0.jar
```

### Issue: JWT Token Validation Fails

```bash
# Verify JWT secret matches
echo $APP_JWT_SECRET

# Check token format (Bearer prefix)
curl -H "Authorization: Bearer TOKEN" ...
```

---

## Post-Deployment Checklist

- [ ] Application is running
- [ ] Database is connected
- [ ] API endpoints respond correctly
- [ ] Authentication works
- [ ] Transactions are recorded
- [ ] Logs are being written
- [ ] Backups are scheduled
- [ ] Monitoring is configured
- [ ] SSL certificate is valid
- [ ] Load balancing is working

---

## Support & Resources

- Spring Boot Documentation: https://spring.io/projects/spring-boot
- MySQL Documentation: https://dev.mysql.com/doc/
- Docker Documentation: https://docs.docker.com/
- Nginx Documentation: https://nginx.org/en/docs/
- AWS Elastic Beanstalk: https://docs.aws.amazon.com/elasticbeanstalk/

---

**Deployment Status**: Ready for Production ✅

Use this guide to deploy your Banking System to any environment!
