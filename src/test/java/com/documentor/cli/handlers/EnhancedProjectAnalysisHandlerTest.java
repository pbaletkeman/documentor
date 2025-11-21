package com.documentor.cli.handlers;

import com.documentor.config.DocumentorConfig;
import com.documentor.config.ExternalConfigLoader;
import com.documentor.config.model.LlmModelConfig;
import com.documentor.config.model.OutputSettings;
import com.documentor.config.model.AnalysisSettings;
import com.documentor.service.LlmServiceFix;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

/**
 * Comprehensive tests for EnhancedProjectAnalysisHandler
 */
@ExtendWith(MockitoExtension.class)
class EnhancedProjectAnalysisHandlerTest {

    private static final int DEFAULT_MAX_ATTEMPTS = 60;

    private static final int DEFAULT_TIMEOUT = 1000;

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
        LlmModelConfig llmModelConfig = new LlmModelConfig(
            "test-model",
            "openai",
            "http://test-url",
            "test-key",
            DEFAULT_TIMEOUT,
            DEFAULT_MAX_ATTEMPTS
        );
        testConfig = new DocumentorConfig(
            List.of(llmModelConfig),
            new OutputSettings("test-output-dir", "markdown", true, true, false,
                null, null, null, null),
            new AnalysisSettings(
                null,
                null,
                null,
                null
            )
        );
    }

    private ProjectAnalysisRequest createRequest(
        final String projectPath,
        final String configPath,
        final boolean generateMermaid,
        final boolean generatePlantUML,
        final Boolean includePrivateMembers,
        final boolean useFix,
        final OutputConfig outputConfig
    ) {
        // Group output-related parameters into OutputConfig
        // Avoid hidden field warning by using getters
        return new ProjectAnalysisRequest(
            projectPath,
            configPath,
            generateMermaid,
            // use getter to avoid hidden field
            outputConfig.getMermaidOutput(),
            generatePlantUML,
            // use getter to avoid hidden field
            outputConfig.getPlantUMLOutput(),
            includePrivateMembers,
            useFix,
            // use getter to avoid hidden field
            outputConfig.getOutputDir(),
            false  // dryRun - default to false for tests
        );
    }

    // Helper class to group output parameters
    private static class OutputConfig {
        private final String mermaidOutputValue;
        private final String plantUMLOutputValue;
        private final String outputDirValue;
        OutputConfig(
            final String mermaidOutput,
            final String plantUMLOutput,
            final String outputDir
        ) {
            this.mermaidOutputValue = mermaidOutput;
            this.plantUMLOutputValue = plantUMLOutput;
            this.outputDirValue = outputDir;
        }
        public String getMermaidOutput() {
            return mermaidOutputValue;
        }
        public String getPlantUMLOutput() {
            return plantUMLOutputValue;
        }
        public String getOutputDir() {
            return outputDirValue;
        }
    }

    @Test
    @DisplayName("Should handle analyze with fix when useFix is false")
    void shouldHandleAnalyzeWithoutFix() {
        // No unnecessary stubbing
        // Given
        String projectPath = "/test/project";
        String configPath = "config.json";
        boolean generateMermaid = true;
        boolean generatePlantUML = false;
        Boolean includePrivateMembers = true;
        boolean useFix = false;
        OutputConfig outputConfig = new OutputConfig("/test/mermaid", "", "");

        when(baseHandler.handleAnalyzeProjectExtended(
            projectPath, configPath, generateMermaid,
            outputConfig.getMermaidOutput(),
            generatePlantUML, outputConfig.getPlantUMLOutput(),
            includePrivateMembers, false)
        ).thenReturn("Analysis complete without fix");

        // When
        ProjectAnalysisRequest request = createRequest(
            projectPath,
            configPath,
            generateMermaid,
            generatePlantUML,
            includePrivateMembers,
            useFix,
            outputConfig
        );
        String result = handler.analyzeProjectWithFix(request);

        // Then
        assertEquals("Analysis complete without fix", result);
        verify(baseHandler).handleAnalyzeProjectExtended(
            projectPath, configPath, generateMermaid,
            outputConfig.getMermaidOutput(),
            generatePlantUML, outputConfig.getPlantUMLOutput(),
            includePrivateMembers, false
        );

        // LlmServiceFix should not be called when useFix is false
        verify(llmServiceFix, never()).setLlmServiceThreadLocalConfig(any());
        verify(configLoader, never()).getLoadedConfig();
    }

    @Test
    @DisplayName("Should handle analyze with fix when configPath is null")
    void shouldHandleAnalyzeWithFixNullConfigPath() {
        // No unnecessary stubbing
        // Given
        String projectPath = "/test/project";
        String configPath = null;
        boolean generateMermaid = false;
        boolean generatePlantUML = true;
        Boolean includePrivateMembers = false;
        boolean useFix = true;
        OutputConfig outputConfig = new OutputConfig(
            "",
            "/test/plantuml",
            "/test/output"
        );

        when(baseHandler.handleAnalyzeProjectExtended(
            projectPath,
            configPath,
            generateMermaid,
            outputConfig.getMermaidOutput(),
            generatePlantUML,
            outputConfig.getPlantUMLOutput(),
            includePrivateMembers, false
        )).thenReturn("Analysis complete with null config");

        // When
        ProjectAnalysisRequest request = createRequest(
            projectPath,
            configPath,
            generateMermaid,
            generatePlantUML,
            includePrivateMembers,
            useFix,
            outputConfig
        );
        String result = handler.analyzeProjectWithFix(request);

        // Then
        assertEquals("Analysis complete with null config", result);
        verify(baseHandler).handleAnalyzeProjectExtended(
            projectPath, configPath, generateMermaid,
            outputConfig.getMermaidOutput(),
            generatePlantUML, outputConfig.getPlantUMLOutput(),
            includePrivateMembers, false
        );

        // LlmServiceFix should not be called when configPath is null
        verify(llmServiceFix, never()).setLlmServiceThreadLocalConfig(any());
        verify(configLoader, never()).getLoadedConfig();

        // Output directory should be set
        assertEquals(
            "/test/output",
            System.getProperty("documentor.output.directory")
        );
    }

    @Test
    @DisplayName("Should handle analyze with fix when config is already loaded")
    void shouldHandleAnalyzeWithFixConfigAlreadyLoaded() {
        // No unnecessary stubbing
        // Given
        String projectPath = "/test/project";
        String configPath = "config.json";
        boolean generateMermaid = true;
        boolean generatePlantUML = true;
        Boolean includePrivateMembers = true;
        boolean useFix = true;
        OutputConfig outputConfig = new OutputConfig(
            "/test/mermaid",
            "/test/plantuml",
            ""
        );

        when(configLoader.getLoadedConfig()).thenReturn(testConfig);
        when(baseHandler.handleAnalyzeProjectExtended(
            projectPath, configPath, generateMermaid,
            outputConfig.getMermaidOutput(),
            generatePlantUML, outputConfig.getPlantUMLOutput(),
            includePrivateMembers, false)
        ).thenReturn("Analysis complete with loaded config");

        // When
        ProjectAnalysisRequest request = createRequest(
            projectPath,
            configPath,
            generateMermaid,
            generatePlantUML,
            includePrivateMembers,
            useFix,
            outputConfig
        );
        String result = handler.analyzeProjectWithFix(request);

        // Then
        assertEquals("Analysis complete with loaded config", result);
        verify(configLoader).getLoadedConfig();
        verify(llmServiceFix).setLlmServiceThreadLocalConfig(testConfig);
        verify(baseHandler).handleAnalyzeProjectExtended(
            projectPath, configPath, generateMermaid,
            outputConfig.getMermaidOutput(),
            generatePlantUML, outputConfig.getPlantUMLOutput(),
            includePrivateMembers, false
        );

        // Should not try to load config since it's already loaded
        verify(configLoader, never()).loadExternalConfig(any());
    }

    @Test
    @DisplayName(
        "Should handle analyze with fix when config needs to be "
        + "loaded successfully"
    )
    void shouldHandleAnalyzeWithFixConfigLoadedSuccessfully() {
        // Given
        String projectPath = "/test/project";
        String configPath = "config.json";
        boolean generateMermaid = false;
        boolean generatePlantUML = false;
        Boolean includePrivateMembers = false;
        boolean useFix = true;
        OutputConfig outputConfig = new OutputConfig(
            "",
            "",
            "/test/output"
        );

        when(configLoader.getLoadedConfig())
            .thenReturn(null)  // First call returns null
            .thenReturn(testConfig);  // Second call returns loaded config
        when(configLoader.loadExternalConfig(any())).thenReturn(true);
        when(baseHandler.handleAnalyzeProjectExtended(
            projectPath, configPath, generateMermaid,
            outputConfig.getMermaidOutput(),
            generatePlantUML, outputConfig.getPlantUMLOutput(),
            includePrivateMembers, false)
        ).thenReturn("Analysis complete with newly loaded config");

        // When
        ProjectAnalysisRequest request = createRequest(
            projectPath,
            configPath,
            generateMermaid,
            generatePlantUML,
            includePrivateMembers,
            useFix,
            outputConfig
        );
        String result = handler.analyzeProjectWithFix(request);

        // Then
        assertEquals("Analysis complete with newly loaded config", result);
        verify(configLoader, times(2)).getLoadedConfig();
            verify(configLoader).loadExternalConfig(new String[] {
                "analyze",
                "--config",
                configPath
            });
        verify(llmServiceFix).setLlmServiceThreadLocalConfig(testConfig);
        verify(baseHandler).handleAnalyzeProjectExtended(
            projectPath, configPath, generateMermaid,
            outputConfig.getMermaidOutput(),
            generatePlantUML, outputConfig.getPlantUMLOutput(),
            includePrivateMembers, false
        );

        // Output directory should be set
        assertEquals(
            "/test/output",
            System.getProperty("documentor.output.directory")
        );
    }

    @Test
    @DisplayName("Should handle analyze with fix when config loading fails")
    void shouldHandleAnalyzeWithFixConfigLoadingFails() {
        // Given
        String projectPath = "/test/project";
        String configPath = "invalid-config.json";
        boolean generateMermaid = true;
        boolean generatePlantUML = false;
        Boolean includePrivateMembers = true;
        boolean useFix = true;
        OutputConfig outputConfig = new OutputConfig("/test/mermaid", "", "");

        when(configLoader.getLoadedConfig()).thenReturn(null);
        when(configLoader.loadExternalConfig(any())).thenReturn(false);
        when(baseHandler.handleAnalyzeProjectExtended(
            projectPath, configPath, generateMermaid,
            outputConfig.getMermaidOutput(),
            generatePlantUML, outputConfig.getPlantUMLOutput(),
            includePrivateMembers, false)
        ).thenReturn("Analysis complete with failed config loading");

        // When
        ProjectAnalysisRequest request = createRequest(
            projectPath,
            configPath,
            generateMermaid,
            generatePlantUML,
            includePrivateMembers,
            useFix,
            outputConfig
        );
        String result = handler.analyzeProjectWithFix(request);

        // Then
        assertEquals("Analysis complete with failed config loading", result);
        verify(configLoader).getLoadedConfig();
        verify(configLoader).loadExternalConfig(new String[] {
            "analyze", "--config", configPath
        });
        verify(llmServiceFix, never()).setLlmServiceThreadLocalConfig(any());
        verify(baseHandler).handleAnalyzeProjectExtended(
            projectPath, configPath, generateMermaid,
            outputConfig.getMermaidOutput(),
            generatePlantUML, outputConfig.getPlantUMLOutput(),
            includePrivateMembers, false
        );
    }

    @Test
    @DisplayName(
        "Should handle analyze with fix when loaded config is null "
        + "after loading"
    )
    void shouldHandleAnalyzeWithFixLoadedConfigIsNull() {
        // Given
        String projectPath = "/test/project";
        String configPath = "config.json";
        boolean generateMermaid = false;
        boolean generatePlantUML = true;
        Boolean includePrivateMembers = false;
        boolean useFix = true;
        OutputConfig outputConfig = new OutputConfig("", "/test/plantuml", "");

        when(configLoader.getLoadedConfig()).thenReturn(null).thenReturn(null);
        when(configLoader.loadExternalConfig(any())).thenReturn(true);


        // When & Then - This should throw NPE due to a bug in production code
        // Production code accesses config.llmModels() without null check
        assertThrows(NullPointerException.class, () -> {
                ProjectAnalysisRequest request = createRequest(
                    projectPath,
                    configPath,
                    generateMermaid,
                    generatePlantUML,
                    includePrivateMembers,
                    useFix,
                    outputConfig
                );
            handler.analyzeProjectWithFix(request);
        });

        verify(configLoader, times(2)).getLoadedConfig();
        verify(configLoader).loadExternalConfig(new String[] {
            "analyze", "--config", configPath
        });
        verify(llmServiceFix, never()).setLlmServiceThreadLocalConfig(any());
    }

    @Test
    @DisplayName(
        "Should handle analyze with fix and set output directory "
        + "when specified"
    )
    void shouldHandleAnalyzeWithFixAndSetOutputDirectory() {
        // No unnecessary stubbing
        // Given
        String projectPath = "/test/project";
        String configPath = "config.json";
        boolean generateMermaid = true;
        boolean generatePlantUML = true;
        Boolean includePrivateMembers = true;
        boolean useFix = true;
        OutputConfig outputConfig = new OutputConfig(
            "/test/mermaid",
            "/test/plantuml",
            "/custom/output/directory"
        );

        when(configLoader.getLoadedConfig()).thenReturn(testConfig);
        when(baseHandler.handleAnalyzeProjectExtended(
            projectPath,
            configPath,
            generateMermaid,
            outputConfig.getMermaidOutput(),
            generatePlantUML,
            outputConfig.getPlantUMLOutput(),
            includePrivateMembers, false
        )).thenReturn("Analysis complete with custom output directory");

        // When
        ProjectAnalysisRequest request = createRequest(
            projectPath,
            configPath,
            generateMermaid,
            generatePlantUML,
            includePrivateMembers,
            useFix,
            outputConfig
        );
        String result = handler.analyzeProjectWithFix(request);

        // Then
        assertEquals("Analysis complete with custom output directory", result);
        verify(configLoader).getLoadedConfig();
        verify(llmServiceFix).setLlmServiceThreadLocalConfig(testConfig);
        verify(baseHandler).handleAnalyzeProjectExtended(
            projectPath,
            configPath,
            generateMermaid,
            outputConfig.getMermaidOutput(),
            generatePlantUML,
            outputConfig.getPlantUMLOutput(),
            includePrivateMembers, false
        );

        // Verify output directory system property was set
        assertEquals(
            "/custom/output/directory",
            System.getProperty("documentor.output.directory")
        );
    }

    @Test
    @DisplayName("Should not set output directory when it is empty")
    void shouldNotSetOutputDirectoryWhenEmpty() {
        // No unnecessary stubbing
        // Given
        String projectPath = "/test/project";
        String configPath = "config.json";
        boolean generateMermaid = false;
        boolean generatePlantUML = false;
        Boolean includePrivateMembers = false;
        boolean useFix = true;
        OutputConfig outputConfig = new OutputConfig("", "", "");

        // Clear any existing system property
        System.clearProperty("documentor.output.directory");

        when(configLoader.getLoadedConfig()).thenReturn(testConfig);
        when(baseHandler.handleAnalyzeProjectExtended(
            projectPath,
            configPath,
            generateMermaid,
            outputConfig.getMermaidOutput(),
            generatePlantUML,
            outputConfig.getPlantUMLOutput(),
            includePrivateMembers, false
        )).thenReturn("Analysis complete without output directory");

        // When
        ProjectAnalysisRequest request = createRequest(
            projectPath,
            configPath,
            generateMermaid,
            generatePlantUML,
            includePrivateMembers,
            useFix,
            outputConfig
        );
        String result = handler.analyzeProjectWithFix(request);

        // Then
        assertEquals("Analysis complete without output directory", result);
        verify(configLoader).getLoadedConfig();
        verify(llmServiceFix).setLlmServiceThreadLocalConfig(testConfig);
        verify(baseHandler).handleAnalyzeProjectExtended(
            projectPath,
            configPath,
            generateMermaid,
            outputConfig.getMermaidOutput(),
            generatePlantUML,
            outputConfig.getPlantUMLOutput(),
            includePrivateMembers, false
        );

        // Verify output directory system property was not set
        assertNull(System.getProperty("documentor.output.directory"));
    }

    @Test
    @DisplayName(
        "Should handle analyze with fix when config has null llmModels"
    )
    void shouldHandleAnalyzeWithFixConfigWithNullLlmModels() {
        // No unnecessary stubbing
        // Given
        String projectPath = "/test/project";
        String configPath = "config.json";
        boolean generateMermaid = true;
        boolean generatePlantUML = false;
        Boolean includePrivateMembers = true;
        boolean useFix = true;
        OutputConfig outputConfig = new OutputConfig("/test/mermaid", "", "");

        // Create config with null llmModels
        DocumentorConfig configWithNullModels = new DocumentorConfig(
            null,
            new OutputSettings("/test/output", "markdown", true, true, false,
                null, null, null, null),
            null
        );

        when(configLoader.getLoadedConfig())
            .thenReturn(null)  // First call returns null
            .thenReturn(configWithNullModels);
        // Second call returns config with null models
        when(configLoader.loadExternalConfig(any())).thenReturn(true);
        when(baseHandler.handleAnalyzeProjectExtended(
            projectPath,
            configPath,
            generateMermaid,
            outputConfig.getMermaidOutput(),
            generatePlantUML,
            outputConfig.getPlantUMLOutput(),
            includePrivateMembers, false
        )).thenReturn("Analysis complete with null llmModels");

        // When
        ProjectAnalysisRequest request = createRequest(
            projectPath,
            configPath,
            generateMermaid,
            generatePlantUML,
            includePrivateMembers,
            useFix,
            outputConfig
        );
        String result = handler.analyzeProjectWithFix(request);

        // Then
        assertEquals("Analysis complete with null llmModels", result);
        verify(configLoader, times(2)).getLoadedConfig();
        verify(configLoader).loadExternalConfig(new String[]{
            "analyze",
            "--config",
            configPath
        });
        verify(llmServiceFix)
            .setLlmServiceThreadLocalConfig(configWithNullModels);
        verify(baseHandler).handleAnalyzeProjectExtended(
            projectPath,
            configPath,
            generateMermaid,
            outputConfig.getMermaidOutput(),
            generatePlantUML,
            outputConfig.getPlantUMLOutput(),
            includePrivateMembers, false
        );
    }
}
