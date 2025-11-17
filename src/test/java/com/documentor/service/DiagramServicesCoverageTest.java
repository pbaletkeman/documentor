package com.documentor.service;

import com.documentor.model.CodeElement;
import com.documentor.model.CodeElementType;
import com.documentor.model.ProjectAnalysis;
import com.documentor.service.diagram.DiagramElementFilter;
import com.documentor.service.diagram.DiagramGeneratorFactory;
import com.documentor.service.diagram.DiagramPathManager;
import com.documentor.service.diagram.MermaidClassDiagramGenerator;
import com.documentor.service.diagram.PlantUMLClassDiagramGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;

/**
 * Coverage enhancement tests for diagram services.
 * Targets missing coverage in exception handling paths.
 */
@ExtendWith(MockitoExtension.class)
class DiagramServicesCoverageTest {

    @Mock
    private DiagramElementFilter mockElementFilter;

    @Mock
    private DiagramPathManager mockPathManager;

    @Mock
    private DiagramGeneratorFactory mockGeneratorFactory;

    @Mock
    private MermaidClassDiagramGenerator mockMermaidClassDiagramGenerator;

    @Mock
    private PlantUMLClassDiagramGenerator mockPlantUMLClassDiagramGenerator;

    private MermaidDiagramService mermaidDiagramService;
    private PlantUMLDiagramService plantUMLDiagramService;

    @TempDir
    private Path tempDir;

    @BeforeEach
    void setUp() {
        mermaidDiagramService = new MermaidDiagramService(
            mockElementFilter, mockPathManager, mockGeneratorFactory);
        plantUMLDiagramService = new PlantUMLDiagramService(
            mockElementFilter, mockPathManager, mockGeneratorFactory);

        // Setup common mocks with lenient behavior
        lenient().when(mockGeneratorFactory.getClassDiagramGenerator())
            .thenReturn(mockMermaidClassDiagramGenerator);
        lenient().when(mockGeneratorFactory.getPlantUMLClassDiagramGenerator())
            .thenReturn(mockPlantUMLClassDiagramGenerator);
        lenient().when(mockPathManager.determineOutputPath(anyString(),
            anyString())).thenReturn(tempDir.toString());
        lenient().when(mockPathManager.createOutputDirectory(anyString()))
            .thenReturn(tempDir);
    }

    @Test
    void testMermaidDiagramGenerationWithException() {
        // Test exception handling in the lambda
        when(mockElementFilter.getEligibleClasses(any()))
            .thenThrow(new RuntimeException("Filter failed"));

        ProjectAnalysis analysis = createTestProjectAnalysis();

        CompletableFuture<List<String>> result = mermaidDiagramService
            .generateClassDiagrams(analysis, "/output");

        assertThrows(
            CompletionException.class, () -> result.join());
    }

    @Test
    void testMermaidDiagramGenerationWithSingleClassException()
        throws Exception {
        // Test exception handling when processing individual classes
        CodeElement testClass = createTestClassElement();
        when(mockElementFilter.getEligibleClasses(any()))
            .thenReturn(List.of(testClass));

        Map<CodeElement, List<CodeElement>> elementsByClass = new HashMap<>();
        elementsByClass.put(testClass, List.of(testClass));
        when(mockElementFilter.groupElementsByClass(any()))
            .thenReturn(elementsByClass);

        // Make the class diagram generator throw an exception
        when(mockMermaidClassDiagramGenerator
            .generateClassDiagram(any(), any(), any()))
            .thenThrow(new RuntimeException("Diagram generation failed"));

        ProjectAnalysis analysis = createTestProjectAnalysis();

        CompletableFuture<List<String>> result = mermaidDiagramService
            .generateClassDiagrams(analysis, "/output");

        // Should not throw but handle the exception gracefully
        List<String> diagrams = assertDoesNotThrow(() -> result.join());
        // No diagrams generated due to exception
        assertTrue(diagrams.isEmpty());
    }    @Test
    void testPlantUMLDiagramGenerationWithException() {
        // Test exception handling in PlantUML service lambda
        when(mockElementFilter.getEligibleClasses(any()))
            .thenThrow(new RuntimeException("Filter failed"));

        ProjectAnalysis analysis = createTestProjectAnalysis();

        CompletableFuture<List<String>> result = plantUMLDiagramService
            .generateClassDiagrams(analysis, "/output");

        assertThrows(CompletionException.class, () -> result.join());
    }

    @Test
    void testPlantUMLDiagramGenerationWithSingleClassException()
        throws Exception {
        // Test exception handling when processing individual
        // classes in PlantUML service
        CodeElement testClass = createTestClassElement();
        lenient().when(mockElementFilter.getEligibleClasses(any()))
            .thenReturn(List.of(testClass));

        Map<CodeElement, List<CodeElement>> elementsByClass = new HashMap<>();
        elementsByClass.put(testClass, List.of(testClass));
        lenient().when(mockElementFilter.groupElementsByClass(any()))
            .thenReturn(elementsByClass);

        // Make the class diagram generator throw an exception
        lenient().when(mockPlantUMLClassDiagramGenerator
            .generateClassDiagram(any(), any(), any()))
            .thenThrow(new RuntimeException("Diagram generation failed"));

        ProjectAnalysis analysis = createTestProjectAnalysis();

        CompletableFuture<List<String>> result =
            plantUMLDiagramService.generateClassDiagrams(analysis, "/output");

        // Should not throw but handle the exception gracefully
        List<String> diagrams = assertDoesNotThrow(() -> result.join());
        // No diagrams generated due to exception
        assertTrue(diagrams.isEmpty());
    }

    @Test
    void testMermaidDiagramSuccessfulGeneration() throws Exception {
        // Test successful generation to ensure normal path coverage
        CodeElement testClass = createTestClassElement();
        when(mockElementFilter.getEligibleClasses(any()))
        .thenReturn(List.of(testClass));

        Map<CodeElement, List<CodeElement>> elementsByClass = new HashMap<>();
        elementsByClass.put(testClass, List.of(testClass));
        when(mockElementFilter.groupElementsByClass(any()))
            .thenReturn(elementsByClass);

        when(mockMermaidClassDiagramGenerator
        .generateClassDiagram(any(), any(), any()))
            .thenReturn("generated-diagram.mmd");

        ProjectAnalysis analysis = createTestProjectAnalysis();

        CompletableFuture<List<String>> result =
            mermaidDiagramService.generateClassDiagrams(analysis, "/output");

        List<String> diagrams = assertDoesNotThrow(() -> result.join());
        assertEquals(1, diagrams.size());
        assertEquals("generated-diagram.mmd", diagrams.get(0));
    }

    @Test
    void testPlantUMLDiagramSuccessfulGeneration() throws Exception {
        // Test successful generation to ensure normal path coverage
        CodeElement testClass = createTestClassElement();
        when(mockElementFilter.getEligibleClasses(any()))
            .thenReturn(List.of(testClass));

        Map<CodeElement, List<CodeElement>> elementsByClass = new HashMap<>();
        elementsByClass.put(testClass, List.of(testClass));
        when(mockElementFilter.groupElementsByClass(any()))
            .thenReturn(elementsByClass);

        when(mockPlantUMLClassDiagramGenerator
            .generateClassDiagram(any(), any(), any()))
            .thenReturn("generated-diagram.puml");

        ProjectAnalysis analysis = createTestProjectAnalysis();

        CompletableFuture<List<String>> result =
            plantUMLDiagramService.generateClassDiagrams(analysis, "/output");

        List<String> diagrams = assertDoesNotThrow(() -> result.join());
        assertEquals(1, diagrams.size());
        assertEquals("generated-diagram.puml", diagrams.get(0));
    }    private ProjectAnalysis createTestProjectAnalysis() {
        CodeElement testElement = createTestClassElement();
        return new ProjectAnalysis(
            "/test/path",
            List.of(testElement),
            System.currentTimeMillis()
        );
    }

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
}
