package com.documentor.service.python;

import com.documentor.model.CodeElement;
import com.documentor.model.CodeElementType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for PythonASTCommandBuilder class
 */
class PythonASTCommandBuilderTest {

    private final PythonASTCommandBuilder commandBuilder = new PythonASTCommandBuilder();

    @Test
    @DisplayName("Should return a valid Python AST script")
    void shouldReturnValidPythonAstScript() {
        // When
        String script = commandBuilder.getPythonAstScript();

        // Then
        assertNotNull(script);
        assertFalse(script.isEmpty());

        // Check for expected content
        assertTrue(script.contains("import ast"));
        assertTrue(script.contains("import sys"));
        assertTrue(script.contains("def analyze_file"));
        assertTrue(script.contains("CLASS|"));
        assertTrue(script.contains("FUNCTION|"));
        assertTrue(script.contains("VARIABLE|"));
    }

    @Test
    @DisplayName("Should write temporary Python script to filesystem")
    void shouldWriteTempScript(@TempDir Path tempDir) throws IOException {
        // Set the temp directory (optional)
        System.setProperty("java.io.tmpdir", tempDir.toString());

        // When
        Path scriptPath = commandBuilder.writeTempScript();

        // Then
        assertNotNull(scriptPath);
        assertTrue(Files.exists(scriptPath));
        assertTrue(scriptPath.getFileName().toString().startsWith("python_analyzer"));
        assertTrue(scriptPath.getFileName().toString().endsWith(".py"));

        // Content check
        String content = Files.readString(scriptPath);
        assertTrue(content.contains("import ast"));
    }

    @Test
    @DisplayName("Should create a valid process builder")
    void shouldCreateProcessBuilder(@TempDir Path tempDir) throws IOException {
        // Given
        Path scriptPath = tempDir.resolve("test_script.py");
        Path filePath = tempDir.resolve("test_file.py");
        Files.writeString(scriptPath, "print('test')");
        Files.writeString(filePath, "class Test: pass");

        // When
        ProcessBuilder processBuilder = commandBuilder.createProcessBuilder(scriptPath, filePath);

        // Then
        assertNotNull(processBuilder);
        List<String> command = processBuilder.command();
        assertEquals(3, command.size());
        assertEquals("python", command.get(0));
        assertEquals(scriptPath.toString(), command.get(1));
        assertEquals(filePath.toString(), command.get(2));
    }

    @Test
    @DisplayName("Should parse CLASS output line")
    void shouldParseClassOutputLine() {
        // Given
        String classLine = "CLASS|TestClass|10|This is a test class docstring";
        Path filePath = Path.of("test.py");

        // When
        CodeElement element = commandBuilder.parseASTOutputLine(classLine, filePath);

        // Then
        assertNotNull(element);
        assertEquals(CodeElementType.CLASS, element.type());
        assertEquals("TestClass", element.name());
        assertEquals("class TestClass", element.qualifiedName());
        assertEquals(filePath.toString(), element.filePath());
        assertEquals(10, element.lineNumber());
        assertEquals("class TestClass:", element.signature());
        assertEquals("This is a test class docstring", element.documentation());
        assertTrue(element.parameters().isEmpty());
        assertTrue(element.annotations().isEmpty());
    }

    @Test
    @DisplayName("Should parse FUNCTION output line")
    void shouldParseFunctionOutputLine() {
        // Given
        String functionLine = "FUNCTION|test_function|15|This is a function docstring|param1,param2,param3";
        Path filePath = Path.of("test.py");

        // When
        CodeElement element = commandBuilder.parseASTOutputLine(functionLine, filePath);

        // Then
        assertNotNull(element);
        assertEquals(CodeElementType.METHOD, element.type());
        assertEquals("test_function", element.name());
        assertEquals("def test_function(param1, param2, param3)", element.qualifiedName());
        assertEquals(filePath.toString(), element.filePath());
        assertEquals(15, element.lineNumber());
        assertEquals("def test_function(param1, param2, param3):", element.signature());
        assertEquals("This is a function docstring", element.documentation());

        // Parameters check
        assertEquals(3, element.parameters().size());
        assertEquals("param1", element.parameters().get(0));
        assertEquals("param2", element.parameters().get(1));
        assertEquals("param3", element.parameters().get(2));

        assertTrue(element.annotations().isEmpty());
    }

    @Test
    @DisplayName("Should parse VARIABLE output line")
    void shouldParseVariableOutputLine() {
        // Given
        String variableLine = "VARIABLE|test_variable|5||";
        Path filePath = Path.of("test.py");

        // When
        CodeElement element = commandBuilder.parseASTOutputLine(variableLine, filePath);

        // Then
        assertNotNull(element);
        assertEquals(CodeElementType.FIELD, element.type());
        assertEquals("test_variable", element.name());
        assertEquals("test_variable", element.qualifiedName());
        assertEquals(filePath.toString(), element.filePath());
        assertEquals(5, element.lineNumber());
        assertEquals("test_variable = ...", element.signature());
        assertEquals("", element.documentation());
        assertTrue(element.parameters().isEmpty());

        assertTrue(element.annotations().isEmpty());
    }

    @Test
    @DisplayName("Should return null for invalid output line")
    void shouldReturnNullForInvalidOutputLine() {
        // Given
        String invalidLine = "INVALID|only_one_part";
        Path filePath = Path.of("test.py");

        // When
        CodeElement element = commandBuilder.parseASTOutputLine(invalidLine, filePath);

        // Then
        assertNull(element);
    }

    @Test
    @DisplayName("Should handle empty parts in output line")
    void shouldHandleEmptyPartsInOutputLine() {
        // Given
        String lineWithEmptyParts = "FUNCTION|empty_function|20||";
        Path filePath = Path.of("test.py");

        // When
        CodeElement element = commandBuilder.parseASTOutputLine(lineWithEmptyParts, filePath);

        // Then
        assertNotNull(element);
        assertEquals(CodeElementType.METHOD, element.type());
        assertEquals("empty_function", element.name());
        assertEquals("def empty_function()", element.qualifiedName());
        assertEquals("", element.documentation());
        assertTrue(element.parameters().isEmpty());
    }
}
