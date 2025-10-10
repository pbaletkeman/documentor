package com.documentor.cli;package com.documentor.cli;package com.documentor.cli;package com.documentor.cli;package com.documentor.cli;



import com.documentor.cli.handlers.ConfigurationCommandHandler;

import com.documentor.cli.handlers.ProjectAnalysisCommandHandler;

import com.documentor.cli.handlers.StatusCommandHandler;import com.documentor.cli.handlers.ConfigurationCommandHandler;

import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;import com.documentor.cli.handlers.ProjectAnalysisCommandHandler;

import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;import com.documentor.cli.handlers.StatusCommandHandler;import com.documentor.cli.handlers.ConfigurationCommandHandler;

import org.mockito.junit.jupiter.MockitoExtension;

import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.*;import org.junit.jupiter.api.Test;import com.documentor.cli.handlers.ProjectAnalysisCommandHandler;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.extension.ExtendWith;

/**

 * Tests for DocumentorCommands CLI componentimport org.junit.jupiter.api.io.TempDir;import com.documentor.cli.handlers.StatusCommandHandler;import com.documentor.cli.handlers.ConfigurationCommandHandler;import com.documentor.cli.handlers.ConfigurationCommandHandler;

 */

@ExtendWith(MockitoExtension.class)import org.mockito.Mock;

class DocumentorCommandsTest {

import org.mockito.junit.jupiter.MockitoExtension;import org.junit.jupiter.api.BeforeEach;

    @Mock

    private ProjectAnalysisCommandHandler projectAnalysisHandler;

    

    @Mockimport java.nio.file.Path;import org.junit.jupiter.api.Test;import com.documentor.cli.handlers.ProjectAnalysisCommandHandler;import com.documentor.cli.handlers.ProjectAnalysisCommandHandler;

    private ConfigurationCommandHandler configurationHandler;

    

    @Mock

    private StatusCommandHandler statusHandler;import static org.junit.jupiter.api.Assertions.*;import org.junit.jupiter.api.extension.ExtendWith;



    private DocumentorCommands documentorCommands;import static org.mockito.ArgumentMatchers.*;



    @BeforeEachimport static org.mockito.Mockito.when;import org.junit.jupiter.api.io.TempDir;import com.documentor.cli.handlers.StatusCommandHandler;import com.documentor.cli.handlers.StatusCommandHandler;

    void setUp() {

        documentorCommands = new DocumentorCommands(

            projectAnalysisHandler, 

            configurationHandler, /**import org.mockito.Mock;

            statusHandler

        ); * 🧪 Tests for DocumentorCommands CLI component

    }

 */import org.mockito.junit.jupiter.MockitoExtension;import org.junit.jupiter.api.BeforeEach;import org.junit.jupiter.api.BeforeEach;

    @Test

    void testShowStatus() {@ExtendWith(MockitoExtension.class)

        // Given

        String expectedStatus = "System Status: All components operational";class DocumentorCommandsTest {

        when(statusHandler.handleShowStatus()).thenReturn(expectedStatus);



        // When

        String result = documentorCommands.showStatus();    @Mockimport java.nio.file.Path;import org.junit.jupiter.api.Test;import org.junit.jupiter.api.Test;



        // Then    private ProjectAnalysisCommandHandler projectAnalysisHandler;

        assertNotNull(result);

        assertEquals(expectedStatus, result);    

    }

    @Mock

    @Test

    void testShowConfiguration() {    private ConfigurationCommandHandler configurationHandler;import static org.junit.jupiter.api.Assertions.*;import org.junit.jupiter.api.extension.ExtendWith;import org.junit.jupiter.api.extension.ExtendWith;

        // Given

        String expectedConfig = "Configuration: Default settings loaded";    

        when(configurationHandler.handleShowConfiguration(anyString())).thenReturn(expectedConfig);

    @Mockimport static org.mockito.ArgumentMatchers.*;

        // When

        String result = documentorCommands.showConfiguration("config.json");    private StatusCommandHandler statusHandler;



        // Thenimport static org.mockito.Mockito.when;import org.junit.jupiter.api.io.TempDir;import org.junit.jupiter.api.io.TempDir;

        assertNotNull(result);

        assertEquals(expectedConfig, result);    private DocumentorCommands documentorCommands;

    }

}    

    @TempDir

    Path tempDir;/**import org.mockito.Mock;import org.mockito.Mock;



    @BeforeEach * 🧪 Tests for DocumentorCommands CLI component

    void setUp() {

        documentorCommands = new DocumentorCommands( */import org.mockito.junit.jupiter.MockitoExtension;import org.mockito.junit.jupiter.MockitoExtension;

            projectAnalysisHandler, 

            configurationHandler, @ExtendWith(MockitoExtension.class)

            statusHandler

        );class DocumentorCommandsTest {

    }



    @Test

    void testAnalyzeProjectSuccess() throws Exception {    @Mockimport java.nio.file.Files;import java.nio.file.Files;

        // Given

        String expectedResult = "✅ Analysis complete! Documentation generated at: docs/output";    private ProjectAnalysisCommandHandler projectAnalysisHandler;

        

        when(projectAnalysisHandler.handleAnalyzeProject(anyString(), anyString(), anyBoolean(), anyString()))    import java.nio.file.Path;import java.nio.file.Path;

            .thenReturn(expectedResult);

    @Mock

        // When

        String result = documentorCommands.analyzeProject(tempDir.toString(), "config.json", false, "");    private ConfigurationCommandHandler configurationHandler;



        // Then    

        assertNotNull(result);

        assertEquals(expectedResult, result);    @Mockimport static org.junit.jupiter.api.Assertions.*;import static org.junit.jupiter.api.Assertions.*;

    }

    private StatusCommandHandler statusHandler;

    @Test

    void testShowStatus() {import static org.mockito.ArgumentMatchers.*;import static org.mockito.ArgumentMatchers.*;

        // Given

        String expectedStatus = "📊 System Status: All components operational";    private DocumentorCommands documentorCommands;

        

        when(statusHandler.handleShowStatus()).thenReturn(expectedStatus);    import static org.mockito.Mockito.*;import static org.mockito.Mockito.*;



        // When    @TempDir

        String result = documentorCommands.showStatus();

    Path tempDir;

        // Then

        assertNotNull(result);

        assertEquals(expectedStatus, result);

    }    @BeforeEach/**/**



    @Test    void setUp() {

    void testShowConfiguration() {

        // Given        documentorCommands = new DocumentorCommands( * 🧪 Unit tests for DocumentorCommands * 🧪 Unit tests for DocumentorCommands

        String expectedConfig = "🔧 Configuration: Default settings loaded";

                    projectAnalysisHandler, 

        when(configurationHandler.handleShowConfiguration(anyString())).thenReturn(expectedConfig);

            configurationHandler,  */ */

        // When

        String result = documentorCommands.showConfiguration("config.json");            statusHandler



        // Then        );@ExtendWith(MockitoExtension.class)@ExtendWith(MockitoExtension.class)

        assertNotNull(result);

        assertEquals(expectedConfig, result);    }

    }

class DocumentorCommandsTest {class DocumentorCommandsTest {

    @Test

    void testAnalyzeProjectWithEmptyPath() {    @Test

        // Given

        when(projectAnalysisHandler.handleAnalyzeProject(eq(""), anyString(), anyBoolean(), anyString()))    void testAnalyzeProjectSuccess() throws Exception {

            .thenReturn("❌ Error: Project path cannot be empty");

        // Given

        // When

        String result = documentorCommands.analyzeProject("", "config.json", false, "");        String expectedResult = "✅ Analysis complete! Documentation generated at: docs/output";    @Mock    @Mock



        // Then        

        assertNotNull(result);

        assertTrue(result.contains("Error"));        when(projectAnalysisHandler.handleAnalyzeProject(anyString(), anyString(), anyBoolean(), anyString()))    private ProjectAnalysisCommandHandler projectAnalysisHandler;    private ProjectAnalysisCommandHandler projectAnalysisHandler;

    }

}            .thenReturn(expectedResult);



        // When

        String result = documentorCommands.analyzeProject(tempDir.toString(), "config.json", false, "");    @Mock    @Mock



        // Then    private StatusCommandHandler statusHandler;    private StatusCommandHandler statusHandler;

        assertNotNull(result);

        assertEquals(expectedResult, result);

    }

    @Mock    @Mock

    @Test

    void testShowStatus() {    private ConfigurationCommandHandler configurationHandler;    private ConfigurationCommandHandler configurationHandler;

        // Given

        String expectedStatus = "📊 System Status: All components operational";

        

        when(statusHandler.handleShowStatus()).thenReturn(expectedStatus);    private DocumentorCommands documentorCommands;    private DocumentorCommands documentorCommands;



        // When

        String result = documentorCommands.showStatus();

    @TempDir    @TempDir

        // Then

        assertNotNull(result);    Path tempDir;    Path tempDir;

        assertEquals(expectedStatus, result);

    }



    @Test    @BeforeEach    @BeforeEach

    void testShowConfiguration() {

        // Given    void setUp() {    void setUp() {

        String expectedConfig = "🔧 Configuration: Default settings loaded";

                documentorCommands = new DocumentorCommands(projectAnalysisHandler, statusHandler, configurationHandler);        documentorCommands = new DocumentorCommands(projectAnalysisHandler, statusHandler, configurationHandler);

        when(configurationHandler.handleShowConfiguration(anyString())).thenReturn(expectedConfig);

    }    }

        // When

        String result = documentorCommands.showConfiguration("config.json");



        // Then    @Test    @Test

        assertNotNull(result);

        assertEquals(expectedConfig, result);    void testAnalyzeProjectSuccess() throws Exception {    void testAnalyzeProjectSuccess() throws Exception {

    }

        // Mock the handler's behavior        // Mock the handler's behavior

    @Test

    void testAnalyzeProjectWithEmptyPath() {        String expectedResult = "✅ Analysis complete! Documentation generated at: docs/output";        String expectedResult = "✅ Analysis complete! Documentation generated at: docs/output";

        // Given

        when(projectAnalysisHandler.handleAnalyzeProject(eq(""), anyString(), anyBoolean(), anyString()))        when(projectAnalysisHandler.handleAnalyzeProject(anyString(), anyString(), anyBoolean(), anyString()))        when(projectAnalysisHandler.handleAnalyzeProject(anyString(), anyString(), anyBoolean(), anyString()))

            .thenReturn("❌ Error: Project path cannot be empty");

            .thenReturn(expectedResult);            .thenReturn(expectedResult);

        // When

        String result = documentorCommands.analyzeProject("", "config.json", false, "");



        // Then        String result = documentorCommands.analyzeProject(tempDir.toString(), "config.json", false, "");        String result = documentorCommands.analyzeProject(tempDir.toString(), "config.json", false, "");

        assertNotNull(result);

        assertTrue(result.contains("Error"));                

    }

}        assertNotNull(result);        assertNotNull(result);

        assertTrue(result.contains("✅"));        assertTrue(result.contains("✅"));

        assertTrue(result.contains("Analysis complete"));        assertTrue(result.contains("Analysis complete"));

                

        verify(projectAnalysisHandler).handleAnalyzeProject(anyString(), anyString(), anyBoolean(), anyString());        verify(projectAnalysisHandler).handleAnalyzeProject(anyString(), anyString(), anyBoolean(), anyString());

    }    }



    @Test    @Test

    void testScanProjectSuccess() throws Exception {    void testScanProjectSuccess() throws Exception {

        // Mock the handler's behavior        // Mock the handler's behavior

        String expectedResult = "📊 Project Analysis Results\nFound 1 elements";        String expectedResult = "📊 Project Analysis Results\nFound 1 elements";

        when(projectAnalysisHandler.handleScanProject(anyString()))        when(projectAnalysisHandler.handleScanProject(anyString()))

            .thenReturn(expectedResult);            .thenReturn(expectedResult);



        String result = documentorCommands.scanProject(tempDir.toString());        String result = documentorCommands.scanProject(tempDir.toString());

                

        assertNotNull(result);        assertNotNull(result);

        assertTrue(result.contains("📊"));        assertTrue(result.contains("📊"));

        assertTrue(result.contains("Project Analysis Results"));        assertTrue(result.contains("Project Analysis Results"));

                

        verify(projectAnalysisHandler).handleScanProject(anyString());        verify(projectAnalysisHandler).handleScanProject(anyString());

    }    }



    @Test    @Test

    void testAnalyzeProjectNonExistentPath() {    void testAnalyzeProjectNonExistentPath() {

        String result = documentorCommands.analyzeProject("non-existent-path", "config.json", false, "");        String result = documentorCommands.analyzeProject("non-existent-path", "config.json", false, "");

                

        assertTrue(result.contains("❌"));        assertTrue(result.contains("❌"));

        assertTrue(result.contains("Project path does not exist"));        assertTrue(result.contains("Project path does not exist"));

                

        verifyNoInteractions(projectAnalysisHandler);        verifyNoInteractions(projectAnalysisHandler);

    }    }



    @Test    @Test

    void testScanProjectNonExistentPath() {    void testScanProjectNonExistentPath() {

        String result = documentorCommands.scanProject("non-existent-path");        String result = documentorCommands.scanProject("non-existent-path");

                

        assertTrue(result.contains("❌"));        assertTrue(result.contains("❌"));

        assertTrue(result.contains("Project path does not exist"));        assertTrue(result.contains("Project path does not exist"));

                

        verifyNoInteractions(projectAnalysisHandler);        verifyNoInteractions(projectAnalysisHandler);

    }    }



    @Test    @Test

    void testValidateConfigValidPath() throws Exception {    void testValidateConfigValidPath() throws Exception {

        Path configFile = tempDir.resolve("test-config.json");        Path configFile = tempDir.resolve("test-config.json");

        String validConfig = """        String validConfig = """

            {            {

                "llmSettings": {                "llmSettings": {

                    "models": [                    "models": [

                        {                        {

                            "name": "test-model",                            "name": "test-model",

                            "provider": "ollama",                            "provider": "ollama",

                            "baseUrl": "http://localhost:11434"                            "baseUrl": "http://localhost:11434"

                        }                        }

                    ]                    ]

                },                },

                "outputSettings": {                "outputSettings": {

                    "outputDirectory": "docs",                    "outputDirectory": "docs",

                    "format": "markdown"                    "format": "markdown"

                },                },

                "analysisSettings": {                "analysisSettings": {

                    "includePrivateMembers": false,                    "includePrivateMembers": false,

                    "maxDepth": 3                    "maxDepth": 3

                }                }

            }            }

            """;            """;

        Files.writeString(configFile, validConfig);        Files.writeString(configFile, validConfig);



        // Mock the handler's behavior        // Mock the handler's behavior

        String expectedResult = "✅ Configuration is valid";        String expectedResult = "✅ Configuration is valid";

        when(configurationHandler.handleValidateConfig(anyString()))        when(configurationHandler.handleValidateConfig(anyString()))

            .thenReturn(expectedResult);            .thenReturn(expectedResult);



        String result = documentorCommands.validateConfig(configFile.toString());        String result = documentorCommands.validateConfig(configFile.toString());

                

        assertNotNull(result);        assertNotNull(result);

        assertTrue(result.contains("✅"));        assertTrue(result.contains("✅"));

                

        verify(configurationHandler).handleValidateConfig(anyString());        verify(configurationHandler).handleValidateConfig(anyString());

    }    }



    @Test    @Test

    void testValidateConfigInvalidPath() {    void testValidateConfigInvalidPath() {

        String result = documentorCommands.validateConfig("non-existent-config.json");        String result = documentorCommands.validateConfig("non-existent-config.json");

                

        assertTrue(result.contains("❌"));        assertTrue(result.contains("❌"));

        assertTrue(result.contains("Configuration file does not exist"));        assertTrue(result.contains("Configuration file does not exist"));

                

        verifyNoInteractions(configurationHandler);        verifyNoInteractions(configurationHandler);

    }    }



    @Test    @Test

    void testShowStatusCommand() {    void testShowStatusCommand() {

        // Mock the handler's behavior        // Mock the handler's behavior

        String expectedResult = "🔍 Application Status\n✅ Services initialized";        String expectedResult = "🔍 Application Status\n✅ Services initialized";

        when(statusHandler.handleShowStatus(anyString(), anyString()))        when(statusHandler.handleShowStatus(anyString(), anyString()))

            .thenReturn(expectedResult);            .thenReturn(expectedResult);



        String result = documentorCommands.showStatus();        String result = documentorCommands.status();

                

        assertNotNull(result);        assertNotNull(result);

        assertTrue(result.contains("🔍"));        assertTrue(result.contains("🔍"));

                

        verify(statusHandler).handleShowStatus(anyString(), anyString());        verify(statusHandler).handleShowStatus(anyString(), anyString());

    }    }

}

    @Test

    void testInfoCommand() {    @Test

        String result = documentorCommands.info();    void testAnalyzeProjectNonExistentPath() {

                String result = documentorCommands.analyzeProject("non-existent-path", "config.json", false, "");

        assertNotNull(result);        

        assertTrue(result.contains("📋"));        assertTrue(result.contains("❌"));

        assertTrue(result.contains("Documentor Information"));        assertTrue(result.contains("Project path does not exist"));

    }        

        verifyNoInteractions(codeAnalysisService);

    @Test        verifyNoInteractions(documentationService);

    void testQuickStartCommand() {    }

        String result = documentorCommands.quickStart();

            @Test

        assertNotNull(result);    void testScanProjectNonExistentPath() {

        assertTrue(result.contains("🚀"));        String result = documentorCommands.scanProject("non-existent-path");

        assertTrue(result.contains("Quick Start Guide"));        

    }        assertTrue(result.contains("❌"));

}        assertTrue(result.contains("Project path does not exist"));
        
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