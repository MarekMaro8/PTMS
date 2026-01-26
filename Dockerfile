# --- ETAP 1: Budowanie Backendu (Gradle) ---
# Używamy lekkiego obrazu z gotowym Gradle i Javą 17
FROM gradle:8-jdk17-alpine AS builder
WORKDIR /app

# KLUCZOWE: Kopiujemy zawartość folderu 'ptms-backend' do głównego katalogu kontenera
# Dzięki temu plik 'gradlew' znajdzie się w /app/gradlew, a nie w /app/ptms-backend/gradlew
COPY ptms-backend/ .

# Nadajemy uprawnienia wykonywania dla wrappera Gradle
RUN chmod +x gradlew

# Budujemy aplikację:
# - clean: czyści stare buildy
# - build: buduje nową wersję
# - -x test: pomija testy (oszczędność czasu i RAMu na Renderze)
# - --no-daemon: nie zostawia procesu Gradle w tle (ważne w Dockerze)
RUN ./gradlew clean build -x test --no-daemon

# --- ETAP 2: Uruchamianie (Samo środowisko Java - JRE) ---
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Kopiujemy zbudowany plik .jar z poprzedniego etapu
# Używamy *.jar, żeby nie martwić się o dokładną wersję w nazwie pliku
COPY --from=builder /app/build/libs/*.jar app.jar

# Otwieramy port 8080 (standardowy dla Spring Boot)
EXPOSE 8080

# Uruchamiamy aplikację z limitem pamięci
# -Xmx350m: Ogranicza zużycie RAMu do 350MB (darmowy Render ma 512MB, zostawiamy zapas dla systemu)
ENTRYPOINT ["java", "-Xmx350m", "-Dserver.port=8080", "-jar", "app.jar"]