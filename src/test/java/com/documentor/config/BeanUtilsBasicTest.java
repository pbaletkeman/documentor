package com.documentor.config;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Simple focused tests for BeanUtils to improve branch coverage.
 * These tests focus on the reachable branches to maximize coverage improvement.
 */
class BeanUtilsBasicTest {

    @Test
    void testOverrideBeanWithNullApplicationContext() {
        // Test null application context - should throw NPE
        // (covers null check branch)
        assertThrows(NullPointerException.class, () ->
            BeanUtils.overrideBean(null, "testBean", "newValue"));
    }

    @Test
    void testOverrideBeanWithNullBeanName() {
        ApplicationContext context = mock(ApplicationContext.class);

        // Test null bean name - should throw NPE (covers null check branch)
        assertThrows(NullPointerException.class, () ->
            BeanUtils.overrideBean(context, null, "newValue"));
    }

    @Test
    void testOverrideBeanWithNullNewBean() {
        ApplicationContext context = mock(ApplicationContext.class);

        // Test null new bean - should throw NPE (covers null check branch)
        assertThrows(NullPointerException.class, () ->
            BeanUtils.overrideBean(context, "testBean", null));
    }

    @Test
    void testOverrideBeanWithNonConfigurableContext() {
        ApplicationContext context = mock(ApplicationContext.class);

        // Test with non-configurable context (covers instanceof check branch)
        assertDoesNotThrow(() ->
            BeanUtils.overrideBean(context, "testBean", "newValue"));

        // Verify no further operations were attempted since
        // context is not configurable
        verifyNoMoreInteractions(context);
    }

    @Test
    void testOverrideBeanBasicOperation() {
        ApplicationContext mockContext = mock(ApplicationContext.class);

        // Test basic operation (covers exception handling branch)
        assertDoesNotThrow(() ->
            BeanUtils.overrideBean(mockContext, "testBean",
            "newBeanInstance"));
    }
}
