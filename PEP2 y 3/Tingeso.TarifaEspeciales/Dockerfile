FROM ubuntu:latest
LABEL authors="magik"
FROM openjdk:17-jdk-slim

ARG SERVICE_NAME=tarifa-especial-service

WORKDIR /app

COPY target/tarifa-especial-service-0.0.1-SNAPSHOT.jar /app/tarifa-especial-service-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java", "-jar", "/app/tarifa-especial-service-0.0.1-SNAPSHOT.jar"]
