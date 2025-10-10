package com.documentor.config.model;

import com.documentor.constants.ApplicationConstants;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * ðŸ”§ LLM Model Configuration Record
 *
 * Extracted from nested DocumentorConfig to reduce complexity.
 * Contains all LLM-specific configuration settings.
 */
public record LlmModelConfig(
    @JsonProperty("name") String name,
    @JsonProperty("provider") String provider,
    @JsonProperty("baseUrl") String baseUrl,
    @JsonProperty("apiKey") String apiKey,
    @JsonProperty("maxTokens") Integer maxTokens,
    @JsonProperty("timeoutSeconds") Integer timeoutSeconds
) {

    /**
     * Creates a copy with default values applied for null fields
     *
     * @return LlmModelConfig with defaults applied
     */
    public LlmModelConfig withDefaults() {
        return new LlmModelConfig(
            name != null ? name : "default",
            provider != null ? provider : "ollama",
            baseUrl != null ? baseUrl : "http://localhost:" + ApplicationConstants.DEFAULT_OLLAMA_PORT,
            apiKey,
            maxTokens != null ? maxTokens : ApplicationConstants.DEFAULT_MAX_TOKENS,
            timeoutSeconds != null ? timeoutSeconds : ApplicationConstants.DEFAULT_TIMEOUT_SECONDS
        );
    }

    /**
     * Applies defaults for null fields (in-place style for legacy compatibility)
     * Note: Records are immutable, so this returns a new instance with defaults
     *
     * @return new LlmModelConfig with defaults applied
     */
    public LlmModelConfig applyDefaults() {
        return withDefaults();
    }

    /**
     * Validates the configuration
     *
     * @throws IllegalArgumentException if invalid
     */
    public void validate() {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("LLM model name cannot be null or empty");
        }
        if (provider == null || provider.trim().isEmpty()) {
            throw new IllegalArgumentException("LLM provider cannot be null or empty");
        }
    }
}
