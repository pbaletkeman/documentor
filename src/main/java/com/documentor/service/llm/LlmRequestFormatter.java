package com.documentor.service.llm;

import com.documentor.config.DocumentorConfig;
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

    /** 🎯 Creates formatted request based on model type */
    public Map<String, Object> createRequest(DocumentorConfig.LlmModelConfig model, String prompt) {
        if (modelTypeDetector.isOllamaModel(model)) {
            return createOllamaRequest(model, prompt);
        } else if (modelTypeDetector.isOpenAICompatible(model)) {
            return createOpenAIRequest(model, prompt);
        } else {
            return createGenericRequest(model, prompt);
        }
    }

    private Map<String, Object> createOllamaRequest(DocumentorConfig.LlmModelConfig model, String prompt) {
        return Map.of("model", model.name(), "prompt", prompt, "stream", false);
    }

    private Map<String, Object> createOpenAIRequest(DocumentorConfig.LlmModelConfig model, String prompt) {
        return Map.of("model", model.name(), 
                     "messages", List.of(Map.of("role", "user", "content", prompt)),
                     "max_tokens", model.maxTokens(), "temperature", model.temperature());
    }

    private Map<String, Object> createGenericRequest(DocumentorConfig.LlmModelConfig model, String prompt) {
        return Map.of("prompt", prompt, "max_tokens", model.maxTokens(), "temperature", model.temperature());
    }
}