package com.documentor.service.llm;

import com.documentor.config.model.LlmModelConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 🧪 Simple tests for LlmResponseHandler component
 */
class LlmResponseHandlerTest {

    private LlmResponseHandler responseHandler;
    private LlmModelConfig testModel;

    @BeforeEach
    void setUp() {
        LlmModelTypeDetector modelTypeDetector = new LlmModelTypeDetector();
        LlmResponseParser responseParser = new LlmResponseParser(modelTypeDetector);
        responseHandler = new LlmResponseHandler(responseParser, modelTypeDetector);
        
        testModel = new LlmModelConfig(
            "test-model", "openai", "http://test.api", "api-key", 1000, 30
        );
    }

    @Test
    void testLlmResponseHandlerConstructor() {
        // Test that the component can be instantiated
        assertNotNull(responseHandler);
    }

    @Test
    void testExtractResponseContentBasic() {
        // Test basic response content extraction - should handle any format gracefully
        String jsonResponse = "{\"response\": \"Test response\"}";
        
        String result = responseHandler.extractResponseContent(jsonResponse, testModel);
        
        assertNotNull(result);
        // Should return either parsed content or fallback to original response
        assertTrue(result.length() > 0);
    }

    @Test
    void testExtractResponseContentWithInvalidJson() {
        // Test that invalid JSON is handled gracefully
        String invalidJson = "Not valid JSON";
        
        String result = responseHandler.extractResponseContent(invalidJson, testModel);
        
        assertNotNull(result);
        assertEquals("Not valid JSON", result); // Should fallback to original
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
            "llama2", "ollama", "http://localhost:11434/api/generate", "", 1000, 30
        );
        
        LlmModelConfig openaiModel = new LlmModelConfig(
            "gpt-4", "openai", "https://api.openai.com/v1/completions", "sk-test", 1000, 30
        );
        
        // Test that component can handle different model configurations
        assertNotNull(responseHandler.getModelEndpoint(ollamaModel));
        assertNotNull(responseHandler.getModelEndpoint(openaiModel));
    }
}
