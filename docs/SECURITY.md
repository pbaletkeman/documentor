# Security Guide - Documentor

## Table of Contents

- [Overview](#overview)
- [Security Principles](#security-principles)
- [Authentication & Authorization](#authentication--authorization)
- [API Key Management](#api-key-management)
- [Input Validation](#input-validation)
- [Data Protection](#data-protection)
- [Secure Coding Practices](#secure-coding-practices)
- [Dependency Management](#dependency-management)
- [Reporting Vulnerabilities](#reporting-vulnerabilities)

## Overview

Documentor handles sensitive data including code analysis and API keys. This guide outlines security best practices and design principles used to protect user data and maintain system integrity.

**Security Goals**:

- Protect API keys from unauthorized access
- Validate all user input to prevent injection attacks
- Maintain confidentiality of analyzed code
- Ensure system reliability and availability
- Follow industry best practices for secure coding

## Security Principles

### 1. Defense in Depth

Multiple layers of security controls:

- Input validation at entry points
- Type checking and constraint validation
- API key encryption in transit
- Secure configuration management
- Error handling without information disclosure

### 2. Least Privilege

- Services have minimal required permissions
- API keys scoped to necessary endpoints
- Thread pools sized appropriately
- Configuration limited to required settings

### 3. Fail Securely

```
Exception Occurs
    ↓
Log Details (internal only)
    ↓
Return Generic Error Message
    ↓
Continue Operation
```

- Never expose sensitive details in error messages
- Log detailed errors for debugging
- Provide user-friendly error feedback
- Maintain system availability

### 4. Secure by Default

- API keys never logged in plain text
- Configuration validation on startup
- ThreadLocal context cleared after use
- Async operations inherit security context

### 5. Keep It Simple

- Clear, readable security-critical code
- Avoid complex crypto implementations
- Use battle-tested libraries
- Document security assumptions

## Authentication & Authorization

### LLM Provider Authentication

**OpenAI**:

```yaml
llm_models:
  - name: "gpt-4"
    api_key: "${OPENAI_API_KEY}" # Via environment variable
    baseUrl: "https://api.openai.com/v1"
```

**Ollama** (Local):

```yaml
llm_models:
  - name: "llama3.2"
    api_key: "not-needed" # No authentication required
    baseUrl: "http://localhost:11434"
```

**Best Practices**:

- Store API keys in environment variables, never in code
- Use secrets management (e.g., AWS Secrets Manager)
- Rotate API keys periodically
- Use least-privilege API keys
- Monitor API key usage

### Configuration-Based Authorization

All access is configuration-driven:

- Projects specified in config.json
- Output directories defined per execution
- Analysis scope limited by configuration
- No hardcoded permissions or access levels

## API Key Management

### Key Storage

**DO**:

```bash
# Via environment variable
export OPENAI_API_KEY="sk-..."
java -jar documentor.jar --config config.json

# Via external secrets management
# AWS Secrets Manager, Vault, 1Password, etc.
```

**DON'T**:

```bash
# Never in config files
echo '{"api_key": "sk-..."}' > config.json

# Never in version control
git add secrets.json

# Never in logs
logger.info("API Key: " + apiKey);
```

### Key Validation

Configuration validation on startup ensures:

- Required keys are present
- Keys have minimum expected format
- Timeouts are reasonable
- URLs are valid and accessible

```java
// During configuration loading
if (apiKey == null || apiKey.trim().isEmpty()) {
    throw new ConfigurationException("API key required for provider: " + name);
}
```

### Key Usage Logging

**Secure Logging**:

```java
// Log key usage without exposing the key itself
logger.info("Calling LLM provider: {}", providerName);
logger.debug("Request tokens: {}, Response tokens: {}",
    requestTokens, responseTokens);

// Never log
logger.info("API Key: " + key);  // WRONG!
logger.info("Full response: " + response);  // May contain sensitive data
```

## Input Validation

### Configuration Validation

**File Path Validation**:

```java
Path projectPath = Paths.get(config.getProjectPath());
if (!Files.exists(projectPath)) {
    throw new IllegalArgumentException("Project path not found: " + projectPath);
}
if (!Files.isDirectory(projectPath)) {
    throw new IllegalArgumentException("Project path is not a directory: " + projectPath);
}
```

**URL Validation**:

```java
try {
    new URL(baseUrl).toURI();
} catch (Exception e) {
    throw new IllegalArgumentException("Invalid URL: " + baseUrl);
}
```

**Constraint Validation**:

```java
if (maxTokens <= 0 || maxTokens > MAX_TOKENS_LIMIT) {
    throw new IllegalArgumentException(
        "maxTokens must be between 1 and " + MAX_TOKENS_LIMIT
    );
}

if (timeoutSeconds < MIN_TIMEOUT || timeoutSeconds > MAX_TIMEOUT) {
    throw new IllegalArgumentException(
        "timeout must be between " + MIN_TIMEOUT + " and " + MAX_TIMEOUT + " seconds"
    );
}
```

### Code Element Validation

Validate all parsed code elements:

- Class/interface names valid Java identifiers
- Method signatures well-formed
- Field types recognized
- Documentation strings don't contain injection payloads

### Error Message Security

**Secure Error Handling**:

```java
try {
    // Process code
} catch (Exception e) {
    // Log full details internally
    logger.error("Processing error for file: {}", filename, e);

    // Return generic message to user
    result.addError("Failed to process file: " + filename);
}
```

## Data Protection

### In-Transit Protection

**HTTPS for External APIs**:

- All external API calls use HTTPS
- Certificate validation enabled
- TLS 1.2 minimum
- No HTTP fallback

**Local LLM Communication**:

- Consider network isolation
- Use VPN for remote servers
- Monitor network traffic
- Implement basic HTTP authentication if needed

### At-Rest Protection

**Sensitive Configuration**:

- API keys in environment variables (not files)
- Use OS-level secrets management
- Restrict file permissions (600 for key files)
- Encrypt sensitive files if stored

**Generated Documentation**:

- Treat output same as input code
- Restrict file permissions (644 for readable, 755 for directories)
- Use VCS safely (never commit sensitive files)
- Consider encryption for sensitive projects

### Output File Security

```bash
# Recommended permissions
chmod 755 output/              # Directory readable
chmod 644 output/*.md          # Documentation readable
chmod 600 config.json          # Config restricted (if contains keys)
```

## Secure Coding Practices

### 1. Exception Handling

```java
// Good: Log details, return safe message
try {
    return callLlm(request);
} catch (Exception e) {
    logger.error("LLM call failed", e);
    throw new DocumentationException(
        "Failed to generate documentation. Check configuration and try again."
    );
}

// Bad: Expose details to user
} catch (Exception e) {
    throw new DocumentationException("Database: " + e.getMessage());
}
```

### 2. Null Safety

Use appropriate null handling:

```java
// Java 21 - Pattern matching
if (config instanceof LlmModelConfig llm && llm.getApiKey() != null) {
    // Safe to use
}

// Or null checks
Objects.requireNonNull(config, "Configuration cannot be null");
```

### 3. Immutability

Configuration objects should be immutable:

```java
public record LlmModelConfig(
    String name,
    String baseUrl,
    String apiKey,
    int maxTokens,
    int timeoutSeconds
) {}
```

### 4. Thread Safety

Proper synchronization for shared resources:

```java
// ThreadLocal for context isolation
private static final ThreadLocal<DocumentorConfig> CONFIG_HOLDER =
    new ThreadLocal<>();

// Synchronization for shared state
private synchronized void updateCache(String key, String value) {
    cache.put(key, value);
}
```

### 5. Resource Management

Always close resources:

```java
try (HttpClient client = HttpClient.newHttpClient();
     HttpResponse<String> response = client.send(request,
         HttpResponse.BodyHandlers.ofString())) {
    // Use response
} catch (Exception e) {
    logger.error("HTTP request failed", e);
}
```

## Dependency Management

### Regular Updates

**Best Practices**:

- Monitor Spring Boot security updates
- Update dependencies monthly
- Test updates before production
- Use dependency vulnerability scanning

**Tools**:

- OWASP Dependency-Check
- Snyk
- Dependabot

### Known Vulnerabilities

Monitor for CVEs affecting:

- Spring Framework
- Spring Boot
- Jackson (JSON processing)
- Apache Commons libraries
- Gradle build tool

**Action Plan**:

1. Identify vulnerability
2. Check if affects Documentor
3. Update dependency version
4. Run full test suite
5. Validate in staging
6. Deploy to production

### Transitive Dependencies

Review indirect dependencies:

```bash
# Show dependency tree
gradle dependencies

# Check for vulnerabilities
gradle dependencyCheck
```

## Reporting Vulnerabilities

### Security Contact

For security vulnerabilities, please email: [maintainer-email]

**Do NOT**:

- Post security issues on public issue tracker
- Share vulnerability details publicly
- Exploit vulnerabilities in live systems

### Responsible Disclosure

**Process**:

1. Email security contact with:

   - Description of vulnerability
   - Affected version(s)
   - Steps to reproduce
   - Potential impact
   - Suggested fix (optional)

2. Allow time for fix and testing

3. Coordinate public disclosure date

4. Acknowledge reporter in security update (if desired)

### Security Update Process

When vulnerability discovered:

1. Create private branch
2. Develop and test fix
3. Create security release
4. Publish security advisory
5. Update all affected versions
6. Notify users

## Security Checklist

### Configuration Security

- [ ] API keys stored in environment variables
- [ ] Configuration file has restricted permissions
- [ ] No keys in version control
- [ ] URL validation implemented
- [ ] Timeout values reasonable

### Code Security

- [ ] Input validation at all entry points
- [ ] Exception handling doesn't leak details
- [ ] Null safety enforced
- [ ] Resources properly closed
- [ ] Thread safety ensured

### Dependency Security

- [ ] Dependencies regularly updated
- [ ] No known vulnerabilities
- [ ] Transitive dependencies reviewed
- [ ] Vulnerability scanning enabled

### Operational Security

- [ ] Logging doesn't expose sensitive data
- [ ] Access logs monitored
- [ ] Error handling tested
- [ ] Security testing part of CI/CD
- [ ] Incidents documented

### Documentation Security

- [ ] README includes security guidelines
- [ ] CONTRIBUTING mentions security
- [ ] Threat model documented
- [ ] Security assumptions listed

## Threat Model

### Threats

| Threat                      | Impact      | Mitigation                                         |
| --------------------------- | ----------- | -------------------------------------------------- |
| API Key Exposure            | High        | Use environment variables, secrets management      |
| Configuration Tampering     | High        | File permission validation, signature verification |
| Malicious Input             | Medium      | Input validation, type checking                    |
| Dependency Vulnerability    | Medium-High | Regular updates, vulnerability scanning            |
| Sensitive Code Leaks        | High        | Treat docs same as source code                     |
| DDoS via Excessive Requests | Medium      | Rate limiting, timeout configuration               |
| Man-in-Middle Attack        | High        | HTTPS enforcement, certificate validation          |

### Risk Assessment

**Overall Risk Level**: **LOW** (for typical usage)

**Assumptions**:

- Users manage their own API keys securely
- Network is reasonably secure
- Dependencies are from trusted sources
- Configuration files are protected

---

**Version**: 2.1.0
**Last Updated**: December 9, 2025
