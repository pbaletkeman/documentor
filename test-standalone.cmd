@echo off
chcp 65001 > nul
@echo off
REM Dedicated test script for running just the enhanced version without bean conflicts

echo =====================================================
echo   RUNNING STANDALONE TEST APPLICATION
echo =====================================================
echo.
echo This script runs the standalone test application with ONLY
echo the enhanced components to avoid any bean conflicts.
echo.

REM First compile all Java files
echo Building all Java files...
call gradlew.bat compileJava

echo.
echo Starting test application with LlamaCPP config...

REM Run the dedicated test application
call gradlew.bat runTestApp -Pargs="analyze,--project-path,./src/main/java,--config,config-llamacpp.json"

echo.
if %ERRORLEVEL% EQU 0 (
    echo =====================================================
    echo   TEST SUCCESSFUL - No errors encountered!
    echo =====================================================
    echo.
    echo The test application successfully ran without errors.
    echo.
    echo Check the logs for any "NullPointerException in CompletableFuture" messages.
    echo If none are found, the enhanced error handling is working correctly.
) else (
    echo =====================================================
    echo   TEST FAILED - Error code: %ERRORLEVEL%
    echo =====================================================
    echo.
    echo The test application encountered errors.
    echo See the logs above for details.
)
