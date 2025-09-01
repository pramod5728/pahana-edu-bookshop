$loginData = @{
    username = "employee"
    password = "employee123"
} | ConvertTo-Json

$headers = @{
    "Content-Type" = "application/json"
    "Origin" = "http://localhost:3002"
}

try {
    Write-Host "Testing LOGIN POST request..."
    $response = Invoke-WebRequest -Uri "http://localhost:8080/api/auth/login" -Method POST -Headers $headers -Body $loginData
    Write-Host "SUCCESS! Status Code: $($response.StatusCode)"
    Write-Host "Response Headers:"
    $response.Headers | ForEach-Object { Write-Host "$($_.Key): $($_.Value)" }
    Write-Host "Response: $($response.Content)"
} catch {
    Write-Host "POST Error: $($_.Exception.Message)"
    if ($_.Exception.Response) {
        $responseStream = $_.Exception.Response.GetResponseStream()
        $reader = New-Object System.IO.StreamReader($responseStream)
        $responseBody = $reader.ReadToEnd()
        Write-Host "Error Response: $responseBody"
    }
}