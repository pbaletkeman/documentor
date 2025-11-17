package com.documentor.model;

import java.util.List;

/**
 * 🔧 Code Element Model
 *
 * Represents a single code element (class, method, or field) discovered during
 * analysis. Contains all metadata needed for documentation generation and LLM
 * processing.
 */
public record CodeElement(
    CodeElementType type,
    String name,
    String qualifiedName,
    String filePath,
    int lineNumber,
    String signature,
    String documentation,
    List<String> parameters,
    List<String> annotations
) {

    /**
     * 🔍 Generates a unique identifier for this code element
     */
    public String getId() {
        return String.format("%s:%s:%d", filePath, qualifiedName, lineNumber);
    }

    /**
     * 🔍 Checks if this element is a public/non-private element
     */
    public boolean isPublic() {
        return !name.startsWith("_") // Python private convention
               && !signature.contains("private"); // Java private keyword
    }

    /**
     * 🔍 Returns a formatted string representation for logging
     */
    public String getDisplayName() {
        return String.format("%s %s (%s:%d)",
            type.getIcon(),
            qualifiedName,
            filePath.substring(filePath.lastIndexOf('/') + 1),
            lineNumber);
    }

    /**
     * 🔍 Creates a context string for LLM analysis
     */
    public String getAnalysisContext() {
        StringBuilder context = new StringBuilder();
        context.append(String.format("Type: %s\n", type.getDescription()));
        context.append(String.format("Name: %s\n", name));
        context.append(String.format("Signature: %s\n", signature));

        if (!parameters.isEmpty()) {
            context.append(String.format("Parameters: %s\n",
                    String.join(", ", parameters)));
        }

        if (!documentation.isEmpty()) {
            context.append(String.format("Documentation: %s\n", documentation));
        }

        if (!annotations.isEmpty()) {
            context.append(String.format("Annotations: %s\n",
                    String.join(", ", annotations)));
        }

        return context.toString();
    }
}
