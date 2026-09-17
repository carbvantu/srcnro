$exe = "C:\Program Files\7-Zip\7z.exe"
$rar = "C:\Users\vtson\Downloads\AWN_Version\rrrr.rar"
$lines = & $exe l $rar
$outCount = ($lines | Where-Object { $_ -match "AWN_Version\\out\\" -and $_ -notmatch "D\.\.\.\." }).Count
$buildCount = ($lines | Where-Object { $_ -match "AWN_Version\\build\\classes\\" -and $_ -notmatch "D\.\.\.\." }).Count
$srcCount = ($lines | Where-Object { $_ -match "AWN_Version\\src\\" -and $_ -notmatch "AWN_Version\\src_original\\" -and $_ -notmatch "D\.\.\.\." }).Count
Write-Host "In rrrr.rar:"
Write-Host "  out: $outCount classes"
Write-Host "  build/classes: $buildCount classes"
Write-Host "  src: $srcCount java files"
