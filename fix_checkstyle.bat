@echo off
setlocal enabledelayedexpansion

echo Automated Checkstyle Line Length Violation Fixer
echo ================================================
echo.

if "%1"=="" (
    echo Usage: fix_checkstyle.bat ^<source_directory^>
    echo Example: fix_checkstyle.bat src\main\java
    exit /b 1
)

set "SOURCE_DIR=%1"
if not exist "%SOURCE_DIR%" (
    echo Error: Directory '%SOURCE_DIR%' does not exist
    exit /b 1
)

echo Processing directory: %SOURCE_DIR%
echo Maximum line length: 80 characters
echo.

set "FILES_PROCESSED=0"
set "VIOLATIONS_FIXED=0"

for /r "%SOURCE_DIR%" %%f in (*.java) do (
    call :process_file "%%f"
)

echo.
echo Processing complete!
echo Files processed: %FILES_PROCESSED%
echo Violations fixed: %VIOLATIONS_FIXED%
echo.
echo Note: Run 'gradlew checkstyleMain' to verify the fixes
pause
exit /b 0

:process_file
set "FILE=%~1"
set "TEMP_FILE=%FILE%.tmp"
set "FILE_VIOLATIONS=0"
set "LINE_NUM=0"

echo Processing: %FILE%

(
    for /f "usebackq delims=" %%a in ("%FILE%") do (
        set /a LINE_NUM+=1
        set "LINE=%%a"

        REM Calculate line length (approximate)
        set "CHAR_COUNT=0"
        set "TEST_LINE=!LINE!"
        :count_loop
        if not "!TEST_LINE!"=="" (
            set /a CHAR_COUNT+=1
            set "TEST_LINE=!TEST_LINE:~1!"
            goto count_loop
        )

        REM If line is longer than 80 characters, try to fix it
        if !CHAR_COUNT! gtr 80 (
            call :fix_line "!LINE!"
            if not "!FIXED_LINE!"=="!LINE!" (
                set /a FILE_VIOLATIONS+=1
                echo(!FIXED_LINE!
            ) else (
                echo(!LINE!
            )
        ) else (
            echo(!LINE!
        )
    )
) > "%TEMP_FILE%"

if %FILE_VIOLATIONS% gtr 0 (
    move "%TEMP_FILE%" "%FILE%" >nul
    echo   Fixed %FILE_VIOLATIONS% violations
    set /a FILES_PROCESSED+=1
    set /a VIOLATIONS_FIXED+=FILE_VIOLATIONS
) else (
    del "%TEMP_FILE%" >nul
)

exit /b 0

:fix_line
set "INPUT_LINE=%~1"
set "FIXED_LINE=%INPUT_LINE%"

REM Fix logger declarations
echo "!INPUT_LINE!" | findstr /C:"LoggerFactory.getLogger" >nul
if !errorlevel! equ 0 (
    echo "!INPUT_LINE!" | findstr /r "private.*static.*final.*Logger.*=" >nul
    if !errorlevel! equ 0 (
        REM Extract logger name and class
        for /f "tokens=5,9 delims= " %%i in ("!INPUT_LINE!") do (
            set "LOGGER_NAME=%%i"
            set "CLASS_NAME=%%j"
        )
        if defined LOGGER_NAME if defined CLASS_NAME (
            REM Get indentation
            set "INDENT="
            for /f "tokens=1 delims=ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz" %%i in ("!INPUT_LINE!") do set "INDENT=%%i"

            set "FIXED_LINE=!INDENT!private static final Logger !LOGGER_NAME! ="
            echo(!FIXED_LINE!
            set "FIXED_LINE=!INDENT!        LoggerFactory.getLogger(!CLASS_NAME!);"
            exit /b 0
        )
    )
)

REM Fix string concatenations with +
echo "!INPUT_LINE!" | findstr /C:" + " >nul
if !errorlevel! equ 0 (
    echo "!INPUT_LINE!" | findstr /C:"""" >nul
    if !errorlevel! equ 0 (
        REM Simple string concatenation fix - break at first +
        for /f "tokens=1* delims=+" %%i in ("!INPUT_LINE!") do (
            set "FIRST_PART=%%i"
            set "REST_PART=%%j"
        )
        if defined FIRST_PART if defined REST_PART (
            REM Get indentation
            set "INDENT="
            for /f "tokens=1 delims=ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz" %%i in ("!INPUT_LINE!") do set "INDENT=%%i"

            set "FIXED_LINE=!FIRST_PART!"
            echo(!FIXED_LINE!
            set "FIXED_LINE=!INDENT!        +!REST_PART!"
            exit /b 0
        )
    )
)

REM Fix method parameters - simple case
echo "!INPUT_LINE!" | findstr /C:", " >nul
if !errorlevel! equ 0 (
    echo "!INPUT_LINE!" | findstr /C:"(" >nul
    if !errorlevel! equ 0 (
        echo "!INPUT_LINE!" | findstr /C:"@ShellOption" >nul
        if !errorlevel! equ 0 (
            REM Break @ShellOption parameters
            for /f "tokens=1* delims=," %%i in ("!INPUT_LINE!") do (
                set "FIRST_PARAM=%%i"
                set "REST_PARAMS=%%j"
            )
            if defined FIRST_PARAM if defined REST_PARAMS (
                REM Get indentation
                set "INDENT="
                for /f "tokens=1 delims=ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz" %%i in ("!INPUT_LINE!") do set "INDENT=%%i"

                set "FIXED_LINE=!FIRST_PARAM!,"
                echo(!FIXED_LINE!
                set "FIXED_LINE=!INDENT!        !REST_PARAMS!"
                exit /b 0
            )
        )
    )
)

exit /b 0
