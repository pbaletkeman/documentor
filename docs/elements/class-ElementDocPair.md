# 📦 ElementDocPair

> **Package:** `com.documentor.service.documentation.ElementDocumentationGeneratorEnhanced`

---

## 📄 Class Documentation

Error: LLM configuration is null. Please check the application configuration.

---

## 💡 Class Usage Examples

Error: LLM configuration is null. Please check the application configuration.

---

## 📋 Class Signature

```java
/** * Helper class to store an element and its documentation/examples */ private static class ElementDocPair {
   private final CodeElement element;
 private final String documentation;
 private final String examples;
 ElementDocPair(final CodeElement codeElement, final String docContent, final String exampleContent) {
   this.element = codeElement;
 this.documentation = docContent;
 this.examples = exampleContent;
 } public CodeElement getElement() {
   return element;
 } public String getDocumentation() {
   return documentation;
 } public String getExamples() {
   return examples;
 } }
```

