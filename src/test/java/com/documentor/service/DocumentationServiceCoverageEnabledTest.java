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
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocumentationServiceCoverageEnabledTest {

    @Mock
    private MainDocumentationGenerator mainDocGenerator;

    @Mock
    private ElementDocumentationGenerator elementDocGenerator;

    @Mock
    private UnitTestDocumentationGenerator unitTestDocumentationGenerator;

    @Mock
    private MermaidDiagramService mermaidDiagramService;

    @TempDir
    private Path tempDir;

    private DocumentorConfig config;

    @BeforeEach
    void setUp() {
        // Build a minimal DocumentorConfig with sensible defaults pointing at the temp directory
        LlmModelConfig model = new LlmModelConfig("test-model", "ollama", "http://localhost", null, null, null);
        OutputSettings outputSettings = new OutputSettings(tempDir.toString(), "md", true, false);
        AnalysisSettings analysisSettings = new AnalysisSettings(null, null, null, null);
        config = new DocumentorConfig(List.of(model), outputSettings, analysisSettings);
    }

    @Test
    void testGenerateDocumentationWritesReadmeAndInvokesGenerators() throws Exception {
        when(mainDocGenerator.generateMainDocumentation(any()))
            .thenReturn(CompletableFuture.completedFuture("# Project Title\nContent"));


        // Use lenient stubbing for generators that may not be invoked for an empty analysis
        lenient().when(unitTestDocumentationGenerator.generateUnitTestDocumentation(any(), any()))
            .thenReturn(CompletableFuture.completedFuture(null));

        lenient().when(mermaidDiagramService.generateClassDiagrams(any(), any()))
            .thenReturn(CompletableFuture.completedFuture(List.of()));

        DocumentationService documentationService = new DocumentationService(
            mainDocGenerator,
            elementDocGenerator,
            unitTestDocumentationGenerator,
            mermaidDiagramService,
            config
        );

        ProjectAnalysis analysis = new ProjectAnalysis(tempDir.toString(), List.of(), System.currentTimeMillis());

        CompletableFuture<String> future = documentationService.generateDocumentation(analysis);
        String outputPath = future.get();

        assertEquals(tempDir.toString(), outputPath);

        Path readme = tempDir.resolve("README.md");
        assertTrue(Files.exists(readme), "README.md should be written to output directory");

        String content = Files.readString(readme);
        assertTrue(content.contains("# Project Title"));

        verify(mainDocGenerator, times(1)).generateMainDocumentation(any());
        verify(elementDocGenerator, atMost(1)).generateElementDocumentation(any(), any());
        verify(mermaidDiagramService, times(1)).generateClassDiagrams(any(), any());
    }

    @Test
    void testGenerateDocumentationWithElementsCallsElementGenerator() throws Exception {
        when(mainDocGenerator.generateMainDocumentation(any()))
            .thenReturn(CompletableFuture.completedFuture("Main content"));

        when(elementDocGenerator.generateElementDocumentation(any(), any()))
            .thenReturn(CompletableFuture.completedFuture(null));

        // Also stub mermaid and unit test generator to avoid NPEs (they are invoked)
        when(mermaidDiagramService.generateClassDiagrams(any(), any()))
            .thenReturn(CompletableFuture.completedFuture(List.of()));
        when(unitTestDocumentationGenerator.generateUnitTestDocumentation(any(), any()))
            .thenReturn(CompletableFuture.completedFuture(null));

        DocumentationService documentationService = new DocumentationService(
            mainDocGenerator,
            elementDocGenerator,
            unitTestDocumentationGenerator,
            mermaidDiagramService,
            config
        );

        CodeElement elem = new CodeElement(
            CodeElementType.CLASS,
            "TestClass",
            "com.example.TestClass",
            tempDir.resolve("TestClass.java").toString(),
            1,
            "public class TestClass",
            "", List.of(), List.of()
        );

        ProjectAnalysis analysis = new ProjectAnalysis(tempDir.toString(), List.of(elem), System.currentTimeMillis());

        CompletableFuture<String> future = documentationService.generateDocumentation(analysis);
        future.get();

        verify(elementDocGenerator, times(1)).generateElementDocumentation(any(), any());
    }
}

