# syntax=docker/dockerfile:1
FROM maven AS build
COPY src D:/Docker/app/src
COPY pom.xml D:/Docker/app
RUN mvn -f D:/Docker/app clean package

FROM openjdk
COPY --from=build target/search-0.0.1-SNAPSHOT.jar target/search-0.0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","target/search-0.0.1-SNAPSHOT.jar"]