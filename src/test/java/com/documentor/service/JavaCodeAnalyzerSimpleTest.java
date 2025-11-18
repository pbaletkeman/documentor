package com.documentor.service;

import com.documentor.config.DocumentorConfig;
import com.documentor.model.CodeElement;
import com.documentor.service.analysis.JavaElementVisitor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doThrow;

/**
 * Simple coverage enhancement tests for JavaCodeAnalyzer.
 */
@ExtendWith(MockitoExtension.class)
class JavaCodeAnalyzerSimpleTest {

    @Mock
    private DocumentorConfig mockConfig;

    @Mock
    private JavaElementVisitor mockElementVisitor;

    private JavaCodeAnalyzer javaCodeAnalyzer;

    @TempDir
    private Path tempDir;

    @BeforeEach
    void setUp() {
        javaCodeAnalyzer = new JavaCodeAnalyzer(mockConfig, mockElementVisitor);
    }

    @Test
    void analyzeFileWithPrivateMembersOverride() throws IOException {
        // Arrange
        Path javaFile = tempDir.resolve("TestClass.java");
        String javaCode = "public class TestClass "
            + "{ private void privateMethod() {} }";
        Files.writeString(javaFile, javaCode);

        // Act
        List<CodeElement> result = javaCodeAnalyzer.analyzeFile(
            javaFile, Boolean.TRUE);

        // Assert
        assertNotNull(result);
        verify(mockElementVisitor).initialize(javaFile, result, Boolean.TRUE);
    }

    @Test
    void analyzeFileWithNullPrivateMembersOverride() throws IOException {
        // Arrange
        Path javaFile = tempDir.resolve("TestClass.java");
        String javaCode = "public class TestClass { public void method() {} }";
        Files.writeString(javaFile, javaCode);

        // Act
        List<CodeElement> result = javaCodeAnalyzer
            .analyzeFile(javaFile, null);

        // Assert
        assertNotNull(result);
        verify(mockElementVisitor).initialize(javaFile, result, null);
    }

    @Test
    void analyzeFileWithVisitorException() throws IOException {
        // Arrange
        Path javaFile = tempDir.resolve("TestClass.java");
        String javaCode = "public class TestClass {}";
        Files.writeString(javaFile, javaCode);

        doThrow(new RuntimeException("Visitor failed"))
            .when(mockElementVisitor).initialize(any(), any(), any());

        // Act & Assert
        assertThrows(IOException.class, () ->
            javaCodeAnalyzer.analyzeFile(javaFile));
    }
}
