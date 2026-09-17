$srcDir = "c:\Users\vtson\Downloads\AWN_Version\AWN_Version\src\nro\server"
$origDir = "c:\Users\vtson\Downloads\AWN_Version\AWN_Version\src_original\nro\server"

$srcFiles = Get-ChildItem "$srcDir\*.java" | Select-Object Name, Length
$origFiles = Get-ChildItem "$origDir\*.java" | Select-Object Name, Length

Write-Host "=== FILES ONLY IN CURRENT src (not in original) ==="
foreach ($f in $srcFiles) {
    $found = $false
    foreach ($o in $origFiles) {
        if ($o.Name -eq $f.Name) { $found = $true; break }
    }
    if (-not $found) { Write-Host $f.Name }
}

Write-Host ""
Write-Host "=== FILES ONLY IN ORIGINAL (deleted from current) ==="
foreach ($f in $origFiles) {
    $found = $false
    foreach ($o in $srcFiles) {
        if ($o.Name -eq $f.Name) { $found = $true; break }
    }
    if (-not $found) { Write-Host $f.Name }
}

Write-Host ""
Write-Host "=== FILES WITH SIZE DIFFERENCES ==="
foreach ($orig in $origFiles) {
    foreach ($curr in $srcFiles) {
        if ($curr.Name -eq $orig.Name -and $curr.Length -ne $orig.Length) {
            $diff = $curr.Length - $orig.Length
            Write-Host "$($orig.Name): orig=$($orig.Length) curr=$($curr.Length) diff=$($diff)"
        }
    }
}
