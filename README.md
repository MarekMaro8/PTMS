# PTMS — Personal Trainer Management System 🏋️‍♂️

**PTMS** is a comprehensive backend system designed specifically for personal trainers. It streamlines client management, workout plan creation, and progress tracking. The application provides a secure REST API built on a multi-layer architecture, ready for integration with frontend and mobile applications.

---

## 🚀 Quick Start (Testing with Docker)

To quickly test the application without the need for manual environment setup and building from source, a pre-built Docker image is available. 

You can find the run instructions and a ready-to-use Docker Compose configuration file in the public repository:
👉 **[https://github.com/MarekMaro8/ptms-public](https://github.com/MarekMaro8/ptms-public)**

---

## 🛠 Tech Stack

The project is built upon modern, industry-standard technologies:
- **Language:** Java 17
- **Framework:** Spring Boot 3+
- **Database:** PostgreSQL
- **ORM:** Spring Data JPA / Hibernate
- **Security:** Spring Security + JSON Web Tokens (JWT)
- **API Documentation:** Swagger UI / OpenAPI
- **DevOps:** Docker (multi-stage builds), GitHub Actions (CI/CD)

---

## ✨ Key Features

- **Role-Based Authorization:** Secure login and registration with distinct roles: `TRAINER` and `CLIENT`.
- **Client Management:** Assign clients to trainers, track their health status (e.g., injuries, rehabilitation), and add private trainer notes.
- **Workout Programs:** Create multi-day workout plans detailing specific exercises, sets, rep ranges, and RPE.
- **Session Tracking (Logbook):** Record completed workouts, measure wellness metrics (energy levels, stress, sleep quality, body weight), and log "ad-hoc" exercises added during a session.
- **Global Exercise Dictionary:** A unified database of exercises categorized by muscle groups.

---

## 🏗 Architecture & Best Practices

1. **Data Isolation (Resource-Level Security):** Access to medical and training data is strictly protected against IDOR (Insecure Direct Object Reference) vulnerabilities. The service layer continuously verifies resource ownership based on the user's token (e.g., trainers can only view their own assigned clients' data).
2. **DTO (Data Transfer Object) Pattern:** Complete separation of the database model from the presentation layer, preventing data leaks and resolving JSON cyclical reference issues.
3. **Global Exception Handling:** Standardized HTTP error responses (400, 403, 404, 409) implemented via `@RestControllerAdvice`, significantly simplifying frontend integration.
4. **Automation (CI/CD):** An automated pipeline built with GitHub Actions that continuously builds, tests, and pushes new application images to the Docker Hub registry.

---

## 💻 Local Setup (For Developers)

### Prerequisites:
- Java 17+
- PostgreSQL 14+
- Gradle

📖 API Documentation (Swagger)
Once the application is running, the interactive API documentation provided by Swagger/OpenAPI is accessible at:
👉 http://localhost:8080/swagger-ui.html
