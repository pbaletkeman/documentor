package com.documentor.service.documentation;

import com.documentor.constants.ApplicationConstants;
import com.documentor.model.CodeElement;
import com.documentor.model.CodeElementType;
import com.documentor.model.ProjectAnalysis;
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
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ElementDocumentationGeneratorTest {

    // Test constants for magic number violations
    private static final int LINE_NUMBER_TEN = 10;
    private static final String TEST_DOCUMENTATION = "Doc content";
    private static final String TEST_EXAMPLES = "Usage content";

    @Mock
    private LlmService llmService;

    private ElementDocumentationGenerator generator;

    @TempDir
    private Path tempDir;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        generator = new ElementDocumentationGenerator(llmService);
    }

    @Test
    void testGenerateElementDocumentationProducesFile() throws Exception {
        // Create a class element
        CodeElement element = new CodeElement(
            CodeElementType.CLASS,
            "TestClass",
            "com.example.TestClass",
            "/src/TestClass.java",
            1,
            "public class TestClass{}",
            "",
            List.of(),
            List.of()
        );

        when(llmService.generateDocumentation(any()))
            .thenReturn(CompletableFuture.completedFuture(TEST_DOCUMENTATION));
        when(llmService.generateUsageExamples(any()))
            .thenReturn(CompletableFuture.completedFuture(TEST_EXAMPLES));

        // Generate documentation using the single element method
        generator.generateElementDocumentation(element, tempDir).join();

        // Verify the elements directory was created
        Path elementsDir = tempDir.resolve("elements");
        assertTrue(Files.exists(elementsDir),
            "Elements directory should exist");

        // Verify the class file was created
        Path classFile = elementsDir.resolve("class-TestClass.md");
        assertTrue(Files.exists(classFile),
            "Class documentation file should exist");

        // Verify the content contains the expected documentation
        String content = Files.readString(classFile);
        assertTrue(content.contains(TEST_DOCUMENTATION),
            "Should contain class documentation");
        assertTrue(content.contains(TEST_EXAMPLES),
            "Should contain usage examples");
    }

    @Test
    void testEmptyElementsList() throws IOException {
        // Create an empty list of elements
        List<CodeElement> emptyList = List.of();
        ProjectAnalysis emptyAnalysis = new ProjectAnalysis(
            "/empty-project", emptyList,
            System.currentTimeMillis());

        // Create the elements directory to avoid file not found exception
        Files.createDirectories(tempDir.resolve("elements"));

        // Should complete normally without exceptions
        generator.generateGroupedDocumentation(emptyAnalysis, tempDir).join();

        // No files should be created since there are no elements
        Path elementsDir = tempDir.resolve("elements");
        assertEquals(0, Files.list(elementsDir).count(),
            "No files should be created for empty element list");
    }

    @Test
    void testGenerateGroupedDocumentation() throws Exception {
        // Create a class with methods and fields
        CodeElement classElement = new CodeElement(
            CodeElementType.CLASS,
            "TestClass",
            "com.example.TestClass",
            "/src/TestClass.java",
            1,
            "public class TestClass{}",
            "",
            List.of(),
            List.of()
        );

        CodeElement methodElement = new CodeElement(
            CodeElementType.METHOD,
            "testMethod",
            "com.example.TestClass.testMethod",
            "/src/TestClass.java",
            LINE_NUMBER_TEN / 2,
            "public void testMethod(){}",
            "",
            List.of(),
            List.of()
        );

        CodeElement fieldElement = new CodeElement(
            CodeElementType.FIELD,
            "testField",
            "com.example.TestClass.testField",
            "/src/TestClass.java",
            LINE_NUMBER_TEN / (LINE_NUMBER_TEN
                / ApplicationConstants.DEFAULT_WORKER_THREAD_COUNT),
            "private String testField;",
            "",
            List.of(),
            List.of()
        );

        // Create project analysis with all elements
        List<CodeElement> elements = Arrays.asList(classElement,
            methodElement, fieldElement);
        ProjectAnalysis analysis = new ProjectAnalysis(
            "/project", elements, System.currentTimeMillis());

        when(llmService.generateDocumentation(any())).thenReturn(
            CompletableFuture.completedFuture(TEST_DOCUMENTATION));
        when(llmService.generateUsageExamples(any())).thenReturn(
            CompletableFuture.completedFuture(TEST_EXAMPLES));

        // Generate documentation
        generator.generateGroupedDocumentation(analysis, tempDir).join();

        // Verify output
        Path elementsDir = tempDir.resolve("elements");
        assertTrue(Files.exists(elementsDir));

        // Should generate at least one file for the class
        long fileCount = Files.list(elementsDir).count();
        assertTrue(
            fileCount >= 1,
            "Should generate at least one file for the class. Found: "
            + fileCount
        );

        // Read the file content
        Path classFilePath = elementsDir.resolve("class-TestClass.md");
        assertTrue(Files.exists(classFilePath),
            "Class documentation file should exist");

        String content = Files.readString(classFilePath);

        // Print content for debugging
        System.out.println(
            "Generated content: " + content
        );

        // Verify the content includes class, method, and field documentation
        // Check for class name with various emoji encodings (handles Windows/Linux differences)
        assertTrue(
            content.contains("TestClass"),
            "Should contain class name"
        );
        assertTrue(
            content.contains("# ") && content.contains(" TestClass"),
            "Should contain class header"
        );
        assertTrue(
            content.contains("> **Package:** `com.example`")
                || content.contains("&gt; **Package:** `com.example`"),
            "Should contain package information"
        );
        assertTrue(
            content.contains("Class Documentation"),
            "Should contain class documentation section"
        );
        assertTrue(
            content.contains("Table of Contents"),
            "Should contain a table of contents"
        );
        assertTrue(
            content.contains("testField"),
            "Should contain field name"
        );
        assertTrue(
            content.contains("testMethod"),
            "Should contain method name"
        );
    }

    @Test
    void testMultipleClassesGrouped() throws Exception {
        // Create two classes with their own methods
        CodeElement class1 = new CodeElement(
            CodeElementType.CLASS, "FirstClass",
            "com.example.FirstClass",
            "/src/FirstClass.java", 1,
            "public class FirstClass{}", "",
            List.of(), List.of()
        );

        CodeElement method1 = new CodeElement(
            CodeElementType.METHOD, "firstMethod",
            "com.example.FirstClass.firstMethod",
            "/src/FirstClass.java", LINE_NUMBER_TEN / 2,
            "public void firstMethod(){}", "",
            List.of(), List.of()
        );

        CodeElement class2 = new CodeElement(
            CodeElementType.CLASS, "SecondClass",
            "com.example.SecondClass",
            "/src/SecondClass.java", 1,
            "public class SecondClass{}", "",
            List.of(), List.of()
        );

        CodeElement method2 = new CodeElement(
            CodeElementType.METHOD, "secondMethod",
            "com.example.SecondClass.secondMethod",
            "/src/SecondClass.java", LINE_NUMBER_TEN / 2,
            "public void secondMethod(){}", "",
            List.of(), List.of()
        );

        // Create project analysis with all elements
        List<CodeElement> elements = Arrays.asList(
            class1, method1, class2, method2);
        ProjectAnalysis analysis = new ProjectAnalysis(
            "/project", elements, System.currentTimeMillis());

        when(llmService.generateDocumentation(any()))
            .thenReturn(CompletableFuture.completedFuture(TEST_DOCUMENTATION));
        when(llmService.generateUsageExamples(any()))
            .thenReturn(CompletableFuture.completedFuture(TEST_EXAMPLES));

        // Generate documentation
        generator.generateGroupedDocumentation(analysis, tempDir).join();

        // Verify output
        Path elementsDir = tempDir.resolve("elements");
        assertTrue(Files.exists(elementsDir));

        // Should generate two files, one for each class
        assertEquals(2, Files.list(elementsDir).count(),
            "Should generate exactly two files for the two classes");

        // Verify first class file
        Path class1FilePath = elementsDir.resolve("class-FirstClass.md");
        assertTrue(Files.exists(class1FilePath),
            "FirstClass documentation file should exist");
        String content1 = Files.readString(class1FilePath);
        assertTrue(content1.contains("FirstClass"),
            "Should contain first class name");
        assertTrue(content1.contains("# ") && content1.contains(" FirstClass"),
            "Should contain first class header");
        assertTrue(content1.contains("firstMethod"),
            "Should contain first method name");
        assertTrue(content1.contains("### ") && content1.contains(" firstMethod"),
            "Should contain first method header");
        assertFalse(content1.contains("secondMethod"),
            "Should not contain second method name");

        // Verify second class file
        Path class2FilePath = elementsDir.resolve("class-SecondClass.md");
        assertTrue(Files.exists(class2FilePath),
            "SecondClass documentation file should exist");
        String content2 = Files.readString(class2FilePath);
        assertTrue(
            content2.contains("# ") && content2.contains(" SecondClass"),
            "Should contain second class name");
        assertTrue(
            content2.contains("### ") && content2.contains(" secondMethod"),
            "Should contain second method name");
        assertFalse(content2.contains("firstMethod"),
            "Should not contain first method name");
    }

    @ParameterizedTest
    @CsvSource({
        "/src/TestClass.java, java",
        "/src/test_script.py, python",
        "/src/config.xml, text",
        "/readme.txt, text",
        "noextension, text"
    })
    void testGetLanguageFromFilePath(
        final String filePath, final String expectedLanguage
        ) throws Exception {
        // First, create the elements directory to avoid the IOException
        Path elementsDir = tempDir.resolve("elements");
        Files.createDirectories(elementsDir);

        // Create class element to ensure it's properly grouped
        CodeElement classElement = new CodeElement(
            CodeElementType.CLASS,
            "TestClass",
            "com.example.TestClass",
            "/src/TestClass.java",
            1,
            "public class TestClass{}",
            "",
            List.of(),
            List.of()
        );

        // Create element with specific file path to test
        // getLanguageFromFile method
        CodeElement element = new CodeElement(
            CodeElementType.METHOD,
            "testMethod",
            "com.example.TestClass.testMethod",
            filePath,
            LINE_NUMBER_TEN,
            "public void testMethod() {}",
            "",
            List.of(),
            List.of()
        );

        // Create a project analysis with both elements
        List<CodeElement> elements = Arrays.asList(classElement, element);
        ProjectAnalysis analysis = new ProjectAnalysis("/project",
            elements, System.currentTimeMillis());

        when(llmService.generateDocumentation(any()))
            .thenReturn(CompletableFuture.completedFuture("Doc content"));
        when(llmService.generateUsageExamples(any()))
            .thenReturn(CompletableFuture.completedFuture("Usage content"));

        // Use the grouped approach directly
        generator.generateGroupedDocumentation(analysis, tempDir).join();

        // Verify the file was created (should be class-TestClass.md)
        Path classFile = elementsDir.resolve("class-TestClass.md");
        assertTrue(Files.exists(classFile),
            "Class documentation file should exist");

        String content = Files.readString(classFile);

        // Check that the code block uses the expected language
        assertTrue(
            content.contains("```" + expectedLanguage),
            "Expected language '" + expectedLanguage + "' not found in: "
            + content
        );
    }

    @Test
    void testGenerateElementDocumentationIOException() throws Exception {
        // Create a read-only directory to simulate IOException
        Path readOnlyDir = tempDir.resolve("readonly");
        Files.createDirectories(readOnlyDir);

        // On Windows, we'll use a different approach
        // - create a file where we want a directory
        Path elementsPath = readOnlyDir.resolve("elements");
        // Create file instead of directory to cause IOException
        Files.createFile(elementsPath);

        CodeElement element = new CodeElement(CodeElementType.CLASS,
            "TestClass", "com.example.TestClass",
            "/src/TestClass.java", 1, "public class TestClass{}",
            "", List.of(), List.of());

        when(llmService.generateDocumentation(any()))
            .thenReturn(CompletableFuture.completedFuture("Doc content"));
        when(llmService.generateUsageExamples(any()))
            .thenReturn(CompletableFuture.completedFuture("Usage content"));

        // This should throw a CompletionException wrapping a RuntimeException
        CompletionException exception = assertThrows(
            CompletionException.class, () -> {
            generator.generateElementDocumentation(element, readOnlyDir)
            .join();
        });

        // Verify it's wrapped in RuntimeException with correct message
        assertTrue(exception.getCause() instanceof RuntimeException);
        assertEquals(
            "Failed to write class documentation",
            exception.getCause().getMessage()
        );
    }
}
