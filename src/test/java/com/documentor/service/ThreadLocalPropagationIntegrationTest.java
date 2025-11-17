package com.documentor.service;

import com.documentor.config.DocumentorConfig;
import com.documentor.config.ThreadLocalPropagatingExecutor;
import com.documentor.config.model.AnalysisSettings;
import com.documentor.config.model.LlmModelConfig;
import com.documentor.config.model.OutputSettings;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.documentor.config.TestConfig;
import com.documentor.DocumentorTestApplication;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/**
 * Integration test specifically focused on verifying that ThreadLocal values
 * are properly propagated from parent threads to child threads in asynchronous
 * operations.
 */
@SpringBootTest(classes = DocumentorTestApplication.class)
@ActiveProfiles("test")
@Import(TestConfig.class)
public class ThreadLocalPropagationIntegrationTest {

    private static final int MAX_DEPTH = 3;
    private static final int TIMEOUT_SECONDS = 60;
    private static final int MAX_TOKENS = 2048;
    private static final Logger LOGGER = LoggerFactory.getLogger(
            ThreadLocalPropagationIntegrationTest.class);

    /**
     * Test that verifies ThreadLocal propagation works correctly with our
     * custom executor.
     */
    @Test
    public void testThreadLocalPropagation() throws Exception {
        // Create a test configuration
        LlmModelConfig testModel = new LlmModelConfig(
            "test-model",
            "test-provider",
            "http://localhost:8080",
            "test-api-key",
            MAX_TOKENS,
            TIMEOUT_SECONDS);

        OutputSettings outputSettings = new OutputSettings(
            "./output",
            "markdown",
            true,
            true,
            true
        );

        AnalysisSettings analysisSettings = new AnalysisSettings(
            true,
            MAX_DEPTH,
            List.of("**/*.java"),
            List.of("**/test/**")
        );

        DocumentorConfig testConfig = new DocumentorConfig(
                List.of(testModel), outputSettings, analysisSettings);

        // Set the config in the main thread
        LlmService.setThreadLocalConfig(testConfig);

        // Create our propagating executor
        Executor executor = ThreadLocalPropagatingExecutor.createExecutor(
                2, "test-thread");

        // Latch to wait for async operation to complete
        CountDownLatch latch = new CountDownLatch(1);

        // Flag to check if config was available in child thread
        AtomicBoolean configAvailableInChildThread = new AtomicBoolean(false);

        // Execute a task that checks if the config is available
        executor.execute(() -> {
            try {
                LOGGER.info("Child thread running...");

                // Check if ThreadLocal config was propagated to this thread
                DocumentorConfig propagatedConfig =
                        LlmService.getThreadLocalConfig();

                if (propagatedConfig != null) {
                    LOGGER.info(
                            "ThreadLocal config available in child thread "
                            + "with {} models",
                            propagatedConfig.llmModels().size());

                    // Verify it's the same config
                    if (propagatedConfig.llmModels().size() == 1
                            && "test-model".equals(
                                    propagatedConfig
                                    .llmModels()
                                    .get(0)
                                    .name())) {
                        configAvailableInChildThread.set(true);
                    }
                } else {
                    LOGGER.error(
                            "ThreadLocal config NOT available in child thread");
                }
            } finally {
                latch.countDown();
            }
        });

        // Wait for the async task to complete
        latch.await();

        // Cleanup
        LlmService.clearThreadLocalConfig();

        // Assert that config was successfully propagated
        assertTrue(configAvailableInChildThread.get(),
                "ThreadLocal config should be available in the child thread");
    }

    /**
     * Test that our LlmServiceFix correctly sets ThreadLocal config
     */
    @Test
    public void testLlmServiceFix() {
        // Create a test configuration
        LlmModelConfig testModel = new LlmModelConfig(
            "test-model-fix",
            "test-provider-fix",
            "http://localhost:8080",
            "test-api-key",
            MAX_TOKENS,
            TIMEOUT_SECONDS);

        OutputSettings outputSettings = new OutputSettings(
            "./output",
            "markdown",
            true,
            true,
            true
        );

        AnalysisSettings analysisSettings = new AnalysisSettings(
            true,
            MAX_DEPTH,
            List.of("**/*.java"),
            List.of("**/test/**")
        );

        DocumentorConfig testConfig = new DocumentorConfig(
                List.of(testModel), outputSettings, analysisSettings);

        // Create our service fix
        LlmServiceFix serviceFix = new LlmServiceFix();

        // Use the fix to set the config
        serviceFix.setLlmServiceThreadLocalConfig(testConfig);

        // Verify the config was set correctly
        DocumentorConfig retrievedConfig = LlmService.getThreadLocalConfig();
        assertTrue(retrievedConfig != null,
                "Config should not be null after setting with LlmServiceFix");
        assertTrue(retrievedConfig.llmModels().size() == 1,
                "Config should have one model");
        assertTrue("test-model-fix".equals(
                        retrievedConfig.llmModels().get(0).name()),
                "Config should contain the correct model name");

        // Cleanup
        LlmService.clearThreadLocalConfig();
    }
}
