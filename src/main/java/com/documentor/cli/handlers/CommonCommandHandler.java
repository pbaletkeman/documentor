package com.documentor.cli.handlers;

import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 🔗 Common Command Handler
 *
 * Provides shared functionality for CLI command handlers to reduce complexity.
 * Extracted from command handlers to improve maintainability.
 */
@Component
public class CommonCommandHandler {

    /**
     * 🔍 Validates a file path exists
     */
    public boolean fileExists(String path) {
        if (path == null || path.trim().isEmpty()) {
            return false;
        }
        
        java.nio.file.Path filePath = java.nio.file.Paths.get(path);
        return java.nio.file.Files.exists(filePath);
    }
    
    /**
     * 🔍 Validates a directory path exists
     */
    public boolean directoryExists(String path) {
        if (path == null || path.trim().isEmpty()) {
            return false;
        }
        
        java.nio.file.Path dirPath = java.nio.file.Paths.get(path);
        return java.nio.file.Files.exists(dirPath) && java.nio.file.Files.isDirectory(dirPath);
    }
    
    /**
     * 📊 Creates standardized statistics section
     */
    public String formatStatistics(String title, Map<String, Object> stats) {
        StringBuilder result = new StringBuilder();
        result.append("📊 ").append(title).append("\n");
        result.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
        
        stats.forEach((key, value) -> {
            result.append(key).append(": ").append(value).append("\n");
        });
        
        return result.toString();
    }
    
    /**
     * 📝 Creates standardized result builder
     */
    public StringBuilder createResultBuilder() {
        return new StringBuilder();
    }
    
    /**
     * ⚠️ Creates error message with consistent format
     */
    public String formatErrorMessage(String message, Exception e) {
        return "❌ " + message + ": " + e.getMessage();
    }
}