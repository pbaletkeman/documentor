#!/usr/bin/env bash
# Comprehensive CLI Options Verification Script
# Tests all available switches/options with proper project paths
# Outputs placed in test-verification-temp directory

set -e  # Exit on error

echo "========================================"
echo "Documentor CLI Options Verification"
echo "========================================"
echo ""

# Clean and create temp directory
echo "Setting up test environment..."
rm -rf test-verification-temp
mkdir -p test-verification-temp
echo "✓ Test directory created"

echo ""
echo "Step 1: Running checkstyle verification..."
./gradlew clean checkstyleMain checkstyleTest
if [ $? -eq 0 ]; then
    echo "✓ Checkstyle verification passed"
else
    echo "✗ Checkstyle verification failed"
    exit 1
fi

echo ""
echo "Step 2: Running all tests..."
./gradlew test
if [ $? -eq 0 ]; then
    echo "✓ All tests passed"
else
    echo "✗ Tests failed"
    exit 1
fi

echo ""
echo "Step 3: Building project..."
./gradlew build
if [ $? -eq 0 ]; then
    echo "✓ Build successful"
else
    echo "✗ Build failed"
    exit 1
fi

echo ""
echo "========================================"
echo "Testing CLI Commands"
echo "========================================"

# Test 1: Status command
echo ""
echo "Test 1: Status command..."
echo "status" | ./gradlew bootRun --args="" > test-verification-temp/status-output.log 2>&1
echo "✓ Status command output saved to test-verification-temp/status-output.log"

# Test 2: Info command
echo ""
echo "Test 2: Info command..."
echo "info" | ./gradlew bootRun --args="" > test-verification-temp/info-output.log 2>&1
echo "✓ Info command output saved to test-verification-temp/info-output.log"

# Test 3: Quick-start command
echo ""
echo "Test 3: Quick-start command..."
echo "quick-start" | ./gradlew bootRun --args="" > test-verification-temp/quickstart-output.log 2>&1
echo "✓ Quick-start command output saved to test-verification-temp/quickstart-output.log"

# Test 4: Validate config
echo ""
echo "Test 4: Validate config command..."
echo "validate-config --config=config.json" | ./gradlew bootRun --args="" > test-verification-temp/validate-output.log 2>&1
echo "✓ Validate config output saved to test-verification-temp/validate-output.log"

# Test 5: Scan project (analyze-only)
echo ""
echo "Test 5: Scan project command..."
echo "scan --project-path=src/main/java/com/documentor/model" | ./gradlew bootRun --args="" > test-verification-temp/scan-output.log 2>&1
echo "✓ Scan output saved to test-verification-temp/scan-output.log"

# Test 6: Dry-run mode
echo ""
echo "Test 6: Dry-run mode..."
echo "analyze --project-path=src/main/java/com/documentor/model --config=config.json --dry-run=true" | ./gradlew bootRun --args="" > test-verification-temp/dryrun-output.log 2>&1
echo "✓ Dry-run output saved to test-verification-temp/dryrun-output.log"

# Test 7: PlantUML generation
echo ""
echo "Test 7: PlantUML generation..."
echo "plantuml --project-path=src/main/java/com/documentor/model --plantuml-output=test-verification-temp/plantuml-diagrams" | ./gradlew bootRun --args="" > test-verification-temp/plantuml-output.log 2>&1
echo "✓ PlantUML output saved to test-verification-temp/plantuml-output.log"

echo ""
echo "========================================"
echo "Verification Summary"
echo "========================================"
echo ""
echo "All CLI options have been tested!"
echo "Output files are in test-verification-temp/ directory:"
echo "  - status-output.log"
echo "  - info-output.log"
echo "  - quickstart-output.log"
echo "  - validate-output.log"
echo "  - scan-output.log"
echo "  - dryrun-output.log"
echo "  - plantuml-output.log"
echo ""
echo "✓ All tests completed successfully!"
