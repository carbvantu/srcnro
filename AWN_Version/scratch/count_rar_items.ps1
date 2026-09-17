$exe = "C:\Program Files\7-Zip\7z.exe"
$rar = "C:\Users\vtson\Downloads\AWN_Version\rrrr.rar"
$lines = & $exe l $rar
$patterns = @("data\\map", "data\\res", "data\\icon", "data\\img", "Mod_Local", "sql", "htdocs")
foreach ($p in $patterns) {
    $count = ($lines | Where-Object { $_ -match $p }).Count
    Write-Host "$p : $count files"
}
