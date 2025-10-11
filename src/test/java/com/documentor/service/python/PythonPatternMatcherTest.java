package com.documentor.service.python;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.regex.Matcher;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for PythonPatternMatcher class
 */
class PythonPatternMatcherTest {

    // Test constants for magic number violations
    private static final int EXPECTED_CLASS_COUNT = 3;
    private static final int EXPECTED_FUNCTION_COUNT = 3;
    private static final int EXPECTED_VARIABLE_COUNT = 4;
    private static final int EXPECTED_COMPLEX_PARAM_COUNT = 5;
    private static final int EXPECTED_PARAM_COUNT = 3;
    private static final int FIRST_ITEM = 1;
    private static final int SECOND_ITEM = 2;
    private static final int THIRD_ITEM = 3;
    private static final int FOURTH_ITEM = 4;
    private static final int FIFTH_ITEM = 5;
    private static final int DEFAULT_PARAM_VALUE = 10;
    private static final int DEFAULT_AGE_VALUE = 30;
    private static final double PI_VALUE = 3.14159;

    private final PythonPatternMatcher matcher = new PythonPatternMatcher();

    @Test
    @DisplayName("Should find class declarations in Python code")
    void shouldFindClassDeclarations() {
        // Given
        String pythonCode = """
            class SimpleClass:
                pass

            class ComplexClass(BaseClass):
                pass

            def not_a_class():
                pass

            class IndentedClass:
                pass
        """;

        // When
        Matcher classMatcher = matcher.findClassMatches(pythonCode);

        // Then
        int count = 0;
        while (classMatcher.find()) {
            count++;
            String className = classMatcher.group(1);
            if (count == FIRST_ITEM) {
                assertEquals("SimpleClass", className);
            } else if (count == SECOND_ITEM) {
                assertEquals("ComplexClass", className);
            } else if (count == THIRD_ITEM) {
                assertEquals("IndentedClass", className);
            }
        }
        assertEquals(EXPECTED_CLASS_COUNT, count, "Should find exactly 3 class declarations");
    }

    @Test
    @DisplayName("Should find function declarations in Python code")
    void shouldFindFunctionDeclarations() {
        // Given
        String pythonCode = """
            def simple_function():
                pass

            def function_with_params(a, b=10, *args, **kwargs):
                pass

            class NotAFunction:
                pass

            def indented_function(x):
                pass
        """;

        // When
        Matcher functionMatcher = matcher.findFunctionMatches(pythonCode);

        // Then
        int count = 0;
        while (functionMatcher.find()) {
            count++;
            String functionName = functionMatcher.group(1);
            String params = functionMatcher.group(2);

            if (count == FIRST_ITEM) {
                assertEquals("simple_function", functionName);
                assertEquals("", params);
            } else if (count == SECOND_ITEM) {
                assertEquals("function_with_params", functionName);
                assertEquals("a, b=10, *args, **kwargs", params);
            } else if (count == THIRD_ITEM) {
                assertEquals("indented_function", functionName);
                assertEquals("x", params);
            }
        }
        assertEquals(EXPECTED_FUNCTION_COUNT, count, "Should find exactly 3 function declarations");
    }

    @Test
    @DisplayName("Should find variable assignments in Python code")
    void shouldFindVariableAssignments() {
        // Given
        String pythonCode = """
            x = 10
            name = "John"
            PI = 3.14159
            complex_var = {"key": "value"}
            not_a_variable
        """;

        // When
        Matcher variableMatcher = matcher.findVariableMatches(pythonCode);

        // Then
        int count = 0;
        while (variableMatcher.find()) {
            count++;
            String variableName = variableMatcher.group(1);
            String value = variableMatcher.group(2);

            if (count == FIRST_ITEM) {
                assertEquals("x", variableName);
                assertEquals("10", value);
            } else if (count == SECOND_ITEM) {
                assertEquals("name", variableName);
                assertEquals("\"John\"", value);
            } else if (count == THIRD_ITEM) {
                assertEquals("PI", variableName);
                assertEquals("3.14159", value);
            } else if (count == FOURTH_ITEM) {
                assertEquals("complex_var", variableName);
                assertEquals("{\"key\": \"value\"}", value);
            }
        }
        assertEquals(EXPECTED_VARIABLE_COUNT, count, "Should find exactly 4 variable assignments");
    }

    @Test
    @DisplayName("Should extract docstrings from Python code")
    void shouldExtractDocstrings() {
        // Given
        String withTripleSingleQuotes = """
            def function():
                '''
                This is a docstring with triple single quotes
                Multiple lines
                '''
                pass
        """;

        String withTripleDoubleQuotes = """
            def function():
                \"\"\"This is a docstring with triple double quotes\"\"\"
                pass
        """;

        String withNoDocstring = """
            def function():
                # Just a comment
                pass
        """;

        // When
        String docstring1 = matcher.findDocstring(withTripleSingleQuotes);
        String docstring2 = matcher.findDocstring(withTripleDoubleQuotes);
        String docstring3 = matcher.findDocstring(withNoDocstring);

        // Then
        // Just check if docstrings were found or not, the exact format might vary
        assertTrue(docstring1.contains("This is a docstring with triple single quotes"));
        assertTrue(docstring1.contains("Multiple lines"));
        assertTrue(docstring2.contains("This is a docstring with triple double quotes"));
        assertEquals("", docstring3);
    }

    @Test
    @DisplayName("Should extract parameters from function signatures")
    void shouldExtractParameters() {
        // Given
        String emptyParams = "";
        String singleParam = "x";
        String multipleParams = "a, b, c";
        String complexParams = "self, name, age=30, *args, **kwargs";

        // When
        String[] result1 = matcher.extractParameters(emptyParams);
        String[] result2 = matcher.extractParameters(singleParam);
        String[] result3 = matcher.extractParameters(multipleParams);
        String[] result4 = matcher.extractParameters(complexParams);
        String[] result5 = matcher.extractParameters(null);

        // Then
        assertEquals(0, result1.length);

        assertEquals(1, result2.length);
        assertEquals("x", result2[0]);

        assertEquals(EXPECTED_PARAM_COUNT, result3.length);
        assertEquals("a", result3[0]);
        assertEquals("b", result3[1]);
        assertEquals("c", result3[2]);

        assertEquals(EXPECTED_COMPLEX_PARAM_COUNT, result4.length);
        assertEquals("self", result4[0]);
        assertEquals("name", result4[1]);
        assertEquals("age=30", result4[2]);
        assertEquals("*args", result4[EXPECTED_CLASS_COUNT]);
        assertEquals("**kwargs", result4[FOURTH_ITEM]);

        assertEquals(0, result5.length);
    }
}

