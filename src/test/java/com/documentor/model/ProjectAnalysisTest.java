package com.documentor.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 🧪 Unit tests for ProjectAnalysis
 */
class ProjectAnalysisTest {

    @Test
    void testConstructorAndGetters() {
        // Given
        CodeElement element1 = new CodeElement(
            CodeElementType.CLASS, "TestClass", "com.test.TestClass", "/test/TestClass.java", 
            1, "public class TestClass", "", List.of(), List.of()
        );
        CodeElement element2 = new CodeElement(
            CodeElementType.METHOD, "testMethod", "com.test.TestClass.testMethod", "/test/TestClass.java", 
            5, "public void testMethod()", "", List.of(), List.of()
        );
        List<CodeElement> elements = List.of(element1, element2);
        long timestamp = System.currentTimeMillis();

        // When
        ProjectAnalysis analysis = new ProjectAnalysis("/test/project", elements, timestamp);

        // Then
        assertNotNull(analysis);
        assertEquals("/test/project", analysis.projectPath());
        assertEquals(elements, analysis.codeElements());
        assertEquals(timestamp, analysis.timestamp());
        assertEquals(2, analysis.codeElements().size());
    }

    @Test
    void testWithEmptyList() {
        // Given
        List<CodeElement> emptyElements = List.of();
        long timestamp = System.currentTimeMillis();

        // When
        ProjectAnalysis analysis = new ProjectAnalysis("/test/empty", emptyElements, timestamp);

        // Then
        assertNotNull(analysis);
        assertEquals("/test/empty", analysis.projectPath());
        assertEquals(emptyElements, analysis.codeElements());
        assertEquals(timestamp, analysis.timestamp());
        assertTrue(analysis.codeElements().isEmpty());
    }

    @Test
    void testEquality() {
        // Given
        CodeElement element = new CodeElement(
            CodeElementType.CLASS, "TestClass", "com.test.TestClass", "/test/TestClass.java", 
            1, "public class TestClass", "", List.of(), List.of()
        );
        List<CodeElement> elements = List.of(element);
        long timestamp = 12345L;
        
        ProjectAnalysis analysis1 = new ProjectAnalysis("/test/project", elements, timestamp);
        ProjectAnalysis analysis2 = new ProjectAnalysis("/test/project", elements, timestamp);

        // Then
        assertEquals(analysis1, analysis2);
        assertEquals(analysis1.hashCode(), analysis2.hashCode());
    }

    @Test
    void testToString() {
        // Given
        CodeElement element = new CodeElement(
            CodeElementType.CLASS, "TestClass", "com.test.TestClass", "/test/TestClass.java", 
            1, "public class TestClass", "", List.of(), List.of()
        );
        List<CodeElement> elements = List.of(element);
        ProjectAnalysis analysis = new ProjectAnalysis("/test/project", elements, System.currentTimeMillis());

        // When
        String result = analysis.toString();

        // Then
        assertNotNull(result);
        assertTrue(result.contains("ProjectAnalysis"));
    }

    @Test
    void testGetClasses() {
        // Given
        CodeElement classElement = new CodeElement(
            CodeElementType.CLASS, "TestClass", "com.test.TestClass", "/test/TestClass.java", 
            1, "public class TestClass", "", List.of(), List.of()
        );
        CodeElement methodElement = new CodeElement(
            CodeElementType.METHOD, "testMethod", "com.test.TestClass.testMethod", "/test/TestClass.java", 
            5, "public void testMethod()", "", List.of(), List.of()
        );
        CodeElement fieldElement = new CodeElement(
            CodeElementType.FIELD, "testField", "com.test.TestClass.testField", "/test/TestClass.java", 
            3, "private String testField", "", List.of(), List.of()
        );
        List<CodeElement> elements = List.of(classElement, methodElement, fieldElement);
        ProjectAnalysis analysis = new ProjectAnalysis("/test/project", elements, System.currentTimeMillis());

        // When
        List<CodeElement> classes = analysis.getClasses();

        // Then
        assertEquals(1, classes.size());
        assertEquals(classElement, classes.get(0));
    }

    @Test
    void testGetMethods() {
        // Given - same setup as above
        CodeElement classElement = new CodeElement(
            CodeElementType.CLASS, "TestClass", "com.test.TestClass", "/test/TestClass.java", 
            1, "public class TestClass", "", List.of(), List.of()
        );
        CodeElement method1 = new CodeElement(
            CodeElementType.METHOD, "method1", "com.test.TestClass.method1", "/test/TestClass.java", 
            5, "public void method1()", "", List.of(), List.of()
        );
        CodeElement method2 = new CodeElement(
            CodeElementType.METHOD, "method2", "com.test.TestClass.method2", "/test/TestClass.java", 
            10, "public void method2()", "", List.of(), List.of()
        );
        List<CodeElement> elements = List.of(classElement, method1, method2);
        ProjectAnalysis analysis = new ProjectAnalysis("/test/project", elements, System.currentTimeMillis());

        // When
        List<CodeElement> methods = analysis.getMethods();

        // Then
        assertEquals(2, methods.size());
        assertTrue(methods.contains(method1));
        assertTrue(methods.contains(method2));
    }

    @Test
    void testGetFields() {
        // Given
        CodeElement field1 = new CodeElement(
            CodeElementType.FIELD, "field1", "com.test.TestClass.field1", "/test/TestClass.java", 
            3, "private String field1", "", List.of(), List.of()
        );
        CodeElement field2 = new CodeElement(
            CodeElementType.FIELD, "field2", "com.test.TestClass.field2", "/test/TestClass.java", 
            4, "private int field2", "", List.of(), List.of()
        );
        CodeElement methodElement = new CodeElement(
            CodeElementType.METHOD, "testMethod", "com.test.TestClass.testMethod", "/test/TestClass.java", 
            5, "public void testMethod()", "", List.of(), List.of()
        );
        List<CodeElement> elements = List.of(field1, field2, methodElement);
        ProjectAnalysis analysis = new ProjectAnalysis("/test/project", elements, System.currentTimeMillis());

        // When
        List<CodeElement> fields = analysis.getFields();

        // Then
        assertEquals(2, fields.size());
        assertTrue(fields.contains(field1));
        assertTrue(fields.contains(field2));
    }

    @Test
    void testGetElementsByFile() {
        // Given
        CodeElement element1 = new CodeElement(
            CodeElementType.CLASS, "TestClass1", "com.test.TestClass1", "/test/TestClass1.java", 
            1, "public class TestClass1", "", List.of(), List.of()
        );
        CodeElement element2 = new CodeElement(
            CodeElementType.CLASS, "TestClass2", "com.test.TestClass2", "/test/TestClass2.java", 
            1, "public class TestClass2", "", List.of(), List.of()
        );
        CodeElement element3 = new CodeElement(
            CodeElementType.METHOD, "method1", "com.test.TestClass1.method1", "/test/TestClass1.java", 
            5, "public void method1()", "", List.of(), List.of()
        );
        List<CodeElement> elements = List.of(element1, element2, element3);
        ProjectAnalysis analysis = new ProjectAnalysis("/test/project", elements, System.currentTimeMillis());

        // When
        var elementsByFile = analysis.getElementsByFile();

        // Then
        assertEquals(2, elementsByFile.size());
        assertTrue(elementsByFile.containsKey("/test/TestClass1.java"));
        assertTrue(elementsByFile.containsKey("/test/TestClass2.java"));
        assertEquals(2, elementsByFile.get("/test/TestClass1.java").size());
        assertEquals(1, elementsByFile.get("/test/TestClass2.java").size());
    }

    @Test
    void testGetElementsByType() {
        // Given
        CodeElement classElement = new CodeElement(
            CodeElementType.CLASS, "TestClass", "com.test.TestClass", "/test/TestClass.java", 
            1, "public class TestClass", "", List.of(), List.of()
        );
        CodeElement methodElement = new CodeElement(
            CodeElementType.METHOD, "testMethod", "com.test.TestClass.testMethod", "/test/TestClass.java", 
            5, "public void testMethod()", "", List.of(), List.of()
        );
        CodeElement fieldElement = new CodeElement(
            CodeElementType.FIELD, "testField", "com.test.TestClass.testField", "/test/TestClass.java", 
            3, "private String testField", "", List.of(), List.of()
        );
        List<CodeElement> elements = List.of(classElement, methodElement, fieldElement);
        ProjectAnalysis analysis = new ProjectAnalysis("/test/project", elements, System.currentTimeMillis());

        // When
        var elementsByType = analysis.getElementsByType();

        // Then
        assertEquals(3, elementsByType.size());
        assertTrue(elementsByType.containsKey(CodeElementType.CLASS));
        assertTrue(elementsByType.containsKey(CodeElementType.METHOD));
        assertTrue(elementsByType.containsKey(CodeElementType.FIELD));
        assertEquals(1, elementsByType.get(CodeElementType.CLASS).size());
        assertEquals(1, elementsByType.get(CodeElementType.METHOD).size());
        assertEquals(1, elementsByType.get(CodeElementType.FIELD).size());
    }

    @Test
    void testGetStats() {
        // Given
        CodeElement classElement = new CodeElement(
            CodeElementType.CLASS, "TestClass", "com.test.TestClass", "/test/TestClass.java", 
            1, "public class TestClass", "", List.of(), List.of()
        );
        CodeElement method1 = new CodeElement(
            CodeElementType.METHOD, "method1", "com.test.TestClass.method1", "/test/TestClass.java", 
            5, "public void method1()", "", List.of(), List.of()
        );
        CodeElement method2 = new CodeElement(
            CodeElementType.METHOD, "method2", "com.test.TestClass.method2", "/test/TestClass.java", 
            10, "public void method2()", "", List.of(), List.of()
        );
        CodeElement fieldElement = new CodeElement(
            CodeElementType.FIELD, "testField", "com.test.TestClass.testField", "/test/TestClass.java", 
            3, "private String testField", "", List.of(), List.of()
        );
        List<CodeElement> elements = List.of(classElement, method1, method2, fieldElement);
        ProjectAnalysis analysis = new ProjectAnalysis("/test/project", elements, System.currentTimeMillis());

        // When
        ProjectAnalysis.AnalysisStats stats = analysis.getStats();

        // Then
        assertNotNull(stats);
        assertEquals(4, stats.totalElements());
        assertEquals(1, stats.classCount());
        assertEquals(2, stats.methodCount());
        assertEquals(1, stats.fieldCount());
        assertEquals(1, stats.fileCount());
    }

    @Test
    void testAnalysisStatsFormattedSummary() {
        // Given
        ProjectAnalysis.AnalysisStats stats = new ProjectAnalysis.AnalysisStats(10, 2, 5, 3, 2);

        // When
        String summary = stats.getFormattedSummary();

        // Then
        assertNotNull(summary);
        assertTrue(summary.contains("10 total elements"));
        assertTrue(summary.contains("2 classes"));
        assertTrue(summary.contains("5 methods"));
        assertTrue(summary.contains("3 fields"));
        assertTrue(summary.contains("2 files"));
    }

    @Test
    void testWithEmptyElements() {
        // Given
        List<CodeElement> emptyElements = List.of();
        ProjectAnalysis analysis = new ProjectAnalysis("/test/empty", emptyElements, System.currentTimeMillis());

        // When & Then
        assertTrue(analysis.getClasses().isEmpty());
        assertTrue(analysis.getMethods().isEmpty());
        assertTrue(analysis.getFields().isEmpty());
        assertTrue(analysis.getElementsByFile().isEmpty());
        assertTrue(analysis.getElementsByType().isEmpty());
        
        ProjectAnalysis.AnalysisStats stats = analysis.getStats();
        assertEquals(0, stats.totalElements());
        assertEquals(0, stats.classCount());
        assertEquals(0, stats.methodCount());
        assertEquals(0, stats.fieldCount());
        assertEquals(0, stats.fileCount());
    }
}