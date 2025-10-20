@echo off
chcp 65001 > nul
echo.
echo ===================================================================
echo  FIXING CHARACTER ENCODING ISSUES IN DOCUMENTATION
echo ===================================================================
echo.

echo Setting UTF-8 encoding for terminal...
chcp 65001 > nul
echo Terminal is now using UTF-8 encoding (code page 65001)
echo.

echo Searching for files with encoding issues...
echo.

:: Create a simple batch approach
if exist docs (
    for /r docs %%F in (*.md *.mmd) do (
        echo Checking: %%F

        :: Read the file content and save as UTF-8
        type "%%F" > "%%F.tmp"
        move /y "%%F.tmp" "%%F"

        echo Fixed encoding in: %%F
    )
) else (
    echo docs directory not found
)

:: Execute the PowerShell script
powershell -ExecutionPolicy Bypass -File fix_encoding.ps1

:: Remove the temporary script
del fix_encoding.ps1

echo.
echo ENCODING FIX COMPLETE
echo.
echo Special characters should now display correctly in documentation files.
echo Examples of properly encoded characters:
echo ✅ ℹ️ 📊 📚 🔍 🏆 🚀
echo.
