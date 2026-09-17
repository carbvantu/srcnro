$log = "C:\Users\vtson\AppData\LocalLow\DefaultCompany\Panel Game\Player.log"
Get-Content $log | Where-Object { $_ -match "Exception|Error|connect|load|fail|socket|ip|port|msg|scene" } | Select-Object -Last 50
