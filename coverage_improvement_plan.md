# Test Coverage Improvement Plan

## Current Status

- **Current Coverage**: 60% (1872 covered / 3076 total instructions)
- **Target Coverage**: 90%
- **Instructions to Add**: ~922 instructions need coverage

## Key Areas Needing Coverage

### 1. LlmService (Only 18/406 instructions covered - 4.4%)

**Priority: HIGH - Largest coverage gap**

- `generateDocumentation()` - 0 instructions covered
- `generateUsageExamples()` - 0 instructions covered
- `generateUnitTests()` - 0 instructions covered
- `callLlmModel()` - 0 instructions covered
- All LLM prompt methods - 0 instructions covered

### 2. DocumentorCommands CLI (30/216 instructions covered - 13.9%)

**Priority: HIGH - Core functionality**

- `analyzeProject()` - 0 instructions covered
- `scanProject()` - 0 instructions covered
- Main CLI command methods missing

### 3. PythonCodeAnalyzer (225/513 instructions covered - 43.9%)

**Priority: MEDIUM**

- `shouldInclude()` - 0 instructions covered
- `extractDocstring()` - 0 instructions covered
- `extractParameters()` - 0 instructions covered
- Regex-based analysis methods

### 4. CodeElement Model (30/195 instructions covered - 15.4%)

**Priority: MEDIUM**

- `getId()` - 0 instructions covered
- `isPublic()` - 0 instructions covered
- `getDisplayName()` - 0 instructions covered
- `getAnalysisContext()` - 0 instructions covered

### 5. DocumentationService (722/782 instructions covered - 92.3%)

**Priority: LOW - Already high coverage**

- Minor gaps in file language detection
- Some error handling paths

## Implementation Strategy

### Phase 1: LlmService Tests (Expected +25% coverage)

1. Create comprehensive LlmServiceTest with mocked WebClient
2. Test all LLM generation methods
3. Test error handling and timeout scenarios
4. Test prompt generation methods

### Phase 2: CLI Command Tests (Expected +10% coverage)

1. Extend DocumentorCommandsTest
2. Test analyzeProject with valid/invalid paths
3. Test scanProject functionality
4. Test async operations and error handling

### Phase 3: Model Tests (Expected +8% coverage)

1. Test CodeElement utility methods
2. Test ProjectAnalysis filtering methods
3. Test configuration validation

### Phase 4: Python Analyzer Tests (Expected +7% coverage)

1. Test remaining Python parsing methods
2. Test docstring extraction
3. Test parameter parsing

## Expected Outcome

- Phase 1: 60% → 85%
- Phase 2: 85% → 95%
- **Final Coverage**: 95% (exceeds 90% target)
