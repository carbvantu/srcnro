$exe = "C:\Program Files\7-Zip\7z.exe"
$rar = "C:\Users\vtson\Downloads\AWN_Version\rrrr.rar"
$rarLines = & $exe l $rar
$rarHtdocs = ($rarLines | Where-Object { $_ -match "AWN_Version\\htdocs" }).Count
$localHtdocs = (Get-ChildItem "c:\Users\vtson\Downloads\AWN_Version\AWN_Version\htdocs" -Recurse -File).Count
Write-Host "Htdocs in rar: $rarHtdocs, local: $localHtdocs"
