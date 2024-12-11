FROM openjdk:17-jdk-alpine
WORKDIR /app
VOLUME /tmp
ARG JAR_FILE=build/libs/fit-coach-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
