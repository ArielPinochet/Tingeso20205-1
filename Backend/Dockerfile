FROM ubuntu:latest
LABEL authors="magik"

# Usa una imagen base de OpenJDK
FROM openjdk:17-jdk-slim

# Crea un directorio para la aplicación
WORKDIR /app

# Copia el JAR al contenedor
COPY target/Pep1Tingeso-0.0.1-SNAPSHOT.jar /app/Pep1Tingeso-0.0.1-SNAPSHOT.jar

# Define el puerto de ejecución
EXPOSE 8080

# Ejecuta la aplicación al iniciar el contenedor
ENTRYPOINT ["java", "-jar", "/app/Pep1Tingeso-0.0.1-SNAPSHOT.jar"]