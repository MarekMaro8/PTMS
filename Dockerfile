# --- ETAP 1: Budowanie Frontendu (React) ---
FROM node:20-alpine AS frontend-builder
WORKDIR /frontend

# UWAGA: Kopiujemy z kontekstu (gdzie leży folder ptms-frontend)
# Nie używamy "../", bo kontekst jest już ustawiony na folder nadrzędny
COPY ptms-frontend/package*.json ./
RUN npm install

COPY ptms-frontend/ ./
RUN npm run build

# --- ETAP 2: Budowanie Backend (Spring Boot) ---
FROM gradle:8.1.0-jdk17-alpine AS builder
WORKDIR /app

# Kopiujemy TYLKO pliki backendu do folderu roboczego kontenera
COPY ptms-backend/ .

# Kopiujemy zbudowany frontend do zasobów statycznych Springa
# WAŻNE: Upewnij się, że Twój React buduje do folderu 'build'.
# Jeśli używasz Vite, zmień '/frontend/build' na '/frontend/dist'
COPY --from=frontend-builder /frontend/build /app/src/main/resources/static

# Nadajemy uprawnienia (na wypadek gdyby Git ich nie przeniósł)
RUN chmod +x gradlew
RUN ./gradlew clean build -x test

# --- ETAP 3: Uruchamianie (Runtime) ---
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/PTMS-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]