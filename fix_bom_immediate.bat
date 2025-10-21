@echo off
echo Fixing BOM characters in Java files...

REM Fix DirectCommandProcessor.java
powershell -Command "(Get-Content 'src\main\java\com\documentor\cli\DirectCommandProcessor.java' -Raw) -replace '^.ackage', 'package' | Set-Content 'src\main\java\com\documentor\cli\DirectCommandProcessor.java' -Encoding UTF8"

REM Fix DocumentorCommands.java
powershell -Command "(Get-Content 'src\main\java\com\documentor\cli\DocumentorCommands.java' -Raw) -replace '^.ackage', 'package' | Set-Content 'src\main\java\com\documentor\cli\DocumentorCommands.java' -Encoding UTF8"

REM Fix handler files
powershell -Command "(Get-Content 'src\main\java\com\documentor\cli\handlers\CommonCommandHandler.java' -Raw) -replace '^.ackage', 'package' | Set-Content 'src\main\java\com\documentor\cli\handlers\CommonCommandHandler.java' -Encoding UTF8"

powershell -Command "(Get-Content 'src\main\java\com\documentor\cli\handlers\ProjectAnalysisCommandHandler.java' -Raw) -replace '^.ackage', 'package' | Set-Content 'src\main\java\com\documentor\cli\handlers\ProjectAnalysisCommandHandler.java' -Encoding UTF8"

powershell -Command "(Get-Content 'src\main\java\com\documentor\cli\handlers\ProjectAnalysisRequest.java' -Raw) -replace '^.ackage', 'package' | Set-Content 'src\main\java\com\documentor\cli\handlers\ProjectAnalysisRequest.java' -Encoding UTF8"

powershell -Command "(Get-Content 'src\main\java\com\documentor\cli\handlers\StatusCommandHandler.java' -Raw) -replace '^.ackage', 'package' | Set-Content 'src\main\java\com\documentor\cli\handlers\StatusCommandHandler.java' -Encoding UTF8"

REM Fix config files
powershell -Command "(Get-Content 'src\main\java\com\documentor\config\AppConfig.java' -Raw) -replace '^.ackage', 'package' | Set-Content 'src\main\java\com\documentor\config\AppConfig.java' -Encoding UTF8"

powershell -Command "(Get-Content 'src\main\java\com\documentor\config\AppConfigEnhanced.java' -Raw) -replace '^.ackage', 'package' | Set-Content 'src\main\java\com\documentor\config\AppConfigEnhanced.java' -Encoding UTF8"

powershell -Command "(Get-Content 'src\main\java\com\documentor\config\BeanUtils.java' -Raw) -replace '^.ackage', 'package' | Set-Content 'src\main\java\com\documentor\config\BeanUtils.java' -Encoding UTF8"

powershell -Command "(Get-Content 'src\main\java\com\documentor\config\DiagramServiceConfiguration.java' -Raw) -replace '^.ackage', 'package' | Set-Content 'src\main\java\com\documentor\config\DiagramServiceConfiguration.java' -Encoding UTF8"

echo Fixed BOM characters in Java files.
echo Now trying to compile...
gradlew.bat compileJava
