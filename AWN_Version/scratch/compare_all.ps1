$srcBase = "c:\Users\vtson\Downloads\AWN_Version\AWN_Version\src"
$origBase = "c:\Users\vtson\Downloads\AWN_Version\AWN_Version\src_original"

$srcAll = Get-ChildItem $srcBase -Recurse -File | ForEach-Object { 
    @{ RelPath = $_.FullName.Replace($srcBase + "\", ""); Length = $_.Length }
}
$origAll = Get-ChildItem $origBase -Recurse -File | ForEach-Object { 
    @{ RelPath = $_.FullName.Replace($origBase + "\", ""); Length = $_.Length }
}

$origMap = @{}
foreach ($f in $origAll) { $origMap[$f.RelPath] = $f.Length }
$srcMap = @{}
foreach ($f in $srcAll) { $srcMap[$f.RelPath] = $f.Length }

$changedCount = 0
$deletedCount = 0
$newCount = 0

Write-Host "=== DELETED FILES (in original but NOT in current src) ==="
foreach ($key in $origMap.Keys) {
    if (-not $srcMap.ContainsKey($key)) {
        Write-Host $key
        $deletedCount++
    }
}

Write-Host ""
Write-Host "=== NEW FILES (in current src but NOT in original) ==="
foreach ($key in $srcMap.Keys) {
    if (-not $origMap.ContainsKey($key)) {
        Write-Host $key
        $newCount++
    }
}

Write-Host ""
Write-Host "=== CHANGED FILES COUNT (size differs) ==="
foreach ($key in $origMap.Keys) {
    if ($srcMap.ContainsKey($key) -and $srcMap[$key] -ne $origMap[$key]) {
        $changedCount++
    }
}
Write-Host "Changed: $changedCount"
Write-Host "Deleted: $deletedCount"
Write-Host "New: $newCount"
Write-Host "Total original: $($origMap.Count)"
Write-Host "Total current: $($srcMap.Count)"
