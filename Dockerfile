# --- Stage 1: Build the Application ---
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy the project files
COPY . .

# Build the application (skip tests to speed it up)
RUN mvn clean package -DskipTests

# --- Stage 2: Run the Application ---
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy the built JAR file from the build stage
# NOTE: The wildcard *.jar handles version changes automatically
COPY --from=build /app/target/*.jar app.jar

# Expose the port Render assigns (default is 8080)
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]