package com.documentor.service.analysis;

import com.documentor.config.DocumentorConfig;
import com.documentor.config.model.AnalysisSettings;
import com.documentor.model.CodeElement;
import com.documentor.model.CodeElementType;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Enhanced test for JavaElementVisitor
 */
class JavaElementVisitorEnhancedTest {

    private JavaElementVisitor visitor;
    private List<CodeElement> elements;

    @BeforeEach
    void setUp() {
        // Initialize with standard config
        DocumentorConfig cfg = new DocumentorConfig(
            List.of(), 
            null, 
            new AnalysisSettings(false, 1, List.of("**/*.java"), List.of())
        );
        visitor = new JavaElementVisitor(cfg);
        elements = new ArrayList<>();
        visitor.initialize(Path.of("Test.java"), elements);
    }

    @Test
    @DisplayName("Should extract public classes")
    void shouldExtractPublicClasses() {
        // Given
        String source = """
            package com.test;
            
            /**
             * Test class documentation
             */
            public class TestClass {
                private String field;
                
                public void method() {}
            }
            """;
        
        // When
        CompilationUnit cu = parseSource(source);
        visitor.visit(cu, null);
        
        // Then
        CodeElement classElement = findElementByType(CodeElementType.CLASS);
        assertNotNull(classElement, "Class element should be found");
        assertEquals("TestClass", classElement.name());
        assertEquals("com.test.TestClass", classElement.qualifiedName());
        assertTrue(classElement.documentation().contains("Test class documentation"));
    }
    
    @Test
    @DisplayName("Should extract public methods")
    void shouldExtractPublicMethods() {
        // Given
        String source = """
            package com.test;
            
            public class TestClass {
                /**
                 * Public method with documentation
                 * @param param1 First parameter
                 * @param param2 Second parameter
                 * @return A test result
                 */
                public String testMethod(String param1, int param2) {
                    return param1 + param2;
                }
                
                private void privateMethod() {
                    // This should not be extracted by default
                }
            }
            """;
        
        // When
        CompilationUnit cu = parseSource(source);
        visitor.visit(cu, null);
        
        // Then
        CodeElement methodElement = findElementByType(CodeElementType.METHOD);
        assertNotNull(methodElement, "Method element should be found");
        assertEquals("testMethod", methodElement.name());
        assertTrue(methodElement.documentation().contains("Public method with documentation"));
        assertEquals(2, methodElement.parameters().size());
        assertEquals("String param1", methodElement.parameters().get(0));
        assertEquals("int param2", methodElement.parameters().get(1));
    }
    
    @Test
    @DisplayName("Should extract annotations")
    void shouldExtractAnnotations() {
        // Given
        String source = """
            package com.test;
            
            import org.springframework.stereotype.Service;
            import javax.validation.constraints.NotNull;
            
            @Service
            public class AnnotatedClass {
                
                @NotNull
                public void annotatedMethod() {}
            }
            """;
        
        // When
        CompilationUnit cu = parseSource(source);
        visitor.visit(cu, null);
        
        // Then
        CodeElement classElement = findElementByType(CodeElementType.CLASS);
        assertNotNull(classElement, "Class element should be found");
        assertTrue(classElement.annotations().contains("@Service"));
        
        CodeElement methodElement = findElementByType(CodeElementType.METHOD);
        assertNotNull(methodElement, "Method element should be found");
        assertTrue(methodElement.annotations().contains("@NotNull"));
    }
    
    @Test
    @DisplayName("Should extract enums")
    void shouldExtractEnums() {
        // Given
        String source = """
            package com.test;
            
            /**
             * Test enum
             */
            public enum TestEnum {
                VALUE1, VALUE2, VALUE3
            }
            """;
        
        // When
        CompilationUnit cu = parseSource(source);
        visitor.visit(cu, null);
        
        // Then
        CodeElement enumElement = findElementByType(CodeElementType.CLASS);
        assertNotNull(enumElement, "Enum element should be found");
        assertEquals("TestEnum", enumElement.name());
        assertTrue(enumElement.documentation().contains("Test enum"));
        assertTrue(enumElement.signature().contains("enum"));
    }
    
    @Test
    @DisplayName("Should extract fields when configured")
    void shouldExtractFieldsWhenConfigured() {
        // Given - Configure to include private fields
        DocumentorConfig cfg = new DocumentorConfig(
            List.of(), 
            null, 
            new AnalysisSettings(true, 1, List.of("**/*.java"), List.of())
        );
        JavaElementVisitor privateVisitor = new JavaElementVisitor(cfg);
        List<CodeElement> privateElements = new ArrayList<>();
        privateVisitor.initialize(Path.of("Test.java"), privateElements);
        
        String source = """
            package com.test;
            
            public class TestClass {
                /**
                 * A private field with docs
                 */
                private String privateField;
                
                public int publicField;
            }
            """;
        
        // When
        CompilationUnit cu = parseSource(source);
        privateVisitor.visit(cu, null);
        
        // Then
        assertTrue(privateElements.stream().anyMatch(e -> 
            e.type() == CodeElementType.FIELD && e.name().equals("privateField")));
        assertTrue(privateElements.stream().anyMatch(e ->
            e.type() == CodeElementType.FIELD && e.name().equals("publicField")));
        
        // Check documentation was captured
        CodeElement fieldElement = privateElements.stream()
            .filter(e -> e.name().equals("privateField"))
            .findFirst()
            .orElse(null);
        assertNotNull(fieldElement);
        assertTrue(fieldElement.documentation().contains("A private field with docs"));
    }

    /**
     * Helper to parse Java source code
     */
    private CompilationUnit parseSource(String source) {
        var parseResult = new JavaParser().parse(source);
        return parseResult.getResult().orElseThrow(() -> 
            new IllegalStateException("Failed to parse Java source code"));
    }
    
    /**
     * Helper to find an element by type
     */
    private CodeElement findElementByType(CodeElementType type) {
        return elements.stream()
            .filter(e -> e.type() == type)
            .findFirst()
            .orElse(null);
    }
}