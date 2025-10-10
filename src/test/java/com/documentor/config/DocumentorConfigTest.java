package com.documentor.config;package com.documentor.config;



import com.documentor.config.model.LlmModelConfig;import static org.junit.jupiter.api.Assertions.*;

import com.documentor.config.model.AnalysisSettings;

import com.documentor.config.model.OutputSettings;import java.util.List;

import org.junit.jupiter.api.Test;import java.util.Map;



import java.util.List;import org.junit.jupiter.api.Test;

import com.documentor.config.model.LlmModelConfig;

import static org.junit.jupiter.api.Assertions.*;import com.documentor.config.model.OutputSettings;

import com.documentor.config.model.AnalysisSettings;

/**

 * Tests for DocumentorConfig and related configuration classes/**

 */ * 🧪 Comprehensive tests for DocumentorConfig and nested record classes

class DocumentorConfigTest { * 

 * Tests configuration property handling, default values, and validation

    @Test * for all configuration records: LlmModelConfig, OutputSettings, AnalysisSettings

    void testLlmModelConfigCreation() { */

        // Givenclass DocumentorConfigTest {

        String name = "gpt-4";

        String provider = "openai";    @Test

        String baseUrl = "https://api.openai.com/v1";    void testLlmModelConfig_WithAllValues() {

        String apiKey = "test-api-key";        // Given

        Integer maxTokens = 2000;        String name = "gpt-4";

        Integer timeoutSeconds = 60;        String provider = "openai";

        String baseUrl = "https://api.openai.com/v1";

        // When        String apiKey = "test-api-key";

        LlmModelConfig config = new LlmModelConfig(        Integer maxTokens = 2000;

            name, provider, baseUrl, apiKey, maxTokens, timeoutSeconds        Integer timeoutSeconds = 60;

        );

        // When

        // Then        LlmModelConfig config = new LlmModelConfig(

        assertEquals(name, config.name());            name, provider, baseUrl, apiKey, maxTokens, timeoutSeconds

        assertEquals(provider, config.provider());        );

        assertEquals(baseUrl, config.baseUrl());

        assertEquals(apiKey, config.apiKey());        // Then

        assertEquals(maxTokens, config.maxTokens());        assertEquals(name, config.name());

        assertEquals(timeoutSeconds, config.timeoutSeconds());        assertEquals(provider, config.provider());

    }        assertEquals(baseUrl, config.baseUrl());

        assertEquals(apiKey, config.apiKey());

    @Test        assertEquals(maxTokens, config.maxTokens());

    void testOutputSettingsCreation() {        assertEquals(timeoutSeconds, config.timeoutSeconds());

        // Given    }

        String outputDirectory = "docs/output";

        String format = "markdown";    @Test

        Boolean generateMermaid = true;    void testLlmModelConfig_WithDefaultValues() {

        Boolean verboseOutput = false;        // Given

        String name = "claude-3";

        // When        String apiKey = "claude-api-key";

        OutputSettings settings = new OutputSettings(

            outputDirectory, format, generateMermaid, verboseOutput        // When

        );        LlmModelConfig config = new LlmModelConfig(

            name, apiKey, null, null, null, null, null

        // Then        );

        assertEquals(outputDirectory, settings.outputDirectory());

        assertEquals(format, settings.format());        // Then

        assertEquals(generateMermaid, settings.generateMermaid());        assertEquals(name, config.name());

        assertEquals(verboseOutput, settings.verboseOutput());        assertEquals(apiKey, config.apiKey());

    }        assertNull(config.endpoint());

        assertEquals(4096, config.maxTokens()); // Default value

    @Test        assertEquals(0.7, config.temperature()); // Default value

    void testAnalysisSettingsCreation() {        assertEquals(30, config.timeoutSeconds()); // Default value

        // Given        assertEquals(Map.of(), config.additionalConfig()); // Default empty map

        Boolean includePrivateMembers = true;    }

        Integer maxDepth = 10;

        List<String> includedPatterns = List.of("**/*.java");    @Test

        List<String> excludePatterns = List.of("**/test/**");    void testLlmModelConfig_WithPartialDefaults() {

        // Given

        // When        String name = "gpt-3.5-turbo";

        AnalysisSettings settings = new AnalysisSettings(        String apiKey = "openai-key";

            includePrivateMembers, maxDepth, includedPatterns, excludePatterns        String endpoint = "https://api.openai.com/v1";

        );        Integer maxTokens = 1500;



        // Then        // When

        assertEquals(includePrivateMembers, settings.includePrivateMembers());        LlmModelConfig config = new LlmModelConfig(

        assertEquals(maxDepth, settings.maxDepth());            name, apiKey, endpoint, maxTokens, null, null, null

        assertEquals(includedPatterns, settings.includedPatterns());        );

        assertEquals(excludePatterns, settings.excludePatterns());

    }        // Then

        assertEquals(name, config.name());

    @Test        assertEquals(apiKey, config.apiKey());

    void testDocumentorConfigCreation() {        assertEquals(endpoint, config.endpoint());

        // Given        assertEquals(maxTokens, config.maxTokens());

        LlmModelConfig llmModel = new LlmModelConfig(        assertEquals(0.7, config.temperature()); // Default

            "gpt-4", "openai", "https://api.openai.com/v1", "test-key", 1000, 30        assertEquals(30, config.timeoutSeconds()); // Default

        );        assertEquals(Map.of(), config.additionalConfig()); // Default

        OutputSettings outputSettings = new OutputSettings(    }

            "docs", "markdown", true, false

        );    @Test

        AnalysisSettings analysisSettings = new AnalysisSettings(    void testOutputSettings_WithAllValues() {

            false, 5, List.of("**/*.java"), List.of("**/test/**")        // Given

        );        String outputPath = "/docs/output";

        String format = "html";

        // When        Boolean includeIcons = false;

        DocumentorConfig config = new DocumentorConfig(        Boolean generateUnitTests = false;

            List.of(llmModel), outputSettings, analysisSettings        Double targetCoverage = 0.95;

        );

        // When

        // Then        OutputSettings settings = new OutputSettings(

        assertNotNull(config);            outputPath, format, includeIcons, generateUnitTests, targetCoverage, false, outputPath

        assertEquals(1, config.llmModels().size());        );

        assertEquals(llmModel, config.llmModels().get(0));

        assertEquals(outputSettings, config.outputSettings());        // Then

        assertEquals(analysisSettings, config.analysisSettings());        assertEquals(outputPath, settings.outputPath());

    }        assertEquals(format, settings.format());

        assertEquals(includeIcons, settings.includeIcons());

    @Test        assertEquals(generateUnitTests, settings.generateUnitTests());

    void testConfigurationEquality() {        assertEquals(targetCoverage, settings.targetCoverage());

        // Given    }

        LlmModelConfig config1 = new LlmModelConfig(

            "gpt-4", "openai", "https://api.openai.com/v1", "key", 1000, 30    @Test

        );    void testOutputSettings_WithDefaultValues() {

        LlmModelConfig config2 = new LlmModelConfig(        // Given

            "gpt-4", "openai", "https://api.openai.com/v1", "key", 1000, 30        String outputPath = "/docs/default";

        );

        // When

        // Then        OutputSettings settings = new OutputSettings(

        assertEquals(config1, config2);            outputPath, null, null, null, null, null, null

        assertEquals(config1.hashCode(), config2.hashCode());        );

    }

}        // Then
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
        OutputSettings settings = new OutputSettings(
            outputPath, format, includeIcons, null, null, null, null
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
        AnalysisSettings settings = new AnalysisSettings(
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
        AnalysisSettings settings = new AnalysisSettings(
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
        AnalysisSettings settings = new AnalysisSettings(
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
        List<LlmModelConfig> llmModels = List.of(
            new LlmModelConfig("gpt-4", "key1", null, null, null, null, null),
            new LlmModelConfig("claude-3", "key2", null, null, null, null, null)
        );
        OutputSettings outputSettings = new OutputSettings(
            "/docs", null, null, null, null, null, null
        );
        AnalysisSettings analysisSettings = new AnalysisSettings(
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
        List<LlmModelConfig> llmModels = List.of(
            new LlmModelConfig("gpt-4", "key1", null, null, null, null, null)
        );
        OutputSettings outputSettings = new OutputSettings(
            "/docs", null, null, null, null, null, null
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
        AnalysisSettings settingsTrue = new AnalysisSettings(
            true, null, null, null
        );
        AnalysisSettings settingsFalse = new AnalysisSettings(
            false, null, null, null
        );
        AnalysisSettings settingsDefault = new AnalysisSettings(
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
        LlmModelConfig config1 = new LlmModelConfig(
            "gpt-4", "key1", "endpoint1", 1000, 0.5, 30, Map.of()
        );
        LlmModelConfig config2 = new LlmModelConfig(
            "gpt-4", "key1", "endpoint1", 1000, 0.5, 30, Map.of()
        );
        LlmModelConfig config3 = new LlmModelConfig(
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
        OutputSettings settings1 = new OutputSettings(
            "/docs", "markdown", true, true, 0.90, false, "/docs"
        );
        OutputSettings settings2 = new OutputSettings(
            "/docs", "markdown", true, true, 0.90, false, "/docs"
        );
        OutputSettings settings3 = new OutputSettings(
            "/other", "html", false, false, 0.95, true, "/other"
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
        AnalysisSettings settings1 = new AnalysisSettings(
            true, 4, List.of("java"), List.of("**/test/**")
        );
        AnalysisSettings settings2 = new AnalysisSettings(
            true, 4, List.of("java"), List.of("**/test/**")
        );
        AnalysisSettings settings3 = new AnalysisSettings(
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
        List<LlmModelConfig> llmModels = List.of(
            new LlmModelConfig("gpt-4", "key1", null, null, null, null, null)
        );
        OutputSettings outputSettings = new OutputSettings(
            "/docs", null, null, null, null, null, null
        );
        AnalysisSettings analysisSettings = new AnalysisSettings(
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
        AnalysisSettings settings = new AnalysisSettings(
            null, null, null, null
        );

        // Then
        assertEquals(expectedProcessors, settings.maxThreads());
    }
}
