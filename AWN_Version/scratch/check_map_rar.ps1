$exe = "C:\Program Files\7-Zip\7z.exe"
$rar = "C:\Users\vtson\Downloads\AWN_Version\rrrr.rar"
$rarLines = & $exe l $rar

$rarMap = @{}
foreach ($l in $rarLines) {
    if ($l -match "^\d{4}-\d{2}-\d{2}\s+\d{2}:\d{2}:\d{2}\s+[\.D][\.R][\.H][\.S][\.A]\s+(\d+)\s+\d+\s+(AWN_Version\\data\\map\\.*)$") {
        $rarMap[$matches[2].ToLower()] = [int64]$matches[1]
    }
}

$localMap = Get-ChildItem "c:\Users\vtson\Downloads\AWN_Version\AWN_Version\data\map" -Recurse -File
Write-Host "Map in rar: $($rarMap.Count)"
Write-Host "Local map: $($localMap.Count)"

foreach ($k in $rarMap.Keys) {
    $localPath = "c:\Users\vtson\Downloads\AWN_Version\" + $k
    if (!(Test-Path $localPath)) {
        Write-Host "MISSING LOCAL: $k"
    }
}
