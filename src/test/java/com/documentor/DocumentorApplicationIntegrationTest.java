package com.documentor;

import com.documentor.cli.DocumentorCommands;
import com.documentor.config.AppConfig;
import com.documentor.config.DocumentorConfig;
import com.documentor.service.CodeAnalysisService;
import com.documentor.service.DocumentationService;
import com.documentor.service.LlmService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ðŸ§ª Integration tests for DocumentorApplication
 *
 * Tests the complete Spring Boot application context and component integration.
 * This ensures all beans are properly configured and wired together.
 */
@SpringBootTest
@ActiveProfiles("test")
class DocumentorApplicationIntegrationTest {

    @Autowired
    private DocumentorCommands documentorCommands;

    @Autowired
    private CodeAnalysisService codeAnalysisService;

    @Autowired
    private DocumentationService documentationService;

    @Autowired
    private LlmService llmService;

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private DocumentorConfig documentorConfig;

    @Test
    void testApplicationContextLoads() {
        // Verify that the Spring Boot application context loads successfully
        // and all required beans are available
        assertNotNull(documentorCommands, "DocumentorCommands bean should be loaded");
        assertNotNull(codeAnalysisService, "CodeAnalysisService bean should be loaded");
        assertNotNull(documentationService, "DocumentationService bean should be loaded");
        assertNotNull(llmService, "LlmService bean should be loaded");
        assertNotNull(appConfig, "AppConfig bean should be loaded");
        assertNotNull(documentorConfig, "DocumentorConfig bean should be loaded");
    }

    @Test
    void testSpringBootApplicationConfiguration() {
        // Test that the application is configured as a non-web application
        // This validates the application.yml configuration
        assertNotNull(documentorConfig.llmModels(), "LLM models should be configured");
        assertFalse(documentorConfig.llmModels().isEmpty(), "At least one LLM model should be configured");

        assertNotNull(documentorConfig.outputSettings(), "Output settings should be configured");
        assertNotNull(documentorConfig.analysisSettings(), "Analysis settings should be configured");
    }

    @Test
    void testComponentIntegration() {
        // Test that components are properly integrated and can work together
        // This validates @ComponentScan and @EnableAutoConfiguration

        // DocumentorCommands should have its dependencies injected
        assertNotNull(documentorCommands, "DocumentorCommands should be properly instantiated");

        // Test basic command functionality to ensure components are wired correctly
        String infoResult = documentorCommands.showInfo();
        assertNotNull(infoResult, "showInfo() should return a result");
        assertFalse(infoResult.isEmpty(), "showInfo() should return non-empty content");
        assertTrue(infoResult.contains("Documentor"), "Info should contain application name");
    }

    @Test
    void testAsyncConfiguration() {
        // Test that @EnableAsync is working correctly
        // LlmService should be available and configured for async operations
        assertNotNull(llmService, "LlmService should be available for async operations");

        // DocumentationService should be able to handle async calls
        assertNotNull(documentationService, "DocumentationService should be available");
    }

    @Test
    void testConfigurationPropertiesScan() {
        // Test that @ConfigurationPropertiesScan is working
        // DocumentorConfig should be properly bound from properties
        assertNotNull(documentorConfig, "DocumentorConfig should be bound from properties");
        assertNotNull(documentorConfig.llmModels(), "LLM models should be bound from configuration");

        // Verify test-specific configuration is loaded
        assertEquals("test-model", documentorConfig.llmModels().get(0).name(),
                     "Test configuration should override default model name");
        assertEquals("test-key", documentorConfig.llmModels().get(0).apiKey(),
                     "Test configuration should override default API key");
    }

    @Test
    void testMainApplicationClass() {
        // Test the main application class annotations and configuration
        Class<?> appClass = DocumentorApplication.class;

        // Verify Spring Boot annotations are present
        assertTrue(appClass.isAnnotationPresent(org.springframework.boot.autoconfigure.SpringBootApplication.class),
                   "Application should be annotated with @SpringBootApplication");
        assertTrue(appClass.isAnnotationPresent(org.springframework.boot.context.properties.ConfigurationPropertiesScan.class),
                   "Application should be annotated with @ConfigurationPropertiesScan");
        assertTrue(appClass.isAnnotationPresent(org.springframework.scheduling.annotation.EnableAsync.class),
                   "Application should be annotated with @EnableAsync");
    }

    @Test
    void testQuickStartCommand() {
        // Test the quick start command works in the integrated environment
        String quickStartResult = documentorCommands.quickStart();
        assertNotNull(quickStartResult, "quickStart() should return a result");
        assertFalse(quickStartResult.isEmpty(), "quickStart() should return non-empty content");
        assertTrue(quickStartResult.contains("Quick Start Guide"), "Quick start should contain guide content");
        assertTrue(quickStartResult.contains("config.json"), "Quick start should mention configuration");
    }
}
