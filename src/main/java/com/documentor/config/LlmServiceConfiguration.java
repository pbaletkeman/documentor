package com.documentor.config;

import com.documentor.config.model.LlmModelConfig;
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
    public DocumentorConfig documentorConfig(DocumentorConfig existingConfig) {
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
            @Autowired(required = false) DocumentorConfig documentorConfig,
            LlmRequestBuilder requestBuilder,
            LlmResponseHandler responseHandler,
            LlmApiClient apiClient) {

        LOGGER.info("Creating LlmService with DocumentorConfig: {}", documentorConfig);

        // Ensure we have a valid config
        if (documentorConfig == null) {
            LOGGER.error("DocumentorConfig is null when creating LlmService - using default");
            documentorConfig = createDefaultConfig();
        } else if (documentorConfig.llmModels() == null || documentorConfig.llmModels().isEmpty()) {
            LOGGER.warn("DocumentorConfig has no models when creating LlmService - adding default");
            documentorConfig = addDefaultModel(documentorConfig);
        }
        
        // Explicitly set the ThreadLocal config before creating the LlmService
        // This ensures any @Async method will have access to the configuration
        LOGGER.info("Setting global ThreadLocal config with {} models", documentorConfig.llmModels().size());
        LlmService.setThreadLocalConfig(documentorConfig);

        LOGGER.info("LlmService created with {} models", documentorConfig.llmModels().size());
        return new LlmService(documentorConfig, requestBuilder, responseHandler, apiClient);
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
            4096,
            30);

        return new DocumentorConfig(
            List.of(defaultModel),
            null, // Will use defaults
            null  // Will use defaults
        );
    }

    /**
     * Adds a default model to the config if none exists
     */
    private DocumentorConfig addDefaultModel(DocumentorConfig config) {
        LlmModelConfig defaultModel = new LlmModelConfig(
            "default-model",
            "ollama",
            "http://localhost:11434",
            "",
            4096,
            30);

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
    public ElementDocumentationGenerator elementDocumentationGenerator(LlmService llmService) {
        LOGGER.info("Creating ElementDocumentationGenerator with our configured LlmService");
        return new ElementDocumentationGenerator(llmService);
    }
}
