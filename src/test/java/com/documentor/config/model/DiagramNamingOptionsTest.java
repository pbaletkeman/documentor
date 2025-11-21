package com.documentor.config.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Unit tests for DiagramNamingOptions.
 */
class DiagramNamingOptionsTest {

    private static final int MAX_PREFIX_LENGTH = 20;
    private static final int MAX_SUFFIX_LENGTH = 20;
    private static final int MAX_EXTENSION_LENGTH = 10;

    @Test
    void testConstructorWithValidInputs() {
        DiagramNamingOptions options = new DiagramNamingOptions(
            "prefix-",
            "-suffix",
            "uml"
        );

        assertEquals("prefix-", options.prefix());
        assertEquals("-suffix", options.suffix());
        assertEquals("uml", options.extension());
    }

    @Test
    void testConstructorWithNullValues() {
        DiagramNamingOptions options = new DiagramNamingOptions(
            null,
            null,
            null
        );

        assertNull(options.prefix());
        assertNull(options.suffix());
        assertNull(options.extension());
    }

    @Test
    void testConstructorWithEmptyStrings() {
        DiagramNamingOptions options = new DiagramNamingOptions(
            "",
            "",
            ""
        );

        assertEquals("", options.prefix());
        assertEquals("", options.suffix());
        assertEquals("", options.extension());
    }

    @Test
    void testPrefixTooLong() {
        String longPrefix = "a".repeat(MAX_PREFIX_LENGTH + 1);
        assertThrows(IllegalArgumentException.class, () ->
            new DiagramNamingOptions(longPrefix, null, null)
        );
    }

    @Test
    void testSuffixTooLong() {
        String longSuffix = "b".repeat(MAX_SUFFIX_LENGTH + 1);
        assertThrows(IllegalArgumentException.class, () ->
            new DiagramNamingOptions(null, longSuffix, null)
        );
    }

    @Test
    void testExtensionTooLong() {
        String longExtension = "c".repeat(MAX_EXTENSION_LENGTH + 1);
        assertThrows(IllegalArgumentException.class, () ->
            new DiagramNamingOptions(null, null, longExtension)
        );
    }

    @Test
    void testPrefixWithInvalidCharacters() {
        assertThrows(IllegalArgumentException.class, () ->
            new DiagramNamingOptions("prefix@#$", null, null)
        );
    }

    @Test
    void testSuffixWithInvalidCharacters() {
        assertThrows(IllegalArgumentException.class, () ->
            new DiagramNamingOptions(null, "suffix!@#", null)
        );
    }

    @Test
    void testExtensionWithInvalidCharacters() {
        assertThrows(IllegalArgumentException.class, () ->
            new DiagramNamingOptions(null, null, "ext*&^")
        );
    }

    @Test
    void testValidCharactersInPrefix() {
        assertDoesNotThrow(() ->
            new DiagramNamingOptions("2025-API-", null, null)
        );
        assertDoesNotThrow(() ->
            new DiagramNamingOptions("arch (new)", null, null)
        );
        assertDoesNotThrow(() ->
            new DiagramNamingOptions("v1.0_", null, null)
        );
    }

    @Test
    void testValidCharactersInSuffix() {
        assertDoesNotThrow(() ->
            new DiagramNamingOptions(null, "_v2", null)
        );
        assertDoesNotThrow(() ->
            new DiagramNamingOptions(null, " (fixed)", null)
        );
        assertDoesNotThrow(() ->
            new DiagramNamingOptions(null, "+beta", null)
        );
    }

    @Test
    void testValidCharactersInExtension() {
        assertDoesNotThrow(() ->
            new DiagramNamingOptions(null, null, "uml")
        );
        assertDoesNotThrow(() ->
            new DiagramNamingOptions(null, null, "md")
        );
        assertDoesNotThrow(() ->
            new DiagramNamingOptions(null, null, "plantuml")
        );
    }

    @Test
    void testGetPrefixOrEmpty() {
        DiagramNamingOptions withPrefix = new DiagramNamingOptions(
            "prefix-", null, null);
        assertEquals("prefix-", withPrefix.getPrefixOrEmpty());

        DiagramNamingOptions withoutPrefix = new DiagramNamingOptions(
            null, null, null);
        assertEquals("", withoutPrefix.getPrefixOrEmpty());
    }

    @Test
    void testGetSuffixOrEmpty() {
        DiagramNamingOptions withSuffix = new DiagramNamingOptions(
            null, "-suffix", null);
        assertEquals("-suffix", withSuffix.getSuffixOrEmpty());

        DiagramNamingOptions withoutSuffix = new DiagramNamingOptions(
            null, null, null);
        assertEquals("", withoutSuffix.getSuffixOrEmpty());
    }

    @Test
    void testGetExtensionOrDefault() {
        DiagramNamingOptions withExtension = new DiagramNamingOptions(
            null, null, "uml");
        assertEquals("uml", withExtension.getExtensionOrDefault("mmd"));

        DiagramNamingOptions withoutExtension = new DiagramNamingOptions(
            null, null, null);
        assertEquals("mmd", withoutExtension.getExtensionOrDefault("mmd"));

        DiagramNamingOptions withEmptyExtension = new DiagramNamingOptions(
            null, null, "");
        assertEquals("mmd", withEmptyExtension.getExtensionOrDefault("mmd"));
    }

    @Test
    void testHasCustomNaming() {
        DiagramNamingOptions noCustom = new DiagramNamingOptions(
            null, null, null);
        assertFalse(noCustom.hasCustomNaming());

        DiagramNamingOptions withPrefix = new DiagramNamingOptions(
            "prefix-", null, null);
        assertTrue(withPrefix.hasCustomNaming());

        DiagramNamingOptions withSuffix = new DiagramNamingOptions(
            null, "-suffix", null);
        assertTrue(withSuffix.hasCustomNaming());

        DiagramNamingOptions withExtension = new DiagramNamingOptions(
            null, null, "uml");
        assertTrue(withExtension.hasCustomNaming());

        DiagramNamingOptions withAll = new DiagramNamingOptions(
            "pre-", "-suf", "ext");
        assertTrue(withAll.hasCustomNaming());
    }

    @Test
    void testCreateSafeWithValidInput() {
        DiagramNamingOptions options = DiagramNamingOptions.createSafe(
            "prefix-", "-suffix", "uml"
        );

        assertEquals("prefix-", options.prefix());
        assertEquals("-suffix", options.suffix());
        assertEquals("uml", options.extension());
    }

    @Test
    void testCreateSafeWithInvalidPrefix() {
        DiagramNamingOptions options = DiagramNamingOptions.createSafe(
            "invalid@prefix", "-suffix", "uml"
        );

        assertNull(options.prefix()); // Invalid prefix ignored
        assertEquals("-suffix", options.suffix());
        assertEquals("uml", options.extension());
    }

    @Test
    void testCreateSafeWithTooLongPrefix() {
        DiagramNamingOptions options = DiagramNamingOptions.createSafe(
            "a".repeat(MAX_PREFIX_LENGTH + 1), "-suffix", "uml"
        );

        assertNull(options.prefix()); // Too long prefix ignored
        assertEquals("-suffix", options.suffix());
        assertEquals("uml", options.extension());
    }

    @Test
    void testCreateSafeWithInvalidSuffix() {
        DiagramNamingOptions options = DiagramNamingOptions.createSafe(
            "prefix-", "invalid!suffix", "uml"
        );

        assertEquals("prefix-", options.prefix());
        assertNull(options.suffix()); // Invalid suffix ignored
        assertEquals("uml", options.extension());
    }

    @Test
    void testCreateSafeWithInvalidExtension() {
        DiagramNamingOptions options = DiagramNamingOptions.createSafe(
            "prefix-", "-suffix", "invalid*ext"
        );

        assertEquals("prefix-", options.prefix());
        assertEquals("-suffix", options.suffix());
        assertNull(options.extension()); // Invalid extension ignored
    }

    @Test
    void testCreateSafeWithNullValues() {
        DiagramNamingOptions options = DiagramNamingOptions.createSafe(
            null, null, null
        );

        assertNull(options.prefix());
        assertNull(options.suffix());
        assertNull(options.extension());
    }

    @Test
    void testCreateSafeWithWhitespace() {
        DiagramNamingOptions options = DiagramNamingOptions.createSafe(
            "  ", "  ", "  "
        );

        assertNull(options.prefix());
        assertNull(options.suffix());
        assertNull(options.extension());
    }
}
