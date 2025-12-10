# Documentor - AI-Powered Code Documentation Generator

A powerful Java Spring Boot CLI application that generates comprehensive documentation for Java projects using Large Language Models (LLMs) including OpenAI, Ollama, and LlamaCpp.

## Table of Contents

- [Features](#features)
- [Quick Start](#quick-start)
- [Installation](#installation)
- [Usage](#usage)
- [Configuration](#configuration)
- [Documentation](#documentation)
- [Contributing](#contributing)
- [License](#license)

## Features

- **Multi-LLM Support**: OpenAI, Ollama, LlamaCpp with easy provider switching
- **Comprehensive Documentation**: Generates Markdown documentation for classes, methods, and fields
- **Diagram Generation**: Mermaid class diagrams with configurable naming and styling
- **Thread-Safe Processing**: Parallel analysis with ThreadLocal context isolation
- **Configuration-Driven**: External JSON configuration with environment variable support
- **80%+ Test Coverage**: Extensive unit and integration test suite
- **Spring Boot 3.2**: Modern Java 21 framework with full async support
- **Security-First**: API key protection, input validation, secure defaults

## Quick Start

### Prerequisites

- Java 21 (main branch) or Java 17 (java-17-lts branch)
- Gradle 9.1.0+
- One of: OpenAI API key, Ollama server, or LlamaCpp server

### 1. Build the Project

```bash
git clone https://github.com/yourusername/documentor.git
cd documentor
./gradlew build
```

### 2. Create Configuration

Create `config.json`:

```json
{
  "llm_models": [{
    "name": "llama3.2",
    "baseUrl": "http://localhost:11434",
    "apiKey": "not-needed",
    "maxTokens": 2048,
    "timeoutSeconds": 60
  }],
  "output_settings": {
    "output_directory": "generated-docs",
    "format": "markdown"
  }
}
```

### 3. Run Documentation Generation

```bash
java -jar build/libs/documentor.jar --config config.json
```

## Installation

### From Source

```bash
git clone https://github.com/yourusername/documentor.git
cd documentor
./gradlew build
java -jar build/libs/documentor.jar --config config.json
```

### Environment Setup

**Ollama** (Local):
```bash
ollama serve
# Default: http://localhost:11434
```

**OpenAI** (Cloud):
```bash
export OPENAI_API_KEY="sk-..."
```

**LlamaCpp** (Custom):
```bash
export LLAMACPP_BASE_URL="http://localhost:8000"
```

## Usage

### Basic Command

```bash
java -jar documentor.jar --config config.json
```

### Configuration Options

See `docs/CONFIGURATION.md` for complete configuration reference including:
- LLM provider settings
- Output formatting options
- Analysis scope and limits
- Diagram generation options

## Documentation

- [Architecture Guide](docs/ARCHITECTURE.md) - System design and components
- [Security Guide](docs/SECURITY.md) - Security best practices
- [Contributing Guide](docs/CONTRIBUTING.md) - How to contribute
- [Configuration Guide](docs/CONFIGURATION.md) - Detailed configuration
- [Agents Workflows](Agents.md) - Build, test, and release workflows

## Project Structure

```
src/main/java/com/documentor/
├── cli/              - Command-line interface
├── config/           - Spring Boot configuration
├── model/            - Data models and domain objects
├── service/          - Business logic services
│   └── llm/         - LLM provider implementations
└── util/            - Utility functions

docs/                 - Documentation files
├── ARCHITECTURE.md
├── SECURITY.md
├── CONTRIBUTING.md
└── README.md        - Full documentation index
```

## Development

### Testing

```bash
./gradlew test                           # Run all tests
./gradlew jacocoTestReport              # Generate coverage report
./gradlew checkstyleMain checkstyleTest # Check code style
```

### Building

```bash
./gradlew clean build                   # Full build with tests
./gradlew build -x test                 # Build without tests
```

### Code Quality

- Minimum 80% test coverage (enforced via JaCoCo)
- Google Java Style Guide compliance (enforced via Checkstyle)
- Spring Boot best practices

## Branches

- **main**: Java 21, latest features, released version
- **java-17-lts**: Java 17 long-term support version with same features

## Contributing

We welcome contributions! See [CONTRIBUTING.md](docs/CONTRIBUTING.md) for guidelines on:
- Code of conduct
- Development setup
- Testing requirements
- Commit message format
- Pull request process

## License

Licensed under the Apache License 2.0. See [LICENSE](LICENSE) file for details.

## Version

**Current**: 2.1.0  
**Updated**: December 9, 2025

---

For detailed documentation, configuration options, and advanced usage, see the [complete documentation index](docs/README.md).
