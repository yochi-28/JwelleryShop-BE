# Build stage with JDK 25
FROM eclipse-temurin:25-jdk AS build
WORKDIR /app
# Install Gradle (or use wrapper)
COPY gradlew build.gradle settings.gradle ./
RUN chmod +x gradlew
COPY . .
RUN ./gradlew bootJar --no-daemon

# Run stage (use a compatible JRE, e.g., 25 or 21)
FROM eclipse-temurin:25-jre
EXPOSE 5001
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]