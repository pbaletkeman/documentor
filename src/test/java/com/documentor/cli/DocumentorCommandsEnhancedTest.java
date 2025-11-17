package com.documentor.cli;

import com.documentor.cli.handlers.ConfigurationCommandHandler;
import com.documentor.cli.handlers.EnhancedProjectAnalysisHandler;
import com.documentor.cli.handlers.ProjectAnalysisCommandHandler;
import com.documentor.cli.handlers.ProjectAnalysisRequest;
import com.documentor.cli.handlers.StatusCommandHandler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for DocumentorCommands class
 */
@ExtendWith(MockitoExtension.class)
class DocumentorCommandsEnhancedTest {

    @Mock
    private ProjectAnalysisCommandHandler projectAnalysisHandler;

    @Mock
    private StatusCommandHandler statusHandler;

    @Mock
    private ConfigurationCommandHandler configurationHandler;

    @Mock
    private EnhancedProjectAnalysisHandler enhancedAnalysisHandler;

    @InjectMocks
    private DocumentorCommands commands;

    @Test
    @DisplayName("Should handle analyze project command")
    void shouldHandleAnalyzeProject() {
        // Given
        String projectPath = "/test/project";
        String configPath = "custom-config.json";
        boolean generateMermaid = true;
        String mermaidOutput = "/test/diagrams";

        when(projectAnalysisHandler.handleAnalyzeProjectExtended(projectPath,
            configPath, generateMermaid, mermaidOutput, false, "", true))
            .thenReturn("Analysis complete");        // When
        String result = commands.analyzeProject(projectPath, configPath, true,
                generateMermaid, mermaidOutput, false, "");

        // Then
        assertEquals("Analysis complete", result);
        verify(projectAnalysisHandler).handleAnalyzeProjectExtended(
            projectPath, configPath, generateMermaid, mermaidOutput,
            false, "", true);
    }

    @Test
    @DisplayName("Should handle scan project command")
    void shouldHandleScanProject() {
        // Given
        String projectPath = "/test/project";

        when(projectAnalysisHandler.handleScanProject(projectPath, true))
            .thenReturn("Scan complete");

        // When
        String result = commands.scanProject(projectPath, true);

        // Then
        assertEquals("Scan complete", result);
        verify(projectAnalysisHandler).handleScanProject(projectPath, true);
    }

    @Test
    @DisplayName("Should handle validate config command")
    void shouldHandleValidateConfig() {
        // Given
        String configPath = "config.json";

        when(configurationHandler.handleValidateConfig(configPath))
            .thenReturn("Config valid");

        // When
        String result = commands.validateConfig(configPath);

        // Then
        assertEquals("Config valid", result);
        verify(configurationHandler).handleValidateConfig(configPath);
    }

    @Test
    @DisplayName("Should handle show info command")
    void shouldHandleShowInfo() {
        // Given
        when(statusHandler.handleShowInfo())
            .thenReturn("Info displayed");

        // When
        String result = commands.showInfo();

        // Then
        assertEquals("Info displayed", result);
        verify(statusHandler).handleShowInfo();
    }

    @Test
    @DisplayName("Should handle quick start command")
    void shouldHandleQuickStart() {
        // Given
        when(statusHandler.handleQuickStart())
            .thenReturn("Quick start guide");

        // When
        String result = commands.quickStart();

        // Then
        assertEquals("Quick start guide", result);
        verify(statusHandler).handleQuickStart();
    }

    @Test
    @DisplayName("Should handle show status command")
    void shouldHandleShowStatus() {
        // Given
        // First set the current project and config path
        commands.analyzeProject("/test/project", "custom-config.json",
            true, false, "", false, "");

        when(statusHandler.handleShowStatus(
            "/test/project", "custom-config.json"))
            .thenReturn("Status displayed");

        // When
        String result = commands.showStatus();

        // Then
        assertEquals("Status displayed", result);
        verify(statusHandler).handleShowStatus("/test/project",
            "custom-config.json");
    }

    @Test
    @DisplayName("Should handle analyze with fix command")
    void shouldHandleAnalyzeWithFix() {
        // Given
        String projectPath = "/test/project";
        String configPath = "config.json";
        boolean includePrivateMembers = true;
        boolean generateMermaid = false;
        String mermaidOutput = "";
        boolean generatePlantUML = true;
        String plantUMLOutput = "/test/plantuml";
        boolean useFix = true;
        String outputDir = "/test/output";

        when(enhancedAnalysisHandler.analyzeProjectWithFix(any(
                ProjectAnalysisRequest.class)))
            .thenReturn("Analysis with fix complete");

        // When
        String result = commands.analyzeWithFix(projectPath, configPath,
            includePrivateMembers, generateMermaid, mermaidOutput,
            generatePlantUML, plantUMLOutput);

        // Then
        assertEquals("Analysis with fix complete", result);
        verify(enhancedAnalysisHandler).analyzeProjectWithFix(any(
            ProjectAnalysisRequest.class));
    }

    @Test
    @DisplayName("Should handle generate PlantUML diagrams command")
    void shouldHandleGeneratePlantUMLDiagrams() {
        // Given
        String projectPath = "/test/project";
        boolean includePrivateMembers = true;
        String plantUMLOutput = "/test/plantuml";

        when(projectAnalysisHandler.handleAnalyzeProjectExtended(
            projectPath, "config.json",
                false, "", true, plantUMLOutput, includePrivateMembers))
            .thenReturn("PlantUML diagrams generated");

        // When
        String result = commands.generatePlantUMLDiagrams(projectPath,
            includePrivateMembers, plantUMLOutput);

        // Then
        assertEquals("PlantUML diagrams generated", result);
        verify(projectAnalysisHandler).handleAnalyzeProjectExtended(
            projectPath, "config.json",
                false, "", true, plantUMLOutput, includePrivateMembers);
    }

    @Test
    @DisplayName("Should handle analyze with fix command "
        + " with default parameters")
    void shouldHandleAnalyzeWithFixDefaultParams() {
        // Given
        String projectPath = "/test/project";
        String configPath = "config.json";

        when(enhancedAnalysisHandler.analyzeProjectWithFix(any(
                ProjectAnalysisRequest.class)))
            .thenReturn("Analysis with fix complete (defaults)");

        // When
        String result = commands.analyzeWithFix(projectPath, configPath, true,
                false, "", false, "");

        // Then
        assertEquals("Analysis with fix complete (defaults)", result);
        verify(enhancedAnalysisHandler).analyzeProjectWithFix(any(
            ProjectAnalysisRequest.class));
    }
}
