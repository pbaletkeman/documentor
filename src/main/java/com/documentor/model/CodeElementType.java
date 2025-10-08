package com.documentor.model;

/**
 * 🏷️ Code Element Type Enumeration
 * 
 * Represents the different types of code elements that can be analyzed:
 * - CLASS: Classes, interfaces, enums
 * - METHOD: Methods, functions, procedures
 * - FIELD: Variables, attributes, constants
 */
public enum CodeElementType {
    CLASS("📦", "Class/Interface"),
    METHOD("🔧", "Method/Function"),
    FIELD("📊", "Field/Variable");

    private final String icon;
    private final String description;

    CodeElementType(String icon, String description) {
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