package com.documentor.service.llm;

import com.documentor.constants.ApplicationConstants;
import com.documentor.config.model.LlmModelConfig;
import org.springframework.stereotype.Component;

/**
 * ðŸ” LLM Model Type Detector
 *
 * Centralized logic for detecting LLM model types and providers.
 * Eliminates duplicate detection logic across LLM components.
 */
@Component
public class LlmModelTypeDetector {

        /**
     * ðŸ” Checks if the model is Ollama-based
     */
    public boolean isOllamaModel(LlmModelConfig model) {
        return model.baseUrl().contains("ollama") ||
               model.baseUrl().contains(ApplicationConstants.DEFAULT_OLLAMA_PORT);
    }

    /**
     * ðŸ” Checks if the model is OpenAI-compatible
     */
    public boolean isOpenAICompatible(LlmModelConfig model) {
        return model.baseUrl().contains("openai") ||
               model.provider().equalsIgnoreCase("openai");
    }

    /**
     * ðŸŒ Gets the appropriate endpoint for the model
     */
    public String getModelEndpoint(LlmModelConfig model) {
        return model.baseUrl() + (model.baseUrl().endsWith("/") ? "" : "/") + "api/generate";
    }
}
