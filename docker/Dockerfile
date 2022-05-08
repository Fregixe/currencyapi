FROM openjdk:13-alpine
ARG JAR=*.jar
COPY ${JAR} application.jar
ENTRYPOINT ["java", "-jar", "application.jar"]