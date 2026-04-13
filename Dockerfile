# ---------- Stage 1: build ----------
FROM gradle:8.7-jdk21 AS build

WORKDIR /app

COPY build.gradle settings.gradle ./
COPY gradle ./gradle
COPY gradlew ./

RUN ./gradlew dependencies --no-daemon

COPY src ./src

RUN ./gradlew bootJar --no-daemon

# ---------- Stage 2: runtime ----------
FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]