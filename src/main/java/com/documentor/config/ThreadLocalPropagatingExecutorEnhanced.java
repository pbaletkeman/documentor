package com.documentor.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Enhanced ThreadLocal Propagating Executor with improved error handling
 *
 * Special executor that ensures ThreadLocal values are properly propagated from parent
 * threads to child threads in asynchronous operations. This is particularly important
 * for CompletableFuture chains and other async operations that span multiple threads.
 *
 * Uses ThreadLocalContextHolder for centralized management of thread-local values.
 */
public final class ThreadLocalPropagatingExecutorEnhanced implements Executor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThreadLocalPropagatingExecutorEnhanced.class);

    /**
     * Default number of threads for the executor.
     */
    public static final int DEFAULT_THREAD_COUNT = 5;

    /**
     * Default timeout in seconds for executor operations.
     */
    public static final int DEFAULT_TIMEOUT_SECONDS = 30;

    /**
     * Fallback executor when delegate is null
     */
    private static final Executor FALLBACK_EXECUTOR = ForkJoinPool.commonPool();

    private final Executor delegate;
    private final String executorName;

    public ThreadLocalPropagatingExecutorEnhanced(final Executor delegateExecutor, final String name) {
        this.delegate = delegateExecutor != null ? delegateExecutor : FALLBACK_EXECUTOR;
        this.executorName = name != null ? name : "unnamed";

        if (delegateExecutor == null) {
            LOGGER.warn("Delegate executor was null for '{}', using ForkJoinPool.commonPool() as fallback",
                this.executorName);
        }
    }

    /**
     * Executes the given command in a thread with ThreadLocal values propagated
     * from the current thread.
     *
     * @param command the runnable task to execute
     */
    @Override
    public void execute(final Runnable command) {
        if (command == null) {
            LOGGER.error("Null command passed to execute() in '{}' - ignoring", executorName);
            return;
        }

        // Capture the ThreadLocal config from the current thread
        DocumentorConfig capturedConfig = ThreadLocalContextHolder.getConfig();
        boolean wasExplicitlySet = ThreadLocalContextHolder.isConfigExplicitlySet();

        if (capturedConfig != null) {
            LOGGER.debug("[{}] Captured ThreadLocal config from parent thread with {} models",
                executorName,
                capturedConfig.llmModels() != null ? capturedConfig.llmModels().size() : 0);
        } else {
            LOGGER.warn("[{}] No ThreadLocal config available in parent thread - service may not work correctly",
                executorName);
        }

        // Create a wrapper that sets the ThreadLocal in the new thread
        Runnable contextAwareRunnable = () -> {
            // Set the ThreadLocal in the new thread
            try {
                if (capturedConfig != null) {
                    ThreadLocalContextHolder.setConfig(capturedConfig);

                    if (wasExplicitlySet) {
                        LOGGER.debug("[{}] Set ThreadLocal config in child thread with {} models (explicitly set)",
                            executorName,
                            capturedConfig.llmModels() != null ? capturedConfig.llmModels().size() : 0);
                    }
                }

                // Execute the original task
                command.run();
            } catch (Exception e) {
                LOGGER.error("[{}] Error in thread execution: {}", executorName, e.getMessage(), e);
            } finally {
                // Clean up ThreadLocal to prevent memory leaks
                ThreadLocalContextHolder.clearConfig();
                LOGGER.debug("[{}] Cleaned up ThreadLocal config in child thread", executorName);
            }
        };

        try {
            // Execute the wrapped task using the delegate executor
            delegate.execute(contextAwareRunnable);
        } catch (Exception e) {
            LOGGER.error("[{}] Failed to execute task with delegate executor: {}", executorName, e.getMessage(), e);

            // Try with fallback executor
            try {
                LOGGER.info("[{}] Attempting to execute with fallback executor", executorName);
                FALLBACK_EXECUTOR.execute(contextAwareRunnable);
            } catch (Exception fallbackEx) {
                LOGGER.error("[{}] Fallback executor also failed: {}", executorName,
                        fallbackEx.getMessage(), fallbackEx);
                // Execute directly in the current thread as last resort
                LOGGER.warn("[{}] Executing task in current thread as last resort", executorName);
                contextAwareRunnable.run();
            }
        }
    }

    /**
     * Creates and returns an instance of ThreadLocalPropagatingExecutorEnhanced
     * wrapped around a ThreadPoolExecutor for thread-local propagation
     *
     * @param threads Number of threads to use in the pool
     * @param namePrefix Prefix for naming the threads
     * @return An Executor that propagates ThreadLocal values or null if creation fails
     */
    public static Executor createExecutor(final int threads, final String namePrefix) {
        // Thread counter for naming
        final AtomicInteger counter = new AtomicInteger();

        // Max queue size
        final int maxQueueSize = 100;

        // Thread keep-alive seconds
        final long keepAliveSeconds = 60L;

        try {
            ThreadFactory threadFactory = r -> {
                Thread thread = new Thread(r);
                thread.setName(namePrefix + "-" + counter.incrementAndGet());
                thread.setDaemon(true);

                // Add an uncaught exception handler to prevent thread death on uncaught exceptions
                thread.setUncaughtExceptionHandler((t, e) ->
                    LOGGER.error("Uncaught exception in thread {}: {}", t.getName(), e.getMessage(), e)
                );

                return thread;
            };

            ThreadPoolExecutor executor = new ThreadPoolExecutor(
                threads,                   // core pool size
                threads * 2,               // max pool size
                keepAliveSeconds,          // keep alive time
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(maxQueueSize), // queue with capacity limit
                threadFactory
            );

            // Set rejection handler to log and use caller thread as fallback
            executor.setRejectedExecutionHandler((r, e) -> {
                LOGGER.warn("Task rejected from executor {} - running in caller thread", namePrefix);
                if (r != null) {
                    r.run();
                }
            });

            return new ThreadLocalPropagatingExecutorEnhanced(executor, namePrefix);
        } catch (Exception e) {
            LOGGER.error("Failed to create thread pool executor {}: {}", namePrefix, e.getMessage(), e);
            // Return a wrapped version of the fallback executor
            return new ThreadLocalPropagatingExecutorEnhanced(FALLBACK_EXECUTOR, namePrefix + "-fallback");
        }
    }
}
