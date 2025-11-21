package com.documentor.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;

import java.io.InputStream;
import java.util.Set;

public class ConfigValidator {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final JsonSchema schema;

    static {
        try {
            InputStream in = ConfigValidator.class.getClassLoader().getResourceAsStream("schema/config-schema.json");
            if (in == null) {
                throw new IllegalStateException("Schema resource 'schema/config-schema.json' not found on classpath");
            }
            JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);
            JsonNode schemaNode = mapper.readTree(in);
            if (schemaNode == null) {
                throw new IllegalStateException("Failed to read schema JSON");
            }
            schema = factory.getSchema(schemaNode);
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static Set<ValidationMessage> validate(InputStream jsonInput) throws Exception {
        JsonNode node = mapper.readTree(jsonInput);
        return schema.validate(node);
    }

    public static Set<ValidationMessage> validate(JsonNode node) {
        return schema.validate(node);
    }
}
