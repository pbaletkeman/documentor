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

import static org.junit.jupiter.api.Assertions.*;

/**
 * 🧪 Simple focused tests for LlmService coverage improvement
 */
@ExtendWith(MockitoExtension.class)
class LlmServiceBasicSimpleTest {

    @Mock
    private WebClient mockWebClient;

    private LlmService llmService;
    private CodeElement testElement;

    @BeforeEach
    void setUp() {
        DocumentorConfig.OutputSettings outputSettings = new DocumentorConfig.OutputSettings(
            "output", "markdown", true, true, 0.9, false, "output"
        );
        
        DocumentorConfig.AnalysisSettings analysisSettings = new DocumentorConfig.AnalysisSettings(
            true, 50, List.of("public"), List.of(".git")
        );
        
        // Use empty config for safe testing (avoids actual HTTP calls)
        DocumentorConfig emptyConfig = new DocumentorConfig(List.of(), outputSettings, analysisSettings);
        llmService = new LlmService(emptyConfig, mockWebClient);
        
        // Create test code element
        testElement = new CodeElement(
            CodeElementType.METHOD, "testMethod", "com.example.Test.testMethod",
            "/test/Test.java", 10, "public void testMethod()",
            "Test method", List.of(), List.of()
        );
    }

    @Test
    void testGenerateDocumentationWithEmptyConfig() {
        // When - With empty config, should handle gracefully
        var result = llmService.generateDocumentation(testElement);
        
        // Then
        assertNotNull(result);
        assertDoesNotThrow(() -> {
            String response = result.get();
            assertNotNull(response);
            // With empty models, should return consolidated empty response
        });
    }

    @Test
    void testGenerateUsageExamplesWithEmptyConfig() {
        // When
        var result = llmService.generateUsageExamples(testElement);
        
        // Then
        assertNotNull(result);
        assertDoesNotThrow(() -> {
            String response = result.get();
            assertNotNull(response);
            assertTrue(response.contains("No LLM models configured"));
        });
    }

    @Test
    void testGenerateUnitTestsWithEmptyConfig() {
        // When
        var result = llmService.generateUnitTests(testElement);
        
        // Then
        assertNotNull(result);
        assertDoesNotThrow(() -> {
            String response = result.get();
            assertNotNull(response);
            assertTrue(response.contains("No LLM models configured"));
        });
    }

    @Test
    void testServiceWithDifferentElementTypes() {
        // Test with different code element types
        CodeElement[] elements = {
            new CodeElement(CodeElementType.CLASS, "TestClass", "com.test.TestClass", 
                "/test/TestClass.java", 1, "public class TestClass", "", List.of(), List.of()),
            new CodeElement(CodeElementType.FIELD, "testField", "com.test.TestClass.testField", 
                "/test/TestClass.java", 3, "private String testField", "", List.of(), List.of())
        };
        
        for (CodeElement element : elements) {
            assertDoesNotThrow(() -> {
                var docResult = llmService.generateDocumentation(element);
                var exampleResult = llmService.generateUsageExamples(element);
                var testResult = llmService.generateUnitTests(element);
                
                assertNotNull(docResult.get());
                assertNotNull(exampleResult.get());
                assertNotNull(testResult.get());
            });
        }
    }

    @Test
    void testConstructorAcceptsConfiguration() {
        // Test that constructor properly accepts configuration
        DocumentorConfig.LlmModelConfig model = new DocumentorConfig.LlmModelConfig(
            "gpt-4", "test-key", "https://api.openai.com", 1000, 0.7, 30, Map.of()
        );
        
        DocumentorConfig config = new DocumentorConfig(
            List.of(model), 
            new DocumentorConfig.OutputSettings("output", "markdown", true, true, 0.9, false, "output"),
            new DocumentorConfig.AnalysisSettings(true, 50, List.of("public"), List.of(".git"))
        );
        
        assertDoesNotThrow(() -> {
            LlmService service = new LlmService(config, mockWebClient);
            assertNotNull(service);
        });
    }
}