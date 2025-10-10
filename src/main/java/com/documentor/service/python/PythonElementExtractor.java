package com.documentor.service.python;

import com.documentor.constants.ApplicationConstants;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 🔧 Python Element Extractor
 *
 * Specialized component for extracting Python-specific elements like
 * docstrings and function parameters from source code.
 */
@Component
public class PythonElementExtractor {

    /**
     * 📝 Extracts docstring from Python code starting at given line index
     */
    public String extractDocstring(List<String> lines, int startIndex) {
        if (startIndex >= lines.size()) return "";

        String nextLine = lines.get(startIndex).trim();
        if (nextLine.startsWith("\"\"\"") || nextLine.startsWith("'''")) {
            StringBuilder docstring = new StringBuilder();
            String quote = nextLine.startsWith("\"\"\"") ? "\"\"\"" : "'''";

            // Handle single-line docstring
            if (nextLine.substring(ApplicationConstants.FUNCTION_DEF_PREFIX_LENGTH).endsWith(quote)) {
                return nextLine.substring(ApplicationConstants.FUNCTION_DEF_PREFIX_LENGTH,
                    nextLine.length() - ApplicationConstants.FUNCTION_DEF_PREFIX_LENGTH);
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

    /**
     * 🔧 Extracts function parameters from a Python function definition line
     */
    public List<String> extractParameters(String functionLine) {
        int start = functionLine.indexOf('(');
        int end = functionLine.lastIndexOf(')');
        if (start == -1 || end == -1 || start >= end) return List.of();

        String params = functionLine.substring(start + 1, end).trim();
        if (params.isEmpty()) return List.of();

        return List.of(params.split(",\\s*"));
    }
}