FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

COPY .mvn .mvn
COPY pom.xml .

RUN mvn dependency:go-offline

COPY src src

RUN mvn clean package -DskipTests
RUN ls -lh /app/target/

FROM openjdk:17-jdk-slim

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app.jar"]