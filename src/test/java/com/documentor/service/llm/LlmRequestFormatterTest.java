package com.documentor.service.llm;

import com.documentor.config.model.LlmModelConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class LlmRequestFormatterTest {

    private LlmRequestFormatter formatter;
    private LlmModelTypeDetector detector;

    @BeforeEach
    void setUp() {
        detector = new LlmModelTypeDetector();
        formatter = new LlmRequestFormatter(detector);
    }

    @Test
    void createOllamaRequest_containsStreamAndModel() {
        LlmModelConfig model = new LlmModelConfig("llama2", "ollama", "http://localhost:11434/api/generate", "", 1000, 30);
        Map<String, Object> body = formatter.createRequest(model, "hello");

        assertNotNull(body);
        assertEquals("llama2", body.get("model"));
        assertEquals("hello", body.get("prompt"));
        assertEquals(Boolean.FALSE, body.get("stream"));
        assertEquals(1000, body.get("max_tokens"));
    }

    @Test
    void createOpenAIRequest_includesTemperatureAndMessages() {
        LlmModelConfig model = new LlmModelConfig("gpt-4", "openai", "https://api.openai.com/v1/completions", "sk", 500, 10);
        Map<String, Object> body = formatter.createRequest(model, "what's up");

        assertNotNull(body);
        assertEquals("gpt-4", body.get("model"));
        assertTrue(body.containsKey("messages"));
        assertEquals(500, body.get("max_tokens"));
        assertEquals(0.7, body.get("temperature"));
    }

    @Test
    void createGenericRequest_exposesPromptAndTemperature() {
        LlmModelConfig model = new LlmModelConfig("claude-3", "anthropic", "https://api.anthropic.com", "key", 2000, 60);
        Map<String, Object> body = formatter.createRequest(model, "generate something");

        assertNotNull(body);
        assertEquals("generate something", body.get("prompt"));
        assertEquals(2000, body.get("max_tokens"));
        assertEquals(0.5, body.get("temperature"));
    }
}
