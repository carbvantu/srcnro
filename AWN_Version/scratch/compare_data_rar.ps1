$exe = "C:\Program Files\7-Zip\7z.exe"
$rar = "C:\Users\vtson\Downloads\AWN_Version\rrrr.rar"
$rarLines = & $exe l $rar

$rarFiles = @{}
foreach ($l in $rarLines) {
    if ($l -match "^\d{4}-\d{2}-\d{2}\s+\d{2}:\d{2}:\d{2}\s+[\.D][\.R][\.H][\.S][\.A]\s+(\d+)\s+\d+\s+(AWN_Version\\data\\.*)$") {
        $rarFiles[$matches[2].ToLower()] = [int64]$matches[1]
    }
}

$localData = Get-ChildItem "c:\Users\vtson\Downloads\AWN_Version\AWN_Version\data" -Recurse -File
Write-Host "Total data files in rar: $($rarFiles.Count)"
Write-Host "Total local data files: $($localData.Count)"

$diffCount = 0
foreach ($f in $localData) {
    $rel = "awn_version\" + $f.FullName.Substring("c:\Users\vtson\Downloads\AWN_Version\AWN_Version\".Length).ToLower()
    if ($rarFiles.ContainsKey($rel)) {
        if ($rarFiles[$rel] -ne $f.Length) {
            Write-Host "DIFF SIZE: $rel (rar=$($rarFiles[$rel]) vs local=$($f.Length))"
            $diffCount++
        }
    } else {
        Write-Host "NEW in local: $rel"
        $diffCount++
    }
}
Write-Host "Total diffs: $diffCount"
