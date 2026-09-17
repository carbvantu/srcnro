$path = "c:\Users\vtson\Downloads\AWN_Version\AWN_Version\src\jbcd\ConnectResultSet.java"
$lines = [System.IO.File]::ReadAllLines($path)
Write-Host "Total lines: $($lines.Length)"
$start = -1
$end = -1
for ($i = 0; $i -lt $lines.Length; $i++) {
    if ($lines[$i] -match "rs\.last\(\);") {
        $start = $i
    }
    if ($start -ge 0 -and $lines[$i] -match "\+\+index;") {
        $end = $i + 2  # closing brace of while and try
        break
    }
}
Write-Host "Start: $start, End: $end"
if ($start -ge 0 -and $end -ge 0) {
    $before = $lines[0..($start - 1)]
    $after = $lines[($end + 1)..($lines.Length - 1)]
    $replacement = @'
            final ResultSetMetaData rsmd = rs.getMetaData();
            final int nColumn = rsmd.getColumnCount();
            java.util.List<Map<String, Object>> dataList = new java.util.ArrayList<>();
            java.util.List<Object[]> valuesList = new java.util.ArrayList<>();
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                Object[] rowVals = new Object[nColumn];
                for (int j = 1; j <= nColumn; ++j) {
                    final String tableName = rsmd.getTableName(j);
                    final String columnName = rsmd.getColumnName(j);
                    final Object columnValue = rs.getObject(j);
                    map.put(columnName.toLowerCase(), columnValue);
                    map.put(tableName.toLowerCase() + "." + columnName.toLowerCase(), columnValue);
                    rowVals[j - 1] = columnValue;
                }
                dataList.add(map);
                valuesList.add(rowVals);
            }
            int nRow = dataList.size();
            this.data = new HashMap[nRow];
            for (int i = 0; i < nRow; ++i) {
                this.data[i] = dataList.get(i);
            }
            this.values = valuesList.toArray(new Object[nRow][]);
        }
'@
    $replacementLines = $replacement -split "`r?`n"
    $newLines = $before + $replacementLines + $after
    [System.IO.File]::WriteAllLines($path, $newLines)
    Write-Host "SUCCESS: ConnectResultSet.java lines replaced!"
}
