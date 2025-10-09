package com.documentor.service.llm;

import com.documentor.config.DocumentorConfig;
import org.springframework.stereotype.Component;

/**
 * 🔍 LLM Model Type Detector
 * 
 * Centralized logic for detecting LLM model types and providers.
 * Eliminates duplicate detection logic across LLM components.
 */
@Component
public class LlmModelTypeDetector {

    /** 🦙 Checks if model is Ollama-based */
    public boolean isOllamaModel(DocumentorConfig.LlmModelConfig model) {
        return model.endpoint().contains("ollama") || 
               model.endpoint().contains("11434") ||
               model.name().startsWith("llama") ||
               model.name().startsWith("mistral") ||
               model.name().startsWith("codellama");
    }

    /** 🤖 Checks if model is OpenAI-compatible */
    public boolean isOpenAICompatible(DocumentorConfig.LlmModelConfig model) {
        return model.endpoint().contains("openai") ||
               model.endpoint().contains("api.openai.com") ||
               model.name().startsWith("gpt-");
    }

    /** 🔧 Gets the appropriate endpoint for the model */
    public String getModelEndpoint(DocumentorConfig.LlmModelConfig model) {
        String baseEndpoint = model.endpoint();
        
        if (isOllamaModel(model)) {
            return ensureEndsWith(baseEndpoint, "/") + "api/generate";
        } else if (isOpenAICompatible(model)) {
            return ensureEndsWith(baseEndpoint, "/") + "v1/chat/completions";
        } else {
            return baseEndpoint;
        }
    }

    private String ensureEndsWith(String str, String suffix) {
        return str.endsWith(suffix) ? str : str + suffix;
    }
}