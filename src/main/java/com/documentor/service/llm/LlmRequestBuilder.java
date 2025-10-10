package com.documentor.service.llm;

import com.documentor.config.model.LlmModelConfig;
import com.documentor.model.CodeElement;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 🔧 LLM Request Builder - Refactored for Low Complexity
 */
@Component
public class LlmRequestBuilder {

    private final LlmPromptTemplates promptTemplates;
    private final LlmRequestFormatter requestFormatter;

    public LlmRequestBuilder(LlmPromptTemplates promptTemplates, LlmRequestFormatter requestFormatter) {
        this.promptTemplates = promptTemplates;
        this.requestFormatter = requestFormatter;
    }

    /** 🏗️ Builds complete request body for LLM API */
    public Map<String, Object> buildRequestBody(LlmModelConfig model, String prompt) {
        return requestFormatter.createRequest(model, prompt);
    }

    /** 📝 Creates documentation generation prompt */
    public String createDocumentationPrompt(CodeElement codeElement) {
        return promptTemplates.createDocumentationPrompt(codeElement);
    }

    /** 💡 Creates usage example generation prompt */
    public String createUsageExamplePrompt(CodeElement codeElement) {
        return promptTemplates.createUsageExamplePrompt(codeElement);
    }

    /** 🧪 Creates unit test generation prompt */
    public String createUnitTestPrompt(CodeElement codeElement) {
        return promptTemplates.createUnitTestPrompt(codeElement);
    }
}