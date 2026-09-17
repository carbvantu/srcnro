$psi = New-Object System.Diagnostics.ProcessStartInfo
$psi.FileName = "java"
$psi.Arguments = "-Xms128m -Xmx3g -Xss256k -cp `"out;lib\*`" nro.server.ServerManager"
$psi.WorkingDirectory = "c:\Users\vtson\Downloads\AWN_Version\AWN_Version"
$psi.UseShellExecute = $true
$psi.WindowStyle = [System.Diagnostics.ProcessWindowStyle]::Normal

$proc = [System.Diagnostics.Process]::Start($psi)
Write-Host "Started ServerManager with PID $($proc.Id)"
