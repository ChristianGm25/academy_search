# syntax=docker/dockerfile:1
FROM maven AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app clean package

FROM openjdk
COPY --from=build target/search-0.0.1-SNAPSHOT.jar target/search-0.0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","target/search-0.0.1-SNAPSHOT.jar"]