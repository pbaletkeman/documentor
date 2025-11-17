package com.documentor.service.diagram;

import org.springframework.stereotype.Component;

/**
 * 🔍 Diagram Generator Factory
 *
 * Factory for creating appropriate diagram generators based on code elements.
 * Supports both Mermaid and PlantUML diagram generation.
 */
@Component
public class DiagramGeneratorFactory {

    private final MermaidClassDiagramGenerator mermaidClassDiagramGenerator;
    private final PlantUMLClassDiagramGenerator plantUMLClassDiagramGenerator;

    public DiagramGeneratorFactory(
            final MermaidClassDiagramGenerator
                    mermaidClassDiagramGeneratorParam,
            final PlantUMLClassDiagramGenerator
                    plantUMLClassDiagramGeneratorParam) {
        this.mermaidClassDiagramGenerator = mermaidClassDiagramGeneratorParam;
        this.plantUMLClassDiagramGenerator = plantUMLClassDiagramGeneratorParam;
    }

    /**
     * 🔍 Returns the Mermaid diagram generator
     */
    public MermaidClassDiagramGenerator getClassDiagramGenerator() {
        return mermaidClassDiagramGenerator;
    }

    /**
     * 🔍 Returns the Mermaid diagram generator (explicit method name)
     */
    public MermaidClassDiagramGenerator getMermaidClassDiagramGenerator() {
        return mermaidClassDiagramGenerator;
    }

    /**
     * 🔍 Returns the PlantUML diagram generator
     */
    public PlantUMLClassDiagramGenerator getPlantUMLClassDiagramGenerator() {
        return plantUMLClassDiagramGenerator;
    }
}
