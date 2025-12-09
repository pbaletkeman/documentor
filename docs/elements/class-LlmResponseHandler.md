# 📦 LlmResponseHandler

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
/** * 🔍 LLM Response Handler - Refactored for Low Complexity * * Simplified response handling by delegating to specialized components. * Reduces complexity by removing duplicate logic and centralizing response * parsing. */ @Component public class LlmResponseHandler {
   private final LlmResponseParser responseParser;
 private final LlmModelTypeDetector modelTypeDetector;
 public LlmResponseHandler(final LlmResponseParser responseParserParam, final LlmModelTypeDetector modelTypeDetectorParam) {
   this.responseParser = responseParserParam;
 this.modelTypeDetector = modelTypeDetectorParam;
 } /** * 🔍 Extracts content from LLM response based on model type */ public String extractResponseContent(final String response, final LlmModelConfig model) {
   return responseParser.parseResponse(response, model);
 } /** * 🔍 Gets the appropriate endpoint for the model */ public String getModelEndpoint(final LlmModelConfig model) {
   return modelTypeDetector.getModelEndpoint(model);
 } }
```

