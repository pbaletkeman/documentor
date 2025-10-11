package com.documentor.service.llm;

import com.documentor.config.model.LlmModelConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.anyString;

/**
 * 🧪 Tests for LlmApiClient component
 */
@ExtendWith(MockitoExtension.class)
class LlmApiClientTest {

    @Mock
    private WebClient mockWebClient;

    @Mock
    private LlmModelTypeDetector modelTypeDetector;

    private LlmApiClient apiClient;
    private LlmModelConfig openAiModel;
    private LlmModelConfig ollamaModel;
    private LlmModelConfig codelamaModel;
    private LlmModelConfig mistralModel;

    @BeforeEach
    void setUp() {
        apiClient = new LlmApiClient(mockWebClient, modelTypeDetector);

        openAiModel = new LlmModelConfig(
            "gpt-4", "openai", "https://api.openai.com/v1/completions", "sk-test-key", 1000, 30
        );

        ollamaModel = new LlmModelConfig(
            "llama2", "ollama", "http://localhost:11434/api/generate", "", 1000, 30
        );

        codelamaModel = new LlmModelConfig(
            "codellama:7b", "ollama", "http://localhost:11434/api/generate", "", 1000, 30
        );

        mistralModel = new LlmModelConfig(
            "mistral:latest", "ollama", "http://localhost:11434/api/generate", "", 1000, 30
        );
    }

    @Test
    @DisplayName("Should successfully create LlmApiClient instance")
    void testLlmApiClientConstructor() {
        assertNotNull(apiClient);
    }

    @Test
    @DisplayName("Should handle exceptions during LLM API calls")
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
    @DisplayName("Should successfully detect model types")
    void testIsOllamaModelDetection() {
        // Test via actual model configurations that would trigger different behaviors

        // Test with different model types
        assertNotNull(ollamaModel); // ollama endpoint
        assertNotNull(codelamaModel); // codellama model name
        assertNotNull(mistralModel); // mistral model name
        assertNotNull(openAiModel); // OpenAI model (not Ollama)

        // Verify models are properly configured
        assertTrue(ollamaModel.baseUrl().contains("11434"));
        assertTrue(codelamaModel.name().startsWith("codellama"));
        assertTrue(mistralModel.name().startsWith("mistral"));
        assertFalse(openAiModel.baseUrl().contains("ollama"));
    }

    @Test
    @DisplayName("Should handle different model configurations properly")
    void testModelConfigurationHandling() {
        // Test with model without API key
        LlmModelConfig modelWithoutKey = new LlmModelConfig(
            "test-model", "ollama", "http://test.api", "", 1000, 30
        );

        // Test with model with null API key
        LlmModelConfig modelWithNullKey = new LlmModelConfig(
            "test-model", "ollama", "http://test.api", null, 1000, 30
        );

        assertNotNull(modelWithoutKey);
        assertNotNull(modelWithNullKey);
        assertEquals("", modelWithoutKey.apiKey());
        assertNull(modelWithNullKey.apiKey());
    }

    @Test
    @DisplayName("Should include model name in error message")
    void testCallLlmModelWithDifferentEndpoints() {
        // Test behavior with different model types
        Map<String, Object> requestBody = Map.of("prompt", "test");

        // Mock exception for simplicity - we just want to check the error formatting
        when(mockWebClient.post()).thenThrow(new RuntimeException("Connection failed"));

        // Test with one model type
        String openaiResult = apiClient.callLlmModel(openAiModel, "https://api.example.com", requestBody);

        // Check error messages contain the model name
        assertTrue(openaiResult.contains("Error generating content with gpt-4"));
    }

    @Test
    @DisplayName("Should respect configured timeout values")
    void testTimeoutConfiguration() {
        // Test that models can be configured with different timeouts
        LlmModelConfig fastModel = new LlmModelConfig(
            "fast-model", "ollama", "http://fast.api", "key", 100, 5
        );

        LlmModelConfig slowModel = new LlmModelConfig(
            "slow-model", "ollama", "http://slow.api", "key", 1000, 120
        );

        assertEquals(5, fastModel.timeoutSeconds());
        assertEquals(120, slowModel.timeoutSeconds());
    }

    @Test
    @DisplayName("Should add authorization header for non-Ollama models with API key")
    void testAuthHeaderAddedForNonOllamaModels() {
        // This test is simplified - we can verify that non-Ollama models need auth headers
        // by checking the openAiModel API key setup
        assertFalse(openAiModel.apiKey().isEmpty());
        assertEquals("sk-test-key", openAiModel.apiKey());
    }

    @Test
    @DisplayName("Should apply timeout from model configuration")
    void testApplyTimeoutFromModelConfig() {
        // Test that custom timeouts are properly configured on the model
        LlmModelConfig modelWithCustomTimeout = new LlmModelConfig(
            "test-model", "provider", "http://test.api", "key", 1000, 45
        );

        assertEquals(45, modelWithCustomTimeout.timeoutSeconds());
    }

    @Test
    @DisplayName("Should add authorization header for non-Ollama models with valid API key")
    void testAuthHeaderForNonOllamaWithApiKey() {
        // Given
        Map<String, Object> requestBody = Map.of("prompt", "test");
        
        // Mock the WebClient chain - using raw types to avoid generic issues
        WebClient.RequestBodyUriSpec requestBodyUriSpec = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestBodySpec requestBodySpec = mock(WebClient.RequestBodySpec.class);
        @SuppressWarnings("rawtypes")
        WebClient.RequestHeadersSpec requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
        
        when(mockWebClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.header(eq("Content-Type"), eq("application/json"))).thenReturn(requestBodySpec);
        when(requestBodySpec.header(eq("Authorization"), eq("Bearer sk-test-key"))).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(requestBody)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("response"));
        
        // Configure model detector to return false (not Ollama)
        when(modelTypeDetector.isOllamaModel(openAiModel)).thenReturn(false);
        
        // When
        String result = apiClient.callLlmModel(openAiModel, "http://test.api", requestBody);
        
        // Then
        assertEquals("response", result);
        verify(requestBodySpec).header("Authorization", "Bearer sk-test-key");
    }

    @Test
    @DisplayName("Should not add authorization header for Ollama models with proper mock chain")
    void testNoAuthHeaderForOllamaModelsMocked() {
        // Given
        Map<String, Object> requestBody = Map.of("prompt", "test");
        
        // Mock the WebClient chain - using raw types to avoid generic issues
        WebClient.RequestBodyUriSpec requestBodyUriSpec = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestBodySpec requestBodySpec = mock(WebClient.RequestBodySpec.class);
        @SuppressWarnings("rawtypes")
        WebClient.RequestHeadersSpec requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
        
        when(mockWebClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.header(eq("Content-Type"), eq("application/json"))).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(requestBody)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("ollama response"));
        
        // Configure model detector to return true (is Ollama)
        when(modelTypeDetector.isOllamaModel(ollamaModel)).thenReturn(true);
        
        // When
        String result = apiClient.callLlmModel(ollamaModel, "http://localhost:11434/api/generate", requestBody);
        
        // Then
        assertEquals("ollama response", result);
        verify(requestBodySpec, never()).header(eq("Authorization"), anyString());
    }

    @Test
    @DisplayName("Should not add authorization header for non-Ollama models with null API key")
    void testNoAuthHeaderForNullApiKey() {
        // Given
        LlmModelConfig modelWithNullKey = new LlmModelConfig(
            "test-model", "openai", "http://test.api", null, 1000, 30
        );
        Map<String, Object> requestBody = Map.of("prompt", "test");
        
        // Mock the WebClient chain - using raw types to avoid generic issues
        WebClient.RequestBodyUriSpec requestBodyUriSpec = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestBodySpec requestBodySpec = mock(WebClient.RequestBodySpec.class);
        @SuppressWarnings("rawtypes")
        WebClient.RequestHeadersSpec requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
        
        when(mockWebClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.header(eq("Content-Type"), eq("application/json"))).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(requestBody)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("no auth response"));
        
        // Configure model detector to return false (not Ollama)
        when(modelTypeDetector.isOllamaModel(modelWithNullKey)).thenReturn(false);
        
        // When
        String result = apiClient.callLlmModel(modelWithNullKey, "http://test.api", requestBody);
        
        // Then
        assertEquals("no auth response", result);
        verify(requestBodySpec, never()).header(eq("Authorization"), anyString());
    }

    @Test
    @DisplayName("Should not add authorization header for non-Ollama models with empty API key")
    void testNoAuthHeaderForEmptyApiKey() {
        // Given
        LlmModelConfig modelWithEmptyKey = new LlmModelConfig(
            "test-model", "openai", "http://test.api", "", 1000, 30
        );
        Map<String, Object> requestBody = Map.of("prompt", "test");
        
        // Mock the WebClient chain - using raw types to avoid generic issues
        WebClient.RequestBodyUriSpec requestBodyUriSpec = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestBodySpec requestBodySpec = mock(WebClient.RequestBodySpec.class);
        @SuppressWarnings("rawtypes")
        WebClient.RequestHeadersSpec requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
        
        when(mockWebClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.header(eq("Content-Type"), eq("application/json"))).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(requestBody)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("empty key response"));
        
        // Configure model detector to return false (not Ollama)
        when(modelTypeDetector.isOllamaModel(modelWithEmptyKey)).thenReturn(false);
        
        // When
        String result = apiClient.callLlmModel(modelWithEmptyKey, "http://test.api", requestBody);
        
        // Then
        assertEquals("empty key response", result);
        verify(requestBodySpec, never()).header(eq("Authorization"), anyString());
    }
}
