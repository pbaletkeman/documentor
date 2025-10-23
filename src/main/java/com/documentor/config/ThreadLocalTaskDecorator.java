package com.documentor.config;

import com.documentor.service.LlmService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskDecorator;

/**
 * Thread Local Task Decorator
 *
 * Responsible for propagating ThreadLocal values from parent threads to
 * child threads in async operations. This ensures that configuration data
 * is properly available across thread boundaries.
 */
public final class ThreadLocalTaskDecorator implements TaskDecorator {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(ThreadLocalTaskDecorator.class);

    /**
     * Decorates the given runnable with ThreadLocal propagation.
     *
     * @param runnable the runnable to decorate
     * @return the decorated runnable
     */
    @Override
    public Runnable decorate(final Runnable runnable) {
        // Capture the config from the parent thread
        DocumentorConfig capturedConfig = LlmService.getThreadLocalConfig();

        if (capturedConfig != null) {
            LOGGER.info("Captured ThreadLocal config from parent thread "
                    + "with {} models", capturedConfig.llmModels().size());
        } else {
            LOGGER.warn("No ThreadLocal config available in parent thread "
                    + "- service may not work correctly");
        }

        // Return a wrapped Runnable that sets up the ThreadLocal
        // in the child thread
        return () -> {
            try {
                // Set the config in the child thread before execution
                if (capturedConfig != null) {
                    LlmService.setThreadLocalConfig(capturedConfig);
                    LOGGER.info("Set ThreadLocal config in child thread "
                            + "with {} models",
                            capturedConfig.llmModels().size());
                } else {
                    LOGGER.warn("Could not set ThreadLocal config in child "
                            + "thread - null config");
                }

                // Execute the original task
                runnable.run();
            } finally {
                // Clean up ThreadLocal to prevent memory leaks
                LlmService.clearThreadLocalConfig();
                LOGGER.debug("Cleaned up ThreadLocal config in child thread");
            }
        };
    }
}
