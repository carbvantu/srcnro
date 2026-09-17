Get-ChildItem -Path "C:\Users\vtson\Downloads\AWN_Version" -Include *.png, *.jpg, *.jpeg -Recurse | Where-Object { 
    $_.FullName -notmatch "data\\icon|data\\effect|htdocs" -and $_.Length -gt 50000 
} | Select-Object FullName, Length, LastWriteTime | Format-Table -AutoSize
