package com.documentor.service.llm;

import com.documentor.config.model.LlmModelConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.Map;

/** ðŸŒ LLM API Client - Refactored for Low Complexity */
@Component
public class LlmApiClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(LlmApiClient.class);
    private final WebClient webClient;
    private final LlmModelTypeDetector modelTypeDetector;

    public LlmApiClient(WebClient webClient, LlmModelTypeDetector modelTypeDetector) {
        this.webClient = webClient;
        this.modelTypeDetector = modelTypeDetector;
    }

    /** ðŸ“ž Makes API call to LLM model */
    public String callLlmModel(LlmModelConfig model, String endpoint, Map<String, Object> requestBody) {
        try {
            WebClient.RequestBodySpec request = webClient.post()
                    .uri(endpoint)
                    .header("Content-Type", "application/json");

            // Add authentication header only if not Ollama (Ollama typically doesn't require auth)
            if (!modelTypeDetector.isOllamaModel(model) && model.apiKey() != null && !model.apiKey().isEmpty()) {
                request = request.header("Authorization", "Bearer " + model.apiKey());
            }

            String response = request
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(model.timeoutSeconds()))
                    .block();

            return response;

        } catch (Exception e) {
            LOGGER.error("âŒ LLM API call failed for model {}: {}", model.name(), e.getMessage());
            return "Error generating content with " + model.name();
        }
    }
}
