package com.documentor.config;

import com.documentor.config.model.AnalysisSettings;
import com.documentor.service.LlmServiceEnhanced;
import com.documentor.service.LlmServiceFixEnhanced;
import com.documentor.service.documentation.UnitTestDocumentationGeneratorEnhanced;
import com.documentor.service.llm.LlmApiClient;
import com.documentor.service.llm.LlmRequestBuilder;
import com.documentor.service.llm.LlmResponseHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.Executor;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotSame;

/**
 * Enhanced comprehensive tests for AppConfigEnhanced class to
 * improve branch coverage.
 * Tests all configuration paths, null handling, and bean creation scenarios.
 */
@ExtendWith(MockitoExtension.class)
class AppConfigEnhancedTest {
    // Magic number constants for test clarity
    private static final int THREADS_EIGHT = 8;
    private static final int THREADS_SIXTEEN = 16;
    private static final int QUEUE_HUNDRED = 100;
    private static final int THREADS_FIVE = 5;
    private static final int THREADS_TEN = 10;
    private static final int THREADS_FOUR = 4;
    private static final int THREADS_THREE = 3;
    private static final int THREADS_SIX = 6;
    private static final int THREADS_TWENTY = 20;
    private static final int THREADS_FORTY = 40;

    @Mock
    private DocumentorConfig documentorConfig;

    @Mock
    private AnalysisSettings analysisSettings;

    @Mock
    private LlmRequestBuilder requestBuilder;

    @Mock
    private LlmResponseHandler responseHandler;

    @Mock
    private LlmApiClient apiClient;

    @Mock
    private LlmServiceFixEnhanced llmServiceFixEnhanced;

    private AppConfigEnhanced appConfigEnhanced;

    @BeforeEach
    void setUp() {
        reset(documentorConfig, analysisSettings, requestBuilder,
        responseHandler, apiClient, llmServiceFixEnhanced);
    }

    @Test
    void testConstructorWithValidDocumentorConfig() {
        // Test constructor with valid DocumentorConfig
        appConfigEnhanced = new AppConfigEnhanced(documentorConfig);

        assertNotNull(appConfigEnhanced);
    }

    @Test
    void testConstructorWithNullDocumentorConfig() {
        // Test constructor with null DocumentorConfig
        appConfigEnhanced = new AppConfigEnhanced(null);

        assertNotNull(appConfigEnhanced);
    }

    @Test
    void testWebClientEnhanced() {
        appConfigEnhanced = new AppConfigEnhanced(documentorConfig);

        WebClient webClient = appConfigEnhanced.webClientEnhanced();

        assertNotNull(webClient);
    }

    @Test
    void testLlmExecutorEnhancedWithValidAnalysisSettings() {
        // Setup valid analysis settings
        when(documentorConfig.analysisSettings()).thenReturn(analysisSettings);
        when(analysisSettings.maxThreads()).thenReturn(THREADS_THREE);
        appConfigEnhanced = new AppConfigEnhanced(documentorConfig);
        ThreadPoolTaskExecutor executor =
            appConfigEnhanced.llmExecutorEnhanced();
        assertNotNull(executor);
        assertEquals(THREADS_THREE, executor.getCorePoolSize());
        assertEquals(THREADS_SIX, executor.getMaxPoolSize()); // 3 * 2
        assertEquals(QUEUE_HUNDRED, executor.getQueueCapacity());
        assertTrue(executor.getThreadNamePrefix().startsWith("LLM-Enhanced-"));
        // Note: These methods are not publicly available in
        // ThreadPoolTaskExecutor, so we verify the setup indirectly by
        // checking that executor is properly initialized
        assertNotNull(executor.getThreadNamePrefix());
    }

    @Test
    void testLlmExecutorEnhancedWithNullDocumentorConfig() {
        appConfigEnhanced = new AppConfigEnhanced(null);

        ThreadPoolTaskExecutor executor = appConfigEnhanced
            .llmExecutorEnhanced();

        assertNotNull(executor);
        assertEquals(THREADS_FIVE, executor.getCorePoolSize()); // Default value
        assertEquals(THREADS_TEN, executor.getMaxPoolSize()); // 5 * 2
    }

    @Test
    void testLlmExecutorEnhancedWithNullAnalysisSettings() {
        when(documentorConfig.analysisSettings()).thenReturn(null);

        appConfigEnhanced = new AppConfigEnhanced(documentorConfig);

        ThreadPoolTaskExecutor executor = appConfigEnhanced
            .llmExecutorEnhanced();

        assertNotNull(executor);
        assertEquals(THREADS_FIVE, executor.getCorePoolSize()); // Default value
        assertEquals(THREADS_TEN, executor.getMaxPoolSize()); // 5 * 2
    }

    @Test
    void testLlmExecutorEnhancedTaskDecoratorWithValidConfig() {
        // Setup ThreadLocal context
        when(documentorConfig.analysisSettings()).thenReturn(analysisSettings);
        when(analysisSettings.maxThreads()).thenReturn(THREADS_EIGHT);
        appConfigEnhanced = new AppConfigEnhanced(documentorConfig);
        ThreadPoolTaskExecutor executor
            = appConfigEnhanced.llmExecutorEnhanced();
        assertEquals(THREADS_EIGHT, executor.getCorePoolSize());
        assertEquals(THREADS_SIXTEEN, executor.getMaxPoolSize()); // 8 * 2
        assertEquals(QUEUE_HUNDRED, executor.getQueueCapacity());

        // Setup ThreadLocal context for testing
        ThreadLocalContextHolder.setConfig(documentorConfig);

        // Create a test task
        boolean[] taskExecuted = {false};
        Runnable testTask = () -> {
            taskExecuted[0] = true;
            // Verify config is available in thread
            assertNotNull(ThreadLocalContextHolder.getConfig());
        };

        // Execute task through decorator
        executor.execute(testTask);

        // Wait a bit for async execution
        try {
                Thread.sleep(QUEUE_HUNDRED);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        assertTrue(taskExecuted[0]);

        // Clean up
        ThreadLocalContextHolder.clearConfig();
    }

    @Test
    void testLlmExecutorEnhancedTaskDecoratorWithNullConfig() {
        appConfigEnhanced = new AppConfigEnhanced(documentorConfig);
        ThreadPoolTaskExecutor executor = appConfigEnhanced
            .llmExecutorEnhanced();

        // Clear ThreadLocal context
        ThreadLocalContextHolder.clearConfig();

        // Create a test task
        boolean[] taskExecuted = {false};
        Runnable testTask = () -> {
            taskExecuted[0] = true;
        };

        // Execute task through decorator
        executor.execute(testTask);

        // Wait a bit for async execution
        try {
                Thread.sleep(QUEUE_HUNDRED);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        assertTrue(taskExecuted[0]);
    }

    @Test
    void testLlmExecutorEnhancedTaskDecoratorExceptionHandling() {
        appConfigEnhanced = new AppConfigEnhanced(documentorConfig);
        ThreadPoolTaskExecutor executor = appConfigEnhanced
            .llmExecutorEnhanced();

        // Create a task that throws exception
        Runnable throwingTask = () -> {
            throw new RuntimeException("Test exception in task");
        };

        // Execute task - should not propagate exception
        assertDoesNotThrow(() -> executor.execute(throwingTask));

        // Wait a bit for async execution
        try {
                Thread.sleep(QUEUE_HUNDRED);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Test
    void testLlmServiceEnhanced() {
        appConfigEnhanced = new AppConfigEnhanced(documentorConfig);

        LlmServiceEnhanced llmService = appConfigEnhanced.llmServiceEnhanced(
            documentorConfig, requestBuilder, responseHandler, apiClient);

        assertNotNull(llmService);
    }

    @Test
    void testLlmServiceEnhancedWithNullConfig() {
        appConfigEnhanced = new AppConfigEnhanced(null);

        LlmServiceEnhanced llmService = appConfigEnhanced.llmServiceEnhanced(
            null, requestBuilder, responseHandler,
            apiClient);

        assertNotNull(llmService);
    }

    @Test
    void testLlmServiceEnhancedForAutowiring() {
        appConfigEnhanced = new AppConfigEnhanced(documentorConfig);

        LlmServiceEnhanced llmService = appConfigEnhanced
            .llmServiceEnhancedForAutowiring(
            requestBuilder, responseHandler, apiClient);

        assertNotNull(llmService);
    }

    @Test
    void testLlmServiceFixEnhanced() {
        appConfigEnhanced = new AppConfigEnhanced(documentorConfig);

        LlmServiceFixEnhanced llmServiceFix = appConfigEnhanced
            .llmServiceFixEnhanced();

        assertNotNull(llmServiceFix);
    }

    @Test
    void testUnitTestDocumentationGeneratorEnhanced() {
        appConfigEnhanced = new AppConfigEnhanced(documentorConfig);

        UnitTestDocumentationGeneratorEnhanced generator =
            appConfigEnhanced.unitTestDocumentationGeneratorEnhanced(
                documentorConfig, llmServiceFixEnhanced, requestBuilder,
                    responseHandler, apiClient);

        assertNotNull(generator);
    }

    @Test
    void testUnitTestDocumentationGeneratorEnhancedWithNullConfig() {
        appConfigEnhanced = new AppConfigEnhanced(null);

        UnitTestDocumentationGeneratorEnhanced generator =
            appConfigEnhanced.unitTestDocumentationGeneratorEnhanced(
                null, llmServiceFixEnhanced,
                requestBuilder, responseHandler, apiClient);

        assertNotNull(generator);
    }

    @Test
    void testGetAsyncExecutor() {
        appConfigEnhanced = new AppConfigEnhanced(documentorConfig);

        Executor asyncExecutor = appConfigEnhanced.getAsyncExecutor();

        assertNotNull(asyncExecutor);
        assertTrue(asyncExecutor instanceof ThreadPoolTaskExecutor);
    }

    @Test
    void testMultipleBeanCreationCallsConsistency() {
        appConfigEnhanced = new AppConfigEnhanced(documentorConfig);

        // Create multiple instances to test consistency
        WebClient webClient1 = appConfigEnhanced.webClientEnhanced();
        WebClient webClient2 = appConfigEnhanced.webClientEnhanced();

        assertNotNull(webClient1);
        assertNotNull(webClient2);
        // They should be different instances since @Scope is not
        // singleton by default for @Bean methods
        assertNotSame(webClient1, webClient2);
    }

    @Test
    void testThreadPoolTaskExecutorConfiguration() {
        when(documentorConfig.analysisSettings()).thenReturn(analysisSettings);
        when(analysisSettings.maxThreads()).thenReturn(THREADS_THREE);

        appConfigEnhanced = new AppConfigEnhanced(documentorConfig);
        ThreadPoolTaskExecutor executor = appConfigEnhanced
        .llmExecutorEnhanced();

        assertNotNull(executor);
        assertEquals(THREADS_THREE, executor.getCorePoolSize());
        assertEquals(THREADS_SIX, executor.getMaxPoolSize()); // 3 * 2
        assertEquals(QUEUE_HUNDRED, executor.getQueueCapacity());
        assertEquals("LLM-Enhanced-", executor.getThreadNamePrefix());
        // Note: Configuration verification
        // - executor internals are not publicly accessible
    }

    @Test
    void testConfigurationWithExtremeBoundaryValues() {
        when(documentorConfig.analysisSettings()).thenReturn(analysisSettings);
        when(analysisSettings.maxThreads()).thenReturn(1); // Minimum

        appConfigEnhanced = new AppConfigEnhanced(documentorConfig);
        ThreadPoolTaskExecutor executor = appConfigEnhanced
            .llmExecutorEnhanced();

        assertNotNull(executor);
        assertEquals(1, executor.getCorePoolSize());
        assertEquals(2, executor.getMaxPoolSize()); // 1 * 2
    }

    @Test
    void testConfigurationWithHighThreadCount() {
        when(documentorConfig.analysisSettings()).thenReturn(analysisSettings);
        // High value
        when(analysisSettings.maxThreads()).thenReturn(THREADS_TWENTY);

        appConfigEnhanced = new AppConfigEnhanced(documentorConfig);
        ThreadPoolTaskExecutor executor = appConfigEnhanced
            .llmExecutorEnhanced();

        assertNotNull(executor);
        assertEquals(THREADS_TWENTY, executor.getCorePoolSize());
        assertEquals(THREADS_FORTY, executor.getMaxPoolSize()); // 20 * 2
    }

    @Test
    void testLlmServiceCreationWithAllNullDependencies() {
        appConfigEnhanced = new AppConfigEnhanced(null);

        // Test with all null dependencies
        LlmServiceEnhanced llmService = appConfigEnhanced.llmServiceEnhanced(
            null, null, null, null);

        assertNotNull(llmService);
    }
}
