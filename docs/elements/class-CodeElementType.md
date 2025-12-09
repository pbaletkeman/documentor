# 📦 CodeElementType

> **Package:** `com.documentor.model`

---

## 📄 Class Documentation

Error: LLM configuration is null. Please check the application configuration.

---

## 💡 Class Usage Examples

Error: LLM configuration is null. Please check the application configuration.

---

## 📋 Class Signature

```java
/** * 🔍 Code Element Type Enumeration * * Represents the different types of code elements that can be analyzed: * - CLASS: Classes, interfaces, enums * - METHOD: Methods, functions, procedures * - FIELD: Variables, attributes, constants */ public enum CodeElementType {
   CLASS("📦", "Class/Interface"), METHOD("🔧", "Method/Function"), FIELD("📊", "Field/Variable");
 private final String icon;
 private final String description;
 CodeElementType(final String iconParam, final String descriptionParam) {
   this.icon = iconParam;
 this.description = descriptionParam;
 } public String getIcon() {
   return icon;
 } public String getDescription() {
   return description;
 } }
```

