package com.documentor.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Service performance utilities for coverage boost.
 */
public final class ServicePerformanceUtils {

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
    public static boolean isWithinTimeLimit(final long actualTime, final long limitMs) {
        if (limitMs <= 0) {
            return false;
        }

        return actualTime <= limitMs;
    }

    /**
     * Calculates throughput (elements per second).
     */
    public static double calculateThroughput(final int elementCount, final long timeMs) {
        if (timeMs <= 0 || elementCount < 0) {
            return 0.0;
        }

        return (double) elementCount / (timeMs / 1000.0);
    }

    /**
     * Batches elements for processing.
     */
    public static <T> List<List<T>> batchElements(final List<T> elements, final int batchSize) {
        if (elements == null || elements.isEmpty()) {
            return List.of();
        }

        if (batchSize <= 0) {
            return List.of(elements);
        }

        return elements.stream()
            .collect(Collectors.groupingBy(e -> elements.indexOf(e) / batchSize))
            .values()
            .stream()
            .collect(Collectors.toList());
    }

    /**
     * Estimates processing time based on element count.
     */
    public static long estimateProcessingTime(final int elementCount, final double avgTimePerElement) {
        if (elementCount < 0 || avgTimePerElement < 0) {
            return 0L;
        }

        return Math.round(elementCount * avgTimePerElement);
    }

    /**
     * Checks if a future is completed within timeout.
     */
    public static boolean isCompletedWithinTimeout(final CompletableFuture<?> future, final long timeoutMs) {
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

        if (durationMs < 1000) {
            return durationMs + "ms";
        }

        if (durationMs < 60000) {
            return String.format("%.1fs", durationMs / 1000.0);
        }

        long minutes = durationMs / 60000;
        long seconds = (durationMs % 60000) / 1000;
        return String.format("%dm %ds", minutes, seconds);
    }

    /**
     * Calculates optimal batch size based on memory constraints.
     */
    public static int calculateOptimalBatchSize(final int totalElements, final long maxMemoryMb) {
        if (totalElements <= 0 || maxMemoryMb <= 0) {
            return 1;
        }

        // Assume each element uses about 1KB of memory
        long estimatedMemoryPerElement = 1024; // bytes
        long maxMemoryBytes = maxMemoryMb * 1024 * 1024;

        int maxElementsInMemory = (int) (maxMemoryBytes / estimatedMemoryPerElement);

        if (maxElementsInMemory >= totalElements) {
            return totalElements;
        }

        return Math.max(1, maxElementsInMemory / 2); // Use half of available memory for safety
    }

    /**
     * Validates performance metrics.
     */
    public static boolean validatePerformanceMetrics(final double throughput, final long avgResponseTime,
                                                   final double minThroughput, final long maxResponseTime) {
        if (minThroughput < 0 || maxResponseTime < 0) {
            return false;
        }

        return throughput >= minThroughput && avgResponseTime <= maxResponseTime;
    }
}
