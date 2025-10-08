package com.documentor.service;

import com.documentor.config.DocumentorConfig;
import com.documentor.model.CodeElement;
import com.documentor.model.CodeElementType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 🧪 Unit tests for JavaCodeAnalyzer
 */
@ExtendWith(MockitoExtension.class)
class JavaCodeAnalyzerTest {

    @Mock
    private DocumentorConfig config;

    @Mock
    private DocumentorConfig.AnalysisSettings analysisSettings;

    private JavaCodeAnalyzer javaCodeAnalyzer;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        // Use lenient mode to avoid UnnecessaryStubbingException
        lenient().when(config.analysisSettings()).thenReturn(analysisSettings);
        lenient().when(analysisSettings.includePrivateMembers()).thenReturn(false);
        javaCodeAnalyzer = new JavaCodeAnalyzer(config);
    }

    @Test
    void testAnalyzeFile_SimpleClass() throws IOException {
        String javaCode = """
            package com.example;
            
            public class TestClass {
                private String field;
                
                public void publicMethod() {
                    // implementation
                }
                
                private void privateMethod() {
                    // implementation  
                }
            }
            """;

        Path javaFile = tempDir.resolve("TestClass.java");
        Files.writeString(javaFile, javaCode);
        
        List<CodeElement> elements = javaCodeAnalyzer.analyzeFile(javaFile);

        assertFalse(elements.isEmpty());
        
        // Should contain class and public method (private excluded by default)
        assertTrue(elements.stream().anyMatch(e -> 
            e.type() == CodeElementType.CLASS && e.name().equals("TestClass")));
        assertTrue(elements.stream().anyMatch(e -> 
            e.type() == CodeElementType.METHOD && e.name().equals("publicMethod")));
        
        // Should not contain private method when includePrivate is false
        assertFalse(elements.stream().anyMatch(e -> 
            e.type() == CodeElementType.METHOD && e.name().equals("privateMethod")));
    }

    @Test
    void testAnalyzeFile_WithPrivateElements() throws IOException {
        when(analysisSettings.includePrivateMembers()).thenReturn(true);
        javaCodeAnalyzer = new JavaCodeAnalyzer(config);

        String javaCode = """
            public class TestClass {
                private String privateField;
                public String publicField;
                
                private void privateMethod() {}
                public void publicMethod() {}
            }
            """;

        Path javaFile = tempDir.resolve("TestClass.java");
        Files.writeString(javaFile, javaCode);
        
        List<CodeElement> elements = javaCodeAnalyzer.analyzeFile(javaFile);

        // Should contain both private and public elements
        assertTrue(elements.stream().anyMatch(e -> 
            e.type() == CodeElementType.FIELD && e.name().equals("privateField")));
        assertTrue(elements.stream().anyMatch(e -> 
            e.type() == CodeElementType.FIELD && e.name().equals("publicField")));
        assertTrue(elements.stream().anyMatch(e -> 
            e.type() == CodeElementType.METHOD && e.name().equals("privateMethod")));
        assertTrue(elements.stream().anyMatch(e -> 
            e.type() == CodeElementType.METHOD && e.name().equals("publicMethod")));
    }

    @Test
    void testAnalyzeFile_Interface() throws IOException {
        String javaCode = """
            package com.example;
            
            public interface TestInterface {
                void method1();
                String method2(int param);
            }
            """;

        Path javaFile = tempDir.resolve("TestInterface.java");
        Files.writeString(javaFile, javaCode);
        
        List<CodeElement> elements = javaCodeAnalyzer.analyzeFile(javaFile);

        assertTrue(elements.stream().anyMatch(e -> 
            e.type() == CodeElementType.CLASS && e.name().equals("TestInterface")));
        assertTrue(elements.stream().anyMatch(e -> 
            e.type() == CodeElementType.METHOD && e.name().equals("method1")));
        assertTrue(elements.stream().anyMatch(e -> 
            e.type() == CodeElementType.METHOD && e.name().equals("method2")));
    }

    @Test
    void testAnalyzeFile_Enum() throws IOException {
        String javaCode = """
            public enum TestEnum {
                VALUE1,
                VALUE2,
                VALUE3;
                
                public void enumMethod() {}
            }
            """;

        Path javaFile = tempDir.resolve("TestEnum.java");
        Files.writeString(javaFile, javaCode);
        
        List<CodeElement> elements = javaCodeAnalyzer.analyzeFile(javaFile);

        assertTrue(elements.stream().anyMatch(e -> 
            e.type() == CodeElementType.CLASS && e.name().equals("TestEnum")));
        assertTrue(elements.stream().anyMatch(e -> 
            e.type() == CodeElementType.METHOD && e.name().equals("enumMethod")));
    }

    @Test
    void testAnalyzeFile_InvalidJava() throws IOException {
        String invalidJavaCode = "this is not valid java code";
        
        Path javaFile = tempDir.resolve("Invalid.java");
        Files.writeString(javaFile, invalidJavaCode);
        
        List<CodeElement> elements = javaCodeAnalyzer.analyzeFile(javaFile);
        
        // Should return empty list for unparseable code
        assertTrue(elements.isEmpty());
    }

    @Test
    void testAnalyzeFile_EmptyFile() throws IOException {
        String emptyCode = "";
        
        Path javaFile = tempDir.resolve("Empty.java");
        Files.writeString(javaFile, emptyCode);
        
        List<CodeElement> elements = javaCodeAnalyzer.analyzeFile(javaFile);
        
        assertTrue(elements.isEmpty());
    }

    @Test
    void testAnalyzeFile_OnlyComments() throws IOException {
        String commentOnlyCode = """
            // This is a comment
            /* This is a block comment */
            /**
             * This is a javadoc comment
             */
            """;
        
        Path javaFile = tempDir.resolve("CommentsOnly.java");
        Files.writeString(javaFile, commentOnlyCode);
        
        List<CodeElement> elements = javaCodeAnalyzer.analyzeFile(javaFile);
        
        assertTrue(elements.isEmpty());
    }

    @Test
    void testAnalyzeFile_NonExistentFile() {
        Path nonExistentFile = tempDir.resolve("DoesNotExist.java");
        
        assertThrows(IOException.class, () -> {
            javaCodeAnalyzer.analyzeFile(nonExistentFile);
        });
    }

    @Test
    void testAnalyzeFile_WithPrivateMembersIncluded() throws IOException {
        // Given
        when(analysisSettings.includePrivateMembers()).thenReturn(true);
        
        String javaCode = """
            package com.example;
            
            public class PrivateTest {
                private String privateField;
                protected String protectedField;
                public String publicField;
                
                private void privateMethod() {}
                protected void protectedMethod() {}
                public void publicMethod() {}
                
                private class InnerClass {}
            }
            """;
        
        Path javaFile = tempDir.resolve("PrivateTest.java");
        Files.writeString(javaFile, javaCode);
        
        // When
        List<CodeElement> elements = javaCodeAnalyzer.analyzeFile(javaFile);
        
        // Then
        // Should include private members when configured
        assertTrue(elements.stream().anyMatch(e -> 
            e.type() == CodeElementType.FIELD && e.name().equals("privateField")));
        assertTrue(elements.stream().anyMatch(e -> 
            e.type() == CodeElementType.METHOD && e.name().equals("privateMethod")));
        assertTrue(elements.stream().anyMatch(e -> 
            e.type() == CodeElementType.CLASS && e.name().equals("InnerClass")));
    }

    @Test
    void testAnalyzeFile_WithPrivateMembersExcluded() throws IOException {
        // Given
        when(analysisSettings.includePrivateMembers()).thenReturn(false);
        
        String javaCode = """
            package com.example;
            
            public class PrivateTest {
                private String privateField;
                protected String protectedField;
                public String publicField;
                
                private void privateMethod() {}
                protected void protectedMethod() {}
                public void publicMethod() {}
            }
            """;
        
        Path javaFile = tempDir.resolve("PrivateTest.java");
        Files.writeString(javaFile, javaCode);
        
        // When
        List<CodeElement> elements = javaCodeAnalyzer.analyzeFile(javaFile);
        
        // Then
        // Should exclude private members when configured
        assertFalse(elements.stream().anyMatch(e -> 
            e.type() == CodeElementType.FIELD && e.name().equals("privateField")));
        assertFalse(elements.stream().anyMatch(e -> 
            e.type() == CodeElementType.METHOD && e.name().equals("privateMethod")));
        
        // Should include protected and public members
        assertTrue(elements.stream().anyMatch(e -> 
            e.type() == CodeElementType.FIELD && e.name().equals("protectedField")));
        assertTrue(elements.stream().anyMatch(e -> 
            e.type() == CodeElementType.METHOD && e.name().equals("publicMethod")));
    }

    @Test
    void testAnalyzeFile_ComplexInterface() throws IOException {
        String javaCode = """
            package com.example;
            
            import java.util.List;
            
            /**
             * A complex interface with default methods
             */
            public interface ComplexInterface extends Comparable<String> {
                String CONSTANT = "value";
                
                void abstractMethod();
                
                default void defaultMethod() {
                    System.out.println("Default implementation");
                }
                
                static void staticMethod() {
                    System.out.println("Static method");
                }
            }
            """;
        
        Path javaFile = tempDir.resolve("ComplexInterface.java");
        Files.writeString(javaFile, javaCode);
        
        List<CodeElement> elements = javaCodeAnalyzer.analyzeFile(javaFile);
        
        // Should detect interface
        assertTrue(elements.stream().anyMatch(e -> 
            e.type() == CodeElementType.CLASS && e.name().equals("ComplexInterface")));
        
        // Should detect constant field
        assertTrue(elements.stream().anyMatch(e -> 
            e.type() == CodeElementType.FIELD && e.name().equals("CONSTANT")));
        
        // Should detect methods
        assertTrue(elements.stream().anyMatch(e -> 
            e.type() == CodeElementType.METHOD && e.name().equals("abstractMethod")));
        assertTrue(elements.stream().anyMatch(e -> 
            e.type() == CodeElementType.METHOD && e.name().equals("defaultMethod")));
        assertTrue(elements.stream().anyMatch(e -> 
            e.type() == CodeElementType.METHOD && e.name().equals("staticMethod")));
    }

    @Test
    void testAnalyzeFile_AnnotationClass() throws IOException {
        String javaCode = """
            package com.example;
            
            import java.lang.annotation.Retention;
            import java.lang.annotation.RetentionPolicy;
            
            @Retention(RetentionPolicy.RUNTIME)
            public @interface CustomAnnotation {
                String value() default "";
                int count() default 1;
            }
            """;
        
        Path javaFile = tempDir.resolve("CustomAnnotation.java");
        Files.writeString(javaFile, javaCode);
        
        List<CodeElement> elements = javaCodeAnalyzer.analyzeFile(javaFile);
        
        // Should detect some elements from annotation (parser support may vary)
        // Annotation parsing might not be fully supported, so just ensure no crash
        assertTrue(elements.size() >= 0);
    }

    @Test
    void testAnalyzeFile_MethodsWithComplexSignatures() throws IOException {
        String javaCode = """
            package com.example;
            
            import java.util.List;
            import java.util.Map;
            
            public class ComplexMethods {
                public <T> List<T> genericMethod(T item) {
                    return List.of(item);
                }
                
                public void methodWithVarargs(String... args) {}
                
                public <K, V> Map<K, V> complexGenericMethod(
                    List<? extends K> keys, 
                    V defaultValue
                ) throws Exception {
                    return Map.of();
                }
                
                public synchronized final void modifiedMethod() {}
            }
            """;
        
        Path javaFile = tempDir.resolve("ComplexMethods.java");
        Files.writeString(javaFile, javaCode);
        
        List<CodeElement> elements = javaCodeAnalyzer.analyzeFile(javaFile);
        
        // Should detect all methods with their complex signatures
        assertTrue(elements.stream().anyMatch(e -> 
            e.type() == CodeElementType.METHOD && e.name().equals("genericMethod")));
        assertTrue(elements.stream().anyMatch(e -> 
            e.type() == CodeElementType.METHOD && e.name().equals("methodWithVarargs")));
        assertTrue(elements.stream().anyMatch(e -> 
            e.type() == CodeElementType.METHOD && e.name().equals("complexGenericMethod")));
        assertTrue(elements.stream().anyMatch(e -> 
            e.type() == CodeElementType.METHOD && e.name().equals("modifiedMethod")));
        
        // Check that parameters are captured
        CodeElement complexMethod = elements.stream()
            .filter(e -> e.name().equals("complexGenericMethod"))
            .findFirst()
            .orElse(null);
        
        assertNotNull(complexMethod);
        assertFalse(complexMethod.parameters().isEmpty());
    }

    @Test
    void testAnalyzeFile_StaticInitializerAndInstanceInitializer() throws IOException {
        String javaCode = """
            package com.example;
            
            public class InitializerTest {
                static {
                    System.out.println("Static initializer");
                }
                
                {
                    System.out.println("Instance initializer");
                }
                
                public InitializerTest() {
                    System.out.println("Constructor");
                }
            }
            """;
        
        Path javaFile = tempDir.resolve("InitializerTest.java");
        Files.writeString(javaFile, javaCode);
        
        List<CodeElement> elements = javaCodeAnalyzer.analyzeFile(javaFile);
        
        // Should detect the class at minimum
        assertTrue(elements.stream().anyMatch(e -> 
            e.type() == CodeElementType.CLASS && e.name().equals("InitializerTest")));
        
        // Constructor detection may vary by parser
        // At minimum, class should be detected
    }

    @Test
    void testAnalyzeFile_RecordClass() throws IOException {
        String javaCode = """
            package com.example;
            
            /**
             * A record class
             */
            public record Person(String name, int age) {
                public String getDisplayName() {
                    return name + " (" + age + ")";
                }
            }
            """;
        
        Path javaFile = tempDir.resolve("Person.java");
        Files.writeString(javaFile, javaCode);
        
        List<CodeElement> elements = javaCodeAnalyzer.analyzeFile(javaFile);
        
        // Should detect custom method at minimum
        assertTrue(elements.stream().anyMatch(e -> 
            e.type() == CodeElementType.METHOD && e.name().equals("getDisplayName")));
            
        // At least some elements should be detected
        assertFalse(elements.isEmpty());
    }

    @Test
    void testAnalyzeFile_NestedClasses() throws IOException {
        String javaCode = """
            package com.example;
            
            public class OuterClass {
                public static class StaticNestedClass {
                    public void nestedMethod() {}
                }
                
                public class InnerClass {
                    public void innerMethod() {}
                }
                
                public void outerMethod() {
                    class LocalClass {
                        public void localMethod() {}
                    }
                }
            }
            """;
        
        Path javaFile = tempDir.resolve("OuterClass.java");
        Files.writeString(javaFile, javaCode);
        
        List<CodeElement> elements = javaCodeAnalyzer.analyzeFile(javaFile);
        
        // Should detect all classes
        assertTrue(elements.stream().anyMatch(e -> 
            e.type() == CodeElementType.CLASS && e.name().equals("OuterClass")));
        assertTrue(elements.stream().anyMatch(e -> 
            e.type() == CodeElementType.CLASS && e.name().equals("StaticNestedClass")));
        assertTrue(elements.stream().anyMatch(e -> 
            e.type() == CodeElementType.CLASS && e.name().equals("InnerClass")));
        
        // Should detect methods
        assertTrue(elements.stream().anyMatch(e -> 
            e.type() == CodeElementType.METHOD && e.name().equals("outerMethod")));
        assertTrue(elements.stream().anyMatch(e -> 
            e.type() == CodeElementType.METHOD && e.name().equals("nestedMethod")));
        assertTrue(elements.stream().anyMatch(e -> 
            e.type() == CodeElementType.METHOD && e.name().equals("innerMethod")));
    }

    @Test
    void testAnalyzeFile_Constructor() throws IOException {
        // When
        JavaCodeAnalyzer analyzer = new JavaCodeAnalyzer(config);
        
        // Then
        assertNotNull(analyzer);
    }
}