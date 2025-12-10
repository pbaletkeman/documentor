# Contributing Guide - Documentor

Welcome to Documentor! We appreciate your interest in contributing. This guide explains how to participate in the project effectively.

## Table of Contents

- [Getting Started](#getting-started)
- [Code of Conduct](#code-of-conduct)
- [How to Contribute](#how-to-contribute)
- [Development Setup](#development-setup)
- [Coding Standards](#coding-standards)
- [Testing Requirements](#testing-requirements)
- [Commit Guidelines](#commit-guidelines)
- [Pull Request Process](#pull-request-process)
- [Reporting Issues](#reporting-issues)

## Getting Started

### Prerequisites

- Java 21 (main branch) or Java 17 (java-17-lts branch)
- Gradle 9.1.0 or higher
- Git
- At least one LLM provider (OpenAI, Ollama, or LlamaCpp)

### Clone and Setup

```bash
git clone https://github.com/yourusername/documentor.git
cd documentor
./gradlew build
```

## Code of Conduct

### Our Pledge

We are committed to providing a welcoming and inspiring community for all. We pledge that everyone participating in our project and community will be treated with respect.

### Expected Behavior

- Use welcoming and inclusive language
- Be respectful of differing opinions and experiences
- Accept constructive criticism gracefully
- Focus on what is best for the community
- Show empathy towards other community members

### Unacceptable Behavior

- Harassment, discrimination, or bigotry
- Unwelcome sexual advances
- Deliberate intimidation or threats
- Publishing others' private information
- Other conduct that is unprofessional

## How to Contribute

### Types of Contributions

#### 1. Bug Reports

Found a bug? Open an issue with:

- Clear description of the problem
- Steps to reproduce
- Expected behavior
- Actual behavior
- Environment (Java version, OS, LLM provider)
- Logs or error messages

#### 2. Feature Requests

Have an idea? Share it! Provide:

- Clear problem statement
- Proposed solution
- Alternative approaches considered
- Use cases and benefits
- Any relevant examples

#### 3. Documentation

Help improve documentation:

- Fix typos and clarify explanations
- Add examples and use cases
- Update outdated information
- Improve code comments
- Translate documentation (if multilingual)

#### 4. Code Contributions

Want to fix a bug or add a feature?

1. Check existing issues and PRs first
2. Open an issue describing your plan (for major changes)
3. Wait for feedback before starting work
4. Follow development setup and coding standards
5. Create a pull request

## Development Setup

### Local Development Environment

1. **Clone the repository**:

   ```bash
   git clone https://github.com/yourusername/documentor.git
   cd documentor
   ```

2. **Create a feature branch**:

   ```bash
   git checkout -b feature/your-feature-name
   # or
   git checkout -b fix/issue-number
   ```

3. **Build the project**:

   ```bash
   ./gradlew build
   ```

4. **Run tests**:

   ```bash
   ./gradlew test
   ```

5. **Set up LLM provider**:
   - OpenAI: Export `OPENAI_API_KEY` environment variable
   - Ollama: Start `ollama serve` (default localhost:11434)
   - LlamaCpp: Start your server (default localhost:8000)

### Development Tools

**Recommended IDE**: IntelliJ IDEA Community Edition (free)

- Import as Gradle project
- Install Spring Boot support plugin
- Configure code style (see `.idea/` if committed)

**Build Commands**:

```bash
./gradlew clean build          # Full build with tests
./gradlew build -x test        # Build without tests
./gradlew test                 # Run tests only
./gradlew test --tests "*Spec" # Run specific tests
./gradlew jacocoTestReport     # Generate coverage report
./gradlew checkstyleMain       # Check code style
```

## Coding Standards

### Code Style

Follow Google Java Style Guide with Spring conventions:

```java
// Class documentation
/**
 * Generates documentation for a code element.
 *
 * This class orchestrates the documentation generation workflow
 * including analysis, LLM interaction, and formatting.
 */
public class DocumentationGenerator {

    // Method documentation
    /**
     * Generates documentation for a single element.
     *
     * @param element the code element to document
     * @return the generated documentation
     * @throws DocumentationException if generation fails
     */
    public String generate(CodeElement element) {
        // Implementation
    }
}
```

### Naming Conventions

- Classes: `PascalCase` (e.g., `DocumentationService`)
- Methods/variables: `camelCase` (e.g., `generateDocumentation()`)
- Constants: `UPPER_SNAKE_CASE` (e.g., `MAX_TOKENS_LIMIT`)
- Packages: `lowercase.dotted.format`
- Boolean methods: `is*` or `has*` prefix (e.g., `isValid()`)

### Code Quality Rules

**Checkstyle Configuration**: See `config/checkstyle/checkstyle.xml`

Run before committing:

```bash
./gradlew checkstyleMain checkstyleTest
```

### Comments

Write comments that explain **why**, not **what**:

```java
// Good: Explains rationale
// Use ThreadLocal to ensure each request has isolated configuration
private static final ThreadLocal<DocumentorConfig> CONFIG_HOLDER = new ThreadLocal<>();

// Bad: Restates the code
// Create a ThreadLocal
private static final ThreadLocal<DocumentorConfig> CONFIG = new ThreadLocal<>();
```

### Imports

Keep imports organized:

```java
// Standard library
import java.io.*;
import java.util.*;

// Third-party libraries
import org.springframework.boot.*;
import com.fasterxml.jackson.annotation.*;

// Project classes
import com.documentor.config.*;
import com.documentor.service.*;
```

## Testing Requirements

### Test Coverage

Maintain **80%+ code coverage**:

- Unit tests for core logic
- Integration tests for service layer
- Configuration tests for bean creation
- Mock external dependencies (LLM APIs)

### Writing Tests

Use JUnit 5 and Mockito:

```java
@ExtendWith(MockitoExtension.class)
class DocumentationServiceTest {

    @Mock
    private LlmService llmService;

    @InjectMocks
    private DocumentationService service;

    @Test
    void shouldGenerateDocumentationSuccessfully() {
        // Arrange
        CodeElement element = createTestElement();
        when(llmService.generateDocumentation(element))
            .thenReturn("Generated docs");

        // Act
        String result = service.generate(element);

        // Assert
        assertThat(result).isNotEmpty();
        verify(llmService).generateDocumentation(element);
    }

    @Test
    void shouldHandleErrorsGracefully() {
        // Arrange
        CodeElement element = createTestElement();
        when(llmService.generateDocumentation(element))
            .thenThrow(new LlmException("API error"));

        // Act & Assert
        assertThatThrownBy(() -> service.generate(element))
            .isInstanceOf(DocumentationException.class);
    }
}
```

### Running Tests

```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests DocumentationServiceTest

# Run with coverage
./gradlew jacocoTestReport

# View coverage report
# Open build/reports/jacoco/test/html/index.html
```

## Commit Guidelines

### Commit Message Format

```
<type>(<scope>): <subject>

<body>

<footer>
```

**Type**: `feat`, `fix`, `docs`, `style`, `refactor`, `perf`, `test`, `chore`

**Scope**: Component affected (e.g., `config`, `llm-service`, `cli`)

**Subject**:

- Imperative mood ("add" not "added")
- No period at end
- Under 50 characters

**Body**:

- Wrap at 72 characters
- Explain what and why (not how)
- Reference related issues

**Example**:

```
feat(llm-service): add support for Ollama provider

Implement LlmService for Ollama integration including:
- HTTP client for Ollama API
- Request/response handling
- Error recovery and retry logic

Fixes #42
```

### Keep Commits Focused

- One feature or fix per commit
- Don't mix refactoring with feature work
- Test should pass after each commit
- Rebase before creating PR to clean up commits

```bash
# Rebase interactive
git rebase -i origin/main

# Squash related commits
pick abc1234 Add Ollama support
squash def5678 Fix Ollama error handling
squash ghi9012 Add Ollama tests
```

## Pull Request Process

### Before Creating PR

1. **Update your branch**:

   ```bash
   git fetch origin
   git rebase origin/main
   ```

2. **Run full test suite**:

   ```bash
   ./gradlew clean build
   ```

3. **Check code style**:

   ```bash
   ./gradlew checkstyleMain checkstyleTest
   ```

4. **Verify coverage**:
   ```bash
   ./gradlew jacocoTestReport
   # Coverage should be 80%+
   ```

### PR Description Template

```markdown
## Description

Brief description of changes

## Type of Change

- [ ] Bug fix
- [ ] New feature
- [ ] Breaking change
- [ ] Documentation update

## Related Issues

Fixes #123

## Testing

Describe testing performed:

- [ ] Unit tests added
- [ ] Integration tests added
- [ ] Manual testing completed

## Checklist

- [ ] Code follows style guidelines
- [ ] Self-review completed
- [ ] Comments added for complex logic
- [ ] Documentation updated
- [ ] Tests added/updated
- [ ] Coverage maintained (80%+)
- [ ] No new warnings
```

### PR Review Process

- Maintainers review code for quality, security, and style
- Address review feedback promptly
- Rebase if requested instead of merge commits
- Once approved, maintainer will merge

### Merging to Main

Only maintainers can merge PRs:

- Squash commits if needed for clean history
- Delete feature branch after merge
- Reference PR number in merge commit message

## Reporting Issues

### Bug Reports

Use GitHub Issues with this template:

```markdown
## Bug Description

Clear, concise description of the issue

## Reproduction Steps

1. Step one
2. Step two
3. ...

## Expected Behavior

What should happen

## Actual Behavior

What actually happens

## Environment

- Java version:
- OS:
- Documentor version:
- LLM provider:

## Logs

Include relevant error messages and logs

## Screenshots

If applicable, add screenshots
```

### Feature Requests

```markdown
## Feature Description

Clear description of proposed feature

## Problem Statement

Problem this solves

## Proposed Solution

How you'd like it implemented

## Alternative Approaches

Other solutions considered

## Additional Context

Any other relevant information
```

## Community

### Discussions

- GitHub Discussions for questions and ideas
- Stack Overflow for general Java questions
- Issues for bug reports and feature requests

### Getting Help

- Check existing documentation and issues
- Ask in GitHub Discussions
- Comment on related issues
- Reach out to maintainers if needed

### Staying Updated

- Watch the repository for updates
- Follow release announcements
- Subscribe to security notices

## Recognition

Contributors are recognized in:

- README.md contributors section
- Release notes
- Project documentation

## License

By contributing, you agree that your contributions will be licensed under the same license as the project (Apache 2.0).

## Questions?

- Open a discussion in GitHub Discussions
- Comment on relevant issues
- Contact maintainers directly

---

**Thank you for contributing to Documentor!**

**Version**: 2.1.0
**Last Updated**: December 9, 2025
