package com.documentor.service.diagram;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Enhanced tests for DiagramPathManager with additional edge cases
 */
class DiagramPathManagerEnhancedTest {

    private final DiagramPathManager pathManager = new DiagramPathManager();

    @Test
    @DisplayName("Should handle empty custom output path")
    void determineOutputPathWithEmptyCustomPath() {
        // When
        String outputPath = pathManager.determineOutputPath("/some/source/path.java", "   ");

        // Then
        // When customPath is empty, it falls back to the parent directory of the source file path
        assertEquals(Paths.get("/some/source").toString(), outputPath);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Class$Name", "Class@Name", "Class#Name", "Class&Name", "Class%Name"})
    @DisplayName("Should sanitize various special characters in file names")
    void generateDiagramFileNameWithVariousSpecialChars(final String className) {
        // When
        String fileName = pathManager.generateDiagramFileName(className);

        // Then
        assertFalse(fileName.contains("$"));
        assertFalse(fileName.contains("@"));
        assertFalse(fileName.contains("#"));
        assertFalse(fileName.contains("&"));
        assertFalse(fileName.contains("%"));
        assertTrue(fileName.endsWith("_diagram.mmd"));
    }

    @Test
    @DisplayName("Should create output directory that exists on filesystem")
    void createOutputDirectoryThatExists(@TempDir final Path tempDir) throws IOException {
        // Given
        String dirPath = tempDir.toString();

        // When
        Path outputPath = pathManager.createOutputDirectory(dirPath);

        // Then
        assertTrue(Files.exists(outputPath));
        assertTrue(Files.isDirectory(outputPath));
    }

    @Test
    @DisplayName("Should handle relative paths in output directory")
    void createOutputDirectoryWithRelativePath() {
        // When
        Path outputPath = pathManager.createOutputDirectory("./diagrams");

        // Then
        assertEquals(Paths.get("./diagrams"), outputPath);
    }

    @Test
    @DisplayName("Should create output directory with multi-level paths")
    void createOutputDirectoryWithNestedPath() {
        // When
        Path outputPath = pathManager.createOutputDirectory("/tmp/diagrams/class/samples");

        // Then
        assertEquals(Paths.get("/tmp/diagrams/class/samples"), outputPath);
    }
}
