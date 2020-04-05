FROM openjdk:8
ADD target/starturn-engine.jar starturn-engine.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "starturn-engine.jar"]