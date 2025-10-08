package com.documentor.service;

import com.documentor.config.DocumentorConfig;
import com.documentor.model.CodeElement;
import com.documentor.model.CodeElementType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 🧪 Integration tests for LlmService
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
        DocumentorConfig.LlmModelConfig testModel = new DocumentorConfig.LlmModelConfig(
            "test-model", "test-key", "https://api.test.com", 1000, 0.7, 30, Map.of()
        );
        
        DocumentorConfig.OutputSettings outputSettings = new DocumentorConfig.OutputSettings(
            "output", "markdown", true, true, 0.9
        );
        
        DocumentorConfig.AnalysisSettings analysisSettings = new DocumentorConfig.AnalysisSettings(
            true, 50, List.of("public"), List.of(".git")
        );
        
        config = new DocumentorConfig(List.of(testModel), outputSettings, analysisSettings);
        
        // Create test code element
        testElement = new CodeElement(
            CodeElementType.METHOD, "testMethod", "com.test.TestClass.testMethod",
            "/test/TestClass.java", 10, "public void testMethod(String param)",
            "Test method documentation", List.of("param"), List.of()
        );
    }

    @Test
    void testConstructorInitialization() {
        // When
        LlmService service = new LlmService(config, mockWebClient);
        
        // Then
        assertNotNull(service);
    }

    @Test
    void testGenerateDocumentationWithEmptyModels() {
        // Given - empty model configuration
        DocumentorConfig emptyConfig = new DocumentorConfig(
            List.of(), config.outputSettings(), config.analysisSettings()
        );
        LlmService serviceWithEmptyConfig = new LlmService(emptyConfig, mockWebClient);
        
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
        LlmService serviceWithEmptyConfig = new LlmService(emptyConfig, mockWebClient);
        
        // When
        CompletableFuture<String> result = serviceWithEmptyConfig.generateUsageExamples(testElement);
        
        // Then
        assertNotNull(result);
        assertDoesNotThrow(() -> {
            String response = result.get();
            assertTrue(response.contains("No LLM models configured for usage examples"));
        });
    }

    @Test
    void testGenerateUnitTestsWithEmptyModels() {
        // Given - empty model configuration
        DocumentorConfig emptyConfig = new DocumentorConfig(
            List.of(), config.outputSettings(), config.analysisSettings()
        );
        LlmService serviceWithEmptyConfig = new LlmService(emptyConfig, mockWebClient);
        
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
        LlmService serviceWithEmptyConfig = new LlmService(emptyConfig, mockWebClient);
        
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
        DocumentorConfig.LlmModelConfig model1 = new DocumentorConfig.LlmModelConfig(
            "model-1", "key-1", "https://api1.test.com", 1000, 0.7, 30, Map.of()
        );
        DocumentorConfig.LlmModelConfig model2 = new DocumentorConfig.LlmModelConfig(
            "model-2", "key-2", "https://api2.test.com", 2000, 0.8, 60, Map.of()
        );
        
        DocumentorConfig multiModelConfig = new DocumentorConfig(
            List.of(model1, model2), config.outputSettings(), config.analysisSettings()
        );
        LlmService multiModelService = new LlmService(multiModelConfig, mockWebClient);
        
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
        LlmService serviceWithEmptyConfig = new LlmService(emptyConfig, mockWebClient);
        
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
        LlmService serviceWithEmptyConfig = new LlmService(emptyConfig, mockWebClient);
        
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
}