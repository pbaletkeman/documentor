# 🤝 Contributing Guide

Comprehensive guide for contributing to the Documentor project, covering setup, workflow, testing, and quality standards.

## Table of Contents

- [Getting Started](#getting-started)
- [Development Setup](#development-setup)
- [Project Structure](#project-structure)
- [Coding Standards](#coding-standards)
- [Testing Requirements](#testing-requirements)
- [Git Workflow](#git-workflow)
- [Pull Request Process](#pull-request-process)
- [Reporting Issues](#reporting-issues)
- [Code Review Guidelines](#code-review-guidelines)
- [Performance Considerations](#performance-considerations)

## Getting Started

We welcome contributions to Documentor! Whether you're fixing bugs, adding features, improving documentation, or optimizing performance, your help is appreciated.

## Development Setup

```bash
# Fork the repository on GitHub

# Clone your fork
git clone https://github.com/YOUR-USERNAME/documentor.git
cd documentor

# Add upstream remote
git remote add upstream https://github.com/pbaletkeman/documentor.git

# Create a feature branch
git checkout -b feature/your-feature-name

# Set up pre-commit hooks
cp .githooks/pre-commit .git/hooks/pre-commit
chmod +x .git/hooks/pre-commit

# Build and verify setup
./gradlew build
```

## Contribution Workflow

### 1. Create Feature Branch

```bash
# Branch naming convention
feature/descriptive-name    # New features
bugfix/descriptive-name     # Bug fixes
docs/descriptive-name       # Documentation
refactor/descriptive-name   # Code refactoring
chore/descriptive-name      # Build, dependencies, etc.

# Example
git checkout -b feature/add-typescript-support
```

### 2. Make Changes

```bash
# Follow code style guidelines
# Write comprehensive tests
# Update documentation
# Run quality checks: ./gradlew check
```

### 3. Test Your Changes

```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests YourTestClass

# Verify coverage (minimum 95%)
./gradlew test jacocoTestReport

# Run quality checks
./gradlew check
```

### 4. Commit Changes

```bash
# Use clear, descriptive commit messages
git commit -m "Add TypeScript support for code analysis"

# Format: [type] Description
# Types: feat, fix, docs, style, refactor, test, chore

# Example good messages
git commit -m "feat: Add TypeScript analyzer for .ts files"
git commit -m "fix: Resolve NullPointerException in LLM service"
git commit -m "docs: Update configuration guide with examples"
git commit -m "test: Add comprehensive tests for Python analyzer"
```

### 5. Push and Create Pull Request

```bash
# Push your branch
git push origin feature/your-feature-name

# Create Pull Request on GitHub
# - Clear title describing changes
# - Detailed description of what changed and why
# - Reference related issues (#123)
# - Screenshots for UI changes
```

## Code Style and Standards

### Java Code Style

We follow the **Google Java Style Guide** with automatic enforcement via Checkstyle.

#### Key Guidelines

```java
// Class names: PascalCase
public class CodeAnalysisService {

    // Constant names: UPPER_SNAKE_CASE
    private static final int MAX_THREADS = 10;

    // Variable/method names: camelCase
    private String projectPath;

    public void analyzeProject() {
        // Implementation
    }

    // Meaningful names
    List<CodeElement> elements = analyzeProject();  // ✅ Good
    List<CodeElement> list = analyzeProject();      // ❌ Avoid
}
```

#### JavaDoc Comments

```java
/**
 * Analyzes a Java project and extracts code elements.
 *
 * @param projectPath the path to the Java project
 * @return a ProjectAnalysis containing all discovered elements
 * @throws IOException if project path is inaccessible
 */
public ProjectAnalysis analyzeProject(String projectPath) {
    // Implementation
}
```

### Testing Standards

- Maintain **minimum 95% code coverage**
- Write tests following **AAA pattern** (Arrange, Act, Assert)
- Use **descriptive test names** indicating the scenario
- Mock external dependencies
- Test **edge cases** (null, empty, invalid inputs)

```java
@Test
void testAnalyzeProjectWithValidPath() {
    // Arrange
    String projectPath = "src/test/resources/test-project";
    CodeAnalysisService service = new CodeAnalysisService();

    // Act
    ProjectAnalysis result = service.analyzeProject(projectPath);

    // Assert
    assertNotNull(result);
    assertTrue(result.getElements().size() > 0);
}
```

### Documentation Standards

- Clear, concise descriptions
- Complete API documentation
- Usage examples for complex features
- Keep documentation in sync with code

## Pull Request Guidelines

### PR Title Format

```
[TYPE] Brief description of changes

Examples:
[feat] Add TypeScript support
[fix] Resolve concurrent modification exception
[docs] Update configuration guide
[refactor] Simplify diagram generation logic
```

### PR Description Template

```markdown
## Description

Brief explanation of what this PR does and why.

## Related Issues

Closes #123
Related to #456

## Changes

- Change 1
- Change 2
- Change 3

## Testing

- [x] Added unit tests
- [x] Added integration tests
- [x] Verified 95%+ coverage
- [x] Manual testing completed

## Checklist

- [x] Code follows style guidelines
- [x] Tests pass locally (`./gradlew check`)
- [x] Documentation updated
- [x] No breaking changes
- [x] Performance impact assessed
```

### Merging Requirements

PRs must satisfy:

- ✅ All CI checks pass
- ✅ Code review approval (1+ maintainer)
- ✅ 95%+ test coverage
- ✅ No Checkstyle violations
- ✅ Documentation updated

## Types of Contributions

### Bug Reports

1. **Search existing issues** - Check if already reported
2. **Provide clear description** - What happened vs. expected
3. **Include reproduction steps** - How to reproduce the bug
4. **Add environment details** - OS, Java version, config
5. **Attach logs** - Error messages and stack traces

### Feature Requests

1. **Describe the feature** - What you want to add
2. **Explain the use case** - Why it's needed
3. **Provide examples** - How it would be used
4. **Consider alternatives** - Any other approaches?

### Documentation Improvements

- Fix typos and errors
- Improve clarity
- Add missing examples
- Update outdated information
- Suggest restructuring for better flow

### Performance Improvements

- Profile code to identify bottlenecks
- Document your measurements
- Show before/after performance metrics
- Include tests verifying improvements

## Development Tips

### Build Commands

```bash
# Clean build
./gradlew clean build

# Build without tests (for quick iteration)
./gradlew build -x test

# Run specific test
./gradlew test --tests CodeAnalysisServiceTest

# Check code quality
./gradlew check

# View test coverage report
open build/reports/jacoco/test/html/index.html
```

### IDE Setup

**VS Code**:

```json
{
  "[java]": {
    "editor.defaultFormatter": "redhat.java",
    "editor.formatOnSave": true,
    "editor.codeActionsOnSave": {
      "source.fixAll": true
    }
  }
}
```

**IntelliJ IDEA**:

1. Import code style from `config/checkstyle/checkstyle.xml`
2. Enable Checkstyle plugin
3. Configure pre-commit hooks via Git settings

### Debugging

```bash
# Run with debug logging
export LOGGING_LEVEL_COM_DOCUMENTOR=DEBUG
./gradlew runApp

# VS Code debug configuration (launch.json)
{
  "type": "java",
  "name": "Debug",
  "request": "launch",
  "mainClass": "com.documentor.DocumentorApplication"
}
```

## Review Process

### What Reviewers Look For

1. **Correctness** - Does the code work as intended?
2. **Design** - Is the approach sound and maintainable?
3. **Testing** - Are there comprehensive tests?
4. **Style** - Does it follow guidelines?
5. **Documentation** - Is it clear and complete?
6. **Performance** - Does it have acceptable performance?

### Responding to Reviews

- Address feedback respectfully
- Ask questions if unclear
- Request re-review after changes
- Acknowledge helpful suggestions

## Code of Conduct

- Be respectful and inclusive
- Welcome diverse perspectives
- Provide constructive feedback
- Assume good intent
- Report inappropriate behavior to maintainers

## Becoming a Maintainer

Long-term contributors demonstrating:

- Consistent quality work
- Good judgment
- Community engagement
- Code review skills

May be invited to join the maintainer team.

## Questions or Need Help?

- 📧 **Email**: documentor@letkeman.ca
- 💬 **GitHub Discussions**: Ask questions and discuss ideas
- 🐛 **GitHub Issues**: Report bugs or request features
- 📖 **Documentation**: Check existing guides first

---

## Release Process (Maintainers)

1. Update version in `build.gradle`
2. Update `CHANGELOG.md`
3. Create release branch: `release/v1.2.0`
4. Update README with new features
5. Create Pull Request for release
6. Merge to main branch
7. Create GitHub Release with tag `v1.2.0`
8. Publish to artifact repository

Thank you for contributing to Documentor! 🎉
