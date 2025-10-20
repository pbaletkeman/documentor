package com.documentor.cli;

import com.documentor.cli.handlers.ConfigurationCommandHandler;
import com.documentor.cli.handlers.EnhancedProjectAnalysisHandler;
import com.documentor.cli.handlers.ProjectAnalysisCommandHandler;
import com.documentor.cli.handlers.ProjectAnalysisRequest;
import com.documentor.cli.handlers.StatusCommandHandler;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

/**
 * 🔍 Command Line Interface for Documentor
 *
 * Provides interactive commands for analyzing projects and generating documentation.
 * Uses Spring Shell for a rich CLI experience with delegated command handlers.
 */
@ShellComponent
public class DocumentorCommands {

    private final ProjectAnalysisCommandHandler projectAnalysisHandler;
    private final StatusCommandHandler statusHandler;
    private final ConfigurationCommandHandler configurationHandler;
    private final EnhancedProjectAnalysisHandler enhancedAnalysisHandler;

    // Track current state
    private String currentProjectPath;
    private String currentConfigPath;

    public DocumentorCommands(
            final ProjectAnalysisCommandHandler projectAnalysisHandlerParam,
            final StatusCommandHandler statusHandlerParam,
            final ConfigurationCommandHandler configurationHandlerParam,
            final EnhancedProjectAnalysisHandler enhancedAnalysisHandlerParam) {
        this.projectAnalysisHandler = projectAnalysisHandlerParam;
        this.statusHandler = statusHandlerParam;
        this.configurationHandler = configurationHandlerParam;
        this.enhancedAnalysisHandler = enhancedAnalysisHandlerParam;
    }

    /**
     * 🔍 Main command to analyze a project and generate documentation
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
            @ShellOption(value = "--include-private-members",
                    help = "Include private members in documentation and diagrams",
                    defaultValue = "true")
            final boolean includePrivateMembers,
            @ShellOption(value = "--generate-mermaid",
                    help = "Generate Mermaid class diagrams",
                    defaultValue = "false")
            final boolean generateMermaid,
            @ShellOption(value = "--mermaid-output",
                    help = "Output directory for Mermaid diagrams (defaults to same directory as source files)",
                    defaultValue = "")
            final String mermaidOutput,
            @ShellOption(value = "--generate-plantuml",
                    help = "Generate PlantUML class diagrams",
                    defaultValue = "false")
            final boolean generatePlantUML,
            @ShellOption(value = "--plantuml-output",
                    help = "Output directory for PlantUML diagrams (defaults to same directory as source files)",
                    defaultValue = "")
            final String plantUMLOutput) {

        // Update current state
        this.currentProjectPath = projectPath;
        this.currentConfigPath = configPath;

        return projectAnalysisHandler.handleAnalyzeProjectExtended(projectPath, configPath,
                generateMermaid, mermaidOutput, generatePlantUML, plantUMLOutput, includePrivateMembers);
    }

    /**
     * 🌱 Generate PlantUML diagrams only (without full analysis)
     */
    @ShellMethod(value = "Generate PlantUML class diagrams for a project",
            key = {"plantuml", "puml"})
    public String generatePlantUMLDiagrams(
            @ShellOption(value = "--project-path",
                    help = "Path to the project directory to analyze")
            final String projectPath,
            @ShellOption(value = "--include-private-members",
                    help = "Include private members in diagrams",
                    defaultValue = "true")
            final boolean includePrivateMembers,
            @ShellOption(value = "--plantuml-output",
                    help = "Output directory for PlantUML diagrams (defaults to same directory as source files)",
                    defaultValue = "")
            final String plantUMLOutput) {

        // Update current state
        this.currentProjectPath = projectPath;

        return projectAnalysisHandler.handleAnalyzeProjectExtended(projectPath, "config.json",
                false, "", true, plantUMLOutput, includePrivateMembers);
    }

    /**
     * 🔍 Scan project without generating documentation
     */
    @ShellMethod(value = "Analyze a project and show statistics",
            key = {"scan", "analyze-only"})
    public String scanProject(
            @ShellOption(value = "--project-path",
                    help = "Path to the project directory to analyze")
            final String projectPath,
            @ShellOption(value = "--include-private-members",
                    help = "Include private members in analysis",
                    defaultValue = "true")
            final boolean includePrivateMembers) {

        // Update current state
        this.currentProjectPath = projectPath;

        return projectAnalysisHandler.handleScanProject(projectPath, includePrivateMembers);
    }

    /**
     * ⚙️ Validate configuration file
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
     * 🔍 Show supported file types and features
     */
    @ShellMethod(value = "Show supported file types and features", key = {"info", "help-extended"})
    public String showInfo() {
        return statusHandler.handleShowInfo();
    }

    /**
     * 🔍 Show quick start guide
     */
    @ShellMethod(value = "Show quick start guide", key = {"quick-start", "getting-started"})
    public String quickStart() {
        return statusHandler.handleQuickStart();
    }

    /**
     * 🔍 Show current application status
     */
    @ShellMethod(value = "Show current application status", key = {"status", "current"})
    public String showStatus() {
        return statusHandler.handleShowStatus(currentProjectPath, currentConfigPath);
    }

    /**
     * 🔧 Analyze a project with ThreadLocal configuration fix
     * This command is specifically designed to address ThreadLocal configuration issues
     */
    @ShellMethod(value = "Analyze a project with ThreadLocal configuration fix",
            key = {"analyze-with-fix", "fixed-analyze"})
    public String analyzeWithFix(
            @ShellOption(value = "--project-path",
                    help = "Path to the project directory to analyze")
            final String projectPath,
            @ShellOption(value = "--config",
                    help = "Path to configuration JSON file",
                    defaultValue = "config.json")
            final String configPath,
            @ShellOption(value = "--include-private-members",
                    help = "Include private members in documentation and diagrams",
                    defaultValue = "true")
            final boolean includePrivateMembers,
            @ShellOption(value = "--generate-mermaid",
                    help = "Generate Mermaid class diagrams",
                    defaultValue = "false")
            final boolean generateMermaid,
            @ShellOption(value = "--mermaid-output",
                    help = "Output directory for Mermaid diagrams",
                    defaultValue = "")
            final String mermaidOutput,
            @ShellOption(value = "--generate-plantuml",
                    help = "Generate PlantUML class diagrams",
                    defaultValue = "false")
            final boolean generatePlantUML,
            @ShellOption(value = "--plantuml-output",
                    help = "Output directory for PlantUML diagrams",
                    defaultValue = "")
            final String plantUMLOutput) {

        return analyzeWithFixAndOptions(projectPath, configPath, includePrivateMembers,
                generateMermaid, mermaidOutput, generatePlantUML, plantUMLOutput, true, "");
    }

    /**
     * Internal method to handle the full analysis with all options
     */
    @SuppressWarnings("checkstyle:ParameterNumber")
    private String analyzeWithFixAndOptions(
            final String projectPath,
            final String configPath,
            final boolean includePrivateMembers,
            final boolean generateMermaid,
            final String mermaidOutput,
            final boolean generatePlantUML,
            final String plantUMLOutput,
            final boolean useFix,
            final String outputDir) {

        // Update current state
        this.currentProjectPath = projectPath;
        this.currentConfigPath = configPath;

        ProjectAnalysisRequest request = new ProjectAnalysisRequest(
            projectPath, configPath, generateMermaid, mermaidOutput,
            generatePlantUML, plantUMLOutput, includePrivateMembers,
            useFix, outputDir);

        return enhancedAnalysisHandler.analyzeProjectWithFix(request);
    }
}
