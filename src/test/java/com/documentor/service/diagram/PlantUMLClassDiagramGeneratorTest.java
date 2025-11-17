package com.documentor.service.diagram;

import com.documentor.model.CodeElement;
import com.documentor.model.CodeElementType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * PlantUML Class Diagram Generator Tests
 */
class PlantUMLClassDiagramGeneratorTest {

    // Test constants for magic number violations
    private static final int FIELD_LINE = 5;
    private static final int METHOD_LINE = 10;
    private static final int CLASS_LINE = 1;
    private static final int PRIVATE_METHOD_LINE = 15;

    private PlantUMLClassDiagramGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new PlantUMLClassDiagramGenerator();
    }

    @Test
    @DisplayName("Should generate basic class diagram")
    void shouldGenerateBasicClassDiagram(@TempDir final Path tempDir) throws IOException {
        // Given
        CodeElement classElement = new CodeElement(
            CodeElementType.CLASS,
            "TestClass",
            "com.example.TestClass",
            "/test/TestClass.java",
            CLASS_LINE,
            "public class TestClass",
            "Test class description",
            List.of(),
            List.of()
        );

        List<CodeElement> allElements = List.of(classElement);

        // When
        String filePath = generator.generateClassDiagram(classElement, allElements, tempDir);

        // Then
        Path diagramPath = Path.of(filePath);
        assertTrue(diagramPath.toFile().exists());
        String content = Files.readString(diagramPath);
        assertTrue(content.contains("@startuml"));
        assertTrue(content.contains("@enduml"));
        assertTrue(content.contains("class TestClass"));
    }

    @Test
    @DisplayName("Should generate class with fields")
    void shouldGenerateClassWithFields(@TempDir final Path tempDir) throws IOException {
        // Given
        CodeElement fieldElement = new CodeElement(
            CodeElementType.FIELD,
            "testField",
            "com.example.TestClass.testField",
            "/test/TestClass.java",
            FIELD_LINE,
            "public String testField",
            "Test field",
            List.of(),
            List.of()
        );

        CodeElement classElement = new CodeElement(
            CodeElementType.CLASS,
            "TestClass",
            "com.example.TestClass",
            "/test/TestClass.java",
            CLASS_LINE,
            "public class TestClass",
            "Test class description",
            List.of(),
            List.of()
        );

        List<CodeElement> allElements = List.of(classElement, fieldElement);

        // When
        String filePath = generator.generateClassDiagram(classElement, allElements, tempDir);

        // Then
        Path diagramPath = Path.of(filePath);
        String content = Files.readString(diagramPath);
        assertTrue(content.contains("+ String testField"));
    }

    @Test
    @DisplayName("Should generate class with methods")
    void shouldGenerateClassWithMethods(@TempDir final Path tempDir) throws IOException {
        // Given
        CodeElement methodElement = new CodeElement(
            CodeElementType.METHOD,
            "testMethod",
            "com.example.TestClass.testMethod",
            "/test/TestClass.java",
            METHOD_LINE,
            "public String testMethod(int param)",
            "Test method",
            List.of("int param"),
            List.of()
        );

        CodeElement classElement = new CodeElement(
            CodeElementType.CLASS,
            "TestClass",
            "com.example.TestClass",
            "/test/TestClass.java",
            CLASS_LINE,
            "public class TestClass",
            "Test class description",
            List.of(),
            List.of()
        );

        List<CodeElement> allElements = List.of(classElement, methodElement);

        // When
        String filePath = generator.generateClassDiagram(classElement, allElements, tempDir);

        // Then
        Path diagramPath = Path.of(filePath);
        String content = Files.readString(diagramPath);
        assertTrue(content.contains("+ String testMethod(int param)"));
    }

    @Test
    @DisplayName("Should handle interface type")
    void shouldHandleInterfaceType(@TempDir final Path tempDir) throws IOException {
        // Given
        CodeElement interfaceElement = new CodeElement(
            CodeElementType.CLASS,
            "TestInterface",
            "com.example.TestInterface",
            "/test/TestInterface.java",
            CLASS_LINE,
            "public interface TestInterface",
            "Test interface",
            List.of(),
            List.of()
        );

        List<CodeElement> allElements = List.of(interfaceElement);

        // When
        String filePath = generator.generateClassDiagram(interfaceElement, allElements, tempDir);

        // Then
        Path diagramPath = Path.of(filePath);
        String content = Files.readString(diagramPath);
        assertTrue(content.contains("interface TestInterface"));
    }

    @Test
    @DisplayName("Should handle abstract class")
    void shouldHandleAbstractClass(@TempDir final Path tempDir) throws IOException {
        // Given
        CodeElement abstractElement = new CodeElement(
            CodeElementType.CLASS,
            "AbstractClass",
            "com.example.AbstractClass",
            "/test/AbstractClass.java",
            CLASS_LINE,
            "public abstract class AbstractClass",
            "Abstract class",
            List.of(),
            List.of()
        );

        List<CodeElement> allElements = List.of(abstractElement);

        // When
        String filePath = generator.generateClassDiagram(abstractElement, allElements, tempDir);

        // Then
        Path diagramPath = Path.of(filePath);
        String content = Files.readString(diagramPath);
        assertTrue(content.contains("abstract class AbstractClass"));
    }

    @Test
    @DisplayName("Should handle enum type")
    void shouldHandleEnumType(@TempDir final Path tempDir) throws IOException {
        // Given
        CodeElement enumElement = new CodeElement(
            CodeElementType.CLASS,
            "TestEnum",
            "com.example.TestEnum",
            "/test/TestEnum.java",
            CLASS_LINE,
            "public enum TestEnum",
            "Test enum",
            List.of(),
            List.of()
        );

        List<CodeElement> allElements = List.of(enumElement);

        // When
        String filePath = generator.generateClassDiagram(enumElement, allElements, tempDir);

        // Then
        Path diagramPath = Path.of(filePath);
        String content = Files.readString(diagramPath);
        assertTrue(content.contains("enum TestEnum"));
    }

    @Test
    @DisplayName("Should map visibility symbols correctly")
    void shouldMapVisibilitySymbols(@TempDir final Path tempDir) throws IOException {
        // Given
        CodeElement publicMethod = new CodeElement(
            CodeElementType.METHOD,
            "publicMethod",
            "com.example.TestClass.publicMethod",
            "/test/TestClass.java",
            METHOD_LINE,
            "public void publicMethod()",
            "Public method",
            List.of(),
            List.of()
        );

        CodeElement publicField = new CodeElement(
            CodeElementType.FIELD,
            "publicField",
            "com.example.TestClass.publicField",
            "/test/TestClass.java",
            FIELD_LINE,
            "public String publicField",
            "Public field",
            List.of(),
            List.of()
        );

        CodeElement classElement = new CodeElement(
            CodeElementType.CLASS,
            "TestClass",
            "com.example.TestClass",
            "/test/TestClass.java",
            CLASS_LINE,
            "public class TestClass",
            "Test class",
            List.of(),
            List.of()
        );

        List<CodeElement> allElements = List.of(classElement, publicMethod, publicField);

        // When
        String filePath = generator.generateClassDiagram(classElement, allElements, tempDir);

        // Then
        Path diagramPath = Path.of(filePath);
        String content = Files.readString(diagramPath);
        assertTrue(content.contains("+ void publicMethod()"));
        assertTrue(content.contains("+ String publicField"));
    }

    @Test
    @DisplayName("Should include all accessibility levels")
    void shouldIncludeAllMembers(@TempDir final Path tempDir) throws IOException {
        // Given
        CodeElement helperMethod = new CodeElement(
            CodeElementType.METHOD,
            "helperMethod",
            "com.example.TestClass.helperMethod",
            "/test/TestClass.java",
            PRIVATE_METHOD_LINE,
            "public void helperMethod()",
            "Public helper method",
            List.of(),
            List.of()
        );

        CodeElement publicMethod = new CodeElement(
            CodeElementType.METHOD,
            "publicMethod",
            "com.example.TestClass.publicMethod",
            "/test/TestClass.java",
            METHOD_LINE,
            "public void publicMethod()",
            "Public method",
            List.of(),
            List.of()
        );

        CodeElement classElement = new CodeElement(
            CodeElementType.CLASS,
            "TestClass",
            "com.example.TestClass",
            "/test/TestClass.java",
            CLASS_LINE,
            "public class TestClass",
            "Test class",
            List.of(),
            List.of()
        );

        List<CodeElement> allElements = List.of(classElement, helperMethod, publicMethod);

        // When
        String filePath = generator.generateClassDiagram(classElement, allElements, tempDir);

        // Then
        Path diagramPath = Path.of(filePath);
        String content = Files.readString(diagramPath);
        assertTrue(content.contains("+ void publicMethod()"));
        assertTrue(content.contains("+ void helperMethod()"));
    }

    @Test
    @DisplayName("Should exclude private members")
    void shouldExcludePrivateMembers(@TempDir final Path tempDir) throws IOException {
        // Given
        CodeElement privateMethod = new CodeElement(
            CodeElementType.METHOD,
            "privateHelper",
            "com.example.TestClass.privateHelper",
            "/test/TestClass.java",
            PRIVATE_METHOD_LINE,
            "private void privateHelper()",
            "Private helper method",
            List.of(),
            List.of()
        );

        CodeElement publicMethod = new CodeElement(
            CodeElementType.METHOD,
            "publicMethod",
            "com.example.TestClass.publicMethod",
            "/test/TestClass.java",
            METHOD_LINE,
            "public void publicMethod()",
            "Public method",
            List.of(),
            List.of()
        );

        CodeElement classElement = new CodeElement(
            CodeElementType.CLASS,
            "TestClass",
            "com.example.TestClass",
            "/test/TestClass.java",
            CLASS_LINE,
            "public class TestClass",
            "Test class",
            List.of(),
            List.of()
        );

        List<CodeElement> allElements = List.of(classElement, privateMethod, publicMethod);

        // When
        String filePath = generator.generateClassDiagram(classElement, allElements, tempDir);

        // Then
        Path diagramPath = Path.of(filePath);
        String content = Files.readString(diagramPath);
        assertTrue(content.contains("+ void publicMethod()"));
        assertFalse(content.contains("privateHelper")); // Private method should be excluded
    }
}
