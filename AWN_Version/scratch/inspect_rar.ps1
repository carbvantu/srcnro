$exe = "C:\Program Files\7-Zip\7z.exe"
$rar = "C:\Users\vtson\Downloads\AWN_Version\rrrr.rar"
$lines = & $exe l $rar
$folders = @{}
foreach ($line in $lines) {
    if ($line -match "AWN_Version\\([^\\]+)") {
        $folders[$matches[1]] = $true
    }
}
$folders.Keys | Sort-Object
