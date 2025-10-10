package com.documentor.service;

import com.documentor.config.DocumentorConfig;
import com.documentor.config.model.LlmModelConfig;
import com.documentor.config.model.AnalysisSettings;
import com.documentor.config.model.OutputSettings;
import com.documentor.model.CodeElement;
import com.documentor.model.CodeElementType;
import com.documentor.service.llm.LlmApiClient;
import com.documentor.service.llm.LlmRequestBuilder;
import com.documentor.service.llm.LlmResponseHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * 🧪 Comprehensive tests for refactored LlmService
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class LlmServiceTest {

    @Mock
    private LlmRequestBuilder mockRequestBuilder;
    
    @Mock
    private LlmResponseHandler mockResponseHandler;
    
    @Mock
    private LlmApiClient mockApiClient;

    private LlmService llmService;
    private DocumentorConfig config;
    private CodeElement testElement;

    @BeforeEach
    void setUp() {
        OutputSettings outputSettings = new OutputSettings(
            "output", "markdown", true, true
        );
        
        AnalysisSettings analysisSettings = new AnalysisSettings(
            true, 50, List.of("**/*.java"), List.of("**/test/**")
        );
        
        LlmModelConfig testModel = new LlmModelConfig(
            "test-model", "openai", "http://test.api", "api-key", 1000, 30
        );
        
        config = new DocumentorConfig(List.of(testModel), outputSettings, analysisSettings);
        llmService = new LlmService(config, mockRequestBuilder, mockResponseHandler, mockApiClient);
        
        testElement = new CodeElement(
            CodeElementType.CLASS, "TestClass", "com.example.TestClass", 
            "TestClass.java", 1, "public class TestClass {}", 
            "", List.of(), List.of()
        );
        
        // Setup mock behaviors
        when(mockRequestBuilder.createDocumentationPrompt(any())).thenReturn("documentation prompt");
        when(mockRequestBuilder.createUsageExamplePrompt(any())).thenReturn("usage prompt");
        when(mockRequestBuilder.createUnitTestPrompt(any())).thenReturn("test prompt");
        when(mockRequestBuilder.buildRequestBody(any(), anyString())).thenReturn(Map.of("prompt", "test"));
        when(mockResponseHandler.getModelEndpoint(any())).thenReturn("http://test.endpoint");
        when(mockApiClient.callLlmModel(any(), anyString(), any())).thenReturn("mock api response");
        when(mockResponseHandler.extractResponseContent(anyString(), any())).thenReturn("extracted content");
    }

    @Test
    void testGenerateDocumentation() {
        CompletableFuture<String> result = llmService.generateDocumentation(testElement);
        
        assertNotNull(result);
        assertEquals("extracted content", result.join());
    }

    @Test
    void testGenerateUsageExamples() {
        CompletableFuture<String> result = llmService.generateUsageExamples(testElement);
        
        assertNotNull(result);
        assertEquals("extracted content", result.join());
    }

    @Test
    void testGenerateUnitTests() {
        CompletableFuture<String> result = llmService.generateUnitTests(testElement);
        
        assertNotNull(result);
        assertEquals("extracted content", result.join());
    }

    @Test
    void testServiceWithDifferentElementTypes() {
        CodeElement methodElement = new CodeElement(
            CodeElementType.METHOD, "testMethod", "com.example.TestClass.testMethod", 
            "TestClass.java", 5, "public void testMethod()", 
            "", List.of("param1"), List.of("@Test")
        );
        
        assertNotNull(llmService.generateDocumentation(methodElement).join());
        assertNotNull(llmService.generateUsageExamples(methodElement).join());
        assertNotNull(llmService.generateUnitTests(methodElement).join());
    }

    @Test
    void testEmptyConfiguration() {
        DocumentorConfig emptyConfig = new DocumentorConfig(
            List.of(), config.outputSettings(), config.analysisSettings()
        );
        LlmService emptyService = new LlmService(emptyConfig, mockRequestBuilder, mockResponseHandler, mockApiClient);
        
        // Should handle empty configuration gracefully
        assertNotNull(emptyService);
    }
}
