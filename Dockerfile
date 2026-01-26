# --- ETAP 1: Budowanie ---
FROM gradle:8.1.0-jdk17-alpine AS builder
WORKDIR /app

# Kopiujemy pliki, które Docker widzi teraz bezpośrednio obok siebie
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle

# Nadajemy uprawnienia (niezbędne, bo GitHub Actions działa na Linuxie)
RUN chmod +x gradlew

# Budujemy aplikację
# Wykorzystujemy cache dla zależności, aby buildy były szybsze
RUN ./gradlew dependencies --no-daemon
COPY src ./src
RUN ./gradlew clean build -x test --no-daemon

# --- ETAP 2: Uruchamianie ---
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Kopiujemy wynikowy plik .jar (używamy gwiazdki, by nie martwić się nazwą)
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]