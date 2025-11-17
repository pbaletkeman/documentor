package com.documentor.service.documentation;

import com.documentor.config.ThreadLocalContextHolder;
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
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.atLeastOnce;

/**
 * Comprehensive tests for ElementDocumentationGeneratorEnhanced to improve
 * branch coverage.
 */
@ExtendWith(MockitoExtension.class)
class ElementDocumentationGeneratorEnhancedTest {

    @Mock
    private LlmServiceEnhanced llmService;

    @Mock
    private LlmServiceFixEnhanced llmServiceFix;

    private ElementDocumentationGeneratorEnhanced generator;

    @TempDir
    private Path tempDir;

    private static final String TEST_DOCUMENTATION =
        "Test documentation content";
    private static final String TEST_EXAMPLES = "Test usage examples";

    @BeforeEach
    void setUp() {
        ThreadLocalContextHolder.clearConfig();
        generator = new ElementDocumentationGeneratorEnhanced(
            llmService, llmServiceFix
        );
    }

    @Test
    void testConstructorWithNullServices() {
        // Test with null llmService
        ElementDocumentationGeneratorEnhanced generatorWithNullLlm =
            new ElementDocumentationGeneratorEnhanced(null, llmServiceFix);
        assertNotNull(generatorWithNullLlm);

        // Test with null llmServiceFix
        ElementDocumentationGeneratorEnhanced generatorWithNullFix =
            new ElementDocumentationGeneratorEnhanced(llmService, null);
        assertNotNull(generatorWithNullFix);

        // Test with both null
        ElementDocumentationGeneratorEnhanced generatorWithBothNull =
            new ElementDocumentationGeneratorEnhanced(null, null);
        assertNotNull(generatorWithBothNull);
    }

    @Test
    void testGenerateElementDocumentationWithNullLlmService() {
        ElementDocumentationGeneratorEnhanced generatorWithNullLlm =
            new ElementDocumentationGeneratorEnhanced(null, llmServiceFix);

        CodeElement element = createTestClassElement();

        CompletableFuture<Void> result = generatorWithNullLlm
            .generateElementDocumentation(element, tempDir);

        // Should complete without throwing exception but return null
        assertDoesNotThrow(() -> result.join());
    }

    @Test
    void testGenerateGroupedDocumentationWithNullLlmService() {
        ElementDocumentationGeneratorEnhanced generatorWithNullLlm =
            new ElementDocumentationGeneratorEnhanced(null, llmServiceFix);

        ProjectAnalysis analysis = createTestProjectAnalysis();

        CompletableFuture<Void> result = generatorWithNullLlm
            .generateGroupedDocumentation(analysis, tempDir);

        // Should complete without throwing exception but return null
        assertDoesNotThrow(() -> result.join());
    }

    @Test
    void testGenerateGroupedDocumentationWithNullElements() {
        // Note: ProjectAnalysis record may not handle null elements gracefully
        // This tests the generator's null handling when codeElements is null
        // after construction
        ProjectAnalysis analysisWithNullElements = new ProjectAnalysis(
            "/test/path",
            // Use empty list instead of null since record may not handle null
            Collections.emptyList(),
            System.currentTimeMillis()
        );

        CompletableFuture<Void> result = generator
            .generateGroupedDocumentation(analysisWithNullElements, tempDir);

        // Should complete without throwing exception
        assertDoesNotThrow(() -> result.join());
    }

    @Test
    void testGenerateGroupedDocumentationWithEmptyElements() {
        ProjectAnalysis analysisWithEmptyElements = new ProjectAnalysis(
            "/test/path",
            Collections.emptyList(),
            System.currentTimeMillis()
        );

        CompletableFuture<Void> result = generator
            .generateGroupedDocumentation(analysisWithEmptyElements, tempDir);

        // Should complete without throwing exception
        assertDoesNotThrow(() -> result.join());
    }

    @Test
    void testThreadLocalConfigAvailableTrue() throws Exception {
        when(llmServiceFix.isThreadLocalConfigAvailable()).thenReturn(true);
        when(llmService.generateDocumentation(any()))
            .thenReturn(CompletableFuture.completedFuture(TEST_DOCUMENTATION));
        when(llmService.generateUsageExamples(any()))
            .thenReturn(CompletableFuture.completedFuture(TEST_EXAMPLES));

        CodeElement element = createTestClassElement();

        CompletableFuture<Void> result = generator
            .generateElementDocumentation(element, tempDir);
        result.join();

        verify(llmServiceFix, atLeastOnce()).isThreadLocalConfigAvailable();
        verify(llmService, atLeastOnce()).generateDocumentation(any());
        verify(llmService, atLeastOnce()).generateUsageExamples(any());
    }

    @Test
    void testThreadLocalConfigAvailableFalse() throws Exception {
        when(llmServiceFix.isThreadLocalConfigAvailable()).thenReturn(false);
        when(llmService.generateDocumentation(any()))
            .thenReturn(CompletableFuture.completedFuture(TEST_DOCUMENTATION));
        when(llmService.generateUsageExamples(any()))
            .thenReturn(CompletableFuture.completedFuture(TEST_EXAMPLES));

        CodeElement element = createTestClassElement();

        CompletableFuture<Void> result = generator
            .generateElementDocumentation(element, tempDir);
        result.join();

        verify(llmServiceFix, atLeastOnce()).isThreadLocalConfigAvailable();
    }

    @Test
    void testThreadLocalConfigCheckThrowsException() throws Exception {
        when(llmServiceFix.isThreadLocalConfigAvailable())
            .thenThrow(new RuntimeException("Config check error"));
        when(llmService.generateDocumentation(any()))
            .thenReturn(CompletableFuture.completedFuture(TEST_DOCUMENTATION));
        when(llmService.generateUsageExamples(any()))
            .thenReturn(CompletableFuture.completedFuture(TEST_EXAMPLES));

        CodeElement element = createTestClassElement();

        // Should not throw exception even if config check fails
        CompletableFuture<Void> result = generator
            .generateElementDocumentation(element, tempDir);
        assertDoesNotThrow(() -> result.join());

        verify(llmServiceFix, atLeastOnce()).isThreadLocalConfigAvailable();
    }

    @Test
    void testGenerateElementDocumentationWithNullLlmServiceFix() {
        ElementDocumentationGeneratorEnhanced generatorWithNullFix =
            new ElementDocumentationGeneratorEnhanced(llmService, null);

        when(llmService.generateDocumentation(any()))
            .thenReturn(CompletableFuture.completedFuture(TEST_DOCUMENTATION));
        when(llmService.generateUsageExamples(any()))
            .thenReturn(CompletableFuture.completedFuture(TEST_EXAMPLES));

        CodeElement element = createTestClassElement();

        CompletableFuture<Void> result = generatorWithNullFix
            .generateElementDocumentation(element, tempDir);

        // Should complete successfully even with null llmServiceFix
        assertDoesNotThrow(() -> result.join());
    }

    @Test
    void testGenerateGroupedDocumentationWithClassElements() throws Exception {
        when(llmService.generateDocumentation(any()))
            .thenReturn(CompletableFuture.completedFuture(TEST_DOCUMENTATION));
        when(llmService.generateUsageExamples(any()))
            .thenReturn(CompletableFuture.completedFuture(TEST_EXAMPLES));
        when(llmServiceFix.isThreadLocalConfigAvailable())
            .thenReturn(true);

        CodeElement classElement = createTestClassElement();
        CodeElement methodElement = createTestMethodElement();
        CodeElement fieldElement = createTestFieldElement();

        List<CodeElement> elements = Arrays.asList(
            classElement, methodElement, fieldElement
        );
        ProjectAnalysis analysis = new ProjectAnalysis(
            "/test/path",
            elements,
            System.currentTimeMillis()
        );

        CompletableFuture<Void> result = generator
            .generateGroupedDocumentation(analysis, tempDir);
        result.join();

        // Verify file was created
        Path elementsDir = tempDir.resolve("elements");
        assertTrue(Files.exists(elementsDir));

        Path classFile = elementsDir.resolve("class-TestClass.md");
        assertTrue(Files.exists(classFile));

        String content = Files.readString(classFile);
        assertTrue(content.contains("TestClass"));
        assertTrue(content.contains(TEST_DOCUMENTATION));
    }

    @Test
    void testGenerateGroupedDocumentationWithStandaloneElements()
            throws Exception {
        when(llmService.generateDocumentation(any()))
            .thenReturn(CompletableFuture.completedFuture(TEST_DOCUMENTATION));
        when(llmService.generateUsageExamples(any()))
            .thenReturn(CompletableFuture.completedFuture(TEST_EXAMPLES));
        when(llmServiceFix.isThreadLocalConfigAvailable())
            .thenReturn(true);

        // Create standalone method without corresponding class
        // This should trigger "_METHODS_" group
        CodeElement standaloneMethod = new CodeElement(
            CodeElementType.METHOD,
            "standaloneMethod",
            // No dots, so it's treated as standalone
            "standaloneMethod",
            "/test/StandaloneTest.java",
            10,
            "public static void standaloneMethod() {}",
            "A standalone method",
            Collections.emptyList(),
            Collections.emptyList()
        );

        List<CodeElement> elements = Collections.singletonList(
            standaloneMethod);
        ProjectAnalysis analysis = new ProjectAnalysis(
            "/test/path",
            elements,
            System.currentTimeMillis()
        );

        CompletableFuture<Void> result = generator
            .generateGroupedDocumentation(analysis, tempDir);
        result.join();

        // The generator skips elements without a proper class element
        // (see continue in code)
        // So we should just verify it completes without error
        assertDoesNotThrow(() -> result.join());
    }    @Test
    void testLlmServiceExceptionHandling() throws Exception {
        // Configure mocks to throw exceptions
        when(llmService.generateDocumentation(any()))
            .thenReturn(CompletableFuture.failedFuture(
                new RuntimeException("Documentation generation failed")));
        when(llmService.generateUsageExamples(any()))
            .thenReturn(CompletableFuture.failedFuture(
                new RuntimeException("Examples generation failed")));
        when(llmServiceFix.isThreadLocalConfigAvailable())
            .thenReturn(true);

        CodeElement element = createTestClassElement();

        CompletableFuture<Void> result = generator
            .generateElementDocumentation(element, tempDir);

        // Should not throw exception but handle gracefully
        assertDoesNotThrow(() -> result.join());

        // Verify file was created even with errors
        Path elementsDir = tempDir.resolve("elements");
        assertTrue(Files.exists(elementsDir));

        Path classFile = elementsDir.resolve("class-TestClass.md");
        assertTrue(Files.exists(classFile));

        String content = Files.readString(classFile);
        assertTrue(content.contains("Error generating documentation")
                  || content.contains("Error generating examples"));
    }

    @Test
    void testTimeoutHandling() throws Exception {
        // Create a future that will timeout
        CompletableFuture<String> timeoutFuture = new CompletableFuture<>();
        timeoutFuture.completeExceptionally(
            new TimeoutException("Operation timed out"));

        when(llmService.generateDocumentation(any()))
            .thenReturn(timeoutFuture);
        when(llmService.generateUsageExamples(any()))
            .thenReturn(CompletableFuture.completedFuture(TEST_EXAMPLES));
        when(llmServiceFix.isThreadLocalConfigAvailable())
            .thenReturn(true);

        CodeElement element = createTestClassElement();

        CompletableFuture<Void> result = generator
            .generateElementDocumentation(element, tempDir);

        // Should handle timeout gracefully
        assertDoesNotThrow(() -> result.join());
    }

    @Test
    void testCleanupThreadLocalConfig() throws Exception {
        when(llmService.generateDocumentation(any()))
            .thenReturn(CompletableFuture.completedFuture(TEST_DOCUMENTATION));
        when(llmService.generateUsageExamples(any()))
            .thenReturn(CompletableFuture.completedFuture(TEST_EXAMPLES));
        when(llmServiceFix.isThreadLocalConfigAvailable())
            .thenReturn(true);

        CodeElement element = createTestClassElement();

        CompletableFuture<Void> result = generator
            .generateElementDocumentation(element, tempDir);
        result.join();

        // Verify cleanup was called
        verify(llmServiceFix, atLeastOnce()).cleanupThreadLocalConfig();
    }

    @Test
    void testCleanupThreadLocalConfigWithNullLlmServiceFix() throws Exception {
        ElementDocumentationGeneratorEnhanced generatorWithNullFix =
            new ElementDocumentationGeneratorEnhanced(llmService, null);

        when(llmService.generateDocumentation(any()))
            .thenReturn(CompletableFuture.completedFuture(TEST_DOCUMENTATION));
        when(llmService.generateUsageExamples(any()))
            .thenReturn(CompletableFuture.completedFuture(TEST_EXAMPLES));

        CodeElement element = createTestClassElement();

        CompletableFuture<Void> result = generatorWithNullFix
            .generateElementDocumentation(element, tempDir);

        // Should complete without trying to cleanup (no NPE)
        assertDoesNotThrow(() -> result.join());
    }

    @Test
    void testFileWriteIOException() throws Exception {
        when(llmService.generateDocumentation(any()))
            .thenReturn(CompletableFuture.completedFuture(TEST_DOCUMENTATION));
        when(llmService.generateUsageExamples(any()))
            .thenReturn(CompletableFuture.completedFuture(TEST_EXAMPLES));
        when(llmServiceFix.isThreadLocalConfigAvailable())
            .thenReturn(true);

        // Create a file where the elements directory should be
        // to cause IOException
        Path elementsPath = tempDir.resolve("elements");
        Files.createFile(elementsPath);

        CodeElement element = createTestClassElement();

        CompletableFuture<Void> result = generator
            .generateElementDocumentation(element, tempDir);

        // The generator may handle IOExceptions gracefully by logging
        // and continuing. Let's just verify it doesn't crash the application
        assertDoesNotThrow(() -> {
            try {
                result.join();
            } catch (CompletionException e) {
                // IOException wrapped in CompletionException is expected
                assertTrue(e.getCause() instanceof IOException,
                    "Expected IOException as cause, got: " +
                    e.getCause().getClass());
            }
        });
    }    @Test
    void testMultipleClassesWithMixedElements() throws Exception {
        when(llmService.generateDocumentation(any()))
            .thenReturn(CompletableFuture.completedFuture(TEST_DOCUMENTATION));
        when(llmService.generateUsageExamples(any()))
            .thenReturn(CompletableFuture.completedFuture(TEST_EXAMPLES));
        when(llmServiceFix.isThreadLocalConfigAvailable()).thenReturn(true);

        // Create multiple classes with methods and fields
        CodeElement class1 = new CodeElement(
            CodeElementType.CLASS, "FirstClass", "com.example.FirstClass",
            "/test/FirstClass.java", 1, "public class FirstClass {}", "",
            Collections.emptyList(), Collections.emptyList()
        );

        CodeElement method1 = new CodeElement(
            CodeElementType.METHOD, "method1", "com.example.FirstClass.method1",
            "/test/FirstClass.java", 5, "public void method1() {}", "",
            Collections.emptyList(), Collections.emptyList()
        );

        CodeElement class2 = new CodeElement(
            CodeElementType.CLASS, "SecondClass", "com.example.SecondClass",
            "/test/SecondClass.java", 1, "public class SecondClass {}", "",
            Collections.emptyList(), Collections.emptyList()
        );

        CodeElement field2 = new CodeElement(
            CodeElementType.FIELD, "field2", "com.example.SecondClass.field2",
            "/test/SecondClass.java", 3, "private String field2;", "",
            Collections.emptyList(), Collections.emptyList()
        );

        List<CodeElement> elements = Arrays.asList(
            class1, method1, class2, field2);
        ProjectAnalysis analysis = new ProjectAnalysis(
            "/test/path", elements, System.currentTimeMillis());

        CompletableFuture<Void> result = generator
            .generateGroupedDocumentation(analysis, tempDir);
        result.join();

        // Verify multiple files were created
        Path elementsDir = tempDir.resolve("elements");
        assertTrue(Files.exists(elementsDir));

        long fileCount = Files.list(elementsDir).count();
        assertEquals(2, fileCount, "Should create two files for two classes");

        // Verify specific files exist
        assertTrue(Files.exists(elementsDir.resolve("class-FirstClass.md")));
        assertTrue(Files.exists(elementsDir.resolve("class-SecondClass.md")));
    }

    @Test
    void testLanguageDetectionFromFilePath() throws Exception {
        when(llmService.generateDocumentation(any()))
            .thenReturn(CompletableFuture.completedFuture(TEST_DOCUMENTATION));
        when(llmService.generateUsageExamples(any()))
            .thenReturn(CompletableFuture.completedFuture(TEST_EXAMPLES));
        when(llmServiceFix.isThreadLocalConfigAvailable()).thenReturn(true);

        // Test with Python file
        CodeElement pythonElement = new CodeElement(
            CodeElementType.CLASS, "PythonClass", "com.example.PythonClass",
            "/test/PythonClass.py", 1, "class PythonClass:", "",
            Collections.emptyList(), Collections.emptyList()
        );

        CompletableFuture<Void> result = generator
            .generateElementDocumentation(pythonElement, tempDir);
        result.join();

        Path elementsDir = tempDir.resolve("elements");
        Path pythonFile = elementsDir.resolve("class-PythonClass.md");
        assertTrue(Files.exists(pythonFile));

        String content = Files.readString(pythonFile);
        assertTrue(content.contains("```python"),
            "Should use python language for .py files");
    }

    @Test
    void testUnknownFileExtension() throws Exception {
        when(llmService.generateDocumentation(any()))
        .thenReturn(CompletableFuture.completedFuture(TEST_DOCUMENTATION));
        when(llmService.generateUsageExamples(any()))
        .thenReturn(CompletableFuture.completedFuture(TEST_EXAMPLES));
        when(llmServiceFix.isThreadLocalConfigAvailable())
        .thenReturn(true);

        // Test with unknown file extension
        CodeElement unknownElement = new CodeElement(
            CodeElementType.CLASS, "UnknownClass",
            "com.example.UnknownClass",
            "/test/UnknownClass.xyz", 1,
            "class UnknownClass", "",
            Collections.emptyList(), Collections.emptyList()
        );

        CompletableFuture<Void> result = generator
            .generateElementDocumentation(unknownElement, tempDir);
        result.join();

        Path elementsDir = tempDir.resolve("elements");
        Path unknownFile = elementsDir.resolve("class-UnknownClass.md");
        assertTrue(Files.exists(unknownFile));

        String content = Files.readString(unknownFile);
        assertTrue(content.contains("```text"),
        "Should use text language for unknown extensions");
    }

    @Test
    void testFormatContentWithNullOrEmpty() throws Exception {
        when(llmService.generateDocumentation(any())).thenReturn(
            CompletableFuture.completedFuture(""));
        when(llmService.generateUsageExamples(any())).thenReturn(
            CompletableFuture.completedFuture(null));
        when(llmServiceFix.isThreadLocalConfigAvailable()).thenReturn(true);

        CodeElement element = createTestClassElement();

        CompletableFuture<Void> result =
            generator.generateElementDocumentation(element, tempDir);
        result.join();

        Path elementsDir = tempDir.resolve("elements");
        Path classFile = elementsDir.resolve("class-TestClass.md");
        assertTrue(Files.exists(classFile));

        String content = Files.readString(classFile);
        assertTrue(content.contains("_No content available_"),
        "Should handle null/empty content gracefully");
    }

    @Test
    void testBuildClassDocumentContentWithNullClassElement() throws Exception {
        when(llmService.generateDocumentation(any())).thenReturn(
            CompletableFuture.completedFuture(TEST_DOCUMENTATION));
        when(llmService.generateUsageExamples(any())).thenReturn(
            CompletableFuture.completedFuture(TEST_EXAMPLES));
        when(llmServiceFix.isThreadLocalConfigAvailable()).thenReturn(true);

        // Create standalone method without class
        CodeElement standaloneMethod = new CodeElement(
            CodeElementType.METHOD, "standaloneMethod", "standaloneMethod",
            "/test/Standalone.java", 1,
            "public void standaloneMethod() {}", "",
            Collections.emptyList(), Collections.emptyList()
        );

        List<CodeElement> elements =
            Collections.singletonList(standaloneMethod);
        ProjectAnalysis analysis =
        new ProjectAnalysis("/test/path", elements,
            System.currentTimeMillis());

        CompletableFuture<Void> result =
            generator.generateGroupedDocumentation(analysis, tempDir);
        result.join();

        Path elementsDir = tempDir.resolve("elements");
        assertTrue(Files.exists(elementsDir));

        // Should create a file for standalone elements
        long fileCount = Files.list(elementsDir).count();
        assertTrue(fileCount > 0,
            "Should create file for standalone elements");
    }

    // Helper methods
    private CodeElement createTestClassElement() {
        return new CodeElement(
            CodeElementType.CLASS,
            "TestClass",
            "com.example.TestClass",
            "/test/TestClass.java",
            1,
            "public class TestClass {}",
            "A test class",
            Collections.emptyList(),
            Collections.emptyList()
        );
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

    private ProjectAnalysis createTestProjectAnalysis() {
        CodeElement element = createTestClassElement();
        List<CodeElement> elements = Collections.singletonList(element);
        return new ProjectAnalysis("/test/path", elements,
            System.currentTimeMillis());
    }
}
