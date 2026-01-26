# --- ETAP 1: Budowanie (Gradle) ---
FROM gradle:8-jdk17-alpine AS builder
WORKDIR /app

# Kopiujemy wszystko z głównego folderu (tam gdzie jest build.gradle i gradlew)
COPY . .

# Nadajemy uprawnienia do gradlew, który widzę na Twoim screenie
RUN chmod +x gradlew

# Budujemy aplikację
RUN ./gradlew clean build -x test --no-daemon

# --- ETAP 2: Uruchamianie (Java Runtime) ---
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Kopiujemy zbudowany plik jar
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

# Limity pamięci dla darmowego planu Render
ENTRYPOINT ["java", "-Xmx350m", "-Dserver.port=8080", "-jar", "app.jar"]