$files = Get-ChildItem src -Recurse -Filter "*.java"
$matches = @()
foreach ($f in $files) {
    $content = Get-Content $f.FullName -Raw -Encoding UTF8
    if ($content -match "tặng quà" -or $content -match "tangqua" -or $content -match "tang_qua" -or $content -match "gift" -or $content -match "Gift") {
        $matches += $f.FullName
    }
}
$matches | ForEach-Object { Write-Host $_ }
