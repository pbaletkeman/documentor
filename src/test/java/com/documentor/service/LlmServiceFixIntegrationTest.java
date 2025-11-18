package com.documentor.service;

import com.documentor.DocumentorTestApplication;
import com.documentor.config.DocumentorConfig;
import com.documentor.config.model.AnalysisSettings;
import com.documentor.config.model.LlmModelConfig;
import com.documentor.config.model.OutputSettings;
import com.documentor.model.CodeElement;
import com.documentor.model.CodeElementType;
import com.documentor.service.llm.LlmApiClient;
import com.documentor.service.llm.LlmRequestBuilder;
import com.documentor.service.llm.LlmResponseHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.when;

import com.documentor.config.TestConfig;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/**
 * Comprehensive integration test for the LlmService with
 * ThreadLocal configuration fix. This test verifies that the
 * LlmService works correctly with the ThreadLocal configuration
 * in multiple async operations.
 */
@SpringBootTest(classes = DocumentorTestApplication.class)
@ActiveProfiles("test")
@Import(TestConfig.class)
public final class LlmServiceFixIntegrationTest {

    @Mock
    private LlmRequestBuilder mockRequestBuilder;

    @Mock
    private LlmResponseHandler mockResponseHandler;

    @Mock
    private LlmApiClient mockApiClient;

    @InjectMocks
    private LlmServiceFix llmServiceFix;

    private LlmService llmService;
    private DocumentorConfig testConfig;

    // Test constants for magic numbers
    private static final int TEST_MAX_TOKENS = 2048;
    private static final int TEST_TIMEOUT_SECONDS = 60;
    private static final int TEST_MAX_DEPTH = 3;
    private static final int TEST_CLASS_LINE_NUMBER = 1;
    private static final int TEST_METHOD_LINE_NUMBER_1 = 10;
    private static final int TEST_METHOD_LINE_NUMBER_2 = 15;
    private static final int TEST_CLASS_LINE_NUMBER_2 = 1;
    private static final int TEST_METHOD_LINE_NUMBER_3 = 10;

    @BeforeEach
    public void setUp() {
        // Create a test configuration
        LlmModelConfig testModel = new LlmModelConfig(
                "test-model",
                "test-provider",
                "http://localhost:8080",
                "test-api-key",
                TEST_MAX_TOKENS,
                TEST_TIMEOUT_SECONDS);

        OutputSettings outputSettings = new OutputSettings(
                "./output",
                "markdown",
                true,
                true,
                true
        );

        AnalysisSettings analysisSettings = new AnalysisSettings(
                true,
                TEST_MAX_DEPTH,
                List.of("**/*.java"),
                List.of("**/test/**")
        );

        testConfig =
                new DocumentorConfig(List.of(testModel), outputSettings,
                analysisSettings);

        // Clear any previous ThreadLocal configuration
        LlmService.clearThreadLocalConfig();

        // Initialize the services
        llmService =
                new LlmService(testConfig, mockRequestBuilder,
                mockResponseHandler, mockApiClient);

        // Set up mock responses
        when(mockRequestBuilder.createDocumentationPrompt(any(
                CodeElement.class)))
                .thenReturn("Test documentation prompt");
        when(mockRequestBuilder.createUsageExamplePrompt(any(
                CodeElement.class)))
                .thenReturn("Test usage examples prompt");
        when(mockRequestBuilder.createUnitTestPrompt(any(
                CodeElement.class)))
                .thenReturn("Test unit tests prompt");

        when(mockRequestBuilder.buildRequestBody(any(
                LlmModelConfig.class), anyString()))
                .thenReturn(Map.of("prompt", "test prompt"));

        when(mockResponseHandler.getModelEndpoint(any(LlmModelConfig.class)))
                .thenReturn("/v1/completions");

        when(mockApiClient.callLlmModel(any(LlmModelConfig.class),
                anyString(), anyMap()))
                .thenReturn("Test LLM response");

        when(mockResponseHandler.extractResponseContent(anyString(),
                any(LlmModelConfig.class)))
                .thenReturn("Generated content");
    }

    /**
     * Test that verifies LlmService can generate documentation correctly
     * after the ThreadLocal configuration is set with LlmServiceFix.
     */
    @Test
    public void testGenerateDocumentationWithFix()
        throws ExecutionException, InterruptedException {

        // Use the fix to set the ThreadLocal configuration
        llmServiceFix.setLlmServiceThreadLocalConfig(testConfig);

        // Create a test code element
        CodeElement testElement = new CodeElement(
                CodeElementType.CLASS,     // type
                "TestClass",               // name
                "com.example.TestClass",   // qualifiedName
                "/test/TestClass.java",    // filePath
                1,                         // lineNumber
                "public class TestClass {}", // signature
                "",                        // documentation
                new ArrayList<>(),         // parameters
                new ArrayList<>()          // annotations
        );

        // Generate documentation
        CompletableFuture<String> docFuture =
                llmService.generateDocumentation(testElement);

        // Wait for the result
        String result = docFuture.get();

        // Verify the result
        assertNotNull(result);
        assertEquals("Generated content", result);
    }

    /**
     * Test that verifies LlmService can generate usage examples correctly
     * after the ThreadLocal configuration is set with LlmServiceFix.
     */
    @Test
    public void testGenerateUsageExamplesWithFix()
        throws ExecutionException, InterruptedException {
        // Use the fix to set the ThreadLocal configuration
        llmServiceFix.setLlmServiceThreadLocalConfig(testConfig);

        // Create a test code element
        CodeElement testElement = new CodeElement(
                CodeElementType.METHOD,    // type
                "testMethod",              // name
                "com.example.TestClass.testMethod", // qualifiedName
                "/test/TestClass.java",    // filePath
                TEST_METHOD_LINE_NUMBER_1,  // lineNumber
                "public void testMethod() {}", // signature
                "",                        // documentation
                new ArrayList<>(),         // parameters
                new ArrayList<>()          // annotations
        );

        // Generate usage examples
        CompletableFuture<String> examplesFuture =
        llmService.generateUsageExamples(testElement);

        // Wait for the result
        String result = examplesFuture.get();

        // Verify the result
        assertNotNull(result);
        assertEquals("Generated content", result);
    }

    /**
     * Test that verifies LlmService can generate unit tests correctly
     * after the ThreadLocal configuration is set with LlmServiceFix.
     */
    @Test
    public void testGenerateUnitTestsWithFix()
        throws ExecutionException, InterruptedException {
        // Use the fix to set the ThreadLocal configuration
        llmServiceFix.setLlmServiceThreadLocalConfig(testConfig);

        // Create a test code element with parameters
        List<String> parameters = new ArrayList<>();
        parameters.add("a: int");
        parameters.add("b: int");

        CodeElement testElement = new CodeElement(
                CodeElementType.METHOD,    // type
                "add",                     // name
                "com.example.TestClass.add", // qualifiedName
                "/test/TestClass.java",    // filePath
                TEST_METHOD_LINE_NUMBER_2,  // lineNumber
                "public int add(int a, int b) { return a + b; }", // signature
                "",                        // documentation
                parameters,                // parameters
                new ArrayList<>()          // annotations
        );

        // Generate unit tests
        CompletableFuture<String> testsFuture =
                llmService.generateUnitTests(testElement);

        // Wait for the result
        String result = testsFuture.get();

        // Verify the result
        assertNotNull(result);
        assertEquals("Generated content", result);
    }

    /**
     * Test that verifies LlmService can run multiple operations in parallel
     * with the ThreadLocal configuration set correctly.
     */
    @Test
    public void testParallelOperationsWithFix()
        throws ExecutionException, InterruptedException {
        // Use the fix to set the ThreadLocal configuration
        llmServiceFix.setLlmServiceThreadLocalConfig(testConfig);

        // Create test code elements
        CodeElement class1 = new CodeElement(
                CodeElementType.CLASS,     // type
                "Class1",                  // name
                "com.example.Class1",      // qualifiedName
                "/test/Class1.java",       // filePath
                1,                         // lineNumber
                "public class Class1 {}",  // signature
                "",                        // documentation
                new ArrayList<>(),         // parameters
                new ArrayList<>()          // annotations
        );

        CodeElement class2 = new CodeElement(
                CodeElementType.CLASS,     // type
                "Class2",                  // name
                "com.example.Class2",      // qualifiedName
                "/test/Class2.java",       // filePath
                1,                         // lineNumber
                "public class Class2 {}",  // signature
                "",                        // documentation
                new ArrayList<>(),         // parameters
                new ArrayList<>()          // annotations
        );

        CodeElement method1 = new CodeElement(
                CodeElementType.METHOD,    // type
                "method1",                 // name
                "com.example.Class1.method1", // qualifiedName
                "/test/Class1.java",       // filePath
                TEST_METHOD_LINE_NUMBER_3,  // lineNumber
                "public void method1() {}", // signature
                "",                        // documentation
                new ArrayList<>(),         // parameters
                new ArrayList<>()          // annotations
        );

        // Generate content in parallel
        CompletableFuture<String> docFuture =
                llmService.generateDocumentation(class1);
        CompletableFuture<String> examplesFuture =
                llmService.generateUsageExamples(class2);
        CompletableFuture<String> testsFuture =
                llmService.generateUnitTests(method1);

        // Wait for all operations to complete
        CompletableFuture.allOf(docFuture, examplesFuture, testsFuture).get();

        // Verify the results
        assertEquals("Generated content", docFuture.get());
        assertEquals("Generated content", examplesFuture.get());
        assertEquals("Generated content", testsFuture.get());
    }

    /**
     * Diagnostic test to verify the isThreadLocalConfigAvailable method
     * correctly reports availability.
     */
    @Test
    public void testThreadLocalConfigAvailability() {
        // Initially clear the ThreadLocal
        LlmService.clearThreadLocalConfig();

        // Check availability (should be false)
        boolean initialAvailability = llmServiceFix
                .isThreadLocalConfigAvailable();
        assertEquals(false, initialAvailability,
                "Config should not be available initially");

        // Set the config
        llmServiceFix.setLlmServiceThreadLocalConfig(testConfig);

        // Check availability again (should be true)
        boolean afterSettingAvailability = llmServiceFix
                .isThreadLocalConfigAvailable();
        assertEquals(true, afterSettingAvailability,
                "Config should be available after setting");

        // Clear again
        LlmService.clearThreadLocalConfig();

        // Check final availability (should be false again)
        boolean finalAvailability =
                llmServiceFix.isThreadLocalConfigAvailable();
        assertEquals(false, finalAvailability,
                "Config should not be available after clearing");
    }
}
