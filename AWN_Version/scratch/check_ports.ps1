Get-NetTCPConnection -State Listen | Where-Object { $_.LocalPort -in @(3306, 14445, 80, 8080) } | Select-Object LocalAddress, LocalPort, OwningProcess
