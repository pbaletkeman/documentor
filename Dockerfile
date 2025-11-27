# Multi-stage Dockerfile for Documentor - Java 17 LTS Spring Boot CLI Application
# Stage 1: Builder - Compile and test the application
FROM eclipse-temurin:17-jdk-jammy AS builder

WORKDIR /app

# Install build dependencies (git, maven/gradle already in base image)
RUN apt-get update && apt-get install -y --no-install-recommends \
    ca-certificates \
    && rm -rf /var/lib/apt/lists/*

# Copy build scripts and source
COPY gradlew gradle.properties settings.gradle build.gradle ./
COPY gradle/ ./gradle/
COPY src/ ./src/
COPY config/ ./config/

# Build the application
RUN ./gradlew clean build -x test \
    && mkdir -p /app/libs \
    && cp build/libs/*.jar /app/libs/

# Stage 2: Runtime - Production image with just the application
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Install runtime dependencies
RUN apt-get update && apt-get install -y --no-install-recommends \
    ca-certificates \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Copy the built JAR from builder stage
COPY --from=builder /app/libs/*.jar ./documentor.jar

# Create non-root user for security
RUN groupadd -r documentor && useradd -r -g documentor documentor
USER documentor

# Health check for the application
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Default command
ENTRYPOINT ["java", "-jar", "documentor.jar"]
CMD ["--help"]

# Labels for metadata
LABEL maintainer="Documentor Team"
LABEL description="Documentor - Code Analysis and Documentation Generation via LLM"
LABEL version="1.0.0"
