package com.documentor.config;

import com.documentor.service.llm.LlmApiClient;
import com.documentor.service.llm.LlmModelTypeDetector;
import com.documentor.service.llm.LlmPromptTemplates;
import com.documentor.service.llm.LlmRequestBuilder;
import com.documentor.service.llm.LlmRequestFormatter;
import com.documentor.service.llm.LlmResponseHandler;
import com.documentor.service.llm.LlmResponseParser;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Test Configuration for Integration Tests
 *
 * This configuration provides mock beans for integration tests to ensure
 * the Spring context can be properly loaded without external dependencies.
 */
@TestConfiguration
@Import(AppConfig.class)
public class TestConfig {

    /**
     * Mock External Config Loader for tests
     */
    @Bean
    public ExternalConfigLoader externalConfigLoader() {
        return new ExternalConfigLoader();
    }

    /**
     * Mock LLM Model Type Detector for tests
     */
    @Bean
    public LlmModelTypeDetector llmModelTypeDetector() {
        return new LlmModelTypeDetector();
    }

    /**
     * Mock LLM Response Parser for tests
     */
    @Bean
    public LlmResponseParser llmResponseParser(final LlmModelTypeDetector modelTypeDetector) {
        return new LlmResponseParser(modelTypeDetector);
    }

    /**
     * Mock LLM Request Formatter for tests
     */
    @Bean
    public LlmRequestFormatter llmRequestFormatter(final LlmModelTypeDetector modelTypeDetector) {
        return new LlmRequestFormatter(modelTypeDetector);
    }

    /**
     * Mock LLM API Client for tests
     */
    @Bean
    public LlmApiClient llmApiClient(final WebClient webClient,
                                     final LlmModelTypeDetector modelTypeDetector) {
        return new LlmApiClient(webClient, modelTypeDetector);
    }

    /**
     * Mock LLM Request Builder for tests
     */
    @Bean
    public LlmRequestBuilder llmRequestBuilder(final LlmPromptTemplates templates,
                                      final LlmRequestFormatter formatter) {
        return new LlmRequestBuilder(templates, formatter);
    }

    /**
     * Mock LLM Response Handler for tests
     */
    @Bean
    public LlmResponseHandler llmResponseHandler(final LlmResponseParser parser, final LlmModelTypeDetector detector) {
        return new LlmResponseHandler(parser, detector);
    }

    /**
     * Mock LLM Prompt Templates for tests
     */
    @Bean
    public LlmPromptTemplates llmPromptTemplates() {
        return new LlmPromptTemplates();
    }
}
