package com.documentor.config.model;

import com.documentor.constants.ApplicationConstants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Output Settings Tests")
class OutputSettingsTest {

    private static final String VALID_OUTPUT_DIR = "docs/output";

    @Test
    @DisplayName("Should create OutputSettings with provided values")
    void shouldCreateWithProvidedValues() {
        // Given
        String outputDirectory = "custom/output/path";
        String format = "html";
        Boolean generateMermaid = true;
        Boolean verboseOutput = true;

        // When
        OutputSettings settings = new OutputSettings(
                outputDirectory,
                format,
                generateMermaid,
                verboseOutput
        );

        // Then
        assertEquals(outputDirectory, settings.outputDirectory());
        assertEquals(format, settings.format());
        assertTrue(settings.generateMermaid());
        assertTrue(settings.verboseOutput());
    }

    @Test
    @DisplayName("Should apply defaults for null values except outputDirectory")
    void shouldApplyDefaultsForNullValues() {
        // When
        OutputSettings settings = new OutputSettings(
                VALID_OUTPUT_DIR,
                null,
                null,
                null
        );

        // Then
        assertEquals(VALID_OUTPUT_DIR, settings.outputDirectory());
        assertEquals(ApplicationConstants.DEFAULT_OUTPUT_FORMAT, settings.format());
        assertFalse(settings.generateMermaid());
        assertFalse(settings.verboseOutput());
    }

    @ParameterizedTest
    @MethodSource("partialDefaultsProvider")
    @DisplayName("Should apply defaults for partial null values")
    void shouldApplyDefaultsForPartialNullValues(String outputDirectory, String format,
                                              Boolean generateMermaid, Boolean verboseOutput,
                                              String expectedFormat, Boolean expectedGenerateMermaid, 
                                              Boolean expectedVerboseOutput) {
        // When
        OutputSettings settings = new OutputSettings(
                outputDirectory,
                format,
                generateMermaid,
                verboseOutput
        );

        // Then
        assertEquals(outputDirectory, settings.outputDirectory());
        assertEquals(expectedFormat, settings.format());
        assertEquals(expectedGenerateMermaid, settings.generateMermaid());
        assertEquals(expectedVerboseOutput, settings.verboseOutput());
    }

    @Test
    @DisplayName("Should return correct outputPath")
    void shouldReturnCorrectOutputPath() {
        // Given
        String outputDirectory = "custom/output/path";
        OutputSettings settings = new OutputSettings(outputDirectory, null, null, null);

        // When & Then
        assertEquals(outputDirectory, settings.outputPath());
    }

    @Test
    @DisplayName("Should always return true for includeIcons")
    void shouldAlwaysReturnTrueForIncludeIcons() {
        // Given
        OutputSettings settings = new OutputSettings(VALID_OUTPUT_DIR, null, null, null);

        // When & Then
        assertTrue(settings.includeIcons());
    }

    @Test
    @DisplayName("Should always return true for generateUnitTests")
    void shouldAlwaysReturnTrueForGenerateUnitTests() {
        // Given
        OutputSettings settings = new OutputSettings(VALID_OUTPUT_DIR, null, null, null);

        // When & Then
        assertTrue(settings.generateUnitTests());
    }

    @Test
    @DisplayName("Should return default coverage threshold")
    void shouldReturnDefaultCoverageThreshold() {
        // Given
        OutputSettings settings = new OutputSettings(VALID_OUTPUT_DIR, null, null, null);

        // When & Then
        assertEquals(ApplicationConstants.DEFAULT_COVERAGE_THRESHOLD, settings.targetCoverage());
    }

    @Test
    @DisplayName("Should return same value for generateMermaidDiagrams as for generateMermaid")
    void shouldReturnSameValueForGenerateMermaidDiagramsAsForGenerateMermaid() {
        // Given
        OutputSettings settingsWithTrue = new OutputSettings(VALID_OUTPUT_DIR, null, true, null);
        OutputSettings settingsWithFalse = new OutputSettings(VALID_OUTPUT_DIR, null, false, null);
        OutputSettings settingsWithNull = new OutputSettings(VALID_OUTPUT_DIR, null, null, null);

        // When & Then
        assertTrue(settingsWithTrue.generateMermaidDiagrams());
        assertFalse(settingsWithFalse.generateMermaidDiagrams());
        assertFalse(settingsWithNull.generateMermaidDiagrams());
    }

    @Test
    @DisplayName("Should return outputDirectory for mermaidOutputPath")
    void shouldReturnOutputDirectoryForMermaidOutputPath() {
        // Given
        String outputDirectory = "custom/output/path";
        OutputSettings settings = new OutputSettings(outputDirectory, null, null, null);

        // When & Then
        assertEquals(outputDirectory, settings.mermaidOutputPath());
    }

    private static Stream<Arguments> partialDefaultsProvider() {
        return Stream.of(
                // Test with only format set
                Arguments.of(
                        VALID_OUTPUT_DIR, "html", null, null,
                        "html", false, false
                ),
                // Test with only generateMermaid set
                Arguments.of(
                        VALID_OUTPUT_DIR, null, true, null,
                        ApplicationConstants.DEFAULT_OUTPUT_FORMAT, true, false
                ),
                // Test with only verboseOutput set
                Arguments.of(
                        VALID_OUTPUT_DIR, null, null, true,
                        ApplicationConstants.DEFAULT_OUTPUT_FORMAT, false, true
                ),
                // Test with all custom values set
                Arguments.of(
                        VALID_OUTPUT_DIR, "custom", true, true,
                        "custom", true, true
                )
        );
    }
}