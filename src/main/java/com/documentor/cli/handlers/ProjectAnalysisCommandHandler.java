package com.documentor.cli.handlers;

import com.documentor.model.ProjectAnalysis;
import com.documentor.service.CodeAnalysisService;
import com.documentor.service.DocumentationService;
import com.documentor.service.MermaidDiagramService;
import com.documentor.service.PlantUMLDiagramService;
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
 * 🔍 Handler for project analysis and documentation generation
 * Enhanced with PlantUML support alongside existing Mermaid functionality
 */
@Component
public class ProjectAnalysisCommandHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(
            ProjectAnalysisCommandHandler.class);

    private final CodeAnalysisService codeAnalysisService;
    private final DocumentationService documentationService;
    private final MermaidDiagramService mermaidDiagramService;
    private final PlantUMLDiagramService plantUMLDiagramService;
    private final CommonCommandHandler commonHandler;

    public ProjectAnalysisCommandHandler(
            final CodeAnalysisService codeAnalysisServiceParam,
            final DocumentationService documentationServiceParam,
            final MermaidDiagramService mermaidDiagramServiceParam,
            final PlantUMLDiagramService plantUMLDiagramServiceParam,
            final CommonCommandHandler commonHandlerParam) {
        this.codeAnalysisService = codeAnalysisServiceParam;
        this.documentationService = documentationServiceParam;
        this.mermaidDiagramService = mermaidDiagramServiceParam;
        this.plantUMLDiagramService = plantUMLDiagramServiceParam;
        this.commonHandler = commonHandlerParam;
    }

    /**
     * Handle project analysis command with optional diagram generation
     */
    public String handleAnalyzeProject(final String projectPath,
                                     final String configPath,
                                     final boolean generateMermaid,
                                     final String mermaidOutput) {
        return handleAnalyzeProject(projectPath, configPath,
                generateMermaid, mermaidOutput, null);
    }

    /**
     * Handle project analysis command with optional diagram generation
     * and private member override
     */
    public String handleAnalyzeProject(final String projectPath,
                                     final String configPath,
                                     final boolean generateMermaid,
                                     final String mermaidOutput,
                                     final Boolean includePrivateMembers) {
        return handleAnalyzeProjectExtended(projectPath, configPath,
                generateMermaid, mermaidOutput,
                false, "", includePrivateMembers);
    }

    /**
     * Handle project analysis command with both Mermaid and PlantUML options
     */
    public String handleAnalyzeProjectExtended(final String projectPath,
                                             final String configPath,
                                             final boolean generateMermaid,
                                             final String mermaidOutput,
                                             final boolean generatePlantUML,
                                             final String plantUMLOutput) {
        return handleAnalyzeProjectExtended(projectPath, configPath,
                generateMermaid, mermaidOutput,
                generatePlantUML, plantUMLOutput, null);
    }

    /**
     * Handle project analysis command with both Mermaid and PlantUML options
     * and private member override and dry-run mode
     */
    public String handleAnalyzeProjectExtended(final String projectPath,
                                            final String configPath,
                                            final boolean generateMermaid,
                                            final String mermaidOutput,
                                            final boolean generatePlantUML,
                                        final String plantUMLOutput,
                                        final Boolean includePrivateMembers) {
        return handleAnalyzeProjectExtended(projectPath, configPath,
                generateMermaid, mermaidOutput,
                generatePlantUML, plantUMLOutput,
                includePrivateMembers, false);
    }

    /**
     * Handle project analysis command with both Mermaid and PlantUML options,
     * private member override, and dry-run mode
     */
    public String handleAnalyzeProjectExtended(final String projectPath,
                                            final String configPath,
                                            final boolean generateMermaid,
                                            final String mermaidOutput,
                                            final boolean generatePlantUML,
                                        final String plantUMLOutput,
                                        final Boolean includePrivateMembers,
                                        final boolean dryRun) {
        try {
            LOGGER.info("🔍 Starting analysis of project: {}", projectPath);

            if (!commonHandler.directoryExists(projectPath)) {
                return "❌ Error: Project path does not exist or is not a "
                        + "directory: " + projectPath;
            }

            ProjectAnalysis analysis = performAnalysis(projectPath,
                    includePrivateMembers);

            StringBuilder result = commonHandler.createResultBuilder();

            if (dryRun) {
                result.append("📋 DRY RUN: Analysis would generate documentation at: ")
                      .append(System.getProperty("user.dir")).append("\n");
                result.append("   (no files actually written in dry-run mode)\n\n");
                result.append("✅ Dry-run analysis complete! Found ")
                      .append(analysis.getClasses().size()).append(" classes\n");
            } else {
                String outputPath = generateDocumentation(analysis);
                result.append(String.format(
                        "✅ Analysis complete! Documentation generated at: %s\n",
                        outputPath));
            }

            if (generateMermaid) {
                handleMermaidGeneration(analysis, mermaidOutput, result, dryRun);
            }

            if (generatePlantUML) {
                handlePlantUMLGeneration(analysis, plantUMLOutput, result, dryRun);
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
        return handleScanProject(projectPath, null);
    }

    /**
     * Handle scanning a project without generating documentation
     * with private member override
     */
    public String handleScanProject(final String projectPath,
                                  final Boolean includePrivateMembers) {
        try {
            if (!commonHandler.directoryExists(projectPath)) {
                return "❌ Error: Project path does not exist or is not a "
                        + "directory: " + projectPath;
            }

            ProjectAnalysis analysis = performAnalysis(projectPath,
                    includePrivateMembers);
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
        return performAnalysis(projectPath, null);
    }

    /**
     * Perform code analysis on the specified project
     * with optional private member override
     */
    private ProjectAnalysis performAnalysis(final String projectPath,
                                          final Boolean includePrivateMembers) {
        Path project = Paths.get(projectPath);
        CompletableFuture<ProjectAnalysis> analysisFuture =
                includePrivateMembers != null
                ? codeAnalysisService.analyzeProject(project,
                        includePrivateMembers)
                : codeAnalysisService.analyzeProject(project);
        return analysisFuture.join();
    }

    /**
     * Generate documentation for the analyzed project
     */
    private String generateDocumentation(final ProjectAnalysis analysis) {
        CompletableFuture<String> docFuture =
                documentationService.generateDocumentation(analysis);
        return docFuture.join();
    }

    /**
     * Generate and handle Mermaid diagrams
     */
    private void handleMermaidGeneration(final ProjectAnalysis analysis,
                                       final String mermaidOutput,
                                       final StringBuilder result,
                                       final boolean dryRun) {
        LOGGER.info("🧩 Generating Mermaid diagrams...");
        String mermaidOutputPath = mermaidOutput.trim().isEmpty()
                ? null : mermaidOutput;

        if (dryRun) {
            result.append("🧩 DRY RUN - Mermaid: would generate ")
                  .append(analysis.getClasses().size())
                  .append(" diagrams\n");
        } else {
            CompletableFuture<List<String>> mermaidFuture = mermaidDiagramService
                    .generateClassDiagrams(analysis, mermaidOutputPath);
            List<String> mermaidResult = mermaidFuture.join();
            result.append("🧩 Mermaid diagrams: ").append(mermaidResult.size())
                    .append(" diagrams generated").append("\n");
        }
    }

    /**
     * Generate and handle PlantUML diagrams
     */
    private void handlePlantUMLGeneration(final ProjectAnalysis analysis,
                                        final String plantUMLOutput,
                                        final StringBuilder result,
                                        final boolean dryRun) {
        LOGGER.info("🌱 Generating PlantUML diagrams...");
        String plantUMLOutputPath = plantUMLOutput.trim().isEmpty()
                ? null : plantUMLOutput;

        if (dryRun) {
            result.append("🌱 DRY RUN - PlantUML: would generate ")
                  .append(analysis.getClasses().size())
                  .append(" diagrams\n");
        } else {
            CompletableFuture<List<String>> plantUMLFuture = plantUMLDiagramService
                    .generateClassDiagrams(analysis, plantUMLOutputPath);
            List<String> plantUMLResult = plantUMLFuture.join();
            result.append("🌱 PlantUML diagrams: ").append(plantUMLResult.size())
                    .append(" diagrams generated").append("\n");
        }
    }

    /**
     * Format project analysis statistics
     */
    private String formatAnalysisStats(final ProjectAnalysis analysis) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("Project", analysis.projectPath());
        stats.put("Total Elements", analysis.codeElements().size());
        stats.put("Analysis Time",
                java.time.Instant.ofEpochMilli(analysis.timestamp()));

        return commonHandler.formatStatistics(
                "Project Analysis Statistics", stats);
    }
}
