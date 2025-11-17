package com.documentor.service.llm;

import com.documentor.config.model.LlmModelConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Simple tests for LlmResponseHandler component
 */
class LlmResponseHandlerTest {

    // Test constants for magic number violations
    private static final int DEFAULT_MAX_TOKENS = 1000;
    private static final int DEFAULT_TIMEOUT_SECONDS = 30;

    private LlmResponseHandler responseHandler;
    private LlmModelConfig testModel;

    @BeforeEach
    void setUp() {
        LlmModelTypeDetector modelTypeDetector =
            new LlmModelTypeDetector();
        LlmResponseParser responseParser =
            new LlmResponseParser(modelTypeDetector);
        responseHandler =
            new LlmResponseHandler(responseParser, modelTypeDetector);

        testModel = new LlmModelConfig(
            "test-model", "openai",
            "http://test.api", "api-key",
            DEFAULT_MAX_TOKENS, DEFAULT_TIMEOUT_SECONDS
        );
    }

    @Test
    void testLlmResponseHandlerConstructor() {
        // Test that the component can be instantiated
        assertNotNull(responseHandler);
    }

    @Test
    void testExtractResponseContentBasic() {
        // Test basic response content extraction
        // - should handle any format gracefully
        String jsonResponse = "{\"response\": \"Test response\"}";

        String result =
            responseHandler.extractResponseContent(jsonResponse, testModel);

        assertNotNull(result);
        // Should return either parsed content or fallback to original response
        assertTrue(result.length() > 0);
    }

    @Test
    void testExtractResponseContentWithInvalidJson() {
        // Test that invalid JSON is handled gracefully
        String invalidJson = "Not valid JSON";

        String result =
            responseHandler.extractResponseContent(invalidJson, testModel);

        assertNotNull(result);
        // Should fallback to original
        assertEquals("Not valid JSON", result);
    }

    @Test
    void testGetModelEndpointReturnsEndpoint() {
        // Test that getModelEndpoint returns a valid endpoint
        String endpoint = responseHandler.getModelEndpoint(testModel);

        assertNotNull(endpoint);
        assertTrue(endpoint.length() > 0);
    }

    @Test
    void testResponseHandlerWithDifferentModels() {
        LlmModelConfig ollamaModel = new LlmModelConfig(
            "llama2", "ollama",
            "http://localhost:11434/api/generate", "",
            DEFAULT_MAX_TOKENS, DEFAULT_TIMEOUT_SECONDS
        );

        LlmModelConfig openaiModel = new LlmModelConfig(
            "gpt-4", "openai",
            "https://api.openai.com/v1/completions", "sk-test",
            DEFAULT_MAX_TOKENS, DEFAULT_TIMEOUT_SECONDS
        );

        // Test that component can handle different model configurations
        assertNotNull(responseHandler.getModelEndpoint(ollamaModel));
        assertNotNull(responseHandler.getModelEndpoint(openaiModel));
    }
}
