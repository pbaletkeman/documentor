package com.documentor.config;

import com.documentor.config.model.LlmModelConfig;
import com.documentor.config.model.OutputSettings;
import com.documentor.config.model.AnalysisSettings;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DocumentorConfigTest {

    // Test constants for magic number violations
    private static final int MAX_TOKENS = 2000;
    private static final int TIMEOUT_SECONDS = 60;
    private static final int MAX_DEPTH = 3;

    @Test
    void testLlmModelConfigCreation() {
        String name = "gpt-4";
        String provider = "openai";
        String baseUrl = "https://api.openai.com/v1";
        String apiKey = "test-api-key";
        Integer maxTokens = MAX_TOKENS;
        Integer timeoutSeconds = TIMEOUT_SECONDS;

        LlmModelConfig config = new LlmModelConfig(name, provider, baseUrl, apiKey, maxTokens, timeoutSeconds);

        assertEquals(name, config.name());
        assertEquals(provider, config.provider());
        assertEquals(baseUrl, config.baseUrl());
        assertEquals(apiKey, config.apiKey());
        assertEquals(maxTokens, config.maxTokens());
        assertEquals(timeoutSeconds, config.timeoutSeconds());
    }

    @Test
    void testOutputSettingsCreation() {
        String outputDirectory = "docs/output";
        String format = "markdown";
        boolean generateMermaid = true;
        boolean verboseOutput = false;

        OutputSettings settings = new OutputSettings(outputDirectory, format, generateMermaid, verboseOutput);

        assertEquals(outputDirectory, settings.outputDirectory());
        assertEquals(format, settings.format());
        assertEquals(generateMermaid, settings.generateMermaid());
        assertEquals(verboseOutput, settings.verboseOutput());
    }

    @Test
    void testAnalysisSettingsCreation() {
        boolean includePrivateMembers = false;
        int maxDepth = MAX_DEPTH;
        List<String> includedPatterns = List.of("**/*.java");
        List<String> excludePatterns = List.of("**/test/**");

        AnalysisSettings settings = new AnalysisSettings(includePrivateMembers, maxDepth, includedPatterns, excludePatterns);

        assertEquals(includePrivateMembers, settings.includePrivateMembers());
        assertEquals(maxDepth, settings.maxDepth());
        assertEquals(includedPatterns, settings.includedPatterns());
        assertEquals(excludePatterns, settings.excludePatterns());
    }
}
