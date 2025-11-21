# Docker Setup for Documentor

This document covers running Documentor in Docker containers for both development and production environments.

## Quick Start

### Development Environment

Start the development container with live code reloading:

```bash
docker-compose up documentor-dev
```

The container will:

- Mount your source code for live updates
- Run continuous Gradle builds
- Expose port 8080 for the app
- Expose port 5005 for remote debugging

### Production Environment

Run the production-optimized image:

```bash
docker-compose up documentor-prod
```

The container will:

- Run the compiled JAR with minimal dependencies
- Use 256MB heap memory
- Expose port 8081
- Include health checks
- Auto-restart on failure

## Building Images

### Build Development Image

```bash
docker build -f Dockerfile.dev -t documentor:dev .
```

### Build Production Image

```bash
docker build -f Dockerfile -t documentor:latest .
```

### Build with Docker Compose

```bash
docker-compose build
```

## Configuration

### Environment Variables

**Development (`documentor-dev`):**

- `JAVA_TOOL_OPTIONS`: JVM memory settings (default: `-Xmx512m -Xms256m`)
- `LOG_LEVEL`: Logging level (default: `DEBUG`)
- `CONFIG_PATH`: Path to configuration file (default: `/app/config/config.json`)

**Production (`documentor-prod`):**

- `JAVA_TOOL_OPTIONS`: JVM memory settings (default: `-Xmx256m -Xms128m`)
- `LOG_LEVEL`: Logging level (default: `INFO`)
- `CONFIG_PATH`: Path to configuration file

### Mounting Configuration

To use custom configuration:

```bash
# Development
docker run -v $(pwd)/config/custom-config.json:/app/config/config.json documentor:dev

# Production
docker run -e CONFIG_PATH=/app/custom-config.json -v $(pwd)/config:/app/config documentor:latest
```

## Advanced Usage

### Remote Debugging

Enable debug port in development:

```bash
docker-compose up documentor-dev
```

Then connect your IDE debugger to `localhost:5005` with Java remote debugging.

### Testing with Mock LLM Service

Run development environment with mock LLM API:

```bash
docker-compose --profile testing up documentor-dev mock-llm-service
```

This starts:

- `documentor-dev`: Development environment
- `mock-llm-service`: MockServer for simulating LLM API responses

### Viewing Logs

Development logs with tail:

```bash
docker-compose logs -f documentor-dev
```

Production logs:

```bash
docker-compose logs -f documentor-prod
```

### Executing Commands in Running Container

Run a command inside development container:

```bash
docker-compose exec documentor-dev ./gradlew test
```

Run a command inside production container:

```bash
docker-compose exec documentor-prod java -jar documentor.jar --config /app/config/custom.json
```

### Interactive Shell

Open a shell in development container:

```bash
docker-compose exec documentor-dev bash
```

## Performance Tuning

### Development Performance

Optimize build cache volumes:

```bash
# Use delegated strategy for source mounts (faster on macOS/Windows)
volumes:
  - ./src:/app/src:delegated
  - gradle-cache:/root/.gradle:cached
```

### Production Performance

Adjust heap memory based on available resources:

```bash
# Low memory (256MB)
JAVA_TOOL_OPTIONS: -Xmx256m -Xms128m

# Medium memory (512MB)
JAVA_TOOL_OPTIONS: -Xmx512m -Xms256m

# High memory (1GB+)
JAVA_TOOL_OPTIONS: -Xmx1g -Xms512m
```

## Networking

All services use the `documentor-network` bridge network. Services can communicate by service name:

```
http://documentor-dev:8080
http://mock-llm-service:1080
```

To expose services to the host, use port mappings in `docker-compose.yml`.

## Cleanup

Remove containers and volumes:

```bash
docker-compose down -v
```

Remove images:

```bash
docker rmi documentor:dev documentor:latest
```

## Health Checks

Production image includes health checks:

```bash
# Check container health
docker ps --format "{{.Names}}\t{{.Status}}"

# Manual health check
curl http://localhost:8081/actuator/health
```

## Security Considerations

### Non-root User

Production image runs as non-root `documentor` user for security.

### Read-only Volumes

Production configuration is mounted read-only:

```yaml
volumes:
  - ./config:/app/config:ro
```

### Network Isolation

Use profile-based services to isolate sensitive components:

```bash
# Run only production without mock services
docker-compose up documentor-prod

# Run with testing services
docker-compose --profile testing up
```

## Troubleshooting

### Build fails in container

Check Gradle cache and permissions:

```bash
docker-compose down -v
docker-compose build --no-cache
```

### Out of memory errors

Increase heap size in `docker-compose.yml`:

```yaml
environment:
  - JAVA_TOOL_OPTIONS=-Xmx1g -Xms512m
```

### Permission denied on volumes

Ensure volumes have correct permissions:

```bash
chmod 755 build/
chmod 755 .gradle/
```

### Cannot connect to service

Verify container is running:

```bash
docker-compose ps
docker-compose logs documentor-dev
```

## References

- [Docker Documentation](https://docs.docker.com/)
- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [Best Practices for Java Docker Images](https://www.docker.com/blog/containerized-java-applications-best-practices/)
- [Spring Boot Docker Documentation](https://spring.io/guides/gs/spring-boot-docker/)
