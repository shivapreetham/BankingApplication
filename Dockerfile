# Multi-stage build
FROM maven:3.8.1-openjdk-17 AS builder
WORKDIR /build
COPY pom.xml .
COPY src/ ./src/
RUN mvn clean package -DskipTests

FROM openjdk:17-alpine
WORKDIR /app

# Install curl for health checks
RUN apk add --no-cache curl

# Copy JAR from builder stage
COPY --from=builder /build/target/banking-system-*.jar app.jar

# Create non-root user
RUN addgroup -g 1000 appuser && \
    adduser -D -u 1000 -G appuser appuser && \
    chown -R appuser:appuser /app

USER appuser

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD curl -f http://localhost:8080/api/auth/validate/test || exit 1

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
