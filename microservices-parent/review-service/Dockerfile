# Stage 1: Build the application
FROM maven:3.9.6-eclipse-temurin-21 AS builder

# Set the working directory inside the container
WORKDIR /app

# Copy the entire project to the container
COPY . .

RUN mvn clean package -DskipTests

# Stage 2: Create the runtime image
FROM eclipse-temurin:21-jdk-jammy

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file from the builder stage
COPY --from=builder /app/target/*.jar review-service.jar

# Expose the application port
EXPOSE 8080
EXPOSE 6379

# Command to run the application
ENTRYPOINT ["java", "-jar", "review-service.jar"]
