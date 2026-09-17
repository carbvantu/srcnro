$srcBase = "c:\Users\vtson\Downloads\AWN_Version\AWN_Version\src"
$origBase = "c:\Users\vtson\Downloads\AWN_Version\AWN_Version\src_original"

$srcAll = Get-ChildItem $srcBase -Recurse -File
$origAll = Get-ChildItem $origBase -Recurse -File

$origMap = @{}
foreach ($f in $origAll) {
    $rel = $f.FullName.Substring($origBase.Length + 1)
    $origMap[$rel] = $f.Length
}

$srcMap = @{}
foreach ($f in $srcAll) {
    $rel = $f.FullName.Substring($srcBase.Length + 1)
    $srcMap[$rel] = $f.Length
}

Write-Host "=== DELETED FILES (in src_original but NOT in src) ==="
foreach ($k in ($origMap.Keys | Sort-Object)) {
    if (-not $srcMap.ContainsKey($k)) {
        Write-Host "DELETED: $k"
    }
}

Write-Host ""
Write-Host "=== NEW FILES (in src but NOT in src_original) ==="
foreach ($k in ($srcMap.Keys | Sort-Object)) {
    if (-not $origMap.ContainsKey($k)) {
        Write-Host "NEW: $k"
    }
}
