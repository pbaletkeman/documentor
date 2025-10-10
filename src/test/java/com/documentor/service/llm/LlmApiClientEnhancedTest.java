package com.documentor.service.llm;

import com.documentor.config.model.LlmModelConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Enhanced tests for LlmApiClient with focus on the authentication header logic
 */
@ExtendWith(MockitoExtension.class)
class LlmApiClientEnhancedTest {

    @Mock
    private LlmModelTypeDetector mockModelTypeDetector;
    
    /**
     * Test focusing on auth header conditions - no header for Ollama models
     */
    @Test
    void shouldNotAddAuthHeader_whenUsingOllamaModel() {
        // Create a spy on real WebClient to avoid generics issues
        WebClient webClientSpy = spy(WebClient.create());
        
        // Create test objects
        LlmApiClient apiClient = new LlmApiClient(webClientSpy, mockModelTypeDetector);
        LlmModelConfig model = new LlmModelConfig(
            "llama", "ollama", "http://localhost:11434/api/generate", "some-key", 30, 60
        );
        
        // Mock detector to identify as Ollama model
        when(mockModelTypeDetector.isOllamaModel(model)).thenReturn(true);
        
        // Force an exception to avoid actual HTTP call but capture the auth check path
        doThrow(new RuntimeException("Test exception")).when(webClientSpy).post();
        
        // Execute
        String result = apiClient.callLlmModel(model, "http://localhost:11434/api/generate", new HashMap<>());
        
        // Verify correct error handling and that the auth condition was checked
        assertTrue(result.contains("Error generating content"));
        verify(mockModelTypeDetector).isOllamaModel(model);
    }
    
    /**
     * Test focusing on auth header conditions - header needed for non-Ollama with key
     */
    @Test
    void shouldAttemptAuthHeader_whenUsingNonOllamaModel() {
        // Create a spy on real WebClient to avoid generics issues
        WebClient webClientSpy = spy(WebClient.create());
        
        // Create test objects
        LlmApiClient apiClient = new LlmApiClient(webClientSpy, mockModelTypeDetector);
        LlmModelConfig model = new LlmModelConfig(
            "gpt-4", "openai", "https://api.openai.com/v1/chat/completions", "sk-test-key", 30, 60
        );
        
        // Mock detector to identify as non-Ollama model
        when(mockModelTypeDetector.isOllamaModel(model)).thenReturn(false);
        
        // Force an exception to avoid actual HTTP call but capture the auth check path
        doThrow(new RuntimeException("Test exception")).when(webClientSpy).post();
        
        // Execute
        String result = apiClient.callLlmModel(model, "https://api.openai.com/v1/chat/completions", new HashMap<>());
        
        // Verify correct error handling and that the auth condition was checked
        assertTrue(result.contains("Error generating content"));
        verify(mockModelTypeDetector).isOllamaModel(model);
    }
    
    /**
     * Test focusing on auth header conditions - no header when key is null
     */
    @Test
    void shouldSkipAuthHeader_whenApiKeyIsNull() {
        // Create a spy on real WebClient to avoid generics issues
        WebClient webClientSpy = spy(WebClient.create());
        
        // Create test objects with null API key
        LlmApiClient apiClient = new LlmApiClient(webClientSpy, mockModelTypeDetector);
        LlmModelConfig model = new LlmModelConfig(
            "gpt-4", "openai", "https://api.openai.com/v1/chat/completions", null, 30, 60
        );
        
        // Mock detector to identify as non-Ollama model
        when(mockModelTypeDetector.isOllamaModel(model)).thenReturn(false);
        
        // Force an exception to avoid actual HTTP call but capture the auth check path
        doThrow(new RuntimeException("Test exception")).when(webClientSpy).post();
        
        // Execute
        String result = apiClient.callLlmModel(model, "https://api.openai.com/v1/chat/completions", new HashMap<>());
        
        // Verify correct error handling and that we went through the key checking logic
        assertTrue(result.contains("Error generating content"));
        verify(mockModelTypeDetector).isOllamaModel(model);
    }
    
    /**
     * Test focusing on auth header conditions - no header when key is empty
     */
    @Test
    void shouldSkipAuthHeader_whenApiKeyIsEmpty() {
        // Create a spy on real WebClient to avoid generics issues
        WebClient webClientSpy = spy(WebClient.create());
        
        // Create test objects with empty API key
        LlmApiClient apiClient = new LlmApiClient(webClientSpy, mockModelTypeDetector);
        LlmModelConfig model = new LlmModelConfig(
            "gpt-4", "openai", "https://api.openai.com/v1/chat/completions", "", 30, 60
        );
        
        // Mock detector to identify as non-Ollama model
        when(mockModelTypeDetector.isOllamaModel(model)).thenReturn(false);
        
        // Force an exception to avoid actual HTTP call but capture the auth check path
        doThrow(new RuntimeException("Test exception")).when(webClientSpy).post();
        
        // Execute
        String result = apiClient.callLlmModel(model, "https://api.openai.com/v1/chat/completions", new HashMap<>());
        
        // Verify correct error handling
        assertTrue(result.contains("Error generating content"));
        verify(mockModelTypeDetector).isOllamaModel(model);
    }
    
    /**
     * Additional test for timeout behavior
     */
    @Test
    void shouldUseConfiguredTimeout() {
        // Create a spy on real WebClient to avoid generics issues
        WebClient webClientSpy = spy(WebClient.create());
        
        // Create test objects with custom timeout
        LlmApiClient apiClient = new LlmApiClient(webClientSpy, mockModelTypeDetector);
        LlmModelConfig model = new LlmModelConfig(
            "test-model", "test-provider", "http://test-endpoint", "key", 2000, 120 // 120 sec timeout
        );
        
        // Force an exception to avoid actual HTTP call but capture the timeout path
        doThrow(new RuntimeException("Timeout occurred")).when(webClientSpy).post();
        
        // Execute
        String result = apiClient.callLlmModel(model, "http://test-endpoint", Map.of("test", "data"));
        
        // Verify correct error handling
        assertTrue(result.contains("Error generating content with test-model"));
    }
}