package com.documentor.service.documentation;

import com.documentor.constants.ApplicationConstants;
import com.documentor.config.DocumentorConfig;
import com.documentor.model.CodeElementType;
import com.documentor.model.ProjectAnalysis;
import com.documentor.service.LlmServiceEnhanced;
import com.documentor.service.LlmServiceFixEnhanced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 🧪 Enhanced Unit Test Documentation Generator
 *
 * Enhanced specialized component for generating unit test documentation and suggestions
 * with improved error handling and null safety.
 */
@Component
public class UnitTestDocumentationGeneratorEnhanced {

    private static final Logger LOGGER = LoggerFactory.getLogger(UnitTestDocumentationGeneratorEnhanced.class);

    private final LlmServiceEnhanced llmService;
    private final DocumentorConfig config;
    private final LlmServiceFixEnhanced llmServiceFix;

    public UnitTestDocumentationGeneratorEnhanced(final LlmServiceEnhanced llmServiceParam,
                                         final DocumentorConfig configParam,
                                         final LlmServiceFixEnhanced llmServiceFixParam) {
        this.llmService = llmServiceParam;
        this.config = configParam;
        this.llmServiceFix = llmServiceFixParam;
    }

    /**
     * 🧪 Generates unit test documentation with enhanced error handling
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

                // Create the tests directory
                Path testsDir = outputPath.resolve("tests");
                try {
                    Files.createDirectories(testsDir);
                } catch (IOException e) {
                    LOGGER.error("Failed to create tests directory: {}", e.getMessage());
                    return null;
                }

                StringBuilder testDoc = new StringBuilder();
                appendTestDocumentationHeader(testDoc);

                // Filter out elements that are not appropriate for unit testing
                List<CompletableFuture<String>> testFutures = analysis.codeElements().stream()
                        .filter(element -> element != null && element.type() != null
                                        && element.type() != CodeElementType.FIELD)
                        .map(element -> {
                            try {
                                // Ensure configuration is set for each element
                                llmServiceFix.setLlmServiceThreadLocalConfig(config);
                                LOGGER.info("Generating unit tests for: {}", element.name());

                                // Safely generate unit tests, catching and handling potential exceptions
                                return llmService.generateUnitTests(element);
                            } catch (Exception e) {
                                LOGGER.error("Error generating unit tests for element {}: {}",
                                    element.name(), e.getMessage(), e);

                                // Return a CompletableFuture with an error message instead of failing
                                return CompletableFuture.completedFuture(
                                    "```java\n// Error generating unit tests for " + element.name()
                                    + ": " + e.getMessage() + "\n```");
                            }
                        })
                        .toList();

                // Wait for all futures to complete
                try {
                    CompletableFuture.allOf(testFutures.toArray(new CompletableFuture[0]))
                            .thenRun(() -> {
                                try {
                                    // Process results
                                    testFutures.forEach(future -> {
                                        try {
                                            String testContent = future.join();
                                            if (testContent != null) {
                                                testDoc.append(testContent).append("\n\n");
                                            }
                                        } catch (Exception e) {
                                            LOGGER.error("Error processing test future result: {}", e.getMessage());
                                            testDoc.append("// Error processing test: ")
                                                  .append(e.getMessage())
                                                  .append("\n\n");
                                        }
                                    });

                                    // Write the output file
                                    try {
                                        Files.write(testsDir.resolve("unit-tests.md"),
                                                testDoc.toString().getBytes());
                                        LOGGER.info("✅ Successfully wrote unit tests to {}",
                                                testsDir.resolve("unit-tests.md"));
                                    } catch (IOException e) {
                                        LOGGER.error("❌ Error writing test documentation: {}", e.getMessage());
                                    }
                                } catch (Exception e) {
                                    LOGGER.error("Error in test future completion handler: {}", e.getMessage(), e);
                                } finally {
                                    // Clean up ThreadLocal to prevent memory leaks
                                    llmServiceFix.cleanupThreadLocalConfig();
                                }
                            })
                            .join();
                } catch (Exception e) {
                    LOGGER.error("Error waiting for test futures: {}", e.getMessage(), e);
                }

                return null;
            } catch (Exception e) {
                LOGGER.error("❌ Error generating test documentation: {}", e.getMessage(), e);
                return null;
            }
        });
    }

    /**
     * 🧪 Appends test documentation header
     */
    private void appendTestDocumentationHeader(final StringBuilder doc) {
        try {
            String icon = config != null && config.outputSettings() != null
                        && config.outputSettings().includeIcons() ? "🧪 " : "";

            doc.append(String.format("# %sGenerated Unit Tests\n\n", icon));
            doc.append("This file contains AI-generated unit test suggestions for the analyzed code.\n\n");

            if (config != null && config.outputSettings() != null) {
                doc.append(String.format("Target Coverage: %.0f%%\n\n",
                    config.outputSettings().targetCoverage() * ApplicationConstants.PERCENTAGE_MULTIPLIER));
            }
        } catch (Exception e) {
            LOGGER.error("Error creating test documentation header: {}", e.getMessage());
            doc.append("# Generated Unit Tests\n\n");
            doc.append("This file contains AI-generated unit test suggestions for the analyzed code.\n\n");
        }
    }
}
