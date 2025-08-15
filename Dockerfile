# Stage 1: Build the Spring Boot application
FROM maven:3.9.6-eclipse-temurin-17 AS build # O la versión que te funcionó

WORKDIR /app

COPY .mvn .mvn
COPY pom.xml .

RUN mvn dependency:go-offline

COPY src src

RUN mvn clean package -DskipTests # Este comando debe generar app.jar en /app/target/

# Stage 2: Create the final lean image
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copiamos el JAR que sabemos que se llama app.jar
COPY --from=build /app/target/app.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app.jar"]