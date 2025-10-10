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
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

class ProjectAnalysisCommandHandlerTest {

    private CodeAnalysisService analysisService;
    private DocumentationService documentationService;
    private MermaidDiagramService mermaidService;
    private ProjectAnalysisCommandHandler handler;

    @BeforeEach
    void setUp() {
        analysisService = mock(CodeAnalysisService.class);
        documentationService = mock(DocumentationService.class);
        mermaidService = mock(MermaidDiagramService.class);

        handler = new ProjectAnalysisCommandHandler(analysisService, documentationService, mermaidService);
    }

    @Test
    void handleScanProjectReturnsFormattedStats(@TempDir Path tmp) {
        ProjectAnalysis pa = new ProjectAnalysis(tmp.toString(), List.of(new CodeElement(CodeElementType.CLASS, "A", "A", "A.java", 1, "sig", "", List.of(), List.of())), System.currentTimeMillis());
        when(analysisService.analyzeProject(tmp)).thenReturn(CompletableFuture.completedFuture(pa));

        String out = handler.handleScanProject(tmp.toString());

        assertTrue(out.contains("Project Analysis Statistics"));
        assertTrue(out.contains("Total Elements"));
    }

    @Test
    void handleAnalyzeProjectGeneratesDocsAndMermaid(@TempDir Path tmp) {
        ProjectAnalysis pa = new ProjectAnalysis(tmp.toString(), List.of(), System.currentTimeMillis());
        when(analysisService.analyzeProject(tmp)).thenReturn(CompletableFuture.completedFuture(pa));
        when(documentationService.generateDocumentation(pa)).thenReturn(CompletableFuture.completedFuture(tmp.toString()));
        when(mermaidService.generateClassDiagrams(pa, "out")).thenReturn(CompletableFuture.completedFuture(List.of("d1", "d2")));

        String res = handler.handleAnalyzeProject(tmp.toString(), "", true, "out");

        assertTrue(res.contains("Documentation generated at"));
        assertTrue(res.contains("Mermaid diagrams"));
    }
}
