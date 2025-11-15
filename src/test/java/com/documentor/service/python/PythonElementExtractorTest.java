package com.documentor.service.python;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PythonElementExtractorTest {

    // Test constants for magic number violations
    private static final int OUT_OF_BOUNDS_INDEX = 10;

    private PythonElementExtractor extractor;

    @BeforeEach
    void setUp() {
        extractor = new PythonElementExtractor();
    }

    @Test
    void extractDocstringShouldHandleSingleLineDocstring() {
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
    void extractDocstringShouldHandleMultiLineDocstring() {
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
        assertEquals("This is a multi-line docstring\n    "
            + "with multiple lines of text.", result);
    }

    @Test
    void extractDocstringShouldHandleSingleQuoteDocstrings() {
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
    void extractDocstringShouldHandleNoDocstring() {
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
    void extractDocstringShouldHandleOutOfBoundsIndex() {
        // Arrange
        List<String> lines = Arrays.asList(
            "def test_function():",
            "    pass"
        );

        // Act
        String result = extractor.extractDocstring(lines, OUT_OF_BOUNDS_INDEX);

        // Assert
        assertEquals("", result);
    }

    @Test
    void extractParametersShouldHandleSimpleParameters() {
        // Arrange
        String functionLine = "def test_function(param1, param2):";

        // Act
        List<String> result = extractor.extractParameters(functionLine);

        // Assert
        assertEquals(List.of("param1", "param2"), result);
    }

    @Test
    void extractParametersShouldHandleComplexParameters() {
        // Arrange
        String functionLine = "def complex_function(self, param1: str, "
            + " param2: int = 10, *args, **kwargs):";

        // Act
        List<String> result = extractor.extractParameters(functionLine);

        // Assert
        assertEquals(List.of("self", "param1: str", "param2: int = 10",
            "*args", "**kwargs"), result);
    }

    @Test
    void extractParametersShouldHandleNoParameters() {
        // Arrange
        String functionLine = "def no_params():";

        // Act
        List<String> result = extractor.extractParameters(functionLine);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void extractParametersShouldHandleInvalidInput() {
        // Arrange
        String functionLine = "not a function definition";

        // Act
        List<String> result = extractor.extractParameters(functionLine);

        // Assert
        assertTrue(result.isEmpty());
    }
}
