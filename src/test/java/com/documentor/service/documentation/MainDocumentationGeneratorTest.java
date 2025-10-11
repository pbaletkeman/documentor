package com.documentor.service.documentation;

import com.documentor.config.DocumentorConfig;
import com.documentor.config.model.AnalysisSettings;
import com.documentor.config.model.LlmModelConfig;
import com.documentor.config.model.OutputSettings;
import com.documentor.model.CodeElement;
import com.documentor.model.CodeElementType;
import com.documentor.model.ProjectAnalysis;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class MainDocumentationGeneratorTest {

    // Test constants for magic number violations
    private static final int MAX_DEPTH = 3;
    private static final int MAX_TOKENS = 500;
    private static final int TIMEOUT_SECONDS = 10;
    private static final int LINE_NUMBER_ONE = 1;

    private MainDocumentationGenerator generator;
    private DocumentorConfig config;

    @BeforeEach
    void setUp() {
        OutputSettings outputSettings = new OutputSettings("out", "markdown", true, false);
        AnalysisSettings analysisSettings = new AnalysisSettings(true, MAX_DEPTH, List.of("**/*.java"), List.of());
        LlmModelConfig model = new LlmModelConfig("m", "ollama", "http://x", null, MAX_TOKENS, TIMEOUT_SECONDS);
        config = new DocumentorConfig(List.of(model), outputSettings, analysisSettings);
        generator = new MainDocumentationGenerator(config);
    }

    @Test
    void testGenerateMainDocumentationContainsSections() {
        CodeElement e1 = new CodeElement(CodeElementType.CLASS, "TestClass", "com.example.TestClass",
                "/src/TestClass.java", LINE_NUMBER_ONE, "public class TestClass{}", "",
                List.of(), List.of());
        ProjectAnalysis analysis = new ProjectAnalysis("/project/path", List.of(e1), System.currentTimeMillis());

        CompletableFuture<String> fut = generator.generateMainDocumentation(analysis);
        String doc = fut.join();

        assertNotNull(doc);
        assertTrue(doc.contains("Project Statistics"));
        assertTrue(doc.contains("API Reference"));
        assertTrue(doc.contains("Usage Examples"));
        assertTrue(doc.contains("TestClass"));
    }

    @Test
    void testGenerateMainDocumentationWithoutIcons() {
        MockitoAnnotations.openMocks(this);

        // Create mocked output settings that return false for includeIcons
        OutputSettings mockOutputSettings = org.mockito.Mockito.mock(OutputSettings.class);
        when(mockOutputSettings.includeIcons()).thenReturn(false);
        when(mockOutputSettings.outputPath()).thenReturn("test-output");
        when(mockOutputSettings.format()).thenReturn("markdown");

        // Create config with mocked output settings
        AnalysisSettings analysisSettings = new AnalysisSettings(true, MAX_DEPTH, List.of("**/*.java"), List.of());
        LlmModelConfig model = new LlmModelConfig("m", "ollama", "http://x", null, MAX_TOKENS, TIMEOUT_SECONDS);
        DocumentorConfig mockConfig = new DocumentorConfig(List.of(model), mockOutputSettings, analysisSettings);

        MainDocumentationGenerator generatorWithMock = new MainDocumentationGenerator(mockConfig);

        CodeElement e1 = new CodeElement(CodeElementType.CLASS, "TestClass", "com.example.TestClass",
                "/src/TestClass.java", 1, "public class TestClass{}", "", List.of(), List.of());
        ProjectAnalysis analysis = new ProjectAnalysis("/project/path", List.of(e1), System.currentTimeMillis());

        CompletableFuture<String> fut = generatorWithMock.generateMainDocumentation(analysis);
        String doc = fut.join();

        assertNotNull(doc);
        // Check that icons are NOT included when includeIcons() returns false
        assertFalse(doc.contains("📚"), "Should not contain book icon when icons disabled");
        assertFalse(doc.contains("📊"), "Should not contain chart icon when icons disabled");
        assertFalse(doc.contains("📦"), "Should not contain package icon when icons disabled");
        assertFalse(doc.contains("🔧"), "Should not contain tool icon when icons disabled");
        assertFalse(doc.contains("📋"), "Should not contain clipboard icon when icons disabled");
        assertFalse(doc.contains("💡"), "Should not contain lightbulb icon when icons disabled");

        // But the content should still be there
        assertTrue(doc.contains("Project Statistics"));
        assertTrue(doc.contains("API Reference"));
        assertTrue(doc.contains("Usage Examples"));
        assertTrue(doc.contains("TestClass"));
    }
}
