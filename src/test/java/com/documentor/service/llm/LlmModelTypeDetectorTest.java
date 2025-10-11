package com.documentor.service.llm;

import com.documentor.config.model.LlmModelConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LlmModelTypeDetectorTest {

    private static final int MAX_TOKENS_2000 = 2000;
    private static final int TIMEOUT_30 = 30;

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
    void isOllamaModel(final String baseUrl, final boolean expected) {
        LlmModelConfig config = new LlmModelConfig("test", "provider", baseUrl, "apiKey", MAX_TOKENS_2000, TIMEOUT_30);
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
    void isOpenAICompatible(final String baseUrl, final String provider, final boolean expected) {
        LlmModelConfig config = new LlmModelConfig("test", provider, baseUrl, "apiKey", MAX_TOKENS_2000, TIMEOUT_30);
        assertEquals(expected, detector.isOpenAICompatible(config));
    }

    @Test
    @DisplayName("Should format model endpoint correctly with trailing slash")
    void getModelEndpointWithTrailingSlash() {
        LlmModelConfig config = new LlmModelConfig("test", "provider", "http://localhost:11434/", "apiKey", MAX_TOKENS_2000, TIMEOUT_30);
        assertEquals("http://localhost:11434/api/generate", detector.getModelEndpoint(config));
    }

    @Test
    @DisplayName("Should format model endpoint correctly without trailing slash")
    void getModelEndpointWithoutTrailingSlash() {
        LlmModelConfig config = new LlmModelConfig("test", "provider", "http://localhost:11434", "apiKey", MAX_TOKENS_2000, TIMEOUT_30);
        assertEquals("http://localhost:11434/api/generate", detector.getModelEndpoint(config));
    }
}
