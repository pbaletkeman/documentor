# 📦 LlmPromptTemplates

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
/** * 🔍 LLM Prompt Templates - Centralized prompt generation */ @Component public class LlmPromptTemplates {
   /** * 🔍 Creates documentation generation prompt with technical writer role */ public String createDocumentationPrompt(final CodeElement codeElement) {
   String type = codeElement.type().getDescription().toLowerCase();
 return String.format("You are an expert technical writer " + "specializing in software documentation " + "with years of experience documenting %s components.\n\n" + "Analyze and document this %s:\n\n%s\n\n" + "Provide comprehensive documentation with the following:\n" + "1. A clear, concise description of the %s's purpose " + "and functionality\n" + "2. Detailed parameter descriptions including types, " + "constraints, and whether optional/required\n" + "3. Return value information with possible values " + "and conditions\n" + "4. Usage notes highlighting best practices, edge cases, " + "and common pitfalls\n" + "5. Any threading or performance considerations\n" + "6. Links to related components when appropriate\n\n" + "Format your documentation using standard documentation " + "conventions. Be thorough but precise.", type, type, codeElement.getAnalysisContext(), type);
 } /** * 💡 Creates usage example generation prompt with developer advocate role */ public String createUsageExamplePrompt(final CodeElement codeElement) {
   String type = codeElement.type().getDescription().toLowerCase();
 return String.format("You are a senior developer advocate " + "responsible for creating high-quality code examples " + "that demonstrate proper usage of APIs and components.\n\n" + "Generate practical, real-world usage examples " + "for this %s:\n\n%s\n\n" + "Provide 3-4 diverse examples that include:\n" + "1. A basic example showing standard usage with clear " + "inputs and outputs\n" + "2. An intermediate example demonstrating integration " + "with other components\n" + "3. An advanced example showing best practices for error " + "handling, performance optimization, \n" + " or complex scenarios\n" + "4. Where applicable, examples showing what NOT to do " + "(anti-patterns)\n\n" + "For each example:\n" + "- Use realistic, meaningful variable names and data\n" + "- Include expected outputs or behavior\n" + "- Add brief explanations for each step\n" + "- Ensure code is idiomatic and follows language " + "conventions", type, codeElement.getAnalysisContext());
 } /** * 🧪 Creates unit test generation prompt with QA engineer role */ public String createUnitTestPrompt(final CodeElement codeElement) {
   String type = codeElement.type().getDescription().toLowerCase();
 return String.format("You are a quality assurance engineer " + "with expertise in test-driven development " + "and extensive experience testing %s components.\n\n" + "Generate comprehensive unit tests for this %s:\n\n%s\n\n" + "Create a thorough test suite that includes:\n" + "1. Basic functionality tests covering the main " + "execution paths\n" + "2. Edge case tests for boundary conditions and " + "unusual inputs\n" + "3. Error handling tests verifying appropriate " + "exceptions and error states\n" + "4. Performance tests where applicable " + "(e.g., handling large inputs)\n" + "5. Mocking strategies for external dependencies\n\n" + "For each test:\n" + "- Use descriptive test method names following the pattern " + "'testShouldXWhenY'\n" + "- Include proper test setup and teardown where needed\n" + "- Use appropriate assertions with meaningful error " + "messages\n" + "- Add comments explaining the test purpose and expected " + "behavior\n" + "- Follow testing best practices for the language " + "and framework", type, type, codeElement.getAnalysisContext());
 } }
```

