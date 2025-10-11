package com.documentor.service.diagram;

/**
 * Test implementation of DiagramGeneratorFactory for use in unit tests
 */
public class TestDiagramGeneratorFactory extends DiagramGeneratorFactory {

    public TestDiagramGeneratorFactory(final MermaidClassDiagramGenerator classDiagramGenerator) {
        super(classDiagramGenerator);
    }
}

