package com.documentor.cli.handlers;

import com.documentor.config.DocumentorConfig;
import com.documentor.constants.ApplicationConstants;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 📋 Handler for application status and information commands
 */
@Component
public final class StatusCommandHandler {

    private final DocumentorConfig documentorConfig;

    public StatusCommandHandler(final DocumentorConfig documentorConfigParam) {
        this.documentorConfig = documentorConfigParam;
    }

    public String handleShowStatus(final String currentProjectPath,
                                  final String currentConfigPath) {
        StringBuilder status = new StringBuilder();
        status.append("📋 Documentor Status\n");
        status.append("━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");

        appendProjectInfo(status, currentProjectPath);
        appendConfigInfo(status, currentConfigPath);
        appendLlmModelsInfo(status);
        appendOutputSettingsInfo(status);
        appendAnalysisSettingsInfo(status);

        status.append("\n💡 Tip: Use 'analyze --project-path <path>' to set "
                + "a new current project");
        return status.toString();
    }

    public String handleShowInfo() {
        StringBuilder info = new StringBuilder();
        info.append("📚 Documentor - AI-Powered Code Documentation Generator\n");
        info.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
                + "\n\n");

        info.append("🔍 Supported File Types:\n");
        info.append("   • Java (.java) - Full AST analysis with JavaParser\n");
        info.append("   • Python (.py) - AST analysis with "
                + "Jython/regex patterns\n\n");

        info.append("🤖 LLM Integration:\n");
        info.append("   • OpenAI GPT models (GPT-3.5, GPT-4, etc.)\n");
        info.append("   • Ollama local models (Llama, CodeLlama, etc.)\n");
        info.append("   • Custom API endpoints\n\n");

        info.append("📊 Generated Documentation:\n");
        info.append("   • API documentation with descriptions\n");
        info.append("   • Usage examples for public methods\n");
        info.append("   • Unit test templates\n");
        info.append("   • Mermaid class diagrams\n\n");

        info.append("⚙️ Configuration:\n");
        info.append("   • JSON-based configuration files\n");
        info.append("   • Multiple LLM model support\n");
        info.append("   • Customizable output formats\n");
        info.append("   • Analysis filters and exclusions\n");

        return info.toString();
    }

    public String handleQuickStart() {
        StringBuilder guide = new StringBuilder();
        guide.append("🚀 Quick Start Guide\n");
        guide.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
                + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                .append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
                + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                .append("\n\n");

        guide.append("1️⃣ Configure LLM Models (config.json):\n");
        guide.append("   Create a config.json file with your preferred "
                + "LLM settings\n\n");

        guide.append("2️⃣ Analyze Your Project:\n");
        guide.append("   analyze --project-path ./my-project\n\n");

        guide.append("3️⃣ Generate Documentation:\n");
        guide.append("   analyze --project-path ./src --generate-mermaid "
                + "true\n\n");

        guide.append("4️⃣ Validate Configuration:\n");
        guide.append("   validate-config --config ./config.json\n\n");

        guide.append("🔧 Available Commands:\n");
        guide.append("   • analyze    - Generate full documentation\n");
        guide.append("   • scan       - Analyze without generating docs\n");
        guide.append("   • status     - Show current application state\n");
        guide.append("   • info       - Show supported features\n");
        guide.append("   • quick-start - Show this guide\n");

        return guide.toString();
    }

    private void appendProjectInfo(final StringBuilder status,
                                  final String currentProjectPath) {
        status.append("📁 Current Project:\n");
        if (currentProjectPath != null) {
            Path projectPath = Paths.get(currentProjectPath);
            status.append("   Path: ").append(currentProjectPath).append("\n");
            status.append("   Exists: ").append(Files.exists(projectPath)
                    ? "✅ Yes" : "❌ No").append("\n");
            if (Files.exists(projectPath)) {
                status.append("   Type: ").append(Files.isDirectory(projectPath)
                        ? "Directory" : "File").append("\n");
            }
        } else {
            status.append("   No project currently selected\n");
        }
        status.append("\n");
    }

    private void appendConfigInfo(final StringBuilder status,
                                 final String currentConfigPath) {
        status.append("⚙️ Configuration:\n");
        if (currentConfigPath != null) {
            status.append("   Config File: ").append(currentConfigPath)
                    .append("\n");
            Path configPath = Paths.get(currentConfigPath);
            status.append("   Config Exists: ").append(Files.exists(configPath)
                    ? "✅ Yes" : "❌ No").append("\n");
        } else {
            status.append("   Using default configuration\n");
        }
        status.append("\n");
    }

    private void appendLlmModelsInfo(final StringBuilder status) {
        status.append("🤖 LLM Models:\n");
        if (documentorConfig != null && documentorConfig.llmModels() != null) {
            status.append("   Total Models: ")
                    .append(documentorConfig.llmModels().size()).append("\n");
            for (int i = 0; i < documentorConfig.llmModels().size(); i++) {
                var model = documentorConfig.llmModels().get(i);
                status.append("   ").append(i + 1).append(". ")
                        .append(model.name()).append("\n");
                status.append("      API Key: ").append(model.apiKey() != null
                        && !model.apiKey().isEmpty()
                    ? (model.apiKey().length()
                            > ApplicationConstants.API_KEY_PREVIEW_LENGTH
                        ? model.apiKey().substring(0,
                                ApplicationConstants.API_KEY_PREVIEW_LENGTH)
                                + "..."
                        : "***")
                    : "Not set").append("\n");
                if (model.baseUrl() != null && !model.baseUrl().isEmpty()) {
                    status.append("      Base URL: ").append(model.baseUrl())
                            .append("\n");
                }
                status.append("      Max Tokens: ").append(model.maxTokens())
                        .append("\n");
                status.append("      Timeout: ").append(model.timeoutSeconds())
                        .append("s\n");
                if (i < documentorConfig.llmModels().size() - 1) {
                    status.append("\n");
                }
            }
        } else {
            status.append("   No LLM models configured\n");
        }
        status.append("\n");
    }

    private void appendOutputSettingsInfo(final StringBuilder status) {
        status.append("📤 Output Settings:\n");
        if (documentorConfig != null
                && documentorConfig.outputSettings() != null) {
            var outputSettings = documentorConfig.outputSettings();
            status.append("   Output Path: ")
                    .append(outputSettings.outputPath())
                    .append("\n");
            status.append("   Format: ").append(outputSettings.format())
                    .append("\n");
            status.append("   Include Icons: ")
                    .append(outputSettings.includeIcons()
                    ? "✅ Yes" : "❌ No")
                    .append("\n");
            status.append("   Generate Unit Tests: ")
                    .append(outputSettings.generateUnitTests()
                            ? "✅ Yes" : "❌ No").append("\n");
            status.append("   Target Coverage: ")
                    .append(String.format("%.1f%%",
                outputSettings.targetCoverage()
                        * ApplicationConstants.PERCENTAGE_MULTIPLIER))
                    .append("\n");
        } else {
            status.append("   Using default output settings\n");
        }
        status.append("\n");
    }

    private void appendAnalysisSettingsInfo(final StringBuilder status) {
        status.append("🔍 Analysis Settings:\n");
        if (documentorConfig != null
                && documentorConfig.analysisSettings() != null) {
            var analysisSettings = documentorConfig.analysisSettings();
            status.append("   Include Private Members: ")
                    .append(analysisSettings.includePrivateMembers()
                            ? "✅ Yes" : "❌ No").append("\n");
            status.append("   Max Threads: ")
                    .append(analysisSettings.maxThreads())
                    .append("\n");
            status.append("   Supported Languages: ")
                    .append(String.join(", ",
                            analysisSettings.supportedLanguages()))
                    .append("\n");
            status.append("   Exclude Patterns: ")
                    .append(String.join(", ",
                            analysisSettings.excludePatterns())).append("\n");
        } else {
            status.append("   Using default analysis settings\n");
        }
    }
}
