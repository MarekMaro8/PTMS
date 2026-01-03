# --- ETAP 1: Budowanie Frontendu (React) ---
FROM node:20-alpine AS frontend-builder
WORKDIR /frontend

# Skrypt w Pipeline pobierze frontend do folderu ptms-frontend
COPY ./ptms-frontend/package*.json ./
RUN npm install

COPY ./ptms-frontend ./
RUN npm run build

# --- ETAP 2: Budowanie Backend (Spring Boot) ---
FROM gradle:8.1.0-jdk17-alpine AS builder
WORKDIR /app

# Skrypt w Pipeline pobierze backend do folderu ptms-backend
COPY ./ptms-backend .

# Kopiujemy zbudowany frontend do zasobów statycznych Springa
# UWAGA: Sprawdź czy Twój React buduje do folderu 'build' czy 'dist'
COPY --from=frontend-builder /frontend/build /app/src/main/resources/static

# Nadajemy uprawnienie i budujemy
RUN chmod +x gradlew
RUN ./gradlew clean build -x test

# --- ETAP 3: Uruchamianie ---
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/PTMS-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]