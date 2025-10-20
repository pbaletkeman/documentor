package com.documentor.config;

import com.documentor.config.model.LlmModelConfig;
import com.documentor.service.LlmService;
import com.documentor.service.documentation.ElementDocumentationGenerator;
import com.documentor.service.llm.LlmApiClient;
import com.documentor.service.llm.LlmRequestBuilder;
import com.documentor.service.llm.LlmResponseHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for LlmServiceConfiguration.
 */
@ExtendWith(MockitoExtension.class)
class LlmServiceConfigurationTest {

    @Mock
    private LlmRequestBuilder requestBuilder;

    @Mock
    private LlmResponseHandler responseHandler;

    @Mock
    private LlmApiClient apiClient;

    private LlmServiceConfiguration configuration;

    @BeforeEach
    void setUp() {
        configuration = new LlmServiceConfiguration();
    }

    @Test
    void testDocumentorConfigWithValidConfig() {
        // Create a valid config
        LlmModelConfig model = new LlmModelConfig("test-model", "ollama", "http://localhost:11434", "key", 1000, 30);
        DocumentorConfig validConfig = new DocumentorConfig(
            List.of(model),
            null,
            null
        );

        // Should return the same config unchanged
        DocumentorConfig result = configuration.documentorConfig(validConfig);
        assertEquals(validConfig, result);
        assertEquals(1, result.llmModels().size());
        assertEquals("test-model", result.llmModels().get(0).name());
    }

    @Test
    void testDocumentorConfigWithNullConfig() {
        // Should create a default config
        DocumentorConfig result = configuration.documentorConfig(null);
        assertNotNull(result);
        assertNotNull(result.llmModels());
        assertEquals(1, result.llmModels().size());
        assertEquals("default-model", result.llmModels().get(0).name());
        assertEquals("ollama", result.llmModels().get(0).provider());
        assertEquals("http://localhost:11434", result.llmModels().get(0).baseUrl());
    }

    @Test
    void testDocumentorConfigWithEmptyModels() {
        // Create config with empty models list
        DocumentorConfig emptyConfig = new DocumentorConfig(
            List.of(),
            null,
            null
        );

        // Should add a default model
        DocumentorConfig result = configuration.documentorConfig(emptyConfig);
        assertNotNull(result);
        assertNotNull(result.llmModels());
        assertEquals(1, result.llmModels().size());
        assertEquals("default-model", result.llmModels().get(0).name());
    }

    @Test
    void testDocumentorConfigWithNullModels() {
        // Create config with null models list
        DocumentorConfig nullModelsConfig = new DocumentorConfig(
            null,
            null,
            null
        );

        // Should add a default model
        DocumentorConfig result = configuration.documentorConfig(nullModelsConfig);
        assertNotNull(result);
        assertNotNull(result.llmModels());
        assertEquals(1, result.llmModels().size());
        assertEquals("default-model", result.llmModels().get(0).name());
    }

    @Test
    void testLlmServiceWithValidConfig() {
        // Create a valid config
        LlmModelConfig model = new LlmModelConfig("test-model", "ollama", "http://localhost:11434", "key", 1000, 30);
        DocumentorConfig validConfig = new DocumentorConfig(
            List.of(model),
            null,
            null
        );

        // Should create LlmService with the provided config
        LlmService result = configuration.llmService(validConfig, requestBuilder, responseHandler, apiClient);
        assertNotNull(result);
    }

    @Test
    void testLlmServiceWithNullConfig() {
        // Should create LlmService with a default config
        LlmService result = configuration.llmService(null, requestBuilder, responseHandler, apiClient);
        assertNotNull(result);
    }

    @Test
    void testLlmServiceWithEmptyModels() {
        // Create config with empty models list
        DocumentorConfig emptyConfig = new DocumentorConfig(
            List.of(),
            null,
            null
        );

        // Should create LlmService with a default model added
        LlmService result = configuration.llmService(emptyConfig, requestBuilder, responseHandler, apiClient);
        assertNotNull(result);
    }

    @Test
    void testLlmServiceWithNullModels() {
        // Create config with null models list
        DocumentorConfig nullModelsConfig = new DocumentorConfig(
            null,
            null,
            null
        );

        // Should create LlmService with a default model added
        LlmService result = configuration.llmService(nullModelsConfig, requestBuilder, responseHandler, apiClient);
        assertNotNull(result);
    }

    @Test
    void testElementDocumentationGenerator() {
        LlmService mockLlmService = mock(LlmService.class);

        ElementDocumentationGenerator result = configuration.elementDocumentationGenerator(mockLlmService);
        assertNotNull(result);
    }
}
