package com.documentor.service.llm;

import com.documentor.config.model.LlmModelConfig;
import com.documentor.model.CodeElement;
import com.documentor.model.CodeElementType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 🧪 Tests for LlmRequestBuilder component
 */
class LlmRequestBuilderTest {

    // Test constants for magic number violations
    private static final int DEFAULT_MAX_TOKENS = 1000;
    private static final int DEFAULT_TIMEOUT_SECONDS = 30;
    private static final int LINE_NUMBER_ONE = 1;
    private static final double DEFAULT_TEMPERATURE = 0.7;
    private static final int LARGE_MAX_TOKENS = 2000;
    private static final int LARGE_TIMEOUT_SECONDS = 60;
    private static final double GENERIC_TEMPERATURE = 0.5;
    private static final int LINE_NUMBER_FIVE = 5;
    private static final int LINE_NUMBER_THREE = 3;
    private static final int LINE_NUMBER_FIFTEEN = 15;
    private static final int LINE_NUMBER_TEN = 10;

    private LlmRequestBuilder requestBuilder;
    private LlmModelConfig ollamaModel;
    private LlmModelConfig openaiModel;

    @BeforeEach
    void setUp() {
        LlmModelTypeDetector modelTypeDetector = new LlmModelTypeDetector();
        LlmPromptTemplates promptTemplates = new LlmPromptTemplates();
        LlmRequestFormatter requestFormatter = new LlmRequestFormatter(modelTypeDetector);
        requestBuilder = new LlmRequestBuilder(promptTemplates, requestFormatter);

        ollamaModel = new LlmModelConfig(
            "llama2", "ollama", "http://localhost:11434/api/generate", "", DEFAULT_MAX_TOKENS, DEFAULT_TIMEOUT_SECONDS
        );

        openaiModel = new LlmModelConfig(
            "gpt-4", "openai", "https://api.openai.com/v1/completions", "sk-test", DEFAULT_MAX_TOKENS, DEFAULT_TIMEOUT_SECONDS
        );
    }

    @Test
    void testCreateDocumentationPrompt() {
        CodeElement codeElement = new CodeElement(
            CodeElementType.CLASS, "TestClass", "com.example.TestClass",
            "TestClass.java", LINE_NUMBER_ONE, "public class TestClass {}",
            "", List.of(), List.of()
        );

        String prompt = requestBuilder.createDocumentationPrompt(codeElement);

        assertNotNull(prompt);
        assertTrue(prompt.contains("TestClass"));
        assertTrue(prompt.contains("public class TestClass {}"));
    }

    @Test
    void testCreateRequestBodyForOllama() {
        String prompt = "Test prompt";

        Map<String, Object> requestBody = requestBuilder.buildRequestBody(ollamaModel, prompt);

        assertNotNull(requestBody);
        assertEquals("llama2", requestBody.get("model"));
        assertEquals(prompt, requestBody.get("prompt"));
        assertEquals(false, requestBody.get("stream"));
    }

    @Test
    void testCreateRequestBodyForOpenAI() {
        String prompt = "Test prompt";

        Map<String, Object> requestBody = requestBuilder.buildRequestBody(openaiModel, prompt);

        assertNotNull(requestBody);
        assertEquals("gpt-4", requestBody.get("model"));
        assertTrue(requestBody.containsKey("messages"));
        assertEquals(DEFAULT_MAX_TOKENS, requestBody.get("max_tokens"));
        assertEquals(DEFAULT_TEMPERATURE, requestBody.get("temperature"));
    }

    @Test
    void testCreateRequestBodyForGeneric() {
        String prompt = "Test prompt";

        // Test generic model (not Ollama or OpenAI)
        LlmModelConfig genericModel = new LlmModelConfig(
            "claude-3", "anthropic", "https://api.anthropic.com", "api-key", LARGE_MAX_TOKENS, LARGE_TIMEOUT_SECONDS
        );

        Map<String, Object> requestBody = requestBuilder.buildRequestBody(genericModel, prompt);

        assertNotNull(requestBody);
        // Generic models should contain basic fields
        assertTrue(requestBody.containsKey("prompt"));
        assertEquals(LARGE_MAX_TOKENS, requestBody.get("max_tokens"));
        assertEquals(GENERIC_TEMPERATURE, requestBody.get("temperature"));
    }

    @Test
    void testCreateDocumentationPromptWithDifferentElementTypes() {
        CodeElement methodElement = new CodeElement(
            CodeElementType.METHOD, "testMethod", "com.example.TestClass.testMethod",
            "TestClass.java", LINE_NUMBER_FIVE, "public void testMethod() {}",
            "", List.of(), List.of()
        );

        CodeElement fieldElement = new CodeElement(
            CodeElementType.FIELD, "testField", "com.example.TestClass.testField",
            "TestClass.java", LINE_NUMBER_THREE, "private String testField",
            "", List.of(), List.of()
        );

        String methodPrompt = requestBuilder.createDocumentationPrompt(methodElement);
        String fieldPrompt = requestBuilder.createDocumentationPrompt(fieldElement);

        assertNotNull(methodPrompt);
        assertNotNull(fieldPrompt);
        assertTrue(methodPrompt.contains("testMethod"));
        assertTrue(fieldPrompt.contains("testField"));
    }

    @Test
    void testCreateUsageExamplePrompt() {
        CodeElement codeElement = new CodeElement(
            CodeElementType.METHOD, "exampleMethod", "com.example.TestClass.exampleMethod",
            "TestClass.java", LINE_NUMBER_TEN, "public String exampleMethod(int param) { return \"test\"; }",
            "", List.of(), List.of()
        );

        String prompt = requestBuilder.createUsageExamplePrompt(codeElement);

        assertNotNull(prompt);
        assertTrue(prompt.contains("exampleMethod"));
        assertTrue(prompt.contains("public String exampleMethod(int param)"));
    }

    @Test
    void testCreateUnitTestPrompt() {
        CodeElement codeElement = new CodeElement(
            CodeElementType.METHOD, "methodToTest", "com.example.TestClass.methodToTest",
            "TestClass.java", LINE_NUMBER_FIFTEEN, "public int methodToTest(String input) { return input.length(); }",
            "", List.of(), List.of()
        );

        String prompt = requestBuilder.createUnitTestPrompt(codeElement);

        assertNotNull(prompt);
        assertTrue(prompt.contains("methodToTest"));
        assertTrue(prompt.contains("public int methodToTest(String input)"));
        assertTrue(prompt.contains("unit test"));
    }
}

