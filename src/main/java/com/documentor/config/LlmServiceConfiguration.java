package com.documentor.config;

import com.documentor.config.model.LlmModelConfig;
import com.documentor.constants.ApplicationConstants;
import com.documentor.service.LlmService;
import com.documentor.service.documentation.ElementDocumentationGenerator;
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
 * Configuration class for LLM services.
 * Ensures proper DocumentorConfig injection into LlmService.
 */
@Configuration
@Order(1) // Run after ExternalConfigLoader
public class LlmServiceConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(LlmServiceConfiguration.class);

    /**
     * Creates a DocumentorConfig bean post-processor that ensures it's not null and has valid models.
     *
     * @return A modified DocumentorConfig if needed
     */
    @Bean
    @Primary
    public DocumentorConfig documentorConfig(final DocumentorConfig existingConfig) {
        LOGGER.info("Checking DocumentorConfig: {}", existingConfig);

        // Check if the config is valid
        if (existingConfig == null) {
            LOGGER.error("DocumentorConfig is null - creating default config");
            return createDefaultConfig();
        } else if (existingConfig.llmModels() == null || existingConfig.llmModels().isEmpty()) {
            LOGGER.warn("No LLM models in config - adding default model");
            return addDefaultModel(existingConfig);
        }

        LOGGER.info("Using DocumentorConfig with {} models",
                   existingConfig.llmModels().size());

        return existingConfig;
    }

    /**
     * Primary bean for LlmService to ensure proper configuration injection
     */
    @Bean
    @Primary
    public LlmService llmService(
            @Autowired(required = false) final DocumentorConfig documentorConfig,
            final LlmRequestBuilder requestBuilder,
            final LlmResponseHandler responseHandler,
            final LlmApiClient apiClient) {

        LOGGER.info("Creating LlmService with DocumentorConfig: {}", documentorConfig);

        // Ensure we have a valid config
        DocumentorConfig validConfig;
        if (documentorConfig == null) {
            LOGGER.error("DocumentorConfig is null when creating LlmService - using default");
            validConfig = createDefaultConfig();
        } else if (documentorConfig.llmModels() == null || documentorConfig.llmModels().isEmpty()) {
            LOGGER.warn("DocumentorConfig has no models when creating LlmService - adding default");
            validConfig = addDefaultModel(documentorConfig);
        } else {
            validConfig = documentorConfig;
        }

        // Explicitly set the ThreadLocal config before creating the LlmService
        // This ensures any @Async method will have access to the configuration
        LOGGER.info("Setting global ThreadLocal config with {} models", validConfig.llmModels().size());
        LlmService.setThreadLocalConfig(validConfig);

        LOGGER.info("LlmService created with {} models", validConfig.llmModels().size());
        return new LlmService(validConfig, requestBuilder, responseHandler, apiClient);
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

    /**
     * Primary bean for ElementDocumentationGenerator to ensure it uses our configured LlmService
     */
    @Bean
    @Primary
    public ElementDocumentationGenerator elementDocumentationGenerator(final LlmService llmService) {
        LOGGER.info("Creating ElementDocumentationGenerator with our configured LlmService");
        return new ElementDocumentationGenerator(llmService);
    }
}
