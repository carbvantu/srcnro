$exe = "C:\Program Files\7-Zip\7z.exe"
$rar = "C:\Users\vtson\Downloads\AWN_Version\rrrr.rar"
$lines = & $exe l $rar

$rarSrc = @{}
foreach ($l in $lines) {
    if ($l -match "^\d{4}-\d{2}-\d{2}\s+\d{2}:\d{2}:\d{2}\s+[\.D][\.R][\.H][\.S][\.A]\s+(\d+)\s+\d+\s+(AWN_Version\\src\\.*\.java)$") {
        $rarSrc[$matches[2].ToLower()] = [int64]$matches[1]
    }
}

$localSrc = Get-ChildItem "c:\Users\vtson\Downloads\AWN_Version\AWN_Version\src" -Recurse -Filter "*.java"
Write-Host "Total in rar src: $($rarSrc.Count)"
Write-Host "Total in local src: $($localSrc.Count)"

$diffs = @()
$news = @()

foreach ($f in $localSrc) {
    $rel = "awn_version\" + $f.FullName.Substring("c:\Users\vtson\Downloads\AWN_Version\AWN_Version\".Length).ToLower()
    if ($rarSrc.ContainsKey($rel)) {
        if ($rarSrc[$rel] -ne $f.Length) {
            $diffs += [PSCustomObject]@{
                Path = $rel
                RarSize = $rarSrc[$rel]
                LocalSize = $f.Length
            }
        }
    } else {
        $news += $rel
    }
}

Write-Host "`n=== MODIFIED FILES ($($diffs.Count)) ==="
$diffs | Format-Table -AutoSize

Write-Host "`n=== NEW FILES ($($news.Count)) ==="
$news
