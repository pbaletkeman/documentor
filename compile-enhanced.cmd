@echo off
chcp 65001 > nul
@echo off
echo Compiling enhanced components for Documentor...
call gradlew.bat compileJava
