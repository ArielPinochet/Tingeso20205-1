FROM ubuntu:latest
LABEL authors="magik"
FROM openjdk:17-jdk-slim

ARG SERVICE_NAME=eureka-service

WORKDIR /app

COPY target/eureka-service-0.0.1-SNAPSHOT.jar /app/eureka-service-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java", "-jar", "/app/eureka-service-0.0.1-SNAPSHOT.jar"]