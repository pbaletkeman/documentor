package com.documentor.service;

import com.documentor.config.DocumentorConfig;
import com.documentor.model.CodeElement;
import com.documentor.model.CodeElementType;
import com.documentor.model.ProjectAnalysis;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 🧪 Unit tests for DocumentationService
 */
@ExtendWith(MockitoExtension.class)
class DocumentationServiceTest {

    @Mock
    private LlmService llmService;

    private DocumentationService documentationService;
    private DocumentorConfig config;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        DocumentorConfig.OutputSettings outputSettings = new DocumentorConfig.OutputSettings(
            tempDir.toString(), "markdown", true, true, 0.9
        );
        
        // Create a real config with valid settings for tests
        DocumentorConfig.LlmModelConfig mockModel = new DocumentorConfig.LlmModelConfig(
            "test-model", "test-provider", "http://test.com", 1000, 0.7, 30, null
        );
        
        DocumentorConfig.AnalysisSettings analysisSettings = new DocumentorConfig.AnalysisSettings(
            true, 50, List.of("public"), List.of(".git", "target")
        );
        
        config = new DocumentorConfig(List.of(mockModel), outputSettings, analysisSettings);
        documentationService = new DocumentationService(llmService, config);
    }

    @Test
    void testConstructor() {
        assertNotNull(documentationService);
    }

    @Test
    void testGenerateDocumentation() throws IOException {
        // Given
        CodeElement element = new CodeElement(
            CodeElementType.CLASS, "TestClass", "com.test.TestClass", "/test/TestClass.java", 
            1, "public class TestClass", "", List.of(), List.of()
        );
        
        ProjectAnalysis analysis = new ProjectAnalysis(
            "/test/project", List.of(element), System.currentTimeMillis()
        );

        // Mock ALL LLM service responses
        when(llmService.generateDocumentation(any(CodeElement.class)))
            .thenReturn(CompletableFuture.completedFuture("Generated documentation"));
        when(llmService.generateUsageExamples(any(CodeElement.class)))
            .thenReturn(CompletableFuture.completedFuture("Usage examples"));
        when(llmService.generateUnitTests(any(CodeElement.class)))
            .thenReturn(CompletableFuture.completedFuture("Unit tests"));

        // When
        CompletableFuture<String> result = documentationService.generateDocumentation(analysis);

        // Then
        assertNotNull(result);
        String resultPath = assertDoesNotThrow(() -> result.get());
        assertNotNull(resultPath);
        
        // Verify LLM service was called
        verify(llmService, atLeastOnce()).generateDocumentation(any(CodeElement.class));
    }

    @Test
    void testGenerateDocumentationWithEmptyAnalysis() throws IOException {
        // Given
        ProjectAnalysis emptyAnalysis = new ProjectAnalysis(
            "/test/empty", List.of(), System.currentTimeMillis()
        );

        // When
        CompletableFuture<String> result = documentationService.generateDocumentation(emptyAnalysis);

        // Then
        assertNotNull(result);
        assertDoesNotThrow(() -> result.get());
        
        // Should not call LLM service for empty analysis
        verify(llmService, never()).generateDocumentation(any(CodeElement.class));
    }

    @Test
    void testGenerateDocumentationWithMultipleElements() throws IOException {
        // Given
        CodeElement element1 = new CodeElement(
            CodeElementType.CLASS, "TestClass", "com.test.TestClass", "/test/TestClass.java", 
            1, "public class TestClass", "", List.of(), List.of()
        );
        CodeElement element2 = new CodeElement(
            CodeElementType.METHOD, "testMethod", "com.test.TestClass.testMethod", "/test/TestClass.java", 
            5, "public void testMethod()", "", List.of(), List.of()
        );
        
        ProjectAnalysis analysis = new ProjectAnalysis(
            "/test/project", List.of(element1, element2), System.currentTimeMillis()
        );

        // Mock ALL LLM service responses
        when(llmService.generateDocumentation(any(CodeElement.class)))
            .thenReturn(CompletableFuture.completedFuture("Generated documentation"));
        when(llmService.generateUsageExamples(any(CodeElement.class)))
            .thenReturn(CompletableFuture.completedFuture("Usage examples"));
        when(llmService.generateUnitTests(any(CodeElement.class)))
            .thenReturn(CompletableFuture.completedFuture("Unit tests"));

        // When
        CompletableFuture<String> result = documentationService.generateDocumentation(analysis);

        // Then
        assertNotNull(result);
        String resultPath = assertDoesNotThrow(() -> result.get());
        assertNotNull(resultPath);
        
        // Verify LLM service was called for each element
        verify(llmService, times(2)).generateDocumentation(any(CodeElement.class));
    }
}