package com.documentor.service;

import com.documentor.config.DocumentorConfig;
import com.documentor.constants.ApplicationConstants;
import com.documentor.model.CodeElement;
import com.documentor.model.ProjectAnalysis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

/**
 * 🔍 Code Analysis Service
 *
 * Orchestrates the analysis of Java and Python projects by:
 * - Discovering source files
 * - Parsing code to extract classes, methods, and variables
 * - Delegating to language-specific analyzers
 */
@Service
public class CodeAnalysisService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(CodeAnalysisService.class);

    private final JavaCodeAnalyzer javaCodeAnalyzer;
    private final PythonCodeAnalyzer pythonCodeAnalyzer;
    private final DocumentorConfig config;

    public CodeAnalysisService(
            final JavaCodeAnalyzer javaCodeAnalyzerParam,
            final PythonCodeAnalyzer pythonCodeAnalyzerParam,
            final DocumentorConfig configParam) {
        this.javaCodeAnalyzer = javaCodeAnalyzerParam;
        this.pythonCodeAnalyzer = pythonCodeAnalyzerParam;
        this.config = configParam;
    }

    /**
     * 🔍 Analyzes a project directory and returns comprehensive analysis results
     *
     * @param projectPath Path to the project directory
     * @return ProjectAnalysis containing all discovered code elements
     */
    public CompletableFuture<ProjectAnalysis> analyzeProject(final Path projectPath) {
        return analyzeProject(projectPath, null);
    }

    /**
     * 🔍 Analyzes a project directory with optional override for private member inclusion
     *
     * @param projectPath Path to the project directory
     * @param includePrivateMembersOverride Optional override for including private members
     * @return ProjectAnalysis containing all discovered code elements
     */
    public CompletableFuture<ProjectAnalysis> analyzeProject(final Path projectPath,
                                                           final Boolean includePrivateMembersOverride) {
        LOGGER.info("🔍 Starting analysis of project: {}", projectPath);

        return CompletableFuture.supplyAsync(() -> {
            try {
                List<CodeElement> allElements =
                        discoverAndAnalyzeFiles(projectPath, includePrivateMembersOverride);

                ProjectAnalysis analysis = new ProjectAnalysis(
                    projectPath.toString(),
                    allElements,
                    System.currentTimeMillis()
                );

                LOGGER.info("✅ Analysis completed. Found {} code elements", allElements.size());
                return analysis;

            } catch (Exception e) {
                LOGGER.error("❌ Error analyzing project: {}", e.getMessage(), e);
                throw new RuntimeException("Failed to analyze project", e);
            }
        });
    }

    /**
     * 🔍 Discovers and analyzes all supported source files in the project
     */
    private List<CodeElement> discoverAndAnalyzeFiles(final Path projectPath) throws IOException {
        return discoverAndAnalyzeFiles(projectPath, null);
    }

    /**
     * 🔍 Discovers and analyzes all supported source files in the project with optional override
     */
    private List<CodeElement> discoverAndAnalyzeFiles(final Path projectPath,
                                                     final Boolean includePrivateMembersOverride) throws IOException {
        try (Stream<Path> fileStream = Files.walk(projectPath)) {
            return fileStream
                    .filter(Files::isRegularFile)
                    .filter(this::isSupportedFile)
                    .filter(this::shouldAnalyzeFile)
                    .flatMap(file -> analyzeFileSafely(file, includePrivateMembersOverride))
                    .toList();
        }
    }

    /**
     * 🔍 Checks if a file is a supported source file
     */
    private boolean isSupportedFile(final Path file) {
        String fileName = file.getFileName().toString().toLowerCase();
        return fileName.endsWith(ApplicationConstants.JAVA_EXTENSION)
               || fileName.endsWith(ApplicationConstants.PYTHON_EXTENSION);
    }

    /**
     * 🔍 Applies exclude patterns to determine if file should be analyzed
     */
    private boolean shouldAnalyzeFile(final Path file) {
        String filePath = file.toString();
        return config.analysisSettings().excludePatterns().stream()
                .noneMatch(pattern -> filePath.matches(
                    pattern.replace(ApplicationConstants.WILDCARD_PATTERN, ApplicationConstants.REGEX_REPLACEMENT)));
    }

    /**
     * 🔍 Safely analyzes a single file, returning empty stream on error
     */
    private Stream<CodeElement> analyzeFileSafely(final Path file) {
        return analyzeFileSafely(file, null);
    }

    /**
     * 🔍 Safely analyzes a single file with optional override, returning empty stream on error
     */
    private Stream<CodeElement> analyzeFileSafely(final Path file, final Boolean includePrivateMembersOverride) {
        try {
            return analyzeFileByType(file, includePrivateMembersOverride);
        } catch (Exception e) {
            LOGGER.warn("⚠️ Failed to analyze file {}: {}", file, e.getMessage());
            return Stream.empty();
        }
    }

    /**
     * 🔍 Analyzes a single file by determining its type
     */
    private Stream<CodeElement> analyzeFileByType(final Path file) throws IOException {
        return analyzeFileByType(file, null);
    }

    /**
     * 🔍 Analyzes a single file by determining its type with optional override
     */
    private Stream<CodeElement> analyzeFileByType(final Path file,
                                                 final Boolean includePrivateMembersOverride) throws IOException {
        String fileName = file.getFileName().toString().toLowerCase();

        if (fileName.endsWith(ApplicationConstants.JAVA_EXTENSION)) {
            return javaCodeAnalyzer.analyzeFile(file, includePrivateMembersOverride).stream();
        } else if (fileName.endsWith(ApplicationConstants.PYTHON_EXTENSION)) {
            return pythonCodeAnalyzer.analyzeFile(file, includePrivateMembersOverride).stream();
        }

        return Stream.empty();
    }
}

