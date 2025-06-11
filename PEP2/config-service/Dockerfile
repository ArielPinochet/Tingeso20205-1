FROM ubuntu:latest
LABEL authors="magik"
FROM openjdk:17-jdk-slim

ARG SERVICE_NAME=config-service

WORKDIR /app

COPY target/config-service-0.0.1-SNAPSHOT.jar /app/config-service-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java", "-jar", "/app/config-service-0.0.1-SNAPSHOT.jar"]
