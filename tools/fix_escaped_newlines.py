import sys
from pathlib import Path

root = Path(r"c:\Users\Pete\Desktop\documentor\src\test\java")
if not root.exists():
    print("Test sources folder not found:", root)
    sys.exit(1)

files_fixed = 0
for p in root.rglob("*.java"):
    text = p.read_text(encoding="utf-8")
    if "\\r\\n" in text or "\\n" in text:
        # heuristics: if the file contains many literal sequences and also contains backslash escapes at line ends,
        # replace them with real CRLFs
        new = text.replace("\\r\\n", "\r\n").replace("\\n", "\r\n")
        if new != text:
            bak = p.with_suffix(p.suffix + ".bak")
            bak.write_text(text, encoding="utf-8")
            p.write_text(new, encoding="utf-8", newline="\r\n")
            files_fixed += 1
            print("Fixed", p)

print("Files fixed:", files_fixed)
