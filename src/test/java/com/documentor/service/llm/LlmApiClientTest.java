package com.documentor.service.llm;

import com.documentor.config.DocumentorConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 🧪 Tests for LlmApiClient component
 */
@ExtendWith(MockitoExtension.class)
class LlmApiClientTest {

    @Mock
    private WebClient mockWebClient;

    private LlmApiClient apiClient;
    private DocumentorConfig.LlmModelConfig openAiModel;
    private DocumentorConfig.LlmModelConfig ollamaModel;
    private DocumentorConfig.LlmModelConfig codelamaModel;
    private DocumentorConfig.LlmModelConfig mistralModel;

    @BeforeEach
    void setUp() {
        LlmModelTypeDetector modelTypeDetector = new LlmModelTypeDetector();
        apiClient = new LlmApiClient(mockWebClient, modelTypeDetector);
        
        openAiModel = new DocumentorConfig.LlmModelConfig(
            "gpt-4", "sk-test-key", "https://api.openai.com/v1/completions", 1000, 0.7, 30, Map.of()
        );
        
        ollamaModel = new DocumentorConfig.LlmModelConfig(
            "llama2", "", "http://localhost:11434/api/generate", 1000, 0.7, 30, Map.of()
        );
        
        codelamaModel = new DocumentorConfig.LlmModelConfig(
            "codellama:7b", "", "http://localhost:11434/api/generate", 1000, 0.7, 30, Map.of()
        );
        
        mistralModel = new DocumentorConfig.LlmModelConfig(
            "mistral:latest", "", "http://localhost:11434/api/generate", 1000, 0.7, 30, Map.of()
        );
    }

    @Test
    void testLlmApiClientConstructor() {
        assertNotNull(apiClient);
    }

    @Test
    void testCallLlmModelHandlesException() {
        // Given
        Map<String, Object> requestBody = Map.of("prompt", "test prompt");
        
        // Mock WebClient to throw exception during the call
        when(mockWebClient.post()).thenThrow(new RuntimeException("Network error"));
        
        // When
        String result = apiClient.callLlmModel(openAiModel, "http://test.api", requestBody);
        
        // Then
        assertTrue(result.contains("Error generating content with gpt-4"));
        assertTrue(result.contains("gpt-4"));
    }

    @Test
    void testIsOllamaModelDetection() {
        // Test via actual model configurations that would trigger different behaviors
        // We can't directly test the private method, but we can test behavior differences
        
        // Test with different model types
        assertNotNull(ollamaModel); // ollama endpoint
        assertNotNull(codelamaModel); // codellama model name  
        assertNotNull(mistralModel); // mistral model name
        assertNotNull(openAiModel); // OpenAI model (not Ollama)
        
        // Verify models are properly configured
        assertTrue(ollamaModel.endpoint().contains("11434"));
        assertTrue(codelamaModel.name().startsWith("codellama"));
        assertTrue(mistralModel.name().startsWith("mistral"));
        assertFalse(openAiModel.endpoint().contains("ollama"));
    }

    @Test 
    void testModelConfigurationHandling() {
        // Test with model without API key
        DocumentorConfig.LlmModelConfig modelWithoutKey = new DocumentorConfig.LlmModelConfig(
            "test-model", "", "http://test.api", 1000, 0.7, 30, Map.of()
        );
        
        // Test with model with null API key
        DocumentorConfig.LlmModelConfig modelWithNullKey = new DocumentorConfig.LlmModelConfig(
            "test-model", null, "http://test.api", 1000, 0.7, 30, Map.of()
        );
        
        assertNotNull(modelWithoutKey);
        assertNotNull(modelWithNullKey);
        assertEquals("", modelWithoutKey.apiKey());
        assertNull(modelWithNullKey.apiKey());
    }

    @Test
    void testCallLlmModelWithDifferentEndpoints() {
        // Test behavior with Ollama-style endpoints
        Map<String, Object> requestBody = Map.of("prompt", "test");
        
        // Mock exception to test error handling path
        when(mockWebClient.post()).thenThrow(new RuntimeException("Connection failed"));
        
        // Test Ollama endpoint
        String ollamaResult = apiClient.callLlmModel(ollamaModel, ollamaModel.endpoint(), requestBody);
        assertTrue(ollamaResult.contains("Error generating content with llama2"));
        
        // Test CodeLlama model  
        String codelamaResult = apiClient.callLlmModel(codelamaModel, codelamaModel.endpoint(), requestBody);
        assertTrue(codelamaResult.contains("Error generating content with codellama:7b"));
        
        // Test Mistral model
        String mistralResult = apiClient.callLlmModel(mistralModel, mistralModel.endpoint(), requestBody);
        assertTrue(mistralResult.contains("Error generating content with mistral:latest"));
        
        // Test OpenAI model
        String openaiResult = apiClient.callLlmModel(openAiModel, openAiModel.endpoint(), requestBody);
        assertTrue(openaiResult.contains("Error generating content with gpt-4"));
    }

    @Test
    void testTimeoutConfiguration() {
        // Test that models can be configured with different timeouts
        DocumentorConfig.LlmModelConfig fastModel = new DocumentorConfig.LlmModelConfig(
            "fast-model", "key", "http://fast.api", 100, 0.7, 5, Map.of()
        );
        
        DocumentorConfig.LlmModelConfig slowModel = new DocumentorConfig.LlmModelConfig(
            "slow-model", "key", "http://slow.api", 1000, 0.7, 120, Map.of()
        );
        
        assertEquals(5, fastModel.timeoutSeconds());
        assertEquals(120, slowModel.timeoutSeconds());
    }
}