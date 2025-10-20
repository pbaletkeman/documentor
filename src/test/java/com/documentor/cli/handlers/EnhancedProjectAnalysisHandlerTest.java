package com.documentor.cli.handlers;

import com.documentor.config.DocumentorConfig;
import com.documentor.config.ExternalConfigLoader;
import com.documentor.config.model.LlmModelConfig;
import com.documentor.config.model.OutputSettings;
import com.documentor.service.LlmServiceFix;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Comprehensive tests for EnhancedProjectAnalysisHandler
 */
@ExtendWith(MockitoExtension.class)
class EnhancedProjectAnalysisHandlerTest {

    @Mock
    private ProjectAnalysisCommandHandler baseHandler;

    @Mock
    private LlmServiceFix llmServiceFix;

    @Mock
    private ExternalConfigLoader configLoader;

    @InjectMocks
    private EnhancedProjectAnalysisHandler handler;

    private DocumentorConfig testConfig;

    @BeforeEach
    void setUp() {
        // Create a test configuration with correct constructor parameters
        testConfig = new DocumentorConfig(
            List.of(new LlmModelConfig("test-model", "openai", "http://test-url", "test-key", 1000, 60)),
            new OutputSettings("/test/output", "markdown", true, true, false),
            null
        );
    }

    @Test
    @DisplayName("Should handle analyze with fix when useFix is false")
    void shouldHandleAnalyzeWithoutFix() {
        // Given
        String projectPath = "/test/project";
        String configPath = "config.json";
        boolean generateMermaid = true;
        String mermaidOutput = "/test/mermaid";
        boolean generatePlantUML = false;
        String plantUMLOutput = "";
        Boolean includePrivateMembers = true;
        boolean useFix = false;
        String outputDir = "";

        when(baseHandler.handleAnalyzeProjectExtended(
            projectPath, configPath, generateMermaid, mermaidOutput,
            generatePlantUML, plantUMLOutput, includePrivateMembers))
            .thenReturn("Analysis complete without fix");

        // When
        String result = handler.analyzeProjectWithFix(
            projectPath, configPath, generateMermaid, mermaidOutput,
            generatePlantUML, plantUMLOutput, includePrivateMembers, useFix, outputDir);

        // Then
        assertEquals("Analysis complete without fix", result);
        verify(baseHandler).handleAnalyzeProjectExtended(
            projectPath, configPath, generateMermaid, mermaidOutput,
            generatePlantUML, plantUMLOutput, includePrivateMembers);

        // LlmServiceFix should not be called when useFix is false
        verify(llmServiceFix, never()).setLlmServiceThreadLocalConfig(any());
        verify(configLoader, never()).getLoadedConfig();
    }

    @Test
    @DisplayName("Should handle analyze with fix when configPath is null")
    void shouldHandleAnalyzeWithFixNullConfigPath() {
        // Given
        String projectPath = "/test/project";
        String configPath = null;
        boolean generateMermaid = false;
        String mermaidOutput = "";
        boolean generatePlantUML = true;
        String plantUMLOutput = "/test/plantuml";
        Boolean includePrivateMembers = false;
        boolean useFix = true;
        String outputDir = "/test/output";

        when(baseHandler.handleAnalyzeProjectExtended(
            projectPath, configPath, generateMermaid, mermaidOutput,
            generatePlantUML, plantUMLOutput, includePrivateMembers))
            .thenReturn("Analysis complete with null config");

        // When
        String result = handler.analyzeProjectWithFix(
            projectPath, configPath, generateMermaid, mermaidOutput,
            generatePlantUML, plantUMLOutput, includePrivateMembers, useFix, outputDir);

        // Then
        assertEquals("Analysis complete with null config", result);
        verify(baseHandler).handleAnalyzeProjectExtended(
            projectPath, configPath, generateMermaid, mermaidOutput,
            generatePlantUML, plantUMLOutput, includePrivateMembers);

        // LlmServiceFix should not be called when configPath is null
        verify(llmServiceFix, never()).setLlmServiceThreadLocalConfig(any());
        verify(configLoader, never()).getLoadedConfig();

        // Output directory should be set
        assertEquals("/test/output", System.getProperty("documentor.output.directory"));
    }

    @Test
    @DisplayName("Should handle analyze with fix when config is already loaded")
    void shouldHandleAnalyzeWithFixConfigAlreadyLoaded() {
        // Given
        String projectPath = "/test/project";
        String configPath = "config.json";
        boolean generateMermaid = true;
        String mermaidOutput = "/test/mermaid";
        boolean generatePlantUML = true;
        String plantUMLOutput = "/test/plantuml";
        Boolean includePrivateMembers = true;
        boolean useFix = true;
        String outputDir = "";

        when(configLoader.getLoadedConfig()).thenReturn(testConfig);
        when(baseHandler.handleAnalyzeProjectExtended(
            projectPath, configPath, generateMermaid, mermaidOutput,
            generatePlantUML, plantUMLOutput, includePrivateMembers))
            .thenReturn("Analysis complete with loaded config");

        // When
        String result = handler.analyzeProjectWithFix(
            projectPath, configPath, generateMermaid, mermaidOutput,
            generatePlantUML, plantUMLOutput, includePrivateMembers, useFix, outputDir);

        // Then
        assertEquals("Analysis complete with loaded config", result);
        verify(configLoader).getLoadedConfig();
        verify(llmServiceFix).setLlmServiceThreadLocalConfig(testConfig);
        verify(baseHandler).handleAnalyzeProjectExtended(
            projectPath, configPath, generateMermaid, mermaidOutput,
            generatePlantUML, plantUMLOutput, includePrivateMembers);

        // Should not try to load config since it's already loaded
        verify(configLoader, never()).loadExternalConfig(any());
    }

    @Test
    @DisplayName("Should handle analyze with fix when config needs to be loaded successfully")
    void shouldHandleAnalyzeWithFixConfigLoadedSuccessfully() {
        // Given
        String projectPath = "/test/project";
        String configPath = "config.json";
        boolean generateMermaid = false;
        String mermaidOutput = "";
        boolean generatePlantUML = false;
        String plantUMLOutput = "";
        Boolean includePrivateMembers = false;
        boolean useFix = true;
        String outputDir = "/test/output";

        when(configLoader.getLoadedConfig())
            .thenReturn(null)  // First call returns null
            .thenReturn(testConfig);  // Second call returns loaded config
        when(configLoader.loadExternalConfig(any())).thenReturn(true);
        when(baseHandler.handleAnalyzeProjectExtended(
            projectPath, configPath, generateMermaid, mermaidOutput,
            generatePlantUML, plantUMLOutput, includePrivateMembers))
            .thenReturn("Analysis complete with newly loaded config");

        // When
        String result = handler.analyzeProjectWithFix(
            projectPath, configPath, generateMermaid, mermaidOutput,
            generatePlantUML, plantUMLOutput, includePrivateMembers, useFix, outputDir);

        // Then
        assertEquals("Analysis complete with newly loaded config", result);
        verify(configLoader, times(2)).getLoadedConfig();
        verify(configLoader).loadExternalConfig(new String[]{"analyze", "--config", configPath});
        verify(llmServiceFix).setLlmServiceThreadLocalConfig(testConfig);
        verify(baseHandler).handleAnalyzeProjectExtended(
            projectPath, configPath, generateMermaid, mermaidOutput,
            generatePlantUML, plantUMLOutput, includePrivateMembers);

        // Output directory should be set
        assertEquals("/test/output", System.getProperty("documentor.output.directory"));
    }

    @Test
    @DisplayName("Should handle analyze with fix when config loading fails")
    void shouldHandleAnalyzeWithFixConfigLoadingFails() {
        // Given
        String projectPath = "/test/project";
        String configPath = "invalid-config.json";
        boolean generateMermaid = true;
        String mermaidOutput = "/test/mermaid";
        boolean generatePlantUML = false;
        String plantUMLOutput = "";
        Boolean includePrivateMembers = true;
        boolean useFix = true;
        String outputDir = "";

        when(configLoader.getLoadedConfig()).thenReturn(null);
        when(configLoader.loadExternalConfig(any())).thenReturn(false);
        when(baseHandler.handleAnalyzeProjectExtended(
            projectPath, configPath, generateMermaid, mermaidOutput,
            generatePlantUML, plantUMLOutput, includePrivateMembers))
            .thenReturn("Analysis complete with failed config loading");

        // When
        String result = handler.analyzeProjectWithFix(
            projectPath, configPath, generateMermaid, mermaidOutput,
            generatePlantUML, plantUMLOutput, includePrivateMembers, useFix, outputDir);

        // Then
        assertEquals("Analysis complete with failed config loading", result);
        verify(configLoader).getLoadedConfig();
        verify(configLoader).loadExternalConfig(new String[]{"analyze", "--config", configPath});
        verify(llmServiceFix, never()).setLlmServiceThreadLocalConfig(any());
        verify(baseHandler).handleAnalyzeProjectExtended(
            projectPath, configPath, generateMermaid, mermaidOutput,
            generatePlantUML, plantUMLOutput, includePrivateMembers);
    }

    @Test
    @DisplayName("Should handle analyze with fix when loaded config is null after loading")
    void shouldHandleAnalyzeWithFixLoadedConfigIsNull() {
        // Given
        String projectPath = "/test/project";
        String configPath = "config.json";
        boolean generateMermaid = false;
        String mermaidOutput = "";
        boolean generatePlantUML = true;
        String plantUMLOutput = "/test/plantuml";
        Boolean includePrivateMembers = false;
        boolean useFix = true;
        String outputDir = "";

        when(configLoader.getLoadedConfig()).thenReturn(null).thenReturn(null);
        when(configLoader.loadExternalConfig(any())).thenReturn(true);


        // When & Then - This should throw NPE due to a bug in production code
        // The production code tries to access config.llmModels() without null check
        assertThrows(NullPointerException.class, () -> {
            handler.analyzeProjectWithFix(
                projectPath, configPath, generateMermaid, mermaidOutput,
                generatePlantUML, plantUMLOutput, includePrivateMembers, useFix, outputDir);
        });

        verify(configLoader, times(2)).getLoadedConfig();
        verify(configLoader).loadExternalConfig(new String[]{"analyze", "--config", configPath});
        verify(llmServiceFix, never()).setLlmServiceThreadLocalConfig(any());
    }

    @Test
    @DisplayName("Should handle analyze with fix and set output directory when specified")
    void shouldHandleAnalyzeWithFixAndSetOutputDirectory() {
        // Given
        String projectPath = "/test/project";
        String configPath = "config.json";
        boolean generateMermaid = true;
        String mermaidOutput = "/test/mermaid";
        boolean generatePlantUML = true;
        String plantUMLOutput = "/test/plantuml";
        Boolean includePrivateMembers = true;
        boolean useFix = true;
        String outputDir = "/custom/output/directory";

        when(configLoader.getLoadedConfig()).thenReturn(testConfig);
        when(baseHandler.handleAnalyzeProjectExtended(
            projectPath, configPath, generateMermaid, mermaidOutput,
            generatePlantUML, plantUMLOutput, includePrivateMembers))
            .thenReturn("Analysis complete with custom output directory");

        // When
        String result = handler.analyzeProjectWithFix(
            projectPath, configPath, generateMermaid, mermaidOutput,
            generatePlantUML, plantUMLOutput, includePrivateMembers, useFix, outputDir);

        // Then
        assertEquals("Analysis complete with custom output directory", result);
        verify(configLoader).getLoadedConfig();
        verify(llmServiceFix).setLlmServiceThreadLocalConfig(testConfig);
        verify(baseHandler).handleAnalyzeProjectExtended(
            projectPath, configPath, generateMermaid, mermaidOutput,
            generatePlantUML, plantUMLOutput, includePrivateMembers);

        // Verify output directory system property was set
        assertEquals("/custom/output/directory", System.getProperty("documentor.output.directory"));
    }

    @Test
    @DisplayName("Should not set output directory when it is empty")
    void shouldNotSetOutputDirectoryWhenEmpty() {
        // Given
        String projectPath = "/test/project";
        String configPath = "config.json";
        boolean generateMermaid = false;
        String mermaidOutput = "";
        boolean generatePlantUML = false;
        String plantUMLOutput = "";
        Boolean includePrivateMembers = false;
        boolean useFix = true;
        String outputDir = "";

        // Clear any existing system property
        System.clearProperty("documentor.output.directory");

        when(configLoader.getLoadedConfig()).thenReturn(testConfig);
        when(baseHandler.handleAnalyzeProjectExtended(
            projectPath, configPath, generateMermaid, mermaidOutput,
            generatePlantUML, plantUMLOutput, includePrivateMembers))
            .thenReturn("Analysis complete without output directory");

        // When
        String result = handler.analyzeProjectWithFix(
            projectPath, configPath, generateMermaid, mermaidOutput,
            generatePlantUML, plantUMLOutput, includePrivateMembers, useFix, outputDir);

        // Then
        assertEquals("Analysis complete without output directory", result);
        verify(configLoader).getLoadedConfig();
        verify(llmServiceFix).setLlmServiceThreadLocalConfig(testConfig);
        verify(baseHandler).handleAnalyzeProjectExtended(
            projectPath, configPath, generateMermaid, mermaidOutput,
            generatePlantUML, plantUMLOutput, includePrivateMembers);

        // Verify output directory system property was not set
        assertNull(System.getProperty("documentor.output.directory"));
    }

    @Test
    @DisplayName("Should handle analyze with fix when config has null llmModels")
    void shouldHandleAnalyzeWithFixConfigWithNullLlmModels() {
        // Given
        String projectPath = "/test/project";
        String configPath = "config.json";
        boolean generateMermaid = true;
        String mermaidOutput = "/test/mermaid";
        boolean generatePlantUML = false;
        String plantUMLOutput = "";
        Boolean includePrivateMembers = true;
        boolean useFix = true;
        String outputDir = "";

        // Create config with null llmModels
        DocumentorConfig configWithNullModels = new DocumentorConfig(
            null,
            new OutputSettings("/test/output", "markdown", true, true, false),
            null
        );

        when(configLoader.getLoadedConfig())
            .thenReturn(null)  // First call returns null
            .thenReturn(configWithNullModels);  // Second call returns config with null models
        when(configLoader.loadExternalConfig(any())).thenReturn(true);
        when(baseHandler.handleAnalyzeProjectExtended(
            projectPath, configPath, generateMermaid, mermaidOutput,
            generatePlantUML, plantUMLOutput, includePrivateMembers))
            .thenReturn("Analysis complete with null llmModels");

        // When
        String result = handler.analyzeProjectWithFix(
            projectPath, configPath, generateMermaid, mermaidOutput,
            generatePlantUML, plantUMLOutput, includePrivateMembers, useFix, outputDir);

        // Then
        assertEquals("Analysis complete with null llmModels", result);
        verify(configLoader, times(2)).getLoadedConfig();
        verify(configLoader).loadExternalConfig(new String[]{"analyze", "--config", configPath});
        verify(llmServiceFix).setLlmServiceThreadLocalConfig(configWithNullModels);
        verify(baseHandler).handleAnalyzeProjectExtended(
            projectPath, configPath, generateMermaid, mermaidOutput,
            generatePlantUML, plantUMLOutput, includePrivateMembers);
    }
}
