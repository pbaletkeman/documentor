package com.documentor.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.concurrent.Executor;

import com.documentor.config.model.AnalysisSettings;
import com.documentor.config.model.LlmModelConfig;
import com.documentor.config.model.OutputSettings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * ðŸ§ª Comprehensive tests for AppConfig
 *
 * Tests Spring configuration bean creation and async executor setup
 */
@ExtendWith(MockitoExtension.class)
class AppConfigTest {

    // Test constants for magic number violations
    private static final int TEST_THREAD_COUNT = 4;
    private static final int TEST_THREAD_COUNT_DOUBLE = 8;
    private static final int TEST_THREAD_COUNT_SIX = 6;
    private static final int TEST_THREAD_COUNT_TWELVE = 12;
    private static final int TEST_THREAD_COUNT_SIXTEEN = 16;
    private static final int TEST_THREAD_COUNT_THIRTY_TWO = 32;
    private static final int TEST_QUEUE_CAPACITY = 100;

    @Mock
    private DocumentorConfig documentorConfig;

    @Mock
    private AnalysisSettings analysisSettings;

    private AppConfig appConfig;

    @BeforeEach
    void setUp() {
        lenient().when(documentorConfig.analysisSettings()).thenReturn(analysisSettings);
        lenient().when(analysisSettings.maxThreads()).thenReturn(TEST_THREAD_COUNT);

        appConfig = new AppConfig(documentorConfig);
    }

    @Test
    void testConstructor() {
        // Given/When
        AppConfig config = new AppConfig(documentorConfig);

        // Then
        assertNotNull(config);
    }

    @Test
    void testWebClientBean() {
        // When
        WebClient webClient = appConfig.webClient();

        // Then
        assertNotNull(webClient);
        // WebClient is properly configured with default settings
    }

    @Test
    void testLlmExecutorBean() {
        // When
        ThreadPoolTaskExecutor executor = appConfig.llmExecutor();

        // Then
        assertNotNull(executor);
        assertEquals(TEST_THREAD_COUNT, executor.getCorePoolSize());
        assertEquals(TEST_THREAD_COUNT_DOUBLE, executor.getMaxPoolSize()); // 4 * 2 (DEFAULT_THREAD_MULTIPLIER)
        assertEquals(TEST_QUEUE_CAPACITY, executor.getQueueCapacity()); // DEFAULT_QUEUE_CAPACITY
        assertEquals("LLM-", executor.getThreadNamePrefix());
        // Note: Cannot directly test these private fields, but we can verify the executor is properly configured
        assertNotNull(executor.getThreadPoolExecutor());
    }

    @Test
    void testLlmExecutorWithDifferentMaxThreads() {
        // Given
        when(analysisSettings.maxThreads()).thenReturn(TEST_THREAD_COUNT_DOUBLE);

        // When
        ThreadPoolTaskExecutor executor = appConfig.llmExecutor();

        // Then
        assertNotNull(executor);
        assertEquals(TEST_THREAD_COUNT_DOUBLE, executor.getCorePoolSize());
        assertEquals(TEST_THREAD_COUNT_SIXTEEN, executor.getMaxPoolSize()); // 8 * 2
        assertEquals(TEST_QUEUE_CAPACITY, executor.getQueueCapacity());
        assertEquals("LLM-", executor.getThreadNamePrefix());
    }

    @Test
    void testLlmExecutorWithSingleThread() {
        // Given
        when(analysisSettings.maxThreads()).thenReturn(1);

        // When
        ThreadPoolTaskExecutor executor = appConfig.llmExecutor();

        // Then
        assertNotNull(executor);
        assertEquals(1, executor.getCorePoolSize());
        assertEquals(2, executor.getMaxPoolSize()); // 1 * 2
        assertEquals(TEST_QUEUE_CAPACITY, executor.getQueueCapacity());
    }

    @Test
    void testGetAsyncExecutor() {
        // When
        Executor asyncExecutor = appConfig.getAsyncExecutor();

        // Then
        assertNotNull(asyncExecutor);
        assertTrue(asyncExecutor instanceof ThreadPoolTaskExecutor);

        ThreadPoolTaskExecutor taskExecutor = (ThreadPoolTaskExecutor) asyncExecutor;
        assertEquals(TEST_THREAD_COUNT, taskExecutor.getCorePoolSize());
        assertEquals("LLM-", taskExecutor.getThreadNamePrefix());
    }

    @Test
    void testMultipleWebClientCalls() {
        // When
        WebClient webClient1 = appConfig.webClient();
        WebClient webClient2 = appConfig.webClient();

        // Then
        assertNotNull(webClient1);
        assertNotNull(webClient2);
        // Should create new instances each time (not singleton)
        assertNotSame(webClient1, webClient2);
    }

    @Test
    void testMultipleLlmExecutorCalls() {
        // When
        ThreadPoolTaskExecutor executor1 = appConfig.llmExecutor();
        ThreadPoolTaskExecutor executor2 = appConfig.llmExecutor();

        // Then
        assertNotNull(executor1);
        assertNotNull(executor2);
        // Should create new instances each time (not singleton)
        assertNotSame(executor1, executor2);
    }

    @Test
    void testAppConfigWithCompleteDocumentorConfig() {
        // Given
        List<LlmModelConfig> llmModels = List.of(
            new LlmModelConfig("gpt-4", "openai", null, "key1", null, null)
        );
        OutputSettings outputSettings = new OutputSettings(
            "docs", "markdown", false, false
        );
        // Set maxThreads to 6 to match expected values
        AnalysisSettings analysisSettings = new AnalysisSettings(
            false, TEST_THREAD_COUNT_SIX, List.of("**/*.java"), List.of()
        );
        DocumentorConfig realConfig = new DocumentorConfig(llmModels, outputSettings, analysisSettings);

        // When
        AppConfig config = new AppConfig(realConfig);
        ThreadPoolTaskExecutor executor = config.llmExecutor();

        // Then
        assertNotNull(config);
        assertNotNull(executor);
        assertEquals(TEST_THREAD_COUNT_SIX, executor.getCorePoolSize());
        assertEquals(TEST_THREAD_COUNT_TWELVE, executor.getMaxPoolSize()); // 6 * 2
    }

    @Test
    void testWebClientConfiguration() {
        // When
        WebClient webClient = appConfig.webClient();

        // Then
        assertNotNull(webClient);
        // WebClient is configured and ready to use
        // Memory size is configured to 10MB (10 * 1024 * 1024 bytes)
    }

    @Test
    void testLlmExecutorInitialization() {
        // When
        ThreadPoolTaskExecutor executor = appConfig.llmExecutor();

        // Then
        assertNotNull(executor);
        // Executor should be initialized and ready
        assertNotNull(executor.getThreadPoolExecutor());
    }

    @Test
    void testConfigurationAsAsyncConfigurer() {
        // Given
        AppConfig config = new AppConfig(documentorConfig);

        // When
        Executor executor = config.getAsyncExecutor();

        // Then
        assertNotNull(executor);
        assertTrue(executor instanceof ThreadPoolTaskExecutor);
    }

    @Test
    void testDefaultConstants() {
        // Given - Using reflection or testing through behavior
        when(analysisSettings.maxThreads()).thenReturn(2);

        // When
        ThreadPoolTaskExecutor executor = appConfig.llmExecutor();

        // Then
        // DEFAULT_THREAD_MULTIPLIER = 2
        assertEquals(TEST_THREAD_COUNT, executor.getMaxPoolSize()); // 2 * 2

        // DEFAULT_QUEUE_CAPACITY = 100
        assertEquals(TEST_QUEUE_CAPACITY, executor.getQueueCapacity());

        // DEFAULT_TERMINATION_TIMEOUT_SECONDS = 60 (cannot be tested directly via getter)

        // Thread name prefix
        assertEquals("LLM-", executor.getThreadNamePrefix());
    }

    @Test
    void testWithHighThreadCount() {
        // Given
        when(analysisSettings.maxThreads()).thenReturn(TEST_THREAD_COUNT_SIXTEEN);

        // When
        ThreadPoolTaskExecutor executor = appConfig.llmExecutor();

        // Then
        assertEquals(TEST_THREAD_COUNT_SIXTEEN, executor.getCorePoolSize());
        assertEquals(TEST_THREAD_COUNT_THIRTY_TWO, executor.getMaxPoolSize()); // 16 * 2
        assertEquals(TEST_QUEUE_CAPACITY, executor.getQueueCapacity());
    }

    @Test
    void testExecutorShutdownConfiguration() {
        // When
        ThreadPoolTaskExecutor executor = appConfig.llmExecutor();

        // Then
        // Executor shutdown configuration is set but cannot be directly tested
        assertNotNull(executor.getThreadPoolExecutor());
    }
}
