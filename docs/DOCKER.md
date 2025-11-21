# 🐳 Docker Setup & Deployment

Complete guide for containerizing and deploying Documentor with Docker.

## Table of Contents

- [Quick Start](#quick-start)
- [Images](#images)
- [Local Development](#local-development)
- [Production Deployment](#production-deployment)
- [Configuration](#configuration)
- [Troubleshooting](#troubleshooting)
- [Performance Tips](#performance-tips)
- [Advanced Configuration](#advanced-configuration)

## Quick Start

### Build Image

```bash
docker build -t documentor:latest .
```

### Run Container

```bash
docker run -it \
  -v $(pwd)/src:/app/src \
  -v $(pwd)/docs:/app/docs \
  documentor:latest
```

### Docker Compose

```bash
docker-compose up -d
```

## Images

### Production Image (`Dockerfile`)

Optimized for production use.

**Features**:
- Multi-stage build for small size
- Java 21 (slim variant)
- Minimal dependencies
- ~500MB image

**Usage**:

```bash
docker build -f Dockerfile -t documentor:prod .
docker run documentor:prod
```

### Development Image (`Dockerfile.dev`)

Includes development tools.

**Features**:
- Full build environment
- Gradle included
- Development utilities
- Testing support
- ~1.2GB image

**Usage**:

```bash
docker build -f Dockerfile.dev -t documentor:dev .
docker run -it documentor:dev bash
```

## Local Development

### Container with Source Code

Mount local code:

```bash
docker run -it \
  -v $(pwd):/app \
  -w /app \
  documentor:dev \
  bash
```

Then inside container:

```bash
./gradlew build
./gradlew runApp
```

### Docker Compose for Development

```yaml
version: '3.9'
services:
  documentor:
    build:
      context: .
      dockerfile: Dockerfile.dev
    container_name: documentor-dev
    volumes:
      - .:/app
      - ~/.m2:/root/.m2  # Maven cache
      - ~/.gradle:/root/.gradle  # Gradle cache
    working_dir: /app
    command: bash
    stdin_open: true
    tty: true
```

Usage:

```bash
docker-compose -f docker-compose.yml up -d documentor
docker-compose exec documentor bash
```

### Hot Reload Setup

Mount entire project and use volume for build cache:

```bash
docker run -it \
  -v $(pwd):/app \
  -v documentor-gradle-cache:/root/.gradle \
  -w /app \
  documentor:dev \
  ./gradlew build --continuous
```

## Production Deployment

### Single Container

```bash
docker run -d \
  --name documentor \
  -v /data/projects:/app/projects:ro \
  -v /data/docs:/app/docs \
  -p 8080:8080 \
  documentor:prod \
  java -jar app.jar
```

### Docker Compose Production

```yaml
version: '3.9'
services:
  documentor:
    image: documentor:prod
    container_name: documentor-prod
    restart: always
    volumes:
      - /data/projects:/app/projects:ro
      - /data/docs:/app/docs
      - /data/logs:/app/logs
    environment:
      - JAVA_OPTS=-Xmx2g -Xms512m
      - DOCUMENTOR_CONFIG=/app/config/config.json
    ports:
      - "8080:8080"
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 40s
    logging:
      driver: "json-file"
      options:
        max-size: "100m"
        max-file: "10"
```

### Kubernetes Deployment

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: documentor
  labels:
    app: documentor
spec:
  replicas: 2
  selector:
    matchLabels:
      app: documentor
  template:
    metadata:
      labels:
        app: documentor
    spec:
      containers:
      - name: documentor
        image: documentor:prod
        imagePullPolicy: IfNotPresent
        resources:
          requests:
            memory: "512Mi"
            cpu: "500m"
          limits:
            memory: "2Gi"
            cpu: "2000m"
        volumeMounts:
        - name: projects
          mountPath: /app/projects
          readOnly: true
        - name: docs
          mountPath: /app/docs
        - name: config
          mountPath: /app/config
          readOnly: true
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /actuator/health/ready
            port: 8080
          initialDelaySeconds: 20
          periodSeconds: 5
      volumes:
      - name: projects
        persistentVolumeClaim:
          claimName: projects-pvc
      - name: docs
        persistentVolumeClaim:
          claimName: docs-pvc
      - name: config
        configMap:
          name: documentor-config
```

## Configuration

### Environment Variables

```bash
docker run \
  -e JAVA_OPTS="-Xmx2g -Xms512m" \
  -e DOCUMENTOR_CONFIG="/app/config/config.json" \
  -e DOCUMENTOR_LOG_LEVEL="INFO" \
  documentor:prod
```

### Config File Mounting

```bash
docker run -d \
  -v $(pwd)/config.json:/app/config/config.json:ro \
  documentor:prod
```

### Spring Boot Properties

Pass as environment variables:

```bash
docker run -e spring.profiles.active=production documentor:prod
```

## Troubleshooting

### Container Won't Start

**Check logs**:

```bash
docker logs documentor
```

**Common issues**:

1. **Out of Memory**
   ```bash
   docker run -m 4g documentor:prod
   ```

2. **Volume Mount Issues**
   ```bash
   docker run -v /local/path:/container/path:z documentor:prod
   ```

3. **Permission Denied**
   ```bash
   docker run --user root documentor:prod
   ```

### Performance Issues

**Increase Memory**:

```bash
docker run -m 4g -e JAVA_OPTS="-Xmx3g" documentor:prod
```

**Enable CPU Limits**:

```bash
docker run --cpus="2" --memory="4g" documentor:prod
```

**Check Resource Usage**:

```bash
docker stats documentor
```

### Network Issues

**Expose Port**:

```bash
docker run -p 8080:8080 documentor:prod
```

**Check Connectivity**:

```bash
docker exec documentor curl http://localhost:8080
```

## Performance Tips

### 1. Multi-Stage Builds

Production image uses multi-stage builds to minimize size:

```dockerfile
FROM gradle:8-jdk21 as builder
# Build steps...

FROM openjdk:21-slim
COPY --from=builder /app/build/libs/*.jar app.jar
```

### 2. Layer Caching

Order Dockerfile layers by change frequency:

```dockerfile
# Base image - rarely changes
FROM openjdk:21-slim

# System dependencies - rarely changes
RUN apt-get update && apt-get install -y curl

# Application - frequently changes
COPY app.jar .
```

### 3. Volume Optimization

Use bind mounts for better performance with source code:

```bash
docker run -v $(pwd):/app documentor:dev
```

### 4. Resource Allocation

Set appropriate limits:

```bash
docker run \
  --cpus="2" \
  --memory="4g" \
  --memory-swap="5g" \
  documentor:prod
```

### 5. Logging Configuration

Limit log volume:

```yaml
logging:
  driver: "json-file"
  options:
    max-size: "100m"
    max-file: "10"
```

## Advanced Configuration

### Network Setup

**Create custom network**:

```bash
docker network create documentor-network
```

**Connect to network**:

```bash
docker run --network documentor-network documentor:prod
```

### Volume Management

**Create named volume**:

```bash
docker volume create documentor-data
```

**Use volume**:

```bash
docker run -v documentor-data:/app/data documentor:prod
```

### Registry Management

**Tag image for registry**:

```bash
docker tag documentor:prod my-registry/documentor:latest
```

**Push to registry**:

```bash
docker push my-registry/documentor:latest
```

**Pull from registry**:

```bash
docker pull my-registry/documentor:latest
```

## Next Steps

- **[LLM Integrations](LLM_INTEGRATIONS.md)** - Configure LLM providers
- **[Configuration Guide](CONFIGURATION.md)** - Configuration options
- **[Usage Examples](USAGE_EXAMPLES.md)** - Command examples
