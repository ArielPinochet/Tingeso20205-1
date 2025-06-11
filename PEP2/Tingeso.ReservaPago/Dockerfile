FROM ubuntu:latest
LABEL authors="magik"
FROM openjdk:17-jdk-slim

ARG SERVICE_NAME=reservapago-service

WORKDIR /app

COPY target/reservapago-service-0.0.1-SNAPSHOT.jar /app/reservapago-service-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java", "-jar", "/app/reservapago-service-0.0.1-SNAPSHOT.jar"]
