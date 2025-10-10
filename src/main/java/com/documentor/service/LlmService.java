package com.documentor.service;

import com.documentor.config.DocumentorConfig;
import com.documentor.config.model.LlmModelConfig;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(LlmService.class);

    private final DocumentorConfig config;
    private final LlmRequestBuilder requestBuilder;
    private final LlmResponseHandler responseHandler;
    private final LlmApiClient apiClient;

    public LlmService(final DocumentorConfig config,
                     final LlmRequestBuilder requestBuilder,
                     final LlmResponseHandler responseHandler,
                     final LlmApiClient apiClient) {
        this.config = config;
        this.requestBuilder = requestBuilder;
        this.responseHandler = responseHandler;
        this.apiClient = apiClient;
    }

    @Async("llmExecutor")
    public CompletableFuture<String> generateDocumentation(final CodeElement codeElement) {
        LOGGER.debug("Generating documentation for: {}", codeElement.getDisplayName());

        if (config.llmModels().isEmpty()) {
            return CompletableFuture.completedFuture("No LLM models configured for documentation generation.");
        }

        LlmModelConfig model = config.llmModels().get(0);
        return CompletableFuture.supplyAsync(() -> generateWithModel(codeElement, model, "documentation"));
    }

    @Async("llmExecutor")
    public CompletableFuture<String> generateUsageExamples(final CodeElement codeElement) {
        LOGGER.debug("Generating usage examples for: {}", codeElement.getDisplayName());

        if (config.llmModels().isEmpty()) {
            return CompletableFuture.completedFuture("No LLM models configured for usage example generation.");
        }

        LlmModelConfig model = config.llmModels().get(0);
        return CompletableFuture.supplyAsync(() -> generateWithModel(codeElement, model, "usage"));
    }

    @Async("llmExecutor")
    public CompletableFuture<String> generateUnitTests(final CodeElement codeElement) {
        LOGGER.debug("Generating unit tests for: {}", codeElement.getDisplayName());

        if (config.llmModels().isEmpty()) {
            return CompletableFuture.completedFuture("No LLM models configured for unit test generation.");
        }

        LlmModelConfig model = config.llmModels().get(0);
        return CompletableFuture.supplyAsync(() -> generateWithModel(codeElement, model, "tests"));
    }

    private String generateWithModel(final CodeElement codeElement, final LlmModelConfig model, final String type) {
        try {
            String prompt = createPrompt(codeElement, type);
            Map<String, Object> requestBody = requestBuilder.buildRequestBody(model, prompt);
            String endpoint = responseHandler.getModelEndpoint(model);
            String response = apiClient.callLlmModel(model, endpoint, requestBody);
            return responseHandler.extractResponseContent(response, model);
        } catch (Exception e) {
            LOGGER.error("Error generating {} with model {}: {}", type, model.name(), e.getMessage());
            return "Error generating " + type + " with " + model.name();
        }
    }

    private String createPrompt(final CodeElement codeElement, final String type) {
        return switch (type) {
            case "documentation" -> requestBuilder.createDocumentationPrompt(codeElement);
            case "usage" -> requestBuilder.createUsageExamplePrompt(codeElement);
            case "tests" -> requestBuilder.createUnitTestPrompt(codeElement);
            default -> requestBuilder.createDocumentationPrompt(codeElement);
        };
    }
}
