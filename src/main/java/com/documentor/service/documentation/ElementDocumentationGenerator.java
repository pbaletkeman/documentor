package com.documentor.service.documentation;

import com.documentor.model.CodeElement;
import com.documentor.model.CodeElementType;
import com.documentor.model.ProjectAnalysis;
import com.documentor.service.LlmService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Element Documentation Generator
 *
 * Specialized component for generating detailed documentation for code elements.
 * Handles element-specific documentation files with LLM-generated content.
 * Elements are grouped by their parent class to create comprehensive class-level documentation
 * that includes all related methods and fields.
 */
@Component
public class ElementDocumentationGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ElementDocumentationGenerator.class);

    private final LlmService llmService;

    public ElementDocumentationGenerator(final LlmService llmServiceParam) {
        this.llmService = llmServiceParam;
    }

    /**
     * Generates documentation for a single code element
     *
     * This creates a single-element project analysis and delegates to the grouped approach
     */
    public CompletableFuture<Void> generateElementDocumentation(final CodeElement element, final Path outputPath) {
        LOGGER.info("ElementDocumentationGenerator using LlmService: {}",
                   (llmService != null ? "OK" : "NULL"));

        if (llmService == null) {
            LOGGER.error("LlmService is null in ElementDocumentationGenerator");
            return CompletableFuture.completedFuture(null);
        }

        // Create a single-element collection and process it using the grouped approach
        List<CodeElement> singleElement = new ArrayList<>();
        singleElement.add(element);

        ProjectAnalysis miniAnalysis = new ProjectAnalysis(
            element.filePath(),
            singleElement,
            System.currentTimeMillis()
        );

        // Use the grouped approach directly
        return generateGroupedDocumentation(miniAnalysis, outputPath);
    }

    /**
     * Generates documentation for code elements grouped by class
     *
     * @param analysis Project analysis containing all code elements
     * @param outputPath Output directory for documentation files
     * @return CompletableFuture that completes when all documentation is generated
     */
    public CompletableFuture<Void> generateGroupedDocumentation(final ProjectAnalysis analysis, final Path outputPath) {
        LOGGER.info("Generating grouped documentation for {} elements", analysis.codeElements().size());

        if (llmService == null) {
            LOGGER.error("LlmService is null in ElementDocumentationGenerator");
            return CompletableFuture.completedFuture(null);
        }

        // Group elements by their parent class
        Map<String, List<CodeElement>> elementsByClass = groupElementsByClass(analysis.codeElements());

        // Create a list to hold all the futures for each class documentation
        List<CompletableFuture<Void>> allFutures = new ArrayList<>();

        // Process each class and its related elements
        for (Map.Entry<String, List<CodeElement>> entry : elementsByClass.entrySet()) {
            String className = entry.getKey();
            List<CodeElement> classElements = entry.getValue();

            // Find the class element itself
            CodeElement classElement = classElements.stream()
                .filter(e -> e.type() == CodeElementType.CLASS && e.qualifiedName().equals(className))
                .findFirst()
                .orElse(null);

            // Skip if we can't find the class element and it's not a special group
            if (classElement == null && !className.equals("_FIELDS_") && !className.equals("_METHODS_")) {
                LOGGER.warn("Could not find class element for {}, skipping", className);
                continue;
            }

            // Generate documentation for this class group
            CompletableFuture<Void> classFuture = generateClassDocumentation(classElement, classElements, outputPath);
            allFutures.add(classFuture);
        }

        // Wait for all class documentation to complete
        return CompletableFuture.allOf(allFutures.toArray(new CompletableFuture[0]));
    }

    /**
     * Generates documentation for a class and all its related elements
     *
     * @param classElement the class element
     * @param classElements the class elements
     * @param outputPath the output path
     * @return a future that completes when documentation is generated
     */
    private CompletableFuture<Void> generateClassDocumentation(final CodeElement classElement,
                                                               final List<CodeElement> classElements,
                                                               final Path outputPath) {
        LOGGER.info("Generating documentation for class: {}",
                   classElement != null ? classElement.name() : "Standalone elements");

        // Split elements by type
        List<CodeElement> fields = classElements.stream()
            .filter(e -> e.type() == CodeElementType.FIELD)
            .toList();

        List<CodeElement> methods = classElements.stream()
            .filter(e -> e.type() == CodeElementType.METHOD)
            .toList();

        // Generate documentation for class if it exists
        CompletableFuture<String> classFuture = classElement != null
            ? llmService.generateDocumentation(classElement)
            : CompletableFuture.completedFuture("");

        // Generate examples for class if it exists
        CompletableFuture<String> classExamplesFuture = classElement != null
            ? llmService.generateUsageExamples(classElement)
            : CompletableFuture.completedFuture("");

        // Create lists to hold futures for fields and methods
        List<CompletableFuture<ElementDocPair>> fieldFutures = new ArrayList<>();
        List<CompletableFuture<ElementDocPair>> methodFutures = new ArrayList<>();

        // Generate documentation for each field
        for (CodeElement field : fields) {
            fieldFutures.add(generateElementDocPair(field));
        }

        // Generate documentation for each method
        for (CodeElement method : methods) {
            methodFutures.add(generateElementDocPair(method));
        }

        // Wait for all field documentation to complete
        CompletableFuture<List<ElementDocPair>> allFieldsFuture =
            CompletableFuture.allOf(fieldFutures.toArray(new CompletableFuture[0]))
                .thenApply(v -> fieldFutures.stream()
                    .map(CompletableFuture::join)
                    .toList());

        // Wait for all method documentation to complete
        CompletableFuture<List<ElementDocPair>> allMethodsFuture =
            CompletableFuture.allOf(methodFutures.toArray(new CompletableFuture[0]))
                .thenApply(v -> methodFutures.stream()
                    .map(CompletableFuture::join)
                    .toList());

        // Combine everything into a final document
        return CompletableFuture.allOf(classFuture, classExamplesFuture, allFieldsFuture, allMethodsFuture)
            .thenApply(v -> {
                try {
                    // Create the content
                    String content = buildClassDocumentContent(
                        classElement,
                        classFuture.join(),
                        classExamplesFuture.join(),
                        allFieldsFuture.join(),
                        allMethodsFuture.join()
                    );

                    // Determine the file name
                    String fileName;
                    if (classElement != null) {
                        fileName = String.format("class-%s.md",
                            classElement.name().replaceAll("[^a-zA-Z0-9]", "_"));
                    } else {
                        // For standalone elements
                        String packageName = classElements.get(0).qualifiedName().split("\\.")[0];
                        fileName = String.format("standalone-%s.md", packageName);
                    }

                    // Write to file
                    Path elementPath = outputPath.resolve("elements").resolve(fileName);
                    Files.createDirectories(elementPath.getParent());
                    Files.write(elementPath, content.getBytes());

                    return null;
                } catch (IOException e) {
                    LOGGER.error("❌ Error writing class documentation: {}", e.getMessage());
                    throw new RuntimeException("Failed to write class documentation", e);
                }
            });
    }

    /**
     * Helper class to store an element and its documentation/examples
     */
    private static class ElementDocPair {
        private final CodeElement element;
        private final String documentation;
        private final String examples;

        /**
         * Constructor for ElementDocPair.
         *
         * @param codeElement the code element
         * @param docContent the documentation content
         * @param exampleContent the examples content
         */
        ElementDocPair(final CodeElement codeElement, final String docContent, final String exampleContent) {
            this.element = codeElement;
            this.documentation = docContent;
            this.examples = exampleContent;
        }

        /**
         * Gets the element.
         *
         * @return the element
         */
        public CodeElement getElement() {
            return element;
        }

        /**
         * Gets the documentation.
         *
         * @return the documentation
         */
        public String getDocumentation() {
            return documentation;
        }

        /**
         * Gets the examples.
         *
         * @return the examples
         */
        public String getExamples() {
            return examples;
        }
    }

    /**
     * Generates documentation and examples for a single element
     *
     * @param codeElement the code element
     * @return a future with element documentation pair
     */
    private CompletableFuture<ElementDocPair> generateElementDocPair(final CodeElement codeElement) {
        return llmService.generateDocumentation(codeElement)
            .thenCombine(llmService.generateUsageExamples(codeElement),
                (docContent, exampleContent) -> new ElementDocPair(codeElement, docContent, exampleContent));
    }

    /**
     * Builds the complete documentation content for a class and its elements
     *
     * @param classElement the class element
     * @param classDoc the class documentation
     * @param classExamples the class examples
     * @param fields the fields
     * @param methods the methods
     * @return the documentation content
     */
    private String buildClassDocumentContent(final CodeElement classElement,
                                           final String classDoc,
                                           final String classExamples,
                                           final List<ElementDocPair> fields,
                                           final List<ElementDocPair> methods) {
        StringBuilder content = new StringBuilder();

        // Class header and documentation
        if (classElement != null) {
            // Add class name and package info
            content.append(String.format("# %s %s\n\n", classElement.type().getIcon(), classElement.name()));
            String packageName = classElement.qualifiedName().substring(0,
                classElement.qualifiedName().lastIndexOf('.'));
            content.append(String.format("> **Package:** `%s`\n\n", packageName));

            // Add class documentation
            content.append("## Class Documentation\n\n");
            content.append(classDoc).append("\n\n");

            // Add class usage examples
            content.append("## Class Usage Examples\n\n");
            content.append(classExamples).append("\n\n");

            // Add class signature
            content.append("## Class Signature\n\n");
            content.append("```").append(getLanguageFromFile(classElement.filePath())).append("\n");
            content.append(classElement.signature()).append("\n");
            content.append("```\n\n");

            // Add table of contents for fields and methods
            if (!fields.isEmpty() || !methods.isEmpty()) {
                content.append("## Table of Contents\n\n");

                if (!fields.isEmpty()) {
                    content.append("### Fields\n\n");
                    for (ElementDocPair field : fields) {
                        content.append(String.format("- [%s %s](#%s)\n",
                            field.getElement().type().getIcon(),
                            field.getElement().name(),
                            field.getElement().name().toLowerCase().replace(' ', '-')));
                    }
                    content.append("\n");
                }

                if (!methods.isEmpty()) {
                    content.append("### Methods\n\n");
                    for (ElementDocPair method : methods) {
                        content.append(String.format("- [%s %s](#%s)\n",
                            method.getElement().type().getIcon(),
                            method.getElement().name(),
                            method.getElement().name().toLowerCase().replace(' ', '-')));
                    }
                    content.append("\n");
                }
            }
        } else {
            content.append("# Standalone Elements\n\n");
            content.append("These elements are not associated with a specific class.\n\n");
        }

        // Fields section
        if (!fields.isEmpty()) {
            content.append("## Fields\n\n");

            for (ElementDocPair field : fields) {
                CodeElement fieldElem = field.getElement();
                content.append(String.format("### %s %s\n\n", fieldElem.type().getIcon(), fieldElem.name()));
                content.append("#### Documentation\n\n");
                content.append(field.getDocumentation()).append("\n\n");
                content.append("#### Usage Examples\n\n");
                content.append(field.getExamples()).append("\n\n");
                content.append("#### Signature\n\n");
                String fieldLang = getLanguageFromFile(fieldElem.filePath());
                content.append("```").append(fieldLang).append("\n");
                content.append(fieldElem.signature()).append("\n");
                content.append("```\n\n");
            }
        }

        // Methods section
        if (!methods.isEmpty()) {
            content.append("## Methods\n\n");

            for (ElementDocPair method : methods) {
                CodeElement methodElem = method.getElement();
                content.append(String.format("### %s %s\n\n", methodElem.type().getIcon(), methodElem.name()));
                content.append("#### Documentation\n\n");
                content.append(method.getDocumentation()).append("\n\n");
                content.append("#### Usage Examples\n\n");
                content.append(method.getExamples()).append("\n\n");
                content.append("#### Signature\n\n");
                String methodLang = getLanguageFromFile(methodElem.filePath());
                content.append("```").append(methodLang).append("\n");
                content.append(methodElem.signature()).append("\n");
                content.append("```\n\n");
            }
        }

        return content.toString();
    }

    /**
     * Groups code elements by their parent class
     *
     * @param elements List of all code elements
     * @return Map with class name as key and list of related elements as value
     */
    private Map<String, List<CodeElement>> groupElementsByClass(final List<CodeElement> elements) {
        Map<String, List<CodeElement>> elementsByClass = new HashMap<>();

        for (CodeElement element : elements) {
            String classKey;

            if (element.type() == CodeElementType.CLASS) {
                // For class elements, use their own qualified name
                classKey = element.qualifiedName();
            } else {
                // For methods and fields, extract the class name from qualified name
                String qualifiedName = element.qualifiedName();
                int lastDotIndex = qualifiedName.lastIndexOf('.');

                if (lastDotIndex > 0) {
                    // Extract the parent class name
                    classKey = qualifiedName.substring(0, lastDotIndex);
                } else {
                    // Handle standalone elements with no parent class
                    if (element.type() == CodeElementType.FIELD) {
                        classKey = "_FIELDS_";
                    } else {
                        classKey = "_METHODS_";
                    }
                }
            }

            // Add to the map
            elementsByClass.computeIfAbsent(classKey, k -> new ArrayList<>()).add(element);
        }

        return elementsByClass;
    }

    /**
     * Determines programming language from file extension
     *
     * @param filePath the file path
     * @return the language identifier
     */
    private String getLanguageFromFile(final String filePath) {
        if (filePath.endsWith(".java")) {
            return "java";
        }
        if (filePath.endsWith(".py")) {
            return "python";
        }
        return "text";
    }
}
