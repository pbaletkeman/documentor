#!/bin/bash

# Script to fix wildcard imports - Part 1: Simple assertion-only files
# These files only use basic assertions from JUnit

echo "Fixing simple assertion-only test files..."

# Fix files that only use basic assertions (most common pattern)
files=(
    "src/test/java/com/documentor/model/CodeElementTypeTest.java"
    "src/test/java/com/documentor/model/CodeElementTest.java" 
    "src/test/java/com/documentor/util/ApplicationConstantsTest.java"
    "src/test/java/com/documentor/service/documentation/DocumentationFormatterTest.java"
    "src/test/java/com/documentor/service/python/PythonPatternMatcherTest.java"
    "src/test/java/com/documentor/service/python/PythonElementExtractorTest.java"
    "src/test/java/com/documentor/service/python/PythonElementExtractorExtendedTest.java"
    "src/test/java/com/documentor/service/llm/LlmResponseParserTest.java"
    "src/test/java/com/documentor/service/llm/LlmRequestFormatterTest.java"
    "src/test/java/com/documentor/service/llm/LlmPromptTemplatesTest.java"
    "src/test/java/com/documentor/service/llm/LlmModelTypeDetectorTest.java"
    "src/test/java/com/documentor/service/python/PythonASTCommandBuilderTest.java"
    "src/test/java/com/documentor/service/diagram/DiagramPathManagerTest.java"
    "src/test/java/com/documentor/service/diagram/DiagramPathManagerEnhancedTest.java"
    "src/test/java/com/documentor/service/diagram/DiagramElementFilterTest.java"
    "src/test/java/com/documentor/model/CodeVisibilityTest.java"
    "src/test/java/com/documentor/model/ProjectAnalysisTest.java"
    "src/test/java/com/documentor/service/analysis/JavaElementVisitorEnhancedTest.java"
    "src/test/java/com/documentor/DocumentorApplicationIntegrationTest.java"
    "src/test/java/com/documentor/DocumentorApplicationEnhancedTest.java"
    "src/test/java/com/documentor/config/model/OutputSettingsTest.java"
    "src/test/java/com/documentor/cli/handlers/CommonCommandHandlerTest.java"
    "src/test/java/com/documentor/config/model/LlmModelConfigTest.java"
    "src/test/java/com/documentor/config/model/AnalysisSettingsTest.java"
)

for file in "${files[@]}"; do
    if [ -f "$file" ]; then
        echo "Processing $file..."
        # Replace wildcard import with common assertions
        sed -i 's/import static org.junit.jupiter.api.Assertions\.\*;/import static org.junit.jupiter.api.Assertions.assertEquals;\nimport static org.junit.jupiter.api.Assertions.assertNotNull;\nimport static org.junit.jupiter.api.Assertions.assertTrue;\nimport static org.junit.jupiter.api.Assertions.assertFalse;/' "$file"
    fi
done

echo "Simple fixes completed. Check for compilation errors and manually adjust as needed."