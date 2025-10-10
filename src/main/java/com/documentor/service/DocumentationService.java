package com.documentor.service;

import com.documentor.config.DocumentorConfig;
import com.documentor.model.ProjectAnalysis;
import com.documentor.service.documentation.ElementDocumentationGenerator;
import com.documentor.service.documentation.MainDocumentationGenerator;
import com.documentor.service.documentation.UnitTestDocumentationGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * ðŸ“ Documentation Generation Service - Refactored for Low Complexity
 *
 * Orchestrates the generation of markdown documentation from code analysis results.
 * Delegates to specialized generators for different types of documentation.
 */
@Service
public class DocumentationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentationService.class);

    private final MainDocumentationGenerator mainDocGenerator;
    private final ElementDocumentationGenerator elementDocGenerator;
    private final UnitTestDocumentationGenerator testDocGenerator;
    private final MermaidDiagramService mermaidDiagramService;
    private final DocumentorConfig config;

    public DocumentationService(
            final MainDocumentationGenerator mainDocGenerator,
            final ElementDocumentationGenerator elementDocGenerator,
            final UnitTestDocumentationGenerator testDocGenerator,
            final MermaidDiagramService mermaidDiagramService,
            final DocumentorConfig config) {
        this.mainDocGenerator = mainDocGenerator;
        this.elementDocGenerator = elementDocGenerator;
        this.testDocGenerator = testDocGenerator;
        this.mermaidDiagramService = mermaidDiagramService;
        this.config = config;
    }

    /**
     * ðŸ“š Generates complete project documentation
     *
     * @param analysis The project analysis results
     * @return CompletableFuture containing the path to generated documentation
     */
    public CompletableFuture<String> generateDocumentation(final ProjectAnalysis analysis) {
        LOGGER.info("ðŸ“ Starting documentation generation for project: {}", analysis.projectPath());

        return CompletableFuture.supplyAsync(() -> {
            try {
                // Create output directory
                Path outputPath = Paths.get(config.outputSettings().outputPath());
                Files.createDirectories(outputPath);

                // Generate main documentation
                String mainDoc = mainDocGenerator.generateMainDocumentation(analysis).join();
                Path mainDocPath = outputPath.resolve("README.md");
                Files.write(mainDocPath, mainDoc.getBytes());

                // Generate detailed documentation for each element
                generateDetailedDocumentation(analysis, outputPath).join();

                // Generate unit tests if enabled
                if (config.outputSettings().generateUnitTests()) {
                    testDocGenerator.generateUnitTestDocumentation(analysis, outputPath).join();
                }

                // Generate Mermaid diagrams if enabled
                if (config.outputSettings().generateMermaidDiagrams()) {
                    List<String> diagramPaths = mermaidDiagramService.generateClassDiagrams(
                        analysis, config.outputSettings().mermaidOutputPath()).join();
                    LOGGER.info("âœ… Generated {} Mermaid diagrams", diagramPaths.size());
                }

                LOGGER.info("âœ… Documentation generated successfully at: {}", outputPath);
                return outputPath.toString();

            } catch (Exception e) {
                LOGGER.error("âŒ Error generating documentation: {}", e.getMessage(), e);
                throw new RuntimeException("Failed to generate documentation", e);
            }
        });
    }

    /**
     * ðŸ“‘ Generates detailed documentation for each code element
     */
    private CompletableFuture<Void> generateDetailedDocumentation(final ProjectAnalysis analysis, final Path outputPath) {
        List<CompletableFuture<Void>> futures = analysis.codeElements().stream()
                .map(element -> elementDocGenerator.generateElementDocumentation(element, outputPath))
                .toList();

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }
}
