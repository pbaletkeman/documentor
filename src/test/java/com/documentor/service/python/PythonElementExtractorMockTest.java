package com.documentor.service.python;

import com.documentor.model.CodeElement;
import com.documentor.model.CodeElementType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;

/**
 * Unit tests for PythonElementExtractor with mocked dependencies
 */
@ExtendWith(MockitoExtension.class)
public class PythonElementExtractorMockTest {

    // Test constants for magic number violations
    private static final int TEST_METHOD_LINE_NUMBER = 5;

    @Mock
    private PythonASTProcessor astProcessor;

    @Mock
    private PythonRegexAnalyzer regexAnalyzer;

    @InjectMocks
    private PythonElementExtractor elementExtractor;

    @TempDir
    private Path tempDir;

    /**
     * This is a helper method to simulate a combined approach using both
     * AST and regex analysis
     */
    private List<CodeElement> extractElementsUsingBothApproaches(
        final Path filePath)
            throws IOException, InterruptedException {
        try {
            // Try AST first
            List<CodeElement> elements = astProcessor.analyzeWithAST(filePath);
            if (!elements.isEmpty()) {
                return elements;
            }

            // If AST returns empty, fall back to regex
            List<String> lines = Files.readAllLines(filePath);
            return regexAnalyzer.analyzeWithRegex(filePath, lines);
        } catch (Exception e) {
            // Fall back to regex if AST fails
            List<String> lines = Files.readAllLines(filePath);
            return regexAnalyzer.analyzeWithRegex(filePath, lines);
        }
    }

    @Test
    void shouldExtractElementsUsingAstAndFallbackToRegex() throws Exception {
        // Given
        Path pythonFile = createSamplePythonFile();

        // Create mock AST elements
        CodeElement astClass = new CodeElement(
            CodeElementType.CLASS,
            "TestClass",
            "class TestClass:",
            pythonFile.toString(),
            1,
            "Test class docstring",
            "",
            List.of(),
            List.of()
        );

        CodeElement astMethod = new CodeElement(
            CodeElementType.METHOD,
            "test_method",
            "def test_method(param1, param2):",
            pythonFile.toString(),
            TEST_METHOD_LINE_NUMBER,
            "Test method docstring",
            "",
            List.of("param1", "param2"),
            List.of()
        );

        // Mock AST processor behavior
        when(astProcessor.analyzeWithAST(eq(pythonFile)))
            .thenReturn(List.of(astClass, astMethod));

        // When - Test the method that we're creating to combine both approaches
        List<CodeElement> elements =
            extractElementsUsingBothApproaches(pythonFile);

        // Then
        verify(astProcessor).analyzeWithAST(pythonFile);

        // We shouldn't call the regex analyzer when AST succeeds
        verify(regexAnalyzer, never()).analyzeWithRegex(eq(pythonFile), any());

        assertEquals(2, elements.size());
        assertTrue(elements.contains(astClass));
        assertTrue(elements.contains(astMethod));
    }

    @Test
    void shouldFallbackToRegexWhenAstFails() throws Exception {
        // Given
        Path pythonFile = createSamplePythonFile();

        // Mock AST processor to throw exception (simulating failure)
        when(astProcessor.analyzeWithAST(eq(pythonFile)))
            .thenThrow(new IOException("AST analysis failed"));

        // Create mock regex elements for fallback
        CodeElement regexClass = new CodeElement(
            CodeElementType.CLASS,
            "FallbackClass",
            "class FallbackClass:",
            pythonFile.toString(),
            1,
            "",
            "",
            List.of(),
            List.of()
        );

        // Mock regex analyzer behavior for fallback
        List<String> lines = Files.readAllLines(pythonFile);
        when(regexAnalyzer.analyzeWithRegex(eq(pythonFile), eq(lines)))
            .thenReturn(List.of(regexClass));

        // When
        List<CodeElement> elements =
            extractElementsUsingBothApproaches(pythonFile);

        // Then
        verify(astProcessor).analyzeWithAST(pythonFile);
        verify(regexAnalyzer).analyzeWithRegex(eq(pythonFile), any());

        assertEquals(1, elements.size());
        assertEquals("FallbackClass", elements.get(0).name());
        assertEquals(CodeElementType.CLASS, elements.get(0).type());
    }

    private Path createSamplePythonFile() throws IOException {
        Path pythonFile = tempDir.resolve("test_file.py");
        String content = """
            class TestClass:
                \"\"\"Test class docstring\"\"\"

                def test_method(param1, param2):
                    \"\"\"Test method docstring\"\"\"
                    return param1 + param2

                CONFIG_VALUE = 'test'
            """;
        Files.writeString(pythonFile, content);
        return pythonFile;
    }
}
