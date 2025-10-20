package com.documentor.service;

import com.documentor.model.CodeElement;
import com.documentor.model.CodeElementType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test coverage for ServiceUtils.
 * This test class ensures 100% coverage of all utility methods.
 */
class ServiceUtilsTest {

    // Test constant values
    private static final int THIRTY_THOUSAND = 30000;
    private static final int THREE = 3;
    private static final int ONE = 1;
    private static final int TWO = 2;
    private static final int FOUR = 4;
    private static final int FIVE = 5;
    private static final int TEN = 10;
    private static final int FIFTEEN = 15;
    private static final int FORTY_TWO = 42;
    private static final int ZERO = 0;
    private static final int NEGATIVE_ONE = -1;
    private static final int NEGATIVE_FIVE = -5;

    // Timeout values in milliseconds
    private static final int ONE_THOUSAND_MS = 1000;
    private static final int TWO_THOUSAND_MS = 2000;
    private static final int FOUR_THOUSAND_MS = 4000;
    private static final int EIGHT_THOUSAND_MS = 8000;
    private static final int TEN_THOUSAND_MS = 10000;
    private static final int NEGATIVE_ONE_THOUSAND_MS = -1000;
    private static final int THREE_HUNDRED_THOUSAND_MS = 300000; // 5 minutes
    private static final int THREE_HUNDRED_THOUSAND_PLUS_ONE_MS = 300001;

    @Test
    void testConstants() {
        // Test all public constants
        assertEquals("com.documentor.service", ServiceUtils.SERVICE_PACKAGE);
        assertEquals(THIRTY_THOUSAND, ServiceUtils.DEFAULT_ASYNC_TIMEOUT);
        assertEquals(THREE, ServiceUtils.MAX_RETRY_ATTEMPTS);
        assertEquals("javadoc", ServiceUtils.DOC_TYPE_JAVADOC);
        assertEquals("markdown", ServiceUtils.DOC_TYPE_MARKDOWN);
        assertEquals("plain", ServiceUtils.DOC_TYPE_PLAIN);
    }

    @Test
    void testConstructorThrowsException() {
        // Test that the private constructor throws UnsupportedOperationException
        Exception exception = assertThrows(Exception.class, () -> {
            // Use reflection to access private constructor
            var constructor = ServiceUtils.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        });

        // The exception should be either UnsupportedOperationException directly
        // or wrapped in InvocationTargetException
        assertTrue(exception instanceof UnsupportedOperationException
                   || (exception.getCause() instanceof UnsupportedOperationException),
                   "Expected UnsupportedOperationException but got: "
                   + exception.getClass());
    }

    @Test
    void testFilterByType() {
        // Create test code elements
        CodeElement classElement = new CodeElement(
            CodeElementType.CLASS, "TestClass", "com.test.TestClass",
            "TestClass.java", ONE, "class TestClass", "", List.of(), List.of()
        );

        CodeElement methodElement = new CodeElement(
            CodeElementType.METHOD, "testMethod", "com.test.TestClass.testMethod",
            "TestClass.java", TEN, "public void testMethod()", "",
            List.of(), List.of()
        );

        List<CodeElement> elements = List.of(classElement, methodElement);

        // Test filtering by CLASS type
        List<CodeElement> classElements = ServiceUtils.filterByType(elements,
                CodeElementType.CLASS);
        assertEquals(ONE, classElements.size());
        assertEquals("TestClass", classElements.get(ZERO).name());

        // Test filtering by METHOD type
        List<CodeElement> methodElements = ServiceUtils.filterByType(elements,
                CodeElementType.METHOD);
        assertEquals(ONE, methodElements.size());
        assertEquals("testMethod", methodElements.get(ZERO).name());

        // Test filtering by non-existent type
        List<CodeElement> fieldElements = ServiceUtils.filterByType(elements,
                CodeElementType.FIELD);
        assertTrue(fieldElements.isEmpty());

        // Test with null list
        List<CodeElement> nullResult = ServiceUtils.filterByType(null,
                CodeElementType.CLASS);
        assertTrue(nullResult.isEmpty());

        // Test with empty list
        List<CodeElement> emptyResult = ServiceUtils.filterByType(List.of(),
                CodeElementType.CLASS);
        assertTrue(emptyResult.isEmpty());

        // Test with null type (should return copy of all elements)
        List<CodeElement> allElements = ServiceUtils.filterByType(elements, null);
        assertEquals(TWO, allElements.size());
    }

    @Test
    void testFilterByTypeWithNullElements() {
        // Test list containing null elements
        CodeElement validElement = new CodeElement(
            CodeElementType.CLASS, "TestClass", "com.test.TestClass",
            "TestClass.java", 1, "class TestClass", "", List.of(), List.of()
        );

        List<CodeElement> elementsWithNull = new java.util.ArrayList<>();
        elementsWithNull.add(validElement);
        elementsWithNull.add(null);

        List<CodeElement> filtered = ServiceUtils.filterByType(elementsWithNull,
                CodeElementType.CLASS);

        assertEquals(1, filtered.size());
        assertEquals("TestClass", filtered.get(0).name());
    }    @Test
    void testGroupByType() {
        // Create test code elements
        CodeElement classElement1 = new CodeElement(
            CodeElementType.CLASS, "TestClass1", "com.test.TestClass1",
            "TestClass1.java", ONE, "class TestClass1", "", List.of(), List.of()
        );

        CodeElement classElement2 = new CodeElement(
            CodeElementType.CLASS, "TestClass2", "com.test.TestClass2",
            "TestClass2.java", ONE, "class TestClass2", "", List.of(), List.of()
        );

        CodeElement methodElement = new CodeElement(
            CodeElementType.METHOD, "testMethod", "com.test.TestClass1.testMethod",
            "TestClass1.java", TEN, "public void testMethod()", "",
            List.of(), List.of()
        );

        List<CodeElement> elements = List.of(classElement1, classElement2, methodElement);

        // Test grouping
        Map<CodeElementType, List<CodeElement>> grouped = ServiceUtils.groupByType(elements);

        assertEquals(TWO, grouped.size());
        assertTrue(grouped.containsKey(CodeElementType.CLASS));
        assertTrue(grouped.containsKey(CodeElementType.METHOD));

        assertEquals(TWO, grouped.get(CodeElementType.CLASS).size());
        assertEquals(ONE, grouped.get(CodeElementType.METHOD).size());

        // Test with null list
        Map<CodeElementType, List<CodeElement>> nullResult =
                ServiceUtils.groupByType(null);
        assertTrue(nullResult.isEmpty());

        // Test with empty list
        Map<CodeElementType, List<CodeElement>> emptyResult =
                ServiceUtils.groupByType(List.of());
        assertTrue(emptyResult.isEmpty());
    }

    @Test
    void testGroupByTypeWithNullElements() {
        // Test with null elements and null types
        CodeElement validElement = new CodeElement(
            CodeElementType.CLASS, "TestClass", "com.test.TestClass",
            "TestClass.java", 1, "class TestClass", "", List.of(), List.of()
        );

        CodeElement nullTypeElement = new CodeElement(
            null, "NullType", "com.test.NullType",
            "NullType.java", ONE, "class NullType", "", List.of(), List.of()
        );

        List<CodeElement> elementsWithNull = new java.util.ArrayList<>();
        elementsWithNull.add(validElement);
        elementsWithNull.add(null);
        elementsWithNull.add(nullTypeElement);

        Map<CodeElementType, List<CodeElement>> grouped =
                ServiceUtils.groupByType(elementsWithNull);

        assertEquals(ONE, grouped.size());
        assertTrue(grouped.containsKey(CodeElementType.CLASS));
        assertEquals(ONE, grouped.get(CodeElementType.CLASS).size());
    }

    @Test
    void testIsValidTimeout() {
        // Test valid timeouts
        assertTrue(ServiceUtils.isValidTimeout(ONE_THOUSAND_MS));
        assertTrue(ServiceUtils.isValidTimeout(THIRTY_THOUSAND));
        assertTrue(ServiceUtils.isValidTimeout(THREE_HUNDRED_THOUSAND_MS)); // Max 5 minutes

        // Test invalid timeouts
        assertFalse(ServiceUtils.isValidTimeout(null));
        assertFalse(ServiceUtils.isValidTimeout(ZERO));
        assertFalse(ServiceUtils.isValidTimeout(NEGATIVE_ONE_THOUSAND_MS));
        assertFalse(ServiceUtils.isValidTimeout(THREE_HUNDRED_THOUSAND_PLUS_ONE_MS)); // Over 5 minutes
    }

    @Test
    void testCalculateAdaptiveTimeout() {
        // Test with zero elements
        assertEquals(ServiceUtils.DEFAULT_ASYNC_TIMEOUT,
                ServiceUtils.calculateAdaptiveTimeout(ZERO));

        // Test with negative elements
        assertEquals(ServiceUtils.DEFAULT_ASYNC_TIMEOUT,
                ServiceUtils.calculateAdaptiveTimeout(NEGATIVE_FIVE));

        // Test with small number of elements
        int timeout10 = ServiceUtils.calculateAdaptiveTimeout(TEN);
        assertEquals(ServiceUtils.DEFAULT_ASYNC_TIMEOUT + TEN_THOUSAND_MS,
                timeout10);

        // Test capping at maximum
        int timeoutHuge = ServiceUtils.calculateAdaptiveTimeout(ONE_THOUSAND_MS);
        assertEquals(THREE_HUNDRED_THOUSAND_MS,
                timeoutHuge); // Should be capped at 5 minutes
    }

    @Test
    void testSanitizeFilePath() {
        // Test null and empty inputs
        assertEquals("", ServiceUtils.sanitizeFilePath(null));
        assertEquals("", ServiceUtils.sanitizeFilePath(""));
        assertEquals("", ServiceUtils.sanitizeFilePath("   "));

        // Test backslash to forward slash conversion
        assertEquals("com/test/TestClass.java",
                ServiceUtils.sanitizeFilePath("com\\test\\TestClass.java"));

        // Test duplicate slash removal
        assertEquals("com/test/TestClass.java",
                ServiceUtils.sanitizeFilePath("com//test///TestClass.java"));

        // Test leading slash removal
        assertEquals("com/test/TestClass.java", ServiceUtils.sanitizeFilePath("/com/test/TestClass.java"));

        // Test whitespace trimming
        assertEquals("com/test/TestClass.java", ServiceUtils.sanitizeFilePath("  com/test/TestClass.java  "));

        // Test complex path
        assertEquals("src/main/java/com/test/TestClass.java",
            ServiceUtils.sanitizeFilePath("  /src\\\\main//java\\com\\test\\TestClass.java  "));
    }

    @Test
    void testIsSupportedDocType() {
        // Test supported types
        assertTrue(ServiceUtils.isSupportedDocType("javadoc"));
        assertTrue(ServiceUtils.isSupportedDocType("markdown"));
        assertTrue(ServiceUtils.isSupportedDocType("plain"));

        // Test case insensitivity
        assertTrue(ServiceUtils.isSupportedDocType("JAVADOC"));
        assertTrue(ServiceUtils.isSupportedDocType("Markdown"));
        assertTrue(ServiceUtils.isSupportedDocType("PLAIN"));

        // Test with whitespace
        assertTrue(ServiceUtils.isSupportedDocType("  javadoc  "));
        assertTrue(ServiceUtils.isSupportedDocType("  markdown  "));

        // Test unsupported types
        assertFalse(ServiceUtils.isSupportedDocType("html"));
        assertFalse(ServiceUtils.isSupportedDocType("xml"));
        assertFalse(ServiceUtils.isSupportedDocType("json"));

        // Test null and empty
        assertFalse(ServiceUtils.isSupportedDocType(null));
        assertFalse(ServiceUtils.isSupportedDocType(""));
        assertFalse(ServiceUtils.isSupportedDocType("   "));
    }

    @Test
    void testCreateDisplayName() {
        // Test normal element
        CodeElement element = new CodeElement(
            CodeElementType.CLASS, "TestClass", "com.test.TestClass",
            "TestClass.java", ONE, "class TestClass", "", List.of(), List.of()
        );

        assertEquals("[CLASS] TestClass", ServiceUtils.createDisplayName(element));

        // Test null element
        assertEquals("Unknown Element", ServiceUtils.createDisplayName(null));

        // Test element with null name
        CodeElement nullNameElement = new CodeElement(
            CodeElementType.METHOD, null, "com.test.TestClass.method",
            "TestClass.java", TEN, "method", "", List.of(), List.of()
        );

        assertEquals("METHOD", ServiceUtils.createDisplayName(nullNameElement));

        // Test element with empty name
        CodeElement emptyNameElement = new CodeElement(
            CodeElementType.FIELD, "", "com.test.TestClass.field",
            "TestClass.java", FIVE, "field", "", List.of(), List.of()
        );

        assertEquals("FIELD", ServiceUtils.createDisplayName(emptyNameElement));

        // Test element with null type
        CodeElement nullTypeElement = new CodeElement(
            null, "TestElement", "com.test.TestElement",
            "TestElement.java", ONE, "element", "", List.of(), List.of()
        );

        assertEquals("[UNKNOWN] TestElement", ServiceUtils.createDisplayName(nullTypeElement));

        // Test element with both null name and type
        CodeElement bothNullElement = new CodeElement(
            null, null, "com.test.Unknown",
            "Unknown.java", ONE, "unknown", "", List.of(), List.of()
        );

        assertEquals("Unknown Element", ServiceUtils.createDisplayName(bothNullElement));
    }

    @Test
    void testValidateOperationParameters() {
        // Test with null operation
        assertFalse(ServiceUtils.validateOperationParameters(null, Map.of()));
        assertFalse(ServiceUtils.validateOperationParameters("", Map.of()));
        assertFalse(ServiceUtils.validateOperationParameters("   ", Map.of()));

        // Test with valid operation and null parameters
        assertTrue(ServiceUtils.validateOperationParameters("testOperation", null));

        // Test with valid operation and empty parameters
        assertTrue(ServiceUtils.validateOperationParameters("testOperation",
                Map.of()));

        // Test with valid operation and valid parameters
        Map<String, Object> validParams = new HashMap<>();
        validParams.put("param1", "value1");
        validParams.put("param2", FORTY_TWO);
        assertTrue(ServiceUtils.validateOperationParameters("testOperation",
                validParams));

        // Test generate operation with parameters
        assertTrue(ServiceUtils.validateOperationParameters("generateDocs",
                validParams));

        // Test generate operation with null values in parameters
        Map<String, Object> paramsWithNull = new HashMap<>();
        paramsWithNull.put("param1", "value1");
        paramsWithNull.put("param2", null);

        // Generate operations with null values should return false
        boolean result = ServiceUtils.validateOperationParameters("generateDocs",
                paramsWithNull);
        // The method rejects generate operations with null parameter values
        assertFalse(result); // Generate operations require non-null parameters
    }

    @Test
    void testFormatErrorMessage() {
        // Test with all parameters
        String fullMessage = ServiceUtils.formatErrorMessage("TestService", "testOperation", "Test error");
        assertEquals("[TestService] Operation 'testOperation' failed: Test error", fullMessage);

        // Test with null service name
        String noServiceMessage = ServiceUtils.formatErrorMessage(null, "testOperation", "Test error");
        assertEquals("Operation 'testOperation' failed: Test error", noServiceMessage);

        // Test with empty service name
        String emptyServiceMessage = ServiceUtils.formatErrorMessage("", "testOperation", "Test error");
        assertEquals("Operation 'testOperation' failed: Test error", emptyServiceMessage);

        // Test with null operation
        String noOperationMessage = ServiceUtils.formatErrorMessage("TestService", null, "Test error");
        assertEquals("[TestService] Operation failed: Test error", noOperationMessage);

        // Test with empty operation
        String emptyOperationMessage = ServiceUtils.formatErrorMessage("TestService", "", "Test error");
        assertEquals("[TestService] Operation failed: Test error", emptyOperationMessage);

        // Test with null cause
        String noCauseMessage = ServiceUtils.formatErrorMessage("TestService", "testOperation", null);
        assertEquals("[TestService] Operation 'testOperation' failed", noCauseMessage);

        // Test with empty cause
        String emptyCauseMessage = ServiceUtils.formatErrorMessage("TestService", "testOperation", "");
        assertEquals("[TestService] Operation 'testOperation' failed", emptyCauseMessage);

        // Test with all null values
        String allNullMessage = ServiceUtils.formatErrorMessage(null, null, null);
        assertEquals("Operation failed", allNullMessage);
    }

    @Test
    void testGetRetryDelay() {
        // Test first attempt (should be 0)
        assertEquals(ZERO, ServiceUtils.getRetryDelay(ONE));
        assertEquals(ZERO, ServiceUtils.getRetryDelay(ZERO));
        assertEquals(ZERO, ServiceUtils.getRetryDelay(NEGATIVE_ONE));

        // Test exponential backoff: 1000 * (1 << (attemptNumber - 1))
        assertEquals(TWO_THOUSAND_MS, ServiceUtils.getRetryDelay(TWO));
        assertEquals(FOUR_THOUSAND_MS, ServiceUtils.getRetryDelay(THREE));
        assertEquals(EIGHT_THOUSAND_MS, ServiceUtils.getRetryDelay(FOUR));
        assertEquals(TEN_THOUSAND_MS, ServiceUtils.getRetryDelay(FIVE));

        // Test capping at maximum (10 seconds)
        assertEquals(TEN_THOUSAND_MS, ServiceUtils.getRetryDelay(TEN));
        assertEquals(TEN_THOUSAND_MS, ServiceUtils.getRetryDelay(FIFTEEN));
    }

    @Test
    void testConstantsAreNotNull() {
        // Ensure all constants are properly initialized
        assertNotNull(ServiceUtils.SERVICE_PACKAGE);
        assertNotNull(ServiceUtils.DOC_TYPE_JAVADOC);
        assertNotNull(ServiceUtils.DOC_TYPE_MARKDOWN);
        assertNotNull(ServiceUtils.DOC_TYPE_PLAIN);

        // Ensure they're not empty
        assertFalse(ServiceUtils.SERVICE_PACKAGE.isEmpty());
        assertFalse(ServiceUtils.DOC_TYPE_JAVADOC.isEmpty());
        assertFalse(ServiceUtils.DOC_TYPE_MARKDOWN.isEmpty());
        assertFalse(ServiceUtils.DOC_TYPE_PLAIN.isEmpty());
    }

    @Test
    void testEdgeCasesForFilePathSanitization() {
        // Test with only slashes
        assertEquals("", ServiceUtils.sanitizeFilePath("/"));
        assertEquals("", ServiceUtils.sanitizeFilePath("//"));
        assertEquals("", ServiceUtils.sanitizeFilePath("\\"));
        assertEquals("", ServiceUtils.sanitizeFilePath("\\\\"));
        assertEquals("", ServiceUtils.sanitizeFilePath("/\\//\\"));

        // Test with mixed slashes and content
        assertEquals("a/b/c", ServiceUtils.sanitizeFilePath("/a\\b//c"));
        assertEquals("a/b/c", ServiceUtils.sanitizeFilePath("a///b\\\\c"));
    }

    @Test
    void testComplexGroupingScenarios() {
        // Test with multiple elements of same type
        List<CodeElement> manyClasses = List.of(
            new CodeElement(CodeElementType.CLASS, "Class1", "com.test.Class1",
                    "Class1.java", ONE, "", "", List.of(), List.of()),
            new CodeElement(CodeElementType.CLASS, "Class2", "com.test.Class2",
                    "Class2.java", ONE, "", "", List.of(), List.of()),
            new CodeElement(CodeElementType.CLASS, "Class3", "com.test.Class3",
                    "Class3.java", ONE, "", "", List.of(), List.of()),
            new CodeElement(CodeElementType.METHOD, "method1", "com.test.Class1.method1",
                    "Class1.java", TEN, "", "", List.of(), List.of()),
            new CodeElement(CodeElementType.METHOD, "method2", "com.test.Class2.method2",
                    "Class2.java", TEN, "", "", List.of(), List.of()),
            new CodeElement(CodeElementType.FIELD, "field1", "com.test.Class1.field1",
                    "Class1.java", FIVE, "", "", List.of(), List.of())
        );

        Map<CodeElementType, List<CodeElement>> grouped =
                ServiceUtils.groupByType(manyClasses);

        assertEquals(THREE, grouped.size());
        assertEquals(THREE, grouped.get(CodeElementType.CLASS).size());
        assertEquals(TWO, grouped.get(CodeElementType.METHOD).size());
        assertEquals(ONE, grouped.get(CodeElementType.FIELD).size());
    }
}
