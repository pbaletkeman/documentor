package com.documentor.cli;

import com.documentor.config.DocumentorConfig;
import com.documentor.service.LlmServiceFix;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for DirectCommandProcessor to improve branch coverage.
 */
@ExtendWith(MockitoExtension.class)
class DirectCommandProcessorTest {

    @Mock
    private DocumentorCommands documentorCommands;

    @Mock
    private DocumentorConfig documentorConfig;

    @Mock
    private LlmServiceFix llmServiceFix;

    @Mock
    private ApplicationArguments applicationArguments;

    private DirectCommandProcessor processor;

    @BeforeEach
    void setUp() {
        processor = new DirectCommandProcessor(documentorCommands, documentorConfig, llmServiceFix);
    }

    @Test
    void testRunWithNullConfig() throws Exception {
        // Test with null config
        processor = new DirectCommandProcessor(documentorCommands, null, llmServiceFix);

        when(applicationArguments.getSourceArgs()).thenReturn(new String[]{});

        assertDoesNotThrow(() -> processor.run(applicationArguments));

        // Verify that llmServiceFix is not called when config is null
        verifyNoInteractions(llmServiceFix);
    }

    @Test
    void testRunWithValidConfig() throws Exception {
        when(applicationArguments.getSourceArgs()).thenReturn(new String[]{});
        when(llmServiceFix.isThreadLocalConfigAvailable()).thenReturn(true);

        processor.run(applicationArguments);

        // Verify config is set in ThreadLocal
        verify(llmServiceFix).setLlmServiceThreadLocalConfig(documentorConfig);
        verify(llmServiceFix).isThreadLocalConfigAvailable();
    }

    @Test
    void testRunWithConfigVerificationFails() throws Exception {
        when(applicationArguments.getSourceArgs()).thenReturn(new String[]{});
        when(llmServiceFix.isThreadLocalConfigAvailable()).thenReturn(false);

        processor.run(applicationArguments);

        // Verify config setting was attempted even if verification fails
        verify(llmServiceFix).setLlmServiceThreadLocalConfig(documentorConfig);
        verify(llmServiceFix).isThreadLocalConfigAvailable();
    }

    @Test
    void testRunWithEmptyArgs() throws Exception {
        when(applicationArguments.getSourceArgs()).thenReturn(new String[]{});

        processor.run(applicationArguments);

        // Should not call analyze command with empty args
        verifyNoInteractions(documentorCommands);
    }

    @Test
    void testRunWithNonAnalyzeCommand() throws Exception {
        when(applicationArguments.getSourceArgs()).thenReturn(new String[]{"help"});

        processor.run(applicationArguments);

        // Should not call analyze command with non-analyze command
        verifyNoInteractions(documentorCommands);
    }

    @Test
    void testAnalyzeCommandWithDefaultParameters() throws Exception {
        when(applicationArguments.getSourceArgs()).thenReturn(new String[]{"analyze"});
        when(
            documentorCommands.analyzeProject(
                anyString(),
                anyString(),
                anyBoolean(),
                anyBoolean(),
                anyString(),
                anyBoolean(),
                anyString()
            )
        ).thenReturn("Analysis complete");

        processor.run(applicationArguments);

        // Verify analyze is called with default parameters
        verify(documentorCommands).analyzeProject(".", "config.json", true, false, "", false, "");
    }

    @Test
    void testAnalyzeCommandWithProjectPath() throws Exception {
        when(applicationArguments.getSourceArgs()).thenReturn(new String[]{
            "analyze", "--project-path", "/custom/path"
        });
        when(
            documentorCommands.analyzeProject(
                anyString(),
                anyString(),
                anyBoolean(),
                anyBoolean(),
                anyString(),
                anyBoolean(),
                anyString()
            )
        ).thenReturn("Analysis complete");

        processor.run(applicationArguments);

        verify(documentorCommands).analyzeProject("/custom/path", "config.json", true, false, "", false, "");
    }

    @Test
    void testAnalyzeCommandWithConfigPath() throws Exception {
        when(applicationArguments.getSourceArgs()).thenReturn(new String[]{
            "analyze", "--config", "custom-config.json"
        });
        when(
            documentorCommands.analyzeProject(
                anyString(),
                anyString(),
                anyBoolean(),
                anyBoolean(),
                anyString(),
                anyBoolean(),
                anyString()
            )
        ).thenReturn("Analysis complete");

        processor.run(applicationArguments);

        verify(documentorCommands).analyzeProject(".", "custom-config.json", true, false, "", false, "");
    }

    @Test
    void testAnalyzeCommandWithIncludePrivateMembers() throws Exception {
        when(applicationArguments.getSourceArgs()).thenReturn(new String[]{
            "analyze", "--include-private-members", "false"
        });
        when(
            documentorCommands.analyzeProject(
                anyString(),
                anyString(),
                anyBoolean(),
                anyBoolean(),
                anyString(),
                anyBoolean(),
                anyString()
            )
        ).thenReturn("Analysis complete");

        processor.run(applicationArguments);

        verify(documentorCommands).analyzeProject(".", "config.json", false, false, "", false, "");
    }

    @Test
    void testAnalyzeCommandWithMermaidGeneration() throws Exception {
        when(applicationArguments.getSourceArgs()).thenReturn(new String[]{
            "analyze", "--generate-mermaid", "true", "--mermaid-output", "diagram.mmd"
        });
        when(
            documentorCommands.analyzeProject(anyString(), anyString(), anyBoolean(),
                anyBoolean(), anyString(), anyBoolean(), anyString())
        ).thenReturn("Analysis complete");

        processor.run(applicationArguments);

        verify(documentorCommands).analyzeProject(".", "config.json", true, true, "diagram.mmd", false, "");
    }

    @Test
    void testAnalyzeCommandWithPlantUMLGeneration() throws Exception {
        when(applicationArguments.getSourceArgs()).thenReturn(new String[]{
            "analyze", "--generate-plantuml", "true", "--plantuml-output", "diagram.puml"
        });
        when(
            documentorCommands.analyzeProject(anyString(), anyString(), anyBoolean(),
                anyBoolean(), anyString(), anyBoolean(), anyString())
        ).thenReturn("Analysis complete");

        processor.run(applicationArguments);

        verify(documentorCommands).analyzeProject(".", "config.json", true, false, "", true, "diagram.puml");
    }

    @Test
    void testAnalyzeCommandWithAllParameters() throws Exception {
        when(applicationArguments.getSourceArgs()).thenReturn(new String[]{
            "analyze",
            "--project-path", "/custom/project",
            "--config", "custom.json",
            "--include-private-members", "false",
            "--generate-mermaid", "true",
            "--mermaid-output", "mermaid.mmd",
            "--generate-plantuml", "true",
            "--plantuml-output", "plantuml.puml"
        });
        when(
            documentorCommands.analyzeProject(
                anyString(),
                anyString(),
                anyBoolean(),
                anyBoolean(),
                anyString(),
                anyBoolean(),
                anyString()
            )
        ).thenReturn("Analysis complete");

        processor.run(applicationArguments);

        verify(
            documentorCommands
        ).analyzeProject(
            "/custom/project",
            "custom.json",
            false,
            true,
            "mermaid.mmd",
            true,
            "plantuml.puml"
        );
    }

    @Test
    void testAnalyzeCommandWithIncompleteParameters() throws Exception {
        // Test with missing values for parameters
        when(applicationArguments.getSourceArgs()).thenReturn(new String[]{
            "analyze", "--project-path"  // Missing value
        });
        when(
            documentorCommands.analyzeProject(
                anyString(),
                anyString(),
                anyBoolean(),
                anyBoolean(),
                anyString(),
                anyBoolean(),
                anyString()
            )
        ).thenReturn("Analysis complete");

        processor.run(applicationArguments);

        // Should use default values when parameter values are missing
        verify(documentorCommands).analyzeProject(".", "config.json", true, false, "", false, "");
    }

    @Test
    void testAnalyzeCommandExceptionHandling() throws Exception {
        when(applicationArguments.getSourceArgs()).thenReturn(new String[]{"analyze"});
        when(documentorCommands.analyzeProject(anyString(), anyString(), anyBoolean(),
                                             anyBoolean(), anyString(), anyBoolean(), anyString()))
            .thenThrow(new RuntimeException("Test exception"));

        // Should not throw exception, just log it
        assertDoesNotThrow(() -> processor.run(applicationArguments));

        verify(documentorCommands).analyzeProject(".", "config.json", true, false, "", false, "");
    }

    @Test
    void testAnalyzeCommandWithDoubleConfigSet() throws Exception {
        when(applicationArguments.getSourceArgs()).thenReturn(new String[]{"analyze"});
        when(
            documentorCommands.analyzeProject(
                anyString(),
                anyString(),
                anyBoolean(),
                anyBoolean(),
                anyString(),
                anyBoolean(),
                anyString()
            )
        ).thenReturn("Analysis complete");

        processor.run(applicationArguments);

        // Verify config is set twice - once at start and once before analyze
        verify(llmServiceFix, times(2)).setLlmServiceThreadLocalConfig(documentorConfig);
    }

    @Test
    void testAnalyzeCommandWithNullConfigDoubleSet() throws Exception {
        processor = new DirectCommandProcessor(documentorCommands, null, llmServiceFix);

        when(applicationArguments.getSourceArgs()).thenReturn(new String[]{"analyze"});
        when(documentorCommands.analyzeProject(anyString(), anyString(), anyBoolean(),
                                             anyBoolean(), anyString(), anyBoolean(), anyString()))
            .thenReturn("Analysis complete");

        processor.run(applicationArguments);

        // Should not call llmServiceFix when config is null
        verifyNoInteractions(llmServiceFix);
        verify(documentorCommands).analyzeProject(".", "config.json", true, false, "", false, "");
    }

    @Test
    void testAnalyzeCommandWithBooleanParameters() throws Exception {
        // Test with various boolean values
        when(applicationArguments.getSourceArgs()).thenReturn(new String[]{
            "analyze",
            "--include-private-members", "TRUE",
            "--generate-mermaid", "FALSE",
            "--generate-plantuml", "true"
        });
        when(documentorCommands.analyzeProject(anyString(), anyString(), anyBoolean(),
                                             anyBoolean(), anyString(), anyBoolean(), anyString()))
            .thenReturn("Analysis complete");

        processor.run(applicationArguments);

        verify(documentorCommands).analyzeProject(".", "config.json", true, false, "", true, "");
    }

    @Test
    void testAnalyzeCommandWithInvalidBooleanParameters() throws Exception {
        when(applicationArguments.getSourceArgs()).thenReturn(new String[]{
            "analyze",
            "--include-private-members", "invalid-boolean",
            "--generate-mermaid", "not-a-boolean"
        });
        when(documentorCommands.analyzeProject(anyString(), anyString(), anyBoolean(),
                                             anyBoolean(), anyString(), anyBoolean(), anyString()))
            .thenReturn("Analysis complete");

        processor.run(applicationArguments);

        // Invalid boolean strings should parse as false
        verify(documentorCommands).analyzeProject(".", "config.json", false, false, "", false, "");
    }
}
