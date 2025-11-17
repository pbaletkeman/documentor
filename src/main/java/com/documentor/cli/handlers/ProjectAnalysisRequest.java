package com.documentor.cli.handlers;

/**
 * Parameter object for project analysis requests
 */
public record ProjectAnalysisRequest(
        String projectPath,
        String configPath,
        boolean generateMermaid,
        String mermaidOutput,
        boolean generatePlantUML,
        String plantUMLOutput,
        Boolean includePrivateMembers,
        boolean useFix,
        String outputDir) {
}

