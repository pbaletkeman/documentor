
package com.documentor.service.documentation;

import com.documentor.model.CodeElement;
import com.documentor.model.CodeElementType;
import com.documentor.model.ProjectAnalysis;
import com.documentor.service.LlmService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Enhanced coverage tests for ElementDocumentationGenerator to achieve >70%
 * branch coverage. Focuses on testing edge cases and branch conditions not
 * covered in the main test file.
 */
class ElementDocumentationGeneratorEnhancedCoverageTest {

    @Mock
    private LlmService llmService;

    private ElementDocumentationGenerator generator;

    @TempDir
    private Path tempDir;

    private static final String TEST_DOCUMENTATION =
        "Test documentation content";
    private static final String TEST_EXAMPLES = "Test usage examples";

        private static final int MANY_FIELDS_COUNT = 15;
        private static final int MANY_METHODS_COUNT = 15;
        private static final int STANDALONE_FIELD_LINE = 3;
        private static final int STANDALONE_METHOD_LINE = 10;
        private static final int LINE_NUMBER_5 = 5;
        private static final int LINE_NUMBER_10 = 10;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        generator = new ElementDocumentationGenerator(llmService);
    }

    @Test
    void testGenerateElementDocumentationWithNullLlmService() {
        ElementDocumentationGenerator generatorWithNullLlm =
                new ElementDocumentationGenerator(null);

        CodeElement element = createTestClassElement();

        CompletableFuture<Void> result =
                generatorWithNullLlm.generateElementDocumentation(element,
                        tempDir);

        // Should complete and return null without throwing exception
        assertNotNull(result);
        result.join(); // Should not throw
    }

    @Test
    void testGenerateGroupedDocumentationWithNullLlmService() {
        ElementDocumentationGenerator generatorWithNullLlm =
                new ElementDocumentationGenerator(null);

        ProjectAnalysis analysis = createTestProjectAnalysis();

        CompletableFuture<Void> result =
                generatorWithNullLlm.generateGroupedDocumentation(analysis,
                        tempDir);

        // Should complete and return null without throwing exception
        assertNotNull(result);
        result.join(); // Should not throw
    }

    @Test
    void testFormatContentWithNull() throws Exception {
        when(llmService.generateDocumentation(any()))
                .thenReturn(CompletableFuture.completedFuture(null));
        when(llmService.generateUsageExamples(any()))
                .thenReturn(CompletableFuture.completedFuture(TEST_EXAMPLES));

        CodeElement element = createTestClassElement();

        generator.generateElementDocumentation(element, tempDir).join();

        // Verify file was created
        Path elementsDir = tempDir.resolve("elements");
        assertTrue(Files.exists(elementsDir));

        Path classFile = elementsDir.resolve("class-TestClass.md");
        assertTrue(Files.exists(classFile));

        String content = Files.readString(classFile);
        assertTrue(content.contains("_No content available_"),
                "Should handle null content gracefully");
    }

    @Test
    void testFormatContentWithEmpty() throws Exception {
        when(llmService.generateDocumentation(any()))
                .thenReturn(CompletableFuture.completedFuture(""));
        when(llmService.generateUsageExamples(any()))
                .thenReturn(CompletableFuture.completedFuture(TEST_EXAMPLES));

        CodeElement element = createTestClassElement();

        generator.generateElementDocumentation(element, tempDir).join();

        // Verify file was created
        Path elementsDir = tempDir.resolve("elements");
        assertTrue(Files.exists(elementsDir));

        Path classFile = elementsDir.resolve("class-TestClass.md");
        assertTrue(Files.exists(classFile));

        String content = Files.readString(classFile);
        assertTrue(content.contains("_No content available_"),
                "Should handle empty content gracefully");
    }

    @Test
    void testFormatContentWithErrorMessage() throws Exception {
        when(llmService.generateDocumentation(any()))
                .thenReturn(CompletableFuture.completedFuture(
                        "Error: Failed to generate"));
        when(llmService.generateUsageExamples(any()))
                .thenReturn(CompletableFuture.completedFuture(TEST_EXAMPLES));

        CodeElement element = createTestClassElement();

        generator.generateElementDocumentation(element, tempDir).join();

        // Verify file was created
        Path elementsDir = tempDir.resolve("elements");
        assertTrue(Files.exists(elementsDir));

        Path classFile = elementsDir.resolve("class-TestClass.md");
        assertTrue(Files.exists(classFile));

        String content = Files.readString(classFile);
        assertTrue(content.contains("Error: Failed to generate"),
                "Should preserve error messages");
    }

    @Test
    void testFormatContentWithTimeoutMessage() throws Exception {
        when(llmService.generateDocumentation(any()))
                .thenReturn(CompletableFuture.completedFuture(
                        "Timeout occurred"));
        when(llmService.generateUsageExamples(any()))
                .thenReturn(CompletableFuture.completedFuture(TEST_EXAMPLES));

        CodeElement element = createTestClassElement();

        generator.generateElementDocumentation(element, tempDir).join();

        // Verify file was created
        Path elementsDir = tempDir.resolve("elements");
        assertTrue(Files.exists(elementsDir));

        Path classFile = elementsDir.resolve("class-TestClass.md");
        assertTrue(Files.exists(classFile));

        String content = Files.readString(classFile);
        assertTrue(content.contains("Timeout occurred"),
                "Should preserve timeout messages");
    }

    @Test
    void testFormatCodeBlockWithNull() throws Exception {
        when(llmService.generateDocumentation(any()))
                .thenReturn(CompletableFuture.completedFuture(
                        TEST_DOCUMENTATION));
        when(llmService.generateUsageExamples(any()))
                .thenReturn(CompletableFuture.completedFuture(TEST_EXAMPLES));

        // Create element with null signature to test formatCodeBlock
        // with null input
        CodeElement elementWithNullSignature = new CodeElement(
            CodeElementType.CLASS,
            "TestClass",
            "com.example.TestClass",
            "/test/TestClass.java",
            1,
            null, // null signature
            "A test class",
            Collections.emptyList(),
            Collections.emptyList()
        );

        generator.generateElementDocumentation(elementWithNullSignature,
                tempDir).join();

        // Should complete without error
        Path elementsDir = tempDir.resolve("elements");
        assertTrue(Files.exists(elementsDir));
    }

    @Test
    void testFormatCodeBlockWithEmpty() throws Exception {
        when(llmService.generateDocumentation(any()))
                .thenReturn(CompletableFuture.completedFuture(
                        TEST_DOCUMENTATION));
        when(llmService.generateUsageExamples(any()))
                .thenReturn(CompletableFuture.completedFuture(TEST_EXAMPLES));

        // Create element with empty signature to test formatCodeBlock
        // with empty input
        CodeElement elementWithEmptySignature = new CodeElement(
            CodeElementType.CLASS,
            "TestClass",
            "com.example.TestClass",
            "/test/TestClass.java",
            1,
            "", // empty signature
            "A test class",
            Collections.emptyList(),
            Collections.emptyList()
        );

        generator.generateElementDocumentation(elementWithEmptySignature,
        tempDir).join();

        // Should complete without error
        Path elementsDir = tempDir.resolve("elements");
        assertTrue(Files.exists(elementsDir));
    }

    @Test
    void testFormatCodeBlockWithLongCompressedJavaCode() throws Exception {
        when(llmService.generateDocumentation(any()))
                .thenReturn(CompletableFuture.completedFuture(
                        TEST_DOCUMENTATION));
        when(llmService.generateUsageExamples(any()))
                .thenReturn(CompletableFuture.completedFuture(TEST_EXAMPLES));

        // Create a long, compressed Java code signature
        // (>100 chars with semicolons)
        String longCompressedCode = "public class TestClass{private "
                + "String field1; private int field2;public void method1(){"
                + "System.out.println(\"Hello\");};public String method2(){"
                + "return \"World\";}}";

        CodeElement elementWithLongCode = new CodeElement(
            CodeElementType.CLASS,
            "TestClass",
            "com.example.TestClass",
            "/test/TestClass.java",
            1,
            longCompressedCode,
            "A test class",
            Collections.emptyList(),
            Collections.emptyList()
        );

        generator.generateElementDocumentation(elementWithLongCode, tempDir)
                .join();

        // Verify file was created and formatted code is present
        Path elementsDir = tempDir.resolve("elements");
        assertTrue(Files.exists(elementsDir));

        Path classFile = elementsDir.resolve("class-TestClass.md");
        assertTrue(Files.exists(classFile));

        String content = Files.readString(classFile);
        // Should contain formatted Java code with line breaks
        assertTrue(content.contains("public class TestClass"),
                "Should contain the class signature");
    }

    @Test
    void testFormatCodeBlockWithStringLiterals() throws Exception {
        when(llmService.generateDocumentation(any()))
                .thenReturn(CompletableFuture.completedFuture(
                        TEST_DOCUMENTATION));
        when(llmService.generateUsageExamples(any()))
                .thenReturn(CompletableFuture.completedFuture(TEST_EXAMPLES));

        // Create code with string literals containing semicolons
        // (should not be formatted)
        String codeWithStrings = "public void test()"
                + "{String message=\"Hello; World; Test;\";"
                + "System.out.println(message);}";

        CodeElement elementWithStrings = new CodeElement(
            CodeElementType.METHOD,
            "test",
            "com.example.TestClass.test",
            "/test/TestClass.java",
            LINE_NUMBER_5,
            codeWithStrings,
            "A test method",
            Collections.emptyList(),
            Collections.emptyList()
        );

        // Also need a class element for proper grouping
        CodeElement classElement = createTestClassElement();
        List<CodeElement> elements = Arrays.asList(classElement,
                elementWithStrings);
        ProjectAnalysis analysis = new ProjectAnalysis(
                "/test/path", elements, System.currentTimeMillis());

        generator.generateGroupedDocumentation(analysis, tempDir).join();

        // Verify file was created
        Path elementsDir = tempDir.resolve("elements");
        assertTrue(Files.exists(elementsDir));

        Path classFile = elementsDir.resolve("class-TestClass.md");
        assertTrue(Files.exists(classFile));

        String content = Files.readString(classFile);
        assertTrue(content.contains("test"), "Should contain the method");
    }

    @Test
    void testSanitizeAnchorWithNull() throws Exception {
        when(llmService.generateDocumentation(any()))
                .thenReturn(CompletableFuture.completedFuture(
                        TEST_DOCUMENTATION));
        when(llmService.generateUsageExamples(any()))
                .thenReturn(CompletableFuture.completedFuture(TEST_EXAMPLES));

        // Create element with null name to test sanitizeAnchor with null input
        CodeElement elementWithNullName = new CodeElement(
            CodeElementType.METHOD,
            null, // null name
            "com.example.TestClass.nullMethod",
            "/test/TestClass.java",
            LINE_NUMBER_5,
            "public void nullMethod() {}",
            "A method with null name",
            Collections.emptyList(),
            Collections.emptyList()
        );

        // Also need a class element for proper grouping
        CodeElement classElement = createTestClassElement();
        List<CodeElement> elements = Arrays.asList(classElement,
                elementWithNullName);
        ProjectAnalysis analysis = new ProjectAnalysis("/test/path", elements,
                System.currentTimeMillis());

        generator.generateGroupedDocumentation(analysis, tempDir).join();

        // Should complete without error
        Path elementsDir = tempDir.resolve("elements");
        assertTrue(Files.exists(elementsDir));
    }

    @Test
    void testSanitizeAnchorWithEmpty() throws Exception {
        when(llmService.generateDocumentation(any()))
                .thenReturn(CompletableFuture.completedFuture(
                        TEST_DOCUMENTATION));
        when(llmService.generateUsageExamples(any()))
                .thenReturn(CompletableFuture.completedFuture(TEST_EXAMPLES));

        // Create element with empty name to test sanitizeAnchor
        // with empty input
        CodeElement elementWithEmptyName = new CodeElement(
            CodeElementType.METHOD,
            "", // empty name
            "com.example.TestClass.emptyMethod",
            "/test/TestClass.java",
            LINE_NUMBER_5,
            "public void emptyMethod() {}",
            "A method with empty name",
            Collections.emptyList(),
            Collections.emptyList()
        );

        // Also need a class element for proper grouping
        CodeElement classElement = createTestClassElement();
        List<CodeElement> elements = Arrays.asList(classElement,
                elementWithEmptyName);
        ProjectAnalysis analysis = new ProjectAnalysis("/test/path", elements,
                System.currentTimeMillis());

        generator.generateGroupedDocumentation(analysis, tempDir).join();

        // Should complete without error
        Path elementsDir = tempDir.resolve("elements");
        assertTrue(Files.exists(elementsDir));
    }

    @Test
    void testGroupElementsByClassWithStandaloneField() throws Exception {
        when(llmService.generateDocumentation(any()))
                .thenReturn(CompletableFuture.completedFuture(
                        TEST_DOCUMENTATION));
        when(llmService.generateUsageExamples(any()))
                .thenReturn(CompletableFuture.completedFuture(TEST_EXAMPLES));

        // Create standalone field without proper class
        // qualification (triggers _FIELDS_ group)
                CodeElement standaloneField = new CodeElement(
                        CodeElementType.FIELD,
                        "standaloneField",
                        "standaloneField", // no dots, so it's standalone
                        "/test/Standalone.java",
                        STANDALONE_FIELD_LINE,
                        "public static String standaloneField;",
                        "A standalone field",
                        Collections.emptyList(),
                        Collections.emptyList()
                );

        List<CodeElement> elements = Collections.singletonList(standaloneField);
        ProjectAnalysis analysis = new ProjectAnalysis("/test/path", elements,
                System.currentTimeMillis());

        generator.generateGroupedDocumentation(analysis, tempDir).join();

        // Should complete without error (though may not
        // create files for standalone elements)
        Path elementsDir = tempDir.resolve("elements");
        assertTrue(Files.exists(elementsDir));
    }

    @Test
    void testGroupElementsByClassWithStandaloneMethod() throws Exception {
        when(llmService.generateDocumentation(any()))
                .thenReturn(CompletableFuture.completedFuture(
                        TEST_DOCUMENTATION));
        when(llmService.generateUsageExamples(any()))
                .thenReturn(CompletableFuture.completedFuture(TEST_EXAMPLES));

        // Create standalone method without proper class qualification
        // (triggers _METHODS_ group)
                CodeElement standaloneMethod = new CodeElement(
                        CodeElementType.METHOD,
                        "standaloneMethod",
                        "standaloneMethod", // no dots, so it's standalone
                        "/test/Standalone.java",
                        STANDALONE_METHOD_LINE,
                        "public static void standaloneMethod() {}",
                        "A standalone method",
                        Collections.emptyList(),
                        Collections.emptyList()
                );

        List<CodeElement> elements = Collections.singletonList(
                standaloneMethod);
        ProjectAnalysis analysis = new ProjectAnalysis("/test/path", elements,
                System.currentTimeMillis());

        generator.generateGroupedDocumentation(analysis, tempDir).join();

        // Should complete without error (though may not create files
        // for standalone elements)
        Path elementsDir = tempDir.resolve("elements");
        assertTrue(Files.exists(elementsDir));
    }

    @Test
    void testBuildClassDocumentContentLongMethodSignature() throws Exception {
        when(llmService.generateDocumentation(any()))
                .thenReturn(CompletableFuture.completedFuture(
                        TEST_DOCUMENTATION));
        when(llmService.generateUsageExamples(any()))
                .thenReturn(CompletableFuture.completedFuture(TEST_EXAMPLES));

        // Create a method with a very long signature (>200 chars)
        // to test collapsible section
        String longSignature =
                "public synchronized final Optional<List<Map<String, Object>>>"
                + " veryLongMethodNameWithManyParameters(String parameter1, "
                + "Integer parameter2, List<String> parameter3, "
                + "Map<String, Object> parameter4, Optional<Boolean> "
                + "parameter5) throws IOException, IllegalArgumentException, "
                + "RuntimeException";

        CodeElement methodWithLongSig = new CodeElement(
            CodeElementType.METHOD,
            "veryLongMethodNameWithManyParameters",
            "com.example.TestClass.veryLongMethodNameWithManyParameters",
            "/test/TestClass.java",
            LINE_NUMBER_10,
            longSignature,
            "A method with a very long signature",
            Collections.emptyList(),
            Collections.emptyList()
        );

        // Also need a class element for proper grouping
        CodeElement classElement = createTestClassElement();
        List<CodeElement> elements =
                Arrays.asList(classElement, methodWithLongSig);
        ProjectAnalysis analysis = new ProjectAnalysis("/test/path", elements,
                System.currentTimeMillis());

        generator.generateGroupedDocumentation(analysis, tempDir).join();

        // Verify file was created and contains collapsible signature
        Path elementsDir = tempDir.resolve("elements");
        assertTrue(Files.exists(elementsDir));

        Path classFile = elementsDir.resolve("class-TestClass.md");
        assertTrue(Files.exists(classFile));

        String content = Files.readString(classFile);
        assertTrue(content.contains("<details>")
                && content.contains("View Method Signature"),
                "Should use collapsible section for long signatures");
    }

    @Test
    void testBuildClassDocumentContentShortMethodSignature()
        throws Exception {
        when(llmService.generateDocumentation(any()))
                .thenReturn(CompletableFuture.completedFuture(
                        TEST_DOCUMENTATION));
        when(llmService.generateUsageExamples(any()))
                .thenReturn(CompletableFuture.completedFuture(TEST_EXAMPLES));

        // Create a method with a short signature (<200 chars)
        // to test regular section
        String shortSignature = "public void shortMethod()";

        CodeElement methodWithShortSig = new CodeElement(
            CodeElementType.METHOD,
            "shortMethod",
            "com.example.TestClass.shortMethod",
            "/test/TestClass.java",
            LINE_NUMBER_10,
            shortSignature,
            "A method with a short signature",
            Collections.emptyList(),
            Collections.emptyList()
        );

        // Also need a class element for proper grouping
        CodeElement classElement = createTestClassElement();
        List<CodeElement> elements = Arrays.asList(classElement,
                methodWithShortSig);
        ProjectAnalysis analysis = new ProjectAnalysis("/test/path", elements,
                System.currentTimeMillis());

        generator.generateGroupedDocumentation(analysis, tempDir).join();

        // Verify file was created and does not contain collapsible signature
        Path elementsDir = tempDir.resolve("elements");
        assertTrue(Files.exists(elementsDir));

        Path classFile = elementsDir.resolve("class-TestClass.md");
        assertTrue(Files.exists(classFile));

        String content = Files.readString(classFile);
        assertFalse(content.contains("<details>")
                && content.contains("View Method Signature"),
                "Should use regular section for short signatures");
        assertTrue(content.contains("public void shortMethod()"),
                "Should contain the short signature");
    }

    @ParameterizedTest
    @ValueSource(strings = {"/test/script.rb", "/test/config.xml",
            "/test/style.css", "/test/README.md", "noextension"})
        void testGetLanguageFromFileWithVariousExtensions(final String filePath)
            throws Exception {
        when(llmService.generateDocumentation(any()))
                .thenReturn(CompletableFuture.completedFuture(
                        TEST_DOCUMENTATION));
        when(llmService.generateUsageExamples(any()))
                .thenReturn(CompletableFuture.completedFuture(TEST_EXAMPLES));

        CodeElement element = new CodeElement(
            CodeElementType.CLASS,
            "TestClass",
            "com.example.TestClass",
            filePath,
            1,
            "class TestClass {}",
            "A test class",
            Collections.emptyList(),
            Collections.emptyList()
        );

        generator.generateElementDocumentation(element, tempDir).join();

        // Verify file was created with text language for unknown extensions
        Path elementsDir = tempDir.resolve("elements");
        assertTrue(Files.exists(elementsDir));

        Path classFile = elementsDir.resolve("class-TestClass.md");
        assertTrue(Files.exists(classFile));

        String content = Files.readString(classFile);
        assertTrue(content.contains("```text"),
                "Should use text language for unknown extensions");
    }

    @Test
    void testBuildClassDocumentContentWithNullClassElement()
        throws Exception {
        when(llmService.generateDocumentation(any()))
                .thenReturn(CompletableFuture.completedFuture(
                        TEST_DOCUMENTATION));
        when(llmService.generateUsageExamples(any()))
                .thenReturn(CompletableFuture.completedFuture(TEST_EXAMPLES));

        // Create standalone elements without a class element
        CodeElement standaloneMethod = new CodeElement(
            CodeElementType.METHOD,
            "standaloneMethod",
            "standaloneMethod", // no dots, so it's standalone
            "/test/Standalone.java",
            LINE_NUMBER_10,
            "public static void standaloneMethod() {}",
            "A standalone method",
            Collections.emptyList(),
            Collections.emptyList()
        );

        List<CodeElement> elements =
        Collections.singletonList(standaloneMethod);
        ProjectAnalysis analysis = new ProjectAnalysis("/test/path", elements,
                System.currentTimeMillis());

        generator.generateGroupedDocumentation(analysis, tempDir).join();

        // Should complete without error
        Path elementsDir = tempDir.resolve("elements");
        assertTrue(Files.exists(elementsDir));
    }

    @Test
    void testGenerateClassDocumentationWithIOException() throws Exception {
        when(llmService.generateDocumentation(any()))
                .thenReturn(CompletableFuture.completedFuture(
                        TEST_DOCUMENTATION));
        when(llmService.generateUsageExamples(any()))
                .thenReturn(CompletableFuture.completedFuture(TEST_EXAMPLES));

        // Create a file where the elements directory should be
        // to cause IOException
        Path elementsPath = tempDir.resolve("elements");
        Files.createFile(elementsPath);

        CodeElement element = createTestClassElement();

        CompletableFuture<Void> result =
                generator.generateElementDocumentation(element, tempDir);

        // Should throw CompletionException wrapping RuntimeException
        CompletionException exception = assertThrows(CompletionException.class,
                () -> result.join());
        assertTrue(exception.getCause() instanceof RuntimeException);
        assertEquals("Failed to write class documentation",
                exception.getCause().getMessage());
    }

    @Test
    void testBuildClassDocumentContentWithManyFields() throws Exception {
        when(llmService.generateDocumentation(any()))
                .thenReturn(CompletableFuture.completedFuture(
                        TEST_DOCUMENTATION));
        when(llmService.generateUsageExamples(any()))
                .thenReturn(CompletableFuture.completedFuture(TEST_EXAMPLES));

        // Create class with many fields (>10) to test line
        // break logic in table of contents
        CodeElement classElement = createTestClassElement();
        List<CodeElement> elements = new ArrayList<>();
        elements.add(classElement);

        // Add 15 fields to trigger line break logic
        for (int i = 1; i <= MANY_FIELDS_COUNT; i++) {
            CodeElement field = new CodeElement(
                CodeElementType.FIELD,
                "field" + i,
                "com.example.TestClass.field" + i,
                "/test/TestClass.java",
                i + 2,
                "private String field" + i + ";",
                "Field " + i,
                Collections.emptyList(),
                Collections.emptyList()
            );
            elements.add(field);
        }

        ProjectAnalysis analysis = new ProjectAnalysis("/test/path", elements,
                System.currentTimeMillis());

        generator.generateGroupedDocumentation(analysis, tempDir).join();

        // Verify file was created
        Path elementsDir = tempDir.resolve("elements");
        assertTrue(Files.exists(elementsDir));

        Path classFile = elementsDir.resolve("class-TestClass.md");
        assertTrue(Files.exists(classFile));

        String content = Files.readString(classFile);
        assertTrue(content.contains("Fields") && content.contains("(15)"),
                "Should contain all 15 fields");
        // Should contain line breaks every 10 fields
        assertTrue(content.contains("field10") && content.contains("field15"),
                "Should contain all field names");
    }

    @Test
    void testBuildClassDocumentContentWithManyMethods() throws Exception {
        when(llmService.generateDocumentation(any()))
                .thenReturn(CompletableFuture.completedFuture(
                        TEST_DOCUMENTATION));
        when(llmService.generateUsageExamples(any()))
                .thenReturn(CompletableFuture.completedFuture(TEST_EXAMPLES));

        // Create class with many methods (>10) to test
        // line break logic in table of contents
        CodeElement classElement = createTestClassElement();
        List<CodeElement> elements = new ArrayList<>();
        elements.add(classElement);

        // Add 15 methods to trigger line break logic
        for (int i = 1; i <= MANY_METHODS_COUNT; i++) {
            CodeElement method = new CodeElement(
                CodeElementType.METHOD,
                "method" + i,
                "com.example.TestClass.method" + i,
                "/test/TestClass.java",
                    i + LINE_NUMBER_10,
                "public void method" + i + "() {}",
                "Method " + i,
                Collections.emptyList(),
                Collections.emptyList()
            );
            elements.add(method);
        }

        ProjectAnalysis analysis = new ProjectAnalysis("/test/path", elements,
                System.currentTimeMillis());

        generator.generateGroupedDocumentation(analysis, tempDir).join();

        // Verify file was created
        Path elementsDir = tempDir.resolve("elements");
        assertTrue(Files.exists(elementsDir));

        Path classFile = elementsDir.resolve("class-TestClass.md");
        assertTrue(Files.exists(classFile));

        String content = Files.readString(classFile);
        assertTrue(content.contains("Methods") && content.contains("(15)"),
                "Should contain all 15 methods");
        // Should contain line breaks every 10 methods
        assertTrue(content.contains("method10") && content.contains(
                "method15"),
                "Should contain all method names");
    }

    @Test
    void testBuildClassDocumentContentWithDefaultPackage() throws Exception {
        when(llmService.generateDocumentation(any()))
                .thenReturn(CompletableFuture.completedFuture(
                        TEST_DOCUMENTATION));
        when(llmService.generateUsageExamples(any()))
                .thenReturn(CompletableFuture.completedFuture(TEST_EXAMPLES));

        // Create class with no package (triggers default package logic)
        CodeElement classInDefaultPackage = new CodeElement(
            CodeElementType.CLASS,
            "TestClass",
            "TestClass", // no dots, so it's in default package
            "/test/TestClass.java",
            1,
            "public class TestClass {}",
            "A test class in default package",
            Collections.emptyList(),
            Collections.emptyList()
        );

        generator.generateElementDocumentation(classInDefaultPackage,
        tempDir).join();

        // Verify file was created with default package info
        Path elementsDir = tempDir.resolve("elements");
        assertTrue(Files.exists(elementsDir));

        Path classFile = elementsDir.resolve("class-TestClass.md");
        assertTrue(Files.exists(classFile));

        String content = Files.readString(classFile);
        assertTrue(content.contains("(default package)"),
                "Should show default package for classes "
                + "without package");
    }

    // Helper methods
    private CodeElement createTestClassElement() {
        return new CodeElement(
            CodeElementType.CLASS,
            "TestClass",
            "com.example.TestClass",
            "/test/TestClass.java",
            1,
            "public class TestClass {}",
            "A test class",
            Collections.emptyList(),
            Collections.emptyList()
        );
    }

    private ProjectAnalysis createTestProjectAnalysis() {
        CodeElement element = createTestClassElement();
        List<CodeElement> elements = Collections.singletonList(element);
        return new ProjectAnalysis("/test/path", elements,
        System.currentTimeMillis());
    }
}
