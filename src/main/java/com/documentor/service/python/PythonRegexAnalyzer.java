package com.documentor.service.python;

import com.documentor.config.DocumentorConfig;
import com.documentor.model.CodeElement;
import com.documentor.model.CodeElementType;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 🔍 Python Regex Analyzer
 * 
 * Specialized component for regex-based Python code analysis as a fallback
 * when AST parsing is not available or fails.
 */
@Component
public class PythonRegexAnalyzer {

    private final DocumentorConfig config;
    private final PythonElementExtractor elementExtractor;

    // Regex patterns for Python code analysis
    private static final Pattern CLASS_PATTERN = Pattern.compile("^(\\s*)class\\s+([A-Za-z_][A-Za-z0-9_]*)\\s*(?:\\([^)]*\\))?\\s*:");
    private static final Pattern FUNCTION_PATTERN = Pattern.compile("^(\\s*)def\\s+([A-Za-z_][A-Za-z0-9_]*)\\s*\\([^)]*\\)\\s*(?:->\\s*[^:]+)?\\s*:");
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("^(\\s*)([A-Za-z_][A-Za-z0-9_]*)\\s*(?:\\s*[^=]+)?\\s*=\\s*(.+)");

    public PythonRegexAnalyzer(DocumentorConfig config, PythonElementExtractor elementExtractor) {
        this.config = config;
        this.elementExtractor = elementExtractor;
    }

    /**
     * 🔍 Fallback regex-based analysis for when AST parsing fails
     */
    public List<CodeElement> analyzeWithRegex(Path filePath, List<String> lines) {
        List<CodeElement> elements = new ArrayList<>();

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            int lineNumber = i + 1;

            // Check for class definitions
            Matcher classMatcher = CLASS_PATTERN.matcher(line);
            if (classMatcher.matches()) {
                String className = classMatcher.group(2);
                if (shouldInclude(className)) {
                    elements.add(new CodeElement(
                        CodeElementType.CLASS,
                        className,
                        "class " + className,
                        filePath.toString(),
                        lineNumber,
                        line.trim(),
                        elementExtractor.extractDocstring(lines, i + 1),
                        List.of(),
                        List.of()
                    ));
                }
            }

            // Check for function definitions
            Matcher functionMatcher = FUNCTION_PATTERN.matcher(line);
            if (functionMatcher.matches()) {
                String functionName = functionMatcher.group(2);
                if (shouldInclude(functionName)) {
                    elements.add(new CodeElement(
                        CodeElementType.METHOD,
                        functionName,
                        line.trim(),
                        filePath.toString(),
                        lineNumber,
                        line.trim(),
                        elementExtractor.extractDocstring(lines, i + 1),
                        elementExtractor.extractParameters(line),
                        List.of()
                    ));
                }
            }

            // Check for variable assignments
            Matcher variableMatcher = VARIABLE_PATTERN.matcher(line);
            if (variableMatcher.matches()) {
                String variableName = variableMatcher.group(2);
                if (shouldInclude(variableName)) {
                    elements.add(new CodeElement(
                        CodeElementType.FIELD,
                        variableName,
                        variableName,
                        filePath.toString(),
                        lineNumber,
                        line.trim(),
                        "",
                        List.of(),
                        List.of()
                    ));
                }
            }
        }

        return elements;
    }

    /**
     * 🔍 Checks if an element should be included based on configuration
     */
    private boolean shouldInclude(String name) {
        boolean isPrivate = name.startsWith("_");
        return config.analysisSettings().includePrivateMembers() || !isPrivate;
    }
}