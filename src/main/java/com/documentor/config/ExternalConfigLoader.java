package com.documentor.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * External Configuration Loader for Documentor.
 *
 * Loads configuration from an external JSON file when specified via the --config parameter.
 * This runs at application startup before any other components.
 */
@Configuration
@Profile("!test")
public class ExternalConfigLoader implements ApplicationContextAware {

    private static final Logger LOGGER =
        LoggerFactory.getLogger(ExternalConfigLoader.class);
    private static final String CONFIG_ARG = "--config";

    private ApplicationContext applicationContext;

    /**
     * Sets the application context.
     *
     * @param context the application context
     * @throws BeansException if an error occurs
     */
    @Override
    public void setApplicationContext(final ApplicationContext context)
            throws BeansException {
        this.applicationContext = context;
    }

    /**
     * Bean post processor to intercept and modify the DocumentorConfig bean.
     * @return a BeanFactoryPostProcessor
     */
    /**
     * Load configuration from command line arguments. This method can be
     * called directly by other components early in the startup process.
     *
     * @param args the command line arguments
     * @return true if configuration was loaded successfully, false otherwise
     */
    public boolean loadExternalConfig(final String[] args) {
        LOGGER.info("Loading external config from arguments: {}",
            Arrays.toString(args));

        // Extract config path
        String configPath = extractConfigPath(args);
        if (configPath == null) {
            LOGGER.info("No config file specified in arguments");
            return false;
        }

        LOGGER.info("Found config path: {}", configPath);

        // Load external configuration if exists
        Path configFile = Paths.get(configPath);
        if (!Files.exists(configFile)) {
            LOGGER.error("Configuration file not found: {}", configPath);
            return false;
        }

        try {
            LOGGER.info("Loading external configuration from: {}", configPath);
            ObjectMapper objectMapper = new ObjectMapper();
            DocumentorConfig externalConfig = objectMapper.readValue(
                    configFile.toFile(), DocumentorConfig.class);
            LOGGER.info("External configuration loaded successfully with {} LLM "
                + "models", externalConfig.llmModels().size());

            // Store the config for later use
            this.loadedConfig = externalConfig;

            return true;
        } catch (Exception e) {
            LOGGER.error("Failed to load external configuration from {}: {}",
                    configPath, e.getMessage(), e);
            return false;
        }
    }

    // Store the loaded configuration
    private DocumentorConfig loadedConfig;

    /**
     * Get the loaded configuration.
     * @return the loaded configuration or null if none was loaded
     */
    public DocumentorConfig getLoadedConfig() {
        return loadedConfig;
    }

    /**
     * Creates a bean factory post processor to handle external configuration.
     *
     * @return a BeanFactoryPostProcessor
     */
    @Bean
    public BeanFactoryPostProcessor configurationPostProcessor() {
        return beanFactory -> {
            LOGGER.info("ExternalConfigLoader's BeanFactoryPostProcessor "
                + "running");

            // Check if we already have a loaded configuration from
            // EarlyConfigurationLoader
            if (loadedConfig != null) {
                LOGGER.info("Using configuration already loaded by "
                    + "EarlyConfigurationLoader");

                // Define the DocumentorConfig bean with the already loaded
                // configuration
                BeanDefinition beanDefinition =
                    BeanDefinitionBuilder.genericBeanDefinition(
                        DocumentorConfig.class, () -> loadedConfig)
                        .setPrimary(true)
                        .getBeanDefinition();

                // Register or replace the bean definition
                ((BeanDefinitionRegistry) beanFactory).registerBeanDefinition(
                    "documentorConfig", beanDefinition);
                LOGGER.info("External configuration registered successfully");
                return;
            }

            // If not loaded yet, try loading from command line args (fallback)
            LOGGER.info("No configuration loaded yet, trying command line "
                + "arguments as fallback");

            // Get command line arguments
            String[] args = null;

            try {
                // Try to get the raw command line arguments
                String cmdArgs = System.getProperty("sun.java.command", "");
                LOGGER.info("Raw command: {}", cmdArgs);

                // Extract the arguments after the main class
                int mainClassEnd = cmdArgs.indexOf(' ');
                if (mainClassEnd > 0) {
                    String argsStr = cmdArgs.substring(mainClassEnd + 1);
                    args = argsStr.split(" ");
                    LOGGER.info("Extracted args: {}",
                        Arrays.toString(args));
                }
            } catch (Exception e) {
                LOGGER.warn("Error extracting command line arguments: {}", e.getMessage());
            }

            // If we couldn't get arguments from system properties, handle the
            // Gradle case
            if (args == null || args.length == 0) {
                String gradleArgs = System.getProperty("args", "");
                LOGGER.info("Gradle args property: {}", gradleArgs);
                if (!gradleArgs.isEmpty()) {
                    args = gradleArgs.split(",");
                    LOGGER.info("Gradle args split: {}",
                    Arrays.toString(args));
                }
            }

            // If we still don't have args, give up
            if (args == null || args.length == 0) {
                LOGGER.info("No command line arguments found");
                return;
            }

            // Extract config path
            String configPath = extractConfigPath(args);
            if (configPath == null) {
                LOGGER.info("No config file specified in arguments");
                return;
            }

            LOGGER.info("Found config path: {}", configPath);

            // Load external configuration if exists
            Path configFile = Paths.get(configPath);
            if (!Files.exists(configFile)) {
                LOGGER.error("Configuration file not found: {}", configPath);
                return;
            }

            try {
                LOGGER.info("Loading external configuration from: {}", configPath);
                ObjectMapper objectMapper = new ObjectMapper();
                DocumentorConfig externalConfig = objectMapper.readValue(
                        configFile.toFile(), DocumentorConfig.class);
                LOGGER.info("External configuration loaded successfully with {} "
                    + "LLM models", externalConfig.llmModels().size());

                // Save for future reference
                this.loadedConfig = externalConfig;

                // Define the DocumentorConfig bean with the loaded configuration
                // This will override any existing definition
                BeanDefinition beanDefinition =
                    BeanDefinitionBuilder.genericBeanDefinition(
                        DocumentorConfig.class, () -> externalConfig)
                        .setPrimary(true)
                        .getBeanDefinition();

                // Register or replace the bean definition
                ((BeanDefinitionRegistry) beanFactory).registerBeanDefinition(
                    "documentorConfig", beanDefinition);
                LOGGER.info("External configuration registered successfully");
            } catch (Exception e) {
                LOGGER.error("Failed to load external configuration from {}: "
                    + "{}", configPath, e.getMessage(), e);
            }
        };
    }

        /**
     * Extract the path to the configuration file from command line
     * arguments.
     * @param args command line arguments
     * @return path to configuration file or null if not found
     */
    private static String extractConfigPath(final String[] args) {
        if (args == null || args.length == 0) {
            return null;
        }

        LOGGER.info("Extracting config path from args: {}",
            Arrays.toString(args));

        // Handle different ways the config might be specified:
        // 1. As separate arguments: "--config" "file.json"
        // 2. As a single argument: "--config=file.json"
        // 3. As comma-separated: "analyze,--config,file.json"
        // 4. Mixed in with other args: "-Pargs=analyze,--config,file.json"

        // First check for comma-separated args (Gradle style)
        for (String arg : args) {
            // Look for comma-separated values
            if (arg.contains(",")) {
                String[] parts = arg.split(",");
                for (int i = 0; i < parts.length - 1; i++) {
                    if (CONFIG_ARG.equals(parts[i]) && i + 1 < parts.length) {
                        LOGGER.info("Found config in comma-separated args: {}",
                            parts[i + 1]);
                        return parts[i + 1];
                    }
                }
            }

            // Look for -Pargs style
            if (arg.contains("-Pargs=") || arg.contains("-Pargs:")) {
                String argValue = arg.substring(arg.indexOf('=') + 1);
                String[] parts = argValue.split(",");
                for (int i = 0; i < parts.length - 1; i++) {
                    if (CONFIG_ARG.equals(parts[i]) && i + 1 < parts.length) {
                        LOGGER.info("Found config in -Pargs: {}", parts[i + 1]);
                        return parts[i + 1];
                    }
                }
            }

            // Look for --config=value style
            if (arg.startsWith(CONFIG_ARG + "=")) {
                String configPath = arg.substring(CONFIG_ARG.length() + 1);
                LOGGER.info("Found config in --config=value style: {}", configPath);
                return configPath;
            }
        }

        // Check for regular args format (--config file.json)
        for (int i = 0; i < args.length - 1; i++) {
            if (CONFIG_ARG.equals(args[i])) {
                LOGGER.info("Found config in standard args: {}", args[i + 1]);
                return args[i + 1];
            }
        }

        return null;
    }
}
