package com.documentor.service;

import com.documentor.model.CodeElement;
import com.documentor.model.ProjectAnalysis;
import com.documentor.service.diagram.DiagramElementFilter;
import com.documentor.service.diagram.DiagramGeneratorFactory;
import com.documentor.service.diagram.DiagramPathManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * ðŸ“Š Mermaid Diagram Service
 *
 * Service for generating Mermaid diagrams from code analysis.
 * This is a refactored version with reduced complexity.
 */
@Service
public class MermaidDiagramService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MermaidDiagramService.class);

    private final DiagramElementFilter elementFilter;
    private final DiagramPathManager pathManager;
    private final DiagramGeneratorFactory generatorFactory;

    public MermaidDiagramService(
            final DiagramElementFilter elementFilterParam,
            final DiagramPathManager pathManagerParam,
            final DiagramGeneratorFactory generatorFactoryParam) {
        this.elementFilter = elementFilterParam;
        this.pathManager = pathManagerParam;
        this.generatorFactory = generatorFactoryParam;
    }

    /**
     * ðŸ“Š Generates class diagrams for the analyzed project
     */
    public CompletableFuture<List<String>> generateClassDiagrams(
            final ProjectAnalysis analysis,
            final String outputPath) {

        return CompletableFuture.supplyAsync(() -> {
            LOGGER.info("ðŸ“Š Starting Mermaid diagram generation for {} elements",
                    analysis.codeElements().size());

            try {
                return generateDiagrams(analysis, outputPath);
            } catch (Exception e) {
                LOGGER.error("âŒ Error generating Mermaid diagrams: {}", e.getMessage(), e);
                throw new RuntimeException("Failed to generate Mermaid diagrams", e);
            }
        });
    }

    /**
     * ðŸ“Š Core diagram generation logic
     */
    private List<String> generateDiagrams(final ProjectAnalysis analysis, final String outputPath) {
        List<String> generatedFiles = new ArrayList<>();

        // Get eligible classes for diagram generation
        List<CodeElement> eligibleClasses = elementFilter.getEligibleClasses(analysis);

        // Group elements by class
        Map<CodeElement, List<CodeElement>> elementsByClass =
            elementFilter.groupElementsByClass(analysis);

        // Process each eligible class
        eligibleClasses.forEach(classElement -> {
            try {
                String diagram = processSingleClassDiagram(classElement, elementsByClass, outputPath);
                generatedFiles.add(diagram);
            } catch (Exception e) {
                LOGGER.warn("âš ï¸ Failed to generate diagram for {}: {}",
                     classElement.name(), e.getMessage());
            }
        });

        LOGGER.info("âœ… Generated {} Mermaid diagrams", generatedFiles.size());
        return generatedFiles;
    }

    /**
     * ðŸ“Š Process a single class diagram
     */
    private String processSingleClassDiagram(
            final CodeElement classElement,
            final Map<CodeElement, List<CodeElement>> elementsByClass,
            final String outputPath) throws Exception {

        List<CodeElement> classElements = elementsByClass.get(classElement);

        // Determine output path
        String resolvedOutputPath = pathManager.determineOutputPath(
            classElement.filePath(), outputPath);
        Path outputDir = pathManager.createOutputDirectory(resolvedOutputPath);

        // Ensure output directory exists
        Files.createDirectories(outputDir);

        // Generate the diagram
        return generatorFactory.getClassDiagramGenerator()
            .generateClassDiagram(classElement, classElements, outputDir);
    }
}
