import json

path = r'C:\Users\vtson\.gemini\antigravity-ide\brain\86539d7f-1088-461f-acae-67be089e5d8c\.system_generated\logs\transcript.jsonl'
with open(path, 'r', encoding='utf-8') as f:
    for line in f:
        try:
            d = json.loads(line)
            if d.get('type') == 'USER_INPUT':
                print(f"Step {d.get('step_index')}: {d.get('content')}")
        except Exception:
            pass
