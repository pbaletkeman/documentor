package com.documentor.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.documentor.config.DocumentorConfig;
import com.documentor.config.model.LlmModelConfig;

/**
 * This class provides a utility method to directly set the LlmService's ThreadLocal configuration.
 * It's designed to work around threading issues where the ThreadLocal value is not being properly
 * propagated to worker threads.
 */
@Service
public class LlmServiceFix {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(LlmServiceFix.class);

    /**
     * Directly set the ThreadLocal configuration in the LlmService class.
     * Call this method before running any LLM operations to ensure the configuration
     * is available to all threads.
     *
     * @param config The DocumentorConfig to set
     */
    public void setLlmServiceThreadLocalConfig(final DocumentorConfig config) {
        if (config == null) {
            LOGGER.warn("Attempted to set null configuration in LlmServiceFix");
            return;
        }

        // Log details about the current thread
        Thread currentThread = Thread.currentThread();
        LOGGER.info("Setting ThreadLocal config in thread [{}]",
                   currentThread.getName());

        // Log model details
        if (config.llmModels() != null && !config.llmModels().isEmpty()) {
            LOGGER.info("Setting global ThreadLocal config through LlmServiceFix with {} models",
                       config.llmModels().size());

            for (int i = 0; i < config.llmModels().size(); i++) {
                LlmModelConfig model = config.llmModels().get(i);
                LOGGER.debug("Model {}: name={}, provider={}, baseUrl={}",
                           i + 1, model.name(), model.provider(), model.baseUrl());
            }
        } else {
            LOGGER.warn("Setting ThreadLocal config, but the model list is empty or null");
        }

        // Directly set the ThreadLocal value in LlmService
        LlmService.setThreadLocalConfig(config);

        // Verify it was set correctly
        DocumentorConfig verifyConfig = LlmService.getThreadLocalConfig();
        if (verifyConfig != null) {
            LOGGER.info("Successfully set and verified ThreadLocal config with {} models",
                       verifyConfig.llmModels().size());

            // Log that the ThreadLocal config can be expected in child threads
            LOGGER.info("Child threads created by ThreadLocalPropagatingExecutor should now receive this config");
        } else {
            LOGGER.error("Failed to set ThreadLocal config - verification returned null");
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
        DocumentorConfig config = LlmService.getThreadLocalConfig();

        if (config != null) {
            LOGGER.info("ThreadLocal config IS available in thread [{}] with {} models",
                      currentThread.getName(),
                      config.llmModels() != null ? config.llmModels().size() : 0);
            return true;
        } else {
            LOGGER.warn("ThreadLocal config is NOT available in thread [{}]",
                      currentThread.getName());
            return false;
        }
    }
}
