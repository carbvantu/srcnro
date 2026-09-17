$searchPaths = @(
    "C:\Users\vtson\Downloads",
    "C:\Users\vtson\Desktop",
    "C:\Users\vtson\AppData\LocalLow"
)

foreach ($p in $searchPaths) {
    if (Test-Path $p) {
        Write-Host "Searching in $p..."
        Get-ChildItem -Path $p -Filter "*Dragon*" -Recurse -Depth 3 -ErrorAction SilentlyContinue | Select-Object FullName
        Get-ChildItem -Path $p -Filter "*Panel*" -Recurse -Depth 3 -ErrorAction SilentlyContinue | Select-Object FullName
        Get-ChildItem -Path $p -Filter "*Mod_Local*" -Recurse -Depth 4 -ErrorAction SilentlyContinue | Select-Object FullName
    }
}
