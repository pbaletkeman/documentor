package com.documentor.model;

/**
 * 🔐 Code Visibility Levels - Simplified visibility detection
 *
 * Enum to reduce complexity in visibility checking across different languages.
 */
public enum CodeVisibility {
    PUBLIC,
    PROTECTED,
    PACKAGE_PRIVATE,
    PRIVATE;

    /**
     * 🔍 Determines visibility from signature and element name
     */
    public static CodeVisibility fromSignatureAndName(final String signature, final String name) {
        String lowerSignature = signature.toLowerCase();

        // Check explicit modifiers first
        if (lowerSignature.contains("private")) {
            return PRIVATE;
        }
        if (lowerSignature.contains("protected")) {
            return PROTECTED;
        }
        if (lowerSignature.contains("public")) {
            return PUBLIC;
        }

        // Check Python conventions
        if (name.startsWith("_")) {
            return PRIVATE;
        }

        // Default to package-private for Java, public for Python
        return PACKAGE_PRIVATE;
    }

    /**
     * 🚫 Check if visibility should be included in documentation
     */
    public boolean shouldInclude(final boolean includePrivate) {
        return includePrivate || this != PRIVATE;
    }
}