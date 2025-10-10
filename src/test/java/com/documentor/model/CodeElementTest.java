package com.documentor.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 🧪 Unit tests for CodeElement
 */
class CodeElementTest {

    @Test
    void testConstructorAndGetters() {
        // Given
        List<String> parameters = List.of("String arg1", "int arg2");
        List<String> annotations = List.of("@Override", "@Test");

        // When
        CodeElement element = new CodeElement(
            CodeElementType.METHOD,
            "testMethod",
            "com.test.TestClass.testMethod",
            "/test/TestClass.java",
            10,
            "public void testMethod(String arg1, int arg2)",
            "This is a test method",
            parameters,
            annotations
        );

        // Then
        assertNotNull(element);
        assertEquals(CodeElementType.METHOD, element.type());
        assertEquals("testMethod", element.name());
        assertEquals("com.test.TestClass.testMethod", element.qualifiedName());
        assertEquals("/test/TestClass.java", element.filePath());
        assertEquals(10, element.lineNumber());
        assertEquals("public void testMethod(String arg1, int arg2)", element.signature());
        assertEquals("This is a test method", element.documentation());
        assertEquals(parameters, element.parameters());
        assertEquals(annotations, element.annotations());
    }

    @Test
    void testWithEmptyCollections() {
        // When
        CodeElement element = new CodeElement(
            CodeElementType.CLASS,
            "TestClass",
            "com.test.TestClass",
            "/test/TestClass.java",
            1,
            "public class TestClass",
            "",
            List.of(),
            List.of()
        );

        // Then
        assertNotNull(element);
        assertEquals(CodeElementType.CLASS, element.type());
        assertTrue(element.parameters().isEmpty());
        assertTrue(element.annotations().isEmpty());
    }

    @Test
    void testEquality() {
        // Given
        CodeElement element1 = new CodeElement(
            CodeElementType.FIELD,
            "testField",
            "com.test.TestClass.testField",
            "/test/TestClass.java",
            5,
            "private String testField",
            "A test field",
            List.of(),
            List.of("@Autowired")
        );
        
        CodeElement element2 = new CodeElement(
            CodeElementType.FIELD,
            "testField",
            "com.test.TestClass.testField",
            "/test/TestClass.java",
            5,
            "private String testField",
            "A test field",
            List.of(),
            List.of("@Autowired")
        );

        // Then
        assertEquals(element1, element2);
        assertEquals(element1.hashCode(), element2.hashCode());
    }

    @Test
    void testToString() {
        // Given
        CodeElement element = new CodeElement(
            CodeElementType.METHOD,
            "toString",
            "com.test.TestClass.toString",
            "/test/TestClass.java",
            15,
            "public String toString()",
            "Returns string representation",
            List.of(),
            List.of("@Override")
        );

        // When
        String result = element.toString();

        // Then
        assertNotNull(result);
        assertTrue(result.contains("CodeElement"));
        assertTrue(result.contains("toString"));
    }

    @Test
    void testGetId() {
        // Given
        CodeElement element = new CodeElement(
            CodeElementType.CLASS,
            "TestClass",
            "com.test.TestClass",
            "/src/test/TestClass.java",
            10,
            "public class TestClass",
            "Test class",
            List.of(),
            List.of()
        );

        // When
        String id = element.getId();

        // Then
        assertEquals("/src/test/TestClass.java:com.test.TestClass:10", id);
    }

    @Test
    void testIsPublicTrue() {
        // Given - Java public method
        CodeElement publicElement = new CodeElement(
            CodeElementType.METHOD,
            "publicMethod",
            "com.test.TestClass.publicMethod",
            "/test/TestClass.java",
            5,
            "public void publicMethod()",
            "Public method",
            List.of(),
            List.of()
        );

        // Then
        assertTrue(publicElement.isPublic());
    }

    @Test
    void testIsPublicFalse() {
        // Given - Java private method
        CodeElement privateElement = new CodeElement(
            CodeElementType.METHOD,
            "privateMethod",
            "com.test.TestClass.privateMethod",
            "/test/TestClass.java",
            5,
            "private void privateMethod()",
            "Private method",
            List.of(),
            List.of()
        );

        // Then
        assertFalse(privateElement.isPublic());
    }

    @Test
    void testIsPublicFalseForPythonPrivate() {
        // Given - Python private method (underscore convention)
        CodeElement pythonPrivateElement = new CodeElement(
            CodeElementType.METHOD,
            "_private_method",
            "test_module._private_method",
            "/test/test_module.py",
            5,
            "def _private_method(self):",
            "Private method in Python",
            List.of(),
            List.of()
        );

        // Then
        assertFalse(pythonPrivateElement.isPublic());
    }

    @Test
    void testGetDisplayName() {
        // Given
        CodeElement element = new CodeElement(
            CodeElementType.METHOD,
            "testMethod",
            "com.test.TestClass.testMethod",
            "/src/main/java/com/test/TestClass.java",
            25,
            "public void testMethod()",
            "Test method",
            List.of(),
            List.of()
        );

        // When
        String displayName = element.getDisplayName();

        // Then
        assertTrue(displayName.contains("🔧")); // METHOD icon
        assertTrue(displayName.contains("com.test.TestClass.testMethod"));
        assertTrue(displayName.contains("TestClass.java"));
        assertTrue(displayName.contains("25"));
    }

    @Test
    void testGetAnalysisContextComplete() {
        // Given
        CodeElement element = new CodeElement(
            CodeElementType.METHOD,
            "processData",
            "com.service.DataProcessor.processData",
            "/src/DataProcessor.java",
            15,
            "public String processData(String input, int flags)",
            "Processes the input data with given flags",
            List.of("String input", "int flags"),
            List.of("@Override", "@Transactional")
        );

        // When
        String context = element.getAnalysisContext();

        // Then
        assertTrue(context.contains("Type: Method"));
        assertTrue(context.contains("Name: processData"));
        assertTrue(context.contains("Signature: public String processData(String input, int flags)"));
        assertTrue(context.contains("Parameters: String input, int flags"));
        assertTrue(context.contains("Documentation: Processes the input data with given flags"));
        assertTrue(context.contains("Annotations: @Override, @Transactional"));
    }

    @Test
    void testGetAnalysisContextMinimal() {
        // Given
        CodeElement element = new CodeElement(
            CodeElementType.FIELD,
            "simpleField",
            "com.test.TestClass.simpleField",
            "/test/TestClass.java",
            5,
            "private String simpleField",
            "",
            List.of(),
            List.of()
        );

        // When
        String context = element.getAnalysisContext();

        // Then
        assertTrue(context.contains("Type: Field"));
        assertTrue(context.contains("Name: simpleField"));
        assertTrue(context.contains("Signature: private String simpleField"));
        assertFalse(context.contains("Parameters:"));
        assertFalse(context.contains("Documentation:"));
        assertFalse(context.contains("Annotations:"));
    }
}
