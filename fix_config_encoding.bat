@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

echo Documentor Configuration Encoding Fixer
echo ======================================
echo.
echo This script will fix encoding issues in configuration files
echo.

REM Check for JSON files in the current directory
echo Scanning for configuration files...

set "json_count=0"
for %%f in (*.json) do (
    set /a "json_count+=1"
    echo Found: %%f
)

echo.
echo Found %json_count% JSON configuration files.
echo.

if %json_count% GTR 0 (
    echo Would you like to convert all JSON files to UTF-8 encoding?
    choice /C YN /M "Convert all JSON files to UTF-8"
    
    if !errorlevel! equ 1 (
        echo.
        echo Converting JSON files to UTF-8...
        for %%f in (*.json) do (
            echo Processing: %%f
            powershell -Command "$content = Get-Content -Path '%%f' -Raw; $utf8NoBom = New-Object System.Text.UTF8Encoding $false; [System.IO.File]::WriteAllText('%%f', $content, $utf8NoBom)"
            echo Converted: %%f
        )
        echo.
        echo All JSON files have been converted to UTF-8 without BOM.
    ) else (
        echo.
        echo Skipping JSON conversion.
    )
)

echo.
echo Checking for YAML configuration files in bin/main/ directory...
if exist "bin\main\*.yml" (
    echo Found YAML files in bin/main/
    echo Would you like to convert these YAML files to UTF-8 encoding?
    choice /C YN /M "Convert YAML files to UTF-8"
    
    if !errorlevel! equ 1 (
        echo.
        echo Converting YAML files to UTF-8...
        for %%f in (bin\main\*.yml) do (
            echo Processing: %%f
            powershell -Command "$content = Get-Content -Path '%%f' -Raw; $utf8NoBom = New-Object System.Text.UTF8Encoding $false; [System.IO.File]::WriteAllText('%%f', $content, $utf8NoBom)"
            echo Converted: %%f
        )
        echo.
        echo All YAML files have been converted to UTF-8 without BOM.
    ) else (
        echo.
        echo Skipping YAML conversion.
    )
) else (
    echo No YAML files found in bin/main/
)

echo.
echo Checking for source files with potential encoding issues...
if exist "src\main\java\" (
    echo Found Java source directory.
    echo Would you like to scan Java files for encoding issues?
    choice /C YN /M "Scan Java files for encoding issues"
    
    if !errorlevel! equ 1 (
        echo.
        echo This will scan for any files containing Unicode symbols that might cause issues...
        echo Scanning Java files...
        
        powershell -Command "$files = Get-ChildItem -Path 'src\main\java' -Recurse -Filter '*.java'; foreach ($file in $files) { $content = Get-Content -Path $file.FullName -Raw; if ($content -match '[^\x00-\x7F]') { Write-Host ('File with non-ASCII characters: ' + $file.FullName) } }"
        
        echo.
        echo Scan complete. Files with potential encoding issues are listed above (if any).
        echo.
        echo Note: To fix individual Java files, you may need to open them in an editor
        echo and save them with UTF-8 encoding.
    ) else (
        echo.
        echo Skipping Java file scan.
    )
) else (
    echo No Java source directory found.
)

echo.
echo Encoding fixes complete. Press any key to exit...
pause >nul
