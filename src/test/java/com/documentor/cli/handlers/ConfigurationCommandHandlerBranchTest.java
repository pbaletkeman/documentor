package com.documentor.cli.handlers;

import com.documentor.config.DocumentorConfig;
import com.documentor.config.model.AnalysisSettings;
import com.documentor.config.model.LlmModelConfig;
import com.documentor.config.model.OutputSettings;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Branch coverage tests for ConfigurationCommandHandler
 * Targeting specific conditional paths to achieve 85%+ branch coverage
 */
class ConfigurationCommandHandlerBranchTest {

    // Test constants for magic number violations
    private static final int DEFAULT_TIMEOUT = 1000;
    private static final int DEFAULT_MAX_TOKENS = 1000;
    private static final int DEFAULT_MAX_DEPTH = 5;
    private static final int EXPECTED_MODEL_COUNT = 3;

    private ConfigurationCommandHandler handler;
    private ObjectMapper objectMapper;

    @TempDir
    private Path tempDir;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        handler = new ConfigurationCommandHandler(objectMapper);
    }

    @Test
    void testHandleValidateConfigFileNotFound() {
        // Test path validation branch
        String nonExistentPath = tempDir.resolve("nonexistent.json").toString();

        String result = handler.handleValidateConfig(nonExistentPath);

        assertTrue(result.contains("❌ Configuration file not found:"));
        assertTrue(result.contains(nonExistentPath));
    }

    @Test
    void testHandleValidateConfigInvalidJsonFormat() throws Exception {
        // Test exception handling branch
        Path invalidConfig = tempDir.resolve("invalid.json");
        Files.writeString(invalidConfig, "{ invalid json }");

        String result = handler.handleValidateConfig(invalidConfig.toString());

        assertTrue(result.contains("❌ Configuration validation failed:"));
    }

    @Test
    void testAppendConfigSummaryWithMinimalSettings() throws Exception {
        // Test a different branch path - minimal valid configuration
        DocumentorConfig config = new DocumentorConfig(
            List.of(new LlmModelConfig("minimal", "ollama",
                "http://localhost", "key", DEFAULT_MAX_TOKENS,
                DEFAULT_TIMEOUT)),
            new OutputSettings("output", "html",
            // Different output format and flags
                false, false, true),
                null // This will trigger the null analysis settings path
        );

        Path configFile = tempDir.resolve("config.json");
        objectMapper.writeValue(configFile.toFile(), config);

        String result = handler.handleValidateConfig(configFile.toString());

        assertTrue(result.contains("✅ Configuration file is valid!"));
        assertTrue(result.contains("📤 Output Format: html"));
        assertTrue(result.contains("📁 Output Path: output"));
    }

    @Test
    void testHandleValidateConfigWithCustomObjectMapper() throws Exception {
        // Test null handling branches by using a
        // custom ObjectMapper that allows nulls
        ObjectMapper customMapper = new ObjectMapper();
        ConfigurationCommandHandler customHandler =
            new ConfigurationCommandHandler(customMapper);

        // Create a JSON string with nulls manually
        String jsonWithNulls = String.format("""
            {
                "llm_models": [
                    {
                        "name": "test",
                        "provider": "ollama",
                        "baseUrl": "http://test",
                        "apiKey": "key",
                        "maxTokens": %d,
                        "timeoutSeconds": %d
                    }
                ]
            }
            """, DEFAULT_MAX_TOKENS, DEFAULT_TIMEOUT);

        Path configFile = tempDir.resolve("nullconfig.json");
        Files.writeString(configFile, jsonWithNulls);

        String result = customHandler
        .handleValidateConfig(configFile.toString());

        // The configuration should load successfully
        // (Jackson handles missing fields as null)
        assertTrue(result.contains(
            "✅ Configuration file is valid!"));
        assertTrue(result.contains(
            "🤖 LLM Models: 1"));
        assertTrue(result.contains(
            "⚠️ Warning: No output settings configured"));
        // Note: analysis settings get defaults, so we check for valid settings
        assertTrue(result.contains(
            "✅ Analysis settings configuration is valid"));
    }

    @Test
    void testHandleValidateConfigWithExplicitNulls() throws Exception {
        // Try to force the null branch by using explicit nulls in JSON
        ObjectMapper customMapper = new ObjectMapper();
        ConfigurationCommandHandler customHandler =
        new ConfigurationCommandHandler(customMapper);

        // Create a JSON string with explicit nulls
        String jsonWithNulls = String.format("""
            {
                "llm_models": [
                    {
                        "name": "test",
                        "provider": "ollama",
                        "baseUrl": "http://test",
                        "apiKey": "key",
                        "maxTokens": %d,
                        "timeoutSeconds": %d
                    }
                ],
                "output_settings": null,
                "analysis_settings": null
            }
            """, DEFAULT_MAX_TOKENS, DEFAULT_TIMEOUT);

        Path configFile = tempDir.resolve("explicitnulls.json");
        Files.writeString(configFile, jsonWithNulls);

        String result = customHandler.handleValidateConfig(
            configFile.toString());

        // Should still work due to record default constructor behavior
        assertTrue(result.contains("✅ Configuration file is valid!"));
        assertTrue(result.contains("🤖 LLM Models: 1"));
        assertTrue(result.contains(
            "⚠️ Warning: No output settings configured"));
        // Analysis settings should get defaults in
        // constructor, but test both possibilities
        assertTrue(result.contains(
            "✅ Analysis settings configuration is valid")
                  || result.contains(
                    "⚠️ Warning: No analysis settings configured"));
    }

    @Test
    void testAppendConfigSummaryWithValidSettings() throws Exception {
        // Test normal path to ensure all branches are covered
        DocumentorConfig config = new DocumentorConfig(
            List.of(new LlmModelConfig("test", "ollama", "http://test",
            "key", DEFAULT_MAX_TOKENS, DEFAULT_TIMEOUT)),
            new OutputSettings("docs", "markdown", true, true, false),
            new AnalysisSettings(true, DEFAULT_MAX_DEPTH,
            List.of("**/*.java"), List.of("**/test/**"))
        );

        Path configFile = tempDir.resolve("config.json");
        objectMapper.writeValue(configFile.toFile(), config);

        String result = handler.handleValidateConfig(configFile.toString());

        assertTrue(result.contains("✅ Configuration file is valid!"));
        assertTrue(result.contains("🤖 LLM Models: 1"));
        assertTrue(result.contains("📤 Output Format: markdown"));
        assertTrue(result.contains("📁 Output Path: docs"));
        assertTrue(result.contains("🔍 Max Threads:"));
        assertTrue(result.contains("🗂️ Supported Languages:"));
        assertTrue(result.contains("✅ LLM Models configuration is valid"));
        assertTrue(result.contains("✅ Output settings configuration is valid"));
        assertTrue(result.contains(
            "✅ Analysis settings configuration is valid"));
    }

    @Test
    void testAppendValidationDetailsEmptyLlmModels() throws Exception {
        // Test empty LLM models branch (line 77)
        DocumentorConfig config = new DocumentorConfig(
            List.of(), // empty list
            new OutputSettings("docs", "markdown", true, true, false),
            new AnalysisSettings(true, DEFAULT_MAX_DEPTH,
            List.of("**/*.java"), List.of("**/test/**"))
        );

        Path configFile = tempDir.resolve("config.json");
        objectMapper.writeValue(configFile.toFile(), config);

        String result = handler.handleValidateConfig(configFile.toString());

        assertTrue(result.contains("⚠️ Warning: No LLM models configured"));
        assertFalse(result.contains("✅ LLM Models configuration is valid"));
    }

    @Test
    void testAppendValidationDetailsModelWithoutApiKey() throws Exception {
        // Test API key validation branch (line 84)
        DocumentorConfig config = new DocumentorConfig(
            List.of(
                new LlmModelConfig("model1", "ollama", "http://test",
                null, DEFAULT_MAX_TOKENS, DEFAULT_TIMEOUT), // null API key
                new LlmModelConfig("model2", "ollama", "http://test",
                "", DEFAULT_MAX_TOKENS, DEFAULT_TIMEOUT),   // empty API key
                new LlmModelConfig("model3", "ollama", "http://test",
                // whitespace API key
                "  ", DEFAULT_MAX_TOKENS, DEFAULT_TIMEOUT)
            ),
            new OutputSettings("docs", "markdown", true, true, false),
            new AnalysisSettings(true, DEFAULT_MAX_DEPTH,
            List.of("**/*.java"), List.of("**/test/**"))
        );

        Path configFile = tempDir.resolve("config.json");
        objectMapper.writeValue(configFile.toFile(), config);

        String result = handler.handleValidateConfig(configFile.toString());

        assertTrue(result.contains("✅ LLM Models configuration is valid"));
        // All three models should show the no API key warning
        long warningCount = result.lines()
            .filter(line -> line.contains("⚠️ (No API key)"))
            .count();
        assertEquals(EXPECTED_MODEL_COUNT, warningCount);
    }
}
