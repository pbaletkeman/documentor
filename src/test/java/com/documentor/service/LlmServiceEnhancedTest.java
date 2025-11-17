package com.documentor.service;

import com.documentor.config.DocumentorConfig;
import com.documentor.config.ThreadLocalContextHolder;
import com.documentor.config.ThreadLocalPropagatingExecutorEnhanced;
import com.documentor.config.model.LlmModelConfig;
import com.documentor.config.model.OutputSettings;
import com.documentor.config.model.AnalysisSettings;
import com.documentor.model.CodeElement;
import com.documentor.model.CodeElementType;
import com.documentor.service.llm.LlmApiClient;
import com.documentor.service.llm.LlmRequestBuilder;
import com.documentor.service.llm.LlmResponseHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

/**
 * Tests for LlmServiceEnhanced to improve branch coverage.
 */
@ExtendWith(MockitoExtension.class)
class LlmServiceEnhancedTest {

    @Mock
    private LlmRequestBuilder requestBuilder;

    @Mock
    private LlmResponseHandler responseHandler;

    @Mock
    private LlmApiClient apiClient;

    private DocumentorConfig config;
    private LlmServiceEnhanced llmService;
    private CodeElement testCodeElement;

    @BeforeEach
    void setUp() {
        config = new DocumentorConfig(
            List.of(new LlmModelConfig("test-model", "ollama",
                    "http://localhost:11434", "test-key", 1000, 30)),
            new OutputSettings("./test-output", "markdown", true, true, false),
            new AnalysisSettings(null, null, null, null)
        );

        testCodeElement = new CodeElement(
            CodeElementType.CLASS,
            "TestClass",
            "TestClass",
            "src/test/TestClass.java",
            1,
            "public class TestClass {}",
            "Test class",
            List.of(),
            List.of()
        );

        llmService = new LlmServiceEnhanced(config, requestBuilder,
                responseHandler, apiClient);
    }

    @Test
    void testConstructorWithValidConfig() {
        // Test constructor completes successfully
        assertNotNull(llmService);
    }

    @Test
    void testConstructorWithNullConfig() {
        // Test constructor handles null config
        assertDoesNotThrow(() -> new LlmServiceEnhanced(null, requestBuilder,
                responseHandler, apiClient));
    }

    @Test
    void testConstructorWithExecutorCreationFailure() {
        try (MockedStatic<ThreadLocalPropagatingExecutorEnhanced> mockedStatic =
             mockStatic(ThreadLocalPropagatingExecutorEnhanced.class)) {

            // Arrange
            mockedStatic.when(() ->
                    ThreadLocalPropagatingExecutorEnhanced.createExecutor(
                            anyInt(), anyString()))
                    .thenThrow(new RuntimeException(
                        "Executor creation failed"));

            // Act & Assert - Should handle failure gracefully
            assertDoesNotThrow(() -> new LlmServiceEnhanced(config,
                    requestBuilder, responseHandler, apiClient));
        }
    }

    @Test
    void testGenerateDocumentationWithValidConfig() throws ExecutionException,
            InterruptedException {
        // Arrange
        when(requestBuilder.createDocumentationPrompt(testCodeElement))
                .thenReturn("test prompt");
        when(requestBuilder.buildRequestBody(any(LlmModelConfig.class),
                anyString()))
            .thenReturn(Map.of("prompt", "test prompt"));
        when(responseHandler.getModelEndpoint(any(LlmModelConfig.class)))
                .thenReturn("/api/generate");
        when(apiClient.callLlmModel(any(LlmModelConfig.class),
                anyString(), any()))
            .thenReturn("LLM response");
        when(responseHandler.extractResponseContent(anyString(),
                any(LlmModelConfig.class)))
            .thenReturn("Generated documentation");

        // Act
        CompletableFuture<String> result =
                llmService.generateDocumentation(testCodeElement);
        String documentation = result.get();

        // Assert
        assertNotNull(documentation);
        assertEquals("Generated documentation", documentation);
        verify(requestBuilder).createDocumentationPrompt(testCodeElement);
    }

    @Test
    void testGenerateDocumentationWithNullConfig() throws ExecutionException,
            InterruptedException {
        // Arrange
        LlmServiceEnhanced serviceWithNullConfig = new LlmServiceEnhanced(null,
                requestBuilder, responseHandler, apiClient);

        try (MockedStatic<ThreadLocalContextHolder> mockedStatic =
                mockStatic(ThreadLocalContextHolder.class)) {
                        mockedStatic.when(ThreadLocalContextHolder::getConfig)
                                .thenReturn(null);

            // Act
            CompletableFuture<String> result =
                    serviceWithNullConfig.generateDocumentation(
                        testCodeElement);
            String documentation = result.get();

            // Assert
                        assertTrue(
                                documentation.contains(
                                        "Error: LLM configuration is null")
                        );
        }
    }

    @Test
    void testGenerateDocumentationWithEmptyModels() throws ExecutionException,
            InterruptedException {
        // Arrange
        DocumentorConfig emptyModelsConfig = new DocumentorConfig(
            List.of(), // Empty models list
            new OutputSettings("./test-output", "markdown", true, true, false),
            new AnalysisSettings(null, null, null, null)
        );
        LlmServiceEnhanced serviceWithEmptyModels = new LlmServiceEnhanced(
                emptyModelsConfig, requestBuilder, responseHandler, apiClient);

        // Act
        CompletableFuture<String> result =
                serviceWithEmptyModels.generateDocumentation(testCodeElement);
        String documentation = result.get();

        // Assert
        assertEquals("No LLM models configured for documentation generation.",
                documentation);
    }

    @Test
    void testGenerateUsageExamplesWithValidConfig() throws ExecutionException,
            InterruptedException {
        // Arrange
        when(requestBuilder.createUsageExamplePrompt(testCodeElement))
                .thenReturn("usage prompt");
        when(requestBuilder.buildRequestBody(any(LlmModelConfig.class),
                anyString()))
            .thenReturn(Map.of("prompt", "usage prompt"));
        when(responseHandler.getModelEndpoint(any(LlmModelConfig.class)))
                .thenReturn("/api/generate");
        when(apiClient.callLlmModel(any(LlmModelConfig.class),
                anyString(), any()))
            .thenReturn("LLM response");
        when(responseHandler.extractResponseContent(anyString(),
                any(LlmModelConfig.class)))
            .thenReturn("Generated usage examples");

        // Act
        CompletableFuture<String> result =
                llmService.generateUsageExamples(testCodeElement);
        String usageExamples = result.get();

        // Assert
        assertNotNull(usageExamples);
        assertEquals("Generated usage examples", usageExamples);
        verify(requestBuilder).createUsageExamplePrompt(testCodeElement);
    }

    @Test
    void testGenerateUnitTestsWithValidConfig() throws ExecutionException,
            InterruptedException {
        // Arrange
        when(requestBuilder.createUnitTestPrompt(testCodeElement))
                .thenReturn("test prompt");
        when(requestBuilder.buildRequestBody(any(LlmModelConfig.class),
                anyString()))
            .thenReturn(Map.of("prompt", "test prompt"));
        when(responseHandler.getModelEndpoint(any(LlmModelConfig.class)))
                .thenReturn("/api/generate");
        when(apiClient.callLlmModel(any(LlmModelConfig.class),
                anyString(), any()))
            .thenReturn("LLM response");
        when(responseHandler.extractResponseContent(anyString(),
                any(LlmModelConfig.class)))
            .thenReturn("Generated unit tests");

        // Act
        CompletableFuture<String> result = llmService
                .generateUnitTests(testCodeElement);
        String unitTests = result.get();

        // Assert
        assertNotNull(unitTests);
        assertEquals("Generated unit tests", unitTests);
        verify(requestBuilder).createUnitTestPrompt(testCodeElement);
    }

    @Test
    void testStaticGetThreadLocalConfig() {
        try (MockedStatic<ThreadLocalContextHolder> mockedStatic =
                mockStatic(ThreadLocalContextHolder.class)) {
            // Arrange
            mockedStatic.when(ThreadLocalContextHolder::getConfig)
                .thenReturn(config);

            // Act
            DocumentorConfig result = LlmServiceEnhanced
                .getThreadLocalConfig();

            // Assert
            assertEquals(config, result);
        }
    }

    @Test
    void testStaticSetThreadLocalConfig() {
        try (MockedStatic<ThreadLocalContextHolder> mockedStatic =
                mockStatic(ThreadLocalContextHolder.class)) {
            // Act
            LlmServiceEnhanced.setThreadLocalConfig(config);

            // Assert
            mockedStatic.verify(() -> ThreadLocalContextHolder
                .setConfig(config));
        }
    }

    @Test
    void testStaticClearThreadLocalConfig() {
        try (MockedStatic<ThreadLocalContextHolder> mockedStatic =
                mockStatic(ThreadLocalContextHolder.class)) {
            // Act
            LlmServiceEnhanced.clearThreadLocalConfig();

            // Assert
            mockedStatic.verify(ThreadLocalContextHolder::clearConfig);
        }
    }

    @Test
    void testGenerateWithModelExceptionHandling()
            throws ExecutionException, InterruptedException {
        // Arrange
        when(requestBuilder.createDocumentationPrompt(testCodeElement))
                .thenReturn("test prompt");
        when(requestBuilder.buildRequestBody(any(LlmModelConfig.class),
                anyString()))
            .thenThrow(new RuntimeException("Request building failed"));

        // Act
        CompletableFuture<String> result =
                llmService.generateDocumentation(testCodeElement);
        String documentation = result.get();

        // Assert
        assertTrue(documentation.contains("Error generating documentation"));
        assertTrue(documentation.contains("Request building failed"));
    }

    @Test
    void testThreadLocalConfigPropagation()
            throws ExecutionException, InterruptedException {
        try (MockedStatic<ThreadLocalContextHolder> mockedStatic =
                mockStatic(ThreadLocalContextHolder.class)) {
            // Arrange
            when(requestBuilder.createDocumentationPrompt(testCodeElement))
                    .thenReturn("test prompt");
        when(requestBuilder.buildRequestBody(any(LlmModelConfig.class),
                anyString()))
                .thenReturn(Map.of("prompt", "test prompt"));
            when(responseHandler.getModelEndpoint(any(LlmModelConfig.class)))
                    .thenReturn("/api/generate");
            when(apiClient.callLlmModel(any(
                        LlmModelConfig.class), anyString(), any()))
                .thenReturn("LLM response");
            when(responseHandler.extractResponseContent(anyString(),
                    any(LlmModelConfig.class)))
                .thenReturn("Generated documentation");

            // Act
            CompletableFuture<String> result =
                    llmService.generateDocumentation(testCodeElement);
            result.get();

            // Assert - Verify ThreadLocal operations were called
            mockedStatic.verify(() -> ThreadLocalContextHolder
                .setConfig(config), atLeastOnce());
            // Note: logConfigStatus may not be called in all code paths,
            // so just verify setConfig
        }
    }

    @Test
    void testGenerateUsageExamplesWithNullConfig()
            throws ExecutionException, InterruptedException {
        // Arrange - Create service with null config
        LlmServiceEnhanced nullConfigService = new LlmServiceEnhanced(null,
                requestBuilder, responseHandler, apiClient);

        try (MockedStatic<ThreadLocalContextHolder> mockedStatic =
                mockStatic(ThreadLocalContextHolder.class)) {
            mockedStatic.when(ThreadLocalContextHolder::getConfig)
                .thenReturn(null);

            // Act
            CompletableFuture<String> result =
                    nullConfigService.generateUsageExamples(testCodeElement);
            String usageExamples = result.get();

            // Assert
            assertEquals(
                    "Error: LLM configuration is null. "
                    + "Please check the application configuration.",
                    usageExamples);
        }
    }

    @Test
    void testGenerateUsageExamplesWithEmptyModelList()
            throws ExecutionException, InterruptedException {
        // Arrange - Create config with empty model list
        DocumentorConfig emptyConfig = new DocumentorConfig(
                List.of(), null, null);
        LlmServiceEnhanced emptyConfigService = new LlmServiceEnhanced(
                emptyConfig, requestBuilder, responseHandler, apiClient);

        // Act
        CompletableFuture<String> result =
                emptyConfigService.generateUsageExamples(testCodeElement);
        String usageExamples = result.get();

        // Assert
        assertEquals("No LLM models configured for example generation.",
                usageExamples);
    }

    @Test
    void testGenerateUsageExamplesWithException()
            throws ExecutionException, InterruptedException {
        // Arrange
        when(requestBuilder.createUsageExamplePrompt(testCodeElement))
                .thenReturn("usage prompt");
        when(requestBuilder.buildRequestBody(any(LlmModelConfig.class),
                anyString()))
            .thenThrow(new RuntimeException("Request building failed"));

        // Act
        CompletableFuture<String> result =
                llmService.generateUsageExamples(testCodeElement);
        String usageExamples = result.get();

        // Assert
        System.out.println("Actual usage examples result: " + usageExamples);
        assertTrue(usageExamples.contains("Error generating"),
                "Expected error message, got: " + usageExamples);
        assertTrue(usageExamples.contains("Request building failed"),
                "Expected 'Request building failed', got: " + usageExamples);
    }

    @Test
    void testGenerateUnitTestsWithNullConfig()
            throws ExecutionException, InterruptedException {
        // Arrange - Create service with null config
        LlmServiceEnhanced nullConfigService = new LlmServiceEnhanced(null,
                requestBuilder, responseHandler, apiClient);

        try (MockedStatic<ThreadLocalContextHolder> mockedStatic =
                mockStatic(ThreadLocalContextHolder.class)) {
            mockedStatic.when(ThreadLocalContextHolder::getConfig)
                .thenReturn(null);

            // Act
            CompletableFuture<String> result =
                    nullConfigService.generateUnitTests(testCodeElement);
            String unitTests = result.get();

            // Assert
            assertEquals(
                    "Error: LLM configuration is null. "
                    + "Please check the application configuration.",
                    unitTests);
        }
    }

    @Test
    void testGenerateUnitTestsWithEmptyModelList()
            throws ExecutionException, InterruptedException {
        // Arrange - Create config with empty model list
        DocumentorConfig emptyConfig = new DocumentorConfig(
                List.of(), null, null);
        LlmServiceEnhanced emptyConfigService = new LlmServiceEnhanced(
                emptyConfig, requestBuilder, responseHandler, apiClient);

        // Act
        CompletableFuture<String> result =
                emptyConfigService.generateUnitTests(testCodeElement);
        String unitTests = result.get();

        // Assert
        assertEquals("No LLM models configured for unit test generation.",
                unitTests);
    }

    @Test
    void testGenerateUnitTestsWithException()
            throws ExecutionException, InterruptedException {
        // Arrange
        when(requestBuilder.createUnitTestPrompt(testCodeElement))
                .thenReturn("test prompt");
        when(requestBuilder.buildRequestBody(any(LlmModelConfig.class),
                anyString()))
            .thenThrow(new RuntimeException("Unit test generation failed"));

        // Act
        CompletableFuture<String> result =
                llmService.generateUnitTests(testCodeElement);
        String unitTests = result.get();

        // Assert
        System.out.println("Actual unit tests result: " + unitTests);
        assertTrue(unitTests.contains("Error generating"),
                "Expected error message, got: " + unitTests);
        assertTrue(unitTests.contains("Unit test generation failed"),
                "Expected 'Unit test generation failed', got: " + unitTests);
    }

    @Test
    void testGetExecutorWithNullThreadLocalExecutor() {
        // Arrange - Create service with null executor
        LlmServiceEnhanced serviceWithNullExecutor = new LlmServiceEnhanced(
                config, requestBuilder, responseHandler, apiClient);

        // Use reflection to set threadLocalExecutor to null
        try {
            java.lang.reflect.Field executorField =
                    LlmServiceEnhanced.class.getDeclaredField(
                        "threadLocalExecutor");
            executorField.setAccessible(true);
            executorField.set(serviceWithNullExecutor, null);

            // Act - invoke private getExecutor method via reflection
            java.lang.reflect.Method getExecutorMethod =
                    LlmServiceEnhanced.class.getDeclaredMethod("getExecutor");
            getExecutorMethod.setAccessible(true);
            Object executor = getExecutorMethod.invoke(
                serviceWithNullExecutor);

            // Assert - Should return fallback executor (not null)
            assertNotNull(executor);
        } catch (Exception e) {
            fail("Failed to test getExecutor with null threadLocalExecutor: "
                    + e.getMessage());
        }
    }

    @Test
    void testGetExecutorWithValidThreadLocalExecutor() {
        // This test verifies the happy path where threadLocalExecutor is
        // not null The existing tests already cover this scenario, but we'll
        // test explicitly

        try {
            // Act - invoke private getExecutor method via reflection
            java.lang.reflect.Method getExecutorMethod =
                    LlmServiceEnhanced.class.getDeclaredMethod("getExecutor");
            getExecutorMethod.setAccessible(true);
            Object executor = getExecutorMethod.invoke(llmService);

            // Assert - Should return the threadLocalExecutor (not null)
            assertNotNull(executor);
        } catch (Exception e) {
            fail("Failed to test getExecutor with valid threadLocalExecutor: "
                    + e.getMessage());
        }
    }

    @Test
    void testCreatePromptWithNullCodeElement() {
        try {
            // Arrange - Mock the request builder to return something when
            // called with null
            when(requestBuilder.createDocumentationPrompt(null))
                    .thenReturn("documentation prompt for null element");

            // Act - invoke private createPrompt method via reflection with
            // null CodeElement
            java.lang.reflect.Method createPromptMethod =
                    LlmServiceEnhanced.class.getDeclaredMethod("createPrompt",
                            CodeElement.class, String.class);
            createPromptMethod.setAccessible(true);
            String prompt = (String) createPromptMethod.invoke(
                    llmService, null, "documentation");

            // Assert - Should handle null gracefully by delegating to
            // requestBuilder
            assertNotNull(prompt);
            assertEquals("documentation prompt for null element", prompt);
        } catch (Exception e) {
            fail("Failed to test createPrompt with null CodeElement: "
                    + e.getMessage());
        }
    }
}
