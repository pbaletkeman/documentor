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

    @Test
    void testValidateCodeElementWithValidElement() {
        CodeElement element = new CodeElement(
            CodeElementType.CLASS, "TestClass", "com.test.TestClass",
            "TestClass.java", 1, "class TestClass", "Test documentation",
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
            "TestClass.java", 1, "class TestClass", "Test documentation",
            List.of(), List.of()
        );

        assertFalse(ServiceValidationUtils.validateCodeElement(element));
    }

    @Test
    void testValidateCodeElementWithEmptyName() {
        CodeElement element = new CodeElement(
            CodeElementType.CLASS, "  ", "com.test.TestClass",
            "TestClass.java", 1, "class TestClass", "Test documentation",
            List.of(), List.of()
        );

        assertFalse(ServiceValidationUtils.validateCodeElement(element));
    }

    @Test
    void testValidateCodeElementWithNullFilePath() {
        CodeElement element = new CodeElement(
            CodeElementType.CLASS, "TestClass", "com.test.TestClass",
            null, 1, "class TestClass", "Test documentation",
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
            "TestClass.java", 0, "class TestClass", "Test documentation",
            List.of(), List.of()
        );

        assertFalse(ServiceValidationUtils.validateCodeElement(element));
    }

    @Test
    void testHasDuplicateNamesWithNoDuplicates() {
        List<CodeElement> elements = List.of(
            new CodeElement(CodeElementType.CLASS, "Class1", "com.test.Class1",
                "Class1.java", 1, "class Class1", "", List.of(), List.of()),
            new CodeElement(CodeElementType.CLASS, "Class2", "com.test.Class2",
                "Class2.java", 1, "class Class2", "", List.of(), List.of())
        );

        assertFalse(ServiceValidationUtils.hasDuplicateNames(elements));
    }

    @Test
    void testHasDuplicateNamesWithDuplicates() {
        List<CodeElement> elements = List.of(
            new CodeElement(CodeElementType.CLASS, "TestClass",
                "com.test.TestClass1", "TestClass1.java", 1, "class TestClass",
                "", List.of(), List.of()),
            new CodeElement(CodeElementType.CLASS, "TestClass",
                "com.test.TestClass2", "TestClass2.java", 1, "class TestClass",
                "", List.of(), List.of())
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
                "com.test.TestClass", "TestClass.java", 1, "class TestClass",
                "", List.of(), List.of()));
        elements.add(null);

        assertFalse(ServiceValidationUtils.hasDuplicateNames(elements));
    }

    @Test
    void testCountByTypeWithMatchingElements() {
        List<CodeElement> elements = List.of(
            new CodeElement(CodeElementType.CLASS, "Class1", "com.test.Class1",
                "Class1.java", 1, "class Class1", "", List.of(), List.of()),
            new CodeElement(CodeElementType.METHOD, "method1",
                "com.test.Class1.method1", "Class1.java", 5,
                "public void method1()", "", List.of(), List.of()),
            new CodeElement(CodeElementType.CLASS, "Class2", "com.test.Class2",
                "Class2.java", 1, "class Class2", "", List.of(), List.of())
        );

        assertEquals(2, ServiceValidationUtils.countByType(elements,
                CodeElementType.CLASS));
        assertEquals(1, ServiceValidationUtils.countByType(elements,
                CodeElementType.METHOD));
        assertEquals(0, ServiceValidationUtils.countByType(elements,
                CodeElementType.FIELD));
    }

    @Test
    void testCountByTypeWithNullList() {
        assertEquals(0, ServiceValidationUtils.countByType(null,
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
                "Class1.java", 1, "class Class1", "Good documentation",
                List.of(), List.of()),
            new CodeElement(CodeElementType.CLASS, "Class2", "com.test.Class2",
                "Class2.java", 1, "class Class2", null, List.of(), List.of())
        );

        assertTrue(ServiceValidationUtils.hasMissingDocumentation(elements));
    }

    @Test
    void testHasMissingDocumentationWithEmptyDocs() {
        List<CodeElement> elements = List.of(
            new CodeElement(CodeElementType.CLASS, "Class1", "com.test.Class1",
                "Class1.java", 1, "class Class1", "Good documentation",
                List.of(), List.of()),
            new CodeElement(CodeElementType.CLASS, "Class2", "com.test.Class2",
                "Class2.java", 1, "class Class2", "  ", List.of(), List.of())
        );

        assertTrue(ServiceValidationUtils.hasMissingDocumentation(
                elements));
    }

    @Test
    void testHasMissingDocumentationWithAllDocs() {
        List<CodeElement> elements = List.of(
            new CodeElement(CodeElementType.CLASS, "Class1", "com.test.Class1",
                "Class1.java", 1, "class Class1", "Good documentation", List.of(), List.of()),
            new CodeElement(CodeElementType.CLASS, "Class2", "com.test.Class2",
                "Class2.java", 1, "class Class2", "Also good docs", List.of(), List.of())
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
                "com.test.Class1", "TestClass.java", 1, "class Class1", "",
                List.of(), List.of()),
            new CodeElement(CodeElementType.METHOD, "method1",
                "com.test.Class1.method1", "TestClass.java", 5,
                "public void method1()", "", List.of(), List.of()),
            new CodeElement(CodeElementType.CLASS, "Class2",
                "com.test.Class2", "OtherClass.java", 1, "class Class2", "",
                List.of(), List.of())
        );

        Set<String> paths = ServiceValidationUtils.getUniqueFilePaths(elements);
        assertEquals(2, paths.size());
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
        assertEquals(80.0, ServiceValidationUtils.calculateCoverage(
                80, 100), 0.01);
        assertEquals(50.0, ServiceValidationUtils.calculateCoverage(
                1, 2), 0.01);
    }

    @Test
    void testCalculateCoverageEdgeCases() {
        assertEquals(0.0, ServiceValidationUtils.calculateCoverage(
                0, 100), 0.01);
        assertEquals(100.0, ServiceValidationUtils.calculateCoverage(
                100, 100), 0.01);
        assertEquals(0.0, ServiceValidationUtils.calculateCoverage(
                50, 0), 0.01);
        assertEquals(0.0, ServiceValidationUtils.calculateCoverage(
                -10, 100), 0.01);
        assertEquals(100.0, ServiceValidationUtils.calculateCoverage(
                150, 100), 0.01);
    }

    @Test
    void testFormatCoverage() {
        assertEquals("80.0%", ServiceValidationUtils.formatCoverage(80.0));
        assertEquals("91.5%", ServiceValidationUtils.formatCoverage(91.47));
        assertEquals("0%", ServiceValidationUtils.formatCoverage(-5.0));
        assertEquals("100%", ServiceValidationUtils.formatCoverage(150.0));
    }

    @Test
    void testMeetsCoverageThreshold() {
        assertTrue(ServiceValidationUtils.meetsCoverageThreshold(
                95.0, 90.0));
        assertTrue(ServiceValidationUtils.meetsCoverageThreshold(
                90.0, 90.0));
        assertFalse(ServiceValidationUtils.meetsCoverageThreshold(
                85.0, 90.0));
        assertFalse(ServiceValidationUtils.meetsCoverageThreshold(
                95.0, -10.0));
        assertFalse(ServiceValidationUtils.meetsCoverageThreshold(
                95.0, 150.0));
    }
}
