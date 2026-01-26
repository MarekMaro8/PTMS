# --- ETAP 1: Budowanie (Builder) ---
FROM gradle:8.1.0-jdk17-alpine AS builder
WORKDIR /app

# Kopiujemy pliki konfiguracyjne Gradle (widoczne na Twoim screenie)
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle

# Nadajemy uprawnienia do pliku gradlew (kluczowe dla Linuxa/Render)
RUN chmod +x gradlew

# Pobieramy zależności (to przyspieszy kolejne buildy)
RUN ./gradlew dependencies --no-daemon

# Kopiujemy kod źródłowy (folder src widoczny na screenie)
COPY src ./src

# Budujemy aplikację (pomijając testy dla szybkości)
RUN ./gradlew clean build -x test --no-daemon

# --- ETAP 2: Uruchamianie (Runtime) ---
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# BEZPIECZNIK: Kopiujemy dowolny plik .jar z folderu libs
# Dzięki temu nazwa pliku (PTMS-0.0.1 czy inna) nie ma znaczenia
COPY --from=builder /app/build/libs/*.jar app.jar

# Port domyślny dla Spring Boot
EXPOSE 8080

# Uruchomienie aplikacji
ENTRYPOINT ["java", "-jar", "app.jar"]