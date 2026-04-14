# ---------- Stage 1: build ----------
FROM gradle:8.7-jdk21 AS build

WORKDIR /app

# 🔹 Копируем файлы Kotlin DSL (.kts)
COPY build.gradle.kts settings.gradle.kts ./
COPY gradle ./gradle
COPY gradlew ./

# 🔹 Делаем gradlew исполняемым (важно для Linux-контейнера!)
RUN chmod +x ./gradlew

# 🔹 Скачиваем зависимости (этот слой кэшируется)
RUN ./gradlew dependencies --no-daemon

# 🔹 Копируем исходный код
COPY src ./src

# 🔹 Собираем JAR
RUN ./gradlew bootJar --no-daemon

# ---------- Stage 2: runtime ----------
# 🔹 Используем JRE вместо JDK — образ будет меньше (~200МБ вместо ~400МБ)
FROM eclipse-temurin:21-jre

WORKDIR /app

# 🔹 Копируем только исполняемый JAR (исключаем *-plain.jar)
COPY --from=build /app/build/libs/*-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]