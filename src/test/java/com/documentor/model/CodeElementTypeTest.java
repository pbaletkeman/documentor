package com.documentor.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * 🧪 Unit tests for CodeElementType
 */
class CodeElementTypeTest {

    // Test constants for magic number violations
    private static final int EXPECTED_ENUM_COUNT = 3;

    @Test
    void testEnumValues() {
        // Test that all expected enum values exist
        assertNotNull(CodeElementType.CLASS);
        assertNotNull(CodeElementType.METHOD);
        assertNotNull(CodeElementType.FIELD);

        // Test enum contains expected number of values
        CodeElementType[] values = CodeElementType.values();
        assertEquals(EXPECTED_ENUM_COUNT, values.length);
    }

    @Test
    void testValueOf() {
        // Test valueOf method
        assertEquals(CodeElementType.CLASS, CodeElementType.valueOf("CLASS"));
        assertEquals(CodeElementType.METHOD, CodeElementType.valueOf("METHOD"));
        assertEquals(CodeElementType.FIELD, CodeElementType.valueOf("FIELD"));
    }

    @Test
    void testToString() {
        // Test toString method
        assertEquals("CLASS", CodeElementType.CLASS.toString());
        assertEquals("METHOD", CodeElementType.METHOD.toString());
        assertEquals("FIELD", CodeElementType.FIELD.toString());
    }

    @Test
    void testOrdinal() {
        // Test ordinal values
        assertTrue(CodeElementType.CLASS.ordinal() >= 0);
        assertTrue(CodeElementType.METHOD.ordinal() >= 0);
        assertTrue(CodeElementType.FIELD.ordinal() >= 0);

        // Test that ordinals are different
        assertNotEquals(CodeElementType.CLASS.ordinal(), CodeElementType.METHOD.ordinal());
        assertNotEquals(CodeElementType.METHOD.ordinal(), CodeElementType.FIELD.ordinal());
    }

    @Test
    void testGetIcon() {
        // Test icon values
        assertEquals("📦", CodeElementType.CLASS.getIcon());
        assertEquals("🔧", CodeElementType.METHOD.getIcon());
        assertEquals("📊", CodeElementType.FIELD.getIcon());
    }

    @Test
    void testGetDescription() {
        // Test description values
        assertEquals("Class/Interface", CodeElementType.CLASS.getDescription());
        assertEquals("Method/Function", CodeElementType.METHOD.getDescription());
        assertEquals("Field/Variable", CodeElementType.FIELD.getDescription());
    }

    @Test
    void testEnumProperties() {
        // Test that each enum has non-null icon and description
        for (CodeElementType type : CodeElementType.values()) {
            assertNotNull(type.getIcon());
            assertNotNull(type.getDescription());
            assertFalse(type.getIcon().isEmpty());
            assertFalse(type.getDescription().isEmpty());
        }
    }
}

