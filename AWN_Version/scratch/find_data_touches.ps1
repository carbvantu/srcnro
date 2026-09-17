$path = "C:\Users\vtson\.gemini\antigravity-ide\brain\86539d7f-1088-461f-acae-67be089e5d8c\.system_generated\logs\transcript.jsonl"
Get-Content $path | ForEach-Object {
    try {
        $d = $_ | ConvertFrom-Json
        if ($d.tool_calls) {
            foreach ($tc in $d.tool_calls) {
                $cmd = $tc.args.CommandLine
                $target = $tc.args.TargetFile
                if (($cmd -and ($cmd -match "data[\\/]" -or $cmd -match "map" -or $cmd -match "res")) -or ($target -and ($target -match "data[\\/]" -or $target -match "map"))) {
                    Write-Host "Step $($d.step_index): $($tc.name) -> $target | $cmd"
                }
            }
        }
    } catch {}
}
