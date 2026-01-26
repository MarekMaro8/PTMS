# --- ETAP 1: Budowanie (Gradle) ---
FROM gradle:8.1.0-jdk17-alpine AS builder
WORKDIR /app

# Kopiujemy WSZYSTKO co jest w repozytorium PTMS do folderu /app w kontenerze
COPY . .

# Nadajemy uprawnienia do pliku gradlew, który jest w root (widoczny na Twoim screenie)
RUN chmod +x gradlew

# Budujemy aplikację używając wrappera
RUN ./gradlew clean build -x test --no-daemon

# --- ETAP 2: Uruchamianie (Java Runtime) ---
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Kopiujemy zbudowany plik jar z folderu build/libs
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

# Ustawiamy limity pamięci dla darmowego planu Render (512MB RAM)
ENTRYPOINT ["java", "-Xmx350m", "-Dserver.port=8080", "-jar", "app.jar"]