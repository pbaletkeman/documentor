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

    private static final int EXPECTED_MIN_PARTS = 3;
    private static final int EXPECTED_FUNCTION_PREFIX_LENGTH = 1;
    private static final int EXPECTED_PARAMS_ARRAY_INDEX = 2;
    private static final int EXPECTED_TIMEOUT_SECONDS = 30;
    private static final int EXPECTED_MAX_LINES_BATCH = 1000;

    @Test
    @DisplayName("Should verify constants are defined with correct values")
    void shouldVerifyConstantValues() {
        // Verify constant values
        assertEquals(EXPECTED_MIN_PARTS,
                ApplicationConstants.MINIMUM_PARTS_FOR_PARSING);
        assertEquals(EXPECTED_FUNCTION_PREFIX_LENGTH,
                ApplicationConstants.FUNCTION_DEF_PREFIX_LENGTH);
        assertEquals(EXPECTED_PARAMS_ARRAY_INDEX,
                ApplicationConstants.PARAMETERS_ARRAY_INDEX);
        assertEquals(EXPECTED_TIMEOUT_SECONDS,
                ApplicationConstants.DEFAULT_PROCESS_TIMEOUT_SECONDS);
        assertEquals(EXPECTED_MAX_LINES_BATCH,
                ApplicationConstants.MAX_LINES_PER_BATCH);
    }

    @Test
    @DisplayName("Should verify class is final")
    void shouldVerifyClassIsFinal() {
        // Verify the class is final
        assertTrue(Modifier.isFinal(
                ApplicationConstants.class.getModifiers()));
    }

    @Test
    @DisplayName("Should verify constructor is private and throws exception when called")
    void shouldVerifyPrivateConstructor() throws Exception {
        // Verify constructor is private
        Constructor<ApplicationConstants> constructor =
                ApplicationConstants.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));

        // Verify constructor throws exception when invoked
        constructor.setAccessible(true);
        try {
            constructor.newInstance();
            fail("Expected UnsupportedOperationException to be thrown");
        } catch (Exception e) {
            // Need to check the cause since reflection wraps the exception
            assertTrue(e.getCause() instanceof UnsupportedOperationException);
            assertEquals("Utility class cannot be instantiated",
                    e.getCause().getMessage());
        }
    }
}
