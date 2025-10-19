package com.documentor.service;

import com.documentor.config.DocumentorConfig;
import com.documentor.config.model.AnalysisSettings;
import com.documentor.model.CodeElement;
import com.documentor.model.CodeElementType;
import com.documentor.model.ProjectAnalysis;
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
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

/**
 * Unit tests for CodeAnalysisService
 */
@ExtendWith(MockitoExtension.class)
class CodeAnalysisServiceTest {

    // Test constants for magic number violations
    private static final int DEFAULT_MAX_THREADS = 4;

    @Mock
    private JavaCodeAnalyzer javaCodeAnalyzer;

    @Mock
    private PythonCodeAnalyzer pythonCodeAnalyzer;

    @Mock
    private DocumentorConfig config;

    @Mock
    private AnalysisSettings analysisSettings;

    private CodeAnalysisService codeAnalysisService;

    @TempDir
    private Path tempDir;

    @BeforeEach
    void setUp() {
        // Use lenient mode to avoid UnnecessaryStubbingException
        lenient().when(config.analysisSettings()).thenReturn(analysisSettings);
        lenient().when(analysisSettings.maxThreads()).thenReturn(DEFAULT_MAX_THREADS);
        codeAnalysisService = new CodeAnalysisService(javaCodeAnalyzer, pythonCodeAnalyzer, config);
    }

    @Test
    void testAnalyzeProjectEmptyDirectory() throws Exception {
        CompletableFuture<ProjectAnalysis> future = codeAnalysisService.analyzeProject(tempDir);
        ProjectAnalysis analysis = future.get();

        assertNotNull(analysis);
        assertEquals(tempDir.toString(), analysis.projectPath());
        assertTrue(analysis.codeElements().isEmpty());
        assertEquals(0, analysis.getStats().totalElements());
    }

    @Test
    void testAnalyzeProjectWithJavaFiles() throws Exception {
        // Create Java file
        Path javaFile = tempDir.resolve("Test.java");
        Files.writeString(javaFile, """
            public class Test {
                public void method() {}
            }
            """);

        // Mock Java analyzer response
        CodeElement classElement = new CodeElement(
            CodeElementType.CLASS, "Test", "Test", javaFile.toString(), 1,
            "public class Test", "", List.of(), List.of()
        );
        CodeElement methodElement = new CodeElement(
            CodeElementType.METHOD, "method", "Test.method", javaFile.toString(), 2,
            "public void method()", "", List.of(), List.of()
        );
        when(javaCodeAnalyzer.analyzeFile(any(Path.class), any()))
            .thenReturn(List.of(classElement, methodElement));

        CompletableFuture<ProjectAnalysis> future = codeAnalysisService.analyzeProject(tempDir);
        ProjectAnalysis analysis = future.get();

        assertNotNull(analysis);
        assertEquals(2, analysis.codeElements().size());
        assertEquals(2, analysis.getStats().totalElements());
        assertTrue(analysis.codeElements().contains(classElement));
        assertTrue(analysis.codeElements().contains(methodElement));
    }

    @Test
    void testAnalyzeProjectWithPythonFiles() throws Exception {
        // Create Python file
        Path pythonFile = tempDir.resolve("test.py");
        Files.writeString(pythonFile, """
            class TestClass:
                def method(self):
                    pass
            """);

        // Mock Python analyzer response
        CodeElement classElement = new CodeElement(
            CodeElementType.CLASS, "TestClass", "TestClass", pythonFile.toString(), 1,
            "class TestClass:", "", List.of(), List.of()
        );
        when(pythonCodeAnalyzer.analyzeFile(any(Path.class), any()))
            .thenReturn(List.of(classElement));

        CompletableFuture<ProjectAnalysis> future = codeAnalysisService.analyzeProject(tempDir);
        ProjectAnalysis analysis = future.get();

        assertNotNull(analysis);
        assertEquals(1, analysis.codeElements().size());
        assertEquals(1, analysis.getStats().totalElements());
        assertTrue(analysis.codeElements().contains(classElement));
    }

    @Test
    void testAnalyzeProjectMixedFiles() throws Exception {
        // Create both Java and Python files
        Path javaFile = tempDir.resolve("Test.java");
        Files.writeString(javaFile, "public class Test {}");

        Path pythonFile = tempDir.resolve("test.py");
        Files.writeString(pythonFile, "class TestClass: pass");

        // Mock analyzer responses
        CodeElement javaElement = new CodeElement(
            CodeElementType.CLASS, "Test", "Test", javaFile.toString(), 1,
            "public class Test", "", List.of(), List.of()
        );
        CodeElement pythonElement = new CodeElement(
            CodeElementType.CLASS, "TestClass", "TestClass", pythonFile.toString(), 1,
            "class TestClass:", "", List.of(), List.of()
        );

        when(javaCodeAnalyzer.analyzeFile(eq(javaFile), any())).thenReturn(List.of(javaElement));
        when(pythonCodeAnalyzer.analyzeFile(eq(pythonFile), any())).thenReturn(List.of(pythonElement));

        CompletableFuture<ProjectAnalysis> future = codeAnalysisService.analyzeProject(tempDir);
        ProjectAnalysis analysis = future.get();

        assertNotNull(analysis);
        assertEquals(2, analysis.codeElements().size());
        assertEquals(2, analysis.getStats().totalElements());
        assertTrue(analysis.codeElements().contains(javaElement));
        assertTrue(analysis.codeElements().contains(pythonElement));
    }

    @Test
    void testAnalyzeProjectIgnoresNonCodeFiles() throws Exception {
        // Create various file types
        Files.writeString(tempDir.resolve("README.md"), "# Test");
        Files.writeString(tempDir.resolve("config.json"), "{}");
        Files.writeString(tempDir.resolve("image.png"), "fake image data");
        Files.writeString(tempDir.resolve("script.sh"), "#!/bin/bash");

        CompletableFuture<ProjectAnalysis> future = codeAnalysisService.analyzeProject(tempDir);
        ProjectAnalysis analysis = future.get();

        assertNotNull(analysis);
        assertTrue(analysis.codeElements().isEmpty());
        assertEquals(0, analysis.getStats().totalElements());
    }

    @Test
    void testAnalyzeProjectHandlesNestedDirectories() throws Exception {
        // Create nested directory structure
        Path subDir = tempDir.resolve("src").resolve("main").resolve("java");
        Files.createDirectories(subDir);

        Path javaFile = subDir.resolve("Test.java");
        Files.writeString(javaFile, "public class Test {}");

        CodeElement element = new CodeElement(
            CodeElementType.CLASS, "Test", "Test", javaFile.toString(), 1,
            "public class Test", "", List.of(), List.of()
        );
        when(javaCodeAnalyzer.analyzeFile(eq(javaFile), any())).thenReturn(List.of(element));

        CompletableFuture<ProjectAnalysis> future = codeAnalysisService.analyzeProject(tempDir);
        ProjectAnalysis analysis = future.get();

        assertNotNull(analysis);
        assertEquals(1, analysis.codeElements().size());
        assertTrue(analysis.codeElements().contains(element));
    }

    @Test
    void testAnalyzeProjectHandlesAnalysisErrors() throws Exception {
        Path javaFile = tempDir.resolve("Test.java");
        Files.writeString(javaFile, "public class Test {}");

        // Mock analyzer to throw exception
        when(javaCodeAnalyzer.analyzeFile(eq(javaFile), any()))
            .thenThrow(new IOException("Analysis failed"));

        CompletableFuture<ProjectAnalysis> future = codeAnalysisService.analyzeProject(tempDir);
        ProjectAnalysis analysis = future.get();

        assertNotNull(analysis);
        // Should continue with other files despite error
        assertTrue(analysis.codeElements().isEmpty());
    }

    @Test
    void testAnalyzeProjectNonExistentDirectory() {
        Path nonExistent = tempDir.resolve("does-not-exist");

        assertThrows(Exception.class, () -> {
            CompletableFuture<ProjectAnalysis> future = codeAnalysisService.analyzeProject(nonExistent);
            future.get();
        });
    }
}
