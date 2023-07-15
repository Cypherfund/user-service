# syntax=docker/dockerfile:1

FROM ghcr.io/graalvm/jdk:ol8-java17-22.3.1
WORKDIR /user

COPY ./target/*.jar user.jar
COPY ./src/main/resources/*.properties application.properties
COPY ./src/main/resources/*.xml log4j2.xml

EXPOSE 8080

CMD ["java", "-jar", "user.jar"]
