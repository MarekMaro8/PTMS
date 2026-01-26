# --- ETAP 1: Budowanie Samego Backendu ---
FROM gradle:8.1.0-jdk17-alpine AS builder
WORKDIR /app

# Kopiujemy wszystko z folderu repozytorium do kontenera
COPY . .

# ZMIANA KLUCZOWA:
# Zamiast bawić się w naprawianie 'gradlew', używamy polecenia 'gradle'
# wbudowanego w ten obraz. To eliminuje błąd "chmod: not found".
# Używamy --no-daemon, żeby nie zawieszać procesu w Dockerze.
RUN gradle clean build -x test --no-daemon

# --- ETAP 2: Uruchamianie (Lekki obraz Java) ---
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Kopiujemy wynik budowania (plik .jar)
# Używamy *.jar, żeby pasowało niezależnie od wersji w nazwie pliku
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

# Limity pamięci dla Rendera (zapobiegają wyłączeniu aplikacji)
ENTRYPOINT ["java", "-Xmx350m", "-Dserver.port=8080", "-jar", "app.jar"]