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

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

class MainDocumentationGeneratorTest {

    private MainDocumentationGenerator generator;
    private DocumentorConfig config;

    @BeforeEach
    void setUp() {
        OutputSettings outputSettings = new OutputSettings("out", "markdown", true, false);
        AnalysisSettings analysisSettings = new AnalysisSettings(true, 3, List.of("**/*.java"), List.of());
        LlmModelConfig model = new LlmModelConfig("m", "ollama", "http://x", null, 500, 10);
        config = new DocumentorConfig(List.of(model), outputSettings, analysisSettings);
        generator = new MainDocumentationGenerator(config);
    }

    @Test
    void testGenerateMainDocumentationContainsSections() {
        CodeElement e1 = new CodeElement(CodeElementType.CLASS, "TestClass", "com.example.TestClass", "/src/TestClass.java", 1, "public class TestClass{}", "", List.of(), List.of());
        ProjectAnalysis analysis = new ProjectAnalysis("/project/path", List.of(e1), System.currentTimeMillis());

        CompletableFuture<String> fut = generator.generateMainDocumentation(analysis);
        String doc = fut.join();

        assertNotNull(doc);
        assertTrue(doc.contains("Project Statistics"));
        assertTrue(doc.contains("API Reference"));
        assertTrue(doc.contains("Usage Examples"));
        assertTrue(doc.contains("TestClass"));
    }
}
