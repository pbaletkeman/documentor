# 📦 DocumentationFormatter

> **Package:** `com.documentor.service.documentation`

---

## 📄 Class Documentation

Error: LLM configuration is null. Please check the application configuration.

---

## 💡 Class Usage Examples

Error: LLM configuration is null. Please check the application configuration.

---

## 📋 Class Signature

```java
/** * 🔍 Documentation Formatter * * Handles formatting of documentation sections. * Reduces DocumentationService complexity by extracting formatting logic. */ @Component public class DocumentationFormatter {
   /** * 🔍 Creates header section for documentation */ public void appendHeader(final StringBuilder doc, final ProjectAnalysis analysis) {
   doc.append("# ").append(getProjectName(analysis.projectPath())).append("\n\n");
 doc.append("*Generated on: ").append(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).append("*\n\n");
 if (analysis.projectPath() != null && !analysis.projectPath().isEmpty()) {
   doc.append("**Project Path:** `").append(analysis.projectPath()).append("`\n\n");
 } } /** * 🔍 Creates statistics section */ public void appendStatistics(final StringBuilder doc, final ProjectAnalysis analysis) {
   doc.append("## 🔍 Project Statistics\n\n");
 List<CodeElement> elements = analysis.codeElements();
 long classCount = elements.stream().filter(e -> e.type().name().equals("CLASS")).count();
 long methodCount = elements.stream().filter(e -> e.type().name().equals("METHOD")).count();
 long fieldCount = elements.stream().filter(e -> e.type().name().equals("FIELD")).count();
 doc.append("- **Total Elements:** ").append(elements.size()).append("\n");
 doc.append("- **Classes:** ").append(classCount).append("\n");
 doc.append("- **Methods:** ").append(methodCount).append("\n");
 doc.append("- **Fields:** ").append(fieldCount).append("\n\n");
 } /** * 🔍 Creates usage examples section */ public void appendUsageExamples(final StringBuilder doc, final ProjectAnalysis analysis) {
   doc.append("## 🔍 Usage Examples\n\n");
 if (analysis.codeElements().isEmpty()) {
   doc.append("*No code elements found for examples.*\n\n");
 return;
 } doc.append("*Usage examples will be generated for key " + "components.*\n\n");
 } /** * 🔍 Creates API reference section */ public void appendApiReference(final StringBuilder doc, final ProjectAnalysis analysis) {
   doc.append("## 🔍 API Reference\n\n");
 if (analysis.codeElements().isEmpty()) {
   doc.append("*No API elements found.*\n\n");
 return;
 } doc.append("*Detailed API documentation will be generated.*\n\n");
 } /** * 🧪 Creates test documentation header */ public void appendTestDocumentationHeader(final StringBuilder doc) {
   doc.append("# 🧪 Unit Test Documentation\n\n");
 doc.append("*Generated test documentation and examples*\n\n");
 } /** * 🔍 Extracts project name from path */ private String getProjectName(final String projectPath) {
   if (projectPath == null || projectPath.isEmpty()) {
   return "Project Documentation";
 } String[] pathParts = projectPath.replace("\\", "/").split("/"); return pathParts[pathParts.length - 1]; } }
```

