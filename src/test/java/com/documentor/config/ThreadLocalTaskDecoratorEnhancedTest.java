package com.documentor.config;

import com.documentor.config.model.AnalysisSettings;
import com.documentor.config.model.LlmModelConfig;
import com.documentor.config.model.OutputSettings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Tests for ThreadLocalTaskDecoratorEnhanced class to improve branch coverage.
 */
@ExtendWith(MockitoExtension.class)
class ThreadLocalTaskDecoratorEnhancedTest {
    // Magic number constants for test clarity
    private static final int TIMEOUT_MILLIS = 4000;
    private static final int TIMEOUT_SECONDS = 30;
    private static final int DEFAULT_COUNT = 5;
    private static final int SMALL_COUNT = 3;

    private ThreadLocalTaskDecoratorEnhanced decorator;
    private DocumentorConfig testConfig;

    @BeforeEach
    void setUp() {
        decorator = new ThreadLocalTaskDecoratorEnhanced();
        ThreadLocalContextHolder.clearConfig();

        // Create test config
        List<LlmModelConfig> models = Collections.singletonList(
            new LlmModelConfig("test-model", "ollama",
            "http://localhost:11434", null, TIMEOUT_MILLIS, TIMEOUT_SECONDS)
        );
        OutputSettings outputSettings = new OutputSettings(
            "output", "markdown", false, false, false);
        AnalysisSettings analysisSettings = new AnalysisSettings(
            true, 5, null, null);

        testConfig = new DocumentorConfig(models, outputSettings,
            analysisSettings);
    }    @Test
    void testDecorateWithNullRunnable() {
        Runnable decoratedRunnable = decorator.decorate(null);

        assertNotNull(decoratedRunnable);
        // Should not throw exception when running
        assertDoesNotThrow(decoratedRunnable::run);
    }

    @Test
    void testDecorateWithValidRunnableAndConfig() throws InterruptedException {
        // Set config in parent thread
        ThreadLocalContextHolder.setConfig(testConfig);

        CountDownLatch latch = new CountDownLatch(1);
        List<DocumentorConfig> capturedConfigs = new ArrayList<>();

        Runnable originalRunnable = () -> {
            capturedConfigs.add(ThreadLocalContextHolder.getConfig());
            latch.countDown();
        };

        Runnable decoratedRunnable = decorator.decorate(originalRunnable);

        // Execute in new thread
        Thread childThread = new Thread(decoratedRunnable);
        childThread.start();

        assertTrue(latch.await(DEFAULT_COUNT, TimeUnit.SECONDS));
        childThread.join();

        // Config should be propagated to child thread
        assertEquals(1, capturedConfigs.size());
        assertEquals(testConfig, capturedConfigs.get(0));
    }

    @Test
    void testDecorateWithNoConfigInParentThread() throws InterruptedException {
        // No config set in parent thread
        CountDownLatch latch = new CountDownLatch(1);
        List<DocumentorConfig> capturedConfigs = new ArrayList<>();

        Runnable originalRunnable = () -> {
            capturedConfigs.add(ThreadLocalContextHolder.getConfig());
            latch.countDown();
        };

        Runnable decoratedRunnable = decorator.decorate(originalRunnable);

        // Execute in new thread
        Thread childThread = new Thread(decoratedRunnable);
        childThread.start();

        assertTrue(latch.await(DEFAULT_COUNT, TimeUnit.SECONDS));
        childThread.join();

        // No config should be available in child thread
        assertEquals(1, capturedConfigs.size());
        assertNull(capturedConfigs.get(0));
    }

    @Test
    void testDecorateWithConfigWithNullModels() throws InterruptedException {
        // Create config with null models (this will fail validation,
        // but test the branch)
        OutputSettings outputSettings = new OutputSettings(
            "output", "markdown", false, false, false);
            AnalysisSettings analysisSettings = new AnalysisSettings(
                true, DEFAULT_COUNT, null, null);

        // We'll test with empty models instead since null models
        // would fail validation
        List<LlmModelConfig> emptyModels = Collections.emptyList();
        DocumentorConfig configWithEmptyModels =
            new DocumentorConfig(emptyModels, outputSettings,
                analysisSettings);

        ThreadLocalContextHolder.setConfig(configWithEmptyModels);

        CountDownLatch latch = new CountDownLatch(1);
        List<DocumentorConfig> capturedConfigs = new ArrayList<>();

        Runnable originalRunnable = () -> {
            capturedConfigs.add(ThreadLocalContextHolder.getConfig());
            latch.countDown();
        };

        Runnable decoratedRunnable = decorator.decorate(originalRunnable);

        // Execute in new thread
        Thread childThread = new Thread(decoratedRunnable);
        childThread.start();

            assertTrue(latch.await(DEFAULT_COUNT, TimeUnit.SECONDS));
        childThread.join();

        // Config should still be propagated even with empty models
        assertEquals(1, capturedConfigs.size());
        assertEquals(configWithEmptyModels, capturedConfigs.get(0));
    }    @Test
    void testDecorateWithRunnableThrowingException()
        throws InterruptedException {
        ThreadLocalContextHolder.setConfig(testConfig);

        CountDownLatch latch = new CountDownLatch(1);

        Runnable throwingRunnable = () -> {
            latch.countDown();
            throw new RuntimeException("Test exception in runnable");
        };

        Runnable decoratedRunnable = decorator.decorate(throwingRunnable);

        // Should not throw exception from decorated runnable
        assertDoesNotThrow(() -> {
            Thread childThread = new Thread(decoratedRunnable);
            childThread.start();

            try {
                assertTrue(latch.await(5, TimeUnit.SECONDS));
                childThread.join();
            } catch (InterruptedException e) {
                fail("Thread execution was interrupted");
            }
        });
    }

    @Test
    void testDecorateWithEmptyModelsList() throws InterruptedException {
        // Create config with empty models list
        List<LlmModelConfig> emptyModels = Collections.emptyList();
        OutputSettings outputSettings = new OutputSettings(
            "output", "markdown", false, false, false);
        AnalysisSettings analysisSettings = new AnalysisSettings(
            true, DEFAULT_COUNT, null, null);

        DocumentorConfig configWithEmptyModels =
            new DocumentorConfig(emptyModels, outputSettings, analysisSettings);

        ThreadLocalContextHolder.setConfig(configWithEmptyModels);

        CountDownLatch latch = new CountDownLatch(1);
        List<DocumentorConfig> capturedConfigs = new ArrayList<>();

        Runnable originalRunnable = () -> {
            capturedConfigs.add(ThreadLocalContextHolder.getConfig());
            latch.countDown();
        };

        Runnable decoratedRunnable = decorator.decorate(originalRunnable);

        // Execute in new thread
        Thread childThread = new Thread(decoratedRunnable);
        childThread.start();

        assertTrue(latch.await(DEFAULT_COUNT, TimeUnit.SECONDS));
        childThread.join();

        // Config should be propagated with empty models list
        assertEquals(1, capturedConfigs.size());
        assertEquals(configWithEmptyModels, capturedConfigs.get(0));
    }

    @Test
    void testDecorateWithMultipleModels() throws InterruptedException {
        // Create config with multiple models
        List<LlmModelConfig> models = List.of(
            new LlmModelConfig(
                "model1", "provider1",
                "url1", "key1", TIMEOUT_MILLIS, TIMEOUT_SECONDS),
            new LlmModelConfig(
                "model2", "provider2",
                "url2", "key2", TIMEOUT_MILLIS, TIMEOUT_SECONDS),
            new LlmModelConfig(
                "model3", "provider3",
                "url3", "key3", TIMEOUT_MILLIS, TIMEOUT_SECONDS)
        );

        OutputSettings outputSettings =
            new OutputSettings("output", "markdown", false, false, false);
        AnalysisSettings analysisSettings =
            new AnalysisSettings(true, DEFAULT_COUNT, null, null);

        DocumentorConfig configWithMultipleModels =
            new DocumentorConfig(models, outputSettings, analysisSettings);

        ThreadLocalContextHolder.setConfig(configWithMultipleModels);

        CountDownLatch latch = new CountDownLatch(1);
        List<DocumentorConfig> capturedConfigs = new ArrayList<>();

        Runnable originalRunnable = () -> {
            capturedConfigs.add(ThreadLocalContextHolder.getConfig());
            latch.countDown();
        };

        Runnable decoratedRunnable = decorator.decorate(originalRunnable);

        // Execute in new thread
        Thread childThread = new Thread(decoratedRunnable);
        childThread.start();

        assertTrue(latch.await(DEFAULT_COUNT, TimeUnit.SECONDS));
        childThread.join();

        // Config should be propagated with all models
        assertEquals(1, capturedConfigs.size());
        DocumentorConfig capturedConfig = capturedConfigs.get(0);
        assertNotNull(capturedConfig);
        assertEquals(3, capturedConfig.llmModels().size());
        assertEquals(configWithMultipleModels, capturedConfig);
    }

    @Test
    void testThreadLocalCleanupAfterExecution() throws InterruptedException {
        ThreadLocalContextHolder.setConfig(testConfig);

        CountDownLatch latch = new CountDownLatch(1);

        Runnable originalRunnable = () -> {
            // Config should be available during execution
            assertNotNull(ThreadLocalContextHolder.getConfig());
            latch.countDown();
        };

        Runnable decoratedRunnable = decorator.decorate(originalRunnable);

        Thread childThread = new Thread(() -> {
            decoratedRunnable.run();
            // Config should be cleaned up after execution
            assertNull(ThreadLocalContextHolder.getConfig());
        });

        childThread.start();
        assertTrue(latch.await(DEFAULT_COUNT, TimeUnit.SECONDS));
        childThread.join();
    }

    @Test
    void testConfigExplicitlySetPropagation() throws InterruptedException {
        ThreadLocalContextHolder.setConfig(testConfig);

        assertTrue(ThreadLocalContextHolder.isConfigExplicitlySet());

        CountDownLatch latch = new CountDownLatch(1);

        Runnable originalRunnable = () -> {
            // Config should be available and properly set in child thread
            assertNotNull(ThreadLocalContextHolder.getConfig());
            latch.countDown();
        };

        Runnable decoratedRunnable = decorator.decorate(originalRunnable);

        Thread childThread = new Thread(decoratedRunnable);
        childThread.start();

        assertTrue(latch.await(DEFAULT_COUNT, TimeUnit.SECONDS));
        childThread.join();
    }    @Test
    void testMultipleDecorationsWorkCorrectly() throws InterruptedException {
        ThreadLocalContextHolder.setConfig(testConfig);

        CountDownLatch latch = new CountDownLatch(SMALL_COUNT);
        List<DocumentorConfig> capturedConfigs = Collections
            .synchronizedList(new ArrayList<>());

        Runnable originalRunnable = () -> {
            capturedConfigs.add(ThreadLocalContextHolder.getConfig());
            latch.countDown();
        };

        // Create multiple decorated runnables
        Runnable decorated1 = decorator.decorate(originalRunnable);
        Runnable decorated2 = decorator.decorate(originalRunnable);
        Runnable decorated3 = decorator.decorate(originalRunnable);

        // Execute all in different threads
        Thread thread1 = new Thread(decorated1);
        Thread thread2 = new Thread(decorated2);
        Thread thread3 = new Thread(decorated3);

        thread1.start();
        thread2.start();
        thread3.start();

        assertTrue(latch.await(DEFAULT_COUNT, TimeUnit.SECONDS));

        thread1.join();
        thread2.join();
        thread3.join();

        // All threads should have received the config
        assertEquals(SMALL_COUNT, capturedConfigs.size());
        capturedConfigs.forEach(config -> assertEquals(testConfig, config));
    }
}
