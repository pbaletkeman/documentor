package com.documentor.config;

import com.documentor.service.DocumentationServiceEnhanced;
import com.documentor.service.LlmServiceFixEnhanced;
import com.documentor.service.MermaidDiagramService;
import com.documentor.service.PlantUMLDiagramService;
import com.documentor.service.documentation.ElementDocumentationGeneratorEnhanced;
import com.documentor.service.documentation.MainDocumentationGenerator;
import com.documentor.service.documentation.UnitTestDocumentationGeneratorEnhanced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Configuration class for enhanced documentation services.
 * Provides enhanced versions of documentation services with improved error handling and ThreadLocal management.
 */
@Configuration
public class DocumentationServiceConfiguration {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(DocumentationServiceConfiguration.class);

    /**
     * Creates an enhanced DocumentationService with improved error handling
     */
    @Bean
    @Primary
    public DocumentationServiceEnhanced documentationServiceEnhanced(
            final MainDocumentationGenerator mainDocGenerator,
            final ElementDocumentationGeneratorEnhanced elementDocGeneratorEnhanced,
            final UnitTestDocumentationGeneratorEnhanced testDocGeneratorEnhanced,
            final MermaidDiagramService mermaidDiagramService,
            final PlantUMLDiagramService plantUMLDiagramService,
            final DocumentorConfig documentorConfig,
            final LlmServiceFixEnhanced llmServiceFixEnhanced) {

        LOGGER.info("Creating enhanced DocumentationService with ThreadLocalContextHolder support");

        // Set the config in ThreadLocalContextHolder for good measure
        if (documentorConfig != null) {
            ThreadLocalContextHolder.setConfig(documentorConfig);
            LOGGER.debug("Config set in ThreadLocalContextHolder during DocumentationServiceEnhanced creation");
        }

        return new DocumentationServiceEnhanced(
            mainDocGenerator,
            elementDocGeneratorEnhanced,
            testDocGeneratorEnhanced,
            mermaidDiagramService,
            plantUMLDiagramService,
            documentorConfig,
            llmServiceFixEnhanced);
    }
}
