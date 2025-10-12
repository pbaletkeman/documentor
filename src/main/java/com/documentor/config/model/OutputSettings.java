package com.documentor.config.model;

import com.documentor.constants.ApplicationConstants;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;

/**
 * 🔍 Output Settings Configuration - Enhanced with PlantUML Support
 */
public record OutputSettings(
    @JsonProperty("output_directory")
    @NotEmpty(message = "Output directory is required")
    String outputDirectory,

    @JsonProperty("format")
    String format,

    @JsonProperty("generate_mermaid")
    Boolean generateMermaid,

    @JsonProperty("generate_plantuml")
    Boolean generatePlantUML,

    @JsonProperty("verbose_output")
    Boolean verboseOutput
) {
    // Simplified defaults
    public OutputSettings {
        if (format == null) {
            format = ApplicationConstants.DEFAULT_OUTPUT_FORMAT;
        }
        if (generateMermaid == null) {
            generateMermaid = false;
        }
        if (generatePlantUML == null) {
            generatePlantUML = false;
        }
        if (verboseOutput == null) {
            verboseOutput = false;
        }
    }

    // Backward compatibility methods
    public String outputPath() {
        return outputDirectory;
    }

    public Boolean includeIcons() {
        return true;
    }

    public Boolean generateUnitTests() {
        return true;
    }

    public Double targetCoverage() {
        return ApplicationConstants.DEFAULT_COVERAGE_THRESHOLD;
    }

    public Boolean generateMermaidDiagrams() {
        return generateMermaid;
    }

    public Boolean generatePlantUMLDiagrams() {
        return generatePlantUML;
    }

    public String mermaidOutputPath() {
        return outputDirectory;
    }

    public String plantUMLOutputPath() {
        return outputDirectory;
    }
}

