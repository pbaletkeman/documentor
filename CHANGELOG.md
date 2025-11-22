# Changelog

All notable changes to this project will be documented in this file.

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
