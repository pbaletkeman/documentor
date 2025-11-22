# Release v2.0.2

**Date:** November 21, 2025

## Summary

v2.0.2 focuses on documentation organization and project structure improvements. All critical functionality remains stable and fully tested.

## Key Changes

- Moved documentation files to `docs/` directory for better organization:
  - `COVERAGE_REPORT.md` → `docs/COVERAGE_REPORT.md`
  - `DOCUMENTATION_SUMMARY.md` → `docs/DOCUMENTATION_SUMMARY.md`
  - `FINAL_TESTING_SUMMARY.md` → `docs/FINAL_TESTING_SUMMARY.md`
- Updated all references to documentation files throughout the project
- Maintained all Checkstyle compliance (0 violations)
- All 1206+ tests passing with 94% code coverage

## Build Status

✅ BUILD SUCCESSFUL

- Checkstyle: 0 violations
- Tests: 1206+ passing (100% success rate)
- Code Coverage: 94%
- No compilation errors

## Artifacts

- `documentor.jar` (binary JAR from main branch)

## Verification

Verify this release with:

```bash
gradlew.bat clean build
gradlew.bat clean checkstyleMain checkstyleTest
```

Both commands should complete successfully with 0 errors and 0 violations.

## Release Info

Automated release - v2.0.2
