# Usaremos una única imagen que contiene tanto Maven como Java
FROM maven:3.9.6-eclipse-temurin-17

# Establecemos el directorio de trabajo
WORKDIR /app

# Copiamos los archivos de dependencias primero para usar el caché de Docker
COPY .mvn .mvn
COPY pom.xml .

# Descargamos las dependencias
RUN mvn dependency:go-offline

# Copiamos el resto del código fuente
COPY src src

# Compilamos y empaquetamos la aplicación. El JAR se creará en /app/target/app.jar
RUN mvn clean package -DskipTests

# Exponemos el puerto que usa Spring Boot
EXPOSE 8080

# El comando final para ejecutar la aplicación directamente desde su ubicación
ENTRYPOINT ["java","-jar","/app/target/app.jar"]
