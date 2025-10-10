package com.documentor.cli.handlers;

import com.documentor.model.ProjectAnalysis;
import com.documentor.service.CodeAnalysisService;
import com.documentor.service.DocumentationService;
import com.documentor.service.MermaidDiagramService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 🚀 Handler for project analysis and documentation generation
 * Refactored to reduce complexity using CommonCommandHandler
 */
@Component
public class ProjectAnalysisCommandHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectAnalysisCommandHandler.class);

    private final CodeAnalysisService codeAnalysisService;
    private final DocumentationService documentationService;
    private final MermaidDiagramService mermaidDiagramService;
    private final CommonCommandHandler commonHandler;

    public ProjectAnalysisCommandHandler(
            final CodeAnalysisService codeAnalysisService,
            final DocumentationService documentationService,
            final MermaidDiagramService mermaidDiagramService,
            final CommonCommandHandler commonHandler) {
        this.codeAnalysisService = codeAnalysisService;
        this.documentationService = documentationService;
        this.mermaidDiagramService = mermaidDiagramService;
        this.commonHandler = commonHandler;
    }

    /**
     * Handle project analysis command with optional Mermaid diagram generation
     */
    public String handleAnalyzeProject(final String projectPath, final String configPath,
                                     final boolean generateMermaid, final String mermaidOutput) {
        try {
            LOGGER.info("🚀 Starting analysis of project: {}", projectPath);

            if (!commonHandler.directoryExists(projectPath)) {
                return "❌ Error: Project path does not exist or is not a directory: " + projectPath;
            }

            ProjectAnalysis analysis = performAnalysis(projectPath);
            String outputPath = generateDocumentation(analysis);
            StringBuilder result = commonHandler.createResultBuilder();
            result.append(String.format("✅ Analysis complete! Documentation generated at: %s\n", outputPath));

            if (generateMermaid) {
                handleMermaidGeneration(analysis, mermaidOutput, result);
            }

            return result.toString();
        } catch (Exception e) {
            LOGGER.error("Analysis failed", e);
            return commonHandler.formatErrorMessage("Error during analysis", e);
        }
    }

    /**
     * Handle scanning a project without generating documentation
     */
    public String handleScanProject(final String projectPath) {
        try {
            if (!commonHandler.directoryExists(projectPath)) {
                return "❌ Error: Project path does not exist or is not a directory: " + projectPath;
            }

            ProjectAnalysis analysis = performAnalysis(projectPath);
            return formatAnalysisStats(analysis);
        } catch (Exception e) {
            LOGGER.error("Scan failed", e);
            return commonHandler.formatErrorMessage("Error during scan", e);
        }
    }

    /**
     * Perform code analysis on the specified project
     */
    private ProjectAnalysis performAnalysis(final String projectPath) {
        Path project = Paths.get(projectPath);
        CompletableFuture<ProjectAnalysis> analysisFuture = codeAnalysisService.analyzeProject(project);
        return analysisFuture.join();
    }

    /**
     * Generate documentation for the analyzed project
     */
    private String generateDocumentation(final ProjectAnalysis analysis) {
        CompletableFuture<String> docFuture = documentationService.generateDocumentation(analysis);
        return docFuture.join();
    }
    
    /**
     * Generate and handle Mermaid diagrams
     */
    private void handleMermaidGeneration(final ProjectAnalysis analysis, final String mermaidOutput,
                                       final StringBuilder result) {
        LOGGER.info("🧩 Generating Mermaid diagrams...");
        String mermaidOutputPath = mermaidOutput.trim().isEmpty() ? null : mermaidOutput;
        CompletableFuture<List<String>> mermaidFuture = mermaidDiagramService.generateClassDiagrams(analysis, mermaidOutputPath);
        List<String> mermaidResult = mermaidFuture.join();
        result.append("🧩 Mermaid diagrams: ").append(mermaidResult.size()).append(" diagrams generated").append("\n");
    }

    /**
     * Format project analysis statistics
     */
    private String formatAnalysisStats(final ProjectAnalysis analysis) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("Project", analysis.projectPath());
        stats.put("Total Elements", analysis.codeElements().size());
        stats.put("Analysis Time", java.time.Instant.ofEpochMilli(analysis.timestamp()));
        
        return commonHandler.formatStatistics("Project Analysis Statistics", stats);
    }
}