package com.documentor.service.python;

import com.documentor.config.DocumentorConfig;
import com.documentor.model.CodeElement;
import com.documentor.model.CodeElementType;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * ðŸ” Python Regex Analyzer
 *
 * Specialized component for regex-based Python code analysis as a fallback
 * when AST parsing is not available or fails.
 * Refactored to reduce complexity by using PythonPatternMatcher.
 */
@Component
public class PythonRegexAnalyzer {

    private final DocumentorConfig config;
    private final PythonElementExtractor elementExtractor;
    private final PythonPatternMatcher patternMatcher;

    public PythonRegexAnalyzer(
            DocumentorConfig configParam,
            PythonElementExtractor elementExtractorParam,
            PythonPatternMatcher patternMatcherParam) {
        this.config = configParam;
        this.elementExtractor = elementExtractorParam;
        this.patternMatcher = patternMatcherParam;
    }

    /**
     * ðŸ” Fallback regex-based analysis for when AST parsing fails
     */
    public List<CodeElement> analyzeWithRegex(Path filePath, List<String> lines) {
        List<CodeElement> elements = new ArrayList<>();
        String content = String.join("\n", lines);

        // Process each type of element
        processClassElements(filePath, lines, content, elements);
        processFunctionElements(filePath, lines, content, elements);
        processVariableElements(filePath, content, elements);

        return elements;
    }

    /**
     * ðŸ“‹ Process class declarations
     */
    private void processClassElements(Path filePath, List<String> lines, String content, List<CodeElement> elements) {
        var matcher = patternMatcher.findClassMatches(content);

        while (matcher.find()) {
            String className = matcher.group(1);
            if (shouldInclude(className)) {
                int lineNumber = getLineNumber(content, matcher.start());
                elements.add(new CodeElement(
                    CodeElementType.CLASS,
                    className,
                    "class " + className,
                    filePath.toString(),
                    lineNumber,
                    matcher.group().trim(),
                    elementExtractor.extractDocstring(lines, lineNumber),
                    List.of(),
                    List.of()
                ));
            }
        }
    }

    /**
     * ðŸ“‹ Process function declarations
     */
    private void processFunctionElements(Path filePath, List<String> lines, String content, List<CodeElement> elements) {
        var matcher = patternMatcher.findFunctionMatches(content);

        while (matcher.find()) {
            String functionName = matcher.group(1);
            if (shouldInclude(functionName)) {
                int lineNumber = getLineNumber(content, matcher.start());
                String paramString = matcher.group(2);
                List<String> params = List.of(patternMatcher.extractParameters(paramString));

                elements.add(new CodeElement(
                    CodeElementType.METHOD,
                    functionName,
                    matcher.group().trim(),
                    filePath.toString(),
                    lineNumber,
                    matcher.group().trim(),
                    elementExtractor.extractDocstring(lines, lineNumber),
                    params,
                    List.of()
                ));
            }
        }
    }

    /**
     * ðŸ“‹ Process variable assignments
     */
    private void processVariableElements(Path filePath, String content, List<CodeElement> elements) {
        var matcher = patternMatcher.findVariableMatches(content);

        while (matcher.find()) {
            String variableName = matcher.group(1);
            if (shouldInclude(variableName)) {
                int lineNumber = getLineNumber(content, matcher.start());
                elements.add(new CodeElement(
                    CodeElementType.FIELD,
                    variableName,
                    variableName,
                    filePath.toString(),
                    lineNumber,
                    matcher.group().trim(),
                    "",
                    List.of(),
                    List.of()
                ));
            }
        }
    }

    /**
     * ðŸ” Determines line number from character position
     */
    private int getLineNumber(String content, int position) {
        int line = 1;
        for (int i = 0; i < position; i++) {
            if (content.charAt(i) == '\n') {
                line++;
            }
        }
        return line;
    }

    /**
     * ðŸ” Checks if an element should be included based on configuration
     */
    private boolean shouldInclude(String name) {
        boolean isPrivate = name.startsWith("_");
        return config.analysisSettings().includePrivateMembers() || !isPrivate;
    }
}
