FROM ubuntu:latest
LABEL authors="magik"
FROM openjdk:17-jdk-slim

ARG SERVICE_NAME=gateway-service

WORKDIR /app

COPY target/gateway-service-0.0.1-SNAPSHOT.jar /app/gateway-service-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java", "-jar", "/app/gateway-service-0.0.1-SNAPSHOT.jar"]
