@echo off
chcp 65001 > nul
@echo off
echo Running llamacpp test with LlmServiceFix...

rem Create test directory for output
if not exist ".\test-docs" mkdir ".\test-docs"

rem Run with our config-test.json and direct fix
call gradlew.bat runApp --args="analyze --project-path ./src/main/java/com/documentor/service/LlmServiceFix.java --config ./config-test.json --output-dir ./test-docs --fix"

echo.
echo Test completed. Check logs for results.
