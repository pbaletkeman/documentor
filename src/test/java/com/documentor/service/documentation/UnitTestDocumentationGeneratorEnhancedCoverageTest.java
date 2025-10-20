package com.documentor.service.documentation;

import com.documentor.config.DocumentorConfig;
import com.documentor.config.model.OutputSettings;
import com.documentor.model.CodeElement;
import com.documentor.model.CodeElementType;
import com.documentor.model.ProjectAnalysis;
import com.documentor.service.LlmServiceEnhanced;
import com.documentor.service.LlmServiceFixEnhanced;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Enhanced coverage tests for UnitTestDocumentationGeneratorEnhanced to achieve >70% branch coverage.
 */
@ExtendWith(MockitoExtension.class)
class UnitTestDocumentationGeneratorEnhancedCoverageTest {

    @Mock
    private LlmServiceEnhanced llmService;

    @Mock
    private DocumentorConfig config;

    @Mock
    private OutputSettings outputSettings;

    @Mock
    private LlmServiceFixEnhanced llmServiceFix;

    private UnitTestDocumentationGeneratorEnhanced generator;

    @TempDir
    private Path tempDir;

    private static final String TEST_UNIT_TESTS = "Test unit test content";

    @BeforeEach
    void setUp() {
        // Use lenient() to avoid UnnecessaryStubbingException
        lenient().when(config.outputSettings()).thenReturn(outputSettings);
        lenient().when(outputSettings.includeIcons()).thenReturn(true);
        lenient().when(outputSettings.targetCoverage()).thenReturn(0.8);
        lenient().when(llmServiceFix.isThreadLocalConfigAvailable()).thenReturn(true);
        lenient().when(llmService.generateUnitTests(any())).thenReturn(CompletableFuture.completedFuture(TEST_UNIT_TESTS));

        generator = new UnitTestDocumentationGeneratorEnhanced(llmService, config, llmServiceFix);
    }

    @Test
    void testGenerateUnitTestDocumentationWithNullConfig() {
        UnitTestDocumentationGeneratorEnhanced generatorWithNullConfig =
            new UnitTestDocumentationGeneratorEnhanced(llmService, null, llmServiceFix);

        ProjectAnalysis analysis = createTestProjectAnalysis();

        CompletableFuture<Void> result = generatorWithNullConfig.generateUnitTestDocumentation(analysis, tempDir);

        // Should complete without error even with null config
        assertDoesNotThrow(() -> result.join());

        // Verify the tests directory was created
        Path testsDir = tempDir.resolve("tests");
        assertTrue(Files.exists(testsDir));
    }

    @Test
    void testGenerateUnitTestDocumentationWithConfigSetFails() {
        when(llmServiceFix.isThreadLocalConfigAvailable()).thenReturn(false); // Config not available after set

        ProjectAnalysis analysis = createTestProjectAnalysis();

        CompletableFuture<Void> result = generator.generateUnitTestDocumentation(analysis, tempDir);

        // Should complete with warning but not fail
        assertDoesNotThrow(() -> result.join());

        // Verify the tests directory was created
        Path testsDir = tempDir.resolve("tests");
        assertTrue(Files.exists(testsDir));
    }

    @Test
    void testGenerateUnitTestDocumentationWithIOExceptionCreatingDirectory() throws IOException {
        // Create a file where the tests directory should be to cause IOException
        Path testsPath = tempDir.resolve("tests");
        Files.createFile(testsPath);

        ProjectAnalysis analysis = createTestProjectAnalysis();

        CompletableFuture<Void> result = generator.generateUnitTestDocumentation(analysis, tempDir);

        // Should handle IOException gracefully
        assertDoesNotThrow(() -> result.join());
    }

    @Test
    void testGenerateUnitTestDocumentationWithElementGenerationException() {
        when(llmService.generateUnitTests(any())).thenThrow(new RuntimeException("LLM service error"));

        ProjectAnalysis analysis = createTestProjectAnalysis();

        CompletableFuture<Void> result = generator.generateUnitTestDocumentation(analysis, tempDir);

        // Should handle element generation exceptions gracefully
        assertDoesNotThrow(() -> result.join());

        // Verify the tests directory was created
        Path testsDir = tempDir.resolve("tests");
        assertTrue(Files.exists(testsDir));

        // Verify the file was created even with errors
        Path testFile = testsDir.resolve("unit-tests.md");
        assertTrue(Files.exists(testFile));
    }

    @Test
    void testGenerateUnitTestDocumentationWithFutureCompletionException() {
        // Create a future that will fail
        CompletableFuture<String> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new RuntimeException("Future completion error"));
        when(llmService.generateUnitTests(any())).thenReturn(failedFuture);

        ProjectAnalysis analysis = createTestProjectAnalysis();

        CompletableFuture<Void> result = generator.generateUnitTestDocumentation(analysis, tempDir);

        // Should handle future completion exceptions gracefully
        assertDoesNotThrow(() -> result.join());

        // Just verify it doesn't crash - the implementation may or may not create files with exceptions
    }

    @Test
    void testGenerateUnitTestDocumentationWithNullElements() {
        when(config.outputSettings()).thenReturn(outputSettings);
        when(outputSettings.includeIcons()).thenReturn(true);
        when(outputSettings.targetCoverage()).thenReturn(0.8);
        when(llmServiceFix.isThreadLocalConfigAvailable()).thenReturn(true);

        // Create analysis with null elements mixed in
        CodeElement validElement = createTestMethodElement();
        List<CodeElement> elementsWithNull = Arrays.asList(validElement, null);
        ProjectAnalysis analysisWithNulls = new ProjectAnalysis("/test/path", elementsWithNull, System.currentTimeMillis());

        when(llmService.generateUnitTests(validElement)).thenReturn(CompletableFuture.completedFuture(TEST_UNIT_TESTS));

        CompletableFuture<Void> result = generator.generateUnitTestDocumentation(analysisWithNulls, tempDir);

        // Should filter out null elements and complete successfully
        assertDoesNotThrow(() -> result.join());

        // Verify the tests directory was created
        Path testsDir = tempDir.resolve("tests");
        assertTrue(Files.exists(testsDir));

        // Verify the file was created
        Path testFile = testsDir.resolve("unit-tests.md");
        assertTrue(Files.exists(testFile));
    }

    @Test
    void testGenerateUnitTestDocumentationWithFieldElements() {
        when(config.outputSettings()).thenReturn(outputSettings);
        when(outputSettings.includeIcons()).thenReturn(true);
        when(outputSettings.targetCoverage()).thenReturn(0.8);
        when(llmServiceFix.isThreadLocalConfigAvailable()).thenReturn(true);

        // Create analysis with field elements (should be filtered out)
        CodeElement fieldElement = createTestFieldElement();
        CodeElement methodElement = createTestMethodElement();
        List<CodeElement> elements = Arrays.asList(fieldElement, methodElement);
        ProjectAnalysis analysis = new ProjectAnalysis("/test/path", elements, System.currentTimeMillis());

        when(llmService.generateUnitTests(methodElement)).thenReturn(CompletableFuture.completedFuture(TEST_UNIT_TESTS));

        CompletableFuture<Void> result = generator.generateUnitTestDocumentation(analysis, tempDir);

        // Should filter out field elements and complete successfully
        assertDoesNotThrow(() -> result.join());

        // Verify the tests directory was created
        Path testsDir = tempDir.resolve("tests");
        assertTrue(Files.exists(testsDir));

        // Verify the file was created
        Path testFile = testsDir.resolve("unit-tests.md");
        assertTrue(Files.exists(testFile));

        // Verify field element was not processed (no generateUnitTests call for field)
        verify(llmService, never()).generateUnitTests(fieldElement);
        verify(llmService).generateUnitTests(methodElement);
    }

    @Test
    void testGenerateUnitTestDocumentationWithElementsWithNullType() {
        when(config.outputSettings()).thenReturn(outputSettings);
        when(outputSettings.includeIcons()).thenReturn(true);
        when(outputSettings.targetCoverage()).thenReturn(0.8);
        when(llmServiceFix.isThreadLocalConfigAvailable()).thenReturn(true);

        // Create element with null type
        CodeElement elementWithNullType = new CodeElement(
            null, // null type
            "testElement",
            "com.example.TestClass.testElement",
            "/test/TestClass.java",
            5,
            "public void testElement() {}",
            "An element with null type",
            Collections.emptyList(),
            Collections.emptyList()
        );

        CodeElement validElement = createTestMethodElement();
        List<CodeElement> elements = Arrays.asList(elementWithNullType, validElement);
        ProjectAnalysis analysis = new ProjectAnalysis("/test/path", elements, System.currentTimeMillis());

        when(llmService.generateUnitTests(validElement)).thenReturn(CompletableFuture.completedFuture(TEST_UNIT_TESTS));

        CompletableFuture<Void> result = generator.generateUnitTestDocumentation(analysis, tempDir);

        // Should filter out elements with null type and complete successfully
        assertDoesNotThrow(() -> result.join());

        // Verify only valid element was processed
        verify(llmService, never()).generateUnitTests(elementWithNullType);
        verify(llmService).generateUnitTests(validElement);
    }

    @Test
    void testGenerateUnitTestDocumentationSuccessful() {
        ProjectAnalysis analysis = createTestProjectAnalysis();

        CompletableFuture<Void> result = generator.generateUnitTestDocumentation(analysis, tempDir);

        // Should complete successfully
        assertDoesNotThrow(() -> result.join());

        // Verify the tests directory was created
        Path testsDir = tempDir.resolve("tests");
        assertTrue(Files.exists(testsDir));

        // Verify the file was created
        Path testFile = testsDir.resolve("unit-tests.md");
        assertTrue(Files.exists(testFile));
    }

    @Test
    void testGenerateUnitTestDocumentationWithFileWriteException() throws IOException {
        when(config.outputSettings()).thenReturn(outputSettings);
        when(outputSettings.includeIcons()).thenReturn(true);
        when(outputSettings.targetCoverage()).thenReturn(0.8);
        when(llmServiceFix.isThreadLocalConfigAvailable()).thenReturn(true);
        when(llmService.generateUnitTests(any())).thenReturn(CompletableFuture.completedFuture(TEST_UNIT_TESTS));

        // Create a read-only directory to simulate file write failure
        Path testsDir = tempDir.resolve("tests");
        Files.createDirectories(testsDir);
        Path testFile = testsDir.resolve("unit-tests.md");
        Files.createFile(testFile);
        testFile.toFile().setReadOnly();

        ProjectAnalysis analysis = createTestProjectAnalysis();

        CompletableFuture<Void> result = generator.generateUnitTestDocumentation(analysis, tempDir);

        // Should handle file write exceptions gracefully
        assertDoesNotThrow(() -> result.join());
    }

    @Test
    void testGenerateUnitTestDocumentationWithNullUnitTestResult() {
        when(config.outputSettings()).thenReturn(outputSettings);
        when(outputSettings.includeIcons()).thenReturn(true);
        when(outputSettings.targetCoverage()).thenReturn(0.8);
        when(llmServiceFix.isThreadLocalConfigAvailable()).thenReturn(true);
        when(llmService.generateUnitTests(any())).thenReturn(CompletableFuture.completedFuture(null));

        ProjectAnalysis analysis = createTestProjectAnalysis();

        CompletableFuture<Void> result = generator.generateUnitTestDocumentation(analysis, tempDir);

        // Should handle null unit test results gracefully
        assertDoesNotThrow(() -> result.join());

        // Verify the tests directory was created
        Path testsDir = tempDir.resolve("tests");
        assertTrue(Files.exists(testsDir));

        // Verify the file was created
        Path testFile = testsDir.resolve("unit-tests.md");
        assertTrue(Files.exists(testFile));
    }

    @Test
    void testGenerateUnitTestDocumentationWithoutIconsConfig() {
        when(config.outputSettings()).thenReturn(outputSettings);
        when(outputSettings.includeIcons()).thenReturn(false); // No icons
        when(outputSettings.targetCoverage()).thenReturn(0.8);
        when(llmServiceFix.isThreadLocalConfigAvailable()).thenReturn(true);
        when(llmService.generateUnitTests(any())).thenReturn(CompletableFuture.completedFuture(TEST_UNIT_TESTS));

        ProjectAnalysis analysis = createTestProjectAnalysis();

        CompletableFuture<Void> result = generator.generateUnitTestDocumentation(analysis, tempDir);

        // Should complete successfully without icons
        assertDoesNotThrow(() -> result.join());

        // Verify the tests directory was created
        Path testsDir = tempDir.resolve("tests");
        assertTrue(Files.exists(testsDir));

        // Verify the file was created
        Path testFile = testsDir.resolve("unit-tests.md");
        assertTrue(Files.exists(testFile));
    }

    @Test
    void testGenerateUnitTestDocumentationWithNullOutputSettings() {
        when(config.outputSettings()).thenReturn(null); // Null output settings
        when(llmServiceFix.isThreadLocalConfigAvailable()).thenReturn(true);
        when(llmService.generateUnitTests(any())).thenReturn(CompletableFuture.completedFuture(TEST_UNIT_TESTS));

        ProjectAnalysis analysis = createTestProjectAnalysis();

        CompletableFuture<Void> result = generator.generateUnitTestDocumentation(analysis, tempDir);

        // Should handle null output settings gracefully
        assertDoesNotThrow(() -> result.join());

        // Verify the tests directory was created
        Path testsDir = tempDir.resolve("tests");
        assertTrue(Files.exists(testsDir));

        // Verify the file was created
        Path testFile = testsDir.resolve("unit-tests.md");
        assertTrue(Files.exists(testFile));
    }

    @Test
    void testGenerateUnitTestDocumentationWithHeaderCreationException() {
        // Create a mock config that will throw exception when accessing properties
        DocumentorConfig faultyConfig = org.mockito.Mockito.mock(DocumentorConfig.class);
        when(faultyConfig.outputSettings()).thenThrow(new RuntimeException("Config access error"));

        UnitTestDocumentationGeneratorEnhanced generatorWithFaultyConfig =
            new UnitTestDocumentationGeneratorEnhanced(llmService, faultyConfig, llmServiceFix);

        when(llmServiceFix.isThreadLocalConfigAvailable()).thenReturn(true);
        when(llmService.generateUnitTests(any())).thenReturn(CompletableFuture.completedFuture(TEST_UNIT_TESTS));

        ProjectAnalysis analysis = createTestProjectAnalysis();

        CompletableFuture<Void> result = generatorWithFaultyConfig.generateUnitTestDocumentation(analysis, tempDir);

        // Should handle header creation exceptions gracefully
        assertDoesNotThrow(() -> result.join());

        // Verify the tests directory was created
        Path testsDir = tempDir.resolve("tests");
        assertTrue(Files.exists(testsDir));

        // Verify the file was created with fallback header
        Path testFile = testsDir.resolve("unit-tests.md");
        assertTrue(Files.exists(testFile));
    }

    @Test
    void testGenerateUnitTestDocumentationWithTopLevelException() {
        // Override the lenient stub to throw an exception
        when(llmService.generateUnitTests(any())).thenThrow(new RuntimeException("Top level error"));

        ProjectAnalysis analysis = createTestProjectAnalysis();

        CompletableFuture<Void> result = generator.generateUnitTestDocumentation(analysis, tempDir);

        // Should handle top-level exceptions gracefully
        assertDoesNotThrow(() -> result.join());
    }

    @Test
    void testGenerateUnitTestDocumentationWithCleanupCall() {
        when(config.outputSettings()).thenReturn(outputSettings);
        when(outputSettings.includeIcons()).thenReturn(true);
        when(outputSettings.targetCoverage()).thenReturn(0.8);
        when(llmServiceFix.isThreadLocalConfigAvailable()).thenReturn(true);
        when(llmService.generateUnitTests(any())).thenReturn(CompletableFuture.completedFuture(TEST_UNIT_TESTS));

        ProjectAnalysis analysis = createTestProjectAnalysis();

        CompletableFuture<Void> result = generator.generateUnitTestDocumentation(analysis, tempDir);

        assertDoesNotThrow(() -> result.join());

        // Verify cleanup was called
        verify(llmServiceFix).cleanupThreadLocalConfig();
    }

    // Helper methods
    private ProjectAnalysis createTestProjectAnalysis() {
        CodeElement element = createTestMethodElement();
        List<CodeElement> elements = Collections.singletonList(element);
        return new ProjectAnalysis("/test/path", elements, System.currentTimeMillis());
    }

    private CodeElement createTestMethodElement() {
        return new CodeElement(
            CodeElementType.METHOD,
            "testMethod",
            "com.example.TestClass.testMethod",
            "/test/TestClass.java",
            5,
            "public void testMethod() {}",
            "A test method",
            Collections.emptyList(),
            Collections.emptyList()
        );
    }

    private CodeElement createTestFieldElement() {
        return new CodeElement(
            CodeElementType.FIELD,
            "testField",
            "com.example.TestClass.testField",
            "/test/TestClass.java",
            3,
            "private String testField;",
            "A test field",
            Collections.emptyList(),
            Collections.emptyList()
        );
    }
}
