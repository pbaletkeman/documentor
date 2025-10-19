package com.documentor.service.documentation;

import com.documentor.model.CodeElement;
import com.documentor.service.LlmService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

/**
 * 🔍 Element Documentation Generator
 *
 * Specialized component for generating detailed documentation for individual code elements.
 * Handles element-specific documentation files with LLM-generated content.
 */
@Component
public class ElementDocumentationGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ElementDocumentationGenerator.class);

    private final LlmService llmService;

    public ElementDocumentationGenerator(final LlmService llmServiceParam) {
        this.llmService = llmServiceParam;
    }

    /**
     * 🔍 Generates documentation for a single code element
     */
    public CompletableFuture<Void> generateElementDocumentation(final CodeElement element, final Path outputPath) {
        LOGGER.info("ElementDocumentationGenerator using LlmService: {}",
                   (llmService != null ? "OK" : "NULL"));

        if (llmService == null) {
            LOGGER.error("LlmService is null in ElementDocumentationGenerator");
            return CompletableFuture.completedFuture(null);
        }

        return llmService.generateDocumentation(element)
                .thenCombine(llmService.generateUsageExamples(element), (doc, examples) -> {
                    try {
                        StringBuilder content = new StringBuilder();
                        content.append(String.format("# %s %s\n\n", element.type().getIcon(), element.name()));
                        content.append("## Documentation\n\n");
                        content.append(doc).append("\n\n");
                        content.append("## Usage Examples\n\n");
                        content.append(examples).append("\n\n");
                        content.append("## Code Signature\n\n");
                        content.append("```").append(getLanguageFromFile(element.filePath())).append("\n");
                        content.append(element.signature()).append("\n");
                        content.append("```\n\n");

                        String fileName = String.format("%s-%s.md",
                            element.type().name().toLowerCase(),
                            element.name().replaceAll("[^a-zA-Z0-9]", "_"));

                        Path elementPath = outputPath.resolve("elements").resolve(fileName);
                        Files.createDirectories(elementPath.getParent());
                        Files.write(elementPath, content.toString().getBytes());

                        return null;
                    } catch (IOException e) {
                        LOGGER.error("❌ Error writing element documentation: {}", e.getMessage());
                        throw new RuntimeException("Failed to write element documentation", e);
                    }
                });
    }

    /**
     * 🔍 Determines programming language from file extension
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
