---
name: Java 17 LTS Branch Implementation
about: Create Java 17 compatible branch from Java 21 main branch
title: "[BRANCH] Implement Java 17 LTS Branch"
labels: enhancement, java-17, lts, refactoring
assignees: ""
---

## 📋 PRD Reference

Full PRD: `.github/implementation-plans/java-17-lts-prd.md`

## 🎯 Objective

Create a long-term support branch (`java-17-lts`) that is fully compatible with Java 17 by refactoring Java 21-specific features. The `main` branch will continue using Java 21 with modern features (Virtual Threads, Pattern Matching, Sequenced Collections).

## ✅ Success Criteria

- [ ] New branch `java-17-lts` created and pushed to remote
- [ ] All Java 21 syntax successfully refactored to Java 17 equivalents
- [ ] Project compiles successfully with Java 17 JDK
- [ ] All existing tests pass without modification (83%+ coverage maintained)
- [ ] Build artifacts generated with Java 17 target
- [ ] Docker image uses Java 17 base
- [ ] Documentation updated to reflect Java 17 requirements
- [ ] All Checkstyle rules pass for both main and test code
- [ ] No regressions in functionality compared to Java 21 version

## 🔧 Implementation Phases

### Phase 1: Branch Setup

```bash
git status  # Verify clean
git checkout -b java-17-lts
git push -u origin java-17-lts
```

### Phase 2: Build Configuration

**Files:** `build.gradle`, `Dockerfile`

- Update `build.gradle`: Change `JavaLanguageVersion.of(21)` → `of(17)`
- Update `build.gradle`: Change `sourceCompatibility = '21'` → `'17'`
- Update `build.gradle`: Change `targetCompatibility = '21'` → `'17'`
- Update `Dockerfile`: Change `eclipse-temurin:21-jdk-alpine` → `17-jdk-alpine`

**Commit:** `build: Downgrade to Java 17 compatibility`

### Phase 3: Code Refactoring

#### 3.1 Virtual Threads

**Search:**

```bash
findstr /S /I "newVirtualThreadPerTaskExecutor" src\main\java\*.java
findstr /S /I "Thread.ofVirtual" src\main\java\*.java
```

**Replace:** `Executors.newVirtualThreadPerTaskExecutor()` → `Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())`

**Add Comment:** `// Java 17: Standard thread pool (Java 21 used Virtual Threads)`

**Expected Files:**

- `src/main/java/com/documentor/service/CodeAnalysisService.java`

**Commit:** `refactor: Replace Virtual Threads with standard thread pools`

#### 3.2 Switch Pattern Matching

**Search:**

```bash
findstr /S /R "case [A-Z][a-z]*[A-Z]* [a-z]" src\main\java\*.java
```

**Convert:**

```java
// FROM:
switch (node) {
    case MethodDeclaration m -> visitMethod(m);
    case ClassOrInterfaceDeclaration c -> visitClass(c);
}

// TO:
if (node instanceof MethodDeclaration m) {
    visitMethod(m);
} else if (node instanceof ClassOrInterfaceDeclaration c) {
    visitClass(c);
}
```

**Expected Files:**

- `src/main/java/com/documentor/visitor/JavaElementVisitor.java`

**Commit:** `refactor: Convert switch patterns to if/instanceof chains`

#### 3.3 Sequenced Collections

**Search:**

```bash
findstr /S /C:".getFirst()" src\main\java\*.java
findstr /S /C:".getLast()" src\main\java\*.java
findstr /S /C:".addFirst(" src\main\java\*.java
findstr /S /C:".addLast(" src\main\java\*.java
```

**Replace:**

- `.getFirst()` → `.get(0)` (add empty check: `if (!list.isEmpty())`)
- `.getLast()` → `.get(list.size() - 1)` (add empty check)
- `.addFirst(item)` → `.add(0, item)`
- `.addLast(item)` → `.add(item)`

**Commit:** `refactor: Replace Sequenced Collections with List methods`

### Phase 4: Documentation

**Files:** `README.md`, `CHANGELOG.md`

**README.md:**

- Add Java 17 prerequisite
- Add branch comparison table (see PRD section 6.1)

**CHANGELOG.md:**

- Add `## [Java 17 LTS] - 2025-11-26` entry (see PRD section 6.2)

**Commit:** `docs: Update for Java 17 LTS branch`

### Phase 5: Verification

```bash
# Compilation
.\gradlew.bat clean compileJava --warning-mode all

# Tests
.\gradlew.bat test

# Checkstyle
.\gradlew.bat checkstyleMain checkstyleTest

# Full build
.\gradlew.bat build

# Docker
docker build -t documentor:java-17-lts .
docker run --rm documentor:java-17-lts java -version

# Runtime test
java -jar build\libs\documentor.jar --config=samples\config-openai.json
```

### Phase 6: Final Push

```bash
git push origin java-17-lts
```

## 📝 Detailed Specifications

**For complete refactoring specifications, edge cases, and acceptance criteria, see:**
`.github/implementation-plans/java-17-lts-prd.md`

### Key Files to Modify

- `build.gradle` - Java version configuration
- `Dockerfile` - Base image version
- `README.md` - Prerequisites and branch info
- `CHANGELOG.md` - Version entry
- `src/main/java/com/documentor/service/CodeAnalysisService.java` - Virtual Threads
- `src/main/java/com/documentor/visitor/JavaElementVisitor.java` - Switch patterns
- Any files with Sequenced Collections usage

## 🔍 Validation Checklist

- [ ] Branch `java-17-lts` exists on remote
- [ ] All commits follow conventional commit format
- [ ] Build passes: `gradlew.bat build`
- [ ] Tests pass: `gradlew.bat test` (83%+ coverage)
- [ ] Checkstyle passes: `gradlew.bat checkstyleMain checkstyleTest`
- [ ] Docker image builds and runs with Java 17
- [ ] Documentation updated and accurate
- [ ] No Java 21 syntax remains in codebase
- [ ] Functional parity with `main` branch maintained
- [ ] No `IndexOutOfBoundsException` risks introduced
- [ ] All empty list guards added where needed

## 🚨 Important Notes

1. **Do not modify tests** - All existing tests must pass without changes
2. **Maintain cyclomatic complexity** - Keep under Checkstyle limit (< 15)
3. **Add empty list checks** - When replacing `.getFirst()`/`.getLast()`
4. **Document thread pool strategy** - Add comments explaining Java 17 approach
5. **Preserve all functionality** - This is a compatibility refactor, not a rewrite

## 🔄 Rollback Plan

If critical issues are discovered:

```bash
git checkout main
git branch -D java-17-lts
git push origin --delete java-17-lts
```

## 📚 Additional Resources

- Full PRD: `.github/implementation-plans/java-17-lts-prd.md`
- Java 17 Migration Guide: [Oracle Java 17 Documentation](https://docs.oracle.com/en/java/javase/17/)
- Spring Boot 3.x Compatibility: [Spring Boot Documentation](https://spring.io/projects/spring-boot)

---

**Estimated Effort:** 2-4 hours
**Risk Level:** Medium
**Priority:** Normal
**Type:** Enhancement

/cc @copilot
