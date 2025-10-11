package com.documentor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;

/**
 * ðŸ§ª Enhanced tests for DocumentorApplication
 *
 * Tests focused on improving coverage of the main application class.
 */
@ExtendWith(MockitoExtension.class)
class DocumentorApplicationEnhancedTest {

    @Test
    void testMainMethod() {
        // Test main method using mocked static SpringApplication.run
        try (MockedStatic<SpringApplication> mockedSpringApp = mockStatic(SpringApplication.class)) {
            // When
            DocumentorApplication.main(new String[]{"--help"});

            // Then
            mockedSpringApp.verify(() -> SpringApplication.run(eq(DocumentorApplication.class), any(String[].class)));
        }
    }

    @Test
    void testMainMethodWithEmptyArgs() {
        // Test main method with empty arguments
        try (MockedStatic<SpringApplication> mockedSpringApp = mockStatic(SpringApplication.class)) {
            // When
            DocumentorApplication.main(new String[]{});

            // Then
            mockedSpringApp.verify(() -> SpringApplication.run(eq(DocumentorApplication.class), any(String[].class)));
        }
    }

    @Test
    void testMainMethodWithNullArgs() {
        // Test main method with null arguments
        try (MockedStatic<SpringApplication> mockedSpringApp = mockStatic(SpringApplication.class)) {
            // When
            DocumentorApplication.main(null);

            // Then
            mockedSpringApp.verify(() -> SpringApplication.run(eq(DocumentorApplication.class), any()));
        }
    }

    @Test
    void testMainMethodWithMultipleArgs() {
        // Test main method with multiple arguments
        try (MockedStatic<SpringApplication> mockedSpringApp = mockStatic(SpringApplication.class)) {
            String[] args = {"--spring.profiles.active=test", "--debug", "--project-path=/test"};

            // When
            DocumentorApplication.main(args);

            // Then
            mockedSpringApp.verify(() -> SpringApplication.run(eq(DocumentorApplication.class), eq(args)));
        }
    }

    @Test
    void testApplicationClassExists() {
        // Verify the application class exists and is properly annotated
        assertNotNull(DocumentorApplication.class);
        assertTrue(DocumentorApplication.class.isAnnotationPresent(SpringBootApplication.class));
    }

    @Test
    void testApplicationClassIsNotAbstract() {
        // Verify the application class is concrete (not abstract)
        Class<DocumentorApplication> clazz = DocumentorApplication.class;
        assertFalse(clazz.isInterface());
        assertFalse(java.lang.reflect.Modifier.isAbstract(clazz.getModifiers()));
    }

    @Test
    void testApplicationClassHasMainMethod() throws NoSuchMethodException {
        // Verify main method exists with correct signature
        Class<DocumentorApplication> clazz = DocumentorApplication.class;
        java.lang.reflect.Method mainMethod = clazz.getDeclaredMethod("main", String[].class);

        assertNotNull(mainMethod);
        assertTrue(java.lang.reflect.Modifier.isStatic(mainMethod.getModifiers()));
        assertTrue(java.lang.reflect.Modifier.isPublic(mainMethod.getModifiers()));
        assertEquals(void.class, mainMethod.getReturnType());
    }

    @Test
    void testApplicationClassPackage() {
        // Verify the application class is in the correct package
        Class<DocumentorApplication> clazz = DocumentorApplication.class;
        assertEquals("com.documentor", clazz.getPackageName());
    }
}
