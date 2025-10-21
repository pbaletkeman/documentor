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
 * ☕ Java Code Analyzer - Refactored for Low Complexity
 *
 * Parses Java source files using JavaParser to extract:
 * - Public/protected classes and interfaces
 * - Public/protected methods with signatures
 * - Public/protected fields and variables
 */
@Component
public class JavaCodeAnalyzer {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(JavaCodeAnalyzer.class);

    private final JavaParser javaParser;
    private final JavaElementVisitor elementVisitor;

    public JavaCodeAnalyzer(final DocumentorConfig config, final JavaElementVisitor elementVisitorParam) {
        this.javaParser = new JavaParser();
        this.elementVisitor = elementVisitorParam;
    }

    /**
     * 🔍 Analyzes a Java file and extracts all non-private code elements
     *
     * @param filePath Path to the Java source file
     * @return List of discovered code elements
     */
    public List<CodeElement> analyzeFile(final Path filePath) throws IOException {
        return analyzeFile(filePath, null);
    }

    /**
     * 🔍 Analyzes a Java file and extracts code elements with optional private member override
     *
     * @param filePath Path to the Java source file
     * @param includePrivateMembersOverride Optional override for including private members
     * @return List of discovered code elements
     */
    public List<CodeElement> analyzeFile(final Path filePath,
                                       final Boolean includePrivateMembersOverride) throws IOException {
        LOGGER.debug("🔍 Analyzing Java file: {}", filePath);

        String sourceCode = Files.readString(filePath);
        List<CodeElement> elements = new ArrayList<>();

        try {
            CompilationUnit cu = javaParser.parse(sourceCode).getResult()
                    .orElseThrow(() -> new IOException("Failed to parse Java file"));

            elementVisitor.initialize(filePath, elements, includePrivateMembersOverride);
            elementVisitor.visit(cu, null);

            LOGGER.debug("✅ Found {} elements in {}", elements.size(), filePath.getFileName());
            return elements;

        } catch (Exception e) {
            LOGGER.error("❌ Error parsing Java file {}: {}", filePath, e.getMessage());
            throw new IOException("Failed to analyze Java file", e);
        }
    }
}

