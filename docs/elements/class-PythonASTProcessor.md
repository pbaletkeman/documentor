# 📦 PythonASTProcessor

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
/** * 🐍 Python AST Processor * * Specialized component for analyzing Python files using Python's AST module * via subprocess execution. Refactored for reduced complexity. */ @Component public class PythonASTProcessor {
   private static final Logger LOGGER = LoggerFactory.getLogger(PythonASTProcessor.class);
 // Logger used in future error handling methods - required by design private final PythonASTCommandBuilder commandBuilder;
 public PythonASTProcessor(final PythonASTCommandBuilder commandBuilderParam) {
   this.commandBuilder = commandBuilderParam;
 } /** * 🔬 Analyzes Python file using Python's AST module via subprocess */ public List<CodeElement> analyzeWithAST(final Path filePath) throws IOException, InterruptedException {
   List<CodeElement> elements = new ArrayList<>();
 Path tempScript = null;
 try {
   // Get temporary script from command builder tempScript = commandBuilder.writeTempScript();
 // Create and execute process ProcessBuilder pb = commandBuilder.createProcessBuilder(tempScript, filePath);
 Process process = pb.start();
 // Process the output elements = processOutput(process, filePath);
 // Check exit code int exitCode = process.waitFor();
 if (exitCode != 0) {
   throw new IOException("Python AST analysis failed with exit code: " + exitCode);
 } } finally {
   // Clean up if (tempScript != null) {
   Files.deleteIfExists(tempScript);
 } } return elements;
 } /** * 📋 Processes the output of the Python process */ private List<CodeElement> processOutput(final Process process, final Path filePath) throws IOException {
   List<CodeElement> elements = new ArrayList<>();
 try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
   String line;
 while ((line = reader.readLine()) != null) {
   try {
   CodeElement element = commandBuilder.parseASTOutputLine(line, filePath);
 if (element != null) {
   elements.add(element);
 } } catch (Exception e) {
   LOGGER.warn("Failed to parse Python AST output line: {}", line, e);
 } } } return elements;
 } }
```

