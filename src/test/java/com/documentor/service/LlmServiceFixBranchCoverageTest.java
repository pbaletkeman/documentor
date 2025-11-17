package com.documentor.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.mock;
import static org.mockito.ArgumentMatchers.any;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.documentor.config.DocumentorConfig;
import com.documentor.config.model.LlmModelConfig;

/**
 * Branch coverage tests for LlmServiceFix.
 */
@ExtendWith(MockitoExtension.class)
class LlmServiceFixBranchCoverageTest {

    @Mock
    private DocumentorConfig mockConfig;

    @Mock
    private LlmModelConfig mockModelConfig;

    private LlmServiceFix llmServiceFix;

    @BeforeEach
    void setUp() {
        llmServiceFix = new LlmServiceFix();
        LlmService.clearThreadLocalConfig();
    }

    @AfterEach
    void tearDown() {
        LlmService.clearThreadLocalConfig();
    }

    /**
     * Test with null configuration
     */
    @Test
    void testNullConfig() {
        // Act
        llmServiceFix.setLlmServiceThreadLocalConfig(null);

        // Assert
        DocumentorConfig retrievedConfig = LlmService.getThreadLocalConfig();
        assertNull(retrievedConfig);
    }

    /**
     * Test with empty models list
     */
    @Test
    void testEmptyModelsList() {
        // Arrange
        when(mockConfig.llmModels()).thenReturn(Collections.emptyList());

        // Act
        llmServiceFix.setLlmServiceThreadLocalConfig(mockConfig);

        // Assert
        DocumentorConfig retrievedConfig = LlmService.getThreadLocalConfig();
        assertNotNull(retrievedConfig);
        assertEquals(mockConfig, retrievedConfig);
    }

    /**
     * Test with valid models list
     */
    @Test
    void testValidModelsList() {
        // Arrange
        when(mockModelConfig.name()).thenReturn("test-model");
        when(mockModelConfig.provider()).thenReturn("test-provider");
        when(mockModelConfig.baseUrl()).thenReturn("test-url");
        when(mockConfig.llmModels()).thenReturn(List.of(mockModelConfig));

        // Act
        llmServiceFix.setLlmServiceThreadLocalConfig(mockConfig);

        // Assert
        DocumentorConfig retrievedConfig = LlmService.getThreadLocalConfig();
        assertNotNull(retrievedConfig);
        assertEquals(mockConfig, retrievedConfig);
    }

    /**
     * Test config availability when config is available
     */
    @Test
    void testConfigAvailable() {
        // Arrange
        when(mockConfig.llmModels()).thenReturn(List.of(mockModelConfig));
        when(mockModelConfig.name()).thenReturn("test-model");
        when(mockModelConfig.provider()).thenReturn("test-provider");
        when(mockModelConfig.baseUrl()).thenReturn("test-url");
        llmServiceFix.setLlmServiceThreadLocalConfig(mockConfig);

        // Act
        boolean isAvailable = llmServiceFix.isThreadLocalConfigAvailable();

        // Assert
        assertTrue(isAvailable);
    }

    /**
     * Test config availability when config is not available
     */
    @Test
    void testConfigNotAvailable() {
        // Arrange
        LlmService.clearThreadLocalConfig();

        // Act
        boolean isAvailable = llmServiceFix.isThreadLocalConfigAvailable();

        // Assert
        assertFalse(isAvailable);
    }

    /**
     * Test config availability with empty models
     */
    @Test
    void testConfigAvailableEmptyModels() {
        // Arrange
        when(mockConfig.llmModels()).thenReturn(Collections.emptyList());
        llmServiceFix.setLlmServiceThreadLocalConfig(mockConfig);

        // Act
        boolean isAvailable = llmServiceFix.isThreadLocalConfigAvailable();

        // Assert
        assertTrue(isAvailable);
    }

    /**
     * Test multiple models logging
     */
    @Test
    void testMultipleModels() {
        // Arrange
        LlmModelConfig model1 = mock(LlmModelConfig.class);
        LlmModelConfig model2 = mock(LlmModelConfig.class);

        when(model1.name()).thenReturn("model1");
        when(model1.provider()).thenReturn("provider1");
        when(model1.baseUrl()).thenReturn("url1");

        when(model2.name()).thenReturn("model2");
        when(model2.provider()).thenReturn("provider2");
        when(model2.baseUrl()).thenReturn("url2");

        when(mockConfig.llmModels()).thenReturn(List.of(model1, model2));

        // Act
        llmServiceFix.setLlmServiceThreadLocalConfig(mockConfig);

        // Assert
        DocumentorConfig retrievedConfig = LlmService.getThreadLocalConfig();
        assertNotNull(retrievedConfig);
        assertEquals(2, retrievedConfig.llmModels().size());
    }

    /**
     * Test setting config twice
     */
    @Test
    void testSetConfigTwice() {
        // Arrange
        DocumentorConfig firstConfig = mock(DocumentorConfig.class);
        DocumentorConfig secondConfig = mock(DocumentorConfig.class);

        when(firstConfig.llmModels()).thenReturn(List.of(mockModelConfig));
        when(secondConfig.llmModels()).thenReturn(List.of(mockModelConfig));
        when(mockModelConfig.name()).thenReturn("test");
        when(mockModelConfig.provider()).thenReturn("test");
        when(mockModelConfig.baseUrl()).thenReturn("test");

        // Act
        llmServiceFix.setLlmServiceThreadLocalConfig(firstConfig);
        DocumentorConfig firstRetrieved = LlmService.getThreadLocalConfig();

        llmServiceFix.setLlmServiceThreadLocalConfig(secondConfig);
        DocumentorConfig secondRetrieved = LlmService.getThreadLocalConfig();

        // Assert
        assertEquals(firstConfig, firstRetrieved);
        assertEquals(secondConfig, secondRetrieved);
        assertNotEquals(firstRetrieved, secondRetrieved);
    }
}
