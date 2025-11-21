package com.documentor.service.diagram;

import com.documentor.config.model.DiagramNamingOptions;
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
 * 📊 PlantUML Class Diagram Generator
 *
 * Specialized component for generating individual class diagrams
 * in PlantUML format.
 * Handles the creation of class structure diagrams with fields and methods.
 */
@Component
public class PlantUMLClassDiagramGenerator {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(
                    PlantUMLClassDiagramGenerator.class);

    private final DiagramPathManager pathManager;

    public PlantUMLClassDiagramGenerator(
            final DiagramPathManager pathManagerParam) {
        this.pathManager = pathManagerParam;
    }

    /**
     * 📊 Generates a PlantUML class diagram for a single class
     */
    public String generateClassDiagram(final CodeElement classElement,
            final List<CodeElement> allElements,
            final Path outputPath) throws IOException {
        return generateClassDiagram(classElement, allElements, outputPath,
                null);
    }

    /**
     * 📊 Generates a PlantUML class diagram with custom naming options
     */
    public String generateClassDiagram(final CodeElement classElement,
            final List<CodeElement> allElements, final Path outputPath,
            final DiagramNamingOptions namingOptions) throws IOException {
        String className = classElement.name();
        String diagramFileName = pathManager.generateDiagramFileName(
            className, namingOptions, "plantuml");
        Path diagramPath = outputPath.resolve(diagramFileName);

        // Generate PlantUML diagram content
        StringBuilder diagram = new StringBuilder();
        diagram.append("@startuml ").append(className).append("\n");
        diagram.append("!theme plain\n");
        diagram.append("title ").append(className).append(" Class Diagram\n\n");

        // Add the main class
        addClassToPlantUML(diagram, classElement, allElements);

        // Add relationships (if we can detect them from method
        // parameters/return types)
        addRelationshipsToPlantUML(diagram, classElement, allElements);

        diagram.append("\n@enduml\n");

        // Write to file
        Files.writeString(diagramPath, diagram.toString(),
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING);

        LOGGER.debug("✅ Generated PlantUML diagram: {}", diagramPath);
        return diagramPath.toString();
    }

    /**
     * 🔍 Adds a class definition to the PlantUML diagram
     */
    private void addClassToPlantUML(final StringBuilder diagram,
            final CodeElement classElement,
            final List<CodeElement> allElements) {
        String className = sanitizeClassName(classElement.name());

        // Get all methods and fields for this class
        List<CodeElement> classMembers = allElements.stream()
                .filter(e -> e.qualifiedName()
                        .startsWith(classElement.qualifiedName()))
                .filter(this::isNonPrivate)
                .toList();

        // Determine class type
        String classType = determineClassType(classElement);
        diagram.append(classType).append(" ").append(className).append(" {\n");

        // Add fields
        classMembers.stream()
            .filter(e -> e.type() == CodeElementType.FIELD)
            .forEach(field -> addFieldToPlantUML(diagram, field));

        // Add separator if we have both fields and methods
        boolean hasFields = classMembers.stream()
                .anyMatch(e -> e.type() == CodeElementType.FIELD);
        boolean hasMethods = classMembers.stream()
                .anyMatch(e -> e.type() == CodeElementType.METHOD);
        if (hasFields && hasMethods) {
            diagram.append("  --\n");
        }

        // Add methods
        classMembers.stream()
            .filter(e -> e.type() == CodeElementType.METHOD)
            .forEach(method -> addMethodToPlantUML(diagram, method));

        diagram.append("}\n\n");
    }

    /**
     * 🔍 Determines the PlantUML class type (class, interface, abstract, etc.)
     */
    private String determineClassType(final CodeElement classElement) {
        String signature = classElement.signature().toLowerCase();
        if (signature.contains("interface")) {
            return "interface";
        }
        if (signature.contains("abstract")) {
            return "abstract class";
        }
        if (signature.contains("enum")) {
            return "enum";
        }
        return "class";
    }

    /**
     * 🔍 Adds a field to the PlantUML diagram
     */
    private void addFieldToPlantUML(final StringBuilder diagram,
            final CodeElement field) {
        String visibility = mapVisibilityToPlantUML(field);
        String fieldType = extractReturnType(field.signature());
        String fieldName = field.name();

        diagram.append("  ").append(visibility).append(" ")
            .append(fieldType).append(" ").append(fieldName).append("\n");
    }

    /**
     * 🔍 Adds a method to the PlantUML diagram
     */
    private void addMethodToPlantUML(final StringBuilder diagram,
            final CodeElement method) {
        String visibility = mapVisibilityToPlantUML(method);
        String methodName = method.name();
        String returnType = extractReturnType(method.signature());
        String parameters = extractParameters(method.signature());

        diagram.append("  ").append(visibility).append(" ")
            .append(returnType).append(" ").append(methodName)
            .append("(").append(parameters).append(")\n");
    }

    /**
     * 🔍 Maps element to PlantUML visibility symbols based on signature analysis
     */
    private String mapVisibilityToPlantUML(final CodeElement element) {
        String signature = element.signature().toLowerCase();
        if (signature.contains("private")) {
            return "-";
        }
        if (signature.contains("protected")) {
            return "#";
        }
        if (signature.contains("public")) {
            return "+";
        }
        // Default to package-private if no visibility modifier found
        return "~";
    }

    /**
     * 🔍 Adds relationships to the PlantUML diagram
     */
    private void addRelationshipsToPlantUML(final StringBuilder diagram,
            final CodeElement classElement,
            final List<CodeElement> allElements) {
        String className = sanitizeClassName(classElement.name());

        // Look for relationships based on method parameters and return types
        allElements.stream()
                .filter(e -> e.qualifiedName()
                        .startsWith(classElement.qualifiedName()))
                .filter(e -> e.type() == CodeElementType.METHOD)
            .forEach(method -> {
                String signature = method.signature();
                // Simple relationship detection - look for other classes
                // in parameters/return types
                allElements.stream()
                    .filter(other -> other.type() == CodeElementType.CLASS)
                    .filter(other -> !other.equals(classElement))
                    .forEach(other -> {
                        String otherClassName = sanitizeClassName(other.name());
                        if (signature.contains(other.name())) {
                            // Add a simple dependency relationship
                            diagram.append(className)
                                    .append(" ..> ")
                                    .append(otherClassName)
                                    .append(" : uses\n");
                        }
                    });
            });
    }

    /**
     * 🔍 Extracts return type from method signature
     */
    private String extractReturnType(final String signature) {
        if (signature == null || signature.isEmpty()) {
            return "void";
        }

        // Simple extraction - look for pattern before method name
        String[] parts = signature.split("\\s+");
        if (parts.length >= 2) {
            // Skip visibility modifiers
            for (String part : parts) {
                if (!part.equals("public") && !part.equals("private")
                    && !part.equals("protected") && !part.equals("static")
                    && !part.equals("final") && !part.contains("(")) {
                    return sanitizeType(part);
                }
            }
        }
        return "void";
    }

    /**
     * 🔍 Extracts parameters from method signature
     */
    private String extractParameters(final String signature) {
        if (signature == null || !signature.contains("(")) {
            return "";
        }

        int startIndex = signature.indexOf('(');
        int endIndex = signature.lastIndexOf(')');
        if (startIndex != -1 && endIndex != -1
                && endIndex > startIndex) {
            String params = signature.substring(startIndex + 1, endIndex)
                    .trim();
            if (params.isEmpty()) {
                return "";
            }
            // Simplify parameters for diagram readability
            return params.replaceAll("\\s+", " ");
        }
        return "";
    }

    /**
     * 🔍 Sanitizes class name for PlantUML compatibility
     */
    private String sanitizeClassName(final String name) {
        if (name == null) {
            return "Unknown";
        }
        // Remove generic type parameters and special characters
        return name.replaceAll("[<>\\[\\]{}]", "")
            .replaceAll("\\s+", "_")
            .replaceAll("[^a-zA-Z0-9_]", "");
    }

    /**
     * 🔍 Sanitizes type name for PlantUML compatibility
     */
    private String sanitizeType(final String type) {
        if (type == null) {
            return "Object";
        }
        // Simplify complex types
        return type.replaceAll("[<>\\[\\]{}]", "")
            .replaceAll("\\s+", "")
            .replaceAll("^.*\\.", ""); // Remove package prefixes
    }

    /**
     * 🔍 Checks if element should be included (non-private)
     */
    private boolean isNonPrivate(final CodeElement element) {
        return element.isPublic()
                || !element.signature().toLowerCase().contains("private");
    }
}
