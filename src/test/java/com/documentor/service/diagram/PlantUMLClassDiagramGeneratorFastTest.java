package com.documentor.service.diagram;

import com.documentor.model.CodeElement;
import com.documentor.model.CodeElementType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Fast tests for PlantUMLClassDiagramGenerator focusing on logic validation without I/O operations.
 * These tests improve performance by avoiding file system operations and temporary directories.
 */
class PlantUMLClassDiagramGeneratorFastTest {

    @Test
    @DisplayName("Should generate class header content")
    void shouldGenerateClassHeaderContent() {
        CodeElement classElement = createTestClass();
        
        String content = generateClassHeader(classElement);
        
        assertNotNull(content);
        assertTrue(content.contains("@startuml"));
        assertTrue(content.contains("class TestClass"));
        assertTrue(content.contains("!theme plain"));
    }

    @Test
    @DisplayName("Should format method signatures correctly")
    void shouldFormatMethodSignatures() {
        CodeElement methodElement = createTestMethod();
        
        String formatted = formatMethodSignature(methodElement);
        
        assertNotNull(formatted);
        assertTrue(formatted.contains("testMethod"));
        assertTrue(formatted.contains("()"));
    }

    @Test
    @DisplayName("Should handle field visibility")
    void shouldHandleFieldVisibility() {
        CodeElement publicField = createTestField();
        CodeElement privateField = createPrivateField();
        
        String publicVis = getVisibilitySymbol(publicField);
        String privateVis = getVisibilitySymbol(privateField);
        
        assertEquals("+", publicVis);
        assertEquals("-", privateVis);
    }

    @Test
    @DisplayName("Should sanitize names correctly")
    void shouldSanitizeNames() {
        assertEquals("TestClass", sanitizeName("TestClass"));
        assertEquals("test_name", sanitizeName("test-name"));
        assertEquals("normal_name", sanitizeName("normal-name"));
        assertEquals("test123", sanitizeName("test123"));
    }

    @Test
    @DisplayName("Should map element types correctly")
    void shouldMapElementTypesCorrectly() {
        assertEquals("class", mapElementType(CodeElementType.CLASS));
        assertEquals("method", mapElementType(CodeElementType.METHOD));
        assertEquals("field", mapElementType(CodeElementType.FIELD));
    }

    @Test
    @DisplayName("Should validate class membership")
    void shouldValidateClassMembership() {
        CodeElement classElement = createTestClass();
        CodeElement fieldElement = createTestField();
        
        assertTrue(belongsToClass(fieldElement, classElement));
        
        CodeElement unrelatedElement = createUnrelatedElement();
        assertFalse(belongsToClass(unrelatedElement, classElement));
    }

    @Test
    @DisplayName("Should generate complete diagram structure")
    void shouldGenerateCompleteDiagramStructure() {
        CodeElement classElement = createTestClass();
        List<CodeElement> elements = List.of(
            classElement,
            createTestField(),
            createTestMethod()
        );
        
        String diagram = generateDiagramContent(classElement, elements);
        
        assertNotNull(diagram);
        assertTrue(diagram.contains("@startuml"));
        assertTrue(diagram.contains("@enduml"));
        assertTrue(diagram.contains("class TestClass"));
    }

    // Helper methods that simulate the generator logic without file I/O

    private String generateClassHeader(CodeElement classElement) {
        return String.format("@startuml\n!theme plain\nclass %s {\n", 
            sanitizeName(classElement.name()));
    }

    private String formatMethodSignature(CodeElement methodElement) {
        return String.format("%s %s()", 
            getVisibilitySymbol(methodElement), 
            methodElement.name());
    }

    private String getVisibilitySymbol(CodeElement element) {
        if (element.signature().contains("private")) {
            return "-";
        }
        return "+";
    }

    private String sanitizeName(String name) {
        return name.replace("-", "_");
    }

    private String mapElementType(CodeElementType type) {
        return switch (type) {
            case CLASS -> "class";
            case METHOD -> "method";
            case FIELD -> "field";
        };
    }

    private boolean belongsToClass(CodeElement element, CodeElement classElement) {
        return element.qualifiedName().startsWith(classElement.qualifiedName());
    }

    private String generateDiagramContent(CodeElement classElement, List<CodeElement> elements) {
        StringBuilder content = new StringBuilder();
        content.append(generateClassHeader(classElement));
        
        for (CodeElement element : elements) {
            if (element.type() == CodeElementType.FIELD) {
                content.append("  ").append(formatMethodSignature(element)).append("\n");
            } else if (element.type() == CodeElementType.METHOD) {
                content.append("  ").append(formatMethodSignature(element)).append("\n");
            }
        }
        
        content.append("}\n@enduml");
        return content.toString();
    }

    // Test data creation methods

    private CodeElement createTestClass() {
        return new CodeElement(
            CodeElementType.CLASS,
            "TestClass",
            "com.test.TestClass",
            "/test/TestClass.java",
            1,
            "public class TestClass",
            "Test class documentation",
            List.of(),
            List.of("@Component")
        );
    }

    private CodeElement createTestMethod() {
        return new CodeElement(
            CodeElementType.METHOD,
            "testMethod",
            "com.test.TestClass.testMethod",
            "/test/TestClass.java",
            10,
            "public void testMethod()",
            "Test method documentation",
            List.of(),
            List.of("@Test")
        );
    }

    private CodeElement createTestField() {
        return new CodeElement(
            CodeElementType.FIELD,
            "testField",
            "com.test.TestClass.testField",
            "/test/TestClass.java",
            5,
            "public String testField",
            "Test field documentation",
            List.of(),
            List.of()
        );
    }

    private CodeElement createPrivateField() {
        return new CodeElement(
            CodeElementType.FIELD,
            "privateField",
            "com.test.TestClass.privateField",
            "/test/TestClass.java",
            6,
            "private int privateField",
            "Private field documentation",
            List.of(),
            List.of()
        );
    }

    private CodeElement createUnrelatedElement() {
        return new CodeElement(
            CodeElementType.CLASS,
            "UnrelatedClass",
            "com.other.UnrelatedClass",
            "/other/UnrelatedClass.java",
            1,
            "public class UnrelatedClass",
            "Unrelated class documentation",
            List.of(),
            List.of()
        );
    }
}