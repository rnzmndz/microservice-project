# Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy only the POM file first to leverage Docker cache
COPY pom.xml .

# Download dependencies (this layer will be cached unless POM changes)
RUN mvn dependency:go-offline -B

# Copy source files
COPY src ./src

# Build the application
RUN mvn package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/target/auth-service-*.jar app.jar

# Expose the default Spring Boot port
EXPOSE 8081

# Wait-for script for database and Keycloak dependencies
# Can remove if I use Kubernetes
ADD https://github.com/eficode/wait-for/releases/download/v2.2.3/wait-for /wait-for
RUN chmod +x /wait-for

# Health check (adjust endpoint as needed)
HEALTHCHECK --start-period=30s --interval=30s --timeout=3s \
  CMD curl -f http://localhost:8081/actuator/health || exit 1

# Run the application with additional JVM options
ENTRYPOINT ["java", "-jar", "app.jar"]