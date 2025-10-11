package com.documentor.service.llm;

import com.documentor.config.model.LlmModelConfig;
import com.documentor.model.CodeElement;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * ðŸ”§ LLM Request Builder - Refactored for Low Complexity
 */
@Component
public class LlmRequestBuilder {

    private final LlmPromptTemplates promptTemplates;
    private final LlmRequestFormatter requestFormatter;

    public LlmRequestBuilder(final LlmPromptTemplates promptTemplatesParam, final LlmRequestFormatter requestFormatterParam) {
        this.promptTemplates = promptTemplatesParam;
        this.requestFormatter = requestFormatterParam;
    }

    /** ðŸ—ï¸ Builds complete request body for LLM API */
    public Map<String, Object> buildRequestBody(final LlmModelConfig model, final String prompt) {
        return requestFormatter.createRequest(model, prompt);
    }

    /** ðŸ" Creates documentation generation prompt */
    public String createDocumentationPrompt(final CodeElement codeElement) {
        return promptTemplates.createDocumentationPrompt(codeElement);
    }

    /** ðŸ'¡ Creates usage example generation prompt */
    public String createUsageExamplePrompt(final CodeElement codeElement) {
        return promptTemplates.createUsageExamplePrompt(codeElement);
    }

    /** ðŸ§ª Creates unit test generation prompt */
    public String createUnitTestPrompt(final CodeElement codeElement) {
        return promptTemplates.createUnitTestPrompt(codeElement);
    }
}
