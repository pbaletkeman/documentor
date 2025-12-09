# 📦 MockOpenAiProvider

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
/** * 🎭 MockOpenAiProvider - Mock implementation of OpenAI API provider * * Simulates OpenAI API responses with deterministic output for testing * without requiring actual API calls or API keys. * * Responses are mock data that simulate common OpenAI response patterns. */ public class MockOpenAiProvider implements MockLlmProvider {
   private static final Logger LOGGER = LoggerFactory.getLogger(MockOpenAiProvider.class);
 private static final String PROVIDER_NAME = "openai";
 private static final String DEFAULT_MODEL = "gpt-3.5-turbo";
 private String defaultModel;
 /** * Creates a mock OpenAI provider with default model. */ public MockOpenAiProvider() {
   this.defaultModel = DEFAULT_MODEL;
 LOGGER.info("Initialized MockOpenAiProvider with model: {}", defaultModel);
 } /** * Creates a mock OpenAI provider with specific model. * * @param model the model name to use */ public MockOpenAiProvider(final String model) {
   this.defaultModel = model != null ? model : DEFAULT_MODEL;
 LOGGER.info("Initialized MockOpenAiProvider with model: {}", defaultModel);
 } @Override public String getProviderName() {
   return PROVIDER_NAME;
 } @Override public String complete(final String prompt) {
   return complete(prompt, defaultModel);
 } @Override public String complete(final String prompt, final String model) {
   if (prompt == null || prompt.trim().isEmpty()) {
   return "[Mock OpenAI] Empty prompt provided";
 } LOGGER.debug("Mock OpenAI completion - prompt: {}, model: {}", prompt, model);
 return generateMockCompletion(prompt, model);
 } @Override public String chat(final List<ChatMessage> messages) {
   return chat(messages, defaultModel);
 } @Override public String chat(final List<ChatMessage> messages, final String model) {
   if (messages == null || messages.isEmpty()) {
   return "[Mock OpenAI] Empty message list provided";
 } LOGGER.debug("Mock OpenAI chat - {} messages, model: {}", messages.size(), model);
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
   return """ [Mock OpenAI - gpt-3.5-turbo] Here's a Java code example: ```java public class Example { public static void main(String[] args) { System.out.println("Mock response from OpenAI"); } } ``` """;
 } if (promptLower.contains("document") || promptLower.contains("explain")) {
   return """ [Mock OpenAI - gpt-3.5-turbo] Documentation: This component provides the following functionality: - Primary purpose and use cases - Key interfaces and classes - Examples and best practices - Performance considerations """;
 } if (promptLower.contains("fix") || promptLower.contains("error")) {
   return """ [Mock OpenAI - gpt-3.5-turbo] Here's a suggested fix: The issue appears to be related to null pointer handling. Suggested solution: 1. Add null checks before operations 2. Use Optional or defensive programming 3. Add proper error handling """;
 } return "[Mock OpenAI - " + model + "] Generic response: " + "This is a mock response from OpenAI provider. " + "Actual API would return real LLM-generated content.";
 } }
```

