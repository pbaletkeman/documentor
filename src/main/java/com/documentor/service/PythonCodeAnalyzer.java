package com.documentor.service;

import com.documentor.model.CodeElement;
import com.documentor.service.python.PythonASTProcessor;
import com.documentor.service.python.PythonRegexAnalyzer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
  * Python Code Analyzer
 *
 * Orchestrates Python source file analysis using specialized components:
 * - AST-based parsing for accuracy (preferred)
 * - Regex-based parsing as fallback
 */
@Component
public class PythonCodeAnalyzer {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(PythonCodeAnalyzer.class);

    private final PythonASTProcessor astProcessor;
    private final PythonRegexAnalyzer regexAnalyzer;

    public PythonCodeAnalyzer(final PythonASTProcessor astProcessorParam,
                             final PythonRegexAnalyzer regexAnalyzerParam) {
        this.astProcessor = astProcessorParam;
        this.regexAnalyzer = regexAnalyzerParam;
    }

    /**
     * Analyzes a Python file and extracts all non-private code elements
     *
     * @param filePath Path to the Python source file
     * @return List of discovered code elements
     */
    public List<CodeElement> analyzeFile(final Path filePath) throws IOException {
        return analyzeFile(filePath, null);
    }

    /**
     * Analyzes a Python file and extracts code elements with optional private member override
     *
     * @param filePath Path to the Python source file
     * @param includePrivateMembersOverride Optional override for including private members
     * @return List of discovered code elements
     */
    public List<CodeElement> analyzeFile(final Path filePath,
                                       final Boolean includePrivateMembersOverride) throws IOException {
        LOGGER.debug("Analyzing Python file: {}", filePath);

        try {
            // Try using Python's AST module for more accurate parsing
            List<CodeElement> astElements = astProcessor.analyzeWithAST(filePath);
            if (!astElements.isEmpty()) {
                LOGGER.debug("Successfully analyzed {} with AST", filePath);
                return astElements;
            }
        } catch (Exception e) {
            LOGGER.debug("AST analysis failed, falling back to regex parsing: {}", e.getMessage());
        }

        // Fallback to regex-based parsing
        List<String> lines = Files.readAllLines(filePath);
        List<CodeElement> regexElements =
                regexAnalyzer.analyzeWithRegex(filePath, lines);
        LOGGER.debug("Successfully analyzed {} with regex (found {} elements)",
                    filePath, regexElements.size());

        return regexElements;
    }
}
