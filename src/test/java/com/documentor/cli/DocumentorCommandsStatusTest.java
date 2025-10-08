package com.documentor.cli;

import com.documentor.config.DocumentorConfig;
import com.documentor.service.CodeAnalysisService;
import com.documentor.service.DocumentationService;
import com.documentor.service.MermaidDiagramService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 🧪 Simple focused tests for CLI coverage improvement
 */
@ExtendWith(MockitoExtension.class)
class DocumentorCommandsStatusTest {

    @Mock
    private CodeAnalysisService codeAnalysisService;

    @Mock
    private DocumentationService documentationService;

    @Mock
    private MermaidDiagramService mermaidDiagramService;

    @Mock
    private DocumentorConfig documentorConfig;

    @BeforeEach
    void setUp() {
        // Simple setup without unused mocks
    }

    @Test
    void testShowStatusWithMockedConfig() {
        // Simple test without complex mocking
        DocumentorCommands simpleCommands = new DocumentorCommands(codeAnalysisService, documentationService, mermaidDiagramService, documentorConfig);
        
        // When
        String result = simpleCommands.showStatus();
        
        // Then
        assertNotNull(result);
        assertTrue(result.contains("Documentor Status"));
    }

    @Test
    void testShowStatusWithNullConfigStillWorks() {
        // Setup - Create DocumentorCommands with null config (no complex mocking needed)
        DocumentorCommands commandsWithNullConfig = new DocumentorCommands(codeAnalysisService, documentationService, mermaidDiagramService, null);
        
        // When - This should not throw an exception
        String result = commandsWithNullConfig.showStatus();
        
        // Then
        assertNotNull(result);
        assertTrue(result.contains("Documentor Status"));
        // With null config, it should handle gracefully without showing specific config details
    }

    @Test
    void testShowInfoContainsExpectedContent() {
        // Create a simple commands instance without complex mocking
        DocumentorCommands simpleCommands = new DocumentorCommands(codeAnalysisService, documentationService, mermaidDiagramService, null);
        
        // When
        String result = simpleCommands.showInfo();
        
        // Then
        assertNotNull(result);
        assertTrue(result.contains("Documentor"));
        assertTrue(result.contains("Languages"));
        assertTrue(result.contains("Java"));
        assertTrue(result.contains("Python"));
        assertTrue(result.contains("analyze"));
    }

    @Test
    void testQuickStartContainsExpectedContent() {
        // Create a simple commands instance without complex mocking
        DocumentorCommands simpleCommands = new DocumentorCommands(codeAnalysisService, documentationService, mermaidDiagramService, null);
        
        // When
        String result = simpleCommands.quickStart();
        
        // Then
        assertNotNull(result);
        assertTrue(result.contains("Quick Start"));
        assertTrue(result.contains("config.json"));
        assertTrue(result.contains("analyze"));
        assertTrue(result.contains("Tips"));
    }
}