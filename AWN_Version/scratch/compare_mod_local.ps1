$exe = "C:\Program Files\7-Zip\7z.exe"
$rar = "C:\Users\vtson\Downloads\AWN_Version\rrrr.rar"
$rarModLocal = & $exe l $rar | Where-Object { $_ -match "Mod_Local" }

$localFiles = Get-ChildItem "C:\Users\vtson\Downloads\AWN_Version\Mod_Local" -Recurse -File
Write-Host "Local Mod_Local files count: $($localFiles.Count)"
foreach ($f in $localFiles) {
    $rel = $f.FullName.Substring("C:\Users\vtson\Downloads\AWN_Version\".Length)
    $match = $rarModLocal | Where-Object { $_ -match [regex]::Escape($rel) }
    if ($match) {
        Write-Host "Match: $rel (Local: $($f.Length) bytes)"
    } else {
        Write-Host "NOT in RAR: $rel"
    }
}
