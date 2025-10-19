package com.documentor.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Early configuration loader that runs before Spring Shell starts processing commands.
 * This component loads the external configuration as early as possible in the application
 * startup process by using ApplicationRunner with a high priority order.
 */
@Component
@Order(0) // Highest priority to run before other components
public class EarlyConfigurationLoader implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(EarlyConfigurationLoader.class);
    private final ExternalConfigLoader configLoader;

    /**
     * Constructor that takes the external configuration loader.
     * @param configLoader the external configuration loader
     */
    public EarlyConfigurationLoader(final ExternalConfigLoader configLoader) {
        this.configLoader = configLoader;
    }

    /**
     * Run method that gets called by Spring Boot before shell initialization.
     * This ensures configuration is loaded before any commands are processed.
     * @param args application arguments
     */
    @Override
    public void run(final ApplicationArguments args) {
        LOGGER.info("Early configuration loading started");

        // Convert Spring's ApplicationArguments to String[] for compatibility with existing code
        String[] rawArgs = args.getSourceArgs();

        // Trigger configuration loading
        boolean loaded = configLoader.loadExternalConfig(rawArgs);

        if (loaded) {
            LOGGER.info("Early configuration loading completed successfully");
        } else {
            LOGGER.info("Early configuration loading completed without finding a config file");
        }
    }
}
