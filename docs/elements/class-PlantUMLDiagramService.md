# 📦 PlantUMLDiagramService

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
/** * 📊 PlantUML Diagram Service * * Service for generating PlantUML diagrams from code analysis. * Follows the same pattern as MermaidDiagramService for consistency. */ @Service public class PlantUMLDiagramService {
   private static final Logger LOGGER = LoggerFactory.getLogger(PlantUMLDiagramService.class);
 private final DiagramElementFilter elementFilter;
 private final DiagramPathManager pathManager;
 private final DiagramGeneratorFactory generatorFactory;
 public PlantUMLDiagramService(final DiagramElementFilter elementFilterParam, final DiagramPathManager pathManagerParam, final DiagramGeneratorFactory generatorFactoryParam) {
   this.elementFilter = elementFilterParam;
 this.pathManager = pathManagerParam;
 this.generatorFactory = generatorFactoryParam;
 } /** * 📊 Generates class diagrams for the analyzed project */ public CompletableFuture<List<String>> generateClassDiagrams(final ProjectAnalysis analysis, final String outputPath) {
   return generateClassDiagrams(analysis, outputPath, null);
 } /** * 📊 Generates class diagrams with custom naming options */ public CompletableFuture<List<String>> generateClassDiagrams(final ProjectAnalysis analysis, final String outputPath, final DiagramNamingOptions namingOptions) {
   return CompletableFuture.supplyAsync(() -> {
   LOGGER.info("📊 Starting PlantUML diagram generation for {} " + "elements", analysis.codeElements().size());
 try {
   return generateDiagrams(analysis, outputPath, namingOptions);
 } catch (Exception e) {
   LOGGER.error("❌ Error generating PlantUML diagrams: {}", e.getMessage(), e);
 throw new RuntimeException("Failed to generate PlantUML diagrams", e);
 } });
 } /** * 📊 Core diagram generation logic */ private List<String> generateDiagrams(final ProjectAnalysis analysis, final String outputPath, final DiagramNamingOptions namingOptions) {
   List<String> generatedFiles = new ArrayList<>();
 // Get eligible classes for diagram generation List<CodeElement> eligibleClasses = elementFilter.getEligibleClasses(analysis);
 // Group elements by class Map<CodeElement, List<CodeElement>> elementsByClass = elementFilter.groupElementsByClass(analysis);
 // Process each eligible class eligibleClasses.forEach(classElement -> {
   try {
   String diagram = processSingleClassDiagram(classElement, elementsByClass, outputPath, namingOptions);
 generatedFiles.add(diagram);
 } catch (Exception e) {
   LOGGER.warn("⚠️ Failed to generate PlantUML diagram for {}: {}", classElement.name(), e.getMessage());
 } });
 LOGGER.info("✅ Generated {} PlantUML diagrams", generatedFiles.size());
 return generatedFiles;
 } /** * 📊 Process a single class diagram */ private String processSingleClassDiagram(final CodeElement classElement, final Map<CodeElement, List<CodeElement>> elementsByClass, final String outputPath, final DiagramNamingOptions namingOptions) throws Exception {
   List<CodeElement> classElements = elementsByClass.get(classElement);
 // Determine output path String resolvedOutputPath = pathManager.determineOutputPath(classElement.filePath(), outputPath);
 Path outputDir = pathManager.createOutputDirectory(resolvedOutputPath);
 // Ensure output directory exists Files.createDirectories(outputDir);
 // Generate the diagram return generatorFactory.getPlantUMLClassDiagramGenerator().generateClassDiagram(classElement, classElements, outputDir, namingOptions);
 } }
```

