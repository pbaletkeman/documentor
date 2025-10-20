package com.documentor.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import com.documentor.config.model.LlmModelConfig;
import com.documentor.config.model.OutputSettings;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for ThreadLocalContextHolder ensuring proper thread-local value management
 */
@ExtendWith(MockitoExtension.class)
public class ThreadLocalContextHolderTest {

    @Mock
    private DocumentorConfig mockConfig;

    @Mock
    private List<LlmModelConfig> mockLlmModels;

    @Mock
    private OutputSettings mockOutputSettings;

    @BeforeEach
    public void setUp() {
        // Clear ThreadLocal before each test
        ThreadLocalContextHolder.clearConfig();

        // Set up common mocks
        when(mockConfig.llmModels()).thenReturn(mockLlmModels);
        when(mockLlmModels.size()).thenReturn(2);
        when(mockConfig.outputSettings()).thenReturn(mockOutputSettings);
    }

    @Test
    public void testSetAndGetConfig() {
        // Set config
        ThreadLocalContextHolder.setConfig(mockConfig);

        // Verify it was set correctly
        DocumentorConfig retrievedConfig = ThreadLocalContextHolder.getConfig();
        assertSame(mockConfig, retrievedConfig);

        // Verify explicitly set status
        assertTrue(ThreadLocalContextHolder.isConfigExplicitlySet());
    }

    @Test
    public void testClearConfig() {
        // Set config
        ThreadLocalContextHolder.setConfig(mockConfig);

        // Clear it
        ThreadLocalContextHolder.clearConfig();

        // Verify it was cleared
        assertNull(ThreadLocalContextHolder.getConfig());
        assertFalse(ThreadLocalContextHolder.isConfigExplicitlySet());
    }

    @Test
    public void testSetNullConfig() {
        // Attempt to set null config
        ThreadLocalContextHolder.setConfig(null);

        // Verify no config was set
        assertNull(ThreadLocalContextHolder.getConfig());
        assertFalse(ThreadLocalContextHolder.isConfigExplicitlySet());
    }

    @Test
    public void testRunWithConfig() {
        // Create a second mock config for testing
        DocumentorConfig mockConfig2 = mock(DocumentorConfig.class);
        List<LlmModelConfig> mockLlmModels2 = mock(List.class);
        when(mockConfig2.llmModels()).thenReturn(mockLlmModels2);

        // Set initial config
        ThreadLocalContextHolder.setConfig(mockConfig);

        // Reference variable to capture config inside runnable
        final AtomicBoolean runnableExecuted = new AtomicBoolean(false);
        final AtomicInteger configModelSize = new AtomicInteger(0);

        // Run with different config
        ThreadLocalContextHolder.runWithConfig(mockConfig2, () -> {
            // Inside runnable, should have mockConfig2
            DocumentorConfig configInRunnable = ThreadLocalContextHolder.getConfig();
            assertEquals(mockConfig2, configInRunnable);

            // Capture model size
            configModelSize.set(configInRunnable.llmModels().size());

            runnableExecuted.set(true);
        });

        // Verify runnable was executed
        assertTrue(runnableExecuted.get());

        // Verify original config is restored after runnable completes
        assertSame(mockConfig, ThreadLocalContextHolder.getConfig());
        assertTrue(ThreadLocalContextHolder.isConfigExplicitlySet());
    }

    @Test
    public void testRunWithConfigWhenExceptionThrown() {
        // Set initial config
        ThreadLocalContextHolder.setConfig(mockConfig);

        // Verify exception is propagated but config is restored
        Exception exception = assertThrows(RuntimeException.class, () -> {
            ThreadLocalContextHolder.runWithConfig(mock(DocumentorConfig.class), () -> {
                throw new RuntimeException("Test exception");
            });
        });

        assertEquals("Test exception", exception.getMessage());

        // Verify original config is restored even after exception
        assertSame(mockConfig, ThreadLocalContextHolder.getConfig());
        assertTrue(ThreadLocalContextHolder.isConfigExplicitlySet());
    }

    @Test
    public void testConfigIsolationBetweenThreads() throws Exception {
        // Test that ThreadLocal values are properly isolated between threads
        DocumentorConfig mainThreadConfig = mock(DocumentorConfig.class);
        DocumentorConfig workerThreadConfig = mock(DocumentorConfig.class);

        // Set config in main thread
        ThreadLocalContextHolder.setConfig(mainThreadConfig);

        // Create a latch to synchronize threads
        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean workerHasCorrectConfig = new AtomicBoolean(false);

        // Create and start worker thread with its own config
        Thread workerThread = new Thread(() -> {
            // Worker thread should start with null config
            DocumentorConfig initialConfig = ThreadLocalContextHolder.getConfig();
            if (initialConfig == null) {
                // Set worker's own config
                ThreadLocalContextHolder.setConfig(workerThreadConfig);

                // Verify worker has its own config
                DocumentorConfig currentConfig = ThreadLocalContextHolder.getConfig();
                workerHasCorrectConfig.set(currentConfig == workerThreadConfig);
            }

            // Signal completion
            latch.countDown();
        });

        workerThread.start();
        latch.await(5, TimeUnit.SECONDS); // Wait for worker to complete

        // Verify worker had correct config
        assertTrue(workerHasCorrectConfig.get());

        // Verify main thread's config is unchanged
        assertSame(mainThreadConfig, ThreadLocalContextHolder.getConfig());
    }

    @Test
    public void testThreadLocalPropagationWithExecutorService() throws Exception {
        // Test ThreadLocal propagation using ThreadLocalPropagatingExecutorEnhanced
        DocumentorConfig mainThreadConfig = mock(DocumentorConfig.class);
        when(mainThreadConfig.llmModels()).thenReturn(mockLlmModels);

        // Set config in main thread
        ThreadLocalContextHolder.setConfig(mainThreadConfig);

        // Create executor and tasks
        Executor executor = ThreadLocalPropagatingExecutorEnhanced.createExecutor(2, "test-executor");
        CountDownLatch latch = new CountDownLatch(5);
        AtomicInteger correctConfigCount = new AtomicInteger(0);

        // Execute multiple tasks
        for (int i = 0; i < 5; i++) {
            CompletableFuture.runAsync(() -> {
                try {
                    // Check if config propagated correctly
                    DocumentorConfig workerConfig = ThreadLocalContextHolder.getConfig();
                    if (workerConfig == mainThreadConfig) {
                        correctConfigCount.incrementAndGet();
                    }
                } finally {
                    latch.countDown();
                }
            }, executor);
        }

        // Wait for all tasks to complete
        latch.await(10, TimeUnit.SECONDS);

        // All tasks should have received the correct config
        assertEquals(5, correctConfigCount.get());
    }

    @Test
    public void testRunWithNullConfigOrRunnable() {
        // Should not throw an exception
        ThreadLocalContextHolder.runWithConfig(null, () -> {});
        ThreadLocalContextHolder.runWithConfig(mockConfig, null);
        ThreadLocalContextHolder.runWithConfig(null, null);
    }

    @Test
    public void testLogConfigStatus() {
        // Just verify it doesn't throw an exception
        ThreadLocalContextHolder.clearConfig();
        ThreadLocalContextHolder.logConfigStatus();

        ThreadLocalContextHolder.setConfig(mockConfig);
        ThreadLocalContextHolder.logConfigStatus();
    }
}
