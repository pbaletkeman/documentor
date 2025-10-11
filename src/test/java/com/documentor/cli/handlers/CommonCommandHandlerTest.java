package com.documentor.cli.handlers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the CommonCommandHandler class
 * Used for testing refactored shared functionality
 */
class CommonCommandHandlerTest {

    @TempDir
    Path tempDir;

    @Test
    void testDirectoryExists() throws Exception {
        // Arrange
        CommonCommandHandler handler = new CommonCommandHandler();
        
        // Act & Assert
        assertTrue(handler.directoryExists(tempDir.toString()), "Should return true for an existing directory");
        assertFalse(handler.directoryExists("/path/that/does/not/exist"), "Should return false for non-existent directory");
        assertFalse(handler.directoryExists(null), "Should return false for null path");
        assertFalse(handler.directoryExists(""), "Should return false for empty path");
    }
    
    @Test
    void testFileExists() throws Exception {
        // Arrange
        CommonCommandHandler handler = new CommonCommandHandler();
        Path tempFile = Files.createFile(tempDir.resolve("test.txt"));
        
        // Act & Assert
        assertTrue(handler.fileExists(tempFile.toString()), "Should return true for an existing file");
        assertFalse(handler.fileExists("/path/that/does/not/exist.txt"), "Should return false for non-existent file");
        assertFalse(handler.fileExists(null), "Should return false for null path");
        assertFalse(handler.fileExists(""), "Should return false for empty path");
    }
    
    @Test
    void testCreateResultBuilder() {
        // Arrange
        CommonCommandHandler handler = new CommonCommandHandler();
        
        // Act
        StringBuilder builder = handler.createResultBuilder();
        
        // Assert
        assertNotNull(builder, "Result builder should not be null");
        assertEquals(0, builder.length(), "Result builder should be empty");
    }
    
    @Test
    void testFormatErrorMessage() {
        // Arrange
        CommonCommandHandler handler = new CommonCommandHandler();
        Exception exception = new RuntimeException("Test error");
        
        // Act
        String errorMessage = handler.formatErrorMessage("Error occurred", exception);
        
        // Assert
        assertEquals("❌ Error occurred: Test error", errorMessage, "Error message should be correctly formatted");
    }
    
    @Test
    void testFormatStatistics() {
        // Arrange
        CommonCommandHandler handler = new CommonCommandHandler();
        Map<String, Object> stats = new HashMap<>();
        stats.put("Key1", "Value1");
        stats.put("Key2", 123);
        
        // Act
        String formattedStats = handler.formatStatistics("Test Stats", stats);
        
        // Assert
        assertTrue(formattedStats.contains("📊 Test Stats"), "Formatted stats should contain the title");
        assertTrue(formattedStats.contains("Key1: Value1"), "Formatted stats should contain key-value pairs");
        assertTrue(formattedStats.contains("Key2: 123"), "Formatted stats should contain numeric values");
    }
}