package com.documentor.service;

import com.documentor.config.DocumentorConfig;
import com.documentor.model.CodeElement;
import com.documentor.service.analysis.JavaElementVisitor;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    private static final Logger logger = LoggerFactory.getLogger(JavaCodeAnalyzer.class);

    private final JavaParser javaParser;
    private final JavaElementVisitor elementVisitor;

    @Autowired
    public JavaCodeAnalyzer(DocumentorConfig config, JavaElementVisitor elementVisitor) {
        this.javaParser = new JavaParser();
        this.elementVisitor = elementVisitor;
    }

    /**
     * @deprecated Use constructor with JavaElementVisitor for better testability
     */
    @Deprecated
    public JavaCodeAnalyzer(DocumentorConfig config) {
        this.javaParser = new JavaParser();
        this.elementVisitor = new JavaElementVisitor(config);
    }

    /**
     * 📄 Analyzes a Java file and extracts all non-private code elements
     * 
     * @param filePath Path to the Java source file
     * @return List of discovered code elements
     */
    public List<CodeElement> analyzeFile(Path filePath) throws IOException {
        logger.debug("🔍 Analyzing Java file: {}", filePath);

        String sourceCode = Files.readString(filePath);
        List<CodeElement> elements = new ArrayList<>();

        try {
            CompilationUnit cu = javaParser.parse(sourceCode).getResult()
                    .orElseThrow(() -> new IOException("Failed to parse Java file"));

            elementVisitor.initialize(filePath, elements);
            elementVisitor.visit(cu, null);

            logger.debug("✅ Found {} elements in {}", elements.size(), filePath.getFileName());
            return elements;

        } catch (Exception e) {
            logger.error("❌ Error parsing Java file {}: {}", filePath, e.getMessage());
            throw new IOException("Failed to analyze Java file", e);
        }
    }
}