package com.documentor.cli.handlers;

import com.documentor.config.DocumentorConfig;
import com.documentor.config.model.AnalysisSettings;
import com.documentor.config.model.LlmModelConfig;
import com.documentor.config.model.OutputSettings;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigurationCommandHandlerTest {

    private static final int TEST_MAX_TOKENS = 100;
    private static final int TEST_TIMEOUT_SECONDS = 10;

    @Test
    void handleValidateConfigFileNotFound() {
        ObjectMapper mapper = new ObjectMapper();
        ConfigurationCommandHandler handler = new ConfigurationCommandHandler(mapper);

        String res = handler.handleValidateConfig("nonexistent-file.json");
        assertTrue(res.contains("Configuration file not found"));
    }

    @Test
    void handleValidateConfigValidConfigProducesSummary(@TempDir final Path tmp) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ConfigurationCommandHandler handler = new ConfigurationCommandHandler(mapper);

        LlmModelConfig model = new LlmModelConfig("m", "openai", "http://x", null, TEST_MAX_TOKENS, TEST_TIMEOUT_SECONDS);
        OutputSettings output = new OutputSettings(tmp.toString(), "md", true, false);
        AnalysisSettings analysis = new AnalysisSettings(false, 2, List.of("**/*.java"), List.of("**/test/**"));
        DocumentorConfig config = new DocumentorConfig(List.of(model), output, analysis);

        Path cfg = tmp.resolve("cfg.json");
        mapper.writeValue(cfg.toFile(), config);

        String res = handler.handleValidateConfig(cfg.toString());

        assertTrue(res.contains("Configuration file is valid"));
        assertTrue(res.contains("LLM Models"));
        assertTrue(res.contains("Output Format"));
        assertTrue(res.contains("Analysis settings") || res.contains("Max Threads"));
    }

    @Test
    void handleValidateConfigWithEmptyLlmModels(@TempDir final Path tmp) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ConfigurationCommandHandler handler = new ConfigurationCommandHandler(mapper);

        // Create config with empty LLM models list
        OutputSettings output = new OutputSettings(tmp.toString(), "md", true, false);
        AnalysisSettings analysis = new AnalysisSettings(false, 2, List.of("**/*.java"), List.of("**/test/**"));
        DocumentorConfig config = new DocumentorConfig(List.of(), output, analysis);

        Path cfg = tmp.resolve("empty-models.json");
        mapper.writeValue(cfg.toFile(), config);

        String res = handler.handleValidateConfig(cfg.toString());

        assertTrue(res.contains("Configuration file is valid"));
        assertTrue(res.contains("Warning: No LLM models configured"));
    }

    @Test
    void handleValidateConfigWithMissingApiKeys(@TempDir final Path tmp) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ConfigurationCommandHandler handler = new ConfigurationCommandHandler(mapper);

        // Create models with null and empty API keys
        LlmModelConfig modelWithNullKey = new LlmModelConfig("modelNull", "openai", "http://test", null, TEST_MAX_TOKENS, TEST_TIMEOUT_SECONDS);
        LlmModelConfig modelWithEmptyKey = new LlmModelConfig("modelEmpty", "openai", "http://test", "", TEST_MAX_TOKENS, TEST_TIMEOUT_SECONDS);
        LlmModelConfig modelWithWhitespaceKey = new LlmModelConfig("modelWhitespace", "openai", "http://test", "   ", TEST_MAX_TOKENS, TEST_TIMEOUT_SECONDS);
        LlmModelConfig modelWithValidKey = new LlmModelConfig("modelValid", "openai", "http://test", "valid-key", TEST_MAX_TOKENS, TEST_TIMEOUT_SECONDS);

        OutputSettings output = new OutputSettings(tmp.toString(), "md", true, false);
        AnalysisSettings analysis = new AnalysisSettings(false, 2, List.of("**/*.java"),
                List.of("**/test/**"));
        DocumentorConfig config = new DocumentorConfig(
                List.of(modelWithNullKey, modelWithEmptyKey, modelWithWhitespaceKey, modelWithValidKey),
                output, analysis);

        Path cfg = tmp.resolve("missing-keys.json");
        mapper.writeValue(cfg.toFile(), config);

        String res = handler.handleValidateConfig(cfg.toString());

        assertTrue(res.contains("Configuration file is valid"));
        assertTrue(res.contains("1. modelNull ⚠️ (No API key)"));
        assertTrue(res.contains("2. modelEmpty ⚠️ (No API key)"));
        assertTrue(res.contains("3. modelWhitespace ⚠️ (No API key)"));
        assertTrue(res.contains("4. modelValid"));
        // Should not show warning for valid key
        assertTrue(!res.contains("4. modelValid ⚠️"));
    }

    @Test
    void handleValidateConfigWithInvalidJson(@TempDir final Path tmp) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ConfigurationCommandHandler handler = new ConfigurationCommandHandler(mapper);

        // Create invalid JSON file
        Path cfg = tmp.resolve("invalid.json");
        java.nio.file.Files.writeString(cfg, "{invalid json content");

        String res = handler.handleValidateConfig(cfg.toString());

        assertTrue(res.contains("Configuration validation failed"));
    }

    @Test
    void handleValidateConfigWithNullOutputSettings(@TempDir final Path tmp) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ConfigurationCommandHandler handler = new ConfigurationCommandHandler(mapper);

        // Create JSON manually to bypass validation during object creation
        String jsonWithNullOutput = """
            {
                "llm_models": [
                    {
                        "name": "test-model",
                        "provider": "openai",
                        "baseUrl": "http://test.com",
                        "apiKey": "test-key",
                        "maxTokens": 100,
                        "requestTimeout": 10
                    }
                ],
                "output_settings": null,
                "analysis_settings": {
                    "includePrivateElements": false,
                    "maxThreads": 2,
                    "includePatterns": ["**/*.java"],
                    "excludePatterns": ["**/test/**"]
                }
            }
            """;

        Path cfg = tmp.resolve("null-output.json");
        java.nio.file.Files.writeString(cfg, jsonWithNullOutput);

        String res = handler.handleValidateConfig(cfg.toString());

        // Should either fail validation or show warning about null output settings
        assertTrue(res.contains("Configuration validation failed")
                || res.contains("Warning: No output settings configured"));
    }

    @Test
    void handleValidateConfigWithNullAnalysisSettings(@TempDir final Path tmp) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ConfigurationCommandHandler handler = new ConfigurationCommandHandler(mapper);

        // Create JSON manually to bypass validation during object creation
        String jsonWithNullAnalysis = """
            {
                "llm_models": [
                    {
                        "name": "test-model",
                        "provider": "openai",
                        "baseUrl": "http://test.com",
                        "apiKey": "test-key",
                        "maxTokens": 100,
                        "requestTimeout": 10
                    }
                ],
                "output_settings": {
                    "outputPath": "%s",
                    "format": "md",
                    "includeUnitTests": true,
                    "includeSourceCode": false
                },
                "analysis_settings": null
            }
            """.formatted(tmp.toString());

        Path cfg = tmp.resolve("null-analysis.json");
        java.nio.file.Files.writeString(cfg, jsonWithNullAnalysis);

        String res = handler.handleValidateConfig(cfg.toString());

        // Should either fail validation or show warning about null analysis settings
        assertTrue(res.contains("Configuration validation failed")
                || res.contains("Warning: No analysis settings configured"));
    }
}

