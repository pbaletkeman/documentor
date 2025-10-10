package com.documentor.service.diagram;

import com.documentor.model.CodeElement;
import com.documentor.model.CodeElementType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MermaidClassDiagramGeneratorTest {

    private final MermaidClassDiagramGenerator generator = new MermaidClassDiagramGenerator();
    
    @Test
    @DisplayName("Should generate class diagram with fields and methods")
    void generateClassDiagram(@TempDir Path tempDir) throws IOException {
        // Given
        CodeElement classElement = new CodeElement(
            CodeElementType.CLASS, 
            "TestClass", 
            "com.example.TestClass", 
            "/path/TestClass.java", 
            1, 
            "public class TestClass {}", 
            "Test class", 
            List.of(), 
            List.of("@Component")
        );
        
        CodeElement fieldElement = new CodeElement(
            CodeElementType.FIELD, 
            "testField", 
            "private String testField", 
            "/path/TestClass.java", 
            2, 
            "private String testField;", 
            "Test field", 
            List.of(), 
            List.of()
        );
        
        CodeElement methodElement = new CodeElement(
            CodeElementType.METHOD, 
            "testMethod", 
            "public void testMethod(String param)", 
            "/path/TestClass.java", 
            3, 
            "public void testMethod(String param) {}", 
            "Test method", 
            List.of("String param"), 
            List.of("@Test")
        );
        
        List<CodeElement> elements = Arrays.asList(classElement, fieldElement, methodElement);
        
        // When
        String result = generator.generateClassDiagram(classElement, elements, tempDir);
        
        // Then
        assertNotNull(result);
        
        // Verify file was created
        Path diagramPath = tempDir.resolve("TestClass_diagram.md");
        assertTrue(Files.exists(diagramPath));
        String fileContent = Files.readString(diagramPath);
        
        // Check for expected content in file content
        assertTrue(fileContent.contains("# TestClass Class Diagram"));
        assertTrue(fileContent.contains("```mermaid"));
        assertTrue(fileContent.contains("classDiagram"));
        assertTrue(fileContent.contains("class TestClass"));
        
        // File path is returned
        assertEquals(diagramPath.toString(), result);
    }
}