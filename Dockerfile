FROM gradle:8.1.0-jdk17-alpine AS builder

# Ustawiamy katalog roboczy
WORKDIR /app

# Kopiujemy pliki potrzebne do budowania
COPY . .

# Kompilacja i pakowanie aplikacji do JARa (-x test pomija testy)
RUN ./gradlew clean build -x test

# ETAP 2: URUCHAMIANIE (Runtime Stage)
# Używamy stabilnego i minimalnego JRE 17 od Eclipse Temurin (Alpine Linux)
FROM eclipse-temurin:17-jre-alpine

# Ustawiamy argumenty (nazwa pliku JAR)
ARG JAR_FILE=build/libs/PTMS-0.0.1-SNAPSHOT.jar

# Kopiujemy gotowy JAR z etapu 'builder'
COPY --from=builder /app/${JAR_FILE} app.jar

# Uruchamiamy aplikację Spring Boot
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]