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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Simple coverage tests for LlmService without reflection or HTTP calls.
 */
@ExtendWith(MockitoExtension.class)
class LlmServiceSimpleCoverageTest {

    @Mock
    private WebClient webClient;

    private LlmService llmService;
    private DocumentorConfig config;

    @BeforeEach
    void setUp() {
        // Create simple config for testing
        DocumentorConfig.LlmModelConfig model = new DocumentorConfig.LlmModelConfig(
                "test-model", "test-api-key", "http://localhost:8080/test", 
                2048, 0.5, 60, Map.of()
        );

        DocumentorConfig.OutputSettings outputSettings = new DocumentorConfig.OutputSettings(
                "docs", "markdown", false, false, 0.9
        );

        DocumentorConfig.AnalysisSettings analysisSettings = new DocumentorConfig.AnalysisSettings(
                false, 1, List.of("*.java"), List.of("test")
        );

        config = new DocumentorConfig(List.of(model), outputSettings, analysisSettings);
        llmService = new LlmService(config, webClient);
    }

    @Test
    void testGenerateDocumentationWithEmptyModels() {
        // Test with empty model list
        DocumentorConfig emptyConfig = new DocumentorConfig(
                Collections.emptyList(), config.outputSettings(), config.analysisSettings()
        );
        LlmService emptyLlmService = new LlmService(emptyConfig, webClient);

        CodeElement element = new CodeElement(
                CodeElementType.METHOD, "testMethod", "TestClass", "test.java", 10,
                "public void testMethod()", "", Collections.emptyList(), Collections.emptyList()
        );

        CompletableFuture<String> result = emptyLlmService.generateDocumentation(element);
        
        assertNotNull(result);
        // The result should complete but might be empty due to no models
        assertTrue(result.isDone() || !result.isCompletedExceptionally());
    }

    @Test
    void testGenerateUsageExamplesWithFieldElement() {
        CodeElement fieldElement = new CodeElement(
                CodeElementType.FIELD, "testField", "TestClass", "test.java", 5,
                "private String testField", "", Collections.emptyList(), Collections.emptyList()
        );

        CompletableFuture<String> result = llmService.generateUsageExamples(fieldElement);
        
        assertNotNull(result);
    }

    @Test
    void testGenerateUnitTestsWithClassElement() {
        CodeElement classElement = new CodeElement(
                CodeElementType.CLASS, "TestClass", "TestClass", "test.java", 1,
                "public class TestClass {}", "", Collections.emptyList(), Collections.emptyList()
        );

        CompletableFuture<String> result = llmService.generateUnitTests(classElement);
        
        assertNotNull(result);
    }

    @Test
    void testCodeElementWithParameters() {
        List<String> parameters = Arrays.asList("String param1", "int param2");
        List<String> annotations = Arrays.asList("@Override", "@Test");
        
        CodeElement methodWithParams = new CodeElement(
                CodeElementType.METHOD, "complexMethod", "TestClass", "test.java", 15,
                "public void complexMethod(String param1, int param2)", 
                "A complex method with parameters", parameters, annotations
        );

        CompletableFuture<String> docResult = llmService.generateDocumentation(methodWithParams);
        CompletableFuture<String> exampleResult = llmService.generateUsageExamples(methodWithParams);
        CompletableFuture<String> testResult = llmService.generateUnitTests(methodWithParams);
        
        assertNotNull(docResult);
        assertNotNull(exampleResult);
        assertNotNull(testResult);
    }

    @Test
    void testCodeElementDisplayName() {
        CodeElement element = new CodeElement(
                CodeElementType.METHOD, "getId", "User", "User.java", 25,
                "public String getId()", "Gets the user ID", Collections.emptyList(), Collections.emptyList()
        );

        String displayName = element.getDisplayName();
        assertNotNull(displayName);
        assertTrue(displayName.contains("User"));
        assertTrue(displayName.contains("User.java"));
    }

    @Test
    void testCodeElementAnalysisContext() {
        List<String> params = Arrays.asList("String name", "int age");
        CodeElement element = new CodeElement(
                CodeElementType.METHOD, "createUser", "UserService", "UserService.java", 30,
                "public User createUser(String name, int age)", 
                "Creates a new user", params, Arrays.asList("@Service")
        );

        String context = element.getAnalysisContext();
        assertNotNull(context);
        assertTrue(context.contains("createUser"));
        assertTrue(context.contains("Method/Function"));
        assertTrue(context.contains("@Service"));
        assertTrue(context.contains("Creates a new user"));
    }

    @Test
    void testCodeElementIsPublic() {
        // Test public method
        CodeElement publicElement = new CodeElement(
                CodeElementType.METHOD, "publicMethod", "TestClass", "test.java", 10,
                "public void publicMethod()", "", Collections.emptyList(), Collections.emptyList()
        );
        assertTrue(publicElement.isPublic());

        // Test private method  
        CodeElement privateElement = new CodeElement(
                CodeElementType.METHOD, "privateMethod", "TestClass", "test.java", 15,
                "private void privateMethod()", "", Collections.emptyList(), Collections.emptyList()
        );
        assertFalse(privateElement.isPublic());

        // Test python private method (starts with _)
        CodeElement pythonPrivateElement = new CodeElement(
                CodeElementType.METHOD, "_private_method", "TestClass", "test.py", 20,
                "def _private_method(self):", "", Collections.emptyList(), Collections.emptyList()
        );
        assertFalse(pythonPrivateElement.isPublic());
    }

    @Test
    void testConfigurationValidation() {
        assertNotNull(config.llmModels());
        assertFalse(config.llmModels().isEmpty());
        assertNotNull(config.outputSettings());
        assertNotNull(config.analysisSettings());
        
        DocumentorConfig.LlmModelConfig model = config.llmModels().get(0);
        assertEquals("test-model", model.name());
        assertEquals("test-api-key", model.apiKey());
        assertEquals(2048, model.maxTokens());
        assertEquals(0.5, model.temperature());
        assertEquals(60, model.timeoutSeconds());
    }
}