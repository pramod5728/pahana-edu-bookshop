@echo off
echo === Pahana Edu Bookshop MySQL Setup ===
echo.

echo Step 1: Checking for XAMPP installation...
echo.

REM Check common XAMPP installation paths
set XAMPP_PATH=
if exist "C:\xampp\xampp-control.exe" (
    set XAMPP_PATH=C:\xampp
    echo Found XAMPP at: C:\xampp
) else if exist "C:\XAMPP\xampp-control.exe" (
    set XAMPP_PATH=C:\XAMPP
    echo Found XAMPP at: C:\XAMPP
) else if exist "D:\xampp\xampp-control.exe" (
    set XAMPP_PATH=D:\xampp
    echo Found XAMPP at: D:\xampp
) else (
    echo XAMPP not found in common locations!
    echo Please install XAMPP or provide the correct path.
    echo.
    echo Download XAMPP from: https://www.apachefriends.org/download.html
    echo.
    pause
    exit /b 1
)

echo.
echo Step 2: Starting XAMPP Control Panel...
echo.
echo Please:
echo 1. Start MySQL service in XAMPP Control Panel
echo 2. Make sure MySQL is running on port 3306
echo 3. Come back to this window when MySQL is running
echo.

start "" "%XAMPP_PATH%\xampp-control.exe"

echo Waiting for you to start MySQL...
pause

echo.
echo Step 3: Testing MySQL connection...
echo.

REM Test MySQL connection
mysql -h localhost -P 3306 -u root --password= -e "SELECT 'MySQL is working!' as Status;"

if %ERRORLEVEL% EQU 0 (
    echo.
    echo MySQL connection successful!
    echo.
    echo Step 4: Setting up database...
    mysql -h localhost -P 3306 -u root --password= < "database\schema.sql"
    
    if %ERRORLEVEL% EQU 0 (
        echo.
        echo Database setup completed successfully!
        echo.
        echo Database: pahana_bookshop
        echo Default admin login: admin / admin123
        echo.
        echo You can now start the backend with: start-backend-mysql.bat
    ) else (
        echo Database setup failed!
    )
) else (
    echo.
    echo MySQL connection failed!
    echo Please make sure:
    echo 1. MySQL is running in XAMPP
    echo 2. MySQL is configured to run on port 3306
    echo 3. Username is 'root' with no password
)

echo.
pause