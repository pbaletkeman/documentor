package com.documentor.cli;

import com.documentor.config.DocumentorConfig;
import com.documentor.model.CodeElement;
import com.documentor.model.CodeElementType;
import com.documentor.model.ProjectAnalysis;
import com.documentor.service.CodeAnalysisService;
import com.documentor.service.DocumentationService;
import com.documentor.service.MermaidDiagramService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * 🧪 Enhanced unit tests for DocumentorCommands to achieve 90%+ coverage
 */
@ExtendWith(MockitoExtension.class)
class DocumentorCommandsEnhancedTest {

    @Mock
    private CodeAnalysisService codeAnalysisService;

    @Mock
    private DocumentationService documentationService;

    @Mock
    private MermaidDiagramService mermaidDiagramService;

    @Mock
    private DocumentorConfig documentorConfig;

    @Mock
    private DocumentorConfig.OutputSettings outputSettings;

    @Mock
    private DocumentorConfig.AnalysisSettings analysisSettings;

    @Mock
    private DocumentorConfig.LlmModelConfig llmModelConfig;

    private DocumentorCommands documentorCommands;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        documentorCommands = new DocumentorCommands(codeAnalysisService, documentationService, mermaidDiagramService, documentorConfig);
    }

    @Test
    void testAnalyzeProjectWithMermaidDiagrams() throws Exception {
        // Setup
        List<CodeElement> elements = List.of(
            new CodeElement(CodeElementType.CLASS, "TestClass", "TestClass", "test.java", 1, 
                "public class TestClass {}", "", List.of(), List.of())
        );
        ProjectAnalysis analysis = new ProjectAnalysis(tempDir.toString(), elements, System.currentTimeMillis());
        
        when(codeAnalysisService.analyzeProject(any(Path.class)))
            .thenReturn(CompletableFuture.completedFuture(analysis));
        when(documentationService.generateDocumentation(any(ProjectAnalysis.class)))
            .thenReturn(CompletableFuture.completedFuture("docs/output"));
        when(mermaidDiagramService.generateClassDiagrams(any(ProjectAnalysis.class), anyString()))
            .thenReturn(CompletableFuture.completedFuture(List.of("diagram1.mmd", "diagram2.mmd")));

        // Test with Mermaid enabled and custom output path
        String result = documentorCommands.analyzeProject(tempDir.toString(), "config.json", true, "custom/diagrams");
        
        // Verify
        assertNotNull(result);
        assertTrue(result.contains("✅"));
        assertTrue(result.contains("Analysis complete"));
        assertTrue(result.contains("📊"));
        assertTrue(result.contains("Generated 2 Mermaid diagrams"));
        assertTrue(result.contains("diagram1.mmd"));
        assertTrue(result.contains("diagram2.mmd"));
        
        verify(codeAnalysisService).analyzeProject(any(Path.class));
        verify(documentationService).generateDocumentation(any(ProjectAnalysis.class));
        verify(mermaidDiagramService).generateClassDiagrams(any(ProjectAnalysis.class), eq("custom/diagrams"));
    }

    @Test
    void testAnalyzeProjectWithMermaidDiagramsEmptyOutput() throws Exception {
        // Setup
        List<CodeElement> elements = List.of(
            new CodeElement(CodeElementType.CLASS, "TestClass", "TestClass", "test.java", 1, 
                "public class TestClass {}", "", List.of(), List.of())
        );
        ProjectAnalysis analysis = new ProjectAnalysis(tempDir.toString(), elements, System.currentTimeMillis());
        
        when(codeAnalysisService.analyzeProject(any(Path.class)))
            .thenReturn(CompletableFuture.completedFuture(analysis));
        when(documentationService.generateDocumentation(any(ProjectAnalysis.class)))
            .thenReturn(CompletableFuture.completedFuture("docs/output"));
        when(mermaidDiagramService.generateClassDiagrams(any(ProjectAnalysis.class), isNull()))
            .thenReturn(CompletableFuture.completedFuture(Collections.emptyList()));

        // Test with Mermaid enabled but empty output path
        String result = documentorCommands.analyzeProject(tempDir.toString(), "config.json", true, "");
        
        // Verify
        assertNotNull(result);
        assertTrue(result.contains("✅"));
        assertTrue(result.contains("Analysis complete"));
        assertTrue(result.contains("📊"));
        assertTrue(result.contains("Generated 0 Mermaid diagrams"));
        assertFalse(result.contains("Diagram files:"));
        
        verify(mermaidDiagramService).generateClassDiagrams(any(ProjectAnalysis.class), isNull());
    }

    @Test
    void testAnalyzeProjectWithMermaidDiagramsWhitespaceOutput() throws Exception {
        // Setup
        List<CodeElement> elements = List.of(
            new CodeElement(CodeElementType.CLASS, "TestClass", "TestClass", "test.java", 1, 
                "public class TestClass {}", "", List.of(), List.of())
        );
        ProjectAnalysis analysis = new ProjectAnalysis(tempDir.toString(), elements, System.currentTimeMillis());
        
        when(codeAnalysisService.analyzeProject(any(Path.class)))
            .thenReturn(CompletableFuture.completedFuture(analysis));
        when(documentationService.generateDocumentation(any(ProjectAnalysis.class)))
            .thenReturn(CompletableFuture.completedFuture("docs/output"));
        when(mermaidDiagramService.generateClassDiagrams(any(ProjectAnalysis.class), isNull()))
            .thenReturn(CompletableFuture.completedFuture(List.of("diagram.mmd")));

        // Test with Mermaid enabled but whitespace-only output path
        String result = documentorCommands.analyzeProject(tempDir.toString(), "config.json", true, "   ");
        
        // Verify
        assertNotNull(result);
        assertTrue(result.contains("✅"));
        assertTrue(result.contains("Analysis complete"));
        assertTrue(result.contains("📊"));
        assertTrue(result.contains("Generated 1 Mermaid diagrams"));
        
        verify(mermaidDiagramService).generateClassDiagrams(any(ProjectAnalysis.class), isNull());
    }

    @Test
    void testAnalyzeProjectDirectoryAsFile() throws Exception {
        // Create a file instead of directory
        Path fileInsteadOfDir = tempDir.resolve("not-a-directory.txt");
        Files.writeString(fileInsteadOfDir, "this is a file");

        String result = documentorCommands.analyzeProject(fileInsteadOfDir.toString(), "config.json", false, "");
        
        assertTrue(result.contains("❌"));
        assertTrue(result.contains("Project path does not exist or is not a directory"));
        
        verifyNoInteractions(codeAnalysisService);
        verifyNoInteractions(documentationService);
    }

    @Test
    void testValidateConfigWithIOException() throws Exception {
        // Create a directory with the same name as the config file to cause an IOException
        Path configDir = tempDir.resolve("config-dir");
        Files.createDirectory(configDir);

        String result = documentorCommands.validateConfig(configDir.toString());
        
        assertTrue(result.contains("❌") || result.contains("Error") || result.contains("error"));
    }

    @Test
    void testShowStatusWithCompleteConfig() {
        // Setup comprehensive mock config
        when(documentorConfig.llmModels()).thenReturn(List.of(llmModelConfig));
        
        when(llmModelConfig.name()).thenReturn("gpt-4");
        when(llmModelConfig.apiKey()).thenReturn("sk-test123");

        String result = documentorCommands.showStatus();
        
        assertNotNull(result);
        assertTrue(result.contains("Documentor Status"));
        assertTrue(result.contains("LLM Models") || result.contains("gpt-4"));
    }

    @Test
    void testShowStatusWithEmptyLlmModels() {
        // Setup empty LLM models list
        when(documentorConfig.llmModels()).thenReturn(Collections.emptyList());

        String result = documentorCommands.showStatus();
        
        assertNotNull(result);
        assertTrue(result.contains("Documentor Status"));
        assertTrue(result.contains("No LLM models configured") || result.contains("LLM Models"));
    }

    @Test
    void testShowStatusWithNullValues() {
        // Setup config with null values to test defensive programming
        when(documentorConfig.llmModels()).thenReturn(null);
        when(documentorConfig.outputSettings()).thenReturn(null);
        when(documentorConfig.analysisSettings()).thenReturn(null);

        String result = documentorCommands.showStatus();
        
        assertNotNull(result);
        assertTrue(result.contains("Documentor Status"));
        // Should handle null values gracefully
    }

    @Test
    void testScanProjectEmptyResults() throws Exception {
        // Setup with empty analysis results
        ProjectAnalysis emptyAnalysis = new ProjectAnalysis(tempDir.toString(), Collections.emptyList(), System.currentTimeMillis());
        
        when(codeAnalysisService.analyzeProject(any(Path.class)))
            .thenReturn(CompletableFuture.completedFuture(emptyAnalysis));

        String result = documentorCommands.scanProject(tempDir.toString());
        
        assertNotNull(result);
        assertTrue(result.contains("📊"));
        assertTrue(result.contains("Project Analysis Results"));
        assertTrue(result.contains("0 total elements"));
        
        verify(codeAnalysisService).analyzeProject(any(Path.class));
    }

    @Test
    void testValidateConfigWithValidComplexJson() throws Exception {
        Path configFile = tempDir.resolve("complex-config.json");
        String complexConfig = """
            {
                "llm_models": [
                    {
                        "name": "gpt-4",
                        "provider": "openai",
                        "api_key": "sk-test123",
                        "endpoint": "https://api.openai.com/v1/chat/completions",
                        "max_tokens": 4000,
                        "temperature": 0.7,
                        "timeout": 30,
                        "parameters": {
                            "model": "gpt-4",
                            "stream": false
                        }
                    },
                    {
                        "name": "local-llama",
                        "provider": "ollama",
                        "endpoint": "http://localhost:11434/api/generate",
                        "max_tokens": 2048,
                        "temperature": 0.3,
                        "timeout": 60
                    }
                ],
                "output_settings": {
                    "output_directory": "./documentation",
                    "markdown_format": true,
                    "generate_mermaid": true,
                    "verbose_output": false,
                    "quality_score": 0.85,
                    "include_stats": true,
                    "custom_template": "templates/custom.md"
                },
                "analysis_settings": {
                    "include_private_members": false,
                    "max_depth": 10,
                    "included_patterns": ["**/*.java", "**/*.py", "**/*.js"],
                    "excluded_patterns": ["**/test/**", "**/target/**", "**/node_modules/**"]
                }
            }
            """;
        Files.writeString(configFile, complexConfig);

        String result = documentorCommands.validateConfig(configFile.toString());
        
        assertTrue(result.contains("✅"));
        assertTrue(result.contains("Configuration file is valid"));
    }

    @Test
    void testShowStatusCurrentProject() throws Exception {
        // Setup config
        when(documentorConfig.llmModels()).thenReturn(List.of(llmModelConfig));
        when(documentorConfig.outputSettings()).thenReturn(outputSettings);
        when(documentorConfig.analysisSettings()).thenReturn(analysisSettings);
        
        when(llmModelConfig.name()).thenReturn("test-model");
        when(outputSettings.outputPath()).thenReturn("./docs");
        when(analysisSettings.includePrivateMembers()).thenReturn(false);

        // First analyze a project to set current state
        List<CodeElement> elements = List.of(
            new CodeElement(CodeElementType.CLASS, "TestClass", "TestClass", "test.java", 1, 
                "public class TestClass {}", "", List.of(), List.of())
        );
        ProjectAnalysis analysis = new ProjectAnalysis(tempDir.toString(), elements, System.currentTimeMillis());
        
        when(codeAnalysisService.analyzeProject(any(Path.class)))
            .thenReturn(CompletableFuture.completedFuture(analysis));
        when(documentationService.generateDocumentation(any(ProjectAnalysis.class)))
            .thenReturn(CompletableFuture.completedFuture("docs/output"));

        // Analyze to set current project
        documentorCommands.analyzeProject(tempDir.toString(), "config.json", false, "");

        // Now check status
        String result = documentorCommands.showStatus();
        
        assertNotNull(result);
        assertTrue(result.contains("Documentor Status"));
        assertTrue(result.contains("Current Project"));
        assertTrue(result.contains(tempDir.toString()) || result.contains("test.java"));
    }
}