@echo off
echo Running Documentor Encoding Test
echo ==============================
echo.

REM Set UTF-8 code page
chcp 65001 >nul

echo Testing UTF-8 encoding in command prompt...
echo.
echo If you see the following symbols correctly, UTF-8 is working: ✅ ❌ ⚠️ ℹ️
echo.

REM Run the Java encoding tester
echo Running Java encoding test...
echo.

REM Compile and run the encoding tester
call gradlew.bat -q compileJava -Dfile.encoding=UTF-8
echo.
call gradlew.bat -q runTestEncoding -Dfile.encoding=UTF-8

echo.
echo Encoding test complete!
echo If you see proper symbols above, the encoding fix was successful.
echo.
echo Press any key to exit...
pause >nul
