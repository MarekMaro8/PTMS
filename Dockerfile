# --- ETAP 1: Budowanie Frontendu (React) ---
FROM node:20-alpine AS frontend-builder
WORKDIR /frontend

# Zakładamy, że folder ptms-frontend jest obok folderu ptms (w Desktop/Projects)
# Jeśli kopiujesz go do środka PTMS, zmień ścieżkę na ./ptms-frontend
COPY ./ptms-frontend/package*.json ./
RUN npm install

COPY ./ptms-frontend ./
RUN npm run build

# --- ETAP 2: Budowanie Backend (Spring Boot) ---
FROM gradle:8.1.0-jdk17-alpine AS builder
WORKDIR /app

# Kopiujemy pliki backendu
COPY . .

# KLUCZOWY MOMENT: Kopiujemy zbudowany frontend do zasobów statycznych Javy.
# Dzięki temu Spring Boot będzie serwował stronę pod adresem http://localhost:8080
COPY --from=frontend-builder /frontend/build /app/src/main/resources/static

# Budujemy JAR (teraz zawiera już w sobie frontend)
RUN ./gradlew clean build -x test

# --- ETAP 3: Uruchamianie (Runtime) ---
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

ARG JAR_FILE=build/libs/PTMS-0.0.1-SNAPSHOT.jar
COPY --from=builder /app/${JAR_FILE} app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]