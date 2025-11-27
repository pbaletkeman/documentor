# Changelog

All notable changes to this project will be documented in this file.

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
