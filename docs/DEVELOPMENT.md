# 🔧 Development Guide

Comprehensive guide for developers working on the Documentor project, including setup, architecture, testing, and contribution guidelines.

## Table of Contents

- [Project Structure](#project-structure)
- [Development Environment Setup](#development-environment-setup)
- [Build System](#build-system)
- [Code Organization](#code-organization)
- [Testing](#testing)
- [Debugging](#debugging)
- [Best Practices](#best-practices)
- [Troubleshooting](#troubleshooting)

## Project Structure

```text
documentor/
├── src/
│   ├── main/
│   │   ├── java/com/documentor/
│   │   │   ├── DocumentorApplication.java
│   │   │   ├── cli/
│   │   │   │   ├── DocumentorCommands.java
│   │   │   │   ├── DirectCommandProcessor.java
│   │   │   │   └── handlers/
│   │   │   │       ├── AnalysisCommandHandler.java
│   │   │   │       ├── ConfigurationCommandHandler.java
│   │   │   │       ├── ScanCommandHandler.java
│   │   │   │       └── StatusCommandHandler.java
│   │   │   ├── config/
│   │   │   │   ├── AppConfig.java
│   │   │   │   ├── DocumentorConfig.java
│   │   │   │   └── model/
│   │   │   │       ├── AnalysisSettings.java
│   │   │   │       ├── LlmModel.java
│   │   │   │       └── OutputSettings.java
│   │   │   ├── model/
│   │   │   │   ├── CodeElement.java
│   │   │   │   ├── CodeElementType.java
│   │   │   │   ├── CodeVisibility.java
│   │   │   │   └── ProjectAnalysis.java
│   │   │   ├── service/
│   │   │   │   ├── CodeAnalysisService.java
│   │   │   │   ├── DocumentationService.java
│   │   │   │   ├── JavaCodeAnalyzer.java
│   │   │   │   ├── LlmService.java
│   │   │   │   ├── MermaidDiagramService.java
│   │   │   │   ├── PlantUMLDiagramService.java
│   │   │   │   ├── PythonCodeAnalyzer.java
│   │   │   │   ├── analysis/
│   │   │   │   │   └── CodeAnalysisOrchestrator.java
│   │   │   │   ├── diagram/
│   │   │   │   │   ├── DiagramElementFilter.java
│   │   │   │   │   ├── DiagramGenerator.java
│   │   │   │   │   ├── DiagramGeneratorFactory.java
│   │   │   │   │   ├── DiagramPathManager.java
│   │   │   │   │   ├── MermaidClassDiagramGenerator.java
│   │   │   │   │   ├── MermaidElementFormatter.java
│   │   │   │   │   ├── MermaidFileManager.java
│   │   │   │   │   ├── PlantUMLClassDiagramGenerator.java
│   │   │   │   │   └── PlantUMLElementFormatter.java
│   │   │   │   ├── documentation/
│   │   │   │   │   ├── DocumentationFormatter.java
│   │   │   │   │   ├── ElementDocumentationGenerator.java
│   │   │   │   │   ├── MainDocumentationGenerator.java
│   │   │   │   │   └── UnitTestDocumentationGenerator.java
│   │   │   │   ├── llm/
│   │   │   │   │   ├── LlmApiClient.java
│   │   │   │   │   ├── LlmModelTypeDetector.java
│   │   │   │   │   ├── LlmPromptTemplates.java
│   │   │   │   │   ├── LlmRequestBuilder.java
│   │   │   │   │   ├── LlmRequestFormatter.java
│   │   │   │   │   ├── LlmResponseHandler.java
│   │   │   │   │   └── LlmResponseParser.java
│   │   │   │   └── python/
│   │   │   │       ├── PythonAstAnalyzer.java
│   │   │   │       ├── PythonAstParserService.java
│   │   │   │       ├── PythonElementExtractor.java
│   │   │   │       ├── PythonRegexAnalyzer.java
│   │   │   │       └── PythonSyntaxValidator.java
│   │   │   └── util/
│   │   │       └── ApplicationConstants.java
│   │   └── resources/
│   │       ├── application.yml
│   │       └── application-test.yml
│   └── test/
│       └── java/com/documentor/
│           ├── DocumentorApplicationIntegrationTest.java
│           ├── DocumentorApplicationTests.java
│           └── [comprehensive test suite matching main structure]
├── config/
│   └── checkstyle/
│       └── checkstyle.xml
├── .githooks/
│   ├── pre-commit
│   └── pre-commit.bat
├── build.gradle
├── config.json
├── config-ollama.json
└── README.md
```

## Key Components

| Component                    | Purpose                                                         |
| ---------------------------- | --------------------------------------------------------------- |
| **CodeAnalysisService**      | Orchestrates project analysis with multi-threaded processing    |
| **JavaCodeAnalyzer**         | Advanced Java parsing using JavaParser with AST analysis        |
| **PythonCodeAnalyzer**       | Sophisticated Python analysis with AST and regex fallback       |
| **LlmService**               | Handles LLM API communication with multiple provider support    |
| **DocumentationService**     | Generates comprehensive markdown documentation                  |
| **MermaidDiagramService**    | Creates visual class diagrams with advanced formatting          |
| **PlantUMLDiagramService**   | Generates professional UML diagrams with relationship detection |
| **DocumentorCommands**       | Spring Shell CLI interface with status monitoring               |
| **CodeAnalysisOrchestrator** | Coordinates complex analysis workflows                          |

## Development Setup

### Prerequisites

- Java 21 or higher
- Gradle 9.1.0 or higher
- Git for version control

### Initial Setup

```bash
# Clone repository
git clone https://github.com/pbaletkeman/documentor.git
cd documentor

# Set up pre-commit hooks
cp .githooks/pre-commit .git/hooks/pre-commit
chmod +x .git/hooks/pre-commit

# Build project
./gradlew build
```

## Building and Running

### Build Commands

```bash
# Clean build
./gradlew clean build

# Build without tests
./gradlew build -x test

# Build and run application
./gradlew runApp

# Create distribution JAR
./gradlew bootJar
```

### Running the Application

```bash
# Interactive mode
./gradlew runApp

# With custom config
./gradlew runApp --args="--spring.config.location=config.json"

# Direct JAR execution
java -jar build/libs/documentor-*.jar
```

## Code Organization

### Layered Architecture

```
┌─────────────────────────────────────┐
│   CLI Layer (DocumentorCommands)    │
├─────────────────────────────────────┤
│  Service Layer (Analysis, LLM, Doc) │
├─────────────────────────────────────┤
│  Analysis Layer (Java, Python)      │
├─────────────────────────────────────┤
│  Utility Layer (Config, Constants)  │
└─────────────────────────────────────┘
```

### Adding Support for New Languages

1. **Create analyzer class** (e.g., `TypeScriptCodeAnalyzer`)
2. **Implement language-specific parsing** logic
3. **Register in CodeAnalysisService** with file extensions
4. **Add configuration options** for the language
5. **Create comprehensive tests** (maintain 95%+ coverage)
6. **Update documentation** with examples

### Example: New Language Support

```java
public class TypeScriptCodeAnalyzer implements CodeAnalyzer {
    @Override
    public List<CodeElement> analyze(String filePath, String content) {
        // Parse TypeScript AST
        // Extract classes, methods, properties
        // Return CodeElement list
    }

    @Override
    public List<String> getSupportedExtensions() {
        return List.of(".ts", ".tsx");
    }
}
```

## Code Quality Standards

### Coverage Requirements

- **Minimum**: 95% code coverage (currently 96%+)
- **Tools**: JaCoCo for measurement, Checkstyle for enforcement
- **Command**: `./gradlew test jacocoTestReport`

### Style Guide

- Follow **Google Java Style Guide**
- Enforce with Checkstyle configuration
- All Pull Requests must pass: `./gradlew check`

### Pre-commit Hooks

Automatically runs before each commit:

```bash
✅ Checkstyle linting
🧪 Unit tests
📊 Coverage verification
```

## Testing Strategy

### Test Organization

```
src/test/java/com/documentor/
├── cli/
│   ├── DocumentorCommandsTest.java
│   ├── DirectCommandProcessorTest.java
│   └── handlers/
├── config/
│   └── ConfigurationTests.java
├── model/
│   └── ModelTests.java
├── service/
│   ├── CodeAnalysisServiceTest.java
│   ├── DocumentationServiceTest.java
│   ├── LlmServiceTest.java
│   └── [service tests]
└── integration/
    └── DocumentorApplicationIntegrationTest.java
```

### Writing Tests

```java
@Test
void testAnalyzeJavaProject() {
    // Arrange
    String projectPath = "test-project";
    CodeAnalysisService service = new CodeAnalysisService();

    // Act
    ProjectAnalysis result = service.analyzeProject(projectPath);

    // Assert
    assertNotNull(result);
    assertTrue(result.getElements().size() > 0);
}
```

### Coverage Verification

```bash
# Run tests with coverage
./gradlew test jacocoTestReport

# View HTML report
open build/reports/jacoco/test/html/index.html
```

## Performance Optimization

### Multi-Threading

```java
// CodeAnalysisService uses thread pools for parallel processing
int threadCount = Runtime.getRuntime().availableProcessors();
ExecutorService executor = Executors.newFixedThreadPool(threadCount);
```

### Caching Strategies

- Cache parsed Java ASTs during single analysis run
- Cache configuration during session
- Cache LLM API responses when appropriate

### Memory Management

For large projects:

```bash
export JAVA_OPTS="-Xmx4g"
./gradlew runApp
```

## Debugging

### Enable Debug Logging

```json
{
  "logging": {
    "level": {
      "root": "INFO",
      "com.documentor": "DEBUG"
    }
  }
}
```

### VS Code Debug Configuration

```json
{
  "type": "java",
  "name": "Debug Documentor",
  "request": "launch",
  "mainClass": "com.documentor.DocumentorApplication",
  "args": "",
  "cwd": "${workspaceFolder}"
}
```

## Version Management

### Current Version: 1.1.1

Recent updates include:

- Spring Boot 3.5.6 upgrade
- Java 21 full support
- Private member analysis
- Enhanced error handling
- 96%+ test coverage

### Release Process

1. Update version in `build.gradle`
2. Create feature branch and tests
3. Update documentation
4. Submit Pull Request
5. Code review and merge
6. Tag release in Git
7. Create GitHub release notes

## Contributing Code

1. **Fork repository** on GitHub
2. **Create feature branch**: `git checkout -b feature/my-feature`
3. **Implement changes** with tests
4. **Run quality checks**: `./gradlew check`
5. **Commit changes**: `git commit -m "Add my feature"`
6. **Push branch**: `git push origin feature/my-feature`
7. **Open Pull Request** with detailed description

### Pull Request Guidelines

- Clear description of changes
- Reference related issues
- Include test coverage
- Update documentation
- Pass all CI checks

## Resources

- **[JavaParser Documentation](https://javaparser.org/)** - Java AST parsing
- **[Spring Boot Guide](https://spring.io/guides/gs/spring-boot/)** - Framework details
- **[Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)** - Code style
- **[JUnit 5 Documentation](https://junit.org/junit5/)** - Testing framework
