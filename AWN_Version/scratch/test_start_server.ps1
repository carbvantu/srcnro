$proc = Start-Process java -ArgumentList "-Xms128m -Xmx2g -cp `"out;lib\*`" -Duser.dir=`"$pwd`" nro.server.ServerManager" -PassThru
Start-Sleep -Seconds 8
$conn = Get-NetTCPConnection -State Listen -LocalPort 14445 -ErrorAction SilentlyContinue
if ($conn) {
    Write-Host "SUCCESS: Port 14445 is LISTENING! Process Id: $($proc.Id)"
} else {
    Write-Host "FAILED: Port 14445 is NOT listening yet."
}
