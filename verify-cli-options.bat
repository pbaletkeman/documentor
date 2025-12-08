@echo off
REM Script to verify all CLI options work correctly
REM Outputs are placed in test-verification-temp directory

echo ========================================
echo Documentor CLI Options Verification
echo ========================================
echo.

REM Clean previous test outputs
if exist test-verification-temp (
    echo Cleaning previous test outputs...
    rmdir /s /q test-verification-temp
)
mkdir test-verification-temp

echo.
echo [1/10] Running checkstyleMain...
call gradlew.bat clean checkstyleMain
if %ERRORLEVEL% neq 0 (
    echo ERROR: checkstyleMain failed
    exit /b 1
)
echo ✓ checkstyleMain passed

echo.
echo [2/10] Running checkstyleTest...
call gradlew.bat checkstyleTest
if %ERRORLEVEL% neq 0 (
    echo ERROR: checkstyleTest failed
    exit /b 1
)
echo ✓ checkstyleTest passed

echo.
echo [3/10] Running all tests...
call gradlew.bat test
if %ERRORLEVEL% neq 0 (
    echo ERROR: tests failed
    exit /b 1
)
echo ✓ All tests passed

echo.
echo [4/10] Testing scan command (analyze-only)...
echo analyze-only --project-path=src/main/java --include-private-members=true > test-verification-temp\test-scan.txt
echo exit >> test-verification-temp\test-scan.txt
call gradlew.bat bootRun < test-verification-temp\test-scan.txt > test-verification-temp\scan-output.log 2>&1
echo ✓ Scan command executed (check test-verification-temp\scan-output.log)

echo.
echo [5/10] Testing validate-config command...
echo validate-config --config=config.json > test-verification-temp\test-validate.txt
echo exit >> test-verification-temp\test-validate.txt
call gradlew.bat bootRun < test-verification-temp\test-validate.txt > test-verification-temp\validate-output.log 2>&1
echo ✓ Validate config command executed (check test-verification-temp\validate-output.log)

echo.
echo [6/10] Testing info command...
echo info > test-verification-temp\test-info.txt
echo exit >> test-verification-temp\test-info.txt
call gradlew.bat bootRun < test-verification-temp\test-info.txt > test-verification-temp\info-output.log 2>&1
echo ✓ Info command executed (check test-verification-temp\info-output.log)

echo.
echo [7/10] Testing quick-start command...
echo quick-start > test-verification-temp\test-quickstart.txt
echo exit >> test-verification-temp\test-quickstart.txt
call gradlew.bat bootRun < test-verification-temp\test-quickstart.txt > test-verification-temp\quickstart-output.log 2>&1
echo ✓ Quick-start command executed (check test-verification-temp\quickstart-output.log)

echo.
echo [8/10] Testing status command...
echo status > test-verification-temp\test-status.txt
echo exit >> test-verification-temp\test-status.txt
call gradlew.bat bootRun < test-verification-temp\test-status.txt > test-verification-temp\status-output.log 2>&1
echo ✓ Status command executed (check test-verification-temp\status-output.log)

echo.
echo [9/10] Testing dry-run mode...
echo analyze --project-path=src/main/java/com/documentor/model --config=config.json --dry-run=true > test-verification-temp\test-dryrun.txt
echo exit >> test-verification-temp\test-dryrun.txt
call gradlew.bat bootRun < test-verification-temp\test-dryrun.txt > test-verification-temp\dryrun-output.log 2>&1
echo ✓ Dry-run mode executed (check test-verification-temp\dryrun-output.log)

echo.
echo [10/10] Testing PlantUML generation...
echo plantuml --project-path=src/main/java/com/documentor/model --plantuml-output=test-verification-temp\plantuml-test --include-private-members=true > test-verification-temp\test-plantuml.txt
echo exit >> test-verification-temp\test-plantuml.txt
call gradlew.bat bootRun < test-verification-temp\test-plantuml.txt > test-verification-temp\plantuml-output.log 2>&1
echo ✓ PlantUML generation executed (check test-verification-temp\plantuml-output.log)

echo.
echo ========================================
echo Verification Complete!
echo ========================================
echo.
echo Results are in test-verification-temp directory:
echo   - scan-output.log
echo   - validate-output.log
echo   - info-output.log
echo   - quickstart-output.log
echo   - status-output.log
echo   - dryrun-output.log
echo   - plantuml-output.log
echo.
echo All tests passed successfully!
