package com.documentor.config;

import com.networknt.schema.ValidationMessage;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConfigValidatorTest {

    @Test
    public void sampleConfigValidates() throws Exception {
        try (InputStream in = getClass().getResourceAsStream(
            "/samples/config-diagram-naming-example.json")) {
            Set<ValidationMessage> errors = ConfigValidator.validate(in);
            assertTrue(errors.isEmpty(),
                "Expected no validation errors, but found: " + errors);
        }
    }
}
