# Base de sistema
FROM ubuntu:latest
LABEL authors="magik"

# Base de Java
FROM openjdk:17-jdk-slim

# Definir argumentos para base de datos
ARG BD_USER=postgres
ARG BD_PASS=1253

# Definir variable de entorno para la aplicación
ENV BD_USER=${BD_USER}
ENV BD_PASS=${BD_PASS}

# Definir el servicio
ARG SERVICE_NAME=clientefrecuente-service

WORKDIR /app

# Copiar el JAR de la aplicación
COPY target/clientefrecuente-service-0.0.1-SNAPSHOT.jar /app/clientefrecuente-service-0.0.1-SNAPSHOT.jar

# Configurar el punto de entrada
ENTRYPOINT ["java", "-jar", "/app/clientefrecuente-service-0.0.1-SNAPSHOT.jar"]
