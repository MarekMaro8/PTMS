# --- ETAP 1: Budowanie z Cache'owaniem (Spring Boot) ---
FROM gradle:8.1.0-jdk17-alpine AS builder
WORKDIR /app

# ZMIANA: Usunęliśmy przedrostek "ptms/", bo jesteśmy już w środku tego folderu.
# Kopiujemy pliki konfiguracyjne z bieżącego katalogu (.) do katalogu roboczego kontenera (./)
COPY build.gradle settings.gradle ./
COPY gradlew ./
COPY gradle ./gradle

# Nadajemy uprawnienia (bez zmian)
RUN chmod +x gradlew

# Pobieranie zależności (bez zmian - to nadal działa dzięki cache)
RUN ./gradlew dependencies --no-daemon

# ZMIANA: Kopiujemy folder src z bieżącego katalogu
COPY src ./src

# Budowanie aplikacji
RUN ./gradlew clean build -x test --no-daemon

# --- ETAP 2: Uruchamianie (Runtime) ---
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Kopiowanie pliku wynikowego (tutaj ścieżka wewnątrz kontenera buildera się nie zmienia)
COPY --from=builder /app/build/libs/PTMS-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]