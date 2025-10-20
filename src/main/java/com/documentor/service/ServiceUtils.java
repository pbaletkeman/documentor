package com.documentor.service;

import com.documentor.model.CodeElement;
import com.documentor.model.CodeElementType;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service utility class providing common helper methods for service layer
 * operations. This class contains utility methods used across different
 * service implementations.
 */
public final class ServiceUtils {

    /**
     * Service package name constant.
     */
    public static final String SERVICE_PACKAGE = "com.documentor.service";

    private static final int MAX_TIMEOUT_MS = 300000; // 5 minutes
    private static final int TIMEOUT_PER_ELEMENT_MS = 1000;
    private static final long MIN_SLEEP_TIME_MS = 1000L;
    private static final long MAX_SLEEP_TIME_MS = 10000L;

    /**
     * Default timeout for async operations (milliseconds).
     */
    public static final int DEFAULT_ASYNC_TIMEOUT = 30000;

    /**
     * Maximum retry attempts for service operations.
     */
    public static final int MAX_RETRY_ATTEMPTS = 3;

    /**
     * Documentation types supported by services.
     */
    public static final String DOC_TYPE_JAVADOC = "javadoc";
    public static final String DOC_TYPE_MARKDOWN = "markdown";
    public static final String DOC_TYPE_PLAIN = "plain";

    /**
     * Private constructor to prevent instantiation.
     */
    private ServiceUtils() {
        throw new UnsupportedOperationException(
                "Utility class cannot be instantiated");
    }

    /**
     * Filters code elements by type.
     *
     * @param elements the list of code elements to filter
     * @param type the type to filter by
     * @return filtered list of code elements
     */
    public static List<CodeElement> filterByType(
            final List<CodeElement> elements,
            final CodeElementType type) {
        if (elements == null || elements.isEmpty()) {
            return List.of();
        }

        if (type == null) {
            return List.copyOf(elements);
        }

        return elements.stream()
            .filter(element -> element != null && type.equals(element.type()))
            .collect(Collectors.toList());
    }

    /**
     * Groups code elements by their type.
     *
     * @param elements the list of code elements to group
     * @return map of elements grouped by type
     */
    public static Map<CodeElementType, List<CodeElement>> groupByType(
            final List<CodeElement> elements) {
        if (elements == null || elements.isEmpty()) {
            return Map.of();
        }

        return elements.stream()
            .filter(element -> element != null && element.type() != null)
            .collect(Collectors.groupingBy(CodeElement::type));
    }

    /**
     * Validates if a service timeout value is within acceptable bounds.
     *
     * @param timeoutMs the timeout value in milliseconds
     * @return true if timeout is valid
     */
    public static boolean isValidTimeout(final Integer timeoutMs) {
        if (timeoutMs == null) {
            return false;
        }

        return timeoutMs > 0 && timeoutMs <= MAX_TIMEOUT_MS; // Max 5 minutes
    }

    /**
     * Calculates adaptive timeout based on element count.
     *
     * @param elementCount the number of elements to process
     * @return adaptive timeout in milliseconds
     */
    public static int calculateAdaptiveTimeout(final int elementCount) {
        if (elementCount <= 0) {
            return DEFAULT_ASYNC_TIMEOUT;
        }

        // Base timeout + additional time per element
        int adaptiveTimeout = DEFAULT_ASYNC_TIMEOUT
                + (elementCount * TIMEOUT_PER_ELEMENT_MS);

        // Cap at maximum timeout
        return Math.min(adaptiveTimeout, MAX_TIMEOUT_MS);
    }

    /**
     * Sanitizes file paths for cross-platform compatibility.
     *
     * @param filePath the file path to sanitize
     * @return sanitized file path
     */
    public static String sanitizeFilePath(final String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return "";
        }

        String sanitized = filePath.trim()
            .replace('\\', '/')  // Normalize to forward slashes
            .replaceAll("/+", "/");  // Remove duplicate slashes

        // Remove leading slash if present
        if (sanitized.startsWith("/")) {
            sanitized = sanitized.substring(1);
        }

        return sanitized;
    }

    /**
     * Checks if a documentation type is supported.
     *
     * @param docType the documentation type to check
     * @return true if the documentation type is supported
     */
    public static boolean isSupportedDocType(final String docType) {
        if (docType == null || docType.trim().isEmpty()) {
            return false;
        }

        String normalized = docType.trim().toLowerCase();
        return DOC_TYPE_JAVADOC.equals(normalized)
               || DOC_TYPE_MARKDOWN.equals(normalized)
               || DOC_TYPE_PLAIN.equals(normalized);
    }

    /**
     * Creates a display name for a code element.
     *
     * @param element the code element
     * @return formatted display name
     */
    public static String createDisplayName(final CodeElement element) {
        if (element == null) {
            return "Unknown Element";
        }

        if (element.name() == null || element.name().isEmpty()) {
            return element.type() != null ? element.type().toString()
                    : "Unknown Element";
        }

        return String.format("[%s] %s",
            element.type() != null ? element.type() : "UNKNOWN",
            element.name());
    }

    /**
     * Validates service operation parameters.
     *
     * @param operation the operation name
     * @param parameters the parameters to validate
     * @return true if parameters are valid
     */
    public static boolean validateOperationParameters(final String operation,
            final Map<String, Object> parameters) {
        if (operation == null || operation.trim().isEmpty()) {
            return false;
        }

        if (parameters == null) {
            return true; // Null parameters are acceptable for some operations
        }

        // Check for null values in required parameters
        return parameters.values().stream()
            .noneMatch(value -> value == null
                    && isRequiredParameter(operation, parameters));
    }

    /**
     * Checks if any parameter is required for the given operation.
     *
     * @param operation the operation name
     * @param parameters the parameter map
     * @return true if required parameters are present
     */
    private static boolean isRequiredParameter(final String operation,
            final Map<String, Object> parameters) {
        // Simple heuristic: operations with "generate" typically require
        // non-null parameters
        return operation.toLowerCase().contains("generate")
                && !parameters.isEmpty();
    }

    /**
     * Formats error messages for service exceptions.
     *
     * @param serviceName the name of the service
     * @param operation the operation that failed
     * @param cause the underlying cause
     * @return formatted error message
     */
    public static String formatErrorMessage(final String serviceName,
                                            final String operation,
                                            final String cause) {
        StringBuilder sb = new StringBuilder();

        if (serviceName != null && !serviceName.isEmpty()) {
            sb.append("[").append(serviceName).append("] ");
        }

        if (operation != null && !operation.isEmpty()) {
            sb.append("Operation '").append(operation).append("' failed");
        } else {
            sb.append("Operation failed");
        }

        if (cause != null && !cause.isEmpty()) {
            sb.append(": ").append(cause);
        }

        return sb.toString();
    }

    /**
     * Gets the retry delay for a given attempt number.
     *
     * @param attemptNumber the current attempt number (1-based)
     * @return delay in milliseconds
     */
    public static long getRetryDelay(final int attemptNumber) {
        if (attemptNumber <= 1) {
            return 0;
        }

        // Exponential backoff: 1s, 2s, 4s, 8s...
        return Math.min(MIN_SLEEP_TIME_MS * (1L << (attemptNumber - 1)),
                MAX_SLEEP_TIME_MS);
    }
}
