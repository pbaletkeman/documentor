package com.documentor.service.diagram;

import com.documentor.model.CodeElement;
import com.documentor.model.CodeElementType;
import com.documentor.model.ProjectAnalysis;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DiagramElementFilterTest {

    private final DiagramElementFilter filter = new DiagramElementFilter();

    @Test
    @DisplayName("Should correctly group elements by class")
    void groupElementsByClass() {
        // Given
        CodeElement class1 = createClass("Class1", "/path/Class1.java");
        CodeElement method1 = createMethod("method1", "/path/Class1.java");
        CodeElement field1 = createField("field1", "/path/Class1.java");

        CodeElement class2 = createClass("Class2", "/path/Class2.java");
        CodeElement method2 = createMethod("method2", "/path/Class2.java");

        List<CodeElement> allElements = Arrays.asList(class1, method1, field1, class2, method2);
        ProjectAnalysis analysis = new ProjectAnalysis("/root", allElements, System.currentTimeMillis());

        // When
        Map<CodeElement, List<CodeElement>> groupedElements = filter.groupElementsByClass(analysis);

        // Then
        assertEquals(2, groupedElements.size());

        // Just check if both class elements are keys
        assertTrue(groupedElements.containsKey(class1));
        assertTrue(groupedElements.containsKey(class2));
    }

    @Test
    @DisplayName("Should identify private elements correctly")
    void isNonPrivate() {
        // Given
        CodeElement publicElement = new CodeElement(
            CodeElementType.METHOD,
            "publicMethod",
            "public void publicMethod()",
            "/path.java",
            1,
            "public void publicMethod() {}",
            "",
            List.of(),
            List.of()
        );

        CodeElement privateElement = new CodeElement(
            CodeElementType.METHOD,
            "privateMethod",
            "private void privateMethod()",
            "/path.java",
            2,
            "private void privateMethod() {}",
            "",
            List.of(),
            List.of()
        );

        CodeElement pythonPrivate = new CodeElement(
            CodeElementType.METHOD,
            "_privateMethod",
            "def _privateMethod()",
            "/path.py",
            3,
            "def _privateMethod():",
            "",
            List.of(),
            List.of()
        );

        // When & Then
        assertTrue(filter.isNonPrivate(publicElement));
        assertFalse(filter.isNonPrivate(privateElement));
        assertFalse(filter.isNonPrivate(pythonPrivate));
    }

    @Test
    @DisplayName("Should correctly group elements by file")
    void groupElementsByFile() {
        // Given
        CodeElement class1 = createClass("Class1", "/path/File1.java");
        CodeElement method1 = createMethod("method1", "/path/File1.java");

        CodeElement class2 = createClass("Class2", "/path/File2.java");
        CodeElement method2 = createMethod("method2", "/path/File2.java");
        CodeElement field2 = createField("field2", "/path/File2.java");

        List<CodeElement> allElements = Arrays.asList(class1, method1, class2, method2, field2);
        ProjectAnalysis analysis = new ProjectAnalysis("/root", allElements, System.currentTimeMillis());

        // When
        Map<String, List<CodeElement>> groupedElements = filter.groupElementsByFile(analysis);

        // Then
        assertEquals(2, groupedElements.size());
        assertTrue(groupedElements.containsKey("/path/File1.java"));
        assertTrue(groupedElements.containsKey("/path/File2.java"));

        // Just check if the files exist as keys
        assertTrue(groupedElements.containsKey("/path/File1.java"));
        assertTrue(groupedElements.containsKey("/path/File2.java"));
    }

    @Test
    @DisplayName("Should return eligible classes only")
    void getEligibleClasses() {
        // Given
        CodeElement publicClass = createClass("PublicClass", "/path/Public.java");
        CodeElement privateClass = new CodeElement(
            CodeElementType.CLASS,
            "PrivateClass",
            "private class PrivateClass",
            "/path/Private.java",
            1,
            "private class PrivateClass {}",
            "",
            List.of(),
            List.of()
        );
        CodeElement method = createMethod("method", "/path/Other.java");

        List<CodeElement> allElements = Arrays.asList(publicClass, privateClass, method);
        ProjectAnalysis analysis = new ProjectAnalysis("/root", allElements, System.currentTimeMillis());

        // When
        List<CodeElement> eligibleClasses = filter.getEligibleClasses(analysis);

        // Then
        assertEquals(1, eligibleClasses.size());
        assertEquals("PublicClass", eligibleClasses.get(0).name());
    }

    private CodeElement createClass(String name, String path) {
        return new CodeElement(
            CodeElementType.CLASS,
            name,
            "public class " + name,
            path,
            1,
            "public class " + name + " {}",
            "",
            List.of(),
            List.of()
        );
    }

    private CodeElement createMethod(String name, String path) {
        return new CodeElement(
            CodeElementType.METHOD,
            name,
            "public void " + name + "()",
            path,
            2,
            "public void " + name + "() {}",
            "",
            List.of(),
            List.of()
        );
    }

    private CodeElement createField(String name, String path) {
        return new CodeElement(
            CodeElementType.FIELD,
            name,
            "private String " + name,
            path,
            3,
            "private String " + name + ";",
            "",
            List.of(),
            List.of()
        );
    }
}
