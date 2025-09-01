@echo off
echo === Pahana Edu Bookshop MySQL Setup (Port 3306) ===
echo.

echo Step 1: Checking for XAMPP MySQL installation...
echo.

REM Check common MySQL client paths
set MYSQL_CLIENT=
if exist "C:\xampp\mysql\bin\mysql.exe" (
    set MYSQL_CLIENT=C:\xampp\mysql\bin\mysql.exe
    echo Found MySQL client at: C:\xampp\mysql\bin\mysql.exe
) else if exist "C:\XAMPP\mysql\bin\mysql.exe" (
    set MYSQL_CLIENT=C:\XAMPP\mysql\bin\mysql.exe
    echo Found MySQL client at: C:\XAMPP\mysql\bin\mysql.exe
) else if exist "D:\xampp\mysql\bin\mysql.exe" (
    set MYSQL_CLIENT=D:\xampp\mysql\bin\mysql.exe
    echo Found MySQL client at: D:\xampp\mysql\bin\mysql.exe
) else if exist "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" (
    set MYSQL_CLIENT=C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe
    echo Found MySQL client at: C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe
) else (
    echo MySQL client not found in common locations!
    echo Please make sure MySQL/XAMPP is installed properly.
    echo.
    pause
    exit /b 1
)

echo.
echo Step 2: Testing MySQL connection (Port 3306)...
echo.

REM Test MySQL connection
"%MYSQL_CLIENT%" -h localhost -P 3306 -u root --password= -e "SELECT 'MySQL Connection Successful!' as Status;"

if %ERRORLEVEL% EQU 0 (
    echo.
    echo MySQL connection successful!
    echo.
    echo Step 3: Setting up database schema...
    echo.
    
    REM Setup database
    "%MYSQL_CLIENT%" -h localhost -P 3306 -u root --password= < "database\schema.sql"
    
    if %ERRORLEVEL% EQU 0 (
        echo.
        echo Database setup completed successfully!
        echo.
        echo Step 4: Verifying database tables...
        echo.
        
        REM Verify tables
        "%MYSQL_CLIENT%" -h localhost -P 3306 -u root --password= -e "USE pahana_bookshop; SHOW TABLES;"
        
        echo.
        echo === Setup Complete! ===
        echo.
        echo Database Information:
        echo - Database Name: pahana_bookshop
        echo - Host: localhost
        echo - Port: 3306
        echo - Username: root
        echo - Password: (empty)
        echo.
        echo Default Login Credentials:
        echo - Username: admin
        echo - Password: admin123
        echo.
        echo Next steps:
        echo 1. Start the backend: start-backend-mysql.bat
        echo 2. Start the frontend: start-frontend.bat
        echo.
    ) else (
        echo Database schema setup failed!
        echo Please check the error messages above.
    )
) else (
    echo.
    echo MySQL connection failed!
    echo.
    echo Please check:
    echo 1. MySQL is running on port 3306
    echo 2. Username is 'root' with no password
    echo 3. MySQL is accessible from localhost
    echo.
    echo Common fixes:
    echo - Start XAMPP and ensure MySQL service is running
    echo - Check if MySQL is running on the correct port (3306)
    echo - Verify MySQL credentials (usually root with no password in XAMPP)
)

echo.
pause