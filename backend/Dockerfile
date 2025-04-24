FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw
COPY src ./src
RUN ./mvnw clean package -DskipTests

FROM openjdk:21-jdk-slim
WORKDIR /app

COPY --from=build /app/target/backend-0.0.1-SNAPSHOT.jar backend.jar

EXPOSE ${server.port}

ENTRYPOINT ["java", "-jar", "backend.jar"]
