FROM gradle:8.0-jdk17-alpine AS build
COPY --chown=gradle:gradle . /thoth
WORKDIR /thoth
RUN gradle :thoth-server:clean --no-daemon
RUN gradle :thoth-server:bootJar --no-daemon

FROM mcr.microsoft.com/playwright/java:v1.39.0-jammy

EXPOSE 8080

RUN mkdir /app

COPY --from=build /thoth/thoth-server/build/libs/*.jar /app/spring-boot-application.jar

ENTRYPOINT ["java","-jar","/app/spring-boot-application.jar"]