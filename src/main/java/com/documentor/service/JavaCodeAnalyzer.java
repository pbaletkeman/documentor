package com.documentor.service;

import com.documentor.config.DocumentorConfig;
import com.documentor.model.CodeElement;
import com.documentor.model.CodeElementType;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * ☕ Java Code Analyzer
 * 
 * Parses Java source files using JavaParser to extract:
 * - Public/protected classes and interfaces
 * - Public/protected methods with signatures
 * - Public/protected fields and variables
 */
@Component
public class JavaCodeAnalyzer {

    private static final Logger logger = LoggerFactory.getLogger(JavaCodeAnalyzer.class);

    private final DocumentorConfig config;
    private final JavaParser javaParser;

    public JavaCodeAnalyzer(DocumentorConfig config) {
        this.config = config;
        this.javaParser = new JavaParser();
    }

    /**
     * 📄 Analyzes a Java file and extracts all non-private code elements
     * 
     * @param filePath Path to the Java source file
     * @return List of discovered code elements
     */
    public List<CodeElement> analyzeFile(Path filePath) throws IOException {
        logger.debug("🔍 Analyzing Java file: {}", filePath);

        String sourceCode = Files.readString(filePath);
        List<CodeElement> elements = new ArrayList<>();

        try {
            CompilationUnit cu = javaParser.parse(sourceCode).getResult()
                    .orElseThrow(() -> new IOException("Failed to parse Java file"));

            JavaElementVisitor visitor = new JavaElementVisitor(filePath, elements);
            visitor.visit(cu, null);

            logger.debug("✅ Found {} elements in {}", elements.size(), filePath.getFileName());
            return elements;

        } catch (Exception e) {
            logger.error("❌ Error parsing Java file {}: {}", filePath, e.getMessage());
            throw new IOException("Failed to analyze Java file", e);
        }
    }

    /**
     * 🚶 AST Visitor for extracting Java code elements
     */
    private class JavaElementVisitor extends VoidVisitorAdapter<Void> {
        
        private final Path filePath;
        private final List<CodeElement> elements;

        public JavaElementVisitor(Path filePath, List<CodeElement> elements) {
            this.filePath = filePath;
            this.elements = elements;
        }

        @Override
        public void visit(ClassOrInterfaceDeclaration declaration, Void arg) {
            if (shouldInclude(declaration.getModifiers())) {
                String name = declaration.getNameAsString();
                String qualifiedName = declaration.getFullyQualifiedName().orElse(name);
                
                CodeElement classElement = new CodeElement(
                    CodeElementType.CLASS,
                    name,
                    qualifiedName,
                    filePath.toString(),
                    declaration.getBegin().map(pos -> pos.line).orElse(0),
                    extractSignature(declaration),
                    extractJavadoc(declaration),
                    List.of(), // Parameters for classes are empty
                    extractAnnotations(declaration)
                );
                
                elements.add(classElement);
                logger.debug("📦 Found class: {}", qualifiedName);
            }

            super.visit(declaration, arg);
        }

        @Override
        public void visit(EnumDeclaration declaration, Void arg) {
            if (shouldInclude(declaration.getModifiers())) {
                String name = declaration.getNameAsString();
                String qualifiedName = declaration.getFullyQualifiedName().orElse(name);
                
                CodeElement enumElement = new CodeElement(
                    CodeElementType.CLASS, // Enums are treated as CLASS type
                    name,
                    qualifiedName,
                    filePath.toString(),
                    declaration.getBegin().map(pos -> pos.line).orElse(0),
                    extractSignature(declaration),
                    extractJavadoc(declaration),
                    List.of(), // Parameters for enums are empty
                    extractAnnotations(declaration)
                );
                
                elements.add(enumElement);
                logger.debug("📦 Found enum: {}", qualifiedName);
            }

            super.visit(declaration, arg);
        }

        @Override
        public void visit(MethodDeclaration declaration, Void arg) {
            if (shouldInclude(declaration.getModifiers())) {
                String name = declaration.getNameAsString();
                String signature = declaration.getDeclarationAsString(false, false, false);
                
                CodeElement methodElement = new CodeElement(
                    CodeElementType.METHOD,
                    name,
                    signature,
                    filePath.toString(),
                    declaration.getBegin().map(pos -> pos.line).orElse(0),
                    signature,
                    extractJavadoc(declaration),
                    extractParameters(declaration),
                    extractAnnotations(declaration)
                );
                
                elements.add(methodElement);
                logger.debug("🔧 Found method: {}", signature);
            }

            super.visit(declaration, arg);
        }

        @Override
        public void visit(FieldDeclaration declaration, Void arg) {
            if (shouldInclude(declaration.getModifiers())) {
                declaration.getVariables().forEach(variable -> {
                    String name = variable.getNameAsString();
                    String type = declaration.getElementType().asString();
                    
                    CodeElement fieldElement = new CodeElement(
                        CodeElementType.FIELD,
                        name,
                        type + " " + name,
                        filePath.toString(),
                        declaration.getBegin().map(pos -> pos.line).orElse(0),
                        type + " " + name,
                        extractJavadoc(declaration),
                        List.of(),
                        extractAnnotations(declaration)
                    );
                    
                    elements.add(fieldElement);
                    logger.debug("📊 Found field: {} {}", type, name);
                });
            }

            super.visit(declaration, arg);
        }

        private boolean shouldInclude(com.github.javaparser.ast.NodeList<com.github.javaparser.ast.Modifier> modifiers) {
            boolean isPrivate = modifiers.stream()
                    .anyMatch(mod -> mod.getKeyword() == com.github.javaparser.ast.Modifier.Keyword.PRIVATE);
            
            return config.analysisSettings().includePrivateMembers() || !isPrivate;
        }

        private String extractSignature(Object declaration) {
            return declaration.toString().split("\\{")[0].trim();
        }

        private String extractJavadoc(Object declaration) {
            // JavaParser provides access to Javadoc comments
            // This is a simplified implementation
            return "";
        }

        private List<String> extractParameters(MethodDeclaration declaration) {
            return declaration.getParameters().stream()
                    .map(param -> param.getType() + " " + param.getName())
                    .toList();
        }

        private List<String> extractAnnotations(Object declaration) {
            // Extract annotations from the declaration
            // This is a simplified implementation
            return List.of();
        }
    }
}