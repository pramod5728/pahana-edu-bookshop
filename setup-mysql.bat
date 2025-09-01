@echo off
echo Setting up MySQL Database for Pahana Edu Bookshop...
echo.

echo Make sure XAMPP MySQL is running before proceeding!
echo.
pause

echo Connecting to MySQL and running schema...
echo Default XAMPP MySQL credentials: root (no password)
echo.

mysql -h localhost -P 3306 -u root -p < "database/schema.sql"

if %ERRORLEVEL% EQU 0 (
    echo.
    echo Database setup completed successfully!
    echo Database: pahana_bookshop
    echo Default login: admin / admin123
) else (
    echo.
    echo Database setup failed. Please check:
    echo 1. XAMPP MySQL is running
    echo 2. MySQL credentials are correct
    echo 3. database/schema.sql exists
)

echo.
pause