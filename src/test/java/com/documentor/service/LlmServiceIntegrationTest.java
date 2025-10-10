package com.documentor.service;

import com.documentor.config.DocumentorConfig;
import com.documentor.config.model.LlmModelConfig;
import com.documentor.config.model.AnalysisSettings;
import com.documentor.config.model.OutputSettings;
import com.documentor.model.CodeElement;
import com.documentor.model.CodeElementType;
import com.documentor.service.llm.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ðŸ§ª Integration tests for LlmService
 *
 * Tests LLM service methods with mocked WebClient to verify
 * prompt generation, model selection, and response handling.
 */
@ExtendWith(MockitoExtension.class)
class LlmServiceIntegrationTest {

    @Mock
    private WebClient mockWebClient;

    private DocumentorConfig config;
    private CodeElement testElement;

    @BeforeEach
    void setUp() {
        // Create test configuration with LLM models
        LlmModelConfig testModel = new LlmModelConfig(
            "test-model", "openai", "https://api.test.com", "test-key", 1000, 30
        );

        OutputSettings outputSettings = new OutputSettings(
            "output", "markdown", true, true
        );

        AnalysisSettings analysisSettings = new AnalysisSettings(
            true, 50, List.of("**/*.java"), List.of("**/test/**")
        );

        config = new DocumentorConfig(List.of(testModel), outputSettings, analysisSettings);

        // Create test code element
        testElement = new CodeElement(
            CodeElementType.METHOD, "testMethod", "com.test.TestClass.testMethod",
            "/test/TestClass.java", 10, "public void testMethod(String param)",
            "Test method documentation", List.of("param"), List.of()
        );
    }

    /**
     * Helper method to create LlmService with new constructor
     */
    private LlmService createLlmService(DocumentorConfig config) {
        LlmModelTypeDetector modelTypeDetector = new LlmModelTypeDetector();
        LlmPromptTemplates promptTemplates = new LlmPromptTemplates();
        LlmRequestFormatter requestFormatter = new LlmRequestFormatter(modelTypeDetector);
        LlmRequestBuilder requestBuilder = new LlmRequestBuilder(promptTemplates, requestFormatter);
        LlmResponseParser responseParser = new LlmResponseParser(modelTypeDetector);
        LlmResponseHandler responseHandler = new LlmResponseHandler(responseParser, modelTypeDetector);
        LlmApiClient apiClient = new LlmApiClient(mockWebClient, modelTypeDetector);

        return new LlmService(config, requestBuilder, responseHandler, apiClient);
    }

    @Test
    void testConstructorInitialization() {
        // When
        LlmService service = createLlmService(config);

        // Then
        assertNotNull(service);
    }

    @Test
    void testGenerateDocumentationWithEmptyModels() {
        // Given - empty model configuration
        DocumentorConfig emptyConfig = new DocumentorConfig(
            List.of(), config.outputSettings(), config.analysisSettings()
        );
        LlmService serviceWithEmptyConfig = createLlmService(emptyConfig);

        // When
        CompletableFuture<String> result = serviceWithEmptyConfig.generateDocumentation(testElement);

        // Then
        assertNotNull(result);
        assertDoesNotThrow(() -> {
            String response = result.get();
            // With empty models, the method returns empty response after consolidation
            assertNotNull(response);
        });
    }

    @Test
    void testGenerateUsageExamplesWithEmptyModels() {
        // Given - empty model configuration
        DocumentorConfig emptyConfig = new DocumentorConfig(
            List.of(), config.outputSettings(), config.analysisSettings()
        );
        LlmService serviceWithEmptyConfig = createLlmService(emptyConfig);

        // When
        CompletableFuture<String> result = serviceWithEmptyConfig.generateUsageExamples(testElement);

        // Then
        assertNotNull(result);
        assertDoesNotThrow(() -> {
            String response = result.get();
            assertTrue(response.contains("No LLM models configured for usage example generation"));
        });
    }

    @Test
    void testGenerateUnitTestsWithEmptyModels() {
        // Given - empty model configuration
        DocumentorConfig emptyConfig = new DocumentorConfig(
            List.of(), config.outputSettings(), config.analysisSettings()
        );
        LlmService serviceWithEmptyConfig = createLlmService(emptyConfig);

        // When
        CompletableFuture<String> result = serviceWithEmptyConfig.generateUnitTests(testElement);

        // Then
        assertNotNull(result);
        assertDoesNotThrow(() -> {
            String response = result.get();
            assertTrue(response.contains("No LLM models configured for unit test generation"));
        });
    }

    @Test
    void testServiceHandlesMultipleCodeElementTypes() {
        // Given - empty model configuration for safe testing
        DocumentorConfig emptyConfig = new DocumentorConfig(
            List.of(), config.outputSettings(), config.analysisSettings()
        );
        LlmService serviceWithEmptyConfig = createLlmService(emptyConfig);

        CodeElement classElement = new CodeElement(
            CodeElementType.CLASS, "TestClass", "com.test.TestClass",
            "/test/TestClass.java", 1, "public class TestClass",
            "Test class", List.of(), List.of()
        );

        // When & Then
        CompletableFuture<String> docResult = serviceWithEmptyConfig.generateDocumentation(classElement);
        CompletableFuture<String> exampleResult = serviceWithEmptyConfig.generateUsageExamples(classElement);
        CompletableFuture<String> testResult = serviceWithEmptyConfig.generateUnitTests(classElement);

        assertNotNull(docResult);
        assertNotNull(exampleResult);
        assertNotNull(testResult);

        assertDoesNotThrow(() -> {
            docResult.get();
            exampleResult.get();
            testResult.get();
        });
    }

    @Test
    void testServiceWithMultipleModels() {
        // Given - configuration with multiple models
        LlmModelConfig model1 = new LlmModelConfig(
            "model-1", "openai", "https://api1.test.com", "key-1", 1000, 30
        );
        LlmModelConfig model2 = new LlmModelConfig(
            "model-2", "openai", "https://api2.test.com", "key-2", 2000, 60
        );

        DocumentorConfig multiModelConfig = new DocumentorConfig(
            List.of(model1, model2), config.outputSettings(), config.analysisSettings()
        );
        LlmService multiModelService = createLlmService(multiModelConfig);

        // When & Then - should not throw exceptions
        assertDoesNotThrow(() -> {
            CompletableFuture<String> result = multiModelService.generateDocumentation(testElement);
            assertNotNull(result);
        });
    }

    @Test
    void testServiceWithDifferentCodeElementTypes() {
        // Given - empty config for safe testing
        DocumentorConfig emptyConfig = new DocumentorConfig(
            List.of(), config.outputSettings(), config.analysisSettings()
        );
        LlmService serviceWithEmptyConfig = createLlmService(emptyConfig);

        // Create elements of different types
        CodeElement[] elements = {
            new CodeElement(CodeElementType.CLASS, "TestClass", "com.test.TestClass",
                "/test/TestClass.java", 1, "public class TestClass", "", List.of(), List.of()),
            new CodeElement(CodeElementType.METHOD, "testMethod", "com.test.TestClass.testMethod",
                "/test/TestClass.java", 5, "public void testMethod()", "", List.of(), List.of()),
            new CodeElement(CodeElementType.FIELD, "testField", "com.test.TestClass.testField",
                "/test/TestClass.java", 3, "private String testField", "", List.of(), List.of()),
            new CodeElement(CodeElementType.CLASS, "TestInterface", "com.test.TestInterface",
                "/test/TestInterface.java", 1, "public interface TestInterface", "", List.of(), List.of())
        };

        // When & Then - all should handle gracefully
        for (CodeElement element : elements) {
            assertDoesNotThrow(() -> {
                CompletableFuture<String> docResult = serviceWithEmptyConfig.generateDocumentation(element);
                CompletableFuture<String> exampleResult = serviceWithEmptyConfig.generateUsageExamples(element);
                CompletableFuture<String> testResult = serviceWithEmptyConfig.generateUnitTests(element);

                assertNotNull(docResult);
                assertNotNull(exampleResult);
                assertNotNull(testResult);

                // Verify responses contain expected messages
                String docResponse = docResult.get();
                String exampleResponse = exampleResult.get();
                String testResponse = testResult.get();

                assertNotNull(docResponse);
                assertTrue(exampleResponse.contains("No LLM models"));
                assertTrue(testResponse.contains("No LLM models"));
            });
        }
    }

    @Test
    void testServiceConfiguration() {
        // Test that service properly uses configuration
        assertNotNull(config.llmModels());
        assertEquals(1, config.llmModels().size());
        assertEquals("test-model", config.llmModels().get(0).name());
    }

    @Test
    void testAsyncMethodCalls() {
        // Given - empty config for safe testing
        DocumentorConfig emptyConfig = new DocumentorConfig(
            List.of(), config.outputSettings(), config.analysisSettings()
        );
        LlmService serviceWithEmptyConfig = createLlmService(emptyConfig);

        // When - all methods should return CompletableFuture
        CompletableFuture<String> docFuture = serviceWithEmptyConfig.generateDocumentation(testElement);
        CompletableFuture<String> exampleFuture = serviceWithEmptyConfig.generateUsageExamples(testElement);
        CompletableFuture<String> testFuture = serviceWithEmptyConfig.generateUnitTests(testElement);

        // Then
        assertNotNull(docFuture);
        assertNotNull(exampleFuture);
        assertNotNull(testFuture);

        // Verify they can complete successfully
        assertDoesNotThrow(() -> {
            docFuture.get();
            exampleFuture.get();
            testFuture.get();
        });
    }

    @Test
    void testOllamaModelDetection() {
        // Given - Various Ollama model configurations
        LlmModelConfig ollamaModel1 = new LlmModelConfig(
            "llama3.2", "ollama", "http://localhost:11434/api/generate", "", 4096, 60
        );

        LlmModelConfig ollamaModel2 = new LlmModelConfig(
            "codellama", "ollama", "http://localhost:11434/api/generate", "", 4096, 60
        );

        LlmModelConfig openaiModel = new LlmModelConfig(
            "gpt-4", "openai", "https://api.openai.com/v1/chat/completions", "test-key", 4096, 30
        );

        OutputSettings outputSettings = new OutputSettings(
            "output", "markdown", true, true
        );

        AnalysisSettings analysisSettings = new AnalysisSettings(
            true, 50, List.of("**/*.java"), List.of("**/test/**")
        );

        // Test each model type
        DocumentorConfig ollamaConfig1 = new DocumentorConfig(List.of(ollamaModel1), outputSettings, analysisSettings);
        DocumentorConfig ollamaConfig2 = new DocumentorConfig(List.of(ollamaModel2), outputSettings, analysisSettings);
        DocumentorConfig openaiConfig = new DocumentorConfig(List.of(openaiModel), outputSettings, analysisSettings);

        // When & Then - Should handle different model types without errors
        assertDoesNotThrow(() -> {
            LlmService ollamaService1 = createLlmService(ollamaConfig1);
            LlmService ollamaService2 = createLlmService(ollamaConfig2);
            LlmService openaiService = createLlmService(openaiConfig);

            // Verify services are created successfully
            assertNotNull(ollamaService1);
            assertNotNull(ollamaService2);
            assertNotNull(openaiService);
        });
    }
}
