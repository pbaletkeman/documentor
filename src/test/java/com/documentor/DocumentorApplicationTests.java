package com.documentor;

import com.documentor.config.DocumentorConfig;
import com.documentor.config.TestConfig;
import com.documentor.config.model.AnalysisSettings;
import com.documentor.config.model.LlmModelConfig;
import com.documentor.config.model.OutputSettings;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

/**
 * Main Application Tests
 *
 * Integration tests for the Documentor Spring Boot application.
 */
@SpringBootTest(classes = DocumentorApplication.class)
@ActiveProfiles("test")
@Import(TestConfig.class)
class DocumentorApplicationTests {

    @Test
    void contextLoads() {
        // Test that the Spring context loads successfully
        // This validates the basic application configuration
    }
}
