# Copilot Instructions for Documentor

This file provides guidelines for AI assistants (like GitHub Copilot) working on the Documentor project.

## Project Context

**Documentor** is a Java Spring Boot CLI application that generates comprehensive documentation for Java projects using Large Language Models (LLMs) including OpenAI, Ollama, and LlamaCpp.

**Key Technologies**:
- Java 21 (main branch), Java 17 (java-17-lts branch)
- Spring Boot 3.2
- Gradle 9.1.0
- Multiple LLM provider integrations

## Code Generation Guidelines

### 1. Follow Project Patterns

**Configuration Pattern**:
- Use `@Configuration` classes for Spring bean definitions
- Use records for immutable data classes
- Implement `LlmService` interface for new LLM providers

**Service Pattern**:
- Inject dependencies through constructor
- Use ThreadLocal for configuration context
- Implement async processing with ExecutorService

### 2. Testing Requirements

**Coverage Target**: 80%+ code coverage

**Test Structure**:
```java
@ExtendWith(MockitoExtension.class)
class MyServiceTest {
    @Mock
    private Dependency dependency;
    
    @InjectMocks
    private MyService service;
    
    @Test
    void shouldPerformExpectedBehavior() {
        // Arrange
        when(dependency.method()).thenReturn(value);
        
        // Act
        Result result = service.doSomething();
        
        // Assert
        assertThat(result).isNotNull();
        verify(dependency).method();
    }
}
```

### 3. Code Style

**Standards**: Google Java Style Guide with Spring conventions

**Commands**:
```bash
./gradlew checkstyleMain
./gradlew checkstyleTest
```

**Key Rules**:
- Classes: PascalCase
- Methods/variables: camelCase
- Constants: UPPER_SNAKE_CASE
- Indentation: 4 spaces
- Line length: 100 characters max

### 4. Configuration and Properties

**JSON Configuration Format**:
- Snake_case for JSON property names
- Maps to camelCase Java fields via @JsonProperty

```json
{
  "llm_models": [{
    "name": "llama3.2",
    "baseUrl": "http://localhost:11434",
    "apiKey": "not-needed",
    "maxTokens": 2048,
    "timeoutSeconds": 60
  }]
}
```

## Branch Strategy

### Main Branch (Java 21)
- Latest features and fixes
- Uses Java 21 modern features

### java-17-lts Branch
- Long-term support version
- Compatible with Java 17+
- Synchronized docs and fixes from main

**Branch Synchronization**:
```bash
# After changes to main:
git checkout java-17-lts
git merge origin/main -- docs/ Agents.md
git add .
git commit -m "Sync: Update documentation from main"
git push origin java-17-lts
```

## LLM Provider Integration

### Adding a New Provider

**Files to Create**:
1. `src/main/java/com/documentor/service/llm/providers/{Provider}Service.java`
2. Tests in `src/test/java/com/documentor/service/llm/providers/{Provider}ServiceTest.java`

**Interface Implementation**:
```java
public class MyProviderService implements LlmService {
    @Override
    public String generateDocumentation(CodeElement element) {
        String prompt = requestBuilder.buildPrompt(element);
        String response = callApi(prompt);
        return responseHandler.parseResponse(response);
    }
}
```

## Error Handling

**Never Expose Sensitive Information**:
```java
// Bad: Exposes API key
logger.error("API call failed with key: " + apiKey);

// Good: Generic user message, detailed internal log
logger.error("API call failed", exception);
throw new DocumentationException(
    "Failed to generate documentation. Check configuration."
);
```

## ThreadLocal Context Management

**Pattern Used**:
```java
private static final ThreadLocal<DocumentorConfig> CONFIG_HOLDER = 
    new ThreadLocal<>();

public static void setConfig(DocumentorConfig config) {
    CONFIG_HOLDER.set(config);
}

public static DocumentorConfig getConfig() {
    return CONFIG_HOLDER.get();
}

public static void clear() {
    CONFIG_HOLDER.remove();
}
```

## Documentation Guidelines

### Code Comments

**Write Why, Not What**:
```java
// Good: Explains intent
// Use ThreadLocal to isolate configuration per request
private static final ThreadLocal<DocumentorConfig> CONFIG = new ThreadLocal<>();

// Bad: Restates code
// Create a ThreadLocal variable
```

### Javadoc

**Required For**:
- All public classes
- All public methods
- Complex private methods

## Security Practices

### API Key Handling

**DO**:
```bash
export OPENAI_API_KEY="sk-..."
# Use secrets management
```

**DON'T**:
```java
logger.info("Key: " + apiKey);  // Never log keys
String key = "sk-hardcoded";     // Never hardcode
```

### Input Validation

**Always Validate**:
```java
if (!Files.exists(projectPath)) {
    throw new IllegalArgumentException("Project not found");
}

if (maxTokens < 1 || maxTokens > MAX_LIMIT) {
    throw new IllegalArgumentException("Invalid token limit");
}
```

## Git Workflow

### Commit Message Format

```
<type>(<scope>): <subject>

<body>

<footer>
```

**Types**: `feat`, `fix`, `docs`, `style`, `refactor`, `perf`, `test`, `chore`

### Before Pushing

```bash
./gradlew clean build
./gradlew checkstyleMain checkstyleTest
./gradlew jacocoTestReport
```

## Build System

### Gradle Commands

```bash
./gradlew build                 # Full build
./gradlew build -x test         # Skip tests
./gradlew test                  # Tests only
./gradlew jacocoTestReport      # Coverage report
./gradlew clean                 # Clean artifacts
./gradlew checkstyle            # Check code style
```

## Documentation Files

**When Modifying Documentation**:
1. Keep main README.md < 150 lines
2. Use proper markdown formatting
3. Update Table of Contents if file > 100 lines
4. Link from docs/README.md index
5. Ensure no broken references

**Key Files**:
- `README.md` - Project overview
- `docs/ARCHITECTURE.md` - System design
- `docs/SECURITY.md` - Security guidelines
- `docs/CONTRIBUTING.md` - Contribution guide
- `Agents.md` - Workflows and automation
- `CHANGELOG.md` - Release notes

## When in Doubt

1. **Follow Existing Patterns**: Look at similar code in project
2. **Check Tests**: Test files show expected behavior
3. **Read Documentation**: See docs/ directory
4. **Ask Questions**: Open GitHub Issues
5. **Run Validation**: Always run full build before submitting

---

**Version**: 2.1.0  
**Last Updated**: December 9, 2025
