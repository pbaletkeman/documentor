@echo off
setlocal

echo Fixing Documentor run files for better Unicode handling
echo.

REM Check if run-enhanced.cmd exists, if so, update it
if exist "run-enhanced.cmd" (
    echo Updating run-enhanced.cmd with UTF-8 encoding support
    (
        echo @echo off
        echo chcp 65001 ^>nul
        echo REM Run Documentor with enhanced UTF-8 support
        echo.
        echo gradlew.bat runEnhancedApp %*
    ) > "run-enhanced.cmd"
    echo Updated run-enhanced.cmd
)

REM Check if run-app.cmd exists, if so, update it
if exist "run-app.cmd" (
    echo Updating run-app.cmd with UTF-8 encoding support
    (
        echo @echo off
        echo chcp 65001 ^>nul
        echo REM Run Documentor with UTF-8 support
        echo.
        echo gradlew.bat runApp %*
    ) > "run-app.cmd"
    echo Updated run-app.cmd
)

REM If neither exists, create a new run file
if not exist "run-enhanced.cmd" if not exist "run-app.cmd" (
    echo Creating new run-documentor.cmd with UTF-8 encoding support
    (
        echo @echo off
        echo chcp 65001 ^>nul
        echo REM Run Documentor with enhanced UTF-8 support
        echo.
        echo gradlew.bat runEnhancedApp %*
    ) > "run-documentor.cmd"
    echo Created run-documentor.cmd
)

echo.
echo Documentor run files have been updated with UTF-8 encoding support
echo This will fix display issues with special characters and emoji
echo.
echo Press any key to exit...
pause >nul
