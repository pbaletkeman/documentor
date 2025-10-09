package com.documentor.service.llm;

import com.documentor.config.DocumentorConfig;
import org.springframework.stereotype.Component;

/**
 * 🔍 LLM Response Handler - Refactored for Low Complexity
 * 
 * Simplified response handling by delegating to specialized components.
 * Reduces complexity by removing duplicate logic and centralizing response parsing.
 */
@Component
public class LlmResponseHandler {

    private final LlmResponseParser responseParser;
    private final LlmModelTypeDetector modelTypeDetector;

    public LlmResponseHandler(LlmResponseParser responseParser, LlmModelTypeDetector modelTypeDetector) {
        this.responseParser = responseParser;
        this.modelTypeDetector = modelTypeDetector;
    }

    /**
     * 📤 Extracts content from LLM response based on model type
     */
    public String extractResponseContent(String response, DocumentorConfig.LlmModelConfig model) {
        return responseParser.parseResponse(response, model);
    }

    /**
     * 🎯 Gets the appropriate endpoint for the model
     */
    public String getModelEndpoint(DocumentorConfig.LlmModelConfig model) {
        return modelTypeDetector.getModelEndpoint(model);
    }
}