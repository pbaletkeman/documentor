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
 * Standalone Test Application for Enhanced Documentor
 *
 * This is a special test version that completely excludes all original components
 * to eliminate any potential bean conflicts.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
@EnableAsync
@Import({
    AppConfigEnhanced.class
})
@ComponentScan(
    basePackages = "com.documentor",
    excludeFilters = {
        @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = {
                com.documentor.DocumentorApplication.class,
                com.documentor.config.AppConfig.class,
                com.documentor.service.LlmService.class,
                com.documentor.config.LlmServiceConfiguration.class
            }
        )
    }
)
public final class DocumentorTestApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentorTestApplication.class);

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private DocumentorTestApplication() {
        throw new UnsupportedOperationException("Utility class should not be instantiated");
    }

    public static void main(final String[] args) {
        LOGGER.info("Starting DocumentorTestApplication - Test version with enhanced error handling");
        try {
            SpringApplication.run(DocumentorTestApplication.class, args);
        } catch (Exception e) {
            LOGGER.error("Error in test application: {}", e.getMessage(), e);
            System.exit(1);
        }
    }
}
