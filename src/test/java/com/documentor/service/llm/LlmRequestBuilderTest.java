package com.documentor.service.llm;

import com.documentor.config.DocumentorConfig;
import com.documentor.model.CodeElement;
import com.documentor.model.CodeElementType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 🧪 Tests for LlmRequestBuilder component
 */
class LlmRequestBuilderTest {

    private LlmRequestBuilder requestBuilder;
    private DocumentorConfig.LlmModelConfig ollamaModel;
    private DocumentorConfig.LlmModelConfig openaiModel;

    @BeforeEach
    void setUp() {
        LlmModelTypeDetector modelTypeDetector = new LlmModelTypeDetector();
        LlmPromptTemplates promptTemplates = new LlmPromptTemplates();
        LlmRequestFormatter requestFormatter = new LlmRequestFormatter(modelTypeDetector);
        requestBuilder = new LlmRequestBuilder(promptTemplates, requestFormatter);
        
        ollamaModel = new DocumentorConfig.LlmModelConfig(
            "llama2", "", "http://localhost:11434/api/generate", 1000, 0.7, 30, Map.of()
        );
        
        openaiModel = new DocumentorConfig.LlmModelConfig(
            "gpt-4", "sk-test", "https://api.openai.com/v1/completions", 1000, 0.7, 30, Map.of()
        );
    }

    @Test
    void testCreateDocumentationPrompt() {
        CodeElement codeElement = new CodeElement(
            CodeElementType.CLASS, "TestClass", "com.example.TestClass", 
            "TestClass.java", 1, "public class TestClass {}", 
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
        assertEquals(1000, requestBody.get("max_tokens"));
        assertEquals(0.7, requestBody.get("temperature"));
    }

    @Test
    void testCreateRequestBodyForGeneric() {
        String prompt = "Test prompt";
        
        // Test generic model (not Ollama or OpenAI)
        DocumentorConfig.LlmModelConfig genericModel = new DocumentorConfig.LlmModelConfig(
            "claude-3", "api-key", "https://api.anthropic.com", 2000, 0.5, 60, Map.of()
        );
        
        Map<String, Object> requestBody = requestBuilder.buildRequestBody(genericModel, prompt);
        
        assertNotNull(requestBody);
        // Generic models should contain basic fields
        assertTrue(requestBody.containsKey("prompt"));
        assertEquals(2000, requestBody.get("max_tokens"));
        assertEquals(0.5, requestBody.get("temperature"));
    }

    @Test
    void testCreateDocumentationPromptWithDifferentElementTypes() {
        CodeElement methodElement = new CodeElement(
            CodeElementType.METHOD, "testMethod", "com.example.TestClass.testMethod", 
            "TestClass.java", 5, "public void testMethod() {}", 
            "", List.of(), List.of()
        );
        
        CodeElement fieldElement = new CodeElement(
            CodeElementType.FIELD, "testField", "com.example.TestClass.testField", 
            "TestClass.java", 3, "private String testField", 
            "", List.of(), List.of()
        );

        String methodPrompt = requestBuilder.createDocumentationPrompt(methodElement);
        String fieldPrompt = requestBuilder.createDocumentationPrompt(fieldElement);
        
        assertNotNull(methodPrompt);
        assertNotNull(fieldPrompt);
        assertTrue(methodPrompt.contains("testMethod"));
        assertTrue(fieldPrompt.contains("testField"));
    }
}