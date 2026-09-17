$logFile = "c:\Users\vtson\Downloads\AWN_Version\AWN_Version\scratch\server_live.log"
if (Test-Path $logFile) { Remove-Item $logFile -Force }

$proc = Start-Process java -ArgumentList "-Xms128m -Xmx2g -cp `"out;lib\*`" -Duser.dir=`"$pwd`" nro.server.ServerManager" -RedirectStandardOutput $logFile -RedirectStandardError "c:\Users\vtson\Downloads\AWN_Version\AWN_Version\scratch\server_err.log" -PassThru

Write-Host "Started ServerManager, PID: $($proc.Id). Waiting for initialization..."

$listening = $false
for ($i = 0; $i -lt 60; $i++) {
    Start-Sleep -Seconds 1
    $conn = Get-NetTCPConnection -State Listen -LocalPort 14445 -ErrorAction SilentlyContinue
    if ($conn) {
        $listening = $true
        Write-Host "`n>>> SUCCESS! Server is LISTENING on port 14445 after $($i+1) seconds! <<<"
        break
    }
    if ($proc.HasExited) {
        Write-Host "`nServer process EXITED with code $($proc.ExitCode)!"
        break
    }
    Write-Host -NoNewline "."
}

Write-Host "`n=== LATEST 30 LINES OF SERVER LOG ==="
if (Test-Path $logFile) {
    Get-Content $logFile -Tail 30
}
if (Test-Path "c:\Users\vtson\Downloads\AWN_Version\AWN_Version\scratch\server_err.log") {
    Write-Host "`n=== STDERR LOG ==="
    Get-Content "c:\Users\vtson\Downloads\AWN_Version\AWN_Version\scratch\server_err.log" -Tail 20
}

if (!$listening -and !$proc.HasExited) {
    Write-Host "Server still initializing or failed to bind port. Killing process."
    $proc.Kill()
}
