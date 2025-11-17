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
 * Provides interactive commands for analyzing projects and generating
 * documentation.
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

    /**
     * Parameter object for analysis options
     */
    private static final class AnalysisOptions {
        private final String projectPath;
        private final String configPath;
        private final boolean includePrivateMembers;
        private final boolean generateMermaid;
        private final String mermaidOutput;
        private final boolean generatePlantUML;
        private final String plantUMLOutput;
        private final boolean useFix;
        private final String outputDir;

        private AnalysisOptions(final Builder builder) {
            this.projectPath = builder.projectPath;
            this.configPath = builder.configPath;
            this.includePrivateMembers = builder.includePrivateMembers;
            this.generateMermaid = builder.generateMermaid;
            this.mermaidOutput = builder.mermaidOutput;
            this.generatePlantUML = builder.generatePlantUML;
            this.plantUMLOutput = builder.plantUMLOutput;
            this.useFix = builder.useFix;
            this.outputDir = builder.outputDir;
        }

        static Builder builder() {
            return new Builder();
        }

        static final class Builder {
            private String projectPath;
            private String configPath;
            private boolean includePrivateMembers;
            private boolean generateMermaid;
            private String mermaidOutput;
            private boolean generatePlantUML;
            private String plantUMLOutput;
            private boolean useFix;
            private String outputDir;

            Builder projectPath(final String projectPathParam) {
                this.projectPath = projectPathParam;
                return this;
            }

            Builder configPath(final String configPathParam) {
                this.configPath = configPathParam;
                return this;
            }

            Builder includePrivateMembers(
                    final boolean includePrivateMembersParam) {
                this.includePrivateMembers = includePrivateMembersParam;
                return this;
            }

            Builder generateMermaid(final boolean generateMermaidParam) {
                this.generateMermaid = generateMermaidParam;
                return this;
            }

            Builder mermaidOutput(final String mermaidOutputParam) {
                this.mermaidOutput = mermaidOutputParam;
                return this;
            }

            Builder generatePlantUML(final boolean generatePlantUMLParam) {
                this.generatePlantUML = generatePlantUMLParam;
                return this;
            }

            Builder plantUMLOutput(final String plantUMLOutputParam) {
                this.plantUMLOutput = plantUMLOutputParam;
                return this;
            }

            Builder useFix(final boolean useFixParam) {
                this.useFix = useFixParam;
                return this;
            }

            Builder outputDir(final String outputDirParam) {
                this.outputDir = outputDirParam;
                return this;
            }

            AnalysisOptions build() {
                return new AnalysisOptions(this);
            }
        }

        public String getProjectPath() {
            return projectPath;
        }

        public String getConfigPath() {
            return configPath;
        }

        public boolean isIncludePrivateMembers() {
            return includePrivateMembers;
        }

        public boolean isGenerateMermaid() {
            return generateMermaid;
        }

        public String getMermaidOutput() {
            return mermaidOutput;
        }

        public boolean isGeneratePlantUML() {
            return generatePlantUML;
        }

        public String getPlantUMLOutput() {
            return plantUMLOutput;
        }

        public boolean isUseFix() {
            return useFix;
        }

        public String getOutputDir() {
            return outputDir;
        }
    }

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
                    help = "Include private members in documentation "
                           + "and diagrams",
                    defaultValue = "true")
            final boolean includePrivateMembers,
            @ShellOption(value = "--generate-mermaid",
                    help = "Generate Mermaid class diagrams",
                    defaultValue = "false")
            final boolean generateMermaid,
            @ShellOption(value = "--mermaid-output",
                    help = "Output directory for Mermaid diagrams "
                           + "(defaults to same directory as source files)",
                    defaultValue = "")
            final String mermaidOutput,
            @ShellOption(value = "--generate-plantuml",
                    help = "Generate PlantUML class diagrams",
                    defaultValue = "false")
            final boolean generatePlantUML,
            @ShellOption(value = "--plantuml-output",
                    help = "Output directory for PlantUML diagrams "
                           + "(defaults to same directory as source files)",
                    defaultValue = "")
            final String plantUMLOutput) {

        // Update current state
        this.currentProjectPath = projectPath;
        this.currentConfigPath = configPath;

        return projectAnalysisHandler.handleAnalyzeProjectExtended(
                projectPath, configPath, generateMermaid, mermaidOutput,
                generatePlantUML, plantUMLOutput, includePrivateMembers);
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
                    help = "Output directory for PlantUML diagrams "
                           + "(defaults to same directory as source files)",
                    defaultValue = "")
            final String plantUMLOutput) {

        // Update current state
        this.currentProjectPath = projectPath;

        return projectAnalysisHandler.handleAnalyzeProjectExtended(
                projectPath, "config.json", false, "", true,
                plantUMLOutput, includePrivateMembers);
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

        return projectAnalysisHandler.handleScanProject(projectPath,
                                                        includePrivateMembers);
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
    @ShellMethod(value = "Show supported file types and features",
                 key = {"info", "help-extended"})
    public String showInfo() {
        return statusHandler.handleShowInfo();
    }

    /**
     * 🔍 Show quick start guide
     */
    @ShellMethod(value = "Show quick start guide",
                 key = {"quick-start", "getting-started"})
    public String quickStart() {
        return statusHandler.handleQuickStart();
    }

    /**
     * 🔍 Show current application status
     */
    @ShellMethod(value = "Show current application status",
                 key = {"status", "current"})
    public String showStatus() {
        return statusHandler.handleShowStatus(currentProjectPath,
                                               currentConfigPath);
    }

    /**
     * 🔧 Analyze a project with ThreadLocal configuration fix
     * This command is specifically designed to address ThreadLocal
     * configuration issues
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
                    help = "Include private members in documentation "
                           + "and diagrams",
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

        return analyzeWithFixAndOptions(AnalysisOptions.builder()
                .projectPath(projectPath)
                .configPath(configPath)
                .includePrivateMembers(includePrivateMembers)
                .generateMermaid(generateMermaid)
                .mermaidOutput(mermaidOutput)
                .generatePlantUML(generatePlantUML)
                .plantUMLOutput(plantUMLOutput)
                .useFix(true)
                .outputDir("")
                .build());
    }

    /**
     * Internal method to handle the full analysis with all options
     */
    private String analyzeWithFixAndOptions(final AnalysisOptions options) {

        // Update current state
        this.currentProjectPath = options.getProjectPath();
        this.currentConfigPath = options.getConfigPath();

        ProjectAnalysisRequest request = new ProjectAnalysisRequest(
            options.getProjectPath(), options.getConfigPath(),
            options.isGenerateMermaid(), options.getMermaidOutput(),
            options.isGeneratePlantUML(), options.getPlantUMLOutput(),
            options.isIncludePrivateMembers(), options.isUseFix(),
            options.getOutputDir());

        return enhancedAnalysisHandler.analyzeProjectWithFix(request);
    }
}

