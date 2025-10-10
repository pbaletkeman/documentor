package com.documentor.service.llm;

import com.documentor.config.model.LlmModelConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class LlmModelTypeDetectorTest {

    private final LlmModelTypeDetector detector = new LlmModelTypeDetector();

    @ParameterizedTest
    @CsvSource({
        "http://localhost:11434/api, true",
        "https://ollama.example.com, true",
        "http://localhost:11434, true",
        "http://api.openai.com, false",
        "http://localhost:8080, false"
    })
    @DisplayName("Should correctly detect Ollama models")
    void isOllamaModel(String baseUrl, boolean expected) {
        LlmModelConfig config = new LlmModelConfig("test", "provider", baseUrl, "apiKey", 2000, 30);
        assertEquals(expected, detector.isOllamaModel(config));
    }

    @ParameterizedTest
    @CsvSource({
        "http://api.openai.com, openai, true",
        "https://api.openai.com/v1, other, true",
        "http://localhost:8080, openai, true",
        "http://localhost:11434, ollama, false"
    })
    @DisplayName("Should correctly detect OpenAI compatible models")
    void isOpenAICompatible(String baseUrl, String provider, boolean expected) {
        LlmModelConfig config = new LlmModelConfig("test", provider, baseUrl, "apiKey", 2000, 30);
        assertEquals(expected, detector.isOpenAICompatible(config));
    }

    @Test
    @DisplayName("Should format model endpoint correctly with trailing slash")
    void getModelEndpointWithTrailingSlash() {
        LlmModelConfig config = new LlmModelConfig("test", "provider", "http://localhost:11434/", "apiKey", 2000, 30);
        assertEquals("http://localhost:11434/api/generate", detector.getModelEndpoint(config));
    }

    @Test
    @DisplayName("Should format model endpoint correctly without trailing slash")
    void getModelEndpointWithoutTrailingSlash() {
        LlmModelConfig config = new LlmModelConfig("test", "provider", "http://localhost:11434", "apiKey", 2000, 30);
        assertEquals("http://localhost:11434/api/generate", detector.getModelEndpoint(config));
    }
}
