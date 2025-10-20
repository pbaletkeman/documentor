package com.documentor.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.reactive.function.client.WebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;
import com.documentor.service.LlmServiceEnhanced;

/**
 * 🔍 Enhanced Application Configuration with improved error handling
 *
 * Configures beans for the Documentor application including:
 * - Thread pool for parallel LLM processing with enhanced error handling
 * - WebClient for HTTP API calls
 * - Async execution configuration
 * - Enhanced LlmService with robust null checks and fallbacks
 */
@Configuration
public class AppConfigEnhanced implements AsyncConfigurer {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppConfigEnhanced.class);
    private static final int DEFAULT_MAX_MEMORY_SIZE_MB = 10;
    private static final int BYTES_PER_MB = 1024 * 1024;
    private static final int DEFAULT_QUEUE_CAPACITY = 100;
    private static final int DEFAULT_THREAD_MULTIPLIER = 2;
    private static final int DEFAULT_TERMINATION_TIMEOUT_SECONDS = 60;

    private final DocumentorConfig documentorConfig;

    public AppConfigEnhanced(final DocumentorConfig documentorConfigParam) {
        this.documentorConfig = documentorConfigParam;
        LOGGER.info("AppConfigEnhanced initialized with DocumentorConfig: {}",
            documentorConfigParam != null ? "valid" : "null");
    }

    /**
     * 🔍 WebClient for making HTTP requests to LLM APIs with enhanced error handling
     * Marked as @Primary to resolve the bean conflict
     */
    @Bean
    @org.springframework.context.annotation.Primary
    public WebClient webClientEnhanced() {
        LOGGER.info("Creating enhanced WebClient (PRIMARY)");
        return WebClient.builder()
                .codecs(configurer -> configurer.defaultCodecs()
                        .maxInMemorySize(DEFAULT_MAX_MEMORY_SIZE_MB * BYTES_PER_MB))
                .build();
    }

    /**
     * ⚡ Thread pool executor for parallel LLM processing with enhanced error handling
     * Marked with a specific name and as @Primary to resolve bean conflicts
     */
    @Bean("llmExecutor")
    @org.springframework.context.annotation.Primary
    public ThreadPoolTaskExecutor llmExecutorEnhanced() {
        int corePoolSize = documentorConfig != null &&
                           documentorConfig.analysisSettings() != null ?
                           documentorConfig.analysisSettings().maxThreads() : 5;

        LOGGER.info("Creating enhanced LLM executor with core pool size: {}", corePoolSize);

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(corePoolSize * DEFAULT_THREAD_MULTIPLIER);
        executor.setQueueCapacity(DEFAULT_QUEUE_CAPACITY);
        executor.setThreadNamePrefix("LLM-Enhanced-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(DEFAULT_TERMINATION_TIMEOUT_SECONDS);

        // Use enhanced ThreadLocalTaskDecorator that uses ThreadLocalContextHolder
        executor.setTaskDecorator(task -> {
            DocumentorConfig capturedConfig = ThreadLocalContextHolder.getConfig();
            boolean wasExplicitlySet = ThreadLocalContextHolder.isConfigExplicitlySet();

            return () -> {
                try {
                    // Set ThreadLocal in the new thread if available
                    if (capturedConfig != null) {
                        ThreadLocalContextHolder.setConfig(capturedConfig);
                        LOGGER.debug("ThreadLocal config set in task thread (explicitly set: {})",
                            wasExplicitlySet);
                    }

                    // Execute the original task
                    task.run();
                } catch (Exception e) {
                    LOGGER.error("Error in async task execution: {}", e.getMessage(), e);
                } finally {
                    // Clean up ThreadLocal
                    ThreadLocalContextHolder.clearConfig();
                    LOGGER.debug("ThreadLocal config cleared in task thread");
                }
            };
        });

        executor.initialize();
        return executor;
    }

    /**
     * 🔍 Enhanced LLM Service with proper dependency injection and improved error handling
     * With a custom name that matches the original service for compatibility
     * Removed @Primary to avoid conflicts with LlmServiceConfigurationEnhanced
     */
    @Bean("llmService")
    public com.documentor.service.LlmServiceEnhanced llmServiceEnhanced(
            final DocumentorConfig documentorConfigParam,
            final com.documentor.service.llm.LlmRequestBuilder requestBuilder,
            final com.documentor.service.llm.LlmResponseHandler responseHandler,
            final com.documentor.service.llm.LlmApiClient apiClient) {
        LOGGER.info("Creating enhanced LlmService (no longer PRIMARY)");
        return new com.documentor.service.LlmServiceEnhanced(documentorConfigParam, requestBuilder, responseHandler, apiClient);
    }

    /**
     * 🔍 Bean specifically for component scanning to resolve autowiring of LlmServiceEnhanced
     * Removed @Primary to avoid conflicts with LlmServiceConfigurationEnhanced
     */
    @Bean("llmServiceEnhancedBean")
    public com.documentor.service.LlmServiceEnhanced llmServiceEnhancedForAutowiring(
            final com.documentor.service.llm.LlmRequestBuilder requestBuilder,
            final com.documentor.service.llm.LlmResponseHandler responseHandler,
            final com.documentor.service.llm.LlmApiClient apiClient) {
        LOGGER.info("Creating standalone LlmServiceEnhanced bean for autowiring");
        // Create a new instance directly instead of calling the other method to avoid conflicts
        return new com.documentor.service.LlmServiceEnhanced(
            documentorConfig, requestBuilder, responseHandler, apiClient);
    }

    /**
     * 🔍 Enhanced LLM Service Fix with improved error handling
     * Intentionally not marked as @Primary to avoid conflicts with LlmServiceConfigurationEnhanced
     */
    @Bean("llmServiceFixEnhancedSecondary")
    public com.documentor.service.LlmServiceFixEnhanced llmServiceFixEnhanced() {
        LOGGER.info("Creating enhanced LlmServiceFix (secondary)");
        return new com.documentor.service.LlmServiceFixEnhanced();
    }

    /**
     * 🧪 Enhanced Unit Test Documentation Generator with improved error handling
     */
    @Bean
    public com.documentor.service.documentation.UnitTestDocumentationGeneratorEnhanced unitTestDocumentationGeneratorEnhanced(
            final DocumentorConfig documentorConfig,
            final com.documentor.service.LlmServiceFixEnhanced llmServiceFixEnhanced,
            final com.documentor.service.llm.LlmRequestBuilder requestBuilder,
            final com.documentor.service.llm.LlmResponseHandler responseHandler,
            final com.documentor.service.llm.LlmApiClient apiClient) {
        LOGGER.info("Creating enhanced UnitTestDocumentationGenerator with direct LlmServiceEnhanced instance");

        // Create a new instance of LlmServiceEnhanced directly instead of injecting
        LlmServiceEnhanced serviceEnhanced = new LlmServiceEnhanced(
            documentorConfig, requestBuilder, responseHandler, apiClient);

        return new com.documentor.service.documentation.UnitTestDocumentationGeneratorEnhanced(
            serviceEnhanced, documentorConfig, llmServiceFixEnhanced);
    }

    /**
     * � Default async executor configuration using the enhanced executor
     */
    @Override
    public Executor getAsyncExecutor() {
        return llmExecutorEnhanced();
    }
}
