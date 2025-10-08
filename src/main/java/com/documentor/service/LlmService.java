package com.documentor.service;

import com.documentor.config.DocumentorConfig;
import com.documentor.model.CodeElement;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 🤖 LLM Integration Service
 * 
 * Handles communication with Large Language Models to generate:
 * - Code summaries and documentation
 * - Usage examples with sample data
 * - Unit test suggestions
 */
@Service
public class LlmService {

    private static final Logger logger = LoggerFactory.getLogger(LlmService.class);

    private final DocumentorConfig config;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public LlmService(DocumentorConfig config, WebClient webClient) {
        this.config = config;
        this.webClient = webClient;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * 📝 Generates documentation for a code element using multiple LLM models
     * 
     * @param codeElement The code element to document
     * @return CompletableFuture containing the generated documentation
     */
    @Async("llmExecutor")
    public CompletableFuture<String> generateDocumentation(CodeElement codeElement) {
        logger.debug("🤖 Generating documentation for: {}", codeElement.getDisplayName());

        List<CompletableFuture<String>> futures = config.llmModels().stream()
                .map(model -> generateWithModel(codeElement, model))
                .map(CompletableFuture::completedFuture)
                .toList();

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> {
                    List<String> responses = futures.stream()
                            .map(CompletableFuture::join)
                            .filter(response -> !response.isEmpty())
                            .toList();
                    
                    return consolidateResponses(responses, codeElement);
                });
    }

    /**
     * 💡 Generates usage examples for a code element
     */
    @Async("llmExecutor")
    public CompletableFuture<String> generateUsageExamples(CodeElement codeElement) {
        logger.debug("💡 Generating usage examples for: {}", codeElement.getDisplayName());

        String prompt = createUsageExamplePrompt(codeElement);
        
        // Use the first available model for examples
        if (!config.llmModels().isEmpty()) {
            DocumentorConfig.LlmModelConfig model = config.llmModels().get(0);
            return CompletableFuture.completedFuture(callLlmModel(model, prompt));
        }
        
        return CompletableFuture.completedFuture("No LLM models configured for usage examples.");
    }

    /**
     * 🧪 Generates unit test suggestions for a code element
     */
    @Async("llmExecutor")
    public CompletableFuture<String> generateUnitTests(CodeElement codeElement) {
        logger.debug("🧪 Generating unit tests for: {}", codeElement.getDisplayName());

        String prompt = createUnitTestPrompt(codeElement);
        
        // Use the first available model for unit tests
        if (!config.llmModels().isEmpty()) {
            DocumentorConfig.LlmModelConfig model = config.llmModels().get(0);
            return CompletableFuture.completedFuture(callLlmModel(model, prompt));
        }
        
        return CompletableFuture.completedFuture("No LLM models configured for unit test generation.");
    }

    /**
     * 🔄 Generates documentation using a specific LLM model
     */
    private String generateWithModel(CodeElement codeElement, DocumentorConfig.LlmModelConfig model) {
        try {
            String prompt = createDocumentationPrompt(codeElement);
            return callLlmModel(model, prompt);
        } catch (Exception e) {
            logger.error("❌ Error generating documentation with model {}: {}", model.name(), e.getMessage());
            return "";
        }
    }

    /**
     * 📞 Makes API call to LLM model
     */
    private String callLlmModel(DocumentorConfig.LlmModelConfig model, String prompt) {
        try {
            Map<String, Object> requestBody = createRequestBody(model, prompt);
            String endpoint = getModelEndpoint(model);

            String response = webClient.post()
                    .uri(endpoint)
                    .header("Authorization", "Bearer " + model.apiKey())
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(model.timeoutSeconds()))
                    .block();

            return extractResponseContent(response, model);

        } catch (Exception e) {
            logger.error("❌ LLM API call failed for model {}: {}", model.name(), e.getMessage());
            return "Error generating content with " + model.name();
        }
    }

    /**
     * 🏗️ Creates request body for LLM API call
     */
    private Map<String, Object> createRequestBody(DocumentorConfig.LlmModelConfig model, String prompt) {
        return Map.of(
            "model", model.name(),
            "messages", List.of(
                Map.of("role", "system", "content", "You are a helpful code documentation assistant."),
                Map.of("role", "user", "content", prompt)
            ),
            "max_tokens", model.maxTokens(),
            "temperature", model.temperature()
        );
    }

    /**
     * 🌐 Gets the appropriate endpoint for the model
     */
    private String getModelEndpoint(DocumentorConfig.LlmModelConfig model) {
        if (model.endpoint() != null && !model.endpoint().isEmpty()) {
            return model.endpoint();
        }
        
        // Default endpoints based on model names
        if (model.name().startsWith("gpt-")) {
            return "https://api.openai.com/v1/chat/completions";
        } else if (model.name().startsWith("claude-")) {
            return "https://api.anthropic.com/v1/messages";
        }
        
        throw new IllegalArgumentException("No endpoint configured for model: " + model.name());
    }

    /**
     * 📤 Extracts content from LLM response
     */
    private String extractResponseContent(String response, DocumentorConfig.LlmModelConfig model) {
        try {
            JsonNode jsonNode = objectMapper.readTree(response);
            
            // OpenAI format
            if (jsonNode.has("choices")) {
                return jsonNode.get("choices").get(0).get("message").get("content").asText();
            }
            
            // Anthropic format
            if (jsonNode.has("content")) {
                return jsonNode.get("content").get(0).get("text").asText();
            }
            
            return "Unable to parse response from " + model.name();
            
        } catch (Exception e) {
            logger.error("❌ Error parsing LLM response: {}", e.getMessage());
            return "Error parsing response from " + model.name();
        }
    }

    /**
     * 🔗 Consolidates multiple LLM responses into a single documentation
     */
    private String consolidateResponses(List<String> responses, CodeElement codeElement) {
        if (responses.isEmpty()) {
            return "No documentation generated.";
        }
        
        if (responses.size() == 1) {
            return responses.get(0);
        }
        
        // Simple consolidation - take the longest response as primary
        // In a real implementation, you might use another LLM call to merge responses
        return responses.stream()
                .max((a, b) -> Integer.compare(a.length(), b.length()))
                .orElse("No documentation generated.");
    }

    /**
     * 📝 Creates documentation generation prompt
     */
    private String createDocumentationPrompt(CodeElement codeElement) {
        return String.format("""
            Please generate comprehensive documentation for the following %s:
            
            %s
            
            Please provide:
            1. A clear description of what this %s does
            2. Parameters explanation (if applicable)
            3. Return value description (if applicable)
            4. Any important notes or considerations
            5. Brief usage context
            
            Format the response in markdown.
            """, 
            codeElement.type().getDescription().toLowerCase(),
            codeElement.getAnalysisContext(),
            codeElement.type().getDescription().toLowerCase()
        );
    }

    /**
     * 💡 Creates usage example generation prompt
     */
    private String createUsageExamplePrompt(CodeElement codeElement) {
        return String.format("""
            Please generate practical usage examples for the following %s:
            
            %s
            
            Please provide:
            1. 2-3 realistic usage examples with sample data
            2. Expected outputs or results
            3. Common use cases
            4. Best practices for usage
            
            Use realistic sample data and format the response in markdown with code blocks.
            """, 
            codeElement.type().getDescription().toLowerCase(),
            codeElement.getAnalysisContext()
        );
    }

    /**
     * 🧪 Creates unit test generation prompt
     */
    private String createUnitTestPrompt(CodeElement codeElement) {
        return String.format("""
            Please generate comprehensive unit tests for the following %s:
            
            %s
            
            Please provide:
            1. Test cases for normal operation
            2. Edge cases and boundary conditions
            3. Error handling tests
            4. Mock objects if needed
            5. Aim for high code coverage
            
            Use appropriate testing framework (JUnit for Java, pytest for Python) and format as code blocks.
            """, 
            codeElement.type().getDescription().toLowerCase(),
            codeElement.getAnalysisContext()
        );
    }
}