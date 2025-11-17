package com.documentor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 📚 Documentor Application - AI-Powered Code Documentation Generator
 *
 * This Spring Boot Command Line application analyzes Java and Python projects
 * to generate comprehensive documentation using Large Language Models (LLMs).
 *
 * Features:
 * - 🔍 Analyzes Java and Python codebases
 * - 🤖 Integrates with multiple LLM models (GPT-3.5, GPT-4, etc.)
 * - 📝 Generates markdown documentation with examples
 * - 🧪 Creates unit tests for analyzed code
 * - ⚡ Multi-threaded processing for performance
 * - 🔧 Pre-commit hooks for quality assurance
 */
@SpringBootApplication
@ConfigurationPropertiesScan
@EnableAsync
public final class DocumentorApplication {

    private DocumentorApplication() {
        // Utility class should not be instantiated
    }

    public static void main(final String[] args) {
        SpringApplication.run(DocumentorApplication.class, args);
    }
}
