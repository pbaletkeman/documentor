package com.documentor.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
// Removed unused imports for checkstyle compliance

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.documentor.config.DocumentorConfig;
import com.documentor.config.ThreadLocalContextHolder;
import com.documentor.config.model.LlmModelConfig;

/**
 * Branch coverage tests for LlmServiceFixEnhanced.
 * Focuses on testing uncovered branches to increase coverage above 75%.
 */
@ExtendWith(MockitoExtension.class)
class LlmServiceFixEnhancedBranchCoverageTest {

    @Mock
    private DocumentorConfig mockConfig;

    @Mock
    private LlmModelConfig mockModelConfig1;

    @Mock
    private LlmModelConfig mockModelConfig2;

    private LlmServiceFixEnhanced llmServiceFixEnhanced;

    @BeforeEach
    void setUp() {
        llmServiceFixEnhanced = new LlmServiceFixEnhanced();
        // Clear any existing ThreadLocal state before each test
        ThreadLocalContextHolder.clearConfig();
    }

    @AfterEach
    void tearDown() {
        // Clean up ThreadLocal state after each test to prevent contamination
        ThreadLocalContextHolder.clearConfig();
    }

    /**
     * Test setLlmServiceThreadLocalConfig with null configuration
     * - covers null check branch
     */
    @Test
    void testSetLlmServiceThreadLocalConfigNullConfig() {
        // Act
        llmServiceFixEnhanced.setLlmServiceThreadLocalConfig(null);

        // Assert - Should handle null gracefully and not set any config
        DocumentorConfig retrievedConfig = ThreadLocalContextHolder
            .getConfig();
        assertNull(retrievedConfig);
    }

    /**
     * Test setLlmServiceThreadLocalConfig with empty models list
     * - covers empty list branch
     */
    @Test
    void testSetLlmServiceThreadLocalConfigEmptyModelsList() {
        // Arrange
        when(mockConfig.llmModels()).thenReturn(Collections.emptyList());

        // Act
        llmServiceFixEnhanced.setLlmServiceThreadLocalConfig(mockConfig);

        // Assert - Should set config even with empty models list
        DocumentorConfig retrievedConfig = ThreadLocalContextHolder.getConfig();
        assertNotNull(retrievedConfig);
        assertEquals(mockConfig, retrievedConfig);
    }

    /**
     * Test setLlmServiceThreadLocalConfig with null models list
     * - covers null models branch
     */
    @Test
    void testSetLlmServiceThreadLocalConfigNullModelsList() {
        // Arrange
        when(mockConfig.llmModels()).thenReturn(null);

        // Act
        llmServiceFixEnhanced.setLlmServiceThreadLocalConfig(mockConfig);

        // Assert - Should set config even with null models list
        DocumentorConfig retrievedConfig = ThreadLocalContextHolder
            .getConfig();
        assertNotNull(retrievedConfig);
        assertEquals(mockConfig, retrievedConfig);
    }

    /**
     * Test setLlmServiceThreadLocalConfig with valid models list
     * - covers success branch
     */
    @Test
    void testSetLlmServiceThreadLocalConfigValidModelsList() {
        // Arrange
        when(mockModelConfig1.name()).thenReturn("model1");
        when(mockModelConfig1.provider()).thenReturn("openai");
        when(mockModelConfig1.baseUrl()).thenReturn("https://api.openai.com");

        when(mockModelConfig2.name()).thenReturn("model2");
        when(mockModelConfig2.provider()).thenReturn("ollama");
        when(mockModelConfig2.baseUrl()).thenReturn("http://localhost:11434");

        when(mockConfig.llmModels()).thenReturn(List.of(mockModelConfig1,
            mockModelConfig2));

        // Act
        llmServiceFixEnhanced.setLlmServiceThreadLocalConfig(mockConfig);

        // Assert - Should set config successfully
        DocumentorConfig retrievedConfig = ThreadLocalContextHolder
            .getConfig();
        assertNotNull(retrievedConfig);
        assertEquals(mockConfig, retrievedConfig);
        assertEquals(2, retrievedConfig.llmModels().size());
    }

    /**
     * Test setLlmServiceThreadLocalConfig with exception during verification
     */
    @Test
    void testSetLlmServiceThreadLocalConfigVerificationException() {
        // Arrange - Use a mock that will cause issues during verification
        DocumentorConfig problematicConfig = mock(DocumentorConfig.class);
        when(problematicConfig.llmModels()).thenThrow(
            new RuntimeException("Model access error"));

        // Act - Should handle the exception gracefully
        assertDoesNotThrow(() -> {
            llmServiceFixEnhanced
                .setLlmServiceThreadLocalConfig(problematicConfig);
        });

        // The config should still be set despite the verification issue
        // (this tests the exception handling in the verification section)
    }

    /**
     * Test isThreadLocalConfigAvailable when config is available
     * - covers true branch
     */
    @Test
    void testIsThreadLocalConfigAvailableConfigAvailable() {
        // Arrange - Set a config first
        when(mockConfig.llmModels()).thenReturn(List.of(mockModelConfig1));
        llmServiceFixEnhanced.setLlmServiceThreadLocalConfig(mockConfig);

        // Act
        boolean isAvailable = llmServiceFixEnhanced
            .isThreadLocalConfigAvailable();

        // Assert
        assertTrue(isAvailable);
    }

    /**
     * Test isThreadLocalConfigAvailable when config is not available
     * - covers false branch
     */
    @Test
    void testIsThreadLocalConfigAvailableConfigNotAvailable() {
        // Arrange - Ensure no config is set
        ThreadLocalContextHolder.clearConfig();

        // Act
        boolean isAvailable = llmServiceFixEnhanced
            .isThreadLocalConfigAvailable();

        // Assert
        assertFalse(isAvailable);
    }

    /**
     * Test isThreadLocalConfigAvailable with config that has null models
     */
    @Test
    void testIsThreadLocalConfigAvailableConfigWithNullModels() {
        // Arrange - Set config with null models
        when(mockConfig.llmModels()).thenReturn(null);
        llmServiceFixEnhanced.setLlmServiceThreadLocalConfig(mockConfig);

        // Act
        boolean isAvailable = llmServiceFixEnhanced
            .isThreadLocalConfigAvailable();

        // Assert - Should still be available even with null models
        assertTrue(isAvailable);
    }

    /**
     * Test isThreadLocalConfigAvailable with exception during check
     *  - covers exception branch
     */
    @Test
    void testIsThreadLocalConfigAvailableExceptionDuringCheck() {
        // This test is tricky since ThreadLocalContextHolder.getConfig()
        // is static We'll test by setting a config that will cause
        // issues when accessed
        DocumentorConfig problematicConfig = mock(DocumentorConfig.class);

        // Set the config first
        ThreadLocalContextHolder.setConfig(problematicConfig);

        // Now make the config throw exception when llmModels() is called
        when(problematicConfig.llmModels()).thenThrow(
            new RuntimeException("Access error"));

        // Act - Should handle exception gracefully
        boolean isAvailable = llmServiceFixEnhanced
            .isThreadLocalConfigAvailable();

        // Assert - Should return false due to exception handling
        assertFalse(isAvailable);
    }

    /**
     * Test cleanupThreadLocalConfig normal operation
     */
    @Test
    void testCleanupThreadLocalConfigNormal() {
        // Arrange - Set a config first
        when(mockConfig.llmModels()).thenReturn(List.of(mockModelConfig1));
        llmServiceFixEnhanced.setLlmServiceThreadLocalConfig(mockConfig);

        // Verify config is set
        assertNotNull(ThreadLocalContextHolder.getConfig());

        // Act
        llmServiceFixEnhanced.cleanupThreadLocalConfig();

        // Assert - Config should be cleared
        assertNull(ThreadLocalContextHolder.getConfig());
    }

    /**
     * Test executeWithConfig functionality
     */
    @Test
    void testExecuteWithConfig() {
        // Arrange
        when(mockConfig.llmModels()).thenReturn(List.of(mockModelConfig1));
        AtomicBoolean runnableExecuted = new AtomicBoolean(false);
        AtomicBoolean configWasAvailable = new AtomicBoolean(false);

        Runnable testRunnable = () -> {
            runnableExecuted.set(true);
            // Check if config is available during execution
            DocumentorConfig currentConfig = ThreadLocalContextHolder
                .getConfig();
            configWasAvailable.set(currentConfig != null);
        };

        // Act
        llmServiceFixEnhanced.executeWithConfig(mockConfig, testRunnable);

        // Assert
        assertTrue(runnableExecuted.get());
        assertTrue(configWasAvailable.get());
    }

    /**
     * Test multiple model configurations to cover loop branch in logging
     */
    @Test
    void testMultipleModelConfigurationsLogging() {
        final int expectedModelCount = 3;
        // Arrange - Create multiple model configs
        LlmModelConfig mockModelConfig3 = mock(LlmModelConfig.class);
        when(mockModelConfig1.name()).thenReturn("model1");
        when(mockModelConfig1.provider()).thenReturn("openai");
        when(mockModelConfig1.baseUrl()).thenReturn("https://api.openai.com");

        when(mockModelConfig2.name()).thenReturn("model2");
        when(mockModelConfig2.provider()).thenReturn("ollama");
        when(mockModelConfig2.baseUrl()).thenReturn("http://localhost:11434");

        when(mockModelConfig3.name()).thenReturn("model3");
        when(mockModelConfig3.provider()).thenReturn("llamacpp");
        when(mockModelConfig3.baseUrl()).thenReturn("http://localhost:8080");

        when(mockConfig.llmModels()).thenReturn(
            List.of(mockModelConfig1, mockModelConfig2, mockModelConfig3));

        // Act
        llmServiceFixEnhanced.setLlmServiceThreadLocalConfig(mockConfig);

        // Assert
        DocumentorConfig retrievedConfig = ThreadLocalContextHolder
            .getConfig();
        assertNotNull(retrievedConfig);
        assertEquals(expectedModelCount, retrievedConfig.llmModels().size());
    }

    /**
     * Test setting config twice to ensure proper overwrite
     */
    @Test
    void testSetConfigTwice() {
        // Arrange - First config
        DocumentorConfig firstConfig = mock(DocumentorConfig.class);
        when(firstConfig.llmModels()).thenReturn(List.of(mockModelConfig1));

        // Second config
        DocumentorConfig secondConfig = mock(DocumentorConfig.class);
        when(secondConfig.llmModels()).thenReturn(List.of(mockModelConfig2));

        // Act - Set first config
        llmServiceFixEnhanced.setLlmServiceThreadLocalConfig(firstConfig);
        DocumentorConfig firstRetrieved = ThreadLocalContextHolder.getConfig();

        // Set second config
        llmServiceFixEnhanced.setLlmServiceThreadLocalConfig(secondConfig);
        DocumentorConfig secondRetrieved = ThreadLocalContextHolder
            .getConfig();

        // Assert
        assertNotNull(firstRetrieved);
        assertEquals(firstConfig, firstRetrieved);

        assertNotNull(secondRetrieved);
        assertEquals(secondConfig, secondRetrieved);
        assertNotEquals(firstRetrieved, secondRetrieved);
    }

    /**
     * Test verification branch when models is null but config
     * is set successfully
     */
    @Test
    void testVerificationWithNullModels() {
        // Arrange
        when(mockConfig.llmModels()).thenReturn(null);

        // Act
        llmServiceFixEnhanced.setLlmServiceThreadLocalConfig(mockConfig);

        // Assert
        DocumentorConfig retrievedConfig = ThreadLocalContextHolder
            .getConfig();
        assertNotNull(retrievedConfig);
        assertEquals(mockConfig, retrievedConfig);

        // Verify that availability check works with null models
        boolean isAvailable = llmServiceFixEnhanced
            .isThreadLocalConfigAvailable();
        assertTrue(isAvailable);
    }
}
