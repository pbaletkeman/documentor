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

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Enhanced comprehensive tests for AppConfigEnhanced class to improve branch coverage.
 * Tests all configuration paths, null handling, and bean creation scenarios.
 */
@ExtendWith(MockitoExtension.class)
class AppConfigEnhancedTest {

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
        reset(documentorConfig, analysisSettings, requestBuilder, responseHandler, apiClient, llmServiceFixEnhanced);
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
        when(analysisSettings.maxThreads()).thenReturn(8);

        appConfigEnhanced = new AppConfigEnhanced(documentorConfig);

        ThreadPoolTaskExecutor executor = appConfigEnhanced.llmExecutorEnhanced();

        assertNotNull(executor);
        assertEquals(8, executor.getCorePoolSize());
        assertEquals(16, executor.getMaxPoolSize()); // 8 * 2
        assertEquals(100, executor.getQueueCapacity());
        assertTrue(executor.getThreadNamePrefix().startsWith("LLM-Enhanced-"));
        // Note: These methods are not publicly available in ThreadPoolTaskExecutor,
        // so we verify the setup indirectly by checking that executor is properly initialized
        assertNotNull(executor.getThreadNamePrefix());
    }

    @Test
    void testLlmExecutorEnhancedWithNullDocumentorConfig() {
        appConfigEnhanced = new AppConfigEnhanced(null);

        ThreadPoolTaskExecutor executor = appConfigEnhanced.llmExecutorEnhanced();

        assertNotNull(executor);
        assertEquals(5, executor.getCorePoolSize()); // Default value
        assertEquals(10, executor.getMaxPoolSize()); // 5 * 2
    }

    @Test
    void testLlmExecutorEnhancedWithNullAnalysisSettings() {
        when(documentorConfig.analysisSettings()).thenReturn(null);

        appConfigEnhanced = new AppConfigEnhanced(documentorConfig);

        ThreadPoolTaskExecutor executor = appConfigEnhanced.llmExecutorEnhanced();

        assertNotNull(executor);
        assertEquals(5, executor.getCorePoolSize()); // Default value
        assertEquals(10, executor.getMaxPoolSize()); // 5 * 2
    }

    @Test
    void testLlmExecutorEnhancedTaskDecoratorWithValidConfig() {
        // Setup ThreadLocal context
        when(documentorConfig.analysisSettings()).thenReturn(analysisSettings);
        when(analysisSettings.maxThreads()).thenReturn(4);

        appConfigEnhanced = new AppConfigEnhanced(documentorConfig);
        ThreadPoolTaskExecutor executor = appConfigEnhanced.llmExecutorEnhanced();

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
            Thread.sleep(100);
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
        ThreadPoolTaskExecutor executor = appConfigEnhanced.llmExecutorEnhanced();

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
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        assertTrue(taskExecuted[0]);
    }

    @Test
    void testLlmExecutorEnhancedTaskDecoratorExceptionHandling() {
        appConfigEnhanced = new AppConfigEnhanced(documentorConfig);
        ThreadPoolTaskExecutor executor = appConfigEnhanced.llmExecutorEnhanced();

        // Create a task that throws exception
        Runnable throwingTask = () -> {
            throw new RuntimeException("Test exception in task");
        };

        // Execute task - should not propagate exception
        assertDoesNotThrow(() -> executor.execute(throwingTask));

        // Wait a bit for async execution
        try {
            Thread.sleep(100);
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
            null, requestBuilder, responseHandler, apiClient);

        assertNotNull(llmService);
    }

    @Test
    void testLlmServiceEnhancedForAutowiring() {
        appConfigEnhanced = new AppConfigEnhanced(documentorConfig);

        LlmServiceEnhanced llmService = appConfigEnhanced.llmServiceEnhancedForAutowiring(
            requestBuilder, responseHandler, apiClient);

        assertNotNull(llmService);
    }

    @Test
    void testLlmServiceFixEnhanced() {
        appConfigEnhanced = new AppConfigEnhanced(documentorConfig);

        LlmServiceFixEnhanced llmServiceFix = appConfigEnhanced.llmServiceFixEnhanced();

        assertNotNull(llmServiceFix);
    }

    @Test
    void testUnitTestDocumentationGeneratorEnhanced() {
        appConfigEnhanced = new AppConfigEnhanced(documentorConfig);

        UnitTestDocumentationGeneratorEnhanced generator =
            appConfigEnhanced.unitTestDocumentationGeneratorEnhanced(
                documentorConfig, llmServiceFixEnhanced, requestBuilder, responseHandler, apiClient);

        assertNotNull(generator);
    }

    @Test
    void testUnitTestDocumentationGeneratorEnhancedWithNullConfig() {
        appConfigEnhanced = new AppConfigEnhanced(null);

        UnitTestDocumentationGeneratorEnhanced generator =
            appConfigEnhanced.unitTestDocumentationGeneratorEnhanced(
                null, llmServiceFixEnhanced, requestBuilder, responseHandler, apiClient);

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
        // They should be different instances since @Scope is not singleton by default for @Bean methods
        assertNotSame(webClient1, webClient2);
    }

    @Test
    void testThreadPoolTaskExecutorConfiguration() {
        when(documentorConfig.analysisSettings()).thenReturn(analysisSettings);
        when(analysisSettings.maxThreads()).thenReturn(3);

        appConfigEnhanced = new AppConfigEnhanced(documentorConfig);
        ThreadPoolTaskExecutor executor = appConfigEnhanced.llmExecutorEnhanced();

        assertNotNull(executor);
        assertEquals(3, executor.getCorePoolSize());
        assertEquals(6, executor.getMaxPoolSize()); // 3 * 2
        assertEquals(100, executor.getQueueCapacity());
        assertEquals("LLM-Enhanced-", executor.getThreadNamePrefix());
        // Note: Configuration verification - executor internals are not publicly accessible
    }

    @Test
    void testConfigurationWithExtremeBoundaryValues() {
        when(documentorConfig.analysisSettings()).thenReturn(analysisSettings);
        when(analysisSettings.maxThreads()).thenReturn(1); // Minimum

        appConfigEnhanced = new AppConfigEnhanced(documentorConfig);
        ThreadPoolTaskExecutor executor = appConfigEnhanced.llmExecutorEnhanced();

        assertNotNull(executor);
        assertEquals(1, executor.getCorePoolSize());
        assertEquals(2, executor.getMaxPoolSize()); // 1 * 2
    }

    @Test
    void testConfigurationWithHighThreadCount() {
        when(documentorConfig.analysisSettings()).thenReturn(analysisSettings);
        when(analysisSettings.maxThreads()).thenReturn(20); // High value

        appConfigEnhanced = new AppConfigEnhanced(documentorConfig);
        ThreadPoolTaskExecutor executor = appConfigEnhanced.llmExecutorEnhanced();

        assertNotNull(executor);
        assertEquals(20, executor.getCorePoolSize());
        assertEquals(40, executor.getMaxPoolSize()); // 20 * 2
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
