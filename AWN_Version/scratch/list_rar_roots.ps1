$exe = "C:\Program Files\7-Zip\7z.exe"
$rar = "C:\Users\vtson\Downloads\AWN_Version\rrrr.rar"
$lines = & $exe l $rar
$roots = @{}
foreach ($l in $lines) {
    if ($l -match "^\d{4}-\d{2}-\d{2}\s+\d{2}:\d{2}:\d{2}\s+[\.D][\.R][\.H][\.S][\.A]\s+\d+\s+\d+\s+(.*)$") {
        $path = $matches[1]
        $firstPart = ($path -split "[\\/]")[0]
        $roots[$firstPart] = $true
    }
}
$roots.Keys | Sort-Object
