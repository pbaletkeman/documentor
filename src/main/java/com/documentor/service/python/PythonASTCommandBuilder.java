package com.documentor.service.python;

import com.documentor.constants.ApplicationConstants;
import com.documentor.model.CodeElement;
import com.documentor.model.CodeElementType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * ðŸ Python AST Processor Command Builder
 *
 * Handles generating and executing Python commands for AST analysis.
 * Extracted from PythonASTProcessor to reduce complexity.
 */
@Component
public class PythonASTCommandBuilder {

    /**
     * ðŸ“ Gets the AST analysis script
     */
    public String getPythonAstScript() {
        return """
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
    }

    /**
     * ðŸ“ Writes the temporary Python script
     */
    public Path writeTempScript() throws IOException {
        Path tempScript = Files.createTempFile("python_analyzer", ".py");
        Files.write(tempScript, getPythonAstScript().getBytes());
        return tempScript;
    }

    /**
     * ðŸ“ Creates a process builder for Python execution
     */
    public ProcessBuilder createProcessBuilder(Path scriptPath, Path filePath) {
        return new ProcessBuilder("python", scriptPath.toString(), filePath.toString());
    }

    /**
     * ðŸ“ Parses a single line of AST output
     */
    public CodeElement parseASTOutputLine(String line, Path filePath) {
        String[] parts = line.split("\\|", -1);
        if (parts.length < ApplicationConstants.MINIMUM_PARTS_FOR_PARSING) {
            return null;
        }

        String type = parts[0];
        String name = parts[1];
        int lineNumber = Integer.parseInt(parts[2]);
        String docstring = parts[ApplicationConstants.FUNCTION_DEF_PREFIX_LENGTH];

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
                List<String> parameters = parts.length > ApplicationConstants.PARAMETERS_ARRAY_INDEX &&
                    !parts[ApplicationConstants.PARAMETERS_ARRAY_INDEX].isEmpty()
                    ? List.of(parts[ApplicationConstants.PARAMETERS_ARRAY_INDEX].split(","))
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
