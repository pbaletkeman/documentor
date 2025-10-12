package com.documentor.service.diagram;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Test class for DiagramGeneratorFactory
 */
class DiagramGeneratorFactoryTest {

    @Mock
    private MermaidClassDiagramGenerator mermaidGenerator;

    @Mock
    private PlantUMLClassDiagramGenerator plantUMLGenerator;

    private DiagramGeneratorFactory factory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        factory = new DiagramGeneratorFactory(mermaidGenerator, plantUMLGenerator);
    }

    @Test
    void shouldReturnMermaidGenerator() {
        // When
        MermaidClassDiagramGenerator result = factory.getMermaidClassDiagramGenerator();

        // Then
        assertNotNull(result);
        assertEquals(mermaidGenerator, result);
    }

    @Test
    void shouldReturnMermaidGeneratorViaGetClassDiagramGenerator() {
        // When
        MermaidClassDiagramGenerator result = factory.getClassDiagramGenerator();

        // Then
        assertNotNull(result);
        assertEquals(mermaidGenerator, result);
    }

    @Test
    void shouldReturnPlantUMLGenerator() {
        // When
        PlantUMLClassDiagramGenerator result = factory.getPlantUMLClassDiagramGenerator();

        // Then
        assertNotNull(result);
        assertEquals(plantUMLGenerator, result);
    }

    @Test
    void shouldCreateFactoryWithValidConstructor() {
        // Given - already created in setUp()

        // Then
        assertNotNull(factory);
        assertNotNull(factory.getMermaidClassDiagramGenerator());
        assertNotNull(factory.getPlantUMLClassDiagramGenerator());
    }

    @Test
    void shouldHandleNullMermaidGenerator() {
        // Given
        DiagramGeneratorFactory factoryWithNull = new DiagramGeneratorFactory(null, plantUMLGenerator);

        // When & Then
        assertNull(factoryWithNull.getMermaidClassDiagramGenerator());
        assertNotNull(factoryWithNull.getPlantUMLClassDiagramGenerator());
    }

    @Test
    void shouldHandleNullPlantUMLGenerator() {
        // Given
        DiagramGeneratorFactory factoryWithNull = new DiagramGeneratorFactory(mermaidGenerator, null);

        // When & Then
        assertNotNull(factoryWithNull.getMermaidClassDiagramGenerator());
        assertNull(factoryWithNull.getPlantUMLClassDiagramGenerator());
    }

    @Test
    void shouldHandleBothGeneratorsNull() {
        // Given
        DiagramGeneratorFactory factoryWithBothNull = new DiagramGeneratorFactory(null, null);

        // When & Then
        assertNull(factoryWithBothNull.getMermaidClassDiagramGenerator());
        assertNull(factoryWithBothNull.getPlantUMLClassDiagramGenerator());
    }
}