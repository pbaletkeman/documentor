package com.documentor.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskDecorator;

/**
 * Enhanced Thread Local Task Decorator with improved error handling
 *
 * Responsible for propagating ThreadLocal values from parent threads to child
 * threads in async operations. This ensures that configuration data is properly
 * available across thread boundaries. This enhanced version adds additional
 * null checks and error handling to ensure robustness.
 *
 * Uses ThreadLocalContextHolder for centralized ThreadLocal value management.
 */
public final class ThreadLocalTaskDecoratorEnhanced
        implements TaskDecorator {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(ThreadLocalTaskDecoratorEnhanced.class);

    /**
     * Decorates the given runnable with ThreadLocal propagation and enhanced
     * error handling.
     *
     * @param runnable the runnable to decorate
     * @return the decorated runnable
     */
    @Override
    public Runnable decorate(final Runnable runnable) {
        if (runnable == null) {
            LOGGER.error("Null runnable passed to "
                    + "ThreadLocalTaskDecoratorEnhanced"
                    + " - returning empty runnable");
            return () -> { /* Do nothing for null runnable */ };
        }

        // Capture the config from the parent thread
        DocumentorConfig capturedConfig = ThreadLocalContextHolder.getConfig();
        boolean wasExplicitlySet =
                ThreadLocalContextHolder.isConfigExplicitlySet();

        if (capturedConfig != null) {
            int modelCount =
                    capturedConfig.llmModels() != null
                            ? capturedConfig.llmModels().size() : 0;
            LOGGER.info("Captured ThreadLocal config from parent thread with {}"
                    + " models (explicitly set: {})", modelCount,
                    wasExplicitlySet);
        } else {
            LOGGER.warn("No ThreadLocal config available in parent thread"
                    + " - service may not work correctly");
        }

        // Return a wrapped Runnable that sets up the ThreadLocal in the child
        // thread with error handling
        return () -> {
            try {
                // Set the config in the child thread before execution
                if (capturedConfig != null) {
                    ThreadLocalContextHolder.setConfig(capturedConfig);
                    int modelCount =
                            capturedConfig.llmModels() != null
                                    ? capturedConfig.llmModels().size() : 0;
                    LOGGER.info("Set ThreadLocal config in child thread with {}"
                            + " models", modelCount);
                } else {
                    LOGGER.warn("Could not set ThreadLocal config in child"
                            + " thread - null config");
                }

                // Execute the original task with error handling
                try {
                    runnable.run();
                } catch (Exception e) {
                    LOGGER.error("Error executing task in child thread: {}",
                            e.getMessage(), e);
                }
            } catch (Exception e) {
                LOGGER.error("Error in ThreadLocal propagation: {}",
                        e.getMessage(), e);
            } finally {
                // Clean up ThreadLocal to prevent memory leaks
                try {
                    ThreadLocalContextHolder.clearConfig();
                    LOGGER.debug("Cleaned up ThreadLocal config in child"
                            + " thread");
                } catch (Exception e) {
                    LOGGER.error("Error cleaning up ThreadLocal: {}",
                            e.getMessage());
                }
            }
        };
    }
}
