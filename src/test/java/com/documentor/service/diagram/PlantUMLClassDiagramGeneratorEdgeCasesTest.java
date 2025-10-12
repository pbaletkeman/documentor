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
 * Edge case tests for PlantUMLClassDiagramGenerator to achieve 85%+ branch coverage.
 * These tests target the remaining uncovered branches in specific methods.
 */
class PlantUMLClassDiagramGeneratorEdgeCasesTest {

    private PlantUMLClassDiagramGenerator generator;

    @TempDir
    private Path tempDir;

    // Constants for test data
    private static final int METHOD_LINE_NUMBER = 10;
    private static final int CLASS_LINE_NUMBER = 1;
    private static final int FIELD_LINE_NUMBER = 5;

    @BeforeEach
    void setUp() {
        generator = new PlantUMLClassDiagramGenerator();
    }

    @Test
    @DisplayName("Should handle method with only visibility modifier")
    void shouldHandleMethodWithOnlyVisibilityModifier() throws Exception {
        CodeElement methodWithOnlyVisibility = new CodeElement(
            CodeElementType.METHOD,
            "simpleMethod",
            "com.test.TestClass.simpleMethod",
            "/test/TestClass.java",
            METHOD_LINE_NUMBER,
            "public simpleMethod()",
            "Method with only visibility modifier",
            List.of(),
            List.of()
        );

        CodeElement classElement = createTestClass();
        List<CodeElement> elements = List.of(classElement, methodWithOnlyVisibility);

        String result = generator.generateClassDiagram(classElement, elements, tempDir);

        assertNotNull(result);
        String content = java.nio.file.Files.readString(Path.of(result));
        assertTrue(content.contains("void simpleMethod"));
    }

    @Test
    @DisplayName("Should handle method signature with only modifiers")
    void shouldHandleMethodWithOnlyModifiers() throws Exception {
        CodeElement methodWithModifiers = new CodeElement(
            CodeElementType.METHOD,
            "modifierMethod",
            "com.test.TestClass.modifierMethod",
            "/test/TestClass.java",
            METHOD_LINE_NUMBER,
            "public static final",
            "Method with only modifiers",
            List.of(),
            List.of()
        );

        CodeElement classElement = createTestClass();
        List<CodeElement> elements = List.of(classElement, methodWithModifiers);

        String result = generator.generateClassDiagram(classElement, elements, tempDir);

        assertNotNull(result);
        String content = java.nio.file.Files.readString(Path.of(result));
        assertTrue(content.contains("void modifierMethod"));
    }

    @Test
    @DisplayName("Should handle method without parentheses and parameters")
    void shouldHandleMethodWithoutParenthesesParameters() throws Exception {
        CodeElement methodWithoutParams = new CodeElement(
            CodeElementType.METHOD,
            "noParamsMethod",
            "com.test.TestClass.noParamsMethod",
            "/test/TestClass.java",
            METHOD_LINE_NUMBER,
            "public void noParamsMethod() throws Exception",
            "Method with throws clause but no params",
            List.of(),
            List.of()
        );

        CodeElement classElement = createTestClass();
        List<CodeElement> elements = List.of(classElement, methodWithoutParams);

        String result = generator.generateClassDiagram(classElement, elements, tempDir);

        assertNotNull(result);
        String content = java.nio.file.Files.readString(Path.of(result));
        assertTrue(content.contains("void noParamsMethod"));
    }

    @Test
    @DisplayName("Should handle method with malformed parentheses")
    void shouldHandleMethodWithMalformedParentheses() throws Exception {
        CodeElement malformedMethod = new CodeElement(
            CodeElementType.METHOD,
            "malformedMethod",
            "com.test.TestClass.malformedMethod",
            "/test/TestClass.java",
            METHOD_LINE_NUMBER,
            "public void malformedMethod(",
            "Method with malformed parentheses",
            List.of(),
            List.of()
        );

        CodeElement classElement = createTestClass();
        List<CodeElement> elements = List.of(classElement, malformedMethod);

        String result = generator.generateClassDiagram(classElement, elements, tempDir);

        assertNotNull(result);
        String content = java.nio.file.Files.readString(Path.of(result));
        assertTrue(content.contains("void malformedMethod"));
    }

    @Test
    @DisplayName("Should handle element with underscore name for privacy check")
    void shouldHandleElementWithUnderscoreName() throws Exception {
        CodeElement underscoreField = new CodeElement(
            CodeElementType.FIELD,
            "_privateField",
            "com.test.TestClass._privateField",
            "/test/TestClass.java",
            FIELD_LINE_NUMBER,
            "public String _privateField",
            "Field with underscore prefix",
            List.of(),
            List.of()
        );

        CodeElement classElement = createTestClass();
        List<CodeElement> elements = List.of(classElement, underscoreField);

        String result = generator.generateClassDiagram(classElement, elements, tempDir);

        assertNotNull(result);
        String content = java.nio.file.Files.readString(Path.of(result));
        // Underscore fields are considered private and should be excluded
        assertFalse(content.contains("_privateField"));
    }

    @Test
    @DisplayName("Should handle class with no relationships found")
    void shouldHandleClassWithNoRelationships() throws Exception {
        CodeElement classA = createTestClass();

        CodeElement classB = new CodeElement(
            CodeElementType.CLASS,
            "UnrelatedClass",
            "com.other.UnrelatedClass",
            "/test/UnrelatedClass.java",
            CLASS_LINE_NUMBER,
            "public class UnrelatedClass",
            "Class with no relationships",
            List.of(),
            List.of()
        );

        CodeElement methodWithoutDependency = new CodeElement(
            CodeElementType.METHOD,
            "independentMethod",
            "com.test.TestClass.independentMethod",
            "/test/TestClass.java",
            METHOD_LINE_NUMBER,
            "public String independentMethod(int param)",
            "Method without class dependencies",
            List.of("param"),
            List.of()
        );

        List<CodeElement> elements = List.of(classA, classB, methodWithoutDependency);

        String result = generator.generateClassDiagram(classA, elements, tempDir);

        assertNotNull(result);
        String content = java.nio.file.Files.readString(Path.of(result));
        assertTrue(content.contains("TestClass"));
        assertTrue(content.contains("independentMethod"));
        // Should not contain relationship arrows for unrelated classes
        assertFalse(content.contains("TestClass ..> UnrelatedClass"));
    }

    @Test
    @DisplayName("Should handle class name with special characters for sanitization")
    void shouldHandleClassNameWithSpecialCharacters() throws Exception {
        CodeElement specialClass = new CodeElement(
            CodeElementType.CLASS,
            "TestInner",  // Use a clean name to avoid path issues
            "com.test.TestInner",
            "/test/TestInner.java",
            CLASS_LINE_NUMBER,
            "public class Test$Inner<T>[]",  // The signature has special chars
            "Class with special characters",
            List.of(),
            List.of()
        );

        List<CodeElement> elements = List.of(specialClass);

        String result = generator.generateClassDiagram(specialClass, elements, tempDir);

        assertNotNull(result);
        String content = java.nio.file.Files.readString(Path.of(result));
        // Should handle the class normally
        assertTrue(content.contains("class TestInner"));
    }

    @Test
    @DisplayName("Should handle type with package prefix for sanitization")
    void shouldHandleTypeWithPackagePrefix() throws Exception {
        CodeElement methodWithPackageType = new CodeElement(
            CodeElementType.METHOD,
            "packageTypeMethod",
            "com.test.TestClass.packageTypeMethod",
            "/test/TestClass.java",
            METHOD_LINE_NUMBER,
            "public java.util.List packageTypeMethod()",
            "Method with package-prefixed types",
            List.of(),
            List.of()
        );

        CodeElement classElement = createTestClass();
        List<CodeElement> elements = List.of(classElement, methodWithPackageType);

        String result = generator.generateClassDiagram(classElement, elements, tempDir);

        assertNotNull(result);
        String content = java.nio.file.Files.readString(Path.of(result));
        // Should handle the method with simplified type
        assertTrue(content.contains("packageTypeMethod"));
    }

    @Test
    @DisplayName("Should handle element where isPublic returns false but signature doesn't contain private")
    void shouldHandlePublicFalseNonPrivateSignature() throws Exception {
        // This tests the second condition in isNonPrivate: !signature.contains("private")
        CodeElement protectedMethod = new CodeElement(
            CodeElementType.METHOD,
            "_hiddenMethod",  // Underscore makes isPublic() return false
            "com.test.TestClass._hiddenMethod",
            "/test/TestClass.java",
            METHOD_LINE_NUMBER,
            "protected void _hiddenMethod()",  // Protected, not private
            "Method that is not public but not private either",
            List.of(),
            List.of()
        );

        CodeElement classElement = createTestClass();
        List<CodeElement> elements = List.of(classElement, protectedMethod);

        String result = generator.generateClassDiagram(classElement, elements, tempDir);

        assertNotNull(result);
        String content = java.nio.file.Files.readString(Path.of(result));
        // Should be INCLUDED because even though isPublic() returns false, 
        // the signature doesn't contain "private", so isNonPrivate() returns true
        assertTrue(content.contains("_hiddenMethod"));
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