package com.documentor.cli.handlers;

import com.documentor.config.DocumentorConfig;
import com.documentor.config.ExternalConfigLoader;
import com.documentor.service.LlmServiceFix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


/**
 * Enhanced project analysis command handler with ThreadLocal fix support
 */
@Component
public class EnhancedProjectAnalysisHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnhancedProjectAnalysisHandler.class);

    private final ProjectAnalysisCommandHandler baseHandler;
    private final LlmServiceFix llmServiceFix;
    private final ExternalConfigLoader configLoader;

    public EnhancedProjectAnalysisHandler(
            final ProjectAnalysisCommandHandler baseHandlerParam,
            final LlmServiceFix llmServiceFixParam,
            final ExternalConfigLoader configLoaderParam) {
        this.baseHandler = baseHandlerParam;
        this.llmServiceFix = llmServiceFixParam;
        this.configLoader = configLoaderParam;
    }

    /**
     * Handle project analysis command with fix option for ThreadLocal configuration
     */
    public String analyzeProjectWithFix(final ProjectAnalysisRequest request) {
        // If the fix option is enabled, set the ThreadLocal configuration directly
        if (request.useFix() && request.configPath() != null) {
            LOGGER.info("Using LlmServiceFix to ensure ThreadLocal configuration is available");

            // Get the current configuration
            DocumentorConfig config = configLoader.getLoadedConfig();

            if (config == null) {
                // Try to load it if not already loaded
                LOGGER.info("Configuration not loaded yet, trying to load from: {}", request.configPath());
                String[] args = {"analyze", "--config", request.configPath()};
                boolean loaded = configLoader.loadExternalConfig(args);

                if (loaded) {
                    config = configLoader.getLoadedConfig();
                    LOGGER.info("Successfully loaded config with {} models",
                                config.llmModels() != null ? config.llmModels().size() : 0);
                } else {
                    LOGGER.error("Failed to load configuration from: {}", request.configPath());
                }
            }

            if (config != null) {
                // Use our fix to set the ThreadLocal configuration directly
                llmServiceFix.setLlmServiceThreadLocalConfig(config);
            } else {
                LOGGER.error("Cannot apply fix - configuration is null");
            }
        }

        // Add output directory option if specified
        if (request.outputDir() != null && !request.outputDir().isEmpty()) {
            System.setProperty("documentor.output.directory", request.outputDir());
            LOGGER.info("Set output directory to: {}", request.outputDir());
        }

        // Call the base handler to perform the analysis
        return baseHandler.handleAnalyzeProjectExtended(
            request.projectPath(), request.configPath(), request.generateMermaid(),
            request.mermaidOutput(), request.generatePlantUML(), request.plantUMLOutput(),
            request.includePrivateMembers());
    }

    /**
     * Legacy method for backwards compatibility - for testing purposes only
     * @deprecated Use analyzeProjectWithFix(ProjectAnalysisRequest) instead
     */
    @Deprecated
    @SuppressWarnings("checkstyle:ParameterNumber")
    public String analyzeProjectWithFix(
            final String projectPath,
            final String configPath,
            final boolean generateMermaid,
            final String mermaidOutput,
            final boolean generatePlantUML,
            final String plantUMLOutput,
            final Boolean includePrivateMembers,
            final boolean useFix,
            final String outputDir) {
        ProjectAnalysisRequest request = new ProjectAnalysisRequest(
            projectPath, configPath, generateMermaid, mermaidOutput,
            generatePlantUML, plantUMLOutput, includePrivateMembers, useFix, outputDir);
        return analyzeProjectWithFix(request);
    }
}
