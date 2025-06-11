FROM ubuntu:latest
LABEL authors="magik"
FROM openjdk:17-jdk-slim

ARG SERVICE_NAME=clientefrecuente-service

WORKDIR /app

COPY target/clientefrecuente-service-0.0.1-SNAPSHOT.jar /app/clientefrecuente-service-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java", "-jar", "/app/clientefrecuente-service-0.0.1-SNAPSHOT.jar"]
