# CLI Options Verification Test Plan

## Overview

This document outlines the test plan for verifying all CLI options/switches work correctly against the Documentor project.

## Test Environment

- **Project**: documentorJava
- **Output Directory**: test-verification-temp
- **Test Date**: 2025-12-08

## Pre-Test Checklist

- [x] Clean build environment
- [x] Run `gradlew.bat clean checkstyleMain checkstyleTest`
- [x] Verify no checkstyle issues
- [x] Run `gradlew.bat test`
- [x] All tests passing

## CLI Commands to Test

### 1. Status Command

**Command**: `status`
**Purpose**: Display current application status
**Expected Output**: System information, current settings
**Test Command**:

```bash
echo status | gradlew.bat bootRun
```

**Output Location**: test-verification-temp/status-output.log
**Status**: ⏳ Pending

---

### 2. Info Command

**Command**: `info`
**Purpose**: Show supported file types and features
**Expected Output**: List of supported languages, file types, features
**Test Command**:

```bash
echo info | gradlew.bat bootRun
```

**Output Location**: test-verification-temp/info-output.log
**Status**: ⏳ Pending

---

### 3. Quick Start Command

**Command**: `quick-start`
**Purpose**: Display quick start guide
**Expected Output**: Getting started instructions
**Test Command**:

```bash
echo quick-start | gradlew.bat bootRun
```

**Output Location**: test-verification-temp/quickstart-output.log
**Status**: ⏳ Pending

---

### 4. Validate Config Command

**Command**: `validate-config`
**Purpose**: Validate configuration file syntax and settings
**Test Command**:

```bash
echo validate-config --config=config.json | gradlew.bat bootRun
```

**Expected Output**: Configuration validation results
**Output Location**: test-verification-temp/validate-output.log
**Status**: ⏳ Pending

---

### 5. Scan Project Command (analyze-only)

**Command**: `scan` or `analyze-only`
**Purpose**: Analyze project structure without generating documentation
**Test Command**:

```bash
echo scan --project-path=src/main/java/com/documentor/model | gradlew.bat bootRun
```

**Expected Output**: Project statistics, file counts, class counts
**Output Location**: test-verification-temp/scan-output.log
**Status**: ⏳ Pending

---

### 6. Analyze Command (Full Analysis)

**Command**: `analyze` or `generate`
**Purpose**: Full project analysis with documentation generation
**Test Options**:

- `--project-path`: Path to project
- `--config`: Configuration file path
- `--include-private-members`: Include private members (default: true)
- `--generate-mermaid`: Generate Mermaid diagrams (default: false)
- `--mermaid-output`: Mermaid output directory
- `--generate-plantuml`: Generate PlantUML diagrams (default: false)
- `--plantuml-output`: PlantUML output directory
- `--dry-run`: Preview without writing files (default: false)

**Test Command**:

```bash
echo analyze --project-path=src/main/java/com/documentor/model --config=config.json --dry-run=true | gradlew.bat bootRun
```

**Expected Output**: Analysis results, file processing info (no files written in dry-run)
**Output Location**: test-verification-temp/analyze-output.log
**Status**: ⏳ Pending

---

### 7. Dry Run Mode Test

**Command**: `analyze` with `--dry-run=true`
**Purpose**: Preview changes without writing files
**Test Command**:

```bash
echo analyze --project-path=src/main/java/com/documentor/model --config=config.json --dry-run=true | gradlew.bat bootRun
```

**Expected Output**: Preview of what would be generated (no files created)
**Output Location**: test-verification-temp/dryrun-output.log
**Verification**: Confirm no files were created in project
**Status**: ⏳ Pending

---

### 8. PlantUML Generation

**Command**: `plantuml` or `puml`
**Purpose**: Generate PlantUML class diagrams only
**Test Options**:

- `--project-path`: Path to project
- `--include-private-members`: Include private members (default: true)
- `--plantuml-output`: Output directory for diagrams
- `--dry-run`: Preview mode (default: false)

**Test Command**:

```bash
echo plantuml --project-path=src/main/java/com/documentor/model --plantuml-output=test-verification-temp/plantuml-diagrams --include-private-members=true | gradlew.bat bootRun
```

**Expected Output**: PlantUML diagram files (.puml)
**Output Location**: test-verification-temp/plantuml-output.log
**Diagram Location**: test-verification-temp/plantuml-diagrams/
**Status**: ⏳ Pending

---

### 9. Mermaid Diagram Generation

**Command**: `analyze` with `--generate-mermaid=true`
**Purpose**: Generate Mermaid class diagrams
**Test Command**:

```bash
echo analyze --project-path=src/main/java/com/documentor/model --config=config.json --generate-mermaid=true --mermaid-output=test-verification-temp/mermaid-diagrams --dry-run=true | gradlew.bat bootRun
```

**Expected Output**: Mermaid diagram files (.mmd)
**Output Location**: test-verification-temp/mermaid-output.log
**Diagram Location**: test-verification-temp/mermaid-diagrams/ (dry-run: preview only)
**Status**: ⏳ Pending

---

### 10. Analyze with Fix Command

**Command**: `analyze-with-fix` or `fixed-analyze`
**Purpose**: Analyze with ThreadLocal configuration fix
**Test Command**:

```bash
echo analyze-with-fix --project-path=src/main/java/com/documentor/model --config=config.json | gradlew.bat bootRun
```

**Expected Output**: Analysis with enhanced configuration handling
**Output Location**: test-verification-temp/fix-analyze-output.log
**Status**: ⏳ Pending

---

### 11. Include Private Members Test

**Command**: `analyze` with `--include-private-members=false`
**Purpose**: Test filtering of private members
**Test Command**:

```bash
echo analyze --project-path=src/main/java/com/documentor/model --config=config.json --include-private-members=false --dry-run=true | gradlew.bat bootRun
```

**Expected Output**: Documentation without private members
**Output Location**: test-verification-temp/no-private-output.log
**Status**: ⏳ Pending

---

### 12. Multiple Output Directories Test

**Command**: Test separate output directories for different diagram types
**Test Command**:

```bash
echo analyze --project-path=src/main/java/com/documentor/model --config=config.json --generate-mermaid=true --mermaid-output=test-verification-temp/mermaid --generate-plantuml=true --plantuml-output=test-verification-temp/plantuml --dry-run=true | gradlew.bat bootRun
```

**Expected Output**: Preview showing separate output paths
**Output Location**: test-verification-temp/multi-output.log
**Status**: ⏳ Pending

---

## Verification Steps

### For Each Test:

1. Run the test command
2. Check output log for errors
3. Verify expected output is present
4. For file generation tests, verify files are created in correct location
5. For dry-run tests, verify NO files are created
6. Update status to ✅ if passed or ❌ if failed

### Post-Test Verification:

1. Run `gradlew.bat clean checkstyleMain checkstyleTest` again
2. Run `gradlew.bat test` again
3. Verify all tests still pass
4. Clean up test-verification-temp directory

## Manual Test Execution

To run individual tests manually:

```bash
# Create temp directory
mkdir test-verification-temp

# Test 1: Status
echo status > test-verification-temp\test-status.txt
echo exit >> test-verification-temp\test-status.txt
gradlew.bat bootRun < test-verification-temp\test-status.txt > test-verification-temp\status-output.log 2>&1

# Test 2: Info
echo info > test-verification-temp\test-info.txt
echo exit >> test-verification-temp\test-info.txt
gradlew.bat bootRun < test-verification-temp\test-info.txt > test-verification-temp\info-output.log 2>&1

# Test 3: Quick-start
echo quick-start > test-verification-temp\test-quickstart.txt
echo exit >> test-verification-temp\test-quickstart.txt
gradlew.bat bootRun < test-verification-temp\test-quickstart.txt > test-verification-temp\quickstart-output.log 2>&1

# Test 4: Validate config
echo validate-config --config=config.json > test-verification-temp\test-validate.txt
echo exit >> test-verification-temp\test-validate.txt
gradlew.bat bootRun < test-verification-temp\test-validate.txt > test-verification-temp\validate-output.log 2>&1

# Test 5: Scan
echo scan --project-path=src/main/java/com/documentor/model > test-verification-temp\test-scan.txt
echo exit >> test-verification-temp\test-scan.txt
gradlew.bat bootRun < test-verification-temp\test-scan.txt > test-verification-temp\scan-output.log 2>&1

# Test 6: Dry-run
echo analyze --project-path=src/main/java/com/documentor/model --config=config.json --dry-run=true > test-verification-temp\test-dryrun.txt
echo exit >> test-verification-temp\test-dryrun.txt
gradlew.bat bootRun < test-verification-temp\test-dryrun.txt > test-verification-temp\dryrun-output.log 2>&1

# Test 7: PlantUML
echo plantuml --project-path=src/main/java/com/documentor/model --plantuml-output=test-verification-temp\plantuml-diagrams > test-verification-temp\test-plantuml.txt
echo exit >> test-verification-temp\test-plantuml.txt
gradlew.bat bootRun < test-verification-temp\test-plantuml.txt > test-verification-temp\plantuml-output.log 2>&1
```

## Automated Test Script

Use the provided `verify-cli-options.bat` script to run all tests automatically:

```bash
verify-cli-options.bat
```

## Test Results Summary

| Test # | Command                       | Status | Notes   |
| ------ | ----------------------------- | ------ | ------- |
| 1      | status                        | ⏳     | Pending |
| 2      | info                          | ⏳     | Pending |
| 3      | quick-start                   | ⏳     | Pending |
| 4      | validate-config               | ⏳     | Pending |
| 5      | scan                          | ⏳     | Pending |
| 6      | analyze (dry-run)             | ⏳     | Pending |
| 7      | plantuml                      | ⏳     | Pending |
| 8      | mermaid                       | ⏳     | Pending |
| 9      | analyze-with-fix              | ⏳     | Pending |
| 10     | include-private-members=false | ⏳     | Pending |
| 11     | multi-output                  | ⏳     | Pending |

## Issues Found

### Checkstyle Issues

- [x] Fixed: PlantUMLDiagramServiceFastTest.java - Parameter tempDir should be final

### Test Failures

- None

### Build Issues

- None

## Conclusion

- **Checkstyle Status**: ✅ Passing
- **Test Status**: ✅ All tests passing
- **Build Status**: ✅ Successful
- **CLI Options**: ⏳ Ready for manual verification

## Next Steps

1. Run manual tests for each CLI option
2. Update test results table
3. Document any issues found
4. Create fixes for any broken functionality
5. Re-run checkstyle and tests after fixes
6. Final verification

## Notes

- This is a Gradle project, not Maven (note: user mentioned `mvn clean checkstylemain` but should use `gradlew.bat clean checkstyleMain`)
- All output files are placed in `test-verification-temp` directory
- Dry-run mode should NOT create any files
- Each diagram type can have its own output directory
