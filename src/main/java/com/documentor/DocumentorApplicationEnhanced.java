package com.documentor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.documentor.config.AppConfigEnhanced;

/**
 * Enhanced Documentor Application
 *
 * This is an enhanced version of the DocumentorApplication with improved error handling
 * and null safety to fix the "NullPointerException in CompletableFuture" issues.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
@EnableAsync
/*
 * Import the enhanced configuration explicitly
 * This ensures our enhanced components with @Primary annotations take precedence
 */
@Import({
    AppConfigEnhanced.class,
    com.documentor.config.LlmServiceConfigurationEnhanced.class,
    com.documentor.config.DocumentationServiceConfiguration.class,
    com.documentor.config.DiagramServiceConfiguration.class
})
@ComponentScan(
    basePackages = "com.documentor",
    excludeFilters = {
        @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = {com.documentor.DocumentorApplication.class}
        ),
        @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = {com.documentor.config.AppConfig.class}
        )
    }
)
public final class DocumentorApplicationEnhanced {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(DocumentorApplicationEnhanced.class);

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private DocumentorApplicationEnhanced() {
        throw new UnsupportedOperationException("Utility class should not be instantiated");
    }

    public static void main(final String[] args) {
        LOGGER.info("Starting DocumentorApplicationEnhanced - Enhanced version with improved error handling");
        SpringApplication.run(DocumentorApplicationEnhanced.class, args);
    }
}
