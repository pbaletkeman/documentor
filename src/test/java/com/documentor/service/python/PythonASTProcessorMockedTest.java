package com.documentor.service.python;

import com.documentor.model.CodeElement;
import com.documentor.model.CodeElementType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.atLeast;

/**
 * Advanced test class for PythonASTProcessor using mocked dependencies
 */
@ExtendWith(MockitoExtension.class)
public class PythonASTProcessorMockedTest {

    // Test constants for magic number violations
    private static final int CLASS_LINE_NUMBER = 10;
    private static final int METHOD_LINE_NUMBER = 15;
    private static final int EXPECTED_PARAM_COUNT = 3;

    @InjectMocks
    private PythonASTProcessor astProcessor;

    @Mock
    private PythonASTCommandBuilder commandBuilder;

    @TempDir
    private Path tempDir;

    @Mock
    private Process process;

    @Test
    void testParseLargerDataSet() throws Exception {
        // Mock the necessary components
        Path filePath = tempDir.resolve("test.py");
        Path tempScript = tempDir.resolve("analyzer.py");

        // Setup the process mock
        when(commandBuilder.writeTempScript()).thenReturn(tempScript);

        // Mock process input stream with test data
        String testOutput =
            "CLASS|ComplexClass|10|Complex class with multiple methods\n"
            + "FUNCTION|complex_method|15|Method that does complex things|param1,param2,param3\n";

        ByteArrayInputStream inputStream = new ByteArrayInputStream(testOutput.getBytes());
        when(process.getInputStream()).thenReturn(inputStream);
        when(process.waitFor()).thenReturn(0);

        // Mock the command builder to return valid CodeElements
        when(commandBuilder.parseASTOutputLine(contains("CLASS|ComplexClass"), eq(filePath)))
            .thenReturn(new CodeElement(
                CodeElementType.CLASS,
                "ComplexClass",
                "class ComplexClass",
                filePath.toString(),
                CLASS_LINE_NUMBER,
                "class ComplexClass:",
                "Complex class with multiple methods",
                List.of(),
                List.of()
            ));

        when(commandBuilder.parseASTOutputLine(contains("FUNCTION|complex_method"), eq(filePath)))
            .thenReturn(new CodeElement(
                CodeElementType.METHOD,
                "complex_method",
                "def complex_method(param1, param2, param3)",
                filePath.toString(),
                METHOD_LINE_NUMBER,
                "def complex_method(param1, param2, param3):",
                "Method that does complex things",
                List.of("param1", "param2", "param3"),
                List.of()
            ));

        // Create a ProcessBuilder mock that returns our mocked Process
        ProcessBuilder mockProcessBuilder = mock(ProcessBuilder.class);
        when(mockProcessBuilder.start()).thenReturn(process);
        when(commandBuilder.createProcessBuilder(any(), any())).thenReturn(mockProcessBuilder);

        // Execute the test method
        List<CodeElement> elements = astProcessor.analyzeWithAST(filePath);

        // Verify the interactions
        verify(commandBuilder).writeTempScript();
        verify(commandBuilder).createProcessBuilder(any(), eq(filePath));

        // Should have called parseASTOutputLine for each line
        verify(commandBuilder, atLeast(1)).parseASTOutputLine(anyString(), eq(filePath));

        // Assertions
        assertNotNull(elements);
        assertFalse(elements.isEmpty());
        assertEquals(2, elements.size());

        // Verify the content of the elements
        CodeElement classElement = elements.stream()
            .filter(e -> e.type() == CodeElementType.CLASS)
            .findFirst()
            .orElse(null);
        assertNotNull(classElement);
        assertEquals("ComplexClass", classElement.name());
        assertEquals(CLASS_LINE_NUMBER, classElement.lineNumber());

        CodeElement methodElement = elements.stream()
            .filter(e -> e.type() == CodeElementType.METHOD)
            .findFirst()
            .orElse(null);
        assertNotNull(methodElement);
        assertEquals("complex_method", methodElement.name());
        assertEquals(METHOD_LINE_NUMBER, methodElement.lineNumber());
        assertEquals(EXPECTED_PARAM_COUNT, methodElement.parameters().size());
    }

    @Test
    void testErrorHandling() throws Exception {
        // Mock for error case
        Path filePath = tempDir.resolve("error_test.py");
        Path tempScript = tempDir.resolve("analyzer.py");

        // Setup mock to simulate error conditions
        when(commandBuilder.writeTempScript()).thenReturn(tempScript);

        // Mock the process builder
        ProcessBuilder mockProcessBuilder = mock(ProcessBuilder.class);

        // Create a process with a non-zero exit code
        Process mockProcess = mock(Process.class);
        ByteArrayInputStream inputStream = new ByteArrayInputStream("".getBytes());
        when(mockProcess.getInputStream()).thenReturn(inputStream);
        when(mockProcess.waitFor()).thenReturn(1); // Non-zero exit code indicating error

        when(mockProcessBuilder.start()).thenReturn(mockProcess);
        when(commandBuilder.createProcessBuilder(any(), eq(filePath))).thenReturn(mockProcessBuilder);

        // Execute the method and expect an exception
        assertThrows(IOException.class, () -> {
            astProcessor.analyzeWithAST(filePath);
        });

        // Verify all mocks were used correctly
        verify(commandBuilder).writeTempScript();
        verify(commandBuilder).createProcessBuilder(any(), eq(filePath));
        verify(mockProcessBuilder).start();
        verify(mockProcess).getInputStream();
        verify(mockProcess).waitFor();
    }

    @Test
    void testTempScriptCleanup() throws Exception {
        // Mock the necessary components
        Path filePath = tempDir.resolve("test.py");
        Path tempScript = Files.createTempFile(tempDir, "analyzer", ".py");

        // Setup the process mock
        when(commandBuilder.writeTempScript()).thenReturn(tempScript);

        // Create a ProcessBuilder mock that throws an exception
        ProcessBuilder mockProcessBuilder = mock(ProcessBuilder.class);
        when(mockProcessBuilder.start()).thenThrow(new IOException("Process failed to start"));
        when(commandBuilder.createProcessBuilder(any(), any())).thenReturn(mockProcessBuilder);

        // Execute the method and expect an exception
        assertThrows(IOException.class, () -> {
            astProcessor.analyzeWithAST(filePath);
        });

        // Verify the temp file was cleaned up
        assertFalse(Files.exists(tempScript), "Temp script should be cleaned up even when exception occurs");
    }
}

