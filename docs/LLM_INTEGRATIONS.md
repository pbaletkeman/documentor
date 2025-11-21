# 🤖 LLM Integrations Guide

Complete setup instructions for integrating different LLM providers with Documentor.

## Table of Contents

- [Supported LLM Providers](#supported-llm-providers)
- [OpenAI / ChatGPT Integration](#openai--chatgpt-integration)
- [Anthropic Claude Integration](#anthropic-claude-integration)
- [Ollama Integration](#ollama-integration)
- [Llama.cpp Integration](#llamacpp-integration)
- [Mock Provider Integration](#mock-provider-integration)
- [Configuration](#configuration)
- [Performance Tuning](#performance-tuning)
- [Troubleshooting](#troubleshooting)

## Supported LLM Providers

- OpenAI / ChatGPT
- Anthropic Claude
- Ollama (local models)
- llama.cpp (local server)
- Any compatible LLM API

## OpenAI / ChatGPT Integration

### Why Choose OpenAI?

- ✅ **Superior Quality**: Highest quality documentation generation
- ✅ **Best Understanding**: Excellent comprehension of complex code
- ✅ **No Local Resources**: Works without GPU or high-end hardware
- ✅ **Production Ready**: Industry-leading reliability and uptime
- ❌ **Cost**: Pay-per-use API (though often worth it for quality)
- ❌ **Privacy**: Code is sent to OpenAI's servers

### Quick Setup

#### 1. Get Your API Key

1. Go to [OpenAI Platform](https://platform.openai.com/)
2. Sign in or create an account
3. Navigate to API keys section
4. Create a new API key

#### 2. Configure Documentor

```bash
cp samples/config-openai.json config.json
```

Edit `config.json` and replace `YOUR_OPENAI_API_KEY` with your actual key:

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
    "max_threads": 4
  }
}
```

#### 3. Run Documentor

```bash
./gradlew runApp
```

In the interactive shell:

```bash
analyze --project-path ./src --config config.json
```

### Model Selection

| Model         | Quality | Speed  | Cost   | Max Tokens | Best For         |
| ------------- | ------- | ------ | ------ | ---------- | ---------------- |
| gpt-3.5-turbo | High    | Fast   | Low    | 4096       | Most use cases   |
| gpt-4         | Highest | Slower | High   | 8192       | Complex projects |
| gpt-4-turbo   | Highest | Medium | Medium | 128k       | Large codebases  |

### Environment Variable Setup (Recommended for Production)

```bash
export OPENAI_API_KEY="sk-..."
```

Then use this simplified config:

```json
{
  "llm_models": [
    {
      "name": "gpt-3.5-turbo",
      "endpoint": "https://api.openai.com/v1/chat/completions"
    }
  ]
}
```

### Pricing Estimation

- ~$0.002 per 1K input tokens
- ~$0.004 per 1K output tokens
- Typical project analysis: $0.50-$5.00

## Ollama Integration

### Why Choose Ollama?

- ✅ **Zero Cost**: Completely free, no API charges
- ✅ **Privacy**: Data stays on your machine
- ✅ **Offline**: Works without internet
- ✅ **Easy Setup**: Simple installation and model management
- ❌ **Local Resources**: Requires GPU or significant RAM
- ❌ **Speed**: Slower than cloud APIs
- ❌ **Quality**: Generally lower quality than OpenAI

### Installation

#### Windows

1. Download installer from [ollama.ai](https://ollama.ai)
2. Run the installer
3. Verify installation:

```bash
ollama --version
```

#### macOS

```bash
brew install ollama
```

#### Linux

```bash
curl -fsSL https://ollama.ai/install.sh | sh
```

### Getting Started

#### 1. Start Ollama

```bash
ollama serve
```

#### 2. Pull a Model

In another terminal:

```bash
# General purpose
ollama pull llama3.2

# Code-specific (recommended for programming projects)
ollama pull codellama

# Lightweight option
ollama pull phi3:mini
```

#### 3. Configure Documentor

```bash
cp samples/config-ollama.json config.json
```

#### 4. Run Analysis

```bash
./gradlew runApp
```

In the interactive shell:

```bash
analyze --project-path ./src --config config.json
```

### Recommended Models

| Model          | Quality   | Speed  | RAM  | Best For                   |
| -------------- | --------- | ------ | ---- | -------------------------- |
| phi3:mini      | Fair      | Fast   | 3GB  | Quick testing              |
| llama3.2       | Good      | Slow   | 8GB  | General documentation      |
| codellama      | Very      | Slow   | 10GB | Code-specific analysis     |
| deepseek-coder | Excellent | Slow   | 15GB | Complex code understanding |
| mistral        | Good      | Medium | 7GB  | Balanced performance       |

### Advanced Ollama Configuration

#### Custom Host/Port

```json
{
  "llm_models": [
    {
      "name": "llama3.2",
      "endpoint": "http://192.168.1.100:11434/api/generate",
      "max_tokens": 4096,
      "temperature": 0.7
    }
  ]
}
```

#### Multiple Models for Different Tasks

```json
{
  "llm_models": [
    {
      "name": "codellama",
      "endpoint": "http://localhost:11434/api/generate",
      "max_tokens": 4096,
      "temperature": 0.2
    },
    {
      "name": "llama3.2",
      "endpoint": "http://localhost:11434/api/generate",
      "max_tokens": 2048,
      "temperature": 0.8
    }
  ]
}
```

### Docker Setup (No Installation Required)

If you prefer Docker, you can run Ollama without installing:

```bash
# Create models directory
mkdir -p ~/ollama-models

# Run with Docker
docker run -it --rm -p 11434:11434 \
  -v ~/ollama-models:/root/.ollama \
  ghcr.io/ollama/ollama ollama serve
```

Pull models:

```bash
docker exec -it <container-id> ollama pull llama3.2
```

### Troubleshooting Ollama

**Model not found**

```bash
ollama pull llama3.2
```

**Connection refused**

Ensure Ollama is running and on correct port (default 11434):

```bash
ollama serve
```

**Out of memory**

Use a smaller model or increase available memory.

**Slow performance**

Use a smaller model like phi3:mini or reduce max_tokens.

## llama.cpp Integration

### Why Choose llama.cpp?

- ✅ **Maximum Control**: Full control over model parameters
- ✅ **Resource Efficient**: Lower memory usage than Ollama
- ✅ **Flexible Quantization**: Support for specific quantization levels
- ✅ **Zero Cost**: Free, local execution
- ❌ **Complexity**: Requires more technical setup
- ❌ **Speed**: Similar to Ollama, depends on hardware

### Installation

#### Step 1: Download Binaries

Visit [llama.cpp Releases](https://github.com/ggerganov/llama.cpp/releases) and download:

- Windows: `llama-<version>-bin-win.zip`
- macOS: `llama-<version>-bin-darwin.tar.gz`
- Linux: `llama-<version>-bin-linux.tar.gz`

#### Step 2: Extract

**Windows (PowerShell):**

```powershell
Expand-Archive -Path llama-<version>-bin-win.zip -DestinationPath llama-cpp
```

**macOS/Linux:**

```bash
mkdir -p llama-cpp
tar -xzf llama-<version>-bin-<platform>.tar.gz -C llama-cpp
```

#### Step 3: Download a Model

```bash
mkdir -p llama-cpp/models

# Download CodeLlama (recommended for code documentation)
curl -L https://huggingface.co/TheBloke/CodeLlama-7B-GGUF/resolve/main/codellama-7b.Q4_K_M.gguf \
  -o llama-cpp/models/codellama-7b.Q4_K_M.gguf
```

### Configuration

#### 1. Start the Server

**Windows:**

```bash
cd llama-cpp
llama-server -m models/codellama-7b.Q4_K_M.gguf --host 0.0.0.0 --port 8080
```

**macOS/Linux:**

```bash
cd llama-cpp
./server -m models/codellama-7b.Q4_K_M.gguf --host 0.0.0.0 --port 8080
```

#### 2. Configure Documentor

```bash
cp samples/config-llamacpp.json config.json
```

#### 3. Run Analysis

```bash
./gradlew runApp
```

In the interactive shell:

```bash
analyze --project-path ./src --config config.json
```

### Model Selection

Download from [TheBloke's HuggingFace](https://huggingface.co/TheBloke):

| Model         | Size | Quantization | Best For               |
| ------------- | ---- | ------------ | ---------------------- |
| CodeLlama-7B  | 7B   | Q4_K_M       | Code analysis          |
| CodeLlama-13B | 13B  | Q5_K_M       | Better code quality    |
| Llama-2-7B    | 7B   | Q4_K_M       | General use            |
| Llama-2-13B   | 13B  | Q5_K_M       | Better general quality |

### Docker Alternative

```bash
# Create models directory
mkdir -p ~/llama-models

# Download a model
curl -L https://huggingface.co/TheBloke/CodeLlama-7B-GGUF/resolve/main/codellama-7b.Q4_K_M.gguf \
  -o ~/llama-models/codellama-7b.Q4_K_M.gguf

# Run with Docker
docker run -it --rm -p 8080:8080 \
  -v ~/llama-models:/models \
  ghcr.io/ggerganov/llama.cpp:full \
  -m /models/codellama-7b.Q4_K_M.gguf \
  --host 0.0.0.0 --port 8080
```

### Advanced Configuration

#### Multiple GPU Support

```bash
llama-server -m models/codellama-7b.Q4_K_M.gguf \
  --n-gpu-layers 32 \
  --host 0.0.0.0 --port 8080
```

#### Context Window Adjustment

```bash
llama-server -m models/codellama-7b.Q4_K_M.gguf \
  -c 4096 \
  --host 0.0.0.0 --port 8080
```

## Comparison Table

| Aspect        | OpenAI  | Ollama     | llama.cpp |
| ------------- | ------- | ---------- | --------- |
| **Cost**      | Paid    | Free       | Free      |
| **Quality**   | Highest | Good       | Good      |
| **Speed**     | Fast    | Slow       | Slow      |
| **Privacy**   | Cloud   | Local      | Local     |
| **Setup**     | Easiest | Easy       | Complex   |
| **Resources** | None    | 8-16GB RAM | 4-8GB RAM |
| **Control**   | Limited | Medium     | Maximum   |

## Best Practices

### For Production

- Use OpenAI or established API providers
- Store API keys in environment variables
- Use rate limiting
- Monitor usage and costs
- Implement retries with backoff

### For Local Development

- Use Ollama for rapid iteration
- Start with smaller models
- Increase resources as needed
- Use Docker for portability

### For Research

- Use llama.cpp for maximum control
- Experiment with different quantizations
- Test multiple models
- Measure performance metrics

## Troubleshooting

### API Connection Issues

```bash
# Test connectivity
curl -X GET "https://api.openai.com/v1/models" \
  -H "Authorization: Bearer sk-..."
```

### Model Not Responding

1. Verify endpoint is correct
2. Check API key/credentials
3. Verify model is available
4. Check timeout settings

### Memory Issues

- Reduce `max_tokens`
- Use smaller models
- Increase system RAM or GPU memory

## Next Steps

- **[Configuration Guide](CONFIGURATION.md)** - Detailed config options
- **[Usage Examples](USAGE_EXAMPLES.md)** - How to use each integration
- **[Getting Started](GETTING_STARTED.md)** - Quick start guide
