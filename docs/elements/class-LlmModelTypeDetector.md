# 📦 LlmModelTypeDetector

> **Package:** `com.documentor.service.llm`

---

## 📄 Class Documentation

Error: LLM configuration is null. Please check the application configuration.

---

## 💡 Class Usage Examples

Error: LLM configuration is null. Please check the application configuration.

---

## 📋 Class Signature

```java
/** * 🔍 LLM Model Type Detector * * Centralized logic for detecting LLM model types and providers. * Eliminates duplicate detection logic across LLM components. */ @Component public class LlmModelTypeDetector {
   /** * 🔍 Checks if the model is Ollama-based */ public boolean isOllamaModel(final LlmModelConfig model) {
   return model.baseUrl().contains("ollama") || model.baseUrl().contains(ApplicationConstants.DEFAULT_OLLAMA_PORT);
 } /** * 🔍 Checks if the model is OpenAI-compatible */ public boolean isOpenAICompatible(final LlmModelConfig model) {
   return model.baseUrl().contains("openai") || model.provider().equalsIgnoreCase("openai");
 } /** * 🔍 Gets the appropriate endpoint for the model */ public String getModelEndpoint(final LlmModelConfig model) {
   return model.baseUrl() + (model.baseUrl().endsWith("/") ? "" : "/") + "api/generate";
 } }
```

