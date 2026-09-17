$pinfo = New-Object System.Diagnostics.ProcessStartInfo
$pinfo.FileName = "java"
$pinfo.Arguments = "-Xms128m -Xmx2g -cp `"out;lib\*`" -Duser.dir=`"$pwd`" nro.server.ServerManager"
$pinfo.RedirectStandardOutput = $true
$pinfo.RedirectStandardError = $true
$pinfo.UseShellExecute = $false
$pinfo.WorkingDirectory = "$pwd"

$p = New-Object System.Diagnostics.Process
$p.StartInfo = $pinfo
$p.Start() | Out-Null

$stdout = ""
$stderr = ""
$sw = [System.Diagnostics.Stopwatch]::StartNew()
while ($sw.ElapsedMilliseconds -lt 10000) {
    Start-Sleep -Milliseconds 500
    if ($p.HasExited) { break }
}

$stdout = $p.StandardOutput.ReadToEnd()
$stderr = $p.StandardError.ReadToEnd()

Write-Host "=== STDOUT ==="
Write-Host $stdout
Write-Host "=== STDERR ==="
Write-Host $stderr
Write-Host "=== PROCESS STATUS ==="
Write-Host "HasExited: $($p.HasExited)"
if (!$p.HasExited) {
    $p.Kill()
}
