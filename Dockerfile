FROM amazoncorretto:11-alpine-jdk
COPY target/*.jar filmorate.jar
ENTRYPOINT ["java", "-jar", "/filmorate.jar"]