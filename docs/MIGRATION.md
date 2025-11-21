# Migration Guide

This guide helps you migrate configurations and applications between Documentor versions and from other documentation tools.

## Table of Contents

1. [Version Compatibility](#version-compatibility)
2. [Configuration Migration](#configuration-migration)
3. [Breaking Changes](#breaking-changes)
4. [Migration Examples](#migration-examples)
5. [Troubleshooting](#troubleshooting)

## Version Compatibility

### Supported Versions

- **Current:** v1.0.0+
- **Maintenance Window:** 12 months from release
- **End of Life:** Announced 3 months in advance

### Upgrade Path

```
v0.x → v1.0.0: Breaking changes require full configuration review
v1.x → v1.y: Backward compatible (y > x)
v1.y → v2.0.0: Breaking changes, requires migration guide
```

## Configuration Migration

### From Documentor v0.x to v1.0.0

#### Key Changes

1. **Output format** now defaults to JSON (was XML)
2. **Provider configuration** restructured with explicit type field
3. **Diagram options** consolidated into single object
4. **Thread settings** moved to advanced section

#### Old Format (v0.x)

```json
{
  "source": {
    "type": "file",
    "path": "/src"
  },
  "output_format": "xml",
  "llm": {
    "provider": "openai",
    "api_key": "${OPENAI_API_KEY}",
    "model": "gpt-3.5-turbo"
  },
  "diagrams": {
    "enabled": true,
    "format": "mermaid",
    "include_sequence": true
  },
  "thread_pool_size": 4,
  "timeout_seconds": 30
}
```

#### New Format (v1.0.0)

```json
{
  "source": {
    "type": "file",
    "path": "/src",
    "excludePatterns": [".git", ".mvn", "node_modules"]
  },
  "output": {
    "format": "json",
    "path": "./output",
    "overwrite": true
  },
  "llm": {
    "type": "openai",
    "provider": "openai",
    "apiKey": "${OPENAI_API_KEY}",
    "model": "gpt-3.5-turbo",
    "temperature": 0.7,
    "maxTokens": 2000
  },
  "diagrams": {
    "enabled": true,
    "formats": ["mermaid", "plantuml"],
    "types": ["class", "sequence", "flowchart"],
    "outputPath": "./diagrams"
  },
  "advanced": {
    "threadPoolSize": 4,
    "timeoutSeconds": 30,
    "retryAttempts": 3,
    "enableCache": true
  }
}
```

#### Migration Steps

1. **Update output format:**

   ```bash
   # Old: "output_format": "xml"
   # New: "output": { "format": "json", "path": "./output" }
   ```

2. **Restructure LLM provider:**

   ```bash
   # Old: "provider": "openai", "api_key": "..."
   # New: "type": "openai", "provider": "openai", "apiKey": "..."
   ```

3. **Consolidate diagrams:**

   ```bash
   # Old: "diagrams": { "format": "mermaid", "include_sequence": true }
   # New: "diagrams": { "formats": ["mermaid"], "types": ["sequence"] }
   ```

4. **Move threading options:**
   ```bash
   # Old: "thread_pool_size": 4
   # New: "advanced": { "threadPoolSize": 4 }
   ```

### Configuration Schema Evolution

#### Source Configuration

**v1.0.0 Change:** Added `excludePatterns`

```json
{
  "source": {
    "type": "file",
    "path": "/path/to/source",
    "excludePatterns": [".git", ".mvn", "node_modules", "*.test.js"]
  }
}
```

#### Output Configuration

**v1.0.0 Change:** Restructured to support multiple formats

```json
{
  "output": {
    "format": "json",
    "path": "./documentation",
    "overwrite": true,
    "indent": 2
  }
}
```

**Valid formats:** `json`, `xml`, `markdown`

#### LLM Configuration

**v1.0.0 Changes:**

- Added explicit `type` field (required)
- `apiKey` replaces `api_key` (camelCase)
- Added `temperature` control
- Added `maxTokens` limit

```json
{
  "llm": {
    "type": "openai",
    "provider": "openai",
    "apiKey": "${OPENAI_API_KEY}",
    "model": "gpt-3.5-turbo",
    "temperature": 0.7,
    "maxTokens": 2000
  }
}
```

**Supported types:** `openai`, `ollama`, `llamacpp`

#### Diagram Configuration

**v1.0.0 Changes:**

- `format` → `formats` (array)
- `include_*` → `types` (array)
- Added `outputPath` for diagram output

```json
{
  "diagrams": {
    "enabled": true,
    "formats": ["mermaid", "plantuml"],
    "types": ["class", "sequence", "flowchart", "component"],
    "outputPath": "./diagrams"
  }
}
```

## Breaking Changes

### v1.0.0

#### 1. Output Format Default Changed

**Before:** XML was default
**After:** JSON is default

**Action:** Explicitly set `output.format: "xml"` to maintain XML output

```json
{
  "output": {
    "format": "xml"
  }
}
```

#### 2. Provider Configuration Required Type Field

**Before:** Optional, inferred from provider name
**After:** Required explicit `type` field

```json
{
  "llm": {
    "type": "openai",
    "provider": "openai"
  }
}
```

#### 3. API Key Environment Variable Format

**Before:** `api_key`, `API_KEY`
**After:** `apiKey`, `OPENAI_API_KEY`

**Migrate environment variables:**

```bash
# Old
export DOCUMENTOR_LLM_API_KEY=sk-xxxxx

# New
export OPENAI_API_KEY=sk-xxxxx
```

#### 4. Thread Settings Location

**Before:** Top-level `thread_pool_size`
**After:** Under `advanced.threadPoolSize`

#### 5. Diagram Type References

**Before:** Individual boolean flags (`include_sequence`, `include_class`)
**After:** Array format (`types: ["sequence", "class"]`)

### Deprecated Features (Will be removed in v2.0.0)

| Feature                   | Deprecated Version | Use Instead            |
| ------------------------- | ------------------ | ---------------------- |
| XML output                | v1.0.0             | JSON format            |
| Old LLM provider config   | v1.0.0             | New `type` field       |
| Top-level thread settings | v1.0.0             | `advanced` section     |
| Individual diagram flags  | v1.0.0             | `diagrams.types` array |

## Migration Examples

### Example 1: Simple OpenAI Setup

#### Before (v0.x)

```json
{
  "source": {
    "type": "file",
    "path": "./src"
  },
  "llm": {
    "provider": "openai",
    "api_key": "${OPENAI_API_KEY}",
    "model": "gpt-3.5-turbo"
  },
  "output_format": "json"
}
```

#### After (v1.0.0)

```json
{
  "source": {
    "type": "file",
    "path": "./src",
    "excludePatterns": [".git", "node_modules"]
  },
  "output": {
    "format": "json",
    "path": "./output"
  },
  "llm": {
    "type": "openai",
    "provider": "openai",
    "apiKey": "${OPENAI_API_KEY}",
    "model": "gpt-3.5-turbo"
  }
}
```

### Example 2: Complex Enterprise Setup

#### Before (v0.x)

```json
{
  "source": {
    "type": "file",
    "path": "/enterprise/codebase"
  },
  "llm": {
    "provider": "openai",
    "api_key": "${OPENAI_API_KEY}",
    "model": "gpt-4"
  },
  "diagrams": {
    "enabled": true,
    "format": "plantuml",
    "include_sequence": true,
    "include_class": true,
    "include_flowchart": true
  },
  "output_format": "xml",
  "thread_pool_size": 8,
  "timeout_seconds": 60
}
```

#### After (v1.0.0)

```json
{
  "source": {
    "type": "file",
    "path": "/enterprise/codebase",
    "excludePatterns": [
      ".git",
      ".mvn",
      "node_modules",
      "build",
      "dist",
      ".idea"
    ]
  },
  "output": {
    "format": "json",
    "path": "./enterprise-docs",
    "overwrite": true
  },
  "llm": {
    "type": "openai",
    "provider": "openai",
    "apiKey": "${OPENAI_API_KEY}",
    "model": "gpt-4",
    "temperature": 0.5,
    "maxTokens": 4000
  },
  "diagrams": {
    "enabled": true,
    "formats": ["plantuml"],
    "types": ["sequence", "class", "flowchart"],
    "outputPath": "./enterprise-docs/diagrams"
  },
  "advanced": {
    "threadPoolSize": 8,
    "timeoutSeconds": 60,
    "retryAttempts": 3,
    "enableCache": true
  }
}
```

### Example 3: Local Development with Ollama

#### Before (v0.x)

```json
{
  "source": {
    "type": "file",
    "path": "./src"
  },
  "llm": {
    "provider": "ollama",
    "endpoint": "http://localhost:11434",
    "model": "llama2"
  },
  "diagrams": {
    "enabled": false
  }
}
```

#### After (v1.0.0)

```json
{
  "source": {
    "type": "file",
    "path": "./src"
  },
  "output": {
    "format": "json",
    "path": "./docs"
  },
  "llm": {
    "type": "ollama",
    "provider": "ollama",
    "endpoint": "http://localhost:11434",
    "model": "llama2",
    "temperature": 0.7
  },
  "diagrams": {
    "enabled": false
  },
  "advanced": {
    "enableCache": true
  }
}
```

## Troubleshooting

### Issue: "Invalid configuration schema"

**Cause:** Old configuration format
**Solution:** Ensure all fields match v1.0.0 format

```bash
# Validate configuration
documentor --validate-config config.json
```

### Issue: "Unknown LLM provider type"

**Cause:** Missing or invalid `type` field in LLM config
**Solution:** Add explicit `type` field

```json
{
  "llm": {
    "type": "openai",
    "provider": "openai"
  }
}
```

### Issue: "API key not found"

**Cause:** Using old environment variable name
**Solution:** Update environment variable name

```bash
# Old (not recognized)
export DOCUMENTOR_LLM_API_KEY=sk-xxxxx

# New (correct)
export OPENAI_API_KEY=sk-xxxxx
```

### Issue: "Diagram output path not found"

**Cause:** New `diagrams.outputPath` not created
**Solution:** Create directory or set `overwrite: true`

```json
{
  "diagrams": {
    "outputPath": "./diagrams"
  }
}
```

### Issue: "Configuration migration validation failed"

**Cause:** Incomplete migration from v0.x
**Solution:** Use migration tool

```bash
# Automatic migration
documentor --migrate-config old-config.json > new-config.json

# Review and validate
documentor --validate-config new-config.json
```

## Configuration Validation Tool

Documentor provides built-in validation:

```bash
# Validate configuration file
./gradlew run --args='--validate-config config.json'

# Generate migration from old format
./gradlew run --args='--migrate-config old-config.json'

# Show configuration schema
./gradlew run --args='--show-schema'
```

## Getting Help

- **Configuration Issues:** See [CONFIGURATION.md](./CONFIGURATION.md)
- **API Keys:** See [LLM_INTEGRATIONS.md](./LLM_INTEGRATIONS.md)
- **Docker Migration:** See [DOCKER.md](./DOCKER.md)
- **Issues/Support:** Create an issue on GitHub

## Version Release Schedule

| Version | Release Date | EOL Date | Status  |
| ------- | ------------ | -------- | ------- |
| v0.x    | 2023         | 2024-06  | EOL     |
| v1.0.0  | 2024         | 2025-06  | Current |
| v1.1.0  | TBD          | TBD      | In Dev  |
| v2.0.0  | TBD          | TBD      | Planned |
