package com.documentor.config;

import com.documentor.config.model.AnalysisSettings;
import com.documentor.config.model.LlmModelConfig;
import com.documentor.config.model.OutputSettings;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * 🔍 Simplified Configuration Properties for Documentor
 *
 * Main configuration class that delegates to specialized configuration records
 * for better maintainability and reduced complexity.
 */
@ConfigurationProperties(prefix = "documentor")
@Validated
@JsonInclude(JsonInclude.Include.NON_NULL)
public record DocumentorConfig(

        @JsonProperty("llm_models")
        @NotEmpty(message = "At least one LLM model must be configured")
        @Valid
        List<LlmModelConfig> llmModels,

        @JsonProperty("output_settings")
        @NotNull(message = "Output settings must be configured")
        @Valid
        OutputSettings outputSettings,

        @JsonProperty("analysis_settings")
        @Valid
        AnalysisSettings analysisSettings
) {
    // Simplified constructor with defaults
    public DocumentorConfig {
        if (analysisSettings == null) {
            analysisSettings = new AnalysisSettings(null, null, null, null);
        }
    }
}
