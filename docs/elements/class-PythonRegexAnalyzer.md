# 📦 PythonRegexAnalyzer

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
/** * Python Regex Analyzer * * Specialized component for regex-based Python code analysis as a fallback * when AST parsing is not available or fails. * Refactored to reduce complexity by using PythonPatternMatcher. */ @Component public class PythonRegexAnalyzer {
   private final DocumentorConfig config;
 private final PythonElementExtractor elementExtractor;
 private final PythonPatternMatcher patternMatcher;
 public PythonRegexAnalyzer(final DocumentorConfig configParam, final PythonElementExtractor elementExtractorParam, final PythonPatternMatcher patternMatcherParam) {
   this.config = configParam;
 this.elementExtractor = elementExtractorParam;
 this.patternMatcher = patternMatcherParam;
 } /** * Fallback regex-based analysis for when AST parsing fails */ public List<CodeElement> analyzeWithRegex(final Path filePath, final List<String> lines) {
   List<CodeElement> elements = new ArrayList<>();
 String content = String.join("\n", lines);
 // Process each type of element processClassElements(filePath, lines, content, elements);
 processFunctionElements(filePath, lines, content, elements);
 processVariableElements(filePath, content, elements);
 return elements;
 } /** * Process class declarations */ private void processClassElements(final Path filePath, final List<String> lines, final String content, final List<CodeElement> elements) {
   var matcher = patternMatcher.findClassMatches(content);
 while (matcher.find()) {
   String className = matcher.group(1);
 if (shouldInclude(className)) {
   int lineNumber = getLineNumber(content, matcher.start());
 elements.add(new CodeElement(CodeElementType.CLASS, className, "class " + className, filePath.toString(), lineNumber, matcher.group().trim(), elementExtractor.extractDocstring(lines, lineNumber), List.of(), List.of()));
 } } } /** * Process function declarations */ private void processFunctionElements(final Path filePath, final List<String> lines, final String content, final List<CodeElement> elements) {
   var matcher = patternMatcher.findFunctionMatches(content);
 while (matcher.find()) {
   String functionName = matcher.group(1);
 if (shouldInclude(functionName)) {
   int lineNumber = getLineNumber(content, matcher.start());
 String paramString = matcher.group(2);
 List<String> params = List.of(patternMatcher.extractParameters(paramString));
 elements.add(new CodeElement(CodeElementType.METHOD, functionName, matcher.group().trim(), filePath.toString(), lineNumber, matcher.group().trim(), elementExtractor.extractDocstring(lines, lineNumber), params, List.of()));
 } } } /** * Process variable assignments */ private void processVariableElements(final Path filePath, final String content, final List<CodeElement> elements) {
   var matcher = patternMatcher.findVariableMatches(content);
 while (matcher.find()) {
   String variableName = matcher.group(1);
 if (shouldInclude(variableName)) {
   int lineNumber = getLineNumber(content, matcher.start());
 elements.add(new CodeElement(CodeElementType.FIELD, variableName, variableName, filePath.toString(), lineNumber, matcher.group().trim(), "", List.of(), List.of()));
 } } } /** * Determines line number from character position */ private int getLineNumber(final String content, final int position) {
   int line = 1;
 for (int i = 0;
 i < position;
 i++) {
   if (content.charAt(i) == '\n') {
   line++;
 } } return line;
 } /** * Checks if an element should be included based on configuration */ private boolean shouldInclude(final String name) {
   boolean isPrivate = name.startsWith("_");
 return config.analysisSettings().includePrivateMembers() || !isPrivate;
 } }
```

