# Agents and Workflows - Documentor

This document describes the agent patterns and automated workflows used in the Documentor project for development, testing, and release management.

## Table of Contents

- [Overview](#overview)
- [Release Agent](#release-agent)
- [Build Agent](#build-agent)
- [Test Agent](#test-agent)
- [Configuration Agent](#configuration-agent)
- [Documentation Agent](#documentation-agent)

## Overview

An "agent" in this project is a reproducible workflow implemented as CLI commands or scripts. These workflows automate common development tasks.

**Goals**:

- Standardize development procedures
- Reduce manual errors
- Enable reproducible builds and releases
- Document best practices as executable workflows

## Release Agent

The release agent manages version tagging, artifact building, and GitHub release publication.

### Process

**Step 1: Create a Release Tag**

```bash
git tag -a vX.Y.Z -m "Release vX.Y.Z"
git push origin vX.Y.Z
```

**Step 2: Build and Verify**

```bash
./gradlew.bat clean checkstyleMain checkstyleTest
./gradlew.bat build
```

**Step 3: Create Release on GitHub**

```bash
gh release create vX.Y.Z \
  --title "Release vX.Y.Z" \
  --notes-file release_notes_vX.Y.Z.md \
  build/libs/documentor.jar
```

### Release Notes Files

- `release_notes_vX.Y.Z.md` - Short summary for GitHub release
- `detailed_release_notes_vX.Y.Z.md` - Extended notes for internal review
- `CHANGELOG.md` - Canonical changelog in repo root

### Example Release Workflow

```bash
# 1. Update version in build.gradle
# 2. Update CHANGELOG.md
# 3. Create release notes
# 4. Commit changes
git add build.gradle CHANGELOG.md release_notes_v2.1.0.md
git commit -m "Release: Prepare v2.1.0"

# 5. Create tag
git tag -a v2.1.0 -m "Release v2.1.0"
git push origin main
git push origin v2.1.0

# 6. Build artifacts
./gradlew.bat clean checkstyleMain checkstyleTest
./gradlew.bat build

# 7. Verify build succeeded
ls -la build/libs/documentor.jar

# 8. Create GitHub release
gh release create v2.1.0 \
  --title "Documentor v2.1.0" \
  --notes-file release_notes_v2.1.0.md \
  build/libs/documentor.jar

# 9. Publish release
gh release edit v2.1.0 --draft=false
```

## Build Agent

The build agent manages compilation, testing, and style validation.

### Commands

```bash
# Full clean build with tests and style checks
./gradlew.bat clean build

# Build without running tests
./gradlew.bat build -x test

# Rebuild specific module
./gradlew.bat :module-name:build

# Build with specific test suite
./gradlew.bat testUnit
./gradlew.bat testAll

# Check code style (must pass before release)
./gradlew.bat checkstyleMain checkstyleTest

# Generate code coverage report
./gradlew.bat jacocoTestReport

# View coverage in browser
# Open: build/reports/jacoco/test/html/index.html
```

### Pre-Release Build Checklist

Before committing changes, ensure:

```bash
# 1. Run clean build
./gradlew.bat clean build

# 2. Verify test coverage (must be 80%+)
./gradlew.bat jacocoTestReport

# 3. Check code style
./gradlew.bat checkstyleMain checkstyleTest

# 4. Run full test suite
./gradlew.bat test

# 5. Build with no errors
./gradlew.bat build -x test
```

## Test Agent

The test agent manages test execution and coverage verification.

### Test Commands

```bash
# Run all tests
./gradlew.bat test

# Run specific test class
./gradlew.bat test --tests DocumentationServiceTest

# Run tests matching pattern
./gradlew.bat test --tests "*Enhanced*"

# Run unit tests only
./gradlew.bat testUnit

# Run all integration tests
./gradlew.bat testAll

# Run with debug output
./gradlew.bat test --info

# Generate test report
./gradlew.bat test
# View: build/reports/tests/test/index.html

# Generate coverage report
./gradlew.bat jacocoTestReport
# View: build/reports/jacoco/test/html/index.html
```

### Test Coverage Targets

- **Overall**: 80%+ code coverage
- **Core Services**: 85%+ coverage
- **Configuration**: 80%+ coverage
- **Utilities**: 75%+ coverage

### Coverage Verification

```bash
# Generate coverage report
./gradlew.bat jacocoTestReport

# View detailed coverage
# build/reports/jacoco/test/html/index.html

# Check if coverage meets minimum
./gradlew.bat jacocoTestCoverageVerification

# View coverage summary
./gradlew.bat -i test jacocoTestReport 2>&1 | grep -i "coverage"
```

## Configuration Agent

The configuration agent manages application configuration and environment setup.

### Configuration Files

**Main Configuration**:

```bash
# Create configuration for Ollama
cat > config-ollama.json << 'EOF'
{
  "llm_models": [
    {
      "name": "llama3.2",
      "baseUrl": "http://localhost:11434",
      "apiKey": "not-needed",
      "maxTokens": 2048,
      "timeoutSeconds": 60
    }
  ],
  "output_settings": {
    "output_directory": "ollama-docs",
    "format": "markdown"
  }
}
EOF
```

**Spring Boot Configuration**:

- Location: `src/main/resources/application.yml`
- Test Config: `src/main/resources/application-test.yml`
- Profiles: test, dev, production

### Environment Setup

```bash
# Set up Ollama provider
export OLLAMA_BASE_URL="http://localhost:11434"

# Set up OpenAI provider
export OPENAI_API_KEY="sk-..."

# Set up LlamaCpp provider
export LLAMACPP_BASE_URL="http://localhost:8000"

# Run with specific profile
java -jar documentor.jar --spring.profiles.active=test

# Override configuration
java -jar documentor.jar \
  --server.port=8081 \
  --spring.profiles.active=production
```

## Documentation Agent

The documentation agent manages documentation generation and maintenance.

### Documentation Files

**Core Documentation**:

- `README.md` - Project overview (150 lines max, with TOC)
- `docs/README.md` - Documentation index
- `docs/ARCHITECTURE.md` - System architecture
- `docs/SECURITY.md` - Security guidelines
- `docs/CONTRIBUTING.md` - Contribution guide
- `Agents.md` - This file
- `copilot-instructions.md` - AI assistant guidelines

**Generated Documentation**:

```bash
# Generate full documentation
java -jar documentor.jar --config config-ollama.json

# Generate and review output
ls -la ollama-docs/
```

### Documentation Update Workflow

```bash
# 1. Create/edit documentation file
vim docs/NEW_FEATURE.md

# 2. Verify markdown syntax
# Check for linting issues (if using markdownlint)

# 3. Link from main index
# Add reference to docs/README.md

# 4. Commit changes
git add docs/NEW_FEATURE.md docs/README.md
git commit -m "docs: Add new feature documentation"

# 5. Push to main
git push origin feature-branch

# 6. Create pull request
gh pr create --title "Docs: Add new feature documentation"
```

### Documentation Synchronization

Keep documentation in sync across branches:

```bash
# Sync docs from main to java-17-lts
git checkout java-17-lts
git merge origin/main -- docs/

# Commit sync
git commit -m "Sync: Update documentation from main"

# Push to branch
git push origin java-17-lts
```

## Continuous Integration Patterns

### GitHub Actions Workflow

The project uses GitHub Actions for automated testing and validation:

```yaml
name: CI/CD Pipeline

on: [push, pull_request]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Set up Java
        uses: actions/setup-java@v3
        with:
          java-version: "21"
          distribution: "temurin"
      - name: Build with Gradle
        run: ./gradlew clean build
      - name: Run Tests
        run: ./gradlew test
      - name: Check Style
        run: ./gradlew checkstyle
```

### Pre-Commit Hooks

Set up local pre-commit validation:

```bash
#!/bin/bash
# .git/hooks/pre-commit

echo "Running pre-commit checks..."

# Format check
./gradlew checkstyleMain checkstyleTest
if [ $? -ne 0 ]; then
    echo "Style check failed. Run: ./gradlew checkstyleMain checkstyleTest"
    exit 1
fi

# Test check
./gradlew test
if [ $? -ne 0 ]; then
    echo "Tests failed. Run: ./gradlew test"
    exit 1
fi

echo "Pre-commit checks passed!"
exit 0
```

## Troubleshooting Agents

### Build Failures

```bash
# Clean and rebuild
./gradlew.bat clean build -x test

# Check for stale dependencies
./gradlew.bat clean

# Update Gradle wrapper
./gradlew.bat wrapper --gradle-version=9.1.0
```

### Test Failures

```bash
# Run single test with output
./gradlew.bat test --tests FailingTest --info

# Debug test
./gradlew.bat test --debug-jvm

# Rerun failed tests
./gradlew.bat test --rerun-tasks
```

### Configuration Issues

```bash
# Validate configuration
java -jar documentor.jar --validate-config config.json

# Check configuration schema
cat src/main/resources/schema/config-schema.json

# Test with minimal config
java -jar documentor.jar --config config-minimal.json
```

## Best Practices

### Release Checklist

- [ ] Version updated in `build.gradle`
- [ ] `CHANGELOG.md` updated with release notes
- [ ] Release notes files created
- [ ] Full test suite passes
- [ ] Code style checks pass
- [ ] Coverage maintained at 80%+
- [ ] Documentation updated and reviewed
- [ ] Tag created and pushed
- [ ] GitHub release created with artifacts
- [ ] Release tested in staging environment

### Documentation Checklist

- [ ] File follows markdown standards
- [ ] Headers use proper hierarchy (# > ## > ###)
- [ ] Code blocks include language specification
- [ ] Links are relative and valid
- [ ] Table of contents is current
- [ ] Examples are tested and working
- [ ] No broken references
- [ ] Consistent formatting and style

### Code Quality Checklist

- [ ] All tests pass
- [ ] Code coverage >= 80%
- [ ] No style warnings
- [ ] No security issues
- [ ] No deprecated API usage
- [ ] Comprehensive comments for complex logic
- [ ] Error handling implemented
- [ ] Logging implemented

## Related Documentation

- [Contributing Guide](CONTRIBUTING.md) - How to contribute to the project
- [Architecture Guide](docs/ARCHITECTURE.md) - System design and components
- [Security Guide](docs/SECURITY.md) - Security best practices
- [Development Guide](docs/DEVELOPMENT.md) - Local development setup

---

**Version**: 2.1.0
**Last Updated**: December 9, 2025

These are documented as reproducible CLI steps (the "agent" is a human or script that runs them):

Create a release tag locally:

```bash
git tag -a vX.Y.Z -m "Release vX.Y.Z"
git push origin vX.Y.Z
```

Build and produce artifacts (runs Checkstyle first):

```bash
gradlew.bat clean checkstyleMain checkstyleTest
gradleMaint build
```

Upload artifact(s) to GitHub release and set notes:

```bash
gh release upload vX.Y.Z build\\libs\\documentor.jar --clobber
gh release edit vX.Y.Z --notes-file detailed_release_notes_vX.Y.Z.md --publish
```

# Local CLI helpers

Always run the clean Checkstyle build before finalizing releases to avoid style regressions:

```bash
gradlew.bat clean checkstyleMain checkstyleTest
```

# Release notes and changelog

- `release_notes_vX.Y.Z.md` — short release summary for the GitHub release body.
- `detailed_release_notes_vX.Y.Z.md` — extended notes for internal review before publishing.
- `CHANGELOG.md` — canonical changelog in the repo root.

# Publishing checklist

1. Commit documentation changes (`CHANGELOG.md`, `docs/DOCUMENTATION_SUMMARY.md`, release note files).
2. Run `gradlew.bat clean checkstyleMain checkstyleTest && gradlew.bat build` and confirm `build/libs/documentor.jar` exists.
3. Create and push annotated tag, create or edit GitHub release, upload artifact(s), then publish.

# Contact

See `README.md` for maintainer contact information.

- Provide a brief reference for contributors and maintainers about release and helper workflows.

## Agents and workflows

- Release agent: helper steps that create tags, build artifacts, and upload them to GitHub Releases using the `gh` CLI and the Gradle wrapper (`gradlew.bat`).

Usage summary:

- Create a release tag locally: `git tag -a vX.Y.Z -m "Release vX.Y.Z"`
- Push tag to origin: `git push origin vX.Y.Z`
- Build artifact: `gradlew.bat clean build`
- Upload artifact to release: `gh release upload vX.Y.Z build\\libs\\documentor.jar --clobber`
- Edit release notes or set draft/publish: `gh release edit vX.Y.Z --notes-file detailed_release_notes_vX.Y.Z.md --publish`

## Local CLI helpers

- Always run the clean Checkstyle build before finalizing releases:

```bash
gradlew.bat clean checkstyleMain checkstyleTest
```

This helps prevent style regressions and ensures the project follows defined rules.

## Release notes and changelog

- `release_notes_vX.Y.Z.md` — concise release summary used for GitHub release notes.
- `detailed_release_notes_vX.Y.Z.md` — extended notes for reviewers and drafts.
- `CHANGELOG.md` — canonical changelog kept in the repo root.

## Reviewing and publishing releases

1. Ensure the working tree is clean and documentation changes are committed.
2. Verify build and style: `gradlew.bat clean checkstyleMain checkstyleTest && gradlew.bat build`.
3. Create and push tag, create/edit the release, upload artifacts, then publish the release when ready.

## Contact

For questions about the release flow or helper scripts, see `README.md` or contact the maintainers listed there.
