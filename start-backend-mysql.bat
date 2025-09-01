@echo off
echo Starting Pahana Edu Bookshop Backend with MySQL...
echo.

REM Add Maven to PATH for this session
set "PATH=%PATH%;C:\Program Files\maven-mvnd-1.0.2-windows-amd64\maven-mvnd-1.0.2-windows-amd64\mvn\bin"

REM Navigate to backend directory
cd /d "c:\Users\Pramod Dilshan\pahana-edu-bookshop\backend"

REM Check if Maven is available
mvn --version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo Maven not found. Please check your Maven installation.
    echo Expected path: C:\Program Files\maven-mvnd-1.0.2-windows-amd64\maven-mvnd-1.0.2-windows-amd64\mvn\bin
    echo.
    pause
    exit /b 1
)

REM Build and run with Maven using MySQL profile
echo Building and starting the application with MySQL database...
echo Make sure XAMPP MySQL is running!
echo.
echo Backend will be available at http://localhost:8080/api
echo API Documentation at http://localhost:8080/api/swagger-ui.html
echo.
echo Default login credentials:
echo Username: admin
echo Password: admin123
echo.

mvn spring-boot:run "-Dspring.profiles.active=mysql"

pause