# Changelog

All notable changes to this project will be documented in this file.

## [v2.1.0] - 2025-12-10

### Added

- **Comprehensive Documentation Suite**:
  - `docs/ARCHITECTURE.md` (550+ lines): Complete system architecture with component layers, data flow diagrams, design patterns, and extension points
  - `docs/SECURITY.md` (450+ lines): Security best practices, API key management, input validation, threat model, and vulnerability reporting
  - `docs/CONTRIBUTING.md` (380+ lines): Community contribution guidelines, development setup, coding standards, testing requirements, and PR process
  - `.github/copilot-instructions.md` (270 lines): AI assistant development guidelines for GitHub Copilot with project patterns, testing standards, and security practices

- **Documentation Index and Organization**:
  - `docs/README.md`: Comprehensive documentation index with role-based quick links (contributors, operations, release management)
  - Organized documentation into logical sections: Getting Started, Configuration, Architecture, Development, Security, Operations, Workflows
  - Added project statistics and support information to documentation index

- **Improved Project README**:
  - Condensed main README.md to 188 lines (from 372 lines)
  - Focused on essentials: Features, Quick Start, Installation, Usage, Configuration
  - Clear links to comprehensive documentation for deeper details
  - ASCII-safe formatting for universal compatibility

- **Enhanced Agents and Workflows Documentation**:
  - Expanded `Agents.md` from 100 to 500+ lines with detailed workflow descriptions
  - Complete release agent process with 9-step release workflow
  - Build and test agent commands with coverage targets and success criteria
  - Configuration and documentation agent guidelines
  - CI/CD patterns and pre-commit hooks setup
  - Troubleshooting procedures for common issues

### Fixed

- **Spring Shell Configuration** (Critical):
  - Disabled Spring Shell globally via `ShellRunnerAutoConfiguration` exclusion
  - Resolved issue where Shell initialization blocked non-interactive CLI mode
  - Applied fix to both main (Java 21) and java-17-lts (Java 17) branches
  - Restored proper CLI behavior for documentation generation

- **Ollama LLM Provider** (Critical):
  - Fixed null pointer exception in `OllamaService` when `requestBuilder` was not initialized
  - Verified Ollama integration with localhost:11434 endpoint
  - Confirmed document generation workflow with llama3.2 model
  - Tested end-to-end with multiple LLM providers (Ollama, LlamaCpp, OpenAI)

- **Test Configuration** (Enhancement):
  - Added proper test configuration files for Ollama and LlamaCpp providers
  - Updated `application-test.yml` with correct Spring Boot configuration
  - Ensured test isolation and reproducibility

### Changed

- **Version Increment**: Updated from v1.1.1 to v2.1.0 (major feature release)
  - `build.gradle`: Updated version field
  - Both main and java-17-lts branches synchronized

- **Documentation Synchronization**:
  - Implemented selective file checkout workflow for cross-branch documentation sync
  - Used `git checkout origin/main -- docs/ README.md Agents.md .github/copilot-instructions.md` for consistent updates
  - Ensured java-17-lts branch receives all documentation updates from main

### Build Quality

- **Code Coverage**: Maintained 80%+ across project (continues from v2.0.2)
- **Checkstyle**: 0 violations (clean code style compliance)
- **Tests**: All unit and integration tests passing
- **Compilation**: 0 errors on both Java 21 (main) and Java 17 (java-17-lts)

### Compatibility

- **Java**: 21 (main branch) and 17 LTS (java-17-lts branch)
- **Spring Boot**: 3.5.6
- **Gradle**: 9.1.0
- **LLM Providers**: Ollama, LlamaCpp, OpenAI (all operational)

### Documentation

- Added 2000+ lines of new documentation across 7 major files
- Created comprehensive documentation index with 15+ linked resources
- Established best practices guides for security, contributing, and architecture
- Documented all development workflows and release procedures

### Notes

- This release focuses on documentation and developer experience
- No breaking API changes from v2.0.2
- Both branches (main/java-17-lts) fully synchronized with identical documentation
- Ready for community contributions with clear guidelines
- Security-first approach with detailed threat model and mitigation strategies

---

## [Java 17 LTS] - 2025-11-27

### Changed

- **Build Configuration**: Downgraded from Java 21 to Java 17 LTS for broader compatibility
  - Updated `build.gradle`: `JavaLanguageVersion.of(21)` → `of(17)`
  - Updated `Dockerfile`: Base images changed from `eclipse-temurin:21-*` to `eclipse-temurin:17-*`

- **Code Refactoring**: Replaced Java 21-specific syntax with Java 17 equivalents
  - Converted switch expressions with arrow syntax to traditional switch statements in:
    - `PythonASTCommandBuilder.java`
    - `MockLlmProviderFactory.java`
    - `AtomicFileWriter.java`
    - `LlmService.java`
    - `LlmServiceEnhanced.java`

### Added

- **GitHub Actions Release Workflows**: Added dedicated release workflows for both Java versions
  - `release-java17.yml`: Builds and releases Java 17 LTS artifacts
  - `release-java21.yml`: Builds and releases Java 21 artifacts
  - Triggered by tags `v*.*.*-java17` and `v*.*.*-java21` or manual dispatch
  - Generates versioned JAR files with SHA256 checksums

### Compatibility

- **Java Version**: Java 17 LTS (minimum)
- **Spring Boot**: 3.5.6 (unchanged)
- **Gradle**: 9.1.0 (unchanged)

### Notes

- This is a compatibility-focused branch maintaining functional parity with the `main` branch
- All existing tests pass without modification
- Checkstyle compliance maintained (0 violations)
- No regressions in functionality

---

## [v2.0.2] - 2025-11-21

### Changed

- Reorganized documentation structure: Moved documentation files to `docs/` directory for better project organization
  - `COVERAGE_REPORT.md` → `docs/COVERAGE_REPORT.md`
  - `DOCUMENTATION_SUMMARY.md` → `docs/DOCUMENTATION_SUMMARY.md`
  - `FINAL_TESTING_SUMMARY.md` → `docs/FINAL_TESTING_SUMMARY.md`
- Updated all internal references to reflect new documentation paths

### Build Quality

- **Checkstyle**: 0 violations (maintained from v2.0.1)
- **Tests**: 1206+ tests passing (100% success rate)
- **Code Coverage**: 94% instruction coverage
- **Compilation**: 0 errors

### Notes

- Non-breaking maintenance release
- All functionality unchanged
- Documentation relocation improves project structure and maintainability

---

## [v2.0.1] - 2025-11-21

### Changed

- Fixed all Checkstyle violations across the repository (LineLength and other issues).
- Reformatted numerous tests to comply with style rules (notable files: `AtomicFileWriterTest.java`, multiple `DocumentationService` tests, CLI handler tests).
- Archived extraneous and temporary test/debug artifacts into `.archived/` and removed malformed stray files from repo root.
- Implemented comprehensive test coverage improvements: BeanUtilsCoverageTest, DiagramServiceConfigurationCoverageTest, DocumentationServiceConfigurationCoverageTest, ConfigValidatorCoverageTest (62 new tests, 1206 total tests passing).

### Test Coverage

- **Overall Coverage**: 94% (917 missed instructions of 16,719 total)
- **Target**: >94 (≤834 missed instructions)
- **Recent Additions**: 62 new unit tests across 4 comprehensive test files
- **Build Quality**: All 1206 tests passing, 0 compilation errors
- **Package Coverage Details**:
  - com.documentor.service: 93% (209 missed)
  - com.documentor.config: 91% (206 missed) — BeanUtils 88%, ConfigValidator coverage improved
  - com.documentor.service.documentation: 95% (192 missed)
  - com.documentor.cli.handlers: 93% (93 missed)
  - com.documentor.model: 100% (0 missed) ✅
  - com.documentor.config.model: 100% (0 missed) ✅

### Notes

- Build verification: run `gradlew.bat clean checkstyleTest` — expected result: BUILD SUCCESSFUL.
- Coverage verification: run `gradlew.bat build` and check `build/reports/jacoco/test/html/index.html` for detailed metrics.
- Artifacts: `documentor.jar` attached to the v2.0.1 GitHub release.

[v2.0.1]: https://github.com/pbaletkeman/documentor/releases/tag/v2.0.1
