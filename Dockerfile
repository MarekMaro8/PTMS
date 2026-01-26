# --- ETAP 1: Budowanie Samego Backendu (Java/Gradle) ---
FROM gradle:8-jdk17-alpine AS builder
WORKDIR /app

# Kopiujemy pliki projektu do kontenera
# Używamy kropki (.), co oznacza "wszystko z obecnego folderu"
COPY . .

# Nadajemy uprawnienia do pliku gradlew (na wszelki wypadek)
RUN chmod +x gradlew

# Budujemy aplikację, pomijając testy (oszczędność czasu i RAMu na Renderze)
RUN ./gradlew clean build -x test --no-daemon

# --- ETAP 2: Uruchamianie (Lekki obraz Java) ---
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Znajdujemy plik .jar i kopiujemy go pod prostą nazwą app.jar
# (Dzięki temu nie musisz się martwić o wersję w nazwie pliku)
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

# WAŻNE: Dodane flagi -Xmx ograniczające pamięć dla darmowego planu (512MB)
ENTRYPOINT ["java", "-Xmx350m", "-Xss512k", "-Dserver.port=8080", "-jar", "app.jar"]