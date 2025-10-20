@echo off
chcp 65001 > nul
echo.
echo ===================================================================
echo  UPDATING ENCODING IN ALL SCRIPT FILES
echo ===================================================================
echo.
echo Setting UTF-8 encoding for terminal...
chcp 65001 > nul
echo Terminal is now using UTF-8 encoding (code page 65001)
echo.

echo Updating Windows CMD scripts with UTF-8 encoding...

:: Iterate through all CMD files
for %%F in (*.cmd) do (
    echo Checking: %cd%\%%F

    :: Check if file already has UTF-8 encoding set
    findstr /B /C:"chcp 65001" "%%F" > nul
    if errorlevel 1 (
        :: File doesn't have encoding set, update it
        type "%%F" > "%%F.tmp"
        echo @echo off> "%%F"
        echo chcp 65001 ^> nul>> "%%F"
        type "%%F.tmp" >> "%%F"
        del "%%F.tmp"
        echo Updated: %cd%\%%F
    ) else (
        echo File already has UTF-8 encoding set.
    )
)

echo.
echo Adding shebang and UTF-8 settings to Bash scripts...

:: Iterate through all Shell scripts
for %%F in (*.sh) do (
    echo Checking: %cd%\%%F

    :: Check if file already has UTF-8 configuration
    findstr /C:"export LANG=" "%%F" > nul
    if errorlevel 1 (
        :: Check if it has a shebang line
        findstr /B /C:"#!/" "%%F" > nul
        if errorlevel 1 (
            :: No shebang, add one with UTF-8 config
            type "%%F" > "%%F.tmp"
            echo #!/bin/bash> "%%F"
            echo.>> "%%F"
            echo # Set UTF-8 encoding for terminal output>> "%%F"
            echo export LANG=en_US.UTF-8>> "%%F"
            echo export LC_ALL=en_US.UTF-8>> "%%F"
            echo.>> "%%F"
            type "%%F.tmp" >> "%%F"
            del "%%F.tmp"
            echo Updated: %cd%\%%F
        ) else (
            :: Has shebang, just add UTF-8 config after it
            findstr /B /C:"#!/" "%%F" > "%%F.first"
            findstr /V /B /C:"#!/" "%%F" > "%%F.rest"

            echo #!/bin/bash> "%%F"
            echo.>> "%%F"
            echo # Set UTF-8 encoding for terminal output>> "%%F"
            echo export LANG=en_US.UTF-8>> "%%F"
            echo export LC_ALL=en_US.UTF-8>> "%%F"
            echo.>> "%%F"
            type "%%F.rest" >> "%%F"

            del "%%F.first" "%%F.rest"
            echo Updated: %cd%\%%F
        )
    ) else (
        echo File already appears to have UTF-8 configuration.
    )

    :: Check for shebang separately
    findstr /B /C:"#!/" "%%F" > nul
    if errorlevel 1 (
        echo Warning: %%F does not have a shebang line.
    ) else (
        echo File already appears to have a shebang.
    )
)

echo.
echo ENCODING UPDATE COMPLETE
echo.
echo All script files have been updated to use UTF-8 encoding.
echo ✅ Windows CMD scripts: Added 'chcp 65001 > nul' at the beginning
echo ✅ Bash scripts: Added export LANG=en_US.UTF-8 and LC_ALL=en_US.UTF-8
echo.
