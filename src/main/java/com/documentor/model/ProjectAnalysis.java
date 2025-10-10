package com.documentor.model;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ðŸ“Š Project Analysis Result
 *
 * Contains the complete analysis results for a project, including all discovered
 * code elements organized by type and file.
 */
public record ProjectAnalysis(
    String projectPath,
    List<CodeElement> codeElements,
    long timestamp
) {

    /**
     * ðŸ“¦ Gets all classes found in the project
     */
    public List<CodeElement> getClasses() {
        return codeElements.stream()
                .filter(element -> element.type() == CodeElementType.CLASS)
                .toList();
    }

    /**
     * ðŸ”§ Gets all methods found in the project
     */
    public List<CodeElement> getMethods() {
        return codeElements.stream()
                .filter(element -> element.type() == CodeElementType.METHOD)
                .toList();
    }

    /**
     * ðŸ“Š Gets all fields found in the project
     */
    public List<CodeElement> getFields() {
        return codeElements.stream()
                .filter(element -> element.type() == CodeElementType.FIELD)
                .toList();
    }

    /**
     * ðŸ“ Groups code elements by file path
     */
    public Map<String, List<CodeElement>> getElementsByFile() {
        return codeElements.stream()
                .collect(Collectors.groupingBy(CodeElement::filePath));
    }

    /**
     * ðŸ·ï¸ Groups code elements by type
     */
    public Map<CodeElementType, List<CodeElement>> getElementsByType() {
        return codeElements.stream()
                .collect(Collectors.groupingBy(CodeElement::type));
    }

    /**
     * ðŸ“ˆ Gets analysis statistics
     */
    public AnalysisStats getStats() {
        Map<CodeElementType, Long> counts = codeElements.stream()
                .collect(Collectors.groupingBy(
                    CodeElement::type,
                    Collectors.counting()
                ));

        long fileCount = codeElements.stream()
                .map(CodeElement::filePath)
                .distinct()
                .count();

        return new AnalysisStats(
            codeElements.size(),
            counts.getOrDefault(CodeElementType.CLASS, 0L).intValue(),
            counts.getOrDefault(CodeElementType.METHOD, 0L).intValue(),
            counts.getOrDefault(CodeElementType.FIELD, 0L).intValue(),
            (int) fileCount
        );
    }

    /**
     * ðŸ“Š Analysis Statistics Record
     */
    public record AnalysisStats(
        int totalElements,
        int classCount,
        int methodCount,
        int fieldCount,
        int fileCount
    ) {
        public String getFormattedSummary() {
            return String.format(
                "ðŸ“Š Analysis Summary: %d total elements (%d classes, %d methods, %d fields) across %d files",
                totalElements, classCount, methodCount, fieldCount, fileCount
            );
        }
    }
}
