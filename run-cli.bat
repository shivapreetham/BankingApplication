@echo off
REM Banking System CLI Launcher
REM This script runs the Banking System in CLI mode

echo.
echo ========================================
echo   Banking System CLI Launcher
echo ========================================
echo.

REM Use Maven from packages folder
set "MAVEN_HOME=%~dp0packages\apache-maven-3.9.12"
set "MAVEN_CMD=%MAVEN_HOME%\bin\mvn.cmd"

if not exist "%MAVEN_CMD%" (
    echo ❌ Maven not found in packages folder
    echo Expected: %MAVEN_CMD%
    pause
    exit /b 1
)

echo ✓ Building application...
call "%MAVEN_CMD%" clean package -DskipTests -q

if errorlevel 1 (
    echo ❌ Build failed!
    pause
    exit /b 1
)

echo ✓ Build successful!
echo.
echo Starting Banking System CLI...
echo.

java -jar target\banking-system-2.0.0.jar --spring.profiles.active=cli

pause
