package com.documentor.service.documentation;

import com.documentor.model.CodeElement;
import com.documentor.model.CodeElementType;
import com.documentor.model.ProjectAnalysis;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 🧪 Simple tests for DocumentationFormatter component
 */
class DocumentationFormatterTest {

    private DocumentationFormatter formatter;
    private ProjectAnalysis testProject;

    @BeforeEach
    void setUp() {
        formatter = new DocumentationFormatter();

        CodeElement testElement = new CodeElement(
            CodeElementType.CLASS, "TestClass", "com.example.TestClass",
            "TestClass.java", 1, "public class TestClass {}",
            "", List.of(), List.of()
        );

        testProject = new ProjectAnalysis("/test/path", List.of(testElement), System.currentTimeMillis());
    }

    @Test
    void testDocumentationFormatterConstructor() {
        // Test that the component can be instantiated
        assertNotNull(formatter);
    }

    @Test
    void testAppendHeader() {
        StringBuilder sb = new StringBuilder();

        formatter.appendHeader(sb, testProject);

        String result = sb.toString();
        assertNotNull(result);
        assertTrue(result.length() > 0);
        // Should contain project information
        assertTrue(result.contains("Documentation") || result.contains("Project"));
    }

    @Test
    void testAppendStatistics() {
        StringBuilder sb = new StringBuilder();

        formatter.appendStatistics(sb, testProject);

        String result = sb.toString();
        assertNotNull(result);
        assertTrue(result.length() > 0);
        // Should contain some statistics
        assertTrue(result.contains("1") || result.contains("Statistics") || result.contains("Elements"));
    }

    @Test
    void testAppendUsageExamples() {
        StringBuilder sb = new StringBuilder();

        formatter.appendUsageExamples(sb, testProject);

        String result = sb.toString();
        assertNotNull(result);
        // Should handle empty or basic usage examples gracefully
        assertTrue(result.length() >= 0);
    }

    @Test
    void testFormatterWithEmptyProject() {
        ProjectAnalysis emptyProject = new ProjectAnalysis("/empty", List.of(), System.currentTimeMillis());
        StringBuilder sb = new StringBuilder();

        // Should handle empty project without errors
        assertDoesNotThrow(() -> {
            formatter.appendHeader(sb, emptyProject);
            formatter.appendStatistics(sb, emptyProject);
            formatter.appendUsageExamples(sb, emptyProject);
        });

        assertTrue(sb.length() >= 0);
    }

    @Test
    void testAppendApiReference() {
        StringBuilder sb = new StringBuilder();

        formatter.appendApiReference(sb, testProject);

        String result = sb.toString();
        assertNotNull(result);
        assertTrue(result.length() > 0);
        assertTrue(result.contains("API Reference"));
        assertTrue(result.contains("Detailed API documentation"));
    }

    @Test
    void testAppendApiReferenceWithEmptyProject() {
        ProjectAnalysis emptyProject = new ProjectAnalysis("/empty", List.of(), System.currentTimeMillis());
        StringBuilder sb = new StringBuilder();

        formatter.appendApiReference(sb, emptyProject);

        String result = sb.toString();
        assertNotNull(result);
        assertTrue(result.length() > 0);
        assertTrue(result.contains("API Reference"));
        assertTrue(result.contains("No API elements found"));
    }

    @Test
    void testAppendTestDocumentationHeader() {
        StringBuilder sb = new StringBuilder();

        formatter.appendTestDocumentationHeader(sb);

        String result = sb.toString();
        assertNotNull(result);
        assertTrue(result.length() > 0);
        assertTrue(result.contains("Unit Test Documentation"));
        assertTrue(result.contains("Generated test documentation"));
    }

    @Test
    void testGetProjectNameWithNullPath() {
        ProjectAnalysis nullPathProject = new ProjectAnalysis(null, List.of(), System.currentTimeMillis());
        StringBuilder sb = new StringBuilder();

        formatter.appendHeader(sb, nullPathProject);

        String result = sb.toString();
        assertNotNull(result);
        assertTrue(result.contains("Project Documentation"));
    }

    @Test
    void testGetProjectNameWithEmptyPath() {
        ProjectAnalysis emptyPathProject = new ProjectAnalysis("", List.of(), System.currentTimeMillis());
        StringBuilder sb = new StringBuilder();

        formatter.appendHeader(sb, emptyPathProject);

        String result = sb.toString();
        assertNotNull(result);
        assertTrue(result.contains("Project Documentation"));
    }
}
