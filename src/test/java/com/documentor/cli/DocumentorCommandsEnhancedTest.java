package com.documentor.cli;

import com.documentor.cli.handlers.ConfigurationCommandHandler;
import com.documentor.cli.handlers.ProjectAnalysisCommandHandler;
import com.documentor.cli.handlers.StatusCommandHandler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

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

        when(projectAnalysisHandler.handleAnalyzeProjectExtended(projectPath, configPath,
            generateMermaid, mermaidOutput, false, ""))
            .thenReturn("Analysis complete");        // When
        String result = commands.analyzeProject(projectPath, configPath, generateMermaid, mermaidOutput, false, "");

        // Then
        assertEquals("Analysis complete", result);
        verify(projectAnalysisHandler).handleAnalyzeProjectExtended(projectPath, configPath,
            generateMermaid, mermaidOutput, false, "");
    }

    @Test
    @DisplayName("Should handle scan project command")
    void shouldHandleScanProject() {
        // Given
        String projectPath = "/test/project";

        when(projectAnalysisHandler.handleScanProject(projectPath))
            .thenReturn("Scan complete");

        // When
        String result = commands.scanProject(projectPath);

        // Then
        assertEquals("Scan complete", result);
        verify(projectAnalysisHandler).handleScanProject(projectPath);
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
        commands.analyzeProject("/test/project", "custom-config.json", false, "", false, "");

        when(statusHandler.handleShowStatus("/test/project", "custom-config.json"))
            .thenReturn("Status displayed");

        // When
        String result = commands.showStatus();

        // Then
        assertEquals("Status displayed", result);
        verify(statusHandler).handleShowStatus("/test/project", "custom-config.json");
    }
}

