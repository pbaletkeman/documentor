package com.documentor.service.diagram;

import com.documentor.config.model.DiagramNamingOptions;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 🔍 Diagram Path Manager
 *
 * Specialized component for managing output paths and file naming for diagrams.
 * Handles path resolution and file naming conventions.
 */
@Component
public class DiagramPathManager {

    /**
     * 🔍 Determines the output path for the diagram
     */
    public String determineOutputPath(final String sourceFilePath,
            final String customOutputPath) {
        if (customOutputPath != null && !customOutputPath.trim().isEmpty()) {
            return customOutputPath;
        }

        // Default to same directory as source file
        Path sourcePath = Paths.get(sourceFilePath);
        return sourcePath.getParent() != null
                ? sourcePath.getParent().toString() : ".";
    }

    /**
     * 🏷️ Generates a standardized diagram file name
     */
    public String generateDiagramFileName(final String className) {
        return sanitizeFileName(className) + "_diagram.mmd";
    }

    /**
     * 🏷️ Generates a customizable diagram file name
     *
     * @param className the name of the class
     * @param namingOptions the naming options (prefix, suffix, extension)
     * @param defaultExtension the default extension if none specified
     * @return the generated file name
     */
    public String generateDiagramFileName(final String className,
            final DiagramNamingOptions namingOptions,
            final String defaultExtension) {
        if (namingOptions == null) {
            return sanitizeFileName(className) + "_diagram."
                    + defaultExtension;
        }

        String prefix = namingOptions.getPrefixOrEmpty();
        String suffix = namingOptions.getSuffixOrEmpty();
        String extension = namingOptions.getExtensionOrDefault(
                defaultExtension);

        return prefix + sanitizeFileName(className) + suffix + "."
                + extension;
    }

    /**
     * 🧹 Sanitizes file name for cross-platform compatibility
     */
    private String sanitizeFileName(final String fileName) {
        return fileName.replaceAll("[^a-zA-Z0-9_\\-]", "_");
    }

    /**
     * 📊 Creates the output directory path
     */
    public Path createOutputDirectory(final String outputPath) {
        return Paths.get(outputPath);
    }
}
