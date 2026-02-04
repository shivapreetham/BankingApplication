#!/bin/bash
# Banking System CLI Launcher
# This script runs the Banking System in CLI mode

echo ""
echo "╔════════════════════════════════════════╗"
echo "║  Banking System CLI Launcher           ║"
echo "╚════════════════════════════════════════╝"
echo ""

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven is not installed or not in PATH"
    echo "Please install Maven from https://maven.apache.org/"
    exit 1
fi

echo "✓ Building application..."
mvn clean package -DskipTests -q

if [ $? -ne 0 ]; then
    echo "❌ Build failed!"
    exit 1
fi

echo "✓ Build successful!"
echo ""
echo "Starting Banking System CLI..."
echo ""

java -jar target/banking-system-*.jar --spring.profiles.active=cli
