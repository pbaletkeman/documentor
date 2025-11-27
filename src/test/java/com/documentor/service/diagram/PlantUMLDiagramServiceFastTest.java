package com.documentor.service.diagram;

import com.documentor.model.CodeElement;
import com.documentor.model.CodeElementType;
import com.documentor.model.ProjectAnalysis;
import com.documentor.service.PlantUMLDiagramService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


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
        when(mockElementFilter.getEligibleClasses(any()))
            .thenReturn(List.of());

        // When
        CompletableFuture<List<String>> result =
            plantUMLDiagramService
                .generateClassDiagrams(emptyAnalysis, "/mock/output");

        // Then
        assertNotNull(result);
        List<String> diagrams = result.join();
        assertNotNull(diagrams);
        assertTrue(diagrams.isEmpty());
    }

    @Test
    @DisplayName("Should handle single class project")
    void shouldHandleSingleClassProject(@TempDir java.nio.file.Path tempDir)
            throws java.io.IOException {
        // Given
        ProjectAnalysis singleClassAnalysis =
            createSingleClassProjectAnalysis();
        CodeElement classElement = createTestClass();
        String tempDirPath = tempDir.toString();

        when(mockElementFilter.getEligibleClasses(any()))
            .thenReturn(List.of(classElement));
        when(mockElementFilter.groupElementsByClass(any()))
            .thenReturn(java.util.Map.of(classElement, List.of(classElement)));
        when(mockPathManager.determineOutputPath(any(), any()))
            .thenReturn(tempDirPath);
        when(mockPathManager.createOutputDirectory(any()))
            .thenReturn(tempDir);
        when(mockGeneratorFactory.getPlantUMLClassDiagramGenerator())
            .thenReturn(mockGenerator);
        when(mockGenerator.generateClassDiagram(any(), any(), any(), any()))
            .thenReturn(tempDir.resolve("diagram.puml").toString());

        // When
        CompletableFuture<List<String>> result =
            plantUMLDiagramService.generateClassDiagrams(
                singleClassAnalysis, "/mock/output");

        // Then
        assertNotNull(result);
        List<String> diagrams = result.join();
        assertNotNull(diagrams);
        assertTrue(diagrams.size() == 1);
        // Use Path to extract filename for cross-platform compatibility
        String diagramPath = diagrams.get(0);
        assertTrue(java.nio.file.Path.of(diagramPath).getFileName()
                .toString().contains("diagram.puml"));
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
