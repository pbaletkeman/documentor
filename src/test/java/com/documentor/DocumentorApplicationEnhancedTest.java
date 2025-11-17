package com.documentor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;

/**
 * Enhanced tests for DocumentorApplicationEnhanced
 *
 * Tests focused on improving coverage of the enhanced application class.
 */
@ExtendWith(MockitoExtension.class)
class DocumentorApplicationEnhancedTest {

    @Test
    void testMainMethod() {
        // Test main method using mocked static SpringApplication.run
        try (MockedStatic<SpringApplication> mockedSpringApp =
            mockStatic(SpringApplication.class)) {
            // When
            DocumentorApplicationEnhanced.main(new String[]{"--help"});

            // Then
            mockedSpringApp.verify(() -> SpringApplication.run(eq(
                    DocumentorApplicationEnhanced.class),
                    any(String[].class)));
        }
    }

    @Test
    void testMainMethodWithEmptyArgs() {
        // Test main method with empty arguments
        try (MockedStatic<SpringApplication> mockedSpringApp =
            mockStatic(SpringApplication.class)) {
            // When
            DocumentorApplicationEnhanced.main(new String[]{});

            // Then
            mockedSpringApp.verify(() -> SpringApplication.run(eq(
                    DocumentorApplicationEnhanced.class),
                    any(String[].class)));
        }
    }

    @Test
    void testMainMethodWithNullArgs() {
        // Test main method with null arguments
        try (MockedStatic<SpringApplication> mockedSpringApp =
            mockStatic(SpringApplication.class)) {
            // When
            DocumentorApplicationEnhanced.main(null);

            // Then
            mockedSpringApp.verify(() -> SpringApplication.run(eq(
                DocumentorApplicationEnhanced.class), any()));
        }
    }

    @Test
    void testMainMethodWithMultipleArgs() {
        // Test main method with multiple arguments
        try (MockedStatic<SpringApplication> mockedSpringApp =
            mockStatic(SpringApplication.class)) {
            String[] args = {"--spring.profiles.active=test",
            "--debug", "--project-path=/test"};

            // When
            DocumentorApplicationEnhanced.main(args);

            // Then
            mockedSpringApp.verify(() -> SpringApplication.run(eq(
                DocumentorApplicationEnhanced.class), eq(args)));
        }
    }

    @Test
    void testMainMethodSuccessfulExecution() {
        // Test successful main method execution
        try (MockedStatic<SpringApplication> mockedSpringApp =
            mockStatic(SpringApplication.class)) {
            // When
            DocumentorApplicationEnhanced.main(new String[]{"--test"});

            // Then
            mockedSpringApp.verify(() -> SpringApplication.run(eq(
                DocumentorApplicationEnhanced.class), any(String[].class)));
        }
    }

    @Test
    void testMainMethodWithLoggingEnabled() {
        // Test main method with various logging configurations
        try (MockedStatic<SpringApplication> mockedSpringApp =
            mockStatic(SpringApplication.class)) {
            String[] args = {"--logging.level.com.documentor=DEBUG",
                "--spring.profiles.active=test"};

            // When
            DocumentorApplicationEnhanced.main(args);

            // Then
            mockedSpringApp.verify(() -> SpringApplication.run(eq(
                DocumentorApplicationEnhanced.class), eq(args)));
        }
    }

    @Test
    void testApplicationClassExists() {
        // Verify the application class exists and has all required annotations
        assertNotNull(DocumentorApplicationEnhanced.class);
        assertTrue(DocumentorApplicationEnhanced.class
        .isAnnotationPresent(SpringBootApplication.class));
        assertTrue(DocumentorApplicationEnhanced.class
        .isAnnotationPresent(ConfigurationPropertiesScan.class));
        assertTrue(DocumentorApplicationEnhanced.class
        .isAnnotationPresent(EnableAsync.class));
        assertTrue(DocumentorApplicationEnhanced.class

        .isAnnotationPresent(Import.class));
        assertTrue(DocumentorApplicationEnhanced.class
        .isAnnotationPresent(ComponentScan.class));
    }

    @Test
    void testApplicationClassIsNotAbstract() {
        // Verify the application class is concrete (not abstract)
        Class<DocumentorApplicationEnhanced> clazz =
            DocumentorApplicationEnhanced.class;
        assertFalse(clazz.isInterface());
        assertFalse(java.lang.reflect.Modifier
            .isAbstract(clazz.getModifiers()));
    }

    @Test
    void testApplicationClassHasMainMethod() throws NoSuchMethodException {
        // Verify main method exists with correct signature
        Class<DocumentorApplicationEnhanced> clazz =
            DocumentorApplicationEnhanced.class;
        java.lang.reflect.Method mainMethod =
            clazz.getDeclaredMethod("main", String[].class);

        assertNotNull(mainMethod);
        assertTrue(java.lang.reflect.Modifier
            .isStatic(mainMethod.getModifiers()));
        assertTrue(java.lang.reflect.Modifier
         .isPublic(mainMethod.getModifiers()));
        assertEquals(void.class, mainMethod.getReturnType());
    }

    @Test
    void testApplicationClassPackage() {
        // Verify the application class is in the correct package
        Class<DocumentorApplicationEnhanced> clazz =
            DocumentorApplicationEnhanced.class;
        assertEquals("com.documentor", clazz.getPackageName());
    }

    @Test
    void testComponentScanConfiguration() {
        // Verify component scan configuration
        ComponentScan componentScan = DocumentorApplicationEnhanced.class
            .getAnnotation(ComponentScan.class);
        assertNotNull(componentScan);
        assertEquals("com.documentor", componentScan.basePackages()[0]);
        assertTrue(componentScan.excludeFilters().length > 0);
    }

    @Test
    void testImportConfiguration() {
        // Verify Import annotation configuration
        Import importAnnotation =
            DocumentorApplicationEnhanced.class.getAnnotation(Import.class);
        assertNotNull(importAnnotation);
        assertTrue(importAnnotation.value().length > 0);
    }

    @Test
    void testSpringBootApplicationConfiguration() {
        // Verify SpringBootApplication configuration
        SpringBootApplication springBootApp =
            DocumentorApplicationEnhanced.class.getAnnotation(
                SpringBootApplication.class);
        assertNotNull(springBootApp);
        // Note: exclude() array may be empty for this application
    }

    @Test
    void testConfigurationPropertiesScanExists() {
        // Verify ConfigurationPropertiesScan annotation exists
        ConfigurationPropertiesScan configScan =
            DocumentorApplicationEnhanced.class.getAnnotation(
                    ConfigurationPropertiesScan.class);
        assertNotNull(configScan);
        // Note: basePackages may be empty if using default package scanning
    }

    @Test
    void testEnableAsyncAnnotation() {
        // Verify EnableAsync annotation is present
        EnableAsync enableAsync =
            DocumentorApplicationEnhanced.class.getAnnotation(
                    EnableAsync.class);
        assertNotNull(enableAsync);
    }

    @Test
    void testMainMethodWithVariousArgumentPatterns() {
        // Test main method with various argument patterns
        try (MockedStatic<SpringApplication> mockedSpringApp =
            mockStatic(SpringApplication.class)) {
            String[] complexArgs = {
                "--spring.profiles.active=enhanced,test",
                "--logging.level.com.documentor=DEBUG",
                "--documentor.llm.provider=test",
                "--project-path=/complex/test/path",
                "--config=/path/to/config.json"
            };

            // When
            DocumentorApplicationEnhanced.main(complexArgs);

            // Then
            mockedSpringApp.verify(() -> SpringApplication.run(eq(
                DocumentorApplicationEnhanced.class), eq(complexArgs)));
        }
    }
}
