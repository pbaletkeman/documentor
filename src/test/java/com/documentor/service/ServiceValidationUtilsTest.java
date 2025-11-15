package com.documentor.service;

import com.documentor.model.CodeElement;
import com.documentor.model.CodeElementType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Comprehensive tests for ServiceValidationUtils to boost coverage.
 */
class ServiceValidationUtilsTest {

    private static final double ONE_FIFTY = 150.0;
    private static final double NINETY_FIVE_PERCENT = 95.0;
    private static final double NINETY_PERCENT = 90.0;
    private static final double EIGHTY_FIVE_PERCENT = 85.0;
    private static final double NEGATIVE_TEN = -10.0;
    private static final double EIGHTY_PERCENT = 80.0;
    private static final double NINETY_ONE_POINT_FOUR_SEVEN = 91.47;
    private static final double MINUS_5 = -5.0;
    private static final String EIGHTY_PERCENT_STRING = "80.0%";
    private static final String NINETY_ONE_POINT_FIVE_STRING = "91.5%";
    private static final String ZERO_PERCENT_STRING = "0%";
    private static final String ONE_HUNDRED_PERCENT_STRING = "100%";
    private static final double ONE_HUNDRED_PERCENT = 100.0;
    private static final int ZERO = 0;
    private static final int ONE_HUNDRED = 100;
    private static final int FIFTY = 50;
    private static final double POINT_ZERO_ONE = 0.01;
    private static final int ONE = 1;
    private static final int TWO = 2;
    private static final int FIVE = 5;

    @Test
    void testValidateCodeElementWithValidElement() {
        CodeElement element = new CodeElement(
            CodeElementType.CLASS, "TestClass", "com.test.TestClass",
            "TestClass.java", ONE, "class TestClass", "Test documentation",
            List.of(), List.of()
        );

        assertTrue(ServiceValidationUtils.validateCodeElement(element));
    }

    @Test
    void testValidateCodeElementWithNull() {
        assertFalse(ServiceValidationUtils.validateCodeElement(null));
    }

    @Test
    void testValidateCodeElementWithNullName() {
        CodeElement element = new CodeElement(
            CodeElementType.CLASS, null, "com.test.TestClass",
            "TestClass.java", ONE, "class TestClass", "Test documentation",
            List.of(), List.of()
        );

        assertFalse(ServiceValidationUtils.validateCodeElement(element));
    }

    @Test
    void testValidateCodeElementWithEmptyName() {
        CodeElement element = new CodeElement(
            CodeElementType.CLASS, "  ", "com.test.TestClass",
            "TestClass.java", ONE, "class TestClass", "Test documentation",
            List.of(), List.of()
        );

        assertFalse(ServiceValidationUtils.validateCodeElement(element));
    }

    @Test
    void testValidateCodeElementWithNullFilePath() {
        CodeElement element = new CodeElement(
            CodeElementType.CLASS, "TestClass", "com.test.TestClass",
            null, ONE, "class TestClass", "Test documentation",
            List.of(), List.of()
        );

        assertFalse(ServiceValidationUtils.validateCodeElement(element));
    }

    @Test
    void testValidateCodeElementWithEmptyFilePath() {
        CodeElement element = new CodeElement(
            CodeElementType.CLASS, "TestClass", "com.test.TestClass",
            "  ", 1, "class TestClass", "Test documentation",
            List.of(), List.of()
        );

        assertFalse(ServiceValidationUtils.validateCodeElement(element));
    }

    @Test
    void testValidateCodeElementWithInvalidLineNumber() {
        CodeElement element = new CodeElement(
            CodeElementType.CLASS, "TestClass", "com.test.TestClass",
            "TestClass.java", ZERO, "class TestClass", "Test documentation",
            List.of(), List.of()
        );

        assertFalse(ServiceValidationUtils.validateCodeElement(element));
    }

    @Test
    void testHasDuplicateNamesWithNoDuplicates() {
        List<CodeElement> elements = List.of(
            new CodeElement(CodeElementType.CLASS, "Class1", "com.test.Class1",
                "Class1.java", ONE, "class Class1", "", List.of(), List.of()),
            new CodeElement(CodeElementType.CLASS, "Class2", "com.test.Class2",
                "Class2.java", ONE, "class Class2", "", List.of(), List.of())
        );

        assertFalse(ServiceValidationUtils.hasDuplicateNames(elements));
    }

    @Test
    void testHasDuplicateNamesWithDuplicates() {
        List<CodeElement> elements = List.of(
            new CodeElement(CodeElementType.CLASS, "TestClass",
                "com.test.TestClass1", "TestClass1.java", ONE,
                "class TestClass", "", List.of(), List.of()),
            new CodeElement(CodeElementType.CLASS, "TestClass",
                "com.test.TestClass2", "TestClass2.java", ONE,
                "class TestClass", "", List.of(), List.of())
        );

        assertTrue(ServiceValidationUtils.hasDuplicateNames(elements));
    }

    @Test
    void testHasDuplicateNamesWithNullList() {
        assertFalse(ServiceValidationUtils.hasDuplicateNames(null));
    }

    @Test
    void testHasDuplicateNamesWithEmptyList() {
        assertFalse(ServiceValidationUtils.hasDuplicateNames(List.of()));
    }

    @Test
    void testHasDuplicateNamesWithNullElements() {
        List<CodeElement> elements = new java.util.ArrayList<>();
        elements.add(new CodeElement(CodeElementType.CLASS, "TestClass",
                "com.test.TestClass", "TestClass.java", ONE, "class TestClass",
                "", List.of(), List.of()));
        elements.add(null);

        assertFalse(ServiceValidationUtils.hasDuplicateNames(elements));
    }

    @Test
    void testCountByTypeWithMatchingElements() {
        List<CodeElement> elements = List.of(
            new CodeElement(CodeElementType.CLASS, "Class1", "com.test.Class1",
                "Class1.java", ONE, "class Class1", "", List.of(), List.of()),
            new CodeElement(CodeElementType.METHOD, "method1",
                "com.test.Class1.method1", "Class1.java", FIVE,
                "public void method1()", "", List.of(), List.of()),
            new CodeElement(CodeElementType.CLASS, "Class2", "com.test.Class2",
                "Class2.java", ONE, "class Class2", "", List.of(), List.of())
        );

        assertEquals(TWO, ServiceValidationUtils.countByType(elements,
                CodeElementType.CLASS));
        assertEquals(ONE, ServiceValidationUtils.countByType(elements,
                CodeElementType.METHOD));
        assertEquals(ZERO, ServiceValidationUtils.countByType(elements,
                CodeElementType.FIELD));
    }

    @Test
    void testCountByTypeWithNullList() {
        assertEquals(ZERO, ServiceValidationUtils.countByType(null,
                CodeElementType.CLASS));
    }

    @Test
    void testCountByTypeWithNullType() {
        List<CodeElement> elements = List.of(
            new CodeElement(CodeElementType.CLASS, "TestClass",
                "com.test.TestClass", "TestClass.java", 1, "class TestClass",
                "", List.of(), List.of())
        );

        assertEquals(0, ServiceValidationUtils.countByType(elements,
                null));
    }

    @Test
    void testHasMissingDocumentationWithMissingDocs() {
        List<CodeElement> elements = List.of(
            new CodeElement(CodeElementType.CLASS, "Class1", "com.test.Class1",
                "Class1.java", ONE, "class Class1", "Good documentation",
                List.of(), List.of()),
            new CodeElement(CodeElementType.CLASS, "Class2", "com.test.Class2",
                "Class2.java", ONE, "class Class2", null, List.of(), List.of())
        );

        assertTrue(ServiceValidationUtils.hasMissingDocumentation(elements));
    }

    @Test
    void testHasMissingDocumentationWithEmptyDocs() {
        List<CodeElement> elements = List.of(
            new CodeElement(CodeElementType.CLASS, "Class1", "com.test.Class1",
                "Class1.java", ONE, "class Class1", "Good documentation",
                List.of(), List.of()),
            new CodeElement(CodeElementType.CLASS, "Class2", "com.test.Class2",
                "Class2.java", ONE, "class Class2", "  ", List.of(), List.of())
        );

        assertTrue(ServiceValidationUtils.hasMissingDocumentation(
                elements));
    }

    @Test
    void testHasMissingDocumentationWithAllDocs() {
        List<CodeElement> elements = List.of(
            new CodeElement(CodeElementType.CLASS, "Class1",
                    "com.test.Class1", "Class1.java", ONE, "class Class1",
                    "Good documentation", List.of(), List.of()),
            new CodeElement(CodeElementType.CLASS, "Class2",
                    "com.test.Class2", "Class2.java", ONE, "class Class2",
                    "Also good docs", List.of(), List.of())
        );

        assertFalse(ServiceValidationUtils.hasMissingDocumentation(elements));
    }

    @Test
    void testHasMissingDocumentationWithNullList() {
        assertFalse(ServiceValidationUtils.hasMissingDocumentation(null));
    }

    @Test
    void testGetUniqueFilePaths() {
        List<CodeElement> elements = List.of(
            new CodeElement(CodeElementType.CLASS, "Class1",
                "com.test.Class1", "TestClass.java", ONE, "class Class1", "",
                List.of(), List.of()),
            new CodeElement(CodeElementType.METHOD, "method1",
                "com.test.Class1.method1", "TestClass.java", FIVE,
                "public void method1()", "", List.of(), List.of()),
            new CodeElement(CodeElementType.CLASS, "Class2",
                "com.test.Class2", "OtherClass.java", ONE, "class Class2", "",
                List.of(), List.of())
        );

        Set<String> paths = ServiceValidationUtils.getUniqueFilePaths(elements);
        assertEquals(TWO, paths.size());
        assertTrue(paths.contains("TestClass.java"));
        assertTrue(paths.contains("OtherClass.java"));
    }

    @Test
    void testGetUniqueFilePathsWithNullList() {
        Set<String> paths = ServiceValidationUtils.getUniqueFilePaths(null);
        assertTrue(paths.isEmpty());
    }

    @Test
    void testIsValidOperationWithValidateOperation() {
        assertTrue(ServiceValidationUtils.isValidOperation(
                "validateData", "config"));
        assertFalse(ServiceValidationUtils.isValidOperation(
                "validateData", null));
    }

    @Test
    void testIsValidOperationWithGenerateOperation() {
        assertTrue(ServiceValidationUtils.isValidOperation(
                "generateDocs", "config"));
        assertFalse(ServiceValidationUtils.isValidOperation(
                "generateDocs", null));
    }

    @Test
    void testIsValidOperationWithOtherOperation() {
        assertTrue(ServiceValidationUtils.isValidOperation(
                "processData", null));
        assertTrue(ServiceValidationUtils.isValidOperation(
                "processData", "config"));
    }

    @Test
    void testIsValidOperationWithNullOperation() {
        assertFalse(ServiceValidationUtils.isValidOperation(
                null, "config"));
    }

    @Test
    void testIsValidOperationWithEmptyOperation() {
        assertFalse(ServiceValidationUtils.isValidOperation(
                "  ", "config"));
    }

    @Test
    void testCalculateCoverageNormal() {
        assertEquals(EIGHTY_PERCENT, ServiceValidationUtils.calculateCoverage(
                (long) EIGHTY_PERCENT, (long) ONE_HUNDRED), POINT_ZERO_ONE);
        assertEquals((double) FIFTY, ServiceValidationUtils.calculateCoverage(
                ONE, TWO), POINT_ZERO_ONE);
    }

    @Test
    void testCalculateCoverageEdgeCases() {
        assertEquals((double) ZERO, ServiceValidationUtils.calculateCoverage(
                ZERO, ONE_HUNDRED), POINT_ZERO_ONE);
        assertEquals(ONE_HUNDRED_PERCENT,
            ServiceValidationUtils.calculateCoverage(
                ONE_HUNDRED, ONE_HUNDRED), POINT_ZERO_ONE);
        assertEquals((double) ZERO, ServiceValidationUtils.calculateCoverage(
                FIFTY, ZERO), POINT_ZERO_ONE);
        assertEquals((double) ZERO, ServiceValidationUtils.calculateCoverage(
                (long) NEGATIVE_TEN, (long) ONE_HUNDRED), POINT_ZERO_ONE);
        assertEquals(ONE_HUNDRED_PERCENT, ServiceValidationUtils
            .calculateCoverage((long) ONE_FIFTY, ONE_HUNDRED), POINT_ZERO_ONE);
    }

    @Test
    void testFormatCoverage() {
        assertEquals(EIGHTY_PERCENT_STRING,
        ServiceValidationUtils.formatCoverage(EIGHTY_PERCENT));
        assertEquals(NINETY_ONE_POINT_FIVE_STRING, ServiceValidationUtils
        .formatCoverage(NINETY_ONE_POINT_FOUR_SEVEN));
        assertEquals(ZERO_PERCENT_STRING,
        ServiceValidationUtils.formatCoverage(MINUS_5));
        assertEquals(ONE_HUNDRED_PERCENT_STRING,
        ServiceValidationUtils.formatCoverage(ONE_FIFTY));

    }

    @Test
    void testMeetsCoverageThreshold() {
        assertTrue(ServiceValidationUtils.meetsCoverageThreshold(
                NINETY_FIVE_PERCENT, NINETY_PERCENT));
        assertTrue(ServiceValidationUtils.meetsCoverageThreshold(
                NINETY_PERCENT, NINETY_PERCENT));
        assertFalse(ServiceValidationUtils.meetsCoverageThreshold(
                EIGHTY_FIVE_PERCENT, NINETY_PERCENT));
        assertFalse(ServiceValidationUtils.meetsCoverageThreshold(
                NINETY_FIVE_PERCENT, NEGATIVE_TEN));
        assertFalse(ServiceValidationUtils.meetsCoverageThreshold(
                NINETY_FIVE_PERCENT, ONE_FIFTY));
    }
}
