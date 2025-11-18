package com.documentor.config;

import com.documentor.config.model.AnalysisSettings;
import com.documentor.config.model.LlmModelConfig;
import com.documentor.config.model.OutputSettings;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Additional coverage tests for config package to reach 94% instruction
 * coverage. Focuses on exception scenarios and edge cases without complex
 * mocking.
 */
class ConfigPackageAdditionalCoverageTest {
    // Magic number constants for test clarity
    private static final int TOKEN_4000 = 4000;
    private static final int TIMEOUT_30 = 30;
    private static final int THREADS_5 = 5;
    private static final int TASKS_150 = 150;
    private static final int WAIT_5_SECONDS = 5;
    private static final int THREADS_3 = 3;
    private static final int TASKS_10 = 10;

    private DocumentorConfig testConfig;

    @BeforeEach
    void setUp() {
        ThreadLocalContextHolder.clearConfig();

        // Create test config
        LlmModelConfig model = new LlmModelConfig(
            "test-model", "provider", "url", "key", TOKEN_4000, TIMEOUT_30);
        OutputSettings outputSettings = new OutputSettings(
            "output", "markdown", false, false, false);
        AnalysisSettings analysisSettings = new AnalysisSettings(
            true, THREADS_5, null, null);

        testConfig = new DocumentorConfig(Collections.singletonList(model),
            outputSettings, analysisSettings);
    }

    @AfterEach
    void tearDown() {
        ThreadLocalContextHolder.clearConfig();
    }

    /**
     * Test ThreadLocalPropagatingExecutorEnhanced with
     * simulated rejection handler scenario
     */
    @Test
    void testExecutorRejectionHandlerExecution() throws InterruptedException {
        // Create executor with very small queue to trigger rejection
        Executor executor = ThreadLocalPropagatingExecutorEnhanced
            .createExecutor(1, "rejection-test");

        assertNotNull(executor);

        // Submit a long-running task to fill the single thread
        CountDownLatch blockingLatch = new CountDownLatch(1);
        CountDownLatch completedLatch = new CountDownLatch(1);

        executor.execute(() -> {
            try {
                // Block the thread for a while
                blockingLatch.await(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            completedLatch.countDown();
        });

        // Submit many tasks to potentially trigger queue overflow
        for (int i = 0; i < TASKS_150; i++) {
            executor.execute(() -> {
                // Simple task
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        // Release the blocking task
        blockingLatch.countDown();

        // Wait for completion
        assertTrue(completedLatch.await(WAIT_5_SECONDS, TimeUnit.SECONDS));
    }

    /**
     * Test ThreadLocalTaskDecoratorEnhanced with complex exception scenarios
     */
    @Test
    void testTaskDecoratorWithRuntimeException() throws InterruptedException {
        ThreadLocalTaskDecoratorEnhanced decorator =
            new ThreadLocalTaskDecoratorEnhanced();

        // Set up config with complex model list
        ThreadLocalContextHolder.setConfig(testConfig);

        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean exceptionHandled = new AtomicBoolean(false);

        Runnable faultyTask = () -> {
            // Task that throws exception after latch countdown
            latch.countDown();
            throw new RuntimeException("Simulated task failure");
        };

        Runnable decoratedTask = decorator.decorate(faultyTask);

        // Execute the decorated task - should handle exception gracefully
        Thread testThread = new Thread(() -> {
            try {
                decoratedTask.run();
                exceptionHandled.set(true);
            } catch (Exception e) {
                // Should not reach here if decorator handles
                // exceptions properly
                fail("Exception should be handled by decorator: "
                + e.getMessage());
            }
        });

        testThread.start();
        assertTrue(latch.await(2, TimeUnit.SECONDS));
        testThread.join();
        assertTrue(exceptionHandled.get());
    }

    /**
     * Test BeanUtils with null parameter validation
     */
    @Test
    void testBeanUtilsNullParameterValidation() {
        // Test null context
        assertThrows(NullPointerException.class, () ->
            BeanUtils.overrideBean(null, "testBean", testConfig)
        );

        // Test null bean name
        assertThrows(NullPointerException.class, () ->
            BeanUtils.overrideBean(mock(
                org.springframework.context.ApplicationContext.class),
                null, testConfig)
        );

        // Test null new bean
        assertThrows(NullPointerException.class, () ->
            BeanUtils.overrideBean(mock(
                org.springframework.context.ApplicationContext.class),
                "testBean", null)
        );
    }

    /**
     * Test ThreadLocalPropagatingExecutorEnhanced with edge case thread counts
     */
    @Test
    void testExecutorCreationEdgeCases() {
        // Test with zero threads - should still create executor
        Executor executor1 = ThreadLocalPropagatingExecutorEnhanced
            .createExecutor(0, "zero-threads");
        assertNotNull(executor1);

        // Test with negative threads - should still create executor
        Executor executor2 = ThreadLocalPropagatingExecutorEnhanced
            .createExecutor(-1, "negative-threads");
        assertNotNull(executor2);

        // Test with null name prefix
        Executor executor3 = ThreadLocalPropagatingExecutorEnhanced
            .createExecutor(2, null);
        assertNotNull(executor3);

        // Test execution still works with edge case executor
        CountDownLatch latch = new CountDownLatch(1);
        executor1.execute(latch::countDown);

        assertDoesNotThrow(() ->
            assertTrue(latch.await(2, TimeUnit.SECONDS))
        );
    }

    /**
     * Test ThreadLocalTaskDecoratorEnhanced with null models in config
     */
    @Test
    void testTaskDecoratorWithNullModelsInConfig() throws InterruptedException {
        ThreadLocalTaskDecoratorEnhanced decorator =
            new ThreadLocalTaskDecoratorEnhanced();

        // Create config with potential null model list
        OutputSettings outputSettings =
            new OutputSettings("output", "markdown", false, false, false);
        AnalysisSettings analysisSettings =
            new AnalysisSettings(true, THREADS_5, null, null);

        // Use empty list instead of null (as null would fail validation)
        DocumentorConfig emptyModelsConfig =
            new DocumentorConfig(Collections.emptyList(), outputSettings,
            analysisSettings);

        ThreadLocalContextHolder.setConfig(emptyModelsConfig);

        CountDownLatch latch = new CountDownLatch(1);

        Runnable task = () -> {
            DocumentorConfig threadConfig =
                ThreadLocalContextHolder.getConfig();
            assertNotNull(threadConfig);
            assertEquals(0, threadConfig.llmModels().size());
            latch.countDown();
        };

        Runnable decoratedTask = decorator.decorate(task);

        Thread testThread = new Thread(decoratedTask);
        testThread.start();

        assertTrue(latch.await(2, TimeUnit.SECONDS));
        testThread.join();
    }

    /**
     * Test ThreadLocalPropagatingExecutorEnhanced constants
     */
    @Test
    void testExecutorConstants() {
        assertEquals(THREADS_5,
            ThreadLocalPropagatingExecutorEnhanced.DEFAULT_THREAD_COUNT);
        assertEquals(TIMEOUT_30,
            ThreadLocalPropagatingExecutorEnhanced.DEFAULT_TIMEOUT_SECONDS);
    }

    /**
     * Test ThreadLocal propagation with explicitly set config
     */
    @Test
    void testThreadLocalPropagationWithExplicitlySetConfig()
        throws InterruptedException {
        ThreadLocalTaskDecoratorEnhanced decorator =
            new ThreadLocalTaskDecoratorEnhanced();

        // Set config explicitly
        ThreadLocalContextHolder.setConfig(testConfig);
        assertTrue(ThreadLocalContextHolder.isConfigExplicitlySet());

        CountDownLatch latch = new CountDownLatch(1);

        Runnable task = () -> {
            // Verify config is propagated
            DocumentorConfig threadConfig =
                ThreadLocalContextHolder.getConfig();
            assertNotNull(threadConfig);
            assertEquals(testConfig, threadConfig);
            latch.countDown();
        };

        Runnable decoratedTask = decorator.decorate(task);

        Thread testThread = new Thread(decoratedTask);
        testThread.start();

        assertTrue(latch.await(2, TimeUnit.SECONDS));
        testThread.join();
    }

    /**
     * Test complex execution scenario with multiple threads
     */
    @Test
    void testComplexMultiThreadedExecution() throws InterruptedException {
        Executor executor =
            ThreadLocalPropagatingExecutorEnhanced.createExecutor(
                THREADS_3, "multi-test");
        ThreadLocalTaskDecoratorEnhanced decorator =
            new ThreadLocalTaskDecoratorEnhanced();

        // Set up parent thread config
        ThreadLocalContextHolder.setConfig(testConfig);

        int taskCount = TASKS_10;
        CountDownLatch latch = new CountDownLatch(taskCount);

        for (int i = 0; i < taskCount; i++) {
            Runnable task = () -> {
                // Verify config is available in each thread
                DocumentorConfig threadConfig =
                    ThreadLocalContextHolder.getConfig();
                assertNotNull(threadConfig);
                latch.countDown();
            };

            Runnable decoratedTask = decorator.decorate(task);
            executor.execute(decoratedTask);
        }

        assertTrue(latch.await(5, TimeUnit.SECONDS));
    }

    /**
     * Helper method to create mock without external dependencies
     */
    @SuppressWarnings("unchecked")
    private <T> T mock(final Class<T> clazz) {
        // Simple mock implementation for basic testing
        try {
            return (T) java.lang.reflect.Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class[]{clazz},
                (proxy, method, args) -> {
                    // Return appropriate defaults based on method name
                    if (method.getName().equals("getBeanFactory")) {
                        return mock(org.springframework.beans.factory
                            .config.ConfigurableListableBeanFactory.class);
                    }
                    if (method.getName().equals("containsBean")) {
                        return false;
                    }
                    if (method.getName().equals("isSingleton")) {
                        return true;
                    }
                    if (method.getName().equals("getBeanDefinitionNames")) {
                        return new String[0];
                    }
                    return null;
                }
            );
        } catch (Exception e) {
            return null;
        }
    }
}
