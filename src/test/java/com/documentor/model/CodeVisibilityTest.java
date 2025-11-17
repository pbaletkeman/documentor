package com.documentor.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for CodeVisibility enum
 */
class CodeVisibilityTest {

    @Test
    @DisplayName("Should detect private visibility from signature")
    void testFromSignatureAndNamePrivate() {
        CodeVisibility result = CodeVisibility.fromSignatureAndName(
            "private void method()", "method");
        assertEquals(CodeVisibility.PRIVATE, result);

        // Test case insensitive
        result = CodeVisibility.fromSignatureAndName(
            "PRIVATE void method()", "method");
        assertEquals(CodeVisibility.PRIVATE, result);
    }

    @Test
    @DisplayName("Should detect protected visibility from signature")
    void testFromSignatureAndNameProtected() {
        CodeVisibility result = CodeVisibility.fromSignatureAndName(
            "protected void method()", "method");
        assertEquals(CodeVisibility.PROTECTED, result);

        // Test case insensitive
        result = CodeVisibility.fromSignatureAndName(
            "PROTECTED void method()", "method");
        assertEquals(CodeVisibility.PROTECTED, result);
    }

    @Test
    @DisplayName("Should detect public visibility from signature")
    void testFromSignatureAndNamePublic() {
        CodeVisibility result =
        CodeVisibility.fromSignatureAndName("public void method()", "method");
        assertEquals(CodeVisibility.PUBLIC, result);

        // Test case insensitive
        result = CodeVisibility.fromSignatureAndName(
            "PUBLIC void method()", "method");
        assertEquals(CodeVisibility.PUBLIC, result);
    }

    @Test
    @DisplayName("Should detect private visibility from"
    +   " Python naming convention")
    void testFromSignatureAndNamePythonPrivate() {
        // Python private convention with underscore
        CodeVisibility result = CodeVisibility.fromSignatureAndName(
            "def method(self):", "_private_method");
        assertEquals(CodeVisibility.PRIVATE, result);

        // Double underscore should also be private
        result = CodeVisibility.fromSignatureAndName(
            "def method(self):", "__private_method");
        assertEquals(CodeVisibility.PRIVATE, result);
    }

    @Test
    @DisplayName("Should default to package private when no modifiers found")
    void testFromSignatureAndNamePackagePrivate() {
        // No explicit modifiers, no underscore prefix
        CodeVisibility result = CodeVisibility.fromSignatureAndName(
            "void method()", "method");
        assertEquals(CodeVisibility.PACKAGE_PRIVATE, result);

        // Empty signature
        result = CodeVisibility.fromSignatureAndName("", "method");
        assertEquals(CodeVisibility.PACKAGE_PRIVATE, result);
    }

    @Test
    @DisplayName("Should prioritize explicit modifiers over naming"
        + " conventions")
    void testFromSignatureAndNameModifierPrecedence() {
        // Explicit public should override underscore naming
        CodeVisibility result = CodeVisibility.fromSignatureAndName(
            "public void method()", "_method");
        assertEquals(CodeVisibility.PUBLIC, result);

        // Explicit private should be detected even with public in name
        result = CodeVisibility.fromSignatureAndName(
            "private void publicMethod()", "publicMethod");
        assertEquals(CodeVisibility.PRIVATE, result);
    }

    @Test
    @DisplayName("Should include non-private elements when"
        + " includePrivate is false")
    void testShouldIncludeExcludePrivate() {
        assertFalse(CodeVisibility.PRIVATE.shouldInclude(false),
                "Private should be excluded when includePrivate is false");
        assertTrue(CodeVisibility.PUBLIC.shouldInclude(false),
                "Public should be included when includePrivate is false");
        assertTrue(CodeVisibility.PROTECTED.shouldInclude(false),
                "Protected should be included when includePrivate is false");
        assertTrue(CodeVisibility.PACKAGE_PRIVATE.shouldInclude(false),
                "Package private should be included when"
                + " includePrivate is false");
    }

    @Test
    @DisplayName("Should include all elements when includePrivate is true")
    void testShouldIncludeIncludePrivate() {
        assertTrue(CodeVisibility.PRIVATE.shouldInclude(true),
                "Private should be included when includePrivate is true");
        assertTrue(CodeVisibility.PUBLIC.shouldInclude(true),
                "Public should be included when includePrivate is true");
        assertTrue(CodeVisibility.PROTECTED.shouldInclude(true),
                "Protected should be included when includePrivate is true");
        assertTrue(CodeVisibility.PACKAGE_PRIVATE.shouldInclude(true),
                "Package private should be included "
                + "when includePrivate is true");
    }

    @Test
    @DisplayName("Should handle complex signatures and edge cases")
    void testFromSignatureAndNameComplexSignatures() {
        // Test that private is detected when present with other modifiers
        CodeVisibility result = CodeVisibility.fromSignatureAndName(
                "static private final void method()", "method");
        assertEquals(CodeVisibility.PRIVATE, result,
        "Should detect private modifier");

        // Complex Java signature with only public
        result = CodeVisibility.fromSignatureAndName(
                "public static synchronized void method() throws Exception",
                "method");
        assertEquals(CodeVisibility.PUBLIC, result);

        // Complex Python signature without explicit visibility
        result = CodeVisibility.fromSignatureAndName(
            "def calculate_value(self, param):", "calculate_value");
        assertEquals(CodeVisibility.PACKAGE_PRIVATE, result,
                "Should default to package private for regular method names");

        // Test with mixed case
        result = CodeVisibility.fromSignatureAndName(
            "PROTECTED void method()", "method");
        assertEquals(CodeVisibility.PROTECTED,
            result, "Should handle uppercase modifiers");
    }

    @Test
    @DisplayName("Should handle edge cases with null and empty inputs")
    void testFromSignatureAndNameEdgeCases() {
        // Null signature should not throw exception
        CodeVisibility result = CodeVisibility
            .fromSignatureAndName("", "method");
        assertEquals(CodeVisibility.PACKAGE_PRIVATE, result);

        // Method name without underscore prefix
        result = CodeVisibility
            .fromSignatureAndName("def method(self):", "method");
        assertEquals(CodeVisibility.PACKAGE_PRIVATE, result);

        // Empty method name (edge case)
        result = CodeVisibility.fromSignatureAndName("void method()", "");
        assertEquals(CodeVisibility.PACKAGE_PRIVATE, result);
    }
}
