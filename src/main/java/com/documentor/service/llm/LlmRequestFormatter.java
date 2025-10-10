package com.documentor.service.llm;

import com.documentor.config.model.LlmModelConfig;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 📋 LLM Request Formatter - Formats requests for different LLM providers
 */
@Component
public class LlmRequestFormatter {

    private final LlmModelTypeDetector modelTypeDetector;

    public LlmRequestFormatter(LlmModelTypeDetector modelTypeDetector) {
        this.modelTypeDetector = modelTypeDetector;
    }

        /**
     * 🎯 Creates request body based on model type
     */
    public Map<String, Object> createRequest(LlmModelConfig model, String prompt) {
        if (modelTypeDetector.isOllamaModel(model)) {
            return createOllamaRequest(model, prompt);
        } else if (modelTypeDetector.isOpenAICompatible(model)) {
            return createOpenAIRequest(model, prompt);
        } else {
            return createGenericRequest(model, prompt);
        }
    }

    private Map<String, Object> createOllamaRequest(LlmModelConfig model, String prompt) {
        return Map.of("model", model.name(), "prompt", prompt);
    }

    private Map<String, Object> createOpenAIRequest(LlmModelConfig model, String prompt) {
        return Map.of(
            "model", model.name(),
            "messages", List.of(Map.of("role", "user", "content", prompt)),
            "max_tokens", model.maxTokens(),
            "timeout", model.timeoutSeconds()
        );
    }

    private Map<String, Object> createGenericRequest(final LlmModelConfig model, final String prompt) {
        return Map.of("input", prompt, "parameters", Map.of(
            "max_tokens", model.maxTokens(),
            "timeout", model.timeoutSeconds()
        ));
    }
}