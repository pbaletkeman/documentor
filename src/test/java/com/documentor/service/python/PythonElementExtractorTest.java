package com.documentor.service.python;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PythonElementExtractorTest {

    private PythonElementExtractor extractor;

    @BeforeEach
    void setUp() {
        extractor = new PythonElementExtractor();
    }

    @Test
    void extractDocstring_shouldHandleSingleLineDocstring() {
        // Arrange
        List<String> lines = Arrays.asList(
            "def test_function():",
            "    \"\"\"This is a single line docstring\"\"\"",
            "    pass"
        );

        // Act
        String result = extractor.extractDocstring(lines, 1);

        // Assert
        assertEquals("This is a single line docstring", result);
    }

    @Test
    void extractDocstring_shouldHandleMultiLineDocstring() {
        // Arrange
        List<String> lines = Arrays.asList(
            "class TestClass:",
            "    \"\"\"",
            "    This is a multi-line docstring",
            "    with multiple lines of text.",
            "    \"\"\"",
            "    pass"
        );

        // Act
        String result = extractor.extractDocstring(lines, 1);

        // Assert
        assertEquals("This is a multi-line docstring\n    with multiple lines of text.", result);
    }

    @Test
    void extractDocstring_shouldHandleSingleQuoteDocstrings() {
        // Arrange
        List<String> lines = Arrays.asList(
            "def another_function():",
            "    '''Single quote docstring'''",
            "    return True"
        );

        // Act
        String result = extractor.extractDocstring(lines, 1);

        // Assert
        assertEquals("Single quote docstring", result);
    }

    @Test
    void extractDocstring_shouldHandleNoDocstring() {
        // Arrange
        List<String> lines = Arrays.asList(
            "def no_docstring():",
            "    return False"
        );

        // Act
        String result = extractor.extractDocstring(lines, 1);

        // Assert
        assertEquals("", result);
    }

    @Test
    void extractDocstring_shouldHandleOutOfBoundsIndex() {
        // Arrange
        List<String> lines = Arrays.asList(
            "def test_function():",
            "    pass"
        );

        // Act
        String result = extractor.extractDocstring(lines, 10);

        // Assert
        assertEquals("", result);
    }

    @Test
    void extractParameters_shouldHandleSimpleParameters() {
        // Arrange
        String functionLine = "def test_function(param1, param2):";

        // Act
        List<String> result = extractor.extractParameters(functionLine);

        // Assert
        assertEquals(List.of("param1", "param2"), result);
    }

    @Test
    void extractParameters_shouldHandleComplexParameters() {
        // Arrange
        String functionLine = "def complex_function(self, param1: str, param2: int = 10, *args, **kwargs):";

        // Act
        List<String> result = extractor.extractParameters(functionLine);

        // Assert
        assertEquals(List.of("self", "param1: str", "param2: int = 10", "*args", "**kwargs"), result);
    }

    @Test
    void extractParameters_shouldHandleNoParameters() {
        // Arrange
        String functionLine = "def no_params():";

        // Act
        List<String> result = extractor.extractParameters(functionLine);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void extractParameters_shouldHandleInvalidInput() {
        // Arrange
        String functionLine = "not a function definition";

        // Act
        List<String> result = extractor.extractParameters(functionLine);

        // Assert
        assertTrue(result.isEmpty());
    }
}