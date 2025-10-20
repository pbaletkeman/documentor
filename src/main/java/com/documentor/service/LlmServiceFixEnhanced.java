package com.documentor.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.documentor.config.DocumentorConfig;
import com.documentor.config.ThreadLocalContextHolder;
import com.documentor.config.model.LlmModelConfig;

/**
 * Enhanced version of LlmServiceFix with improved error handling
 * that works with LlmServiceEnhanced.
 *
 * This class provides utility methods to directly set the ThreadLocalContextHolder's
 * configuration. It's designed to work around threading issues where the ThreadLocal value
 * is not being properly propagated to worker threads.
 */
@Service
public class LlmServiceFixEnhanced {

    private static final Logger LOGGER = LoggerFactory.getLogger(LlmServiceFixEnhanced.class);

    /**
     * Directly set the ThreadLocal configuration in the LlmServiceEnhanced class.
     * Call this method before running any LLM operations to ensure the configuration
     * is available to all threads.
     *
     * @param config The DocumentorConfig to set
     */
    public void setLlmServiceThreadLocalConfig(final DocumentorConfig config) {
        if (config == null) {
            LOGGER.warn("Attempted to set null configuration in LlmServiceFixEnhanced");
            return;
        }

        try {
            // Log details about the current thread
            Thread currentThread = Thread.currentThread();
            LOGGER.info("Setting ThreadLocal config in thread [{}]",
                    currentThread.getName());

            // Log model details
            if (config.llmModels() != null && !config.llmModels().isEmpty()) {
                LOGGER.info("Setting global ThreadLocal config through LlmServiceFixEnhanced with {} models",
                        config.llmModels().size());

                for (int i = 0; i < config.llmModels().size(); i++) {
                    LlmModelConfig model = config.llmModels().get(i);
                    LOGGER.debug("Model {}: name={}, provider={}, baseUrl={}",
                            i + 1, model.name(), model.provider(), model.baseUrl());
                }
            } else {
                LOGGER.warn("Setting ThreadLocal config, but the model list is empty or null");
            }

            // Directly set the ThreadLocal value in ThreadLocalContextHolder
            ThreadLocalContextHolder.setConfig(config);

            // Verify it was set correctly
            DocumentorConfig verifyConfig = ThreadLocalContextHolder.getConfig();
            if (verifyConfig != null) {
                int modelCount = verifyConfig.llmModels() != null ? verifyConfig.llmModels().size() : 0;
                LOGGER.info("Successfully set and verified ThreadLocal config with {} models",
                        modelCount);

                // Log that the ThreadLocal config can be expected in child threads
                LOGGER.info("Child threads created by ThreadLocalPropagatingExecutorEnhanced "
                        + "should now receive this config");
            } else {
                LOGGER.error("Failed to set ThreadLocal config - verification returned null");
            }
        } catch (Exception e) {
            LOGGER.error("Error setting ThreadLocal config: {}", e.getMessage(), e);
        }
    }

    /**
     * Diagnostic method to check if ThreadLocal config is available in the current thread.
     * Can be called from any thread to verify if the configuration is accessible.
     *
     * @return true if config is available, false otherwise
     */
    public boolean isThreadLocalConfigAvailable() {
        Thread currentThread = Thread.currentThread();
        try {
            DocumentorConfig config = ThreadLocalContextHolder.getConfig();

            if (config != null) {
                int modelCount = config.llmModels() != null ? config.llmModels().size() : 0;
                LOGGER.info("ThreadLocal config IS available in thread [{}] with {} models",
                        currentThread.getName(), modelCount);
                return true;
            } else {
                LOGGER.warn("ThreadLocal config is NOT available in thread [{}]",
                        currentThread.getName());
                return false;
            }
        } catch (Exception e) {
            LOGGER.error("Error checking ThreadLocal config availability: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Cleans up ThreadLocal resources to prevent memory leaks.
     * Call this method when done with the thread to ensure proper cleanup.
     */
    public void cleanupThreadLocalConfig() {
        try {
            Thread currentThread = Thread.currentThread();
            LOGGER.debug("Cleaning up ThreadLocal config in thread [{}]", currentThread.getName());
            ThreadLocalContextHolder.clearConfig();
        } catch (Exception e) {
            LOGGER.error("Error cleaning up ThreadLocal config: {}", e.getMessage(), e);
        }
    }

    /**
     * Executes the provided runnable with the specified config set in the thread context
     *
     * @param config The configuration to set
     * @param runnable The runnable to execute
     */
    public void executeWithConfig(final DocumentorConfig config, final Runnable runnable) {
        ThreadLocalContextHolder.runWithConfig(config, runnable);
    }
}
