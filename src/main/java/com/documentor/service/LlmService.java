package com.documentor.service;

import com.documentor.config.DocumentorConfig;
import com.documentor.config.ThreadLocalPropagatingExecutor;
import com.documentor.config.model.LlmModelConfig;
import com.documentor.constants.ApplicationConstants;
import com.documentor.model.CodeElement;
import com.documentor.service.llm.LlmApiClient;
import com.documentor.service.llm.LlmRequestBuilder;
import com.documentor.service.llm.LlmResponseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * LLM Integration Service - Refactored for Low Complexity
 */
public class LlmService {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        LlmService.class);

    private final DocumentorConfig config;
    private final LlmRequestBuilder requestBuilder;
    private final LlmResponseHandler responseHandler;
    private final LlmApiClient apiClient;

    public LlmService(final DocumentorConfig configParam,
                     final LlmRequestBuilder requestBuilderParam,
                     final LlmResponseHandler responseHandlerParam,
                     final LlmApiClient apiClientParam) {
        this.config = configParam;
        this.requestBuilder = requestBuilderParam;
        this.responseHandler = responseHandlerParam;
        this.apiClient = apiClientParam;

        // Create the thread-local propagating executor
        this.threadLocalExecutor = ThreadLocalPropagatingExecutor
            .createExecutor(
                getWorkerThreadCount(), // Number of threads
                "llm-worker"
            );

        // Store config in ThreadLocal when service is created
        if (configParam != null) {
            THREAD_LOCAL_CONFIG.set(configParam);
        }
    }

    /**
     * Store the config in a thread-local variable to ensure it's available
     * in async contexts
     */
    private static final ThreadLocal<DocumentorConfig> THREAD_LOCAL_CONFIG =
        new ThreadLocal<>();

    /**
     * Get the ThreadLocal config - used by ThreadLocalTaskDecorator
     */
    public static DocumentorConfig getThreadLocalConfig() {
        return THREAD_LOCAL_CONFIG.get();
    }

    /**
     * Set the ThreadLocal config - used by ThreadLocalTaskDecorator
     */
    public static void setThreadLocalConfig(final DocumentorConfig config) {
        THREAD_LOCAL_CONFIG.set(config);
    }

    /**
     * Clear the ThreadLocal config - used by ThreadLocalTaskDecorator to
     * prevent memory leaks
     */
    public static void clearThreadLocalConfig() {
        THREAD_LOCAL_CONFIG.remove();
    }

    /**
     * Generate documentation for a code element using the default LLM model
     */
    @Async("llmExecutor")
    public final CompletableFuture<String> generateDocumentation(
            final CodeElement codeElement) {
        LOGGER.info("Generating documentation for: {}",
            codeElement.getDisplayName());

        // Store config in ThreadLocal to ensure it's available in this
        // async context
        if (config != null) {
            setThreadLocalConfig(config);
        }

        // Try to get config from ThreadLocal if it's null
        DocumentorConfig effectiveConfig = config != null ? config
            : getThreadLocalConfig();

        if (effectiveConfig == null) {
            LOGGER.error("Configuration is null in "
                + "LlmService.generateDocumentation");
            return CompletableFuture.completedFuture(
                "Error: LLM configuration is null. Please check the "
                + "application configuration.");
        }

        if (effectiveConfig.llmModels().isEmpty()) {
            return CompletableFuture.completedFuture(
                "No LLM models configured for documentation generation.");
        }

        final LlmModelConfig model = effectiveConfig.llmModels().get(0);
        LOGGER.info("Using LLM model: {}", model.name());

        try {
            // Use our ThreadLocalPropagatingExecutor to ensure config is
            // available in the async thread
            return CompletableFuture.supplyAsync(() ->
                generateWithModel(codeElement, model, "documentation"),
                threadLocalExecutor
            );
        } catch (NullPointerException e) {
            LOGGER.error("NullPointerException in CompletableFuture for "
                + "documentation generation: {}", e.getMessage());
            return CompletableFuture.completedFuture(
                "Error: Unable to process documentation asynchronously. "
                + "Using synchronous fallback.");
        }
    }

    /**
     * Generate usage examples for a code element using the default LLM model
     */
    @Async("llmExecutor")
    public final CompletableFuture<String> generateUsageExamples(
            final CodeElement codeElement) {
        LOGGER.info("Generating usage examples for: {}",
            codeElement.getDisplayName());

        // Store config in ThreadLocal to ensure it's available
        if (config != null) {
            setThreadLocalConfig(config);
        }

        // Try to get config from ThreadLocal if it's null
        DocumentorConfig effectiveConfig = config != null ? config
            : getThreadLocalConfig();

        if (effectiveConfig == null) {
            LOGGER.error("Configuration is null in "
                + "LlmService.generateUsageExamples");
            return CompletableFuture.completedFuture(
                "Error: LLM configuration is null. Please check the "
                + "application configuration.");
        }

        if (effectiveConfig.llmModels().isEmpty()) {
            return CompletableFuture.completedFuture(
                "No LLM models configured for example generation.");
        }

        final LlmModelConfig model = effectiveConfig.llmModels().get(0);
        LOGGER.info("Using LLM model for examples: {}", model.name());

        try {
            // Use our ThreadLocalPropagatingExecutor to ensure config is
            // available in the async thread
            return CompletableFuture.supplyAsync(() ->
                generateWithModel(codeElement, model, "usage"),
                threadLocalExecutor
            );
        } catch (NullPointerException e) {
            LOGGER.error("NullPointerException in CompletableFuture for usage "
                + "examples: {}", e.getMessage());
            return CompletableFuture.completedFuture(
                "Error: Unable to process usage examples asynchronously. "
                + "Using synchronous fallback.");
        }
    }

    @Async("llmExecutor")
    public final CompletableFuture<String> generateUnitTests(
            final CodeElement codeElement) {
        LOGGER.info("Generating unit tests for: {}",
            codeElement.getDisplayName());

        // Store config in ThreadLocal to ensure it's available
        if (config != null) {
            setThreadLocalConfig(config);
        }

        // Try to get config from ThreadLocal if it's null
        DocumentorConfig effectiveConfig = config != null ? config
            : getThreadLocalConfig();

        if (effectiveConfig == null) {
            LOGGER.error("Configuration is null in "
                + "LlmService.generateUnitTests");
            return CompletableFuture.completedFuture(
                "Error: LLM configuration is null. Please check the "
                + "application configuration.");
        }

        if (effectiveConfig.llmModels().isEmpty()) {
            return CompletableFuture.completedFuture(
                "No LLM models configured for unit test generation.");
        }

        final LlmModelConfig model = effectiveConfig.llmModels().get(0);
        LOGGER.info("Using LLM model for unit tests: {}", model.name());

        try {
            // Use our ThreadLocalPropagatingExecutor to ensure config is
            // available in the async thread
            return CompletableFuture.supplyAsync(() ->
                generateWithModel(codeElement, model, "tests"),
                threadLocalExecutor
            );
        } catch (NullPointerException e) {
            LOGGER.error("NullPointerException in CompletableFuture for unit "
                + "tests: {}", e.getMessage());
            return CompletableFuture.completedFuture(
                "Error: Unable to process unit tests asynchronously. "
                + "Using synchronous fallback.");
        }
    }

    /**
     * Thread-local executor for CompletableFuture tasks to ensure config
     * propagation
     */
    private final java.util.concurrent.Executor threadLocalExecutor;

    /**
     * Gets the worker thread count.
     *
     * @return the worker thread count
     */
    private int getWorkerThreadCount() {
        return ApplicationConstants.DEFAULT_WORKER_THREAD_COUNT;
    }

    /**
     * Generate content with the specified model
     */
    private String generateWithModel(final CodeElement codeElement,
            final LlmModelConfig model, final String type) {
        try {
            String prompt = createPrompt(codeElement, type);
            Map<String, Object> requestBody = requestBuilder.buildRequestBody(
                model, prompt);
            String endpoint = responseHandler.getModelEndpoint(model);
            String response = apiClient.callLlmModel(model, endpoint,
                requestBody);
            return responseHandler.extractResponseContent(response, model);
        } catch (Exception e) {
            LOGGER.error("Error generating {} with model {}: {}", type,
                model.name(), e.getMessage());
            return "Error generating " + type + " with " + model.name();
        }
    }

    private String createPrompt(final CodeElement codeElement,
            final String type) {
        return switch (type) {
            case "documentation" -> requestBuilder.createDocumentationPrompt(
                codeElement);
            case "usage" -> requestBuilder.createUsageExamplePrompt(
                codeElement);
            case "tests" -> requestBuilder.createUnitTestPrompt(codeElement);
            default -> requestBuilder.createDocumentationPrompt(codeElement);
        };
    }
}
