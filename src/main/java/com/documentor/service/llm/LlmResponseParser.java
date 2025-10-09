package com.documentor.service.llm;

import com.documentor.config.DocumentorConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

/** 📤 LLM Response Parser - Centralized response parsing for different LLM providers */
@Component
public class LlmResponseParser {

    private final ObjectMapper objectMapper;
    private final LlmModelTypeDetector modelTypeDetector;

    public LlmResponseParser(LlmModelTypeDetector modelTypeDetector) {
        this.objectMapper = new ObjectMapper();
        this.modelTypeDetector = modelTypeDetector;
    }

    /** 📤 Main response parsing method that delegates to specific parsers */
    public String parseResponse(String response, DocumentorConfig.LlmModelConfig model) {
        try {
            if (modelTypeDetector.isOllamaModel(model)) return parseOllamaResponse(response);
            if (modelTypeDetector.isOpenAICompatible(model)) return parseOpenAIResponse(response);
            return parseGenericResponse(response);
        } catch (Exception e) {
            return response;
        }
    }

    /** 🦙 Extracts response from Ollama format */
    public String parseOllamaResponse(String response) {
        return parseGenericResponse(response, "response");
    }

    /** 🤖 Extracts response from OpenAI format */
    public String parseOpenAIResponse(String response) {
        try {
            JsonNode json = objectMapper.readTree(response);
            if (json.has("choices") && json.get("choices").isArray()) {
                JsonNode choice = json.get("choices").get(0);
                if (choice.has("message") && choice.get("message").has("content")) {
                    return choice.get("message").get("content").asText();
                }
                if (choice.has("text")) return choice.get("text").asText();
            }
            return json.asText();
        } catch (Exception e) {
            return response;
        }
    }

    /** 🔧 Extracts response from generic format */
    public String parseGenericResponse(String response) {
        return parseGenericResponse(response, "text", "content", "response", "output", "result");
    }

    private String parseGenericResponse(String response, String... fields) {
        try {
            JsonNode json = objectMapper.readTree(response);
            for (String field : fields) {
                if (json.has(field)) return json.get(field).asText();
            }
            return json.asText();
        } catch (Exception e) {
            return response;
        }
    }
}