package com.documentor.service.python;

import com.documentor.model.CodeElement;
import com.documentor.model.CodeElementType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.argThat;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class PythonASTProcessorTest {

    // Test constants for magic number violations
    private static final int LINE_NUMBER_TEN = 10;
    private static final int LINE_NUMBER_FIFTEEN = 15;
    private static final int LINE_NUMBER_TWENTY = 20;
    private static final int LINE_NUMBER_THREE = 3;
    private static final int LINE_NUMBER_FIVE = 5;

    @InjectMocks
    private PythonASTProcessor astProcessor;

    @TempDir
    private Path tempDir;

    // Removed unused field commandBuilder to fix Checkstyle warning

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Setup removed - each test creates its own commandBuilder
    }

    @Test
    void testParseASTOutputClass() throws Exception {
        // Create a mock PythonASTCommandBuilder to test the parsing logic
        PythonASTCommandBuilder commandBuilder = new PythonASTCommandBuilder();

        Path filePath = tempDir.resolve("test.py");
        String line = "CLASS|TestClass|10|This is a test class";

        CodeElement element = commandBuilder.parseASTOutputLine(line, filePath);

        assertNotNull(element);
        assertEquals(CodeElementType.CLASS, element.type());
        assertEquals("TestClass", element.name());
        assertEquals("class TestClass:", element.signature());
        assertEquals(filePath.toString(), element.filePath());
        assertEquals(LINE_NUMBER_TEN, element.lineNumber());
        assertEquals("This is a test class", element.documentation());
    }

    @Test
    void testParseASTOutputFunction() throws Exception {
        // Create a mock PythonASTCommandBuilder to test the parsing logic
        PythonASTCommandBuilder commandBuilder = new PythonASTCommandBuilder();

        Path filePath = tempDir.resolve("test.py");
        String line = "FUNCTION|test_function|15|This is a test function|param1,param2";

        CodeElement element = commandBuilder.parseASTOutputLine(line, filePath);

        assertNotNull(element);
        assertEquals(CodeElementType.METHOD, element.type());
        assertEquals("test_function", element.name());
        assertEquals("def test_function(param1, param2):", element.signature());
        assertEquals(filePath.toString(), element.filePath());
        assertEquals(LINE_NUMBER_FIFTEEN, element.lineNumber());
        assertEquals("This is a test function", element.documentation());
        assertEquals(List.of("param1", "param2"), element.parameters());
    }

    @Test
    void testParseASTOutputVariable() throws Exception {
        // Create a mock PythonASTCommandBuilder to test the parsing logic
        PythonASTCommandBuilder commandBuilder = new PythonASTCommandBuilder();

        Path filePath = tempDir.resolve("test.py");
        String line = "VARIABLE|test_var|20||";

        CodeElement element = commandBuilder.parseASTOutputLine(line, filePath);

        assertNotNull(element);
        assertEquals(CodeElementType.FIELD, element.type());
        assertEquals("test_var", element.name());
        assertEquals("test_var = ...", element.signature());
        assertEquals(filePath.toString(), element.filePath());
        assertEquals(LINE_NUMBER_TWENTY, element.lineNumber());
        assertEquals("", element.documentation());
    }

    @Test
    void testParseASTOutputInvalidFormat() throws Exception {
        // Create a mock PythonASTCommandBuilder to test the parsing logic
        PythonASTCommandBuilder commandBuilder = new PythonASTCommandBuilder();

        Path filePath = tempDir.resolve("test.py");
        String line = "INVALID|Format";

        CodeElement element = commandBuilder.parseASTOutputLine(line, filePath);

        assertNull(element);
    }

    @Test
    void testParseASTOutputUnknownType() throws Exception {
        // Create a mock PythonASTCommandBuilder to test the parsing logic
        PythonASTCommandBuilder commandBuilder = new PythonASTCommandBuilder();

        Path filePath = tempDir.resolve("test.py");
        String line = "UNKNOWN|name|30||";

        CodeElement element = commandBuilder.parseASTOutputLine(line, filePath);

        assertNull(element);
    }

    @Test
    void analyzeWithASTIntegrationMocked() throws IOException, InterruptedException {
        // Create a mocked version of PythonASTProcessor with a mocked command builder
        PythonASTCommandBuilder mockedCommandBuilder = mock(PythonASTCommandBuilder.class);
        PythonASTProcessor mockedProcessor = new PythonASTProcessor(mockedCommandBuilder);

        // Create test file
        Path filePath = Files.createTempFile(tempDir, "test", ".py");
        String pythonCode = "class TestClass:\n" +
                           "    \"\"\"This is a test class\"\"\"\n" +
                           "    def test_method(self, param1, param2):\n" +
                           "        \"\"\"This is a test method\"\"\"\n" +
                           "        test_var = 42\n" +
                           "        return test_var\n";
        Files.write(filePath, pythonCode.getBytes());

        // Setup mocks
        Path tempScript = Files.createTempFile(tempDir, "analyzer", ".py");
        when(mockedCommandBuilder.writeTempScript()).thenReturn(tempScript);

        // Mock process
        Process mockedProcess = mock(Process.class);
        String testOutput =
            "CLASS|TestClass|1|This is a test class\n" +
            "FUNCTION|test_method|3|This is a test method|self,param1,param2\n" +
            "VARIABLE|test_var|5||\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(testOutput.getBytes());
        when(mockedProcess.getInputStream()).thenReturn(inputStream);
        when(mockedProcess.waitFor()).thenReturn(0);

        // Mock the process builder
        ProcessBuilder mockedProcessBuilder = mock(ProcessBuilder.class);
        when(mockedProcessBuilder.start()).thenReturn(mockedProcess);
        when(mockedCommandBuilder.createProcessBuilder(any(), eq(filePath))).thenReturn(mockedProcessBuilder);

        // Create mock code elements that will be returned by parseASTOutputLine
        CodeElement classElement = new CodeElement(
            CodeElementType.CLASS, "TestClass", "class TestClass",
            filePath.toString(), 1, "class TestClass:", "This is a test class",
            List.of(), List.of()
        );

        CodeElement methodElement = new CodeElement(
            CodeElementType.METHOD, "test_method", "def test_method(self, param1, param2)",
            filePath.toString(), LINE_NUMBER_THREE, "def test_method(self, param1, param2):", "This is a test method",
            List.of("self", "param1", "param2"), List.of()
        );

        CodeElement fieldElement = new CodeElement(
            CodeElementType.FIELD, "test_var", "test_var = ...",
            filePath.toString(), LINE_NUMBER_FIVE, "test_var = ...", "",
            List.of(), List.of()
        );

        // Mock the command builder to parse the lines - use argThat for better matching
        when(mockedCommandBuilder.parseASTOutputLine(
                argThat(arg -> arg != null && arg.contains("CLASS|TestClass")), eq(filePath)))
            .thenReturn(classElement);
        when(mockedCommandBuilder.parseASTOutputLine(
                argThat(arg -> arg != null && arg.contains("FUNCTION|test_method")), eq(filePath)))
            .thenReturn(methodElement);
        when(mockedCommandBuilder.parseASTOutputLine(
                argThat(arg -> arg != null && arg.contains("VARIABLE|test_var")), eq(filePath)))
            .thenReturn(fieldElement);

        // Execute and verify
        List<CodeElement> elements = mockedProcessor.analyzeWithAST(filePath);

        assertNotNull(elements);
        assertEquals(LINE_NUMBER_THREE, elements.size());

        // Verify we have the expected elements
        assertTrue(elements.stream().anyMatch(e -> e.type() == CodeElementType.CLASS 
                && "TestClass".equals(e.name())));
        assertTrue(elements.stream().anyMatch(e -> e.type() == CodeElementType.METHOD 
                && "test_method".equals(e.name())));
        assertTrue(elements.stream().anyMatch(e -> e.type() == CodeElementType.FIELD 
                && "test_var".equals(e.name())));
    }
}
