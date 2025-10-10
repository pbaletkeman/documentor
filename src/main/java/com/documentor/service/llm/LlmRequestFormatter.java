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
        // Ollama expects model, prompt and optional streaming flag. Tests expect
        // a 'stream' boolean (default false) to be present.
        return Map.of(
            "model", model.name(),
            "prompt", prompt,
            "stream", Boolean.FALSE,
            "max_tokens", model.maxTokens()
        );
    }

    private Map<String, Object> createOpenAIRequest(LlmModelConfig model, String prompt) {
        // OpenAI-compatible payload: include temperature default (0.7) and ensure
        // numeric types are present for max_tokens. Use messages for chat models.
        return Map.of(
            "model", model.name(),
            "messages", List.of(Map.of("role", "user", "content", prompt)),
            "max_tokens", model.maxTokens(),
            "temperature", Double.valueOf(0.7),
            "timeout", model.timeoutSeconds()
        );
    }

    private Map<String, Object> createGenericRequest(final LlmModelConfig model, final String prompt) {
        // Generic providers: expose a top-level 'prompt' and common parameters
        // like temperature (default 0.5) and max_tokens so tests can assert on
        // these values directly.
        return Map.of(
            "prompt", prompt,
            "max_tokens", model.maxTokens(),
            "temperature", Double.valueOf(0.5),
            "timeout", model.timeoutSeconds()
        );
    }
}