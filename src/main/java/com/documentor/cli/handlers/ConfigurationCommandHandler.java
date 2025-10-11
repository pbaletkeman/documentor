package com.documentor.cli.handlers;

import com.documentor.config.DocumentorConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * ⚙️ Handler for configuration validation commands
 */
@Component
public final class ConfigurationCommandHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationCommandHandler.class);

    private final ObjectMapper objectMapper;

    public ConfigurationCommandHandler(final ObjectMapper objectMapperParam) {
        this.objectMapper = objectMapperParam;
    }

    public String handleValidateConfig(final String configPath) {
        try {
            LOGGER.info("🔍 Validating configuration file: {}", configPath);

            Path config = Paths.get(configPath);
            if (!Files.exists(config)) {
                return "❌ Configuration file not found: " + configPath;
            }

            // Parse and validate configuration
            DocumentorConfig validatedConfig = objectMapper.readValue(config.toFile(), DocumentorConfig.class);

            StringBuilder result = new StringBuilder();
            result.append("✅ Configuration file is valid!\n\n");

            appendConfigSummary(result, validatedConfig);
            appendValidationDetails(result, validatedConfig);

            return result.toString();

        } catch (Exception e) {
            LOGGER.error("Configuration validation failed", e);
            return "❌ Configuration validation failed: " + e.getMessage();
        }
    }

    private void appendConfigSummary(final StringBuilder result, final DocumentorConfig config) {
        result.append("📋 Configuration Summary:\n");
        result.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        result.append("🤖 LLM Models: ").append(config.llmModels().size()).append("\n");

        if (config.outputSettings() != null) {
            result.append("📤 Output Format: ").append(config.outputSettings().format()).append("\n");
            result.append("📁 Output Path: ").append(config.outputSettings().outputPath()).append("\n");
        }

        if (config.analysisSettings() != null) {
            result.append("🔍 Max Threads: ").append(config.analysisSettings().maxThreads()).append("\n");
            result.append("🗂️ Supported Languages: ")
                    .append(String.join(", ", config.analysisSettings().supportedLanguages()))
                    .append("\n");
        }
        result.append("\n");
    }

    private void appendValidationDetails(final StringBuilder result, final DocumentorConfig config) {
        result.append("🔍 Validation Details:\n");
        result.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");

        // Validate LLM models
        if (config.llmModels().isEmpty()) {
            result.append("⚠️ Warning: No LLM models configured\n");
        } else {
            result.append("✅ LLM Models configuration is valid\n");
            for (int i = 0; i < config.llmModels().size(); i++) {
                var model = config.llmModels().get(i);
                result.append("   ").append(i + 1).append(". ").append(model.name());
                if (model.apiKey() == null || model.apiKey().trim().isEmpty()) {
                    result.append(" ⚠️ (No API key)");
                }
                result.append("\n");
            }
        }

        // Validate output settings
        if (config.outputSettings() != null) {
            result.append("✅ Output settings configuration is valid\n");
        } else {
            result.append("⚠️ Warning: No output settings configured (using defaults)\n");
        }

        // Validate analysis settings
        if (config.analysisSettings() != null) {
            result.append("✅ Analysis settings configuration is valid\n");
        } else {
            result.append("⚠️ Warning: No analysis settings configured (using defaults)\n");
        }
    }
}
