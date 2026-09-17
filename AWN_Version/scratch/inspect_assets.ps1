$baseDir = "c:\Users\vtson\Downloads\AWN_Version\file ảnh"
Write-Output "=== DIRECTORIES ==="
Get-ChildItem -LiteralPath $baseDir -Directory | ForEach-Object {
    Write-Output "Dir: $($_.Name)"
}

Write-Output "`n=== NON-PNG FILES ==="
Get-ChildItem -LiteralPath $baseDir -Recurse -File | Where-Object { $_.Extension -ne ".png" } | ForEach-Object {
    Write-Output "$($_.FullName) ($($_.Length) bytes)"
}

Write-Output "`n=== SUMMARY OF SUBFOLDERS ==="
Get-ChildItem -LiteralPath $baseDir -Directory | ForEach-Object {
    $d = $_
    $count = (Get-ChildItem -LiteralPath $d.FullName -Recurse -File).Count
    Write-Output "$($d.Name): $count files"
}
