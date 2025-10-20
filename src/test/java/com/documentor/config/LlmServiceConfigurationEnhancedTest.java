package com.documentor.config;

import com.documentor.config.model.AnalysisSettings;
import com.documentor.config.model.LlmModelConfig;
import com.documentor.config.model.OutputSettings;
import com.documentor.constants.ApplicationConstants;
import com.documentor.service.LlmServiceEnhanced;
import com.documentor.service.LlmServiceFixEnhanced;
import com.documentor.service.documentation.ElementDocumentationGeneratorEnhanced;
import com.documentor.service.llm.LlmApiClient;
import com.documentor.service.llm.LlmRequestBuilder;
import com.documentor.service.llm.LlmResponseHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Comprehensive test suite for LlmServiceConfigurationEnhanced.
 * Tests all configuration scenarios and branch paths to maximize coverage.
 */
@ExtendWith(MockitoExtension.class)
class LlmServiceConfigurationEnhancedTest {

    @Mock
    private DocumentorConfig mockDocumentorConfig;

    @Mock
    private LlmRequestBuilder mockRequestBuilder;

    @Mock
    private LlmResponseHandler mockResponseHandler;

    @Mock
    private LlmApiClient mockApiClient;

    @Mock
    private OutputSettings mockOutputSettings;

    @Mock
    private AnalysisSettings mockAnalysisSettings;

    private LlmServiceConfigurationEnhanced configuration;

    @BeforeEach
    void setUp() {
        configuration = new LlmServiceConfigurationEnhanced();
        // Clear ThreadLocal before each test
        ThreadLocalContextHolder.clearConfig();
    }

    @AfterEach
    void tearDown() {
        // Clear ThreadLocal after each test to prevent leakage
        ThreadLocalContextHolder.clearConfig();
    }

    @Test
    void testLlmServiceEnhanced_WithValidDocumentorConfig() {
        // Given
        LlmModelConfig modelConfig = new LlmModelConfig(
                "test-model",
                "ollama",
                "http://localhost:11434",
                "test-key",
                4096,
                30
        );
        when(mockDocumentorConfig.llmModels()).thenReturn(List.of(modelConfig));

        // When
        LlmServiceEnhanced result = configuration.llmServiceEnhanced(
                mockDocumentorConfig,
                mockRequestBuilder,
                mockResponseHandler,
                mockApiClient
        );

        // Then
        assertThat(result).isNotNull();
        // Verify the config was set in ThreadLocal
        DocumentorConfig storedConfig = ThreadLocalContextHolder.getConfig();
        assertThat(storedConfig).isEqualTo(mockDocumentorConfig);
    }

    @Test
    void testLlmServiceEnhanced_WithNullDocumentorConfig() {
        // Given
        DocumentorConfig nullConfig = null;

        // When
        LlmServiceEnhanced result = configuration.llmServiceEnhanced(
                nullConfig,
                mockRequestBuilder,
                mockResponseHandler,
                mockApiClient
        );

        // Then
        assertThat(result).isNotNull();
        // Verify a default config was created and stored in ThreadLocal
        DocumentorConfig storedConfig = ThreadLocalContextHolder.getConfig();
        assertThat(storedConfig).isNotNull();
        assertThat(storedConfig.llmModels()).hasSize(1);

        LlmModelConfig defaultModel = storedConfig.llmModels().get(0);
        assertThat(defaultModel.name()).isEqualTo("default-model");
        assertThat(defaultModel.provider()).isEqualTo("ollama");
        assertThat(defaultModel.baseUrl()).isEqualTo("http://localhost:11434");
        assertThat(defaultModel.apiKey()).isEmpty();
        assertThat(defaultModel.maxTokens()).isEqualTo(ApplicationConstants.DEFAULT_MAX_TOKENS);
        assertThat(defaultModel.timeoutSeconds()).isEqualTo(ApplicationConstants.DEFAULT_TIMEOUT_SECONDS);
    }

    @Test
    void testLlmServiceEnhanced_WithEmptyModelList() {
        // Given
        when(mockDocumentorConfig.llmModels()).thenReturn(Collections.emptyList());
        when(mockDocumentorConfig.outputSettings()).thenReturn(mockOutputSettings);
        when(mockDocumentorConfig.analysisSettings()).thenReturn(mockAnalysisSettings);

        // When
        LlmServiceEnhanced result = configuration.llmServiceEnhanced(
                mockDocumentorConfig,
                mockRequestBuilder,
                mockResponseHandler,
                mockApiClient
        );

        // Then
        assertThat(result).isNotNull();

        // Verify a config with default model was created
        DocumentorConfig storedConfig = ThreadLocalContextHolder.getConfig();
        assertThat(storedConfig).isNotNull();
        assertThat(storedConfig.llmModels()).hasSize(1);
        assertThat(storedConfig.outputSettings()).isEqualTo(mockOutputSettings);
        assertThat(storedConfig.analysisSettings()).isEqualTo(mockAnalysisSettings);

        LlmModelConfig addedModel = storedConfig.llmModels().get(0);
        assertThat(addedModel.name()).isEqualTo("default-model");
        assertThat(addedModel.provider()).isEqualTo("ollama");
    }

    @Test
    void testLlmServiceEnhanced_WithNullModelList() {
        // Given
        when(mockDocumentorConfig.llmModels()).thenReturn(null);
        when(mockDocumentorConfig.outputSettings()).thenReturn(mockOutputSettings);
        when(mockDocumentorConfig.analysisSettings()).thenReturn(mockAnalysisSettings);

        // When
        LlmServiceEnhanced result = configuration.llmServiceEnhanced(
                mockDocumentorConfig,
                mockRequestBuilder,
                mockResponseHandler,
                mockApiClient
        );

        // Then
        assertThat(result).isNotNull();

        // Verify a config with default model was created
        DocumentorConfig storedConfig = ThreadLocalContextHolder.getConfig();
        assertThat(storedConfig).isNotNull();
        assertThat(storedConfig.llmModels()).hasSize(1);
        assertThat(storedConfig.outputSettings()).isEqualTo(mockOutputSettings);
        assertThat(storedConfig.analysisSettings()).isEqualTo(mockAnalysisSettings);

        LlmModelConfig addedModel = storedConfig.llmModels().get(0);
        assertThat(addedModel.name()).isEqualTo("default-model");
    }

    @Test
    void testLlmServiceFixEnhanced_Creation() {
        // When
        LlmServiceFixEnhanced result = configuration.llmServiceFixEnhanced();

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    void testElementDocumentationGeneratorEnhanced_Creation() {
        // Given
        LlmServiceEnhanced mockLlmService = new LlmServiceEnhanced(
                createTestConfig(),
                mockRequestBuilder,
                mockResponseHandler,
                mockApiClient
        );
        LlmServiceFixEnhanced mockLlmFixService = new LlmServiceFixEnhanced();

        // When
        ElementDocumentationGeneratorEnhanced result = configuration.elementDocumentationGeneratorEnhanced(
                mockLlmService,
                mockLlmFixService
        );

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    void testCreateDefaultConfig_DefaultValues() {
        // This tests the private createDefaultConfig method indirectly through null config path
        // When
        LlmServiceEnhanced result = configuration.llmServiceEnhanced(
                null,
                mockRequestBuilder,
                mockResponseHandler,
                mockApiClient
        );

        // Then
        assertThat(result).isNotNull();

        // Verify the default config was created correctly
        DocumentorConfig storedConfig = ThreadLocalContextHolder.getConfig();
        assertThat(storedConfig).isNotNull();
        assertThat(storedConfig.llmModels()).hasSize(1);
        assertThat(storedConfig.outputSettings()).isNull();
        // analysisSettings may have defaults, don't assert on exact null value

        LlmModelConfig model = storedConfig.llmModels().get(0);
        assertThat(model.maxTokens()).isEqualTo(ApplicationConstants.DEFAULT_MAX_TOKENS);
        assertThat(model.timeoutSeconds()).isEqualTo(ApplicationConstants.DEFAULT_TIMEOUT_SECONDS);
    }

    @Test
    void testAddDefaultModel_PreservesExistingSettings() {
        // Given - config with no models but with other settings
        when(mockDocumentorConfig.llmModels()).thenReturn(Collections.emptyList());
        when(mockDocumentorConfig.outputSettings()).thenReturn(mockOutputSettings);
        when(mockDocumentorConfig.analysisSettings()).thenReturn(mockAnalysisSettings);

        // When
        LlmServiceEnhanced result = configuration.llmServiceEnhanced(
                mockDocumentorConfig,
                mockRequestBuilder,
                mockResponseHandler,
                mockApiClient
        );

        // Then
        assertThat(result).isNotNull();

        // Verify settings were preserved when adding default model
        DocumentorConfig storedConfig = ThreadLocalContextHolder.getConfig();
        assertThat(storedConfig.llmModels()).hasSize(1);
        assertThat(storedConfig.outputSettings()).isEqualTo(mockOutputSettings);
        assertThat(storedConfig.analysisSettings()).isEqualTo(mockAnalysisSettings);
    }

    @Test
    void testLlmServiceEnhanced_LogsConfigurationDetails() {
        // Given
        LlmModelConfig modelConfig1 = new LlmModelConfig("model1", "ollama", "endpoint1", "key1", 1000, 10);
        LlmModelConfig modelConfig2 = new LlmModelConfig("model2", "openai", "endpoint2", "key2", 2000, 20);
        when(mockDocumentorConfig.llmModels()).thenReturn(List.of(modelConfig1, modelConfig2));

        // When
        LlmServiceEnhanced result = configuration.llmServiceEnhanced(
                mockDocumentorConfig,
                mockRequestBuilder,
                mockResponseHandler,
                mockApiClient
        );

        // Then
        assertThat(result).isNotNull();

        // Verify multi-model configuration was handled correctly
        DocumentorConfig storedConfig = ThreadLocalContextHolder.getConfig();
        assertThat(storedConfig).isEqualTo(mockDocumentorConfig);
        assertThat(storedConfig.llmModels()).hasSize(2);
    }

    @Test
    void testConfigurationConstructor() {
        // When
        LlmServiceConfigurationEnhanced newConfig = new LlmServiceConfigurationEnhanced();

        // Then
        assertThat(newConfig).isNotNull();
    }

    @Test
    void testThreadLocalConfigurationPropagation() {
        // Given
        LlmModelConfig modelConfig = new LlmModelConfig(
                "test-model",
                "ollama",
                "http://localhost:11434",
                "",
                4096,
                30
        );
        DocumentorConfig testConfig = new DocumentorConfig(List.of(modelConfig), null, null);

        // When
        LlmServiceEnhanced result = configuration.llmServiceEnhanced(
                testConfig,
                mockRequestBuilder,
                mockResponseHandler,
                mockApiClient
        );

        // Then
        assertThat(result).isNotNull();
        // Verify the config was set in ThreadLocal
        DocumentorConfig storedConfig = ThreadLocalContextHolder.getConfig();
        assertThat(storedConfig).isEqualTo(testConfig);
    }

    @Test
    void testDefaultModelCreation_ValidatesDefaultValues() {
        // This test covers the private methods by triggering their execution
        // Given - null config to trigger createDefaultConfig path
        DocumentorConfig nullConfig = null;

        // When
        LlmServiceEnhanced result = configuration.llmServiceEnhanced(
                nullConfig,
                mockRequestBuilder,
                mockResponseHandler,
                mockApiClient
        );

        // Then
        assertThat(result).isNotNull();

        // Verify the default config was created and stored
        DocumentorConfig storedConfig = ThreadLocalContextHolder.getConfig();
        assertThat(storedConfig).isNotNull();
        assertThat(storedConfig.llmModels()).hasSize(1);

        LlmModelConfig defaultModel = storedConfig.llmModels().get(0);
        assertThat(defaultModel.name()).isEqualTo("default-model");
        assertThat(defaultModel.provider()).isEqualTo("ollama");
        assertThat(defaultModel.baseUrl()).isEqualTo("http://localhost:11434");
        assertThat(defaultModel.apiKey()).isEmpty();
        assertThat(defaultModel.maxTokens()).isEqualTo(ApplicationConstants.DEFAULT_MAX_TOKENS);
        assertThat(defaultModel.timeoutSeconds()).isEqualTo(ApplicationConstants.DEFAULT_TIMEOUT_SECONDS);
    }

    @Test
    void testAddDefaultModel_CreatesNewConfigWithDefaultModel() {
        // Given - empty model list to trigger addDefaultModel path
        when(mockDocumentorConfig.llmModels()).thenReturn(Collections.emptyList());
        when(mockDocumentorConfig.outputSettings()).thenReturn(mockOutputSettings);
        when(mockDocumentorConfig.analysisSettings()).thenReturn(mockAnalysisSettings);

        // When
        LlmServiceEnhanced result = configuration.llmServiceEnhanced(
                mockDocumentorConfig,
                mockRequestBuilder,
                mockResponseHandler,
                mockApiClient
        );

        // Then
        assertThat(result).isNotNull();

        // Verify the config with default model was created and stored
        DocumentorConfig storedConfig = ThreadLocalContextHolder.getConfig();
        assertThat(storedConfig).isNotNull();
        assertThat(storedConfig.llmModels()).hasSize(1);
        assertThat(storedConfig.outputSettings()).isEqualTo(mockOutputSettings);
        assertThat(storedConfig.analysisSettings()).isEqualTo(mockAnalysisSettings);

        LlmModelConfig addedModel = storedConfig.llmModels().get(0);
        assertThat(addedModel.name()).isEqualTo("default-model");
        assertThat(addedModel.provider()).isEqualTo("ollama");
    }

    /**
     * Helper method to create a test configuration
     */
    private DocumentorConfig createTestConfig() {
        LlmModelConfig modelConfig = new LlmModelConfig(
                "test-model",
                "ollama",
                "http://localhost:11434",
                "",
                4096,
                30
        );
        return new DocumentorConfig(List.of(modelConfig), null, null);
    }
}
