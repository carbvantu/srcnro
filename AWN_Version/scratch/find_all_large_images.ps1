$dirs = @("C:\Users\vtson\Downloads", "C:\Users\vtson\Desktop", "C:\Users\vtson\AppData\LocalLow")
$results = @()
foreach ($d in $dirs) {
    if (Test-Path $d) {
        $files = Get-ChildItem -Path $d -Include *.png, *.jpg, *.jpeg -Recurse -ErrorAction SilentlyContinue | Where-Object { 
            $_.Length -gt 100000 -and $_.FullName -notmatch "node_modules|\.git" 
        }
        foreach ($f in $files) {
            $results += [PSCustomObject]@{
                FullName = $f.FullName
                Length = $f.Length
                LastWriteTime = $f.LastWriteTime
            }
        }
    }
}
$results | Sort-Object Length -Descending | Select-Object -First 30 | Format-Table -AutoSize
