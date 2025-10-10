package com.documentor.service.documentation;

import com.documentor.model.CodeElement;
import com.documentor.model.CodeElementType;
import com.documentor.service.LlmService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ElementDocumentationGeneratorTest {

    @Mock
    private LlmService llmService;

    private ElementDocumentationGenerator generator;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        generator = new ElementDocumentationGenerator(llmService);
    }

    @Test
    void testGenerateElementDocumentationProducesFile() throws Exception {
        CodeElement element = new CodeElement(CodeElementType.CLASS, "TestClass", "com.example.TestClass",
            "/src/TestClass.java", 1, "public class TestClass{}", "", List.of(), List.of());

        when(llmService.generateDocumentation(any())).thenReturn(CompletableFuture.completedFuture("Doc content"));
        when(llmService.generateUsageExamples(any())).thenReturn(CompletableFuture.completedFuture("Usage content"));

        generator.generateElementDocumentation(element, tempDir).join();

        Path elementsDir = tempDir.resolve("elements");
        assertTrue(Files.exists(elementsDir));
        // Find a generated file
        long count = Files.list(elementsDir).count();
        assertTrue(count >= 1);
    }
}
