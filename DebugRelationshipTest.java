import com.documentor.model.CodeElement;
import com.documentor.model.CodeElementType;
import com.documentor.service.diagram.MermaidClassDiagramGenerator;
import java.nio.file.Path;
import java.util.List;

public class DebugRelationshipTest {
    public static void main(String[] args) throws Exception {
        MermaidClassDiagramGenerator generator = new MermaidClassDiagramGenerator();

        CodeElement mainClass = new CodeElement(
            CodeElementType.CLASS,
            "MainClass",
            "com.example.MainClass",
            "/src/main/java/MainClass.java",
            1,
            "public class MainClass",
            "Main class with relationships",
            List.of(),
            List.of()
        );

        CodeElement otherClass = new CodeElement(
            CodeElementType.CLASS,
            "OtherClass",
            "com.example.OtherClass",
            "/src/main/java/OtherClass.java",
            1,
            "public class OtherClass",
            "Another class for relationships",
            List.of(),
            List.of()
        );

        CodeElement methodWithDependency = new CodeElement(
            CodeElementType.METHOD,
            "useOtherClass",
            "com.example.MainClass.useOtherClass",
            "/src/main/java/MainClass.java",
            3,
            "public void useOtherClass(OtherClass other)",
            "Method that uses another class",
            List.of("OtherClass other"),
            List.of()
        );

        List<CodeElement> elements = List.of(mainClass, otherClass, methodWithDependency);

        String output = generator.generateClassDiagram(mainClass, elements, Path.of("temp"));
        System.out.println("Generated diagram content:");
        System.out.println(output);
    }
}
