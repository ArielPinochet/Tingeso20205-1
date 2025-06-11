FROM ubuntu:latest
LABEL authors="magik"
FROM openjdk:17-jdk-slim

ARG SERVICE_NAME=descuento-service

WORKDIR /app

COPY target/descuento-service-0.0.1-SNAPSHOT.jar /app/descuento-service-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java", "-jar", "/app/descuento-0.0.1-SNAPSHOT.jar"]
