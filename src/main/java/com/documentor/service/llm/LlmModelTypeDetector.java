package com.documentor.service.llm;

import com.documentor.constants.ApplicationConstants;
import com.documentor.config.model.LlmModelConfig;
import org.springframework.stereotype.Component;

/**
 * 🔍 LLM Model Type Detector
 *
 * Centralized logic for detecting LLM model types and providers.
 * Eliminates duplicate detection logic across LLM components.
 */
@Component
public class LlmModelTypeDetector {

    /**
     * 🔍 Checks if the model is Ollama-based
     */
    public boolean isOllamaModel(final LlmModelConfig model) {
        return model.baseUrl().contains("ollama")
               || model.baseUrl().contains(
                       ApplicationConstants.DEFAULT_OLLAMA_PORT);
    }

    /**
     * 🔍 Checks if the model is OpenAI-compatible
     */
    public boolean isOpenAICompatible(final LlmModelConfig model) {
        return model.baseUrl().contains("openai")
               || model.provider().equalsIgnoreCase("openai");
    }

    /**
     * 🔍 Gets the appropriate endpoint for the model
     */
    public String getModelEndpoint(final LlmModelConfig model) {
        return model.baseUrl() + (model.baseUrl().endsWith("/") ? "" : "/")
                + "api/generate";
    }
}
