package com.documentor.service;

/**
 * Service metrics utilities for final coverage boost.
 */
public final class ServiceMetricsUtils {

    private static final double HUNDRED_PERCENT = 100.0;

    private ServiceMetricsUtils() {
        // Utility class - prevent instantiation
    }

    /**
     * Calculates success rate percentage.
     */
    public static double calculateSuccessRate(final int successful, final int total) {
        if (total <= 0) {
            return 0.0;
        }

        if (successful < 0) {
            return 0.0;
        }

        if (successful > total) {
            return HUNDRED_PERCENT;
        }

        return (double) successful / total * HUNDRED_PERCENT;
    }

    /**
     * Formats success rate as percentage string.
     */
    public static String formatSuccessRate(final double successRate) {
        if (successRate < 0) {
            return "0.0%";
        }

        if (successRate > HUNDRED_PERCENT) {
            return "100.0%";
        }

        return String.format("%.1f%%", successRate);
    }

    /**
     * Checks if success rate meets minimum threshold.
     */
    public static boolean meetsSuccessThreshold(final double successRate, final double threshold) {
        if (threshold < 0 || threshold > HUNDRED_PERCENT) {
            return false;
        }

        return successRate >= threshold;
    }

    /**
     * Calculates error rate percentage.
     */
    public static double calculateErrorRate(final int errors, final int total) {
        if (total <= 0) {
            return 0.0;
        }

        if (errors < 0) {
            return 0.0;
        }

        if (errors > total) {
            return HUNDRED_PERCENT;
        }

        return (double) errors / total * HUNDRED_PERCENT;
    }

    /**
     * Validates service health based on metrics.
     */
    public static boolean isServiceHealthy(final double successRate, final double errorRate,
                                         final double minSuccess, final double maxError) {
        if (minSuccess < 0 || minSuccess > HUNDRED_PERCENT || maxError < 0 || maxError > HUNDRED_PERCENT) {
            return false;
        }

        return successRate >= minSuccess && errorRate <= maxError;
    }

    /**
     * Calculates availability percentage.
     */
    public static double calculateAvailability(final long uptimeMs, final long totalTimeMs) {
        if (totalTimeMs <= 0) {
            return 0.0;
        }

        if (uptimeMs < 0) {
            return 0.0;
        }

        if (uptimeMs > totalTimeMs) {
            return HUNDRED_PERCENT;
        }

        return (double) uptimeMs / totalTimeMs * HUNDRED_PERCENT;
    }

    /**
     * Formats metrics summary.
     */
    public static String formatMetricsSummary(final double successRate, final double errorRate,
                                            final double availability) {
        return String.format("Success: %.1f%%, Errors: %.1f%%, Availability: %.1f%%",
                           successRate, errorRate, availability);
    }
}
