package com.documentor.service.llm;

import com.documentor.model.CodeElement;
import org.springframework.stereotype.Component;

/** ðŸ“ LLM Prompt Templates - Centralized prompt generation */
@Component
public class LlmPromptTemplates {

    /** 🔍 Creates documentation generation prompt */
    public String createDocumentationPrompt(final CodeElement codeElement) {
        String type = codeElement.type().getDescription().toLowerCase();
        return String.format("Analyze and document this %s:\n\n%s\n\n"
                + "Provide: description, parameters, return value, usage notes.",
                           type, codeElement.getAnalysisContext());
    }

    /** 💡 Creates usage example generation prompt */
    public String createUsageExamplePrompt(final CodeElement codeElement) {
        String type = codeElement.type().getDescription().toLowerCase();
        return String.format("Generate practical usage examples for this %s:\n\n%s\n\n"
                + "Provide: 2-3 examples with sample data, expected outputs, use cases.",
                           type, codeElement.getAnalysisContext());
    }

    /** 🧪 Creates unit test generation prompt */
    public String createUnitTestPrompt(final CodeElement codeElement) {
        String type = codeElement.type().getDescription().toLowerCase();
        return String.format("Generate comprehensive unit tests for this %s:\n\n%s\n\n"
                + "Provide: normal cases, edge cases, error handling tests.",
                           type, codeElement.getAnalysisContext());
    }
}
