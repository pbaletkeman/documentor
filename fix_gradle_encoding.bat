@echo off
setlocal

echo Fixing Gradle build file for proper encoding support
echo.

REM Backup the original build.gradle file
if exist "build.gradle" (
    echo Creating backup of build.gradle...
    copy /Y "build.gradle" "build.gradle.bak"
    echo Backup created as build.gradle.bak
)

echo Adding UTF-8 encoding settings to build.gradle...

REM Use PowerShell to find the correct location and insert the encoding configuration
powershell -Command "$content = Get-Content -Path 'build.gradle' -Raw; if ($content -match 'java \{') { $content = $content -replace 'java \{', 'java {\r\n    sourceCompatibility = JavaVersion.VERSION_21\r\n    targetCompatibility = JavaVersion.VERSION_21\r\n    compileJava.options.encoding = ''UTF-8''\r\n    compileTestJava.options.encoding = ''UTF-8'''; [System.IO.File]::WriteAllText('build.gradle', $content) } else { Write-Host 'Could not find java block in build.gradle' }"

echo.
echo Creating gradle.properties file with UTF-8 encoding settings...

REM Create a gradle.properties file with UTF-8 settings
(
    echo # Ensure Gradle uses UTF-8 encoding for Java compilation
    echo org.gradle.jvmargs=-Dfile.encoding=UTF-8
    echo systemProp.file.encoding=UTF-8
) > "gradle.properties"

echo.
echo Creating UTF-8 batch wrapper for running the application...

REM Create a UTF-8 aware runner script
(
    echo @echo off
    echo chcp 65001 ^>nul
    echo REM Run Documentor with UTF-8 support
    echo.
    echo gradlew.bat runEnhancedApp -Dfile.encoding=UTF-8 %%*
) > "run-utf8.cmd"

echo Created run-utf8.cmd for running the application with UTF-8 encoding

echo.
echo Updating JVM settings for better encoding support...

REM Create .vscode folder if it doesn't exist
if not exist ".vscode" mkdir ".vscode"

REM Create settings.json with encoding settings
(
    echo {
    echo     "java.jdt.ls.java.home": "",
    echo     "java.configuration.runtimes": [
    echo         {
    echo             "name": "JavaSE-21",
    echo             "path": "",
    echo             "default": true
    echo         }
    echo     ],
    echo     "java.compile.nullAnalysis.mode": "automatic",
    echo     "java.configuration.updateBuildConfiguration": "automatic",
    echo     "java.project.encoding": "UTF-8",
    echo     "files.encoding": "utf8",
    echo     "terminal.integrated.defaultProfile.windows": "Command Prompt",
    echo     "terminal.integrated.profiles.windows": {
    echo         "Command Prompt": {
    echo             "path": "cmd.exe",
    echo             "args": ["/k", "chcp 65001 ^>nul"]
    echo         }
    echo     }
    echo }
) > ".vscode\settings.json"

echo Created .vscode/settings.json with UTF-8 encoding settings

echo.
echo All encoding fixes for Gradle have been applied.
echo.
echo Press any key to exit...
pause >nul
