package com.documentor.service.llm;

import com.documentor.config.model.LlmModelConfig;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Enhanced test for LlmApiClient focused only on core functionality
 * without complex mocking of WebClient, which has generic type issues
 */
class LlmApiClientSimplifiedTest {

    // Test constants for magic number violations
    private static final int MAX_TOKENS_1000 = 1000;
    private static final int TIMEOUT_30 = 30;
    private static final int MAX_TOKENS_2000 = 2000;
    private static final int TIMEOUT_60 = 60;

    @Test
    void testErrorHandlingWithNullApi() {
        // Setup
        WebClient mockWebClient = mock(WebClient.class);
        LlmModelTypeDetector modelTypeDetector = mock(
            LlmModelTypeDetector.class);

        // Make WebClient.post() throw NullPointerException
        when(mockWebClient.post()).thenThrow(new NullPointerException(
            "API endpoint not available"));

        // Create client and model
        LlmApiClient apiClient = new LlmApiClient(
            mockWebClient, modelTypeDetector);
        LlmModelConfig model = new LlmModelConfig(
            "test-model", "test-provider",
            "http://test.api", "key", MAX_TOKENS_1000, TIMEOUT_30
        );

        // Execute
        String result = apiClient.callLlmModel(model,
            "http://test.api", null);

        // Verify proper error handling
        assertTrue(result.contains(
            "Error generating content with test-model"));
        verify(mockWebClient).post();
    }

    @Test
    void testErrorHandlingWithTimeoutException() {
        // Setup - direct testing of error case without complex mocking
        WebClient mockWebClient = mock(WebClient.class);
        LlmModelTypeDetector modelTypeDetector = mock(
            LlmModelTypeDetector.class);

        // Make WebClient.post() throw timeout exception
        when(mockWebClient.post()).thenThrow(new RuntimeException(
            "Request timeout after 30 seconds"));

        // Create client and model with different names to
        // check they appear in error
        LlmApiClient apiClient = new LlmApiClient(
            mockWebClient, modelTypeDetector);
        LlmModelConfig model = new LlmModelConfig(
            "gpt-4", "openai", "https://api.openai.com",
            "key", MAX_TOKENS_1000, TIMEOUT_30
        );

        // Execute
        String result = apiClient.callLlmModel(model,
        "https://api.openai.com", null);

        // Verify proper error handling with correct model name
        assertTrue(result.contains("Error generating content with gpt-4"));
        // Should not contain wrong model name
        assertFalse(result.contains("test-model"));
        verify(mockWebClient).post();
    }

    @Test
    void testErrorHandlingWithIllegalArgumentException() {
        // Setup
        WebClient mockWebClient = mock(WebClient.class);
        LlmModelTypeDetector modelTypeDetector = mock(
            LlmModelTypeDetector.class);

        // Make WebClient.post() throw IllegalArgumentException
        when(mockWebClient.post()).thenThrow(new IllegalArgumentException(
            "Invalid request format"));

        // Create client and model
        LlmApiClient apiClient = new LlmApiClient(
            mockWebClient, modelTypeDetector);
        LlmModelConfig model = new LlmModelConfig(
            "claude-3", "anthropic",
            "https://api.anthropic.com", "key", MAX_TOKENS_2000, TIMEOUT_60
        );

        // Execute
        String result = apiClient.callLlmModel(model,
            "https://api.anthropic.com", null);

        // Verify proper error handling
        assertTrue(result.contains("Error generating content with claude-3"));
        verify(mockWebClient).post();
    }
}
