package com.documentor.service.diagram;

import com.documentor.config.model.DiagramNamingOptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DiagramPathManager naming functionality.
 */
class DiagramPathManagerNamingTest {

    private DiagramPathManager pathManager;

    @BeforeEach
    void setUp() {
        pathManager = new DiagramPathManager();
    }

    @Test
    void testGenerateDiagramFileNameWithNullOptions_Mermaid() {
        String fileName = pathManager.generateDiagramFileName(
            "UserService",
            null,
            "mmd"
        );

        assertEquals("UserService_diagram.mmd", fileName);
    }

    @Test
    void testGenerateDiagramFileNameWithNullOptions_PlantUML() {
        String fileName = pathManager.generateDiagramFileName(
            "UserService",
            null,
            "plantuml"
        );

        assertEquals("UserService_plantuml.puml", fileName);
    }

    @Test
    void testGenerateDiagramFileNameWithPrefixOnly() {
        DiagramNamingOptions options = new DiagramNamingOptions(
            "2025-API-",
            null,
            null
        );

        String fileName = pathManager.generateDiagramFileName(
            "UserService",
            options,
            "mmd"
        );

        assertEquals("2025-API-UserService.mmd", fileName);
    }

    @Test
    void testGenerateDiagramFileNameWithSuffixOnly() {
        DiagramNamingOptions options = new DiagramNamingOptions(
            null,
            "_v2",
            null
        );

        String fileName = pathManager.generateDiagramFileName(
            "UserService",
            options,
            "mmd"
        );

        assertEquals("UserService_v2.mmd", fileName);
    }

    @Test
    void testGenerateDiagramFileNameWithExtensionOnly() {
        DiagramNamingOptions options = new DiagramNamingOptions(
            null,
            null,
            "uml"
        );

        String fileName = pathManager.generateDiagramFileName(
            "UserService",
            options,
            "mmd"
        );

        assertEquals("UserService.uml", fileName);
    }

    @Test
    void testGenerateDiagramFileNameWithAllOptions() {
        DiagramNamingOptions options = new DiagramNamingOptions(
            "arch-",
            "_final",
            "md"
        );

        String fileName = pathManager.generateDiagramFileName(
            "DataFlow",
            options,
            "mmd"
        );

        assertEquals("arch-DataFlow_final.md", fileName);
    }

    @Test
    void testGenerateDiagramFileNameWithEmptyStrings() {
        DiagramNamingOptions options = new DiagramNamingOptions(
            "",
            "",
            ""
        );

        String fileName = pathManager.generateDiagramFileName(
            "UserService",
            options,
            "mmd"
        );

        // Empty strings result in just the class name and default extension
        assertEquals("UserService.mmd", fileName);
    }

    @Test
    void testGenerateDiagramFileNameSanitizesClassName() {
        DiagramNamingOptions options = new DiagramNamingOptions(
            "api-",
            null,
            "uml"
        );

        String fileName = pathManager.generateDiagramFileName(
            "User$Service@123",
            options,
            "mmd"
        );

        // Special characters should be sanitized
        assertEquals("api-User_Service_123.uml", fileName);
    }

    @Test
    void testGenerateDiagramFileNameWithSpaces() {
        DiagramNamingOptions options = new DiagramNamingOptions(
            "arch ",
            " v1",
            "md"
        );

        String fileName = pathManager.generateDiagramFileName(
            "User Service",
            options,
            "mmd"
        );

        assertEquals("arch User_Service v1.md", fileName);
    }

    @Test
    void testGenerateDiagramFileNameWithParentheses() {
        DiagramNamingOptions options = new DiagramNamingOptions(
            "api (new)",
            " (fixed)",
            "uml"
        );

        String fileName = pathManager.generateDiagramFileName(
            "UserService",
            options,
            "mmd"
        );

        assertEquals("api (new)UserService (fixed).uml", fileName);
    }

    @Test
    void testGenerateDiagramFileNameWithPlus() {
        DiagramNamingOptions options = new DiagramNamingOptions(
            "v2.0+",
            "+beta",
            "md"
        );

        String fileName = pathManager.generateDiagramFileName(
            "Service",
            options,
            "mmd"
        );

        assertEquals("v2.0+Service+beta.md", fileName);
    }

    @Test
    void testGenerateDiagramFileNameWithDots() {
        DiagramNamingOptions options = new DiagramNamingOptions(
            "v1.0.",
            ".final",
            "puml"
        );

        String fileName = pathManager.generateDiagramFileName(
            "Component",
            options,
            "mmd"
        );

        assertEquals("v1.0.Component.final.puml", fileName);
    }

    @Test
    void testBackwardCompatibilityMermaid() {
        // When no naming options are provided, should use old format
        String fileName = pathManager.generateDiagramFileName(
            "TestClass",
            null,
            "mmd"
        );

        assertEquals("TestClass_diagram.mmd", fileName);
    }

    @Test
    void testBackwardCompatibilityPlantUML() {
        // When no naming options are provided, should use old format
        String fileName = pathManager.generateDiagramFileName(
            "TestClass",
            null,
            "plantuml"
        );

        assertEquals("TestClass_plantuml.puml", fileName);
    }

    @Test
    void testGetExtensionOrDefaultWithCustomExtension() {
        DiagramNamingOptions options = new DiagramNamingOptions(
            null,
            null,
            "custom"
        );

        String fileName = pathManager.generateDiagramFileName(
            "Service",
            options,
            "default"
        );

        assertEquals("Service.custom", fileName);
    }

    @Test
    void testGetExtensionOrDefaultWithDefaultExtension() {
        DiagramNamingOptions options = new DiagramNamingOptions(
            null,
            null,
            null
        );

        String fileName = pathManager.generateDiagramFileName(
            "Service",
            options,
            "default"
        );

        assertEquals("Service.default", fileName);
    }
}
