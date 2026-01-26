# --- ETAP 1: Budowanie ---
FROM gradle:8.1.0-jdk17-alpine AS builder
WORKDIR /app

# Kopiujemy wszystko (build.gradle, src itp.)
COPY . .

# ZMIANA: Usuwamy linię "RUN chmod...", która powodowała błąd.
# Zamiast tego używamy komendy 'gradle' wbudowanej w ten obraz.
# To zadziała nawet jeśli pliku 'gradlew' fizycznie nie ma w kontenerze.
RUN gradle clean build -x test --no-daemon

# --- ETAP 2: Uruchamianie ---
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Kopiujemy wynik (plik .jar)
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-Xmx350m", "-Dserver.port=8080", "-jar", "app.jar"]