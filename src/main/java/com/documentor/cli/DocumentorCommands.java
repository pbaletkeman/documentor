package com.documentor.cli;

import com.documentor.config.DocumentorConfig;
import com.documentor.model.ProjectAnalysis;
import com.documentor.service.CodeAnalysisService;
import com.documentor.service.DocumentationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

/**
 * 🖥️ Command Line Interface for Documentor
 *
 * Provides interactive commands for analyzing projects and generating documentation.
 * Uses Spring Shell for a rich CLI experience.
 */
@ShellComponent
public class DocumentorCommands {

    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentorCommands.class);

    private final CodeAnalysisService codeAnalysisService;
    private final DocumentationService documentationService;
    private final DocumentorConfig documentorConfig;
    
    // Track current state
    private String currentProjectPath;
    private String currentConfigPath;

    public DocumentorCommands(
            final CodeAnalysisService codeAnalysisServiceParam,
            final DocumentationService documentationServiceParam,
            final DocumentorConfig documentorConfigParam) {
        this.codeAnalysisService = codeAnalysisServiceParam;
        this.documentationService = documentationServiceParam;
        this.documentorConfig = documentorConfigParam;
    }

    /**
     * 🚀 Main command to analyze a project and generate documentation
     */
    @ShellMethod(value = "Analyze a project and generate documentation",
            key = {"analyze", "generate"})
    public String analyzeProject(
            @ShellOption(value = "--project-path",
                    help = "Path to the project directory to analyze")
            final String projectPath,
            @ShellOption(value = "--config",
                    help = "Path to configuration JSON file",
                    defaultValue = "config.json")
            final String configPath) {

        try {
            LOGGER.info("🚀 Starting analysis of project: {}", projectPath);

            // Update current state
            this.currentProjectPath = projectPath;
            this.currentConfigPath = configPath;

            // Validate project path
            Path project = Paths.get(projectPath);
            if (!Files.exists(project) || !Files.isDirectory(project)) {
                return "❌ Error: Project path does not exist or is not a directory: " + projectPath;
            }

            // Analyze project
            CompletableFuture<ProjectAnalysis> analysisFuture = codeAnalysisService.analyzeProject(project);
            ProjectAnalysis analysis = analysisFuture.join();

            // Generate documentation
            CompletableFuture<String> docFuture = documentationService.generateDocumentation(analysis);
            String outputPath = docFuture.join();

            return String.format("✅ Analysis complete! Documentation generated at: %s\n%s",
                outputPath, analysis.getStats().getFormattedSummary());

        } catch (Exception e) {
            LOGGER.error("❌ Error during analysis: {}", e.getMessage(), e);
            return "❌ Error: " + e.getMessage();
        }
    }

    /**
     * 🔍 Command to only analyze a project without generating documentation
     */
    @ShellMethod(value = "Analyze a project and show statistics",
            key = {"scan", "analyze-only"})
    public String scanProject(
            @ShellOption(value = "--project-path",
                    help = "Path to the project directory to analyze")
            final String projectPath) {

        try {
            LOGGER.info("🔍 Scanning project: {}", projectPath);

            // Update current state
            this.currentProjectPath = projectPath;

            Path project = Paths.get(projectPath);
            if (!Files.exists(project) || !Files.isDirectory(project)) {
                return "❌ Error: Project path does not exist or is not a directory: " + projectPath;
            }

            CompletableFuture<ProjectAnalysis> analysisFuture = codeAnalysisService.analyzeProject(project);
            ProjectAnalysis analysis = analysisFuture.join();

            StringBuilder result = new StringBuilder();
            result.append("📊 Project Analysis Results\n");
            result.append("━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            result.append(analysis.getStats().getFormattedSummary()).append("\n\n");

            result.append("📁 Files analyzed:\n");
            analysis.getElementsByFile().keySet().stream()
                    .sorted()
                    .forEach(file -> result.append("  - ").append(file).append("\n"));

            return result.toString();

        } catch (Exception e) {
            LOGGER.error("❌ Error during scan: {}", e.getMessage(), e);
            return "❌ Error: " + e.getMessage();
        }
    }

    /**
     * ⚙️ Command to validate configuration
     */
    @ShellMethod(value = "Validate configuration file",
            key = {"validate-config", "check-config"})
    public String validateConfig(
            @ShellOption(value = "--config",
                    help = "Path to configuration JSON file",
                    defaultValue = "config.json")
            final String configPath) {

        try {
            Path config = Paths.get(configPath);
            if (!Files.exists(config)) {
                return "❌ Configuration file not found: " + configPath;
            }

            // Basic validation - check if file is readable and has valid JSON structure
            String content = Files.readString(config);
            if (content.trim().isEmpty()) {
                return "❌ Configuration file is empty";
            }

            return String.format("✅ Configuration file is valid: %s\nSize: %d bytes",
                configPath, Files.size(config));

        } catch (Exception e) {
            LOGGER.error("❌ Error validating config: {}", e.getMessage());
            return "❌ Error validating configuration: " + e.getMessage();
        }
    }

    /**
     * 📋 Command to list supported file types
     */
    @ShellMethod(value = "Show supported file types and features", key = {"info", "help-extended"})
    public String showInfo() {
        return """
            📚 Documentor - AI-Powered Code Documentation Generator
            ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

            🔍 Supported Languages:
              • Java (.java) - Full AST parsing with JavaParser
              • Python (.py) - AST parsing with fallback to regex

            🤖 LLM Integration:
              • OpenAI GPT models (gpt-3.5-turbo, gpt-4, etc.)
              • Anthropic Claude models
              • Configurable endpoints and parameters
              • Multi-threaded processing for performance

            📝 Generated Documentation:
              • Comprehensive README.md
              • Individual element documentation
              • Usage examples with sample data
              • Unit test suggestions
              • API reference with signatures

            🛠️ Features:
              • Markdown output with icons
              • Configurable coverage targets
              • Pre-commit hooks support
              • Gradle project structure
              • Multi-threaded LLM processing

            📊 Analysis Coverage:
              • Public classes, interfaces, and enums
              • Public methods and functions
              • Public fields and variables
              • Documentation extraction
              • Parameter and return type analysis

            Use 'analyze --project-path <path>' to get started!
            """;
    }

    /**
     * 🎯 Command to show quick start guide
     */
    @ShellMethod(value = "Show quick start guide", key = {"quick-start", "getting-started"})
    public String quickStart() {
        return """
            🚀 Quick Start Guide
            ━━━━━━━━━━━━━━━━━━━━━━

            1️⃣ Create a configuration file (config.json):
               {
                 "llm_models": [{
                   "name": "gpt-3.5-turbo",
                   "api_key": "your-api-key-here"
                 }],
                 "output_settings": {
                   "output_path": "./docs"
                 }
               }

            2️⃣ Analyze your project:
               analyze --project-path /path/to/your/project

            3️⃣ View generated documentation:
               Check the ./docs directory for generated files

            💡 Pro Tips:
               • Use 'scan' command to preview analysis results
               • Configure multiple LLM models for better results
               • Set include_icons: false for plain text output
               • Adjust max_threads for better performance

            For detailed configuration options, see the README.md file.
            """;
    }

    /**
     * 📋 Shows the current status of the application
     */
    @ShellMethod(value = "Show current application status", key = {"status", "current"})
    public String showStatus() {
        StringBuilder status = new StringBuilder();
        status.append("📋 Documentor Status\n");
        status.append("━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");

        // Current Project Information
        status.append("📁 Current Project:\n");
        if (currentProjectPath != null) {
            Path projectPath = Paths.get(currentProjectPath);
            status.append("   Path: ").append(currentProjectPath).append("\n");
            status.append("   Exists: ").append(Files.exists(projectPath) ? "✅ Yes" : "❌ No").append("\n");
            if (Files.exists(projectPath)) {
                status.append("   Type: ").append(Files.isDirectory(projectPath) ? "Directory" : "File").append("\n");
            }
        } else {
            status.append("   No project currently selected\n");
        }
        status.append("\n");

        // Configuration Information
        status.append("⚙️ Configuration:\n");
        if (currentConfigPath != null) {
            status.append("   Config File: ").append(currentConfigPath).append("\n");
            Path configPath = Paths.get(currentConfigPath);
            status.append("   Config Exists: ").append(Files.exists(configPath) ? "✅ Yes" : "❌ No").append("\n");
        } else {
            status.append("   Using default configuration\n");
        }
        status.append("\n");

        // LLM Models Information
        status.append("🤖 LLM Models:\n");
        if (documentorConfig != null && documentorConfig.llmModels() != null) {
            status.append("   Total Models: ").append(documentorConfig.llmModels().size()).append("\n");
            for (int i = 0; i < documentorConfig.llmModels().size(); i++) {
                var model = documentorConfig.llmModels().get(i);
                status.append("   ").append(i + 1).append(". ").append(model.name()).append("\n");
                status.append("      API Key: ").append(model.apiKey() != null && !model.apiKey().isEmpty() ? 
                    (model.apiKey().length() > 10 ? model.apiKey().substring(0, 10) + "..." : "***") : "Not set").append("\n");
                if (model.endpoint() != null && !model.endpoint().isEmpty()) {
                    status.append("      Endpoint: ").append(model.endpoint()).append("\n");
                }
                status.append("      Max Tokens: ").append(model.maxTokens()).append("\n");
                status.append("      Temperature: ").append(model.temperature()).append("\n");
                if (i < documentorConfig.llmModels().size() - 1) {
                    status.append("\n");
                }
            }
        } else {
            status.append("   No LLM models configured\n");
        }
        status.append("\n");

        // Output Settings
        status.append("📤 Output Settings:\n");
        if (documentorConfig != null && documentorConfig.outputSettings() != null) {
            var outputSettings = documentorConfig.outputSettings();
            status.append("   Output Path: ").append(outputSettings.outputPath()).append("\n");
            status.append("   Format: ").append(outputSettings.format()).append("\n");
            status.append("   Include Icons: ").append(outputSettings.includeIcons() ? "✅ Yes" : "❌ No").append("\n");
            status.append("   Generate Unit Tests: ").append(outputSettings.generateUnitTests() ? "✅ Yes" : "❌ No").append("\n");
            status.append("   Target Coverage: ").append(String.format("%.1f%%", outputSettings.targetCoverage() * 100)).append("\n");
        } else {
            status.append("   Using default output settings\n");
        }
        status.append("\n");

        // Analysis Settings
        status.append("🔍 Analysis Settings:\n");
        if (documentorConfig != null && documentorConfig.analysisSettings() != null) {
            var analysisSettings = documentorConfig.analysisSettings();
            status.append("   Include Private Members: ").append(analysisSettings.includePrivateMembers() ? "✅ Yes" : "❌ No").append("\n");
            status.append("   Max Threads: ").append(analysisSettings.maxThreads()).append("\n");
            status.append("   Supported Languages: ").append(String.join(", ", analysisSettings.supportedLanguages())).append("\n");
            status.append("   Exclude Patterns: ").append(String.join(", ", analysisSettings.excludePatterns())).append("\n");
        } else {
            status.append("   Using default analysis settings\n");
        }

        status.append("\n💡 Tip: Use 'analyze --project-path <path>' to set a new current project");

        return status.toString();
    }
}
