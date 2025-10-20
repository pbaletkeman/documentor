package com.documentor.service;

import com.documentor.config.DocumentorConfig;
import com.documentor.config.ThreadLocalContextHolder;
import com.documentor.config.model.LlmModelConfig;
import com.documentor.config.model.OutputSettings;
import com.documentor.config.model.AnalysisSettings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for LlmServiceFixEnhanced to improve branch coverage.
 */
@ExtendWith(MockitoExtension.class)
class LlmServiceFixEnhancedTest {

    private LlmServiceFixEnhanced llmServiceFix;
    private DocumentorConfig testConfig;

    @BeforeEach
    void setUp() {
        llmServiceFix = new LlmServiceFixEnhanced();

        testConfig = new DocumentorConfig(
            List.of(new LlmModelConfig("test-model", "ollama", "http://localhost:11434", "test-key", 1000, 30)),
            new OutputSettings("./test-output", "markdown", true, true, false),
            new AnalysisSettings(null, null, null, null)
        );
    }

    @Test
    void testSetLlmServiceThreadLocalConfigWithValidConfig() {
        try (MockedStatic<ThreadLocalContextHolder> mockedStatic = mockStatic(ThreadLocalContextHolder.class)) {
            // Act
            llmServiceFix.setLlmServiceThreadLocalConfig(testConfig);

            // Assert - verify ThreadLocal operations were called
            mockedStatic.verify(() -> ThreadLocalContextHolder.setConfig(testConfig));
            mockedStatic.verify(() -> ThreadLocalContextHolder.getConfig());
        }
    }

    @Test
    void testSetLlmServiceThreadLocalConfigWithNullConfig() {
        // Act - should return early for null config
        assertDoesNotThrow(() -> llmServiceFix.setLlmServiceThreadLocalConfig(null));
    }

    @Test
    void testIsThreadLocalConfigAvailableWhenConfigExists() {
        try (MockedStatic<ThreadLocalContextHolder> mockedStatic = mockStatic(ThreadLocalContextHolder.class)) {
            // Arrange
            mockedStatic.when(ThreadLocalContextHolder::getConfig).thenReturn(testConfig);

            // Act
            boolean result = llmServiceFix.isThreadLocalConfigAvailable();

            // Assert
            assertTrue(result);
            mockedStatic.verify(ThreadLocalContextHolder::getConfig);
        }
    }

    @Test
    void testIsThreadLocalConfigAvailableWhenConfigNull() {
        try (MockedStatic<ThreadLocalContextHolder> mockedStatic = mockStatic(ThreadLocalContextHolder.class)) {
            // Arrange
            mockedStatic.when(ThreadLocalContextHolder::getConfig).thenReturn(null);

            // Act
            boolean result = llmServiceFix.isThreadLocalConfigAvailable();

            // Assert
            assertFalse(result);
        }
    }

    @Test
    void testIsThreadLocalConfigAvailableWithException() {
        try (MockedStatic<ThreadLocalContextHolder> mockedStatic = mockStatic(ThreadLocalContextHolder.class)) {
            // Arrange
            mockedStatic.when(ThreadLocalContextHolder::getConfig)
                    .thenThrow(new RuntimeException("Test exception"));

            // Act
            boolean result = llmServiceFix.isThreadLocalConfigAvailable();

            // Assert
            assertFalse(result);
        }
    }

    @Test
    void testCleanupThreadLocalConfig() {
        try (MockedStatic<ThreadLocalContextHolder> mockedStatic = mockStatic(ThreadLocalContextHolder.class)) {
            // Act
            llmServiceFix.cleanupThreadLocalConfig();

            // Assert
            mockedStatic.verify(() -> ThreadLocalContextHolder.clearConfig());
        }
    }

    @Test
    void testExecuteWithConfig() {
        try (MockedStatic<ThreadLocalContextHolder> mockedStatic = mockStatic(ThreadLocalContextHolder.class)) {
            // Arrange
            Runnable testRunnable = mock(Runnable.class);

            // Act
            llmServiceFix.executeWithConfig(testConfig, testRunnable);

            // Assert
            mockedStatic.verify(() -> ThreadLocalContextHolder.runWithConfig(testConfig, testRunnable));
        }
    }

    @Test
    void testSetConfigWithEmptyModels() {
        // Arrange
        DocumentorConfig emptyModelsConfig = new DocumentorConfig(
            List.of(), // Empty models list
            new OutputSettings("./test-output", "markdown", true, true, false),
            new AnalysisSettings(null, null, null, null)
        );

        try (MockedStatic<ThreadLocalContextHolder> mockedStatic = mockStatic(ThreadLocalContextHolder.class)) {
            // Act
            llmServiceFix.setLlmServiceThreadLocalConfig(emptyModelsConfig);

            // Assert
            mockedStatic.verify(() -> ThreadLocalContextHolder.setConfig(emptyModelsConfig));
        }
    }

    @Test
    void testSetConfigWithMultipleModels() {
        // Arrange
        DocumentorConfig multiModelConfig = new DocumentorConfig(
            List.of(
                new LlmModelConfig("model1", "ollama", "http://localhost:11434", "key1", 1000, 30),
                new LlmModelConfig("model2", "openai", "https://api.openai.com", "key2", 2000, 60)
            ),
            new OutputSettings("./test-output", "markdown", true, true, false),
            new AnalysisSettings(null, null, null, null)
        );

        try (MockedStatic<ThreadLocalContextHolder> mockedStatic = mockStatic(ThreadLocalContextHolder.class)) {
            // Act
            llmServiceFix.setLlmServiceThreadLocalConfig(multiModelConfig);

            // Assert
            mockedStatic.verify(() -> ThreadLocalContextHolder.setConfig(multiModelConfig));
        }
    }

    @Test
    void testExceptionHandlingInSetConfig() {
        try (MockedStatic<ThreadLocalContextHolder> mockedStatic = mockStatic(ThreadLocalContextHolder.class)) {
            // Arrange
            mockedStatic.when(() -> ThreadLocalContextHolder.setConfig(any()))
                    .thenThrow(new RuntimeException("Set config error"));

            // Act & Assert - Should not throw exception, handles gracefully
            assertDoesNotThrow(() -> llmServiceFix.setLlmServiceThreadLocalConfig(testConfig));
        }
    }

    @Test
    void testExceptionHandlingInCleanup() {
        try (MockedStatic<ThreadLocalContextHolder> mockedStatic = mockStatic(ThreadLocalContextHolder.class)) {
            // Arrange
            mockedStatic.when(ThreadLocalContextHolder::clearConfig)
                    .thenThrow(new RuntimeException("Cleanup error"));

            // Act & Assert - Should not throw exception, handles gracefully
            assertDoesNotThrow(() -> llmServiceFix.cleanupThreadLocalConfig());
        }
    }
}
