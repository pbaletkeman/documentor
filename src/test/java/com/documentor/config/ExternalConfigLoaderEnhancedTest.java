package com.documentor.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.context.ApplicationContext;

import java.nio.file.Path;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests for enhanced ExternalConfigLoader functionality.
 */
public class ExternalConfigLoaderEnhancedTest {

    @TempDir
    private Path tempDir;



    @Test
    public void testLoadExternalConfigWithNoConfigArg() {
        // Create a mock ApplicationContext for the loader
        ApplicationContext mockContext = mock(ApplicationContext.class);

        // Create our loader
        ExternalConfigLoader loader = spy(new ExternalConfigLoader());
        loader.setApplicationContext(mockContext);

        // Test with no config argument
        String[] args = new String[]{"analyze", "--verbose"};
        boolean result = loader.loadExternalConfig(args);

        // Verify result
        assertFalse(result, "Config loading should return"
        + " false when no config arg provided");
        assertNull(loader.getLoadedConfig(), "Config should not be loaded");
    }

    @Test
    public void testLoadExternalConfigWithNonexistentFile() {
        // Create a mock ApplicationContext for the loader
        ApplicationContext mockContext = mock(ApplicationContext.class);

        // Create our loader
        ExternalConfigLoader loader = spy(new ExternalConfigLoader());
        loader.setApplicationContext(mockContext);

        // Test with nonexistent config file
        String[] args = new String[]{"--config",
        "/path/to/nonexistent/file.json"};
        boolean result = loader.loadExternalConfig(args);

        // Verify result
        assertFalse(result,
            "Config loading should return false when file doesn't exist");
        assertNull(loader.getLoadedConfig(), "Config should not be loaded");
    }
}
