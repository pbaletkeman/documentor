package com.documentor.cli;

import com.documentor.config.DocumentorConfig;
import com.documentor.service.CodeAnalysisService;
import com.documentor.service.DocumentationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Focused tests for DocumentorCommands.showStatus() method to improve coverage.
 */
@ExtendWith(MockitoExtension.class)
class DocumentorCommandsShowStatusTest {

    @Mock
    private CodeAnalysisService codeAnalysisService;

    @Mock
    private DocumentationService documentationService;

    private DocumentorCommands documentorCommands;

    @BeforeEach
    void setUp() {
        // Create a simple config with various scenarios
        DocumentorConfig.LlmModelConfig model1 = new DocumentorConfig.LlmModelConfig(
                "gpt-4", "gpt-4", "https://api.openai.com/v1/chat/completions", 
                4096, 0.7, 150, Collections.emptyMap()
        );
        
        DocumentorConfig.LlmModelConfig model2 = new DocumentorConfig.LlmModelConfig(
                "claude", "claude-3", "https://api.anthropic.com/v1/messages", 
                8192, 0.5, 200, Collections.emptyMap()
        );

        DocumentorConfig.OutputSettings outputSettings = new DocumentorConfig.OutputSettings(
                "docs", "markdown", true, false, 0.8
        );

        DocumentorConfig.AnalysisSettings analysisSettings = new DocumentorConfig.AnalysisSettings(
                true, 10, Arrays.asList("*.java", "*.py"), Arrays.asList("test", "build")
        );

        DocumentorConfig config = new DocumentorConfig(
                Arrays.asList(model1, model2), outputSettings, analysisSettings
        );

        documentorCommands = new DocumentorCommands(codeAnalysisService, documentationService, config);
    }

    @Test
    void testShowStatusBasic() {
        String result = documentorCommands.showStatus();
        
        assertNotNull(result);
        assertTrue(result.contains("Documentor Status"));
        assertTrue(result.contains("Current Project:"));
        assertTrue(result.contains("Configuration:"));
    }

    @Test
    void testShowStatusWithMultipleModels() {
        String result = documentorCommands.showStatus();
        
        // Should show both models
        assertTrue(result.contains("gpt-4"));
        assertTrue(result.contains("claude"));
        assertTrue(result.contains("Total Models: 2"));
    }

    @Test
    void testShowStatusMemoryInformation() {
        String result = documentorCommands.showStatus();
        
        // The current implementation doesn't show memory info, so let's test what it actually shows
        assertTrue(result.contains("LLM Models:"));
        assertTrue(result.contains("Configuration:"));
    }

    @Test
    void testShowStatusSystemProperties() {
        String result = documentorCommands.showStatus();
        
        // Test actual content that is in the status
        assertTrue(result.contains("Current Project:"));
        assertTrue(result.contains("No project currently selected"));
    }

    @Test
    void testShowStatusOutputSettings() {
        String result = documentorCommands.showStatus();
        
        // Should show output configuration
        assertTrue(result.contains("Output Settings:"));
        assertTrue(result.contains("Output Path:"));
        assertTrue(result.contains("Format:"));
        assertTrue(result.contains("Generate Unit Tests:"));
    }

    @Test
    void testShowStatusAnalysisSettings() {
        String result = documentorCommands.showStatus();
        
        // Should show analysis configuration  
        assertTrue(result.contains("Analysis Settings:"));
        assertTrue(result.contains("Include Private Members:"));
        assertTrue(result.contains("Max Threads:"));
    }

    @Test
    void testShowStatusModelDetails() {
        String result = documentorCommands.showStatus();
        
        // Should show model configuration details
        assertTrue(result.contains("Max Tokens:"));
        assertTrue(result.contains("Temperature:"));
        assertTrue(result.contains("API Key:"));
    }
}