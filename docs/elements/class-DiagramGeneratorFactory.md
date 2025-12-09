# 📦 DiagramGeneratorFactory

> **Package:** `com.documentor.service.diagram`

---

## 📄 Class Documentation

Error: LLM configuration is null. Please check the application configuration.

---

## 💡 Class Usage Examples

Error: LLM configuration is null. Please check the application configuration.

---

## 📋 Class Signature

```java
/** * 🔍 Diagram Generator Factory * * Factory for creating appropriate diagram generators based on code elements. * Supports both Mermaid and PlantUML diagram generation. */ @Component public class DiagramGeneratorFactory {
   private final MermaidClassDiagramGenerator mermaidClassDiagramGenerator;
 private final PlantUMLClassDiagramGenerator plantUMLClassDiagramGenerator;
 public DiagramGeneratorFactory(final MermaidClassDiagramGenerator mermaidClassDiagramGeneratorParam, final PlantUMLClassDiagramGenerator plantUMLClassDiagramGeneratorParam) {
   this.mermaidClassDiagramGenerator = mermaidClassDiagramGeneratorParam;
 this.plantUMLClassDiagramGenerator = plantUMLClassDiagramGeneratorParam;
 } /** * 🔍 Returns the Mermaid diagram generator */ public MermaidClassDiagramGenerator getClassDiagramGenerator() {
   return mermaidClassDiagramGenerator;
 } /** * 🔍 Returns the Mermaid diagram generator (explicit method name) */ public MermaidClassDiagramGenerator getMermaidClassDiagramGenerator() {
   return mermaidClassDiagramGenerator;
 } /** * 🔍 Returns the PlantUML diagram generator */ public PlantUMLClassDiagramGenerator getPlantUMLClassDiagramGenerator() {
   return plantUMLClassDiagramGenerator;
 } }
```

