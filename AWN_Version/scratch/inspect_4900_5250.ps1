$path = "C:\Users\vtson\.gemini\antigravity-ide\brain\86539d7f-1088-461f-acae-67be089e5d8c\.system_generated\logs\transcript.jsonl"
Get-Content $path | ForEach-Object {
    try {
        $d = $_ | ConvertFrom-Json
        if ($d.step_index -ge 4900 -and $d.step_index -le 5250) {
            if ($d.tool_calls) {
                foreach ($tc in $d.tool_calls) {
                    Write-Host "Step $($d.step_index): Tool=$($tc.name) Summary=$($tc.args.toolSummary) CMD=$($tc.args.CommandLine)"
                }
            }
        }
    } catch {}
}
