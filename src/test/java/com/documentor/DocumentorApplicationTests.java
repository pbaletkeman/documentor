package com.documentor;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * 🧪 Main Application Tests
 * 
 * Integration tests for the Documentor Spring Boot application.
 */
@SpringBootTest
@ActiveProfiles("test")
class DocumentorApplicationTests {

    @Test
    void contextLoads() {
        // Test that the Spring context loads successfully
        // This validates the basic application configuration
    }
}