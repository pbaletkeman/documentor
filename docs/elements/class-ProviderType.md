# 📦 ProviderType

> **Package:** `com.documentor.service.llm.mock.MockLlmProviderFactory`

---

## 📄 Class Documentation

Error: LLM configuration is null. Please check the application configuration.

---

## 💡 Class Usage Examples

Error: LLM configuration is null. Please check the application configuration.

---

## 📋 Class Signature

```java
/** * Supported provider types. */ public enum ProviderType {
   OPENAI("openai"), OLLAMA("ollama"), LLAMACPP("llamacpp");
 private final String id;
 ProviderType(final String id) {
   this.id = id;
 } public String getId() {
   return id;
 } /** * Gets provider type by ID string. * * @param id provider ID * @return provider type or null if not found */ public static ProviderType fromId(final String id) {
   if (id == null) {
   return null;
 } for (final ProviderType type : ProviderType.values()) {
   if (type.id.equalsIgnoreCase(id)) {
   return type;
 } } return null;
 } }
```

