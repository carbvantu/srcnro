$exe = "C:\Program Files\7-Zip\7z.exe"
$rar = "C:\Users\vtson\Downloads\AWN_Version\rrrr.rar"
$rarLines = & $exe l $rar

$subdirs = @{}
foreach ($l in $rarLines) {
    if ($l -match "AWN_Version\\src\\([^\\]+)") {
        $subdirs[$matches[1]] = $true
    }
}
Write-Host "Top dirs under src in rrrr.rar:"
$subdirs.Keys | Sort-Object | ForEach-Object {
    $rarCount = ($rarLines | Where-Object { $_ -match "AWN_Version\\src\\$_" }).Count
    $localPath = "c:\Users\vtson\Downloads\AWN_Version\AWN_Version\src\" + $_
    $localCount = 0
    if (Test-Path $localPath) {
        $localCount = (Get-ChildItem $localPath -Recurse -File).Count
    }
    Write-Host "  $_ -> Rar: $rarCount, Local: $localCount"
}
