package com.documentor.service.llm;

import com.documentor.config.model.LlmModelConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LlmResponseParserTest {

    private static final int TEST_MAX_TOKENS = 100;
    private static final int TEST_TIMEOUT_SECONDS = 10;

    private LlmResponseParser parser;
    private LlmModelTypeDetector detector;

    @BeforeEach
    void setUp() {
        detector = new LlmModelTypeDetector();
        parser = new LlmResponseParser(detector);
    }

    @Test
    void parseGenericResponseReturnsResponseField() {
        LlmModelConfig model = new LlmModelConfig("test", "other",
        "http://api.test", "key", TEST_MAX_TOKENS,
        TEST_TIMEOUT_SECONDS);
        String json = "{\"response\": \"ok\"}";
        String out = parser.parseResponse(json, model);
        assertEquals("ok", out);
    }

    @Test
    void parseOpenAIResponseHandlesSimpleResponseField() {
        LlmModelConfig model = new LlmModelConfig("gpt", "openai",
            "https://api.openai.com/v1", "key", TEST_MAX_TOKENS,
            TEST_TIMEOUT_SECONDS);
        String json = "{\"response\": \"fallback\"}";
        String out = parser.parseResponse(json, model);
        assertEquals("fallback", out);
    }
}
