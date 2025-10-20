package com.documentor.cli;

import com.documentor.config.DocumentorConfig;
import com.documentor.service.LlmServiceFix;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Command processor that directly handles application arguments.
 * This is an alternative approach to Spring Shell for command processing.
 * It intercepts command line arguments and delegates to the appropriate handlers.
 */
@Component
public class DirectCommandProcessor implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(DirectCommandProcessor.class);
    private final DocumentorCommands documentorCommands;
    private final DocumentorConfig documentorConfig;
    private final LlmServiceFix llmServiceFix;

    public DirectCommandProcessor(final DocumentorCommands documentorCommands,
                                 final DocumentorConfig documentorConfig,
                                 final LlmServiceFix llmServiceFix) {
        this.documentorCommands = documentorCommands;
        this.documentorConfig = documentorConfig;
        this.llmServiceFix = llmServiceFix;
    }

    @Override
    public void run(final ApplicationArguments args) throws Exception {
        String[] sourceArgs = args.getSourceArgs();
        LOGGER.info("Processing command arguments: {}", Arrays.toString(sourceArgs));

        // Ensure the configuration is set in the ThreadLocal for all threads
        if (documentorConfig != null) {
            LOGGER.info("Setting ThreadLocal config directly via LlmServiceFix");
            llmServiceFix.setLlmServiceThreadLocalConfig(documentorConfig);

            // Verify the configuration was set properly
            boolean configAvailable = llmServiceFix.isThreadLocalConfigAvailable();
            LOGGER.info("ThreadLocal config verification: {}", configAvailable ? "AVAILABLE" : "NOT AVAILABLE");
        } else {
            LOGGER.warn("No DocumentorConfig bean available - ThreadLocal configuration may be unavailable");
        }

        // Process the arguments directly if they match our expected format
        if (sourceArgs.length > 0) {
            String command = sourceArgs[0];

            // Process "analyze" command
            if ("analyze".equals(command)) {
                processAnalyzeCommand(sourceArgs);
            }
        }
    }

    private void processAnalyzeCommand(final String[] args) {
        String projectPath = ".";
        String configPath = "config.json";
        boolean includePrivateMembers = true;
        boolean generateMermaid = false;
        String mermaidOutput = "";
        boolean generatePlantUML = false;
        String plantUMLOutput = "";

        // Parse arguments
        for (int i = 1; i < args.length; i++) {
            if ("--project-path".equals(args[i]) && i + 1 < args.length) {
                projectPath = args[i + 1];
                i++;
            } else if ("--config".equals(args[i]) && i + 1 < args.length) {
                configPath = args[i + 1];
                i++;
            } else if ("--include-private-members".equals(args[i]) && i + 1 < args.length) {
                includePrivateMembers = Boolean.parseBoolean(args[i + 1]);
                i++;
            } else if ("--generate-mermaid".equals(args[i]) && i + 1 < args.length) {
                generateMermaid = Boolean.parseBoolean(args[i + 1]);
                i++;
            } else if ("--mermaid-output".equals(args[i]) && i + 1 < args.length) {
                mermaidOutput = args[i + 1];
                i++;
            } else if ("--generate-plantuml".equals(args[i]) && i + 1 < args.length) {
                generatePlantUML = Boolean.parseBoolean(args[i + 1]);
                i++;
            } else if ("--plantuml-output".equals(args[i]) && i + 1 < args.length) {
                plantUMLOutput = args[i + 1];
                i++;
            }
        }

        LOGGER.info("Executing analyze command with: project={}, config={}, mermaid={}",
                    projectPath, configPath, generateMermaid);

        // Double-check ThreadLocal configuration before processing
        if (documentorConfig != null) {
            LOGGER.info("Ensuring ThreadLocal config is set before analyze command execution");
            llmServiceFix.setLlmServiceThreadLocalConfig(documentorConfig);
        }

        try {
            // Directly call the DocumentorCommands method
            String result = documentorCommands.analyzeProject(
                projectPath, configPath, includePrivateMembers,
                generateMermaid, mermaidOutput, generatePlantUML, plantUMLOutput);

            LOGGER.info("Command execution result: {}", result);
        } catch (Exception e) {
            LOGGER.error("Error executing analyze command", e);
        }
    }
}
