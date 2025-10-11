package com.documentor.service.llm;

import com.documentor.config.model.LlmModelConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LlmRequestFormatterTest {

    private static final int MAX_TOKENS_LARGE = 2000;
    private static final int TIMEOUT_SECONDS = 60;
    private static final double TEMPERATURE_HALF = 0.5;
    private static final int MAX_TOKENS_STANDARD = 1000;
    private static final int TIMEOUT_SECONDS_30 = 30;
    private static final int MAX_TOKENS_MEDIUM = 500;
    private static final int TIMEOUT_SECONDS_10 = 10;
    private static final double TEMPERATURE_POINT_SEVEN = 0.7;

    private LlmRequestFormatter formatter;
    private LlmModelTypeDetector detector;

    @BeforeEach
    void setUp() {
        detector = new LlmModelTypeDetector();
        formatter = new LlmRequestFormatter(detector);
    }

    @Test
    void createOllamaRequestContainsStreamAndModel() {
        LlmModelConfig model = new LlmModelConfig("llama2", "ollama", "http://localhost:11434/api/generate", "", MAX_TOKENS_STANDARD, TIMEOUT_SECONDS_30);
        Map<String, Object> body = formatter.createRequest(model, "hello");

        assertNotNull(body);
        assertEquals("llama2", body.get("model"));
        assertEquals("hello", body.get("prompt"));
        assertEquals(Boolean.FALSE, body.get("stream"));
        assertEquals(MAX_TOKENS_STANDARD, body.get("max_tokens"));
    }

    @Test
    void createOpenAIRequestIncludesTemperatureAndMessages() {
        LlmModelConfig model = new LlmModelConfig("gpt-4", "openai", "https://api.openai.com/v1/completions", "sk", MAX_TOKENS_MEDIUM, TIMEOUT_SECONDS_10);
        Map<String, Object> body = formatter.createRequest(model, "what's up");

        assertNotNull(body);
        assertEquals("gpt-4", body.get("model"));
        assertTrue(body.containsKey("messages"));
        assertEquals(MAX_TOKENS_MEDIUM, body.get("max_tokens"));
        assertEquals(TEMPERATURE_POINT_SEVEN, body.get("temperature"));
    }

    @Test
    void createGenericRequestExposesPromptAndTemperature() {
        LlmModelConfig model = new LlmModelConfig("claude-3", "anthropic", "https://api.anthropic.com", "key", MAX_TOKENS_LARGE, TIMEOUT_SECONDS);
        Map<String, Object> body = formatter.createRequest(model, "generate something");

        assertNotNull(body);
        assertEquals("generate something", body.get("prompt"));
        assertEquals(MAX_TOKENS_LARGE, body.get("max_tokens"));
        assertEquals(TEMPERATURE_HALF, body.get("temperature"));
    }
}
