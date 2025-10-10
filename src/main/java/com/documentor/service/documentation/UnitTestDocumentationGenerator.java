package com.documentor.service.documentation;

import com.documentor.constants.ApplicationConstants;
import com.documentor.config.DocumentorConfig;
import com.documentor.model.CodeElementType;
import com.documentor.model.ProjectAnalysis;
import com.documentor.service.LlmService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * ðŸ§ª Unit Test Documentation Generator
 *
 * Specialized component for generating unit test documentation and suggestions.
 * Handles AI-generated test cases and coverage recommendations.
 */
@Component
public class UnitTestDocumentationGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(UnitTestDocumentationGenerator.class);

    private final LlmService llmService;
    private final DocumentorConfig config;

    public UnitTestDocumentationGenerator(LlmService llmServiceParam, DocumentorConfig configParam) {
        this.llmService = llmServiceParam;
        this.config = configParam;
    }

    /**
     * ðŸ§ª Generates unit test documentation
     */
    public CompletableFuture<Void> generateUnitTestDocumentation(final ProjectAnalysis analysis, final Path outputPath) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Path testsDir = outputPath.resolve("tests");
                Files.createDirectories(testsDir);

                StringBuilder testDoc = new StringBuilder();
                appendTestDocumentationHeader(testDoc);

                List<CompletableFuture<String>> testFutures = analysis.codeElements().stream()
                        .filter(element -> element.type() != CodeElementType.FIELD)
                        .map(llmService::generateUnitTests)
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
                                LOGGER.error("âŒ Error writing test documentation: {}", e.getMessage());
                            }
                        })
                        .join();

                return null;
            } catch (Exception e) {
                LOGGER.error("âŒ Error generating test documentation: {}", e.getMessage());
                throw new RuntimeException("Failed to generate test documentation", e);
            }
        });
    }

    /**
     * ðŸ§ª Appends test documentation header
     */
    private void appendTestDocumentationHeader(final StringBuilder doc) {
        String icon = config.outputSettings().includeIcons() ? "ðŸ§ª " : "";
        doc.append(String.format("# %sGenerated Unit Tests\n\n", icon));
        doc.append("This file contains AI-generated unit test suggestions for the analyzed code.\n\n");
        doc.append(String.format("Target Coverage: %.0f%%\n\n",
            config.outputSettings().targetCoverage() * ApplicationConstants.PERCENTAGE_MULTIPLIER));
    }
}
