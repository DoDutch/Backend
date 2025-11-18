# Dockerfile

# OpenJDK 17 공식 교체 이미지 (openjdk:17 제거됨)
FROM eclipse-temurin:17-jdk

ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]