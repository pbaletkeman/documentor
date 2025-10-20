package com.documentor.service.diagram;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DiagramPathManagerTest {

    private final DiagramPathManager pathManager = new DiagramPathManager();

    @Test
    @DisplayName("Should use custom output path when provided")
    void determineOutputPathWithCustomPath() {
        // When
        String outputPath = pathManager.determineOutputPath("/some/source/path.java", "/custom/output/path");

        // Then
        assertEquals("/custom/output/path", outputPath);
    }

    @Test
    @DisplayName("Should fallback to source directory when custom path is not provided")
    void determineOutputPathWithoutCustomPath() {
        // When
        String outputPath = pathManager.determineOutputPath(
            "C:\\some\\source\\path.java".replace("\\", java.io.File.separator), null);

        // Then
        String expected = "C:\\some\\source".replace("\\", java.io.File.separator);
        assertEquals(expected, outputPath);
    }

    @Test
    @DisplayName("Should fallback to current directory for files without parent directory")
    void determineOutputPathWithoutParentDirectory() {
        // When
        String outputPath = pathManager.determineOutputPath("file.java", null);

        // Then
        assertEquals(".", outputPath);
    }

    @Test
    @DisplayName("Should generate diagram file name with proper sanitization")
    void generateDiagramFileName() {
        // When
        String fileName = pathManager.generateDiagramFileName("TestClass");

        // Then
        assertEquals("TestClass_diagram.mmd", fileName);
    }

    @Test
    @DisplayName("Should sanitize special characters in file names")
    void generateDiagramFileNameWithSpecialChars() {
        // When
        String fileName = pathManager.generateDiagramFileName("Test:Class?With*Invalid/Chars");

        // Then
        assertEquals("Test_Class_With_Invalid_Chars_diagram.mmd", fileName);
    }

    @Test
    @DisplayName("Should create output directory path")
    void createOutputDirectory() {
        // When
        Path dirPath = pathManager.createOutputDirectory("/output/dir");

        // Then
        assertEquals(Paths.get("/output/dir"), dirPath);
    }
}
