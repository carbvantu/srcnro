$exe = "C:\Program Files\7-Zip\7z.exe"
$rar = "C:\Users\vtson\Downloads\AWN_Version\rrrr.rar"
$rarLines = & $exe l $rar

$folders = @("AWN_Version\data", "AWN_Version\htdocs", "AWN_Version\lib", "AWN_Version\part", "AWN_Version\sql", "AWN_Version\src")

foreach ($fld in $folders) {
    $rarCount = ($rarLines | Where-Object { $_ -match [regex]::Escape($fld) -and $_ -notmatch "D\.\.\.\." }).Count
    $localPath = "c:\Users\vtson\Downloads\AWN_Version\" + $fld
    $localCount = 0
    if (Test-Path $localPath) {
        $localCount = (Get-ChildItem $localPath -Recurse -File).Count
    }
    Write-Host "$fld -> Rar: $rarCount, Local: $localCount"
}
