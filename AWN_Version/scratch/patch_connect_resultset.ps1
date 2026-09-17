$path = "c:\Users\vtson\Downloads\AWN_Version\AWN_Version\src\jbcd\ConnectResultSet.java"
$content = Get-Content $path -Raw

$oldCode = @'
        try {
            rs.last();
            final int nRow = rs.getRow();
            rs.beforeFirst();
            final ResultSetMetaData rsmd = rs.getMetaData();
            final int nColumn = rsmd.getColumnCount();
            this.data = new HashMap[nRow];
            for (int i = 0; i < this.data.length; ++i) {
                this.data[i] = new HashMap<>();
            }
            this.values = new Object[nRow][nColumn];
            int index = 0;
            while (rs.next()) {
                for (int j = 1; j <= nColumn; ++j) {
                    final String tableName = rsmd.getTableName(j);
                    final String columnName = rsmd.getColumnName(j);
                    final Object columnValue = rs.getObject(j);
                    this.data[index].put(columnName.toLowerCase(), columnValue);
                    this.data[index].put(tableName.toLowerCase() + "." + columnName.toLowerCase(), columnValue);
                    this.values[index][j - 1] = columnValue;
                }
                ++index;
            }
        }
'@

$newCode = @'
        try {
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

if ($content.Contains($oldCode)) {
    $content = $content.Replace($oldCode, $newCode)
    [System.IO.File]::WriteAllText($path, $content, [System.Text.Encoding]::UTF8)
    Write-Host "SUCCESS: ConnectResultSet.java replaced perfectly!"
} else {
    Write-Host "ERROR: Old code not found!"
}
