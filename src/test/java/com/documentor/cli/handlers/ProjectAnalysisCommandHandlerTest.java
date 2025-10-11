package com.documentor.cli.handlers;

import com.documentor.model.CodeElement;
import com.documentor.model.CodeElementType;
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
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;

/**
 * Updated test for ProjectAnalysisCommandHandler with CommonCommandHandler
 */
class ProjectAnalysisCommandHandlerTest {

    private CodeAnalysisService analysisService;
    private DocumentationService documentationService;
    private MermaidDiagramService mermaidService;
    private CommonCommandHandler commonHandler;
    private ProjectAnalysisCommandHandler handler;

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
        when(commonHandler.formatStatistics(anyString(), anyMap())).thenAnswer(invocation -> {
            String title = invocation.getArgument(0);
            return "📊 " + title
                    + "\n─────────────────────────────────────\n\n";
        });
    }

    @Test
    void handleScanProjectReturnsFormattedStats(@TempDir final Path tmp) {
        // Arrange
        ProjectAnalysis pa = new ProjectAnalysis(tmp.toString(),
            List.of(new CodeElement(CodeElementType.CLASS, "A", "A", "A.java", 1, "sig", "", List.of(), List.of())),
            System.currentTimeMillis());
        when(analysisService.analyzeProject(tmp)).thenReturn(CompletableFuture.completedFuture(pa));
        when(commonHandler.directoryExists(tmp.toString())).thenReturn(true);

        // Act
        String out = handler.handleScanProject(tmp.toString());

        // Assert
        verify(commonHandler).directoryExists(tmp.toString());
        verify(commonHandler).formatStatistics(eq("Project Analysis Statistics"), anyMap());
        assertTrue(out.contains("Project Analysis Statistics"));
    }

    @Test
    void handleAnalyzeProjectGeneratesDocsAndMermaid(@TempDir final Path tmp) {
        // Arrange
        ProjectAnalysis pa = new ProjectAnalysis(tmp.toString(), List.of(), System.currentTimeMillis());
        when(analysisService.analyzeProject(tmp)).thenReturn(CompletableFuture.completedFuture(pa));
        when(documentationService.generateDocumentation(pa)).thenReturn(CompletableFuture.completedFuture(tmp.toString()));
        when(mermaidService.generateClassDiagrams(pa, "out")).thenReturn(CompletableFuture.completedFuture(List.of("d1", "d2")));
        when(commonHandler.directoryExists(tmp.toString())).thenReturn(true);

        // Act
        String res = handler.handleAnalyzeProject(tmp.toString(), "", true, "out");

        // Assert
        verify(commonHandler).directoryExists(tmp.toString());
        verify(commonHandler).createResultBuilder();
        assertTrue(res.contains("Documentation generated at"));
        assertTrue(res.contains("Mermaid diagrams"));
    }

    @Test
    void handleScanProjectHandlesNonExistentDirectory(@TempDir final Path tmp) {
        // Arrange
        String nonExistentPath = tmp.toString() + "/nonexistent";
        when(commonHandler.directoryExists(nonExistentPath)).thenReturn(false);

        // Act
        String result = handler.handleScanProject(nonExistentPath);

        // Assert
        verify(commonHandler).directoryExists(nonExistentPath);
        verify(analysisService, never()).analyzeProject(any(Path.class));
        assertTrue(result.contains("âŒ Error"), "Should return error for non-existent directory");
    }

    @Test
    void handleAnalyzeProjectHandlesException(@TempDir final Path tmp) {
        // Arrange
        Exception exception = new RuntimeException("Test exception");
        when(commonHandler.directoryExists(tmp.toString())).thenReturn(true);
        when(analysisService.analyzeProject(tmp)).thenThrow(exception);
        when(commonHandler.formatErrorMessage(anyString(), eq(exception)))
            .thenReturn("âŒ Error during analysis: Test exception");

        // Act
        String result = handler.handleAnalyzeProject(tmp.toString(), "", false, "");

        // Assert
        verify(commonHandler).formatErrorMessage(anyString(), eq(exception));
        assertTrue(result.contains("âŒ Error during analysis"), "Should format exception message");
    }
}
