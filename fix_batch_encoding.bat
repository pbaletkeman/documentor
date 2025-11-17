@echo off
setlocal enabledelayedexpansion

echo Updating batch scripts to use UTF-8 encoding...
echo.

REM Add the chcp command to all .cmd and .bat files that don't have it already
for %%f in (*.cmd *.bat) do (
    echo Checking %%f
    findstr /c:"chcp 65001" "%%f" >nul 2>&1
    if !errorlevel! neq 0 (
        echo Adding UTF-8 encoding support to %%f
        (
            echo @echo off
            echo chcp 65001 ^>nul
            type "%%f" | findstr /v "@echo off"
        ) > "%%f.new"
        move /y "%%f.new" "%%f" >nul
    ) else (
        echo %%f already has UTF-8 encoding support
    )
)

echo.
echo All batch files have been updated to support UTF-8 encoding
echo This will fix display issues with emoji characters like ✅ ❌ 📊
echo.
echo Press any key to exit...
pause >nul
