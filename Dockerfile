FROM openjdk:21-jdk-slim

COPY wait-for-it.sh /wait-for-it.sh
RUN chmod +x /wait-for-it.sh

ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

ENTRYPOINT ["/wait-for-it.sh", "mysql:3306", "--", "java","-jar","/app.jar"]
