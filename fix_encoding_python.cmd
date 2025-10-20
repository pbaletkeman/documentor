@echo off
chcp 65001 > nul

echo Creating Python script for encoding fix...
echo import os > fix_encoding.py
echo import re >> fix_encoding.py
echo def fix_file(file_path): >> fix_encoding.py
echo     print(f"Checking: {file_path}") >> fix_encoding.py
echo     try: >> fix_encoding.py
echo         with open(file_path, 'r', encoding='utf-8', errors='replace') as f: >> fix_encoding.py
echo             content = f.read() >> fix_encoding.py
echo         # Fix common encoding issues >> fix_encoding.py
echo         content = content.replace('ðŸ"š', '📚') >> fix_encoding.py
echo         content = content.replace('ðŸ"Š', '📊') >> fix_encoding.py
echo         content = content.replace('ðŸ"¦', '📦') >> fix_encoding.py
echo         content = content.replace('ðŸ"§', '🔧') >> fix_encoding.py
echo         content = content.replace('ðŸ"‹', '📋') >> fix_encoding.py
echo         with open(file_path, 'w', encoding='utf-8') as f: >> fix_encoding.py
echo             f.write(content) >> fix_encoding.py
echo         print(f"Fixed encoding in: {file_path}") >> fix_encoding.py
echo     except Exception as e: >> fix_encoding.py
echo         print(f"Error processing {file_path}: {e}") >> fix_encoding.py
echo >> fix_encoding.py
echo def process_directory(directory): >> fix_encoding.py
echo     for root, dirs, files in os.walk(directory): >> fix_encoding.py
echo         for file in files: >> fix_encoding.py
echo             if file.endswith(('.md', '.mmd')): >> fix_encoding.py
echo                 file_path = os.path.join(root, file) >> fix_encoding.py
echo                 fix_file(file_path) >> fix_encoding.py
echo >> fix_encoding.py
echo if os.path.exists('docs'): >> fix_encoding.py
echo     process_directory('docs') >> fix_encoding.py
echo else: >> fix_encoding.py
echo     print("docs directory not found") >> fix_encoding.py

echo Running encoding fix script...
python fix_encoding.py

echo Cleaning up...
del fix_encoding.py

echo.
echo ENCODING FIX COMPLETE
echo.
echo Special characters should now display correctly in documentation files.
echo Examples of properly encoded characters:
echo ✅ ℹ️ 📊 📚 🔍 🏆 🚀
echo.
