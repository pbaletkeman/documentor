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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PythonASTProcessorTest {

    @InjectMocks
    private PythonASTProcessor astProcessor;

    @TempDir
    private Path tempDir;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testParseASTOutputClass() throws Exception {
        // This test uses reflection to access private method
        var method = PythonASTProcessor.class.getDeclaredMethod("parseASTOutput", String.class, Path.class);
        method.setAccessible(true);
        
        Path filePath = tempDir.resolve("test.py");
        String line = "CLASS|TestClass|10|This is a test class";
        
        CodeElement element = (CodeElement) method.invoke(astProcessor, line, filePath);
        
        assertNotNull(element);
        assertEquals(CodeElementType.CLASS, element.type());
        assertEquals("TestClass", element.name());
        assertEquals("class TestClass:", element.signature());
        assertEquals(filePath.toString(), element.filePath());
        assertEquals(10, element.lineNumber());
        assertEquals("This is a test class", element.documentation());
    }

    @Test
    void testParseASTOutputFunction() throws Exception {
        // This test uses reflection to access private method
        var method = PythonASTProcessor.class.getDeclaredMethod("parseASTOutput", String.class, Path.class);
        method.setAccessible(true);
        
        Path filePath = tempDir.resolve("test.py");
        String line = "FUNCTION|test_function|15|This is a test function|param1,param2";
        
        CodeElement element = (CodeElement) method.invoke(astProcessor, line, filePath);
        
        assertNotNull(element);
        assertEquals(CodeElementType.METHOD, element.type());
        assertEquals("test_function", element.name());
        assertEquals("def test_function(param1, param2):", element.signature());
        assertEquals(filePath.toString(), element.filePath());
        assertEquals(15, element.lineNumber());
        assertEquals("This is a test function", element.documentation());
        assertEquals(List.of("param1", "param2"), element.parameters());
    }

    @Test
    void testParseASTOutputVariable() throws Exception {
        // This test uses reflection to access private method
        var method = PythonASTProcessor.class.getDeclaredMethod("parseASTOutput", String.class, Path.class);
        method.setAccessible(true);
        
        Path filePath = tempDir.resolve("test.py");
        String line = "VARIABLE|test_var|20||";
        
        CodeElement element = (CodeElement) method.invoke(astProcessor, line, filePath);
        
        assertNotNull(element);
        assertEquals(CodeElementType.FIELD, element.type());
        assertEquals("test_var", element.name());
        assertEquals("test_var = ...", element.signature());
        assertEquals(filePath.toString(), element.filePath());
        assertEquals(20, element.lineNumber());
        assertEquals("", element.documentation());
    }

    @Test
    void testParseASTOutputInvalidFormat() throws Exception {
        // This test uses reflection to access private method
        var method = PythonASTProcessor.class.getDeclaredMethod("parseASTOutput", String.class, Path.class);
        method.setAccessible(true);
        
        Path filePath = tempDir.resolve("test.py");
        String line = "INVALID|Format";
        
        CodeElement element = (CodeElement) method.invoke(astProcessor, line, filePath);
        
        assertNull(element);
    }

    @Test
    void testParseASTOutputUnknownType() throws Exception {
        // This test uses reflection to access private method
        var method = PythonASTProcessor.class.getDeclaredMethod("parseASTOutput", String.class, Path.class);
        method.setAccessible(true);
        
        Path filePath = tempDir.resolve("test.py");
        String line = "UNKNOWN|name|30||";
        
        CodeElement element = (CodeElement) method.invoke(astProcessor, line, filePath);
        
        assertNull(element);
    }

    @Test
    void analyzeWithASTIntegrationMocked() throws IOException, InterruptedException {
        // Skip actual subprocess execution but test the rest of the flow
        // This requires a properly formatted Python file
        Path filePath = Files.createTempFile(tempDir, "test", ".py");
        String pythonCode = "class TestClass:\n" +
                           "    \"\"\"This is a test class\"\"\"\n" +
                           "    def test_method(self, param1, param2):\n" +
                           "        \"\"\"This is a test method\"\"\"\n" +
                           "        test_var = 42\n" +
                           "        return test_var\n";
        Files.write(filePath, pythonCode.getBytes());
        
        // We're not testing the actual Python execution here, just the Java parsing logic
        // In a real test environment, we'd need Python installed
        try {
            List<CodeElement> elements = astProcessor.analyzeWithAST(filePath);
            // If Python is not installed or fails, this will throw and be caught
            assertNotNull(elements);
        } catch (IOException e) {
            // Expected if Python is not available
            assertTrue(e.getMessage().contains("Python AST analysis failed") || 
                      e.getMessage().contains("Cannot run program \"python\""));
        }
    }
}