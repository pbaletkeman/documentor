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
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 🧪 Unit tests for DocumentorCommands
 */
@ExtendWith(MockitoExtension.class)
class DocumentorCommandsTest {

    @Mock
    private CodeAnalysisService codeAnalysisService;

    @Mock
    private DocumentationService documentationService;

    @Mock
    private MermaidDiagramService mermaidDiagramService;

    @Mock
    private DocumentorConfig documentorConfig;

    private DocumentorCommands documentorCommands;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        documentorCommands = new DocumentorCommands(codeAnalysisService, documentationService, mermaidDiagramService, documentorConfig);
    }

    @Test
    void testAnalyzeProjectSuccess() throws Exception {
        // Create a simple real ProjectAnalysis instead of complex mocking
        List<CodeElement> elements = List.of(
            new CodeElement(CodeElementType.CLASS, "TestClass", "TestClass", "test.java", 1, 
                "public class TestClass {}", "", List.of(), List.of())
        );
        ProjectAnalysis analysis = new ProjectAnalysis(tempDir.toString(), elements, System.currentTimeMillis());
        
        when(codeAnalysisService.analyzeProject(any(Path.class)))
            .thenReturn(CompletableFuture.completedFuture(analysis));
        when(documentationService.generateDocumentation(any(ProjectAnalysis.class)))
            .thenReturn(CompletableFuture.completedFuture("docs/output"));

        String result = documentorCommands.analyzeProject(tempDir.toString(), "config.json", false, "");
        
        assertNotNull(result);
        assertTrue(result.contains("✅"));
        assertTrue(result.contains("Analysis complete"));
        
        verify(codeAnalysisService).analyzeProject(any(Path.class));
        verify(documentationService).generateDocumentation(any(ProjectAnalysis.class));
    }

    @Test
    void testScanProjectSuccess() throws Exception {
        // Create a simple real ProjectAnalysis
        List<CodeElement> elements = List.of(
            new CodeElement(CodeElementType.CLASS, "TestClass", "TestClass", "test.java", 1, 
                "public class TestClass {}", "", List.of(), List.of()),
            new CodeElement(CodeElementType.METHOD, "testMethod", "TestClass.testMethod", "test.java", 2,
                "public void testMethod() {}", "", List.of(), List.of())
        );
        ProjectAnalysis analysis = new ProjectAnalysis(tempDir.toString(), elements, System.currentTimeMillis());
        
        when(codeAnalysisService.analyzeProject(any(Path.class)))
            .thenReturn(CompletableFuture.completedFuture(analysis));

        String result = documentorCommands.scanProject(tempDir.toString());
        
        assertNotNull(result);
        assertTrue(result.contains("📊"));
        assertTrue(result.contains("Project Analysis Results"));
        
        verify(codeAnalysisService).analyzeProject(any(Path.class));
        verify(documentationService, never()).generateDocumentation(any());
    }

    @Test
    void testAnalyzeProjectNonExistentPath() {
        String result = documentorCommands.analyzeProject("non-existent-path", "config.json", false, "");
        
        assertTrue(result.contains("❌"));
        assertTrue(result.contains("Project path does not exist"));
        
        verifyNoInteractions(codeAnalysisService);
        verifyNoInteractions(documentationService);
    }

    @Test
    void testScanProjectNonExistentPath() {
        String result = documentorCommands.scanProject("non-existent-path");
        
        assertTrue(result.contains("❌"));
        assertTrue(result.contains("Project path does not exist"));
        
        verifyNoInteractions(codeAnalysisService);
    }

    @Test
    void testValidateConfigValidPath() throws Exception {
        Path configFile = tempDir.resolve("test-config.json");
        String validConfig = """
            {
                "llmSettings": {
                    "provider": "openai",
                    "model": "gpt-4"
                },
                "analysisSettings": {
                    "includePrivate": false
                }
            }
            """;
        Files.writeString(configFile, validConfig);

        String result = documentorCommands.validateConfig(configFile.toString());
        
        assertTrue(result.contains("✅"));
        assertTrue(result.contains("Configuration file is valid"));
    }

    @Test
    void testValidateConfigNonExistentPath() {
        String result = documentorCommands.validateConfig("non-existent-config.json");
        
        assertTrue(result.contains("❌"));
        assertTrue(result.contains("Configuration file not found"));
    }

    @Test
    void testAnalyzeProjectWithException() throws Exception {
        when(codeAnalysisService.analyzeProject(any(Path.class)))
            .thenReturn(CompletableFuture.failedFuture(new RuntimeException("Analysis failed")));

        String result = documentorCommands.analyzeProject(tempDir.toString(), "config.json", false, "");
        
        assertTrue(result.contains("❌"));
        assertTrue(result.contains("Error"));
        
        verify(codeAnalysisService).analyzeProject(any(Path.class));
    }

    @Test
    void testScanProjectWithException() throws Exception {
        when(codeAnalysisService.analyzeProject(any(Path.class)))
            .thenReturn(CompletableFuture.failedFuture(new RuntimeException("Scan failed")));

        String result = documentorCommands.scanProject(tempDir.toString());
        
        assertTrue(result.contains("❌"));
        assertTrue(result.contains("Error"));
        
        verify(codeAnalysisService).analyzeProject(any(Path.class));
    }

    @Test
    void testShowInfo() {
        // When
        String result = documentorCommands.showInfo();
        
        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.contains("Documentor"));
        assertTrue(result.contains("Supported Languages"));
        assertTrue(result.contains("Java"));
        assertTrue(result.contains("Python"));
        assertTrue(result.contains("LLM Integration"));
        assertTrue(result.contains("Generated Documentation"));
        assertTrue(result.contains("Features"));
        assertTrue(result.contains("Analysis Coverage"));
        assertTrue(result.contains("analyze --project-path"));
        
        // Should not interact with services
        verifyNoInteractions(codeAnalysisService);
        verifyNoInteractions(documentationService);
    }

    @Test
    void testQuickStart() {
        // When
        String result = documentorCommands.quickStart();
        
        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.contains("Quick Start Guide"));
        assertTrue(result.contains("Create a configuration file"));
        assertTrue(result.contains("config.json"));
        assertTrue(result.contains("llm_models"));
        assertTrue(result.contains("api_key"));
        assertTrue(result.contains("Analyze your project"));
        assertTrue(result.contains("analyze --project-path"));
        assertTrue(result.contains("View generated documentation"));
        assertTrue(result.contains("Pro Tips"));
        assertTrue(result.contains("scan"));
        
        // Should not interact with services
        verifyNoInteractions(codeAnalysisService);
        verifyNoInteractions(documentationService);
    }

    @Test
    void testConstructor() {
        // When
        DocumentorCommands commands = new DocumentorCommands(codeAnalysisService, documentationService, mermaidDiagramService, documentorConfig);
        
        // Then
        assertNotNull(commands);
    }

    @Test
    void testShowStatus() {
        // When
        String result = documentorCommands.showStatus();
        
        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.contains("Documentor Status"));
        assertTrue(result.contains("Current Project"));
        assertTrue(result.contains("Configuration"));
        assertTrue(result.contains("LLM Models"));
        assertTrue(result.contains("Output Settings"));
        assertTrue(result.contains("Analysis Settings"));
        
        // Should not interact with services for status display
        verifyNoInteractions(codeAnalysisService);
        verifyNoInteractions(documentationService);
    }

    @Test
    void testShowStatusWithNullConfig() {
        // Setup - Create DocumentorCommands with null config
        DocumentorCommands commandsWithNullConfig = new DocumentorCommands(codeAnalysisService, documentationService, mermaidDiagramService, null);
        
        // When
        String result = commandsWithNullConfig.showStatus();
        
        // Then
        assertNotNull(result);
        assertTrue(result.contains("Documentor Status"));
        // With null config, status should handle gracefully
    }

    @Test
    void testValidateConfigInvalidJson() throws Exception {
        Path configFile = tempDir.resolve("invalid-config.json");
        String invalidJson = "{ invalid json content without proper quotes or structure and missing closing brace";
        Files.writeString(configFile, invalidJson);

        String result = documentorCommands.validateConfig(configFile.toString());
        
        // The validation method should detect this as invalid and return an error message
        assertTrue(result.contains("❌") || result.contains("Error") || result.contains("error") ||
                   result.contains("Invalid") || result.contains("invalid") || 
                   result.contains("Failed") || result.contains("failed") ||
                   result.contains("parsing") || result.contains("parse"));
    }

    @Test
    void testAnalyzeProjectWithOutputPath() throws Exception {
        // Create a simple real ProjectAnalysis
        List<CodeElement> elements = List.of(
            new CodeElement(CodeElementType.CLASS, "TestClass", "TestClass", "test.java", 1, 
                "public class TestClass {}", "", List.of(), List.of())
        );
        ProjectAnalysis analysis = new ProjectAnalysis(tempDir.toString(), elements, System.currentTimeMillis());
        
        when(codeAnalysisService.analyzeProject(any(Path.class)))
            .thenReturn(CompletableFuture.completedFuture(analysis));
        when(documentationService.generateDocumentation(any(ProjectAnalysis.class)))
            .thenReturn(CompletableFuture.completedFuture("docs/output"));

        String outputPath = tempDir.resolve("output").toString();
        String result = documentorCommands.analyzeProject(tempDir.toString(), outputPath, false, "");
        
        assertNotNull(result);
        assertTrue(result.contains("✅") || result.contains("Analysis complete"));
        
        verify(codeAnalysisService).analyzeProject(any(Path.class));
        verify(documentationService).generateDocumentation(any(ProjectAnalysis.class));
    }
}