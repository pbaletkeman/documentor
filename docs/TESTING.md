# 🧪 Testing Guide

## Test Coverage: 96%+ ✅

Documentor maintains high-quality code standards with comprehensive test coverage exceeding the required 95% minimum threshold.

## Running Tests

### Execute All Tests

```bash
# Run all tests
./gradlew test

# Run tests with coverage report
./gradlew test jacocoTestReport

# Run specific test class
./gradlew test --tests DocumentorCommandsTest

# Run specific test method
./gradlew test --tests DocumentorCommandsTest.testAnalyzeCommand
```

### Viewing Coverage Reports

```bash
# Windows
start build/reports/jacoco/test/html/index.html

# macOS
open build/reports/jacoco/test/html/index.html

# Linux
xdg-open build/reports/jacoco/test/html/index.html
```

## Quality Checks

### Run All Quality Checks

```bash
# Run Checkstyle, tests, and coverage verification
./gradlew check

# Build with full verification
./gradlew build

# Run Checkstyle only
./gradlew checkstyleMain checkstyleTest
```

## Test Coverage by Module

| Module                       | Coverage | Details                                               |
| ---------------------------- | -------- | ----------------------------------------------------- |
| **Model Classes**            | 100%     | CodeElement, ProjectAnalysis, etc.                    |
| **Service Layer**            | 88-99%   | CodeAnalysisService, DocumentationService, LlmService |
| **CLI Commands**             | 97%      | DocumentorCommands with status command                |
| **Configuration**            | 100%     | DocumentorConfig, AppConfig                           |
| **Analyzers**                | 88-98%   | JavaCodeAnalyzer, PythonCodeAnalyzer                  |
| **Diagram Generators**       | 98%      | All diagram service modules                           |
| **Documentation Generators** | 98%      | All documentation modules                             |

## Test Organization

### Directory Structure

```
src/test/java/com/documentor/
├── cli/
│   ├── DocumentorCommandsTest.java
│   ├── DocumentorCommandsEnhancedTest.java
│   ├── DirectCommandProcessorTest.java
│   └── handlers/
│       ├── AnalysisCommandHandlerTest.java
│       ├── CommonCommandHandlerTest.java
│       ├── ConfigurationCommandHandlerTest.java
│       ├── ConfigurationCommandHandlerBranchTest.java
│       ├── ScanCommandHandlerTest.java
│       └── StatusCommandHandlerTest.java
├── config/
│   ├── AppConfigTest.java
│   ├── DocumentorConfigTest.java
│   └── ConfigurationIntegrationTest.java
├── model/
│   ├── CodeElementTest.java
│   ├── CodeElementTypeTest.java
│   ├── CodeVisibilityTest.java
│   └── ProjectAnalysisTest.java
├── service/
│   ├── CodeAnalysisServiceTest.java
│   ├── DocumentationServiceTest.java
│   ├── LlmServiceTest.java
│   ├── MermaidDiagramServiceTest.java
│   ├── PlantUMLDiagramServiceTest.java
│   ├── PythonCodeAnalyzerTest.java
│   ├── analysis/
│   │   └── CodeAnalysisOrchestratorTest.java
│   ├── diagram/
│   │   ├── DiagramElementFilterTest.java
│   │   ├── MermaidClassDiagramGeneratorTest.java
│   │   ├── PlantUMLClassDiagramGeneratorTest.java
│   │   └── [diagram tests]
│   ├── documentation/
│   │   ├── DocumentationFormatterTest.java
│   │   ├── ElementDocumentationGeneratorTest.java
│   │   ├── MainDocumentationGeneratorTest.java
│   │   └── UnitTestDocumentationGeneratorTest.java
│   └── llm/
│       ├── LlmApiClientTest.java
│       ├── LlmModelTypeDetectorTest.java
│       ├── LlmRequestBuilderTest.java
│       └── [llm tests]
└── integration/
    ├── DocumentorApplicationIntegrationTest.java
    └── DocumentorApplicationTests.java
```

## Writing Tests

### Test Structure (AAA Pattern)

```java
@Test
void testAnalyzeProject() {
    // Arrange - Set up test data and dependencies
    String projectPath = "test-project";
    CodeAnalysisService service = new CodeAnalysisService();

    // Act - Execute the code being tested
    ProjectAnalysis result = service.analyzeProject(projectPath);

    // Assert - Verify the results
    assertNotNull(result);
    assertTrue(result.getElements().size() > 0);
}
```

### Example: Service Test with Mocking

```java
@ExtendWith(MockitoExtension.class)
class CodeAnalysisServiceTest {

    @Mock
    private JavaCodeAnalyzer javaAnalyzer;

    @Mock
    private PythonCodeAnalyzer pythonAnalyzer;

    @InjectMocks
    private CodeAnalysisService service;

    @Test
    void testAnalyzeJavaFiles() {
        // Arrange
        CodeElement mockElement = new CodeElement();
        mockElement.setName("TestClass");
        when(javaAnalyzer.analyze("Test.java", "..."))
            .thenReturn(List.of(mockElement));

        // Act
        List<CodeElement> results = service.analyzeFile("Test.java");

        // Assert
        assertEquals(1, results.size());
        assertEquals("TestClass", results.get(0).getName());
    }
}
```

### Example: Integration Test

```java
@SpringBootTest
class DocumentorApplicationIntegrationTest {

    @Autowired
    private CodeAnalysisService codeAnalysisService;

    @Autowired
    private DocumentationService documentationService;

    @Test
    void testFullAnalysisPipeline() {
        // Arrange
        String projectPath = "src/test/resources/test-project";

        // Act
        ProjectAnalysis analysis = codeAnalysisService.analyzeProject(projectPath);
        String documentation = documentationService.generateDocumentation(analysis);

        // Assert
        assertNotNull(analysis);
        assertNotNull(documentation);
        assertTrue(documentation.contains("class"));
    }
}
```

## Test Types

### Unit Tests

- **Purpose**: Test individual methods/classes in isolation
- **Scope**: Single method or class
- **Dependencies**: Mocked
- **Execution**: Fast (milliseconds)
- **Example**: Testing parameter validation logic

### Integration Tests

- **Purpose**: Test component interactions
- **Scope**: Multiple classes/services
- **Dependencies**: Real or properly configured
- **Execution**: Moderate (seconds)
- **Example**: Testing full analysis pipeline

### End-to-End Tests

- **Purpose**: Test complete workflows
- **Scope**: Entire application
- **Dependencies**: All real systems
- **Execution**: Slower (minutes)
- **Example**: Full project analysis with file I/O

## Mocking and Testing Utilities

### Common Mocking Patterns

```java
// Mock LLM responses
@Mock
private LlmService llmService;

when(llmService.generateDocumentation(any()))
    .thenReturn("Generated documentation");

// Mock file I/O
Path testFile = Files.createTempFile("test", ".java");
Files.write(testFile, "public class Test {}".getBytes());

// Verify interactions
verify(llmService).generateDocumentation(any());
verify(llmService, times(2)).generateDocumentation(any());
```

### Test Fixtures

```java
class TestFixtures {
    public static CodeElement createTestClass() {
        CodeElement element = new CodeElement();
        element.setName("TestClass");
        element.setType(CodeElementType.CLASS);
        return element;
    }

    public static ProjectAnalysis createTestAnalysis() {
        ProjectAnalysis analysis = new ProjectAnalysis();
        analysis.setElements(List.of(createTestClass()));
        return analysis;
    }
}
```

## Coverage Requirements

### Minimum Coverage: 95%

```bash
# Verify coverage meets minimum
./gradlew jacocoTestCoverageVerification

# Generate detailed report
./gradlew jacocoTestReport
```

### Coverage Targets by Component

| Component     | Target | Current |
| ------------- | ------ | ------- |
| Models        | 100%   | 100%    |
| Services      | 95%+   | 96%     |
| CLI           | 95%+   | 97%     |
| Configuration | 100%   | 100%    |
| Analyzers     | 95%+   | 95%     |
| Generators    | 95%+   | 98%     |

## Pre-commit Hooks

### Automatic Quality Checks

Hooks run automatically before each commit:

```bash
✅ Checkstyle linting
🧪 Unit tests
📊 Coverage verification
```

### Manual Hook Execution

```bash
# Run pre-commit checks manually
./.githooks/pre-commit

# On Windows
.\.githooks\pre-commit.bat
```

## Debugging Tests

### VS Code Debug Configuration

```json
{
  "type": "java",
  "name": "Debug Tests",
  "request": "launch",
  "mainClass": "org.junit.platform.console.ConsoleLauncher",
  "args": "--scan-classpath",
  "cwd": "${workspaceFolder}"
}
```

### Enable Debug Output

```bash
# Run with debug logging
./gradlew test -i --debug

# Run single test with output
./gradlew test --tests TestClassName -i
```

## Continuous Integration

### GitHub Actions Workflow

Tests run automatically on:

- Push to main branch
- Pull requests
- Scheduled daily runs

### Local CI Simulation

```bash
# Run all CI checks locally
./gradlew check build jacocoTestReport
```

## Test Maintenance

### Common Issues and Solutions

| Issue                 | Solution                                     |
| --------------------- | -------------------------------------------- |
| **Flaky tests**       | Use `@TestTimeout`, mock time-dependent code |
| **Slow tests**        | Profile, optimize queries, reduce I/O        |
| **Hard to test code** | Refactor for dependency injection            |
| **Coverage gaps**     | Review uncovered branches, add edge cases    |

### Best Practices

1. **One assertion per test** (when possible)
2. **Descriptive test names** describing the scenario
3. **Comprehensive edge cases** (null, empty, invalid)
4. **Avoid test interdependencies** (tests should be independent)
5. **Keep tests maintainable** (update when code changes)

## Resources

- **[JUnit 5 Documentation](https://junit.org/junit5/)** - Testing framework
- **[Mockito Documentation](https://site.mockito.org/)** - Mocking library
- **[JaCoCo Guide](https://www.jacoco.org/jacoco/trunk/doc/)** - Coverage tool
- **[Spring Boot Testing](https://spring.io/guides/gs/testing-web/)** - Integration testing
