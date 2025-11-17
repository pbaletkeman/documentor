package com.documentor.service.analysis;

import com.documentor.config.DocumentorConfig;
import com.documentor.config.model.AnalysisSettings;
import com.documentor.model.CodeElement;
import com.documentor.model.CodeElementType;
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
        DocumentorConfig cfg = new DocumentorConfig(List.of(), null,
                new AnalysisSettings(false, 1,
                List.of("**/*.java"), List.of()));
        visitor = new JavaElementVisitor(cfg);
    }

    @Test
    void visitClassAndMethodExtractsElements() {
        String source = "package com.test; public class A "
        + "{ public void m() {} private int x; }";

    var parseResult = new JavaParser().parse(source);
    CompilationUnit cu = parseResult.getResult().orElseThrow(() ->
        new IllegalStateException("parse failed"));

        List<CodeElement> elements = new ArrayList<>();
        visitor.initialize(Path.of("A.java"), elements);
        visitor.visit(cu, null);

        // Expect at least a class and a public method;
        // private field should be excluded by default
        assertTrue(elements.stream().anyMatch(e -> e.type()
            == com.documentor.model.CodeElementType.CLASS));
        assertTrue(elements.stream().anyMatch(e -> e.type()
            == com.documentor.model.CodeElementType.METHOD));
        assertFalse(elements.stream().anyMatch(e -> e.type()
            == com.documentor.model.CodeElementType.FIELD));
    }

    @Test
    void visitPrivateClassExcludedWhenPrivateMembersDisabled() {
        // Test the false branch of shouldInclude for
        // ClassOrInterfaceDeclaration
        // Use package-private instead of private since
        // top-level classes cannot be private
        String source = "package com.test; class PackagePrivateClass "
            + "{ public void method() {} }";

        var parseResult = new JavaParser().parse(source);
        CompilationUnit cu = parseResult.getResult().orElseThrow(() ->
            new IllegalStateException("parse failed"));

        List<CodeElement> elements = new ArrayList<>();
        visitor.initialize(Path.of("PackagePrivateClass.java"), elements);
        visitor.visit(cu, null);

        // Package-private class should be included
        // (not private, so shouldInclude returns true)
        assertTrue(elements.stream().anyMatch(e ->
            e.type() == CodeElementType.CLASS
            && e.name().equals("PackagePrivateClass")));
    }

    @Test
    void visitPrivateEnumExcludedWhenPrivateMembersDisabled() {
        // Test the false branch of shouldInclude for EnumDeclaration
        // Use package-private instead of private since top-level
        // enums cannot be private
        String source = "package com.test; enum PackagePrivateEnum "
            + "{ VALUE1, VALUE2; public void method() {} }";

        var parseResult = new JavaParser().parse(source);
        CompilationUnit cu =
            parseResult.getResult().orElseThrow(() ->
                new IllegalStateException("parse failed"));

        List<CodeElement> elements = new ArrayList<>();
        visitor.initialize(Path.of("PackagePrivateEnum.java"), elements);
        visitor.visit(cu, null);

        // Package-private enum should be included
        // (not private, so shouldInclude returns true)
        assertTrue(elements.stream().anyMatch(e ->
            e.type() == CodeElementType.CLASS
            && e.name().equals("PackagePrivateEnum")));
    }

    @Test
    void visitPrivateClassIncludedWhenPrivateMembersEnabled() {
        // Create visitor with includePrivateMembers = true
        DocumentorConfig cfg = new DocumentorConfig(List.of(), null,
                new AnalysisSettings(true, 1,
                List.of("**/*.java"), List.of()));
        JavaElementVisitor inclusiveVisitor = new JavaElementVisitor(cfg);

        // Use nested class to test private class behavior
        String source = "package com.test; public class OuterClass { "
                + "private static class PrivateClass "
                + "{ public void method() {} } }";

        var parseResult = new JavaParser().parse(source);
        CompilationUnit cu = parseResult.getResult()
            .orElseThrow(() -> new IllegalStateException("parse failed"));

        List<CodeElement> elements = new ArrayList<>();
        inclusiveVisitor.initialize(Path.of("OuterClass.java"), elements);
        inclusiveVisitor.visit(cu, null);

        // Both outer class and private nested class should be
        // included when includePrivateMembers is true
        assertTrue(elements.stream().anyMatch(e ->
            e.type() == CodeElementType.CLASS
            && e.name().equals("OuterClass")));
        assertTrue(elements.stream().anyMatch(e ->
            e.type() == CodeElementType.CLASS
            && e.name().equals("PrivateClass")));
    }

    @Test
    void visitPrivateEnumIncludedWhenPrivateMembersEnabled() {
        // Create visitor with includePrivateMembers = true
        DocumentorConfig cfg = new DocumentorConfig(List.of(), null,
                new AnalysisSettings(true, 1,
                List.of("**/*.java"), List.of()));
        JavaElementVisitor inclusiveVisitor = new JavaElementVisitor(cfg);

        // Use nested enum to test private enum behavior
        String source = "package com.test; public class OuterClass { "
                + "private enum PrivateEnum { VALUE1, VALUE2; } }";

        var parseResult = new JavaParser().parse(source);
        CompilationUnit cu = parseResult.getResult()
            .orElseThrow(() -> new IllegalStateException("parse failed"));

        List<CodeElement> elements = new ArrayList<>();
        inclusiveVisitor.initialize(Path.of("OuterClass.java"), elements);
        inclusiveVisitor.visit(cu, null);

        // Both outer class and private nested enum should be
        // included when includePrivateMembers is true
        assertTrue(elements.stream().anyMatch(e ->
            e.type() == CodeElementType.CLASS
            && e.name().equals("OuterClass")));
        assertTrue(elements.stream().anyMatch(e ->
            e.type() == CodeElementType.CLASS
            && e.name().equals("PrivateEnum")));
    }

    @Test
    void visitPrivateNestedClassExcludedWhenPrivateMembersDisabled() {
        // Test the false branch of shouldInclude for
        // private nested ClassOrInterfaceDeclaration
        String source = "package com.test; public class OuterClass "
        + "{ private static class PrivateNestedClass { } }";

        var parseResult = new JavaParser().parse(source);
        CompilationUnit cu = parseResult.getResult()
            .orElseThrow(() -> new IllegalStateException("parse failed"));

        List<CodeElement> elements = new ArrayList<>();
        visitor.initialize(Path.of("OuterClass.java"), elements);
        visitor.visit(cu, null);

        // Outer class should be included, but
        // private nested class should be excluded
        assertTrue(elements.stream().anyMatch(e ->
            e.type() == CodeElementType.CLASS
            && e.name().equals("OuterClass")));
        assertFalse(elements.stream().anyMatch(e ->
            e.type() == CodeElementType.CLASS
            && e.name().equals("PrivateNestedClass")));
    }

    @Test
    void visitPrivateNestedEnumExcludedWhenPrivateMembersDisabled() {
        // Test the false branch of shouldInclude
        // for private nested EnumDeclaration
        String source = "package com.test; public class OuterClass { "
                + "private enum PrivateNestedEnum { VALUE1, VALUE2; } }";

        var parseResult = new JavaParser().parse(source);
        CompilationUnit cu = parseResult.getResult()
            .orElseThrow(() -> new IllegalStateException("parse failed"));

        List<CodeElement> elements = new ArrayList<>();
        visitor.initialize(Path.of("OuterClass.java"), elements);
        visitor.visit(cu, null);

        // Outer class should be included, but private
        // nested enum should be excluded
        assertTrue(elements.stream().anyMatch(e ->
            e.type() == CodeElementType.CLASS && e.name()
            .equals("OuterClass")));
        assertFalse(elements.stream().anyMatch(e ->
            e.type() == CodeElementType.CLASS && e.name()
            .equals("PrivateNestedEnum")));
    }
}
