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

    // Test constants for magic number violations
    private static final int MAX_TOKENS = 200;
    private static final int TIMEOUT_SECONDS = 20;
    private static final int TIMEOUT_SECONDS_SHORTER = 10;
    private static final int MAX_TOKENS_LOWER = 100;
    private static final int THREAD_COUNT = 4;

    @Test
    void showStatusIncludesProjectAndConfigInfo() {
        LlmModelConfig model = new LlmModelConfig("m", "openai",
            "http://x", "apikey123456", MAX_TOKENS, TIMEOUT_SECONDS);
        OutputSettings output = new OutputSettings("out", "md",
            true, false, true);
        AnalysisSettings analysis = new AnalysisSettings(true,
            2, List.of("**/*.java"), List.of("**/test/**"));

        DocumentorConfig cfg =
            new DocumentorConfig(List.of(model), output, analysis);
        StatusCommandHandler handler = new StatusCommandHandler(cfg);

        String status = handler.handleShowStatus(null, null);
        assertTrue(status.contains("Documentor Status"));
        assertTrue(status.contains("LLM Models")
            || status.contains("No LLM models"));
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
    void showStatusWithExistingProjectDirectory(@TempDir final Path tempDir)
        throws IOException {
        // Create a test directory
        Path projectDir = Files.createDirectory(tempDir.resolve(
            "test-project"));

        StatusCommandHandler handler = new StatusCommandHandler(null);
        String status = handler.handleShowStatus(projectDir.toString(), null);

        assertTrue(status.contains("📁 Current Project:"));
        assertTrue(status.contains("Path: " + projectDir.toString()));
        assertTrue(status.contains("Exists: ✅ Yes"));
        assertTrue(status.contains("Type: Directory"));
    }

    @Test
    void showStatusWithExistingProjectFile(@TempDir final Path tempDir)
        throws IOException {
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
    void showStatusWithNonExistentProjectPath() {
        StatusCommandHandler handler = new StatusCommandHandler(null);
        String status = handler.handleShowStatus("/non/existent/path", null);

        assertTrue(status.contains("📁 Current Project:"));
        assertTrue(status.contains("Path: /non/existent/path"));
        assertTrue(status.contains("Exists: ❌ No"));
    }

    @Test
    void showStatusWithNullProjectPath() {
        StatusCommandHandler handler = new StatusCommandHandler(null);
        String status = handler.handleShowStatus(null, null);

        assertTrue(status.contains("📁 Current Project:"));
        assertTrue(status.contains("No project currently selected"));
    }

    @Test
    void showStatusWithExistingConfigFile(@TempDir final Path tempDir)
        throws IOException {
        // Create a test config file
        Path configFile = Files.createFile(tempDir.resolve("config.json"));

        StatusCommandHandler handler = new StatusCommandHandler(null);
        String status = handler.handleShowStatus(null, configFile.toString());

        assertTrue(status.contains("⚙️ Configuration:"));
        assertTrue(status.contains("Config File: " + configFile.toString()));
        assertTrue(status.contains("Config Exists: ✅ Yes"));
    }

    @Test
    void showStatusWithNonExistentConfigFile() {
        StatusCommandHandler handler = new StatusCommandHandler(null);
        String status = handler.handleShowStatus(null,
            "/non/existent/config.json");

        assertTrue(status.contains("⚙️ Configuration:"));
        assertTrue(status.contains("Config File: /non/existent/config.json"));
        assertTrue(status.contains("Config Exists: ❌ No"));
    }

    @Test
    void showStatusWithNullConfigPath() {
        StatusCommandHandler handler = new StatusCommandHandler(null);
        String status = handler.handleShowStatus(null, null);

        assertTrue(status.contains("⚙️ Configuration:"));
        assertTrue(status.contains("Using default configuration"));
    }

    @Test
    void showStatusWithNullDocumentorConfig() {
        StatusCommandHandler handler = new StatusCommandHandler(null);
        String status = handler.handleShowStatus(null, null);

        assertTrue(status.contains("🤖 LLM Models:"));
        assertTrue(status.contains("No LLM models configured"));
    }

    @Test
    void showStatusWithEmptyLlmModelsList() {
        DocumentorConfig cfg = new DocumentorConfig(Collections.emptyList(),
            null, null);
        StatusCommandHandler handler = new StatusCommandHandler(cfg);
        String status = handler.handleShowStatus(null,
            null);

        assertTrue(status.contains("🤖 LLM Models:"));
        assertTrue(status.contains("Total Models: 0"));
    }

    @Test
    void showStatusWithMultipleLlmModels() {
        LlmModelConfig model1 = new LlmModelConfig("model1",
            "openai", "http://api1", "key1", MAX_TOKENS_LOWER,
            TIMEOUT_SECONDS_SHORTER);
        LlmModelConfig model2 = new LlmModelConfig("model2",
            "ollama", "http://api2", "key2",
            MAX_TOKENS, TIMEOUT_SECONDS);

        DocumentorConfig cfg = new DocumentorConfig(List.of(model1, model2),
            null, null);
        StatusCommandHandler handler = new StatusCommandHandler(cfg);
        String status = handler.handleShowStatus(null,
            null);

        assertTrue(status.contains("🤖 LLM Models:"));
        assertTrue(status.contains("Total Models: 2"));
        assertTrue(status.contains("1. model1"));
        assertTrue(status.contains("2. model2"));
        assertTrue(status.contains("API Key: ***")); // Short keys show as ***
    }

    @Test
    void showStatusWithLlmModelWithLongApiKey() {
        // Create a model with long API key to test truncation
        LlmModelConfig model = new LlmModelConfig("model", "openai",
            "http://api", "verylongapikeystring123456", MAX_TOKENS_LOWER,
            TIMEOUT_SECONDS_SHORTER);

        DocumentorConfig cfg = new DocumentorConfig(List.of(model), null, null);
        StatusCommandHandler handler = new StatusCommandHandler(cfg);
        String status = handler.handleShowStatus(null, null);

        assertTrue(status.contains("🤖 LLM Models:"));
        // Long keys get truncated
        assertTrue(status.contains("API Key: verylongap..."));
    }

    @Test
    void showStatusWithLlmModelWithEmptyApiKey() {
        LlmModelConfig model = new LlmModelConfig("model", "openai",
            "http://api", "", MAX_TOKENS_LOWER, TIMEOUT_SECONDS_SHORTER);

        DocumentorConfig cfg = new DocumentorConfig(List.of(model), null,
            null);
        StatusCommandHandler handler = new StatusCommandHandler(cfg);
        String status = handler.handleShowStatus(null, null);

        assertTrue(status.contains("🤖 LLM Models:"));
        assertTrue(status.contains("API Key: Not set"));
    }

    @Test
    void showStatusWithNullOutputSettings() {
        DocumentorConfig cfg = new DocumentorConfig(Collections.emptyList(),
            null, null);
        StatusCommandHandler handler = new StatusCommandHandler(cfg);
        String status = handler.handleShowStatus(null, null);

        assertTrue(status.contains("📤 Output Settings:"));
        assertTrue(status.contains("Using default output settings"));
    }

    @Test
    void showStatusWithCompleteOutputSettings() {
        OutputSettings output = new OutputSettings("./docs", "markdown",
            true, false, false);
        DocumentorConfig cfg = new DocumentorConfig(Collections.emptyList(),
            output, null);
        StatusCommandHandler handler = new StatusCommandHandler(cfg);
        String status = handler.handleShowStatus(null,
            null);

        assertTrue(status.contains("📤 Output Settings:"));
        assertTrue(status.contains("Output Path: ./docs"));
        assertTrue(status.contains("Format: markdown"));
        // Always returns true
        assertTrue(status.contains("Include Icons: ✅ Yes"));
        // Always returns true
        assertTrue(status.contains("Generate Unit Tests: ✅ Yes"));
        // Contains percentage
        assertTrue(status.contains("Target Coverage:"));
    }

    @Test
    void showStatusWithNullAnalysisSettings() {
        // DocumentorConfig constructor creates default
        // AnalysisSettings even if null is passed
        DocumentorConfig cfg = new DocumentorConfig(Collections.emptyList(),
            null, null);
        StatusCommandHandler handler = new StatusCommandHandler(cfg);
        String status = handler.handleShowStatus(null, null);

        assertTrue(status.contains("🔍 Analysis Settings:"));
        // Default is now true
        assertTrue(status.contains("Include Private Members: ✅ Yes"));
        // Default languages
        assertTrue(status.contains("Supported Languages: java, python"));
        // Has default exclude patterns
        assertTrue(status.contains("Exclude Patterns:"));
    }

    @Test
    void showStatusWithCompleteAnalysisSettings() {
        AnalysisSettings analysis = new AnalysisSettings(false, THREAD_COUNT,
                List.of("java", "python"), List.of("*.class", "*.pyc"));
        DocumentorConfig cfg = new DocumentorConfig(Collections.emptyList(),
            null, analysis);
        StatusCommandHandler handler = new StatusCommandHandler(cfg);
        String status = handler.handleShowStatus(null, null);

        assertTrue(status.contains("🔍 Analysis Settings:"));
        assertTrue(status.contains("Include Private Members: ❌ No"));
        assertTrue(status.contains("Max Threads: " + THREAD_COUNT));
        assertTrue(status.contains("Supported Languages: java, python"));
        assertTrue(status.contains("Exclude Patterns: *.class, *.pyc"));
    }

    @Test
    void showStatusWithLlmModelWithNullApiKey() {
        // Test branch where model.apiKey() is null - should show "Not set"
        LlmModelConfig model = new LlmModelConfig("model", "openai",
            "http://api", null, MAX_TOKENS_LOWER, TIMEOUT_SECONDS_SHORTER);
        DocumentorConfig cfg = new DocumentorConfig(List.of(model), null,
            null);
        StatusCommandHandler handler = new StatusCommandHandler(cfg);
        String status = handler.handleShowStatus(null, null);

        assertTrue(status.contains("🤖 LLM Models:"));
        assertTrue(status.contains("1. model"));
        // Should hit the null apiKey branch
        assertTrue(status.contains("API Key: Not set"));
    }
}
