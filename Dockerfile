FROM openjdk:17
LABEL authors="ONE TO ONE"
ARG JAR_FILE=build/libs/sparta-onetoone-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} onetotone-docker.jar
ENTRYPOINT ["java", "-jar", "/onetotone-docker.jar", ">", "app.log"]