package com.documentor.cli.handlers;

import com.documentor.config.DocumentorConfig;
import com.documentor.config.ExternalConfigLoader;
import com.documentor.service.LlmServiceFix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.nio.file.Paths;

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

        // If the fix option is enabled, set the ThreadLocal configuration directly
        if (useFix && configPath != null) {
            LOGGER.info("Using LlmServiceFix to ensure ThreadLocal configuration is available");

            // Get the current configuration
            DocumentorConfig config = configLoader.getLoadedConfig();

            if (config == null) {
                // Try to load it if not already loaded
                LOGGER.info("Configuration not loaded yet, trying to load from: {}", configPath);
                String[] args = {"analyze", "--config", configPath};
                boolean loaded = configLoader.loadExternalConfig(args);

                if (loaded) {
                    config = configLoader.getLoadedConfig();
                    LOGGER.info("Successfully loaded config with {} models",
                                config.llmModels() != null ? config.llmModels().size() : 0);
                } else {
                    LOGGER.error("Failed to load configuration from: {}", configPath);
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
        if (outputDir != null && !outputDir.isEmpty()) {
            System.setProperty("documentor.output.directory", outputDir);
            LOGGER.info("Set output directory to: {}", outputDir);
        }

        // Call the base handler to perform the analysis
        return baseHandler.handleAnalyzeProjectExtended(
            projectPath, configPath, generateMermaid, mermaidOutput,
            generatePlantUML, plantUMLOutput, includePrivateMembers);
    }
}
