package com.documentor.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ServiceMetricsUtils to reach final coverage boost.
 */
class ServiceMetricsUtilsTest {

    @Test
    void testCalculateSuccessRate() {
        assertEquals(80.0, ServiceMetricsUtils.calculateSuccessRate(80, 100), 0.01);
        assertEquals(100.0, ServiceMetricsUtils.calculateSuccessRate(100, 100), 0.01);
        assertEquals(0.0, ServiceMetricsUtils.calculateSuccessRate(0, 100), 0.01);
        assertEquals(0.0, ServiceMetricsUtils.calculateSuccessRate(50, 0), 0.01);
        assertEquals(0.0, ServiceMetricsUtils.calculateSuccessRate(-10, 100), 0.01);
        assertEquals(100.0, ServiceMetricsUtils.calculateSuccessRate(150, 100), 0.01);
    }

    @Test
    void testFormatSuccessRate() {
        assertEquals("80.0%", ServiceMetricsUtils.formatSuccessRate(80.0));
        assertEquals("95.5%", ServiceMetricsUtils.formatSuccessRate(95.47));
        assertEquals("0.0%", ServiceMetricsUtils.formatSuccessRate(-5.0));
        assertEquals("100.0%", ServiceMetricsUtils.formatSuccessRate(150.0));
    }

    @Test
    void testMeetsSuccessThreshold() {
        assertTrue(ServiceMetricsUtils.meetsSuccessThreshold(95.0, 90.0));
        assertTrue(ServiceMetricsUtils.meetsSuccessThreshold(90.0, 90.0));
        assertFalse(ServiceMetricsUtils.meetsSuccessThreshold(85.0, 90.0));
        assertFalse(ServiceMetricsUtils.meetsSuccessThreshold(95.0, -10.0));
        assertFalse(ServiceMetricsUtils.meetsSuccessThreshold(95.0, 150.0));
    }

    @Test
    void testCalculateErrorRate() {
        assertEquals(20.0, ServiceMetricsUtils.calculateErrorRate(20, 100), 0.01);
        assertEquals(0.0, ServiceMetricsUtils.calculateErrorRate(0, 100), 0.01);
        assertEquals(100.0, ServiceMetricsUtils.calculateErrorRate(100, 100), 0.01);
        assertEquals(0.0, ServiceMetricsUtils.calculateErrorRate(50, 0), 0.01);
        assertEquals(0.0, ServiceMetricsUtils.calculateErrorRate(-10, 100), 0.01);
        assertEquals(100.0, ServiceMetricsUtils.calculateErrorRate(150, 100), 0.01);
    }

    @Test
    void testIsServiceHealthy() {
        assertTrue(ServiceMetricsUtils.isServiceHealthy(95.0, 5.0, 90.0, 10.0));
        assertTrue(ServiceMetricsUtils.isServiceHealthy(90.0, 10.0, 90.0, 10.0));
        assertFalse(ServiceMetricsUtils.isServiceHealthy(85.0, 5.0, 90.0, 10.0));
        assertFalse(ServiceMetricsUtils.isServiceHealthy(95.0, 15.0, 90.0, 10.0));
        assertFalse(ServiceMetricsUtils.isServiceHealthy(95.0, 5.0, -10.0, 10.0));
        assertFalse(ServiceMetricsUtils.isServiceHealthy(95.0, 5.0, 90.0, -5.0));
        assertFalse(ServiceMetricsUtils.isServiceHealthy(95.0, 5.0, 150.0, 10.0));
        assertFalse(ServiceMetricsUtils.isServiceHealthy(95.0, 5.0, 90.0, 150.0));
    }

    @Test
    void testCalculateAvailability() {
        assertEquals(99.9, ServiceMetricsUtils.calculateAvailability(999, 1000), 0.01);
        assertEquals(100.0, ServiceMetricsUtils.calculateAvailability(1000, 1000), 0.01);
        assertEquals(0.0, ServiceMetricsUtils.calculateAvailability(0, 1000), 0.01);
        assertEquals(0.0, ServiceMetricsUtils.calculateAvailability(500, 0), 0.01);
        assertEquals(0.0, ServiceMetricsUtils.calculateAvailability(-100, 1000), 0.01);
        assertEquals(100.0, ServiceMetricsUtils.calculateAvailability(1500, 1000), 0.01);
    }

    @Test
    void testFormatMetricsSummary() {
        String summary = ServiceMetricsUtils.formatMetricsSummary(95.5, 4.5, 99.9);
        assertEquals("Success: 95.5%, Errors: 4.5%, Availability: 99.9%", summary);

        summary = ServiceMetricsUtils.formatMetricsSummary(100.0, 0.0, 100.0);
        assertEquals("Success: 100.0%, Errors: 0.0%, Availability: 100.0%", summary);
    }
}
