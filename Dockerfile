# Stage 1: Build the Spring Boot application
FROM maven:3.9.6-eclipse-temurin-17 AS build # Mantén esta si fue la que finalmente te permitió construir

WORKDIR /app

COPY .mvn .mvn
COPY pom.xml .

RUN mvn dependency:go-offline

COPY src src

RUN mvn clean package -DskipTests

# Stage 2: Create the final lean image
FROM openjdk:17-jdk-slim

WORKDIR /app

# Nuevo enfoque para copiar el JAR: encontrar el JAR ejecutable generado por Spring Boot y copiarlo
# Este comando 'sh -c' ejecuta el find y luego copia el archivo encontrado.
# El archivo JAR ejecutable de Spring Boot es el que NO tiene el sufijo '.original'.
RUN apt-get update && apt-get install -y findutils # Asegura que 'find' esté disponible
COPY --from=build /app/target/*.jar /tmp/ # Copiamos todos los JARs a una ubicación temporal
RUN mv $(find /tmp -name "*-SNAPSHOT.jar" ! -name "*-SNAPSHOT.jar.original") /app/app.jar # Movemos el JAR correcto y lo renombramos a app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"] # ¡Ahora ejecutamos app.jar!