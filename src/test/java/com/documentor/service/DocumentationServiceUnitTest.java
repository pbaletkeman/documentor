package com.documentor.service;

import com.documentor.config.DocumentorConfig;
import com.documentor.config.model.AnalysisSettings;
import com.documentor.config.model.LlmModelConfig;
import com.documentor.config.model.OutputSettings;
import com.documentor.model.CodeElement;
import com.documentor.model.CodeElementType;
import com.documentor.model.ProjectAnalysis;
import com.documentor.service.documentation.ElementDocumentationGenerator;
import com.documentor.service.documentation.MainDocumentationGenerator;
import com.documentor.service.documentation.UnitTestDocumentationGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DocumentationServiceUnitTest {

    @Mock
    private MainDocumentationGenerator mainGenerator;

    @Mock
    private ElementDocumentationGenerator elementGenerator;

    @Mock
    private UnitTestDocumentationGenerator testGenerator;

    @Mock
    private MermaidDiagramService mermaidService;

    private DocumentationService documentationService;
    private DocumentorConfig config;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        OutputSettings outputSettings = new OutputSettings(
            tempDir.toString(), "markdown", true, true
        );

        AnalysisSettings analysisSettings = new AnalysisSettings(true, 5, List.of("**/*.java"), List.of("**/test/**"));

        LlmModelConfig model = new LlmModelConfig("m", "ollama", "http://x", null, 500, 10);
        config = new DocumentorConfig(List.of(model), outputSettings, analysisSettings);

        documentationService = new DocumentationService(mainGenerator, elementGenerator,
                testGenerator, mermaidService, config);
    }

    @Test
    void testGenerateDocumentationWritesFilesAndInvokesGenerators() throws Exception {
        // Arrange
        CodeElement element = new CodeElement(CodeElementType.CLASS, "TestClass", "com.test.TestClass",
            "/src/TestClass.java", 1, "public class TestClass {}", "", List.of(), List.of());

        ProjectAnalysis analysis = new ProjectAnalysis("/project", List.of(element), System.currentTimeMillis());

        when(mainGenerator.generateMainDocumentation(any()))
                .thenReturn(CompletableFuture.completedFuture("# README"));
        when(elementGenerator.generateElementDocumentation(any(), any()))
                .thenReturn(CompletableFuture.completedFuture(null));
        when(testGenerator.generateUnitTestDocumentation(any(), any()))
                .thenReturn(CompletableFuture.completedFuture(null));
        when(mermaidService.generateClassDiagrams(any(), anyString()))
                .thenReturn(CompletableFuture.completedFuture(List.of("diagram1")));

        // Act
        CompletableFuture<String> result = documentationService.generateDocumentation(analysis);

        // Assert
        assertNotNull(result);
        String outputPath = result.get();
        assertEquals(tempDir.toString(), outputPath);

        // README.md should exist
        Path readme = tempDir.resolve("README.md");
        assertTrue(Files.exists(readme));
        String content = Files.readString(readme);
        assertTrue(content.contains("# README"));

        // Verify generators invoked
        verify(mainGenerator, atLeastOnce()).generateMainDocumentation(any());
        verify(elementGenerator, atLeastOnce()).generateElementDocumentation(any(), any());
        verify(testGenerator, atLeastOnce()).generateUnitTestDocumentation(any(), any());
        verify(mermaidService, atLeastOnce()).generateClassDiagrams(any(), anyString());
    }

    @Test
    void testGenerateDocumentationHandlesEmptyAnalysis() throws Exception {
        ProjectAnalysis empty = new ProjectAnalysis("/empty", List.of(), System.currentTimeMillis());

        when(mainGenerator.generateMainDocumentation(any()))
                .thenReturn(CompletableFuture.completedFuture("# README"));
        // Stub other generators that may be invoked by configuration defaults
        when(testGenerator.generateUnitTestDocumentation(any(), any()))
                .thenReturn(CompletableFuture.completedFuture(null));
        when(mermaidService.generateClassDiagrams(any(), anyString()))
                .thenReturn(CompletableFuture.completedFuture(List.of()));

        CompletableFuture<String> result = documentationService.generateDocumentation(empty);

        assertNotNull(result);
        String outputPath = result.get();
        assertEquals(tempDir.toString(), outputPath);

        // Should not call element generator for empty analysis
        verify(elementGenerator, never()).generateElementDocumentation(any(), any());
    }

    @Test
    void testGenerateDocumentationWithMermaidDiagramsDisabled() throws Exception {
        // Create config with generateMermaidDiagrams = false (third parameter)
        OutputSettings outputSettings = new OutputSettings(
            tempDir.toString(), "markdown", false, true
        );
        AnalysisSettings analysisSettings = new AnalysisSettings(true, 5, List.of("**/*.java"), List.of("**/test/**"));
        DocumentorConfig testConfig = new DocumentorConfig(List.of(), outputSettings, analysisSettings);
        DocumentationService testService = new DocumentationService(mainGenerator, elementGenerator,
                testGenerator, mermaidService, testConfig);

        CodeElement element = new CodeElement(CodeElementType.CLASS, "TestClass", "com.test.TestClass",
            "/src/TestClass.java", 1, "public class TestClass {}", "", List.of(), List.of());
        ProjectAnalysis analysis = new ProjectAnalysis("/project", List.of(element), System.currentTimeMillis());

        when(mainGenerator.generateMainDocumentation(any()))
                .thenReturn(CompletableFuture.completedFuture("# README"));
        when(elementGenerator.generateElementDocumentation(any(), any()))
                .thenReturn(CompletableFuture.completedFuture(null));
        when(testGenerator.generateUnitTestDocumentation(any(), any()))
                .thenReturn(CompletableFuture.completedFuture(null));

        // Act
        CompletableFuture<String> result = testService.generateDocumentation(analysis);

        // Assert
        assertNotNull(result);
        String outputPath = result.get();
        assertEquals(tempDir.toString(), outputPath);

        // Verify mermaid service was NOT called since generateMermaid = false
        verify(mermaidService, never()).generateClassDiagrams(any(), anyString());
        // Unit test generator should still be called since generateUnitTests() always returns true
        verify(testGenerator, atLeastOnce()).generateUnitTestDocumentation(any(), any());
    }

    @Test
    void testGenerateDocumentationWithUnitTestsDisabled() throws Exception {
        // Create a mock OutputSettings that returns false for generateUnitTests()
        OutputSettings mockOutputSettings = mock(OutputSettings.class);
        when(mockOutputSettings.outputPath()).thenReturn(tempDir.toString());
        when(mockOutputSettings.generateUnitTests()).thenReturn(false);
        when(mockOutputSettings.generateMermaidDiagrams()).thenReturn(true);
        when(mockOutputSettings.mermaidOutputPath()).thenReturn(tempDir.toString());

        AnalysisSettings analysisSettings = new AnalysisSettings(true, 5, List.of("**/*.java"), List.of("**/test/**"));
        DocumentorConfig testConfig = new DocumentorConfig(List.of(), mockOutputSettings, analysisSettings);
        DocumentationService testService = new DocumentationService(mainGenerator, elementGenerator,
                testGenerator, mermaidService, testConfig);

        CodeElement element = new CodeElement(CodeElementType.CLASS, "TestClass", "com.test.TestClass",
            "/src/TestClass.java", 1, "public class TestClass {}", "", List.of(), List.of());
        ProjectAnalysis analysis = new ProjectAnalysis("/project", List.of(element), System.currentTimeMillis());

        when(mainGenerator.generateMainDocumentation(any()))
                .thenReturn(CompletableFuture.completedFuture("# README"));
        when(elementGenerator.generateElementDocumentation(any(), any()))
                .thenReturn(CompletableFuture.completedFuture(null));
        when(mermaidService.generateClassDiagrams(any(), anyString()))
                .thenReturn(CompletableFuture.completedFuture(List.of("diagram1")));

        // Act
        CompletableFuture<String> result = testService.generateDocumentation(analysis);

        // Assert
        assertNotNull(result);
        String outputPath = result.get();
        assertEquals(tempDir.toString(), outputPath);

        // Verify unit test generator was NOT called since generateUnitTests = false
        verify(testGenerator, never()).generateUnitTestDocumentation(any(), any());
        // Mermaid service should still be called since generateMermaidDiagrams = true
        verify(mermaidService, atLeastOnce()).generateClassDiagrams(any(), anyString());
    }
}
