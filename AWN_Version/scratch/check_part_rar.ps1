$exe = "C:\Program Files\7-Zip\7z.exe"
$rar = "C:\Users\vtson\Downloads\AWN_Version\rrrr.rar"
$rarLines = & $exe l $rar
$rarPart = ($lines | Where-Object { $_ -match "AWN_Version\\part" }).Count
$localPart = (Get-ChildItem "c:\Users\vtson\Downloads\AWN_Version\AWN_Version\part" -Recurse -File).Count
Write-Host "Part in rar: $rarPart, local: $localPart"
