Write-Host "=== Pahana Edu Bookshop MySQL Setup ===" -ForegroundColor Green
Write-Host ""

# Check if MySQL is running on port 3306
Write-Host "Checking if MySQL is running on port 3306..." -ForegroundColor Yellow
$mysqlProcess = Get-NetTCPConnection -LocalPort 3306 -ErrorAction SilentlyContinue

if (-not $mysqlProcess) {
    Write-Host "MySQL is not running on port 3306!" -ForegroundColor Red
    Write-Host ""
    Write-Host "Please start MySQL and run this script again." -ForegroundColor Yellow
    exit 1
} else {
    Write-Host "MySQL is running on port 3306! ✓" -ForegroundColor Green
}

Write-Host ""
Write-Host "Looking for MySQL client..." -ForegroundColor Yellow

# Find XAMPP MySQL client
$mysqlPaths = @(
    "C:\xampp\mysql\bin\mysql.exe",
    "C:\XAMPP\mysql\bin\mysql.exe", 
    "D:\xampp\mysql\bin\mysql.exe",
    "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe",
    "C:\Program Files (x86)\MySQL\MySQL Server 8.0\bin\mysql.exe"
)

$mysqlClient = $null
foreach ($path in $mysqlPaths) {
    if (Test-Path $path) {
        $mysqlClient = $path
        Write-Host "Found MySQL client at: $path" -ForegroundColor Green
        break
    }
}

if (-not $mysqlClient) {
    Write-Host "MySQL client not found in common locations!" -ForegroundColor Red
    Write-Host "Please make sure MySQL/XAMPP is installed properly." -ForegroundColor Yellow
    exit 1
}

Write-Host ""
Write-Host "Testing MySQL connection..." -ForegroundColor Yellow

# Test connection
$testResult = Start-Process -FilePath $mysqlClient -ArgumentList "-h", "localhost", "-P", "3306", "-u", "root", "--password=", "-e", "SELECT 'Connection successful!' as Status;" -Wait -PassThru -NoNewWindow -RedirectStandardOutput "test_output.txt" -RedirectStandardError "test_error.txt"

if ($testResult.ExitCode -eq 0) {
    Write-Host "MySQL connection successful! ✓" -ForegroundColor Green
    $output = Get-Content "test_output.txt" -ErrorAction SilentlyContinue
    if ($output) {
        Write-Host $output -ForegroundColor Cyan
    }
} else {
    Write-Host "MySQL connection failed!" -ForegroundColor Red
    $error = Get-Content "test_error.txt" -ErrorAction SilentlyContinue
    if ($error) {
        Write-Host "Error: $error" -ForegroundColor Red
    }
    Write-Host ""
    Write-Host "Please check:" -ForegroundColor Yellow
    Write-Host "1. MySQL is running" -ForegroundColor White
    Write-Host "2. Username is 'root' with no password" -ForegroundColor White
    Write-Host "3. MySQL is accessible from localhost on port 3306" -ForegroundColor White
    exit 1
}

# Clean up temp files
Remove-Item "test_output.txt" -ErrorAction SilentlyContinue
Remove-Item "test_error.txt" -ErrorAction SilentlyContinue

Write-Host ""
Write-Host "Setting up database schema..." -ForegroundColor Yellow

# Check schema file
$schemaPath = "database\schema.sql"
if (-not (Test-Path $schemaPath)) {
    Write-Host "Schema file not found at: $schemaPath" -ForegroundColor Red
    exit 1
}

# Setup database
Write-Host "Creating database and tables..." -ForegroundColor Yellow
$setupResult = Start-Process -FilePath $mysqlClient -ArgumentList "-h", "localhost", "-P", "3306", "-u", "root", "--password=", "-e", "source $schemaPath" -Wait -PassThru -NoNewWindow -RedirectStandardOutput "setup_output.txt" -RedirectStandardError "setup_error.txt"

if ($setupResult.ExitCode -eq 0) {
    Write-Host "Database schema setup completed successfully! ✓" -ForegroundColor Green
} else {
    Write-Host "Database setup failed!" -ForegroundColor Red
    $error = Get-Content "setup_error.txt" -ErrorAction SilentlyContinue
    if ($error) {
        Write-Host "Error: $error" -ForegroundColor Red
    }
    exit 1
}

# Clean up temp files
Remove-Item "setup_output.txt" -ErrorAction SilentlyContinue
Remove-Item "setup_error.txt" -ErrorAction SilentlyContinue

Write-Host ""
Write-Host "Verifying database setup..." -ForegroundColor Yellow

# Verify tables
$verifyResult = Start-Process -FilePath $mysqlClient -ArgumentList "-h", "localhost", "-P", "3306", "-u", "root", "--password=", "-e", "USE pahana_bookshop; SHOW TABLES;" -Wait -PassThru -NoNewWindow -RedirectStandardOutput "verify_output.txt" -RedirectStandardError "verify_error.txt"

if ($verifyResult.ExitCode -eq 0) {
    Write-Host "Database tables created successfully! ✓" -ForegroundColor Green
    Write-Host "Tables created:" -ForegroundColor Cyan
    $output = Get-Content "verify_output.txt" -ErrorAction SilentlyContinue
    if ($output) {
        Write-Host $output -ForegroundColor White
    }
} else {
    Write-Host "Error verifying database!" -ForegroundColor Red
    $error = Get-Content "verify_error.txt" -ErrorAction SilentlyContinue
    if ($error) {
        Write-Host "Error: $error" -ForegroundColor Red
    }
}

# Clean up temp files
Remove-Item "verify_output.txt" -ErrorAction SilentlyContinue
Remove-Item "verify_error.txt" -ErrorAction SilentlyContinue

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
Write-Host "1. Start the backend: .\start-backend-mysql.bat" -ForegroundColor White
Write-Host "2. Start the frontend: .\start-frontend.bat" -ForegroundColor White
Write-Host ""