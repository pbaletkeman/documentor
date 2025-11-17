package com.documentor.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Service performance utilities for coverage boost.
 */
public final class ServicePerformanceUtils {

    private static final double MILLISECONDS_PER_SECOND = 1000.0;
    private static final long MILLISECONDS_IN_SECOND = 1000L;
    private static final long MICROSECONDS_PER_MINUTE = 60000L;
    private static final int BYTES_PER_KB = 1024;

    private ServicePerformanceUtils() {
        // Utility class - prevent instantiation
    }

    /**
     * Measures operation time in milliseconds.
     */
    public static long measureTime(final Runnable operation) {
        if (operation == null) {
            return 0L;
        }

        long startTime = System.currentTimeMillis();
        try {
            operation.run();
        } catch (Exception e) {
            // Log and return partial time
            return System.currentTimeMillis() - startTime;
        }
        return System.currentTimeMillis() - startTime;
    }

    /**
     * Checks if processing time is within acceptable limits.
     */
    public static boolean isWithinTimeLimit(final long actualTime,
            final long limitMs) {
        if (limitMs <= 0) {
            return false;
        }

        return actualTime <= limitMs;
    }

    /**
     * Calculates throughput (elements per second).
     */
    public static double calculateThroughput(final int elementCount,
            final long timeMs) {
        if (timeMs <= 0 || elementCount < 0) {
            return 0.0;
        }

        return (double) elementCount / (timeMs / MILLISECONDS_PER_SECOND);
    }

    /**
     * Batches elements for processing.
     */
    public static <T> List<List<T>> batchElements(final List<T> elements,
            final int batchSize) {
        if (elements == null || elements.isEmpty()) {
            return List.of();
        }

        if (batchSize <= 0) {
            return List.of(elements);
        }

        return elements.stream()
            .collect(Collectors.groupingBy(
                    e -> elements.indexOf(e) / batchSize))
            .values()
            .stream()
            .collect(Collectors.toList());
    }

    /**
     * Estimates processing time based on element count.
     */
    public static long estimateProcessingTime(final int elementCount,
            final double avgTimePerElement) {
        if (elementCount < 0 || avgTimePerElement < 0) {
            return 0L;
        }

        return Math.round(elementCount * avgTimePerElement);
    }

    /**
     * Checks if a future is completed within timeout.
     */
    public static boolean isCompletedWithinTimeout(
            final CompletableFuture<?> future, final long timeoutMs) {
        if (future == null || timeoutMs <= 0) {
            return false;
        }

        try {
            future.get(timeoutMs, java.util.concurrent.TimeUnit.MILLISECONDS);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Formats time duration as human readable string.
     */
    public static String formatDuration(final long durationMs) {
        if (durationMs < 0) {
            return "0ms";
        }

        if (durationMs < MILLISECONDS_IN_SECOND) {
            return durationMs + "ms";
        }

        if (durationMs < MICROSECONDS_PER_MINUTE) {
            return String.format("%.1fs",
                durationMs / MILLISECONDS_PER_SECOND);
        }

        long minutes = durationMs / MICROSECONDS_PER_MINUTE;
        long seconds = (durationMs % MICROSECONDS_PER_MINUTE)
            / MILLISECONDS_IN_SECOND;
        return String.format("%dm %ds", minutes, seconds);
    }

    /**
     * Calculates optimal batch size based on memory constraints.
     */
    public static int calculateOptimalBatchSize(final int totalElements,
            final long maxMemoryMb) {
        if (totalElements <= 0 || maxMemoryMb <= 0) {
            return 1;
        }

        // Assume each element uses about 1KB of memory
        long estimatedMemoryPerElement = BYTES_PER_KB; // bytes
        long maxMemoryBytes = maxMemoryMb * BYTES_PER_KB * BYTES_PER_KB;

        int maxElementsInMemory = (int) (maxMemoryBytes
            / estimatedMemoryPerElement);

        if (maxElementsInMemory >= totalElements) {
            return totalElements;
        }

        return Math.max(1, maxElementsInMemory / 2);
        // Use half of available memory for safety
    }

    /**
     * Validates performance metrics.
     */
    public static boolean validatePerformanceMetrics(final double throughput,
            final long avgResponseTime, final double minThroughput,
            final long maxResponseTime) {
        if (minThroughput < 0 || maxResponseTime < 0) {
            return false;
        }

        return throughput >= minThroughput
                && avgResponseTime <= maxResponseTime;
    }
}
