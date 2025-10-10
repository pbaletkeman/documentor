package com.documentor.service;

import com.documentor.config.DocumentorConfig;
import com.documentor.model.CodeElement;
import com.documentor.model.CodeElementType;
import com.documentor.service.analysis.JavaElementVisitor;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JavaCodeAnalyzerTest {

    @Mock
    private DocumentorConfig mockConfig;
    
    @Mock
    private JavaElementVisitor mockElementVisitor;
    
    @Mock
    private JavaParser mockJavaParser;
    
    @Mock
    private CompilationUnit mockCompilationUnit;
    
    private JavaCodeAnalyzer javaCodeAnalyzer;
    
    @BeforeEach
    void setUp() {
        // Create the JavaCodeAnalyzer with mocked dependencies
        javaCodeAnalyzer = new JavaCodeAnalyzer(mockConfig, mockElementVisitor);
        
        // Use reflection to inject the mockJavaParser
        try {
            var javaParserField = JavaCodeAnalyzer.class.getDeclaredField("javaParser");
            javaParserField.setAccessible(true);
            javaParserField.set(javaCodeAnalyzer, mockJavaParser);
        } catch (Exception e) {
            fail("Failed to inject mock parser: " + e.getMessage());
        }
    }
    
    @Test
    void analyzeFile_shouldParseAndVisitCompilationUnit() throws IOException {
        // Arrange
        Path mockPath = Mockito.mock(Path.class);
        when(mockPath.getFileName()).thenReturn(mockPath);
        when(mockPath.toString()).thenReturn("TestFile.java");
        
        List<CodeElement> expectedElements = new ArrayList<>();
        // Creating a CodeElement with the correct constructor parameters
        CodeElement element = new CodeElement(
            CodeElementType.CLASS,
            "TestClass",
            "com.example.TestClass", // qualified name
            "TestFile.java", // file path
            1, // line number
            "public class TestClass {}", // signature
            "", // documentation
            Collections.emptyList(), // parameters
            Collections.emptyList() // annotations
        );
        expectedElements.add(element);
        
        String mockSourceCode = "public class TestClass {}";
        
        // Use mockStatic for Files
        try (var mockFiles = mockStatic(Files.class)) {
            mockFiles.when(() -> Files.readString(eq(mockPath))).thenReturn(mockSourceCode);
            
            // Mock JavaParser behavior - use direct when() call
            @SuppressWarnings("unchecked")
            ParseResult<CompilationUnit> mockResult = mock(ParseResult.class);
            when(mockResult.getResult()).thenReturn(Optional.of(mockCompilationUnit));
            when(mockJavaParser.parse(eq(mockSourceCode))).thenReturn(mockResult);
            
            // Mock visitor behavior - capture the elements list
            doAnswer(invocation -> {
                List<CodeElement> elements = invocation.getArgument(1);
                elements.addAll(expectedElements);
                return null;
            }).when(mockElementVisitor).initialize(eq(mockPath), any());
            
            // Act
            List<CodeElement> result = javaCodeAnalyzer.analyzeFile(mockPath);
            
            // Assert
            assertEquals(expectedElements.size(), result.size());
            assertEquals(expectedElements.get(0), result.get(0));
            
            // Verify interactions
            verify(mockElementVisitor).initialize(eq(mockPath), any());
            verify(mockElementVisitor).visit(eq(mockCompilationUnit), eq(null));
        }
    }
    
    @Test
    void analyzeFile_shouldThrowIOException_whenParsingFails() throws IOException {
        // Arrange
        Path mockPath = Mockito.mock(Path.class);
        when(mockPath.toString()).thenReturn("TestFile.java");
        String mockSourceCode = "invalid java code";
        
        // Use mockStatic for Files
        try (var mockFiles = mockStatic(Files.class)) {
            mockFiles.when(() -> Files.readString(eq(mockPath))).thenReturn(mockSourceCode);
            
            // Mock JavaParser to return empty result
            @SuppressWarnings("unchecked")
            ParseResult<CompilationUnit> mockResult = mock(ParseResult.class);
            when(mockResult.getResult()).thenReturn(Optional.empty());
            when(mockJavaParser.parse(eq(mockSourceCode))).thenReturn(mockResult);
            
            // Act & Assert
            IOException exception = assertThrows(IOException.class, () -> javaCodeAnalyzer.analyzeFile(mockPath));
            assertEquals("Failed to parse Java file", exception.getMessage());
        }
    }
    
    @Test
    void analyzeFile_shouldPropagateExceptions() throws IOException {
        // Arrange
        Path mockPath = Mockito.mock(Path.class);
        when(mockPath.toString()).thenReturn("TestFile.java");
        
        // Use mockStatic for Files
        try (var mockFiles = mockStatic(Files.class)) {
            mockFiles.when(() -> Files.readString(eq(mockPath))).thenThrow(new IOException("File not found"));
            
            // Act & Assert
            IOException exception = assertThrows(IOException.class, () -> javaCodeAnalyzer.analyzeFile(mockPath));
            assertEquals("File not found", exception.getMessage());
        }
    }
}