package com.documentor.config;

import com.documentor.config.model.LlmModelConfig;
import com.documentor.constants.ApplicationConstants;
import com.documentor.service.LlmServiceEnhanced;
import com.documentor.service.LlmServiceFixEnhanced;
import com.documentor.service.documentation.ElementDocumentationGeneratorEnhanced;
import com.documentor.service.llm.LlmApiClient;
import com.documentor.service.llm.LlmRequestBuilder;
import com.documentor.service.llm.LlmResponseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;

import java.util.List;

/**
 * Enhanced Configuration class for LLM services.
 * Provides enhanced versions of LLM services with improved error handling and ThreadLocal management.
 */
@Configuration
@Order(1) // Run after ExternalConfigLoader
public class LlmServiceConfigurationEnhanced {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(LlmServiceConfigurationEnhanced.class);

    /**
     * Creates an enhanced LlmService with improved error handling
     */
    @Bean
    @Primary
    public LlmServiceEnhanced llmServiceEnhanced(
            @Autowired(required = false) final DocumentorConfig documentorConfig,
            final LlmRequestBuilder requestBuilder,
            final LlmResponseHandler responseHandler,
            final LlmApiClient apiClient) {

        LOGGER.info("Creating enhanced LlmServiceEnhanced with DocumentorConfig: {}", documentorConfig);

        // Ensure we have a valid config
        DocumentorConfig validConfig;
        if (documentorConfig == null) {
            LOGGER.error("DocumentorConfig is null when creating LlmServiceEnhanced - using default");
            validConfig = createDefaultConfig();
        } else if (documentorConfig.llmModels() == null || documentorConfig.llmModels().isEmpty()) {
            LOGGER.warn("DocumentorConfig has no models when creating LlmServiceEnhanced - adding default");
            validConfig = addDefaultModel(documentorConfig);
        } else {
            validConfig = documentorConfig;
        }

        // Explicitly set the ThreadLocalContextHolder config
        LOGGER.info("Setting global ThreadLocalContextHolder config with {} models",
                validConfig.llmModels().size());
        ThreadLocalContextHolder.setConfig(validConfig);

        LOGGER.info("LlmServiceEnhanced created with {} models", validConfig.llmModels().size());
        return new LlmServiceEnhanced(validConfig, requestBuilder, responseHandler, apiClient);
    }

    /**
     * Primary bean for LlmServiceFixEnhanced to ensure proper configuration handling
     */
    @Bean
    @Primary
    public LlmServiceFixEnhanced llmServiceFixEnhanced() {
        LOGGER.info("Creating enhanced LlmServiceFixEnhanced for ThreadLocal management");
        return new LlmServiceFixEnhanced();
    }

    /**
     * Primary bean for ElementDocumentationGeneratorEnhanced with improved error handling
     */
    @Bean
    @Primary
    public ElementDocumentationGeneratorEnhanced elementDocumentationGeneratorEnhanced(
            final LlmServiceEnhanced llmServiceEnhanced,
            final LlmServiceFixEnhanced llmServiceFixEnhanced) {
        LOGGER.info("Creating ElementDocumentationGeneratorEnhanced with enhanced services");
        return new ElementDocumentationGeneratorEnhanced(llmServiceEnhanced, llmServiceFixEnhanced);
    }

    /**
     * Creates a minimal default configuration if none is available
     */
    private DocumentorConfig createDefaultConfig() {
        LlmModelConfig defaultModel = new LlmModelConfig(
            "default-model",
            "ollama",
            "http://localhost:11434",
            "",
            // Context size and timeout parameters
            getDefaultContextSize(),
            getDefaultTimeoutSeconds());

        return new DocumentorConfig(
            List.of(defaultModel),
            null, // Will use defaults
            null  // Will use defaults
        );
    }

    /**
     * Returns the default context size.
     *
     * @return the default context size
     */
    private int getDefaultContextSize() {
        return ApplicationConstants.DEFAULT_MAX_TOKENS;
    }

    /**
     * Returns the default timeout in seconds.
     *
     * @return the default timeout in seconds
     */
    private int getDefaultTimeoutSeconds() {
        return ApplicationConstants.DEFAULT_TIMEOUT_SECONDS;
    }

    /**
     * Adds a default model to the config if none exists
     *
     * @param config the existing configuration
     * @return a new configuration with a default model added
     */
    private DocumentorConfig addDefaultModel(final DocumentorConfig config) {
        LlmModelConfig defaultModel = new LlmModelConfig(
            "default-model",
            "ollama",
            "http://localhost:11434",
            "",
            getDefaultContextSize(),
            getDefaultTimeoutSeconds());

        return new DocumentorConfig(
            List.of(defaultModel),
            config.outputSettings(),
            config.analysisSettings()
        );
    }
}
