@echo off
REM 🔧 Pre-commit hook for Documentor project (Windows batch version)
REM Runs unit tests and linting before allowing commits

echo 🚀 Running pre-commit checks...

REM Check if gradlew.bat exists
if not exist "gradlew.bat" (
    echo ❌ gradlew.bat not found. Make sure you're in the project root.
    exit /b 1
)

REM Run Checkstyle linting
echo 🔍 Running Checkstyle...
call gradlew.bat checkstyleMain checkstyleTest
if %ERRORLEVEL% neq 0 (
    echo ❌ Checkstyle failed. Please fix the issues and try again.
    exit /b 1
)

REM Run unit tests
echo 🧪 Running unit tests...
call gradlew.bat test
if %ERRORLEVEL% neq 0 (
    echo ❌ Unit tests failed. Please fix the issues and try again.
    exit /b 1
)

REM Check test coverage
echo 📊 Checking test coverage...
call gradlew.bat jacocoTestCoverageVerification
if %ERRORLEVEL% neq 0 (
    echo ⚠️ Test coverage below threshold. Consider adding more tests.
    echo 📈 View coverage report: build\reports\jacoco\test\html\index.html
    REM Don't fail the commit for coverage, just warn
)

echo ✅ All pre-commit checks passed!
exit /b 0