$exe = "C:\Program Files\7-Zip\7z.exe"
$rar = "C:\Users\vtson\Downloads\AWN_Version\rrrr.rar"
$content = & $exe e $rar "AWN_Version\src\nro\server\ServerManagerUI.java" -so
$lines = $content -split "`r?`n"
$matches = $lines | Select-String -Pattern "run\(\)|ServerManager"
$matches | Select-Object -First 30
