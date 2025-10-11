package com.documentor.service;

import com.documentor.config.DocumentorConfig;
import com.documentor.config.model.AnalysisSettings;
import com.documentor.config.model.LlmModelConfig;
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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class LlmServiceTest_clean {

    private static final int MAX_DEPTH = 50;
    private static final int DEFAULT_MAX_TOKENS = 1000;
    private static final int DEFAULT_TIMEOUT_SECONDS = 30;

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
        OutputSettings outputSettings = new OutputSettings("output", "markdown", true, true);
        AnalysisSettings analysisSettings = new AnalysisSettings(true, MAX_DEPTH,
                List.of("**/*.java"), List.of("**/test/**"));
        LlmModelConfig testModel = new LlmModelConfig("test-model", "openai", "http://test.api",
                "api-key", DEFAULT_MAX_TOKENS, DEFAULT_TIMEOUT_SECONDS);

        config = new DocumentorConfig(List.of(testModel), outputSettings, analysisSettings);
        llmService = new LlmService(config, mockRequestBuilder, mockResponseHandler, mockApiClient);

        testElement = new CodeElement(CodeElementType.CLASS, "TestClass", "com.example.TestClass", "TestClass.java", 1,
                "public class TestClass {}", "", List.of(), List.of());

        // Common mock behavior used by tests
        when(mockRequestBuilder.createDocumentationPrompt(any())).thenReturn("documentation prompt");
        when(mockRequestBuilder.createUsageExamplePrompt(any())).thenReturn("usage prompt");
        when(mockRequestBuilder.createUnitTestPrompt(any())).thenReturn("test prompt");
        when(mockRequestBuilder.buildRequestBody(any(), anyString())).thenReturn(Map.of("prompt", "test"));
        when(mockResponseHandler.getModelEndpoint(any())).thenReturn("http://test.endpoint");
        when(mockApiClient.callLlmModel(any(), anyString(), any())).thenReturn("mock api response");
        when(mockResponseHandler.extractResponseContent(anyString(), any())).thenReturn("extracted content");
    }

    @Test
    void generateDocumentationReturnsExtractedContent() {
        CompletableFuture<String> result = llmService.generateDocumentation(testElement);
        assertEquals("extracted content", result.join());
    }

    @Test
    void generateUsageExamplesReturnsExtractedContent() {
        CompletableFuture<String> result = llmService.generateUsageExamples(testElement);
        assertEquals("extracted content", result.join());
    }

    @Test
    void generateUnitTestsReturnsExtractedContent() {
        CompletableFuture<String> result = llmService.generateUnitTests(testElement);
        assertEquals("extracted content", result.join());
    }

    @Test
    void serviceConstructsWithEmptyModelList() {
        DocumentorConfig emptyConfig = new DocumentorConfig(List.of(), config.outputSettings(),
                config.analysisSettings());
        LlmService emptyService = new LlmService(emptyConfig, mockRequestBuilder, mockResponseHandler,
                mockApiClient);
        assertNotNull(emptyService);
    }
}
