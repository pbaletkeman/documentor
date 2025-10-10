package com.documentor.service.diagram;

import org.springframework.stereotype.Component;

/**
 * 🏭 Diagram Generator Factory
 * 
 * Factory for creating appropriate diagram generators based on code elements.
 * Reduces complexity by extracting the diagram generation logic from MermaidDiagramService.
 */
@Component
public class DiagramGeneratorFactory {
    
    private final MermaidClassDiagramGenerator classDiagramGenerator;
    
    public DiagramGeneratorFactory(MermaidClassDiagramGenerator classDiagramGenerator) {
        this.classDiagramGenerator = classDiagramGenerator;
    }
    
    /**
     * 🔄 Returns the appropriate diagram generator
     */
    public MermaidClassDiagramGenerator getClassDiagramGenerator() {
        return classDiagramGenerator;
    }
}