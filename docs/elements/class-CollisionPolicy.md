# 📦 CollisionPolicy

> **Package:** `com.documentor.service.io`

---

## 📄 Class Documentation

Error: LLM configuration is null. Please check the application configuration.

---

## 💡 Class Usage Examples

Error: LLM configuration is null. Please check the application configuration.

---

## 📋 Class Signature

```java
/** * 📝 Collision Policy - Defines behavior when file already exists * * Specifies how the AtomicFileWriter should handle situations where the target * file already exists during write operations. */ public enum CollisionPolicy { /** * Overwrite the existing file with new content. This is the default behavior * and is useful for output files that should always be refreshed. */ OVERWRITE, /** * Skip writing and return false if the file already exists. Useful for * preserving existing files that should not be modified. */ SKIP, /** * Add a numeric suffix to the filename before the extension if the file * exists (e.g., file.txt → file_1.txt, file_2.txt, etc.). Useful for * creating versioned outputs. */ SUFFIX }
```

