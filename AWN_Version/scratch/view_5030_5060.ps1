$path = "C:\Users\vtson\.gemini\antigravity-ide\brain\86539d7f-1088-461f-acae-67be089e5d8c\.system_generated\logs\transcript.jsonl"
Get-Content $path | ForEach-Object {
    try {
        $d = $_ | ConvertFrom-Json
        if ($d.step_index -ge 5030 -and $d.step_index -le 5060) {
            Write-Host "Step $($d.step_index): $($d.type) - $($d.tool_calls[0].name) - $($d.tool_calls[0].args.CommandLine)"
        }
    } catch {}
}
