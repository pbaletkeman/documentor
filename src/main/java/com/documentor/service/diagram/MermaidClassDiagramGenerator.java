package com.documentor.service.diagram;

import com.documentor.model.CodeElement;
import com.documentor.model.CodeElementType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

/**
 * 📐 Mermaid Class Diagram Generator
 * 
 * Specialized component for generating individual class diagrams in Mermaid format.
 * Handles the creation of class structure diagrams with fields and methods.
 */
@Component
public class MermaidClassDiagramGenerator {

    private static final Logger logger = LoggerFactory.getLogger(MermaidClassDiagramGenerator.class);

    /**
     * 📊 Generates a Mermaid class diagram for a single class
     */
    public String generateClassDiagram(CodeElement classElement, List<CodeElement> allElements, Path outputPath) throws IOException {
        String className = classElement.name();
        String diagramFileName = className + "_diagram.md";
        Path diagramPath = outputPath.resolve(diagramFileName);
        
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
        // In a full implementation, we would analyze method parameters, return types, and field types
        // to detect associations, dependencies, and inheritance relationships
        
        String className = sanitizeClassName(classElement.name());
        
        // Look for potential relationships in method signatures
        List<CodeElement> methods = allElements.stream()
            .filter(e -> e.type() == CodeElementType.METHOD)
            .filter(e -> e.qualifiedName().startsWith(classElement.qualifiedName()))
            .filter(this::isNonPrivate)
            .toList();
        
        methods.forEach(method -> {
            // Simple heuristic: if method signature contains another class name, add dependency
            String signature = method.signature();
            allElements.stream()
                .filter(e -> e.type() == CodeElementType.CLASS)
                .filter(e -> !e.name().equals(classElement.name()))
                .filter(e -> signature.contains(e.name()))
                .forEach(relatedClass -> {
                    String relatedClassName = sanitizeClassName(relatedClass.name());
                    diagram.append("    ").append(className)
                          .append(" --> ").append(relatedClassName).append(" : uses\n");
                });
        });
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