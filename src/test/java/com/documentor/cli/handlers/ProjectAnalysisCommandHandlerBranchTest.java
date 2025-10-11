package com.documentor.cli.handlers;

import com.documentor.model.ProjectAnalysis;
import com.documentor.service.CodeAnalysisService;
import com.documentor.service.DocumentationService;
import com.documentor.service.MermaidDiagramService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;

/**
 * Additional branch coverage tests for ProjectAnalysisCommandHandler.
 * These tests focus on improving branch coverage for the cli.handlers package.
 */
class ProjectAnalysisCommandHandlerBranchTest {

    private ProjectAnalysisCommandHandler handler;
    private CodeAnalysisService analysisService;
    private DocumentationService documentationService;
    private MermaidDiagramService mermaidService;
    private CommonCommandHandler commonHandler;

    @BeforeEach
    void setUp() {
        analysisService = mock(CodeAnalysisService.class);
        documentationService = mock(DocumentationService.class);
        mermaidService = mock(MermaidDiagramService.class);
        commonHandler = mock(CommonCommandHandler.class);
        handler = new ProjectAnalysisCommandHandler(analysisService, documentationService,
                mermaidService, commonHandler);

        // Setup default behavior for commonHandler
        when(commonHandler.createResultBuilder()).thenReturn(new StringBuilder());
        when(commonHandler.formatStatistics(any(), any())).thenAnswer(invocation -> {
            String title = invocation.getArgument(0);
            return "📊 " + title + "\n════════════════════════════════\n\n";
        });
    }

    @Test
    void handleAnalyzeProjectWithoutMermaidGeneration(@TempDir final Path tmp) {
        // Test the false branch of generateMermaid condition
        ProjectAnalysis pa = new ProjectAnalysis(tmp.toString(), List.of(),
                System.currentTimeMillis());
        when(analysisService.analyzeProject(tmp)).thenReturn(CompletableFuture.completedFuture(pa));
        when(documentationService.generateDocumentation(pa))
                .thenReturn(CompletableFuture.completedFuture(tmp.toString()));
        when(commonHandler.directoryExists(tmp.toString())).thenReturn(true);

        // Act with generateMermaid = false
        String result = handler.handleAnalyzeProject(tmp.toString(), "", false, "");

        // Assert
        verify(mermaidService, never()).generateClassDiagrams(any(), any());
        assertTrue(result.contains("Documentation generated at"));
        assertTrue(!result.contains("Mermaid diagrams")); // Should not contain mermaid output
    }

    @Test
    void handleMermaidGenerationWithEmptyOutput(@TempDir final Path tmp) {
        // Test the ternary operator branch in handleMermaidGeneration with empty mermaidOutput
        ProjectAnalysis pa = new ProjectAnalysis(tmp.toString(), List.of(),
                System.currentTimeMillis());
        when(analysisService.analyzeProject(tmp)).thenReturn(CompletableFuture.completedFuture(pa));
        when(documentationService.generateDocumentation(pa))
                .thenReturn(CompletableFuture.completedFuture(tmp.toString()));
        when(mermaidService.generateClassDiagrams(pa, null))
                .thenReturn(CompletableFuture.completedFuture(List.of("d1")));
        when(commonHandler.directoryExists(tmp.toString())).thenReturn(true);

        // Act with generateMermaid = true but empty mermaidOutput (should pass null to service)
        String result = handler.handleAnalyzeProject(tmp.toString(), "", true, "");

        // Assert
        verify(mermaidService).generateClassDiagrams(pa, null); // Should pass null for empty output
        assertTrue(result.contains("Mermaid diagrams"));
    }

    @Test
    void handleMermaidGenerationWithSpecificOutput(@TempDir final Path tmp) {
        // Test the ternary operator branch in handleMermaidGeneration with specific mermaidOutput
        ProjectAnalysis pa = new ProjectAnalysis(tmp.toString(), List.of(),
                System.currentTimeMillis());
        when(analysisService.analyzeProject(tmp)).thenReturn(CompletableFuture.completedFuture(pa));
        when(documentationService.generateDocumentation(pa))
                .thenReturn(CompletableFuture.completedFuture(tmp.toString()));
        when(mermaidService.generateClassDiagrams(pa, "custom-output"))
                .thenReturn(CompletableFuture.completedFuture(List.of("d1", "d2")));
        when(commonHandler.directoryExists(tmp.toString())).thenReturn(true);

        // Act with generateMermaid = true and specific mermaidOutput
        String result = handler.handleAnalyzeProject(tmp.toString(), "", true, "custom-output");

        // Assert
        verify(mermaidService).generateClassDiagrams(pa, "custom-output"); // Should pass specific output path
        assertTrue(result.contains("Mermaid diagrams"));
        assertTrue(result.contains("2 diagrams generated"));
    }
}