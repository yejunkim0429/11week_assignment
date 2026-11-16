FROM gradle:8.14.7-jdk21 AS build
USER root
WORKDIR /app
COPY . .
RUN gradle --no-daemon bootJar

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]