package com.documentor.config.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.regex.Pattern;

/**
 * 🔍 Diagram Naming Options
 *
 * Configuration model for customizing diagram file names with prefix, suffix,
 * and extension. Supports validation for allowed characters and length limits.
 */
public record DiagramNamingOptions(
        @JsonProperty("prefix")
        String prefix,

        @JsonProperty("suffix")
        String suffix,

        @JsonProperty("extension")
        String extension
) {
    // Validation pattern for file names: [0-9a-zA-Z- ()+._]
    private static final Pattern VALID_NAME_PATTERN =
            Pattern.compile("^[0-9a-zA-Z\\-\\s()+._]*$");
    private static final int MAX_PREFIX_SUFFIX_LENGTH = 20;
    private static final int MAX_EXTENSION_LENGTH = 10;

    /**
     * 🔍 Constructor with validation
     */
    public DiagramNamingOptions {
        // Validate prefix
        if (prefix != null && !prefix.isEmpty()) {
            if (prefix.length() > MAX_PREFIX_SUFFIX_LENGTH) {
                throw new IllegalArgumentException(
                    "Prefix must be less than " + MAX_PREFIX_SUFFIX_LENGTH
                        + " characters");
            }
            if (!VALID_NAME_PATTERN.matcher(prefix).matches()) {
                throw new IllegalArgumentException(
                    "Prefix contains invalid characters. "
                        + "Allowed: [0-9a-zA-Z- ()+._]");
            }
        }

        // Validate suffix
        if (suffix != null && !suffix.isEmpty()) {
            if (suffix.length() > MAX_PREFIX_SUFFIX_LENGTH) {
                throw new IllegalArgumentException(
                    "Suffix must be less than " + MAX_PREFIX_SUFFIX_LENGTH
                        + " characters");
            }
            if (!VALID_NAME_PATTERN.matcher(suffix).matches()) {
                throw new IllegalArgumentException(
                    "Suffix contains invalid characters. "
                        + "Allowed: [0-9a-zA-Z- ()+._]");
            }
        }

        // Validate extension
        if (extension != null && !extension.isEmpty()) {
            if (extension.length() > MAX_EXTENSION_LENGTH) {
                throw new IllegalArgumentException(
                    "Extension must be less than " + MAX_EXTENSION_LENGTH
                        + " characters");
            }
            if (!VALID_NAME_PATTERN.matcher(extension).matches()) {
                throw new IllegalArgumentException(
                    "Extension contains invalid characters. "
                        + "Allowed: [0-9a-zA-Z- ()+._]");
            }
        }
    }

    /**
     * 🔍 Creates options with safe values (invalid input ignored)
     */
    public static DiagramNamingOptions createSafe(final String prefixParam,
            final String suffixParam, final String extensionParam) {
        String safePrefix = validateAndSanitize(
            prefixParam, MAX_PREFIX_SUFFIX_LENGTH);
        String safeSuffix = validateAndSanitize(
            suffixParam, MAX_PREFIX_SUFFIX_LENGTH);
        String safeExtension = validateAndSanitize(
            extensionParam, MAX_EXTENSION_LENGTH);

        return new DiagramNamingOptions(safePrefix, safeSuffix, safeExtension);
    }

    /**
     * 🔍 Validates and sanitizes input
     */
    private static String validateAndSanitize(final String value,
            final int maxLength) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        String trimmed = value.trim();
        if (trimmed.length() > maxLength) {
            return null;
        }

        if (!VALID_NAME_PATTERN.matcher(trimmed).matches()) {
            return null;
        }

        return trimmed;
    }

    /**
     * 🔍 Gets the prefix or empty string
     */
    public String getPrefixOrEmpty() {
        return prefix != null ? prefix : "";
    }

    /**
     * 🔍 Gets the suffix or empty string
     */
    public String getSuffixOrEmpty() {
        return suffix != null ? suffix : "";
    }

    /**
     * 🔍 Gets the extension or default
     */
    public String getExtensionOrDefault(final String defaultExtension) {
        if (extension == null || extension.isEmpty()) {
            return defaultExtension;
        }
        return extension;
    }

    /**
     * 🔍 Checks if any naming option is configured
     */
    public boolean hasCustomNaming() {
        return (prefix != null && !prefix.isEmpty())
            || (suffix != null && !suffix.isEmpty())
            || (extension != null && !extension.isEmpty());
    }
}
