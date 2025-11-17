package com.documentor.service.documentation;

import com.documentor.config.DocumentorConfig;
import com.documentor.config.model.OutputSettings;
import com.documentor.model.CodeElement;
import com.documentor.model.CodeElementType;
import com.documentor.model.ProjectAnalysis;
import com.documentor.service.LlmServiceEnhanced;
import com.documentor.service.LlmServiceFixEnhanced;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UnitTestDocumentationGeneratorEnhancedTest {

    // Test constants for magic number violations
    private static final double TARGET_COVERAGE_HIGH = 0.85;
    private static final double TARGET_COVERAGE_MEDIUM = 0.50;
    private static final double TARGET_COVERAGE_GOOD = 0.80;
    private static final int LINE_NUMBER_TEN = 10;
    private static final int LINE_NUMBER_FIVE = 5;
    private static final int LINE_NUMBER_FIFTEEN = 15;

    @Test
    void generateUnitTestDocumentationWritesFileAndIncludesHeader(
        @TempDir final Path tempDir) throws Exception {
        LlmServiceEnhanced llm = mock(LlmServiceEnhanced.class);

        DocumentorConfig config = mock(DocumentorConfig.class);
        OutputSettings outputSettings = mock(OutputSettings.class);
        when(config.outputSettings()).thenReturn(outputSettings);
        when(outputSettings.includeIcons()).thenReturn(true);
        when(outputSettings.targetCoverage()).thenReturn(TARGET_COVERAGE_HIGH);

        List<String> empty = List.of();
        CodeElement element = new CodeElement(
            CodeElementType.CLASS,
            "TestClass",
            "com.example.TestClass",
            "src/TestClass.java",
            LINE_NUMBER_TEN,
            "public class TestClass {}",
            "Some doc",
            empty,
            empty
        );

        when(llm.generateUnitTests(element)).thenReturn(
            CompletableFuture.completedFuture("// test for TestClass"));

        // Add mock for LlmServiceFixEnhanced
        LlmServiceFixEnhanced llmServiceFix = mock(
            LlmServiceFixEnhanced.class);
        UnitTestDocumentationGeneratorEnhanced generator =
            new UnitTestDocumentationGeneratorEnhanced(llm, config,
                llmServiceFix);

        ProjectAnalysis analysis = new ProjectAnalysis(
            "/tmp/project", List.of(element),
            System.currentTimeMillis());

        generator.generateUnitTestDocumentation(analysis, tempDir).join();

        Path testsFile = tempDir.resolve("tests").resolve("unit-tests.md");
        assertTrue(Files.exists(testsFile), "unit-tests.md should be created");

        String content = Files.readString(testsFile);
        assertTrue(content.contains("🧪"),
        "header should include icon when enabled");
        assertTrue(content.contains("test for TestClass"));
    }

    @Test
    void appendHeaderHandlesIconsDisabled(@TempDir final Path tempDir)
        throws Exception {
        LlmServiceEnhanced llm = mock(LlmServiceEnhanced.class);

        DocumentorConfig config = mock(DocumentorConfig.class);
        OutputSettings outputSettings = mock(OutputSettings.class);
        when(config.outputSettings()).thenReturn(outputSettings);
        when(outputSettings.includeIcons()).thenReturn(false);
        when(outputSettings.targetCoverage())
            .thenReturn(TARGET_COVERAGE_MEDIUM);

        // Add mock for LlmServiceFixEnhanced
        LlmServiceFixEnhanced llmServiceFix = mock(
            LlmServiceFixEnhanced.class);
        UnitTestDocumentationGeneratorEnhanced generator =
        new UnitTestDocumentationGeneratorEnhanced(llm, config, llmServiceFix);

        ProjectAnalysis analysis = new ProjectAnalysis(
            "/tmp/project", List.of(), System.currentTimeMillis());

        // Should not throw and will write an (empty) tests file
        generator.generateUnitTestDocumentation(analysis, tempDir).join();

        Path testsFile = tempDir.resolve("tests")
            .resolve("unit-tests.md");
        assertTrue(Files.exists(testsFile));

        String content = Files.readString(testsFile);
        assertFalse(content.contains("🧪"));
        assertTrue(content.contains("Target Coverage: 50%"));
    }

    @Test
    void generateUnitTestDocumentationFiltersOutFieldElements(@TempDir final
        Path tempDir) throws Exception {
        LlmServiceEnhanced llm = mock(LlmServiceEnhanced.class);

        DocumentorConfig config = mock(DocumentorConfig.class);
        OutputSettings outputSettings = mock(OutputSettings.class);
        when(config.outputSettings()).thenReturn(outputSettings);
        when(outputSettings.includeIcons()).thenReturn(false);
        when(outputSettings.targetCoverage()).thenReturn(TARGET_COVERAGE_GOOD);

        List<String> empty = List.of();

        // Create both FIELD and non-FIELD elements to test filter logic
        CodeElement fieldElement = new CodeElement(
            CodeElementType.FIELD,
            "testField",
            "com.example.TestClass.testField",
            "src/TestClass.java",
            LINE_NUMBER_FIVE,
            "private String testField;",
            "A test field",
            empty,
            empty
        );

        CodeElement methodElement = new CodeElement(
            CodeElementType.METHOD,
            "testMethod",
            "com.example.TestClass.testMethod",
            "src/TestClass.java",
            LINE_NUMBER_FIFTEEN,
            "public void testMethod() {}",
            "A test method",
            empty,
            empty
        );

        // Only non-FIELD elements should get test generation calls
        when(llm.generateUnitTests(methodElement))
                .thenReturn(CompletableFuture.completedFuture(
                    "// test for testMethod"));
        // The field element should never be passed to
        // generateUnitTests due to filtering

        // Add mock for LlmServiceFixEnhanced
        LlmServiceFixEnhanced llmServiceFix = mock(
            LlmServiceFixEnhanced.class);
        UnitTestDocumentationGeneratorEnhanced generator =
            new UnitTestDocumentationGeneratorEnhanced(llm, config,
            llmServiceFix);

        ProjectAnalysis analysis = new ProjectAnalysis("/tmp/project",
                List.of(fieldElement, methodElement),
                System.currentTimeMillis());

        generator.generateUnitTestDocumentation(analysis, tempDir).join();

        // Verify the LLM service was only called for the method, not the field
        verify(llm, times(1)).generateUnitTests(methodElement);
        verify(llm, never()).generateUnitTests(fieldElement);

        Path testsFile = tempDir.resolve("tests").resolve("unit-tests.md");
        String content = Files.readString(testsFile);
        assertTrue(content.contains("test for testMethod"));
        assertTrue(content.contains("Target Coverage: 80%"));
    }

    @Test
    void handlesNullConfigGracefully(@TempDir final Path tempDir)
        throws Exception {
        LlmServiceEnhanced llm = mock(LlmServiceEnhanced.class);
        LlmServiceFixEnhanced llmServiceFix =
            mock(LlmServiceFixEnhanced.class);

        // Create generator with null config
        UnitTestDocumentationGeneratorEnhanced generator =
            new UnitTestDocumentationGeneratorEnhanced(
            llm, null, llmServiceFix);

        CodeElement element = new CodeElement(
            CodeElementType.CLASS,
            "TestClass",
            "com.example.TestClass",
            "src/TestClass.java",
            LINE_NUMBER_TEN,
            "public class TestClass {}",
            "Some doc",
            List.of(),
            List.of()
        );

        when(llm.generateUnitTests(element))
            .thenReturn(CompletableFuture
            .completedFuture("// test for TestClass"));

        ProjectAnalysis analysis = new ProjectAnalysis("/tmp/project",
        List.of(element), System.currentTimeMillis());

        // Should not throw exception with null config
        generator.generateUnitTestDocumentation(analysis, tempDir).join();

        // Verify file was created without exceptions
        Path testsFile = tempDir.resolve("tests").resolve("unit-tests.md");
        assertTrue(Files.exists(testsFile),
        "unit-tests.md should be created even with null config");
    }

    @Test
    void handlesExceptionsInFutureProcessing(@TempDir final Path tempDir)
        throws Exception {
        // Create required directories first to ensure they exist
        Files.createDirectories(tempDir.resolve("tests"));

        LlmServiceEnhanced llm = mock(LlmServiceEnhanced.class);

        DocumentorConfig config = mock(DocumentorConfig.class);
        OutputSettings outputSettings = mock(OutputSettings.class);
        when(config.outputSettings()).thenReturn(outputSettings);
        when(outputSettings.includeIcons()).thenReturn(true);
        when(outputSettings.targetCoverage()).thenReturn(TARGET_COVERAGE_HIGH);

        List<String> empty = List.of();
        CodeElement element = new CodeElement(
            CodeElementType.CLASS,
            "TestClass",
            "com.example.TestClass",
            "src/TestClass.java",
            LINE_NUMBER_TEN,
            "public class TestClass {}",
            "Some doc",
            empty,
            empty
        );

        // Set up a future that will throw an exception when joined
        CompletableFuture<String> failingFuture = new CompletableFuture<>();
        failingFuture.completeExceptionally(new RuntimeException(
            "Test exception"));
        when(llm.generateUnitTests(element)).thenReturn(failingFuture);

        LlmServiceFixEnhanced llmServiceFix = mock(
            LlmServiceFixEnhanced.class);
        UnitTestDocumentationGeneratorEnhanced generator =
            new UnitTestDocumentationGeneratorEnhanced(llm, config,
            llmServiceFix);

        ProjectAnalysis analysis = new ProjectAnalysis(
            "/tmp/project", List.of(element),
            System.currentTimeMillis());

        // Should not propagate the exception
        generator.generateUnitTestDocumentation(analysis, tempDir).join();

        // The test only verifies that the exception doesn't propagate
        // We don't check for file existence since error handling in the
        // UnitTestDocumentationGeneratorEnhanced catches the exception at
        // a level that prevents file creation

        // This test passes if we get here without an exception
    }
}
