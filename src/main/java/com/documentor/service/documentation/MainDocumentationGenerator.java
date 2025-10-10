package com.documentor.service.documentation;

import com.documentor.config.DocumentorConfig;
import com.documentor.model.CodeElement;
import com.documentor.model.ProjectAnalysis;
import org.springframework.stereotype.Component;

import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * ðŸ“– Main Documentation Generator
 *
 * Specialized component for generating the main README.md documentation.
 * Handles project overview, statistics, and API reference sections.
 */
@Component
public class MainDocumentationGenerator {

    private final DocumentorConfig config;

    public MainDocumentationGenerator(DocumentorConfig config) {
        this.config = config;
    }

    /**
     * ðŸ“– Generates the main README.md documentation
     */
    public CompletableFuture<String> generateMainDocumentation(final ProjectAnalysis analysis) {
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
     * ðŸ“Š Appends header section to documentation
     */
    private void appendHeader(final StringBuilder doc, final ProjectAnalysis analysis) {
        String projectName = Paths.get(analysis.projectPath()).getFileName().toString();
        String icon = config.outputSettings().includeIcons() ? "ðŸ“š " : "";

        doc.append(String.format("# %s%s - Code Documentation\n\n", icon, projectName));
        doc.append(String.format("Generated on: %s\n\n",
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        doc.append("This documentation was automatically generated using AI-powered code analysis.\n\n");
    }

    /**
     * ðŸ“ˆ Appends statistics section
     */
    private void appendStatistics(final StringBuilder doc, final ProjectAnalysis analysis) {
        ProjectAnalysis.AnalysisStats stats = analysis.getStats();
        String icon = config.outputSettings().includeIcons() ? "ðŸ“Š " : "";

        doc.append(String.format("## %sProject Statistics\n\n", icon));
        doc.append(stats.getFormattedSummary()).append("\n\n");

        doc.append("| Element Type | Count |\n");
        doc.append("|--------------|-------|\n");
        doc.append(String.format("| %s Classes | %d |\n",
            config.outputSettings().includeIcons() ? "ðŸ“¦" : "", stats.classCount()));
        doc.append(String.format("| %s Methods | %d |\n",
            config.outputSettings().includeIcons() ? "ðŸ”§" : "", stats.methodCount()));
        doc.append(String.format("| %s Fields | %d |\n",
            config.outputSettings().includeIcons() ? "ðŸ“Š" : "", stats.fieldCount()));
        doc.append("\n");
    }

    /**
     * ðŸ“‹ Appends API reference section
     */
    private void appendApiReference(final StringBuilder doc, final ProjectAnalysis analysis) {
        String icon = config.outputSettings().includeIcons() ? "ðŸ“‹ " : "";
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
     * ðŸ’¡ Appends usage examples section
     */
    private void appendUsageExamples(final StringBuilder doc, final ProjectAnalysis analysis) {
        String icon = config.outputSettings().includeIcons() ? "ðŸ’¡ " : "";
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
}
