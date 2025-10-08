package com.documentor.service;

import com.documentor.config.DocumentorConfig;
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

    private static final Logger logger = LoggerFactory.getLogger(CodeAnalysisService.class);

    private final JavaCodeAnalyzer javaCodeAnalyzer;
    private final PythonCodeAnalyzer pythonCodeAnalyzer;
    private final DocumentorConfig config;

    public CodeAnalysisService(
            JavaCodeAnalyzer javaCodeAnalyzer,
            PythonCodeAnalyzer pythonCodeAnalyzer,
            DocumentorConfig config) {
        this.javaCodeAnalyzer = javaCodeAnalyzer;
        this.pythonCodeAnalyzer = pythonCodeAnalyzer;
        this.config = config;
    }

    /**
     * 📊 Analyzes a project directory and returns comprehensive analysis results
     * 
     * @param projectPath Path to the project directory
     * @return ProjectAnalysis containing all discovered code elements
     */
    public CompletableFuture<ProjectAnalysis> analyzeProject(Path projectPath) {
        logger.info("🚀 Starting analysis of project: {}", projectPath);

        return CompletableFuture.supplyAsync(() -> {
            try {
                List<CodeElement> allElements = discoverAndAnalyzeFiles(projectPath);
                
                ProjectAnalysis analysis = new ProjectAnalysis(
                    projectPath.toString(),
                    allElements,
                    System.currentTimeMillis()
                );
                
                logger.info("✅ Analysis completed. Found {} code elements", allElements.size());
                return analysis;
                
            } catch (Exception e) {
                logger.error("❌ Error analyzing project: {}", e.getMessage(), e);
                throw new RuntimeException("Failed to analyze project", e);
            }
        });
    }

    /**
     * 🔎 Discovers and analyzes all supported source files in the project
     */
    private List<CodeElement> discoverAndAnalyzeFiles(Path projectPath) throws IOException {
        return Files.walk(projectPath)
                .filter(Files::isRegularFile)
                .filter(this::isSupportedFile)
                .filter(this::shouldAnalyzeFile)
                .flatMap(this::analyzeFile)
                .toList();
    }

    /**
     * 📁 Checks if a file is a supported source file
     */
    private boolean isSupportedFile(Path file) {
        String fileName = file.getFileName().toString().toLowerCase();
        return fileName.endsWith(".java") || fileName.endsWith(".py");
    }

    /**
     * 🚫 Applies exclude patterns to determine if file should be analyzed
     */
    private boolean shouldAnalyzeFile(Path file) {
        String filePath = file.toString();
        return config.analysisSettings().excludePatterns().stream()
                .noneMatch(pattern -> filePath.matches(pattern.replace("*", ".*")));
    }

    /**
     * 🔍 Analyzes a single file and returns a stream of code elements
     */
    private Stream<CodeElement> analyzeFile(Path file) {
        try {
            String fileName = file.getFileName().toString().toLowerCase();
            
            if (fileName.endsWith(".java")) {
                return javaCodeAnalyzer.analyzeFile(file).stream();
            } else if (fileName.endsWith(".py")) {
                return pythonCodeAnalyzer.analyzeFile(file).stream();
            }
            
            return Stream.empty();
            
        } catch (Exception e) {
            logger.warn("⚠️ Failed to analyze file {}: {}", file, e.getMessage());
            return Stream.empty();
        }
    }
}