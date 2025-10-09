package com.documentor.service.python;

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

/**
 * 🐍 Python AST Processor
 * 
 * Specialized component for analyzing Python files using Python's AST module
 * via subprocess execution.
 */
@Component
public class PythonASTProcessor {

    private static final Logger logger = LoggerFactory.getLogger(PythonASTProcessor.class);

    /**
     * 🔬 Analyzes Python file using Python's AST module via subprocess
     */
    public List<CodeElement> analyzeWithAST(Path filePath) throws IOException, InterruptedException {
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
                    CodeElement element = parseASTOutput(line, filePath);
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
    private CodeElement parseASTOutput(String line, Path filePath) {
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
}