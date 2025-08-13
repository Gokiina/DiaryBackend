# Stage 1: Build the Spring Boot application
# Usamos una imagen base que ya tiene Maven y OpenJDK 17.
# Esta etapa se encarga de compilar tu proyecto.
FROM maven:3.9.6-eclipse-temurin-17 AS build

# Establecemos el directorio de trabajo dentro del contenedor para esta etapa.
WORKDIR /app

# Copiamos los archivos de configuración de Maven y el pom.xml.
# Esto ayuda a Docker a cachear las dependencias si el pom.xml no cambia.
COPY .mvn .mvn
COPY pom.xml .

# Descargamos las dependencias de Maven.
RUN mvn dependency:go-offline

# Copiamos el código fuente de tu aplicación.
COPY src src

# Construimos el archivo JAR ejecutable de Spring Boot, saltando los tests para un build más rápido.
RUN mvn clean package -DskipTests

# Stage 2: Create the final lean image
# Usamos una imagen base más ligera (solo OpenJDK) para la imagen final.
# Esto reduce significativamente el tamaño de la imagen del contenedor.
FROM openjdk:17-jdk-slim

# Establecemos el directorio de trabajo para la aplicación en la imagen final.
WORKDIR /app

# Copiamos el archivo JAR compilado desde la etapa de "build" a la imagen final.
COPY --from=build /app/target/*.jar app.jar

# Expone el puerto por defecto de Spring Boot (8080).
# Esto le dice a Docker y a Render que el contenedor escuchará en este puerto.
EXPOSE 8080

# Define el comando que se ejecutará cuando el contenedor se inicie.
# Este comando arranca tu aplicación Spring Boot.
ENTRYPOINT ["java","-jar","/app.jar"]

# Establecemos el directorio de trabajo dentro del contenedor para esta etapa.
WORKDIR /app

# Copiamos los archivos de configuración de Maven y el pom.xml.
# Esto ayuda a Docker a cachear las dependencias si el pom.xml no cambia.
COPY .mvn .mvn
COPY pom.xml .

# Descargamos las dependencias de Maven.
RUN mvn dependency:go-offline

# Copiamos el código fuente de tu aplicación.
COPY src src

# Construimos el archivo JAR ejecutable de Spring Boot, saltando los tests para un build más rápido.
RUN mvn clean package -DskipTests

# Stage 2: Create the final lean image
# Usamos una imagen base más ligera (solo OpenJDK) para la imagen final.
# Esto reduce significativamente el tamaño de la imagen del contenedor.
FROM openjdk:17-jdk-slim

# Establecemos el directorio de trabajo para la aplicación en la imagen final.
WORKDIR /app

# Copiamos el archivo JAR compilado desde la etapa de "build" a la imagen final.
COPY --from=build /app/target/*.jar app.jar

# Expone el puerto por defecto de Spring Boot (8080).
# Esto le dice a Docker y a Render que el contenedor escuchará en este puerto.
EXPOSE 8080

# Define el comando que se ejecutará cuando el contenedor se inicie.
# Este comando arranca tu aplicación Spring Boot.
ENTRYPOINT ["java","-jar","/app.jar"]