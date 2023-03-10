# syntax=docker/dockerfile:1
FROM openjdk
COPY --from=build target/search-0.0.1-SNAPSHOT.jar target/search-0.0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","target/search-0.0.1-SNAPSHOT.jar"]