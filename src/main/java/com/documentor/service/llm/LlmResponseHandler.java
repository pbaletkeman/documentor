package com.documentor.service.llm;

import com.documentor.config.model.LlmModelConfig;
import org.springframework.stereotype.Component;

/**
 * ðŸ” LLM Response Handler - Refactored for Low Complexity
 *
 * Simplified response handling by delegating to specialized components.
 * Reduces complexity by removing duplicate logic and centralizing response parsing.
 */
@Component
public class LlmResponseHandler {

    private final LlmResponseParser responseParser;
    private final LlmModelTypeDetector modelTypeDetector;

    public LlmResponseHandler(final LlmResponseParser responseParserParam, final LlmModelTypeDetector modelTypeDetectorParam) {
        this.responseParser = responseParserParam;
        this.modelTypeDetector = modelTypeDetectorParam;
    }

    /**
     * ðŸ"¤ Extracts content from LLM response based on model type
     */
    public String extractResponseContent(final String response, final LlmModelConfig model) {
        return responseParser.parseResponse(response, model);
    }

    /**
     * ðŸŽ¯ Gets the appropriate endpoint for the model
     */
    public String getModelEndpoint(final LlmModelConfig model) {
        return modelTypeDetector.getModelEndpoint(model);
    }
}
