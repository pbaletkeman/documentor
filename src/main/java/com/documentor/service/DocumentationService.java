package com.documentor.service;

import com.documentor.config.DocumentorConfig;
import com.documentor.model.CodeElement;
import com.documentor.model.CodeElementType;
import com.documentor.model.ProjectAnalysis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 📝 Documentation Generation Service
 * 
 * Orchestrates the generation of markdown documentation from code analysis results.
 * Combines LLM-generated content with structured information about the codebase.
 */
@Service
public class DocumentationService {

    private static final Logger logger = LoggerFactory.getLogger(DocumentationService.class);

    private final LlmService llmService;
    private final DocumentorConfig config;

    public DocumentationService(LlmService llmService, DocumentorConfig config) {
        this.llmService = llmService;
        this.config = config;
    }

    /**
     * 📚 Generates complete project documentation
     * 
     * @param analysis The project analysis results
     * @return CompletableFuture containing the path to generated documentation
     */
    public CompletableFuture<String> generateDocumentation(ProjectAnalysis analysis) {
        logger.info("📝 Starting documentation generation for project: {}", analysis.projectPath());

        return CompletableFuture.supplyAsync(() -> {
            try {
                // Create output directory
                Path outputPath = Paths.get(config.outputSettings().outputPath());
                Files.createDirectories(outputPath);

                // Generate main documentation
                String mainDoc = generateMainDocumentation(analysis).join();
                Path mainDocPath = outputPath.resolve("README.md");
                Files.write(mainDocPath, mainDoc.getBytes());

                // Generate detailed documentation for each element
                generateDetailedDocumentation(analysis, outputPath).join();

                // Generate unit tests if enabled
                if (config.outputSettings().generateUnitTests()) {
                    generateUnitTestDocumentation(analysis, outputPath).join();
                }

                logger.info("✅ Documentation generated successfully at: {}", outputPath);
                return outputPath.toString();

            } catch (Exception e) {
                logger.error("❌ Error generating documentation: {}", e.getMessage(), e);
                throw new RuntimeException("Failed to generate documentation", e);
            }
        });
    }

    /**
     * 📖 Generates the main README.md documentation
     */
    private CompletableFuture<String> generateMainDocumentation(ProjectAnalysis analysis) {
        return CompletableFuture.supplyAsync(() -> {
            StringBuilder doc = new StringBuilder();

            // Header
            appendHeader(doc, analysis);

            // Statistics
            appendStatistics(doc, analysis);

            // API Reference
            appendApiReference(doc, analysis);

            // Usage Examples
            appendUsageExamples(doc, analysis);

            return doc.toString();
        });
    }

    /**
     * 📑 Generates detailed documentation for each code element
     */
    private CompletableFuture<Void> generateDetailedDocumentation(ProjectAnalysis analysis, Path outputPath) {
        List<CompletableFuture<Void>> futures = analysis.codeElements().stream()
                .map(element -> generateElementDocumentation(element, outputPath))
                .toList();

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    /**
     * 🧪 Generates unit test documentation
     */
    private CompletableFuture<Void> generateUnitTestDocumentation(ProjectAnalysis analysis, Path outputPath) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Path testsDir = outputPath.resolve("tests");
                Files.createDirectories(testsDir);

                StringBuilder testDoc = new StringBuilder();
                appendTestDocumentationHeader(testDoc);

                List<CompletableFuture<String>> testFutures = analysis.codeElements().stream()
                        .filter(element -> element.type() != CodeElementType.FIELD)
                        .map(llmService::generateUnitTests)
                        .toList();

                CompletableFuture.allOf(testFutures.toArray(new CompletableFuture[0]))
                        .thenRun(() -> {
                            testFutures.forEach(future -> {
                                String testContent = future.join();
                                testDoc.append(testContent).append("\n\n");
                            });

                            try {
                                Files.write(testsDir.resolve("unit-tests.md"), testDoc.toString().getBytes());
                            } catch (IOException e) {
                                logger.error("❌ Error writing test documentation: {}", e.getMessage());
                            }
                        })
                        .join();

                return null;
            } catch (Exception e) {
                logger.error("❌ Error generating test documentation: {}", e.getMessage());
                throw new RuntimeException("Failed to generate test documentation", e);
            }
        });
    }

    /**
     * 🔧 Generates documentation for a single code element
     */
    private CompletableFuture<Void> generateElementDocumentation(CodeElement element, Path outputPath) {
        return llmService.generateDocumentation(element)
                .thenCombine(llmService.generateUsageExamples(element), (doc, examples) -> {
                    try {
                        StringBuilder content = new StringBuilder();
                        content.append(String.format("# %s %s\n\n", element.type().getIcon(), element.name()));
                        content.append("## Documentation\n\n");
                        content.append(doc).append("\n\n");
                        content.append("## Usage Examples\n\n");
                        content.append(examples).append("\n\n");
                        content.append("## Code Signature\n\n");
                        content.append("```").append(getLanguageFromFile(element.filePath())).append("\n");
                        content.append(element.signature()).append("\n");
                        content.append("```\n\n");

                        String fileName = String.format("%s-%s.md", 
                            element.type().name().toLowerCase(), 
                            element.name().replaceAll("[^a-zA-Z0-9]", "_"));
                        
                        Path elementPath = outputPath.resolve("elements").resolve(fileName);
                        Files.createDirectories(elementPath.getParent());
                        Files.write(elementPath, content.toString().getBytes());

                        return null;
                    } catch (IOException e) {
                        logger.error("❌ Error writing element documentation: {}", e.getMessage());
                        throw new RuntimeException("Failed to write element documentation", e);
                    }
                });
    }

    /**
     * 📊 Appends header section to documentation
     */
    private void appendHeader(StringBuilder doc, ProjectAnalysis analysis) {
        String projectName = Paths.get(analysis.projectPath()).getFileName().toString();
        String icon = config.outputSettings().includeIcons() ? "📚 " : "";
        
        doc.append(String.format("# %s%s - Code Documentation\n\n", icon, projectName));
        doc.append(String.format("Generated on: %s\n\n", 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        doc.append("This documentation was automatically generated using AI-powered code analysis.\n\n");
    }

    /**
     * 📈 Appends statistics section
     */
    private void appendStatistics(StringBuilder doc, ProjectAnalysis analysis) {
        ProjectAnalysis.AnalysisStats stats = analysis.getStats();
        String icon = config.outputSettings().includeIcons() ? "📊 " : "";
        
        doc.append(String.format("## %sProject Statistics\n\n", icon));
        doc.append(stats.getFormattedSummary()).append("\n\n");
        
        doc.append("| Element Type | Count |\n");
        doc.append("|--------------|-------|\n");
        doc.append(String.format("| %s Classes | %d |\n", 
            config.outputSettings().includeIcons() ? "📦" : "", stats.classCount()));
        doc.append(String.format("| %s Methods | %d |\n", 
            config.outputSettings().includeIcons() ? "🔧" : "", stats.methodCount()));
        doc.append(String.format("| %s Fields | %d |\n", 
            config.outputSettings().includeIcons() ? "📊" : "", stats.fieldCount()));
        doc.append("\n");
    }

    /**
     * 📋 Appends API reference section
     */
    private void appendApiReference(StringBuilder doc, ProjectAnalysis analysis) {
        String icon = config.outputSettings().includeIcons() ? "📋 " : "";
        doc.append(String.format("## %sAPI Reference\n\n", icon));

        Map<String, List<CodeElement>> elementsByFile = analysis.getElementsByFile();
        
        elementsByFile.forEach((filePath, elements) -> {
            String fileName = Paths.get(filePath).getFileName().toString();
            doc.append(String.format("### %s\n\n", fileName));
            
            elements.stream()
                    .collect(Collectors.groupingBy(CodeElement::type))
                    .forEach((type, typeElements) -> {
                        doc.append(String.format("#### %s %s\n\n", type.getIcon(), type.getDescription()));
                        typeElements.forEach(element -> {
                            doc.append(String.format("- **%s** - `%s`\n", element.name(), element.signature()));
                        });
                        doc.append("\n");
                    });
        });
    }

    /**
     * 💡 Appends usage examples section
     */
    private void appendUsageExamples(StringBuilder doc, ProjectAnalysis analysis) {
        String icon = config.outputSettings().includeIcons() ? "💡 " : "";
        doc.append(String.format("## %sUsage Examples\n\n", icon));
        doc.append("Detailed usage examples can be found in the individual element documentation files.\n\n");
        
        // Add links to detailed documentation
        doc.append("### Quick Links\n\n");
        analysis.getClasses().stream()
                .limit(5) // Show first 5 classes as examples
                .forEach(cls -> {
                    String fileName = String.format("elements/class-%s.md", 
                        cls.name().replaceAll("[^a-zA-Z0-9]", "_"));
                    doc.append(String.format("- [%s](%s)\n", cls.name(), fileName));
                });
        doc.append("\n");
    }

    /**
     * 🧪 Appends test documentation header
     */
    private void appendTestDocumentationHeader(StringBuilder doc) {
        String icon = config.outputSettings().includeIcons() ? "🧪 " : "";
        doc.append(String.format("# %sGenerated Unit Tests\n\n", icon));
        doc.append("This file contains AI-generated unit test suggestions for the analyzed code.\n\n");
        doc.append(String.format("Target Coverage: %.0f%%\n\n", config.outputSettings().targetCoverage() * 100));
    }

    /**
     * 🔍 Determines programming language from file extension
     */
    private String getLanguageFromFile(String filePath) {
        if (filePath.endsWith(".java")) return "java";
        if (filePath.endsWith(".py")) return "python";
        return "text";
    }
}