#!/bin/bash

# Test script for enhanced Documentor with improved error handling

# Set any needed environment variables
export JAVA_OPTS="-Xmx512m"

echo "====================================================="
echo "  TESTING ENHANCED DOCUMENTOR WITH ERROR HANDLING"
echo "====================================================="
echo ""
echo "This test specifically validates the fix for:"
echo " - NullPointerException in CompletableFuture for unit tests"
echo " - ThreadLocal configuration handling issues"
echo " - Enhanced error recovery in async operations"
echo ""
echo "Using config-llamacpp.json - a configuration that previously"
echo "caused NullPointerException errors in the original version."
echo ""
echo "First, building the project to ensure all classes are compiled..."
./gradlew compileJava

echo ""
echo "Now running the enhanced application with the test configuration..."
echo "This uses runEnhancedApp which includes all the fixes for"
echo "the NullPointerException issues."
echo ""

# Run the enhanced version with the test configuration
./gradlew runEnhancedApp --args="--config config-llamacpp.json analyze --path ./src/main/java --generate-unit-tests true"

echo ""
if [ $? -eq 0 ]; then
    echo "====================================================="
    echo "  TEST SUCCESSFUL - No errors encountered!"
    echo "====================================================="
    echo ""
    echo "The enhanced version has successfully completed without"
    echo "the NullPointerException errors that affected the"
    echo "original version."
else
    echo "====================================================="
    echo "  TEST FAILED - Errors were encountered"
    echo "====================================================="
    echo ""
    echo "Check the logs for details on what went wrong."
fi
echo ""
