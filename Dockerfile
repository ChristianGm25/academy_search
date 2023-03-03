# syntax=docker/dockerfile:1
FROM maven AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app clean package

FROM openjdk
COPY --from=build /home/app/target/search-0.0.1-SNAPSHOT.jar /usr/local/lib/search-0.0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/usr/local/lib/demo.jar"]