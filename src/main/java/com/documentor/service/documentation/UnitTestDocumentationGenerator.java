package com.documentor.service.documentation;

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
 * 🧪 Unit Test Documentation Generator
 * 
 * Specialized component for generating unit test documentation and suggestions.
 * Handles AI-generated test cases and coverage recommendations.
 */
@Component
public class UnitTestDocumentationGenerator {

    private static final Logger logger = LoggerFactory.getLogger(UnitTestDocumentationGenerator.class);

    private final LlmService llmService;
    private final DocumentorConfig config;

    public UnitTestDocumentationGenerator(LlmService llmService, DocumentorConfig config) {
        this.llmService = llmService;
        this.config = config;
    }

    /**
     * 🧪 Generates unit test documentation
     */
    public CompletableFuture<Void> generateUnitTestDocumentation(ProjectAnalysis analysis, Path outputPath) {
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
                                logger.error("❌ Error writing test documentation: {}", e.getMessage());
                            }
                        })
                        .join();

                return null;
            } catch (Exception e) {
                logger.error("❌ Error generating test documentation: {}", e.getMessage());
                throw new RuntimeException("Failed to generate test documentation", e);
            }
        });
    }

    /**
     * 🧪 Appends test documentation header
     */
    private void appendTestDocumentationHeader(StringBuilder doc) {
        String icon = config.outputSettings().includeIcons() ? "🧪 " : "";
        doc.append(String.format("# %sGenerated Unit Tests\n\n", icon));
        doc.append("This file contains AI-generated unit test suggestions for the analyzed code.\n\n");
        doc.append(String.format("Target Coverage: %.0f%%\n\n", config.outputSettings().targetCoverage() * 100));
    }
}