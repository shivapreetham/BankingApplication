# 🐳 Banking System - Docker Deployment Guide

## Quick Start

### Prerequisites
- Docker installed (https://www.docker.com/products/docker-desktop)
- Docker Compose installed (included with Docker Desktop)

### 1. Build and Run

```bash
# Navigate to project directory
cd BankingSystem

# Build and start services
docker-compose up -d

# Check status
docker-compose ps
```

**Expected output:**
```
NAME                COMMAND                  SERVICE      STATUS
banking-mysql       "docker-entrypoint.s…"   mysql        Up 2 minutes (healthy)
banking-app         "java -jar app.jar"      banking-app  Up 1 minute (healthy)
```

### 2. Verify Services

```bash
# Test database connection
docker exec banking-mysql mysql -u banking_user -pbanking_password banking_db -e "SELECT 1;"

# Test API endpoint
curl http://localhost:8080/api/auth/validate/test
```

### 3. View Logs

```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f banking-app
docker-compose logs -f mysql
```

### 4. Stop Services

```bash
# Stop (keeps data)
docker-compose stop

# Stop and remove (removes data)
docker-compose down

# Remove volumes too (removes database)
docker-compose down -v
```

---

## File Structure

```
BankingSystem/
├── Dockerfile                 # Multi-stage build configuration
├── docker-compose.yml         # Services orchestration
├── .dockerignore              # Files to ignore in build
├── pom.xml                    # Maven dependencies
├── src/main/
│   ├── java/com/banking/      # Java source code
│   └── resources/
│       ├── application.properties
│       └── database_schema.sql
└── resources/
    └── database_schema.sql    # Database initialization
```

---

## Container Configuration

### MySQL Service
- **Image**: mysql:8.0
- **Container Name**: banking-mysql
- **Port**: 3306
- **Database**: banking_db
- **User**: banking_user
- **Password**: banking_password
- **Volumes**: mysql_data (persistent)
- **Health Check**: Enabled (ping every 10s)

### Banking App Service
- **Build**: From Dockerfile (multi-stage)
- **Container Name**: banking-app
- **Port**: 8080
- **Depends On**: mysql (waits for healthy)
- **Memory**: 512MB max, 256MB initial
- **Health Check**: Enabled (HTTP check every 30s)

---

## Environment Variables

Set in `docker-compose.yml`:

```yaml
environment:
  # Database
  SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/banking_db
  SPRING_DATASOURCE_USERNAME: banking_user
  SPRING_DATASOURCE_PASSWORD: banking_password
  
  # JPA
  SPRING_JPA_HIBERNATE_DDL_AUTO: update
  
  # JWT
  APP_JWT_SECRET: your-secret-key-min-32-chars
  APP_JWT_EXPIRATION: 86400000
  
  # Server
  SERVER_PORT: 8080
  
  # JVM
  JAVA_OPTS: -Xmx512m -Xms256m
```

### Customize Environment

Edit `docker-compose.yml` before running:

```yaml
environment:
  SPRING_DATASOURCE_PASSWORD: your-secure-password
  APP_JWT_SECRET: your-custom-secret-key-here
```

---

## Database Initialization

### Automatic Initialization
The `database_schema.sql` is automatically executed on MySQL startup:

```yaml
volumes:
  - ./resources/database_schema.sql:/docker-entrypoint-initdb.d/init.sql
```

### Manual Database Access

```bash
# Connect to MySQL container
docker exec -it banking-mysql mysql -u banking_user -pbanking_password banking_db

# List tables
SHOW TABLES;

# View users
SELECT * FROM users;

# Exit
EXIT;
```

---

## Docker Commands Reference

### Build Operations
```bash
# Build image
docker build -t banking-system:2.0.0 .

# View images
docker images | grep banking

# Remove image
docker rmi banking-system:2.0.0
```

### Container Operations
```bash
# View running containers
docker ps

# View all containers (including stopped)
docker ps -a

# View container logs
docker logs -f banking-app

# Execute command in container
docker exec -it banking-app sh

# View container details
docker inspect banking-app
```

### Compose Operations
```bash
# Start services
docker-compose up -d

# Stop services
docker-compose stop

# Remove services and volumes
docker-compose down -v

# Rebuild and start
docker-compose up -d --build

# View logs
docker-compose logs -f

# Check status
docker-compose ps
```

---

## Common Issues & Solutions

### Issue: Port Already in Use

**Problem**: `Error: bind: address already in use`

**Solution**:
```bash
# Find process using port 8080
lsof -i :8080
# or on Windows
netstat -ano | findstr :8080

# Kill process
kill -9 <PID>

# Or use different port in docker-compose.yml
ports:
  - "8081:8080"
```

### Issue: MySQL Not Starting

**Problem**: Container exits immediately

**Solution**:
```bash
# Check logs
docker-compose logs mysql

# Verify database_schema.sql exists
ls -la resources/database_schema.sql

# Remove and recreate
docker-compose down -v
docker-compose up -d
```

### Issue: App Can't Connect to Database

**Problem**: Connection refused error

**Solution**:
```bash
# Wait for MySQL to be healthy (automatic)
docker-compose up -d

# Check MySQL status
docker-compose logs mysql | grep "ready for connections"

# Manually verify connection
docker exec banking-app curl http://mysql:3306
```

### Issue: Out of Memory

**Problem**: App crashes with OutOfMemoryError

**Solution**: Increase JVM memory in `docker-compose.yml`:
```yaml
JAVA_OPTS: -Xmx1024m -Xms512m
```

---

## Production Deployment

### Best Practices

1. **Use External Database**
   ```yaml
   mysql:
     image: mysql:8.0-enterprise
     # Use managed database service (RDS, Cloud SQL, etc.)
   ```

2. **Use Private Registry**
   ```bash
   docker build -t myregistry.azurecr.io/banking-system:2.0.0 .
   docker push myregistry.azurecr.io/banking-system:2.0.0
   ```

3. **Use Secrets Management**
   ```bash
   # Instead of plain text, use Docker secrets
   docker secret create jwt_secret -
   docker secret create db_password -
   ```

4. **Enable Resource Limits**
   ```yaml
   banking-app:
     deploy:
       resources:
         limits:
           cpus: '1'
           memory: 1G
         reservations:
           cpus: '0.5'
           memory: 512M
   ```

5. **Use Health Checks**
   ```yaml
   healthcheck:
     test: ["CMD", "curl", "-f", "http://localhost:8080/api/auth/validate/test"]
     interval: 30s
     timeout: 3s
     retries: 3
   ```

### Deploy to Docker Swarm

```bash
# Initialize swarm
docker swarm init

# Deploy stack
docker stack deploy -c docker-compose.yml banking

# View services
docker service ls

# View logs
docker service logs banking_banking-app

# Remove stack
docker stack rm banking
```

### Deploy to Kubernetes

Create `banking-deployment.yaml`:
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: banking-app
spec:
  replicas: 3
  selector:
    matchLabels:
      app: banking-app
  template:
    metadata:
      labels:
        app: banking-app
    spec:
      containers:
      - name: banking-app
        image: banking-system:2.0.0
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_DATASOURCE_URL
          value: "jdbc:mysql://mysql-service:3306/banking_db"
        livenessProbe:
          httpGet:
            path: /api/auth/validate/test
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
```

Deploy:
```bash
kubectl apply -f banking-deployment.yaml
```

---

## Performance Tuning

### Optimize Image Size

```dockerfile
# Use alpine for smaller image
FROM openjdk:17-alpine

# Multi-stage build (already implemented)
FROM maven:3.8.1-openjdk-17 AS builder
...
FROM openjdk:17-alpine
COPY --from=builder ...
```

### Optimize Database Performance

```yaml
mysql:
  command: --default-authentication-plugin=mysql_native_password
  environment:
    MYSQL_MAX_CONNECTIONS: 100
    MYSQL_QUERY_CACHE_SIZE: 0
```

### Optimize App Performance

```yaml
banking-app:
  environment:
    JAVA_OPTS: -Xmx1024m -Xms512m -XX:+UseG1GC -XX:MaxGCPauseMillis=200
```

---

## Security Hardening

### 1. Change Default Passwords

Edit `docker-compose.yml`:
```yaml
mysql:
  environment:
    MYSQL_ROOT_PASSWORD: SecurePassword123!
    MYSQL_PASSWORD: AnotherSecurePassword456!

banking-app:
  environment:
    APP_JWT_SECRET: your-very-long-secure-secret-key-minimum-32-characters
```

### 2. Use .env File

Create `.env` file (don't commit to git):
```bash
MYSQL_ROOT_PASSWORD=SecurePassword123!
MYSQL_PASSWORD=AnotherSecurePassword456!
JWT_SECRET=your-very-long-secure-secret-key
```

Update `docker-compose.yml`:
```yaml
mysql:
  environment:
    MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD}
    MYSQL_PASSWORD: ${MYSQL_PASSWORD}
```

Run:
```bash
docker-compose --env-file .env up -d
```

### 3. Network Isolation

```yaml
networks:
  banking-network:
    driver: bridge
    driver_opts:
      com.docker.network.bridge.name: br_banking
```

### 4. Read-Only Filesystem

```yaml
banking-app:
  read_only: true
  tmpfs:
    - /tmp
    - /var/cache
```

---

## Monitoring

### View System Resource Usage

```bash
# Docker stats
docker stats

# Per container
docker stats banking-app
docker stats banking-mysql
```

### Log Aggregation

```bash
# View all logs
docker-compose logs

# Follow logs
docker-compose logs -f

# View logs from specific time
docker-compose logs --since 1h

# Last 100 lines
docker-compose logs --tail 100
```

### Health Check Status

```bash
# Check container health
docker ps --format "table {{.Names}}\t{{.Status}}"

# Example output:
# banking-mysql   Up 5 minutes (healthy)
# banking-app     Up 4 minutes (healthy)
```

---

## Backup & Restore

### Backup Database

```bash
# Backup to file
docker exec banking-mysql mysqldump -u banking_user -pbanking_password banking_db > backup.sql

# Verify backup
file backup.sql
```

### Restore Database

```bash
# Restore from file
docker exec -i banking-mysql mysql -u banking_user -pbanking_password banking_db < backup.sql

# Verify
docker exec banking-mysql mysql -u banking_user -pbanking_password banking_db -e "SELECT COUNT(*) FROM users;"
```

---

## Next Steps

1. **Local Testing**: Run `docker-compose up -d` and test locally
2. **CI/CD Integration**: Push image to registry
3. **Production Deployment**: Use cloud orchestration (Kubernetes, Docker Swarm)
4. **Monitoring**: Set up centralized logging and metrics
5. **Scaling**: Adjust replicas and resources based on load

---

**Status**: Docker configuration ready for development and production! 🐳

For questions, refer to:
- Docker Docs: https://docs.docker.com/
- Docker Compose: https://docs.docker.com/compose/
- Spring Boot Docker: https://spring.io/guides/gs/spring-boot-docker/
