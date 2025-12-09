# 📦 MockOllamaProvider

> **Package:** `com.documentor.service.llm.mock`

---

## 📄 Class Documentation

Error: LLM configuration is null. Please check the application configuration.

---

## 💡 Class Usage Examples

Error: LLM configuration is null. Please check the application configuration.

---

## 📋 Class Signature

```java
/** * 🎭 MockOllamaProvider - Mock implementation of Ollama local LLM provider * * Simulates Ollama API responses for testing local LLM setups without * requiring Ollama to be running or having specific models installed. * * Useful for CI/CD pipelines and development environments. */ public class MockOllamaProvider implements MockLlmProvider {
   private static final Logger LOGGER = LoggerFactory.getLogger(MockOllamaProvider.class);
 private static final String PROVIDER_NAME = "ollama";
 private static final String DEFAULT_MODEL = "llama2";
 private String defaultModel;
 /** * Creates a mock Ollama provider with default model. */ public MockOllamaProvider() {
   this.defaultModel = DEFAULT_MODEL;
 LOGGER.info("Initialized MockOllamaProvider with model: {}", defaultModel);
 } /** * Creates a mock Ollama provider with specific model. * * @param model the model name to use */ public MockOllamaProvider(final String model) {
   this.defaultModel = model != null ? model : DEFAULT_MODEL;
 LOGGER.info("Initialized MockOllamaProvider with model: {}", defaultModel);
 } @Override public String getProviderName() {
   return PROVIDER_NAME;
 } @Override public String complete(final String prompt) {
   return complete(prompt, defaultModel);
 } @Override public String complete(final String prompt, final String model) {
   if (prompt == null || prompt.trim().isEmpty()) {
   return "[Mock Ollama] Empty prompt provided";
 } LOGGER.debug("Mock Ollama completion - prompt: {}, model: {}", prompt, model);
 return generateMockCompletion(prompt, model);
 } @Override public String chat(final List<ChatMessage> messages) {
   return chat(messages, defaultModel);
 } @Override public String chat(final List<ChatMessage> messages, final String model) {
   if (messages == null || messages.isEmpty()) {
   return "[Mock Ollama] Empty message list provided";
 } LOGGER.debug("Mock Ollama chat - {} messages, model: {}", messages.size(), model);
 String lastUserMessage = messages.stream().filter(m -> "user".equals(m.role())).map(ChatMessage::content).reduce((first, second) -> second).orElse("No user message found");
 return generateMockCompletion(lastUserMessage, model);
 } @Override public String getDefaultModel() {
   return defaultModel;
 } @Override public void setDefaultModel(final String model) {
   this.defaultModel = model != null ? model : DEFAULT_MODEL;
 LOGGER.debug("Updated default model to: {}", defaultModel);
 } @Override public boolean isAvailable() {
   return true;
 } /** * Generates mock completion responses based on prompt content. * * @param prompt the input prompt * @param model the model being used * @return mock completion response */ private String generateMockCompletion(final String prompt, final String model) {
   String promptLower = prompt.toLowerCase();
 if (promptLower.contains("code") || promptLower.contains("java")) {
   return """ [Mock Ollama - llama2] Code example: public void exampleMethod() { // This is a mock response from local Ollama logger.info("Running locally without API calls"); } """;
 } if (promptLower.contains("document") || promptLower.contains("explain")) {
   return """ [Mock Ollama - llama2] Local explanation: This component provides functionality for code analysis and documentation generation. It uses local LLM models via Ollama for offline processing. Key benefits: - No external API calls required - Data privacy maintained - Fast local inference """;
 } if (promptLower.contains("fix") || promptLower.contains("error")) {
   return """ [Mock Ollama - llama2] Suggested improvement: Consider refactoring the implementation: 1. Improve error handling patterns 2. Add input validation 3. Document edge cases This is a mock response demonstrating local LLM usage. """;
 } return "[Mock Ollama - " + model + "] Local response: " + "This mock response simulates Ollama local inference. " + "Actual Ollama server would provide real model outputs.";
 } }
```

