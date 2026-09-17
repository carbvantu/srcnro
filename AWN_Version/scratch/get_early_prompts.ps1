$path = "C:\Users\vtson\.gemini\antigravity-ide\brain\86539d7f-1088-461f-acae-67be089e5d8c\.system_generated\logs\transcript.jsonl"
Get-Content $path | ForEach-Object {
    try {
        $d = $_ | ConvertFrom-Json
        if ($d.type -eq "USER_INPUT" -and $d.step_index -lt 4000) {
            Write-Host "Step $($d.step_index): $($d.content)"
        }
    } catch {}
}
