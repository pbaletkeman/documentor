package com.documentor.service;

import com.documentor.config.DocumentorConfig;
import com.documentor.model.CodeElement;
import com.documentor.service.analysis.JavaElementVisitor;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * â˜• Java Code Analyzer - Refactored for Low Complexity
 *
 * Parses Java source files using JavaParser to extract:
 * - Public/protected classes and interfaces
 * - Public/protected methods with signatures
 * - Public/protected fields and variables
 */
@Component
public class JavaCodeAnalyzer {

    private static final Logger LOGGER = LoggerFactory.getLogger(JavaCodeAnalyzer.class);

    private final JavaParser javaParser;
    private final JavaElementVisitor elementVisitor;

    public JavaCodeAnalyzer(final DocumentorConfig config, final JavaElementVisitor elementVisitor) {
        this.javaParser = new JavaParser();
        this.elementVisitor = elementVisitor;
    }

    /**
     * ðŸ“„ Analyzes a Java file and extracts all non-private code elements
     *
     * @param filePath Path to the Java source file
     * @return List of discovered code elements
     */
    public List<CodeElement> analyzeFile(final Path filePath) throws IOException {
        LOGGER.debug("ðŸ” Analyzing Java file: {}", filePath);

        String sourceCode = Files.readString(filePath);
        List<CodeElement> elements = new ArrayList<>();

        try {
            CompilationUnit cu = javaParser.parse(sourceCode).getResult()
                    .orElseThrow(() -> new IOException("Failed to parse Java file"));

            elementVisitor.initialize(filePath, elements);
            elementVisitor.visit(cu, null);

            LOGGER.debug("âœ… Found {} elements in {}", elements.size(), filePath.getFileName());
            return elements;

        } catch (Exception e) {
            LOGGER.error("âŒ Error parsing Java file {}: {}", filePath, e.getMessage());
            throw new IOException("Failed to analyze Java file", e);
        }
    }
}
