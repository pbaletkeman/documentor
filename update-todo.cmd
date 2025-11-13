@echo off
echo Parsing checkstyle results...
findstr /C:"LineLength" "build\reports\checkstyle\test.xml" > temp-linelength.txt
echo # Line Length Issues in Test Files > todo-long.md

rem Count violations per file
for /f "tokens=2 delims=\" %%a in ('findstr /C:"filename=" temp-linelength.txt ^| findstr /C:"Test.java"') do (
    set file=%%a
    call :count_violations "%%a"
)

del temp-linelength.txt
echo Updated todo-long.md

goto :eof

:count_violations
set "filename=%~1"
set count=0
for /f %%i in ('findstr /C:"%filename%" temp-linelength.txt ^| findstr /C:"LineLength" ^| find /c /v ""') do set count=%%i
if %count% gtr 0 (
    echo %filename% - %count% violations >> todo-long.md
)
goto :eof
