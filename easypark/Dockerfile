# syntax=docker/dockerfile:1

# --- Build stage ---
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Copy Maven wrapper and project metadata first for dependency caching
COPY --link pom.xml mvnw ./
COPY --link .mvn .mvn/

# Ensure Maven wrapper is executable and download dependencies (offline cache)
RUN chmod +x mvnw && ./mvnw dependency:go-offline

# Copy source code
COPY --link src ./src/

# Build the application (skip tests compilation and execution for faster build)
RUN ./mvnw package -DskipTests -Dmaven.test.skip=true

# --- Runtime stage ---
FROM eclipse-temurin:21-jre
WORKDIR /app

# Create a non-root user for security
RUN addgroup --system appgroup && adduser --system --ingroup appgroup appuser
USER appuser

# Copy the built jar from the build stage
COPY --link --from=build /app/target/*.jar /app/app.jar

# JVM container-aware flags for memory/resource management
ENV JAVA_OPTS="-XX:MaxRAMPercentage=80.0 -XX:+UseContainerSupport"

# Expose the default Spring Boot port
EXPOSE 8080

# Use exec form for proper signal handling
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
