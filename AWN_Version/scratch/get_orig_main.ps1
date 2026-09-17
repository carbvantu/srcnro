$exe = "C:\Program Files\7-Zip\7z.exe"
$rar = "C:\Users\vtson\Downloads\AWN_Version\rrrr.rar"
$content = & $exe e $rar "AWN_Version\src\nro\server\ServerManager.java" -so
$lines = $content -split "`r?`n"
$mainIdx = -1
for ($i = 0; $i -lt $lines.Length; $i++) {
    if ($lines[$i] -match "public static void main") {
        $mainIdx = $i
        break
    }
}
if ($mainIdx -ge 0) {
    for ($i = $mainIdx - 5; $i -le [Math]::Min($mainIdx + 50, $lines.Length - 1); $i++) {
        Write-Host "$($i): $($lines[$i])"
    }
}
