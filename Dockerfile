# syntax=docker/dockerfile:1
FROM openjdk
WORKDIR /docker
COPY target/search-0.0.1-SNAPSHOT.jar target/search-0.0.1-SNAPSHOT.jar
CMD ["java","-jar","target/search-0.0.1-SNAPSHOT.jar"]