package com.documentor.service;

import com.documentor.model.CodeElement;
import com.documentor.model.CodeElementType;
import com.documentor.model.ProjectAnalysis;
import com.documentor.service.diagram.DiagramElementFilter;
import com.documentor.service.diagram.DiagramPathManager;
import com.documentor.service.diagram.PlantUMLClassDiagramGenerator;
import com.documentor.service.diagram.TestDiagramGeneratorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class PlantUMLDiagramServiceTest {

    private static final String TEST_CLASS_NAME = "TestClass";
    private static final String TEST_PROJECT_PATH = "/test/project";
    private static final int CLASS_LINE_NUMBER = 1;
    private static final int FIELD_LINE_NUMBER = 3;
    private static final int METHOD_LINE_NUMBER = 5;

    private PlantUMLDiagramService plantUMLDiagramService;
    private DiagramElementFilter diagramElementFilter;
    private DiagramPathManager diagramPathManager;
    private PlantUMLClassDiagramGenerator plantUMLClassDiagramGenerator;

    @TempDir
    private Path tempDir;

    @BeforeEach
    void setUp() {
        diagramElementFilter = new DiagramElementFilter();
        diagramPathManager = new DiagramPathManager();
        plantUMLClassDiagramGenerator = new PlantUMLClassDiagramGenerator();

        // Update TestDiagramGeneratorFactory to support PlantUML
        TestDiagramGeneratorFactory generatorFactory = new TestDiagramGeneratorFactory(null) {
            @Override
            public PlantUMLClassDiagramGenerator getPlantUMLClassDiagramGenerator() {
                return plantUMLClassDiagramGenerator;
            }
        };

        plantUMLDiagramService = new PlantUMLDiagramService(
            diagramElementFilter,
            diagramPathManager,
            generatorFactory
        );
    }

    @Test
    void generateClassDiagramsWithValidAnalysisShouldGeneratePlantUMLFiles() throws IOException {
        // Given
        ProjectAnalysis analysis = createTestProjectAnalysis();

        // When
        CompletableFuture<List<String>> future = plantUMLDiagramService
            .generateClassDiagrams(analysis, tempDir.toString());
        List<String> generatedFiles = future.join();

        // Then
        assertThat(generatedFiles).isNotEmpty();
        assertThat(generatedFiles).hasSize(1);

        // Verify PlantUML file was created
        String generatedFile = generatedFiles.get(0);
        Path diagramPath = Path.of(generatedFile);
        assertThat(Files.exists(diagramPath)).isTrue();
        assertThat(diagramPath.getFileName().toString()).endsWith("_plantuml.puml");

        // Verify PlantUML content
        String content = Files.readString(diagramPath);
        assertThat(content).contains("@startuml");
        assertThat(content).contains("@enduml");
        assertThat(content).contains(TEST_CLASS_NAME);
        assertThat(content).contains("class " + TEST_CLASS_NAME);
    }

    @Test
    void generateClassDiagramsWithEmptyAnalysisShouldReturnEmptyList() {
        // Given
        ProjectAnalysis emptyAnalysis = new ProjectAnalysis(
            TEST_PROJECT_PATH,
            List.of(),
            System.currentTimeMillis()
        );

        // When
        CompletableFuture<List<String>> future = plantUMLDiagramService
            .generateClassDiagrams(emptyAnalysis, tempDir.toString());
        List<String> generatedFiles = future.join();

        // Then
        assertThat(generatedFiles).isEmpty();
    }

    @Test
    void generateClassDiagramsWithNullOutputPathShouldUseDefaultPath() {
        // Given
        ProjectAnalysis analysis = createTestProjectAnalysis();

        // When
        CompletableFuture<List<String>> future = plantUMLDiagramService
            .generateClassDiagrams(analysis, null);
        List<String> generatedFiles = future.join();

        // Then
        assertThat(generatedFiles).isNotEmpty();
        assertThat(generatedFiles).hasSize(1);

        // Verify file was created in the expected location
        String generatedFile = generatedFiles.get(0);
        assertThat(generatedFile).isNotNull();
        Path diagramPath = Path.of(generatedFile);
        assertThat(Files.exists(diagramPath)).isTrue();
    }

    @Test
    void generateClassDiagramsWithMultipleClassesShouldGenerateMultipleDiagrams() throws IOException {
        // Given
        ProjectAnalysis analysis = createMultiClassProjectAnalysis();

        // When
        CompletableFuture<List<String>> future = plantUMLDiagramService
            .generateClassDiagrams(analysis, tempDir.toString());
        List<String> generatedFiles = future.join();

        // Then
        assertThat(generatedFiles).hasSize(2); // Two classes should generate two diagrams

        // Verify both files exist and have PlantUML format
        for (String generatedFile : generatedFiles) {
            Path diagramPath = Path.of(generatedFile);
            assertThat(Files.exists(diagramPath)).isTrue();
            assertThat(diagramPath.getFileName().toString()).endsWith("_plantuml.puml");

            String content = Files.readString(diagramPath);
            assertThat(content).contains("@startuml");
            assertThat(content).contains("@enduml");
        }
    }

    private ProjectAnalysis createTestProjectAnalysis() {
        List<CodeElement> elements = List.of(
            new CodeElement(
                CodeElementType.CLASS,
                TEST_CLASS_NAME,
                "com.test." + TEST_CLASS_NAME,
                "/test/path/TestClass.java",
                CLASS_LINE_NUMBER,
                "public class " + TEST_CLASS_NAME,
                "Test class for PlantUML diagram generation",
                List.of(),
                List.of()
            ),
            new CodeElement(
                CodeElementType.METHOD,
                "testMethod",
                "com.test." + TEST_CLASS_NAME + ".testMethod",
                "/test/path/TestClass.java",
                METHOD_LINE_NUMBER,
                "public void testMethod()",
                "Test method",
                List.of(),
                List.of()
            ),
            new CodeElement(
                CodeElementType.FIELD,
                "testField",
                "com.test." + TEST_CLASS_NAME + ".testField",
                "/test/path/TestClass.java",
                FIELD_LINE_NUMBER,
                "private String testField",
                "Test field",
                List.of(),
                List.of()
            )
        );

        return new ProjectAnalysis(TEST_PROJECT_PATH, elements, System.currentTimeMillis());
    }

    private ProjectAnalysis createMultiClassProjectAnalysis() {
        List<CodeElement> elements = List.of(
            new CodeElement(
                CodeElementType.CLASS,
                "FirstClass",
                "com.test.FirstClass",
                "/test/path/FirstClass.java",
                CLASS_LINE_NUMBER,
                "public class FirstClass",
                "First test class",
                List.of(),
                List.of()
            ),
            new CodeElement(
                CodeElementType.CLASS,
                "SecondClass",
                "com.test.SecondClass",
                "/test/path/SecondClass.java",
                CLASS_LINE_NUMBER,
                "public class SecondClass",
                "Second test class",
                List.of(),
                List.of()
            ),
            new CodeElement(
                CodeElementType.METHOD,
                "firstMethod",
                "com.test.FirstClass.firstMethod",
                "/test/path/FirstClass.java",
                METHOD_LINE_NUMBER,
                "public void firstMethod()",
                "First method",
                List.of(),
                List.of()
            ),
            new CodeElement(
                CodeElementType.METHOD,
                "secondMethod",
                "com.test.SecondClass.secondMethod",
                "/test/path/SecondClass.java",
                METHOD_LINE_NUMBER,
                "public void secondMethod()",
                "Second method",
                List.of(),
                List.of()
            )
        );

        return new ProjectAnalysis(TEST_PROJECT_PATH, elements, System.currentTimeMillis());
    }
}