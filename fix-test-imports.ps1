# Fix all test files that reference moved configuration classes
Write-Host "🔧 Fixing test file imports for moved configuration classes..."

# Files to process
$testFiles = @(
    "src\test\java\com\documentor\config\DocumentorConfigTest.java",
    "src\test\java\com\documentor\service\CodeAnalysisServiceTest.java",
    "src\test\java\com\documentor\service\PythonCodeAnalyzerTest.java",
    "src\test\java\com\documentor\service\LlmServiceIntegrationTest.java",
    "src\test\java\com\documentor\service\llm\LlmApiClientTest.java",
    "src\test\java\com\documentor\service\llm\LlmRequestBuilderTest.java",
    "src\test\java\com\documentor\service\llm\LlmResponseHandlerTest.java"
)

foreach ($file in $testFiles) {
    if (Test-Path $file) {
        Write-Host "Processing: $file"
        $content = Get-Content $file -Raw
        
        # Add imports if not present
        if ($content -notmatch "import com\.documentor\.config\.model\.LlmModelConfig;") {
            $content = $content -replace "package com\.documentor\.[^;]+;", '$&

import com.documentor.config.model.LlmModelConfig;
import com.documentor.config.model.AnalysisSettings;
import com.documentor.config.model.OutputSettings;'
        }
        
        # Replace all DocumentorConfig.* references
        $content = $content -replace "DocumentorConfig\.LlmModelConfig", "LlmModelConfig"
        $content = $content -replace "DocumentorConfig\.AnalysisSettings", "AnalysisSettings"  
        $content = $content -replace "DocumentorConfig\.OutputSettings", "OutputSettings"
        
        Set-Content $file $content -Encoding UTF8
        Write-Host "✅ Fixed: $file"
    } else {
        Write-Host "⚠️ File not found: $file"
    }
}

Write-Host "🎯 All test files processed!"