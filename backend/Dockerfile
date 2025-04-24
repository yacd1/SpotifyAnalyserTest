# Use the official OpenJDK 21 image as the base image (aligned with the pom.xml java version)
FROM openjdk:21-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven wrapper and pom.xml to the container
COPY .mvn .mvn
COPY mvnw pom.xml ./

# Install Maven and build the app
RUN chmod +x mvnw && ./mvnw clean package -DskipTests

# Copy the built JAR file into the container
COPY target/backend-0.0.1-SNAPSHOT.jar backend.jar

# Expose the port that the Spring Boot application will run on
EXPOSE 8080

# Run the Spring Boot application when the container starts
ENTRYPOINT ["java", "-jar", "backend.jar"]