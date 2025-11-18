package com.documentor.cli.handlers;

import com.documentor.model.ProjectAnalysis;
import com.documentor.service.CodeAnalysisService;
import com.documentor.service.DocumentationService;
import com.documentor.service.MermaidDiagramService;
import com.documentor.service.PlantUMLDiagramService;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Branch coverage tests for ProjectAnalysisCommandHandler.
 */
@ExtendWith(MockitoExtension.class)
class ProjectAnalysisCommandHandlerBranchTest {

    @Mock
    private CodeAnalysisService codeAnalysisService;

    @Mock
    private DocumentationService documentationService;

    @Mock
    private MermaidDiagramService mermaidDiagramService;

    @Mock
    private PlantUMLDiagramService plantUMLDiagramService;

    @Mock
    private CommonCommandHandler commonHandler;

    @Mock
    private ProjectAnalysis projectAnalysis;

    private ProjectAnalysisCommandHandler handler;

    @TempDir
    private Path tempDir;

    @BeforeEach
    void setUp() {
        handler = new ProjectAnalysisCommandHandler(
                codeAnalysisService, documentationService,
                mermaidDiagramService, plantUMLDiagramService, commonHandler);
    }

    @Test
    void handleAnalyzeProjectExtendedWithInvalidPath() {
        // Given: Invalid project path
        String invalidPath = "/nonexistent/path";
        when(commonHandler.directoryExists(invalidPath)).thenReturn(false);

        // When
        String result = handler.handleAnalyzeProjectExtended(
                invalidPath, "output", false, "", false, "");

        // Then: Returns error message
        assertThat(result).contains("❌ Error: Project path does not exist");
    }

    @Test
    void handleAnalyzeProjectExtendedWithPlantUMLGeneration()
        throws Exception {
        // Given: Valid project path with PlantUML generation enabled
        Path projectPath = Files.createDirectory(tempDir
                .resolve("test-project"));
        when(commonHandler.directoryExists(projectPath.toString()))
        .thenReturn(true);
        when(codeAnalysisService.analyzeProject(any(Path.class)))
                .thenReturn(CompletableFuture
                .completedFuture(projectAnalysis));
        when(documentationService.generateDocumentation(projectAnalysis))
                .thenReturn(CompletableFuture.completedFuture("docs/output"));
        when(commonHandler.createResultBuilder())
                .thenReturn(new StringBuilder());
        when(plantUMLDiagramService.generateClassDiagrams(any(), anyString()))
                .thenReturn(CompletableFuture.completedFuture(
                        List.of("diagram1.puml", "diagram2.puml")));

        // When: Generate with PlantUML enabled
        String result = handler.handleAnalyzeProjectExtended(
                projectPath.toString(), "output", false,
                "", true, "plantuml-output");

        // Then: PlantUML generation is handled
        assertThat(result).contains(
                "✅ Analysis complete!");
        assertThat(result).contains(
                "🌱 PlantUML diagrams: 2 diagrams generated");
    }

    @Test
    void handleAnalyzeProjectExtendedWithMermaidGeneration() throws Exception {
        // Given: Valid project path with Mermaid generation enabled
        Path projectPath = Files.createDirectory(tempDir
                .resolve("test-project"));
        when(commonHandler.directoryExists(projectPath.toString()))
                .thenReturn(true);
        when(codeAnalysisService.analyzeProject(any(Path.class)))
                .thenReturn(CompletableFuture.completedFuture(
                        projectAnalysis));
        when(documentationService.generateDocumentation(projectAnalysis))
                .thenReturn(CompletableFuture.completedFuture("docs/output"));
        when(commonHandler.createResultBuilder()).thenReturn(
                new StringBuilder());
        when(mermaidDiagramService.generateClassDiagrams(any(), anyString()))
                .thenReturn(CompletableFuture.completedFuture(
                        List.of("diagram1.mmd")));

        // When: Generate with Mermaid enabled
        String result = handler.handleAnalyzeProjectExtended(
                projectPath.toString(), "output",
                true, "mermaid-output", false, "");

        // Then: Mermaid generation is handled
        assertThat(result).contains("✅ Analysis complete!");
        assertThat(result).contains(
                "🧩 Mermaid diagrams: 1 diagrams generated");
    }

    @Test
    void handleAnalyzeProjectExtendedWithBothDiagramTypes() throws Exception {
        // Given: Valid project path with both diagram types enabled
        Path projectPath = Files.createDirectory(tempDir.resolve(
                "test-project"));
        when(commonHandler.directoryExists(projectPath.toString()))
                .thenReturn(true);
        when(codeAnalysisService.analyzeProject(any(Path.class)))
                .thenReturn(CompletableFuture.completedFuture(projectAnalysis));
        when(documentationService.generateDocumentation(projectAnalysis))
                .thenReturn(CompletableFuture.completedFuture("docs/output"));
        when(commonHandler.createResultBuilder())
                .thenReturn(new StringBuilder());
        when(mermaidDiagramService.generateClassDiagrams(any(), anyString()))
                .thenReturn(CompletableFuture.completedFuture(
                        List.of("mermaid.mmd")));
        when(plantUMLDiagramService.generateClassDiagrams(any(), anyString()))
                .thenReturn(CompletableFuture.completedFuture(
                        List.of("plantuml.puml")));

        // When: Generate with both types enabled
        String result = handler.handleAnalyzeProjectExtended(
                projectPath.toString(), "output",
                true, "mermaid-output", true, "plantuml-output");

        // Then: Both diagram types are generated
        assertThat(result).contains(
                "✅ Analysis complete!");
        assertThat(result).contains(
                "🧩 Mermaid diagrams: 1 diagrams generated");
        assertThat(result).contains(
                "🌱 PlantUML diagrams: 1 diagrams generated");
    }

    @Test
    void handleAnalyzeProjectExtendedWithException() throws Exception {
        // Given: Project path that causes analysis exception
        Path projectPath = Files.createDirectory(tempDir
                .resolve("test-project"));
        when(commonHandler.directoryExists(projectPath.toString()))
                .thenReturn(true);
        when(codeAnalysisService.analyzeProject(any(Path.class)))
                .thenThrow(new RuntimeException("Analysis failed"));
        when(commonHandler.formatErrorMessage(anyString(),
                any(Exception.class)))
                .thenReturn("❌ Error: Analysis failed");

        // When: Analysis throws exception
        String result = handler.handleAnalyzeProjectExtended(
                projectPath.toString(), "output", false, "", false, "");

        // Then: Exception is handled gracefully
        assertThat(result).contains("❌ Error: Analysis failed");
    }

    @Test
    void handleScanProjectWithInvalidPath() {
        // Given: Invalid project path
        String invalidPath = "/nonexistent/path";
        when(commonHandler.directoryExists(invalidPath)).thenReturn(false);

        // When
        String result = handler.handleScanProject(invalidPath);

        // Then: Returns error message
        assertThat(result).contains("❌ Error: Project path does not exist");
    }

    @Test
    void handleScanProjectWithException() throws Exception {
        // Given: Project path that causes scan exception
        Path projectPath = Files.createDirectory(tempDir.resolve(
                "test-project"));
        when(commonHandler.directoryExists(projectPath.toString()))
                .thenReturn(true);
        when(codeAnalysisService.analyzeProject(any(Path.class)))
                .thenThrow(new RuntimeException("Scan failed"));
        when(commonHandler.formatErrorMessage(anyString(),
                any(Exception.class)))
                .thenReturn("❌ Error: Scan failed");

        // When: Scan throws exception
        String result = handler.handleScanProject(projectPath.toString());

        // Then: Exception is handled gracefully
        assertThat(result).contains("❌ Error: Scan failed");
    }

    @Test
    void handleAnalyzeProjectExtendedWithoutDiagrams() throws Exception {
        // Given: Valid project path with no diagram generation
        Path projectPath = Files.createDirectory(tempDir.resolve(
                "test-project"));
        when(commonHandler.directoryExists(projectPath.toString()))
                .thenReturn(true);
        when(codeAnalysisService.analyzeProject(any(Path.class)))
                .thenReturn(CompletableFuture
                .completedFuture(projectAnalysis));
        when(documentationService.generateDocumentation(projectAnalysis))
                .thenReturn(CompletableFuture.completedFuture("docs/output"));
        when(commonHandler.createResultBuilder()).thenReturn(
                new StringBuilder());

        // When: Generate without any diagrams
        String result = handler.handleAnalyzeProjectExtended(
                projectPath.toString(), "output", false, "", false, "");

        // Then: Only documentation is generated
        assertThat(result).contains("✅ Analysis complete!");
        assertThat(result).doesNotContain("Mermaid diagrams");
        assertThat(result).doesNotContain("PlantUML diagrams");
        verify(mermaidDiagramService, never()).generateClassDiagrams(
                any(), any());
        verify(plantUMLDiagramService, never()).generateClassDiagrams(
                any(), any());
    }
}
