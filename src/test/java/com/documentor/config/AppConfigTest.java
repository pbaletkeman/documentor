package com.documentor.config;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
 * 🧪 Comprehensive tests for AppConfig
 * 
 * Tests Spring configuration bean creation and async executor setup
 */
@ExtendWith(MockitoExtension.class)
class AppConfigTest {

    @Mock
    private DocumentorConfig documentorConfig;

    @Mock
    private AnalysisSettings analysisSettings;

    private AppConfig appConfig;

    @BeforeEach
    void setUp() {
        lenient().when(documentorConfig.analysisSettings()).thenReturn(analysisSettings);
        lenient().when(analysisSettings.maxThreads()).thenReturn(4);
        
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
        assertEquals(4, executor.getCorePoolSize());
        assertEquals(8, executor.getMaxPoolSize()); // 4 * 2 (DEFAULT_THREAD_MULTIPLIER)
        assertEquals(100, executor.getQueueCapacity()); // DEFAULT_QUEUE_CAPACITY
        assertEquals("LLM-", executor.getThreadNamePrefix());
        // Note: Cannot directly test these private fields, but we can verify the executor is properly configured
        assertNotNull(executor.getThreadPoolExecutor());
    }

    @Test
    void testLlmExecutorWithDifferentMaxThreads() {
        // Given
        when(analysisSettings.maxThreads()).thenReturn(8);

        // When
        ThreadPoolTaskExecutor executor = appConfig.llmExecutor();

        // Then
        assertNotNull(executor);
        assertEquals(8, executor.getCorePoolSize());
        assertEquals(16, executor.getMaxPoolSize()); // 8 * 2
        assertEquals(100, executor.getQueueCapacity());
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
        assertEquals(100, executor.getQueueCapacity());
    }

    @Test
    void testGetAsyncExecutor() {
        // When
        Executor asyncExecutor = appConfig.getAsyncExecutor();

        // Then
        assertNotNull(asyncExecutor);
        assertTrue(asyncExecutor instanceof ThreadPoolTaskExecutor);
        
        ThreadPoolTaskExecutor threadPoolExecutor = (ThreadPoolTaskExecutor) asyncExecutor;
        assertEquals(4, threadPoolExecutor.getCorePoolSize());
        assertEquals("LLM-", threadPoolExecutor.getThreadNamePrefix());
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
            false, 6, List.of("**/*.java"), List.of()
        );
        DocumentorConfig realConfig = new DocumentorConfig(llmModels, outputSettings, analysisSettings);

        // When
        AppConfig config = new AppConfig(realConfig);
        ThreadPoolTaskExecutor executor = config.llmExecutor();

        // Then
        assertNotNull(config);
        assertNotNull(executor);
        assertEquals(6, executor.getCorePoolSize());
        assertEquals(12, executor.getMaxPoolSize()); // 6 * 2
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
        assertEquals(4, executor.getMaxPoolSize()); // 2 * 2

        // DEFAULT_QUEUE_CAPACITY = 100
        assertEquals(100, executor.getQueueCapacity());

        // DEFAULT_TERMINATION_TIMEOUT_SECONDS = 60 (cannot be tested directly via getter)

        // Thread name prefix
        assertEquals("LLM-", executor.getThreadNamePrefix());
    }

    @Test
    void testWithHighThreadCount() {
        // Given
        when(analysisSettings.maxThreads()).thenReturn(16);

        // When
        ThreadPoolTaskExecutor executor = appConfig.llmExecutor();

        // Then
        assertEquals(16, executor.getCorePoolSize());
        assertEquals(32, executor.getMaxPoolSize()); // 16 * 2
        assertEquals(100, executor.getQueueCapacity());
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
