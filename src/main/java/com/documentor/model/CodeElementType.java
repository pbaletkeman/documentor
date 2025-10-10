package com.documentor.model;

/**
 * ðŸ·ï¸ Code Element Type Enumeration
 *
 * Represents the different types of code elements that can be analyzed:
 * - CLASS: Classes, interfaces, enums
 * - METHOD: Methods, functions, procedures
 * - FIELD: Variables, attributes, constants
 */
public enum CodeElementType {
    CLASS("ðŸ“¦", "Class/Interface"),
    METHOD("ðŸ”§", "Method/Function"),
    FIELD("ðŸ“Š", "Field/Variable");

    private final String icon;
    private final String description;

    CodeElementType(final String icon, final String description) {
        this.icon = icon;
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public String getDescription() {
        return description;
    }
}
