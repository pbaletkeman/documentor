package com.documentor.service.documentation;

import com.documentor.config.DocumentorConfig;
import com.documentor.config.model.OutputSettings;
import com.documentor.model.CodeElement;
import com.documentor.model.CodeElementType;
import com.documentor.model.ProjectAnalysis;
import com.documentor.service.LlmService;
import com.documentor.service.LlmServiceFix;
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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.lenient;

/**
 * Additional branch coverage tests for UnitTestDocumentationGenerator.
 * Targets the remaining 2 missed branches to improve overall branch coverage.
 */
@ExtendWith(MockitoExtension.class)
class UnitTestDocumentationGeneratorBranchTest {

    @Mock
    private LlmService llmService;

    @Mock
    private DocumentorConfig config;

    @Mock
    private OutputSettings outputSettings;

    @Mock
    private LlmServiceFix llmServiceFix;

    private UnitTestDocumentationGenerator generator;

    @TempDir
    private Path tempDir;

    private static final String TEST_UNIT_TESTS =
        "// Generated unit tests\n@Test\nvoid testMethod() {\n"
        + "    // test code\n"
        + "}";

    // Magic number constants for checkstyle compliance
    private static final int LINE_NUMBER_3 = 3;
    private static final int LINE_NUMBER_5 = 5;
    private static final int LINE_NUMBER_10 = 10;
    private static final int LINE_NUMBER_1 = 1;
    private static final double TARGET_COVERAGE_085 = 0.85;
    private static final double TARGET_COVERAGE_09 = 0.9;
    private static final double TARGET_COVERAGE_06 = 0.6;

    @BeforeEach
    void setUp() {
        lenient().when(config.outputSettings()).thenReturn(outputSettings);
        lenient().when(outputSettings.includeIcons()).thenReturn(true);
        lenient().when(outputSettings.targetCoverage()).thenReturn(TARGET_COVERAGE_085);
        generator = new UnitTestDocumentationGenerator(llmService, config,
            llmServiceFix);
    }

    @Test
    void testGenerateUnitTestDocumentationWithEmptyProject() {
        // Test with empty project analysis - should create empty tests file
        ProjectAnalysis emptyAnalysis =
            new ProjectAnalysis(
                "/test/path",
                Collections.emptyList(),
                System.currentTimeMillis()
            );

        CompletableFuture<Void> result = generator
            .generateUnitTestDocumentation(emptyAnalysis, tempDir);

        assertDoesNotThrow(() -> result.join());

        // Verify tests directory and file are created even with no elements
        Path testsDir = tempDir.resolve("tests");
        assertTrue(Files.exists(testsDir));

        Path testsFile = testsDir.resolve("unit-tests.md");
        assertTrue(Files.exists(testsFile));
    }

    @Test
    void testGenerateUnitTestDocumentationWithOnlyFieldElements() {
        // Test with project containing only FIELD elements
        // - should create empty tests file (fields are filtered out)
        CodeElement fieldElement1 = new CodeElement(
            CodeElementType.FIELD, "field1", "com.example.TestClass.field1",
            "/test/TestClass.java", LINE_NUMBER_3, "private String field1;", "A test field",
            Collections.emptyList(), Collections.emptyList()
        );

        CodeElement fieldElement2 = new CodeElement(
            CodeElementType.FIELD, "field2", "com.example.TestClass.field2",
            "/test/TestClass.java", LINE_NUMBER_5, "private int field2;",
            "Another test field",
            Collections.emptyList(), Collections.emptyList()
        );

        List<CodeElement> elements = Arrays.asList(
            fieldElement1,
            fieldElement2
        );
        ProjectAnalysis analysis = new ProjectAnalysis(
            "/test/path",
            elements,
            System.currentTimeMillis()
        );

        CompletableFuture<Void> result = generator
            .generateUnitTestDocumentation(analysis, tempDir);

        assertDoesNotThrow(() -> result.join());

        // Verify llmService.generateUnitTests
        // was never called for FIELD elements
        verify(llmService, never()).generateUnitTests(any());

        // Verify tests file exists but contains no test content (only header)
        Path testsFile = tempDir.resolve("tests").resolve("unit-tests.md");
        assertTrue(Files.exists(testsFile));
    }

    @Test
    void testGenerateUnitTestDocumentationWithMixedElements() throws Exception {
        // Test with mixed elements
        // - only non-FIELD elements should generate tests
        CodeElement fieldElement = new CodeElement(
            CodeElementType.FIELD,
            "field",
            "com.example.TestClass.field",
            "/test/TestClass.java",
            LINE_NUMBER_3,
            "private String field;",
            "A test field",
            Collections.emptyList(),
            Collections.emptyList()
        );

        CodeElement methodElement = new CodeElement(
            CodeElementType.METHOD,
            "testMethod",
            "com.example.TestClass.testMethod",
            "/test/TestClass.java",
            LINE_NUMBER_10,
            "public void testMethod() {}",
            "A test method",
            Collections.emptyList(),
            Collections.emptyList()
        );

        CodeElement classElement = new CodeElement(
            CodeElementType.CLASS,
            "TestClass",
            "com.example.TestClass",
            "/test/TestClass.java",
            LINE_NUMBER_1,
            "public class TestClass {}",
            "A test class",
            Collections.emptyList(),
            Collections.emptyList()
        );

        when(
            llmService.generateUnitTests(methodElement)
        ).thenReturn(
            CompletableFuture.completedFuture(TEST_UNIT_TESTS)
        );
        when(
            llmService.generateUnitTests(classElement)
        ).thenReturn(
            CompletableFuture.completedFuture(
                    "// Tests for TestClass"
            )
        );

        List<CodeElement> elements =
            Arrays.asList(fieldElement, methodElement, classElement);
            ProjectAnalysis analysis = new ProjectAnalysis(
                "/test/path",
                elements,
                System.currentTimeMillis()
            );

        CompletableFuture<Void> result =
        generator.generateUnitTestDocumentation(analysis, tempDir);

        assertDoesNotThrow(() -> result.join());

        // Verify llmService.generateUnitTests was called
        // for non-FIELD elements only
        verify(llmService, times(1)).generateUnitTests(methodElement);
        verify(llmService, times(1)).generateUnitTests(classElement);
        verify(llmService, never()).generateUnitTests(fieldElement);

        // Verify tests file contains expected content
        Path testsFile = tempDir.resolve("tests").resolve("unit-tests.md");
        assertTrue(Files.exists(testsFile));

        String content = Files.readString(testsFile);
        assertTrue(content.contains(TEST_UNIT_TESTS));
        assertTrue(content.contains("Tests for TestClass"));
    }

    @Test
    void testGenerateUnitTestDocumentationWithLlmException() {
        // Test behavior when LLM service throws exception
        CodeElement methodElement = new CodeElement(
            CodeElementType.METHOD, "testMethod",
            "com.example.TestClass.testMethod",
            "/test/TestClass.java", LINE_NUMBER_10, "public void testMethod() {}",
                "A test method",
                Collections.emptyList(),
                Collections.emptyList()
        );

        when(
            llmService.generateUnitTests(methodElement)
        ).thenReturn(CompletableFuture.failedFuture(
                new RuntimeException("LLM service error")
            ));

        List<CodeElement> elements = Collections.singletonList(methodElement);
        ProjectAnalysis analysis = new ProjectAnalysis("/test/path", elements,
                System.currentTimeMillis()
            );

        CompletableFuture<Void> result =
            generator.generateUnitTestDocumentation(analysis, tempDir);

        // Should throw CompletionException because join() on
        // failed future causes exception
        assertThrows(CompletionException.class, () -> result.join());
    }

    @Test
    void testGenerateUnitTestDocumentationWithIOException()
        throws IOException {
        // Test behavior when IO exception occurs during file writing
        CodeElement methodElement = new CodeElement(
            CodeElementType.METHOD, "testMethod",
            "com.example.TestClass.testMethod",
            "/test/TestClass.java", LINE_NUMBER_10, "public void testMethod() {}",
            "A test method",
            Collections.emptyList(), Collections.emptyList()
        );

        // Create a file where the tests directory
        // should be to cause IOException
        Path testsPath = tempDir.resolve("tests");
        Files.createFile(testsPath);

        List<CodeElement> elements = Collections.singletonList(methodElement);
        ProjectAnalysis analysis = new ProjectAnalysis(
            "/test/path", elements, System.currentTimeMillis());

        CompletableFuture<Void> result = generator
        .generateUnitTestDocumentation(analysis, tempDir);

        // Should throw CompletionException wrapping RuntimeException
        assertThrows(CompletionException.class, () -> result.join());
    }

    @Test
    void testGenerateUnitTestDocumentationWithNullLlmService() {
        // Test behavior with null LLM service
        UnitTestDocumentationGenerator generatorWithNullLlm =
            new UnitTestDocumentationGenerator(null, config, llmServiceFix);

        CodeElement methodElement = new CodeElement(
            CodeElementType.METHOD, "testMethod",
            "com.example.TestClass.testMethod", "/test/TestClass.java", LINE_NUMBER_10,
            "public void testMethod() {}", "A test method",
                Collections.emptyList(),
                Collections.emptyList()
        );

        List<CodeElement> elements = Collections.singletonList(methodElement);
        ProjectAnalysis analysis = new ProjectAnalysis("/test/path", elements,
            System.currentTimeMillis());

        CompletableFuture<Void> result =
            generatorWithNullLlm.generateUnitTestDocumentation(analysis,
                tempDir);

        // Should handle null LLM service gracefully
        assertDoesNotThrow(() -> result.join());

        Path testsFile = tempDir.resolve("tests").resolve("unit-tests.md");
        assertTrue(Files.exists(testsFile));
    }

    @Test
    void testGenerateUnitTestDocumentationTargetCoverageBranches()
        throws Exception {
        // Test different target coverage values to ensure all branches
        // in lambda$generateUnitTestDocumentation$4 are covered

        // Test with high target coverage (>80%)
        when(outputSettings.targetCoverage())
            .thenReturn(TARGET_COVERAGE_09);

        CodeElement methodElement = new CodeElement(
            CodeElementType.METHOD,
            "testMethod",
            "com.example.TestClass.testMethod",
            "/test/TestClass.java",
            LINE_NUMBER_10,
            "public void testMethod() {}",
            "A test method",
            Collections.emptyList(),
            Collections.emptyList()
        );

        when(llmService.generateUnitTests(methodElement))
            .thenReturn(CompletableFuture.completedFuture(TEST_UNIT_TESTS));

        List<CodeElement> elements = Collections.singletonList(methodElement);
        ProjectAnalysis analysis = new ProjectAnalysis(
            "/test/path",
            elements,
            System.currentTimeMillis()
        );

        CompletableFuture<Void> result = generator
            .generateUnitTestDocumentation(analysis, tempDir);
        result.join();

        Path testsFile = tempDir.resolve("tests")
            .resolve("unit-tests.md");
        String content = Files.readString(testsFile);
        assertTrue(content.contains("Target Coverage: 90%"));

        // Test with low target coverage (≤80%)
        when(outputSettings.targetCoverage())
            .thenReturn(TARGET_COVERAGE_06);

        // Create new generator with updated config
        UnitTestDocumentationGenerator generatorLowCoverage =
            new UnitTestDocumentationGenerator(
                llmService,
                config,
                llmServiceFix
            );

        // Delete existing file first
        Files.deleteIfExists(testsFile);
        Files.deleteIfExists(tempDir.resolve("tests"));

        CompletableFuture<Void> result2 = generatorLowCoverage
            .generateUnitTestDocumentation(analysis, tempDir);
        result2.join();

        String content2 = Files.readString(testsFile);
        assertTrue(content2.contains("Target Coverage: 60%"));
    }

    @Test
    void testLambdaFilteringLogic() {
        // Test the filtering logic in lambda$generateUnitTestDocumentation$1
        // and lambda$generateUnitTestDocumentation$0

        // Create elements of different types to test filtering
        CodeElement fieldElement = new CodeElement(
            CodeElementType.FIELD, "field", "com.example.TestClass.field",
            "/test/TestClass.java", LINE_NUMBER_3, "private String field;", "A test field",
            Collections.emptyList(), Collections.emptyList()
        );

        CodeElement methodElement = new CodeElement(
            CodeElementType.METHOD, "method", "com.example.TestClass.method",
            "/test/TestClass.java", LINE_NUMBER_10, "public void method() {}",
            "A test method",
            Collections.emptyList(), Collections.emptyList()
        );

        CodeElement classElement = new CodeElement(
            CodeElementType.CLASS, "TestClass", "com.example.TestClass",
            "/test/TestClass.java", LINE_NUMBER_1, "public class TestClass {}",
            "A test class",
            Collections.emptyList(), Collections.emptyList()
        );


        CodeElement constructorElement = new CodeElement(
            CodeElementType.METHOD, "TestClass", "com.example.TestClass.TestClass",
            "/test/TestClass.java", LINE_NUMBER_5, "public TestClass() {}", "A test constructor",
            Collections.emptyList(), Collections.emptyList()
        );

        when(
            llmService.generateUnitTests(any())
        ).thenReturn(CompletableFuture.completedFuture("// test content"));

        List<CodeElement> elements = Arrays.asList(fieldElement, methodElement,
            classElement, constructorElement);
        ProjectAnalysis analysis = new ProjectAnalysis("/test/path",
            elements, System.currentTimeMillis());

        CompletableFuture<Void> result = generator.
            generateUnitTestDocumentation(analysis, tempDir);

        assertDoesNotThrow(() -> result.join());

        // Verify only non-FIELD elements were processed
        verify(llmService, times(1)).generateUnitTests(methodElement);
        verify(llmService, times(1)).generateUnitTests(classElement);
        verify(llmService, times(1)).generateUnitTests(constructorElement);
        verify(llmService, never()).generateUnitTests(fieldElement);
    }
}
