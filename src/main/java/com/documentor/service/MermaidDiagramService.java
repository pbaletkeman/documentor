package com.documentor.service;

import com.documentor.model.CodeElement;
import com.documentor.model.CodeElementType;
import com.documentor.model.ProjectAnalysis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 🧩 Mermaid Diagram Service
 * 
 * Generates Mermaid class diagrams for non-private classes, methods, and variables.
 * Creates visual representations of code structure in Markdown format.
 */
@Service
public class MermaidDiagramService {

    private static final Logger logger = LoggerFactory.getLogger(MermaidDiagramService.class);

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
            
            // Group elements by file and class
            Map<String, List<CodeElement>> elementsByFile = analysis.getElementsByFile();
            
            for (Map.Entry<String, List<CodeElement>> fileEntry : elementsByFile.entrySet()) {
                String filePath = fileEntry.getKey();
                List<CodeElement> elements = fileEntry.getValue();
                
                // Filter for non-private classes
                List<CodeElement> classes = elements.stream()
                    .filter(e -> e.type() == CodeElementType.CLASS)
                    .filter(this::isNonPrivate)
                    .toList();
                
                for (CodeElement clazz : classes) {
                    try {
                        String diagramPath = generateClassDiagram(clazz, elements, filePath, outputPath);
                        if (diagramPath != null) {
                            generatedFiles.add(diagramPath);
                        }
                    } catch (Exception e) {
                        logger.error("❌ Failed to generate diagram for class {}: {}", 
                            clazz.qualifiedName(), e.getMessage(), e);
                    }
                }
            }
            
            logger.info("✅ Generated {} Mermaid diagrams", generatedFiles.size());
            return generatedFiles;
        });
    }

    /**
     * 🎨 Generates a Mermaid class diagram for a specific class
     */
    private String generateClassDiagram(
            CodeElement classElement, 
            List<CodeElement> allElements, 
            String sourceFilePath, 
            String customOutputPath) throws IOException {
        
        String className = classElement.name();
        logger.debug("🎨 Generating diagram for class: {}", className);
        
        // Determine output path
        String outputDir = determineOutputPath(sourceFilePath, customOutputPath);
        Path outputDirPath = Paths.get(outputDir);
        if (!Files.exists(outputDirPath)) {
            Files.createDirectories(outputDirPath);
        }
        
        String diagramFileName = className + "_diagram.md";
        Path diagramPath = outputDirPath.resolve(diagramFileName);
        
        // Generate Mermaid diagram content
        StringBuilder diagram = new StringBuilder();
        diagram.append("# ").append(className).append(" Class Diagram\n\n");
        diagram.append("```mermaid\n");
        diagram.append("classDiagram\n");
        
        // Add the main class
        addClassToMermaid(diagram, classElement, allElements);
        
        // Add relationships (if we can detect them from method parameters/return types)
        addRelationshipsToMermaid(diagram, classElement, allElements);
        
        diagram.append("```\n\n");
        diagram.append("Generated on: ").append(java.time.LocalDateTime.now()).append("\n");
        
        // Write to file
        Files.writeString(diagramPath, diagram.toString(), 
            StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        
        logger.debug("✅ Generated diagram: {}", diagramPath);
        return diagramPath.toString();
    }

    /**
     * 📐 Adds a class definition to the Mermaid diagram
     */
    private void addClassToMermaid(StringBuilder diagram, CodeElement classElement, List<CodeElement> allElements) {
        String className = sanitizeClassName(classElement.name());
        
        // Get all methods and fields for this class
        List<CodeElement> classMembers = allElements.stream()
            .filter(e -> e.qualifiedName().startsWith(classElement.qualifiedName()))
            .filter(this::isNonPrivate)
            .toList();
        
        diagram.append("    class ").append(className).append(" {\n");
        
        // Add non-private fields
        classMembers.stream()
            .filter(e -> e.type() == CodeElementType.FIELD)
            .forEach(field -> {
                String fieldSignature = sanitizeSignature(field.signature());
                diagram.append("        ").append(fieldSignature).append("\n");
            });
        
        // Add non-private methods
        classMembers.stream()
            .filter(e -> e.type() == CodeElementType.METHOD)
            .forEach(method -> {
                String methodSignature = sanitizeSignature(method.signature());
                diagram.append("        ").append(methodSignature).append("\n");
            });
        
        diagram.append("    }\n\n");
    }

    /**
     * 🔗 Adds relationships between classes to the Mermaid diagram
     */
    private void addRelationshipsToMermaid(StringBuilder diagram, CodeElement classElement, List<CodeElement> allElements) {
        // This is a simplified relationship detection
        // In a more sophisticated implementation, we would analyze imports, extends, implements, etc.
        
        String className = sanitizeClassName(classElement.name());
        
        // Look for method parameters that might reference other classes
        Set<String> referencedClasses = allElements.stream()
            .filter(e -> e.qualifiedName().startsWith(classElement.qualifiedName()))
            .filter(e -> e.type() == CodeElementType.METHOD)
            .flatMap(method -> method.parameters().stream())
            .map(this::extractClassNameFromParameter)
            .filter(refClass -> !refClass.isEmpty() && !refClass.equals(className))
            .collect(Collectors.toSet());
        
        for (String referencedClass : referencedClasses) {
            diagram.append("    ").append(className)
                   .append(" --> ").append(sanitizeClassName(referencedClass))
                   .append(" : uses\n");
        }
    }

    /**
     * 🏷️ Extracts class name from method parameter
     */
    private String extractClassNameFromParameter(String parameter) {
        // Simple extraction - look for capitalized words that might be class names
        String[] parts = parameter.split("\\s+");
        for (String part : parts) {
            if (part.matches("[A-Z][a-zA-Z0-9]*")) {
                return part;
            }
        }
        return "";
    }

    /**
     * 🧹 Sanitizes class name for Mermaid compatibility
     */
    private String sanitizeClassName(String className) {
        return className.replaceAll("[^a-zA-Z0-9_]", "_");
    }

    /**
     * 🧹 Sanitizes method/field signature for Mermaid compatibility
     */
    private String sanitizeSignature(String signature) {
        // Remove complex generics and packages for readability
        String cleaned = signature.replaceAll("<[^>]*>", "");
        cleaned = cleaned.replaceAll("\\b\\w+\\.", "");
        
        // Limit length for diagram readability
        if (cleaned.length() > 50) {
            cleaned = cleaned.substring(0, 47) + "...";
        }
        
        return cleaned;
    }

    /**
     * 📁 Determines the output path for the diagram
     */
    private String determineOutputPath(String sourceFilePath, String customOutputPath) {
        if (customOutputPath != null && !customOutputPath.trim().isEmpty()) {
            return customOutputPath;
        }
        
        // Default to same directory as source file
        Path sourcePath = Paths.get(sourceFilePath);
        return sourcePath.getParent() != null ? sourcePath.getParent().toString() : ".";
    }

    /**
     * 🔍 Checks if a code element is non-private
     */
    private boolean isNonPrivate(CodeElement element) {
        String signature = element.signature().toLowerCase();
        String name = element.name();
        
        // Check for explicit private modifier
        if (signature.contains("private")) {
            return false;
        }
        
        // Check for Python private convention (starting with underscore)
        if (name.startsWith("_")) {
            return false;
        }
        
        // Check for Java package-private (no explicit modifier)
        // This is a simplified check - a more sophisticated approach would parse the full AST
        return true;
    }
}