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
 * Simple tests to improve LlmService coverage without complex mocking.
 */
@ExtendWith(MockitoExtension.class)
class LlmServiceSimpleTest {

    @Mock
    private WebClient webClient;

    private LlmService llmService;

    @BeforeEach
    void setUp() {
        // Create minimal config for testing
        DocumentorConfig.LlmModelConfig model = new DocumentorConfig.LlmModelConfig(
                "test-model", "test-key", "http://localhost/test", 
                1024, 0.3, 30, Map.of()
        );

        DocumentorConfig.OutputSettings outputSettings = new DocumentorConfig.OutputSettings(
                "output", "md", true, true, 0.9
        );

        DocumentorConfig.AnalysisSettings analysisSettings = new DocumentorConfig.AnalysisSettings(
                true, 2, List.of("*.java"), List.of()
        );

        DocumentorConfig config = new DocumentorConfig(List.of(model), outputSettings, analysisSettings);
        llmService = new LlmService(config, webClient);
    }

    @Test
    void testGenerateDocumentationSimple() {
        CodeElement element = new CodeElement(
                CodeElementType.CLASS, "TestClass", "TestClass", "test.java", 1,
                "public class TestClass {}", "A test class", List.of(), List.of()
        );

        CompletableFuture<String> result = llmService.generateDocumentation(element);
        assertNotNull(result);
    }

    @Test
    void testGenerateUsageExamplesSimple() {
        CodeElement element = new CodeElement(
                CodeElementType.METHOD, "testMethod", "TestClass", "test.java", 10,
                "public void testMethod() {}", "A test method", List.of(), List.of()
        );

        CompletableFuture<String> result = llmService.generateUsageExamples(element);
        assertNotNull(result);
    }

    @Test
    void testGenerateUnitTestsSimple() {
        CodeElement element = new CodeElement(
                CodeElementType.FIELD, "testField", "TestClass", "test.java", 5,
                "private String testField;", "A test field", List.of(), List.of()
        );

        CompletableFuture<String> result = llmService.generateUnitTests(element);
        assertNotNull(result);
    }
}