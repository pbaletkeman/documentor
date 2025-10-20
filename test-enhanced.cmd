@echo off
REM Test script for enhanced Documentor with improved error handling

REM Set any needed environment variables
set JAVA_OPTS=-Xmx512m

echo =====================================================
echo   TESTING ENHANCED DOCUMENTOR WITH ERROR HANDLING
echo =====================================================
echo.
echo This test will use config-llamacpp.json - a configuration that
echo previously caused NullPointerException errors in CompletableFuture.
echo.
echo First, building the project to ensure all classes are compiled...
call gradlew.bat compileJava

echo.
echo Now running the test application with the special configuration...
echo.

REM Run the special test version with specific test configuration
call gradlew.bat runTestApp -Pargs="--config,config-llamacpp.json,analyze,--path,./src/main/java"

echo.
if %ERRORLEVEL% EQU 0 (
    echo =====================================================
    echo   TEST SUCCESSFUL - No errors encountered!
    echo =====================================================
    echo.
    echo The enhanced version has successfully completed without
    echo the NullPointerException errors that affected the
    echo original version.
) else (
    echo =====================================================
    echo   TEST FAILED - Errors were encountered
    echo =====================================================
    echo.
    echo Check the logs for details on what went wrong.
)
echo.
