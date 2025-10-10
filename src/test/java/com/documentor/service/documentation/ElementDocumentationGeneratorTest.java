package com.documentor.service.documentation;

import com.documentor.model.CodeElement;
import com.documentor.model.CodeElementType;
import com.documentor.service.LlmService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

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

    @ParameterizedTest
    @CsvSource({
        "/src/TestClass.java, java",
        "/src/test_script.py, python", 
        "/src/config.xml, text",
        "/readme.txt, text",
        "noextension, text"
    })
    void testGetLanguageFromFilePath(String filePath, String expectedLanguage) throws Exception {
        // Create element with specific file path to test getLanguageFromFile method
        CodeElement element = new CodeElement(CodeElementType.METHOD, "testMethod", "com.example.TestClass.testMethod",
            filePath, 10, "public void testMethod() {}", "", List.of(), List.of());

        when(llmService.generateDocumentation(any())).thenReturn(CompletableFuture.completedFuture("Doc content"));
        when(llmService.generateUsageExamples(any())).thenReturn(CompletableFuture.completedFuture("Usage content"));

        generator.generateElementDocumentation(element, tempDir).join();

        // Verify the file was created and contains the expected language specifier
        Path elementsDir = tempDir.resolve("elements");
        Path createdFile = Files.list(elementsDir).findFirst().orElseThrow();
        String content = Files.readString(createdFile);
        
        // Check that the code block uses the expected language
        assertTrue(content.contains("```" + expectedLanguage), 
            "Expected language '" + expectedLanguage + "' not found in: " + content);
    }

    @Test
    void testGenerateElementDocumentationIOException() throws Exception {
        // Create a read-only directory to simulate IOException
        Path readOnlyDir = tempDir.resolve("readonly");
        Files.createDirectories(readOnlyDir);
        
        // On Windows, we'll use a different approach - create a file where we want a directory
        Path elementsPath = readOnlyDir.resolve("elements");
        Files.createFile(elementsPath); // Create file instead of directory to cause IOException
        
        CodeElement element = new CodeElement(CodeElementType.CLASS, "TestClass", "com.example.TestClass",
            "/src/TestClass.java", 1, "public class TestClass{}", "", List.of(), List.of());

        when(llmService.generateDocumentation(any())).thenReturn(CompletableFuture.completedFuture("Doc content"));
        when(llmService.generateUsageExamples(any())).thenReturn(CompletableFuture.completedFuture("Usage content"));

        // This should throw a CompletionException wrapping a RuntimeException
        CompletionException exception = assertThrows(CompletionException.class, () -> {
            generator.generateElementDocumentation(element, readOnlyDir).join();
        });
        
        // Verify it's wrapped in RuntimeException with correct message
        assertTrue(exception.getCause() instanceof RuntimeException);
        assertEquals("Failed to write element documentation", exception.getCause().getMessage());
    }
}
