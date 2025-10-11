package com.documentor.service.python;

import com.documentor.model.CodeElement;

import java.nio.file.Path;

/**
 * Utility class for testing refactored Python code analysis components
 */
public final class PythonASTTestUtils {

    /**
     * Private constructor to prevent instantiation of utility class
     */
    private PythonASTTestUtils() {
        throw new UnsupportedOperationException("Utility class should not be instantiated");
    }

    /**
     * Helper method to invoke the parseASTOutputLine method on PythonASTCommandBuilder
     */
    public static CodeElement parseASTOutputLine(final PythonASTCommandBuilder commandBuilder,
                                                 final String line, final Path filePath) {
        try {
            return commandBuilder.parseASTOutputLine(line, filePath);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse AST output line", e);
        }
    }
}

