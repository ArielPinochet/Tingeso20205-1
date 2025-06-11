FROM ubuntu:latest
LABEL authors="magik"
FROM openjdk:17-jdk-slim

ARG SERVICE_NAME=tarifas-service

WORKDIR /app

COPY target/tarifas-service-0.0.1-SNAPSHOT.jar /app/tarifas-service-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java", "-jar", "/app/tarifas-service-0.0.1-SNAPSHOT.jar"]
