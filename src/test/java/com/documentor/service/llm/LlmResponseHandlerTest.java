package com.documentor.service.llm;

import com.documentor.config.DocumentorConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 🧪 Simple tests for LlmResponseHandler component
 */
class LlmResponseHandlerTest {

    private LlmResponseHandler responseHandler;
    private DocumentorConfig.LlmModelConfig testModel;

    @BeforeEach
    void setUp() {
        LlmModelTypeDetector modelTypeDetector = new LlmModelTypeDetector();
        LlmResponseParser responseParser = new LlmResponseParser(modelTypeDetector);
        responseHandler = new LlmResponseHandler(responseParser, modelTypeDetector);
        
        testModel = new DocumentorConfig.LlmModelConfig(
            "test-model", "api-key", "http://test.api", 1000, 0.7, 30, Map.of()
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
        DocumentorConfig.LlmModelConfig ollamaModel = new DocumentorConfig.LlmModelConfig(
            "llama2", "", "http://localhost:11434/api/generate", 1000, 0.7, 30, Map.of()
        );
        
        DocumentorConfig.LlmModelConfig openaiModel = new DocumentorConfig.LlmModelConfig(
            "gpt-4", "sk-test", "https://api.openai.com/v1/completions", 1000, 0.7, 30, Map.of()
        );
        
        // Test that component can handle different model configurations
        assertNotNull(responseHandler.getModelEndpoint(ollamaModel));
        assertNotNull(responseHandler.getModelEndpoint(openaiModel));
    }
}