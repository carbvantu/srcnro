Get-ChildItem -Path "c:\Users\vtson\Downloads\AWN_Version\AWN_Version" -Filter "*.properties" -Recurse | Select-Object FullName
Get-ChildItem -Path "c:\Users\vtson\Downloads\AWN_Version\AWN_Version" -Filter "*.json" -Recurse | Where-Object { $_.FullName -notmatch "node_modules|\.system_generated" } | Select-Object FullName
Get-ChildItem -Path "c:\Users\vtson\Downloads\AWN_Version\AWN_Version" -Filter "*.txt" -Recurse | Where-Object { $_.FullName -notmatch "scratch" } | Select-Object FullName
