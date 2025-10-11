package com.documentor.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Tests for ApplicationConstants class
 */
class ApplicationConstantsTest {

    @Test
    @DisplayName("Should verify constants are defined with correct values")
    void shouldVerifyConstantValues() {
        // Verify constant values
        assertEquals(3, ApplicationConstants.MINIMUM_PARTS_FOR_PARSING);
        assertEquals(1, ApplicationConstants.FUNCTION_DEF_PREFIX_LENGTH);
        assertEquals(2, ApplicationConstants.PARAMETERS_ARRAY_INDEX);
        assertEquals(30, ApplicationConstants.DEFAULT_PROCESS_TIMEOUT_SECONDS);
        assertEquals(1000, ApplicationConstants.MAX_LINES_PER_BATCH);
    }

    @Test
    @DisplayName("Should verify class is final")
    void shouldVerifyClassIsFinal() {
        // Verify the class is final
        assertTrue(Modifier.isFinal(ApplicationConstants.class.getModifiers()));
    }

    @Test
    @DisplayName("Should verify constructor is private and throws exception when called")
    void shouldVerifyPrivateConstructor() throws Exception {
        // Verify constructor is private
        Constructor<ApplicationConstants> constructor = ApplicationConstants.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));

        // Verify constructor throws exception when invoked
        constructor.setAccessible(true);
        try {
            constructor.newInstance();
            fail("Expected UnsupportedOperationException to be thrown");
        } catch (Exception e) {
            // Need to check the cause since reflection wraps the exception
            assertTrue(e.getCause() instanceof UnsupportedOperationException);
            assertEquals("Utility class cannot be instantiated", e.getCause().getMessage());
        }
    }
}
