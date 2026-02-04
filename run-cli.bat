@echo off
REM Banking System CLI Launcher
REM This script runs the Banking System in CLI mode

echo.
echo ╔════════════════════════════════════════╗
echo ║  Banking System CLI Launcher           ║
echo ╚════════════════════════════════════════╝
echo.

REM Check if Maven is installed
mvn --version >nul 2>&1
if errorlevel 1 (
    echo ❌ Maven is not installed or not in PATH
    echo Please install Maven from https://maven.apache.org/
    pause
    exit /b 1
)

echo ✓ Building application...
call mvn clean package -DskipTests -q

if errorlevel 1 (
    echo ❌ Build failed!
    pause
    exit /b 1
)

echo ✓ Build successful!
echo.
echo Starting Banking System CLI...
echo.

java -jar target/banking-system-*.jar --spring.profiles.active=cli

pause
