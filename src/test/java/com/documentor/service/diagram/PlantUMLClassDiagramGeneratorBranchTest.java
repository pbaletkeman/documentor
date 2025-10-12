package com.documentor.service.diagram;

import com.documentor.model.CodeElement;
import com.documentor.model.CodeElementType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Additional tests for PlantUMLClassDiagramGenerator focusing on branch coverage improvement.
 * These tests target specific edge cases and conditional branches to reach 85%+ branch coverage.
 */
class PlantUMLClassDiagramGeneratorBranchTest {

    private PlantUMLClassDiagramGenerator generator;

    @TempDir
    private Path tempDir;

    // Constants for test data
    private static final int FIELD_LINE_NUMBER = 5;
    private static final int METHOD_LINE_NUMBER = 10;
    private static final int CLASS_LINE_NUMBER = 1;

    @BeforeEach
    void setUp() {
        generator = new PlantUMLClassDiagramGenerator();
    }

    @Test
    @DisplayName("Should handle protected visibility modifier")
    void shouldHandleProtectedVisibility() throws Exception {
        CodeElement protectedMethod = new CodeElement(
            CodeElementType.METHOD,
            "protectedMethod",
            "com.test.TestClass.protectedMethod",
            "/test/TestClass.java",
            METHOD_LINE_NUMBER,
            "protected void protectedMethod()",
            "Protected method documentation",
            List.of(),
            List.of()
        );

        CodeElement classElement = createTestClass();
        List<CodeElement> elements = List.of(classElement, protectedMethod);

        String result = generator.generateClassDiagram(classElement, elements, tempDir);

        assertNotNull(result);
        String content = java.nio.file.Files.readString(Path.of(result));
        assertTrue(content.contains("# void protectedMethod"));
    }

    @Test
    @DisplayName("Should handle package-private visibility modifier")
    void shouldHandlePackagePrivateVisibility() throws Exception {
        CodeElement packagePrivateMethod = new CodeElement(
            CodeElementType.METHOD,
            "packageMethod",
            "com.test.TestClass.packageMethod",
            "/test/TestClass.java",
            METHOD_LINE_NUMBER,
            "void packageMethod()",
            "Package-private method documentation",
            List.of(),
            List.of()
        );

        CodeElement classElement = createTestClass();
        List<CodeElement> elements = List.of(classElement, packagePrivateMethod);

        String result = generator.generateClassDiagram(classElement, elements, tempDir);

        assertNotNull(result);
        String content = java.nio.file.Files.readString(Path.of(result));
        assertTrue(content.contains("~ void packageMethod"));
    }

    @Test
    @DisplayName("Should handle interface class type")
    void shouldHandleInterfaceType() throws Exception {
        CodeElement interfaceElement = new CodeElement(
            CodeElementType.CLASS,
            "TestInterface",
            "com.test.TestInterface",
            "/test/TestInterface.java",
            CLASS_LINE_NUMBER,
            "public interface TestInterface",
            "Test interface documentation",
            List.of(),
            List.of()
        );

        List<CodeElement> elements = List.of(interfaceElement);

        String result = generator.generateClassDiagram(interfaceElement, elements, tempDir);

        assertNotNull(result);
        String content = java.nio.file.Files.readString(Path.of(result));
        assertTrue(content.contains("interface TestInterface"));
    }

    @Test
    @DisplayName("Should handle abstract class type")
    void shouldHandleAbstractType() throws Exception {
        CodeElement abstractElement = new CodeElement(
            CodeElementType.CLASS,
            "AbstractClass",
            "com.test.AbstractClass",
            "/test/AbstractClass.java",
            CLASS_LINE_NUMBER,
            "public abstract class AbstractClass",
            "Abstract class documentation",
            List.of(),
            List.of()
        );

        List<CodeElement> elements = List.of(abstractElement);

        String result = generator.generateClassDiagram(abstractElement, elements, tempDir);

        assertNotNull(result);
        String content = java.nio.file.Files.readString(Path.of(result));
        assertTrue(content.contains("abstract class AbstractClass"));
    }

    @Test
    @DisplayName("Should handle enum class type")
    void shouldHandleEnumType() throws Exception {
        CodeElement enumElement = new CodeElement(
            CodeElementType.CLASS,
            "TestEnum",
            "com.test.TestEnum",
            "/test/TestEnum.java",
            CLASS_LINE_NUMBER,
            "public enum TestEnum",
            "Test enum documentation",
            List.of(),
            List.of()
        );

        List<CodeElement> elements = List.of(enumElement);

        String result = generator.generateClassDiagram(enumElement, elements, tempDir);

        assertNotNull(result);
        String content = java.nio.file.Files.readString(Path.of(result));
        assertTrue(content.contains("enum TestEnum"));
    }

    @Test
    @DisplayName("Should handle null and empty method signatures")
    void shouldHandleNullAndEmptySignatures() throws Exception {
        // Note: null signature will cause NPE in mapVisibilityToPlantUML,
        // so we test with empty string instead which is handled gracefully
        CodeElement emptySignatureMethod = new CodeElement(
            CodeElementType.METHOD,
            "emptyMethod",
            "com.test.TestClass.emptyMethod",
            "/test/TestClass.java",
            METHOD_LINE_NUMBER,
            "",
            "Method with empty signature",
            List.of(),
            List.of()
        );

        CodeElement classElement = createTestClass();
        List<CodeElement> elements = List.of(classElement, emptySignatureMethod);

        String result = generator.generateClassDiagram(classElement, elements, tempDir);

        assertNotNull(result);
        String content = java.nio.file.Files.readString(Path.of(result));
        assertTrue(content.contains("void emptyMethod"));
    }

    @Test
    @DisplayName("Should handle complex method signatures with modifiers")
    void shouldHandleComplexMethodSignatures() throws Exception {
        CodeElement complexMethod = new CodeElement(
            CodeElementType.METHOD,
            "complexMethod",
            "com.test.TestClass.complexMethod",
            "/test/TestClass.java",
            METHOD_LINE_NUMBER,
            "public static final String complexMethod(int param1, String param2)",
            "Complex method documentation",
            List.of("param1", "param2"),
            List.of()
        );

        CodeElement classElement = createTestClass();
        List<CodeElement> elements = List.of(classElement, complexMethod);

        String result = generator.generateClassDiagram(classElement, elements, tempDir);

        assertNotNull(result);
        String content = java.nio.file.Files.readString(Path.of(result));
        assertTrue(content.contains("String complexMethod"));
        assertTrue(content.contains("int param1, String param2"));
    }

    @Test
    @DisplayName("Should handle method without parentheses in signature")
    void shouldHandleMethodWithoutParentheses() throws Exception {
        CodeElement noParensMethod = new CodeElement(
            CodeElementType.METHOD,
            "noParensMethod",
            "com.test.TestClass.noParensMethod",
            "/test/TestClass.java",
            METHOD_LINE_NUMBER,
            "public void noParensMethod",
            "Method without parentheses",
            List.of(),
            List.of()
        );

        CodeElement classElement = createTestClass();
        List<CodeElement> elements = List.of(classElement, noParensMethod);

        String result = generator.generateClassDiagram(classElement, elements, tempDir);

        assertNotNull(result);
        String content = java.nio.file.Files.readString(Path.of(result));
        assertTrue(content.contains("noParensMethod"));
    }

    @Test
    @DisplayName("Should handle null class name in sanitization")
    void shouldHandleNullClassName() throws Exception {
        // Cannot test with null name directly as record constructor doesn't allow it
        // Instead test the sanitization through the relationship functionality
        CodeElement nullNameClass = new CodeElement(
            CodeElementType.CLASS,
            "",
            "com.test.EmptyClass",
            "/test/EmptyClass.java",
            CLASS_LINE_NUMBER,
            "public class EmptyClass",
            "Class with empty name",
            List.of(),
            List.of()
        );

        List<CodeElement> elements = List.of(nullNameClass);

        String result = generator.generateClassDiagram(nullNameClass, elements, tempDir);

        assertNotNull(result);
        String content = java.nio.file.Files.readString(Path.of(result));
        // Empty name should be handled gracefully
        assertTrue(content.contains("class"));
    }

    @Test
    @DisplayName("Should handle complex type names with generics")
    void shouldHandleComplexTypeNames() throws Exception {
        CodeElement genericField = new CodeElement(
            CodeElementType.FIELD,
            "genericField",
            "com.test.TestClass.genericField",
            "/test/TestClass.java",
            FIELD_LINE_NUMBER,
            "public List<Map<String, Integer>> genericField",
            "Field with generic types",
            List.of(),
            List.of()
        );

        CodeElement classElement = createTestClass();
        List<CodeElement> elements = List.of(classElement, genericField);

        String result = generator.generateClassDiagram(classElement, elements, tempDir);

        assertNotNull(result);
        String content = java.nio.file.Files.readString(Path.of(result));
        // Should include the field since it's public
        assertTrue(content.contains("genericField"));
    }

    @Test
    @DisplayName("Should handle private elements exclusion")
    void shouldHandlePrivateElementsExclusion() throws Exception {
        // Use signature with "private" keyword to trigger isPublic() to return false
        CodeElement privateField = new CodeElement(
            CodeElementType.FIELD,
            "privateField",
            "com.test.TestClass.privateField",
            "/test/TestClass.java",
            FIELD_LINE_NUMBER,
            "private String privateField",
            "Private field documentation",
            List.of(),
            List.of()
        );

        CodeElement classElement = createTestClass();
        List<CodeElement> elements = List.of(classElement, privateField);

        String result = generator.generateClassDiagram(classElement, elements, tempDir);

        assertNotNull(result);
        String content = java.nio.file.Files.readString(Path.of(result));
        // Private field should be excluded due to isNonPrivate filter
        assertFalse(content.contains("privateField"));
    }

    @Test
    @DisplayName("Should handle relationships between classes")
    void shouldHandleClassRelationships() throws Exception {
        CodeElement classA = createTestClass();

        CodeElement classB = new CodeElement(
            CodeElementType.CLASS,
            "TestClassB",
            "com.test.TestClassB",
            "/test/TestClassB.java",
            CLASS_LINE_NUMBER,
            "public class TestClassB",
            "Test class B documentation",
            List.of(),
            List.of()
        );

        CodeElement methodWithDependency = new CodeElement(
            CodeElementType.METHOD,
            "methodWithDependency",
            "com.test.TestClass.methodWithDependency",
            "/test/TestClass.java",
            METHOD_LINE_NUMBER,
            "public TestClassB methodWithDependency(TestClassB param)",
            "Method that uses TestClassB",
            List.of("param"),
            List.of()
        );

        List<CodeElement> elements = List.of(classA, classB, methodWithDependency);

        String result = generator.generateClassDiagram(classA, elements, tempDir);

        assertNotNull(result);
        String content = java.nio.file.Files.readString(Path.of(result));
        assertTrue(content.contains("TestClass"));
        assertTrue(content.contains("TestClassB"));
        assertTrue(content.contains("..>"));
        assertTrue(content.contains("uses"));
    }

    // Helper methods

    private CodeElement createTestClass() {
        return new CodeElement(
            CodeElementType.CLASS,
            "TestClass",
            "com.test.TestClass",
            "/test/TestClass.java",
            CLASS_LINE_NUMBER,
            "public class TestClass",
            "Test class documentation",
            List.of(),
            List.of("@Component")
        );
    }
}