package com.documentor.service.analysis;

import com.documentor.config.DocumentorConfig;
import com.documentor.config.model.AnalysisSettings;
import com.documentor.model.CodeElement;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JavaElementVisitorTest {

    private JavaElementVisitor visitor;

    @BeforeEach
    void setUp() {
        DocumentorConfig cfg = new DocumentorConfig(List.of(), null, new AnalysisSettings(false, 1, List.of("**/*.java"), List.of()));
        visitor = new JavaElementVisitor(cfg);
    }

    @Test
    void visitClassAndMethodExtractsElements() {
        String source = "package com.test; public class A { public void m() {} private int x; }";

    var parseResult = new JavaParser().parse(source);
    CompilationUnit cu = parseResult.getResult().orElseThrow(() -> new IllegalStateException("parse failed"));

        List<CodeElement> elements = new ArrayList<>();
        visitor.initialize(Path.of("A.java"), elements);
        visitor.visit(cu, null);

        // Expect at least a class and a public method; private field should be excluded by default
        assertTrue(elements.stream().anyMatch(e -> e.type() == com.documentor.model.CodeElementType.CLASS));
        assertTrue(elements.stream().anyMatch(e -> e.type() == com.documentor.model.CodeElementType.METHOD));
        assertFalse(elements.stream().anyMatch(e -> e.type() == com.documentor.model.CodeElementType.FIELD));
    }
}
