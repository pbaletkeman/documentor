# ⚙️ Configuration Guide

Complete reference for all Documentor configuration options.

## Configuration Structure

The configuration file (typically `config.json`) is divided into three main sections:

1. **llm_models** - LLM model configuration and API settings
2. **output_settings** - Documentation generation preferences
3. **analysis_settings** - Code analysis behavior and filtering

## LLM Models Configuration

The `llm_models` section defines the LLM providers you want to use.

### General LLM Configuration

```json
"llm_models": [
  {
    "name": "model-name",
    "api_key": "your-api-key",
    "endpoint": "api-endpoint",
    "max_tokens": 4096,
    "temperature": 0.7,
    "timeout_seconds": 30,
    "additional_config": {}
  }
]
```

### Configuration Options Reference

| Option              | Type        | Default  | Description                                   |
| ------------------- | ----------- | -------- | --------------------------------------------- |
| `name`              | String      | Required | Model identifier (gpt-3.5-turbo, llama3.2)    |
| `api_key`           | String      | Optional | Authentication key (required for OpenAI)      |
| `endpoint`          | String      | Required | API endpoint URL                              |
| `max_tokens`        | Integer     | 4096     | Maximum tokens in response                    |
| `temperature`       | Float 0-1.0 | 0.7      | Response creativity (lower = more consistent) |
| `timeout_seconds`   | Integer     | 30       | Request timeout in seconds                    |
| `additional_config` | Object      | {}       | Provider-specific settings                    |

## Output Settings Configuration

Controls documentation generation behavior and output formats.

### Complete Output Settings Reference

```json
"output_settings": {
  "output_directory": "./docs",
  "format": "markdown",
  "include_icons": true,
  "generate_unit_tests": true,
  "run_unit_test_commands": false,
  "log_unit_test_commands": true,
  "target_coverage": 0.9,
  "generate_mermaid": true,
  "mermaid_output_path": "./diagrams",
  "generate_plantuml": true,
  "plantuml_output_path": "./uml",
  "verbose_output": false
}
```

### Output Settings Options

| Option                   | Type    | Default      | Description                               |
| ------------------------ | ------- | ------------ | ----------------------------------------- |
| `output_directory`       | String  | `./docs`     | Directory for documentation output        |
| `format`                 | String  | `markdown`   | Output format (currently markdown only)   |
| `include_icons`          | Boolean | `true`       | Include emoji icons in documentation      |
| `generate_unit_tests`    | Boolean | `true`       | Generate unit test suggestions            |
| `run_unit_test_commands` | Boolean | `false`      | Automatically run generated test commands |
| `log_unit_test_commands` | Boolean | `true`       | Log test commands to file                 |
| `target_coverage`        | Float   | `0.9`        | Target test coverage (0.0-1.0)            |
| `generate_mermaid`       | Boolean | `true`       | Generate Mermaid class diagrams           |
| `mermaid_output_path`    | String  | `./diagrams` | Output directory for Mermaid diagrams     |
| `generate_plantuml`      | Boolean | `false`      | Generate PlantUML class diagrams          |
| `plantuml_output_path`   | String  | `./uml`      | Output directory for PlantUML diagrams    |
| `verbose_output`         | Boolean | `false`      | Include more detailed information in logs |

### Diagram File Naming

Customize how diagram files are named using prefix, suffix, and extension options. Each diagram type (Mermaid and PlantUML) can have independent naming configuration.

#### Configuration Structure

```json
{
  "output_settings": {
    "mermaid_naming": {
      "prefix": "arch-",
      "suffix": "",
      "extension": "md"
    },
    "plantuml_naming": {
      "prefix": "2025-API-",
      "suffix": "_v2",
      "extension": "uml"
    },
    "error_log": "errors.log",
    "output_log": "out.log"
  }
}
```

#### Naming Options

| Option       | Type   | Max Length | Description                        | Example      |
| ------------ | ------ | ---------- | ---------------------------------- | ------------ |
| `prefix`     | String | 20         | Text prepended to file name        | `arch-`      |
| `suffix`     | String | 20         | Text appended to file name         | `_v2`        |
| `extension`  | String | 10         | File extension (without dot)       | `uml`        |
| `error_log`  | String | -          | Path for error log file            | `errors.log` |
| `output_log` | String | -          | Path for successful generation log | `out.log`    |

#### Allowed Characters

File names must use valid UTF-8 characters from this set: `[0-9a-zA-Z- ()+._]`

**Valid Examples:**

- `2025-API-`
- `arch (v2)`
- `legacy_old`
- `v1.0+beta`

**Invalid Examples:**

- `api@#$` (contains special characters)
- `test!service` (contains exclamation mark)

#### Error Handling

- **Invalid prefix/suffix**: Ignored, operation continues with default naming
- **Invalid extension**: Defaults to `mmd` (Mermaid) or `plantuml` (PlantUML)
- **All errors**: Logged to `error_log` path (default: `errors.log`)
- **Successful generations**: Logged to `output_log` path (default: `out.log`)

#### Default Values

When naming options are not specified:

- **Mermaid diagrams**: `ClassName_diagram.mmd`
- **PlantUML diagrams**: `ClassName_plantuml.puml`

This maintains backward compatibility with existing projects.

#### Examples

**Example 1: Project Versioning**

```json
{
  "plantuml_naming": {
    "prefix": "v2.0-",
    "suffix": "_final",
    "extension": "puml"
  }
}
```

Result: `v2.0-UserService_final.puml`

**Example 2: Architecture Documentation**

```json
{
  "mermaid_naming": {
    "prefix": "arch-",
    "suffix": "",
    "extension": "md"
  }
}
```

Result: `arch-DataFlow.md`

**Example 3: Different Naming Per Type**

```json
{
  "mermaid_naming": {
    "prefix": "backend-",
    "extension": "mmd"
  },
  "plantuml_naming": {
    "prefix": "api-",
    "suffix": "_design",
    "extension": "uml"
  }
}
```

Results:

- Mermaid: `backend-OrderService.mmd`
- PlantUML: `api-OrderService_design.uml`

## Analysis Settings Configuration

Controls code parsing behavior and analysis scope.

### Complete Analysis Settings Reference

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

### Analysis Settings Options

| Option                    | Type         | Default              | Description                         |
| ------------------------- | ------------ | -------------------- | ----------------------------------- |
| `include_private_members` | Boolean      | `false`              | Include private fields and methods  |
| `max_threads`             | Integer      | `4`                  | Maximum parallel processing threads |
| `supported_languages`     | String Array | `["java", "python"]` | Languages to analyze                |
| `exclude_patterns`        | String Array | (see below)          | Glob patterns to exclude            |

### Default Exclude Patterns

```json
[
  "**/test/**",
  "**/target/**",
  "**/build/**",
  "**/__pycache__/**",
  "**/node_modules/**",
  "**/.git/**",
  "**/.gradle/**"
]
```

## Common Configuration Templates

### Minimal Configuration

```json
{
  "llm_models": [
    {
      "name": "llama3.2",
      "endpoint": "http://localhost:11434/api/generate",
      "max_tokens": 4096,
      "temperature": 0.7,
      "timeout_seconds": 60
    }
  ],
  "output_settings": {
    "output_directory": "./docs",
    "format": "markdown",
    "generate_unit_tests": true
  },
  "analysis_settings": {
    "include_private_members": false,
    "max_threads": 4,
    "supported_languages": ["java", "python"]
  }
}
```

### Comprehensive Configuration

```json
{
  "llm_models": [
    {
      "name": "gpt-3.5-turbo",
      "api_key": "sk-...",
      "endpoint": "https://api.openai.com/v1/chat/completions",
      "max_tokens": 4096,
      "temperature": 0.7,
      "timeout_seconds": 30
    },
    {
      "name": "codellama",
      "endpoint": "http://localhost:11434/api/generate",
      "max_tokens": 4096,
      "temperature": 0.3,
      "timeout_seconds": 60
    }
  ],
  "output_settings": {
    "output_directory": "./docs",
    "format": "markdown",
    "include_icons": true,
    "generate_unit_tests": true,
    "run_unit_test_commands": false,
    "log_unit_test_commands": true,
    "target_coverage": 0.9,
    "generate_mermaid": true,
    "mermaid_output_path": "./diagrams",
    "generate_plantuml": true,
    "plantuml_output_path": "./uml-diagrams",
    "verbose_output": false
  },
  "analysis_settings": {
    "include_private_members": true,
    "max_threads": 6,
    "supported_languages": ["java", "python"],
    "exclude_patterns": [
      "**/test/**",
      "**/target/**",
      "**/build/**",
      "**/__pycache__/**",
      "**/node_modules/**"
    ]
  }
}
```

### Diagrams-Only Configuration

```json
{
  "llm_models": [
    {
      "name": "codellama",
      "endpoint": "http://localhost:11434/api/generate",
      "max_tokens": 2048,
      "temperature": 0.3,
      "timeout_seconds": 45
    }
  ],
  "output_settings": {
    "output_directory": "./diagrams",
    "generate_unit_tests": false,
    "generate_mermaid": true,
    "generate_plantuml": true,
    "mermaid_output_path": "./diagrams/mermaid",
    "plantuml_output_path": "./diagrams/plantuml"
  },
  "analysis_settings": {
    "include_private_members": false,
    "max_threads": 4
  }
}
```

### Documentation-Only Configuration

```json
{
  "llm_models": [
    {
      "name": "llama3.2",
      "endpoint": "http://localhost:11434/api/generate",
      "max_tokens": 4096,
      "temperature": 0.7,
      "timeout_seconds": 60
    }
  ],
  "output_settings": {
    "output_directory": "./docs",
    "include_icons": true,
    "generate_unit_tests": true,
    "generate_mermaid": false,
    "generate_plantuml": false,
    "verbose_output": true
  },
  "analysis_settings": {
    "include_private_members": true,
    "max_threads": 6
  }
}
```

## Early Configuration Loading

Documentor implements a sophisticated configuration loading system that ensures settings are available before command processing:

### How It Works

1. **Early Loading**: Configuration is loaded at startup via `EarlyConfigurationLoader`
2. **Multiple Formats**: Supports various command-line argument formats
3. **Bean Registration**: Configuration is registered as a Spring bean
4. **Dependency Injection**: All services receive properly loaded configuration

### Supported Command-Line Formats

```bash
# Standard format
./gradlew runApp -Pargs="analyze --project-path ./src --config config.json"

# Equals format
./gradlew runApp -Pargs="analyze --project-path ./src --config=config.json"

# Comma-separated format (for Gradle arguments)
./gradlew runApp -Pargs="analyze,--project-path,./src,--config,config.json"
```

## API Key Configuration

### Method 1: Direct in config.json (Not Recommended for Production)

```json
{
  "llm_models": [
    {
      "name": "gpt-3.5-turbo",
      "api_key": "sk-...",
      "endpoint": "https://api.openai.com/v1/chat/completions"
    }
  ]
}
```

### Method 2: Environment Variables

```bash
export LLM_API_KEY=your-api-key-here
export OPENAI_API_KEY=sk-...
export ANTHROPIC_API_KEY=sk-ant-...
```

### Method 3: System Properties

```bash
java -DLLM_API_KEY=your-api-key-here -jar documentor.jar
```

## Configuration Validation

Validate your configuration before running analysis:

```bash
documentor:> validate-config --config config.json
✅ Configuration file is valid: config.json
Size: 1024 bytes
```

## Best Practices

### Security

- ✅ **Never commit API keys** to version control
- ✅ **Use environment variables** for sensitive data
- ✅ **Mask keys in logs** and error messages
- ✅ **Rotate API keys regularly**

### Performance

- 🚀 **Adjust max_threads** based on system resources
- 🚀 **Use appropriate temperature** for your use case
  - `0.1-0.3`: Consistent, factual outputs
  - `0.5-0.7`: Balanced generation (default)
  - `0.8-1.0`: Creative, varied outputs
- 🚀 **Set reasonable timeout_seconds** for your network
- 🚀 **Use exclude_patterns** to skip unnecessary files

### Analysis Quality

- 🎯 **Include private members** for complete documentation
- 🎯 **Support multiple languages** if your project uses them
- 🎯 **Set target_coverage** appropriately for your codebase
- 🎯 **Generate both Mermaid and PlantUML** for comprehensive diagrams

### Troubleshooting Configuration

**Issue**: Configuration not found

```bash
# Verify file exists and is readable
ls -la config.json
```

**Issue**: Invalid JSON

```bash
# Validate JSON syntax
jq . config.json
```

**Issue**: Unknown options

```bash
# Check configuration against provided templates
```

## File Locations

### Default Configuration Search Paths

1. `config.json` (current directory)
2. `./config/config.json`
3. Specified via `--config` parameter
4. Environment variable `CONFIG_PATH`

## Examples by Use Case

### Local Development (Ollama)

See `samples/config-ollama.json`

### Production (OpenAI)

See `samples/config-openai.json`

### Research (llama.cpp)

See `samples/config-llamacpp.json`

### Visualization (Diagrams Only)

See `samples/config-diagrams-only.json`

### Documentation (Docs Only)

See `samples/config-docs-only.json`

### Custom Diagram Naming

See `samples/config-diagram-naming-example.json` for a complete example of configuring custom prefixes, suffixes, and extensions for Mermaid and PlantUML diagrams.

## Next Steps

- **[LLM Integrations](LLM_INTEGRATIONS.md)** - Detailed setup for each provider
- **[Usage Examples](USAGE_EXAMPLES.md)** - How to use each configuration
- **[Troubleshooting](../TROUBLESHOOTING.md)** - Common issues and solutions
