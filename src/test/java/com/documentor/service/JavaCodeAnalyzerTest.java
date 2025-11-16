package com.documentor.service;

import com.documentor.config.DocumentorConfig;
import com.documentor.model.CodeElement;
import com.documentor.service.analysis.JavaElementVisitor;
import com.github.javaparser.ast.CompilationUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class JavaCodeAnalyzerTest {

    @Mock
    private DocumentorConfig mockConfig;

    @Mock
    private JavaElementVisitor mockElementVisitor;

    @Mock
    private CompilationUnit mockCompilationUnit;

    private JavaCodeAnalyzer javaCodeAnalyzer;

    @BeforeEach
    void setUp() {
        // Create the JavaCodeAnalyzer with mocked dependencies
        javaCodeAnalyzer = new JavaCodeAnalyzer(mockConfig, mockElementVisitor);
    }

    @Test
    void analyzeFileShouldParseAndVisitCompilationUnit() throws IOException {
        // Arrange
        Path tempFile = Files.createTempFile("test", ".java");
        String validJavaCode =
            "public class TestClass { public void testMethod() {} }";
        Files.writeString(tempFile, validJavaCode);

        List<CodeElement> expectedElements = new ArrayList<>();

        // Mock visitor behavior - capture the elements list
        doAnswer(invocation -> {
            List<CodeElement> elements = invocation.getArgument(1);
            elements.addAll(expectedElements);
            return null;
        }).when(mockElementVisitor).initialize(eq(tempFile), any(), any());

        try {
            // Act
            List<CodeElement> result = javaCodeAnalyzer.analyzeFile(tempFile);

            // Assert
            assertNotNull(result);
            verify(mockElementVisitor).initialize(eq(tempFile), any(), any());
            verify(mockElementVisitor).visit(any(
                CompilationUnit.class), eq(null));
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    @Test
    void analyzeFileShouldThrowIOExceptionWhenFileNotFound() {
        // Arrange
        Path nonExistentPath = Path.of("non-existent-file.java");

        // Act & Assert
        IOException exception = assertThrows(
            IOException.class, () ->
            javaCodeAnalyzer.analyzeFile(nonExistentPath));
        // Just verify that an IOException is thrown,
        // don't check the specific message since it's wrapped
        assertNotNull(exception);
    }

    @Test
    void analyzeFileShouldCallVisitorProperly() throws IOException {
        // Arrange
        Path tempFile = Files.createTempFile("test", ".java");
        String validJavaCode = "public class TestClass {}";
        Files.writeString(tempFile, validJavaCode);

        try {
            // Act
            List<CodeElement> result = javaCodeAnalyzer.analyzeFile(tempFile);

            // Assert
            assertNotNull(result);
            verify(mockElementVisitor).initialize(eq(tempFile), any(), any());
            verify(mockElementVisitor).visit(any(CompilationUnit.class),
                eq(null));
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }
}
