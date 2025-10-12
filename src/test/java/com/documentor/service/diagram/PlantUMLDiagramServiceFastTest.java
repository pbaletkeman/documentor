package com.documentor.service.diagram;

import com.documentor.model.CodeElement;
import com.documentor.model.CodeElementType;
import com.documentor.model.ProjectAnalysis;
import com.documentor.service.PlantUMLDiagramService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Fast tests for PlantUMLDiagramService focusing on logic validation without I/O operations.
 * These tests improve performance by avoiding file system operations and asynchronous operations.
 */
@ExtendWith(MockitoExtension.class)
class PlantUMLDiagramServiceFastTest {

    @Mock
    private DiagramElementFilter mockElementFilter;

    @Mock
    private DiagramPathManager mockPathManager;

    @Mock
    private DiagramGeneratorFactory mockGeneratorFactory;

    @Mock
    private PlantUMLClassDiagramGenerator mockGenerator;

    private PlantUMLDiagramService plantUMLDiagramService;

    @BeforeEach
    void setUp() {
        plantUMLDiagramService = new PlantUMLDiagramService(
            mockElementFilter,
            mockPathManager,
            mockGeneratorFactory
        );
    }

    @Test
    @DisplayName("Should create service with proper dependencies")
    void shouldCreateServiceWithProperDependencies() {
        assertNotNull(plantUMLDiagramService);
    }

    @Test
    @DisplayName("Should handle empty project analysis")
    void shouldHandleEmptyProjectAnalysis() {
        // Given
        ProjectAnalysis emptyAnalysis = createEmptyProjectAnalysis();
        when(mockElementFilter.getEligibleClasses(any())).thenReturn(List.of());

        // When
        CompletableFuture<List<String>> result =
            plantUMLDiagramService.generateClassDiagrams(emptyAnalysis, "/mock/output");

        // Then
        assertNotNull(result);
        List<String> diagrams = result.join();
        assertNotNull(diagrams);
        assertTrue(diagrams.isEmpty());
    }

    @Test
    @DisplayName("Should handle single class project")
    void shouldHandleSingleClassProject() {
        // Given
        ProjectAnalysis singleClassAnalysis = createSingleClassProjectAnalysis();
        CodeElement classElement = createTestClass();
        
        when(mockElementFilter.getEligibleClasses(any()))
            .thenReturn(List.of(classElement));
        when(mockElementFilter.groupElementsByClass(any()))
            .thenReturn(java.util.Map.of(classElement, List.of(classElement)));
        when(mockPathManager.determineOutputPath(any(), any()))
            .thenReturn("/mock/resolved");
        when(mockPathManager.createOutputDirectory(any()))
            .thenReturn(java.nio.file.Paths.get("/mock/resolved"));
        when(mockGeneratorFactory.getPlantUMLClassDiagramGenerator())
            .thenReturn(mockGenerator);
        try {
            when(mockGenerator.generateClassDiagram(any(), any(), any()))
                .thenReturn("/mock/diagram.puml");
        } catch (java.io.IOException e) {
            // This shouldn't happen in a test with mocks
        }

        // When
        CompletableFuture<List<String>> result =
            plantUMLDiagramService.generateClassDiagrams(singleClassAnalysis, "/mock/output");

        // Then
        assertNotNull(result);
        List<String> diagrams = result.join();
        assertNotNull(diagrams);
        assertTrue(diagrams.size() == 1);
        assertTrue(diagrams.get(0).contains("diagram.puml"));
    }

    // Test data creation methods

    private ProjectAnalysis createEmptyProjectAnalysis() {
        return new ProjectAnalysis(
            "/test/project",
            List.of(),
            System.currentTimeMillis()
        );
    }

    private ProjectAnalysis createSingleClassProjectAnalysis() {
        CodeElement classElement = createTestClass();
        return new ProjectAnalysis(
            "/test/project",
            List.of(classElement),
            System.currentTimeMillis()
        );
    }

    private CodeElement createTestClass() {
        return new CodeElement(
            CodeElementType.CLASS,
            "TestClass",
            "com.test.TestClass",
            "/test/TestClass.java",
            1,
            "public class TestClass",
            "Test class documentation",
            List.of(),
            List.of("@Component")
        );
    }
}