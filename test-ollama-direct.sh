#!/bin/bash
# Direct Ollama API test - bypass ThreadLocal issues

echo "Testing Ollama API directly..."
curl -X POST http://localhost:11434/api/generate \
  -H "Content-Type: application/json" \
  -d '{
    "model": "llama3.2",
    "prompt": "Write a brief one-sentence description of what a Java enum is.",
    "stream": false
  }' | head -20

echo ""
echo "If you see a response above, Ollama itself is working fine."
echo "The issue is purely ThreadLocal propagation in the Java application."
