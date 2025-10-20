package com.documentor.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ServiceMetricsUtils to reach final coverage boost.
 */
class ServiceMetricsUtilsTest {

    // Test constants for percentages and rates
    private static final double EIGHTY_PERCENT = 80.0;
    private static final double NINETY_PERCENT = 90.0;
    private static final double NINETY_FIVE_PERCENT = 95.0;
    private static final double NINETY_FIVE_POINT_FIVE = 95.5;
    private static final double NINETY_NINE_POINT_NINE = 99.9;
    private static final double HUNDRED_PERCENT = 100.0;
    private static final double ZERO_PERCENT = 0.0;
    private static final double EIGHTY_FIVE_PERCENT = 85.0;
    private static final double FOUR_POINT_FIVE = 4.5;
    private static final double FIVE_PERCENT = 5.0;
    private static final double TEN_PERCENT = 10.0;
    private static final double FIFTEEN_PERCENT = 15.0;
    private static final double TWENTY_PERCENT = 20.0;
    private static final double NEGATIVE_FIVE = -5.0;
    private static final double NEGATIVE_TEN = -10.0;
    private static final double ONE_FIFTY = 150.0;

    // Test constants for counts and numbers
    private static final int FIFTY = 50;
    private static final int EIGHTY = 80;
    private static final int ONE_HUNDRED = 100;
    private static final int NEGATIVE_ONE_HUNDRED = -100;
    private static final int FIVE_HUNDRED = 500;
    private static final int NINE_NINETY_NINE = 999;
    private static final int ONE_THOUSAND = 1000;
    private static final int FIFTEEN_HUNDRED = 1500;
    private static final int TWENTY_THOUSAND = 20000;
    private static final int TWENTY = 20;
    private static final int ONE_FIFTY_INT = 150;
    private static final int NEGATIVE_TEN_INT = -10;
    private static final double NEGATIVE_FIVE_DOT_ZERO = -5.0;

    // Test constants for assertion deltas
    private static final double DELTA = 0.01;

    @Test
    void testCalculateSuccessRate() {
        assertEquals(EIGHTY_PERCENT, ServiceMetricsUtils.calculateSuccessRate(EIGHTY, ONE_HUNDRED), DELTA);
        assertEquals(HUNDRED_PERCENT, ServiceMetricsUtils.calculateSuccessRate(ONE_HUNDRED, ONE_HUNDRED), DELTA);
        assertEquals(ZERO_PERCENT, ServiceMetricsUtils.calculateSuccessRate(0, ONE_HUNDRED), DELTA);
        assertEquals(ZERO_PERCENT, ServiceMetricsUtils.calculateSuccessRate(FIFTY, 0), DELTA);
        assertEquals(ZERO_PERCENT, ServiceMetricsUtils.calculateSuccessRate(NEGATIVE_TEN_INT, ONE_HUNDRED), DELTA);
        assertEquals(HUNDRED_PERCENT, ServiceMetricsUtils.calculateSuccessRate(ONE_FIFTY_INT, ONE_HUNDRED), DELTA);
    }

    @Test
    void testFormatSuccessRate() {
        assertEquals("80.0%", ServiceMetricsUtils.formatSuccessRate(EIGHTY_PERCENT));
        assertEquals("95.5%", ServiceMetricsUtils.formatSuccessRate(NINETY_FIVE_POINT_FIVE));
        assertEquals("0.0%", ServiceMetricsUtils.formatSuccessRate(NEGATIVE_FIVE));
        assertEquals("100.0%", ServiceMetricsUtils.formatSuccessRate(ONE_FIFTY));
    }

    @Test
    void testMeetsSuccessThreshold() {
        assertTrue(ServiceMetricsUtils.meetsSuccessThreshold(NINETY_FIVE_PERCENT, NINETY_PERCENT));
        assertTrue(ServiceMetricsUtils.meetsSuccessThreshold(NINETY_PERCENT, NINETY_PERCENT));
        assertFalse(ServiceMetricsUtils.meetsSuccessThreshold(EIGHTY_FIVE_PERCENT, NINETY_PERCENT));
        assertFalse(ServiceMetricsUtils.meetsSuccessThreshold(NINETY_FIVE_PERCENT, NEGATIVE_TEN));
        assertFalse(ServiceMetricsUtils.meetsSuccessThreshold(NINETY_FIVE_PERCENT, ONE_FIFTY));
    }

    @Test
    void testCalculateErrorRate() {
        assertEquals(TWENTY_PERCENT, ServiceMetricsUtils.calculateErrorRate(TWENTY, ONE_HUNDRED), DELTA);
        assertEquals(ZERO_PERCENT, ServiceMetricsUtils.calculateErrorRate(0, ONE_HUNDRED), DELTA);
        assertEquals(HUNDRED_PERCENT, ServiceMetricsUtils.calculateErrorRate(ONE_HUNDRED, ONE_HUNDRED), DELTA);
        assertEquals(ZERO_PERCENT, ServiceMetricsUtils.calculateErrorRate(FIFTY, 0), DELTA);
        assertEquals(ZERO_PERCENT, ServiceMetricsUtils.calculateErrorRate(NEGATIVE_TEN_INT, ONE_HUNDRED), DELTA);
        assertEquals(HUNDRED_PERCENT, ServiceMetricsUtils.calculateErrorRate(ONE_FIFTY_INT, ONE_HUNDRED), DELTA);
    }

    @Test
    void testIsServiceHealthy() {
        assertTrue(ServiceMetricsUtils.isServiceHealthy(NINETY_FIVE_PERCENT, FIVE_PERCENT, NINETY_PERCENT, TEN_PERCENT));
        assertTrue(ServiceMetricsUtils.isServiceHealthy(NINETY_PERCENT, TEN_PERCENT, NINETY_PERCENT, TEN_PERCENT));
        assertFalse(ServiceMetricsUtils.isServiceHealthy(EIGHTY_FIVE_PERCENT, FIVE_PERCENT, NINETY_PERCENT, TEN_PERCENT));
        assertFalse(ServiceMetricsUtils.isServiceHealthy(NINETY_FIVE_PERCENT, FIFTEEN_PERCENT, NINETY_PERCENT, TEN_PERCENT));
        assertFalse(ServiceMetricsUtils.isServiceHealthy(NINETY_FIVE_PERCENT, FIVE_PERCENT, NEGATIVE_TEN, TEN_PERCENT));
        assertFalse(ServiceMetricsUtils.isServiceHealthy(NINETY_FIVE_PERCENT, FIVE_PERCENT, NINETY_PERCENT, NEGATIVE_FIVE_DOT_ZERO));
        assertFalse(ServiceMetricsUtils.isServiceHealthy(NINETY_FIVE_PERCENT, FIVE_PERCENT, ONE_FIFTY, TEN_PERCENT));
        assertFalse(ServiceMetricsUtils.isServiceHealthy(NINETY_FIVE_PERCENT, FIVE_PERCENT, NINETY_PERCENT, ONE_FIFTY));
    }

    @Test
    void testCalculateAvailability() {
        assertEquals(NINETY_NINE_POINT_NINE, ServiceMetricsUtils.calculateAvailability(NINE_NINETY_NINE, ONE_THOUSAND), DELTA);
        assertEquals(HUNDRED_PERCENT, ServiceMetricsUtils.calculateAvailability(ONE_THOUSAND, ONE_THOUSAND), DELTA);
        assertEquals(ZERO_PERCENT, ServiceMetricsUtils.calculateAvailability(0, ONE_THOUSAND), DELTA);
        assertEquals(ZERO_PERCENT, ServiceMetricsUtils.calculateAvailability(FIVE_HUNDRED, 0), DELTA);
        assertEquals(ZERO_PERCENT, ServiceMetricsUtils.calculateAvailability(NEGATIVE_ONE_HUNDRED, ONE_THOUSAND), DELTA);
        assertEquals(HUNDRED_PERCENT, ServiceMetricsUtils.calculateAvailability(FIFTEEN_HUNDRED, ONE_THOUSAND), DELTA);
    }

    @Test
    void testFormatMetricsSummary() {
        String summary = ServiceMetricsUtils.formatMetricsSummary(NINETY_FIVE_POINT_FIVE, FOUR_POINT_FIVE, NINETY_NINE_POINT_NINE);
        assertEquals("Success: 95.5%, Errors: 4.5%, Availability: 99.9%", summary);

        summary = ServiceMetricsUtils.formatMetricsSummary(HUNDRED_PERCENT, ZERO_PERCENT, HUNDRED_PERCENT);
        assertEquals("Success: 100.0%, Errors: 0.0%, Availability: 100.0%", summary);
    }
}
