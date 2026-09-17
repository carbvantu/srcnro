$log = "C:\Users\vtson\AppData\LocalLow\DefaultCompany\Panel Game\Player.log"
if (Test-Path $log) {
    Get-Content $log -Tail 100
} else {
    Write-Host "Player.log does not exist"
}
