package com.documentor.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.Executor;

/**
 * 🔧 Application Configuration
 *
 * Configures beans for the Documentor application including:
 * - Thread pool for parallel LLM processing
 * - WebClient for HTTP API calls
 * - Async execution configuration
 */
@Configuration
public class AppConfig implements AsyncConfigurer {

    private static final int DEFAULT_MAX_MEMORY_SIZE_MB = 10;
    private static final int BYTES_PER_MB = 1024 * 1024;
    private static final int DEFAULT_QUEUE_CAPACITY = 100;
    private static final int DEFAULT_THREAD_MULTIPLIER = 2;
    private static final int DEFAULT_TERMINATION_TIMEOUT_SECONDS = 60;

    private final DocumentorConfig documentorConfig;

    public AppConfig(final DocumentorConfig documentorConfigParam) {
        this.documentorConfig = documentorConfigParam;
    }

    /**
     * 🌐 WebClient for making HTTP requests to LLM APIs
     */
    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .codecs(configurer -> configurer.defaultCodecs()
                        .maxInMemorySize(DEFAULT_MAX_MEMORY_SIZE_MB * BYTES_PER_MB))
                .build();
    }

    /**
     * ⚡ Thread pool executor for parallel LLM processing
     */
    @Bean("llmExecutor")
    public ThreadPoolTaskExecutor llmExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(documentorConfig.analysisSettings().maxThreads());
        executor.setMaxPoolSize(documentorConfig.analysisSettings().maxThreads() 
                * DEFAULT_THREAD_MULTIPLIER);
        executor.setQueueCapacity(DEFAULT_QUEUE_CAPACITY);
        executor.setThreadNamePrefix("LLM-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(DEFAULT_TERMINATION_TIMEOUT_SECONDS);
        executor.initialize();
        return executor;
    }

    /**
     * 🤖 LLM Service with proper dependency injection
     */
    @Bean
    public com.documentor.service.LlmService llmService(
            DocumentorConfig documentorConfig,
            com.documentor.service.llm.LlmRequestBuilder requestBuilder,
            com.documentor.service.llm.LlmResponseHandler responseHandler,
            com.documentor.service.llm.LlmApiClient apiClient) {
        return new com.documentor.service.LlmService(documentorConfig, requestBuilder, responseHandler, apiClient);
    }

    /**
     * � Documentation Service with proper dependency injection
     */
    @Bean
    public com.documentor.service.DocumentationService documentationService(
            com.documentor.service.documentation.MainDocumentationGenerator mainDocGenerator,
            com.documentor.service.documentation.ElementDocumentationGenerator elementDocGenerator,
            com.documentor.service.documentation.UnitTestDocumentationGenerator testDocGenerator,
            com.documentor.service.MermaidDiagramService mermaidDiagramService,
            DocumentorConfig documentorConfig) {
        return new com.documentor.service.DocumentationService(
                mainDocGenerator, elementDocGenerator, testDocGenerator, 
                mermaidDiagramService, documentorConfig);
    }

    /**
     * �🔄 Default async executor configuration
     */
    @Override
    public Executor getAsyncExecutor() {
        return llmExecutor();
    }
}