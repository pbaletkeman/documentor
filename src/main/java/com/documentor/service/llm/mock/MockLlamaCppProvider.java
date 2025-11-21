package com.documentor.service.llm.mock;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 🎭 MockLlamaCppProvider - Mock implementation of llama.cpp local LLM provider
 *
 * Simulates llama.cpp API responses for testing C++ based local LLM inference
 * without requiring llama.cpp server or model files.
 *
 * Useful for testing in containerized environments and CI/CD pipelines.
 */
public class MockLlamaCppProvider implements MockLlmProvider {
    private static final Logger logger = LoggerFactory.getLogger(MockLlamaCppProvider.class);

    private static final String PROVIDER_NAME = "llamacpp";
    private static final String DEFAULT_MODEL = "llama-7b-gguf";

    private String defaultModel;

    /**
     * Creates a mock llama.cpp provider with default model.
     */
    public MockLlamaCppProvider() {
        this.defaultModel = DEFAULT_MODEL;
        logger.info("Initialized MockLlamaCppProvider with model: {}", defaultModel);
    }

    /**
     * Creates a mock llama.cpp provider with specific model.
     *
     * @param model the model name to use
     */
    public MockLlamaCppProvider(final String model) {
        this.defaultModel = model != null ? model : DEFAULT_MODEL;
        logger.info("Initialized MockLlamaCppProvider with model: {}", defaultModel);
    }

    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }

    @Override
    public String complete(final String prompt) {
        return complete(prompt, defaultModel);
    }

    @Override
    public String complete(final String prompt, final String model) {
        if (prompt == null || prompt.trim().isEmpty()) {
            return "[Mock llama.cpp] Empty prompt provided";
        }

        logger.debug("Mock llama.cpp completion - prompt: {}, model: {}", prompt, model);

        return generateMockCompletion(prompt, model);
    }

    @Override
    public String chat(final List<ChatMessage> messages) {
        return chat(messages, defaultModel);
    }

    @Override
    public String chat(final List<ChatMessage> messages, final String model) {
        if (messages == null || messages.isEmpty()) {
            return "[Mock llama.cpp] Empty message list provided";
        }

        logger.debug("Mock llama.cpp chat - {} messages, model: {}", messages.size(), model);

        String lastUserMessage = messages.stream()
                .filter(m -> "user".equals(m.role()))
                .map(ChatMessage::content)
                .reduce((first, second) -> second)
                .orElse("No user message found");

        return generateMockCompletion(lastUserMessage, model);
    }

    @Override
    public String getDefaultModel() {
        return defaultModel;
    }

    @Override
    public void setDefaultModel(final String model) {
        this.defaultModel = model != null ? model : DEFAULT_MODEL;
        logger.debug("Updated default model to: {}", defaultModel);
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    /**
     * Generates mock completion responses based on prompt content.
     *
     * @param prompt the input prompt
     * @param model the model being used
     * @return mock completion response
     */
    private String generateMockCompletion(final String prompt, final String model) {
        String promptLower = prompt.toLowerCase();

        if (promptLower.contains("code") || promptLower.contains("java")) {
            return """
                    [Mock llama.cpp - llama-7b-gguf] Code snippet:

                    // Efficient implementation using llama.cpp
                    public class OptimizedClass {
                        // llama.cpp provides fast CPU-based inference
                        public String processData(String input) {
                            return "Mock response from llama.cpp";
                        }
                    }
                    """;
        }

        if (promptLower.contains("document") || promptLower.contains("explain")) {
            return """
                    [Mock llama.cpp - llama-7b-gguf] C++ optimized inference:

                    This uses llama.cpp for efficient quantized model inference:
                    - Runs on CPU efficiently
                    - Supports GGML quantization
                    - Minimal memory footprint
                    - Great for edge devices and containers

                    Mock response demonstrates llama.cpp capabilities.
                    """;
        }

        if (promptLower.contains("fix") || promptLower.contains("error")) {
            return """
                    [Mock llama.cpp - llama-7b-gguf] Optimization suggestion:

                    For llama.cpp deployments:
                    1. Use quantized models (GGML) for efficiency
                    2. Configure context window appropriately
                    3. Monitor CPU usage during inference
                    4. Consider batching for throughput

                    This mock demonstrates llama.cpp response format.
                    """;
        }

        return "[Mock llama.cpp - " + model + "] CPU-efficient response: " +
                "This mock response simulates llama.cpp C++ inference. " +
                "Actual llama.cpp server would provide real model outputs with optimal performance.";
    }
}
