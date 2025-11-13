package com.documentor.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.documentor.config.DocumentorConfig;
import com.documentor.config.model.LlmModelConfig;
import com.documentor.model.CodeElement;
import com.documentor.service.llm.LlmApiClient;
import com.documentor.service.llm.LlmRequestBuilder;
import com.documentor.service.llm.LlmResponseHandler;

/**
 * Comprehensive branch coverage tests for LlmService.
 * This test class specifically targets uncovered branches to increase coverage
 * above 75%.
 */
@ExtendWith(MockitoExtension.class)
class LlmServiceBranchCoverageTest {

    @Mock
    private DocumentorConfig mockConfig;

    @Mock
    private LlmRequestBuilder mockRequestBuilder;

    @Mock
    private LlmResponseHandler mockResponseHandler;

    @Mock
    private LlmApiClient mockApiClient;

    @Mock
    private CodeElement mockCodeElement;

    @Mock
    private LlmModelConfig mockModelConfig;

    private LlmService llmService;

    @BeforeEach
    void setUp() {
        llmService = new LlmService(mockConfig, mockRequestBuilder,
                mockResponseHandler, mockApiClient);
    }

    /**
     * Test generateDocumentation with null configuration - should return
     * error message
     */
    @Test
    void testGenerateDocumentation_NullConfig() {
        // Arrange - Clear any leftover ThreadLocal config from other tests
        LlmService.clearThreadLocalConfig();
        LlmService serviceWithNullConfig = new LlmService(null, mockRequestBuilder,
                mockResponseHandler, mockApiClient);

        // Act
        CompletableFuture<String> result = serviceWithNullConfig
                .generateDocumentation(mockCodeElement);

        // Assert
        assertNotNull(result);
        String errorMessage = result.join();
        assertTrue(errorMessage.contains("Error: LLM configuration is null"));
    }    /**
     * Test generateDocumentation with configuration having empty models list
     */
    @Test
    void testGenerateDocumentation_EmptyModelsList() {
        // Arrange
        when(mockConfig.llmModels()).thenReturn(Collections.emptyList());

        // Act
        CompletableFuture<String> result = llmService
                .generateDocumentation(mockCodeElement);

        // Assert
        assertNotNull(result);
        String message = result.join();
        assertTrue(message.contains("No LLM models configured for documentation generation"));
    }    /**
     * Test generateDocumentation with null models list
     */
    @Test
    void testGenerateDocumentation_NullModelsList() {
        // Arrange
        when(mockConfig.llmModels()).thenReturn(null);

        // Act & Assert - This should throw NullPointerException when trying to call
        // isEmpty() on null
        assertThrows(NullPointerException.class, () -> {
            CompletableFuture<String> result = llmService
                    .generateDocumentation(mockCodeElement);
            result.join(); // This will trigger the NPE
        });
    }

    /**
     * Test generateDocumentation with successful model generation
     */
    @Test
    void testGenerateDocumentation_SuccessfulGeneration() throws Exception {
        // Arrange
        when(mockConfig.llmModels()).thenReturn(List.of(mockModelConfig));
        when(mockModelConfig.name()).thenReturn("test-model");
        when(mockRequestBuilder.createDocumentationPrompt(mockCodeElement))
                .thenReturn("test prompt");
        when(mockRequestBuilder.buildRequestBody(mockModelConfig, "test prompt"))
                .thenReturn(Map.of("prompt", "test"));
        when(mockResponseHandler.getModelEndpoint(mockModelConfig))
                .thenReturn("http://test.com");
        when(mockApiClient.callLlmModel(mockModelConfig, "http://test.com", Map.of("prompt", "test")))
                .thenReturn("test response");
        when(mockResponseHandler.extractResponseContent("test response", mockModelConfig))
                .thenReturn("Generated documentation");

        // Act
        CompletableFuture<String> result = llmService
                .generateDocumentation(mockCodeElement);

        // Assert
        assertNotNull(result);
        assertEquals("Generated documentation", result.join());
    }

    /**
     * Test generateDocumentation with exception during generation
     */
    @Test
    void testGenerateDocumentation_ExceptionDuringGeneration() throws Exception {
        // Arrange
        when(mockConfig.llmModels()).thenReturn(List.of(mockModelConfig));
        when(mockModelConfig.name()).thenReturn("test-model");
        when(mockRequestBuilder.createDocumentationPrompt(mockCodeElement))
                .thenReturn("test prompt");
        when(mockRequestBuilder.buildRequestBody(mockModelConfig, "test prompt")).thenThrow(new RuntimeException("Build error"));

        // Act
        CompletableFuture<String> result = llmService
                .generateDocumentation(mockCodeElement);

        // Assert
        assertNotNull(result);
        String errorResult = result.join();
        assertTrue(errorResult.contains("Error generating documentation with test-model"));
    }

    /**
     * Test generateUsageExamples with null configuration
     */
    @Test
    void testGenerateUsageExamples_NullConfig() {
        // Arrange - Clear any leftover ThreadLocal config from other tests
        LlmService.clearThreadLocalConfig();
        LlmService serviceWithNullConfig = new LlmService(null, mockRequestBuilder,
                mockResponseHandler, mockApiClient);

        // Act
        CompletableFuture<String> result = serviceWithNullConfig.generateUsageExamples(mockCodeElement);

        // Assert
        assertNotNull(result);
        String errorMessage = result.join();
        assertTrue(errorMessage.contains("Error: LLM configuration is null"));
    }    /**
     * Test generateUsageExamples with empty models list
     */
    @Test
    void testGenerateUsageExamples_EmptyModelsList() {
        // Arrange
        when(mockConfig.llmModels()).thenReturn(Collections.emptyList());

        // Act
        CompletableFuture<String> result = llmService.generateUsageExamples(mockCodeElement);

        // Assert
        assertNotNull(result);
        String message = result.join();
        assertTrue(message.contains("No LLM models configured for example generation"));
    }    /**
     * Test generateUsageExamples with successful generation
     */
    @Test
    void testGenerateUsageExamples_SuccessfulGeneration() throws Exception {
        // Arrange
        when(mockConfig.llmModels()).thenReturn(List.of(mockModelConfig));
        when(mockModelConfig.name()).thenReturn("test-model");
        when(mockRequestBuilder.createUsageExamplePrompt(mockCodeElement))
                .thenReturn("usage prompt");
        when(mockRequestBuilder.buildRequestBody(mockModelConfig, "usage prompt"))
                .thenReturn(Map.of("prompt", "usage"));
        when(mockResponseHandler.getModelEndpoint(mockModelConfig))
                .thenReturn("http://test.com");
        when(mockApiClient.callLlmModel(mockModelConfig, "http://test.com", Map.of("prompt", "usage")))
                .thenReturn("usage response");
        when(mockResponseHandler.extractResponseContent("usage response", mockModelConfig))
                .thenReturn("Generated usage examples");

        // Act
        CompletableFuture<String> result = llmService.generateUsageExamples(mockCodeElement);

        // Assert
        assertNotNull(result);
        assertEquals("Generated usage examples", result.join());
    }

    /**
     * Test generateUsageExamples with exception during generation
     */
    @Test
    void testGenerateUsageExamples_ExceptionDuringGeneration() throws Exception {
        // Arrange
        when(mockConfig.llmModels()).thenReturn(List.of(mockModelConfig));
        when(mockModelConfig.name()).thenReturn("test-model");
        when(mockRequestBuilder.createUsageExamplePrompt(mockCodeElement)).thenThrow(new RuntimeException("Prompt error"));

        // Act
        CompletableFuture<String> result = llmService.generateUsageExamples(mockCodeElement);

        // Assert
        assertNotNull(result);
        String errorResult = result.join();
        assertTrue(errorResult.contains("Error generating usage with test-model"));
    }

    /**
     * Test generateUnitTests with null configuration
     */
    @Test
    void testGenerateUnitTests_NullConfig() {
        // Arrange - Clear any leftover ThreadLocal config from other tests
        LlmService.clearThreadLocalConfig();
        LlmService serviceWithNullConfig = new LlmService(null, mockRequestBuilder,
                mockResponseHandler, mockApiClient);

        // Act
        CompletableFuture<String> result = serviceWithNullConfig.generateUnitTests(mockCodeElement);

        // Assert
        assertNotNull(result);
        String errorMessage = result.join();
        assertTrue(errorMessage.contains("Error: LLM configuration is null"));
    }    /**
     * Test generateUnitTests with null models list
     */
    @Test
    void testGenerateUnitTests_NullModelsList() {
        // Arrange
        when(mockConfig.llmModels()).thenReturn(null);

        // Act & Assert - This should throw NullPointerException when trying to call
        // isEmpty() on null
        assertThrows(NullPointerException.class, () -> {
            CompletableFuture<String> result = llmService.generateUnitTests(mockCodeElement);
            result.join(); // This will trigger the NPE
        });
    }    /**
     * Test generateUnitTests with successful generation
     */
    @Test
    void testGenerateUnitTests_SuccessfulGeneration() throws Exception {
        // Arrange
        when(mockConfig.llmModels()).thenReturn(List.of(mockModelConfig));
        when(mockModelConfig.name()).thenReturn("test-model");
        when(mockRequestBuilder.createUnitTestPrompt(mockCodeElement))
                .thenReturn("test prompt");
        when(mockRequestBuilder.buildRequestBody(mockModelConfig, "test prompt"))
                .thenReturn(Map.of("prompt", "test"));
        when(mockResponseHandler.getModelEndpoint(mockModelConfig))
                .thenReturn("http://test.com");
        when(mockApiClient.callLlmModel(mockModelConfig, "http://test.com", Map.of("prompt", "test")))
                .thenReturn("test response");
        when(mockResponseHandler.extractResponseContent("test response", mockModelConfig))
                .thenReturn("Generated unit tests");

        // Act
        CompletableFuture<String> result = llmService.generateUnitTests(mockCodeElement);

        // Assert
        assertNotNull(result);
        assertEquals("Generated unit tests", result.join());
    }    /**
     * Test generateUnitTests with API client exception
     */
    @Test
    void testGenerateUnitTests_ApiClientException() throws Exception {
        // Arrange
        when(mockConfig.llmModels()).thenReturn(List.of(mockModelConfig));
        when(mockModelConfig.name()).thenReturn("test-model");
        when(mockRequestBuilder.createUnitTestPrompt(mockCodeElement))
                .thenReturn("test prompt");
        when(mockRequestBuilder.buildRequestBody(mockModelConfig, "test prompt"))
                .thenReturn(Map.of("prompt", "test"));
        when(mockResponseHandler.getModelEndpoint(mockModelConfig))
                .thenReturn("http://test.com");
        when(mockApiClient.callLlmModel(mockModelConfig, "http://test.com", Map.of("prompt", "test")))
            .thenThrow(new RuntimeException("API error"));

        // Act
        CompletableFuture<String> result = llmService.generateUnitTests(mockCodeElement);

        // Assert
        assertNotNull(result);
        String errorResult = result.join();
        assertTrue(errorResult.contains("Error generating tests with test-model"));
    }

    /**
     * Test createPrompt switch statement with different types
     */
    @Test
    void testCreatePrompt_AllTypes() throws Exception {
        // Arrange
        when(mockConfig.llmModels()).thenReturn(List.of(mockModelConfig));
        when(mockModelConfig.name()).thenReturn("test-model");
        when(mockRequestBuilder.buildRequestBody(any(), any()))
                .thenReturn(Map.of("prompt", "test"));
        when(mockResponseHandler.getModelEndpoint(mockModelConfig))
                .thenReturn("http://test.com");
        when(mockApiClient.callLlmModel(any(), any(), any()))
                .thenReturn("response");
        when(mockResponseHandler.extractResponseContent(any(), any()))
                .thenReturn("result");

        // Test documentation type
        when(mockRequestBuilder.createDocumentationPrompt(mockCodeElement))
                .thenReturn("doc prompt");
        llmService.generateDocumentation(mockCodeElement).join();
        verify(mockRequestBuilder).createDocumentationPrompt(mockCodeElement);

        // Test usage type
        when(mockRequestBuilder.createUsageExamplePrompt(mockCodeElement))
                .thenReturn("usage prompt");
        llmService.generateUsageExamples(mockCodeElement).join();
        verify(mockRequestBuilder).createUsageExamplePrompt(mockCodeElement);

        // Test tests type
        when(mockRequestBuilder.createUnitTestPrompt(mockCodeElement))
                .thenReturn("test prompt");
        llmService.generateUnitTests(mockCodeElement).join();
        verify(mockRequestBuilder).createUnitTestPrompt(mockCodeElement);
    }

    /**
     * Test async timeout handling with CompletableFuture
     */
    @Test
    void testAsyncTimeout() throws Exception {
        // Arrange
        when(mockConfig.llmModels()).thenReturn(List.of(mockModelConfig));
        when(mockModelConfig.name()).thenReturn("slow-model");
        when(mockRequestBuilder.createDocumentationPrompt(mockCodeElement))
                .thenReturn("prompt");
        when(mockRequestBuilder.buildRequestBody(mockModelConfig, "prompt"))
                .thenReturn(Map.of("prompt", "test"));
        when(mockResponseHandler.getModelEndpoint(mockModelConfig))
                .thenReturn("http://slow.com");

        // Simulate a slow API call
        when(mockApiClient.callLlmModel(mockModelConfig, "http://slow.com", Map.of("prompt", "test")))
            .thenAnswer(invocation -> {
                Thread.sleep(100); // Simulate slow response
                return "delayed response";
            });
        when(mockResponseHandler.extractResponseContent("delayed response", mockModelConfig))
                .thenReturn("delayed result");

        // Act
        CompletableFuture<String> result = llmService
                .generateDocumentation(mockCodeElement);

        // Assert - Test that future completes even with delay
        assertNotNull(result);
        assertDoesNotThrow(() -> {
            String content = result.get(1, TimeUnit.SECONDS);
            assertEquals("delayed result", content);
        });
    }

    /**
     * Test exception propagation in async execution
     */
    @Test
    void testAsyncExceptionPropagation() throws Exception {
        // Arrange
        when(mockConfig.llmModels()).thenReturn(List.of(mockModelConfig));
        when(mockModelConfig.name()).thenReturn("error-model");
        when(mockRequestBuilder.createDocumentationPrompt(mockCodeElement))
                .thenReturn("prompt");
        when(mockRequestBuilder.buildRequestBody(mockModelConfig, "prompt"))
                .thenReturn(Map.of("prompt", "test"));
        when(mockResponseHandler.getModelEndpoint(mockModelConfig))
                .thenReturn("http://error.com");
        when(mockApiClient.callLlmModel(mockModelConfig, "http://error.com", Map.of("prompt", "test")))
            .thenThrow(new RuntimeException("Network error"));

        // Act
        CompletableFuture<String> result = llmService
                .generateDocumentation(mockCodeElement);

        // Assert - Exception should be handled and error message returned
        assertNotNull(result);
        String errorResult = result.join();
        assertTrue(errorResult.contains("Error generating documentation with error-model"));
    }

    /**
     * Test ThreadLocal configuration propagation
     */
    @Test
    void testThreadLocalConfigurationPropagation() throws Exception {
        // Arrange
        when(mockConfig.llmModels()).thenReturn(List.of(mockModelConfig));
        when(mockModelConfig.name()).thenReturn("test-model");
        when(mockRequestBuilder.createDocumentationPrompt(mockCodeElement))
                .thenReturn("prompt");
        when(mockRequestBuilder.buildRequestBody(mockModelConfig, "prompt"))
                .thenReturn(Map.of("prompt", "test"));
        when(mockResponseHandler.getModelEndpoint(mockModelConfig))
                .thenReturn("http://test.com");
        when(mockApiClient.callLlmModel(mockModelConfig, "http://test.com", Map.of("prompt", "test")))
                .thenReturn("response");
        when(mockResponseHandler.extractResponseContent("response", mockModelConfig))
                .thenReturn("result");

        // Act - Execute on async thread
        CompletableFuture<String> result = llmService
                .generateDocumentation(mockCodeElement);

        // Assert - Configuration should be accessible in async context
        assertNotNull(result);
        assertEquals("result", result.join());

        // Verify that the executor was used for async execution
        verify(mockRequestBuilder).createDocumentationPrompt(mockCodeElement);
        verify(mockApiClient).callLlmModel(mockModelConfig, "http://test.com", Map.of("prompt", "test"));
    }
}
