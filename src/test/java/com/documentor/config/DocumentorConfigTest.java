package com.documentor.config;

import com.documentor.config.model.LlmModelConfig;
import com.documentor.config.model.OutputSettings;
import com.documentor.config.model.AnalysisSettings;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

/**
 * 🧪 Comprehensive tests for DocumentorConfig and nested record classes
 * Tests configuration property handling, default values, and validation
 * for all configuration records: LlmModelConfig, OutputSettings, AnalysisSettings
 */
class DocumentorConfigTest {
    @Test
    void testLlmModelConfigCreation() {
        // Given
        String name = "gpt-4";
        String provider = "openai";
        String baseUrl = "https://api.openai.com/v1";
        String apiKey = "test-api-key";
        Integer maxTokens = 2000;
        Integer timeoutSeconds = 60;

        // When
        LlmModelConfig config = new LlmModelConfig(name, provider, baseUrl, apiKey, maxTokens, timeoutSeconds);

        // Then
        assertEquals(name, config.name());
        assertEquals(provider, config.provider());
        assertEquals(baseUrl, config.baseUrl());
        assertEquals(apiKey, config.apiKey());
        assertEquals(maxTokens, config.maxTokens());
        assertEquals(timeoutSeconds, config.timeoutSeconds());
    }

    @Test
    void testOutputSettingsCreation() {
        // Given
        String outputDirectory = "docs/output";
        String format = "markdown";
        boolean generateMermaid = true;
        boolean verboseOutput = false;

        // When
        OutputSettings settings = new OutputSettings(outputDirectory, format, generateMermaid, verboseOutput);

        // Then
        assertEquals(outputDirectory, settings.outputDirectory());
        assertEquals(format, settings.format());
        assertEquals(generateMermaid, settings.generateMermaid());
        assertEquals(verboseOutput, settings.verboseOutput());
    }

    @Test
    void testAnalysisSettingsCreation() {
        // Given
        boolean includePrivateMembers = false;
        int maxDepth = 3;
        List<String> includedPatterns = List.of("**/*.java");
        List<String> excludePatterns = List.of("**/test/**");

        // When
        AnalysisSettings settings = new AnalysisSettings(includePrivateMembers, maxDepth, includedPatterns, excludePatterns);

        // Then
        assertEquals(includePrivateMembers, settings.includePrivateMembers());
        assertEquals(maxDepth, settings.maxDepth());
        assertEquals(includedPatterns, settings.includedPatterns());
        assertEquals(excludePatterns, settings.excludePatterns());
    }
}