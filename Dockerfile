# Stage 1: Build the Spring Boot application
FROM maven:3.9.6-eclipse-temurin-17 AS build

# Establecemos el directorio de trabajo dentro del contenedor para esta etapa.
WORKDIR /app

# Copiamos los archivos de configuración de Maven y el pom.xml.
COPY .mvn .mvn
COPY pom.xml .

# Descargamos las dependencias de Maven.
RUN mvn dependency:go-offline

# Copiamos el código fuente de tu aplicación.
COPY src src

# Construimos el archivo JAR ejecutable de Spring Boot, saltando los tests para un build más rápido.
RUN mvn clean package -DskipTests

# Stage 2: Create the final lean image
FROM openjdk:17-jdk-slim

# Establecemos el directorio de trabajo para la aplicación en la imagen final.
WORKDIR /app

# Copiamos el archivo JAR compilado desde la etapa de "build" a la imagen final.
# ¡IMPORTANTE! Usamos el nombre exacto del JAR generado.
COPY --from=build /app/target/*.jar app.jar

# Expone el puerto por defecto de Spring Boot (8080).
EXPOSE 8080

# Define el comando que se ejecutará cuando el contenedor se inicie.
ENTRYPOINT ["java","-jar","/app.jar"]