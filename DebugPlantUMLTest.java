package com.documentor.service.diagram;

import com.documentor.model.CodeElement;
import com.documentor.model.CodeElementType;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class DebugPlantUMLTest {
    public static void main(String[] args) throws IOException {
        PlantUMLClassDiagramGenerator generator = new PlantUMLClassDiagramGenerator();
        
        CodeElement classElement = new CodeElement(
            CodeElementType.CLASS,
            "TestClass",
            "com.example.TestClass",
            "/test/TestClass.java",
            1,
            "public class TestClass",
            "Test class description",
            List.of(),
            List.of()
        );

        List<CodeElement> allElements = List.of(classElement);
        Path tempDir = Paths.get("c:\\temp");

        String result = generator.generateClassDiagram(classElement, allElements, tempDir);
        System.out.println("Generated PlantUML:");
        System.out.println(result);
        System.out.println("Contains @startuml: " + result.contains("@startuml"));
        System.out.println("Contains @enduml: " + result.contains("@enduml"));
        System.out.println("Contains class TestClass: " + result.contains("class TestClass"));
    }
}
