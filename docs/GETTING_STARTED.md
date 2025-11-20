# 🚀 Getting Started with Documentor

A comprehensive guide to setting up and running Documentor for your first code analysis project.

## Installation

### Prerequisites

- **Java 21** or higher
- **Gradle 9.1.0** or higher
- **Git** (optional, for pre-commit hooks)

### Quick Setup

#### 1. Clone the Repository

```bash
git clone https://github.com/pbaletkeman/documentor.git
cd documentor
```

#### 2. Set Up Pre-commit Hooks (Recommended)

**Unix/Linux/macOS:**

```bash
cp .githooks/pre-commit .git/hooks/pre-commit
chmod +x .git/hooks/pre-commit
```

**Windows:**

```bash
copy .githooks\\pre-commit.bat .git\\hooks\\pre-commit.bat
```

#### 3. Build the Project

**Unix/Linux/macOS:**

```bash
./gradlew build
```

**Windows:**

```bash
gradlew.bat build
```

#### 4. Create Distribution JARs (Optional)

```bash
# Create Spring Boot executable JAR (recommended)
./gradlew bootJar

# Create fat JAR with all dependencies
./gradlew fatJar

# Both JARs will be in build/libs/
```

## Running Documentor

### Start the Interactive Shell

```bash
# Unix/Linux/macOS
./gradlew runApp

# Windows
gradlew.bat runApp
```

### Run with Enhanced Version (Recommended for Unit Tests)

```bash
# Windows convenience script
run-enhanced.cmd

# Or directly with Gradle
./gradlew runEnhancedApp
```

### Display Help

```bash
# Show help without running
./gradlew helpInfo

# Show detailed command help
./gradlew runApp -Pargs="help"

# List all available Gradle tasks
./gradlew tasks
```

## Your First Analysis

### Option 1: Quick Start with Ollama (Recommended for Local Development)

**Step 1: Install Ollama**

```bash
# Visit https://ollama.ai and download for your OS
# Or use package managers:

# macOS
brew install ollama

# Linux
curl -fsSL https://ollama.ai/install.sh | sh
```

**Step 2: Start Ollama and Pull a Model**

```bash
ollama serve
# In another terminal:
ollama pull llama3.2  # Or codellama for code-specific tasks
```

**Step 3: Use Ollama Configuration**

```bash
cp samples/config-ollama.json config.json
```

**Step 4: Run Documentor**

```bash
./gradlew runApp
```

**Step 5: Analyze Your Project**

In the Documentor interactive shell:

```bash
analyze --project-path ./src --generate-mermaid true --config config.json
```

### Option 2: Using OpenAI/ChatGPT

**Step 1: Get Your API Key**

1. Sign up at [OpenAI Platform](https://platform.openai.com/)
2. Create an API key in your account dashboard

**Step 2: Configure OpenAI**

```bash
cp samples/config-openai.json config.json
```

Edit `config.json` and replace `YOUR_OPENAI_API_KEY` with your actual key.

**Step 3: Run Analysis**

```bash
./gradlew runApp
```

In the interactive shell:

```bash
analyze --project-path ./src --config config.json
```

### Option 3: Using llama.cpp

**Step 1: Download llama.cpp**

- Download from [GitHub Releases](https://github.com/ggerganov/llama.cpp/releases)
- Extract the binary to a local directory

**Step 2: Get a Model**

```bash
mkdir -p llama-cpp/models
curl -L https://huggingface.co/TheBloke/CodeLlama-7B-GGUF/resolve/main/codellama-7b.Q4_K_M.gguf \
  -o llama-cpp/models/codellama-7b.Q4_K_M.gguf
```

**Step 3: Start the Server**

```bash
cd llama-cpp
# Windows:
llama-server -m models/codellama-7b.Q4_K_M.gguf --host 0.0.0.0 --port 8080

# Unix/Linux/macOS:
./server -m models/codellama-7b.Q4_K_M.gguf --host 0.0.0.0 --port 8080
```

**Step 4: Use with Documentor**

```bash
cp samples/config-llamacpp.json config.json
./gradlew runApp
```

In the interactive shell:

```bash
analyze --project-path ./src --config config.json
```

## Common Commands

### Analyze a Project

```bash
analyze --project-path /path/to/project --config config.json
```

With diagrams:

```bash
analyze --project-path /path/to/project --generate-mermaid true --config config.json
```

### Scan Project (Analysis Only)

```bash
scan --project-path /path/to/project
```

### Validate Configuration

```bash
validate-config --config config.json
```

### Check Application Status

```bash
status
```

### Show Help

```bash
help
info
quick-start
```

## Configuration Quick Reference

### Configuration Files Provided

| File                                | Purpose                      |
| ----------------------------------- | ---------------------------- |
| `samples/config-ollama.json`        | Local LLM with Ollama        |
| `samples/config-openai.json`        | OpenAI/ChatGPT models        |
| `samples/config-llamacpp.json`      | llama.cpp server integration |
| `samples/config-diagrams-only.json` | Generate diagrams only       |
| `samples/config-docs-only.json`     | Generate documentation only  |

### Minimal Configuration Template

```json
{
  "llm_models": [
    {
      "name": "model-name",
      "endpoint": "api-endpoint",
      "max_tokens": 4096,
      "temperature": 0.7,
      "timeout_seconds": 30
    }
  ],
  "output_settings": {
    "output_directory": "./docs",
    "format": "markdown",
    "include_icons": true,
    "generate_unit_tests": true,
    "generate_mermaid": true
  },
  "analysis_settings": {
    "include_private_members": false,
    "max_threads": 4,
    "supported_languages": ["java", "python"],
    "exclude_patterns": ["**/test/**", "**/target/**", "**/build/**"]
  }
}
```

## Troubleshooting

### API Key Issues

```text
Error: LLM API call failed
```

**Solution**: Verify API keys are correct in `config.json` or set environment variables:

```bash
export LLM_API_KEY=your-api-key-here
```

### Java Version Issues

```text
Error: Unsupported Java version
```

**Solution**: Ensure Java 21+ is installed:

```bash
java -version
```

### Permission Denied (Unix/Linux)

```text
Error: Permission denied
```

**Solution**: Make gradlew executable:

```bash
chmod +x gradlew
```

### Memory Issues with Large Projects

```text
Error: OutOfMemoryError
```

**Solution**: Increase JVM memory:

```bash
export JAVA_OPTS="-Xmx4g"
./gradlew runApp
```

### Ollama Connection Failed

**Solution**: Verify Ollama is running:

```bash
ollama serve
```

Check the endpoint in your config matches (default: `http://localhost:11434`).

### Model Not Found in Ollama

**Solution**: Pull the model first:

```bash
ollama pull llama3.2
```

## Next Steps

- **[Configuration Guide](CONFIGURATION.md)** - Detailed configuration options
- **[LLM Integrations](LLM_INTEGRATIONS.md)** - Setup guides for each LLM provider
- **[Usage & Examples](USAGE_EXAMPLES.md)** - Comprehensive command examples
- **[Diagrams Guide](DIAGRAMS_GUIDE.md)** - Understanding diagram generation
- **[Development Guide](DEVELOPMENT.md)** - Contributing and extending Documentor

## Support

- 📧 **Email**: `documentor@letkeman.ca`
- 🐛 **Issues**: [GitHub Issues](https://github.com/pbaletkeman/documentor/issues)
- 💬 **Discussions**: [GitHub Discussions](https://github.com/pbaletkeman/documentor/discussions)
