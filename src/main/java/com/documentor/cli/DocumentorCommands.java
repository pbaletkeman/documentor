package com.documentor.cli;

import com.documentor.cli.handlers.ConfigurationCommandHandler;
import com.documentor.cli.handlers.ProjectAnalysisCommandHandler;
import com.documentor.cli.handlers.StatusCommandHandler;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

/**
 * ðŸ–¥ï¸ Command Line Interface for Documentor
 *
 * Provides interactive commands for analyzing projects and generating documentation.
 * Uses Spring Shell for a rich CLI experience with delegated command handlers.
 */
@ShellComponent
public class DocumentorCommands {

    private final ProjectAnalysisCommandHandler projectAnalysisHandler;
    private final StatusCommandHandler statusHandler;
    private final ConfigurationCommandHandler configurationHandler;

    // Track current state
    private String currentProjectPath;
    private String currentConfigPath;

    public DocumentorCommands(
            final ProjectAnalysisCommandHandler projectAnalysisHandler,
            final StatusCommandHandler statusHandler,
            final ConfigurationCommandHandler configurationHandler) {
        this.projectAnalysisHandler = projectAnalysisHandler;
        this.statusHandler = statusHandler;
        this.configurationHandler = configurationHandler;
    }

    /**
     * ðŸš€ Main command to analyze a project and generate documentation
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
            final String configPath,
            @ShellOption(value = "--generate-mermaid",
                    help = "Generate Mermaid class diagrams",
                    defaultValue = "false")
            final boolean generateMermaid,
            @ShellOption(value = "--mermaid-output",
                    help = "Output directory for Mermaid diagrams (defaults to same directory as source files)",
                    defaultValue = "")
            final String mermaidOutput) {

        // Update current state
        this.currentProjectPath = projectPath;
        this.currentConfigPath = configPath;

        return projectAnalysisHandler.handleAnalyzeProject(projectPath, configPath, generateMermaid, mermaidOutput);
    }

    /**
     * ðŸ“Š Scan project without generating documentation
     */
    @ShellMethod(value = "Analyze a project and show statistics",
            key = {"scan", "analyze-only"})
    public String scanProject(
            @ShellOption(value = "--project-path",
                    help = "Path to the project directory to analyze")
            final String projectPath) {

        // Update current state
        this.currentProjectPath = projectPath;

        return projectAnalysisHandler.handleScanProject(projectPath);
    }

    /**
     * âš™ï¸ Validate configuration file
     */
    @ShellMethod(value = "Validate configuration file",
            key = {"validate-config", "check-config"})
    public String validateConfig(
            @ShellOption(value = "--config",
                    help = "Path to configuration JSON file to validate",
                    defaultValue = "config.json")
            final String configPath) {

        return configurationHandler.handleValidateConfig(configPath);
    }

    /**
     * ðŸ“š Show supported file types and features
     */
    @ShellMethod(value = "Show supported file types and features", key = {"info", "help-extended"})
    public String showInfo() {
        return statusHandler.handleShowInfo();
    }

    /**
     * ðŸš€ Show quick start guide
     */
    @ShellMethod(value = "Show quick start guide", key = {"quick-start", "getting-started"})
    public String quickStart() {
        return statusHandler.handleQuickStart();
    }

    /**
     * ðŸ“‹ Show current application status
     */
    @ShellMethod(value = "Show current application status", key = {"status", "current"})
    public String showStatus() {
        return statusHandler.handleShowStatus(currentProjectPath, currentConfigPath);
    }
}
