package com.documentor.config;

import com.documentor.config.model.LlmModelConfig;
import com.documentor.config.model.OutputSettings;
import com.documentor.config.model.AnalysisSettings;
import com.documentor.service.LlmService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.ArgumentMatchers.any;

/**
 * Comprehensive tests for ThreadLocalTaskDecorator to improve branch coverage.
 */
@ExtendWith(MockitoExtension.class)
class ThreadLocalTaskDecoratorTest {
    private static final int TEST_MAX_TOKENS = 1000;
    private static final int TEST_TIMEOUT = 30;

    private ThreadLocalTaskDecorator decorator;
    private DocumentorConfig testConfig;
    private MockedStatic<LlmService> mockedLlmService;

    @BeforeEach
    void setUp() {
        decorator = new ThreadLocalTaskDecorator();

        // Create test configuration
        testConfig = new DocumentorConfig(
            List.of(new LlmModelConfig("test-model", "ollama",
                "test-endpoint", "test-key", TEST_MAX_TOKENS, TEST_TIMEOUT)),
            new OutputSettings("test/output", "markdown", false, false, false,
                null, null, null, null),
            new AnalysisSettings(null, null, null, null)
        );

        // Mock the static methods
        mockedLlmService = mockStatic(LlmService.class);
    }

    @AfterEach
    void tearDown() {
        if (mockedLlmService != null) {
            mockedLlmService.close();
        }
    }

    @Test
    void testDecorateWithAvailableThreadLocalConfig() {
        // Setup: Mock that config is available in parent thread
        mockedLlmService.when(LlmService::getThreadLocalConfig)
            .thenReturn(testConfig);

        // Create a test runnable
        Runnable originalRunnable = mock(Runnable.class);

        // Decorate the runnable
        Runnable decoratedRunnable = decorator.decorate(originalRunnable);

        assertNotNull(decoratedRunnable);
        assertNotSame(originalRunnable, decoratedRunnable);

        // Execute the decorated runnable
        decoratedRunnable.run();

        // Verify interactions
        // Captured config from parent thread
        mockedLlmService.verify(LlmService::getThreadLocalConfig);
        // Set config in child thread
        mockedLlmService.verify(() -> LlmService
        .setThreadLocalConfig(testConfig));
        // Original runnable was executed
        verify(originalRunnable).run();
        // Cleanup was called
        mockedLlmService.verify(LlmService::clearThreadLocalConfig);
    }

    @Test
    void testDecorateWithNullThreadLocalConfig() {
        // Setup: Mock that no config is available in parent thread
        mockedLlmService.when(LlmService::getThreadLocalConfig)
            .thenReturn(null);

        // Create a test runnable
        Runnable originalRunnable = mock(Runnable.class);

        // Decorate the runnable
        Runnable decoratedRunnable = decorator.decorate(originalRunnable);

        assertNotNull(decoratedRunnable);
        assertNotSame(originalRunnable, decoratedRunnable);

        // Execute the decorated runnable
        decoratedRunnable.run();

        // Verify interactions
        // Attempted to get config from parent thread
        mockedLlmService.verify(LlmService::getThreadLocalConfig);
        // Should not set null config
        mockedLlmService.verify(() ->
            LlmService.setThreadLocalConfig(any()), never());
        // Original runnable was still executed
        verify(originalRunnable).run();
        // Cleanup was still called
        mockedLlmService.verify(LlmService::clearThreadLocalConfig);
    }

    @Test
    void testDecorateWithExceptionInOriginalRunnable() {
        // Setup: Mock that config is available in parent thread
        mockedLlmService.when(LlmService::getThreadLocalConfig)
            .thenReturn(testConfig);

        // Create a test runnable that throws an exception
        Runnable originalRunnable = mock(Runnable.class);
        RuntimeException testException =
            new RuntimeException("Test exception");
        doThrow(testException).when(originalRunnable).run();

        // Decorate the runnable
        Runnable decoratedRunnable = decorator.decorate(originalRunnable);

        // Execute the decorated runnable
        // - should throw the exception but still cleanup
        RuntimeException thrown = assertThrows(
            RuntimeException.class, decoratedRunnable::run);
        assertEquals("Test exception", thrown.getMessage());

        // Verify interactions - cleanup should still
        // happen even with exception
        // Captured config from parent thread
        mockedLlmService.verify(LlmService::getThreadLocalConfig);
        // Set config in child thread
        mockedLlmService.verify(() ->
            LlmService.setThreadLocalConfig(testConfig));
        // Original runnable was executed (and threw exception)
        verify(originalRunnable).run();
        // Cleanup was still called despite exception
        mockedLlmService.verify(LlmService::clearThreadLocalConfig);
    }

    @Test
    void testDecorateWithExceptionInConfigSetup() {
        // Setup: Mock that getting config throws an exception
        RuntimeException configException =
            new RuntimeException("Config retrieval failed");
        mockedLlmService.when(LlmService::getThreadLocalConfig)
            .thenThrow(configException);

        // Create a test runnable
        Runnable originalRunnable = mock(Runnable.class);

        // Decorate the runnable - should throw exception during
        // decoration since config retrieval fails
        RuntimeException thrown = assertThrows(
            RuntimeException.class, () -> decorator
            .decorate(originalRunnable));
        assertEquals("Config retrieval failed", thrown.getMessage());

        // Verify that getThreadLocalConfig was called and exception
        // was thrown during decoration
        mockedLlmService.verify(LlmService::getThreadLocalConfig);
        // Original runnable should NOT be executed since decoration failed
        verify(originalRunnable, never()).run();
    }

    @Test
    void testDecorateMultipleTimes() {
        // Setup: Mock that config is available in parent thread
        mockedLlmService.when(LlmService::getThreadLocalConfig)
            .thenReturn(testConfig);

        // Create multiple test runnables
        Runnable runnable1 = mock(Runnable.class);
        Runnable runnable2 = mock(Runnable.class);

        // Decorate multiple runnables
        Runnable decorated1 = decorator.decorate(runnable1);
        Runnable decorated2 = decorator.decorate(runnable2);

        assertNotNull(decorated1);
        assertNotNull(decorated2);
        assertNotSame(decorated1, decorated2);

        // Execute both decorated runnables
        decorated1.run();
        decorated2.run();

        // Verify interactions for both
        verify(runnable1).run();
        verify(runnable2).run();
        mockedLlmService.verify(LlmService::getThreadLocalConfig,
            times(2)); // Called twice for decoration
        mockedLlmService.verify(() -> LlmService.
            setThreadLocalConfig(testConfig),
            times(2)); // Set twice for execution
        mockedLlmService.verify(LlmService::clearThreadLocalConfig,
            times(2)); // Cleanup called twice
    }

    @Test
    void testDecorateWithConfigHavingMultipleModels() {
        // Setup: Create config with multiple models
        DocumentorConfig multiModelConfig = new DocumentorConfig(
            List.of(
                new LlmModelConfig(
                    "model1", "ollama", "endpoint1",
                    "key1", TEST_MAX_TOKENS, TEST_TIMEOUT),
                new LlmModelConfig(
                    "model2", "ollama", "endpoint2",
                    "key2", TEST_MAX_TOKENS, TEST_TIMEOUT),
                new LlmModelConfig(
                    "model3", "ollama", "endpoint3",
                    "key3", TEST_MAX_TOKENS, TEST_TIMEOUT)
            ),
            new OutputSettings("test/output", "markdown", false, false, false,
                null, null, null, null),
            new AnalysisSettings(null, null, null, null)
        );

        mockedLlmService.when(LlmService::getThreadLocalConfig)
            .thenReturn(multiModelConfig);

        // Create a test runnable
        Runnable originalRunnable = mock(Runnable.class);

        // Decorate the runnable
        Runnable decoratedRunnable = decorator.decorate(originalRunnable);

        // Execute the decorated runnable
        decoratedRunnable.run();

        // Verify interactions
        mockedLlmService.verify(LlmService::getThreadLocalConfig);
        mockedLlmService.verify(() ->
            LlmService.setThreadLocalConfig(multiModelConfig));
        verify(originalRunnable).run();
        mockedLlmService.verify(LlmService::clearThreadLocalConfig);
    }

    @Test
    void testDecorateWithEmptyModelsConfig() {
        // Setup: Create config with empty models list
        DocumentorConfig emptyModelsConfig = new DocumentorConfig(
            List.of(), // Empty models list
            new OutputSettings("test/output", "markdown", false, false, false,
                null, null, null, null),
            new AnalysisSettings(null, null, null, null)
        );

        mockedLlmService.when(LlmService::getThreadLocalConfig)
            .thenReturn(emptyModelsConfig);

        // Create a test runnable
        Runnable originalRunnable = mock(Runnable.class);

        // Decorate the runnable
        Runnable decoratedRunnable = decorator.decorate(originalRunnable);

        // Execute the decorated runnable
        decoratedRunnable.run();

        // Verify interactions - should still work with empty models
        mockedLlmService.verify(LlmService::getThreadLocalConfig);
        mockedLlmService.verify(() ->
            LlmService.setThreadLocalConfig(emptyModelsConfig));
        verify(originalRunnable).run();
        mockedLlmService.verify(LlmService::clearThreadLocalConfig);
    }

    @Test
    void testDecoratePreservesRunnableIdentity() {
        // Setup
        mockedLlmService.when(LlmService::getThreadLocalConfig)
            .thenReturn(testConfig);

        // Create a specific runnable implementation
        final boolean[] executed = {false};
        Runnable originalRunnable = () -> executed[0] = true;

        // Decorate the runnable
        Runnable decoratedRunnable = decorator.decorate(originalRunnable);

        // Execute and verify the original logic was preserved
        decoratedRunnable.run();

        assertTrue(executed[0], "Original runnable logic should be executed");

        // Verify ThreadLocal operations occurred
        mockedLlmService.verify(LlmService::getThreadLocalConfig);
        mockedLlmService.verify(() ->
            LlmService.setThreadLocalConfig(testConfig));
        mockedLlmService.verify(LlmService::clearThreadLocalConfig);
    }
}
