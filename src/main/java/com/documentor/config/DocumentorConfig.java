package com.documentor.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Map;

/**
 * 🔧 Configuration properties for the Documentor application
 *
 * This class maps the external JSON configuration file to Java objects,
 * providing type-safe access to LLM configurations and application settings.
 */
@ConfigurationProperties(prefix = "documentor")
@Validated
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

    // Default values constants
    private static final int DEFAULT_MAX_TOKENS = 4096;
    private static final double DEFAULT_TEMPERATURE = 0.7;
    private static final int DEFAULT_TIMEOUT_SECONDS = 30;
    private static final double DEFAULT_TOP_P = 0.90;
    
    /**
     * 🤖 LLM Model Configuration
     */
    public record LlmModelConfig(
        @JsonProperty("name")
        @NotEmpty(message = "Model name is required")
        String name,
        
        @JsonProperty("api_key")
        @NotEmpty(message = "API key is required")
        String apiKey,
        
        @JsonProperty("endpoint")
        String endpoint,
        
        @JsonProperty("max_tokens")
        Integer maxTokens,
        
        @JsonProperty("temperature")
        Double temperature,
        
        @JsonProperty("timeout_seconds")
        Integer timeoutSeconds,
        
        @JsonProperty("additional_config")
        Map<String, Object> additionalConfig
    ) {
        public LlmModelConfig {
            if (maxTokens == null) {
                maxTokens = DEFAULT_MAX_TOKENS;
            }
            if (temperature == null) {
                temperature = DEFAULT_TEMPERATURE;
            }
            if (timeoutSeconds == null) {
                timeoutSeconds = DEFAULT_TIMEOUT_SECONDS;
            }
            if (additionalConfig == null) additionalConfig = Map.of();
        }
    }
    
    /**
     * 📄 Output Settings Configuration
     */
    public record OutputSettings(
        @JsonProperty("output_path")
        @NotEmpty(message = "Output path is required")
        String outputPath,
        
        @JsonProperty("format")
        String format,
        
        @JsonProperty("include_icons")
        Boolean includeIcons,
        
        @JsonProperty("generate_unit_tests")
        Boolean generateUnitTests,
        
        @JsonProperty("target_coverage")
        Double targetCoverage
    ) {
        public OutputSettings {
            if (format == null) format = "markdown";
            if (includeIcons == null) includeIcons = true;
            if (generateUnitTests == null) generateUnitTests = true;
            if (targetCoverage == null) {
                targetCoverage = DEFAULT_TOP_P;
            }
        }
    }
    
    /**
     * 🔍 Analysis Settings Configuration
     */
    public record AnalysisSettings(
        @JsonProperty("include_private_members")
        Boolean includePrivateMembers,
        
        @JsonProperty("max_threads")
        Integer maxThreads,
        
        @JsonProperty("supported_languages")
        List<String> supportedLanguages,
        
        @JsonProperty("exclude_patterns")
        List<String> excludePatterns
    ) {
        public AnalysisSettings {
            if (includePrivateMembers == null) includePrivateMembers = false;
            if (maxThreads == null) maxThreads = Runtime.getRuntime().availableProcessors();
            if (supportedLanguages == null) supportedLanguages = List.of("java", "python");
            if (excludePatterns == null) excludePatterns = List.of("**/test/**", "**/target/**", "**/__pycache__/**");
        }
    }
    
    public DocumentorConfig {
        if (analysisSettings == null) {
            analysisSettings = new AnalysisSettings(null, null, null, null);
        }
    }
}