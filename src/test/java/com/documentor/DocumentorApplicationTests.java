package com.documentor;

import com.documentor.config.DocumentorConfig;
import com.documentor.config.model.AnalysisSettings;
import com.documentor.config.model.LlmModelConfig;
import com.documentor.config.model.OutputSettings;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

/**
 * 🧪 Main Application Tests
 * 
 * Integration tests for the Documentor Spring Boot application.
 */
@SpringBootTest
@ActiveProfiles("test")
class DocumentorApplicationTests {

    @TestConfiguration
    static class TestConfig {
        
        @Bean
        @Primary
        public DocumentorConfig documentorConfig() {
            LlmModelConfig llmModel = new LlmModelConfig(
                "test-model", 
                "test-provider", 
                "http://localhost:11434", 
                "test-key", 
                1000, 
                10
            );
            
            OutputSettings outputSettings = new OutputSettings(
                "./test-docs",
                "markdown",
                false,
                false
            );
            
            AnalysisSettings analysisSettings = new AnalysisSettings(
                true,
                5,
                List.of("**/*.java"),
                List.of("**/target/**")
            );
            
            return new DocumentorConfig(
                List.of(llmModel),
                outputSettings,
                analysisSettings
            );
        }
    }

    @Test
    void contextLoads() {
        // Test that the Spring context loads successfully
        // This validates the basic application configuration
    }
}
