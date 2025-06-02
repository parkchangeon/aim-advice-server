FROM openjdk:21-jdk-slim

COPY wait-for-it.sh /wait-for-it.sh
RUN chmod +x /wait-for-it.sh

COPY build/libs/app.jar app.jar

ENTRYPOINT ["/wait-for-it.sh", "mysql:3306", "--", "java","-jar","/app.jar"]
