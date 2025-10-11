package com.documentor.service;

import com.documentor.model.CodeElement;
import com.documentor.model.CodeElementType;
import com.documentor.service.python.PythonASTProcessor;
import com.documentor.service.python.PythonRegexAnalyzer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class PythonCodeAnalyzerTest {

    // Test constants for magic number violations
    private static final int ELEMENT_LINE_NUMBER = 3;
    private static final int LINE_NUMBER_ONE = 1;
    private static final int LINE_NUMBER_TWO = 2;

    @Mock
    private PythonASTProcessor astProcessor;

    @Mock
    private PythonRegexAnalyzer regexAnalyzer;

    @InjectMocks
    private PythonCodeAnalyzer analyzer;

    @TempDir
    private Path tempDir;

    @Test
    void pythonCodeAnalyzerClassIsPresent() throws ClassNotFoundException {
        // Verify class is present on classpath (sanity check)
        assertNotNull(Class.forName("com.documentor.service.PythonCodeAnalyzer"));
    }

    @Test
    void analyzeFileWithSuccessfulAstProcessingReturnsAstElements() throws IOException, InterruptedException {
        // Arrange
        Path testFile = createPythonTestFile();
        List<CodeElement> expectedElements = List.of(
            new CodeElement(CodeElementType.CLASS, "TestClass", "class TestClass",
                testFile.toString(), LINE_NUMBER_ONE, "class TestClass:", "",
                Collections.emptyList(), Collections.emptyList())
        );

        when(astProcessor.analyzeWithAST(any(Path.class))).thenReturn(expectedElements);

        // Act
        List<CodeElement> result = analyzer.analyzeFile(testFile);

        // Assert
        assertEquals(expectedElements, result);
        verify(astProcessor).analyzeWithAST(testFile);
        verifyNoInteractions(regexAnalyzer);
    }

    @Test
    void analyzeFileWhenAstProcessingFailsUsesRegexAnalyzer() throws IOException, InterruptedException {
        // Arrange
        Path testFile = createPythonTestFile();
        List<CodeElement> expectedElements = List.of(
            new CodeElement(CodeElementType.METHOD, "test_method", "def test_method()",
                testFile.toString(), LINE_NUMBER_TWO, "def test_method():", "",
                Collections.emptyList(), Collections.emptyList())
        );

        when(astProcessor.analyzeWithAST(any(Path.class))).thenThrow(new IOException("AST processing failed"));
        when(regexAnalyzer.analyzeWithRegex(eq(testFile), anyList())).thenReturn(expectedElements);

        // Act
        List<CodeElement> result = analyzer.analyzeFile(testFile);

        // Assert
        assertEquals(expectedElements, result);
        verify(astProcessor).analyzeWithAST(testFile);
        verify(regexAnalyzer).analyzeWithRegex(eq(testFile), anyList());
    }

    @Test
    void analyzeFileWhenAstReturnsEmptyUsesRegexAnalyzer() throws IOException, InterruptedException {
        // Arrange
        Path testFile = createPythonTestFile();
        List<CodeElement> expectedElements = List.of(
            new CodeElement(CodeElementType.FIELD, "variable", "variable",
                testFile.toString(), ELEMENT_LINE_NUMBER, "variable = 42", "",
                Collections.emptyList(), Collections.emptyList())
        );

        when(astProcessor.analyzeWithAST(any(Path.class))).thenReturn(Collections.emptyList());
        when(regexAnalyzer.analyzeWithRegex(eq(testFile), anyList())).thenReturn(expectedElements);

        // Act
        List<CodeElement> result = analyzer.analyzeFile(testFile);

        // Assert
        assertEquals(expectedElements, result);
        verify(astProcessor).analyzeWithAST(testFile);
        verify(regexAnalyzer).analyzeWithRegex(eq(testFile), anyList());
    }

    private Path createPythonTestFile() throws IOException {
        Path filePath = tempDir.resolve("test.py");
        String content = """
                class TestClass:
                    def test_method(self):
                        pass

                variable = 42
                """;
        Files.write(filePath, content.getBytes());
        return filePath;
    }
}
