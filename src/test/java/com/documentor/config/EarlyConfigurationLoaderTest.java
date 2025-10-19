package com.documentor.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.DefaultApplicationArguments;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.mockito.Mockito.*;

/**
 * Test for early configuration loading functionality.
 */
public class EarlyConfigurationLoaderTest {

    @TempDir
    Path tempDir;

    @Test
    public void testEarlyConfigurationLoading() throws IOException {
        // Create a mock ExternalConfigLoader
        ExternalConfigLoader mockConfigLoader = mock(ExternalConfigLoader.class);

        // Create test arguments with config parameter
        String configPath = tempDir.resolve("test-config.json").toString();
        Files.writeString(tempDir.resolve("test-config.json"), "{}");
        String[] args = new String[]{"--config", configPath};

        // Create ApplicationArguments object with our test args
        ApplicationArguments appArgs = new DefaultApplicationArguments(args);

        // Create the early loader
        EarlyConfigurationLoader earlyLoader = new EarlyConfigurationLoader(mockConfigLoader);

        // Run the loader
        earlyLoader.run(appArgs);

        // Verify that loadExternalConfig was called with the correct arguments
        verify(mockConfigLoader).loadExternalConfig(args);
    }

    @Test
    public void testEarlyConfigurationLoadingWithMultipleArgs() throws IOException {
        // Create a mock ExternalConfigLoader
        ExternalConfigLoader mockConfigLoader = mock(ExternalConfigLoader.class);

        // Create test arguments with config parameter among other args
        String configPath = tempDir.resolve("test-config.json").toString();
        Files.writeString(tempDir.resolve("test-config.json"), "{}");
        String[] args = new String[]{"analyze", "--config", configPath, "--verbose"};

        // Create ApplicationArguments object with our test args
        ApplicationArguments appArgs = new DefaultApplicationArguments(args);

        // Create the early loader
        EarlyConfigurationLoader earlyLoader = new EarlyConfigurationLoader(mockConfigLoader);

        // Run the loader
        earlyLoader.run(appArgs);

        // Verify that loadExternalConfig was called with the correct arguments
        verify(mockConfigLoader).loadExternalConfig(args);
    }
}
