# 📦 JavaElementVisitor

> **Package:** `com.documentor.service.analysis`

---

## 📄 Class Documentation

Error: LLM configuration is null. Please check the application configuration.

---

## 💡 Class Usage Examples

Error: LLM configuration is null. Please check the application configuration.

---

## 📋 Class Signature

```java
/** * 🔍 Java AST Visitor for extracting code elements * * Specialized visitor that traverses the Java AST and extracts * classes, methods, and fields based on visibility rules. */ @Component public class JavaElementVisitor extends VoidVisitorAdapter<Void> {
   private final DocumentorConfig config;
 private Path filePath;
 private List<CodeElement> elements;
 private Boolean includePrivateMembersOverride;
 public JavaElementVisitor(final DocumentorConfig configParam) {
   this.config = configParam;
 } /** * Initialize visitor with file context */ public void initialize(final Path filePathParam, final List<CodeElement> elementsParam) {
   initialize(filePathParam, elementsParam, null);
 } /** * Initialize visitor with file context and optional private member * override */ public void initialize(final Path filePathParam, final List<CodeElement> elementsParam, final Boolean includePrivateMembersOverrideParam) {
   this.filePath = filePathParam;
 this.elements = elementsParam;
 this.includePrivateMembersOverride = includePrivateMembersOverrideParam;
 } @Override public final void visit(final ClassOrInterfaceDeclaration declaration, final Void arg) {
   if (shouldInclude(declaration.getModifiers())) {
   String name = declaration.getNameAsString();
 String qualifiedName = declaration.getFullyQualifiedName().orElse(name);
 CodeElement classElement = new CodeElement(CodeElementType.CLASS, name, qualifiedName, filePath.toString(), declaration.getBegin().map(pos -> pos.line).orElse(0), extractSignature(declaration), extractJavadoc(declaration), // Parameters for classes are empty List.of(), extractAnnotations(declaration));
 elements.add(classElement);
 } super.visit(declaration, arg);
 } @Override public final void visit(final EnumDeclaration declaration, final Void arg) {
   if (shouldInclude(declaration.getModifiers())) {
   String name = declaration.getNameAsString();
 String qualifiedName = declaration.getFullyQualifiedName().orElse(name);
 CodeElement enumElement = new CodeElement(// Treat enums as classes CodeElementType.CLASS, name, qualifiedName, filePath.toString(), declaration.getBegin().map(pos -> pos.line).orElse(0), extractSignature(declaration), extractJavadoc(declaration), List.of(), extractAnnotations(declaration));
 elements.add(enumElement);
 } super.visit(declaration, arg);
 } @Override public final void visit(final MethodDeclaration declaration, final Void arg) {
   if (shouldInclude(declaration.getModifiers())) {
   String name = declaration.getNameAsString();
 String signature = declaration.getDeclarationAsString();
 CodeElement methodElement = new CodeElement(CodeElementType.METHOD, name, signature, filePath.toString(), declaration.getBegin().map(pos -> pos.line).orElse(0), signature, extractJavadoc(declaration), extractParameters(declaration), extractAnnotations(declaration));
 elements.add(methodElement);
 } super.visit(declaration, arg);
 } @Override public final void visit(final FieldDeclaration declaration, final Void arg) {
   if (shouldInclude(declaration.getModifiers())) {
   declaration.getVariables().forEach(variable -> {
   String name = variable.getNameAsString();
 // Simplified for now String qualifiedName = name;
 CodeElement fieldElement = new CodeElement(CodeElementType.FIELD, name, qualifiedName, filePath.toString(), declaration.getBegin().map(pos -> pos.line).orElse(0), declaration.toString().replace("\n", " ").replaceAll("\\s+", " ").trim(), extractJavadoc(declaration), List.of(), extractAnnotations(declaration));
 elements.add(fieldElement);
 });
 } super.visit(declaration, arg);
 } private boolean shouldInclude(final com.github.javaparser.ast.NodeList<com.github.javaparser.ast.Modifier> modifiers) {
   // Use override if provided, otherwise use config setting boolean includePrivateMembers = includePrivateMembersOverride != null ? includePrivateMembersOverride : config.analysisSettings().includePrivateMembers();
 if (includePrivateMembers) {
   return true;
 } boolean isPrivate = modifiers.stream().anyMatch(mod -> mod.getKeyword() == com.github.javaparser.ast.Modifier.Keyword.PRIVATE);
 return !isPrivate;
 } private String extractSignature(final ClassOrInterfaceDeclaration declaration) {
   return declaration.toString().replace("\n", " ").replaceAll("\\s+", " ").trim();
 } private String extractSignature(final EnumDeclaration declaration) {
   return declaration.toString().replace("\n", " ").replaceAll("\\s+", " ").trim();
 } private String extractJavadoc(final com.github.javaparser.ast.nodeTypes.NodeWithJavadoc<?> node) {
   return node.getJavadoc().map(javadoc -> javadoc.getDescription().toText()).orElse("");
 } private List<String> extractParameters(final MethodDeclaration declaration) {
   return declaration.getParameters().stream().map(param -> param.getType().asString() + " " + param.getNameAsString()).toList();
 } private List<String> extractAnnotations(final com.github.javaparser.ast.nodeTypes.NodeWithAnnotations<?> node) {
   return node.getAnnotations().stream().map(annotation -> "@" + annotation.getNameAsString()).toList();
 } }
```

