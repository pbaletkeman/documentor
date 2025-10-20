@echo off
chcp 65001 >nul
echo Updating build.gradle to add encoding test task
echo.

powershell -Command "$content = Get-Content -Path 'build.gradle' -Raw; if ($content -notmatch 'task runTestEncoding') { $content = $content + \"\r\n\r\n// Encoding test task\r\ntask runTestEncoding(type: JavaExec) {\r\n    group = 'application'\r\n    description = 'Run the encoding test to verify UTF-8 support'\r\n    classpath = sourceSets.main.runtimeClasspath\r\n    mainClass = 'com.documentor.util.EncodingTester'\r\n    systemProperty 'file.encoding', 'UTF-8'\r\n}\"; [System.IO.File]::WriteAllText('build.gradle', $content) }"

echo Build file updated with encoding test task
echo.
echo Press any key to exit...
pause >nul
