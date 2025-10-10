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

    @Test
    void handleValidateConfigFileNotFound() {
        ObjectMapper mapper = new ObjectMapper();
        ConfigurationCommandHandler handler = new ConfigurationCommandHandler(mapper);

        String res = handler.handleValidateConfig("nonexistent-file.json");
        assertTrue(res.contains("Configuration file not found"));
    }

    @Test
    void handleValidateConfigValidConfigProducesSummary(@TempDir Path tmp) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ConfigurationCommandHandler handler = new ConfigurationCommandHandler(mapper);

        LlmModelConfig model = new LlmModelConfig("m", "openai", "http://x", null, 100, 10);
        OutputSettings output = new OutputSettings(tmp.toString(), "md", true, false);
        AnalysisSettings analysis = new AnalysisSettings(false, 2, List.of("**/*.java"), List.of("**/test/**"));
        DocumentorConfig config = new DocumentorConfig(List.of(model), output, analysis);

        Path cfg = tmp.resolve("cfg.json");
        mapper.writeValue(cfg.toFile(), config);

        String res = handler.handleValidateConfig(cfg.toString());

        assertTrue(res.contains("Configuration file is valid"));
        assertTrue(res.contains("LLM Models"));
        assertTrue(res.contains("Output Format"));
        assertTrue(res.contains("Analysis settings" ) || res.contains("Max Threads"));
    }

    @Test
    void handleValidateConfigWithEmptyLlmModels(@TempDir Path tmp) throws Exception {
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
    void handleValidateConfigWithMissingApiKeys(@TempDir Path tmp) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ConfigurationCommandHandler handler = new ConfigurationCommandHandler(mapper);

        // Create models with null and empty API keys
        LlmModelConfig modelWithNullKey = new LlmModelConfig("modelNull", "openai", "http://test", null, 100, 10);
        LlmModelConfig modelWithEmptyKey = new LlmModelConfig("modelEmpty", "openai", "http://test", "", 100, 10);
        LlmModelConfig modelWithWhitespaceKey = new LlmModelConfig("modelWhitespace", "openai", "http://test", "   ", 100, 10);
        LlmModelConfig modelWithValidKey = new LlmModelConfig("modelValid", "openai", "http://test", "valid-key", 100, 10);
        
        OutputSettings output = new OutputSettings(tmp.toString(), "md", true, false);
        AnalysisSettings analysis = new AnalysisSettings(false, 2, List.of("**/*.java"), List.of("**/test/**"));
        DocumentorConfig config = new DocumentorConfig(List.of(modelWithNullKey, modelWithEmptyKey, modelWithWhitespaceKey, modelWithValidKey), output, analysis);

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
    void handleValidateConfigWithInvalidJson(@TempDir Path tmp) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ConfigurationCommandHandler handler = new ConfigurationCommandHandler(mapper);

        // Create invalid JSON file
        Path cfg = tmp.resolve("invalid.json");
        java.nio.file.Files.writeString(cfg, "{invalid json content");

        String res = handler.handleValidateConfig(cfg.toString());

        assertTrue(res.contains("Configuration validation failed"));
    }
}
