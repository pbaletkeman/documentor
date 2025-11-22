package com.documentor.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;

import java.io.InputStream;
import java.util.Set;

public final class ConfigValidator {

    private ConfigValidator() {
        throw new UnsupportedOperationException(
            "Utility class cannot be instantiated");
    }

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final JsonSchema SCHEMA;

    static {
        try {
            InputStream in = ConfigValidator.class
                .getClassLoader().getResourceAsStream("schema/config-schema.json");
            if (in == null) {
                throw new IllegalStateException(
                    "Schema resource 'schema/config-schema.json'"
                    + " not found on classpath");
            }
            JsonSchemaFactory factory = JsonSchemaFactory.getInstance(
                    SpecVersion.VersionFlag.V7);
            JsonNode schemaNode = MAPPER.readTree(in);
            if (schemaNode == null) {
                throw new IllegalStateException("Failed to read schema JSON");
            }
            SCHEMA = factory.getSchema(schemaNode);
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static Set<ValidationMessage> validate(final InputStream jsonInput) throws Exception {
        JsonNode node = MAPPER.readTree(jsonInput);
        return SCHEMA.validate(node);
    }

    public static Set<ValidationMessage> validate(final JsonNode node) {
        return SCHEMA.validate(node);
    }
}
