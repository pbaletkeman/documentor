package com.documentor.service.llm;

import com.documentor.model.CodeElement;
import com.documentor.model.CodeElementType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class LlmPromptTemplatesTest {

    private final LlmPromptTemplates templates = new LlmPromptTemplates();

    @Test
    @DisplayName("Should create documentation prompt with correct format")
    void createDocumentationPrompt() {
        // Given
        CodeElement mockElement = Mockito.mock(CodeElement.class);
        when(mockElement.type()).thenReturn(CodeElementType.METHOD);
        when(mockElement.getAnalysisContext()).thenReturn("Method context");

        // When
        String prompt = templates.createDocumentationPrompt(mockElement);

        // Then
        assertTrue(prompt.contains("Analyze and document this method"));
        assertTrue(prompt.contains("Method context"));
    }

    @Test
    @DisplayName("Should create usage example prompt with correct format")
    void createUsageExamplePrompt() {
        // Given
        CodeElement mockElement = Mockito.mock(CodeElement.class);
        when(mockElement.type()).thenReturn(CodeElementType.CLASS);
        when(mockElement.getAnalysisContext()).thenReturn("Class context");

        // When
        String prompt = templates.createUsageExamplePrompt(mockElement);

        // Then
        assertTrue(prompt.contains("Generate practical usage examples for this class"));
        assertTrue(prompt.contains("Class context"));
    }

    @Test
    @DisplayName("Should create unit test prompt with correct format")
    void createUnitTestPrompt() {
        // Given
        CodeElement mockElement = Mockito.mock(CodeElement.class);
        when(mockElement.type()).thenReturn(CodeElementType.FIELD);
        when(mockElement.getAnalysisContext()).thenReturn("Field context");

        // When
        String prompt = templates.createUnitTestPrompt(mockElement);

        // Then
        assertTrue(prompt.contains("Generate comprehensive unit tests for this field"));
        assertTrue(prompt.contains("Field context"));
    }
}
