package com.documentor.config;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * 🧪 Comprehensive tests for DocumentorConfig and nested record classes
 * 
 * Tests configuration property handling, default values, and validation
 * for all configuration records: LlmModelConfig, OutputSettings, AnalysisSettings
 */
class DocumentorConfigTest {

    @Test
    void testLlmModelConfig_WithAllValues() {
        // Given
        String name = "gpt-4";
        String apiKey = "test-api-key";
        String endpoint = "https://api.openai.com/v1";
        Integer maxTokens = 2000;
        Double temperature = 0.5;
        Integer timeoutSeconds = 60;
        Map<String, Object> additionalConfig = Map.of("top_p", 0.95);

        // When
        DocumentorConfig.LlmModelConfig config = new DocumentorConfig.LlmModelConfig(
            name, apiKey, endpoint, maxTokens, temperature, timeoutSeconds, additionalConfig
        );

        // Then
        assertEquals(name, config.name());
        assertEquals(apiKey, config.apiKey());
        assertEquals(endpoint, config.endpoint());
        assertEquals(maxTokens, config.maxTokens());
        assertEquals(temperature, config.temperature());
        assertEquals(timeoutSeconds, config.timeoutSeconds());
        assertEquals(additionalConfig, config.additionalConfig());
    }

    @Test
    void testLlmModelConfig_WithDefaultValues() {
        // Given
        String name = "claude-3";
        String apiKey = "claude-api-key";

        // When
        DocumentorConfig.LlmModelConfig config = new DocumentorConfig.LlmModelConfig(
            name, apiKey, null, null, null, null, null
        );

        // Then
        assertEquals(name, config.name());
        assertEquals(apiKey, config.apiKey());
        assertNull(config.endpoint());
        assertEquals(4096, config.maxTokens()); // Default value
        assertEquals(0.7, config.temperature()); // Default value
        assertEquals(30, config.timeoutSeconds()); // Default value
        assertEquals(Map.of(), config.additionalConfig()); // Default empty map
    }

    @Test
    void testLlmModelConfig_WithPartialDefaults() {
        // Given
        String name = "gpt-3.5-turbo";
        String apiKey = "openai-key";
        String endpoint = "https://api.openai.com/v1";
        Integer maxTokens = 1500;

        // When
        DocumentorConfig.LlmModelConfig config = new DocumentorConfig.LlmModelConfig(
            name, apiKey, endpoint, maxTokens, null, null, null
        );

        // Then
        assertEquals(name, config.name());
        assertEquals(apiKey, config.apiKey());
        assertEquals(endpoint, config.endpoint());
        assertEquals(maxTokens, config.maxTokens());
        assertEquals(0.7, config.temperature()); // Default
        assertEquals(30, config.timeoutSeconds()); // Default
        assertEquals(Map.of(), config.additionalConfig()); // Default
    }

    @Test
    void testOutputSettings_WithAllValues() {
        // Given
        String outputPath = "/docs/output";
        String format = "html";
        Boolean includeIcons = false;
        Boolean generateUnitTests = false;
        Double targetCoverage = 0.95;

        // When
        DocumentorConfig.OutputSettings settings = new DocumentorConfig.OutputSettings(
            outputPath, format, includeIcons, generateUnitTests, targetCoverage
        );

        // Then
        assertEquals(outputPath, settings.outputPath());
        assertEquals(format, settings.format());
        assertEquals(includeIcons, settings.includeIcons());
        assertEquals(generateUnitTests, settings.generateUnitTests());
        assertEquals(targetCoverage, settings.targetCoverage());
    }

    @Test
    void testOutputSettings_WithDefaultValues() {
        // Given
        String outputPath = "/docs/default";

        // When
        DocumentorConfig.OutputSettings settings = new DocumentorConfig.OutputSettings(
            outputPath, null, null, null, null
        );

        // Then
        assertEquals(outputPath, settings.outputPath());
        assertEquals("markdown", settings.format()); // Default
        assertEquals(true, settings.includeIcons()); // Default
        assertEquals(true, settings.generateUnitTests()); // Default
        assertEquals(0.90, settings.targetCoverage()); // Default
    }

    @Test
    void testOutputSettings_WithPartialDefaults() {
        // Given
        String outputPath = "/docs/mixed";
        String format = "json";
        Boolean includeIcons = false;

        // When
        DocumentorConfig.OutputSettings settings = new DocumentorConfig.OutputSettings(
            outputPath, format, includeIcons, null, null
        );

        // Then
        assertEquals(outputPath, settings.outputPath());
        assertEquals(format, settings.format());
        assertEquals(includeIcons, settings.includeIcons());
        assertEquals(true, settings.generateUnitTests()); // Default
        assertEquals(0.90, settings.targetCoverage()); // Default
    }

    @Test
    void testAnalysisSettings_WithAllValues() {
        // Given
        Boolean includePrivateMembers = true;
        Integer maxThreads = 8;
        List<String> supportedLanguages = List.of("java", "python", "javascript");
        List<String> excludePatterns = List.of("**/build/**", "**/dist/**");

        // When
        DocumentorConfig.AnalysisSettings settings = new DocumentorConfig.AnalysisSettings(
            includePrivateMembers, maxThreads, supportedLanguages, excludePatterns
        );

        // Then
        assertEquals(includePrivateMembers, settings.includePrivateMembers());
        assertEquals(maxThreads, settings.maxThreads());
        assertEquals(supportedLanguages, settings.supportedLanguages());
        assertEquals(excludePatterns, settings.excludePatterns());
    }

    @Test
    void testAnalysisSettings_WithDefaultValues() {
        // When
        DocumentorConfig.AnalysisSettings settings = new DocumentorConfig.AnalysisSettings(
            null, null, null, null
        );

        // Then
        assertEquals(false, settings.includePrivateMembers()); // Default
        assertEquals(Runtime.getRuntime().availableProcessors(), settings.maxThreads()); // Default
        assertEquals(List.of("java", "python"), settings.supportedLanguages()); // Default
        assertEquals(List.of("**/test/**", "**/target/**", "**/__pycache__/**"), settings.excludePatterns()); // Default
    }

    @Test
    void testAnalysisSettings_WithPartialDefaults() {
        // Given
        Boolean includePrivateMembers = true;
        Integer maxThreads = 4;

        // When
        DocumentorConfig.AnalysisSettings settings = new DocumentorConfig.AnalysisSettings(
            includePrivateMembers, maxThreads, null, null
        );

        // Then
        assertEquals(includePrivateMembers, settings.includePrivateMembers());
        assertEquals(maxThreads, settings.maxThreads());
        assertEquals(List.of("java", "python"), settings.supportedLanguages()); // Default
        assertEquals(List.of("**/test/**", "**/target/**", "**/__pycache__/**"), settings.excludePatterns()); // Default
    }

    @Test
    void testDocumentorConfig_Complete() {
        // Given
        List<DocumentorConfig.LlmModelConfig> llmModels = List.of(
            new DocumentorConfig.LlmModelConfig("gpt-4", "key1", null, null, null, null, null),
            new DocumentorConfig.LlmModelConfig("claude-3", "key2", null, null, null, null, null)
        );
        DocumentorConfig.OutputSettings outputSettings = new DocumentorConfig.OutputSettings(
            "/docs", null, null, null, null
        );
        DocumentorConfig.AnalysisSettings analysisSettings = new DocumentorConfig.AnalysisSettings(
            null, null, null, null
        );

        // When
        DocumentorConfig config = new DocumentorConfig(llmModels, outputSettings, analysisSettings);

        // Then
        assertEquals(llmModels, config.llmModels());
        assertEquals(outputSettings, config.outputSettings());
        assertEquals(analysisSettings, config.analysisSettings());
    }

    @Test
    void testDocumentorConfig_WithNullAnalysisSettings() {
        // Given
        List<DocumentorConfig.LlmModelConfig> llmModels = List.of(
            new DocumentorConfig.LlmModelConfig("gpt-4", "key1", null, null, null, null, null)
        );
        DocumentorConfig.OutputSettings outputSettings = new DocumentorConfig.OutputSettings(
            "/docs", null, null, null, null
        );

        // When
        DocumentorConfig config = new DocumentorConfig(llmModels, outputSettings, null);

        // Then
        assertEquals(llmModels, config.llmModels());
        assertEquals(outputSettings, config.outputSettings());
        assertNotNull(config.analysisSettings()); // Should create default
        assertEquals(false, config.analysisSettings().includePrivateMembers()); // Default value
    }

    @Test
    void testAnalysisSettings_IncludePrivateMembersMethod() {
        // Given
        DocumentorConfig.AnalysisSettings settingsTrue = new DocumentorConfig.AnalysisSettings(
            true, null, null, null
        );
        DocumentorConfig.AnalysisSettings settingsFalse = new DocumentorConfig.AnalysisSettings(
            false, null, null, null
        );
        DocumentorConfig.AnalysisSettings settingsDefault = new DocumentorConfig.AnalysisSettings(
            null, null, null, null
        );

        // Then
        assertTrue(settingsTrue.includePrivateMembers());
        assertFalse(settingsFalse.includePrivateMembers());
        assertFalse(settingsDefault.includePrivateMembers()); // Default is false
    }

    @Test
    void testLlmModelConfig_Equality() {
        // Given
        DocumentorConfig.LlmModelConfig config1 = new DocumentorConfig.LlmModelConfig(
            "gpt-4", "key1", "endpoint1", 1000, 0.5, 30, Map.of()
        );
        DocumentorConfig.LlmModelConfig config2 = new DocumentorConfig.LlmModelConfig(
            "gpt-4", "key1", "endpoint1", 1000, 0.5, 30, Map.of()
        );
        DocumentorConfig.LlmModelConfig config3 = new DocumentorConfig.LlmModelConfig(
            "claude-3", "key2", "endpoint2", 2000, 0.7, 60, Map.of()
        );

        // Then
        assertEquals(config1, config2);
        assertNotEquals(config1, config3);
        assertEquals(config1.hashCode(), config2.hashCode());
        assertNotEquals(config1.hashCode(), config3.hashCode());
    }

    @Test
    void testOutputSettings_Equality() {
        // Given
        DocumentorConfig.OutputSettings settings1 = new DocumentorConfig.OutputSettings(
            "/docs", "markdown", true, true, 0.90
        );
        DocumentorConfig.OutputSettings settings2 = new DocumentorConfig.OutputSettings(
            "/docs", "markdown", true, true, 0.90
        );
        DocumentorConfig.OutputSettings settings3 = new DocumentorConfig.OutputSettings(
            "/other", "html", false, false, 0.95
        );

        // Then
        assertEquals(settings1, settings2);
        assertNotEquals(settings1, settings3);
        assertEquals(settings1.hashCode(), settings2.hashCode());
        assertNotEquals(settings1.hashCode(), settings3.hashCode());
    }

    @Test
    void testAnalysisSettings_Equality() {
        // Given
        DocumentorConfig.AnalysisSettings settings1 = new DocumentorConfig.AnalysisSettings(
            true, 4, List.of("java"), List.of("**/test/**")
        );
        DocumentorConfig.AnalysisSettings settings2 = new DocumentorConfig.AnalysisSettings(
            true, 4, List.of("java"), List.of("**/test/**")
        );
        DocumentorConfig.AnalysisSettings settings3 = new DocumentorConfig.AnalysisSettings(
            false, 8, List.of("python"), List.of("**/build/**")
        );

        // Then
        assertEquals(settings1, settings2);
        assertNotEquals(settings1, settings3);
        assertEquals(settings1.hashCode(), settings2.hashCode());
        assertNotEquals(settings1.hashCode(), settings3.hashCode());
    }

    @Test
    void testDocumentorConfig_ToString() {
        // Given
        List<DocumentorConfig.LlmModelConfig> llmModels = List.of(
            new DocumentorConfig.LlmModelConfig("gpt-4", "key1", null, null, null, null, null)
        );
        DocumentorConfig.OutputSettings outputSettings = new DocumentorConfig.OutputSettings(
            "/docs", null, null, null, null
        );
        DocumentorConfig.AnalysisSettings analysisSettings = new DocumentorConfig.AnalysisSettings(
            null, null, null, null
        );
        DocumentorConfig config = new DocumentorConfig(llmModels, outputSettings, analysisSettings);

        // When
        String configString = config.toString();

        // Then
        assertNotNull(configString);
        assertTrue(configString.contains("DocumentorConfig"));
        assertTrue(configString.contains("gpt-4"));
        assertTrue(configString.contains("/docs"));
    }

    @Test
    void testMaxThreadsDefaultValue() {
        // Given
        int expectedProcessors = Runtime.getRuntime().availableProcessors();

        // When
        DocumentorConfig.AnalysisSettings settings = new DocumentorConfig.AnalysisSettings(
            null, null, null, null
        );

        // Then
        assertEquals(expectedProcessors, settings.maxThreads());
    }
}