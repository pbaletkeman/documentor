package com.documentor.config.model;

import com.documentor.constants.ApplicationConstants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for LlmModelConfig
 */
class LlmModelConfigTest {

    // Test constants for magic number violations
    private static final int DEFAULT_MAX_TOKENS = 1000;
    private static final int DEFAULT_TIMEOUT_SECONDS = 60;
    private static final int ALTERNATIVE_MAX_TOKENS = 2000;
    private static final int ALTERNATIVE_TIMEOUT_SECONDS = 45;

    @Test
    @DisplayName("Should create a valid LLM model config")
    void shouldCreateValidConfig() {
        // Given
        String name = "test-model";
        String provider = "test-provider";
        String baseUrl = "http://test-url";
        String apiKey = "test-key";
        Integer maxTokens = DEFAULT_MAX_TOKENS;
        Integer timeoutSeconds = DEFAULT_TIMEOUT_SECONDS;

        // When
        LlmModelConfig config = new LlmModelConfig(name, provider, baseUrl, apiKey, maxTokens, timeoutSeconds);

        // Then
        assertEquals(name, config.name());
        assertEquals(provider, config.provider());
        assertEquals(baseUrl, config.baseUrl());
        assertEquals(apiKey, config.apiKey());
        assertEquals(maxTokens, config.maxTokens());
        assertEquals(timeoutSeconds, config.timeoutSeconds());
    }

    @Test
    @DisplayName("Should apply defaults to null fields")
    void shouldApplyDefaultsToNullFields() {
        // Given
        LlmModelConfig config = new LlmModelConfig(null, null, null, "api-key", null, null);

        // When
        LlmModelConfig withDefaults = config.withDefaults();

        // Then
        assertEquals("default", withDefaults.name());
        assertEquals("ollama", withDefaults.provider());
        assertEquals("http://localhost:" + ApplicationConstants.DEFAULT_OLLAMA_PORT, withDefaults.baseUrl());
        assertEquals("api-key", withDefaults.apiKey());
        assertEquals(ApplicationConstants.DEFAULT_MAX_TOKENS, withDefaults.maxTokens());
        assertEquals(ApplicationConstants.DEFAULT_TIMEOUT_SECONDS, withDefaults.timeoutSeconds());
    }

    @Test
    @DisplayName("Should preserve non-null values when applying defaults")
    void shouldPreserveNonNullValuesWhenApplyingDefaults() {
        // Given
        String name = "custom-model";
        String provider = "custom-provider";
        String baseUrl = "http://custom-url";
        String apiKey = "custom-key";
        Integer maxTokens = ALTERNATIVE_MAX_TOKENS;
        Integer timeoutSeconds = ALTERNATIVE_TIMEOUT_SECONDS;

        LlmModelConfig config = new LlmModelConfig(name, provider, baseUrl, apiKey, maxTokens, timeoutSeconds);

        // When
        LlmModelConfig withDefaults = config.withDefaults();

        // Then
        assertEquals(name, withDefaults.name());
        assertEquals(provider, withDefaults.provider());
        assertEquals(baseUrl, withDefaults.baseUrl());
        assertEquals(apiKey, withDefaults.apiKey());
        assertEquals(maxTokens, withDefaults.maxTokens());
        assertEquals(timeoutSeconds, withDefaults.timeoutSeconds());
    }

    @Test
    @DisplayName("Should apply defaults using legacy method")
    void shouldApplyDefaultsUsingLegacyMethod() {
        // Given
        LlmModelConfig config = new LlmModelConfig(null, null, null, "api-key", null, null);

        // When
        LlmModelConfig withDefaults = config.applyDefaults();

        // Then
        assertEquals("default", withDefaults.name());
        assertEquals("ollama", withDefaults.provider());
        assertEquals("http://localhost:" + ApplicationConstants.DEFAULT_OLLAMA_PORT, withDefaults.baseUrl());
        assertEquals("api-key", withDefaults.apiKey());
        assertEquals(ApplicationConstants.DEFAULT_MAX_TOKENS, withDefaults.maxTokens());
        assertEquals(ApplicationConstants.DEFAULT_TIMEOUT_SECONDS, withDefaults.timeoutSeconds());
    }

    @Test
    @DisplayName("Should validate successfully with valid config")
    void shouldValidateSuccessfullyWithValidConfig() {
        // Given
        LlmModelConfig config = new LlmModelConfig("model", "provider", "url", "key",
                DEFAULT_MAX_TOKENS, DEFAULT_TIMEOUT_SECONDS);

        // When & Then
        assertDoesNotThrow(config::validate);
    }

    @ParameterizedTest
    @MethodSource("provideInvalidConfigs")
    @DisplayName("Should throw exception for invalid config")
    void shouldThrowExceptionForInvalidConfig(final LlmModelConfig config, final String expectedMessage) {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, config::validate);
        assertTrue(exception.getMessage().contains(expectedMessage),
            "Expected message to contain '" + expectedMessage + "' but was '" + exception.getMessage() + "'");
    }

    private static Stream<Arguments> provideInvalidConfigs() {
        return Stream.of(
            Arguments.of(new LlmModelConfig(null, "provider", "url", "key",
                    DEFAULT_MAX_TOKENS, DEFAULT_TIMEOUT_SECONDS),
                "name cannot be null or empty"),
            Arguments.of(new LlmModelConfig("", "provider", "url", "key",
                    DEFAULT_MAX_TOKENS, DEFAULT_TIMEOUT_SECONDS),
                "name cannot be null or empty"),
            Arguments.of(new LlmModelConfig("  ", "provider", "url", "key",
                    DEFAULT_MAX_TOKENS, DEFAULT_TIMEOUT_SECONDS),
                "name cannot be null or empty"),
            Arguments.of(new LlmModelConfig("model", null, "url", "key",
                    DEFAULT_MAX_TOKENS, DEFAULT_TIMEOUT_SECONDS),
                "provider cannot be null or empty"),
            Arguments.of(new LlmModelConfig("model", "", "url", "key",
                    DEFAULT_MAX_TOKENS, DEFAULT_TIMEOUT_SECONDS),
                "provider cannot be null or empty"),
            Arguments.of(new LlmModelConfig("model", "  ", "url", "key", DEFAULT_MAX_TOKENS, DEFAULT_TIMEOUT_SECONDS),
                "provider cannot be null or empty")
        );
    }
}

