package com.documentor.cli.handlers;

import com.documentor.model.ProjectAnalysis;
import com.documentor.service.CodeAnalysisService;
import com.documentor.service.DocumentationService;
import com.documentor.service.MermaidDiagramService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 🚀 Handler for project analysis and documentation generation
 */
@Component
public class ProjectAnalysisCommandHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectAnalysisCommandHandler.class);

    private final CodeAnalysisService codeAnalysisService;
    private final DocumentationService documentationService;
    private final MermaidDiagramService mermaidDiagramService;

    public ProjectAnalysisCommandHandler(
            final CodeAnalysisService codeAnalysisService,
            final DocumentationService documentationService,
            final MermaidDiagramService mermaidDiagramService) {
        this.codeAnalysisService = codeAnalysisService;
        this.documentationService = documentationService;
        this.mermaidDiagramService = mermaidDiagramService;
    }

    public String handleAnalyzeProject(final String projectPath, final String configPath,
                                     final boolean generateMermaid, final String mermaidOutput) {
        try {
            LOGGER.info("🚀 Starting analysis of project: {}", projectPath);

            if (!validateProjectPath(projectPath)) {
                return "❌ Error: Project path does not exist or is not a directory: " + projectPath;
            }

            ProjectAnalysis analysis = performAnalysis(projectPath);
            String outputPath = generateDocumentation(analysis);
            StringBuilder result = buildResult(outputPath);

            if (generateMermaid) {
                handleMermaidGeneration(analysis, mermaidOutput, result);
            }

            return result.toString();
        } catch (Exception e) {
            LOGGER.error("Analysis failed", e);
            return "❌ Error during analysis: " + e.getMessage();
        }
    }

    public String handleScanProject(final String projectPath) {
        try {
            if (!validateProjectPath(projectPath)) {
                return "❌ Error: Project path does not exist or is not a directory: " + projectPath;
            }

            ProjectAnalysis analysis = performAnalysis(projectPath);
            return formatAnalysisStats(analysis);
        } catch (Exception e) {
            LOGGER.error("Scan failed", e);
            return "❌ Error during scan: " + e.getMessage();
        }
    }

    private boolean validateProjectPath(final String projectPath) {
        Path project = Paths.get(projectPath);
        return Files.exists(project) && Files.isDirectory(project);
    }

    private ProjectAnalysis performAnalysis(final String projectPath) {
        Path project = Paths.get(projectPath);
        CompletableFuture<ProjectAnalysis> analysisFuture = codeAnalysisService.analyzeProject(project);
        return analysisFuture.join();
    }

    private String generateDocumentation(final ProjectAnalysis analysis) {
        CompletableFuture<String> docFuture = documentationService.generateDocumentation(analysis);
        return docFuture.join();
    }

    private StringBuilder buildResult(final String outputPath) {
        StringBuilder result = new StringBuilder();
        result.append(String.format("✅ Analysis complete! Documentation generated at: %s\n", outputPath));
        return result;
    }

    private void handleMermaidGeneration(final ProjectAnalysis analysis, final String mermaidOutput,
                                       final StringBuilder result) {
        LOGGER.info("🧩 Generating Mermaid diagrams...");
        String mermaidOutputPath = mermaidOutput.trim().isEmpty() ? null : mermaidOutput;
        CompletableFuture<List<String>> mermaidFuture = mermaidDiagramService.generateClassDiagrams(analysis, mermaidOutputPath);
        List<String> mermaidResult = mermaidFuture.join();
        result.append("🧩 Mermaid diagrams: ").append(mermaidResult.size()).append(" diagrams generated").append("\n");
    }

    private String formatAnalysisStats(final ProjectAnalysis analysis) {
        StringBuilder stats = new StringBuilder();
        stats.append("📊 Project Analysis Statistics\n");
        stats.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
        stats.append("📁 Project: ").append(analysis.projectPath()).append("\n");
        stats.append("📋 Total Elements: ").append(analysis.codeElements().size()).append("\n");
        stats.append("📅 Analysis Time: ").append(java.time.Instant.ofEpochMilli(analysis.timestamp())).append("\n");
        return stats.toString();
    }
}