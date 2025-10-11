package com.documentor.constants;

/**
 * 🔍 Application Constants - Centralized constants to reduce complexity
 *
 * Contains commonly used string literals, magic numbers, and configuration defaults
 * to improve maintainability and reduce duplication.
 */
public final class ApplicationConstants {

    // File Extensions
    public static final String JAVA_EXTENSION = ".java";
    public static final String PYTHON_EXTENSION = ".py";
    public static final String MARKDOWN_EXTENSION = ".md";

    // Configuration Defaults
    public static final String DEFAULT_CONFIG_FILE = "config.json";
    public static final String DEFAULT_OUTPUT_FORMAT = "markdown";
    public static final double DEFAULT_COVERAGE_THRESHOLD = 0.80;

    // Documentation
    public static final String DOCS_DIRECTORY = "docs";
    public static final String ELEMENTS_DIRECTORY = "elements";

    // Patterns
    public static final String WILDCARD_PATTERN = "*";
    public static final String REGEX_REPLACEMENT = ".*";

    // Status Messages
    public static final String SUCCESS_PREFIX = "✅";
    public static final String ERROR_PREFIX = "❌";
    public static final String WARNING_PREFIX = "⚠️";
    public static final String INFO_PREFIX = "ℹ️";

    // Limit Values
    public static final int MAX_SIGNATURE_LENGTH = 50;
    public static final int TRUNCATE_SUFFIX_LENGTH = 3; // for "..."
    public static final int API_KEY_PREVIEW_LENGTH = 10; // for status display

    // Python parsing constants
    public static final int MIN_INDENTATION_SPACES = 4;
    public static final int FUNCTION_DEF_PREFIX_LENGTH = 3; // "def"
    public static final int CLASS_DEF_PREFIX_LENGTH = 5; // "class"

    // Default Configuration Values
    public static final int DEFAULT_MAX_TOKENS = 4096;
    public static final int DEFAULT_TIMEOUT_SECONDS = 30;
    public static final int DEFAULT_MAX_DEPTH = 10;
    public static final String DEFAULT_OLLAMA_PORT = "11434";

    // Coverage and formatting
    public static final int PERCENTAGE_MULTIPLIER = 100;

    // Array indexing constants for parsing
    public static final int PARAMETERS_ARRAY_INDEX = 4;
    public static final int MINIMUM_PARTS_FOR_PARSING = 4;

    private ApplicationConstants() {
        // Utility class - prevent instantiation
    }
}

