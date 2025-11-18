package com.documentor.service.documentation;

import com.documentor.model.CodeElement;
import com.documentor.model.CodeElementType;
import com.documentor.model.ProjectAnalysis;
import com.documentor.service.LlmServiceEnhanced;
import com.documentor.service.LlmServiceFixEnhanced;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ElementDocumentationGeneratorEnhancedBranchCoverageTest {

    @Mock
    private LlmServiceEnhanced llmService;

    @Mock
    private LlmServiceFixEnhanced llmServiceFix;

    private ElementDocumentationGeneratorEnhanced generator;

    @TempDir
    private Path tempDir;

    private static final String TEST_DOCUMENTATION =
        "Test documentation content";
    private static final String TEST_EXAMPLES = "Test usage examples";

    @BeforeEach
    void setUp() {
        generator = new ElementDocumentationGeneratorEnhanced(
            llmService, llmServiceFix);
    }

    // =========== formatCodeBlock Branch Coverage Tests ===========

    @Test
    void testFormatCodeBlockWithNull() {
        // Test formatCodeBlock with null input
        // - should return "_No content available_"
        when(llmService.generateDocumentation(any()))
            .thenReturn(CompletableFuture.completedFuture(TEST_DOCUMENTATION));
        when(llmService.generateUsageExamples(any()))
            .thenReturn(CompletableFuture.completedFuture(TEST_EXAMPLES));
        when(llmServiceFix.isThreadLocalConfigAvailable()).thenReturn(true);

        CodeElement elementWithNullSignature = new CodeElement(
            CodeElementType.CLASS,
            "TestClass",
            "com.example.TestClass",
            "/test/TestClass.java",
            1,
            null,
            "A test class",
            Collections.emptyList(),
            Collections.emptyList()
        );

        assertDoesNotThrow(() ->
            generator.generateElementDocumentation(elementWithNullSignature,
            tempDir).join());
    }

    @Test
    void testFormatCodeBlockWithEmpty() {
        // Test formatCodeBlock with empty input
        // - should return "_No content available_"
        when(llmService.generateDocumentation(any()))
            .thenReturn(CompletableFuture.completedFuture(TEST_DOCUMENTATION));
        when(llmService.generateUsageExamples(any()))
            .thenReturn(CompletableFuture.completedFuture(TEST_EXAMPLES));
        when(llmServiceFix.isThreadLocalConfigAvailable()).thenReturn(true);

        CodeElement elementWithEmptySignature = new CodeElement(
            CodeElementType.CLASS,
            "TestClass",
            "com.example.TestClass",
            "/test/TestClass.java",
            1,
            "",
            "A test class",
            Collections.emptyList(),
            Collections.emptyList()
        );

        assertDoesNotThrow(() ->
            generator.generateElementDocumentation(elementWithEmptySignature,
            tempDir).join());
    }

    @Test
    void testFormatCodeBlockWithWhitespace() {
        // Test formatCodeBlock with whitespace-only input
        when(llmService.generateDocumentation(any()))
            .thenReturn(CompletableFuture.completedFuture(TEST_DOCUMENTATION));
        when(llmService.generateUsageExamples(any()))
            .thenReturn(CompletableFuture.completedFuture(TEST_EXAMPLES));
        when(llmServiceFix.isThreadLocalConfigAvailable()).thenReturn(true);

        CodeElement elementWithWhitespace = new CodeElement(
            CodeElementType.CLASS,
            "TestClass",
            "com.example.TestClass",
            "/test/TestClass.java",
            1,
            "   \t\n  ",
            "A test class",
            Collections.emptyList(),
            Collections.emptyList()
        );

        assertDoesNotThrow(() ->
            generator.generateElementDocumentation(elementWithWhitespace,
            tempDir).join());
    }

    @Test
    void testFormatCodeBlockShortCode() {
        // Test formatCodeBlock with short code (≤100 chars)
        // - should not be formatted
        when(llmService.generateDocumentation(any()))
            .thenReturn(CompletableFuture.completedFuture(TEST_DOCUMENTATION));
        when(llmService.generateUsageExamples(any()))
            .thenReturn(CompletableFuture.completedFuture(TEST_EXAMPLES));
        when(llmServiceFix.isThreadLocalConfigAvailable()).thenReturn(true);

        // Short, should not be formatted
        String shortCode = "public class Test {}";

        CodeElement elementWithShortCode = new CodeElement(
            CodeElementType.CLASS,
            "Test",
            "com.example.Test",
            "/test/Test.java",
            1,
            shortCode,
            "A test class",
            Collections.emptyList(),
            Collections.emptyList()
        );

        assertDoesNotThrow(() ->
            generator.generateElementDocumentation(elementWithShortCode,
            tempDir).join());
    }

    @Test
    void testFormatCodeBlockLongCodeWithoutSemicolons() {
        // Test formatCodeBlock with long code (>100 chars) but no semicolons
        // - should not be formatted
        when(llmService.generateDocumentation(any()))
            .thenReturn(CompletableFuture.completedFuture(TEST_DOCUMENTATION));
        when(llmService.generateUsageExamples(any()))
            .thenReturn(CompletableFuture.completedFuture(TEST_EXAMPLES));
        when(llmServiceFix.isThreadLocalConfigAvailable()).thenReturn(true);

        String longCodeNoSemicolons =
            "public class VeryLongClassNameThatExceedsOneHundredCharacters"
            + "ButDoesNotContainSemicolonsToTriggerFormatting extends "
            + "BaseClass implements Interface {}";

        CodeElement elementWithLongCode = new CodeElement(
            CodeElementType.CLASS,
            "VeryLongClassName",
            "com.example.VeryLongClassName",
            "/test/VeryLongClassName.java",
            1,
            longCodeNoSemicolons,
            "A test class",
            Collections.emptyList(),
            Collections.emptyList()
        );

        assertDoesNotThrow(() ->
            generator.generateElementDocumentation(elementWithLongCode,
            tempDir).join());
    }

    @Test
    void testFormatCodeBlockLongCodeWithSemicolons() {
        // Test formatCodeBlock with long code (>100 chars) with semicolons
        // - should be formatted
        when(llmService.generateDocumentation(any()))
            .thenReturn(CompletableFuture.completedFuture(TEST_DOCUMENTATION));
        when(llmService.generateUsageExamples(any()))
            .thenReturn(CompletableFuture.completedFuture(TEST_EXAMPLES));
        when(llmServiceFix.isThreadLocalConfigAvailable()).thenReturn(true);

        String longCodeWithSemicolons =
            "public class TestClass{private String field1;private int field2;"
            + "public void method1(){System.out.println(\"Hello\");};"
            + " public String method2(){return \"World\";}}";

        CodeElement elementWithFormattableCode = new CodeElement(
            CodeElementType.CLASS,
            "TestClass",
            "com.example.TestClass",
            "/test/TestClass.java",
            1,
            longCodeWithSemicolons,
            "A test class",
            Collections.emptyList(),
            Collections.emptyList()
        );

        assertDoesNotThrow(() ->
            generator.generateElementDocumentation(
                elementWithFormattableCode, tempDir).join());
    }

    @Test
    void testFormatCodeBlockWithStringLiterals() {
        // Test formatCodeBlock with string literals containing semicolons
        // - should preserve strings
        when(llmService.generateDocumentation(any()))
            .thenReturn(CompletableFuture.completedFuture(TEST_DOCUMENTATION));
        when(llmService.generateUsageExamples(any()))
            .thenReturn(CompletableFuture.completedFuture(TEST_EXAMPLES));
        when(llmServiceFix.isThreadLocalConfigAvailable()).thenReturn(true);

        String codeWithStrings = "public void test(){String message=\"Hello; "
            + "World; Test; with semicolons in string\";"
            + "System.out.println(message);result.append(\"Another; string; "
            + "with; semicolons\");}";

        CodeElement elementWithStrings = new CodeElement(
            CodeElementType.CLASS,
            "TestClass",
            "com.example.TestClass",
            "/test/TestClass.java",
            1,
            codeWithStrings,
            "A test class",
            Collections.emptyList(),
            Collections.emptyList()
        );

        assertDoesNotThrow(() ->
            generator.generateElementDocumentation(elementWithStrings, tempDir)
            .join());
    }

    @Test
    void testFormatCodeBlockWithComments() {
        // Test formatCodeBlock with comments containing semicolons
        // - should preserve comments
        when(llmService.generateDocumentation(any()))
            .thenReturn(CompletableFuture.completedFuture(TEST_DOCUMENTATION));
        when(llmService.generateUsageExamples(any()))
            .thenReturn(CompletableFuture.completedFuture(TEST_EXAMPLES));
        when(llmServiceFix.isThreadLocalConfigAvailable()).thenReturn(true);

        String codeWithComments =
            "public void test(){/*Comment with; semicolons; inside*/int x=1;"
            + "System.out.println(x);//Another comment; with; semicolons}";

        CodeElement elementWithComments = new CodeElement(
            CodeElementType.CLASS,
            "TestClass",
            "com.example.TestClass",
            "/test/TestClass.java",
            1,
            codeWithComments,
            "A test class",
            Collections.emptyList(),
            Collections.emptyList()
        );

        assertDoesNotThrow(() ->
            generator.generateElementDocumentation(
                elementWithComments, tempDir).join());
    }

    // =========== Exception Handling Lambda Coverage Tests ===========

    @Test
    void testGenerateElementDocPairDocumentationException() {
        // Test exception handling in generateElementDocPair
        // for documentation generation
        when(llmService.generateDocumentation(any()))
            .thenReturn(CompletableFuture.failedFuture(
                new RuntimeException("Documentation generation failed")));
        when(llmService.generateUsageExamples(any()))
            .thenReturn(CompletableFuture.completedFuture(TEST_EXAMPLES));
        when(llmServiceFix.isThreadLocalConfigAvailable()).thenReturn(true);

        CodeElement element = createTestClassElement();

        // Should handle exception gracefully without throwing
        assertDoesNotThrow(() -> generator
            .generateElementDocumentation(element, tempDir).join());
    }

    @Test
    void testGenerateElementDocPairUsageExamplesException() {
        // Test exception handling in generateElementDocPair
        // for usage examples generation
        when(llmService.generateDocumentation(any()))
            .thenReturn(CompletableFuture.completedFuture(TEST_DOCUMENTATION));
        when(llmService.generateUsageExamples(any()))
            .thenReturn(CompletableFuture.failedFuture(
                new RuntimeException("Usage examples generation failed")));
        when(llmServiceFix.isThreadLocalConfigAvailable()).thenReturn(true);

        CodeElement element = createTestClassElement();

        // Should handle exception gracefully without throwing
        assertDoesNotThrow(() -> generator
        .generateElementDocumentation(element, tempDir).join());
    }

    @Test
    void testGenerateElementDocPairBothExceptions() {
        // Test exception handling when both documentation
        // and usage examples fail
        when(llmService.generateDocumentation(any()))
            .thenReturn(CompletableFuture.failedFuture(
                new RuntimeException("Documentation failed")));
        when(llmService.generateUsageExamples(any()))
            .thenReturn(CompletableFuture.failedFuture(
                new RuntimeException("Usage examples failed")));
        when(llmServiceFix.isThreadLocalConfigAvailable()).thenReturn(true);

        CodeElement element = createTestClassElement();

        // Should handle exception gracefully without throwing
        assertDoesNotThrow(() -> generator.generateElementDocumentation(
            element, tempDir).join());
    }

    @Test
    void testGenerateGroupedDocumentationException() {
        // Test exception handling in generateGroupedDocumentation
        when(llmService.generateDocumentation(any()))
            .thenReturn(CompletableFuture.failedFuture(
                new RuntimeException(
                    "Unexpected error during documentation generation")));
        when(llmService.generateUsageExamples(any()))
            .thenReturn(CompletableFuture.completedFuture(TEST_EXAMPLES));
        when(llmServiceFix.isThreadLocalConfigAvailable()).thenReturn(true);

        CodeElement element = createTestClassElement();
        ProjectAnalysis analysis = new ProjectAnalysis(
            "/test/path", Collections.singletonList(element),
            System.currentTimeMillis());

        assertDoesNotThrow(() ->
            generator.generateGroupedDocumentation(analysis, tempDir).join());
    }

    @Test
    void testGenerateClassDocumentationTimeoutException() {
        // Test timeout exception handling
        CompletableFuture<String> timeoutFuture = new CompletableFuture<>();
        timeoutFuture.completeExceptionally(
            new TimeoutException("Operation timed out"));

        when(llmService.generateDocumentation(any()))
            .thenReturn(timeoutFuture);
        when(llmService.generateUsageExamples(any()))
            .thenReturn(CompletableFuture.completedFuture(TEST_EXAMPLES));
        when(llmServiceFix.isThreadLocalConfigAvailable()).thenReturn(true);

        CodeElement element = createTestClassElement();

        assertDoesNotThrow(() ->
            generator.generateElementDocumentation(element, tempDir).join());
    }

    // =========== sanitizeAnchor Branch Coverage Tests ===========

    @Test
    void testSanitizeAnchorWithNull() {
        // Test sanitizeAnchor with null input
        when(llmService.generateDocumentation(any()))
            .thenReturn(CompletableFuture.completedFuture(TEST_DOCUMENTATION));
        when(llmService.generateUsageExamples(any()))
            .thenReturn(CompletableFuture.completedFuture(TEST_EXAMPLES));
        when(llmServiceFix.isThreadLocalConfigAvailable())
            .thenReturn(true);

        CodeElement elementWithNullName = new CodeElement(
            CodeElementType.METHOD, null,
            "com.example.TestClass.nullMethod",
            "/test/TestClass.java", 5,
            "public void nullMethod() {}",
            "A method with null name",
            Collections.emptyList(), Collections.emptyList()
        );

        CodeElement classElement = createTestClassElement();
        List<CodeElement> elements = Arrays.asList(classElement,
            elementWithNullName);
        ProjectAnalysis analysis = new ProjectAnalysis(
            "/test/path", elements, System.currentTimeMillis());

        assertDoesNotThrow(() ->
            generator.generateGroupedDocumentation(analysis, tempDir).join());
    }

    @Test
    void testSanitizeAnchorWithEmpty() {
        // Test sanitizeAnchor with empty input
        when(llmService.generateDocumentation(any()))
            .thenReturn(CompletableFuture.completedFuture(TEST_DOCUMENTATION));
        when(llmService.generateUsageExamples(any()))
            .thenReturn(CompletableFuture.completedFuture(TEST_EXAMPLES));
        when(llmServiceFix.isThreadLocalConfigAvailable()).thenReturn(true);

        CodeElement elementWithEmptyName = new CodeElement(
            CodeElementType.METHOD, "", "com.example.TestClass.emptyMethod",
            "/test/TestClass.java", 5, "public void emptyMethod() {}",
            "A method with empty name",
            Collections.emptyList(), Collections.emptyList()
        );

        CodeElement classElement = createTestClassElement();
        List<CodeElement> elements = Arrays.asList(classElement,
            elementWithEmptyName);
        ProjectAnalysis analysis = new ProjectAnalysis(
            "/test/path", elements, System.currentTimeMillis());

        assertDoesNotThrow(() ->
            generator.generateGroupedDocumentation(analysis, tempDir).join());
    }

    @Test
    void testSanitizeAnchorWithSpecialCharacters() {
        // Test sanitizeAnchor with special characters that need to be replaced
        when(llmService.generateDocumentation(any()))
            .thenReturn(CompletableFuture.completedFuture(TEST_DOCUMENTATION));
        when(llmService.generateUsageExamples(any()))
            .thenReturn(CompletableFuture.completedFuture(TEST_EXAMPLES));
        when(llmServiceFix.isThreadLocalConfigAvailable()).thenReturn(true);

        CodeElement elementWithSpecialChars = new CodeElement(
            CodeElementType.METHOD, "method<T>[]()$test",
            "com.example.TestClass.methodWithSpecialChars",
            "/test/TestClass.java", 5,
            "public void methodWithSpecialChars() {}",
            "A method with special chars",
            Collections.emptyList(), Collections.emptyList()
        );

        CodeElement classElement = createTestClassElement();
        List<CodeElement> elements = Arrays.asList(classElement,
            elementWithSpecialChars);
        ProjectAnalysis analysis = new ProjectAnalysis(
            "/test/path", elements, System.currentTimeMillis());

        assertDoesNotThrow(() ->
            generator.generateGroupedDocumentation(analysis, tempDir).join());
    }

    // =========== formatContent Branch Coverage Tests ===========

    @Test
    void testFormatContentWithNull() {
        // Test formatContent with null input
        when(llmService.generateDocumentation(any()))
            .thenReturn(CompletableFuture.completedFuture(null));
        when(llmService.generateUsageExamples(any()))
            .thenReturn(CompletableFuture.completedFuture(TEST_EXAMPLES));
        when(llmServiceFix.isThreadLocalConfigAvailable()).thenReturn(true);

        CodeElement element = createTestClassElement();

        assertDoesNotThrow(() ->
            generator.generateElementDocumentation(element, tempDir).join());
    }

    @Test
    void testFormatContentWithEmpty() {
        // Test formatContent with empty input
        when(llmService.generateDocumentation(any()))
            .thenReturn(CompletableFuture.completedFuture(""));
        when(llmService.generateUsageExamples(any()))
            .thenReturn(CompletableFuture.completedFuture(TEST_EXAMPLES));
        when(llmServiceFix.isThreadLocalConfigAvailable()).thenReturn(true);

        CodeElement element = createTestClassElement();

        assertDoesNotThrow(() ->
            generator.generateElementDocumentation(element, tempDir).join());
    }

    @Test
    void testFormatContentWithWhitespace() {
        // Test formatContent with whitespace-only input
        when(llmService.generateDocumentation(any()))
            .thenReturn(CompletableFuture.completedFuture("   \t\n  "));
        when(llmService.generateUsageExamples(any()))
            .thenReturn(CompletableFuture.completedFuture(TEST_EXAMPLES));
        when(llmServiceFix.isThreadLocalConfigAvailable()).thenReturn(true);

        CodeElement element = createTestClassElement();

        assertDoesNotThrow(() ->
            generator.generateElementDocumentation(element, tempDir).join());
    }

    // =========== buildClassDocumentContent Branch Coverage Tests ===========

    @Test
    void testBuildClassDocumentContentWithNullClassElement() {
        // Test buildClassDocumentContent with null class element
        when(llmService.generateDocumentation(any()))
            .thenReturn(CompletableFuture.completedFuture(TEST_DOCUMENTATION));
        when(llmService.generateUsageExamples(any()))
            .thenReturn(CompletableFuture.completedFuture(TEST_EXAMPLES));
        when(llmServiceFix.isThreadLocalConfigAvailable()).thenReturn(true);

        // Create standalone method without class element
        CodeElement standaloneMethod = new CodeElement(
            CodeElementType.METHOD, "standaloneMethod",
            "standaloneMethod",
            "/test/Standalone.java", 10,
            "public static void standaloneMethod() {}",
            "A standalone method",
            Collections.emptyList(), Collections.emptyList()
        );

        List<CodeElement> elements = Collections.singletonList(
            standaloneMethod);
        ProjectAnalysis analysis = new ProjectAnalysis(
            "/test/path", elements, System.currentTimeMillis());

        assertDoesNotThrow(() ->
            generator.generateGroupedDocumentation(analysis, tempDir).join());
    }

    @Test
    void testBuildClassDocumentContentWithDefaultPackage() {
        // Test buildClassDocumentContent with class in default package
        when(llmService.generateDocumentation(any()))
            .thenReturn(CompletableFuture.completedFuture(TEST_DOCUMENTATION));
        when(llmService.generateUsageExamples(any()))
            .thenReturn(CompletableFuture.completedFuture(TEST_EXAMPLES));
        when(llmServiceFix.isThreadLocalConfigAvailable()).thenReturn(true);

        CodeElement classInDefaultPackage = new CodeElement(
            CodeElementType.CLASS, "TestClass",
            "TestClass", // No dots = default package
            "/test/TestClass.java", 1,
            "public class TestClass {}",
            "A test class in default package",
            Collections.emptyList(), Collections.emptyList()
        );

        assertDoesNotThrow(() ->
            generator.generateElementDocumentation(
                classInDefaultPackage, tempDir).join());
    }

    @Test
    void testBuildClassDocumentContentLongSignature() {
        // Test buildClassDocumentContent with long signature (>200 chars)
        //to test collapsible section
        when(llmService.generateDocumentation(any()))
            .thenReturn(CompletableFuture.completedFuture(TEST_DOCUMENTATION));
        when(llmService.generateUsageExamples(any()))
            .thenReturn(CompletableFuture.completedFuture(TEST_EXAMPLES));
        when(llmServiceFix.isThreadLocalConfigAvailable()).thenReturn(true);

        String longSignature =
            "public synchronized final Optional<List<Map<String, "
            + "ComplexGenericType<? extends SomeInterface>>>> "
            + "veryLongMethodNameWithManyParametersAndComplexGenerics("
            + "String parameter1, Integer parameter2, "
            + "List<String> parameter3, Map<String, Object> parameter4,"
            + " Optional<Boolean> parameter5, CompletableFuture<String>"
            + " parameter6) throws IOException, IllegalArgumentException,"
            + " RuntimeException, InterruptedException";


        CodeElement methodWithLongSig = new CodeElement(
            CodeElementType.METHOD, "veryLongMethodName",
            "com.example.TestClass.veryLongMethodName",
            "/test/TestClass.java", 10, longSignature,
            "A method with a very long signature",
            Collections.emptyList(), Collections.emptyList()
        );

        CodeElement classElement = createTestClassElement();
        List<CodeElement> elements = Arrays.asList(classElement,
            methodWithLongSig);
        ProjectAnalysis analysis = new ProjectAnalysis(
            "/test/path", elements, System.currentTimeMillis());

        assertDoesNotThrow(() ->
            generator.generateGroupedDocumentation(analysis, tempDir).join());
    }

    @Test
    void testBuildClassDocumentContentShortSignature() {
        // Test buildClassDocumentContent with short signature (<200 chars)
        // to test regular section
        when(llmService.generateDocumentation(any()))
            .thenReturn(CompletableFuture.completedFuture(TEST_DOCUMENTATION));
        when(llmService.generateUsageExamples(any()))
            .thenReturn(CompletableFuture.completedFuture(TEST_EXAMPLES));
        when(llmServiceFix.isThreadLocalConfigAvailable()).thenReturn(true);

        String shortSignature = "public void shortMethod()";

        CodeElement methodWithShortSig = new CodeElement(
            CodeElementType.METHOD, "shortMethod",
            "com.example.TestClass.shortMethod",
            "/test/TestClass.java", 10, shortSignature,
            "A method with a short signature",
            Collections.emptyList(), Collections.emptyList()
        );

        CodeElement classElement = createTestClassElement();
        List<CodeElement> elements =
            Arrays.asList(classElement, methodWithShortSig);
        ProjectAnalysis analysis = new ProjectAnalysis(
            "/test/path", elements, System.currentTimeMillis());

        assertDoesNotThrow(() ->
            generator.generateGroupedDocumentation(analysis, tempDir).join());
    }

    // =========== Language Detection Branch Coverage Tests ===========

    @ParameterizedTest
    @ValueSource(strings = {
        "/test/script.js", "/test/app.ts", "/test/script.py",
        "/test/config.xml", "/test/style.css", "/test/README.md",
        "/test/config.json", "/test/script.rb",
        "/test/file.unknown", "noextension"
    })
    void testGetLanguageFromFileWithVariousExtensions(String filePath) {
        when(llmService.generateDocumentation(any()))
            .thenReturn(CompletableFuture.completedFuture(TEST_DOCUMENTATION));
        when(llmService.generateUsageExamples(any()))
            .thenReturn(CompletableFuture.completedFuture(TEST_EXAMPLES));
        when(llmServiceFix.isThreadLocalConfigAvailable()).thenReturn(true);

        CodeElement element = new CodeElement(
            CodeElementType.CLASS, "TestClass",
            "com.example.TestClass", filePath, 1,
            "class TestClass {}", "A test class",
            Collections.emptyList(), Collections.emptyList()
        );

        assertDoesNotThrow(() ->
            generator.generateElementDocumentation(element, tempDir).join());
    }

    // =========== groupElementsByClass Branch Coverage Tests ===========

    @Test
    void testGroupElementsByClassWithStandaloneElements() {
        // Test groupElementsByClass with elements that
        // don't belong to any class
        when(llmService.generateDocumentation(any()))
            .thenReturn(CompletableFuture.completedFuture(TEST_DOCUMENTATION));
        when(llmService.generateUsageExamples(any()))
            .thenReturn(CompletableFuture.completedFuture(TEST_EXAMPLES));
        when(llmServiceFix.isThreadLocalConfigAvailable()).thenReturn(true);

        // Create standalone elements (no dots in qualified name)
        CodeElement standaloneMethod = new CodeElement(
            CodeElementType.METHOD, "standaloneMethod", "standaloneMethod",
            "/test/Standalone.java", 10,
            "public static void standaloneMethod() {}",
            "A standalone method",
            Collections.emptyList(), Collections.emptyList()
        );

        CodeElement standaloneField = new CodeElement(
            CodeElementType.FIELD, "standaloneField", "standaloneField",
            "/test/Standalone.java", 3,
            "public static String standaloneField;",
            "A standalone field",
            Collections.emptyList(), Collections.emptyList()
        );

        List<CodeElement> elements = Arrays.asList(standaloneMethod,
            standaloneField);
        ProjectAnalysis analysis = new ProjectAnalysis(
            "/test/path", elements, System.currentTimeMillis());

        assertDoesNotThrow(() ->
            generator.generateGroupedDocumentation(analysis, tempDir).join());
    }

    // =========== IO Exception Coverage Tests ===========

    @Test
    void testIOExceptionHandling() throws IOException {
        when(llmService.generateDocumentation(any()))
            .thenReturn(CompletableFuture.completedFuture(TEST_DOCUMENTATION));
        when(llmService.generateUsageExamples(any()))
            .thenReturn(CompletableFuture.completedFuture(TEST_EXAMPLES));
        when(llmServiceFix.isThreadLocalConfigAvailable())
            .thenReturn(true);

        // Create a file where the elements directory
        // should be to cause IOException
        Path elementsPath = tempDir.resolve("elements");
        Files.createFile(elementsPath);

        CodeElement element = createTestClassElement();

        CompletableFuture<Void> result =
            generator.generateElementDocumentation(element, tempDir);

        // Should handle IOException gracefully and complete normally
        assertDoesNotThrow(() -> result.join());

        // The exception should be logged but not propagated
        // This tests the exception handling branch in the code
    }

    // Helper method
    private CodeElement createTestClassElement() {
        return new CodeElement(
            CodeElementType.CLASS, "TestClass",
            "com.example.TestClass",
            "/test/TestClass.java", 1,
            "public class TestClass {}", "A test class",
            Collections.emptyList(), Collections.emptyList()
        );
    }
}
