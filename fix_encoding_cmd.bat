@echo off
chcp 65001 >nul
echo Setting up proper Unicode character encoding for Windows Command Prompt
echo.

powershell -ExecutionPolicy Bypass -File "%~dp0fix_encoding_cmd.ps1"
