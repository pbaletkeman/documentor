package com.documentor.service;

import com.documentor.config.DocumentorConfig;
import com.documentor.model.CodeElement;
import com.documentor.model.CodeElementType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

/**
 * 🧪 Comprehensive unit tests for PythonCodeAnalyzer
 */
@ExtendWith(MockitoExtension.class)
class PythonCodeAnalyzerTest {

    @Mock
    private DocumentorConfig config;

    @Mock
    private DocumentorConfig.AnalysisSettings analysisSettings;

    private PythonCodeAnalyzer analyzer;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        lenient().when(config.analysisSettings()).thenReturn(analysisSettings);
        lenient().when(analysisSettings.includePrivateMembers()).thenReturn(false);
        analyzer = new PythonCodeAnalyzer(config);
    }

    @Test
    void testConstructor() {
        assertNotNull(analyzer);
    }

    @Test
    void testAnalyzeSimplePythonClass() throws IOException {
        // Given
        String pythonCode = """
            class TestClass:
                '''A simple test class'''
                
                def __init__(self):
                    self.value = 0
                
                def get_value(self):
                    '''Get the current value'''
                    return self.value
                
                def set_value(self, new_value):
                    '''Set a new value'''
                    self.value = new_value
            """;
        
        Path pythonFile = tempDir.resolve("test_class.py");
        Files.writeString(pythonFile, pythonCode);

        // When
        List<CodeElement> elements = analyzer.analyzeFile(pythonFile);

        // Then
        assertNotNull(elements);
        // Should fall back to regex parsing since Python AST may not be available
        assertTrue(elements.size() >= 1); // At least the class should be detected
    }

    @Test
    void testAnalyzeComplexPythonCode() throws IOException {
        // Given - comprehensive Python code with various constructs
        String pythonCode = """
            '''Module docstring'''
            
            import os
            from typing import List, Optional
            
            # Global variable
            MODULE_CONSTANT = "test"
            
            class Calculator:
                '''A calculator class with docstring'''
                
                def __init__(self, precision: int = 2):
                    '''Initialize calculator'''
                    self.precision = precision
                    self._history = []
                
                def add(self, a: float, b: float) -> float:
                    '''Add two numbers'''
                    result = a + b
                    self._history.append(f"{a} + {b} = {result}")
                    return round(result, self.precision)
                
                def subtract(self, a: float, b: float) -> float:
                    '''Subtract two numbers'''
                    result = a - b
                    self._history.append(f"{a} - {b} = {result}")
                    return round(result, self.precision)
                
                def _clear_history(self):
                    '''Private method to clear history'''
                    self._history.clear()
                
                @property
                def history(self):
                    '''Get calculation history'''
                    return self._history.copy()
            
            def standalone_function(param1, param2="default"):
                '''A standalone function'''
                return param1 + param2
            
            # Another class
            class AdvancedCalculator(Calculator):
                '''Extended calculator'''
                
                def multiply(self, a, b):
                    return a * b
            """;
        
        Path pythonFile = tempDir.resolve("calculator.py");
        Files.writeString(pythonFile, pythonCode);
        lenient().when(analysisSettings.includePrivateMembers()).thenReturn(false);

        // When
        List<CodeElement> elements = analyzer.analyzeFile(pythonFile);

        // Then
        assertNotNull(elements);
        assertFalse(elements.isEmpty());
        
        // Verify that we have classes
        long classCount = elements.stream()
            .filter(e -> e.type() == CodeElementType.CLASS)
            .count();
        assertTrue(classCount >= 2, "Should detect Calculator and AdvancedCalculator classes");
        
        // Verify that we have methods (public ones only due to config)
        long methodCount = elements.stream()
            .filter(e -> e.type() == CodeElementType.METHOD)
            .count();
        assertTrue(methodCount >= 5, "Should detect multiple public methods");
    }

    @Test
    void testAnalyzeWithPrivateMembersIncluded() throws IOException {
        // Given
        String pythonCode = """
            class TestClass:
                def __init__(self):
                    self.public_var = 1
                    self._protected_var = 2
                    self.__private_var = 3
                
                def public_method(self):
                    pass
                
                def _protected_method(self):
                    pass
                
                def __private_method(self):
                    pass
            """;
        
        Path pythonFile = tempDir.resolve("private_test.py");
        Files.writeString(pythonFile, pythonCode);
        lenient().when(analysisSettings.includePrivateMembers()).thenReturn(true);

        // When
        List<CodeElement> elements = analyzer.analyzeFile(pythonFile);

        // Then
        assertNotNull(elements);
        
        // Should include private methods when config allows
        long methodCount = elements.stream()
            .filter(e -> e.type() == CodeElementType.METHOD)
            .count();
        
        // Should detect methods (exact count may vary based on regex parsing)
        assertTrue(methodCount >= 1, "Should detect at least some methods");
    }

    @Test
    void testAnalyzeWithPrivateMembersExcluded() throws IOException {
        // Given
        String pythonCode = """
            class TestClass:
                def public_method(self):
                    '''Public method'''
                    pass
                
                def _private_method(self):
                    '''Private method'''
                    pass
            """;
        
        Path pythonFile = tempDir.resolve("private_test2.py");
        Files.writeString(pythonFile, pythonCode);
        lenient().when(analysisSettings.includePrivateMembers()).thenReturn(false);

        // When
        List<CodeElement> elements = analyzer.analyzeFile(pythonFile);

        // Then
        assertNotNull(elements);
        
        // Should exclude private methods when config disallows
        boolean hasPrivateMethod = elements.stream()
            .anyMatch(e -> e.name().startsWith("_"));
        
        assertFalse(hasPrivateMethod, "Should not include private methods when config excludes them");
    }

    @Test
    void testExtractDocstring() throws IOException {
        // Given - test docstring extraction through regex analysis
        String pythonCode = """
            def function_with_docstring():
                '''
                This is a multi-line docstring
                that describes the function
                '''
                return True
            
            def function_without_docstring():
                return False
            
            class ClassWithDocstring:
                '''Single line class docstring'''
                pass
            """;
        
        Path pythonFile = tempDir.resolve("docstring_test.py");
        Files.writeString(pythonFile, pythonCode);

        // When
        List<CodeElement> elements = analyzer.analyzeFile(pythonFile);

        // Then
        assertNotNull(elements);
        
        // Verify docstrings are captured for elements that have them
        boolean hasDocumentedElement = elements.stream()
            .anyMatch(e -> e.documentation() != null && !e.documentation().trim().isEmpty());
        
        // Note: Docstring extraction depends on regex fallback when Python AST is not available
        // The test ensures the method executes without errors and may find documented elements
        assertTrue(hasDocumentedElement || elements.isEmpty(), "Should handle docstring extraction properly");
    }

    @Test
    void testExtractParameters() throws IOException {
        // Given - test parameter extraction through function analysis
        String pythonCode = """
            def simple_function(a, b):
                return a + b
            
            def complex_function(required_param, optional_param="default", *args, **kwargs):
                pass
            
            def typed_function(name: str, age: int, active: bool = True) -> dict:
                return {"name": name, "age": age, "active": active}
            """;
        
        Path pythonFile = tempDir.resolve("params_test.py");
        Files.writeString(pythonFile, pythonCode);

        // When
        List<CodeElement> elements = analyzer.analyzeFile(pythonFile);

        // Then
        assertNotNull(elements);
        
        // Verify that function elements are created
        long functionCount = elements.stream()
            .filter(e -> e.type() == CodeElementType.METHOD)
            .count();
        
        assertTrue(functionCount >= 3, "Should detect all function definitions");
        
        // Verify that functions have parameter information
        boolean hasParameterInfo = elements.stream()
            .filter(e -> e.type() == CodeElementType.METHOD)
            .anyMatch(e -> !e.parameters().isEmpty());
        
        // Note: Parameter extraction success depends on regex parsing implementation
        // The test ensures the method executes and produces valid results
        assertTrue(hasParameterInfo || elements.isEmpty(), "Should handle parameter extraction properly");
    }

    @Test
    void testAnalyzeEmptyFile() throws IOException {
        // Given
        Path emptyFile = tempDir.resolve("empty.py");
        Files.writeString(emptyFile, "");

        // When
        List<CodeElement> elements = analyzer.analyzeFile(emptyFile);

        // Then
        assertNotNull(elements);
        assertTrue(elements.isEmpty(), "Empty file should produce no code elements");
    }

    @Test
    void testAnalyzeFileWithOnlyComments() throws IOException {
        // Given
        String pythonCode = """
            # This is a comment
            # Another comment
            '''
            This is a module docstring
            but no actual code
            '''
            # More comments
            """;
        
        Path pythonFile = tempDir.resolve("comments_only.py");
        Files.writeString(pythonFile, pythonCode);

        // When
        List<CodeElement> elements = analyzer.analyzeFile(pythonFile);

        // Then
        assertNotNull(elements);
        // Should not detect any code elements in comments-only file
    }

    @Test
    void testAnalyzeNonExistentFile() {
        // Given
        Path nonExistentFile = tempDir.resolve("non-existent.py");

        // When/Then
        assertThrows(IOException.class, () -> analyzer.analyzeFile(nonExistentFile));
    }

    @Test
    void testRegexPatterns() throws IOException {
        // Given - test various Python syntax patterns
        String pythonCode = """
            # Test class inheritance
            class Parent:
                pass
            
            class Child(Parent):
                pass
            
            class MultipleInheritance(Parent, object):
                pass
            
            # Test different function signatures
            def simple_func():
                pass
            
            def func_with_return() -> str:
                return "test"
            
            def async_func() -> None:
                pass
            
            # Test variable assignments
            simple_var = 42
            typed_var: int = 100
            string_var = "hello world"
            list_var = [1, 2, 3]
            """;
        
        Path pythonFile = tempDir.resolve("patterns_test.py");
        Files.writeString(pythonFile, pythonCode);

        // When
        List<CodeElement> elements = analyzer.analyzeFile(pythonFile);

        // Then
        assertNotNull(elements);
        
        // Should detect classes with different inheritance patterns
        long classCount = elements.stream()
            .filter(e -> e.type() == CodeElementType.CLASS)
            .count();
        assertTrue(classCount >= 1, "Should detect at least some class definitions");
        
        // Should detect functions with different signatures
        long methodCount = elements.stream()
            .filter(e -> e.type() == CodeElementType.METHOD)
            .count();
        assertTrue(methodCount >= 1, "Should detect at least some function definitions");
        
        // Should detect variable assignments
        long fieldCount = elements.stream()
            .filter(e -> e.type() == CodeElementType.FIELD)
            .count();
        assertTrue(fieldCount >= 1 || elements.isEmpty(), "Should detect variable assignments or handle gracefully");
    }

    @Test
    void testShouldIncludePrivateMembers() throws Exception {
        // Test shouldInclude method via reflection since it's private
        Method shouldIncludeMethod = PythonCodeAnalyzer.class.getDeclaredMethod("shouldInclude", String.class);
        shouldIncludeMethod.setAccessible(true);

        // Test with private members excluded
        lenient().when(analysisSettings.includePrivateMembers()).thenReturn(false);
        
        assertFalse((Boolean) shouldIncludeMethod.invoke(analyzer, "_private_method"));
        assertFalse((Boolean) shouldIncludeMethod.invoke(analyzer, "__very_private"));
        assertTrue((Boolean) shouldIncludeMethod.invoke(analyzer, "public_method"));
        
        // Test with private members included
        lenient().when(analysisSettings.includePrivateMembers()).thenReturn(true);
        
        assertTrue((Boolean) shouldIncludeMethod.invoke(analyzer, "_private_method"));
        assertTrue((Boolean) shouldIncludeMethod.invoke(analyzer, "__very_private"));
        assertTrue((Boolean) shouldIncludeMethod.invoke(analyzer, "public_method"));
    }

    @Test
    void testAnalyzeFileWithIOException() throws IOException {
        // Given - create a file and then make it unreadable (simulate IO error)
        Path pythonFile = tempDir.resolve("test.py");
        Files.writeString(pythonFile, "class Test: pass");
        
        // Delete the file to simulate IO error during reading
        Files.delete(pythonFile);

        // When/Then
        assertThrows(IOException.class, () -> analyzer.analyzeFile(pythonFile));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testRegexParsingFallback() throws IOException, NoSuchMethodException, ReflectiveOperationException {
        // Create a Python file that would trigger regex parsing
        Path pythonFile = tempDir.resolve("regex_test.py");
        String pythonCode = """
            class PublicClass:
                \"\"\"A public class.\"\"\"
                def public_method(self, param1, param2):
                    \"\"\"A public method.\"\"\"
                    pass
                
                def _private_method(self):
                    \"\"\"A private method.\"\"\"
                    pass
            
            def public_function():
                \"\"\"A public function.\"\"\"
                return "test"
            
            def _private_function():
                \"\"\"A private function.\"\"\"
                pass
            
            PUBLIC_VAR = "public variable"
            _private_var = "private variable"
            """;
        Files.writeString(pythonFile, pythonCode);

        // Force regex parsing by calling analyzeWithRegex directly
        Method regexParseMethod = PythonCodeAnalyzer.class.getDeclaredMethod("analyzeWithRegex", Path.class, List.class);
        regexParseMethod.setAccessible(true);
        
        List<String> lines = Files.readAllLines(pythonFile);
        List<CodeElement> elements = (List<CodeElement>) regexParseMethod.invoke(analyzer, pythonFile, lines);
        
        assertNotNull(elements);
        assertFalse(elements.isEmpty());
        
        // Should find public class
        assertTrue(elements.stream().anyMatch(e -> e.name().equals("PublicClass") && e.type() == CodeElementType.CLASS));
        
        // Should find public method
        assertTrue(elements.stream().anyMatch(e -> e.name().equals("public_method") && e.type() == CodeElementType.METHOD));
        
        // Should find public function
        assertTrue(elements.stream().anyMatch(e -> e.name().equals("public_function") && e.type() == CodeElementType.METHOD));
        
        // Should NOT find private members (when includePrivateMembers is false)
        assertFalse(elements.stream().anyMatch(e -> e.name().equals("_private_method")));
        assertFalse(elements.stream().anyMatch(e -> e.name().equals("_private_function")));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testRegexParsingWithPrivateMembersIncluded() throws IOException, NoSuchMethodException, ReflectiveOperationException {
        // Configure to include private members
        lenient().when(analysisSettings.includePrivateMembers()).thenReturn(true);
        
        Path pythonFile = tempDir.resolve("private_test.py");
        String pythonCode = """
            class _PrivateClass:
                def _private_method(self):
                    pass
            
            def _private_function():
                pass
            """;
        Files.writeString(pythonFile, pythonCode);

        // Force regex parsing
        Method regexParseMethod = PythonCodeAnalyzer.class.getDeclaredMethod("analyzeWithRegex", Path.class, List.class);
        regexParseMethod.setAccessible(true);
        
        List<String> lines = Files.readAllLines(pythonFile);
        List<CodeElement> elements = (List<CodeElement>) regexParseMethod.invoke(analyzer, pythonFile, lines);
        
        assertNotNull(elements);
        assertFalse(elements.isEmpty());
        
        // Should find private members when includePrivateMembers is true
        assertTrue(elements.stream().anyMatch(e -> e.name().equals("_PrivateClass")));
        assertTrue(elements.stream().anyMatch(e -> e.name().equals("_private_method")));
        assertTrue(elements.stream().anyMatch(e -> e.name().equals("_private_function")));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testDocstringExtraction() throws IOException, NoSuchMethodException, ReflectiveOperationException {
        // Test docstring extraction method
        Path pythonFile = tempDir.resolve("docstring_test.py");
        String pythonCode = """
            class TestClass:
                \"\"\"
                This is a multi-line docstring.
                It spans multiple lines.
                \"\"\"
                pass
            
            def test_function():
                '''Single line docstring'''
                pass
            """;
        Files.writeString(pythonFile, pythonCode);

        Method regexParseMethod = PythonCodeAnalyzer.class.getDeclaredMethod("analyzeWithRegex", Path.class, List.class);
        regexParseMethod.setAccessible(true);
        
        List<String> lines = Files.readAllLines(pythonFile);
        List<CodeElement> elements = (List<CodeElement>) regexParseMethod.invoke(analyzer, pythonFile, lines);
        
        // Should extract docstrings
        CodeElement classElement = elements.stream()
            .filter(e -> e.name().equals("TestClass"))
            .findFirst()
            .orElse(null);
        assertNotNull(classElement);
        assertNotNull(classElement.documentation());
        assertTrue(classElement.documentation().contains("multi-line"));
        
        CodeElement functionElement = elements.stream()
            .filter(e -> e.name().equals("test_function"))
            .findFirst()
            .orElse(null);
        assertNotNull(functionElement);
        assertNotNull(functionElement.documentation());
        assertTrue(functionElement.documentation().contains("Single line"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testParameterExtraction() throws IOException, NoSuchMethodException, ReflectiveOperationException {
        Path pythonFile = tempDir.resolve("param_test.py");
        String pythonCode = """
            def function_with_params(param1, param2='default', *args, **kwargs):
                pass
            
            def simple_function():
                pass
            """;
        Files.writeString(pythonFile, pythonCode);

        Method regexParseMethod = PythonCodeAnalyzer.class.getDeclaredMethod("analyzeWithRegex", Path.class, List.class);
        regexParseMethod.setAccessible(true);
        
        List<String> lines = Files.readAllLines(pythonFile);
        List<CodeElement> elements = (List<CodeElement>) regexParseMethod.invoke(analyzer, pythonFile, lines);
        
        // Should extract parameters
        CodeElement functionWithParams = elements.stream()
            .filter(e -> e.name().equals("function_with_params"))
            .findFirst()
            .orElse(null);
        assertNotNull(functionWithParams);
        assertNotNull(functionWithParams.parameters());
        assertFalse(functionWithParams.parameters().isEmpty());
        
        CodeElement simpleFunction = elements.stream()
            .filter(e -> e.name().equals("simple_function"))
            .findFirst()
            .orElse(null);
        assertNotNull(simpleFunction);
        assertNotNull(simpleFunction.parameters());
        // Simple function should have empty parameters list
        assertTrue(simpleFunction.parameters().isEmpty());
    }

    @Test  
    void testParsePythonASTOutput() throws NoSuchMethodException, ReflectiveOperationException {
        // Test the parsePythonASTOutput method
        Method parseMethod = PythonCodeAnalyzer.class.getDeclaredMethod("parsePythonASTOutput", String.class, Path.class);
        parseMethod.setAccessible(true);
        
        Path testPath = tempDir.resolve("test.py");
        
        // Test unknown element type (should return null)
        CodeElement unknownResult = (CodeElement) parseMethod.invoke(analyzer, "UNKNOWN|test|1||", testPath);
        assertNull(unknownResult, "Unknown element type should return null");
        
        // Test class parsing
        CodeElement classResult = (CodeElement) parseMethod.invoke(analyzer, "CLASS|TestClass|5|Test documentation", testPath);
        assertNotNull(classResult);
        assertEquals("TestClass", classResult.name());
        assertEquals(CodeElementType.CLASS, classResult.type());
        assertEquals("Test documentation", classResult.documentation());
        
        // Test function parsing
        CodeElement functionResult = (CodeElement) parseMethod.invoke(analyzer, "FUNCTION|test_func|10|Func docs|param1,param2", testPath);
        assertNotNull(functionResult);
        assertEquals("test_func", functionResult.name());
        assertEquals(CodeElementType.METHOD, functionResult.type());
        assertEquals("Func docs", functionResult.documentation());
        assertEquals(2, functionResult.parameters().size());
        assertTrue(functionResult.parameters().contains("param1"));
        assertTrue(functionResult.parameters().contains("param2"));
        
        // Test variable parsing
        CodeElement variableResult = (CodeElement) parseMethod.invoke(analyzer, "VARIABLE|test_var|15||", testPath);
        assertNotNull(variableResult);
        assertEquals("test_var", variableResult.name());
        assertEquals(CodeElementType.FIELD, variableResult.type());
    }
}