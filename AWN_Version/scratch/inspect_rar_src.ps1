$exe = "C:\Program Files\7-Zip\7z.exe"
$rar = "C:\Users\vtson\Downloads\AWN_Version\rrrr.rar"
$lines = & $exe l $rar
$srcLines = $lines | Where-Object { $_ -match "AWN_Version\\src\\" }
Write-Host "Total src files in rrrr.rar: $($srcLines.Count)"
$srcLines | Select-Object -First 20
