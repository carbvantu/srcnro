$client = New-Object System.Net.Sockets.TcpClient
$client.Connect("127.0.0.1", 14445)
Write-Host "Connected: $($client.Connected)"
$stream = $client.GetStream()
# The client sends CMD_GET_KEY (-27) or wait for server
# In NRO protocol, server might send key first or client sends key
Start-Sleep -Milliseconds 500
if ($stream.DataAvailable) {
    $buffer = New-Object byte[] 1024
    $read = $stream.Read($buffer, 0, $buffer.Length)
    Write-Host "Received bytes: $read"
    Write-Host ($buffer[0..($read-1)] -join " ")
} else {
    Write-Host "No data received yet from server"
}
$client.Close()
