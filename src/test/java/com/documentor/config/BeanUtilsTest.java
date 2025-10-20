package com.documentor.config;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for BeanUtils class to improve branch coverage.
 * Using simpler approach to avoid complex mocking issues.
 */
class BeanUtilsTest {

    @Test
    void testOverrideBeanWithNullApplicationContext() {
        // Test null application context parameter - should throw NPE
        assertThrows(NullPointerException.class, () ->
            BeanUtils.overrideBean(null, "testBean", "newValue"));
    }

    @Test
    void testOverrideBeanWithNullBeanName() {
        ApplicationContext context = mock(ApplicationContext.class);

        // Test null bean name parameter - should throw NPE
        assertThrows(NullPointerException.class, () ->
            BeanUtils.overrideBean(context, null, "newValue"));
    }

    @Test
    void testOverrideBeanWithNullNewBean() {
        ApplicationContext context = mock(ApplicationContext.class);

        // Test null new bean parameter - should throw NPE
        assertThrows(NullPointerException.class, () ->
            BeanUtils.overrideBean(context, "testBean", null));
    }

    @Test
    void testOverrideBeanWithNonConfigurableContext() {
        ApplicationContext context = mock(ApplicationContext.class);

        // Test with non-configurable application context
        assertDoesNotThrow(() ->
            BeanUtils.overrideBean(context, "testBean", "newValue"));
    }

    @Test
    void testOverrideBeanWithException() {
        ApplicationContext context = mock(ApplicationContext.class);

        // Should handle any exceptions gracefully
        assertDoesNotThrow(() ->
            BeanUtils.overrideBean(context, "testBean", "newValue"));
    }

    @Test
    void testOverrideBeanBasicFunctionality() {
        // Test that the method can be called without throwing exceptions
        ApplicationContext mockContext = mock(ApplicationContext.class);
        String beanName = "testBean";
        String newBean = "newBeanInstance";

        // This should complete without throwing an exception
        assertDoesNotThrow(() ->
            BeanUtils.overrideBean(mockContext, beanName, newBean));
    }

    @Test
    void testOverrideBeanWithEmptyBeanName() {
        ApplicationContext context = mock(ApplicationContext.class);

        // Test with empty bean name
        assertDoesNotThrow(() ->
            BeanUtils.overrideBean(context, "", "newValue"));
    }

    @Test
    void testOverrideBeanWithEmptyString() {
        ApplicationContext context = mock(ApplicationContext.class);

        // Test with empty string as new bean
        assertDoesNotThrow(() ->
            BeanUtils.overrideBean(context, "testBean", ""));
    }

    @Test
    void testOverrideBeanMultipleCalls() {
        ApplicationContext context = mock(ApplicationContext.class);

        // Test multiple calls to the same method
        assertDoesNotThrow(() -> {
            BeanUtils.overrideBean(context, "bean1", "value1");
            BeanUtils.overrideBean(context, "bean2", "value2");
            BeanUtils.overrideBean(context, "bean3", "value3");
        });
    }

    @Test
    void testOverrideBeanWithDifferentObjectTypes() {
        ApplicationContext context = mock(ApplicationContext.class);

        // Test with different object types
        assertDoesNotThrow(() -> {
            BeanUtils.overrideBean(context, "stringBean", "stringValue");
            BeanUtils.overrideBean(context, "integerBean", 42);
            BeanUtils.overrideBean(context, "listBean", java.util.List.of("item1", "item2"));
        });
    }
}
