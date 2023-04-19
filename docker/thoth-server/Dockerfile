FROM gradle:8.0-jdk19-alpine AS build
COPY --chown=gradle:gradle . /thoth
WORKDIR /thoth
RUN gradle :thoth-server:bootJar --no-daemon

FROM mcr.microsoft.com/playwright/java:v1.30.0-focal

RUN apt install openjdk-19-jre-headless -y

RUN java -version

EXPOSE 8080

RUN mkdir /app

COPY --from=build /thoth/thoth-server/build/libs/*.jar /app/spring-boot-application.jar

ENTRYPOINT ["java","-jar","/app/spring-boot-application.jar"]