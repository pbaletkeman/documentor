package com.documentor.service.diagram;

import com.documentor.constants.ApplicationConstants;
import com.documentor.model.CodeElement;
import com.documentor.model.CodeElementType;
import com.documentor.model.CodeVisibility;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(MermaidClassDiagramGenerator.class);

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

        LOGGER.debug("✅ Generated diagram: {}", diagramPath);
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

        // Limit length for diagram readability using constants
        if (cleaned.length() > ApplicationConstants.MAX_SIGNATURE_LENGTH) {
            cleaned = cleaned.substring(0, ApplicationConstants.MAX_SIGNATURE_LENGTH - ApplicationConstants.TRUNCATE_SUFFIX_LENGTH) + "...";
        }

        return cleaned;
    }

    /**
     * 🔍 Simplified visibility check using enum
     */
    private boolean isNonPrivate(CodeElement element) {
        CodeVisibility visibility = CodeVisibility.fromSignatureAndName(element.signature(), element.name());
        return visibility.shouldInclude(false); // Don't include private elements in diagrams
    }
}