package com.documentor.service;

import com.documentor.config.DocumentorConfig;
import com.documentor.model.CodeElement;
import com.documentor.model.CodeElementType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 🐍 Python Code Analyzer
 * 
 * Parses Python source files using AST (Abstract Syntax Tree) to extract:
 * - Public classes (not starting with _)
 * - Public methods and functions
 * - Public variables and attributes
 */
@Component
public class PythonCodeAnalyzer {

    private static final Logger logger = LoggerFactory.getLogger(PythonCodeAnalyzer.class);

    private final DocumentorConfig config;

    // Regex patterns for Python code analysis
    private static final Pattern CLASS_PATTERN = Pattern.compile("^(\\s*)class\\s+([A-Za-z_][A-Za-z0-9_]*)\\s*(?:\\([^)]*\\))?\\s*:");
    private static final Pattern FUNCTION_PATTERN = Pattern.compile("^(\\s*)def\\s+([A-Za-z_][A-Za-z0-9_]*)\\s*\\([^)]*\\)\\s*(?:->\\s*[^:]+)?\\s*:");
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("^(\\s*)([A-Za-z_][A-Za-z0-9_]*)\\s*(?::\\s*[^=]+)?\\s*=\\s*(.+)");

    public PythonCodeAnalyzer(DocumentorConfig config) {
        this.config = config;
    }

    /**
     * 📄 Analyzes a Python file and extracts all non-private code elements
     * 
     * @param filePath Path to the Python source file
     * @return List of discovered code elements
     */
    public List<CodeElement> analyzeFile(Path filePath) throws IOException {
        logger.debug("🔍 Analyzing Python file: {}", filePath);

        List<String> lines = Files.readAllLines(filePath);
        List<CodeElement> elements = new ArrayList<>();

        try {
            // Try using Python's AST module for more accurate parsing
            List<CodeElement> astElements = analyzeWithPythonAST(filePath);
            if (!astElements.isEmpty()) {
                return astElements;
            }
        } catch (Exception e) {
            logger.debug("AST analysis failed, falling back to regex parsing: {}", e.getMessage());
        }

        // Fallback to regex-based parsing
        return analyzeWithRegex(filePath, lines);
    }

    /**
     * 🔬 Analyzes Python file using Python's AST module via subprocess
     */
    private List<CodeElement> analyzeWithPythonAST(Path filePath) throws IOException, InterruptedException {
        List<CodeElement> elements = new ArrayList<>();

        // Python script to analyze AST
        String pythonScript = """
            import ast
            import sys
            
            def analyze_file(filename):
                with open(filename, 'r', encoding='utf-8') as f:
                    source = f.read()
                
                try:
                    tree = ast.parse(source, filename)
                    for node in ast.walk(tree):
                        if isinstance(node, ast.ClassDef):
                            if not node.name.startswith('_'):
                                print(f"CLASS|{node.name}|{node.lineno}|{ast.get_docstring(node) or ''}")
                        elif isinstance(node, ast.FunctionDef):
                            if not node.name.startswith('_'):
                                args = [arg.arg for arg in node.args.args]
                                print(f"FUNCTION|{node.name}|{node.lineno}|{ast.get_docstring(node) or ''}|{','.join(args)}")
                        elif isinstance(node, ast.Assign):
                            for target in node.targets:
                                if isinstance(target, ast.Name) and not target.id.startswith('_'):
                                    print(f"VARIABLE|{target.id}|{node.lineno}||")
                except Exception as e:
                    print(f"ERROR|{str(e)}", file=sys.stderr)
            
            if __name__ == '__main__':
                analyze_file(sys.argv[1])
            """;

        // Write temporary Python script
        Path tempScript = Files.createTempFile("python_analyzer", ".py");
        Files.write(tempScript, pythonScript.getBytes());

        try {
            ProcessBuilder pb = new ProcessBuilder("python", tempScript.toString(), filePath.toString());
            Process process = pb.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    CodeElement element = parsePythonASTOutput(line, filePath);
                    if (element != null) {
                        elements.add(element);
                    }
                }
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new IOException("Python AST analysis failed with exit code: " + exitCode);
            }

        } finally {
            Files.deleteIfExists(tempScript);
        }

        return elements;
    }

    /**
     * 📝 Parses output from Python AST analysis
     */
    private CodeElement parsePythonASTOutput(String line, Path filePath) {
        String[] parts = line.split("\\|", -1);
        if (parts.length < 4) return null;

        String type = parts[0];
        String name = parts[1];
        int lineNumber = Integer.parseInt(parts[2]);
        String docstring = parts[3];

        return switch (type) {
            case "CLASS" -> new CodeElement(
                CodeElementType.CLASS,
                name,
                "class " + name,
                filePath.toString(),
                lineNumber,
                "class " + name + ":",
                docstring,
                List.of(),
                List.of()
            );
            case "FUNCTION" -> {
                List<String> parameters = parts.length > 4 && !parts[4].isEmpty() 
                    ? List.of(parts[4].split(",")) 
                    : List.of();
                yield new CodeElement(
                    CodeElementType.METHOD,
                    name,
                    "def " + name + "(" + String.join(", ", parameters) + ")",
                    filePath.toString(),
                    lineNumber,
                    "def " + name + "(" + String.join(", ", parameters) + "):",
                    docstring,
                    parameters,
                    List.of()
                );
            }
            case "VARIABLE" -> new CodeElement(
                CodeElementType.FIELD,
                name,
                name,
                filePath.toString(),
                lineNumber,
                name + " = ...",
                "",
                List.of(),
                List.of()
            );
            default -> null;
        };
    }

    /**
     * 🔍 Fallback regex-based analysis for when AST parsing fails
     */
    private List<CodeElement> analyzeWithRegex(Path filePath, List<String> lines) {
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
                        extractDocstring(lines, i + 1),
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
                        extractDocstring(lines, i + 1),
                        extractParameters(line),
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

    private boolean shouldInclude(String name) {
        boolean isPrivate = name.startsWith("_");
        return config.analysisSettings().includePrivateMembers() || !isPrivate;
    }

    private String extractDocstring(List<String> lines, int startIndex) {
        if (startIndex >= lines.size()) return "";
        
        String nextLine = lines.get(startIndex).trim();
        if (nextLine.startsWith("\"\"\"") || nextLine.startsWith("'''")) {
            StringBuilder docstring = new StringBuilder();
            String quote = nextLine.startsWith("\"\"\"") ? "\"\"\"" : "'''";
            
            // Handle single-line docstring
            if (nextLine.substring(3).endsWith(quote)) {
                return nextLine.substring(3, nextLine.length() - 3);
            }
            
            // Handle multi-line docstring
            for (int i = startIndex; i < lines.size(); i++) {
                String line = lines.get(i);
                docstring.append(line).append("\n");
                if (i > startIndex && line.trim().endsWith(quote)) {
                    break;
                }
            }
            
            return docstring.toString().replace(quote, "").trim();
        }
        
        return "";
    }

    private List<String> extractParameters(String functionLine) {
        int start = functionLine.indexOf('(');
        int end = functionLine.lastIndexOf(')');
        if (start == -1 || end == -1 || start >= end) return List.of();
        
        String params = functionLine.substring(start + 1, end).trim();
        if (params.isEmpty()) return List.of();
        
        return List.of(params.split(",\\s*"));
    }
}