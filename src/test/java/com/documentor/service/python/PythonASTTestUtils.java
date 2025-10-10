package com.documentor.service.python;

import com.documentor.model.CodeElement;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;

/**
 * Utility class for testing refactored Python code analysis components
 */
public class PythonASTTestUtils {

    /**
     * Helper method to invoke the parseASTOutputLine method on PythonASTCommandBuilder
     */
    public static CodeElement parseASTOutputLine(PythonASTCommandBuilder commandBuilder, String line, Path filePath) {
        try {
            return commandBuilder.parseASTOutputLine(line, filePath);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse AST output line", e);
        }
    }
}
