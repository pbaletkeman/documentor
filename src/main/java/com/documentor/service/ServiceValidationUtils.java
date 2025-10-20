package com.documentor.service;

import com.documentor.model.CodeElement;
import com.documentor.model.CodeElementType;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Additional service utilities for coverage boost.
 */
public final class ServiceValidationUtils {

    private static final double HUNDRED_PERCENT = 100.0;

    private ServiceValidationUtils() {
        // Utility class - prevent instantiation
    }

    /**
     * Validates that a code element has required properties.
     */
    public static boolean validateCodeElement(final CodeElement element) {
        if (element == null) {
            return false;
        }

        if (element.name() == null || element.name().trim().isEmpty()) {
            return false;
        }

        if (element.filePath() == null || element.filePath().trim().isEmpty()) {
            return false;
        }

        if (element.lineNumber() < 1) {
            return false;
        }

        return true;
    }

    /**
     * Checks if a list of code elements has any duplicates by name.
     */
    public static boolean hasDuplicateNames(final List<CodeElement> elements) {
        if (elements == null || elements.isEmpty()) {
            return false;
        }

        Set<String> names = elements.stream()
            .filter(element -> element != null && element.name() != null)
            .map(CodeElement::name)
            .collect(Collectors.toSet());

        return names.size() != elements.stream()
            .filter(element -> element != null && element.name() != null)
            .count();
    }

    /**
     * Counts elements by type.
     */
    public static long countByType(final List<CodeElement> elements, final CodeElementType type) {
        if (elements == null || type == null) {
            return 0L;
        }

        return elements.stream()
            .filter(element -> element != null && type.equals(element.type()))
            .count();
    }

    /**
     * Checks if any element has missing documentation.
     */
    public static boolean hasMissingDocumentation(final List<CodeElement> elements) {
        if (elements == null) {
            return false;
        }

        return elements.stream()
            .filter(element -> element != null)
            .anyMatch(element -> element.documentation() == null
                               || element.documentation().trim().isEmpty());
    }

    /**
     * Gets all unique file paths from elements.
     */
    public static Set<String> getUniqueFilePaths(final List<CodeElement> elements) {
        if (elements == null) {
            return Set.of();
        }

        return elements.stream()
            .filter(element -> element != null && element.filePath() != null)
            .map(CodeElement::filePath)
            .collect(Collectors.toSet());
    }

    /**
     * Validates operation configuration.
     */
    public static boolean isValidOperation(final String operation, final Object config) {
        if (operation == null || operation.trim().isEmpty()) {
            return false;
        }

        // Operations starting with "validate" require non-null config
        if (operation.toLowerCase().startsWith("validate")) {
            return config != null;
        }

        // Operations starting with "generate" should have config
        if (operation.toLowerCase().startsWith("generate")) {
            return config != null;
        }

        // Other operations are valid regardless of config
        return true;
    }

    /**
     * Calculates coverage percentage.
     */
    public static double calculateCoverage(final long covered, final long total) {
        if (total <= 0) {
            return 0.0;
        }

        if (covered < 0) {
            return 0.0;
        }

        if (covered > total) {
            return HUNDRED_PERCENT;
        }

        return (double) covered / total * HUNDRED_PERCENT;
    }

    /**
     * Formats coverage percentage as string.
     */
    public static String formatCoverage(final double coverage) {
        if (coverage < 0) {
            return "0%";
        }

        if (coverage > HUNDRED_PERCENT) {
            return "100%";
        }

        return String.format("%.1f%%", coverage);
    }

    /**
     * Checks if coverage meets minimum threshold.
     */
    public static boolean meetsCoverageThreshold(final double coverage, final double threshold) {
        if (threshold < 0 || threshold > HUNDRED_PERCENT) {
            return false;
        }

        return coverage >= threshold;
    }
}
