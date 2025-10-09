package com.documentor.service;

import com.documentor.model.CodeElement;
import com.documentor.model.ProjectAnalysis;
import com.documentor.service.diagram.DiagramElementFilter;
import com.documentor.service.diagram.DiagramPathManager;
import com.documentor.service.diagram.MermaidClassDiagramGenerator;
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
 * 🧩 Mermaid Diagram Service - Refactored for Low Complexity
 * 
 * Orchestrates the generation of Mermaid class diagrams by delegating to specialized components.
 * Coordinates diagram generation workflow for non-private classes, methods, and variables.
 */
@Service
public class MermaidDiagramService {

    private static final Logger logger = LoggerFactory.getLogger(MermaidDiagramService.class);

    private final DiagramElementFilter elementFilter;
    private final DiagramPathManager pathManager;
    private final MermaidClassDiagramGenerator diagramGenerator;

    public MermaidDiagramService(
            DiagramElementFilter elementFilter,
            DiagramPathManager pathManager,
            MermaidClassDiagramGenerator diagramGenerator) {
        this.elementFilter = elementFilter;
        this.pathManager = pathManager;
        this.diagramGenerator = diagramGenerator;
    }

    /**
     * 📊 Generates Mermaid class diagrams for all eligible classes in the project
     * 
     * @param analysis Project analysis containing code elements
     * @param outputPath Optional custom output path, defaults to same directory as source file
     * @return CompletableFuture with the list of generated diagram file paths
     */
    public CompletableFuture<List<String>> generateClassDiagrams(
            ProjectAnalysis analysis, 
            String outputPath) {
        
        return CompletableFuture.supplyAsync(() -> {
            logger.info("🧩 Starting Mermaid diagram generation for {} files", 
                analysis.getElementsByFile().size());
            
            List<String> generatedFiles = new ArrayList<>();
            
            try {
                // Get eligible classes for diagram generation
                List<CodeElement> eligibleClasses = elementFilter.getEligibleClasses(analysis);
                
                // Group elements by class
                Map<CodeElement, List<CodeElement>> elementsByClass = 
                    elementFilter.groupElementsByClass(analysis);
                
                // Generate diagram for each class
                for (CodeElement classElement : eligibleClasses) {
                    List<CodeElement> classElements = elementsByClass.get(classElement);

                    // Determine output path
                    String resolvedOutputPath = pathManager.determineOutputPath(
                        classElement.filePath(), outputPath);
                    Path outputDir = pathManager.createOutputDirectory(resolvedOutputPath);

                    // Ensure output directory exists
                    Files.createDirectories(outputDir);

                    // Generate the diagram
                    String diagramPath = diagramGenerator.generateClassDiagram(
                        classElement, classElements, outputDir);
                    generatedFiles.add(diagramPath);
                }

                logger.info("✅ Generated {} Mermaid diagrams", generatedFiles.size());
                return generatedFiles;

            } catch (Exception e) {
                logger.error("❌ Error generating Mermaid diagrams: {}", e.getMessage(), e);
                throw new RuntimeException("Failed to generate Mermaid diagrams", e);
            }
        });
    }
}