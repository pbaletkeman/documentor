@echo off
REM Direct Ollama API test - bypass ThreadLocal issues

echo Testing Ollama API directly...
curl -X POST http://localhost:11434/api/generate -H "Content-Type: application/json" -d "{\"model\":\"llama3.2\",\"prompt\":\"Write a brief one-sentence description of what a Java enum is.\",\"stream\":false}"

echo.
echo.
echo If you see a response above with generated text, Ollama itself is working fine.
echo The issue is purely ThreadLocal propagation in the Java application.
echo.
echo To fix: Ensure config is set in ThreadLocalContextHolder before async tasks execute.
