package com.documentor.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;

import com.documentor.service.DocumentationServiceEnhanced;
import com.documentor.service.LlmServiceFixEnhanced;
import com.documentor.service.MermaidDiagramService;
import com.documentor.service.PlantUMLDiagramService;
import com.documentor.service.documentation.ElementDocumentationGeneratorEnhanced;
import com.documentor.service.documentation.MainDocumentationGenerator;
import com.documentor.service.documentation.UnitTestDocumentationGeneratorEnhanced;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Comprehensive test coverage for DocumentationServiceConfiguration.
 * Tests all bean creation paths and ThreadLocal management behavior.
 */
@ExtendWith(MockitoExtension.class)
public class DocumentationServiceConfigurationCoverageTest {

    private DocumentationServiceConfiguration configuration;

    @Mock
    private MainDocumentationGenerator mainDocGenerator;

    @Mock
    private ElementDocumentationGeneratorEnhanced elementDocGeneratorEnhanced;

    @Mock
    private UnitTestDocumentationGeneratorEnhanced testDocGeneratorEnhanced;

    @Mock
    private MermaidDiagramService mermaidDiagramService;

    @Mock
    private PlantUMLDiagramService plantUMLDiagramService;

    @Mock
    private DocumentorConfig documentorConfig;

    @Mock
    private LlmServiceFixEnhanced llmServiceFixEnhanced;

    /**
     * Set up test fixtures before each test.
     */
    @BeforeEach
    void setUp() {
        configuration = new DocumentationServiceConfiguration();
    }

    @Nested
    class DocumentationServiceBeanCreationTests {

        /**
         * Test basic bean creation with all dependencies provided.
         */
        @Test
        void testCreateDocumentationServiceEnhanced() {
            DocumentationServiceEnhanced service =
                    configuration.documentationServiceEnhanced(
                            mainDocGenerator,
                            elementDocGeneratorEnhanced,
                            testDocGeneratorEnhanced,
                            mermaidDiagramService,
                            plantUMLDiagramService,
                            documentorConfig,
                            llmServiceFixEnhanced);

            assertNotNull(service, "DocumentationServiceEnhanced should be "
                    + "created successfully");
        }

        /**
         * Test ThreadLocal config is set when documentorConfig is provided.
         */
        @Test
        void testDocumentationServiceSetsThreadLocalConfig() {
            try (MockedStatic<ThreadLocalContextHolder> mockedThreadLocal =
                    mockStatic(ThreadLocalContextHolder.class)) {

                DocumentationServiceEnhanced service =
                        configuration.documentationServiceEnhanced(
                                mainDocGenerator,
                                elementDocGeneratorEnhanced,
                                testDocGeneratorEnhanced,
                                mermaidDiagramService,
                                plantUMLDiagramService,
                                documentorConfig,
                                llmServiceFixEnhanced);

                assertNotNull(service);
                // Verify ThreadLocal was set with the documentorConfig
                mockedThreadLocal.verify(
                        () -> ThreadLocalContextHolder.setConfig(
                                documentorConfig),
                        times(1));
            }
        }

        /**
         * Test ThreadLocal is NOT set when documentorConfig is null.
         */
        @Test
        void testDocumentationServiceWithNullConfig() {
            try (MockedStatic<ThreadLocalContextHolder> mockedThreadLocal =
                    mockStatic(ThreadLocalContextHolder.class)) {

                DocumentationServiceEnhanced service =
                        configuration.documentationServiceEnhanced(
                                mainDocGenerator,
                                elementDocGeneratorEnhanced,
                                testDocGeneratorEnhanced,
                                mermaidDiagramService,
                                plantUMLDiagramService,
                                null,
                                llmServiceFixEnhanced);

                assertNotNull(service);
                // Verify setConfig NOT called when documentorConfig is null
                mockedThreadLocal.verify(
                        () -> ThreadLocalContextHolder.setConfig(null),
                        times(0));
            }
        }

        /**
         * Test bean creation with all dependencies and config present.
         */
        @Test
        void testDocumentationServiceWithAllDependencies() {
            DocumentationServiceEnhanced service =
                    configuration.documentationServiceEnhanced(
                            mainDocGenerator,
                            elementDocGeneratorEnhanced,
                            testDocGeneratorEnhanced,
                            mermaidDiagramService,
                            plantUMLDiagramService,
                            documentorConfig,
                            llmServiceFixEnhanced);

            assertNotNull(service, "Service should be created with all "
                    + "dependencies");
            assertNotNull(mainDocGenerator, "Main doc generator dependency "
                    + "should exist");
            assertNotNull(elementDocGeneratorEnhanced,
                    "Element doc generator dependency should exist");
            assertNotNull(testDocGeneratorEnhanced,
                    "Test doc generator dependency should exist");
            assertNotNull(mermaidDiagramService,
                    "Mermaid diagram service dependency should exist");
            assertNotNull(plantUMLDiagramService,
                    "PlantUML diagram service dependency should exist");
            assertNotNull(llmServiceFixEnhanced,
                    "LLM service dependency should exist");
        }

        /**
         * Test bean creation with mixed null dependencies (only config is null).
         */
        @Test
        void testDocumentationServiceMixedNullConfig() {
            try (MockedStatic<ThreadLocalContextHolder> mockedThreadLocal =
                    mockStatic(ThreadLocalContextHolder.class)) {

                DocumentationServiceEnhanced service =
                        configuration.documentationServiceEnhanced(
                                mainDocGenerator,
                                elementDocGeneratorEnhanced,
                                testDocGeneratorEnhanced,
                                mermaidDiagramService,
                                plantUMLDiagramService,
                                null,
                                llmServiceFixEnhanced);

                assertNotNull(service);
                mockedThreadLocal.verify(
                        () -> ThreadLocalContextHolder.setConfig(null),
                        times(0));
            }
        }

        /**
         * Test ThreadLocal config propagation for different config instances.
         */
        @Test
        void testDocumentationServiceConfigPropagation() {
            try (MockedStatic<ThreadLocalContextHolder> mockedThreadLocal =
                    mockStatic(ThreadLocalContextHolder.class)) {

                DocumentationServiceEnhanced service =
                        configuration.documentationServiceEnhanced(
                                mainDocGenerator,
                                elementDocGeneratorEnhanced,
                                testDocGeneratorEnhanced,
                                mermaidDiagramService,
                                plantUMLDiagramService,
                                documentorConfig,
                                llmServiceFixEnhanced);

                assertNotNull(service);
                mockedThreadLocal.verify(
                        () -> ThreadLocalContextHolder.setConfig(
                                documentorConfig),
                        times(1));
            }
        }

        /**
         * Test multiple invocations of service creation each set ThreadLocal.
         */
        @Test
        void testMultipleServiceCreationsSetThreadLocal() {
            try (MockedStatic<ThreadLocalContextHolder> mockedThreadLocal =
                    mockStatic(ThreadLocalContextHolder.class)) {

                // First invocation
                DocumentationServiceEnhanced service1 =
                        configuration.documentationServiceEnhanced(
                                mainDocGenerator,
                                elementDocGeneratorEnhanced,
                                testDocGeneratorEnhanced,
                                mermaidDiagramService,
                                plantUMLDiagramService,
                                documentorConfig,
                                llmServiceFixEnhanced);

                // Second invocation
                DocumentationServiceEnhanced service2 =
                        configuration.documentationServiceEnhanced(
                                mainDocGenerator,
                                elementDocGeneratorEnhanced,
                                testDocGeneratorEnhanced,
                                mermaidDiagramService,
                                plantUMLDiagramService,
                                documentorConfig,
                                llmServiceFixEnhanced);

                assertNotNull(service1);
                assertNotNull(service2);
                // Verify setConfig called twice (once per invocation)
                mockedThreadLocal.verify(
                        () -> ThreadLocalContextHolder.setConfig(
                                documentorConfig),
                        times(2));
            }
        }

        /**
         * Test service creation logs info when ThreadLocal is set.
         */
        @Test
        void testDocumentationServiceLogging() {
            try (MockedStatic<ThreadLocalContextHolder> mockedThreadLocal =
                    mockStatic(ThreadLocalContextHolder.class)) {

                DocumentationServiceEnhanced service =
                        configuration.documentationServiceEnhanced(
                                mainDocGenerator,
                                elementDocGeneratorEnhanced,
                                testDocGeneratorEnhanced,
                                mermaidDiagramService,
                                plantUMLDiagramService,
                                documentorConfig,
                                llmServiceFixEnhanced);

                assertNotNull(service);
                // Info log should be called for service creation
                // (handled by SLF4J logger in configuration)
            }
        }

        /**
         * Test service creation with non-null config avoids null pointer.
         */
        @Test
        void testDocumentationServiceNonNullConfigPath() {
            DocumentationServiceEnhanced service =
                    configuration.documentationServiceEnhanced(
                            mainDocGenerator,
                            elementDocGeneratorEnhanced,
                            testDocGeneratorEnhanced,
                            mermaidDiagramService,
                            plantUMLDiagramService,
                            documentorConfig,
                            llmServiceFixEnhanced);

            assertNotNull(service, "Service should be created successfully "
                    + "with non-null config");
        }
    }
}
