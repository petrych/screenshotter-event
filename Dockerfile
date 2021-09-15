# syntax=docker/dockerfile:1

FROM openjdk:16-alpine3.13 as base

WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./
COPY src ./src

RUN ./mvnw clean package

FROM base as development

COPY --from=base /app/target/screenshotter-*.jar /screenshotter.jar

# Hardcoded because wanted to comply with the existing setup
EXPOSE 8083

ENTRYPOINT ["java","-jar","/screenshotter.jar"]