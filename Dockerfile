# --- ETAP 1: Frontend ---
FROM node:20-alpine AS frontend-builder
WORKDIR /frontend

# Szukamy folderu ptms-frontend obok folderu z backendem
COPY ../ptms-frontend/package*.json ./
RUN npm install
COPY ../ptms-frontend ./
RUN npm run build

# --- ETAP 2: Backend ---
FROM gradle:8.1.0-jdk17-alpine AS builder
WORKDIR /app

# Kopiujemy pliki z aktualnego folderu (ptms-backend)
COPY . .

# Kopiujemy zbudowany frontend
COPY --from=frontend-builder /frontend/build /app/src/main/resources/static

RUN chmod +x gradlew
RUN ./gradlew clean build -x test

# --- ETAP 3: Runtime ---
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/PTMS-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]