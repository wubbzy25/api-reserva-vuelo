# Build
FROM gradle:8.10.1-jdk17 AS build
WORKDIR /app

COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle

RUN gradle dependencies --no-daemon

COPY src ./src
RUN gradle build --no-daemon -x test

#start application
FROM openjdk:17

WORKDIR /app

COPY --from=build /app/build/libs/*.jar ./app.jar

EXPOSE 8080

ENTRYPOINT ["java" , "-jar", "app.jar"]