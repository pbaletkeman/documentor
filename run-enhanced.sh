#!/bin/bash

# Run script for enhanced Documentor with improved error handling

echo ""
echo "==================================================================="
echo "  RUNNING ENHANCED VERSION OF DOCUMENTOR"
echo "==================================================================="
echo ""
echo "This version includes:"
echo " - Fixed NullPointerException issues in unit test generation"
echo " - Improved thread safety with ThreadLocal handling"
echo " - Enhanced error handling and logging"
echo " - Memory leak prevention with proper ThreadLocal cleanup"
echo ""
echo "Starting enhanced application..."
echo ""

# Run the enhanced version
./gradlew runEnhancedApp "$@"
