FROM gradle:8.0-jdk19-alpine AS build
COPY --chown=gradle:gradle . /thoth
WORKDIR /thoth
RUN gradle :thoth-server:bootJar --no-daemon

FROM mcr.microsoft.com/playwright/java:v1.30.0-focal

RUN curl -O https://download.oracle.com/java/19/latest/jdk-19_linux-x64_bin.deb

RUN apt-get -qqy install ./jdk-19_linux-x64_bin.deb

RUN update-alternatives --install /usr/bin/java java /usr/lib/jvm/jdk-19/bin/java 1919

EXPOSE 8080

RUN mkdir /app

COPY --from=build /thoth/thoth-server/build/libs/*.jar /app/spring-boot-application.jar

ENTRYPOINT ["java","-jar","/app/spring-boot-application.jar"]