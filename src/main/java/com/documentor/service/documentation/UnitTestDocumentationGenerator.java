package com.documentor.service.documentation;

import com.documentor.constants.ApplicationConstants;
import com.documentor.config.DocumentorConfig;
import com.documentor.model.CodeElementType;
import com.documentor.model.ProjectAnalysis;
import com.documentor.service.LlmService;
import com.documentor.service.LlmServiceFix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 🧪 Unit Test Documentation Generator
 *
 * Specialized component for generating unit test documentation and suggestions.
 * Handles AI-generated test cases and coverage recommendations.
 */
@Component
public class UnitTestDocumentationGenerator {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(UnitTestDocumentationGenerator.class);

    private final LlmService llmService;
    private final DocumentorConfig config;
    private final LlmServiceFix llmServiceFix;

    public UnitTestDocumentationGenerator(final LlmService llmServiceParam,
                                         final DocumentorConfig configParam,
                                         final LlmServiceFix llmServiceFixParam) {
        this.llmService = llmServiceParam;
        this.config = configParam;
        this.llmServiceFix = llmServiceFixParam;
    }

    /**
     * 🧪 Generates unit test documentation
     */
    public CompletableFuture<Void> generateUnitTestDocumentation(final ProjectAnalysis analysis,
            final Path outputPath) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Ensure the ThreadLocal configuration is set before generating unit tests
                if (config != null) {
                    LOGGER.info("Setting ThreadLocal configuration for unit test generation");
                    llmServiceFix.setLlmServiceThreadLocalConfig(config);

                    // Verify the configuration was set properly
                    boolean configAvailable = llmServiceFix.isThreadLocalConfigAvailable();
                    if (!configAvailable) {
                        LOGGER.warn("ThreadLocal configuration is still not available - unit test generation may fail");
                    }
                } else {
                    LOGGER.warn("Configuration is null - cannot set ThreadLocal config for unit test generation");
                }

                Path testsDir = outputPath.resolve("tests");
                Files.createDirectories(testsDir);

                StringBuilder testDoc = new StringBuilder();
                appendTestDocumentationHeader(testDoc);

                // Create a wrapper method that handles potential executor issues
                List<CompletableFuture<String>> testFutures = analysis.codeElements().stream()
                        .filter(element -> element.type() != CodeElementType.FIELD)
                        .map(element -> {
                            try {
                                // Ensure configuration is set for each element
                                llmServiceFix.setLlmServiceThreadLocalConfig(config);

                                // Safely generate unit tests, catching and handling potential exceptions
                                return llmService.generateUnitTests(element);
                            } catch (Exception e) {
                                LOGGER.error("Error generating unit tests for element {}: {}",
                                    element.name(), e.getMessage());

                                // Return a CompletableFuture with an error message instead of failing
                                return CompletableFuture.completedFuture(
                                    "```\n// Error generating unit tests: " + e.getMessage() + "\n```");
                            }
                        })
                        .toList();

                CompletableFuture.allOf(testFutures.toArray(new CompletableFuture[0]))
                        .thenRun(() -> {
                            testFutures.forEach(future -> {
                                String testContent = future.join();
                                testDoc.append(testContent).append("\n\n");
                            });

                            try {
                                Files.write(testsDir.resolve("unit-tests.md"), testDoc.toString().getBytes());
                            } catch (IOException e) {
                                LOGGER.error("❌ Error writing test documentation: {}", e.getMessage());
                            }
                        })
                        .join();

                return null;
            } catch (Exception e) {
                LOGGER.error("❌ Error generating test documentation: {}", e.getMessage());
                throw new RuntimeException("Failed to generate test documentation", e);
            }
        });
    }

    /**
     * 🧪 Appends test documentation header
     */
    private void appendTestDocumentationHeader(final StringBuilder doc) {
        String icon = config.outputSettings().includeIcons() ? "🧪 " : "";
        doc.append(String.format("# %sGenerated Unit Tests\n\n", icon));
        doc.append("This file contains AI-generated unit test suggestions for the analyzed code.\n\n");
        doc.append(String.format("Target Coverage: %.0f%%\n\n",
            config.outputSettings().targetCoverage() * ApplicationConstants.PERCENTAGE_MULTIPLIER));
    }
}
