package com.documentor.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Thread Local Context Holder
 *
 * A utility class for managing thread-local values across the application.
 * This centralizes all ThreadLocal access and provides diagnostic capabilities.
 */
public final class ThreadLocalContextHolder {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(ThreadLocalContextHolder.class);

    // Store the DocumentorConfig in a thread-local variable
    private static final ThreadLocal<DocumentorConfig> CONFIG_THREAD_LOCAL =
            new ThreadLocal<>();

    // Track whether this thread's config was explicitly set
    private static final ThreadLocal<Boolean> CONFIG_EXPLICITLY_SET =
            new ThreadLocal<>();

    private ThreadLocalContextHolder() {
        // Private constructor to prevent instantiation
    }

    /**
     * Sets the DocumentorConfig in the current thread's context
     *
     * @param config The configuration to set
     */
    public static void setConfig(final DocumentorConfig config) {
        if (config == null) {
            LOGGER.warn("Attempted to set null configuration in ThreadLocalContextHolder");
            return;
        }

        Thread currentThread = Thread.currentThread();
        int modelCount = config.llmModels() != null ? config.llmModels().size() : 0;

        LOGGER.debug("Setting ThreadLocal config in thread [{}] with {} models",
            currentThread.getName(), modelCount);

        CONFIG_THREAD_LOCAL.set(config);
        CONFIG_EXPLICITLY_SET.set(Boolean.TRUE);
    }

    /**
     * Gets the DocumentorConfig from the current thread's context
     *
     * @return The configuration, or null if not set
     */
    public static DocumentorConfig getConfig() {
        DocumentorConfig config = CONFIG_THREAD_LOCAL.get();

        if (config == null) {
            LOGGER.debug("ThreadLocal config is not available in thread [{}]",
                Thread.currentThread().getName());
        }

        return config;
    }

    /**
     * Clears the DocumentorConfig from the current thread's context
     */
    public static void clearConfig() {
        Thread currentThread = Thread.currentThread();
        Boolean wasExplicitlySet = CONFIG_EXPLICITLY_SET.get();

        LOGGER.debug("Clearing ThreadLocal config in thread [{}] (explicitly set: {})",
            currentThread.getName(), wasExplicitlySet);

        CONFIG_THREAD_LOCAL.remove();
        CONFIG_EXPLICITLY_SET.remove();
    }

    /**
     * Checks if the configuration was explicitly set in this thread
     *
     * @return true if the config was explicitly set, false otherwise
     */
    public static boolean isConfigExplicitlySet() {
        Boolean result = CONFIG_EXPLICITLY_SET.get();
        return result != null && result;
    }

    /**
     * Runs the provided runnable with the specified config set in the thread context
     *
     * @param config The configuration to set
     * @param runnable The runnable to execute
     */
    public static void runWithConfig(final DocumentorConfig config, final Runnable runnable) {
        if (config == null || runnable == null) {
            LOGGER.warn("Cannot run with null config or null runnable");
            return;
        }

        // Capture current config to restore it later
        DocumentorConfig originalConfig = getConfig();
        Boolean originallySet = CONFIG_EXPLICITLY_SET.get();

        try {
            // Set new config
            setConfig(config);

            // Run the provided code
            runnable.run();
        } catch (Exception e) {
            LOGGER.error("Error while running with config: {}", e.getMessage(), e);
            throw e; // Rethrow to allow caller to handle
        } finally {
            // Restore original config state
            if (originalConfig != null) {
                CONFIG_THREAD_LOCAL.set(originalConfig);
                CONFIG_EXPLICITLY_SET.set(originallySet);
                LOGGER.debug("Restored previous ThreadLocal config in thread [{}]",
                    Thread.currentThread().getName());
            } else {
                clearConfig();
            }
        }
    }

    /**
     * Helper method to log the current thread's configuration status for diagnostics
     */
    public static void logConfigStatus() {
        Thread currentThread = Thread.currentThread();
        DocumentorConfig config = CONFIG_THREAD_LOCAL.get();
        Boolean explicitlySet = CONFIG_EXPLICITLY_SET.get();

        if (config != null) {
            int modelCount = config.llmModels() != null ? config.llmModels().size() : 0;
            LOGGER.info("Thread [{}] has config with {} models (explicitly set: {})",
                currentThread.getName(), modelCount, explicitlySet);
        } else {
            LOGGER.info("Thread [{}] has no config set", currentThread.getName());
        }
    }
}
