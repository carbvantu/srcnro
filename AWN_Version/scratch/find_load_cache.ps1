$lines = Get-Content "c:\Users\vtson\Downloads\AWN_Version\AWN_Version\src\nro\server\DropItemPanel.java"
for ($i = 0; $i -lt $lines.Length; $i++) {
    if ($lines[$i] -match "loadCacheData") {
        Write-Host "$($i+1): $($lines[$i])"
    }
}
