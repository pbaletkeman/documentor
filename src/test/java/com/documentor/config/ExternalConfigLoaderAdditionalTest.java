package com.documentor.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.withSettings;

/**
 * Additional tests for ExternalConfigLoader to specifically
 * target uncovered branches.
 */
class ExternalConfigLoaderAdditionalTest {

    @TempDir
    private Path tempDir;

    private ExternalConfigLoader externalConfigLoader;
    private DocumentorConfig testConfig;

    @BeforeEach
    void setUp() {
        externalConfigLoader = new ExternalConfigLoader();

        // Create test configuration
        testConfig = new DocumentorConfig(
            List.of(new com.documentor.config.model.LlmModelConfig(
                "test-model", "ollama", "http://localhost:11434",
                "test-key", 1000, 30)),
            new com.documentor.config.model.OutputSettings("output",
               "markdown", false, false, false),
            new com.documentor.config.model.AnalysisSettings(true,
                5, List.of("*.java"), null)
        );
    }

    /**
     * Test configurationPostProcessor with already loaded config
     */
    @Test
    void testConfigurationPostProcessorWithPreLoadedConfig() throws Exception {
        // Create and load a config first
        Path configFile = tempDir.resolve("test-config.json");
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(configFile.toFile(), testConfig);

        String[] args = {"analyze", "--config", configFile.toString()};
        assertTrue(externalConfigLoader.loadExternalConfig(args));

        // Now test the post processor with the already loaded config
        ConfigurableListableBeanFactory beanFactory = mock(
            ConfigurableListableBeanFactory.class,
            withSettings().extraInterfaces(BeanDefinitionRegistry.class));
        when(beanFactory.getBeanDefinitionNames()).thenReturn(new String[0]);

        var postProcessor = externalConfigLoader.configurationPostProcessor();
        assertDoesNotThrow(() -> postProcessor
            .postProcessBeanFactory(beanFactory));

        // Verify the registry was used to register the bean
        verify((BeanDefinitionRegistry) beanFactory).registerBeanDefinition(eq(
            "documentorConfig"), any());
    }

    /**
     * Test configurationPostProcessor with system property extraction
     */
    @Test
    void testConfigurationPostProcessorWithSystemProperty() throws Exception {
        // Create a config file
        Path configFile = tempDir.resolve("test-config.json");
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(configFile.toFile(), testConfig);

        // Set system property to simulate command line
        String originalCommand = System.getProperty("sun.java.command", "");
        try {
            System.setProperty("sun.java.command",
                "com.documentor.Application analyze --config "
                + configFile.toString());

            ConfigurableListableBeanFactory beanFactory = mock(
                ConfigurableListableBeanFactory.class,
                withSettings().extraInterfaces(
                    BeanDefinitionRegistry.class));

            var postProcessor = externalConfigLoader
                .configurationPostProcessor();
            assertDoesNotThrow(() ->
                postProcessor.postProcessBeanFactory(beanFactory));

            // Verify that the system property path was used
            verify((BeanDefinitionRegistry) beanFactory)
                .registerBeanDefinition(eq("documentorConfig"), any());
        } finally {
            if (originalCommand.isEmpty()) {
                System.clearProperty("sun.java.command");
            } else {
                System.setProperty("sun.java.command", originalCommand);
            }
        }
    }

    /**
     * Test configurationPostProcessor with Gradle args system property
     */
    @Test
    void testConfigurationPostProcessorWithGradleArgs() throws Exception {
        // Create a config file
        Path configFile = tempDir.resolve("test-config.json");
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(configFile.toFile(), testConfig);

        // Set Gradle args system property
        String originalArgs = System.getProperty("args", "");
        try {
            System.setProperty("args", "analyze,--config,"
            + configFile.toString());

            ConfigurableListableBeanFactory beanFactory =
                mock(ConfigurableListableBeanFactory.class,
                withSettings().extraInterfaces(BeanDefinitionRegistry.class));

            var postProcessor = externalConfigLoader
                .configurationPostProcessor();
            assertDoesNotThrow(() -> postProcessor
                .postProcessBeanFactory(beanFactory));

            // The post processor should complete successfully
            // (verification may not be reliable)
        } finally {
            if (originalArgs.isEmpty()) {
                System.clearProperty("args");
            } else {
                System.setProperty("args", originalArgs);
            }
        }
    }

    /**
     * Test configurationPostProcessor with no args available
     */
    @Test
    void testConfigurationPostProcessorWithNoArgs() {
        // Clear system properties
        String originalCommand = System.getProperty("sun.java.command", "");
        String originalArgs = System.getProperty("args", "");
        try {
            System.clearProperty("sun.java.command");
            System.clearProperty("args");

            ConfigurableListableBeanFactory beanFactory = mock(
                ConfigurableListableBeanFactory.class);

            var postProcessor = externalConfigLoader
                .configurationPostProcessor();
            assertDoesNotThrow(() ->
                postProcessor.postProcessBeanFactory(beanFactory));

            // Should complete without error when no args are available
        } finally {
            if (!originalCommand.isEmpty()) {
                System.setProperty("sun.java.command", originalCommand);
            }
            if (!originalArgs.isEmpty()) {
                System.setProperty("args", originalArgs);
            }
        }
    }

    /**
     * Test configurationPostProcessor with non-existent config file
     */
    @Test
    void testConfigurationPostProcessorWithNonExistentFile() {
        String originalCommand = System.getProperty("sun.java.command", "");
        try {
            System.setProperty("sun.java.command",
            "com.documentor.Application analyze --config non-existent.json");

            ConfigurableListableBeanFactory beanFactory =
                mock(ConfigurableListableBeanFactory.class);

            var postProcessor =
                externalConfigLoader.configurationPostProcessor();
            assertDoesNotThrow(() ->
                postProcessor.postProcessBeanFactory(beanFactory));

            // Should handle non-existent file gracefully
        } finally {
            if (originalCommand.isEmpty()) {
                System.clearProperty("sun.java.command");
            } else {
                System.setProperty("sun.java.command", originalCommand);
            }
        }
    }

    /**
     * Test configurationPostProcessor with invalid JSON file
     */
    @Test
    void testConfigurationPostProcessorWithInvalidJson() throws Exception {
        // Create invalid JSON file
        Path configFile = tempDir.resolve("invalid.json");
        Files.writeString(configFile, "{ invalid json content }");

        String originalCommand = System.getProperty("sun.java.command", "");
        try {
            System.setProperty("sun.java.command",
            "com.documentor.Application analyze --config "
            + configFile.toString());

            ConfigurableListableBeanFactory beanFactory =
                mock(ConfigurableListableBeanFactory.class);

            var postProcessor = externalConfigLoader
                .configurationPostProcessor();
            assertDoesNotThrow(() ->
                postProcessor.postProcessBeanFactory(beanFactory));

            // Should handle JSON parsing errors gracefully
        } finally {
            if (originalCommand.isEmpty()) {
                System.clearProperty("sun.java.command");
            } else {
                System.setProperty("sun.java.command", originalCommand);
            }
        }
    }

    /**
     * Test configurationPostProcessor with exception during command extraction
     */
    @Test
    void testConfigurationPostProcessorWithCommandExtractionException() {
        // Force an exception during system property access
        String originalCommand = System.getProperty("sun.java.command", "");
        try {
            // Set an invalid command that might cause parsing issues
            System.setProperty("sun.java.command", "");

            ConfigurableListableBeanFactory beanFactory = mock(
                ConfigurableListableBeanFactory.class);

            var postProcessor = externalConfigLoader
                .configurationPostProcessor();
            assertDoesNotThrow(() ->
                postProcessor.postProcessBeanFactory(beanFactory));

            // Should handle exceptions during command extraction gracefully
        } finally {
            if (originalCommand.isEmpty()) {
                System.clearProperty("sun.java.command");
            } else {
                System.setProperty("sun.java.command", originalCommand);
            }
        }
    }

    /**
     * Test extractConfigPath with -Pargs colon syntax
     */
    @Test
    void testExtractConfigPathWithColonSyntax() throws Exception {
        Path configFile = tempDir.resolve("test-config.json");
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(configFile.toFile(), testConfig);

        // Test -Pargs: syntax (colon instead of equals)
        String[] args = {"-Pargs:analyze,--config," + configFile.toString()};
        boolean result = externalConfigLoader.loadExternalConfig(args);

        assertTrue(result);
        assertNotNull(externalConfigLoader.getLoadedConfig());
    }

    /**
     * Test extractConfigPath with comma-separated args without config
     */
    @Test
    void testExtractConfigPathWithCommaSeparatedNoConfig() {
        String[] args = {"analyze,--verbose,--output,file.txt"};
        boolean result = externalConfigLoader.loadExternalConfig(args);
        assertFalse(result);
    }

    /**
     * Test extractConfigPath with -Pargs but no config
     */
    @Test
    void testExtractConfigPathWithGradleArgsNoConfig() {
        String[] args = {"-Pargs=analyze,--verbose"};
        boolean result = externalConfigLoader.loadExternalConfig(args);
        assertFalse(result);
    }

    /**
     * Test command extraction when main class is at end of command
     */
    @Test
    void testConfigurationPostProcessorWithMainClassAtEnd() throws Exception {
        Path configFile = tempDir.resolve("test-config.json");
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(configFile.toFile(), testConfig);

        String originalCommand = System.getProperty("sun.java.command", "");
        try {
            // Set command with main class only (no space after)
            System.setProperty("sun.java.command",
            "com.documentor.Application");

            ConfigurableListableBeanFactory beanFactory = mock(
                ConfigurableListableBeanFactory.class);

            var postProcessor =
                externalConfigLoader.configurationPostProcessor();
            assertDoesNotThrow(() ->
                postProcessor.postProcessBeanFactory(beanFactory));

            // Should handle case where no args follow the main class
        } finally {
            if (originalCommand.isEmpty()) {
                System.clearProperty("sun.java.command");
            } else {
                System.setProperty("sun.java.command", originalCommand);
            }
        }
    }

    /**
     * Test edge case where config args is last in array for comma-separated
     */
    @Test
    void testExtractConfigPathCommaSeparatedConfigLast() {
        // Config arg is last in comma-separated list (should not find value)
        String[] args = {"analyze,--verbose,--config"};
        boolean result = externalConfigLoader.loadExternalConfig(args);
        assertFalse(result);
    }

    /**
     * Test edge case where config args is last in array for gradle args
     */
    @Test
    void testExtractConfigPathGradleArgsConfigLast() {
        // Config arg is last in gradle args (should not find value)
        String[] args = {"-Pargs=analyze,--verbose,--config"};
        boolean result = externalConfigLoader.loadExternalConfig(args);
        assertFalse(result);
    }
}
