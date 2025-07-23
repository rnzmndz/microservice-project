# Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy only the POM file first to leverage Docker cache
COPY pom.xml .

# Download dependencies
RUN mvn dependency:go-offline -B

# Copy source files
COPY src ./src

# Build the application
RUN mvn package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copy the built JAR
COPY --from=build /app/target/employee-service-*.jar app.jar

# Expose the application port (default: 8080)
EXPOSE 8082

# Health check (uses Spring Boot Actuator)
HEALTHCHECK --start-period=30s --interval=30s --timeout=3s \
  CMD curl -f http://localhost:8082/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]