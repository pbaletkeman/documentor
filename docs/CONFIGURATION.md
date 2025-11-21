# ⚙️ Configuration Guide

Complete reference for all Documentor configuration options.

## Table of Contents

- [Configuration Structure](#configuration-structure)
- [LLM Models Configuration](#llm-models-configuration)
- [Output Settings](#output-settings)
- [Analysis Settings](#analysis-settings)
- [Dry-Run Mode](#dry-run-mode)
- [File Collision Handling](#file-collision-handling)
- [Best Practices](#best-practices)
- [Examples](#examples)

## Configuration Structure

The configuration file (`config.json`) has three main sections: `llm_models`, `output_settings`, and `analysis_settings`.

## LLM Models Configuration

```json
"llm_models": [
  {
    "name": "model-name",
    "endpoint": "api-endpoint",
    "max_tokens": 4096,
    "temperature": 0.7,
    "timeout_seconds": 30
  }
]
```

| Option            | Type    | Default  | Description                                |
| ----------------- | ------- | -------- | ------------------------------------------ |
| `name`            | String  | Required | Model identifier (gpt-3.5-turbo, llama3.2) |
| `endpoint`        | String  | Required | API endpoint URL                           |
| `max_tokens`      | Integer | 4096     | Maximum tokens in response                 |
| `temperature`     | Float   | 0.7      | Response creativity (0.1-1.0)              |
| `timeout_seconds` | Integer | 30       | Request timeout in seconds                 |
| `api_key`         | String  | Optional | Authentication key (if required)           |

## Output Settings

Controls documentation generation and file output behavior.

```json
"output_settings": {
  "output_directory": "./docs",
  "format": "markdown",
  "generate_unit_tests": true,
  "target_coverage": 0.9,
  "generate_mermaid": true,
  "mermaid_output_path": "./diagrams",
  "generate_plantuml": true,
  "plantuml_output_path": "./uml",
  "mermaid_naming": {
    "prefix": "arch-",
    "suffix": "",
    "extension": "mmd"
  },
  "plantuml_naming": {
    "prefix": "design-",
    "suffix": "_v1",
    "extension": "puml"
  },
  "error_log": "errors.log",
  "output_log": "output.log",
  "overwrite": false,
  "collision_strategy": "timestamp",
  "backup_existing": true
}
```

### Output Settings Options

| Option                 | Type    | Default      | Description                                   |
| ---------------------- | ------- | ------------ | --------------------------------------------- |
| `output_directory`     | String  | `./docs`     | Documentation output directory                |
| `format`               | String  | `markdown`   | Output format (markdown only)                 |
| `include_icons`        | Boolean | `true`       | Include emoji icons                           |
| `generate_unit_tests`  | Boolean | `true`       | Generate test suggestions                     |
| `target_coverage`      | Float   | 0.9          | Test coverage goal (0.0-1.0)                  |
| `generate_mermaid`     | Boolean | `true`       | Generate Mermaid diagrams                     |
| `mermaid_output_path`  | String  | Auto         | Mermaid output directory                      |
| `generate_plantuml`    | Boolean | `false`      | Generate PlantUML diagrams                    |
| `plantuml_output_path` | String  | Auto         | PlantUML output directory                     |
| `error_log`            | String  | `errors.log` | Error log file path                           |
| `output_log`           | String  | `output.log` | Success log file path                         |
| `overwrite`            | Boolean | `false`      | Overwrite existing files                      |
| `collision_strategy`   | String  | `timestamp`  | Collision handling: timestamp/increment/error |
| `backup_existing`      | Boolean | `true`       | Backup files before overwriting               |

### Diagram Naming Configuration

Customize file naming with prefix, suffix, and extension:

```json
"mermaid_naming": {
  "prefix": "arch-",
  "suffix": "_v2",
  "extension": "mmd"
}
```

Example output: `arch-UserService_v2.mmd`

**Allowed characters**: `[0-9a-zA-Z- ()+._]`

## Analysis Settings

Controls code parsing and analysis scope.

```json
"analysis_settings": {
  "include_private_members": false,
  "max_threads": 4,
  "supported_languages": ["java", "python"],
  "exclude_patterns": [
    "**/test/**",
    "**/target/**",
    "**/build/**",
    "**/__pycache__/**",
    "**/node_modules/**"
  ]
}
```

| Option                    | Type    | Default              | Description                 |
| ------------------------- | ------- | -------------------- | --------------------------- |
| `include_private_members` | Boolean | `false`              | Include private members     |
| `max_threads`             | Integer | 4                    | Parallel processing threads |
| `supported_languages`     | Array   | `["java", "python"]` | Languages to analyze        |
| `exclude_patterns`        | Array   | (see above)          | Glob patterns to exclude    |

## Dry-Run Mode

Preview documentation generation without creating files using the `--dry-run` CLI flag.

```bash
documentor> analyze --project-path ./src --config config.json --dry-run true
```

Output example:

```text
📋 DRY RUN: Analysis would generate documentation at: ./docs
📋 DRY RUN: Would create 12 documentation files
📋 DRY RUN: Would create 5 Mermaid diagrams
📋 DRY RUN: No files were actually written
```

### Use Cases for Dry-Run

- **Validation**: Verify analysis completes without errors
- **Preview**: See what would be generated before committing
- **CI/CD Testing**: Test configuration in pipelines safely
- **Troubleshooting**: Debug issues without file side effects

## File Collision Handling

Control behavior when output files already exist.

### Collision Strategies - Timestamp (Default - Recommended)

```json
{
  "overwrite": false,
  "collision_strategy": "timestamp",
  "backup_existing": true
}
```

Creates versioned files: `MyClass_diagram_20251121_143022.mmd`

### Collision Strategies - Increment (Versioning)

```json
{
  "overwrite": true,
  "collision_strategy": "increment",
  "backup_existing": true
}
```

Creates numbered versions: `MyClass_diagram_v1.mmd`, `MyClass_diagram_v2.mmd`

### Collision Strategies - Error (Strict Mode)

```json
{
  "overwrite": false,
  "collision_strategy": "error",
  "backup_existing": false
}
```

Fails if file exists (useful for CI/CD)

## Best Practices

### Security

- ✅ Never commit API keys; use environment variables
- ✅ Use `api_key` from `$LLM_API_KEY` env var
- ✅ Rotate keys regularly

### Performance

- 🚀 Adjust `max_threads` for your system resources
- 🚀 Use `temperature` 0.1-0.3 for consistent outputs, 0.7-1.0 for creative
- 🚀 Set reasonable `timeout_seconds` for your network
- 🚀 Use `exclude_patterns` to skip test/build directories

### Analysis Quality

- 🎯 Include private members for complete documentation
- 🎯 Support multiple languages if applicable
- 🎯 Set appropriate target_coverage for your codebase

## Examples

### Quick Start (Ollama)

```json
{
  "llm_models": [
    { "name": "llama3.2", "endpoint": "http://localhost:11434/api/generate" }
  ],
  "output_settings": { "output_directory": "./docs", "generate_mermaid": true },
  "analysis_settings": { "include_private_members": false }
}
```

### Production (OpenAI)

```json
{
  "llm_models": [
    {
      "name": "gpt-3.5-turbo",
      "endpoint": "https://api.openai.com/v1/chat/completions"
    }
  ],
  "output_settings": {
    "output_directory": "./docs",
    "generate_mermaid": true,
    "generate_plantuml": true
  },
  "analysis_settings": { "include_private_members": true }
}
```

### Diagrams Only

```json
{
  "llm_models": [
    { "name": "codellama", "endpoint": "http://localhost:11434/api/generate" }
  ],
  "output_settings": { "generate_unit_tests": false, "generate_mermaid": true },
  "analysis_settings": { "include_private_members": false }
}
```

### Sample Configurations

All sample configs are in `samples/` directory:

- `config-ollama.json` - Local Ollama setup with all features
- `config-openai.json` - OpenAI GPT configuration
- `config-diagrams-only.json` - Fast diagram generation
- `config-docs-only.json` - Documentation without diagrams
- `config-dryrun-example.json` - Dry-run feature showcase
- `config-collision-strategies.json` - File handling strategies
- `config-unit-test-logging.json` - Unit test configuration
- `config-llamacpp.json` - llama.cpp local setup

## Next Steps

- **[Usage Examples](USAGE_EXAMPLES.md)** - How to run each command
- **[LLM Integrations](LLM_INTEGRATIONS.md)** - Setup for each provider
- **[Diagrams Guide](DIAGRAMS_GUIDE.md)** - Understanding diagrams
