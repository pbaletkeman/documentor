# 📚 model - Code Documentation

Generated on: 2025-12-08 18:08:52

This documentation was automatically generated using AI-powered code analysis.

## 📊 Project Statistics

📊 Analysis Summary: 19 total elements (2 classes, 15 methods, 2 fields) across 4 files

| Element Type | Count |
|--------------|-------|
| 📦 Classes | 2 |
| 🔧 Methods | 15 |
| 📊 Fields | 2 |

## 📋 API Reference

### CodeElementType.java

#### 📦 Class/Interface

- **CodeElementType** - `/** * 🔍 Code Element Type Enumeration * * Represents the different types of code elements that can be analyzed: * - CLASS: Classes, interfaces, enums * - METHOD: Methods, functions, procedures * - FIELD: Variables, attributes, constants */ public enum CodeElementType { CLASS("📦", "Class/Interface"), METHOD("🔧", "Method/Function"), FIELD("📊", "Field/Variable"); private final String icon; private final String description; CodeElementType(final String iconParam, final String descriptionParam) { this.icon = iconParam; this.description = descriptionParam; } public String getIcon() { return icon; } public String getDescription() { return description; } }`

#### 🔧 Method/Function

- **getIcon** - `public String getIcon()`
- **getDescription** - `public String getDescription()`

#### 📊 Field/Variable

- **icon** - `private final String icon;`
- **description** - `private final String description;`

### ProjectAnalysis.java

#### 🔧 Method/Function

- **getClasses** - `public List<CodeElement> getClasses()`
- **getMethods** - `public List<CodeElement> getMethods()`
- **getFields** - `public List<CodeElement> getFields()`
- **getElementsByFile** - `public Map<String, List<CodeElement>> getElementsByFile()`
- **getElementsByType** - `public Map<CodeElementType, List<CodeElement>> getElementsByType()`
- **getStats** - `public AnalysisStats getStats()`
- **getFormattedSummary** - `public String getFormattedSummary()`

### CodeElement.java

#### 🔧 Method/Function

- **getId** - `public String getId()`
- **isPublic** - `public boolean isPublic()`
- **getDisplayName** - `public String getDisplayName()`
- **getAnalysisContext** - `public String getAnalysisContext()`

### CodeVisibility.java

#### 📦 Class/Interface

- **CodeVisibility** - `/** * 🔍 Code Visibility Levels - Simplified visibility detection * * Enum to reduce complexity in visibility checking across different languages. */ public enum CodeVisibility { PUBLIC, PROTECTED, PACKAGE_PRIVATE, PRIVATE; /** * 🔍 Determines visibility from signature and element name */ public static CodeVisibility fromSignatureAndName(final String signature, final String name) { String lowerSignature = signature.toLowerCase(); // Check explicit modifiers first if (lowerSignature.contains("private")) { return PRIVATE; } if (lowerSignature.contains("protected")) { return PROTECTED; } if (lowerSignature.contains("public")) { return PUBLIC; } // Check Python conventions if (name.startsWith("_")) { return PRIVATE; } // Default to package-private for Java, public for Python return PACKAGE_PRIVATE; } /** * 🔍 Check if visibility should be included in documentation */ public boolean shouldInclude(final boolean includePrivate) { return includePrivate || this != PRIVATE; } }`

#### 🔧 Method/Function

- **fromSignatureAndName** - `public static CodeVisibility fromSignatureAndName(final String signature, final String name)`
- **shouldInclude** - `public boolean shouldInclude(final boolean includePrivate)`

## 💡 Usage Examples

Detailed usage examples can be found in the individual element documentation files.

### Quick Links

- [CodeElementType](elements/class-CodeElementType.md)
- [CodeVisibility](elements/class-CodeVisibility.md)

