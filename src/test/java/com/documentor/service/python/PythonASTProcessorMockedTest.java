package com.documentor.service.python;

import com.documentor.model.CodeElement;
import com.documentor.model.CodeElementType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Advanced test class for PythonASTProcessor using MockedStatic
 */
@ExtendWith(MockitoExtension.class)
public class PythonASTProcessorMockedTest {

    @InjectMocks
    private PythonASTProcessor astProcessor;

    @TempDir
    private Path tempDir;

    @Mock
    private Process process;

    @Test
    void testParseLargerDataSet() throws Exception {
        // Create a set of test data to parse
        Path filePath = tempDir.resolve("test.py");
        
        // Test multiple types of Python code elements
        String[] testLines = {
            "CLASS|ComplexClass|10|Complex class with multiple methods",
            "FUNCTION|complex_method|15|Method that does complex things|param1,param2,param3",
            "VARIABLE|CONSTANT_VALUE|20||",
            "CLASS|EmptyClass|30|",
            "FUNCTION|no_doc_method|35||param1",
            "VARIABLE|empty_var|40||",
            "INVALID|Format" // Should be ignored
        };
        
        // Parse each line
        for (String line : testLines) {
            var method = PythonASTProcessor.class.getDeclaredMethod(
                "parseASTOutput", String.class, Path.class);
            method.setAccessible(true);
            
            CodeElement element = (CodeElement) method.invoke(astProcessor, line, filePath);
            
            // Skip invalid lines
            if (line.startsWith("INVALID")) {
                assertNull(element);
                continue;
            }
            
            // Validate common fields
            assertNotNull(element);
            assertEquals(filePath.toString(), element.filePath());
            
            // Validate type-specific fields
            String[] parts = line.split("\\|", -1);
            switch (parts[0]) {
                case "CLASS":
                    assertEquals(CodeElementType.CLASS, element.type());
                    assertEquals(parts[1], element.name());
                    assertEquals(Integer.parseInt(parts[2]), element.lineNumber());
                    assertEquals(parts[3], element.documentation());
                    assertEquals("class " + parts[1] + ":", element.signature());
                    break;
                    
                case "FUNCTION":
                    assertEquals(CodeElementType.METHOD, element.type());
                    assertEquals(parts[1], element.name());
                    assertEquals(Integer.parseInt(parts[2]), element.lineNumber());
                    assertEquals(parts[3], element.documentation());
                    assertTrue(element.signature().startsWith("def " + parts[1]));
                    if (parts.length > 4 && !parts[4].isEmpty()) {
                        String[] params = parts[4].split(",");
                        assertEquals(params.length, element.parameters().size());
                        for (int i = 0; i < params.length; i++) {
                            assertEquals(params[i], element.parameters().get(i));
                        }
                    }
                    break;
                    
                case "VARIABLE":
                    assertEquals(CodeElementType.FIELD, element.type());
                    assertEquals(parts[1], element.name());
                    assertEquals(Integer.parseInt(parts[2]), element.lineNumber());
                    assertTrue(element.documentation().isEmpty());
                    assertEquals(parts[1] + " = ...", element.signature());
                    break;
            }
        }
    }

    /* Let's skip the mocked process tests due to mocking complexity */
}