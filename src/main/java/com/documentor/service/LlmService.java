package com.documentor.service;

import com.documentor.config.DocumentorConfig;
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

    private static final Logger logger = LoggerFactory.getLogger(LlmService.class);

    private final DocumentorConfig config;
    private final LlmRequestBuilder requestBuilder;
    private final LlmResponseHandler responseHandler;
    private final LlmApiClient apiClient;

    public LlmService(DocumentorConfig config, LlmRequestBuilder requestBuilder, 
                     LlmResponseHandler responseHandler, LlmApiClient apiClient) {
        this.config = config;
        this.requestBuilder = requestBuilder;
        this.responseHandler = responseHandler;
        this.apiClient = apiClient;
    }

    @Async("llmExecutor")
    public CompletableFuture<String> generateDocumentation(CodeElement codeElement) {
        logger.debug("Generating documentation for: {}", codeElement.getDisplayName());

        if (config.llmModels().isEmpty()) {
            return CompletableFuture.completedFuture("No LLM models configured for documentation generation.");
        }

        DocumentorConfig.LlmModelConfig model = config.llmModels().get(0);
        return CompletableFuture.supplyAsync(() -> generateWithModel(codeElement, model, "documentation"));
    }

    @Async("llmExecutor")
    public CompletableFuture<String> generateUsageExamples(CodeElement codeElement) {
        logger.debug("Generating usage examples for: {}", codeElement.getDisplayName());

        if (config.llmModels().isEmpty()) {
            return CompletableFuture.completedFuture("No LLM models configured for usage example generation.");
        }

        DocumentorConfig.LlmModelConfig model = config.llmModels().get(0);
        return CompletableFuture.supplyAsync(() -> generateWithModel(codeElement, model, "usage"));
    }

    @Async("llmExecutor")
    public CompletableFuture<String> generateUnitTests(CodeElement codeElement) {
        logger.debug("Generating unit tests for: {}", codeElement.getDisplayName());

        if (config.llmModels().isEmpty()) {
            return CompletableFuture.completedFuture("No LLM models configured for unit test generation.");
        }

        DocumentorConfig.LlmModelConfig model = config.llmModels().get(0);
        return CompletableFuture.supplyAsync(() -> generateWithModel(codeElement, model, "tests"));
    }

    private String generateWithModel(CodeElement codeElement, DocumentorConfig.LlmModelConfig model, String type) {
        try {
            String prompt = createPrompt(codeElement, type);
            Map<String, Object> requestBody = requestBuilder.buildRequestBody(model, prompt);
            String endpoint = responseHandler.getModelEndpoint(model);
            String response = apiClient.callLlmModel(model, endpoint, requestBody);
            return responseHandler.extractResponseContent(response, model);
        } catch (Exception e) {
            logger.error("Error generating {} with model {}: {}", type, model.name(), e.getMessage());
            return "Error generating " + type + " with " + model.name();
        }
    }

    private String createPrompt(CodeElement codeElement, String type) {
        return switch (type) {
            case "documentation" -> requestBuilder.createDocumentationPrompt(codeElement);
            case "usage" -> requestBuilder.createUsageExamplePrompt(codeElement);
            case "tests" -> requestBuilder.createUnitTestPrompt(codeElement);
            default -> requestBuilder.createDocumentationPrompt(codeElement);
        };
    }
}