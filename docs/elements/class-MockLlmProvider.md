# 📦 MockLlmProvider

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
/** * 🎭 MockLlmProvider - Interface for mock LLM provider implementations * * Defines contract for mock LLM providers that simulate real LLM providers * (OpenAI, Ollama, LlamaCpp) for testing and offline development. * * Implementations should provide deterministic responses without requiring * actual API calls or external dependencies. */ public interface MockLlmProvider {
   /** * Gets the provider name (e.g., "openai", "ollama", "llamacpp"). * * @return provider name */ String getProviderName();
 /** * Sends a message to the mock provider and receives a completion. * * @param prompt the input prompt * @return the mocked completion response */ String complete(String prompt);
 /** * Sends a message with custom model name to the mock provider. * * @param prompt the input prompt * @param model the model name to use * @return the mocked completion response */ String complete(String prompt, String model);
 /** * Sends messages in conversational format and receives a response. * * @param messages list of message objects with role and content * @return the mocked completion response */ String chat(List<ChatMessage> messages);
 /** * Sends messages with custom model name for chat completion. * * @param messages list of message objects * @param model the model name to use * @return the mocked completion response */ String chat(List<ChatMessage> messages, String model);
 /** * Gets the default model name for this provider. * * @return default model name */ String getDefaultModel();
 /** * Sets the default model name for this provider. * * @param model the model name to use by default */ void setDefaultModel(String model);
 /** * Checks if provider is available (can respond to requests). * * @return true if provider is available */ boolean isAvailable();
 /** * ChatMessage - Represents a message in conversation format. * * @param role message role (system, user, assistant) * @param content message content */ record ChatMessage(String role, String content) {
   } }
```

