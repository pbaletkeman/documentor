# Sample Configuration Files

This directory contains ready-to-use configuration files for different Documentor use cases.

## Quick Reference

| Configuration File                                                  | Purpose               | Description                                                       |
| ------------------------------------------------------------------- | --------------------- | ----------------------------------------------------------------- |
| `config-openai.json`                                                | OpenAI/ChatGPT        | Use OpenAI's GPT models for high-quality documentation generation |
| `config-ollama.json`                                                | Ollama Integration    | Use locally-running Ollama models for offline, private analysis   |
| `config-llamacpp.json`                                              | llama.cpp Integration | Use llama.cpp server with GGUF format models                      |
| `config-diagrams-only.json`                                         | Diagrams Only         | Generate only Mermaid and PlantUML diagrams (no documentation)    |
| `config-docs-only.json`                                             | Documentation Only    | Generate only comprehensive documentation (no diagrams)           |
| `config-unit-test-logging.json`                                     | Unit Test Logging     | Minimal configuration focused on unit test command logging        |
| `fixed-config.json`, `fixed-config-v2.json`, `fixed-config-v3.json` | Fixed/Development     | Development and testing configurations                            |

## How to Use

### Option 1: Copy and Modify

```bash
# Copy a sample config to use as your primary config
cp samples/config-ollama.json config.json

# Edit config.json with your preferences
# Then run Documentor
./gradlew runApp
```

### Option 2: Use Directly

```bash
# Run Documentor with a specific sample config directly
./gradlew runApp -Pargs="analyze,--project-path,./src,--config,samples/config-openai.json"
```

## Configuration Descriptions

### config-openai.json

**Best For:** Production-grade documentation with maximum quality

**Key Features:**

- Uses OpenAI's GPT-3.5-turbo model
- 4096 token limit for detailed responses
- Requires OpenAI API key
- Best documentation quality

**Usage:**

```bash
cp samples/config-openai.json config.json
# Edit config.json to add your API key
./gradlew runApp
```

### config-ollama.json

**Best For:** Local, offline analysis with privacy

**Key Features:**

- Uses local Ollama models (llama3.2, codellama)
- No API costs
- Complete data privacy
- Requires Ollama server running locally

**Usage:**

```bash
# First, start Ollama
ollama serve

# In another terminal:
cp samples/config-ollama.json config.json
./gradlew runApp
```

### config-llamacpp.json

**Best For:** Efficient local analysis with precise control

**Key Features:**

- Uses llama.cpp server with GGUF models
- Lower resource usage than Ollama
- More customization options
- Requires llama.cpp server running

### config-diagrams-only.json

**Best For:** Quick visualization of project structure

**Key Features:**

- Generates Mermaid class diagrams
- Generates PlantUML class diagrams
- Skips documentation generation
- Faster processing

**Usage:**

```bash
cp samples/config-diagrams-only.json config.json
analyze --project-path ./src
```

### config-docs-only.json

**Best For:** Comprehensive textual documentation

**Key Features:**

- Generates comprehensive documentation
- Includes unit test suggestions
- Skips diagram generation
- Better for reference documentation

**Usage:**

```bash
cp samples/config-docs-only.json config.json
analyze --project-path ./src
```

### config-unit-test-logging.json

**Best For:** Testing and logging unit test commands

**Key Features:**

- Generates unit tests
- Logs test commands to file
- Minimal configuration
- Good for CI/CD integration

## Directory Structure

```
samples/
├── config-openai.json              # OpenAI configuration
├── config-ollama.json              # Ollama configuration
├── config-llamacpp.json            # llama.cpp configuration
├── config-diagrams-only.json       # Diagrams-only configuration
├── config-docs-only.json           # Documentation-only configuration
├── config-unit-test-logging.json   # Unit test logging configuration
├── fixed-config.json               # Fixed/development configuration
├── fixed-config-v2.json            # Alternative development config
├── fixed-config-v3.json            # Alternative development config
└── README.md                        # This file
```

## Customizing Configurations

Each configuration file can be customized by modifying:

1. **LLM Models** - Change the model name, API key, endpoint, and parameters
2. **Output Settings** - Configure output directory, diagram generation, test generation
3. **Analysis Settings** - Control thread count, language support, file patterns to exclude

See the main README.md for detailed configuration documentation.

## Tips

- **Start Simple:** Begin with `config-ollama.json` if you have Ollama installed
- **Try Diagrams First:** Use `config-diagrams-only.json` for a quick visual check
- **Test Your Setup:** Run `validate-config --config samples/config-XXXX.json` to verify
- **Keep Original:** Don't modify files in the samples directory; copy them first
