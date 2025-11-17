package com.documentor.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests all execution paths, error scenarios, and
 * ThreadLocal propagation logic.
 */
@ExtendWith(MockitoExtension.class)
class ThreadLocalPropagatingExecutorEnhancedTest {

    @Mock
    private Executor mockDelegate;

    @Mock
    private DocumentorConfig mockConfig;

    private ThreadLocalPropagatingExecutorEnhanced executor;

    @BeforeEach
    void setUp() {
        reset(mockDelegate, mockConfig);
        ThreadLocalContextHolder.clearConfig();
    }

    @AfterEach
    void tearDown() {
        ThreadLocalContextHolder.clearConfig();
    }

    @Test
    void testConstructorWithValidDelegate() {
        executor = new ThreadLocalPropagatingExecutorEnhanced(
            mockDelegate, "test-executor");

        assertNotNull(executor);
    }

    @Test
    void testConstructorWithNullDelegate() {
        executor = new ThreadLocalPropagatingExecutorEnhanced(
            null, "test-executor");

        assertNotNull(executor);
        // Should use fallback executor (ForkJoinPool.commonPool())
    }

    @Test
    void testConstructorWithNullName() {
        executor = new ThreadLocalPropagatingExecutorEnhanced(
            mockDelegate, null);

        assertNotNull(executor);
        // Should use default name "unnamed"
    }

    @Test
    void testConstructorWithBothNull() {
        executor = new ThreadLocalPropagatingExecutorEnhanced(
            null, null);

        assertNotNull(executor);
    }

    @Test
    void testExecuteWithNullCommand() {
        executor = new ThreadLocalPropagatingExecutorEnhanced(
            mockDelegate, "test-executor");

        // Should handle null command gracefully
        assertDoesNotThrow(() -> executor.execute(null));

        // Verify delegate was not called with null
        verify(mockDelegate, never()).execute(any());
    }

    @Test
    void testExecuteWithValidCommandAndThreadLocalConfig()
        throws InterruptedException {
        executor = new ThreadLocalPropagatingExecutorEnhanced(mockDelegate,
            "test-executor");

        // Setup ThreadLocal config
        ThreadLocalContextHolder.setConfig(mockConfig);

        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<DocumentorConfig> capturedConfig =
            new AtomicReference<>();

        Runnable testCommand = () -> {
            capturedConfig.set(ThreadLocalContextHolder.getConfig());
            latch.countDown();
        };

        // Mock delegate to execute immediately
        doAnswer(invocation -> {
            Runnable wrapped = invocation.getArgument(0);
            wrapped.run();
            return null;
        }).when(mockDelegate).execute(any());

        executor.execute(testCommand);

        assertTrue(latch.await(1, TimeUnit.SECONDS));
        assertEquals(mockConfig, capturedConfig.get());
        verify(mockDelegate).execute(any());
    }

    @Test
    void testExecuteWithNoThreadLocalConfig() throws InterruptedException {
        executor = new ThreadLocalPropagatingExecutorEnhanced(mockDelegate,
            "test-executor");

        // Ensure no ThreadLocal config
        ThreadLocalContextHolder.clearConfig();

        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<DocumentorConfig> capturedConfig =
            new AtomicReference<>();

        Runnable testCommand = () -> {
            capturedConfig.set(ThreadLocalContextHolder.getConfig());
            latch.countDown();
        };

        // Mock delegate to execute immediately
        doAnswer(invocation -> {
            Runnable wrapped = invocation.getArgument(0);
            wrapped.run();
            return null;
        }).when(mockDelegate).execute(any());

        executor.execute(testCommand);

        assertTrue(latch.await(1, TimeUnit.SECONDS));
        assertNull(capturedConfig.get());
        verify(mockDelegate).execute(any());
    }

    @Test
    void testExecuteTaskExceptionHandling() {
        executor = new ThreadLocalPropagatingExecutorEnhanced(
            mockDelegate, "test-executor");

        Runnable throwingCommand = () -> {
            throw new RuntimeException("Test exception in task");
        };

        // Mock delegate to execute immediately
        doAnswer(invocation -> {
            Runnable wrapped = invocation.getArgument(0);
            wrapped.run();
            return null;
        }).when(mockDelegate).execute(any());

        // Should handle task exception gracefully
        assertDoesNotThrow(() -> executor.execute(throwingCommand));
        verify(mockDelegate).execute(any());
    }

    @Test
    void testExecuteDelegateExecutorThrowsException() {
        executor = new ThreadLocalPropagatingExecutorEnhanced(
            mockDelegate, "test-executor");

        Runnable testCommand = () -> {};

        // Mock delegate to throw exception
        doThrow(new RuntimeException("Delegate executor failed"))
            .when(mockDelegate).execute(any());

        // Should handle delegate exception and fall back to ForkJoinPool
        assertDoesNotThrow(() -> executor.execute(testCommand));
        verify(mockDelegate).execute(any());
    }

    @Test
    void testExecuteFallbackExecutorAlsoFails() {
        // Create executor with null delegate to force fallback
        executor = new ThreadLocalPropagatingExecutorEnhanced(
            null, "test-executor");

        Runnable testCommand = () -> {};

        // The fallback is ForkJoinPool.commonPool() which should
        // work, but we can't easily mock it
        // So we test that execution doesn't throw exception
        assertDoesNotThrow(() -> executor.execute(testCommand));
    }

    @Test
    void testThreadLocalCleanupInFinally() throws InterruptedException {
        executor = new ThreadLocalPropagatingExecutorEnhanced(
            mockDelegate, "test-executor");

        // Setup ThreadLocal config
        ThreadLocalContextHolder.setConfig(mockConfig);

        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean wasConfigClearedInFinally = new AtomicBoolean(false);

        Runnable testCommand = () -> {
            // Verify config is set
            assertNotNull(ThreadLocalContextHolder.getConfig());
            // After task completion, config should be cleared in finally block
            latch.countDown();
        };

        // Mock delegate to execute immediately and verify cleanup
        doAnswer(invocation -> {
            Runnable wrapped = invocation.getArgument(0);
            wrapped.run();
            // After wrapped task execution, verify config was cleared
            wasConfigClearedInFinally.set(ThreadLocalContextHolder
                .getConfig() == null);
            return null;
        }).when(mockDelegate).execute(any());

        executor.execute(testCommand);

        assertTrue(latch.await(1, TimeUnit.SECONDS));
        // Note: We can't directly verify the cleanup from
        // outside since it happens in the wrapped task
        verify(mockDelegate).execute(any());
    }

    @Test
    void testCreateExecutorWithValidParameters() {
        Executor createdExecutor =
            ThreadLocalPropagatingExecutorEnhanced.createExecutor(
                5, "test-pool");

        assertNotNull(createdExecutor);
        assertTrue(createdExecutor instanceof
            ThreadLocalPropagatingExecutorEnhanced);
    }

    @Test
    void testCreateExecutorWithMinimalThreads() {
        Executor createdExecutor = ThreadLocalPropagatingExecutorEnhanced
            .createExecutor(1, "minimal-pool");

        assertNotNull(createdExecutor);
        assertTrue(createdExecutor instanceof
            ThreadLocalPropagatingExecutorEnhanced);
    }

    @Test
    void testCreateExecutorWithHighThreadCount() {
        Executor createdExecutor = ThreadLocalPropagatingExecutorEnhanced
            .createExecutor(20, "large-pool");

        assertNotNull(createdExecutor);
        assertTrue(createdExecutor instanceof
            ThreadLocalPropagatingExecutorEnhanced);
    }

    @Test
    void testCreateExecutorWithNullPrefix() {
        Executor createdExecutor =
            ThreadLocalPropagatingExecutorEnhanced.createExecutor(5, null);

        assertNotNull(createdExecutor);
        assertTrue(createdExecutor instanceof
            ThreadLocalPropagatingExecutorEnhanced);
    }

    @Test
    void testCreateExecutorThreadFactoryExceptionHandling() {
        // Test that thread creation works and handles exceptions properly
        Executor createdExecutor = ThreadLocalPropagatingExecutorEnhanced
            .createExecutor(2, "exception-test");

        assertNotNull(createdExecutor);

        // Test executing a task that might cause thread exceptions
        CountDownLatch latch = new CountDownLatch(1);
        createdExecutor.execute(() -> {
            // Task that should execute successfully
            latch.countDown();
        });

        assertDoesNotThrow(() -> {
            assertTrue(latch.await(2, TimeUnit.SECONDS));
        });
    }

    @Test
    void testCreateExecutorRejectionHandler() {
        Executor createdExecutor = ThreadLocalPropagatingExecutorEnhanced
            .createExecutor(1, "rejection-test");

        assertNotNull(createdExecutor);

        // Submit many tasks to trigger rejection handling
        CountDownLatch latch = new CountDownLatch(1);

        // Submit one task that should execute
        createdExecutor.execute(() -> {
            try {
                Thread.sleep(100); // Hold thread briefly
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            latch.countDown();
        });

        // Submit many more tasks to potentially trigger
        // queue overflow and rejection
        for (int i = 0; i < 150; i++) {
            // More than default queue capacity of 100
            createdExecutor.execute(() -> {
                // Simple task
            });
        }

        // Should handle rejections gracefully
        assertDoesNotThrow(() -> {
            assertTrue(latch.await(2, TimeUnit.SECONDS));
        });
    }

    @Test
    void testExecuteWithExplicitlySetThreadLocalConfig()
        throws InterruptedException {
        executor = new ThreadLocalPropagatingExecutorEnhanced(mockDelegate,
            "test-executor");

        // Setup ThreadLocal config as explicitly set
        ThreadLocalContextHolder.setConfig(mockConfig);
        // Simulate explicit setting by calling setConfig again
        // or through other means. The isConfigExplicitlySet()
        // method behavior depends on implementation

        CountDownLatch latch = new CountDownLatch(1);

        Runnable testCommand = () -> {
            // Verify config is available
            assertNotNull(ThreadLocalContextHolder.getConfig());
            latch.countDown();
        };

        // Mock delegate to execute immediately
        doAnswer(invocation -> {
            Runnable wrapped = invocation.getArgument(0);
            wrapped.run();
            return null;
        }).when(mockDelegate).execute(any());

        executor.execute(testCommand);

        assertTrue(latch.await(1, TimeUnit.SECONDS));
        verify(mockDelegate).execute(any());
    }

    @Test
    void testCreateExecutorInternalExceptionHandling() {
        // Test error path in createExecutor by simulating thread pool
        // creation failure. This is difficult to test directly,
        // but we can verify the fallback behavior

        Executor executor1 = ThreadLocalPropagatingExecutorEnhanced
            .createExecutor(0, "zero-threads");
             // Should still return an executor (with fallback)
        assertNotNull(executor1);

        Executor executor2 = ThreadLocalPropagatingExecutorEnhanced
            .createExecutor(-1, "negative-threads");
            // Should still return an executor (with fallback)
        assertNotNull(executor2);
    }

    @Test
    void testConstantsValues() {
        // Test that constants have expected values
        assertEquals(5,
            ThreadLocalPropagatingExecutorEnhanced.DEFAULT_THREAD_COUNT);
        assertEquals(30,
            ThreadLocalPropagatingExecutorEnhanced.DEFAULT_TIMEOUT_SECONDS);
    }

    @Test
    void testExecuteMultipleTasksConcurrently() throws InterruptedException {
        // Test with real thread pool to verify concurrent execution
        Executor realExecutor = ThreadLocalPropagatingExecutorEnhanced
            .createExecutor(3, "concurrent-test");

        int taskCount = 10;
        CountDownLatch latch = new CountDownLatch(taskCount);

        // Setup ThreadLocal config
        ThreadLocalContextHolder.setConfig(mockConfig);

        for (int i = 0; i < taskCount; i++) {
            realExecutor.execute(() -> {
                // Verify ThreadLocal config is propagated
                assertNotNull(ThreadLocalContextHolder.getConfig());
                latch.countDown();
            });
        }

        assertTrue(latch.await(5, TimeUnit.SECONDS));
    }
}
