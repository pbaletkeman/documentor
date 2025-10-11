package com.documentor.service.diagram;

import com.documentor.model.CodeElement;
import com.documentor.model.CodeElementType;
import com.documentor.model.ProjectAnalysis;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 🔍 Diagram Element Filter
 *
 * Specialized component for filtering and organizing code elements for diagram generation.
 * Handles visibility rules and element grouping logic.
 */
@Component
public class DiagramElementFilter {

    /**
     * 📦 Groups elements by class for diagram generation
     */
    public Map<CodeElement, List<CodeElement>> groupElementsByClass(final ProjectAnalysis analysis) {
        // Get all non-private classes
        List<CodeElement> eligibleClasses = analysis.codeElements().stream()
            .filter(e -> e.type() == CodeElementType.CLASS)
            .filter(this::isNonPrivate)
            .toList();

        // Group all elements by their containing class
        return eligibleClasses.stream()
            .collect(Collectors.toMap(
                classElement -> classElement,
                classElement -> getElementsForClass(analysis.codeElements(), classElement)
            ));
    }

    /**
     * 🔍 Gets all eligible classes from the analysis
     */
    public List<CodeElement> getEligibleClasses(final ProjectAnalysis analysis) {
        return analysis.codeElements().stream()
            .filter(e -> e.type() == CodeElementType.CLASS)
            .filter(this::isNonPrivate)
            .toList();
    }

    /**
     * 📋 Gets all elements belonging to a specific class
     */
    public List<CodeElement> getElementsForClass(final List<CodeElement> allElements, final CodeElement classElement) {
        Set<String> classFiles = Set.of(classElement.filePath());

        return allElements.stream()
            .filter(e -> classFiles.contains(e.filePath()))
            .filter(this::isNonPrivate)
            .toList();
    }

    /**
     * 🔍 Checks if a code element is non-private and should be included in diagrams
     */
    public boolean isNonPrivate(final CodeElement element) {
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

    /**
     * 🗃️ Groups elements by file for file-based diagram generation
     */
    public Map<String, List<CodeElement>> groupElementsByFile(final ProjectAnalysis analysis) {
        return analysis.codeElements().stream()
            .filter(this::isNonPrivate)
            .collect(Collectors.groupingBy(CodeElement::filePath));
    }
}

