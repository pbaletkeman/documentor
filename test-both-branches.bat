@echo off
REM Comprehensive testing script for both Java 17 and Java 21 branches

echo ========================================
echo Documentor JAR Testing Script
echo ========================================
echo.

REM Test java-17-lts branch (currently checked out)
echo [1/4] Testing java-17-lts branch...
echo.

echo Running checkstyle...
call gradlew.bat clean checkstyleMain checkstyleTest
if %ERRORLEVEL% neq 0 (
    echo ERROR: Checkstyle failed on java-17-lts
    exit /b 1
)
echo ✓ Checkstyle passed

echo.
echo Running tests...
call gradlew.bat test
if %ERRORLEVEL% neq 0 (
    echo ERROR: Tests failed on java-17-lts
    exit /b 1
)
echo ✓ Tests passed

echo.
echo Building project...
call gradlew.bat build
if %ERRORLEVEL% neq 0 (
    echo ERROR: Build failed on java-17-lts
    exit /b 1
)
echo ✓ Build successful

echo.
echo [2/4] Switching to main branch for Java 21 testing...
git checkout main
if %ERRORLEVEL% neq 0 (
    echo ERROR: Failed to switch to main branch
    exit /b 1
)

echo.
echo [3/4] Testing main branch (Java 21)...
echo.

echo Running checkstyle...
call gradlew.bat clean checkstyleMain checkstyleTest
if %ERRORLEVEL% neq 0 (
    echo ERROR: Checkstyle failed on main
    exit /b 1
)
echo ✓ Checkstyle passed

echo.
echo Running tests...
call gradlew.bat test
if %ERRORLEVEL% neq 0 (
    echo ERROR: Tests failed on main
    exit /b 1
)
echo ✓ Tests passed

echo.
echo Building project...
call gradlew.bat build
if %ERRORLEVEL% neq 0 (
    echo ERROR: Build failed on main
    exit /b 1
)
echo ✓ Build successful

echo.
echo [4/4] Creating work directories...
if not exist work-test-17 mkdir work-test-17
if not exist work-test-21 mkdir work-test-21

echo.
echo ========================================
echo All Tests Passed!
echo ========================================
echo.
echo Java 17 (java-17-lts branch): ✓
echo Java 21 (main branch): ✓
echo Work directories created:
echo   - work-test-17
echo   - work-test-21
echo.
echo JAR file location: build\libs\documentor.jar
echo.
