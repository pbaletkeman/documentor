package com.documentor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;

/**
 * Main Application Tests
 *
 * Integration tests for the Documentor Spring Boot application.
 */
@SpringBootTest(classes = DocumentorApplication.class)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class DocumentorApplicationTests {

    @Test
    void contextLoads() {
        // Test that the Spring context loads successfully
        // This validates the basic application configuration
    }

    @Test
    void testMainMethod() {
        // Test main method using mocked static SpringApplication.run
        try (MockedStatic<SpringApplication> mockedSpringApp =
            mockStatic(SpringApplication.class)) {
            // When
            DocumentorApplication.main(new String[]{"--help"});

            // Then
            mockedSpringApp.verify(() -> SpringApplication.run(eq(
                DocumentorApplication.class), any(String[].class)));
        }
    }

    @Test
    void testMainMethodWithEmptyArgs() {
        // Test main method with empty arguments
        try (MockedStatic<SpringApplication> mockedSpringApp =
            mockStatic(SpringApplication.class)) {
            // When
            DocumentorApplication.main(new String[]{});

            // Then
            mockedSpringApp.verify(() -> SpringApplication.run(eq(
                DocumentorApplication.class), any(String[].class)));
        }
    }

    @Test
    void testMainMethodWithNullArgs() {
        // Test main method with null arguments
        try (MockedStatic<SpringApplication> mockedSpringApp = mockStatic(
            SpringApplication.class)) {
            // When
            DocumentorApplication.main(null);

            // Then
            mockedSpringApp.verify(() -> SpringApplication.run(eq(
                DocumentorApplication.class), any()));
        }
    }

    @Test
    void testMainMethodWithMultipleArgs() {
        // Test main method with multiple arguments
        try (MockedStatic<SpringApplication> mockedSpringApp =
            mockStatic(SpringApplication.class)) {
            String[] args =
                {"--spring.profiles.active=test", "--debug",
                "--project-path=/test"};

            // When
            DocumentorApplication.main(args);

            // Then
            mockedSpringApp.verify(() -> SpringApplication.run(eq(
                DocumentorApplication.class), eq(args)));
        }
    }

    @Test
    void testApplicationClassAnnotations() {
        // Verify the application class has required annotations
        Class<DocumentorApplication> clazz = DocumentorApplication.class;

        assertTrue(clazz.isAnnotationPresent(SpringBootApplication.class));
    }

    @Test
    void testApplicationClassExists() {
        // Verify the application class exists and is properly defined
        assertNotNull(DocumentorApplication.class);
        assertFalse(DocumentorApplication.class.isInterface());
        assertFalse(java.lang.reflect.Modifier
            .isAbstract(DocumentorApplication.class.getModifiers()));
    }

    @Test
    void testMainMethodExists() throws NoSuchMethodException {
        // Verify main method exists with correct signature
        java.lang.reflect.Method mainMethod = DocumentorApplication.class
            .getDeclaredMethod("main", String[].class);

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
        assertEquals("com.documentor",
            DocumentorApplication.class.getPackageName());
    }

    @Test
    void testMainMethodWithComplexArguments() {
        // Test main method with complex argument patterns
        try (MockedStatic<SpringApplication> mockedSpringApp =
            mockStatic(SpringApplication.class)) {
            String[] complexArgs = {
                "--spring.profiles.active=production",
                "--logging.level.com.documentor=INFO",
                "--server.port=8080",
                "--project-path=/production/path"
            };

            // When
            DocumentorApplication.main(complexArgs);

            // Then
            mockedSpringApp.verify(() -> SpringApplication.run(eq(
                DocumentorApplication.class), eq(complexArgs)));
        }
    }

    @Test
    void testSpringBootApplicationAnnotation() {
        // Verify SpringBootApplication annotation configuration
        SpringBootApplication annotation = DocumentorApplication.class.
            getAnnotation(SpringBootApplication.class);
        assertNotNull(annotation);
    }

    @Test

    void testMainMethodWithProductionProfile() {
        // Test main method with production profile
        try (MockedStatic<SpringApplication> mockedSpringApp =
            mockStatic(SpringApplication.class)) {
            String[] prodArgs = {"--spring.profiles.active=production"};

            // When
            DocumentorApplication.main(prodArgs);

            // Then
            mockedSpringApp.verify(() -> SpringApplication.run(eq(
                DocumentorApplication.class), eq(prodArgs)));
        }
    }

    @Test
    void testMainMethodWithLoggingConfiguration() {
        // Test main method with logging configuration
        try (MockedStatic<SpringApplication> mockedSpringApp =
            mockStatic(SpringApplication.class)) {
            String[] loggingArgs = {
                "--logging.level.com.documentor=INFO",
                "--logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
            };

            // When
            DocumentorApplication.main(loggingArgs);

            // Then
            mockedSpringApp.verify(() -> SpringApplication.run(eq(
                DocumentorApplication.class), eq(loggingArgs)));
        }
    }

    @Test
    void testMainMethodWithJvmArguments() {
        // Test main method with JVM-like arguments
        try (MockedStatic<SpringApplication> mockedSpringApp =
            mockStatic(SpringApplication.class)) {
            String[] jvmArgs = {
                "-Dfile.encoding=UTF-8",
                "--spring.application.name=documentor"
            };

            // When
            DocumentorApplication.main(jvmArgs);

            // Then
            mockedSpringApp.verify(() ->
                SpringApplication.run(eq(
                    DocumentorApplication.class), eq(jvmArgs)));
        }
    }

    @Test
    void testApplicationClassInheritance() {
        // Test application class inheritance and structure
        Class<DocumentorApplication> clazz = DocumentorApplication.class;
        assertEquals(Object.class, clazz.getSuperclass());
        assertFalse(clazz.isEnum());
        assertFalse(clazz.isAnnotation());
    }
}
