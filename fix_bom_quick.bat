@echo off
echo Fixing BOM issues in Java files...

REM Fix specific files with BOM issues
powershell -NoProfile -ExecutionPolicy Bypass -Command ^
"$files = @('src\main\java\com\documentor\config\DiagramServiceConfiguration.java', 'src\main\java\com\documentor\config\DocumentationServiceConfiguration.java', 'src\main\java\com\documentor\config\EarlyConfigurationLoader.java', 'src\main\java\com\documentor\config\LlmServiceConfiguration.java', 'src\main\java\com\documentor\config\LlmServiceConfigurationEnhanced.java', 'src\main\java\com\documentor\config\ThreadLocalContextHolder.java', 'src\main\java\com\documentor\config\ThreadLocalPropagatingExecutor.java', 'src\main\java\com\documentor\config\ThreadLocalTaskDecorator.java'); foreach ($file in $files) { if (Test-Path $file) { $content = Get-Content $file -Raw -Encoding UTF8; if ($content -match '^.ackage') { $content = $content -replace '^.ackage', 'package'; Set-Content -Path $file -Value $content -Encoding UTF8 -NoNewline; Write-Host \"Fixed: $file\" } } }"

echo BOM fix complete!
