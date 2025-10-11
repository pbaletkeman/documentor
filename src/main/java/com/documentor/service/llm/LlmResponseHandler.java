package com.documentor.service.llm;

import com.documentor.config.model.LlmModelConfig;
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

    public LlmResponseHandler(final LlmResponseParser responseParserParam,
            final LlmModelTypeDetector modelTypeDetectorParam) {
        this.responseParser = responseParserParam;
        this.modelTypeDetector = modelTypeDetectorParam;
    }

    /**
     * 🔍 Extracts content from LLM response based on model type
     */
    public String extractResponseContent(final String response, final LlmModelConfig model) {
        return responseParser.parseResponse(response, model);
    }

    /**
     * 🔍 Gets the appropriate endpoint for the model
     */
    public String getModelEndpoint(final LlmModelConfig model) {
        return modelTypeDetector.getModelEndpoint(model);
    }
}

