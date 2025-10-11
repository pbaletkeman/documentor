package com.documentor.service.llm;

import com.documentor.config.model.LlmModelConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LlmResponseParserTest {

    private LlmResponseParser parser;
    private LlmModelTypeDetector detector;

    @BeforeEach
    void setUp() {
        detector = new LlmModelTypeDetector();
        parser = new LlmResponseParser(detector);
    }

    @Test
    void parseGenericResponse_returnsResponseField() {
        LlmModelConfig model = new LlmModelConfig("test", "other", "http://api.test", "key", 100, 10);
        String json = "{\"response\": \"ok\"}";
        String out = parser.parseResponse(json, model);
        assertEquals("ok", out);
    }

    @Test
    void parseOpenAIResponse_handlesSimpleResponseField() {
        LlmModelConfig model = new LlmModelConfig("gpt", "openai", "https://api.openai.com/v1", "key", 100, 10);
        String json = "{\"response\": \"fallback\"}";
        String out = parser.parseResponse(json, model);
        assertEquals("fallback", out);
    }
}
