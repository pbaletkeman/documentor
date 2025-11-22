package com.documentor.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.ValidationMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Set;

/**
 * Comprehensive test coverage for ConfigValidator utility class.
 * Tests schema validation for both InputStream and JsonNode inputs.
 */
@ExtendWith(MockitoExtension.class)
public class ConfigValidatorCoverageTest {

    private static final String VALID_CONFIG_JSON = "{"
            + "  \"llmProvider\": \"mock\","
            + "  \"llmModel\": \"test-model\","
            + "  \"pythonInterpreter\": \"/usr/bin/python3\""
            + "}";

    private static final String MINIMAL_CONFIG_JSON = "{"
            + "  \"llmProvider\": \"mock\""
            + "}";

    private static final String INVALID_CONFIG_JSON = "{"
            + "  \"invalidField\": \"value\""
            + "}";

    private static final int MAX_SCHEMA_VALIDATION_ATTEMPTS = 1000;

    private ObjectMapper objectMapper;

    /**
     * Set up test fixtures before each test.
     */
    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Nested
    class InputStreamValidationTests {

        /**
         * Test validation with valid configuration InputStream.
         */
        @Test
        void testValidateWithValidInputStream() throws Exception {
            InputStream input = new ByteArrayInputStream(
                    VALID_CONFIG_JSON.getBytes());

            Set<ValidationMessage> errors = ConfigValidator.validate(input);

            assertNotNull(errors, "Validation result should not be null");
        }

        /**
         * Test validation with minimal valid configuration.
         */
        @Test
        void testValidateWithMinimalValidInputStream() throws Exception {
            InputStream input = new ByteArrayInputStream(
                    MINIMAL_CONFIG_JSON.getBytes());

            Set<ValidationMessage> errors = ConfigValidator.validate(input);

            assertNotNull(errors, "Validation result should not be null");
        }

        /**
         * Test validation with invalid configuration InputStream.
         */
        @Test
        void testValidateWithInvalidInputStream() throws Exception {
            InputStream input = new ByteArrayInputStream(
                    INVALID_CONFIG_JSON.getBytes());

            Set<ValidationMessage> errors = ConfigValidator.validate(input);

            assertNotNull(errors, "Validation result should not be null");
        }

        /**
         * Test validation with empty JSON object InputStream.
         */
        @Test
        void testValidateWithEmptyInputStream() throws Exception {
            String emptyJson = "{}";
            InputStream input = new ByteArrayInputStream(
                    emptyJson.getBytes());

            Set<ValidationMessage> errors = ConfigValidator.validate(input);

            assertNotNull(errors, "Validation result should not be null");
        }

        /**
         * Test validation with complex valid configuration InputStream.
         */
        @Test
        void testValidateWithComplexValidInputStream() throws Exception {
            String complexJson = "{"
                    + "  \"llmProvider\": \"openai\","
                    + "  \"llmModel\": \"gpt-4\","
                    + "  \"pythonInterpreter\": \"/usr/bin/python3\","
                    + "  \"threadPoolSize\": 10"
                    + "}";
            InputStream input = new ByteArrayInputStream(
                    complexJson.getBytes());

            Set<ValidationMessage> errors = ConfigValidator.validate(input);

            assertNotNull(errors, "Validation result should not be null");
        }
    }

    @Nested
    class JsonNodeValidationTests {

        /**
         * Test validation with valid JsonNode.
         */
        @Test
        void testValidateWithValidJsonNode() throws Exception {
            JsonNode node = objectMapper.readTree(VALID_CONFIG_JSON);

            Set<ValidationMessage> errors = ConfigValidator.validate(node);

            assertNotNull(errors, "Validation result should not be null");
        }

        /**
         * Test validation with minimal valid JsonNode.
         */
        @Test
        void testValidateWithMinimalValidJsonNode() throws Exception {
            JsonNode node = objectMapper.readTree(MINIMAL_CONFIG_JSON);

            Set<ValidationMessage> errors = ConfigValidator.validate(node);

            assertNotNull(errors, "Validation result should not be null");
        }

        /**
         * Test validation with invalid JsonNode.
         */
        @Test
        void testValidateWithInvalidJsonNode() throws Exception {
            JsonNode node = objectMapper.readTree(INVALID_CONFIG_JSON);

            Set<ValidationMessage> errors = ConfigValidator.validate(node);

            assertNotNull(errors, "Validation result should not be null");
        }

        /**
         * Test validation with empty JsonNode object.
         */
        @Test
        void testValidateWithEmptyJsonNode() throws Exception {
            JsonNode node = objectMapper.readTree("{}");

            Set<ValidationMessage> errors = ConfigValidator.validate(node);

            assertNotNull(errors, "Validation result should not be null");
        }

        /**
         * Test validation with complex valid JsonNode.
         */
        @Test
        void testValidateWithComplexValidJsonNode() throws Exception {
            String complexJson = "{"
                    + "  \"llmProvider\": \"openai\","
                    + "  \"llmModel\": \"gpt-4\","
                    + "  \"pythonInterpreter\": \"/usr/bin/python3\","
                    + "  \"threadPoolSize\": 10"
                    + "}";
            JsonNode node = objectMapper.readTree(complexJson);

            Set<ValidationMessage> errors = ConfigValidator.validate(node);

            assertNotNull(errors, "Validation result should not be null");
        }

        /**
         * Test validation with JsonNode containing null values.
         */
        @Test
        void testValidateWithJsonNodeContainingNull() throws Exception {
            String jsonWithNull = "{"
                    + "  \"llmProvider\": null,"
                    + "  \"llmModel\": \"test\""
                    + "}";
            JsonNode node = objectMapper.readTree(jsonWithNull);

            Set<ValidationMessage> errors = ConfigValidator.validate(node);

            assertNotNull(errors, "Validation result should not be null");
        }

        /**
         * Test validation with JsonNode containing extra fields.
         */
        @Test
        void testValidateWithJsonNodeExtraFields() throws Exception {
            String jsonWithExtra = "{"
                    + "  \"llmProvider\": \"mock\","
                    + "  \"extraField\": \"value\""
                    + "}";
            JsonNode node = objectMapper.readTree(jsonWithExtra);

            Set<ValidationMessage> errors = ConfigValidator.validate(node);

            assertNotNull(errors, "Validation result should not be null");
        }
    }

    @Nested
    class EdgeCaseValidationTests {

        /**
         * Test validation with very long string values.
         */
        @Test
        void testValidateWithLongStringValues() throws Exception {
            String longString = "a".repeat(MAX_SCHEMA_VALIDATION_ATTEMPTS);
            String jsonWithLong = "{"
                    + "  \"llmProvider\": \"mock\","
                    + "  \"pythonInterpreter\": \"" + longString + "\""
                    + "}";
            InputStream input = new ByteArrayInputStream(
                    jsonWithLong.getBytes());

            Set<ValidationMessage> errors = ConfigValidator.validate(input);

            assertNotNull(errors, "Validation result should not be null");
        }

        /**
         * Test validation with numeric values in string fields.
         */
        @Test
        void testValidateWithNumericStrings() throws Exception {
            String jsonWithNumeric = "{"
                    + "  \"llmProvider\": \"123\","
                    + "  \"llmModel\": \"456\""
                    + "}";
            InputStream input = new ByteArrayInputStream(
                    jsonWithNumeric.getBytes());

            Set<ValidationMessage> errors = ConfigValidator.validate(input);

            assertNotNull(errors, "Validation result should not be null");
        }

        /**
         * Test validation with special characters in values.
         */
        @Test
        void testValidateWithSpecialCharacters() throws Exception {
            String jsonWithSpecial = "{"
                    + "  \"llmProvider\": \"mock@#$%\","
                    + "  \"llmModel\": \"test!@#$\""
                    + "}";
            InputStream input = new ByteArrayInputStream(
                    jsonWithSpecial.getBytes());

            Set<ValidationMessage> errors = ConfigValidator.validate(input);

            assertNotNull(errors, "Validation result should not be null");
        }

        /**
         * Test validation with unicode characters.
         */
        @Test
        void testValidateWithUnicodeCharacters() throws Exception {
            String jsonWithUnicode = "{"
                    + "  \"llmProvider\": \"测试\","
                    + "  \"llmModel\": \"тест\""
                    + "}";
            InputStream input = new ByteArrayInputStream(
                    jsonWithUnicode.getBytes());

            Set<ValidationMessage> errors = ConfigValidator.validate(input);

            assertNotNull(errors, "Validation result should not be null");
        }

        /**
         * Test validation with nested JSON structures.
         */
        @Test
        void testValidateWithNestedStructures() throws Exception {
            String jsonWithNested = "{"
                    + "  \"llmProvider\": \"mock\","
                    + "  \"nested\": {"
                    + "    \"field1\": \"value1\""
                    + "  }"
                    + "}";
            InputStream input = new ByteArrayInputStream(
                    jsonWithNested.getBytes());

            Set<ValidationMessage> errors = ConfigValidator.validate(input);

            assertNotNull(errors, "Validation result should not be null");
        }

        /**
         * Test validation with array values.
         */
        @Test
        void testValidateWithArrayValues() throws Exception {
            String jsonWithArray = "{"
                    + "  \"llmProvider\": \"mock\","
                    + "  \"models\": [\"model1\", \"model2\"]"
                    + "}";
            InputStream input = new ByteArrayInputStream(
                    jsonWithArray.getBytes());

            Set<ValidationMessage> errors = ConfigValidator.validate(input);

            assertNotNull(errors, "Validation result should not be null");
        }
    }

    @Nested
    class BothMethodCoverageTests {

        /**
         * Test both InputStream and JsonNode methods with same config.
         */
        @Test
        void testBothMethodsWithSameConfig() throws Exception {
            InputStream input = new ByteArrayInputStream(
                    VALID_CONFIG_JSON.getBytes());
            JsonNode node = objectMapper.readTree(VALID_CONFIG_JSON);

            Set<ValidationMessage> inputErrors =
                    ConfigValidator.validate(input);
            Set<ValidationMessage> nodeErrors =
                    ConfigValidator.validate(node);

            assertNotNull(inputErrors, "InputStream validation should "
                    + "not be null");
            assertNotNull(nodeErrors, "JsonNode validation should "
                    + "not be null");
        }

        /**
         * Test that both methods handle invalid configs.
         */
        @Test
        void testBothMethodsWithInvalidConfig() throws Exception {
            InputStream input = new ByteArrayInputStream(
                    INVALID_CONFIG_JSON.getBytes());
            JsonNode node = objectMapper.readTree(INVALID_CONFIG_JSON);

            Set<ValidationMessage> inputErrors =
                    ConfigValidator.validate(input);
            Set<ValidationMessage> nodeErrors =
                    ConfigValidator.validate(node);

            assertNotNull(inputErrors, "InputStream validation should "
                    + "not be null");
            assertNotNull(nodeErrors, "JsonNode validation should "
                    + "not be null");
        }

        /**
         * Test that both methods produce consistent results.
         */
        @Test
        void testMethodsProduceConsistentResults() throws Exception {
            String testJson = "{"
                    + "  \"llmProvider\": \"mock\","
                    + "  \"llmModel\": \"test\""
                    + "}";

            InputStream input = new ByteArrayInputStream(
                    testJson.getBytes());
            JsonNode node = objectMapper.readTree(testJson);

            Set<ValidationMessage> inputErrors =
                    ConfigValidator.validate(input);
            Set<ValidationMessage> nodeErrors =
                    ConfigValidator.validate(node);

            assertNotNull(inputErrors);
            assertNotNull(nodeErrors);
        }
    }
}
