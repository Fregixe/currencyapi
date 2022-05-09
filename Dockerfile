FROM openjdk:13-alpine
ARG JAR_FILE=target/currency-0.1.0-SNAPSHOT-spring-boot.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]