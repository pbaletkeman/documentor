package com.documentor.config;

import com.documentor.service.LlmService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;

/**
 * ThreadLocal Propagating Executor
 *
 * Special executor that ensures ThreadLocal values are properly propagated from parent
 * threads to child threads in asynchronous operations. This is particularly important
 * for CompletableFuture chains and other async operations that span multiple threads.
 */
public final class ThreadLocalPropagatingExecutor implements Executor {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(ThreadLocalPropagatingExecutor.class);

    /**
     * Default number of threads for the executor.
     */
    public static final int DEFAULT_THREAD_COUNT = 5;

    /**
     * Default timeout in seconds for executor operations.
     */
    public static final int DEFAULT_TIMEOUT_SECONDS = 5;

    private final Executor delegate;

    public ThreadLocalPropagatingExecutor(final Executor delegateExecutor) {
        this.delegate = delegateExecutor;
    }

    /**
     * Executes the given command in a thread with ThreadLocal values propagated
     * from the current thread.
     *
     * @param command the runnable task to execute
     */
    @Override
    public void execute(final Runnable command) {
        // Capture the ThreadLocal config from the current thread
        DocumentorConfig capturedConfig = LlmService.getThreadLocalConfig();

        if (capturedConfig != null) {
            LOGGER.debug("Captured ThreadLocal config from parent thread with {} models",
                capturedConfig.llmModels().size());
        } else {
            LOGGER.warn("No ThreadLocal config available in parent thread - service may not work correctly");
        }

        // Create a wrapper that sets the ThreadLocal in the new thread
        Runnable contextAwareRunnable = () -> {
            // Set the ThreadLocal in the new thread
            try {
                if (capturedConfig != null) {
                    LlmService.setThreadLocalConfig(capturedConfig);
                    LOGGER.debug("Set ThreadLocal config in child thread with {} models",
                        capturedConfig.llmModels().size());
                }

                // Execute the original task
                command.run();
            } finally {
                // Clean up ThreadLocal to prevent memory leaks
                LlmService.clearThreadLocalConfig();
                LOGGER.debug("Cleaned up ThreadLocal config in child thread");
            }
        };

        // Execute the wrapped task using the delegate executor
        delegate.execute(contextAwareRunnable);
    }

    /**
     * Creates and returns an instance of ThreadLocalPropagatingExecutor
     * wrapped around a ThreadPoolExecutor for thread-local propagation
     *
     * @param threads Number of threads to use in the pool
     * @param namePrefix Prefix for naming the threads
     * @return An Executor that propagates ThreadLocal values
     */
    public static Executor createExecutor(final int threads, final String namePrefix) {
        // Thread counter for naming
        final java.util.concurrent.atomic.AtomicInteger counter =
                new java.util.concurrent.atomic.AtomicInteger();

        // Max queue size
        final int maxQueueSize = 100;

        // Thread keep-alive seconds
        final long keepAliveSeconds = 60L;

        return new ThreadLocalPropagatingExecutor(
                new java.util.concurrent.ThreadPoolExecutor(
                        threads, // core pool size
                        threads * 2, // max pool size
                        keepAliveSeconds, // keep alive time
                        java.util.concurrent.TimeUnit.SECONDS,
                        new java.util.concurrent.LinkedBlockingQueue<>(maxQueueSize), // queue with capacity limit
                        r -> { // thread factory
                            Thread thread = new Thread(r);
                            thread.setName(namePrefix + "-" + counter.incrementAndGet());
                            thread.setDaemon(true);
                            return thread;
                        }
                )
        );
    }
}
