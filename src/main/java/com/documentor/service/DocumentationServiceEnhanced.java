package com.documentor.service;

import com.documentor.config.DocumentorConfig;
import com.documentor.model.ProjectAnalysis;
import com.documentor.service.documentation.ElementDocumentationGeneratorEnhanced;
import com.documentor.service.documentation.MainDocumentationGenerator;
import com.documentor.service.documentation.UnitTestDocumentationGeneratorEnhanced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 📄 Enhanced Documentation Generation Service
 *
 * Enhanced version that uses the improved LlmServiceEnhanced components
 * throughout,
 * with better error handling, thread safety, and monitoring.
 */
@Service
public class DocumentationServiceEnhanced {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(DocumentationServiceEnhanced.class);
    private static final long DEFAULT_FUTURE_TIMEOUT_SECONDS = 180; // 3 minutes

    private final MainDocumentationGenerator mainDocGenerator;
    private final ElementDocumentationGeneratorEnhanced elementDocGenerator;
    private final UnitTestDocumentationGeneratorEnhanced testDocGenerator;
    private final MermaidDiagramService mermaidDiagramService;
    private final PlantUMLDiagramService plantUMLDiagramService;
    private final DocumentorConfig config;
    private final LlmServiceFixEnhanced llmServiceFix;

    public DocumentationServiceEnhanced(
            final MainDocumentationGenerator mainDocGeneratorParam,
            final ElementDocumentationGeneratorEnhanced
                elementDocGeneratorParam,
            final UnitTestDocumentationGeneratorEnhanced testDocGeneratorParam,
            final MermaidDiagramService mermaidDiagramServiceParam,
            final PlantUMLDiagramService plantUMLDiagramServiceParam,
            final DocumentorConfig configParam,
            final LlmServiceFixEnhanced llmServiceFixParam) {
        this.mainDocGenerator = mainDocGeneratorParam;
        this.elementDocGenerator = elementDocGeneratorParam;
        this.testDocGenerator = testDocGeneratorParam;
        this.mermaidDiagramService = mermaidDiagramServiceParam;
        this.plantUMLDiagramService = plantUMLDiagramServiceParam;
        this.config = configParam;
        this.llmServiceFix = llmServiceFixParam;

        LOGGER.info("DocumentationServiceEnhanced initialized with enhanced "
                + "components");
    }

    /**
     * 📚 Generates complete project documentation with enhanced error handling
     * and threading
     *
     * @param analysis The project analysis results
     * @return CompletableFuture containing the path to generated documentation
     */
    public CompletableFuture<String> generateDocumentation(
            final ProjectAnalysis analysis) {
        setupThreadLocalConfig(analysis);

        return CompletableFuture.supplyAsync(() -> {
            try {
                // Create output directory
                Path outputPath =
                        Paths.get(config.outputSettings().outputPath());
                Files.createDirectories(outputPath);
                LOGGER.info("Created output directory: {}", outputPath);

                // Generate documentation components
                generateMainDocumentation(analysis, outputPath);
                generateElementDocumentation(analysis, outputPath);
                generateUnitTestDocumentation(analysis, outputPath);
                generateMermaidDiagrams(analysis);
                generatePlantUMLDiagrams(analysis);

                LOGGER.info("✅ Documentation generated successfully at: {}",
                        outputPath);
                return outputPath.toString();

            } catch (Exception e) {
                LOGGER.error("❌ Critical error generating documentation: {}",
                        e.getMessage(), e);
                throw new RuntimeException("Failed to generate documentation", e);
            } finally {
                cleanupThreadLocalResources();
            }
        });
    }

    /**
     * Sets up ThreadLocal configuration for LLM service.
     * @param analysis Project analysis data
     */
    private void setupThreadLocalConfig(final ProjectAnalysis analysis) {
        LOGGER.info("📄 Starting enhanced documentation generation " +
                "for project: {}", analysis.projectPath());

        // Ensure ThreadLocal configuration is properly set up first
        if (llmServiceFix != null) {
            try {
                LOGGER.info("Setting ThreadLocal config for " +
                        "documentation generation");
                llmServiceFix.setLlmServiceThreadLocalConfig(config);

                // Verify the configuration was set properly
                boolean configAvailable =
                        llmServiceFix.isThreadLocalConfigAvailable();
                if (!configAvailable) {
                    LOGGER.warn("ThreadLocal configuration is still not " +
                            "available - documentation generation may fail");
                }
            } catch (Exception e) {
                LOGGER.error("Error setting up ThreadLocal config: {}",
                        e.getMessage(), e);
            }
        } else {
            LOGGER.warn("LlmServiceFixEnhanced is null - ThreadLocal config not set up");
        }
    }

    /**
     * Generates main documentation file.
     * @param analysis Project analysis data
     * @param outputPath Output directory path
     * @throws IOException if file writing fails
     */
    private void generateMainDocumentation(final ProjectAnalysis analysis,
            final Path outputPath) throws IOException {
        // Generate main documentation with timeout handling
        CompletableFuture<String> mainDocFuture = mainDocGenerator
            .generateMainDocumentation(analysis)
            .orTimeout(DEFAULT_FUTURE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .exceptionally(ex -> {
                if (ex instanceof TimeoutException) {
                    LOGGER.error("Timeout while generating main documentation");
                } else {
                    LOGGER.error("Error generating main documentation: {}",
                            ex.getMessage(), ex);
                }
                return "# Error Generating Documentation\n\n"
                       + "There was an error generating the main documentation: "
                       + ex.getMessage();
            });

        String mainDoc = mainDocFuture.join();
        Path mainDocPath = outputPath.resolve("README.md");
        Files.write(mainDocPath, mainDoc.getBytes());
        LOGGER.info("✅ Main documentation written to: {}", mainDocPath);
    }

    /**
     * Generates detailed documentation for each code element.
     * @param analysis Project analysis data
     * @param outputPath Output directory path
     */
    private void generateElementDocumentation(final ProjectAnalysis analysis,
            final Path outputPath) {
        try {
            // First ensure ThreadLocal is properly set
            if (llmServiceFix != null) {
                llmServiceFix.setLlmServiceThreadLocalConfig(config);
            }

            // Use the enhanced element doc generator with better threading
            CompletableFuture<Void> elementDocFuture = elementDocGenerator
                .generateGroupedDocumentation(analysis, outputPath)
                .orTimeout(DEFAULT_FUTURE_TIMEOUT_SECONDS * 2,
                        TimeUnit.SECONDS)
                .exceptionally(ex -> {
                    if (ex instanceof TimeoutException) {
                        LOGGER.error("Timeout while generating " +
                                "element documentation");
                    } else {
                        LOGGER.error("Error generating element documentation: {}",
                                ex.getMessage(), ex);
                    }
                    return null; // Continue with other tasks
                });

            // Wait for element documentation to complete
            elementDocFuture.join();
            LOGGER.info("✅ Element documentation completed");
        } catch (Exception e) {
            LOGGER.error("Error in element documentation generation: {}",
                    e.getMessage(), e);
            // Continue with other tasks despite errors
        }
    }

    /**
     * Generates unit test documentation if enabled.
     * @param analysis Project analysis data
     * @param outputPath Output directory path
     */
    private void generateUnitTestDocumentation(
            final ProjectAnalysis analysis, final Path outputPath) {
        // Generate unit tests if enabled
        if (config.outputSettings().generateUnitTests() != null
                && config.outputSettings().generateUnitTests()) {
            try {
                // First ensure ThreadLocal is properly set again
                if (llmServiceFix != null) {
                    LOGGER.info("Refreshing ThreadLocal config before " +
                            "unit test generation");
                    llmServiceFix.setLlmServiceThreadLocalConfig(config);
                }

                LOGGER.info("Generating unit tests as specified " +
                        "in configuration");
                CompletableFuture<Void> testFuture = testDocGenerator
                    .generateUnitTestDocumentation(analysis, outputPath)
                    .orTimeout(DEFAULT_FUTURE_TIMEOUT_SECONDS * 2,
                            TimeUnit.SECONDS)
                    .exceptionally(ex -> {
                        if (ex instanceof TimeoutException) {
                            LOGGER.error("Timeout while generating " +
                                    "unit test documentation");
                        } else {
                            LOGGER.error("Error generating unit tests: {}",
                                    ex.getMessage(), ex);
                        }
                        return null; // Continue with other tasks
                    });

                // Wait for unit test documentation to complete
                testFuture.join();
                LOGGER.info("✅ Unit test documentation completed");
            } catch (Exception e) {
                LOGGER.error("Error in unit test documentation generation: {}",
                        e.getMessage(), e);
                // Continue with other tasks despite errors
            }
        } else {
            LOGGER.info("Unit test generation is disabled in configuration " +
                    "- skipping");
        }
    }

    /**
     * Generates Mermaid diagrams if enabled.
     * @param analysis Project analysis data
     */
    private void generateMermaidDiagrams(final ProjectAnalysis analysis) {
        // Generate Mermaid diagrams if enabled
        if (config.outputSettings().generateMermaidDiagrams()) {
            try {
                CompletableFuture<List<String>> diagramFuture =
                        mermaidDiagramService
                    .generateClassDiagrams(analysis,
                            config.outputSettings().mermaidOutputPath())
                    .orTimeout(DEFAULT_FUTURE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                    .exceptionally(ex -> {
                        LOGGER.error("Error generating Mermaid diagrams: {}",
                                ex.getMessage(), ex);
                        return List.of(); // Return empty list to avoid NPE
                    });

                List<String> diagramPaths = diagramFuture.join();
                LOGGER.info("✅ Generated {} Mermaid diagrams",
                        diagramPaths.size());
            } catch (Exception e) {
                LOGGER.error("Error in Mermaid diagram generation: {}",
                        e.getMessage(), e);
            }
        }
    }

    /**
     * Generates PlantUML diagrams if enabled.
     * @param analysis Project analysis data
     */
    private void generatePlantUMLDiagrams(final ProjectAnalysis analysis) {
        // Generate PlantUML diagrams if enabled
        if (config.outputSettings().generatePlantUMLDiagrams()) {
            try {
                CompletableFuture<List<String>> plantUMLFuture =
                        plantUMLDiagramService
                    .generateClassDiagrams(analysis,
                            config.outputSettings().plantUMLOutputPath())
                    .orTimeout(DEFAULT_FUTURE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                    .exceptionally(ex -> {
                        LOGGER.error("Error generating PlantUML diagrams: {}",
                                ex.getMessage(), ex);
                        return List.of(); // Return empty list to avoid NPE
                    });

                List<String> plantUMLPaths = plantUMLFuture.join();
                LOGGER.info("✅ Generated {} PlantUML diagrams",
                        plantUMLPaths.size());
            } catch (Exception e) {
                LOGGER.error("Error in PlantUML diagram generation: {}",
                        e.getMessage(), e);
            }
        }
    }

    /**
     * Cleans up ThreadLocal resources after documentation generation.
     */
    private void cleanupThreadLocalResources() {
        // Always clean up ThreadLocal resources to prevent memory leaks
        if (llmServiceFix != null) {
            LOGGER.info("Cleaning up ThreadLocal resources after documentation generation");
            llmServiceFix.cleanupThreadLocalConfig();
        }
    }
}
