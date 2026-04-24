FROM gradle:8.7-jdk21 AS build

WORKDIR /app

COPY build.gradle.kts settings.gradle.kts ./
COPY gradle ./gradle
COPY gradlew ./
RUN chmod +x ./gradlew

# 🔹 Важно: используйте wrapper, а не системный gradle
RUN ./gradlew dependencies --no-daemon

COPY src .
COPY .env .

# 🔹 Добавьте --stacktrace для лучшей отладки при ошибках
RUN ./gradlew bootJar --no-daemon --stacktrace

# Runtime stage
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/*-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]