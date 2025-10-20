package com.documentor.service.documentation;

import com.documentor.config.ThreadLocalContextHolder;
import com.documentor.model.CodeElement;
import com.documentor.model.CodeElementType;
import com.documentor.model.ProjectAnalysis;
import com.documentor.service.LlmServiceEnhanced;
import com.documentor.service.LlmServiceFixEnhanced;
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
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Enhanced Element Documentation Generator with Improved Threading
 *
 * Enhanced version of the documentation generator that uses LlmServiceEnhanced
 * directly and includes improved error handling, thread safety, and diagnostics.
 */
@Component
public class ElementDocumentationGeneratorEnhanced {

    private static final Logger LOGGER = LoggerFactory.getLogger(ElementDocumentationGeneratorEnhanced.class);
    private static final int DEFAULT_FUTURE_TIMEOUT_SECONDS = 60;

    private final LlmServiceEnhanced llmService;
    private final LlmServiceFixEnhanced llmServiceFix;

    public ElementDocumentationGeneratorEnhanced(
            final LlmServiceEnhanced llmServiceParam,
            final LlmServiceFixEnhanced llmServiceFixParam) {
        this.llmService = llmServiceParam;
        this.llmServiceFix = llmServiceFixParam;
        LOGGER.info("ElementDocumentationGeneratorEnhanced initialized with services: LlmServiceEnhanced={}, LlmServiceFixEnhanced={}",
                llmServiceParam != null ? "OK" : "NULL",
                llmServiceFixParam != null ? "OK" : "NULL");
    }

    /**
     * Generates documentation for a single code element with enhanced error handling
     */
    public CompletableFuture<Void> generateElementDocumentation(final CodeElement element, final Path outputPath) {
        LOGGER.info("Enhanced generator using LlmServiceEnhanced: {}",
                   (llmService != null ? "OK" : "NULL"));

        if (llmService == null) {
            LOGGER.error("LlmServiceEnhanced is null in ElementDocumentationGeneratorEnhanced");
            return CompletableFuture.completedFuture(null);
        }

        // Ensure ThreadLocal config is set before generating documentation
        if (llmServiceFix != null) {
            try {
                llmServiceFix.isThreadLocalConfigAvailable();
            } catch (Exception e) {
                LOGGER.error("Error checking ThreadLocal availability: {}", e.getMessage(), e);
            }
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
     * Generates documentation for code elements grouped by class with enhanced error handling
     */
    public CompletableFuture<Void> generateGroupedDocumentation(final ProjectAnalysis analysis, final Path outputPath) {
        LOGGER.info("Generating grouped documentation for {} elements with enhanced thread handling",
                   analysis.codeElements().size());

        if (llmService == null) {
            LOGGER.error("LlmServiceEnhanced is null in ElementDocumentationGeneratorEnhanced");
            return CompletableFuture.completedFuture(null);
        }

        // Make sure we have elements to process
        if (analysis.codeElements() == null || analysis.codeElements().isEmpty()) {
            LOGGER.info("No code elements to document");
            return CompletableFuture.completedFuture(null);
        }

        // Ensure the ThreadLocal config is set before we start generating documentation
        if (llmServiceFix != null) {
            LOGGER.info("Setting ThreadLocal config for documentation generation");
            try {
                // Check if config is already available
                boolean configAvailable = llmServiceFix.isThreadLocalConfigAvailable();
                if (!configAvailable) {
                    LOGGER.warn("ThreadLocal config not available - documentation generation may fail");
                }
            } catch (Exception e) {
                LOGGER.error("Error checking ThreadLocal config: {}", e.getMessage(), e);
            }
        }

        // Group elements by their parent class
        Map<String, List<CodeElement>> elementsByClass = groupElementsByClass(analysis.codeElements());
        LOGGER.info("Grouped {} elements into {} classes",
                   analysis.codeElements().size(), elementsByClass.size());

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

            // Generate documentation for this class group with timeout handling
            CompletableFuture<Void> classFuture = generateClassDocumentation(classElement, classElements, outputPath)
                .orTimeout(DEFAULT_FUTURE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .exceptionally(ex -> {
                    if (ex instanceof TimeoutException) {
                        LOGGER.error("Timeout while generating documentation for class: {}",
                                    classElement != null ? classElement.name() : className);
                    } else {
                        LOGGER.error("Error generating documentation for class: {}, error: {}",
                                    classElement != null ? classElement.name() : className,
                                    ex.getMessage(), ex);
                    }
                    // Continue processing other classes even if this one fails
                    return null;
                });

            allFutures.add(classFuture);
        }

        // Wait for all class documentation to complete
        return CompletableFuture.allOf(allFutures.toArray(new CompletableFuture[0]))
            .exceptionally(ex -> {
                LOGGER.error("Error in grouped documentation: {}", ex.getMessage(), ex);
                return null;
            })
            .whenComplete((result, ex) -> {
                // Cleanup ThreadLocal to prevent memory leaks
                if (llmServiceFix != null) {
                    LOGGER.debug("Cleaning up ThreadLocal config after documentation generation");
                    llmServiceFix.cleanupThreadLocalConfig();
                }
            });
    }

    /**
     * Generates documentation for a class and all its related elements
     */
    private CompletableFuture<Void> generateClassDocumentation(final CodeElement classElement,
                                                             final List<CodeElement> classElements,
                                                             final Path outputPath) {
        LOGGER.info("Generating documentation for class: {}",
                   classElement != null ? classElement.name() : "Standalone elements");

        // Ensure ThreadLocal config is available for this specific task
        if (llmServiceFix != null) {
            try {
                boolean configAvailable = llmServiceFix.isThreadLocalConfigAvailable();
                if (!configAvailable) {
                    LOGGER.warn("ThreadLocal config not available for class: {} - refreshing",
                               classElement != null ? classElement.name() : "Standalone elements");
                    // Additional diagnostics could be added here
                }
            } catch (Exception e) {
                LOGGER.error("Error checking ThreadLocal availability for class: {}",
                            classElement != null ? classElement.name() : "Standalone elements", e);
            }
        }

        // Split elements by type
        List<CodeElement> fields = classElements.stream()
            .filter(e -> e.type() == CodeElementType.FIELD)
            .toList();

        List<CodeElement> methods = classElements.stream()
            .filter(e -> e.type() == CodeElementType.METHOD)
            .toList();

        // Generate documentation for class if it exists with error handling
        CompletableFuture<String> classFuture = classElement != null
            ? llmService.generateDocumentation(classElement)
                .exceptionally(ex -> {
                    LOGGER.error("Error generating class documentation for {}: {}",
                                classElement.name(), ex.getMessage());
                    return "Error generating documentation: " + ex.getMessage();
                })
            : CompletableFuture.completedFuture("");

        // Generate examples for class if it exists with error handling
        CompletableFuture<String> classExamplesFuture = classElement != null
            ? llmService.generateUsageExamples(classElement)
                .exceptionally(ex -> {
                    LOGGER.error("Error generating class examples for {}: {}",
                                classElement.name(), ex.getMessage());
                    return "Error generating examples: " + ex.getMessage();
                })
            : CompletableFuture.completedFuture("");

        // Create lists to hold futures for fields and methods
        List<CompletableFuture<ElementDocPair>> fieldFutures = new ArrayList<>();
        List<CompletableFuture<ElementDocPair>> methodFutures = new ArrayList<>();

        // Generate documentation for each field with proper error handling
        for (CodeElement field : fields) {
            fieldFutures.add(generateElementDocPair(field));
        }

        // Generate documentation for each method with proper error handling
        for (CodeElement method : methods) {
            methodFutures.add(generateElementDocPair(method));
        }

        // Wait for all field documentation to complete
        CompletableFuture<List<ElementDocPair>> allFieldsFuture =
            CompletableFuture.allOf(fieldFutures.toArray(new CompletableFuture[0]))
                .thenApply(v -> fieldFutures.stream()
                    .map(future -> {
                        try {
                            return future.join();
                        } catch (CompletionException e) {
                            LOGGER.error("Error joining field future: {}", e.getMessage(), e);
                            return new ElementDocPair(
                                null,
                                "Error generating documentation",
                                "Error generating examples"
                            );
                        }
                    })
                    .filter(pair -> pair.getElement() != null)
                    .toList());

        // Wait for all method documentation to complete
        CompletableFuture<List<ElementDocPair>> allMethodsFuture =
            CompletableFuture.allOf(methodFutures.toArray(new CompletableFuture[0]))
                .thenApply(v -> methodFutures.stream()
                    .map(future -> {
                        try {
                            return future.join();
                        } catch (CompletionException e) {
                            LOGGER.error("Error joining method future: {}", e.getMessage(), e);
                            return new ElementDocPair(
                                null,
                                "Error generating documentation",
                                "Error generating examples"
                            );
                        }
                    })
                    .filter(pair -> pair.getElement() != null)
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
                        String packageName = classElements.isEmpty() ? "unknown" :
                            classElements.get(0).qualifiedName().split("\\.")[0];
                        fileName = String.format("standalone-%s.md", packageName);
                    }

                    // Write to file
                    Path elementPath = outputPath.resolve("elements").resolve(fileName);
                    Files.createDirectories(elementPath.getParent());
                    Files.write(elementPath, content.getBytes());

                    LOGGER.info("✅ Successfully wrote documentation for: {}",
                              classElement != null ? classElement.name() : "Standalone elements");
                    return null;
                } catch (IOException e) {
                    LOGGER.error("❌ Error writing class documentation: {}", e.getMessage(), e);
                    throw new CompletionException("Failed to write class documentation", e);
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

        ElementDocPair(final CodeElement codeElement, final String docContent, final String exampleContent) {
            this.element = codeElement;
            this.documentation = docContent;
            this.examples = exampleContent;
        }

        public CodeElement getElement() {
            return element;
        }

        public String getDocumentation() {
            return documentation;
        }

        public String getExamples() {
            return examples;
        }
    }

    /**
     * Generates documentation and examples for a single element with enhanced error handling
     */
    private CompletableFuture<ElementDocPair> generateElementDocPair(final CodeElement codeElement) {
        // Additional ThreadLocal verification
        try {
            // Direct check from ThreadLocalContextHolder for diagnostic purposes
            ThreadLocalContextHolder.logConfigStatus();

            if (llmServiceFix != null) {
                boolean configAvailable = llmServiceFix.isThreadLocalConfigAvailable();
                if (!configAvailable) {
                    LOGGER.warn("ThreadLocal config not available for element: {} - refreshing",
                               codeElement.name());
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error checking ThreadLocal availability for element: {}",
                        codeElement.name(), e);
        }

        // Generate both documentation and examples with proper error handling
        CompletableFuture<String> docFuture = llmService.generateDocumentation(codeElement)
            .exceptionally(ex -> {
                LOGGER.error("Error generating documentation for {}: {}",
                            codeElement.name(), ex.getMessage());
                return "Error generating documentation: " + ex.getMessage();
            });

        CompletableFuture<String> examplesFuture = llmService.generateUsageExamples(codeElement)
            .exceptionally(ex -> {
                LOGGER.error("Error generating examples for {}: {}",
                            codeElement.name(), ex.getMessage());
                return "Error generating examples: " + ex.getMessage();
            });

        // Combine the futures with timeout handling
        return docFuture.thenCombine(examplesFuture,
                (docContent, exampleContent) -> new ElementDocPair(codeElement, docContent, exampleContent))
            .orTimeout(DEFAULT_FUTURE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .exceptionally(ex -> {
                if (ex instanceof TimeoutException) {
                    LOGGER.error("Timeout while generating documentation for element: {}",
                                codeElement.name());
                } else {
                    LOGGER.error("Error generating doc pair for {}: {}",
                                codeElement.name(), ex.getMessage(), ex);
                }
                // Return a placeholder to avoid breaking the whole process
                return new ElementDocPair(
                    codeElement,
                    "Timeout or error generating documentation",
                    "Timeout or error generating examples"
                );
            });
    }

    /**
     * Builds the complete documentation content for a class and its elements
     *
     * Improved version with better formatting and readability:
     * - Adds visual separation between sections
     * - Formats code blocks with proper indentation
     * - Adds collapsible sections for large method signatures
     * - Improves the table of contents with better organization
     * - Adds descriptive headers and visual elements
     */
    private String buildClassDocumentContent(final CodeElement classElement,
                                           final String classDoc,
                                           final String classExamples,
                                           final List<ElementDocPair> fields,
                                           final List<ElementDocPair> methods) {
        StringBuilder content = new StringBuilder();

        // Class header and documentation
        if (classElement != null) {
            // Add class name with larger header and package info
            content.append(String.format("# %s %s\n\n", classElement.type().getIcon(), classElement.name()));

            // Safely extract package name and format it nicely
            String qualifiedName = classElement.qualifiedName();
            int lastDotIndex = qualifiedName.lastIndexOf('.');
            String packageName = lastDotIndex > 0 ? qualifiedName.substring(0, lastDotIndex) : "(default package)";
            content.append(String.format("> **Package:** `%s`\n\n", packageName));

            // Add horizontal rule for visual separation
            content.append("---\n\n");

            // Add class documentation in a highlighted section
            content.append("## 📄 Class Documentation\n\n");
            // Format the documentation content nicely
            String formattedDoc = formatContent(classDoc);
            content.append(formattedDoc).append("\n\n");

            // Add horizontal rule for visual separation
            content.append("---\n\n");

            // Add class usage examples in a highlighted section
            content.append("## 💡 Class Usage Examples\n\n");
            // Format the examples content nicely
            String formattedExamples = formatContent(classExamples);
            content.append(formattedExamples).append("\n\n");

            // Add horizontal rule for visual separation
            content.append("---\n\n");

            // Add class signature with proper formatting
            content.append("## 📋 Class Signature\n\n");
            // Format the code block properly with line breaks and indentation
            String language = getLanguageFromFile(classElement.filePath());
            content.append("```").append(language).append("\n");
            content.append(formatCodeBlock(classElement.signature())).append("\n");
            content.append("```\n\n");

            // Add table of contents with better organization
            if (!fields.isEmpty() || !methods.isEmpty()) {
                content.append("## 📑 Table of Contents\n\n");

                if (!fields.isEmpty()) {
                    content.append("<details open>\n<summary><strong>🔹 Fields</strong> (" + fields.size() + ")</summary>\n\n");
                    int fieldCount = 0;
                    for (ElementDocPair field : fields) {
                        content.append(String.format("- [%s %s](#%s)\n",
                            field.getElement().type().getIcon(),
                            field.getElement().name(),
                            sanitizeAnchor(field.getElement().name())));
                        fieldCount++;

                        // Add line breaks for better readability in long lists
                        if (fieldCount % 10 == 0 && fieldCount < fields.size()) {
                            content.append("\n");
                        }
                    }
                    content.append("\n</details>\n\n");
                }

                if (!methods.isEmpty()) {
                    content.append("<details open>\n<summary><strong>🔸 Methods</strong> (" + methods.size() + ")</summary>\n\n");
                    int methodCount = 0;
                    for (ElementDocPair method : methods) {
                        content.append(String.format("- [%s %s](#%s)\n",
                            method.getElement().type().getIcon(),
                            method.getElement().name(),
                            sanitizeAnchor(method.getElement().name())));
                        methodCount++;

                        // Add line breaks for better readability in long lists
                        if (methodCount % 10 == 0 && methodCount < methods.size()) {
                            content.append("\n");
                        }
                    }
                    content.append("\n</details>\n\n");
                }

                // Add horizontal rule for visual separation
                content.append("---\n\n");
            }
        } else {
            content.append("# 📁 Standalone Elements\n\n");
            content.append("These elements are not associated with a specific class.\n\n");
            // Add horizontal rule for visual separation
            content.append("---\n\n");
        }

        // Fields section with improved formatting
        if (!fields.isEmpty()) {
            content.append("## 🔹 Fields\n\n");

            for (ElementDocPair field : fields) {
                CodeElement fieldElem = field.getElement();
                // Add a box around each field for visual separation
                content.append("<div class=\"element-box\">\n\n");
                content.append(String.format("### %s %s\n\n", fieldElem.type().getIcon(), fieldElem.name()));

                // Documentation section with better formatting
                content.append("#### 📄 Documentation\n\n");
                String formattedFieldDoc = formatContent(field.getDocumentation());
                content.append(formattedFieldDoc).append("\n\n");

                // Usage examples section with better formatting
                content.append("#### 💡 Usage Examples\n\n");
                String formattedFieldExamples = formatContent(field.getExamples());
                content.append(formattedFieldExamples).append("\n\n");

                // Signature with better code formatting
                content.append("#### 📋 Signature\n\n");
                String fieldLang = getLanguageFromFile(fieldElem.filePath());
                content.append("```").append(fieldLang).append("\n");
                content.append(formatCodeBlock(fieldElem.signature())).append("\n");
                content.append("```\n\n");

                // Close the box
                content.append("</div>\n\n");

                // Add horizontal rule for visual separation between fields
                content.append("---\n\n");
            }
        }

        // Methods section with improved formatting
        if (!methods.isEmpty()) {
            content.append("## 🔸 Methods\n\n");

            for (ElementDocPair method : methods) {
                CodeElement methodElem = method.getElement();
                // Add a box around each method for visual separation
                content.append("<div class=\"element-box\">\n\n");
                content.append(String.format("### %s %s\n\n", methodElem.type().getIcon(), methodElem.name()));

                // Documentation section with better formatting
                content.append("#### 📄 Documentation\n\n");
                String formattedMethodDoc = formatContent(method.getDocumentation());
                content.append(formattedMethodDoc).append("\n\n");

                // Usage examples section with better formatting
                content.append("#### 💡 Usage Examples\n\n");
                String formattedMethodExamples = formatContent(method.getExamples());
                content.append(formattedMethodExamples).append("\n\n");

                // Signature with better code formatting and collapsible section for long signatures
                String methodSignature = methodElem.signature();
                if (methodSignature.length() > 200) {
                    content.append("#### 📋 Signature\n\n");
                    content.append("<details>\n<summary>View Method Signature</summary>\n\n");
                    content.append("```").append(getLanguageFromFile(methodElem.filePath())).append("\n");
                    content.append(formatCodeBlock(methodSignature)).append("\n");
                    content.append("```\n\n</details>\n\n");
                } else {
                    content.append("#### 📋 Signature\n\n");
                    content.append("```").append(getLanguageFromFile(methodElem.filePath())).append("\n");
                    content.append(formatCodeBlock(methodSignature)).append("\n");
                    content.append("```\n\n");
                }

                // Close the box
                content.append("</div>\n\n");

                // Add horizontal rule for visual separation between methods
                content.append("---\n\n");
            }
        }

        return content.toString();
    }

    /**
     * Formats content text for better readability
     *
     * @param content The raw content text
     * @return Formatted content
     */
    private String formatContent(final String content) {
        if (content == null || content.isEmpty()) {
            return "_No content available_";
        }

        // If content contains error message, return it as is
        if (content.startsWith("Error:") || content.startsWith("Timeout")) {
            return content;
        }

        // Return the content as is, already formatted by the LLM
        return content;
    }

    /**
     * Formats code blocks for better readability
     *
     * @param code The raw code block
     * @return Formatted code block
     */
    private String formatCodeBlock(final String code) {
        if (code == null || code.isEmpty()) {
            return "";
        }

        // Format Java code blocks properly
        // This ensures proper indentation and line breaks
        StringBuilder formattedCode = new StringBuilder();
        String[] lines = code.split("\\n");

        // If it's a one-liner but has semicolons, it might be compressed Java code
        if (lines.length == 1 && code.contains(";") && code.length() > 100) {
            // Try to format it with proper line breaks
            // Replace semicolons with semicolon + newline, except in string literals
            boolean inString = false;
            StringBuilder reformatted = new StringBuilder();

            for (int i = 0; i < code.length(); i++) {
                char c = code.charAt(i);
                reformatted.append(c);

                // Toggle string mode
                if (c == '"' && (i == 0 || code.charAt(i-1) != '\\')) {
                    inString = !inString;
                }

                // Add newlines after semicolons and open braces when not in a string
                if (!inString && (c == '{' || c == ';')) {
                    reformatted.append("\n");

                    // Add indentation after open brace
                    if (c == '{') {
                        reformatted.append("  ");
                    }
                }
            }

            return reformatted.toString();
        }

        // Otherwise, return code as is
        return code;
    }

    /**
     * Sanitizes a string to be used as a Markdown anchor
     *
     * @param text The text to sanitize
     * @return Sanitized anchor text
     */
    private String sanitizeAnchor(final String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        // Convert to lowercase
        String result = text.toLowerCase();

        // Replace spaces and special characters
        result = result.replace(' ', '-')
            .replaceAll("[^a-z0-9\\-_]", "");

        return result;
    }

    /**
     * Groups code elements by their parent class
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
