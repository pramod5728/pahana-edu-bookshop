@echo off
echo Starting Pahana Edu Bookshop Frontend...
echo.

REM Navigate to frontend directory
cd /d "c:\Users\Pramod Dilshan\pahana-edu-bookshop\frontend"

REM Check if Node.js is available
where node >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo Node.js not found. Please install Node.js first.
    pause
    exit /b 1
)

REM Check if node_modules exists
if not exist "node_modules" (
    echo Installing dependencies...
    npm install
    echo.
)

REM Start the React development server
echo Starting React development server...
echo.
echo Frontend will be available at: http://localhost:3000
echo Make sure the backend is running at: http://localhost:8080/api
echo.
echo Default login credentials:
echo Username: admin
echo Password: admin123
echo.

npm start

pause