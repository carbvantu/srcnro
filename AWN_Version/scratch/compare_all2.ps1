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

$changedFiles = @()
$deletedFiles = @()
$newFiles = @()

foreach ($key in $origMap.Keys) {
    if (-not $srcMap.ContainsKey($key)) {
        $deletedFiles += $key
    } elseif ($srcMap[$key] -ne $origMap[$key]) {
        $changedFiles += "$key (orig=$($origMap[$key]) curr=$($srcMap[$key]))"
    }
}

foreach ($key in $srcMap.Keys) {
    if (-not $origMap.ContainsKey($key)) {
        $newFiles += $key
    }
}

Write-Host "=== DELETED FILES (in original but NOT in current src) ==="
if ($deletedFiles.Count -eq 0) { Write-Host "(none)" }
foreach ($f in $deletedFiles) { Write-Host $f }

Write-Host ""
Write-Host "=== NEW FILES (in current src but NOT in original) ==="
if ($newFiles.Count -eq 0) { Write-Host "(none)" }
foreach ($f in $newFiles) { Write-Host $f }

Write-Host ""
Write-Host "=== CHANGED FILES (same path, different size) ==="
if ($changedFiles.Count -eq 0) { Write-Host "(none)" }
foreach ($f in $changedFiles) { Write-Host $f }

Write-Host ""
Write-Host "=== SUMMARY ==="
Write-Host "Total original: $($origMap.Count)"
Write-Host "Total current: $($srcMap.Count)"
Write-Host "Changed: $($changedFiles.Count)"
Write-Host "Deleted: $($deletedFiles.Count)"
Write-Host "New: $($newFiles.Count)"
