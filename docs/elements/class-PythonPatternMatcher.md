# 📦 PythonPatternMatcher

> **Package:** `com.documentor.service.python`

---

## 📄 Class Documentation

Error: LLM configuration is null. Please check the application configuration.

---

## 💡 Class Usage Examples

Error: LLM configuration is null. Please check the application configuration.

---

## 📋 Class Signature

```java
/** * Python Pattern Matcher * * Helper class to extract pattern matches from Python code. * Extracted from PythonRegexAnalyzer to reduce complexity. */ @Component public class PythonPatternMatcher {
   // Class pattern: "class Name[(Parent)]:" private final Pattern classPattern = Pattern.compile("^\\s*class\\s+(\\w+)(?:\\([^)]*\\))?\\s*:", Pattern.MULTILINE);
 // Function pattern: "def name(param1, param2...):" private final Pattern functionPattern = Pattern.compile("^\\s*def\\s+(\\w+)\\s*\\(([^)]*)\\)\\s*:", Pattern.MULTILINE);
 // Variable pattern: Simple variable assignments private final Pattern variablePattern = Pattern.compile("^\\s*(\\w+)\\s*=\\s*(.+)$", Pattern.MULTILINE);
 // Docstring pattern: Triple quotes after class/def private final Pattern docstringPattern = Pattern.compile("(?:'''|\"\"\")([^'\"]*?)(?:'''|\"\"\")", Pattern.DOTALL);
 /** * Finds all class matches in the given content */ public Matcher findClassMatches(final String content) {
   return classPattern.matcher(content);
 } /** * Finds all function matches in the given content */ public Matcher findFunctionMatches(final String content) {
   return functionPattern.matcher(content);
 } /** * Finds all variable matches in the given content */ public Matcher findVariableMatches(final String content) {
   return variablePattern.matcher(content);
 } /** * Finds docstring in the given content */ public String findDocstring(final String content) {
   Matcher docstringMatcher = docstringPattern.matcher(content);
 return docstringMatcher.find() ? docstringMatcher.group(1).trim() : "";
 } /** * Extracts parameters from function signature */ public String[] extractParameters(final String paramString) {
   if (paramString == null || paramString.trim().isEmpty()) {
   return new String[0];
 } return paramString.split("\\s*,\\s*");
 } }
```

