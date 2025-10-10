package com.documentor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * ðŸ“š Documentor Application - AI-Powered Code Documentation Generator
 *
 * This Spring Boot Command Line application analyzes Java and Python projects
 * to generate comprehensive documentation using Large Language Models (LLMs).
 *
 * Features:
 * - ðŸ” Analyzes Java and Python codebases
 * - ðŸ¤– Integrates with multiple LLM models (GPT-3.5, GPT-4, etc.)
 * - ðŸ“ Generates markdown documentation with examples
 * - ðŸ§ª Creates unit tests for analyzed code
 * - âš¡ Multi-threaded processing for performance
 * - ðŸ”§ Pre-commit hooks for quality assurance
 */
@SpringBootApplication
@ConfigurationPropertiesScan
@EnableAsync
public class DocumentorApplication {

    public static void main(final String[] args) {
        SpringApplication.run(DocumentorApplication.class, args);
    }
}
