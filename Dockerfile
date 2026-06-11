# Build stage
FROM --platform=linux/amd64 gradle:8.5-jdk17 AS build
WORKDIR /app
COPY . .
RUN chmod +x gradlew
RUN ./gradlew bootJar --no-daemon

# Run stage
FROM --platform=linux/amd64 openjdk:17-jdk-slim
EXPOSE 5001
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]