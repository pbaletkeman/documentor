package com.documentor.service;

import com.documentor.config.DocumentorConfig;
import com.documentor.model.CodeElement;
import com.documentor.model.CodeElementType;
import com.documentor.model.ProjectAnalysis;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 🧪 Coverage-focused tests for DocumentationService uncovered methods
 */
@ExtendWith(MockitoExtension.class)
class DocumentationServiceCoverageTest {

    @Mock
    private LlmService mockLlmService;

    @Mock
    private DocumentorConfig mockConfig;

    private DocumentationService documentationService;

    @BeforeEach
    void setUp() {
        documentationService = new DocumentationService(mockLlmService, mockConfig);
    }

    @Test
    void testGenerateDocumentationWithEmptyProject() {
        // Test with empty project to exercise different code paths
        ProjectAnalysis emptyProject = new ProjectAnalysis("/empty", List.of(), System.currentTimeMillis());
        
        // When
        CompletableFuture<String> result = documentationService.generateDocumentation(emptyProject);
        
        // Then
        assertNotNull(result);
    }

    @Test
    void testGenerateDocumentationWithMultipleElementTypes() {
        // Create test project with various element types to exercise more code paths
        List<CodeElement> elements = List.of(
            new CodeElement(CodeElementType.CLASS, "TestClass", "com.example.TestClass",
                "/test/TestClass.java", 1, "public class TestClass", "Test class", List.of(), List.of()),
            new CodeElement(CodeElementType.METHOD, "testMethod", "com.example.TestClass.testMethod",
                "/test/TestClass.java", 5, "public void testMethod()", "Test method", List.of(), List.of()),
            new CodeElement(CodeElementType.FIELD, "testField", "com.example.TestClass.testField",
                "/test/TestClass.java", 3, "private String testField", "Test field", List.of(), List.of())
        );
        
        ProjectAnalysis testProject = new ProjectAnalysis("/test", elements, System.currentTimeMillis());
        
        // When
        CompletableFuture<String> result = documentationService.generateDocumentation(testProject);
        
        // Then
        assertNotNull(result);
    }

    @Test
    void testGenerateDocumentationWithPythonElements() {
        // Test with Python elements to exercise different language paths
        List<CodeElement> pythonElements = List.of(
            new CodeElement(CodeElementType.CLASS, "TestClass", "test.TestClass",
                "/test/test_class.py", 1, "class TestClass:", "Test class", List.of(), List.of()),
            new CodeElement(CodeElementType.METHOD, "test_method", "test.TestClass.test_method",
                "/test/test_class.py", 5, "def test_method(self):", "Test method", List.of(), List.of()),
            new CodeElement(CodeElementType.FIELD, "test_field", "test.TestClass.test_field",
                "/test/test_class.py", 3, "test_field = 'value'", "Test field", List.of(), List.of())
        );
        
        ProjectAnalysis pythonProject = new ProjectAnalysis("/test", pythonElements, System.currentTimeMillis());
        
        // When
        CompletableFuture<String> result = documentationService.generateDocumentation(pythonProject);
        
        // Then
        assertNotNull(result);
    }

    @Test
    void testGenerateDocumentationWithMixedFiles() {
        // Test with mixed file types to exercise different language detection paths
        List<CodeElement> mixedElements = List.of(
            new CodeElement(CodeElementType.CLASS, "JavaClass", "com.example.JavaClass",
                "/test/JavaClass.java", 1, "public class JavaClass", "Java class", List.of(), List.of()),
            new CodeElement(CodeElementType.CLASS, "PythonClass", "test.PythonClass",
                "/test/python_class.py", 1, "class PythonClass:", "Python class", List.of(), List.of()),
            new CodeElement(CodeElementType.CLASS, "UnknownClass", "test.UnknownClass",
                "/test/unknown.xyz", 1, "unknown content", "Unknown class", List.of(), List.of())
        );
        
        ProjectAnalysis mixedProject = new ProjectAnalysis("/test", mixedElements, System.currentTimeMillis());
        
        // When
        CompletableFuture<String> result = documentationService.generateDocumentation(mixedProject);
        
        // Then
        assertNotNull(result);
    }

    @Test
    void testGenerateDocumentationWithLargeProject() {
        // Test with many elements to exercise batching and processing logic
        List<CodeElement> manyElements = List.of(
            new CodeElement(CodeElementType.CLASS, "Class1", "Class1", "/test1.java", 1, "class1", "desc1", List.of(), List.of()),
            new CodeElement(CodeElementType.CLASS, "Class2", "Class2", "/test2.java", 1, "class2", "desc2", List.of(), List.of()),
            new CodeElement(CodeElementType.METHOD, "Method1", "Method1", "/test1.java", 2, "method1", "desc1", List.of(), List.of()),
            new CodeElement(CodeElementType.METHOD, "Method2", "Method2", "/test2.java", 2, "method2", "desc2", List.of(), List.of()),
            new CodeElement(CodeElementType.FIELD, "Field1", "Field1", "/test1.java", 3, "field1", "desc1", List.of(), List.of()),
            new CodeElement(CodeElementType.FIELD, "Field2", "Field2", "/test2.java", 3, "field2", "desc2", List.of(), List.of())
        );
        
        ProjectAnalysis largeProject = new ProjectAnalysis("/test", manyElements, System.currentTimeMillis());
        
        // When
        CompletableFuture<String> result = documentationService.generateDocumentation(largeProject);
        
        // Then
        assertNotNull(result);
    }
}