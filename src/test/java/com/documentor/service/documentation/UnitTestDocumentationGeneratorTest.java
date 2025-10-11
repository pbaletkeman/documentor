package com.documentor.service.documentation;

import com.documentor.config.DocumentorConfig;
import com.documentor.config.model.OutputSettings;
import com.documentor.model.CodeElement;
import com.documentor.model.CodeElementType;
import com.documentor.model.ProjectAnalysis;
import com.documentor.service.LlmService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UnitTestDocumentationGeneratorTest {

    @Test
    void generateUnitTestDocumentation_writesFileAndIncludesHeader(@TempDir Path tempDir) throws Exception {
        LlmService llm = mock(LlmService.class);

        DocumentorConfig config = mock(DocumentorConfig.class);
        OutputSettings outputSettings = mock(OutputSettings.class);
        when(config.outputSettings()).thenReturn(outputSettings);
        when(outputSettings.includeIcons()).thenReturn(true);
        when(outputSettings.targetCoverage()).thenReturn(0.85);

        List<String> empty = List.of();
        CodeElement element = new CodeElement(
            CodeElementType.CLASS,
            "TestClass",
            "com.example.TestClass",
            "src/TestClass.java",
            10,
            "public class TestClass {}",
            "Some doc",
            empty,
            empty
        );

        when(llm.generateUnitTests(element)).thenReturn(CompletableFuture.completedFuture("// test for TestClass"));

        UnitTestDocumentationGenerator generator = new UnitTestDocumentationGenerator(llm, config);

        ProjectAnalysis analysis = new ProjectAnalysis("/tmp/project", List.of(element), System.currentTimeMillis());

        generator.generateUnitTestDocumentation(analysis, tempDir).join();

        Path testsFile = tempDir.resolve("tests").resolve("unit-tests.md");
        assertTrue(Files.exists(testsFile), "unit-tests.md should be created");

        String content = Files.readString(testsFile);
        assertTrue(content.contains("🧪"), "header should include icon when enabled");
        assertTrue(content.contains("test for TestClass"));
    }

    @Test
    void appendHeader_handlesIconsDisabled(@TempDir Path tempDir) throws Exception {
        LlmService llm = mock(LlmService.class);

        DocumentorConfig config = mock(DocumentorConfig.class);
        OutputSettings outputSettings = mock(OutputSettings.class);
        when(config.outputSettings()).thenReturn(outputSettings);
        when(outputSettings.includeIcons()).thenReturn(false);
        when(outputSettings.targetCoverage()).thenReturn(0.50);

        UnitTestDocumentationGenerator generator = new UnitTestDocumentationGenerator(llm, config);

    ProjectAnalysis analysis = new ProjectAnalysis("/tmp/project", List.of(), System.currentTimeMillis());

        // Should not throw and will write an (empty) tests file
        generator.generateUnitTestDocumentation(analysis, tempDir).join();

        Path testsFile = tempDir.resolve("tests").resolve("unit-tests.md");
        assertTrue(Files.exists(testsFile));

        String content = Files.readString(testsFile);
        assertFalse(content.contains("🧪"));
        assertTrue(content.contains("Target Coverage: 50%"));
    }

    @Test
    void generateUnitTestDocumentation_filtersOutFieldElements(@TempDir Path tempDir) throws Exception {
        LlmService llm = mock(LlmService.class);

        DocumentorConfig config = mock(DocumentorConfig.class);
        OutputSettings outputSettings = mock(OutputSettings.class);
        when(config.outputSettings()).thenReturn(outputSettings);
        when(outputSettings.includeIcons()).thenReturn(false);
        when(outputSettings.targetCoverage()).thenReturn(0.80);

        List<String> empty = List.of();
        
        // Create both FIELD and non-FIELD elements to test filter logic
        CodeElement fieldElement = new CodeElement(
            CodeElementType.FIELD,
            "testField",
            "com.example.TestClass.testField",
            "src/TestClass.java",
            5,
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
            15,
            "public void testMethod() {}",
            "A test method",
            empty,
            empty
        );

        // Only non-FIELD elements should get test generation calls
        when(llm.generateUnitTests(methodElement)).thenReturn(CompletableFuture.completedFuture("// test for testMethod"));
        // The field element should never be passed to generateUnitTests due to filtering

        UnitTestDocumentationGenerator generator = new UnitTestDocumentationGenerator(llm, config);

        ProjectAnalysis analysis = new ProjectAnalysis("/tmp/project", List.of(fieldElement, methodElement), System.currentTimeMillis());

        generator.generateUnitTestDocumentation(analysis, tempDir).join();

        // Verify the LLM service was only called for the method, not the field
        verify(llm, times(1)).generateUnitTests(methodElement);
        verify(llm, never()).generateUnitTests(fieldElement);

        Path testsFile = tempDir.resolve("tests").resolve("unit-tests.md");
        String content = Files.readString(testsFile);
        assertTrue(content.contains("test for testMethod"));
        assertTrue(content.contains("Target Coverage: 80%"));
    }
}
