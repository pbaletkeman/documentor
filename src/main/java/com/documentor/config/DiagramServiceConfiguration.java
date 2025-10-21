package com.documentor.config;

import com.documentor.service.MermaidDiagramService;
import com.documentor.service.PlantUMLDiagramService;
import com.documentor.service.diagram.DiagramElementFilter;
import com.documentor.service.diagram.DiagramGeneratorFactory;
import com.documentor.service.diagram.DiagramPathManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Configuration class for diagram services with ThreadLocalContextHolder support.
 * Provides enhanced versions of diagram services with improved thread-local context handling.
 */
@Configuration
public class DiagramServiceConfiguration {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(DiagramServiceConfiguration.class);

    /**
     * Creates a MermaidDiagramService with ThreadLocalContextHolder support
     */
    @Bean
    @Primary
    public MermaidDiagramService mermaidDiagramService(
            final DiagramElementFilter elementFilter,
            final DiagramPathManager pathManager,
            final DiagramGeneratorFactory generatorFactory,
            final DocumentorConfig documentorConfig) {

        LOGGER.info("Creating MermaidDiagramService with ThreadLocalContextHolder support");

        // Set the config in ThreadLocalContextHolder for good measure
        if (documentorConfig != null) {
            ThreadLocalContextHolder.setConfig(documentorConfig);
            LOGGER.debug("Config set in ThreadLocalContextHolder during MermaidDiagramService creation");
        }

        return new MermaidDiagramService(elementFilter, pathManager, generatorFactory);
    }

    /**
     * Creates a PlantUMLDiagramService with ThreadLocalContextHolder support
     */
    @Bean
    @Primary
    public PlantUMLDiagramService plantUMLDiagramService(
            final DiagramElementFilter elementFilter,
            final DiagramPathManager pathManager,
            final DiagramGeneratorFactory generatorFactory,
            final DocumentorConfig documentorConfig) {

        LOGGER.info("Creating PlantUMLDiagramService with ThreadLocalContextHolder support");

        // Set the config in ThreadLocalContextHolder for good measure
        if (documentorConfig != null) {
            ThreadLocalContextHolder.setConfig(documentorConfig);
            LOGGER.debug("Config set in ThreadLocalContextHolder during PlantUMLDiagramService creation");
        }

        return new PlantUMLDiagramService(elementFilter, pathManager, generatorFactory);
    }
}

