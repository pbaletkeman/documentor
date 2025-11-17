@echo off
setlocal enabledelayedexpansion
echo Documentor Encoding Fix Master Script
echo ====================================
echo.
echo This script will fix all encoding-related issues in the Documentor project
echo.

REM Check if scripts exist, if not create them
if not exist "fix_encoding_cmd.ps1" (
    echo Creating fix_encoding_cmd.ps1...
    (
        echo # PowerShell script to fix Unicode character encoding issues in Windows command prompt
        echo Write-Host "Setting up proper Unicode character encoding for Windows Command Prompt"
        echo.
        echo # Set the console code page to UTF-8 ^(65001^)
        echo Write-Host "Setting console code page to UTF-8..."
        echo [System.Console]::OutputEncoding = [System.Text.Encoding]::UTF8
        echo.
        echo # Update registry settings for better Unicode support
        echo Write-Host "Updating registry settings for better Unicode support..."
        echo.
        echo try {
        echo     # Set default console code page to UTF-8
        echo     New-ItemProperty -Path "HKCU:\Console" -Name "CodePage" -Value 65001 -PropertyType DWORD -Force ^| Out-Null
        echo     Write-Host "Registry settings updated successfully."
        echo } catch {
        echo     Write-Host "Error updating registry settings: $_" -ForegroundColor Red
        echo     Write-Host "You may need to run this script as administrator."
        echo }
        echo.
        echo # Modify console properties
        echo Write-Host "Updating console properties..."
        echo $consoleInfo = Get-ItemProperty -Path "HKCU:\Console" -ErrorAction SilentlyContinue
        echo.
        echo if ^($null -ne $consoleInfo^) {
        echo     # Set font to a TrueType font that supports Unicode better
        echo     Set-ItemProperty -Path "HKCU:\Console" -Name "FaceName" -Value "Consolas" -Type String -Force
        echo     Write-Host "Console properties updated successfully."
        echo } else {
        echo     Write-Host "Could not access console properties." -ForegroundColor Yellow
        echo }
        echo.
        echo Write-Host "Unicode character encoding setup complete!" -ForegroundColor Green
        echo Write-Host "You may need to restart your command prompt or PowerShell for changes to take effect."
        echo Write-Host "Test unicode: ✅ ⚠️ ℹ️"
    ) > "fix_encoding_cmd.ps1"
    echo Created fix_encoding_cmd.ps1
)

if not exist "fix_encoding_cmd.bat" (
    echo Creating fix_encoding_cmd.bat...
    (
        echo @echo off
        echo echo Setting up proper Unicode character encoding for Windows Command Prompt
        echo echo.
        echo powershell -ExecutionPolicy Bypass -File "%%~dp0fix_encoding_cmd.ps1"
    ) > "fix_encoding_cmd.bat"
    echo Created fix_encoding_cmd.bat
)

if not exist "fix_batch_encoding.bat" (
    echo Creating fix_batch_encoding.bat...
    (
        echo @echo off
        echo setlocal enabledelayedexpansion
        echo.
        echo echo Updating batch files to use UTF-8 encoding
        echo echo.
        echo.
        echo REM Find all batch files in the current directory and subdirectories
        echo for /r %%f in ^(*.bat *.cmd^) do ^(
        echo     echo Processing: %%f
        echo     set "file=%%f"
        echo     set "content="
        echo
        echo     REM Check if file already has chcp 65001
        echo     findstr /C:"chcp 65001" "!file!" ^>nul
        echo     if errorlevel 1 ^(
        echo         REM File doesn't have chcp 65001, let's add it
        echo         echo   - Adding UTF-8 support to %%f
        echo
        echo         REM Create a temporary file
        echo         ^(
        echo             echo @echo off
        echo             echo chcp 65001 ^>nul
        echo             type "!file!" ^| findstr /V "@echo off"
        echo         ^) ^> "!file!.tmp"
        echo
        echo         REM Replace the original file
        echo         move /y "!file!.tmp" "!file!" ^>nul
        echo         echo   - Updated successfully
        echo     ^) else ^(
        echo         echo   - Already has UTF-8 support, skipping
        echo     ^)
        echo ^)
        echo.
        echo echo All batch files have been updated to use UTF-8 encoding
        echo echo.
        echo echo Press any key to exit...
        echo pause ^>nul
    ) > "fix_batch_encoding.bat"
    echo Created fix_batch_encoding.bat
)

echo.
echo All required scripts have been created
echo.
echo Step 1: Fixing Command Prompt Encoding...
call fix_encoding_cmd.bat
echo.
echo Step 2: Fixing Batch File Encoding...
call fix_batch_encoding.bat
echo.
echo Step 3: Fixing Documentor Run Files...
call fix_documentor_encoding.bat
echo.
echo Step 4: Fixing Configuration Files...
call fix_config_encoding.bat
echo.
echo Step 5: Fixing Gradle Build Files...
call fix_gradle_encoding.bat
echo.
echo ====================================
echo All encoding fixes have been applied!
echo.
echo To run Documentor with proper Unicode support:
echo   1. Close and reopen your command prompt
echo   2. Use the run-utf8.cmd script to run the application
echo   3. Or use gradlew.bat runEnhancedApp -Dfile.encoding=UTF-8
echo.
echo Special characters should now display correctly: ✅ ⚠️ ℹ️
echo.
echo Press any key to exit...
pause >nul
