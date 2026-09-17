$pinfo = New-Object System.Diagnostics.ProcessStartInfo
$pinfo.FileName = "java"
$pinfo.Arguments = "-Xms128m -Xmx3g -Xss256k -cp `"out;lib\*`" nro.server.ServerManager"
$pinfo.RedirectStandardOutput = $true
$pinfo.RedirectStandardError = $true
$pinfo.UseShellExecute = $false
$pinfo.WorkingDirectory = "c:\Users\vtson\Downloads\AWN_Version\AWN_Version"

$p = New-Object System.Diagnostics.Process
$p.StartInfo = $pinfo
$p.Start() | Out-Null

$stdout = ""
$stderr = ""
for ($i = 0; $i -lt 15; $i++) {
    Start-Sleep -Seconds 1
    if ($p.HasExited) { break }
}

$stdout = $p.StandardOutput.ReadToEnd()
$stderr = $p.StandardError.ReadToEnd()

Write-Host "ExitCode: $($p.ExitCode)"
Write-Host "STDOUT:`n$stdout"
Write-Host "STDERR:`n$stderr"
