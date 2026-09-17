$exe = "C:\Program Files\7-Zip\7z.exe"
$rar = "C:\Users\vtson\Downloads\AWN_Version\rrrr.rar"
$lines = & $exe l $rar
$lines | Where-Object { $_ -match "Mod_Local|Panel|login|bg|goku|super|background" } | Select-Object -First 40
