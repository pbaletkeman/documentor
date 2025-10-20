package com.documentor.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.documentor.config.DocumentorConfig;
import com.documentor.config.model.OutputSettings;
import com.documentor.model.ProjectAnalysis;
import com.documentor.service.documentation.ElementDocumentationGeneratorEnhanced;
import com.documentor.service.documentation.MainDocumentationGenerator;
import com.documentor.service.documentation.UnitTestDocumentationGeneratorEnhanced;

/**
 * Branch coverage tests for DocumentationServiceEnhanced.
 */
@ExtendWith(MockitoExtension.class)
class DocumentationServiceEnhancedBranchCoverageTest {

    @Mock
    private MainDocumentationGenerator mockMainDocGenerator;

    @Mock
    private ElementDocumentationGeneratorEnhanced mockElementDocGenerator;

    @Mock
    private UnitTestDocumentationGeneratorEnhanced mockTestDocGenerator;

    @Mock
    private MermaidDiagramService mockMermaidService;

    @Mock
    private PlantUMLDiagramService mockPlantUMLService;

    @Mock
    private DocumentorConfig mockConfig;

    @Mock
    private LlmServiceFixEnhanced mockLlmServiceFix;

    @Mock
    private ProjectAnalysis mockAnalysis;

    @Mock
    private OutputSettings mockOutputSettings;

    @TempDir
    Path tempDir;

    private DocumentationServiceEnhanced service;

    @BeforeEach
    void setUp() {
        service = new DocumentationServiceEnhanced(
            mockMainDocGenerator,
            mockElementDocGenerator,
            mockTestDocGenerator,
            mockMermaidService,
            mockPlantUMLService,
            mockConfig,
            mockLlmServiceFix
        );

        // Default setup with lenient stubbing to avoid unnecessary stubbing exceptions
        lenient().when(mockConfig.outputSettings()).thenReturn(mockOutputSettings);
        lenient().when(mockOutputSettings.outputPath()).thenReturn(tempDir.toString());
        lenient().when(mockOutputSettings.generateUnitTests()).thenReturn(true);
        lenient().when(mockOutputSettings.generateMermaidDiagrams()).thenReturn(true);
        lenient().when(mockOutputSettings.generatePlantUMLDiagrams()).thenReturn(true);
        lenient().when(mockOutputSettings.mermaidOutputPath()).thenReturn(tempDir.resolve("mermaid").toString());
        lenient().when(mockOutputSettings.plantUMLOutputPath()).thenReturn(tempDir.resolve("plantuml").toString());
        lenient().when(mockAnalysis.projectPath()).thenReturn("test-project");
    }

    /**
     * Test main documentation timeout exception handler
     */
    @Test
    void testMainDocumentationGeneratorTimeout() throws Exception {
        // Arrange
        CompletableFuture<String> timeoutFuture = new CompletableFuture<>();
        timeoutFuture.completeExceptionally(new TimeoutException("Operation timed out"));

        when(mockMainDocGenerator.generateMainDocumentation(mockAnalysis))
            .thenReturn(timeoutFuture);
        when(mockElementDocGenerator.generateGroupedDocumentation(any(), any()))
            .thenReturn(CompletableFuture.completedFuture(null));
        when(mockTestDocGenerator.generateUnitTestDocumentation(any(), any()))
            .thenReturn(CompletableFuture.completedFuture(null));
        when(mockMermaidService.generateClassDiagrams(any(), any()))
            .thenReturn(CompletableFuture.completedFuture(List.of()));
        when(mockPlantUMLService.generateClassDiagrams(any(), any()))
            .thenReturn(CompletableFuture.completedFuture(List.of()));

        // Act
        CompletableFuture<String> result = service.generateDocumentation(mockAnalysis);

        // Assert
        assertNotNull(result);
        String outputPath = result.join();
        assertEquals(tempDir.toString(), outputPath);

        // Verify error document was created
        Path readmePath = tempDir.resolve("README.md");
        assertTrue(Files.exists(readmePath));
        String content = Files.readString(readmePath);
        assertTrue(content.contains("Error Generating Documentation"));
    }

    /**
     * Test element documentation timeout exception handler
     */
    @Test
    void testElementDocumentationTimeout() throws Exception {
        // Arrange
        when(mockMainDocGenerator.generateMainDocumentation(mockAnalysis))
            .thenReturn(CompletableFuture.completedFuture("# Main Documentation"));

        CompletableFuture<Void> timeoutFuture = new CompletableFuture<>();
        timeoutFuture.completeExceptionally(new TimeoutException("Element timeout"));
        when(mockElementDocGenerator.generateGroupedDocumentation(any(), any()))
            .thenReturn(timeoutFuture);

        when(mockTestDocGenerator.generateUnitTestDocumentation(any(), any()))
            .thenReturn(CompletableFuture.completedFuture(null));
        when(mockMermaidService.generateClassDiagrams(any(), any()))
            .thenReturn(CompletableFuture.completedFuture(List.of()));
        when(mockPlantUMLService.generateClassDiagrams(any(), any()))
            .thenReturn(CompletableFuture.completedFuture(List.of()));

        // Act
        CompletableFuture<String> result = service.generateDocumentation(mockAnalysis);

        // Assert
        assertNotNull(result);
        String outputPath = result.join();
        assertEquals(tempDir.toString(), outputPath);
    }

    /**
     * Test unit test generation disabled
     */
    @Test
    void testUnitTestsDisabled() throws Exception {
        // Arrange
        when(mockOutputSettings.generateUnitTests()).thenReturn(false);

        when(mockMainDocGenerator.generateMainDocumentation(mockAnalysis))
            .thenReturn(CompletableFuture.completedFuture("# Main Documentation"));
        when(mockElementDocGenerator.generateGroupedDocumentation(any(), any()))
            .thenReturn(CompletableFuture.completedFuture(null));
        when(mockMermaidService.generateClassDiagrams(any(), any()))
            .thenReturn(CompletableFuture.completedFuture(List.of()));
        when(mockPlantUMLService.generateClassDiagrams(any(), any()))
            .thenReturn(CompletableFuture.completedFuture(List.of()));

        // Act
        CompletableFuture<String> result = service.generateDocumentation(mockAnalysis);

        // Assert
        assertNotNull(result);
        String outputPath = result.join();
        assertEquals(tempDir.toString(), outputPath);

        // Verify unit test generator was never called
        verify(mockTestDocGenerator, never()).generateUnitTestDocumentation(any(), any());
    }

    /**
     * Test PlantUML diagram generation exception
     */
    @Test
    void testPlantUMLException() throws Exception {
        // Arrange
        when(mockMainDocGenerator.generateMainDocumentation(mockAnalysis))
            .thenReturn(CompletableFuture.completedFuture("# Main Documentation"));
        when(mockElementDocGenerator.generateGroupedDocumentation(any(), any()))
            .thenReturn(CompletableFuture.completedFuture(null));
        when(mockTestDocGenerator.generateUnitTestDocumentation(any(), any()))
            .thenReturn(CompletableFuture.completedFuture(null));
        when(mockMermaidService.generateClassDiagrams(any(), any()))
            .thenReturn(CompletableFuture.completedFuture(List.of()));

        CompletableFuture<List<String>> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new RuntimeException("PlantUML failed"));
        when(mockPlantUMLService.generateClassDiagrams(any(), any()))
            .thenReturn(failedFuture);

        // Act
        CompletableFuture<String> result = service.generateDocumentation(mockAnalysis);

        // Assert
        assertNotNull(result);
        String outputPath = result.join();
        assertEquals(tempDir.toString(), outputPath);
    }
}
