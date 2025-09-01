Write-Host "=== Pahana Edu Bookshop MySQL Setup ===" -ForegroundColor Green
Write-Host ""

# Check if MySQL is running on port 3306
Write-Host "Checking if MySQL is running on port 3306..." -ForegroundColor Yellow
$mysqlProcess = Get-NetTCPConnection -LocalPort 3306 -ErrorAction SilentlyContinue

if (-not $mysqlProcess) {
    Write-Host "MySQL is not running on port 3306!" -ForegroundColor Red
    Write-Host ""
    Write-Host "Please start XAMPP and make sure MySQL is running on port 3306" -ForegroundColor Yellow
    Write-Host "Steps:" -ForegroundColor Cyan
    Write-Host "1. Open XAMPP Control Panel" -ForegroundColor White
    Write-Host "2. Start MySQL service" -ForegroundColor White
    Write-Host "3. Verify it's running on port 3306" -ForegroundColor White
    Write-Host ""
    Write-Host "Please start MySQL manually and run this script again." -ForegroundColor Yellow
    exit 1
} else {
    Write-Host "MySQL is running on port 3306! " -ForegroundColor Green
}

Write-Host ""
Write-Host "Testing MySQL connection..." -ForegroundColor Yellow

# Test connection with simple command
$testResult = cmd /c "mysql -h localhost -P 3306 -u root --password= -e `"SELECT 1;`"" 2>&1
if ($LASTEXITCODE -eq 0) {
    Write-Host "MySQL connection successful!" -ForegroundColor Green
} else {
    Write-Host "MySQL connection failed: $testResult" -ForegroundColor Red
    Write-Host ""
    Write-Host "Please check:" -ForegroundColor Yellow
    Write-Host "1. MySQL is running on port 3306" -ForegroundColor White
    Write-Host "2. Username is 'root' with no password" -ForegroundColor White
    Write-Host "3. MySQL is accessible from localhost" -ForegroundColor White
    exit 1
}

Write-Host ""
Write-Host "Setting up database schema..." -ForegroundColor Yellow

# Check if schema file exists
$schemaPath = "database\schema.sql"
if (-not (Test-Path $schemaPath)) {
    Write-Host "Schema file not found at: $schemaPath" -ForegroundColor Red
    exit 1
}

# Run schema setup
Write-Host "Creating database and tables..." -ForegroundColor Yellow
$setupResult = cmd /c "mysql -h localhost -P 3306 -u root --password= < `"$schemaPath`"" 2>&1
if ($LASTEXITCODE -eq 0) {
    Write-Host "Database schema setup completed successfully!" -ForegroundColor Green
} else {
    Write-Host "Database schema setup failed: $setupResult" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "Verifying database setup..." -ForegroundColor Yellow

# Verify tables were created
$verifyResult = cmd /c "mysql -h localhost -P 3306 -u root --password= -e `"USE pahana_bookshop; SHOW TABLES;`"" 2>&1
if ($LASTEXITCODE -eq 0) {
    Write-Host "Database tables created successfully!" -ForegroundColor Green
    Write-Host "Tables created:" -ForegroundColor Cyan
    Write-Host $verifyResult -ForegroundColor White
} else {
    Write-Host "Error verifying database: $verifyResult" -ForegroundColor Red
}

Write-Host ""
Write-Host "=== Setup Complete! ===" -ForegroundColor Green
Write-Host ""
Write-Host "Database Information:" -ForegroundColor Cyan
Write-Host "- Database Name: pahana_bookshop" -ForegroundColor White
Write-Host "- Host: localhost" -ForegroundColor White
Write-Host "- Port: 3306" -ForegroundColor White
Write-Host "- Username: root" -ForegroundColor White
Write-Host "- Password: (empty)" -ForegroundColor White
Write-Host ""
Write-Host "Default Login Credentials:" -ForegroundColor Cyan
Write-Host "- Username: admin" -ForegroundColor White
Write-Host "- Password: admin123" -ForegroundColor White
Write-Host ""
Write-Host "Next steps:" -ForegroundColor Yellow
Write-Host "1. Start the backend with MySQL profile: .\start-backend-mysql.bat" -ForegroundColor White
Write-Host "2. Start the frontend: .\start-frontend.bat" -ForegroundColor White
Write-Host ""