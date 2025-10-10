package com.documentor.service.python;

import com.documentor.config.DocumentorConfig;
import com.documentor.config.model.AnalysisSettings;
import com.documentor.model.CodeElement;
import com.documentor.model.CodeElementType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("Python Regex Analyzer Tests")
class PythonRegexAnalyzerTest {

    private DocumentorConfig mockConfig;
    private AnalysisSettings mockAnalysisSettings;
    private PythonElementExtractor mockElementExtractor;
    private PythonPatternMatcher mockPatternMatcher;
    private PythonRegexAnalyzer regexAnalyzer;

    private Path testFilePath;

    @BeforeEach
    void setUp() {
        mockConfig = mock(DocumentorConfig.class);
        mockAnalysisSettings = mock(AnalysisSettings.class);
        mockElementExtractor = mock(PythonElementExtractor.class);
        mockPatternMatcher = mock(PythonPatternMatcher.class);

        when(mockConfig.analysisSettings()).thenReturn(mockAnalysisSettings);

        // Setup default mocks for line numbers
        when(mockElementExtractor.extractDocstring(any(), anyInt())).thenReturn("");

        regexAnalyzer = new PythonRegexAnalyzer(mockConfig, mockElementExtractor, mockPatternMatcher);
        testFilePath = Path.of("test_file.py");
    }

    @Test
    @DisplayName("Should handle empty Python file")
    void shouldHandleEmptyPythonFile() {
        // Given
        List<String> emptyLines = List.of("");  // Use a single empty line instead of an empty list

        // Setup pattern matchers to return no matches but with appropriate behavior
        Matcher mockClassMatcher = mock(Matcher.class);
        Matcher mockFunctionMatcher = mock(Matcher.class);
        Matcher mockVariableMatcher = mock(Matcher.class);

        // Setup the mocks to return false for find() to avoid IndexOutOfBoundsException
        when(mockClassMatcher.find()).thenReturn(false);
        when(mockFunctionMatcher.find()).thenReturn(false);
        when(mockVariableMatcher.find()).thenReturn(false);

        // Configure the mockPatternMatcher to return our controlled matchers
        when(mockPatternMatcher.findClassMatches(anyString())).thenReturn(mockClassMatcher);
        when(mockPatternMatcher.findFunctionMatches(anyString())).thenReturn(mockFunctionMatcher);
        when(mockPatternMatcher.findVariableMatches(anyString())).thenReturn(mockVariableMatcher);

        // When
        List<CodeElement> elements = regexAnalyzer.analyzeWithRegex(testFilePath, emptyLines);

        // Then
        assertTrue(elements.isEmpty(), "Empty file should produce no elements");
    }

    @Test
    @DisplayName("Should exclude private elements when configuration says so")
    void shouldExcludePrivateElements() {
        // Given
        List<String> testFileLines = Arrays.asList(
            "class PublicClass:",
            "    pass",
            "class _PrivateClass:",
            "    pass",
            "def public_function():",
            "    pass",
            "def _private_function():",
            "    pass",
            "public_var = 100",
            "_private_var = 42"
        );

        // Configure to exclude private members
        when(mockAnalysisSettings.includePrivateMembers()).thenReturn(false);

        // Setup mock pattern matchers with real patterns for testing
        setupMockMatchersWithCustomMatches(testFileLines);

        // When
        List<CodeElement> elements = regexAnalyzer.analyzeWithRegex(testFilePath, testFileLines);

        // Then - verify we only have public elements
        assertFalse(elements.isEmpty(), "Should have at least one element");
        for (CodeElement element : elements) {
            assertFalse(element.name().startsWith("_"),
                       "Element should not be private: " + element.name());
        }
    }

    @Test
    @DisplayName("Should include private elements when configuration allows it")
    void shouldIncludePrivateElements() {
        // Given
        List<String> testFileLines = Arrays.asList(
            "class PublicClass:",
            "    pass",
            "class _PrivateClass:",
            "    pass",
            "def public_function():",
            "    pass",
            "def _private_function():",
            "    pass",
            "public_var = 100",
            "_private_var = 42"
        );

        // Configure to include private members
        when(mockAnalysisSettings.includePrivateMembers()).thenReturn(true);

        // Setup mock pattern matchers with real patterns for testing
        setupMockMatchersWithCustomMatches(testFileLines);

        // When
        List<CodeElement> elements = regexAnalyzer.analyzeWithRegex(testFilePath, testFileLines);

        // Then - verify we have both public and private elements
        assertFalse(elements.isEmpty(), "Should have at least one element");

        // Check if we have at least one private element
        boolean hasPrivateElement = elements.stream()
                .anyMatch(element -> element.name().startsWith("_"));

        assertTrue(hasPrivateElement, "Should have at least one private element");
    }

    @Test
    @DisplayName("Should create appropriate CodeElement types")
    void shouldCreateAppropriateElementTypes() {
        // Given
        List<String> testFileLines = Arrays.asList(
            "class TestClass:",
            "    pass",
            "def test_function():",
            "    pass",
            "test_var = 100"
        );

        // Configure to include all members
        when(mockAnalysisSettings.includePrivateMembers()).thenReturn(true);

        // Setup mock pattern matchers with real patterns for testing
        setupMockMatchersWithSimpleMatches();

        // When
        List<CodeElement> elements = regexAnalyzer.analyzeWithRegex(testFilePath, testFileLines);

        // Then - verify correct element types
        assertFalse(elements.isEmpty(), "Should have elements");

        // Count elements by type
        long classCount = elements.stream()
                .filter(e -> e.type() == CodeElementType.CLASS)
                .count();

        long methodCount = elements.stream()
                .filter(e -> e.type() == CodeElementType.METHOD)
                .count();

        long fieldCount = elements.stream()
                .filter(e -> e.type() == CodeElementType.FIELD)
                .count();

        assertEquals(1, classCount, "Should have one class");
        assertEquals(1, methodCount, "Should have one method");
        assertEquals(1, fieldCount, "Should have one field");
    }

    // Helper methods to set up the test

    private void setupMockMatchersWithCustomMatches(List<String> lines) {
        // We'll use the real pattern matcher for this test to avoid complex mocking
        PythonPatternMatcher realMatcher = new PythonPatternMatcher();
        String content = String.join("\n", lines);

        // Find real matches but use our mock to return them
        when(mockPatternMatcher.findClassMatches(anyString()))
            .thenReturn(realMatcher.findClassMatches(content));

        when(mockPatternMatcher.findFunctionMatches(anyString()))
            .thenReturn(realMatcher.findFunctionMatches(content));

        when(mockPatternMatcher.findVariableMatches(anyString()))
            .thenReturn(realMatcher.findVariableMatches(content));

        when(mockPatternMatcher.extractParameters(anyString()))
            .thenReturn(new String[0]);
    }

    private void setupMockMatchersWithSimpleMatches() {
        // Class matcher
        Matcher classMatcher = Mockito.mock(Matcher.class);
        when(classMatcher.find()).thenReturn(true, false);
        when(classMatcher.group()).thenReturn("class TestClass:");
        when(classMatcher.group(1)).thenReturn("TestClass");
        when(classMatcher.start()).thenReturn(0);

        // Function matcher
        Matcher functionMatcher = Mockito.mock(Matcher.class);
        when(functionMatcher.find()).thenReturn(true, false);
        when(functionMatcher.group()).thenReturn("def test_function():");
        when(functionMatcher.group(1)).thenReturn("test_function");
        when(functionMatcher.group(2)).thenReturn("");
        when(functionMatcher.start()).thenReturn(20);

        // Variable matcher
        Matcher variableMatcher = Mockito.mock(Matcher.class);
        when(variableMatcher.find()).thenReturn(true, false);
        when(variableMatcher.group()).thenReturn("test_var = 100");
        when(variableMatcher.group(1)).thenReturn("test_var");
        when(variableMatcher.start()).thenReturn(40);

        when(mockPatternMatcher.findClassMatches(anyString())).thenReturn(classMatcher);
        when(mockPatternMatcher.findFunctionMatches(anyString())).thenReturn(functionMatcher);
        when(mockPatternMatcher.findVariableMatches(anyString())).thenReturn(variableMatcher);
        when(mockPatternMatcher.extractParameters(anyString())).thenReturn(new String[0]);
    }
}
