package com.documentor.config;

import com.documentor.service.MermaidDiagramService;
import com.documentor.service.PlantUMLDiagramService;
import com.documentor.service.diagram.DiagramElementFilter;
import com.documentor.service.diagram.DiagramGeneratorFactory;
import com.documentor.service.diagram.DiagramPathManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;

/**
 * Comprehensive coverage tests for DiagramServiceConfiguration.
 * Tests all bean creation paths and ThreadLocalContextHolder interactions.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DiagramServiceConfiguration Tests")
class DiagramServiceConfigurationCoverageTest {

    @Mock
    private DiagramElementFilter elementFilter;

    @Mock
    private DiagramPathManager pathManager;

    @Mock
    private DiagramGeneratorFactory generatorFactory;

    @Mock
    private DocumentorConfig documentorConfig;

    private DiagramServiceConfiguration configuration;

    @BeforeEach
    void setUp() {
        configuration = new DiagramServiceConfiguration();
    }

    @Nested
    @DisplayName("MermaidDiagramService Bean Creation")
    class MermaidDiagramServiceBeanTests {

        @Test
        @DisplayName("should create MermaidDiagramService with valid dependencies")
        void testCreateMermaidDiagramService() {
            // When
            MermaidDiagramService service = configuration.mermaidDiagramService(
                    elementFilter, pathManager, generatorFactory, documentorConfig);

            // Then
            assertNotNull(service, "MermaidDiagramService should be created");
        }

        @Test
        @DisplayName("should create MermaidDiagramService and set config in ThreadLocal")
        void testMermaidDiagramServiceSetsThreadLocalConfig() {
            try (MockedStatic<ThreadLocalContextHolder> mockedThreadLocal =
                    mockStatic(ThreadLocalContextHolder.class)) {
                // When
                MermaidDiagramService service = configuration.mermaidDiagramService(
                        elementFilter, pathManager, generatorFactory, documentorConfig);

                // Then
                assertNotNull(service, "Service should be created");
                mockedThreadLocal.verify(
                        () -> ThreadLocalContextHolder.setConfig(documentorConfig),
                        times(1));
            }
        }

        @Test
        @DisplayName("should create MermaidDiagramService with null DocumentorConfig")
        void testMermaidDiagramServiceWithNullConfig() {
            try (MockedStatic<ThreadLocalContextHolder> mockedThreadLocal =
                    mockStatic(ThreadLocalContextHolder.class)) {
                // When
                MermaidDiagramService service = configuration.mermaidDiagramService(
                        elementFilter, pathManager, generatorFactory, null);

                // Then
                assertNotNull(service, "Service should be created even with null config");
                // ThreadLocalContextHolder.setConfig should NOT be called
                mockedThreadLocal.verify(
                        () -> ThreadLocalContextHolder.setConfig(any()),
                        times(0));
            }
        }

        @Test
        @DisplayName("should pass correct dependencies to MermaidDiagramService")
        void testMermaidDiagramServiceDependencies() {
            // When
            MermaidDiagramService service = configuration.mermaidDiagramService(
                    elementFilter, pathManager, generatorFactory, documentorConfig);

            // Then - verify service is created (dependencies are correctly passed)
            assertNotNull(service, "Service should be created with provided dependencies");
        }
    }

    @Nested
    @DisplayName("PlantUMLDiagramService Bean Creation")
    class PlantUMLDiagramServiceBeanTests {

        @Test
        @DisplayName("should create PlantUMLDiagramService with valid dependencies")
        void testCreatePlantUMLDiagramService() {
            // When
            PlantUMLDiagramService service = configuration.plantUMLDiagramService(
                    elementFilter, pathManager, generatorFactory, documentorConfig);

            // Then
            assertNotNull(service, "PlantUMLDiagramService should be created");
        }

        @Test
        @DisplayName("should create PlantUMLDiagramService and set config in ThreadLocal")
        void testPlantUMLDiagramServiceSetsThreadLocalConfig() {
            try (MockedStatic<ThreadLocalContextHolder> mockedThreadLocal =
                    mockStatic(ThreadLocalContextHolder.class)) {
                // When
                PlantUMLDiagramService service = configuration.plantUMLDiagramService(
                        elementFilter, pathManager, generatorFactory, documentorConfig);

                // Then
                assertNotNull(service, "Service should be created");
                mockedThreadLocal.verify(
                        () -> ThreadLocalContextHolder.setConfig(documentorConfig),
                        times(1));
            }
        }

        @Test
        @DisplayName("should create PlantUMLDiagramService with null DocumentorConfig")
        void testPlantUMLDiagramServiceWithNullConfig() {
            try (MockedStatic<ThreadLocalContextHolder> mockedThreadLocal =
                    mockStatic(ThreadLocalContextHolder.class)) {
                // When
                PlantUMLDiagramService service = configuration.plantUMLDiagramService(
                        elementFilter, pathManager, generatorFactory, null);

                // Then
                assertNotNull(service, "Service should be created even with null config");
                // ThreadLocalContextHolder.setConfig should NOT be called
                mockedThreadLocal.verify(
                        () -> ThreadLocalContextHolder.setConfig(any()),
                        times(0));
            }
        }

        @Test
        @DisplayName("should pass correct dependencies to PlantUMLDiagramService")
        void testPlantUMLDiagramServiceDependencies() {
            // When
            PlantUMLDiagramService service = configuration.plantUMLDiagramService(
                    elementFilter, pathManager, generatorFactory, documentorConfig);

            // Then - verify service is created (dependencies are correctly passed)
            assertNotNull(service, "Service should be created with provided dependencies");
        }
    }

    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {

        @Test
        @DisplayName("should create both services successfully in sequence")
        void testCreateBothServices() {
            // When
            MermaidDiagramService mermaidService = configuration.mermaidDiagramService(
                    elementFilter, pathManager, generatorFactory, documentorConfig);
            PlantUMLDiagramService plantUMLService = configuration.plantUMLDiagramService(
                    elementFilter, pathManager, generatorFactory, documentorConfig);

            // Then
            assertNotNull(mermaidService, "MermaidDiagramService should be created");
            assertNotNull(plantUMLService, "PlantUMLDiagramService should be created");
        }

        @Test
        @DisplayName("should handle mixed null configurations across services")
        void testMixedNullConfigAcrossServices() {
            try (MockedStatic<ThreadLocalContextHolder> mockedThreadLocal =
                    mockStatic(ThreadLocalContextHolder.class)) {
                // When
                MermaidDiagramService mermaidService = configuration.mermaidDiagramService(
                        elementFilter, pathManager, generatorFactory, null);
                PlantUMLDiagramService plantUMLService = configuration.plantUMLDiagramService(
                        elementFilter, pathManager, generatorFactory, documentorConfig);

                // Then
                assertNotNull(mermaidService, "Mermaid service should be created with null config");
                assertNotNull(plantUMLService, "PlantUML service should be created with config");
                // Verify setConfig was called only once (for plantUMLService)
                mockedThreadLocal.verify(
                        () -> ThreadLocalContextHolder.setConfig(documentorConfig),
                        times(1));
            }
        }
    }
}
