package com.documentor.cli.handlers;

import com.documentor.config.DocumentorConfig;
import com.documentor.config.model.AnalysisSettings;
import com.documentor.config.model.LlmModelConfig;
import com.documentor.config.model.OutputSettings;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class StatusCommandHandlerTest {

    @Test
    void showStatusIncludesProjectAndConfigInfo() {
        LlmModelConfig model = new LlmModelConfig("m", "openai", "http://x", "apikey123456", 200, 20);
        OutputSettings output = new OutputSettings("out", "md", true, true);
        AnalysisSettings analysis = new AnalysisSettings(true, 2, List.of("**/*.java"), List.of("**/test/**"));

        DocumentorConfig cfg = new DocumentorConfig(List.of(model), output, analysis);
        StatusCommandHandler handler = new StatusCommandHandler(cfg);

        String status = handler.handleShowStatus(null, null);
        assertTrue(status.contains("Documentor Status"));
        assertTrue(status.contains("LLM Models") || status.contains("No LLM models"));
    }

    @Test
    void showInfoAndQuickStartReturnNonEmpty() {
        StatusCommandHandler handler = new StatusCommandHandler(null);

        String info = handler.handleShowInfo();
        String quick = handler.handleQuickStart();

        assertTrue(info.contains("Supported File Types"));
        assertTrue(quick.contains("Quick Start Guide"));
    }

    @Test
    void showStatus_withExistingProjectDirectory(@TempDir Path tempDir) throws IOException {
        // Create a test directory
        Path projectDir = Files.createDirectory(tempDir.resolve("test-project"));
        
        StatusCommandHandler handler = new StatusCommandHandler(null);
        String status = handler.handleShowStatus(projectDir.toString(), null);
        
        assertTrue(status.contains("📁 Current Project:"));
        assertTrue(status.contains("Path: " + projectDir.toString()));
        assertTrue(status.contains("Exists: ✅ Yes"));
        assertTrue(status.contains("Type: Directory"));
    }

    @Test
    void showStatus_withExistingProjectFile(@TempDir Path tempDir) throws IOException {
        // Create a test file
        Path projectFile = Files.createFile(tempDir.resolve("test-file.txt"));
        
        StatusCommandHandler handler = new StatusCommandHandler(null);
        String status = handler.handleShowStatus(projectFile.toString(), null);
        
        assertTrue(status.contains("📁 Current Project:"));
        assertTrue(status.contains("Path: " + projectFile.toString()));
        assertTrue(status.contains("Exists: ✅ Yes"));
        assertTrue(status.contains("Type: File"));
    }

    @Test
    void showStatus_withNonExistentProjectPath() {
        StatusCommandHandler handler = new StatusCommandHandler(null);
        String status = handler.handleShowStatus("/non/existent/path", null);
        
        assertTrue(status.contains("📁 Current Project:"));
        assertTrue(status.contains("Path: /non/existent/path"));
        assertTrue(status.contains("Exists: ❌ No"));
    }

    @Test
    void showStatus_withNullProjectPath() {
        StatusCommandHandler handler = new StatusCommandHandler(null);
        String status = handler.handleShowStatus(null, null);
        
        assertTrue(status.contains("📁 Current Project:"));
        assertTrue(status.contains("No project currently selected"));
    }

    @Test
    void showStatus_withExistingConfigFile(@TempDir Path tempDir) throws IOException {
        // Create a test config file
        Path configFile = Files.createFile(tempDir.resolve("config.json"));
        
        StatusCommandHandler handler = new StatusCommandHandler(null);
        String status = handler.handleShowStatus(null, configFile.toString());
        
        assertTrue(status.contains("⚙️ Configuration:"));
        assertTrue(status.contains("Config File: " + configFile.toString()));
        assertTrue(status.contains("Config Exists: ✅ Yes"));
    }

    @Test
    void showStatus_withNonExistentConfigFile() {
        StatusCommandHandler handler = new StatusCommandHandler(null);
        String status = handler.handleShowStatus(null, "/non/existent/config.json");
        
        assertTrue(status.contains("⚙️ Configuration:"));
        assertTrue(status.contains("Config File: /non/existent/config.json"));
        assertTrue(status.contains("Config Exists: ❌ No"));
    }

    @Test
    void showStatus_withNullConfigPath() {
        StatusCommandHandler handler = new StatusCommandHandler(null);
        String status = handler.handleShowStatus(null, null);
        
        assertTrue(status.contains("⚙️ Configuration:"));
        assertTrue(status.contains("Using default configuration"));
    }

    @Test
    void showStatus_withNullDocumentorConfig() {
        StatusCommandHandler handler = new StatusCommandHandler(null);
        String status = handler.handleShowStatus(null, null);
        
        assertTrue(status.contains("🤖 LLM Models:"));
        assertTrue(status.contains("No LLM models configured"));
    }

    @Test
    void showStatus_withEmptyLlmModelsList() {
        DocumentorConfig cfg = new DocumentorConfig(Collections.emptyList(), null, null);
        StatusCommandHandler handler = new StatusCommandHandler(cfg);
        String status = handler.handleShowStatus(null, null);
        
        assertTrue(status.contains("🤖 LLM Models:"));
        assertTrue(status.contains("Total Models: 0"));
    }

    @Test
    void showStatus_withMultipleLlmModels() {
        LlmModelConfig model1 = new LlmModelConfig("model1", "openai", "http://api1", "key1", 100, 10);
        LlmModelConfig model2 = new LlmModelConfig("model2", "ollama", "http://api2", "key2", 200, 20);
        
        DocumentorConfig cfg = new DocumentorConfig(List.of(model1, model2), null, null);
        StatusCommandHandler handler = new StatusCommandHandler(cfg);
        String status = handler.handleShowStatus(null, null);
        
        assertTrue(status.contains("🤖 LLM Models:"));
        assertTrue(status.contains("Total Models: 2"));
        assertTrue(status.contains("1. model1"));
        assertTrue(status.contains("2. model2"));
        assertTrue(status.contains("API Key: ***")); // Short keys show as ***
    }

    @Test
    void showStatus_withLlmModelWithLongApiKey() {
        // Create a model with long API key to test truncation
        LlmModelConfig model = new LlmModelConfig("model", "openai", "http://api", "verylongapikeystring123456", 100, 10);
        
        DocumentorConfig cfg = new DocumentorConfig(List.of(model), null, null);
        StatusCommandHandler handler = new StatusCommandHandler(cfg);
        String status = handler.handleShowStatus(null, null);
        
        assertTrue(status.contains("🤖 LLM Models:"));
        assertTrue(status.contains("API Key: verylongap...")); // Long keys get truncated
    }

    @Test
    void showStatus_withLlmModelWithEmptyApiKey() {
        LlmModelConfig model = new LlmModelConfig("model", "openai", "http://api", "", 100, 10);
        
        DocumentorConfig cfg = new DocumentorConfig(List.of(model), null, null);
        StatusCommandHandler handler = new StatusCommandHandler(cfg);
        String status = handler.handleShowStatus(null, null);
        
        assertTrue(status.contains("🤖 LLM Models:"));
        assertTrue(status.contains("API Key: Not set"));
    }

    @Test
    void showStatus_withNullOutputSettings() {
        DocumentorConfig cfg = new DocumentorConfig(Collections.emptyList(), null, null);
        StatusCommandHandler handler = new StatusCommandHandler(cfg);
        String status = handler.handleShowStatus(null, null);
        
        assertTrue(status.contains("📤 Output Settings:"));
        assertTrue(status.contains("Using default output settings"));
    }

    @Test
    void showStatus_withCompleteOutputSettings() {
        OutputSettings output = new OutputSettings("./docs", "markdown", true, false);
        DocumentorConfig cfg = new DocumentorConfig(Collections.emptyList(), output, null);
        StatusCommandHandler handler = new StatusCommandHandler(cfg);
        String status = handler.handleShowStatus(null, null);
        
        assertTrue(status.contains("📤 Output Settings:"));
        assertTrue(status.contains("Output Path: ./docs"));
        assertTrue(status.contains("Format: markdown"));
        assertTrue(status.contains("Include Icons: ✅ Yes")); // Always returns true
        assertTrue(status.contains("Generate Unit Tests: ✅ Yes")); // Always returns true
        assertTrue(status.contains("Target Coverage:")); // Contains percentage
    }

    @Test
    void showStatus_withNullAnalysisSettings() {
        // DocumentorConfig constructor creates default AnalysisSettings even if null is passed
        DocumentorConfig cfg = new DocumentorConfig(Collections.emptyList(), null, null);
        StatusCommandHandler handler = new StatusCommandHandler(cfg);
        String status = handler.handleShowStatus(null, null);
        
        assertTrue(status.contains("🔍 Analysis Settings:"));
        assertTrue(status.contains("Include Private Members: ❌ No")); // Default is false
        assertTrue(status.contains("Supported Languages: java, python")); // Default languages
        assertTrue(status.contains("Exclude Patterns:")); // Has default exclude patterns
    }

    @Test
    void showStatus_withCompleteAnalysisSettings() {
        AnalysisSettings analysis = new AnalysisSettings(false, 4, List.of("java", "python"), List.of("*.class", "*.pyc"));
        DocumentorConfig cfg = new DocumentorConfig(Collections.emptyList(), null, analysis);
        StatusCommandHandler handler = new StatusCommandHandler(cfg);
        String status = handler.handleShowStatus(null, null);
        
        assertTrue(status.contains("🔍 Analysis Settings:"));
        assertTrue(status.contains("Include Private Members: ❌ No"));
        assertTrue(status.contains("Max Threads: 4"));
        assertTrue(status.contains("Supported Languages: java, python"));
        assertTrue(status.contains("Exclude Patterns: *.class, *.pyc"));
    }
}
