# 📦 DocumentationService

> **Package:** `com.documentor.service`

---

## 📄 Class Documentation

Error: LLM configuration is null. Please check the application configuration.

---

## 💡 Class Usage Examples

Error: LLM configuration is null. Please check the application configuration.

---

## 📋 Class Signature

```java
/** * 📄 Documentation Generation Service - Enhanced with PlantUML Support * * Orchestrates the generation of markdown documentation from code analysis * results. Delegates to specialized generators for different types of * documentation. Now supports both Mermaid and PlantUML diagram generation. */ @Service public class DocumentationService {
   private static final Logger LOGGER = LoggerFactory.getLogger(DocumentationService.class);
 private final MainDocumentationGenerator mainDocGenerator;
 private final ElementDocumentationGenerator elementDocGenerator;
 private final UnitTestDocumentationGenerator testDocGenerator;
 private final MermaidDiagramService mermaidDiagramService;
 private final PlantUMLDiagramService plantUMLDiagramService;
 private final DocumentorConfig config;
 public DocumentationService(final MainDocumentationGenerator mainDocGeneratorParam, final ElementDocumentationGenerator elementDocGeneratorParam, final UnitTestDocumentationGenerator testDocGeneratorParam, final MermaidDiagramService mermaidDiagramServiceParam, final PlantUMLDiagramService plantUMLDiagramServiceParam, final DocumentorConfig configParam) {
   this.mainDocGenerator = mainDocGeneratorParam;
 this.elementDocGenerator = elementDocGeneratorParam;
 this.testDocGenerator = testDocGeneratorParam;
 this.mermaidDiagramService = mermaidDiagramServiceParam;
 this.plantUMLDiagramService = plantUMLDiagramServiceParam;
 this.config = configParam;
 } /** * Generate comprehensive documentation for a project * (uses config settings for diagram generation) * * @param analysis The project analysis results * @return CompletableFuture containing the path to generated * documentation */ public CompletableFuture<String> generateDocumentation(final ProjectAnalysis analysis) {
   return generateDocumentation(analysis, false);
 } /** * Generate documentation with control over diagram generation * @param analysis The project analysis * @param skipDiagrams If true, skip internal diagram generation * (allows CLI to handle it) * @return Path to generated documentation */ public CompletableFuture<String> generateDocumentation(final ProjectAnalysis analysis, final boolean skipDiagrams) {
   LOGGER.info("📄 Starting documentation generation for project: {}", analysis.projectPath());
 return CompletableFuture.supplyAsync(() -> {
   try {
   // Create output directory Path outputPath = Paths.get(config.outputSettings().outputPath());
 Files.createDirectories(outputPath);
 // Generate main documentation String mainDoc = mainDocGenerator.generateMainDocumentation(analysis).join();
 Path mainDocPath = outputPath.resolve("README.md");
 Files.write(mainDocPath, mainDoc.getBytes());
 // Generate detailed documentation for each element generateDetailedDocumentation(analysis, outputPath).join();
 // Generate unit tests if enabled if (config.outputSettings().generateUnitTests() != null && config.outputSettings().generateUnitTests()) {
   LOGGER.info("Generating unit tests as specified " + "in configuration");
 testDocGenerator.generateUnitTestDocumentation(analysis, outputPath).join();
 } else {
   LOGGER.info("Unit test generation is disabled " + "in configuration - skipping");
 } // Generate diagrams only if not skipped // When skipped, CLI handler will manage diagram generation if (!skipDiagrams) {
   // Generate Mermaid diagrams if enabled if (config.outputSettings().generateMermaidDiagrams()) {
   List<String> diagramPaths = mermaidDiagramService.generateClassDiagrams(analysis, config.outputSettings().mermaidOutputPath(), config.outputSettings().getMermaidNamingOrDefault()).join();
 LOGGER.info("✅ Generated {} Mermaid diagrams", diagramPaths.size());
 } // Generate PlantUML diagrams if enabled if (config.outputSettings().generatePlantUMLDiagrams()) {
   List<String> plantUMLPaths = plantUMLDiagramService.generateClassDiagrams(analysis, config.outputSettings().plantUMLOutputPath(), config.outputSettings().getPlantumlNamingOrDefault()).join();
 LOGGER.info("✅ Generated {} PlantUML diagrams", plantUMLPaths.size());
 } } else {
   LOGGER.info("⏭️ Skipping internal diagram generation " + "(will be handled by CLI)");
 } LOGGER.info("✅ Documentation generated successfully at: {}", outputPath);
 return outputPath.toString();
 } catch (Exception e) {
   LOGGER.error("❌ Error generating documentation: {}", e.getMessage(), e);
 throw new RuntimeException("Failed to generate documentation", e);
 } });
 } /** * 📝 Generates detailed documentation for each code element */ private CompletableFuture<Void> generateDetailedDocumentation(final ProjectAnalysis analysis, final Path outputPath) {
   LOGGER.info("Generating grouped documentation for {} elements...", analysis.codeElements().size());
 // Make sure we have a valid list of elements to process if (analysis.codeElements() == null || analysis.codeElements().isEmpty()) {
   LOGGER.info("No code elements to document");
 return CompletableFuture.completedFuture(null);
 } try {
   // Create the elements directory structure Path elementsDir = outputPath.resolve("elements");
 Files.createDirectories(elementsDir);
 } catch (Exception e) {
   LOGGER.error("Failed to create elements directory: {}", e.getMessage());
 return CompletableFuture.completedFuture(null);
 } // Use the new grouped documentation generation approach return elementDocGenerator.generateGroupedDocumentation(analysis, outputPath);
 } }
```

